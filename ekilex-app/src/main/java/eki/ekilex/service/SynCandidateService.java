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

import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.TextWithSource;
import eki.ekilex.data.api.WordSynCandidate;
import eki.ekilex.data.api.WordSynCandidateData;
import eki.ekilex.service.db.LookupDbService;

@Component
public class SynCandidateService extends AbstractCudService implements GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(SynCandidateService.class);

	@Autowired
	private LookupDbService lookupDbService;

	@Transactional
	public void createWordSynCandidates(WordSynCandidateData wordSynCandidateData) throws Exception {

		String headwordValue = wordSynCandidateData.getValue();
		String headwordLang = wordSynCandidateData.getLang();
		String synCandidateDatasetCode = wordSynCandidateData.getDatasetCode();
		List<WordSynCandidate> wordSynCandidates = wordSynCandidateData.getWordSynCandidates();

		List<Word> existingHeadwords = lookupDbService.getWords(headwordValue, headwordLang);

		for (WordSynCandidate wordSynCandidate : wordSynCandidates) {

			BigDecimal synLexemeWeight = wordSynCandidate.getWeight();

			handleSynCandidateData(synCandidateDatasetCode, wordSynCandidate);

			//TODO relate candidates with existingHeadwords
		}
	}

	private WordLexemeMeaningIdTuple handleSynCandidateData(String synCandidateDatasetCode, WordSynCandidate wordSynCandidate) throws Exception {

		String synCandidateWordValue = wordSynCandidate.getValue();
		String synCandidateWordLang = wordSynCandidate.getLang();
		List<String> synPosCodes = wordSynCandidate.getPosCodes();
		List<TextWithSource> synDefinitions = wordSynCandidate.getDefinitions();
		List<TextWithSource> synUsages = wordSynCandidate.getUsages();

		List<Word> existingSynCandidateWords = lookupDbService.getWords(synCandidateWordValue, synCandidateWordLang);
		WordLexemeMeaningIdTuple synCandidateWordLexemeMeaningId;

		if (CollectionUtils.isEmpty(existingSynCandidateWords)) {
			synCandidateWordLexemeMeaningId = createWordAndLexemeAndMeaning(synCandidateWordValue, synCandidateWordLang, synCandidateDatasetCode);
		} else {
			Word existingSynCandidateWord = existingSynCandidateWords.stream()
					.filter(word -> word.getDatasetCodes().contains(synCandidateDatasetCode))
					.findFirst()
					.orElse(null);
			if (existingSynCandidateWord != null) {
				Long existingSynCandidateWordId = existingSynCandidateWord.getWordId();
				List<WordLexemeMeaningIdTuple> existingSynCandidateWordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIdsByWord(existingSynCandidateWordId, synCandidateDatasetCode);
				synCandidateWordLexemeMeaningId = existingSynCandidateWordLexemeMeaningIds.get(0);
			} else if (existingSynCandidateWords.size() == 1) {
				existingSynCandidateWord = existingSynCandidateWords.get(0);
				Long existingSynCandidateWordId = existingSynCandidateWord.getWordId();
				synCandidateWordLexemeMeaningId = createLexemeAndMeaning(existingSynCandidateWordId, synCandidateDatasetCode);
			} else {
				synCandidateWordLexemeMeaningId = createWordAndLexemeAndMeaning(synCandidateWordValue, synCandidateWordLang, synCandidateDatasetCode);
			}
		}

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

	private WordLexemeMeaningIdTuple createWordAndLexemeAndMeaning(String value, String lang, String datasetCode) throws Exception {

		value = textDecorationService.removeEkiElementMarkup(value);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createWordAndLexemeAndMeaning(value, value, valueAsWord, value, lang, datasetCode, PUBLICITY_PRIVATE, null);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		boolean isManualEventOnUpdateEnabled = MANUAL_EVENT_ON_UPDATE_DISABLED;
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createWordAndLexemeAndMeaning", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createWordAndLexemeAndMeaning", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createWordAndLexemeAndMeaning", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);

		return wordLexemeMeaningId;
	}

	private WordLexemeMeaningIdTuple createLexemeAndMeaning(Long wordId, String datasetCode) throws Exception {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexemeAndMeaning(wordId, datasetCode, PUBLICITY_PRIVATE);

		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		boolean isManualEventOnUpdateEnabled = MANUAL_EVENT_ON_UPDATE_DISABLED;
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createLexemeAndMeaning", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createLexemeAndMeaning", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createLexemeAndMeaning", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);

		return wordLexemeMeaningId;
	}
}
