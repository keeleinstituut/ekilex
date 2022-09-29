package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.SynCandidacy;
import eki.ekilex.data.api.SynCandidateWord;
import eki.ekilex.data.api.TextWithSource;
import eki.ekilex.service.db.LookupDbService;

@Component
public class SynCandidateService extends AbstractCudService implements GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(SynCandidateService.class);

	private static final String WORD_RELATION_PARAM_NAME_SYN_CANDIDATE = "syn candidate";

	@Autowired
	private LookupDbService lookupDbService;

	@Transactional
	public void createFullSynCandidacy(SynCandidacy synCandidacy) throws Exception {

		String headwordValue = synCandidacy.getHeadwordValue();
		String headwordLang = synCandidacy.getHeadwordLang();
		String synCandidateDatasetCode = synCandidacy.getSynCandidateDatasetCode();
		List<SynCandidateWord> synCandidateWords = synCandidacy.getSynCandidateWords();

		logger.info("Creating syn {} candidates for \"{}\" ({}) in \"{}\"", synCandidateWords.size(), headwordValue, headwordLang, synCandidateDatasetCode);

		List<Word> existingHeadwords = lookupDbService.getWords(headwordValue, headwordLang);

		for (SynCandidateWord synCandidateWord : synCandidateWords) {

			BigDecimal synLexemeWeight = synCandidateWord.getWeight();

			WordLexemeMeaningIdTuple synCandidateWordLexemeMeaningId = handleSynCandidateWord(synCandidateDatasetCode, synCandidateWord);

			for (Word headword : existingHeadwords) {
				Long headwordId = headword.getWordId();
				Long synCandidateWordId = synCandidateWordLexemeMeaningId.getWordId();
				createSynCandidateWordRelation(headwordId, synCandidateWordId, synLexemeWeight);
			}
		}
	}

	private WordLexemeMeaningIdTuple handleSynCandidateWord(String synCandidateDatasetCode, SynCandidateWord synCandidateWord) throws Exception {

		String synCandidateWordValue = synCandidateWord.getValue();
		String synCandidateWordLang = synCandidateWord.getLang();
		List<String> synPosCodes = synCandidateWord.getPosCodes();
		List<TextWithSource> synDefinitions = synCandidateWord.getDefinitions();
		List<TextWithSource> synUsages = synCandidateWord.getUsages();

		WordLexemeMeaningIdTuple synCandidateWordLexemeMeaningId = createSynCandidateWordAndLexemeAndMeaning(synCandidateWordValue, synCandidateWordLang, synCandidateDatasetCode);

		Long synCandidateLexemeId = synCandidateWordLexemeMeaningId.getLexemeId();
		Long synCandidateMeaningId = synCandidateWordLexemeMeaningId.getMeaningId();

		if (CollectionUtils.isNotEmpty(synPosCodes)) {
			for (String posCode : synPosCodes) {
				createLexemePos(synCandidateLexemeId, posCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
			}
		}
		if (CollectionUtils.isNotEmpty(synUsages)) {
			for (TextWithSource usage : synUsages) {
				createUsage(synCandidateLexemeId, usage.getValue(), synCandidateWordLang,
						Complexity.DETAIL, PUBLICITY_PRIVATE, MANUAL_EVENT_ON_UPDATE_DISABLED);
			}
		}
		if (CollectionUtils.isNotEmpty(synDefinitions)) {
			for (TextWithSource definition : synDefinitions) {
				createDefinition(synCandidateMeaningId, definition.getValue(), synCandidateWordLang, synCandidateDatasetCode,
						Complexity.DETAIL, DEFINITION_TYPE_UNDEFINED, PUBLICITY_PRIVATE, MANUAL_EVENT_ON_UPDATE_DISABLED);
			}
		}

		return synCandidateWordLexemeMeaningId;
	}

	private WordLexemeMeaningIdTuple createSynCandidateWordAndLexemeAndMeaning(String value, String lang, String datasetCode) throws Exception {

		value = textDecorationService.removeEkiElementMarkup(value);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createPrivateWordAndLexemeAndMeaning(value, value, valueAsWord, value, lang, datasetCode);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		boolean isManualEventOnUpdateEnabled = MANUAL_EVENT_ON_UPDATE_DISABLED;
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createSynCandidateWordAndLexemeAndMeaning", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createSynCandidateWordAndLexemeAndMeaning", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createSynCandidateWordAndLexemeAndMeaning", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);

		return wordLexemeMeaningId;
	}

	private void createSynCandidateWordRelation(Long headwordId, Long synCandidateWordId, BigDecimal weight) throws Exception {

		boolean isManualEventOnUpdateEnabled = MANUAL_EVENT_ON_UPDATE_DISABLED;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSynCandidateWordRelation", headwordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long createdRelationId = cudDbService.createWordRelation(headwordId, synCandidateWordId, WORD_REL_TYPE_CODE_RAW, UNDEFINED_RELATION_STATUS);
		cudDbService.createWordRelationParam(createdRelationId, WORD_RELATION_PARAM_NAME_SYN_CANDIDATE, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}
}
