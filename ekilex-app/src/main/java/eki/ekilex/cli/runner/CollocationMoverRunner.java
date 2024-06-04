package eki.ekilex.cli.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.TagType;
import eki.common.data.Count;
import eki.common.service.AbstractLoaderCommons;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.migra.CollocationMember;
import eki.ekilex.data.migra.MigraForm;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;
import eki.ekilex.service.db.TagDbService;

// under construction!
@Component
public class CollocationMoverRunner extends AbstractLoaderCommons implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(CollocationMoverRunner.class);

	private static final String ACTIVITY_FUNCT_NAME = "migrateCollocations";

	private static final String USER_NAME = "Kollide kolija";

	@Autowired
	private MigrationDbService migrationDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private TagDbService tagDbService;

	@Autowired
	private ActivityLogService activityLogService;

	private boolean doSave = true;

	private boolean doLog = true;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String importFilePath) throws Exception {

		logger.info("Collecting and moving collocations...");

		createSecurityContext();

		File importFile = new File(importFilePath);
		String importFolderPath = StringUtils.replace(importFile.getParentFile().getPath(), "\\", "/") + "/";
		List<String> collocMemberFormMappingLines = readFileLines(importFile);
		collocMemberFormMappingLines.remove(0);//remove header

		List<CollocationTuple> collocMemberFormMappingTuples = new ArrayList<>();

		for (String collocMemberFormMappingLine : collocMemberFormMappingLines) {

			if (StringUtils.isBlank(collocMemberFormMappingLine)) {
				continue;
			}
			String[] collocMemberFormMappingCells = StringUtils.splitPreserveAllTokens(collocMemberFormMappingLine, CSV_SEPARATOR);
			if (collocMemberFormMappingCells.length != 5) {
				System.out.println(collocMemberFormMappingLine + "; " + collocMemberFormMappingCells.length);
				continue;
			}

			Long collocId = Long.valueOf(StringUtils.trim(collocMemberFormMappingCells[0]));
			String collocValue = StringUtils.trim(collocMemberFormMappingCells[1]);
			Long collocMemberWordId = Long.valueOf(StringUtils.trim(collocMemberFormMappingCells[2]));
			String collocMemberFormValue = StringUtils.trim(collocMemberFormMappingCells[3]);
			String collocMemberMorphCode = StringUtils.trim(collocMemberFormMappingCells[4]);

			CollocationTuple collocMemberFormMappingTuple = new CollocationTuple();
			collocMemberFormMappingTuple.setCollocId(collocId);
			collocMemberFormMappingTuple.setCollocValue(collocValue);
			collocMemberFormMappingTuple.setCollocMemberWordId(collocMemberWordId);
			collocMemberFormMappingTuple.setCollocMemberFormValue(collocMemberFormValue);
			collocMemberFormMappingTuple.setCollocMemberMorphCode(collocMemberMorphCode);

			collocMemberFormMappingTuples.add(collocMemberFormMappingTuple);
		}

		Map<Long, List<CollocationTuple>> collocMemberFormMap = collocMemberFormMappingTuples.stream().collect(Collectors.groupingBy(CollocationTuple::getCollocId));

		logger.info("Loaded colloc form mappings file");

		List<String> missingMembersCollocValues = new ArrayList<>();
		List<String> collocMemberFormIssues = new ArrayList<>();
		collocMemberFormIssues.add("colloc_id	colloc_value	colloc_memb_word_id	colloc_memb_form_value	colloc_memb_morph_codes	issue_code");

		final String datasetCode = DATASET_EKI;
		final String languageCode = LANGUAGE_CODE_EST;
		final String tagNameCollocMigration = "Kollide kolimine";
		final String tagNameCollocNoMembers = "Liikmeteta koll";
		Count missingMembersCollocCount = new Count();
		Count missingWordMorphCount = new Count();
		Count missingCollocMemberFormMappingCount = new Count();
		Count inconclusiveCollocMemberFormMappingCount = new Count();
		Count existingCollocMemberFormCount = new Count();
		Count multipleCollocMemberFormMappingCount = new Count();
		Count existingCollocMemberCount = new Count();
		Count createdCollocMemberCount = new Count();
		Count unresolvedCollocMemberCount = new Count();

		boolean tagExists;
		tagExists = tagDbService.tagExists(tagNameCollocMigration);
		if (!tagExists) {
			createTag(tagNameCollocMigration);
		}
		tagExists = tagDbService.tagExists(tagNameCollocNoMembers);
		if (!tagExists) {
			createTag(tagNameCollocNoMembers);
		}

		List<Long> collocIds = migrationDbService.getCollocationIds();

		int collocCounter = 0;
		int collocCount = collocIds.size();
		int progressIndicator = collocCount / Math.min(collocCount, 100);

		logger.info("Starting moving {} collocations...", collocCount);

		for (Long collocId : collocIds) {

			Collocation collocation = migrationDbService.getCollocation(collocId);
			List<CollocationTuple> collocationAndMembers = migrationDbService.getCollocationAndMembers(collocId);
			String collocValue = collocation.getValue();
			List<String> collocUsages = collocation.getCollocUsages();
			String definitionValue = collocation.getDefinition();
			Complexity complexity = collocation.getComplexity();

			// resolve colloc value word, lexeme, meaning

			WordLexemeMeaningIdTuple collocValueWordLexemeMeaningId = createCollocValueWordLexemeMeaning(
					collocValue, languageCode, datasetCode, complexity, tagNameCollocMigration);
			Long collocValueLexemeId = collocValueWordLexemeMeaningId.getLexemeId();
			Long collocValueMeaningId = collocValueWordLexemeMeaningId.getMeaningId();

			createDefinition(collocValueMeaningId, definitionValue, languageCode, datasetCode, complexity);
			createUsages(collocValueLexemeId, collocUsages, languageCode, datasetCode, complexity);

			// resolve colloc members

			if (CollectionUtils.isEmpty(collocationAndMembers)) {

				addTag(collocValueLexemeId, tagNameCollocNoMembers);
				if (!missingMembersCollocValues.contains(collocValue)) {
					missingMembersCollocValues.add(collocValue);
				}
				missingMembersCollocCount.increment();

			} else {

				List<CollocationTuple> collocMembersMappedForms = collocMemberFormMap.get(collocId);

				for (CollocationTuple collocMemberTuple : collocationAndMembers) {

					Long collocMemberWordId = collocMemberTuple.getCollocMemberWordId();
					String collocMemberFormValue = collocMemberTuple.getCollocMemberFormValue();

					List<MigraForm> collocMemberForms = migrationDbService.getForms(collocMemberWordId, collocMemberFormValue, null);
					MigraForm collocMemberForm;
					Long collocMemberFormId = null;
					CollocationTuple collocMemberMappedFormCandidate;
					String collocMemberMorphCode;

					if (CollectionUtils.isEmpty(collocMemberForms)) {

						// missing morpho
						missingWordMorphCount.increment();
						collocMemberFormId = createUnknownForm(collocMemberWordId, collocMemberFormValue);

					} else if (collocMemberForms.size() == 1) {

						// perfect, go ahead
						existingCollocMemberFormCount.increment();
						collocMemberForm = collocMemberForms.get(0);
						collocMemberFormId = collocMemberForm.getFormId();

					} else if (CollectionUtils.isEmpty(collocMembersMappedForms)) {

						// missing morpho mapping for entire colloc
						missingCollocMemberFormMappingCount.increment();
						addLog(collocMemberFormIssues, collocId, collocValue, collocMemberWordId, collocMemberFormValue, collocMemberForms, "<s2.1>");
						// TODO needs mapping
						// TODO ignoring, take any first
						collocMemberForm = collocMemberForms.get(0);
						collocMemberFormId = collocMemberForm.getFormId();

					} else {

						// more than one morphos by value only, looking for mapping
						Map<Long, List<CollocationTuple>> collocMemberMappedFormCandidateMap = collocMembersMappedForms.stream().collect(Collectors.groupingBy(CollocationTuple::getCollocMemberWordId));
						List<CollocationTuple> collocMemberMappedFormCandidates = collocMemberMappedFormCandidateMap.get(collocMemberWordId);

						if (CollectionUtils.isEmpty(collocMemberMappedFormCandidates)) {

							// missing morpho mapping for the member
							missingCollocMemberFormMappingCount.increment();
							addLog(collocMemberFormIssues, collocId, collocValue, collocMemberWordId, collocMemberFormValue, collocMemberForms, "<s2.2>");
							// TODO needs mapping
							// TODO ignoring, take any first
							collocMemberForm = collocMemberForms.get(0);
							collocMemberFormId = collocMemberForm.getFormId();

						} else {

							// morpho mapping(s) exists

							boolean isInconclusiveMapping = collocMemberMappedFormCandidates.stream()
									.map(CollocationTuple::getCollocMemberMorphCode)
									.anyMatch(morphCode -> StringUtils.equalsIgnoreCase(morphCode, "viga"));

							if (isInconclusiveMapping) {

								// inconclusive form mapping
								inconclusiveCollocMemberFormMappingCount.increment();
								collocMemberForm = collocMemberForms.get(0);
								collocMemberFormId = collocMemberForm.getFormId();

							} else {

								collocMemberMappedFormCandidates = collocMemberMappedFormCandidates.stream()
										.filter(tuple -> !StringUtils.equalsIgnoreCase(tuple.getCollocMemberMorphCode(), "viga"))
										.collect(Collectors.toList());

								if (CollectionUtils.isEmpty(collocMemberMappedFormCandidates)) {

									// missing morpho mapping for the member
									missingCollocMemberFormMappingCount.increment();
									addLog(collocMemberFormIssues, collocId, collocValue, collocMemberWordId, collocMemberFormValue, collocMemberForms, "<s2.3>");
									// TODO needs mapping
									// TODO ignoring, take any first
									collocMemberForm = collocMemberForms.get(0);
									collocMemberFormId = collocMemberForm.getFormId();

								} else if (collocMemberMappedFormCandidates.size() == 1) {

									// perfect, mapping exists
									existingCollocMemberFormCount.increment();
									collocMemberMappedFormCandidate = collocMemberMappedFormCandidates.get(0);
									collocMemberMorphCode = collocMemberMappedFormCandidate.getCollocMemberMorphCode();
									collocMemberForms = migrationDbService.getForms(collocMemberWordId, collocMemberFormValue, collocMemberMorphCode);
									collocMemberForm = collocMemberForms.get(0);
									collocMemberFormId = collocMemberForm.getFormId();

								} else {

									// multiple mappings, not an actual scenario
									multipleCollocMemberFormMappingCount.increment();
								}
							}
						}
					}

					if (collocMemberFormId == null) {

						unresolvedCollocMemberCount.increment();

					} else {

						createCollocMember(collocValueLexemeId, collocMemberFormId, collocMemberTuple);
						createdCollocMemberCount.increment();
					}
				}

				existingCollocMemberCount.increment(collocationAndMembers.size());
			}

			collocCounter++;
			if (collocCounter % progressIndicator == 0) {
				int progressPercent = collocCounter / progressIndicator;
				logger.info("{}% - {} collocations iterated", progressPercent, collocCounter);
			}
		}

		Collections.sort(missingMembersCollocValues);
		File missingMembersLogFile = new File(importFolderPath + "missing-members-colloc-values.txt");
		FileUtils.writeLines(missingMembersLogFile, "UTF-8", missingMembersCollocValues);

		File collocMemberFormIssuesLogFile = new File(importFolderPath + "colloc-member-form-issues.tsv");
		FileUtils.writeLines(collocMemberFormIssuesLogFile, "UTF-8", collocMemberFormIssues);

		logger.info("colloc count: {}", collocIds.size());
		logger.info("missingMembersCollocCount: {}", missingMembersCollocCount.getValue());
		logger.info("missingWordMorphCount: {}", missingWordMorphCount.getValue());
		logger.info("missingCollocMemberMorphMappingCount: {}", missingCollocMemberFormMappingCount.getValue());
		logger.info("existingCollocMemberFormCount: {}", existingCollocMemberFormCount.getValue());
		logger.info("existingCollocMemberCount: {}", existingCollocMemberCount.getValue());
		logger.info("inconclusiveCollocMemberFormMappingCount: {}", inconclusiveCollocMemberFormMappingCount.getValue());
		logger.info("multipleCollocMemberFormMappingCount: {}", multipleCollocMemberFormMappingCount.getValue());
		logger.info("createdCollocMemberCount: {}", createdCollocMemberCount.getValue());
		logger.info("unresolvedCollocMemberCount: {}", unresolvedCollocMemberCount.getValue());

		logger.info("Done");
	}

	private void createSecurityContext() {

		EkiUser user = new EkiUser();
		user.setName("Kollokatsioonide kolija");
		user.setAdmin(true);
		user.setEnabled(Boolean.TRUE);

		DatasetPermission recentRole = new DatasetPermission();
		recentRole.setDatasetName("xxx");
		recentRole.setSuperiorDataset(true);
		recentRole.setSuperiorPermission(true);
		user.setRecentRole(recentRole);

		GrantedAuthority authority = new SimpleGrantedAuthority("import");
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("cmov", user, Arrays.asList(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void addLog(
			List<String> collocMemberFormIssues,
			Long collocId,
			String collocValue,
			Long collocMemberWordId,
			String collocMemberFormValue,
			List<MigraForm> collocMemberForms,
			String issueCode) {

		String morphCodesStr = StringUtils.join(collocMemberForms.stream().map(MigraForm::getMorphCode).distinct().collect(Collectors.toList()), ",");
		String logRow = collocId + "\t" + collocValue + "\t" + collocMemberWordId + "\t" + collocMemberFormValue + "\t" + morphCodesStr + "\t" + issueCode;
		collocMemberFormIssues.add(logRow);
	}

	private Long createUnknownForm(Long wordId, String formValue) {

		if (!doSave) {
			return null;
		}
		Long formId = migrationDbService.createFormWithBlankParadigm(wordId, formValue, UNKNOWN_FORM_CODE);
		return formId;
	}

	private void createTag(final String tagName) {

		if (!doSave) {
			return;
		}
		tagDbService.createTag(tagName, TagType.LEX, false, true);
	}

	private void addTag(Long lexemeId, final String tagName) {

		if (!doSave) {
			return;
		}
		cudDbService.createLexemeTag(lexemeId, tagName);
	}

	private WordLexemeMeaningIdTuple createCollocValueWordLexemeMeaning(String collocValue, String languageCode, String datasetCode, Complexity complexity, String tagName) throws Exception {

		if (!doSave) {
			return new WordLexemeMeaningIdTuple();
		}

		WordLexemeMeaningIdTuple wordLexemeMeaningId = migrationDbService.createWordAndLexemeAndMeaning(collocValue, languageCode, datasetCode, complexity, PUBLICITY_PUBLIC);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		tagDbService.createLexemeAutomaticTags(lexemeId);
		addTag(lexemeId, tagName);
		if (doLog) {
			activityLogService.createActivityLog(ACTIVITY_FUNCT_NAME, wordId, ActivityOwner.WORD, datasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			activityLogService.createActivityLog(ACTIVITY_FUNCT_NAME, lexemeId, ActivityOwner.LEXEME, datasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			activityLogService.createActivityLog(ACTIVITY_FUNCT_NAME, meaningId, ActivityOwner.MEANING, datasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		}

		return wordLexemeMeaningId;
	}

	private void createDefinition(Long meaningId, String definitionValue, final String languageCode, final String datasetCode, Complexity complexity) throws Exception {

		if (StringUtils.isBlank(definitionValue)) {
			return;
		}
		if (!doSave) {
			return;
		}

		ActivityLogData activityLog = null;
		if (doLog) {
			activityLog = activityLogService.prepareActivityLog(ACTIVITY_FUNCT_NAME, meaningId, ActivityOwner.MEANING, datasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		}
		Long definitionId = cudDbService.createDefinition(meaningId, definitionValue, definitionValue, languageCode, DEFINITION_TYPE_CODE_UNDEFINED, complexity, PUBLICITY_PUBLIC);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		if (doLog) {
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		}
	}

	private void createUsages(Long lexemeId, List<String> collocUsages, final String languageCode, final String datasetCode, Complexity complexity) throws Exception {

		if (CollectionUtils.isEmpty(collocUsages)) {
			return;
		}
		if (!doSave) {
			return;
		}

		for (String usageValue : collocUsages) {

			FreeForm freeform = new FreeForm();
			freeform.setType(FreeformType.USAGE);
			freeform.setValueText(usageValue);
			freeform.setValuePrese(usageValue);
			freeform.setLang(languageCode);
			freeform.setComplexity(complexity);
			freeform.setPublic(PUBLICITY_PUBLIC);

			ActivityLogData activityLog = null;
			if (doLog) {
				activityLog = activityLogService.prepareActivityLog(ACTIVITY_FUNCT_NAME, lexemeId, ActivityOwner.LEXEME, datasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			}
			Long usageId = cudDbService.createLexemeFreeform(lexemeId, freeform, USER_NAME);
			if (doLog) {
				activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
			}

		}
	}

	private void createCollocMember(Long collocValueLexemeId, Long collocMemberFormId, CollocationTuple collocMemberTuple) {

		if (!doSave) {
			return;
		}

		String posGroupCode = collocMemberTuple.getPosGroupCode();
		String relGroupName = collocMemberTuple.getRelGroupName();
		Long collocMemberLexemeId = collocMemberTuple.getCollocMemberLexemeId();
		String collocMemberConjunct = collocMemberTuple.getCollocMemberConjunct();
		BigDecimal collocMemberWeight = collocMemberTuple.getCollocMemberWeight();
		Integer collocGroupOrder = collocMemberTuple.getCollocGroupOrder();
		Integer collocMemberOrder = collocMemberTuple.getCollocMemberOrder();

		CollocationMember collocationMember = new CollocationMember();
		collocationMember.setCollocLexemeId(collocValueLexemeId);
		collocationMember.setMemberLexemeId(collocMemberLexemeId);
		collocationMember.setMemberFormId(collocMemberFormId);
		collocationMember.setPosGroupCode(posGroupCode);
		collocationMember.setRelGroupCode(relGroupName);
		collocationMember.setConjunct(collocMemberConjunct);
		collocationMember.setWeight(collocMemberWeight);
		collocationMember.setMemberOrder(collocMemberOrder);
		collocationMember.setGroupOrder(collocGroupOrder);

		migrationDbService.createCollocationMember(collocationMember);
	}
}
