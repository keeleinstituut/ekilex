package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.PermConstant;
import eki.common.data.AsWordResult;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Word;
import eki.ekilex.data.db.main.tables.records.LexRelationRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRecord;
import eki.ekilex.data.db.main.tables.records.WordRecord;
import eki.ekilex.security.EkilexPermissionEvaluator;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.TagDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class CompositionService extends AbstractCudService implements PermConstant {

	private static final Logger logger = LoggerFactory.getLogger(CompositionService.class);

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private TagDbService tagDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private EkilexPermissionEvaluator ekilexPermissionEvaluator;

	@Transactional(rollbackFor = Exception.class)
	public Long cloneMeaningWithLexemes(Long sourceMeaningId, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Map<Long, Long> sourceTargetLexemeIdMap = new HashMap<>();
		boolean publicDataOnly = false;
		Long targetMeaningId = cloneMeaningAndData(sourceMeaningId, publicDataOnly, roleDatasetCode, isManualEventOnUpdateEnabled);
		List<LexemeRecord> sourceLexemes = lookupDbService.getLexemeRecordsByMeaning(sourceMeaningId);
		for (LexemeRecord sourceLexeme : sourceLexemes) {
			Long sourceLexemeId = sourceLexeme.getId();
			Long targetLexemeId = cloneLexemeAndData(sourceLexemeId, targetMeaningId, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceTargetLexemeIdMap.put(sourceLexemeId, targetLexemeId);
		}
		cloneLexemeRelations(sourceTargetLexemeIdMap, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetMeaningId;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean cloneLexemeMeaningAndLexemes(Long lexemeId, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Map<Long, Long> sourceTargetLexemeIdMap = new HashMap<>();
		boolean isPublicDataOnly = false;
		LexemeRecord lexeme = lookupDbService.getLexemeRecord(lexemeId);
		String datasetCode = lexeme.getDatasetCode();
		Long sourceWordId = lexeme.getWordId();
		Long sourceMeaningId = lexeme.getMeaningId();
		Long targetMeaningId = cloneMeaningAndData(sourceMeaningId, isPublicDataOnly, roleDatasetCode, isManualEventOnUpdateEnabled);

		List<LexemeRecord> sourceLexemes = lookupDbService.getLexemeRecordsByMeaning(sourceMeaningId, datasetCode);
		for (LexemeRecord sourceLexeme : sourceLexemes) {
			Long sourceLexemeId = sourceLexeme.getId();
			Long targetLexemeId = cloneLexemeAndData(sourceLexemeId, targetMeaningId, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceTargetLexemeIdMap.put(sourceLexemeId, targetLexemeId);
		}
		cloneLexemeRelations(sourceTargetLexemeIdMap, roleDatasetCode, isManualEventOnUpdateEnabled);
		recalculateAndUpdateAllLexemeLevels(sourceWordId, datasetCode);
		boolean success = MapUtils.isNotEmpty(sourceTargetLexemeIdMap);
		return success;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long cloneEmptyLexemeAndMeaning(Long sourceLexemeId, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord sourceLexemeRecord = lookupDbService.getLexemeRecord(sourceLexemeId);
		Long sourceWordId = sourceLexemeRecord.getWordId();
		String datasetCode = sourceLexemeRecord.getDatasetCode();
		Long targetMeaningId = cudDbService.createMeaning();
		activityLogService.createActivityLog("cloneEmptyLexemeAndMeaning", targetMeaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long targetLexemeId = compositionDbService.cloneEmptyLexeme(sourceLexemeId, targetMeaningId);
		recalculateAndUpdateAllLexemeLevels(sourceWordId, datasetCode);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME, targetLexemeId);
		activityLogService.createActivityLog("cloneEmptyLexemeAndMeaning", targetLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetLexemeId;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long cloneLexemeAndWord(Long sourceLexemeId, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord sourceLexemeRecord = lookupDbService.getLexemeRecord(sourceLexemeId);
		Long sourceWordId = sourceLexemeRecord.getWordId();
		String datasetCode = sourceLexemeRecord.getDatasetCode();
		Long targetWordId = cloneWordAndData(sourceWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long targetLexemeId = cloneLexemeAndData(sourceLexemeId, null, targetWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME, targetLexemeId);
		recalculateAndUpdateAllLexemeLevels(sourceWordId, datasetCode);

		return targetLexemeId;
	}

	@Transactional(rollbackFor = Exception.class)
	public IdPair cloneWordAndMoveLexeme(Long lexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord lexemeRecord = lookupDbService.getLexemeRecord(lexemeId);
		Long sourceWordId = lexemeRecord.getWordId();
		String datasetCode = lexemeRecord.getDatasetCode();
		ActivityLogData lexemeActivityLog = activityLogService.prepareActivityLog("cloneWordAndMoveLexeme", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long targetWordId = cloneWordAndData(sourceWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeWordId(lexemeId, targetWordId);
		recalculateAndUpdateAllLexemeLevels(sourceWordId, datasetCode);
		recalculateAndUpdateAllLexemeLevels(targetWordId, datasetCode);
		activityLogService.createActivityLog(lexemeActivityLog, lexemeId, ActivityEntity.LEXEME);

		return new IdPair(sourceWordId, targetWordId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void moveLexeme(Long lexemeId, Long targetWordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord lexemeRecord = lookupDbService.getLexemeRecord(lexemeId);
		Long sourceWordId = lexemeRecord.getWordId();
		String datasetCode = lexemeRecord.getDatasetCode();
		ActivityLogData lexemeActivityLog = activityLogService.prepareActivityLog("moveLexeme", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeWordId(lexemeId, targetWordId);
		recalculateAndUpdateAllLexemeLevels(sourceWordId, datasetCode);
		recalculateAndUpdateAllLexemeLevels(targetWordId, datasetCode);
		cudDbService.deleteFloatingWord(sourceWordId);
		activityLogService.createActivityLog(lexemeActivityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateWordValuePrese(EkiUser user, Long wordId, String wordValuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		DatasetPermission userRole = user.getRecentRole();
		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, datasetCode, wordId);
		if (isWordCrudGrant) {
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordValuePrese", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordValuePrese(wordId, wordValuePrese);
			activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
		}
		return isWordCrudGrant;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateLexemeWordValue(Long lexemeId, String wordValuePrese, String wordLang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData lexemeActivityLog = activityLogService.prepareActivityLog("updateLexemeWordValue", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		String wordValue = textDecorationService.removeEkiElementMarkup(wordValuePrese);
		LexemeRecord sourceLexemeRecord = lookupDbService.getLexemeRecord(lexemeId);
		Long sourceWordId = sourceLexemeRecord.getWordId();
		String datasetCode = sourceLexemeRecord.getDatasetCode();
		WordRecord sourceWord = lookupDbService.getWordRecord(sourceWordId);
		String sourceWordValue = sourceWord.getValue();
		String sourceWordLang = sourceWord.getLang();
		List<Long> sourceDatasetCodes = lookupDbService.getWordDatasetCodes(sourceWordId);
		boolean sourceWordInSingleDataset = sourceDatasetCodes.size() == 1;
		List<Word> existingSameValueWords = lookupDbService.getWords(wordValue, wordLang);

		if (CollectionUtils.isEmpty(existingSameValueWords)) {

			if (sourceWordInSingleDataset) {

				ActivityLogData wordActivityLog = activityLogService.prepareActivityLog("updateLexemeWordValue", sourceWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
				updateWordValue(sourceWordId, wordValue, wordValuePrese, wordLang, sourceWordValue, sourceWordLang);
				activityLogService.createActivityLog(wordActivityLog, sourceWordId, ActivityEntity.WORD);

			} else {

				Long targetWordId = cloneWordAndData(sourceWordId, datasetCode, isManualEventOnUpdateEnabled);
				ActivityLogData wordActivityLog = activityLogService.prepareActivityLog("updateLexemeWordValue", targetWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
				updateWordValue(targetWordId, wordValue, wordValuePrese, wordLang, sourceWordValue, sourceWordLang);
				cudDbService.updateLexemeWordId(lexemeId, targetWordId);
				recalculateAndUpdateAllLexemeLevels(targetWordId, datasetCode);
				activityLogService.createActivityLog(wordActivityLog, targetWordId, ActivityEntity.WORD);
			}

		} else if (existingSameValueWords.size() == 1) {

			Word existingSameValueWord = existingSameValueWords.get(0);
			Long existingWordId = existingSameValueWord.getWordId();
			cudDbService.updateLexemeWordId(lexemeId, existingWordId);
			recalculateAndUpdateAllLexemeLevels(existingWordId, datasetCode);

		} else if (existingSameValueWords.size() > 1) {
			throw new UnsupportedOperationException();
		}

		cudDbService.deleteFloatingWord(sourceWordId);
		activityLogService.createActivityLog(lexemeActivityLog, lexemeId, ActivityEntity.LEXEME);
	}

	private void updateWordValue(Long wordId, String wordValue, String wordValuePrese, String wordLang, String originalWordValue, String originalWordLang) {

		AsWordResult asWordResult = textDecorationService.getAsWordResult(wordValue);
		String valueAsWord = asWordResult.getValueAsWord();
		boolean valueAsWordExists = asWordResult.isValueAsWordExists();
		SimpleWord originalSimpleWord = new SimpleWord(null, originalWordValue, originalWordLang);
		SimpleWord updatedSimpleWord = new SimpleWord(null, wordValue, wordLang);

		cudDbService.updateWordValueAndLang(wordId, wordValue, wordValuePrese, wordLang);
		if (valueAsWordExists) {
			cudDbService.updateAsWordValue(wordId, valueAsWord);
		}

		cudDbService.adjustWordHomonymNrs(originalSimpleWord);
		cudDbService.adjustWordHomonymNrs(updatedSimpleWord);
	}

	private Long cloneLexemeAndData(Long sourceLexemeId, Long targetMeaningId, Long targetWordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long targetLexemeId = compositionDbService.cloneLexeme(sourceLexemeId, targetMeaningId, targetWordId);
		compositionDbService.cloneLexemeUsagesAndSubdata(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeNotesAndSubdata(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeTags(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeGrammarsAndSubdata(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeGovernmentsAndSubdata(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeDerivs(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeRegions(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemePoses(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeRegisters(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeFreeformsAndSubdata(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeSoureLinks(sourceLexemeId, targetLexemeId);
		compositionDbService.clonePublishing(sourceLexemeId, targetLexemeId, ENTITY_NAME_LEXEME);
		// cloning of lexeme relations are in separate flow after all lexemes have been cloned

		activityLogService.createActivityLog("cloneLexemeAndData", targetLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetLexemeId;
	}

	private Long cloneWordAndData(Long sourceWordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord sourceWord = compositionDbService.getSimpleWord(sourceWordId);
		Long targetWordId = compositionDbService.cloneWord(sourceWord);
		compositionDbService.cloneWordParadigmsAndForms(sourceWordId, targetWordId);
		compositionDbService.cloneWordTypes(sourceWordId, targetWordId);
		compositionDbService.cloneWordTags(sourceWordId, targetWordId);
		compositionDbService.cloneWordForums(sourceWordId, targetWordId);
		compositionDbService.cloneWordRelationsAndSubdata(sourceWordId, targetWordId);
		compositionDbService.cloneWordGroupMembers(sourceWordId, targetWordId);
		compositionDbService.cloneWordFreeformsAndSubdata(sourceWordId, targetWordId);
		compositionDbService.cloneWordEtymologyAndSubdata(sourceWordId, targetWordId);

		activityLogService.createActivityLog("cloneWordAndData", targetWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetWordId;
	}

	private Long cloneMeaningAndData(Long sourceMeaningId, boolean isPublicDataOnly, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long targetMeaningId = compositionDbService.cloneMeaning(sourceMeaningId);
		compositionDbService.cloneMeaningSemanticType(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningTags(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningForums(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningDomains(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningNotesAndSubdata(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningImagesAndSubdata(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningMediasAndSubdata(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningRelations(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningFreeformsAndSubdata(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningDefinitionsAndSubdata(sourceMeaningId, targetMeaningId, isPublicDataOnly);

		activityLogService.createActivityLog("cloneMeaningAndData", targetMeaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetMeaningId;
	}

	private void cloneLexemeRelations(Map<Long, Long> sourceTargetLexemeIdMap, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (Map.Entry<Long, Long> sourceTargetLexemeIdEntry : sourceTargetLexemeIdMap.entrySet()) {

			Long sourceLexemeId = sourceTargetLexemeIdEntry.getKey();
			Long targetLexemeId = sourceTargetLexemeIdEntry.getValue();
			ActivityLogData activityLog;

			List<LexRelationRecord> sourceLexemeRelations = lookupDbService.getLexRelationRecords(sourceLexemeId);
			for (LexRelationRecord sourceLexemeRelation : sourceLexemeRelations) {

				Long relationSourceLexeme1Id = sourceLexemeRelation.getLexeme1Id();
				Long relationSourceLexeme2Id = sourceLexemeRelation.getLexeme2Id();
				String lexRelTypeCode = sourceLexemeRelation.getLexRelTypeCode();
				Long lexemeRelationId;

				activityLog = activityLogService.prepareActivityLog("cloneLexemeRelations", targetLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
				if (relationSourceLexeme1Id.equals(sourceLexemeId)) {
					if (sourceTargetLexemeIdMap.containsKey(relationSourceLexeme2Id)) {
						Long targetLexeme2Id = sourceTargetLexemeIdMap.get(relationSourceLexeme2Id);
						lexemeRelationId = cudDbService.createLexemeRelation(targetLexemeId, targetLexeme2Id, lexRelTypeCode);
					} else {
						lexemeRelationId = cudDbService.createLexemeRelation(targetLexemeId, relationSourceLexeme2Id, lexRelTypeCode);
					}
				} else {
					if (sourceTargetLexemeIdMap.containsKey(relationSourceLexeme1Id)) {
						Long targetLexeme1Id = sourceTargetLexemeIdMap.get(relationSourceLexeme1Id);
						lexemeRelationId = cudDbService.createLexemeRelation(targetLexeme1Id, targetLexemeId, lexRelTypeCode);
					} else {
						lexemeRelationId = cudDbService.createLexemeRelation(relationSourceLexeme1Id, targetLexemeId, lexRelTypeCode);
					}
				}
				if (lexemeRelationId != null) {
					activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
				}
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void joinMeanings(Long targetMeaningId, List<Long> sourceMeaningIds, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceMeaningId : sourceMeaningIds) {
			joinMeanings(targetMeaningId, sourceMeaningId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	private void joinMeanings(Long targetMeaningId, Long sourceMeaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinMeanings", sourceMeaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinMeanings", targetMeaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);

		activityLogService.joinApproveMeaning(targetMeaningId, sourceMeaningId);
		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
		updateMeaningLexemesPublicity(targetMeaningId, DATASET_EKI);

		activityLogService.createActivityLog(activityLog1, sourceMeaningId, ActivityEntity.MEANING);
		activityLogService.createActivityLog(activityLog2, targetMeaningId, ActivityEntity.MEANING);
	}

	@Transactional(rollbackFor = Exception.class)
	public void joinLexemes(Long targetLexemeId, List<Long> sourceLexemeIds, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceLexemeId : sourceLexemeIds) {
			joinLexemes(targetLexemeId, sourceLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	private void joinLexemes(Long targetLexemeId, Long sourceLexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord sourceLexeme = lookupDbService.getLexemeRecord(sourceLexemeId);
		LexemeRecord targetLexeme = lookupDbService.getLexemeRecord(targetLexemeId);
		if (sourceLexeme == null) {
			return;
		}
		Long targetMeaningId = targetLexeme.getMeaningId();
		Long sourceMeaningId = sourceLexeme.getMeaningId();

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinLexemes", sourceLexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinLexemes", targetLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);

		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
		updateMeaningLexemesPublicity(targetMeaningId, DATASET_EKI);

		activityLogService.createActivityLog(activityLog1, sourceLexemeId, ActivityEntity.LEXEME);
		activityLogService.createActivityLog(activityLog2, targetLexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long joinWords(Long targetWordId, List<Long> sourceWordIds, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceWordId : sourceWordIds) {
			targetWordId = joinWords(targetWordId, sourceWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
		return targetWordId;
	}

	public Long joinWords(Long firstWordId, Long secondWordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Integer firstWordHomonymNum = compositionDbService.getWordHomonymNum(firstWordId);
		Integer secondWordHomonymNum = compositionDbService.getWordHomonymNum(secondWordId);
		Long targetWordId = firstWordHomonymNum <= secondWordHomonymNum ? firstWordId : secondWordId;
		Long sourceWordId = secondWordHomonymNum >= firstWordHomonymNum ? secondWordId : firstWordId;

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinWords", sourceWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinWords", targetWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);

		SimpleWord sourceWord = lookupDbService.getSimpleWord(sourceWordId);
		logger.debug("Joining homonyms for value \"{}\"", sourceWord.getWordValue());
		compositionDbService.joinWordData(targetWordId, sourceWordId);
		joinWordStressAndMarkupData(targetWordId, sourceWordId);
		joinLexemeData(targetWordId, sourceWordId);
		joinParadigms(targetWordId, sourceWordId);
		cudDbService.deleteWord(sourceWord);
		updateWordLexemesPublicity(targetWordId, DATASET_EKI);

		activityLogService.createActivityLog(activityLog1, sourceWordId, ActivityEntity.WORD);
		activityLogService.createActivityLog(activityLog2, targetWordId, ActivityEntity.WORD);

		return targetWordId;
	}

	private void joinWordStressAndMarkupData(Long targetWordId, Long sourceWordId) {

		String sourceValuePrese = lookupDbService.getWordValuePrese(sourceWordId);
		String targetValuePrese = lookupDbService.getWordValuePrese(targetWordId);

		if (!StringUtils.equals(targetValuePrese, sourceValuePrese)) {
			boolean isTargetWordDecorated = textDecorationService.isDecorated(targetValuePrese);
			boolean isSourceWordDecorated = textDecorationService.isDecorated(sourceValuePrese);
			if (!isTargetWordDecorated && isSourceWordDecorated) {
				cudDbService.updateWordValuePrese(targetWordId, sourceValuePrese);
			}
		}
	}

	private void joinLexemeData(Long targetWordId, Long sourceWordId) {

		List<LexemeRecord> sourceLexemes = lookupDbService.getLexemeRecordsByWord(sourceWordId);

		if (CollectionUtils.isEmpty(sourceLexemes)) {
			return;
		}

		List<String> datasetCodes = sourceLexemes.stream().map(LexemeRecord::getDatasetCode).distinct().collect(Collectors.toList());

		for (LexemeRecord sourceLexeme : sourceLexemes) {

			Long sourceLexemeId = sourceLexeme.getId();
			Long sourceMeaningId = sourceLexeme.getMeaningId();
			String datasetCode = sourceLexeme.getDatasetCode();
			LexemeRecord targetLexeme = lookupDbService.getLexemeRecord(targetWordId, sourceMeaningId, datasetCode);
			if (targetLexeme == null) {
				cudDbService.updateLexemeWordId(sourceLexemeId, targetWordId);
			} else {
				Long targetLexemeId = targetLexeme.getId();
				compositionDbService.joinLexemes(targetLexemeId, sourceLexemeId);
			}
		}

		for (String datasetCode : datasetCodes) {

			recalculateAndUpdateAllLexemeLevels(sourceWordId, datasetCode);
			recalculateAndUpdateAllLexemeLevels(targetWordId, datasetCode);
		}
	}

	private void joinParadigms(Long targetWordId, Long sourceWordId) {

		boolean targetWordHasForms = lookupDbService.wordHasForms(targetWordId);
		if (targetWordHasForms) {
			return;
		}
		boolean sourceWordHasForms = lookupDbService.wordHasForms(sourceWordId);
		if (sourceWordHasForms) {
			compositionDbService.moveParadigms(targetWordId, sourceWordId);
		}
	}

	private void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<Lexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (Lexeme lexeme : lexemes) {
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2());
		}
	}

	private void recalculateAndUpdateAllLexemeLevels(Long wordId, String datasetCode) {

		List<Lexeme> lexemes = lookupDbService.getWordLexemeLevels(wordId, datasetCode);

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		Map<Integer, List<Lexeme>> lexemeLevel1Map = lexemes.stream().collect(Collectors.groupingBy(Lexeme::getLevel1));
		List<Integer> existingLevel1Values = new ArrayList<>(lexemeLevel1Map.keySet());
		Collections.sort(existingLevel1Values);
		int existingLevel1Count = existingLevel1Values.size();
		int recalcLevel1 = 0;

		for (int level1Index = 0; level1Index < existingLevel1Count; level1Index++) {

			int existingLevel1 = existingLevel1Values.get(level1Index);
			List<Lexeme> level2Lexemes = lexemeLevel1Map.get(existingLevel1);
			int existingLevel2Count = level2Lexemes.size();
			recalcLevel1++;
			int recalcLevel2 = 0;
			int level1InsertCount = 0;
			int recalcLevel1Adjust = 0;

			for (int level2Index = 0; level2Index < existingLevel2Count; level2Index++) {

				Lexeme level2Lexeme = level2Lexemes.get(level2Index);
				Long lexemeId = level2Lexeme.getLexemeId();
				int existingLevel2 = level2Lexeme.getLevel2();

				if ((level2Index > 0) && (existingLevel2 == 1)) {

					level1InsertCount++;
					recalcLevel1Adjust = recalcLevel1 + level1InsertCount;

					if (recalcLevel1Adjust != existingLevel1) {
						cudDbService.updateLexemeLevel1(lexemeId, recalcLevel1Adjust);
					}

				} else {

					recalcLevel2++;

					if (recalcLevel1 != existingLevel1) {
						cudDbService.updateLexemeLevel1(lexemeId, recalcLevel1);
					}
					if (recalcLevel2 != existingLevel2) {
						cudDbService.updateLexemeLevel2(lexemeId, recalcLevel2);
					}
				}
			}

			recalcLevel1 = recalcLevel1 + level1InsertCount;
		}
	}

	private void updateMeaningLexemesPublicity(Long targetMeaningId, String datasetCode) {

		boolean publicLexemeExists = lookupDbService.meaningPublicLexemeExists(targetMeaningId, datasetCode);
		if (publicLexemeExists) {
			cudDbService.updateMeaningLexemesPublicity(targetMeaningId, datasetCode, PUBLICITY_PUBLIC);
		}
	}

	private void updateWordLexemesPublicity(Long targetWordId, String datasetCode) {

		boolean publicLexemeExists = lookupDbService.wordPublicLexemeExists(targetWordId, datasetCode);
		if (publicLexemeExists) {
			cudDbService.updateWordLexemesPublicity(targetWordId, datasetCode, PUBLICITY_PUBLIC);
		}
	}

	private void joinMeaningsCommonWordsLexemes(Long targetMeaningId, Long sourceMeaningId) {

		List<IdPair> meaningsCommonWordsLexemeIdPairs = lookupDbService.getMeaningsCommonWordsLexemeIdPairs(targetMeaningId, sourceMeaningId);
		boolean meaningsShareCommonWord = CollectionUtils.isNotEmpty(meaningsCommonWordsLexemeIdPairs);
		if (meaningsShareCommonWord) {
			for (IdPair lexemeIdPair : meaningsCommonWordsLexemeIdPairs) {
				Long targetLexemeId = lexemeIdPair.getId1();
				Long sourceLexemeId = lexemeIdPair.getId2();
				updateLexemeLevels(sourceLexemeId, "delete");
				compositionDbService.joinLexemes(targetLexemeId, sourceLexemeId);
				tagDbService.createLexemeAutomaticTags(targetLexemeId);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void approveMeaning(Long meaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog(FUNCT_NAME_APPROVE_MEANING, meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
	}
}
