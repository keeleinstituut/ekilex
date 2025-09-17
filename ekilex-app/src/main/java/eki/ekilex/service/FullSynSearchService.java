package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TypeWordRelMeaning;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.WordRelationDetails;

@Component
public class FullSynSearchService extends AbstractSynSearchService {

	@Transactional
	public WordDetails getWordFullSynDetails(
			Long wordId, List<ClassifierSelect> languagesOrder, String synCandidateDatasetCode, String synCandidateLangCode,
			List<String> synMeaningWordLangCodes, Tag activeTag, EkiUser user, EkiUserProfile userProfile) {

		DatasetPermission userRole = user.getRecentRole();
		String datasetCode = userRole.getDatasetCode();
		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);

		Word word = synSearchDbService.getWord(wordId);
		permCalculator.applyCrud(user, word);
		List<WordForum> wordForums = commonDataDbService.getWordForums(wordId);
		permCalculator.applyCrud(user, wordForums);
		word.setForums(wordForums);

		List<ClassifierSelect> sortedLanguagesOrder = languagesOrder.stream()
				.sorted(Comparator.comparing(orderLang -> !StringUtils.equals(orderLang.getCode(), synCandidateLangCode)))
				.collect(Collectors.toList());
		List<Lexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
		synLexemes.forEach(lexeme -> {
			populateLexeme(lexeme, word, sortedLanguagesOrder, synMeaningWordLangCodes, user, userProfile);
			reorderFullSynLangGroups(lexeme, synCandidateLangCode);
		});
		lexemeLevelPreseUtil.combineLevels(synLexemes);
		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(synLexemes, activeTag);

		List<SynRelation> synRelations = Collections.emptyList();
		if (StringUtils.isNoneBlank(synCandidateDatasetCode, synCandidateLangCode)) {
			synRelations = synSearchDbService.getWordFullSynRelations(wordId, WORD_REL_TYPE_CODE_RAW, synCandidateDatasetCode, synCandidateLangCode);
			for (SynRelation synRelation : synRelations) {
				populateFullSynRelationUsagesAndDefinitions(synRelation);
			}
		}
		WordRelationDetails wordRelationDetails = new WordRelationDetails();
		wordRelationDetails.setWordSynRelations(synRelations);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setLexemes(synLexemes);
		wordDetails.setWordRelationDetails(wordRelationDetails);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	private void reorderFullSynLangGroups(Lexeme lexeme, String synCandidateLangCode) {

		List<SynonymLangGroup> synonymLangGroups = lexeme.getSynonymLangGroups();
		SynonymLangGroup emptySynonymLangGroup = new SynonymLangGroup();
		emptySynonymLangGroup.setLang(synCandidateLangCode);

		if (synonymLangGroups.isEmpty()) {
			synonymLangGroups.add(emptySynonymLangGroup);
		} else {
			SynonymLangGroup firstSynonymLangGroup = synonymLangGroups.get(0);
			if (!StringUtils.equals(firstSynonymLangGroup.getLang(), synCandidateLangCode)) {
				synonymLangGroups.add(0, emptySynonymLangGroup);
			}
		}
	}

	private void populateFullSynRelationUsagesAndDefinitions(SynRelation synRelation) {

		List<TypeWordRelMeaning> wordMeanings = synRelation.getWordMeanings();
		for (TypeWordRelMeaning wordMeaning : wordMeanings) {
			Long lexemeId = wordMeaning.getLexemeId();
			Long meaningId = wordMeaning.getMeaningId();

			List<Usage> usages = commonDataDbService.getUsages(lexemeId);
			List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST);

			wordMeaning.setUsages(usages);
			wordMeaning.setDefinitions(definitions);
		}
	}

	@Transactional
	public List<WordDescript> getRelationWordCandidates(Long wordRelationId, EkiUser user) {

		Word sourceWord = synSearchDbService.getSynCandidateWord(wordRelationId);
		String sourceWordValue = sourceWord.getWordValue();
		String sourceWordLang = sourceWord.getLang();

		List<WordDescript> wordCandidates = getWordCandidates(sourceWordValue, sourceWordLang, user);
		return wordCandidates;
	}

	@Transactional
	public List<WordDescript> getMeaningWordCandidates(String wordValue, String wordLang, EkiUser user) {

		List<WordDescript> wordCandidates = getWordCandidates(wordValue, wordLang, user);
		return wordCandidates;
	}

	private List<WordDescript> getWordCandidates(String sourceWordValue, String sourceWordLang, EkiUser user) {

		List<WordDescript> wordCandidates = new ArrayList<>();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		List<Word> words = lookupDbService.getWords(sourceWordValue, sourceWordLang);

		for (Word word : words) {

			Long wordId = word.getWordId();
			List<Lexeme> wordLexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);

			wordLexemes.forEach(lexeme -> {
				Long meaningId = lexeme.getMeaningId();
				String lexemeDatasetCode = lexeme.getDatasetCode();
				List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, lexemeDatasetCode, CLASSIF_LABEL_LANG_EST);
				permCalculator.filterVisibility(user, definitions);
				Meaning meaning = new Meaning();
				meaning.setMeaningId(meaningId);
				meaning.setDefinitions(definitions);
				lexeme.setMeaning(meaning);
				lexeme.setLexemeWord(word);
			});

			WordDescript wordCandidate = new WordDescript();
			wordCandidate.setWord(word);
			wordCandidate.setLexemes(wordLexemes);

			wordCandidates.add(wordCandidate);
		}
		return wordCandidates;
	}

}
