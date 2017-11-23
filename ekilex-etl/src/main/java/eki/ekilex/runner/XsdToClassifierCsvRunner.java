package eki.ekilex.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transform.ClassifierMapping;

@Component
public class XsdToClassifierCsvRunner extends AbstractClassifierRunner {

	private static Logger logger = LoggerFactory.getLogger(XsdToClassifierCsvRunner.class);

	@Override
	void initialise() throws Exception {
	}

	public void execute(List<String> classifierXsdFilePaths) throws Exception {

		validateFilePaths(classifierXsdFilePaths);

		List<ClassifierMapping> targetClassifiers = new ArrayList<>();
		List<ClassifierMapping> sourceClassifiers = loadSourceClassifiers(classifierXsdFilePaths, null, null);

		File classifierCsvFile = new File(CLASSIFIER_MAIN_CSV_PATH);

		if (classifierCsvFile.exists()) {
			List<ClassifierMapping> existingClassifiers = loadExistingMainClassifiers();
			targetClassifiers = merge(sourceClassifiers, existingClassifiers);
			targetClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiType).thenComparing(ClassifierMapping::getLexName).thenComparing(ClassifierMapping::getOrder));
		} else {
			targetClassifiers = sourceClassifiers;
		}

		writeMainClassifierCsvFile(targetClassifiers);

		logger.debug("Done. Recompiled {} rows", targetClassifiers.size());
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

	private void writeMainClassifierCsvFile(List<ClassifierMapping> classifiers) throws Exception {

		if (CollectionUtils.isEmpty(classifiers)) {
			logger.warn("No classifiers to save. Interrupting...");
			return;
		}

		FileOutputStream classifierCsvStream = new FileOutputStream(CLASSIFIER_MAIN_CSV_PATH);

		StringBuffer classifCsvLineBuf;
		String classifCsvLine;

		classifCsvLine = composeRow(CSV_SEPARATOR,
				"EKI liik", "EKI nimi", "EKI kood", "EKI väärtus", "EKI keel", "LEX nimi", "LEX kood", "LEX väärtus", "LEX keel", "LEX väärtuse liik");
		classifCsvLine += "\n";

		IOUtils.write(classifCsvLine, classifierCsvStream, StandardCharsets.UTF_8);

		for (ClassifierMapping classifier : classifiers) {

			classifCsvLineBuf = new StringBuffer();

			appendCell(classifCsvLineBuf, classifier.getEkiType(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiName(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiCode(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiValue(), false);
			appendCell(classifCsvLineBuf, classifier.getEkiValueLang(), false);
			appendCell(classifCsvLineBuf, classifier.getLexName(), false);
			appendCell(classifCsvLineBuf, classifier.getLexCode(), false);
			appendCell(classifCsvLineBuf, classifier.getLexValue(), false);
			appendCell(classifCsvLineBuf, classifier.getLexValueLang(), false);
			appendCell(classifCsvLineBuf, classifier.getLexValueType(), true);

			classifCsvLine = classifCsvLineBuf.toString();
			IOUtils.write(classifCsvLine, classifierCsvStream, StandardCharsets.UTF_8);
		}

		classifierCsvStream.flush();
		classifierCsvStream.close();
	}

}
