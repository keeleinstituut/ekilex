package eki.ekilex.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.WordMeaningRelationsDetails;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.LogData;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordStress;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.LexRelationRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class CompositionService extends AbstractService implements GlobalConstant {

	private static final int DEFAULT_LEXEME_LEVEL = 1;

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Transactional
	public void createWordAndMeaningAndRelations(WordMeaningRelationsDetails wordMeaningRelationsDetails) throws Exception {

		String wordValue = wordMeaningRelationsDetails.getWordValue();
		String language = wordMeaningRelationsDetails.getLanguage();
		String morphCode = wordMeaningRelationsDetails.getMorphCode();
		Long meaningId = wordMeaningRelationsDetails.getMeaningId();
		Long relatedMeaningId = wordMeaningRelationsDetails.getRelatedMeaningId();
		String dataset = wordMeaningRelationsDetails.getDataset();
		boolean importMeaningData = wordMeaningRelationsDetails.isImportMeaningData();
		boolean createRelation = wordMeaningRelationsDetails.isCreateRelation();

		if (meaningId == null) {
			if (importMeaningData) {
				String userName = wordMeaningRelationsDetails.getUserName();
				List<String> userPermDatasetCodes = wordMeaningRelationsDetails.getUserPermDatasetCodes();
				meaningId = duplicateMeaningWithLexemesAndUpdateDataset(relatedMeaningId, userName, dataset, userPermDatasetCodes);
			} else {
				meaningId = cudDbService.createMeaning();
				activityLogService.createActivityLog("createWordAndMeaningAndRelations", meaningId, LifecycleLogOwner.MEANING);
			}
		}

		if (!importMeaningData) {
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
					.createWordAndLexeme(wordValue, wordValue, null, language, morphCode, dataset, PUBLICITY_PUBLIC, meaningId);
			Long wordId = wordLexemeMeaningId.getWordId();
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			List<String> tagNames = cudDbService.createLexemeAutomaticTags(lexemeId);

			LogData wordLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, wordValue);
			createLifecycleLog(wordLogData);
			LogData lexemeLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, dataset);
			createLifecycleLog(lexemeLogData);
			tagNames.forEach(tagName -> {
				LogData tagLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
				createLifecycleLog(tagLogData);
			});
			activityLogService.createActivityLog("createWordAndMeaningAndRelations", wordId, LifecycleLogOwner.WORD);
			activityLogService.createActivityLog("createWordAndMeaningAndRelations", lexemeId, LifecycleLogOwner.LEXEME);
		}

		if (createRelation) {
			String relationType = wordMeaningRelationsDetails.getRelationType();
			String oppositeRelationType = wordMeaningRelationsDetails.getOppositeRelationType();
			ActivityLogData activityLog;
			activityLog = activityLogService.prepareActivityLog("createWordAndMeaningAndRelations", meaningId, LifecycleLogOwner.MEANING);
			Long relationId = cudDbService.createMeaningRelation(meaningId, relatedMeaningId, relationType);
			activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
			LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId, relationType);
			createLifecycleLog(relationLogData);
			if (StringUtils.isNotEmpty(oppositeRelationType)) {
				boolean oppositeRelationExists = lookupDbService.meaningRelationExists(relatedMeaningId, meaningId, oppositeRelationType);
				if (oppositeRelationExists) {
					return;
				}
				activityLog = activityLogService.prepareActivityLog("createWordAndMeaningAndRelations", relatedMeaningId, LifecycleLogOwner.MEANING);
				Long oppositeRelationId = cudDbService.createMeaningRelation(relatedMeaningId, meaningId, oppositeRelationType);
				activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
				LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, oppositeRelationId, oppositeRelationType);
				createLifecycleLog(oppositeRelationLogData);
			}
		}
	}

	@Transactional
	public Optional<Long> optionalDuplicateMeaningWithLexemes(Long meaningId, String userName) throws Exception {
		return Optional.of(duplicateMeaningWithLexemes(meaningId, userName));
	}

	private Long duplicateMeaningWithLexemesAndUpdateDataset(Long meaningId, String userName, String dataset, List<String> userPermDatasetCodes) throws Exception {

		Map<Long, Long> lexemeIdAndDuplicateLexemeIdMap = new HashMap<>();
		Long duplicateMeaningId = duplicateMeaningData(meaningId, userName);
		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId, userPermDatasetCodes);
		for (LexemeRecord meaningLexeme : meaningLexemes) {
			Long meaningLexemeId = meaningLexeme.getId();
			Long duplicateLexemeId = duplicateLexemeData(meaningLexemeId, duplicateMeaningId, null, userName);
			lexemeIdAndDuplicateLexemeIdMap.put(meaningLexemeId, duplicateLexemeId);
		}
		duplicateLexemeRelations(lexemeIdAndDuplicateLexemeIdMap);
		updateLexemesDataset(lexemeIdAndDuplicateLexemeIdMap, dataset);
		return duplicateMeaningId;
	}

	private void updateLexemesDataset(Map<Long, Long> lexemeIdAndDuplicateLexemeIdMap, String dataset) throws Exception {
		for (Map.Entry<Long, Long> lexemeIdAndDuplicateLexemeId : lexemeIdAndDuplicateLexemeIdMap.entrySet()) {
			Long duplicateLexemeId = lexemeIdAndDuplicateLexemeId.getValue();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemesDataset", duplicateLexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateLexemeDataset(duplicateLexemeId, dataset);
			activityLogService.createActivityLog(activityLog, duplicateLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public List<Long> duplicateLexemeAndMeaningWithSameDatasetLexemes(Long lexemeId, String userName) throws Exception {

		Map<Long, Long> lexemeIdAndDuplicateLexemeIdMap = new HashMap<>();
		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		String datasetCode = lexeme.getDatasetCode();
		Long meaningId = lexeme.getMeaningId();
		Long duplicateMeaningId = duplicateMeaningData(meaningId, userName);

		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId, datasetCode);
		for (LexemeRecord meaningLexeme : meaningLexemes) {
			Long meaningLexemeId = meaningLexeme.getId();
			Long duplicateLexemeId = duplicateLexemeData(meaningLexemeId, duplicateMeaningId, null, userName);
			lexemeIdAndDuplicateLexemeIdMap.put(meaningLexemeId, duplicateLexemeId);
		}
		duplicateLexemeRelations(lexemeIdAndDuplicateLexemeIdMap);
		List<Long> duplicateLexemeIds = new ArrayList<>(lexemeIdAndDuplicateLexemeIdMap.values());
		return duplicateLexemeIds;
	}

	@Transactional
	public Long duplicateEmptyLexemeAndMeaning(Long lexemeId, String userName) throws Exception {
		Long duplicateMeaningId = cudDbService.createMeaning();
		activityLogService.createActivityLog("duplicateEmptyLexemeAndMeaning", duplicateMeaningId, LifecycleLogOwner.MEANING);
		Long duplicateLexemeId = compositionDbService.cloneEmptyLexeme(lexemeId, duplicateMeaningId);
		updateLexemeLevelsAfterDuplication(duplicateLexemeId);
		activityLogService.createActivityLog("duplicateEmptyLexemeAndMeaning", duplicateLexemeId, LifecycleLogOwner.LEXEME);

		String targetLexemeDescription = lifecycleLogDbService.getSimpleLexemeDescription(duplicateLexemeId);
		LogData meaningLogData = new LogData();
		meaningLogData.setUserName(userName);
		meaningLogData.setEventType(LifecycleEventType.CREATE);
		meaningLogData.setEntityName(LifecycleEntity.MEANING);
		meaningLogData.setProperty(LifecycleProperty.VALUE);
		meaningLogData.setEntityId(duplicateMeaningId);
		meaningLogData.setEntry(targetLexemeDescription);
		lifecycleLogDbService.createLog(meaningLogData);

		LogData lexemeLogData = new LogData();
		lexemeLogData.setUserName(userName);
		lexemeLogData.setEventType(LifecycleEventType.CREATE);
		lexemeLogData.setEntityName(LifecycleEntity.LEXEME);
		lexemeLogData.setProperty(LifecycleProperty.VALUE);
		lexemeLogData.setEntityId(duplicateLexemeId);
		lexemeLogData.setEntry(targetLexemeDescription);
		lifecycleLogDbService.createLog(lexemeLogData);

		return duplicateLexemeId;
	}

	@Transactional
	public void duplicateLexemeAndWord(Long lexemeId, String userName) throws Exception {

		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		Long wordId = lexeme.getWordId();
		Long duplicateWordId = duplicateWordData(wordId, userName);
		duplicateLexemeData(lexemeId, null, duplicateWordId, userName);
	}

	private Long duplicateMeaningWithLexemes(Long meaningId, String userName) throws Exception {

		Map<Long, Long> lexemeIdAndDuplicateLexemeIdMap = new HashMap<>();
		Long duplicateMeaningId = duplicateMeaningData(meaningId, userName);
		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId);
		for (LexemeRecord meaningLexeme : meaningLexemes) {
			Long lexemeId = meaningLexeme.getId();
			Long duplicateLexemeId = duplicateLexemeData(lexemeId, duplicateMeaningId, null, userName);
			lexemeIdAndDuplicateLexemeIdMap.put(lexemeId, duplicateLexemeId);
		}
		duplicateLexemeRelations(lexemeIdAndDuplicateLexemeIdMap);
		return duplicateMeaningId;
	}

	private Long duplicateLexemeData(Long lexemeId, Long meaningId, Long wordId, String userName) throws Exception {

		Long duplicateLexemeId = compositionDbService.cloneLexeme(lexemeId, meaningId, wordId);
		updateLexemeLevelsAfterDuplication(duplicateLexemeId);
		compositionDbService.cloneLexemeDerivs(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeFreeforms(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemePoses(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeRegisters(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeSoureLinks(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeCollocations(lexemeId, duplicateLexemeId);
		activityLogService.createActivityLog("duplicateLexemeData", duplicateLexemeId, LifecycleLogOwner.LEXEME);

		String sourceLexemeDescription = lifecycleLogDbService.getSimpleLexemeDescription(lexemeId);
		String targetLexemeDescription = lifecycleLogDbService.getExtendedLexemeDescription(duplicateLexemeId);
		LogData logData = new LogData();
		logData.setUserName(userName);
		logData.setEventType(LifecycleEventType.CLONE);
		logData.setEntityName(LifecycleEntity.LEXEME);
		logData.setProperty(LifecycleProperty.VALUE);
		logData.setEntityId(duplicateLexemeId);
		logData.setRecent(sourceLexemeDescription);
		logData.setEntry(targetLexemeDescription);
		lifecycleLogDbService.createLog(logData);

		return duplicateLexemeId;
	}

	private Long duplicateWordData(Long wordId, String userName) throws Exception {

		SimpleWord simpleWord = compositionDbService.getSimpleWord(wordId);
		Long duplicateWordId = compositionDbService.cloneWord(simpleWord);
		compositionDbService.cloneWordParadigmsAndForms(wordId, duplicateWordId);
		compositionDbService.cloneWordTypes(wordId, duplicateWordId);
		compositionDbService.cloneWordRelations(wordId, duplicateWordId);
		compositionDbService.cloneWordGroupMembers(wordId, duplicateWordId);
		compositionDbService.cloneWordFreeforms(wordId, duplicateWordId);
		compositionDbService.cloneWordEtymology(wordId, duplicateWordId);
		activityLogService.createActivityLog("duplicateWordData", duplicateWordId, LifecycleLogOwner.WORD);

		String wordDescription = simpleWord.getWordValue() + " - " + simpleWord.getLang();
		LogData logData = new LogData();
		logData.setUserName(userName);
		logData.setEventType(LifecycleEventType.CLONE);
		logData.setEntityName(LifecycleEntity.WORD);
		logData.setProperty(LifecycleProperty.VALUE);
		logData.setEntityId(duplicateWordId);
		logData.setEntry(wordDescription);
		lifecycleLogDbService.createLog(logData);

		return duplicateWordId;
	}

	private Long duplicateMeaningData(Long meaningId, String userName) throws Exception {

		Long duplicateMeaningId = compositionDbService.cloneMeaning(meaningId);
		compositionDbService.cloneMeaningDomains(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningRelations(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningFreeforms(meaningId, duplicateMeaningId);
		duplicateMeaningDefinitions(meaningId, duplicateMeaningId);
		activityLogService.createActivityLog("duplicateMeaningData", duplicateMeaningId, LifecycleLogOwner.MEANING);

		String targetMeaningDescription = lifecycleLogDbService.getCombinedMeaningDefinitions(duplicateMeaningId);
		LogData logData = new LogData();
		logData.setUserName(userName);
		logData.setEventType(LifecycleEventType.CLONE);
		logData.setEntityName(LifecycleEntity.MEANING);
		logData.setProperty(LifecycleProperty.VALUE);
		logData.setEntityId(duplicateMeaningId);
		logData.setEntry(targetMeaningDescription);
		lifecycleLogDbService.createLog(logData);

		return duplicateMeaningId;
	}

	private void duplicateMeaningDefinitions(Long meaningId, Long duplicateMeaningId) throws Exception {

		List<DefinitionRecord> meaningDefinitions = compositionDbService.getMeaningDefinitions(meaningId);
		for (DefinitionRecord meaningDefinition : meaningDefinitions) {
			Long duplicateDefinintionId = compositionDbService.cloneMeaningDefinition(meaningDefinition.getId(), duplicateMeaningId);
			compositionDbService.cloneDefinitionFreeforms(meaningDefinition.getId(), duplicateDefinintionId);
			compositionDbService.cloneDefinitionDatasets(meaningDefinition.getId(), duplicateDefinintionId);
			compositionDbService.cloneDefinitionSourceLinks(meaningDefinition.getId(), duplicateDefinintionId);
		}
	}

	private void duplicateLexemeRelations(Map<Long, Long> existingLexemeIdAndDuplicateLexemeIdMap) throws Exception {

		for (Map.Entry<Long, Long> lexemeIdAndDuplicateLexemeId : existingLexemeIdAndDuplicateLexemeIdMap.entrySet()) {

			Long existingLexemeId = lexemeIdAndDuplicateLexemeId.getKey();
			Long duplicateLexemeId = lexemeIdAndDuplicateLexemeId.getValue();
			ActivityLogData activityLog;

			List<LexRelationRecord> existingLexemeRelations = compositionDbService.getLexemeRelations(existingLexemeId);
			for (LexRelationRecord existingLexemeRelation : existingLexemeRelations) {

				Long existingLexeme1Id = existingLexemeRelation.getLexeme1Id();
				Long existingLexeme2Id = existingLexemeRelation.getLexeme2Id();
				String lexRelTypeCode = existingLexemeRelation.getLexRelTypeCode();
				Long lexemeRelationId;

				if (existingLexeme1Id.equals(existingLexemeId)) {
					if (existingLexemeIdAndDuplicateLexemeIdMap.containsKey(existingLexeme2Id)) {
						Long duplicateLexeme2Id = existingLexemeIdAndDuplicateLexemeIdMap.get(existingLexeme2Id);
						activityLog = activityLogService.prepareActivityLog("duplicateLexemeRelations", duplicateLexemeId, LifecycleLogOwner.LEXEME);
						lexemeRelationId = cudDbService.createLexemeRelation(duplicateLexemeId, duplicateLexeme2Id, lexRelTypeCode);
						activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
					} else {
						activityLog = activityLogService.prepareActivityLog("duplicateLexemeRelations", duplicateLexemeId, LifecycleLogOwner.LEXEME);
						lexemeRelationId = cudDbService.createLexemeRelation(duplicateLexemeId, existingLexeme2Id, lexRelTypeCode);
						activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
					}
				} else {
					if (existingLexemeIdAndDuplicateLexemeIdMap.containsKey(existingLexeme1Id)) {
						Long duplicateLexeme1Id = existingLexemeIdAndDuplicateLexemeIdMap.get(existingLexeme1Id);
						activityLog = activityLogService.prepareActivityLog("duplicateLexemeRelations", duplicateLexemeId, LifecycleLogOwner.LEXEME);
						lexemeRelationId = cudDbService.createLexemeRelation(duplicateLexeme1Id, duplicateLexemeId, lexRelTypeCode);
						activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
					} else {
						activityLog = activityLogService.prepareActivityLog("duplicateLexemeRelations", duplicateLexemeId, LifecycleLogOwner.LEXEME);
						lexemeRelationId = cudDbService.createLexemeRelation(existingLexeme1Id, duplicateLexemeId, lexRelTypeCode);
						activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
					}
				}
			}
		}
	}

	@Transactional
	public void joinMeanings(Long targetMeaningId, List<Long> sourceMeaningIds) throws Exception {
		for (Long sourceMeaningId : sourceMeaningIds) {
			joinMeanings(targetMeaningId, sourceMeaningId);
		}
	}

	private void joinMeanings(Long targetMeaningId, Long sourceMeaningId) throws Exception {

		String logEntrySource = compositionDbService.getFirstDefinitionOfMeaning(sourceMeaningId);
		String logEntryTarget  = compositionDbService.getFirstDefinitionOfMeaning(targetMeaningId);
		LogData logData = new LogData(
				LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, targetMeaningId, logEntrySource, logEntryTarget);
		createLifecycleLog(logData);

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinMeanings", sourceMeaningId, LifecycleLogOwner.MEANING);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinMeanings", targetMeaningId, LifecycleLogOwner.MEANING);

		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
		updateMeaningLexemesPublicity(targetMeaningId);

		activityLogService.createActivityLog(activityLog1, sourceMeaningId, ActivityEntity.MEANING);
		activityLogService.createActivityLog(activityLog2, targetMeaningId, ActivityEntity.MEANING);
	}

	@Transactional
	public void separateLexemeMeaning(Long lexemeId) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("separateLexemeMeaning", lexemeId, LifecycleLogOwner.LEXEME);
		Long meaningId = compositionDbService.separateLexemeMeaning(lexemeId);
		activityLogService.createActivityLog(activityLog, meaningId, ActivityEntity.MEANING);
	}

	@Transactional
	public void joinLexemes(Long targetLexemeId, List<Long> sourceLexemeIds) throws Exception {
		for (Long sourceLexemeId: sourceLexemeIds) {
			joinLexemes(targetLexemeId, sourceLexemeId);
		}
	}

	private void joinLexemes(Long targetLexemeId, Long sourceLexemeId) throws Exception {

		LexemeRecord targetLexeme = compositionDbService.getLexeme(targetLexemeId);
		LexemeRecord sourceLexeme = compositionDbService.getLexeme(sourceLexemeId);
		if (sourceLexeme == null) {
			return;
		}
		Long targetMeaningId = targetLexeme.getMeaningId();
		Long sourceMeaningId = sourceLexeme.getMeaningId();

		String logEntrySource = compositionDbService.getFirstDefinitionOfMeaning(sourceMeaningId);
		String logEntryTarget = compositionDbService.getFirstDefinitionOfMeaning(targetMeaningId);
		LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, targetMeaningId, logEntrySource,
				logEntryTarget);
		createLifecycleLog(logData);

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinLexemes", sourceLexemeId, LifecycleLogOwner.LEXEME);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinLexemes", targetLexemeId, LifecycleLogOwner.LEXEME);

		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
		updateMeaningLexemesPublicity(targetMeaningId);

		activityLogService.createActivityLog(activityLog1, sourceLexemeId, ActivityEntity.LEXEME);
		activityLogService.createActivityLog(activityLog2, targetLexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public Long joinWords(Long targetWordId, List<Long> sourceWordIds) throws Exception {
		for (Long sourceWordId : sourceWordIds) {
			targetWordId = joinWords(targetWordId, sourceWordId);
		}
		return targetWordId;
	}

	private Long joinWords(Long firstWordId, Long secondWordId) throws Exception {

		SimpleWord firstWord = lookupDbService.getSimpleWord(firstWordId);
		String wordValue = firstWord.getWordValue();
		Integer firstWordHomonymNum = compositionDbService.getWordHomonymNum(firstWordId);
		Integer secondWordHomonymNum = compositionDbService.getWordHomonymNum(secondWordId);
		Long targetWordId = firstWordHomonymNum <= secondWordHomonymNum ? firstWordId : secondWordId;
		Long sourceWordId = secondWordHomonymNum >= firstWordHomonymNum? secondWordId : firstWordId;

		LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.WORD, LifecycleProperty.VALUE, targetWordId, wordValue, wordValue);
		createLifecycleLog(logData);

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinWords", sourceWordId, LifecycleLogOwner.WORD);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinWords", targetWordId, LifecycleLogOwner.WORD);

		SimpleWord sourceWord = lookupDbService.getSimpleWord(sourceWordId);
		compositionDbService.joinWordData(targetWordId, sourceWordId);
		joinWordStressAndMarkupData(targetWordId, sourceWordId);
		joinLexemeData(targetWordId, sourceWordId);
		joinParadigms(targetWordId, sourceWordId);
		cudDbService.deleteWord(sourceWord);
		updateWordLexemesPublicity(targetWordId);

		activityLogService.createActivityLog(activityLog1, sourceWordId, ActivityEntity.WORD);
		activityLogService.createActivityLog(activityLog2, targetWordId, ActivityEntity.WORD);

		return targetWordId;
	}

	private void joinWordStressAndMarkupData(Long targetWordId, Long sourceWordId) {

		Map<Long, WordStress> wordStressData = lookupDbService.getWordStressData(sourceWordId, targetWordId, DISPLAY_FORM_STRESS_SYMBOL);
		WordStress sourceWordStress = wordStressData.get(sourceWordId);
		WordStress targetWordStress = wordStressData.get(targetWordId);

		String sourceDisplayForm = sourceWordStress.getDisplayForm();
		String sourceValuePrese = sourceWordStress.getValuePrese();
		boolean isSourceDisplayFormStressExists = sourceWordStress.isStressExists();
		String targetDisplayForm = targetWordStress.getDisplayForm();
		String targetValuePrese = targetWordStress.getValuePrese();
		boolean isTargetDisplayFormStressExists = targetWordStress.isStressExists();
		Long targetFormId = targetWordStress.getFormId();

		if (sourceDisplayForm != null) {
			if (targetDisplayForm == null) {
				cudDbService.updateFormDisplayForm(targetFormId, sourceDisplayForm);
			} else {
				if (isSourceDisplayFormStressExists && !isTargetDisplayFormStressExists) {
					cudDbService.updateFormDisplayForm(targetFormId, sourceDisplayForm);
				} else if (!isSourceDisplayFormStressExists && isTargetDisplayFormStressExists) {
					// do nothing
				} else if (sourceDisplayForm.length() > targetDisplayForm.length()) {
					cudDbService.updateFormDisplayForm(targetFormId, sourceDisplayForm);
				}
			}
		}

		if (!StringUtils.equals(targetValuePrese, sourceValuePrese)) {
			boolean isTargetWordDecorated = textDecorationService.isDecorated(targetValuePrese);
			boolean isSourceWordDecorated = textDecorationService.isDecorated(sourceValuePrese);
			if (!isTargetWordDecorated && isSourceWordDecorated) {
				cudDbService.updateFormValuePrese(targetFormId, sourceValuePrese);
			}
		}
	}

	private void joinLexemeData(Long targetWordId, Long sourceWordId) {

		List<LexemeRecord> sourceWordLexemes = compositionDbService.getWordLexemes(sourceWordId);
		for (LexemeRecord sourceWordLexeme : sourceWordLexemes) {
			Long sourceWordLexemeId = sourceWordLexeme.getId();
			Long sourceWordLexemeMeaningId = sourceWordLexeme.getMeaningId();
			String sourceWordLexemeDatasetCode = sourceWordLexeme.getDatasetCode();
			boolean isSourceWordLexemePrimaryType = StringUtils.equals(sourceWordLexeme.getType(), LEXEME_TYPE_PRIMARY);

			LexemeRecord targetWordLexeme = compositionDbService.getLexeme(targetWordId, sourceWordLexemeMeaningId, sourceWordLexemeDatasetCode);
			boolean targetLexemeExists = targetWordLexeme != null;

			if (targetLexemeExists) {
				Long targetWordLexemeId = targetWordLexeme.getId();
				boolean isTargetWordLexemePrimaryType = StringUtils.equals(targetWordLexeme.getType(), LEXEME_TYPE_PRIMARY);

				if (isTargetWordLexemePrimaryType && isSourceWordLexemePrimaryType) {
					compositionDbService.joinLexemes(targetWordLexemeId, sourceWordLexemeId);
				} else if (isSourceWordLexemePrimaryType) {
					cudDbService.deleteLexeme(targetWordLexemeId);
					connectLexemeToAnotherWord(targetWordId, sourceWordLexemeId, sourceWordLexemeDatasetCode);
				} else {
					cudDbService.deleteLexeme(sourceWordLexemeId);
				}
			} else {
				connectLexemeToAnotherWord(targetWordId, sourceWordLexemeId, sourceWordLexemeDatasetCode);
			}

			cudDbService.adjustWordSecondaryLexemesComplexity(targetWordId);
			cudDbService.adjustWordSecondaryLexemesComplexity(sourceWordId);
		}
	}

	private void connectLexemeToAnotherWord(Long targetWordId, Long sourceWordLexemeId, String datasetCode) {

		Integer currentTargetWordLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(targetWordId, datasetCode);
		int level1 = currentTargetWordLexemesMaxLevel1 + 1;
		compositionDbService.updateLexemeWordIdAndLevels(sourceWordLexemeId, targetWordId, level1, DEFAULT_LEXEME_LEVEL);
	}

	private void joinParadigms(Long targetWordId, Long sourceWordId) {

		boolean targetWordHasForms = lookupDbService.wordHasForms(targetWordId);
		if (targetWordHasForms) {
			return;
		}
		boolean sourceWordHasForms = lookupDbService.wordHasForms(sourceWordId);
		if (sourceWordHasForms) {
			compositionDbService.joinParadigms(targetWordId, sourceWordId);
		}
	}

	private void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordPrimaryLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2());
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			createLifecycleLog(logData);
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2());
		}
	}

	private void updateLexemeLevelsAfterDuplication(Long duplicateLexemeId) throws Exception {

		LexemeRecord duplicatedLexeme = compositionDbService.getLexeme(duplicateLexemeId);
		Integer level1 = duplicatedLexeme.getLevel1();
		Integer level2 = duplicatedLexeme.getLevel2();
		Long wordId = duplicatedLexeme.getWordId();
		String datasetCode = duplicatedLexeme.getDatasetCode();

		Integer level2MinValue = compositionDbService.getLevel2MinimumValue(wordId, datasetCode, level1);
		boolean isLevel1Increase = Objects.equals(level2, level2MinValue);

		if (isLevel1Increase) {
			List<LexemeRecord> lexemesWithLargerLevel1 = compositionDbService.getLexemesWithHigherLevel1(wordId, datasetCode, level1);
			int increasedDuplicatedLexemeLevel1 = level1 + 1;
			cudDbService.updateLexemeLevel1(duplicateLexemeId, increasedDuplicatedLexemeLevel1);
			for (LexemeRecord lexeme : lexemesWithLargerLevel1) {
				Long lexemeId = lexeme.getId();
				int increasedLevel1 = lexeme.getLevel1() + 1;
				cudDbService.updateLexemeLevel1(lexemeId, increasedLevel1);
			}
		} else {
			List<LexemeRecord> lexemesWithLargerLevel2 = compositionDbService.getLexemesWithHigherLevel2(wordId, datasetCode, level1, level2);
			int increasedDuplicatedLexemeLevel2 = level2 + 1;
			cudDbService.updateLexemeLevel2(duplicateLexemeId, increasedDuplicatedLexemeLevel2);
			for (LexemeRecord lexeme : lexemesWithLargerLevel2) {
				Long lexemeId = lexeme.getId();
				int increasedLevel2 = lexeme.getLevel2() + 1;
				cudDbService.updateLexemeLevel2(lexemeId, increasedLevel2);
			}
		}
	}

	private void updateMeaningLexemesPublicity(Long targetMeaningId) {

		boolean publicLexemeExists = lookupDbService.meaningPublicLexemeExists(targetMeaningId);
		if (publicLexemeExists) {
			cudDbService.updateMeaningLexemesPublicity(targetMeaningId, PUBLICITY_PUBLIC);
		}
	}

	private void updateWordLexemesPublicity(Long targetWordId) {

		boolean publicLexemeExists = lookupDbService.wordPublicLexemeExists(targetWordId);
		if (publicLexemeExists) {
			cudDbService.updateWordLexemesPublicity(targetWordId, PUBLICITY_PUBLIC);
		}
	}

	//TODO revisit activity logging
	private void joinMeaningsCommonWordsLexemes(Long targetMeaningId, Long sourceMeaningId) throws Exception {

		List<IdPair> meaningsCommonWordsLexemeIdPairs = compositionDbService.getMeaningsCommonWordsLexemeIdPairs(targetMeaningId, sourceMeaningId);
		boolean meaningsShareCommonWord = CollectionUtils.isNotEmpty(meaningsCommonWordsLexemeIdPairs);
		if (meaningsShareCommonWord) {
			for (IdPair lexemeIdPair : meaningsCommonWordsLexemeIdPairs) {
				Long targetLexemeId = lexemeIdPair.getId1();
				Long sourceLexemeId = lexemeIdPair.getId2();
				LexemeRecord targetLexeme = compositionDbService.getLexeme(targetLexemeId);
				LexemeRecord sourceLexeme = compositionDbService.getLexeme(sourceLexemeId);
				Long targetLexemeWordId = targetLexeme.getWordId();
				Long sourceLexemeWordId = sourceLexeme.getWordId();
				boolean isTargetLexemePrimaryType = StringUtils.equals(targetLexeme.getType(), LEXEME_TYPE_PRIMARY);
				boolean isSourceLexemePrimaryType = StringUtils.equals(sourceLexeme.getType(), LEXEME_TYPE_PRIMARY);

				if (isTargetLexemePrimaryType && isSourceLexemePrimaryType) {
					updateLexemeLevels(sourceLexemeId, "delete");

					String logEntrySource = StringUtils.joinWith(".", sourceLexeme.getLevel1(), sourceLexeme.getLevel2());
					String logEntryTarget = StringUtils.joinWith(".", targetLexeme.getLevel1(), targetLexeme.getLevel2());
					LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, targetLexemeId, logEntrySource, logEntryTarget);
					createLifecycleLog(logData);

					compositionDbService.joinLexemes(targetLexemeId, sourceLexemeId);

					List<String> tagNames = cudDbService.createLexemeAutomaticTags(targetLexemeId);
					tagNames.forEach(tagName -> {
						LogData tagLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, targetLexemeId, tagName);
						createLifecycleLog(tagLogData);
					});
				} else if (isSourceLexemePrimaryType) {
					String datasetCode = targetLexeme.getDatasetCode();
					cudDbService.deleteLexeme(targetLexemeId);
					connectLexemeToAnotherWord(targetLexemeWordId, sourceLexemeId, datasetCode);
				} else {
					cudDbService.deleteLexeme(sourceLexemeId);
				}

				cudDbService.adjustWordSecondaryLexemesComplexity(sourceLexemeWordId);
				cudDbService.adjustWordSecondaryLexemesComplexity(targetLexemeWordId);
			}
		}
	}

	@Transactional
	public boolean validateMeaningDataImport(Long meaningId, List<String> userPermDatasetCodes) {

		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId, userPermDatasetCodes);
		long distinctWordIdCount = meaningLexemes.stream().map(LexemeRecord::getWordId).distinct().count();
		long meaningLexemesCount = meaningLexemes.size();
		return meaningLexemesCount == distinctWordIdCount;
	}
}
