package eki.ekilex.cli.runner;

import java.io.File;
import java.util.ArrayList;
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
import eki.common.data.Count;
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
		Map<String, List<String>> languageToGroupMapSs1 = collectLanguageToGroupMapSs1(folderPath);
		List<String> languageCodesVsl = collectLanguageCodesVsl(folderPath);
		List<String> languageGroupNamesVsl = collectLanguageGroupNamesVsl(folderPath);

		validateLoadedContent(languageGroups, languageCodes, languageToGroupMapSs1, languageCodesVsl, languageGroupNamesVsl);

		int languageCount = languageToGroupMapSs1.size();
		long languageGroupCount = languageToGroupMapSs1.values().stream().distinct().count();
		Count languageGroupMemberCount = new Count();

		createLanguageGroupMembers(languageGroups, languageToGroupMapSs1, languageGroupMemberCount);

		logger.info("Completed load. Added {} languages to {} groups, altogether created {} group members",
				languageCount, languageGroupCount, languageGroupMemberCount.getValue());
	}

	private List<String> collectExistingLanguageCodes() {

		List<Classifier> languages = commonDataDbService.getDefaultClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST);
		List<String> languageCodes = languages.stream().map(Classifier::getCode).collect(Collectors.toList());
		return languageCodes;
	}

	private Map<String, List<String>> collectLanguageToGroupMapSs1(String folderPath) throws Exception {

		String langGroupSs1FilePath = folderPath + CLASSIF_LANG_GROUP_SS1_FILENAME;
		List<String> langGroupSs1FileLines = readFileLines(langGroupSs1FilePath);
		langGroupSs1FileLines.remove(0);
		Map<String, List<String>> langToGroupMap = new HashMap<>();

		for (String fileLine : langGroupSs1FileLines) {

			String[] fileLineCells = StringUtils.splitPreserveAllTokens(fileLine, CSV_SEPARATOR);
			String languageCode = StringUtils.trim(fileLineCells[0]);
			String languageGroupName = StringUtils.trim(fileLineCells[2]);
			List<String> languageGroupNames = langToGroupMap.get(languageCode);
			if (languageGroupNames == null) {
				languageGroupNames = new ArrayList<>();
				langToGroupMap.put(languageCode, languageGroupNames);
			}
			languageGroupNames.add(languageGroupName);
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

	private void createLanguageGroupMembers(
			List<LanguageGroup> languageGroups,
			Map<String, List<String>> languageToGroupMapSs1,
			Count languageGroupMemberCount) {

		Map<String, Long> languageGroupIdMap = languageGroups.stream()
				.collect(Collectors.toMap(LanguageGroup::getName, LanguageGroup::getId));

		for (String languageCode : languageToGroupMapSs1.keySet()) {

			List<String> languageGroupNames = languageToGroupMapSs1.get(languageCode);
			languageGroupNames = languageGroupNames.stream().distinct().collect(Collectors.toList());

			for (String languageGroupName : languageGroupNames) {

				Long languageGroupId = languageGroupIdMap.get(languageGroupName);
				classifierDbService.createLanguageGroupMember(languageGroupId, languageCode);
				languageGroupMemberCount.increment();
			}
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
			Map<String, List<String>> languageToGroupMapSs1,
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
				.flatMap(list -> list.stream())
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
