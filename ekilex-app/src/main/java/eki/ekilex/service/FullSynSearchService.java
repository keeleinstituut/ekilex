package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TypeWordRelMeaning;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordRelationDetails;

@Component
public class FullSynSearchService extends AbstractSynSearchService {

	@Transactional
	public WordDetails getWordFullSynDetails(
			Long wordId, List<ClassifierSelect> languagesOrder, String synCandidateDatasetCode, String synCandidateLangCode,
			List<String> synMeaningWordLangCodes, Tag activeTag, EkiUser user, EkiUserProfile userProfile) {

		DatasetPermission userRole = user.getRecentRole();
		boolean isAdmin = user.isAdmin();
		String datasetCode = userRole.getDatasetCode();
		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);

		Word word = synSearchDbService.getWord(wordId);
		permCalculator.applyCrud(userRole, word);
		List<WordForum> wordForums = commonDataDbService.getWordForums(wordId);
		permCalculator.applyCrud(userRole, isAdmin, wordForums);
		String wordLang = word.getLang();

		List<WordLexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		synLexemes.forEach(lexeme -> {
			languagesOrder.sort(Comparator.comparing(orderLang -> !StringUtils.equals(orderLang.getCode(), synCandidateLangCode)));
			populateLexeme(lexeme, languagesOrder, wordLang, synMeaningWordLangCodes, userRole, userProfile);
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
		word.setForums(wordForums);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	private void reorderFullSynLangGroups(WordLexeme lexeme, String synCandidateLangCode) {

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

			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples = commonDataDbService.
					getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);

			List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<DefSourceAndNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndNoteSourceTuples(meaningId);
			conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);

			wordMeaning.setUsages(usages);
			wordMeaning.setDefinitions(definitions);
		}
	}

	@Transactional
	public List<WordDescript> getRelationWordCandidates(Long wordRelationId, DatasetPermission userRole) {

		Long sourceWordId = synSearchDbService.getSynCandidateWordId(wordRelationId);
		SimpleWord sourceWord = synSearchDbService.getSimpleWord(sourceWordId);
		String sourceWordValue = sourceWord.getWordValue();
		String sourceWordLang = sourceWord.getLang();

		List<WordDescript> wordCandidates = getWordCandidates(sourceWordValue, sourceWordLang, userRole);
		return wordCandidates;
	}

	@Transactional
	public List<WordDescript> getMeaningWordCandidates(String wordValue, String wordLang, DatasetPermission userRole) {

		List<WordDescript> wordCandidates = getWordCandidates(wordValue, wordLang, userRole);
		return wordCandidates;
	}

	private List<WordDescript> getWordCandidates(String sourceWordValue, String sourceWordLang, DatasetPermission userRole) {

		List<WordDescript> wordCandidates = new ArrayList<>();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		List<Word> words = lookupDbService.getWords(sourceWordValue, sourceWordLang);

		for (Word word : words) {
			Long wordId = word.getWordId();
			List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

			wordLexemes.forEach(lexeme -> {
				Long meaningId = lexeme.getMeaningId();
				String lexemeDatasetCode = lexeme.getDatasetCode();
				List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, lexemeDatasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
				permCalculator.filterVisibility(userRole, definitions);
				Meaning meaning = new Meaning();
				meaning.setMeaningId(meaningId);
				meaning.setDefinitions(definitions);
				lexeme.setMeaning(meaning);
			});

			WordDescript wordCandidate = new WordDescript();
			wordCandidate.setWord(word);
			wordCandidate.setLexemes(wordLexemes);

			wordCandidates.add(wordCandidate);
		}
		return wordCandidates;
	}
}
