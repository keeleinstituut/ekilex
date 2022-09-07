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
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.records.LexRelationRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.data.db.tables.records.WordRecord;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.TagDbService;
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
	private TagDbService tagDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private TextDecorationService textDecorationService;

	@Transactional
	public Optional<Long> optionalDuplicateMeaningWithLexemes(Long meaningId, boolean isManualEventOnUpdateEnabled) throws Exception {
		return Optional.of(duplicateMeaningWithLexemes(meaningId, isManualEventOnUpdateEnabled));
	}

	@Transactional
	public List<Long> duplicateLexemeAndMeaningWithSameDatasetLexemes(Long lexemeId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Map<Long, Long> lexemeIdAndDuplicateLexemeIdMap = new HashMap<>();
		boolean publicDataOnly = false;
		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		String datasetCode = lexeme.getDatasetCode();
		Long meaningId = lexeme.getMeaningId();
		Long duplicateMeaningId = duplicateMeaningData(meaningId, publicDataOnly, isManualEventOnUpdateEnabled);

		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId, datasetCode);
		for (LexemeRecord meaningLexeme : meaningLexemes) {
			Long meaningLexemeId = meaningLexeme.getId();
			Long duplicateLexemeId = duplicateLexemeData(meaningLexemeId, duplicateMeaningId, null, publicDataOnly, isManualEventOnUpdateEnabled);
			lexemeIdAndDuplicateLexemeIdMap.put(meaningLexemeId, duplicateLexemeId);
		}
		duplicateLexemeRelations(lexemeIdAndDuplicateLexemeIdMap, isManualEventOnUpdateEnabled);
		List<Long> duplicateLexemeIds = new ArrayList<>(lexemeIdAndDuplicateLexemeIdMap.values());
		return duplicateLexemeIds;
	}

	@Transactional
	public Long duplicateEmptyLexemeAndMeaning(Long lexemeId, boolean isManualEventOnUpdateEnabled) throws Exception {
		Long duplicateMeaningId = cudDbService.createMeaning();
		activityLogService.createActivityLog("duplicateEmptyLexemeAndMeaning", duplicateMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long duplicateLexemeId = compositionDbService.cloneEmptyLexeme(lexemeId, duplicateMeaningId);
		updateLexemeLevelsAfterDuplication(duplicateLexemeId);
		activityLogService.createActivityLog("duplicateEmptyLexemeAndMeaning", duplicateLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		return duplicateLexemeId;
	}

	@Transactional
	public void duplicateLexemeAndWord(Long lexemeId, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		Long wordId = lexeme.getWordId();
		Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
		duplicateLexemeData(lexemeId, null, duplicateWordId, false, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeWordId(Long lexemeId, Long wordId, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData lexemeActivityLog = activityLogService
				.prepareActivityLog("updateLexemeWordId", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeWordId = lookupDbService.getLexemeWordId(lexemeId);
		connectLexemeToAnotherWord(wordId, lexemeId);
		cudDbService.deleteFloatingWord(lexemeWordId);
		activityLogService.createActivityLog(lexemeActivityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeWordValue(Long lexemeId, String wordValuePrese, String wordLang, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData lexemeActivityLog = activityLogService
				.prepareActivityLog("updateLexemeWordValue", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		String wordValue = textDecorationService.removeEkiElementMarkup(wordValuePrese);
		Long originalWordId = lookupDbService.getLexemeWordId(lexemeId);
		WordRecord originalWord = compositionDbService.getWord(originalWordId);
		String originalWordValue = originalWord.getValue();
		String originalWordLang = originalWord.getLang();
		List<Long> originalWordDatasetCodes = lookupDbService.getWordDatasetCodes(originalWordId);
		boolean originalWordExistsOnlyInOneDataset = originalWordDatasetCodes.size() == 1;
		List<Word> existingSameValueWords = lookupDbService.getWords(wordValue, wordLang);

		if (existingSameValueWords.size() == 0) {
			if (originalWordExistsOnlyInOneDataset) {
				ActivityLogData wordActivityLog = activityLogService
						.prepareActivityLog("updateLexemeWordValue", originalWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
				updateWordValue(originalWordId, wordValue, wordValuePrese, wordLang, originalWordValue, originalWordLang);
				activityLogService.createActivityLog(wordActivityLog, originalWordId, ActivityEntity.WORD);
			} else {
				Long duplicateWordId = duplicateWordData(originalWordId, isManualEventOnUpdateEnabled);
				ActivityLogData wordActivityLog = activityLogService
						.prepareActivityLog("updateLexemeWordValue", duplicateWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
				updateWordValue(duplicateWordId, wordValue, wordValuePrese, wordLang, originalWordValue, originalWordLang);
				connectLexemeToAnotherWord(duplicateWordId, lexemeId);
				activityLogService.createActivityLog(wordActivityLog, duplicateWordId, ActivityEntity.WORD);
			}
		}

		if (existingSameValueWords.size() == 1) {
			Long existingWordId = existingSameValueWords.get(0).getWordId();
			connectLexemeToAnotherWord(existingWordId, lexemeId);
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

	private Long duplicateMeaningWithLexemes(Long meaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Map<Long, Long> lexemeIdAndDuplicateLexemeIdMap = new HashMap<>();
		boolean publicDataOnly = false;
		Long duplicateMeaningId = duplicateMeaningData(meaningId, publicDataOnly, isManualEventOnUpdateEnabled);
		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId);
		for (LexemeRecord meaningLexeme : meaningLexemes) {
			Long lexemeId = meaningLexeme.getId();
			Long duplicateLexemeId = duplicateLexemeData(lexemeId, duplicateMeaningId, null, publicDataOnly, isManualEventOnUpdateEnabled);
			lexemeIdAndDuplicateLexemeIdMap.put(lexemeId, duplicateLexemeId);
		}
		duplicateLexemeRelations(lexemeIdAndDuplicateLexemeIdMap, isManualEventOnUpdateEnabled);

		return duplicateMeaningId;
	}

	private Long duplicateLexemeData(Long lexemeId, Long meaningId, Long wordId, boolean publicDataOnly, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long duplicateLexemeId = compositionDbService.cloneLexeme(lexemeId, meaningId, wordId);
		updateLexemeLevelsAfterDuplication(duplicateLexemeId);
		compositionDbService.cloneLexemeDerivs(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeFreeforms(lexemeId, duplicateLexemeId, publicDataOnly);
		compositionDbService.cloneLexemePoses(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeRegisters(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeSoureLinks(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeCollocations(lexemeId, duplicateLexemeId);
		activityLogService.createActivityLog("duplicateLexemeData", duplicateLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);

		return duplicateLexemeId;
	}

	private Long duplicateWordData(Long wordId, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord simpleWord = compositionDbService.getSimpleWord(wordId);
		Long duplicateWordId = compositionDbService.cloneWord(simpleWord);
		compositionDbService.cloneWordParadigmsAndForms(wordId, duplicateWordId);
		compositionDbService.cloneWordTypes(wordId, duplicateWordId);
		compositionDbService.cloneWordRelations(wordId, duplicateWordId);
		compositionDbService.cloneWordGroupMembers(wordId, duplicateWordId);
		compositionDbService.cloneWordFreeforms(wordId, duplicateWordId);
		compositionDbService.cloneWordEtymology(wordId, duplicateWordId);
		activityLogService.createActivityLog("duplicateWordData", duplicateWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);

		return duplicateWordId;
	}

	private Long duplicateMeaningData(Long meaningId, boolean publicDataOnly, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long duplicateMeaningId = compositionDbService.cloneMeaning(meaningId);
		compositionDbService.cloneMeaningDomains(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningRelations(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningFreeforms(meaningId, duplicateMeaningId, publicDataOnly);
		duplicateMeaningDefinitions(meaningId, duplicateMeaningId, publicDataOnly);
		activityLogService.createActivityLog("duplicateMeaningData", duplicateMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);

		return duplicateMeaningId;
	}

	private void duplicateMeaningDefinitions(Long meaningId, Long duplicateMeaningId, boolean publicDataOnly) {

		List<Long> meaningDefinitionIds = compositionDbService.getMeaningDefinitionIds(meaningId, publicDataOnly);
		for (Long meaningDefinitionId : meaningDefinitionIds) {
			Long duplicateDefinintionId = compositionDbService.cloneMeaningDefinition(meaningDefinitionId, duplicateMeaningId);
			compositionDbService.cloneDefinitionFreeforms(meaningDefinitionId, duplicateDefinintionId);
			compositionDbService.cloneDefinitionDatasets(meaningDefinitionId, duplicateDefinintionId);
			compositionDbService.cloneDefinitionSourceLinks(meaningDefinitionId, duplicateDefinintionId);
		}
	}

	private void duplicateLexemeRelations(Map<Long, Long> existingLexemeIdAndDuplicateLexemeIdMap, boolean isManualEventOnUpdateEnabled) throws Exception {

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

				activityLog = activityLogService.prepareActivityLog("duplicateLexemeRelations", duplicateLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
				if (existingLexeme1Id.equals(existingLexemeId)) {
					if (existingLexemeIdAndDuplicateLexemeIdMap.containsKey(existingLexeme2Id)) {
						Long duplicateLexeme2Id = existingLexemeIdAndDuplicateLexemeIdMap.get(existingLexeme2Id);
						lexemeRelationId = cudDbService.createLexemeRelation(duplicateLexemeId, duplicateLexeme2Id, lexRelTypeCode);
					} else {
						lexemeRelationId = cudDbService.createLexemeRelation(duplicateLexemeId, existingLexeme2Id, lexRelTypeCode);
					}
				} else {
					if (existingLexemeIdAndDuplicateLexemeIdMap.containsKey(existingLexeme1Id)) {
						Long duplicateLexeme1Id = existingLexemeIdAndDuplicateLexemeIdMap.get(existingLexeme1Id);
						lexemeRelationId = cudDbService.createLexemeRelation(duplicateLexeme1Id, duplicateLexemeId, lexRelTypeCode);
					} else {
						lexemeRelationId = cudDbService.createLexemeRelation(existingLexeme1Id, duplicateLexemeId, lexRelTypeCode);
					}
				}
				if (lexemeRelationId != null) {
					activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
				}
			}
		}
	}

	@Transactional
	public void joinMeanings(Long targetMeaningId, List<Long> sourceMeaningIds, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceMeaningId : sourceMeaningIds) {
			joinMeanings(targetMeaningId, sourceMeaningId, isManualEventOnUpdateEnabled);
		}
	}

	private void joinMeanings(Long targetMeaningId, Long sourceMeaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinMeanings", sourceMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinMeanings", targetMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);

		activityLogService.joinApproveMeaning(targetMeaningId, sourceMeaningId);
		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
		updateMeaningLexemesPublicity(targetMeaningId);

		activityLogService.createActivityLog(activityLog1, sourceMeaningId, ActivityEntity.MEANING);
		activityLogService.createActivityLog(activityLog2, targetMeaningId, ActivityEntity.MEANING);
	}

	@Transactional
	public void joinLexemes(Long targetLexemeId, List<Long> sourceLexemeIds, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceLexemeId: sourceLexemeIds) {
			joinLexemes(targetLexemeId, sourceLexemeId, isManualEventOnUpdateEnabled);
		}
	}

	private void joinLexemes(Long targetLexemeId, Long sourceLexemeId, boolean isManualEventOnUpdateEnabled) throws Exception {

		LexemeRecord targetLexeme = compositionDbService.getLexeme(targetLexemeId);
		LexemeRecord sourceLexeme = compositionDbService.getLexeme(sourceLexemeId);
		if (sourceLexeme == null) {
			return;
		}
		Long targetMeaningId = targetLexeme.getMeaningId();
		Long sourceMeaningId = sourceLexeme.getMeaningId();

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinLexemes", sourceLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinLexemes", targetLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);

		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
		updateMeaningLexemesPublicity(targetMeaningId);

		activityLogService.createActivityLog(activityLog1, sourceLexemeId, ActivityEntity.LEXEME);
		activityLogService.createActivityLog(activityLog2, targetLexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public Long joinWords(Long targetWordId, List<Long> sourceWordIds, boolean isManualEventOnUpdateEnabled) throws Exception {
		for (Long sourceWordId : sourceWordIds) {
			targetWordId = joinWords(targetWordId, sourceWordId, isManualEventOnUpdateEnabled);
		}
		return targetWordId;
	}

	public Long joinWords(Long firstWordId, Long secondWordId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Integer firstWordHomonymNum = compositionDbService.getWordHomonymNum(firstWordId);
		Integer secondWordHomonymNum = compositionDbService.getWordHomonymNum(secondWordId);
		Long targetWordId = firstWordHomonymNum <= secondWordHomonymNum ? firstWordId : secondWordId;
		Long sourceWordId = secondWordHomonymNum >= firstWordHomonymNum ? secondWordId : firstWordId;

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinWords", sourceWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinWords", targetWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);

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

		List<LexemeRecord> sourceWordLexemes = compositionDbService.getWordLexemes(sourceWordId);
		for (LexemeRecord sourceWordLexeme : sourceWordLexemes) {
			Long sourceWordLexemeId = sourceWordLexeme.getId();
			Long sourceWordLexemeMeaningId = sourceWordLexeme.getMeaningId();
			String sourceWordLexemeDatasetCode = sourceWordLexeme.getDatasetCode();
			LexemeRecord targetWordLexeme = compositionDbService.getLexeme(targetWordId, sourceWordLexemeMeaningId, sourceWordLexemeDatasetCode);
			boolean targetLexemeExists = targetWordLexeme != null;
			if (targetLexemeExists) {
				Long targetWordLexemeId = targetWordLexeme.getId();
				compositionDbService.joinLexemes(targetWordLexemeId, sourceWordLexemeId);
			} else {
				connectLexemeToAnotherWord(targetWordId, sourceWordLexemeId);
			}
		}
	}

	private void connectLexemeToAnotherWord(Long targetWordId, Long sourceWordLexemeId) {

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
			compositionDbService.joinParadigms(targetWordId, sourceWordId);
		}
	}

	private void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2());
		}
	}

	private void updateLexemeLevelsAfterDuplication(Long duplicateLexemeId) {

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

	private void joinMeaningsCommonWordsLexemes(Long targetMeaningId, Long sourceMeaningId) {

		List<IdPair> meaningsCommonWordsLexemeIdPairs = compositionDbService.getMeaningsCommonWordsLexemeIdPairs(targetMeaningId, sourceMeaningId);
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

	@Transactional
	public void approveMeaning(Long meaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog(FUNCT_NAME_APPROVE_MEANING, meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
	}
}
