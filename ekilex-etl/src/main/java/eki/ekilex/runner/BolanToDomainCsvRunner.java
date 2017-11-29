package eki.ekilex.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transform.ClassifierMapping;

@Component
public class BolanToDomainCsvRunner extends AbstractDomainRunner {

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

		List<ClassifierMapping> sourceClassifiers = loadSourceClassifiers(classifierXsdFilePaths, DOMAIN_EKI_TYPE, DOMAIN_EKI_ORIGIN);
		sourceClassifiers = removeReusedCodes(sourceClassifiers);
		List<ClassifierMapping> existingClassifiers = loadExistingDomainClassifierMappings();
		List<ClassifierMapping> targetClassifiers = merge(sourceClassifiers, existingClassifiers);
		targetClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getOrder));

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

	private List<ClassifierMapping> removeReusedCodes(List<ClassifierMapping> classifierMappings) {

		List<String> usedKeys = new ArrayList<>();
		List<ClassifierMapping> cleanClassifierMappings = new ArrayList<>();
		for (int index1 = 0; index1 < classifierMappings.size() - 1; index1++) {
			ClassifierMapping classifierMapping1 = classifierMappings.get(index1);
			String origin1 = classifierMapping1.getEkiOrigin();
			String code1 = classifierMapping1.getEkiCode();
			String value1 = classifierMapping1.getEkiValue();
			String valueLang1 = classifierMapping1.getEkiValueLang();
			String key1 = composeRow(CLASSIFIER_KEY_SEPARATOR, origin1, code1, valueLang1);
			if (usedKeys.contains(key1)) {
				continue;
			}
			usedKeys.add(key1);
			boolean isMatch = false;
			for (int index2 = index1 + 1; index2 < classifierMappings.size(); index2++) {
				ClassifierMapping classifierMapping2 = classifierMappings.get(index2);
				String origin2 = classifierMapping2.getEkiOrigin();
				String code2 = classifierMapping2.getEkiCode();
				String value2 = classifierMapping2.getEkiValue();
				String valueLang2 = classifierMapping2.getEkiValueLang();
				String key2 = composeRow(CLASSIFIER_KEY_SEPARATOR, origin2, code2, valueLang2);
				if (StringUtils.equals(key1, key2)) {
					logger.warn("Found reused classifier code for \"{}\" and \"{}\"", value1, value2);
					if (StringUtils.length(valueLang1) > StringUtils.length(valueLang2)) {
						cleanClassifierMappings.add(classifierMapping1);
						isMatch = true;
					} else if (StringUtils.length(valueLang2) > StringUtils.length(valueLang1)) {
						cleanClassifierMappings.add(classifierMapping2);
						isMatch = true;
					}
					break;
				}
			}
			if (!isMatch) {
				cleanClassifierMappings.add(classifierMapping1);
			}
		}
		return cleanClassifierMappings;
	}
}
