package eki.ekilex.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transform.Classifier;
import eki.ekilex.data.transform.ClassifierMapping;

@Component
public class XsdToClassifierCsvRunner extends AbstractClassifierRunner {

	private static Logger logger = LoggerFactory.getLogger(XsdToClassifierCsvRunner.class);

	@Override
	void initialise() throws Exception {
	}

	public void execute(List<String> classifierXsdFilePaths) throws Exception {

		validateFilePaths(classifierXsdFilePaths);

		List<ClassifierMapping> sourceClassifiers = loadSourceClassifiers(classifierXsdFilePaths, null, null);
		List<ClassifierMapping> existingClassifierMappings = loadExistingMainClassifierMappings();
		List<Classifier> existingClassifiers = loadExistingMainClassifiers();
		List<ClassifierMapping> targetClassifierMappings = merge(sourceClassifiers, existingClassifierMappings);
		targetClassifierMappings.sort(
				Comparator.comparing(ClassifierMapping::getEkiType)
				.thenComparing(ClassifierMapping::getLexName)
				.thenComparing(ClassifierMapping::getOrder));

		compare(existingClassifierMappings, existingClassifiers);
		
		writeMainClassifierMappingsCsvFile(targetClassifierMappings);
		writeMainClassifiersCsvFile(existingClassifiers);

		logger.debug("Done. Recompiled {} mappings", targetClassifierMappings.size());
		logger.debug("Done. Recompiled {} classifiers", existingClassifiers.size());
	}

	private void compare(List<ClassifierMapping> classifierMappings, List<Classifier> classifiers) {

		for (ClassifierMapping classifierMapping : classifierMappings) {
			if (StringUtils.equalsAny(classifierMapping.getLexName(), emptyCellValue, undefinedCellValue)) {
				continue;
			}
			Classifier mappedClassifier = find(classifierMapping, classifiers);
			if (mappedClassifier == null) {
				logger.warn("Mapping file refers to missing classifier {}-{}", classifierMapping.getLexName(), classifierMapping.getLexCode());
			}
		}
	}

	private Classifier find(ClassifierMapping classifierMapping, List<Classifier> classifiers) {

		String mappingName = classifierMapping.getLexName();
		String mappingCode = classifierMapping.getLexCode();
		for (Classifier classifier : classifiers) {
			String classifierName = classifier.getName();
			String classifierCode = classifier.getCode();
			if (StringUtils.equals(mappingName, classifierName) && StringUtils.equals(mappingCode, classifierCode)) {
				return classifier;
			}
		}
		return null;
	}

	private void validateFilePaths(List<String> filePaths) throws Exception {

		if (CollectionUtils.isEmpty(filePaths)) {
			return;
		}

		for (String filePath : filePaths) {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new FileNotFoundException("Incorrect file path: " + filePath);
			}
		}
	}

	private void writeMainClassifierMappingsCsvFile(List<ClassifierMapping> classifierMappings) throws Exception {

		if (CollectionUtils.isEmpty(classifierMappings)) {
			logger.warn("No classifier mappings to save. Interrupting...");
			return;
		}

		FileOutputStream csvStream = new FileOutputStream(CLASSIFIER_MAIN_MAP_CSV_PATH);

		StringBuffer csvFileLineBuf;
		String classifCsvLine;

		classifCsvLine = composeRow(CSV_SEPARATOR,
				"EKI liik", "EKI nimi", "EKI kood", "EKI väärtus", "EKI keel", "LEX nimi", "LEX kood");
		classifCsvLine += "\n";

		IOUtils.write(classifCsvLine, csvStream, StandardCharsets.UTF_8);

		for (ClassifierMapping classifierMapping : classifierMappings) {

			csvFileLineBuf = new StringBuffer();

			appendCell(csvFileLineBuf, classifierMapping.getEkiType(), false);
			appendCell(csvFileLineBuf, classifierMapping.getEkiName(), false);
			appendCell(csvFileLineBuf, classifierMapping.getEkiCode(), false);
			appendCell(csvFileLineBuf, classifierMapping.getEkiValue(), false);
			appendCell(csvFileLineBuf, classifierMapping.getEkiValueLang(), false);
			appendCell(csvFileLineBuf, classifierMapping.getLexName(), false);
			appendCell(csvFileLineBuf, classifierMapping.getLexCode(), true);

			classifCsvLine = csvFileLineBuf.toString();
			IOUtils.write(classifCsvLine, csvStream, StandardCharsets.UTF_8);
		}

		csvStream.flush();
		csvStream.close();
	}

	private void writeMainClassifiersCsvFile(List<Classifier> classifiers) throws Exception {

		if (CollectionUtils.isEmpty(classifiers)) {
			logger.warn("No classifiers to save. Interrupting...");
			return;
		}

		FileOutputStream csvStream = new FileOutputStream(CLASSIFIER_MAIN_VALUE_CSV_PATH);

		StringBuffer csvFileLineBuf;
		String classifCsvLine;

		classifCsvLine = composeRow(CSV_SEPARATOR, "LEX nimi", "LEX kood", "LEX väärtus", "LEX keel", "LEX väärtuse liik");
		classifCsvLine += "\n";

		IOUtils.write(classifCsvLine, csvStream, StandardCharsets.UTF_8);

		for (Classifier classifier : classifiers) {

			csvFileLineBuf = new StringBuffer();

			appendCell(csvFileLineBuf, classifier.getName(), false);
			appendCell(csvFileLineBuf, classifier.getCode(), false);
			appendCell(csvFileLineBuf, classifier.getValue(), false);
			appendCell(csvFileLineBuf, classifier.getValueLang(), false);
			appendCell(csvFileLineBuf, classifier.getValueType(), true);

			classifCsvLine = csvFileLineBuf.toString();
			IOUtils.write(classifCsvLine, csvStream, StandardCharsets.UTF_8);
		}

		csvStream.flush();
		csvStream.close();
	}
}
