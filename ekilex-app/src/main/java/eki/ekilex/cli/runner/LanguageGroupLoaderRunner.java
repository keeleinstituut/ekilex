package eki.ekilex.cli.runner;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.exception.DataLoadingException;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.LanguageGroup;
import eki.ekilex.service.db.ClassifierDbService;
import eki.ekilex.service.db.CommonDataDbService;

@Component
public class LanguageGroupLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(LanguageGroupLoaderRunner.class);

	private static final String CLASSIF_LANG_GROUP_SS1_FILENAME = "classif-lang-group-ss1.tsv";

	private static final String CLASSIF_LANG_GROUP_VSL_FILENAME = "classif-lang-group-vsl.tsv";

	private static final String CLASSIF_LANG_VSL_FILENAME = "classif-lang-vsl.tsv";

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ClassifierDbService classifierDbService;

	public void execute(String folderPath) throws Exception {

		validateFilesExist(folderPath);
		folderPath = cleanup(folderPath);

		logger.info("Loading language groups...");

		List<LanguageGroup> languageGroups = classifierDbService.getLanguageGroups(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<String> languageCodes = collectExistingLanguageCodes();
		Map<String, String> languageToGroupMapSs1 = collectLanguageToGroupMapSs1(folderPath);
		List<String> languageCodesVsl = collectLanguageCodesVsl(folderPath);
		List<String> languageGroupNamesVsl = collectLanguageGroupNamesVsl(folderPath);

		validateLoadedContent(languageGroups, languageCodes, languageToGroupMapSs1, languageCodesVsl, languageGroupNamesVsl);

		//createLanguageGroupMembers(languageGroups, languageToGroupMapSs1);

		int languageCount = languageToGroupMapSs1.size();
		long languageGroupCount = languageToGroupMapSs1.values().stream().distinct().count();

		logger.info("Completed load. Added {} languages to {} groups", languageCount, languageGroupCount);
	}

	private List<String> collectExistingLanguageCodes() {

		List<Classifier> languages = commonDataDbService.getDefaultClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST);
		List<String> languageCodes = languages.stream().map(Classifier::getCode).collect(Collectors.toList());
		return languageCodes;
	}

	private Map<String, String> collectLanguageToGroupMapSs1(String folderPath) throws Exception {

		String langGroupSs1FilePath = folderPath + CLASSIF_LANG_GROUP_SS1_FILENAME;
		List<String> langGroupSs1FileLines = readFileLines(langGroupSs1FilePath);
		langGroupSs1FileLines.remove(0);
		Map<String, String> langToGroupMap = new HashMap<>();

		for (String fileLine : langGroupSs1FileLines) {

			String[] fileLineCells = StringUtils.splitPreserveAllTokens(fileLine, CSV_SEPARATOR);
			String languageCode = StringUtils.trim(fileLineCells[0]);
			String languageGroupName = StringUtils.trim(fileLineCells[2]);
			langToGroupMap.put(languageCode, languageGroupName);
		}

		return langToGroupMap;
	}

	private List<String> collectLanguageCodesVsl(String folderPath) throws Exception {

		String langVslFilePath = folderPath + CLASSIF_LANG_VSL_FILENAME;
		List<String> langVslFileLines = readFileLines(langVslFilePath);
		langVslFileLines.remove(0);
		langVslFileLines = langVslFileLines.stream()
				.map(line -> StringUtils.trim(StringUtils.split(line, CSV_SEPARATOR)[0]))
				.collect(Collectors.toList());
		return langVslFileLines;
	}

	private List<String> collectLanguageGroupNamesVsl(String folderPath) throws Exception {

		String langGroupVslFilePath = folderPath + CLASSIF_LANG_GROUP_VSL_FILENAME;
		List<String> langGroupVslFileLines = readFileLines(langGroupVslFilePath);
		langGroupVslFileLines.remove(0);
		langGroupVslFileLines = langGroupVslFileLines.stream()
				.map(line -> StringUtils.trim(StringUtils.split(line, CSV_SEPARATOR)[0]))
				.collect(Collectors.toList());
		return langGroupVslFileLines;
	}

	private void createLanguageGroupMembers(List<LanguageGroup> languageGroups, Map<String, String> languageToGroupMapSs1) {

		Map<String, Long> languageGroupIdMap = languageGroups.stream()
				.collect(Collectors.toMap(LanguageGroup::getName, LanguageGroup::getId));

		for (String languageCode : languageToGroupMapSs1.keySet()) {

			String languageGroupName = languageToGroupMapSs1.get(languageCode);
			Long languageGroupId = languageGroupIdMap.get(languageGroupName);
			classifierDbService.createLanguageGroupMember(languageGroupId, languageCode);
		}
	}

	private String cleanup(String folderPath) {

		folderPath = StringUtils.trim(folderPath);
		if (!StringUtils.endsWith(folderPath, "/")) {
			folderPath = folderPath + "/";
		}
		return folderPath;
	}

	private void validateFilesExist(String folderPath) throws DataLoadingException {

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
		List<String> requiredFilenames = Arrays.asList(
				CLASSIF_LANG_GROUP_SS1_FILENAME,
				CLASSIF_LANG_GROUP_VSL_FILENAME,
				CLASSIF_LANG_VSL_FILENAME);

		boolean requiredFilesExist = CollectionUtils.containsAll(allFilenames, requiredFilenames);

		if (!requiredFilesExist) {
			throw new DataLoadingException("Selles kataloogis puuduvad vajalikud failid!");
		}
	}

	private void validateLoadedContent(
			List<LanguageGroup> languageGroups,
			List<String> languageCodes,
			Map<String, String> languageToGroupMapSs1,
			List<String> languageCodesVsl,
			List<String> languageGroupNamesVsl) throws DataLoadingException {

		logger.info("Validating loaded content...");

		List<String> languageGroupNames = languageGroups.stream()
				.map(LanguageGroup::getName)
				.collect(Collectors.toList());

		boolean languageCodesExistSs1 = CollectionUtils.containsAll(languageCodes, languageToGroupMapSs1.keySet());

		if (!languageCodesExistSs1) {
			throw new DataLoadingException("SS1 sisaldab registreerimata keeli!");
		}

		List<String> languageGroupNamesSs1 = languageToGroupMapSs1.values().stream()
				.distinct()
				.collect(Collectors.toList());

		boolean languageGroupNamesExistSs1 = CollectionUtils.containsAll(languageGroupNames, languageGroupNamesSs1);

		if (!languageGroupNamesExistSs1) {
			throw new DataLoadingException("SS1 sisaldab registreerimata keele gruppe!");
		}

		boolean languageCodesExistVsl = CollectionUtils.containsAll(languageCodes, languageCodesVsl);

		if (!languageCodesExistVsl) {
			throw new DataLoadingException("VSL sisaldab registreerimata keeli!");
		}

		boolean languageGroupNamesExistVsl = CollectionUtils.containsAll(languageGroupNames, languageGroupNamesVsl);

		if (!languageGroupNamesExistVsl) {
			throw new DataLoadingException("VSL sisaldab registreerimata keele gruppe!");
		}
	}
}
