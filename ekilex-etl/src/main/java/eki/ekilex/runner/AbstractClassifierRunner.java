package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ClassifierName;
import eki.common.constant.TableName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.transform.ClassifierMapping;
import eki.ekilex.service.XmlReader;

public abstract class AbstractClassifierRunner implements InitializingBean, SystemConstant, TableName {

	private static Logger logger = LoggerFactory.getLogger(AbstractClassifierRunner.class);

	public final static char CLASSIFIER_KEY_SEPARATOR = '|';

	public final static int CLASSIFIER_MAIN_CSV_COL_COUNT = 10;
	public final static int CLASSIFIER_DOMAIN_CSV_COL_COUNT = 5;

	public final static String CLASSIFIER_MAIN_CSV_PATH = "./fileresources/csv/classifier-main.csv";
	public final static String CLASSIFIER_MAIN_SQL_PATH = "./fileresources/sql/classifier-main.sql";

	public final static String CLASSIFIER_DOMAIN_CSV_PATH = "./fileresources/csv/classifier-domain.csv";
	public final static String CLASSIFIER_DOMAIN_SQL_PATH = "./fileresources/sql/classifier-domain.sql";

	private final String classifGroupExp = "/xs:schema/xs:simpleType[@name]";
	private final String classifNameExp = "xs:annotation/xs:documentation";
	private final String classifValuesExp = "xs:restriction/xs:enumeration";

	private final String classifTypeAttr = "name";
	private final String classifCodeAttr = "value";
	private final String classifValueLangAttr = "lang";

	public final String emptyCellValue = String.valueOf(CSV_EMPTY_CELL);

	@Autowired
	private XmlReader xmlReader;

	abstract void initialise() throws Exception;

	@Override
	public void afterPropertiesSet() throws Exception {

		initialise();
	}

	public List<ClassifierMapping> loadSourceClassifiers(List<String> classifierXsdFilePaths, String filteringEkiType, String origin) throws Exception {

		List<ClassifierMapping> loadedClassifiers = new ArrayList<>();

		if (CollectionUtils.isEmpty(classifierXsdFilePaths)) {
			return loadedClassifiers;
		}

		ClassifierMapping classifier;
		int order = 0;

		List<String> loadedClassifierKeys = new ArrayList<>();

		for (String classifierXsdFilePath : classifierXsdFilePaths) {

			logger.debug("Loading \"{}\"", classifierXsdFilePath);
	
			Document dataDoc = xmlReader.readDocument(classifierXsdFilePath);
	
			List<Element> classifierGroupNodes = dataDoc.selectNodes(classifGroupExp);
			for (Element classifierGroupNode : classifierGroupNodes) {
	
				String ekiType = classifierGroupNode.attributeValue(classifTypeAttr);
				if (StringUtils.isNotBlank(filteringEkiType) && !StringUtils.equals(ekiType, filteringEkiType)) {
					continue;
				}
				String ekiName = null;
				Element classifierNameNode = (Element) classifierGroupNode.selectSingleNode(classifNameExp);
				if (classifierNameNode == null) {
					ekiName = ekiType;
				} else {
					ekiName = classifierNameNode.getTextTrim();
				}
				List<Element> classifierNodes = classifierGroupNode.selectNodes(classifValuesExp);
				for (Element classifierNode : classifierNodes) {
	
					String ekiCode = classifierNode.attributeValue(classifCodeAttr);
	
					List<Element> classifierValueNodes = classifierNode.selectNodes(classifNameExp);
					for (Element classifierValueNode : classifierValueNodes) {
	
						String ekiValueLang = classifierValueNode.attributeValue(classifValueLangAttr);
						String ekiValue = classifierValueNode.getTextTrim();
	
						if (StringUtils.isNotBlank(ekiValue)) {
	
							String ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang);
							if (loadedClassifierKeys.contains(ekiKey)) {
								logger.info("Already loaded classifier: \"{}\"", ekiKey);
								continue;
							}
							order++;
							classifier = new ClassifierMapping();
							classifier.setEkiOrigin(origin);
							classifier.setEkiType(ekiType);
							classifier.setEkiName(ekiName);
							classifier.setEkiParentCode(null);
							classifier.setEkiCode(ekiCode);
							classifier.setEkiValue(ekiValue);
							classifier.setEkiValueLang(ekiValueLang);
							classifier.setOrder(order);
							classifier.setEkiKey(ekiKey);
							loadedClassifiers.add(classifier);
							loadedClassifierKeys.add(ekiKey);
						}
					}
				}
			}
		}
		loadedClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getEkiType).thenComparing(ClassifierMapping::getOrder));

		logger.debug("Collected {} rows from source", loadedClassifiers.size());

		return loadedClassifiers;
	}

	public List<ClassifierMapping> loadExistingMainClassifiers() throws Exception {

		File classifierCsvFile = new File(CLASSIFIER_MAIN_CSV_PATH);

		if (!classifierCsvFile.exists()) {
			throw new Exception("Classifiers CSV file could not be located!");
		}

		List<String> classifierFileLines = readFileLines(classifierCsvFile);
		classifierFileLines.remove(0);//remove header

		List<ClassifierMapping> existingClassifiers = new ArrayList<>();
		ClassifierMapping classifier;
		int order = 0;

		List<String> existingClassifierKeys = new ArrayList<>();

		for (String classifierFileLine : classifierFileLines) {
			if (StringUtils.isBlank(classifierFileLine)) {
				continue;
			}
			String[] classifierLineCells = StringUtils.split(classifierFileLine, CSV_SEPARATOR);
			if (classifierLineCells.length != CLASSIFIER_MAIN_CSV_COL_COUNT) {
				String lineLog = StringUtils.join(classifierLineCells, CLASSIFIER_KEY_SEPARATOR);
				logger.warn("Inconsistent row \"{}\"", lineLog);
				continue;
			}
			String ekiType = StringUtils.lowerCase(classifierLineCells[0]);
			String ekiName = classifierLineCells[1];
			String ekiCode = classifierLineCells[2];
			String ekiValue = classifierLineCells[3];
			String ekiValueLang = classifierLineCells[4];
			String lexName = StringUtils.lowerCase(classifierLineCells[5]);
			String lexCode = classifierLineCells[6];
			String lexValue = classifierLineCells[7];
			String lexValueLang = classifierLineCells[8];
			String lexValueType = StringUtils.lowerCase(classifierLineCells[9]);
			String ekiKey = null;

			if (StringUtils.equals(ekiType, emptyCellValue)) {

				try {
					ClassifierName.valueOf(lexName.toUpperCase());
				} catch (Exception e) {
					logger.warn("Unknown classifier name \"{}\"", lexName);
				}
				String lexKey = composeRow(CLASSIFIER_KEY_SEPARATOR, lexName, lexCode, lexValue, lexValueLang, lexValueType);
				if (existingClassifierKeys.contains(lexKey)) {
					logger.warn("Duplicate new classifier entry: \"{}\"", lexKey);
					continue;
				}
				existingClassifierKeys.add(lexKey);

			} else if (StringUtils.equals(lexName, emptyCellValue)) {

				ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang);
				if (existingClassifierKeys.contains(ekiKey)) {
					logger.warn("Duplicate EKI classifier entry: \"{}\"", ekiKey);
					continue;
				}
				existingClassifierKeys.add(ekiKey);

			} else {

				try {
					ClassifierName.valueOf(lexName.toUpperCase());
				} catch (Exception e) {
					logger.warn("Unknown classifier name \"{}\"", lexName);
				}
				ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang);
				String fullKey = composeRow(CLASSIFIER_KEY_SEPARATOR,
						ekiType, ekiCode, ekiValue, ekiValueLang, lexName, lexCode, lexValue, lexValueLang, lexValueType);
				if (existingClassifierKeys.contains(fullKey)) {
					logger.warn("Duplicate classifier mapping entry: \"{}\"", fullKey);
					continue;
				}
				existingClassifierKeys.add(fullKey);
			}

			order++;
			classifier = new ClassifierMapping();
			classifier.setEkiOrigin(null);
			classifier.setEkiType(ekiType);
			classifier.setEkiName(ekiName);
			classifier.setEkiCode(ekiCode);
			classifier.setEkiParentCode(null);
			classifier.setEkiValue(ekiValue);
			classifier.setEkiValueLang(ekiValueLang);
			classifier.setLexName(lexName);
			classifier.setLexCode(lexCode);
			classifier.setLexValue(lexValue);
			classifier.setLexValueLang(lexValueLang);
			classifier.setLexValueType(lexValueType);
			classifier.setOrder(order);
			classifier.setEkiKey(ekiKey);
			existingClassifiers.add(classifier);
		}

		existingClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiType).thenComparing(ClassifierMapping::getLexName).thenComparing(ClassifierMapping::getOrder));

		logger.debug("Collected {} existing classifiers", existingClassifiers.size());

		return existingClassifiers;
	}

	public List<ClassifierMapping> loadExistingDomainClassifiers() throws Exception {

		File classifierCsvFile = new File(CLASSIFIER_DOMAIN_CSV_PATH);

		if (!classifierCsvFile.exists()) {
			throw new Exception("Classifiers CSV file could not be located!");
		}

		List<String> classifierFileLines = readFileLines(classifierCsvFile);
		classifierFileLines.remove(0);//remove header

		List<ClassifierMapping> existingClassifiers = new ArrayList<>();
		ClassifierMapping classifier;
		int order = 0;

		List<String> existingClassifierKeys = new ArrayList<>();

		for (String classifierFileLine : classifierFileLines) {
			if (StringUtils.isBlank(classifierFileLine)) {
				continue;
			}
			String[] classifierLineCells = StringUtils.split(classifierFileLine, CSV_SEPARATOR);
			if (classifierLineCells.length != CLASSIFIER_DOMAIN_CSV_COL_COUNT) {
				String lineLog = StringUtils.join(classifierLineCells, CLASSIFIER_KEY_SEPARATOR);
				logger.warn("Inconsistent row \"{}\"", lineLog);
				continue;
			}

			String ekiOrigin = StringUtils.lowerCase(classifierLineCells[0]);
			String ekiCode = classifierLineCells[1];
			String ekiParentCode = classifierLineCells[2];
			String ekiValue = classifierLineCells[3];
			String ekiValueLang = classifierLineCells[4];

			String ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiOrigin, ekiCode, ekiParentCode, ekiValue, ekiValueLang);
			if (existingClassifierKeys.contains(ekiKey)) {
				logger.warn("Duplicate new classifier entry: \"{}\"", ekiKey);
				continue;
			}
			existingClassifierKeys.add(ekiKey);

			order++;
			classifier = new ClassifierMapping();
			classifier.setEkiOrigin(ekiOrigin);
			classifier.setEkiCode(ekiCode);
			classifier.setEkiParentCode(ekiParentCode);
			classifier.setEkiValue(ekiValue);
			classifier.setEkiValueLang(ekiValueLang);
			classifier.setOrder(order);
			classifier.setEkiKey(ekiKey);
			existingClassifiers.add(classifier);
		}

		existingClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getOrder));

		logger.debug("Collected {} existing classifiers", existingClassifiers.size());

		return existingClassifiers;
	}

	public List<ClassifierMapping> merge(List<ClassifierMapping> sourceClassifiers, List<ClassifierMapping> existingClassifiers) {

		if (CollectionUtils.isEmpty(sourceClassifiers)) {
			return existingClassifiers;
		}
		if (CollectionUtils.isEmpty(existingClassifiers)) {
			return sourceClassifiers;
		}

		List<ClassifierMapping> mergedClassifiers = new ArrayList<>();
		mergedClassifiers.addAll(existingClassifiers);
		for (ClassifierMapping sourceClassifier : sourceClassifiers) {
			ClassifierMapping existingClassifier = find(sourceClassifier, existingClassifiers);
			if (existingClassifier == null) {
				mergedClassifiers.add(sourceClassifier);
			}
		}
		return mergedClassifiers;
	}

	public ClassifierMapping find(ClassifierMapping sourceClassifier, List<ClassifierMapping> existingClassifiers) {
		for (ClassifierMapping existingClassifier : existingClassifiers) {
			if (StringUtils.equals(sourceClassifier.getEkiKey(), existingClassifier.getEkiKey())) {
				return existingClassifier;
			}
		}
		return null;
	}

	public String composeRow(char separator, String... values) {
		return StringUtils.join(values, separator);
	}

	public void appendCell(StringBuffer classifCsvLineBuf, String cellValue, boolean isLast) {
		if (StringUtils.isBlank(cellValue)) {
			classifCsvLineBuf.append(CSV_EMPTY_CELL);
		} else {
			classifCsvLineBuf.append(cellValue);
		}
		if (isLast) {
			classifCsvLineBuf.append('\n');
		} else {
			classifCsvLineBuf.append(CSV_SEPARATOR);
		}
	}

	public void writeDomainClassifierCsvFile(List<ClassifierMapping> classifiers) throws Exception {

		if (CollectionUtils.isEmpty(classifiers)) {
			logger.warn("No classifiers to save. Interrupting...");
			return;
		}

		FileOutputStream classifierCsvStream = new FileOutputStream(CLASSIFIER_DOMAIN_CSV_PATH);

		StringBuffer classifCsvLineBuf;
		String classifCsvLine;

		classifCsvLine = composeRow(CSV_SEPARATOR, "Päritolu", "Kood", "Alluvus", "Väärtus", "Keel");
		classifCsvLine += "\n";

		IOUtils.write(classifCsvLine, classifierCsvStream, StandardCharsets.UTF_8);

		for (ClassifierMapping classifier : classifiers) {

			classifCsvLineBuf = new StringBuffer();

			appendCell(classifCsvLineBuf, classifier.getEkiOrigin(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiCode(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiParentCode(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiValue(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiValueLang(), true);

			classifCsvLine = classifCsvLineBuf.toString();
			IOUtils.write(classifCsvLine, classifierCsvStream, StandardCharsets.UTF_8);
		}

		classifierCsvStream.flush();
		classifierCsvStream.close();
	}

	public List<String> readFileLines(File classifierCsvFile) throws Exception {
		FileInputStream classifierFileInputStream = new FileInputStream(classifierCsvFile);
		List<String> classifierFileLines = IOUtils.readLines(classifierFileInputStream, UTF_8);
		classifierFileInputStream.close();
		return classifierFileLines;
	}
}
