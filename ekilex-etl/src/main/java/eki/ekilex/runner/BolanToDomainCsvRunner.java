package eki.ekilex.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transform.ClassifierMapping;

@Component
public class BolanToDomainCsvRunner extends AbstractClassifierRunner {

	private static Logger logger = LoggerFactory.getLogger(BolanToDomainCsvRunner.class);

	private static final String DOMAIN_EKI_ORIGIN = "bolan";

	private static final String DOMAIN_EKI_TYPE = "v_tyyp";

	@Override
	void initialise() throws Exception {

	}

	public void execute(String classifierXsdRootFolderPath) throws Exception {

		File classifierXsdRootFolder = new File(classifierXsdRootFolderPath);
		List<File> classifierXsdFiles = collectXsdFiles(classifierXsdRootFolder);

		List<String> classifierXsdFilePaths = new ArrayList<>();
		for (File classifierXsdFile : classifierXsdFiles) {
			String classifierXsdFilePath = classifierXsdFile.getAbsolutePath();
			classifierXsdFilePaths.add(classifierXsdFilePath);
		}

		List<ClassifierMapping> targetClassifiers = new ArrayList<>();
		List<ClassifierMapping> sourceClassifiers = loadSourceClassifiers(classifierXsdFilePaths, DOMAIN_EKI_TYPE, DOMAIN_EKI_ORIGIN);

		File classifierCsvFile = new File(CLASSIFIER_DOMAIN_CSV_PATH);

		if (classifierCsvFile.exists()) {
			List<ClassifierMapping> existingClassifiers = loadExistingDomainClassifiers();
			targetClassifiers = merge(sourceClassifiers, existingClassifiers);
			targetClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getOrder));
		} else {
			targetClassifiers = sourceClassifiers;
		}

		writeDomainClassifierCsvFile(targetClassifiers);

		logger.debug("Done. Recompiled {} rows", targetClassifiers.size());
	}

	private List<File> collectXsdFiles(File origDataFilesRootFolder) {

		List<File> origDataFilesList = new ArrayList<File>();
		collectXsdFiles(origDataFilesRootFolder, origDataFilesList);

		return origDataFilesList;
	}

	private void collectXsdFiles(File fileOrFolder, List<File> filesList) {

		if (fileOrFolder.isDirectory()) {
			File[] listedFilesOrFolders = fileOrFolder.listFiles();
			if (ArrayUtils.isEmpty(listedFilesOrFolders)) {
				return;
			}
			for (File listedFileOrFolder : listedFilesOrFolders) {
				collectXsdFiles(listedFileOrFolder, filesList);
			}
		} else if (fileOrFolder.isFile()
				&& StringUtils.endsWithIgnoreCase(fileOrFolder.getName(), ".xsd")) {
			filesList.add(fileOrFolder);
		}
	}

	private void writeDomainClassifierCsvFile(List<ClassifierMapping> classifiers) throws Exception {

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
}
