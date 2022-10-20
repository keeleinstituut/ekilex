package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.common.constant.RelationStatus;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordRelation;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.SynSearchDbService;

@Component
public class SynCudService extends AbstractCudService implements GlobalConstant, SystemConstant {

	private static final String USER_ADDED_WORD_RELATION_NAME = "user";

	private static final float DEFAULT_MEANING_RELATION_WEIGHT = 1;

	@Value("#{${relation.weight.multipliers}}")
	private Map<String, Float> relationWeightMultiplierMap;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private CompositionDbService compositionDbService;

	@Transactional
	public void createSynMeaningRelation(Long targetMeaningId, Long sourceMeaningId, Long wordRelationId, boolean isManualEventOnUpdateEnabled) throws Exception {

		List<TypeWordRelParam> typeWordRelParams = synSearchDbService.getWordRelationParams(wordRelationId);
		Float meaningRelationWeight = getCalculatedMeaningRelationWeight(typeWordRelParams);
		createSynMeaningRelation(targetMeaningId, sourceMeaningId, meaningRelationWeight, isManualEventOnUpdateEnabled);

		updateWordRelationStatus("createSynMeaningRelation", wordRelationId, RelationStatus.PROCESSED.name(), isManualEventOnUpdateEnabled);
	}

	private Float getCalculatedMeaningRelationWeight(List<TypeWordRelParam> typeWordRelParams) {

		if (typeWordRelParams.isEmpty()) {
			return DEFAULT_MEANING_RELATION_WEIGHT;
		}

		float dividend = 0;
		float divisor = 0;

		for (TypeWordRelParam typeWordRelParam : typeWordRelParams) {
			String relationParamName = typeWordRelParam.getName();
			Float relationParamValue = typeWordRelParam.getValue();
			Float relationParamWeightMultiplier = relationWeightMultiplierMap.get(relationParamName);

			if (relationParamWeightMultiplier == null) {
				throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Unknown relation weight name: " + relationParamName);
			}

			dividend += (relationParamValue * relationParamWeightMultiplier);
			divisor += relationParamWeightMultiplier;
		}

		float relationWeight = dividend / divisor;
		return relationWeight;
	}

	@Transactional
	public void createSynMeaningRelation(Long targetMeaningId, Long sourceMeaningId, String weightStr, boolean isManualEventOnUpdateEnabled) throws Exception {

		Float meaningRelationWeight = NumberUtils.toFloat(weightStr);
		createSynMeaningRelation(targetMeaningId, sourceMeaningId, meaningRelationWeight, isManualEventOnUpdateEnabled);
	}

	private void createSynMeaningRelation(Long targetMeaningId, Long sourceMeaningId, Float meaningRelationWeight, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		Long meaningRelationId;
		activityLog = activityLogService.prepareActivityLog("createSynMeaningRelation", targetMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		meaningRelationId = cudDbService.createMeaningRelation(targetMeaningId, sourceMeaningId, MEANING_REL_TYPE_CODE_SIMILAR, meaningRelationWeight);
		activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);

		boolean oppositeRelationExists = lookupDbService.meaningRelationExists(sourceMeaningId, targetMeaningId, MEANING_REL_TYPE_CODE_SIMILAR);
		if (oppositeRelationExists) {
			return;
		}

		activityLog = activityLogService.prepareActivityLog("createSynMeaningRelation", sourceMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		meaningRelationId = cudDbService.createMeaningRelation(sourceMeaningId, targetMeaningId, MEANING_REL_TYPE_CODE_SIMILAR, meaningRelationWeight);
		activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional
	public void createSynMeaningWord(Long targetMeaningId, Long synWordId, Long wordRelationId, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSynMeaningWord", targetMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long sourceWordId = synSearchDbService.getSynCandidateWordId(wordRelationId);
		SimpleWord sourceWord = synSearchDbService.getSimpleWord(sourceWordId);
		String sourceWordValue = sourceWord.getWordValue();
		String sourceWordLang = sourceWord.getLang();

		updateWordRelationStatus("createSynMeaningWord", wordRelationId, RelationStatus.PROCESSED.name(), isManualEventOnUpdateEnabled);

		List<LexemeRecord> sourceWordLexemes = compositionDbService.getWordLexemes(sourceWordId);
		if (sourceWordLexemes.size() != 1) {
			throw new OperationDeniedException();
		}
		LexemeRecord sourceWordLexeme = sourceWordLexemes.get(0);
		Long sourceLexemeId = sourceWordLexeme.getId();
		Long sourceMeaningId = sourceWordLexeme.getMeaningId();

		Long targetMeaningSameWordLexemeId = synSearchDbService.getMeaningFirstWordLexemeId(targetMeaningId, datasetCode, sourceWordValue, sourceWordLang);
		boolean targetMeaningHasWord = targetMeaningSameWordLexemeId != null;
		if (targetMeaningHasWord) {
			synSearchDbService.cloneSynLexemeData(targetMeaningSameWordLexemeId, sourceLexemeId);
			synSearchDbService.cloneSynMeaningData(targetMeaningId, sourceMeaningId, datasetCode);
			activityLogService.createActivityLog(activityLog, targetMeaningId, ActivityEntity.MEANING_WORD);
			return;
		}

		List<Word> sameValueWords = lookupDbService.getWords(sourceWordValue, sourceWordLang);
		if (sameValueWords.size() == 0) {
			synWordId = synSearchDbService.createSynWord(sourceWordId);
		} else if (sameValueWords.size() == 1) {
			synWordId = sameValueWords.get(0).getWordId();
		} else {
			if (synWordId == null) {
				throw new OperationDeniedException();
			}
		}

		BigDecimal weight = synSearchDbService.getWordRelationParamValue(wordRelationId, WORD_RELATION_PARAM_NAME_SYN_CANDIDATE);
		int currentSynWordLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(synWordId, datasetCode);
		int synLexemeLevel1 = currentSynWordLexemesMaxLevel1 + 1;
		synSearchDbService.createSynLexeme(sourceLexemeId, synWordId, synLexemeLevel1, targetMeaningId, datasetCode, weight);
		synSearchDbService.cloneSynMeaningData(targetMeaningId, sourceMeaningId, datasetCode);

		activityLogService.createActivityLog(activityLog, targetMeaningId, ActivityEntity.MEANING_WORD);
	}

	@Transactional
	public void createWordAndSynRelation(
			Long existingWordId, String valuePrese, String datasetCode, String language, String weightStr, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createWordAndLexemeAndMeaning(value, valuePrese, valueAsWord, value, language, datasetCode, PUBLICITY_PRIVATE, null);
		Long createdWordId = wordLexemeMeaningId.getWordId();
		Long createdLexemeId = wordLexemeMeaningId.getLexemeId();
		tagDbService.createLexemeAutomaticTags(createdLexemeId);

		activityLogService.createActivityLog("createWordAndSynRelation", createdWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordAndSynRelation", existingWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long createdRelationId = cudDbService.createWordRelation(existingWordId, createdWordId, WORD_REL_TYPE_CODE_RAW, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(existingWordId, createdRelationId, WORD_REL_TYPE_CODE_RAW);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void createSynWordRelation(Long targetWordId, Long sourceWordId, String weightStr, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean word2DatasetLexemeExists = lookupDbService.wordLexemeExists(sourceWordId, datasetCode);
		if (!word2DatasetLexemeExists) {
			createLexeme(sourceWordId, datasetCode, null, isManualEventOnUpdateEnabled);
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSynWordRelation", targetWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long createdRelationId = cudDbService.createWordRelation(targetWordId, sourceWordId, WORD_REL_TYPE_CODE_RAW, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(targetWordId, createdRelationId, WORD_REL_TYPE_CODE_RAW);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void updateRelationStatus(Long relationId, String relationStatus, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.equals(RelationStatus.DELETED.name(), relationStatus)) {
			moveChangedRelationToLast(relationId);
		}
		updateWordRelationStatus("updateRelationStatus", relationId, relationStatus, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateWordSynRelationsStatusDeleted(
			Long wordId, String datasetCode, String synCandidateLangCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		List<String> synCandidateLangCodes = new ArrayList<>(Collections.singletonList(synCandidateLangCode));
		List<SynRelation> wordSynRelations = synSearchDbService.getWordPartSynRelations(wordId, WORD_REL_TYPE_CODE_RAW, datasetCode, synCandidateLangCodes);
		List<SynRelation> filteredWordSynRelations = wordSynRelations.stream()
				.filter(synRelation -> synRelation.getRelationStatus() == null || synRelation.getRelationStatus().equals(RelationStatus.UNDEFINED))
				.collect(Collectors.toList());

		for (SynRelation synRelation : filteredWordSynRelations) {
			Long relationId = synRelation.getId();
			updateRelationStatus(relationId, RelationStatus.DELETED.name(), isManualEventOnUpdateEnabled);
		}
	}

	private void updateWordRelationStatus(String functName, Long wordRelationId, String relationStatus, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long relationWordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, relationWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		synSearchDbService.updateRelationStatus(wordRelationId, relationStatus);
		activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
	}

	private void moveCreatedWordRelationToFirst(Long wordId, Long relationId, String relTypeCode) {

		List<WordRelation> existingRelations = lookupDbService.getWordRelations(wordId, relTypeCode);
		if (existingRelations.size() > 1) {

			WordRelation firstRelation = existingRelations.get(0);
			List<Long> existingOrderByValues = existingRelations.stream().map(WordRelation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, firstRelation.getOrderBy());
			existingRelations.remove(existingRelations.size() - 1);
			existingOrderByValues.remove(0);

			int relIdx = 0;
			for (WordRelation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}

	private void moveChangedRelationToLast(Long relationId) {
		List<WordRelation> existingRelations = synSearchDbService.getExistingFollowingRelationsForWord(relationId, WORD_REL_TYPE_CODE_RAW);

		if (existingRelations.size() > 1) {
			WordRelation lastRelation = existingRelations.get(existingRelations.size() - 1);
			List<Long> existingOrderByValues = existingRelations.stream().map(WordRelation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, lastRelation.getOrderBy());
			existingRelations.remove(0);

			existingOrderByValues.remove(existingOrderByValues.size() - 1);

			int relIdx = 0;
			for (WordRelation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}
}
