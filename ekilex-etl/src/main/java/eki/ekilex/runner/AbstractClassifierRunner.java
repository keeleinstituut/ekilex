package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eki.ekilex.data.transform.Classifier;
import eki.ekilex.data.transform.ClassifierMapping;
import eki.ekilex.service.XmlReader;

public abstract class AbstractClassifierRunner implements InitializingBean, SystemConstant, TableName {

	private static Logger logger = LoggerFactory.getLogger(AbstractClassifierRunner.class);

	public final static char CLASSIFIER_KEY_SEPARATOR = '|';

	public final static int CLASSIFIER_MAIN_MAP_CSV_COL_COUNT = 7;
	public final static int CLASSIFIER_MAIN_VALUE_CSV_COL_COUNT = 5;
	public final static int CLASSIFIER_DOMAIN_CSV_COL_COUNT = 5;

	public final static String CLASSIFIER_MAIN_MAP_CSV_PATH = "./fileresources/csv/classifier-main-map.csv";
	public final static String CLASSIFIER_MAIN_VALUE_CSV_PATH = "./fileresources/csv/classifier-main-value.csv";
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
	public final String undefinedCellValue = String.valueOf(CSV_UNDEFINED_CELL);

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
		loadedClassifiers.sort(
				Comparator.comparing(ClassifierMapping::getEkiOrigin)
				.thenComparing(ClassifierMapping::getEkiType)
				.thenComparing(ClassifierMapping::getOrder));

		logger.debug("Collected {} rows from source", loadedClassifiers.size());

		return loadedClassifiers;
	}

	public List<ClassifierMapping> loadExistingMainClassifierMappings() throws Exception {

		List<ClassifierMapping> existingClassifierMappings = new ArrayList<>();

		File classifierCsvFile = new File(CLASSIFIER_MAIN_MAP_CSV_PATH);
		if (!classifierCsvFile.exists()) {
			return existingClassifierMappings;
		}

		List<String> classifierFileLines = readFileLines(classifierCsvFile);
		classifierFileLines.remove(0);//remove header

		Map<String, Boolean> mappingExistsMap = new HashMap<>();
		List<String> existingClassifierKeys = new ArrayList<>();
		ClassifierMapping classifierMapping;
		int order = 0;

		for (String classifierFileLine : classifierFileLines) {

			if (StringUtils.isBlank(classifierFileLine)) {
				continue;
			}
			String[] classifierLineCells = StringUtils.split(classifierFileLine, CSV_SEPARATOR);
			if (classifierLineCells.length != CLASSIFIER_MAIN_MAP_CSV_COL_COUNT) {
				String lineLog = StringUtils.join(classifierLineCells, CLASSIFIER_KEY_SEPARATOR);
				logger.warn("Inconsistent row \"{}\"", lineLog);
				continue;
			}
			classifierMapping = new ClassifierMapping();
			String ekiType = StringUtils.lowerCase(classifierLineCells[0]);
			String ekiName = classifierLineCells[1];
			String ekiCode = classifierLineCells[2];
			String ekiValue = classifierLineCells[3];
			String ekiValueLang = classifierLineCells[4];
			String lexName = StringUtils.lowerCase(classifierLineCells[5]);
			String lexCode = classifierLineCells[6];
			String ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang);
			String fullKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang, lexName, lexCode);
			Boolean mappingExists = Boolean.FALSE;
			boolean isNew = true;

			if (StringUtils.equalsAny(ekiType, emptyCellValue, undefinedCellValue)) {

				logger.warn("Missing mapping source, ignoring \"{}\"", fullKey);
				continue;

			} else if (StringUtils.equalsAny(lexName, emptyCellValue, undefinedCellValue)) {

				if (existingClassifierKeys.contains(ekiKey)) {
					logger.warn("Duplicate EKI classifier entry: \"{}\"", fullKey);
					continue;
				}
				existingClassifierKeys.add(ekiKey);

				//logger.warn("Empty mapping row \"{}\"", fullKey);
				mappingExists = mappingExistsMap.get(ekiKey);
				if (mappingExists == null) {
					mappingExistsMap.put(ekiKey, Boolean.FALSE);
				} else if (mappingExists.equals(Boolean.TRUE)) {
					logger.warn("Duplicate entry without mapping, even though mapping exists: \"{}\"", ekiKey);
					continue;
				}
			} else {

				if (existingClassifierKeys.contains(ekiKey)) {
					logger.warn("Duplicate EKI classifier entry: \"{}\"", fullKey);
					continue;
				}
				existingClassifierKeys.add(ekiKey);

				try {
					ClassifierName.valueOf(lexName.toUpperCase());
				} catch (Exception e) {
					logger.warn("Unknown classifier name \"{}\"", lexName);
				}
				mappingExists = mappingExistsMap.get(ekiKey);
				if (mappingExists == null) {
					mappingExistsMap.put(ekiKey, Boolean.TRUE);
				} else if (mappingExists.equals(Boolean.FALSE)) {
					logger.warn("Duplicate entry with mapping, replacing the one without mapping: \"{}\"", ekiKey);
					classifierMapping = find(ekiKey, existingClassifierMappings, true);
					isNew = false;
				}
			}

			order++;
			classifierMapping.setEkiOrigin(null);
			classifierMapping.setEkiType(ekiType);
			classifierMapping.setEkiName(ekiName);
			classifierMapping.setEkiCode(ekiCode);
			classifierMapping.setEkiParentCode(null);
			classifierMapping.setEkiValue(ekiValue);
			classifierMapping.setEkiValueLang(ekiValueLang);
			classifierMapping.setLexName(lexName);
			classifierMapping.setLexCode(lexCode);
			classifierMapping.setOrder(order);
			classifierMapping.setEkiKey(ekiKey);
			if (isNew) {
				existingClassifierMappings.add(classifierMapping);
			}
		}

		existingClassifierMappings.sort(
				Comparator.comparing(ClassifierMapping::getEkiType)
				.thenComparing(ClassifierMapping::getLexName)
				.thenComparing(ClassifierMapping::getOrder));

		logger.debug("Collected {} existing mappings", existingClassifierMappings.size());

		return existingClassifierMappings;
	}

	public List<Classifier> loadExistingMainClassifiers() throws Exception {

		List<Classifier> existingClassifiers = new ArrayList<>();

		File classifierCsvFile = new File(CLASSIFIER_MAIN_VALUE_CSV_PATH);
		if (!classifierCsvFile.exists()) {
			return existingClassifiers;
		}

		List<String> classifierFileLines = readFileLines(classifierCsvFile);
		classifierFileLines.remove(0);//remove header

		Classifier classifier;
		int order = 0;

		List<String> existingClassifierKeys = new ArrayList<>();

		for (String classifierFileLine : classifierFileLines) {

			if (StringUtils.isBlank(classifierFileLine)) {
				continue;
			}
			String[] classifierLineCells = StringUtils.split(classifierFileLine, CSV_SEPARATOR);
			if (classifierLineCells.length != CLASSIFIER_MAIN_VALUE_CSV_COL_COUNT) {
				String lineLog = StringUtils.join(classifierLineCells, CLASSIFIER_KEY_SEPARATOR);
				logger.warn("Inconsistent row \"{}\"", lineLog);
				continue;
			}
			String name = StringUtils.lowerCase(classifierLineCells[0]);
			String code = classifierLineCells[1];
			String value = classifierLineCells[2];
			String valueLang = classifierLineCells[3];
			String valueType = StringUtils.lowerCase(classifierLineCells[4]);
			String key = composeRow(CLASSIFIER_KEY_SEPARATOR, name, code, value, valueLang, valueType);

			if (StringUtils.equalsAny(name, emptyCellValue, undefinedCellValue)) {
				logger.warn("Incomplete classifier, ignoring \"{}\"", key);
				continue;

			}
			if (existingClassifierKeys.contains(key)) {
				logger.warn("Duplicate classifier entry: \"{}\"", key);
				continue;
			}
			existingClassifierKeys.add(key);

			order++;
			classifier = new Classifier();
			classifier.setOrigin(null);
			classifier.setName(name);
			classifier.setCode(code);
			classifier.setValue(value);
			classifier.setValueType(valueType);
			classifier.setValueLang(valueLang);
			classifier.setOrder(order);
			classifier.setKey(key);
			existingClassifiers.add(classifier);
		}

		existingClassifiers.sort(Comparator.comparing(Classifier::getName).thenComparing(Classifier::getOrder));

		logger.debug("Collected {} existing classifiers", existingClassifiers.size());

		return existingClassifiers;
	}

	public List<ClassifierMapping> loadExistingDomainClassifiers() throws Exception {

		List<ClassifierMapping> existingClassifiers = new ArrayList<>();

		File classifierCsvFile = new File(CLASSIFIER_DOMAIN_CSV_PATH);
		if (!classifierCsvFile.exists()) {
			return existingClassifiers;
		}

		List<String> classifierFileLines = readFileLines(classifierCsvFile);
		classifierFileLines.remove(0);//remove header

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

			String origin = StringUtils.lowerCase(classifierLineCells[0]);
			String code = classifierLineCells[1];
			String parentCode = classifierLineCells[2];
			String value = classifierLineCells[3];
			String valueLang = classifierLineCells[4];
			String key = composeRow(CLASSIFIER_KEY_SEPARATOR, origin, code, value, valueLang);
			if (existingClassifierKeys.contains(key)) {
				logger.warn("Duplicate new classifier entry: \"{}\"", key);
				continue;
			}
			existingClassifierKeys.add(key);

			order++;
			classifier = new ClassifierMapping();
			classifier.setEkiOrigin(origin);
			classifier.setEkiCode(code);
			classifier.setEkiParentCode(parentCode);
			classifier.setEkiValue(value);
			classifier.setEkiValueLang(valueLang);
			classifier.setOrder(order);
			classifier.setEkiKey(key);
			existingClassifiers.add(classifier);
		}

		existingClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getOrder));

		logger.debug("Collected {} existing classifiers", existingClassifiers.size());

		return existingClassifiers;
	}

	public List<ClassifierMapping> merge(List<ClassifierMapping> sourceClassifiers, List<ClassifierMapping> existingClassifierMappings) {

		if (CollectionUtils.isEmpty(sourceClassifiers)) {
			return existingClassifierMappings;
		}
		if (CollectionUtils.isEmpty(existingClassifierMappings)) {
			return sourceClassifiers;
		}

		List<ClassifierMapping> mergedClassifierMappings = new ArrayList<>();
		mergedClassifierMappings.addAll(existingClassifierMappings);
		for (ClassifierMapping sourceClassifier : sourceClassifiers) {
			ClassifierMapping existingClassifierMapping = find(sourceClassifier.getEkiKey(), existingClassifierMappings, false);
			if (existingClassifierMapping == null) {
				mergedClassifierMappings.add(sourceClassifier);
			}
		}
		return mergedClassifierMappings;
	}

	private ClassifierMapping find(String ekiKey, List<ClassifierMapping> existingClassifierMappings, boolean isFindOnlyWithoutMapping) {
		for (ClassifierMapping existingClassifierMapping : existingClassifierMappings) {
			if (StringUtils.equals(ekiKey, existingClassifierMapping.getEkiKey())) {
				if (isFindOnlyWithoutMapping) {
					if (StringUtils.equalsAny(existingClassifierMapping.getLexName(), emptyCellValue, undefinedCellValue)) {
						return existingClassifierMapping;
					}
				} else {
					return existingClassifierMapping;
				}
			}
		}
		return null;
	}

	public String composeRow(char separator, String... values) {
		return StringUtils.join(values, separator);
	}

	public void appendCell(StringBuffer csvFileLineBuf, String cellValue, boolean isLast) {
		if (StringUtils.isBlank(cellValue)) {
			csvFileLineBuf.append(CSV_EMPTY_CELL);
		} else {
			csvFileLineBuf.append(cellValue);
		}
		if (isLast) {
			csvFileLineBuf.append('\n');
		} else {
			csvFileLineBuf.append(CSV_SEPARATOR);
		}
	}

	public List<String> readFileLines(File classifierCsvFile) throws Exception {
		FileInputStream classifierFileInputStream = new FileInputStream(classifierCsvFile);
		List<String> classifierFileLines = IOUtils.readLines(classifierFileInputStream, UTF_8);
		classifierFileInputStream.close();
		return classifierFileLines;
	}
}
