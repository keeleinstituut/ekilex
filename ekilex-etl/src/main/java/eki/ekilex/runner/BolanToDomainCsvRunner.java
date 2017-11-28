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

		List<ClassifierMapping> sourceClassifiersRaw = loadSourceClassifiers(classifierXsdFilePaths, DOMAIN_EKI_TYPE, DOMAIN_EKI_ORIGIN);
		List<ClassifierMapping> existingClassifiers = loadExistingDomainClassifiers();
		List<ClassifierMapping> targetClassifiers = merge(sourceClassifiersRaw, existingClassifiers);
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

}
