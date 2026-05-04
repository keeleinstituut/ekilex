package eki.ekilex.cli.runner;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ClassifierName;
import eki.common.exception.DataLoadingException;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.LanguageGroup;
import eki.ekilex.service.db.ClassifierDbService;
import eki.ekilex.service.db.CommonDataDbService;

public abstract class AbstractLanguageGroupLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(AbstractLanguageGroupLoaderRunner.class);

	protected static final String CLASSIF_LANG_GROUP_SS1_FILENAME = "classif-lang-group-ss1.tsv";

	protected static final String CLASSIF_LANG_GROUP_VSL_FILENAME = "classif-lang-group-vsl.tsv";

	protected static final String CLASSIF_LANG_SS1_FILENAME = "classif-lang-ss1.tsv";

	protected static final String CLASSIF_LANG_VSL_FILENAME = "classif-lang-vsl.tsv";

	protected static final String SS1_FILENAME = "etymoloogia_ss1_v3.xml";

	protected static final String VSL_FILENAME = "etymoloogia_vsl.xml";

	@Autowired
	protected ClassifierDbService classifierDbService;

	@Autowired
	protected CommonDataDbService commonDataDbService;

	abstract List<String> getRequiredFilenames();

	protected List<LanguageGroup> getLanguageGroups() {
		List<LanguageGroup> languageGroups = classifierDbService.getLanguageGroups(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		return languageGroups;
	}

	protected List<String> getLanguageCodes() {

		List<Classifier> languages = commonDataDbService.getDefaultClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST);
		List<String> languageCodes = languages.stream().map(Classifier::getCode).collect(Collectors.toList());
		return languageCodes;
	}

	protected String cleanup(String folderPath) {

		folderPath = StringUtils.trim(folderPath);
		if (!StringUtils.endsWith(folderPath, "/")) {
			folderPath = folderPath + "/";
		}
		return folderPath;
	}

	protected void validateFilesExist(String folderPath) throws DataLoadingException {

		logger.info("Validating files exist...");

		if (StringUtils.isEmpty(folderPath)) {
			throw new DataLoadingException("Failide kataloog on määramata!");
		}

		folderPath = cleanup(folderPath);
		File folder = new File(folderPath);

		if (!folder.exists()) {
			throw new DataLoadingException("Sellist failide kataloogi pole!!");
		}

		List<String> allFilenames = Arrays.asList(folder.list());
		List<String> requiredFilenames = getRequiredFilenames();

		boolean requiredFilesExist = CollectionUtils.containsAll(allFilenames, requiredFilenames);

		if (!requiredFilesExist) {
			throw new DataLoadingException("Selles kataloogis puuduvad vajalikud failid!");
		}
	}

	protected void writeLogRow(OutputStreamWriter reportWriter, List<String> logRow) throws Exception {

		String logRowStr = StringUtils.join(logRow, CSV_SEPARATOR) + "\n";
		reportWriter.write(logRowStr);
	}
}
