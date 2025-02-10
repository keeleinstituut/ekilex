package eki.ekilex.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.PermConstant;
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
public class CompositionService extends AbstractService implements PermConstant {

	private static final Logger logger = LoggerFactory.getLogger(CompositionService.class);

	private static final int DEFAULT_LEXEME_LEVEL = 1;

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

	@Transactional(rollbackOn = Exception.class)
	public Long cloneMeaningWithLexemes(Long sourceMeaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Map<Long, Long> sourceTargetLexemeIdMap = new HashMap<>();
		boolean publicDataOnly = false;
		Long targetMeaningId = cloneMeaningAndData(sourceMeaningId, publicDataOnly, roleDatasetCode, isManualEventOnUpdateEnabled);
		List<LexemeRecord> sourceLexemes = lookupDbService.getLexemeRecordsByMeaning(sourceMeaningId);
		for (LexemeRecord sourceLexeme : sourceLexemes) {
			Long sourceLexemeId = sourceLexeme.getId();
			Long targetLexemeId = cloneLexemeAndData(sourceLexemeId, targetMeaningId, null, publicDataOnly, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceTargetLexemeIdMap.put(sourceLexemeId, targetLexemeId);
		}
		cloneLexemeRelations(sourceTargetLexemeIdMap, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetMeaningId;
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean cloneLexemeMeaningAndLexemes(Long lexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Map<Long, Long> sourceTargetLexemeIdMap = new HashMap<>();
		boolean isPublicDataOnly = false;
		LexemeRecord lexeme = lookupDbService.getLexemeRecord(lexemeId);
		String datasetCode = lexeme.getDatasetCode();
		Long sourceMeaningId = lexeme.getMeaningId();
		Long targetMeaningId = cloneMeaningAndData(sourceMeaningId, isPublicDataOnly, roleDatasetCode, isManualEventOnUpdateEnabled);

		List<LexemeRecord> sourceLexemes = lookupDbService.getLexemeRecordsByMeaning(sourceMeaningId, datasetCode);
		for (LexemeRecord sourceLexeme : sourceLexemes) {
			Long sourceLexemeId = sourceLexeme.getId();
			Long targetLexemeId = cloneLexemeAndData(sourceLexemeId, targetMeaningId, null, isPublicDataOnly, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceTargetLexemeIdMap.put(sourceLexemeId, targetLexemeId);
		}
		cloneLexemeRelations(sourceTargetLexemeIdMap, roleDatasetCode, isManualEventOnUpdateEnabled);
		boolean success = MapUtils.isNotEmpty(sourceTargetLexemeIdMap);
		return success;
	}

	@Transactional(rollbackOn = Exception.class)
	public Long cloneEmptyLexemeAndMeaning(Long sourceLexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long targetMeaningId = cudDbService.createMeaning();
		activityLogService.createActivityLog("cloneEmptyLexemeAndMeaning", targetMeaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long targetLexemeId = compositionDbService.cloneEmptyLexeme(sourceLexemeId, targetMeaningId);
		updateLexemeLevelsAfterDuplication(targetLexemeId);
		activityLogService.createActivityLog("cloneEmptyLexemeAndMeaning", targetLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		return targetLexemeId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void cloneLexemeAndWord(Long sourceLexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord sourceLexeme = lookupDbService.getLexemeRecord(sourceLexemeId);
		Long sourceWordId = sourceLexeme.getWordId();
		Long targetWordId = cloneWordAndData(sourceWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
		cloneLexemeAndData(sourceLexemeId, null, targetWordId, false, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeWordId(Long lexemeId, Long wordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData lexemeActivityLog = activityLogService.prepareActivityLog("updateLexemeWordId", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeWordId = lookupDbService.getLexemeWordId(lexemeId);
		moveLexemeToAnotherWord(wordId, lexemeId);
		cudDbService.deleteFloatingWord(lexemeWordId);
		activityLogService.createActivityLog(lexemeActivityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean updateWordValuePrese(EkiUser user, Long wordId, String wordValuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		DatasetPermission userRole = user.getRecentRole();
		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, datasetCode, wordId);
		if (isWordCrudGrant) {
			ActivityLogData activityLog = activityLogService
					.prepareActivityLog("updateWordValuePrese", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordValuePrese(wordId, wordValuePrese);
			activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
		}
		return isWordCrudGrant;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeWordValue(Long lexemeId, String wordValuePrese, String wordLang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData lexemeActivityLog = activityLogService
				.prepareActivityLog("updateLexemeWordValue", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		String wordValue = textDecorationService.removeEkiElementMarkup(wordValuePrese);
		Long originalWordId = lookupDbService.getLexemeWordId(lexemeId);
		WordRecord originalWord = lookupDbService.getWordRecord(originalWordId);
		String originalWordValue = originalWord.getValue();
		String originalWordLang = originalWord.getLang();
		List<Long> originalWordDatasetCodes = lookupDbService.getWordDatasetCodes(originalWordId);
		boolean originalWordExistsOnlyInOneDataset = originalWordDatasetCodes.size() == 1;
		List<Word> existingSameValueWords = lookupDbService.getWords(wordValue, wordLang);

		if (existingSameValueWords.size() == 0) {
			if (originalWordExistsOnlyInOneDataset) {
				ActivityLogData wordActivityLog = activityLogService
						.prepareActivityLog("updateLexemeWordValue", originalWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
				updateWordValue(originalWordId, wordValue, wordValuePrese, wordLang, originalWordValue, originalWordLang);
				activityLogService.createActivityLog(wordActivityLog, originalWordId, ActivityEntity.WORD);
			} else {
				Long duplicateWordId = cloneWordAndData(originalWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
				ActivityLogData wordActivityLog = activityLogService
						.prepareActivityLog("updateLexemeWordValue", duplicateWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
				updateWordValue(duplicateWordId, wordValue, wordValuePrese, wordLang, originalWordValue, originalWordLang);
				moveLexemeToAnotherWord(duplicateWordId, lexemeId);
				activityLogService.createActivityLog(wordActivityLog, duplicateWordId, ActivityEntity.WORD);
			}
		}

		if (existingSameValueWords.size() == 1) {
			Long existingWordId = existingSameValueWords.get(0).getWordId();
			moveLexemeToAnotherWord(existingWordId, lexemeId);
		}

		if (existingSameValueWords.size() > 1) {
			throw new UnsupportedOperationException();
		}

		cudDbService.deleteFloatingWord(originalWordId);
		activityLogService.createActivityLog(lexemeActivityLog, lexemeId, ActivityEntity.LEXEME);
	}

	private void updateWordValue(Long wordId, String wordValue, String wordValuePrese, String wordLang, String originalWordValue, String originalWordLang) {

		String cleanValue = textDecorationService.unifyToApostrophe(wordValue);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(wordValue, cleanValue)) {
			valueAsWord = cleanValue;
		}
		SimpleWord originalSimpleWord = new SimpleWord(null, originalWordValue, originalWordLang);
		SimpleWord updatedSimpleWord = new SimpleWord(null, wordValue, wordLang);

		cudDbService.updateWordValueAndLang(wordId, wordValue, wordValuePrese, wordLang);
		if (StringUtils.isNotEmpty(valueAsWord)) {
			cudDbService.updateAsWordValue(wordId, valueAsWord);
		}

		cudDbService.adjustWordHomonymNrs(originalSimpleWord);
		cudDbService.adjustWordHomonymNrs(updatedSimpleWord);
	}

	private Long cloneLexemeAndData(Long sourceLexemeId, Long targetMeaningId, Long targetWordId, boolean isPublicDataOnly, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long targetLexemeId = compositionDbService.cloneLexeme(sourceLexemeId, targetMeaningId, targetWordId);
		updateLexemeLevelsAfterDuplication(targetLexemeId);

		compositionDbService.cloneLexemeUsages(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeNotes(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeTags(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeDerivs(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeRegions(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemePoses(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeRegisters(sourceLexemeId, targetLexemeId);
		compositionDbService.cloneLexemeFreeforms(sourceLexemeId, targetLexemeId, isPublicDataOnly);
		compositionDbService.cloneLexemeSoureLinks(sourceLexemeId, targetLexemeId);
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
		compositionDbService.cloneWordRelations(sourceWordId, targetWordId);
		compositionDbService.cloneWordGroupMembers(sourceWordId, targetWordId);
		compositionDbService.cloneWordFreeforms(sourceWordId, targetWordId);
		compositionDbService.cloneWordEtymology(sourceWordId, targetWordId);

		activityLogService.createActivityLog("cloneWordAndData", targetWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);

		return targetWordId;
	}

	private Long cloneMeaningAndData(Long sourceMeaningId, boolean isPublicDataOnly, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long targetMeaningId = compositionDbService.cloneMeaning(sourceMeaningId);
		compositionDbService.cloneMeaningSemanticType(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningTags(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningForums(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningDomains(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningNotes(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningImages(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningRelations(sourceMeaningId, targetMeaningId);
		compositionDbService.cloneMeaningFreeforms(sourceMeaningId, targetMeaningId, isPublicDataOnly);
		compositionDbService.cloneMeaningDefinitions(sourceMeaningId, targetMeaningId, isPublicDataOnly);

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

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void joinLexemes(Long targetLexemeId, List<Long> sourceLexemeIds, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceLexemeId : sourceLexemeIds) {
			joinLexemes(targetLexemeId, sourceLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	private void joinLexemes(Long targetLexemeId, Long sourceLexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord targetLexeme = lookupDbService.getLexemeRecord(targetLexemeId);
		LexemeRecord sourceLexeme = lookupDbService.getLexemeRecord(sourceLexemeId);
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

	@Transactional(rollbackOn = Exception.class)
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

		List<LexemeRecord> sourceWordLexemes = lookupDbService.getLexemeRecordsByWord(sourceWordId);

		for (LexemeRecord sourceWordLexeme : sourceWordLexemes) {

			Long sourceWordLexemeId = sourceWordLexeme.getId();
			Long sourceWordLexemeMeaningId = sourceWordLexeme.getMeaningId();
			String sourceWordLexemeDatasetCode = sourceWordLexeme.getDatasetCode();
			LexemeRecord targetWordLexeme = lookupDbService.getLexemeRecord(targetWordId, sourceWordLexemeMeaningId, sourceWordLexemeDatasetCode);
			boolean targetLexemeExists = targetWordLexeme != null;
			if (targetLexemeExists) {
				Long targetWordLexemeId = targetWordLexeme.getId();
				compositionDbService.joinLexemes(targetWordLexemeId, sourceWordLexemeId);
			} else {
				moveLexemeToAnotherWord(targetWordId, sourceWordLexemeId);
			}
		}
	}

	private void moveLexemeToAnotherWord(Long targetWordId, Long sourceWordLexemeId) {

		String datasetCode = lookupDbService.getLexemeDatasetCode(sourceWordLexemeId);
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

	private void updateLexemeLevelsAfterDuplication(Long duplicateLexemeId) {

		LexemeRecord duplicatedLexeme = lookupDbService.getLexemeRecord(duplicateLexemeId);
		Integer level1 = duplicatedLexeme.getLevel1();
		Integer level2 = duplicatedLexeme.getLevel2();
		Long wordId = duplicatedLexeme.getWordId();
		String datasetCode = duplicatedLexeme.getDatasetCode();

		Integer level2MinValue = lookupDbService.getLexemeLevel2MinimumValue(wordId, datasetCode, level1);
		boolean isLevel1Increase = Objects.equals(level2, level2MinValue);

		if (isLevel1Increase) {
			List<LexemeRecord> lexemesWithLargerLevel1 = lookupDbService.getLexemeRecordsWithHigherLevel1(wordId, datasetCode, level1);
			int increasedDuplicatedLexemeLevel1 = level1 + 1;
			cudDbService.updateLexemeLevel1(duplicateLexemeId, increasedDuplicatedLexemeLevel1);
			for (LexemeRecord lexeme : lexemesWithLargerLevel1) {
				Long lexemeId = lexeme.getId();
				int increasedLevel1 = lexeme.getLevel1() + 1;
				cudDbService.updateLexemeLevel1(lexemeId, increasedLevel1);
			}
		} else {
			List<LexemeRecord> lexemesWithLargerLevel2 = lookupDbService.getLexemeRecordsWithHigherLevel2(wordId, datasetCode, level1, level2);
			int increasedDuplicatedLexemeLevel2 = level2 + 1;
			cudDbService.updateLexemeLevel2(duplicateLexemeId, increasedDuplicatedLexemeLevel2);
			for (LexemeRecord lexeme : lexemesWithLargerLevel2) {
				Long lexemeId = lexeme.getId();
				int increasedLevel2 = lexeme.getLevel2() + 1;
				cudDbService.updateLexemeLevel2(lexemeId, increasedLevel2);
			}
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

	@Transactional(rollbackOn = Exception.class)
	public void approveMeaning(Long meaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog(FUNCT_NAME_APPROVE_MEANING, meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
	}
}
