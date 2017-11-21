package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.transform.Classifier;

@Component
public class XsdToClassifierCsvRunner implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(XsdToClassifierCsvRunner.class);

	private final static char CLASSIFIER_KEY_SEPARATOR = '|';

	private final static int CLASSIFIER_ALL_CSV_COL_COUNT = 10;

	private final static String CLASSIFIER_ALL_CSV_PATH = "./fileresources/csv/classifier-all.csv";

	@Autowired
	private XmlReader xmlReader;

	public void execute(String[] classifierXsdFilePaths) throws Exception {

		verifyClassifierXsdFilePaths(classifierXsdFilePaths);

		List<Classifier> targetClassifiers = new ArrayList<>();
		List<Classifier> sourceClassifiers = loadSourceClassifiers(classifierXsdFilePaths);

		/*
		File classifierCsvFile = new File(CLASSIFIER_ALL_CSV_PATH);

		if (classifierCsvFile.exists()) {
			List<Classifier> existingClassifiers = loadExistingClassifiers();
			//TODO merge, etc...
		} else {
			targetClassifiers.addAll(sourceClassifiers);
		}
		*/
		//FIXME temp solution
		targetClassifiers.addAll(sourceClassifiers);

		writeClassifierCsvFile(targetClassifiers);

		logger.debug("Done. Recompiled {} rows", targetClassifiers.size());
	}

	private void writeClassifierCsvFile(List<Classifier> classifiers) throws Exception {

		FileOutputStream classifierCsvStream = new FileOutputStream(CLASSIFIER_ALL_CSV_PATH);

		StringBuffer classifCsvlineBuf;
		String classifCsvLine;

		classifCsvLine = composeRow(CSV_SEPARATOR,
				"EKI liik", "EKI nimi", "EKI kood", "EKI väärtus", "EKI keel", "LEX nimi", "LEX kood", "LEX väärtus", "LEX keel", "LEX väärtuse liik");
		classifCsvLine += "\n";

		IOUtils.write(classifCsvLine, classifierCsvStream, StandardCharsets.UTF_8);

		for (Classifier classifier : classifiers) {

			classifCsvlineBuf = new StringBuffer();

			appendCell(classifCsvlineBuf, classifier.getEkiType());
			appendCell(classifCsvlineBuf, classifier.getEkiName());
			appendCell(classifCsvlineBuf, classifier.getEkiCode());
			appendCell(classifCsvlineBuf, classifier.getEkiValue());
			appendCell(classifCsvlineBuf, classifier.getEkiValueLang());
			appendCell(classifCsvlineBuf, classifier.getLexName());
			appendCell(classifCsvlineBuf, classifier.getLexCode());
			appendCell(classifCsvlineBuf, classifier.getLexValue());
			appendCell(classifCsvlineBuf, classifier.getLexValueLang());
			appendCell(classifCsvlineBuf, classifier.getLexValueType());

			classifCsvlineBuf.append('\n');
			classifCsvLine = classifCsvlineBuf.toString();
			IOUtils.write(classifCsvLine, classifierCsvStream, StandardCharsets.UTF_8);
		}

		classifierCsvStream.flush();
		classifierCsvStream.close();
	}

	private void appendCell(StringBuffer classifCsvlineBuf, String cellValue) {
		if (StringUtils.isBlank(cellValue)) {
			classifCsvlineBuf.append(CSV_EMPTY_CELL);
		} else {
			classifCsvlineBuf.append(cellValue);
		}
		classifCsvlineBuf.append(CSV_SEPARATOR);
	}

	//TODO impl
	private List<Classifier> loadExistingClassifiers() throws Exception {

		FileInputStream classifierFileInputStream = new FileInputStream(CLASSIFIER_ALL_CSV_PATH);
		List<String> classifierFileLines = IOUtils.readLines(classifierFileInputStream, UTF_8);
		classifierFileInputStream.close();

		final String emptyCellValue = String.valueOf(CSV_EMPTY_CELL);

		for (String classifierFileLine : classifierFileLines) {
			if (StringUtils.isBlank(classifierFileLine)) {
				continue;
			}
			String[] classifierLineCells = StringUtils.split(classifierFileLine, CSV_SEPARATOR);
			if (classifierLineCells.length != CLASSIFIER_ALL_CSV_COL_COUNT) {
				String inconsistentLineLog = StringUtils.join(classifierLineCells, CLASSIFIER_KEY_SEPARATOR);
				logger.warn("Inconsistent row \"{}\"", inconsistentLineLog);
				continue;
			}
			String ekiType = classifierLineCells[0];
			String ekiName = classifierLineCells[1];
			String ekiCode = classifierLineCells[2];
			String ekiValue = classifierLineCells[3];
			String ekiValueLang = classifierLineCells[4];
			String lexName = classifierLineCells[5];
			String lexCode = classifierLineCells[6];
			String lexValue = classifierLineCells[7];
			String lexValueLang = classifierLineCells[8];
			String lexValueType = classifierLineCells[9];

			if (StringUtils.equals(ekiType, emptyCellValue)) {

				String lexKey = composeRow(CLASSIFIER_KEY_SEPARATOR, lexName, lexCode, lexValue, lexValueLang, lexValueType);

			} else if (StringUtils.equals(lexName, emptyCellValue)) {

				String ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang);

			}
		}

		return null;
	}

	private List<Classifier> loadSourceClassifiers(String[] classifierXsdFilePaths) throws Exception {

		List<Classifier> loadedClassifiers = new ArrayList<>();
		Classifier classifier;
		int ekiOrder = 0;

		List<String> loadedClassifierKeys = new ArrayList<>();

		for (String classifierXsdFilePath : classifierXsdFilePaths) {

			logger.debug("Loading \"{}\"", classifierXsdFilePath);

			Document dataDoc = xmlReader.readDocument(classifierXsdFilePath);

			List<Element> classifierGroupNodes = dataDoc.selectNodes("/xs:schema/xs:simpleType[@name]");
			for (Element classifierGroupNode : classifierGroupNodes) {

				String ekiType = classifierGroupNode.attributeValue("name");
				String ekiName = null;
				Element classifierNameNode = (Element) classifierGroupNode.selectSingleNode("xs:annotation/xs:documentation");
				if (classifierNameNode == null) {
					ekiName = ekiType;
				} else {
					ekiName = classifierNameNode.getTextTrim();
				}
				List<Element> classifierNodes = classifierGroupNode.selectNodes("xs:restriction/xs:enumeration");
				for (Element classifierNode : classifierNodes) {

					String ekiCode = classifierNode.attributeValue("value");

					List<Element> classifierValueNodes = classifierNode.selectNodes("xs:annotation/xs:documentation");
					for (Element classifierValueNode : classifierValueNodes) {

						String ekiValueLang = classifierValueNode.attributeValue("lang");
						String ekiValue = classifierValueNode.getTextTrim();

						if (StringUtils.isNotBlank(ekiValue)) {

							String ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, ekiType, ekiCode, ekiValue, ekiValueLang);
							if (loadedClassifierKeys.contains(ekiKey)) {
								logger.info("Already loaded classifier: \"{}\"", ekiKey);
								continue;
							}
							ekiOrder++;
							classifier = new Classifier();
							classifier.setEkiType(ekiType);
							classifier.setEkiName(ekiName);
							classifier.setEkiCode(ekiCode);
							classifier.setEkiValue(ekiValue);
							classifier.setEkiValueLang(ekiValueLang);
							classifier.setEkiOrder(ekiOrder);
							classifier.setEkiKey(ekiKey);
							loadedClassifiers.add(classifier);
							loadedClassifierKeys.add(ekiKey);
						}
					}
				}
			}
		}
		loadedClassifiers.sort(Comparator.comparing(Classifier::getEkiType).thenComparing(Classifier::getEkiOrder));

		logger.debug("Collected {} rows from source", loadedClassifiers.size());

		return loadedClassifiers;
	}

	private String composeRow(char separator, String... values) {
		return StringUtils.join(values, CSV_SEPARATOR);
	}

	private void verifyClassifierXsdFilePaths(String[] classifierXsdFilePaths) throws FileNotFoundException {
		for (String classifierXsdFilePath : classifierXsdFilePaths) {
			File classifierXsdFile = new File(classifierXsdFilePath);
			if (!classifierXsdFile.exists()) {
				throw new FileNotFoundException("No such file exists: \"" + classifierXsdFilePath + "\"");
			}
		}
	}
}
