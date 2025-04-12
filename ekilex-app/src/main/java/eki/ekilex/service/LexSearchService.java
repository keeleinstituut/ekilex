package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Colloc;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.CollocPosGroup;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.Government;
import eki.ekilex.data.InexactSynonym;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningForum;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Media;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Tag;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordOdRecommendation;
import eki.ekilex.data.WordOdUsage;
import eki.ekilex.data.WordRelation;
import eki.ekilex.data.WordRelationDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.util.PermCalculator;

@Component
public class LexSearchService extends AbstractWordSearchService {

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public WordDetails getWordDetails(
			Long wordId, Long fullDataMeaningId, List<String> selectedDatasetCodes, List<ClassifierSelect> languagesOrder,
			EkiUser user, EkiUserProfile userProfile, Tag activeTag, boolean isFullData) throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Word word = lexSearchDbService.getWord(wordId);
		if (word == null) {
			return null;
		}
		DatasetPermission userRole = null;
		if (user != null) {
			userRole = user.getRecentRole();
		}
		permCalculator.applyCrud(user, word);
		String wordLang = word.getLang();
		List<Lexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
		List<Freeform> wordFreeforms = commonDataDbService.getWordFreeforms(wordId, EXCLUDED_WORD_ATTRIBUTE_FF_TYPE_CODES, CLASSIF_LABEL_LANG_EST);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.getParadigmFormTuples(wordId, CLASSIF_LABEL_LANG_EST);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		List<WordRelation> wordRelations = lexDataDbService.getWordRelations(wordId, CLASSIF_LABEL_LANG_EST);
		List<Classifier> allWordRelationTypes = commonDataDbService.getDefaultClassifiers(ClassifierName.WORD_REL_TYPE, CLASSIF_LABEL_LANG_EST);
		List<Classifier> allAspects = commonDataDbService.getDefaultClassifiers(ClassifierName.ASPECT, CLASSIF_LABEL_LANG_EST);
		List<WordRelation> wordGroupMembers = lexDataDbService.getWordGroupMembers(wordId, CLASSIF_LABEL_LANG_EST);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers, allAspects);
		WordRelationDetails wordRelationDetails = conversionUtil.composeWordRelationDetails(wordRelations, wordGroups, wordLang, allWordRelationTypes);
		List<WordEtymTuple> wordEtymTuples = lexDataDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<WordForum> wordForums = commonDataDbService.getWordForums(wordId);
		permCalculator.applyCrud(user, wordForums);
		WordOdRecommendation wordOdRecommendation = odDataDbService.getWordOdRecommendation(wordId);
		List<WordOdUsage> wordOdUsages = odDataDbService.getWordOdUsages(wordId);

		boolean isFullDataCorrection = isFullData | CollectionUtils.size(lexemes) == 1;
		boolean isFullDataByMeaningId = !isFullDataCorrection && fullDataMeaningId != null;
		for (Lexeme lexeme : lexemes) {
			Long lexemeMeaningId = lexeme.getMeaningId();
			if (isFullDataByMeaningId && fullDataMeaningId.equals(lexemeMeaningId)) {
				populateLexeme(lexeme, word, languagesOrder, user, userProfile, true);
				isFullDataByMeaningId = false;
			} else {
				populateLexeme(lexeme, word, languagesOrder, user, userProfile, isFullDataCorrection);
			}
		}
		lexemeLevelPreseUtil.combineLevels(lexemes);
		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(userRole, lexemes, activeTag);

		word.setWordTypes(wordTypes);
		word.setParadigms(paradigms);
		word.setEtymology(wordEtymology);
		word.setForums(wordForums);
		word.setFreeforms(wordFreeforms);
		word.setWordOdRecommendation(wordOdRecommendation);
		word.setWordOdUsages(wordOdUsages);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordRelationDetails(wordRelationDetails);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	@Transactional
	public Lexeme getWordLexeme(
			Long lexemeId, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile, EkiUser user, boolean isFullData) throws Exception {

		Lexeme lexeme = lexSearchDbService.getLexeme(lexemeId, CLASSIF_LABEL_LANG_EST);
		if (lexeme != null) {
			Long wordId = lexeme.getWordId();
			Word word = lexSearchDbService.getWord(wordId);
			populateLexeme(lexeme, word, languagesOrder, user, userProfile, isFullData);
		}
		return lexeme;
	}

	@Transactional
	public List<Lexeme> getWordLexemesWithDefinitionsData(
			String searchFilter, List<String> datasetCodes, List<String> tagNames, EkiUser user) throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
		List<Lexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			WordsResult words = getWords(searchFilter, datasetCodes, tagNames, user, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, false);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				for (Word word : words.getWords()) {

					List<Lexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
					wordLexemes.forEach(lexeme -> {

						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
						List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
						permCalculator.filterVisibility(user, definitions);
						lexeme.setMeaningWords(meaningWords);
						Meaning meaning = new Meaning();
						meaning.setDefinitions(definitions);
						lexeme.setMeaning(meaning);
						lexeme.setLexemeWord(word);
					});
					lexemeLevelPreseUtil.combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		return lexemes;
	}

	@Transactional
	public Word getWord(Long wordId) {
		return lexSearchDbService.getWord(wordId);
	}

	private void populateLexeme(
			Lexeme lexeme, Word word, List<ClassifierSelect> languagesOrder, EkiUser user, EkiUserProfile userProfile, boolean isFullData) throws Exception {

		List<String> preferredMeaningWordLangs = new ArrayList<>();
		if (userProfile != null) {
			preferredMeaningWordLangs = userProfile.getPreferredSynLexMeaningWordLangs();
		}
		SearchLangsRestriction meaningWordLangsRestriction = composeLangsRestriction(preferredMeaningWordLangs);

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		String wordLang = word.getLang();

		permCalculator.applyCrud(user, lexeme);
		List<MeaningRelation> synMeaningRelations = commonDataDbService.getSynMeaningRelations(meaningId, datasetCode);
		appendLexemeLevels(synMeaningRelations);
		List<MeaningWord> synMeaningWords = commonDataDbService.getMeaningWords(lexemeId, meaningWordLangsRestriction);
		List<InexactSynonym> inexactSynonyms = lookupDbService.getMeaningInexactSynonyms(meaningId, wordLang, datasetCode);
		List<SynonymLangGroup> synonymLangGroups = conversionUtil.composeSynonymLangGroups(synMeaningRelations, synMeaningWords, inexactSynonyms, userProfile, wordLang, languagesOrder);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
		permCalculator.applyCrud(user, definitions);
		permCalculator.filterVisibility(user, definitions);
		List<MeaningForum> meaningForums = commonDataDbService.getMeaningForums(meaningId);
		permCalculator.applyCrud(user, meaningForums);

		Meaning meaning = new Meaning();
		meaning.setMeaningId(meaningId);
		meaning.setDomains(meaningDomains);
		meaning.setDefinitions(definitions);
		meaning.setForums(meaningForums);

		lexeme.setLexemeWord(word);
		lexeme.setMeaning(meaning);
		lexeme.setSynonymLangGroups(synonymLangGroups);

		if (isFullData) {

			for (Definition definition : definitions) {
				List<DefinitionNote> definitionNotes = definition.getNotes();
				permCalculator.filterVisibility(user, definitionNotes);
			}
			List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
			List<Freeform> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<Usage> usages = lexeme.getUsages();
			permCalculator.applyCrud(user, usages);
			permCalculator.filterVisibility(user, usages);
			List<LexemeNote> lexemeNotes = lexeme.getNotes();
			permCalculator.filterVisibility(user, lexemeNotes);
			List<Freeform> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, EXCLUDED_LEXEME_ATTRIBUTE_FF_TYPE_CODES, CLASSIF_LABEL_LANG_EST);
			List<NoteLangGroup> lexemeNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemeNotes, languagesOrder);
			List<LexemeRelation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST);
			List<CollocPosGroup> primaryCollocations = lexDataDbService.getPrimaryCollocations(lexemeId, CLASSIF_LABEL_LANG_EST);
			List<Colloc> secondaryCollocations = lexDataDbService.getSecondaryCollocations(lexemeId);
			List<CollocMember> collocationMembers = commonDataDbService.getCollocationMembers(lexemeId);
			List<Freeform> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, EXCLUDED_MEANING_ATTRIBUTE_FF_TYPE_CODES, CLASSIF_LABEL_LANG_EST);
			List<Freeform> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
			List<Media> meaningImages = commonDataDbService.getMeaningImagesAsMedia(meaningId);
			List<Media> meaningMedias = commonDataDbService.getMeaningMediaFiles(meaningId);
			List<MeaningNote> meaningNotes = commonDataDbService.getMeaningNotes(meaningId);
			permCalculator.filterVisibility(user, meaningNotes);
			List<NoteLangGroup> meaningNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningNotes, languagesOrder);
			List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST);
			List<String> meaningWordPreferredOrderDatasetCodes = Arrays.asList(datasetCode);
			List<MeaningRelation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, meaningWordPreferredOrderDatasetCodes, CLASSIF_LABEL_LANG_EST);
			List<List<MeaningRelation>> viewMeaningRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, wordLang, languagesOrder);
			List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
			boolean isCollocationsExist = lexDataDbService.isCollocationsExist(lexemeId);

			lexeme.setGovernments(governments);
			lexeme.setGrammars(grammars);
			lexeme.setFreeforms(lexemeFreeforms);
			lexeme.setNoteLangGroups(lexemeNoteLangGroups);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setPrimaryCollocations(primaryCollocations);
			lexeme.setSecondaryCollocations(secondaryCollocations);
			lexeme.setCollocationMembers(collocationMembers);
			lexeme.setCollocationsExist(isCollocationsExist);

			meaning.setFreeforms(meaningFreeforms);
			meaning.setLearnerComments(meaningLearnerComments);
			meaning.setImages(meaningImages);
			meaning.setMedias(meaningMedias);
			meaning.setNoteLangGroups(meaningNoteLangGroups);
			meaning.setSemanticTypes(meaningSemanticTypes);
			meaning.setRelations(meaningRelations);
			meaning.setViewRelations(viewMeaningRelations);
			meaning.setDefinitionLangGroups(definitionLangGroups);
			permCalculator.applyCrud(user, meaning);

			boolean lexemeOrMeaningClassifiersExist = StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeProficiencyLevelCode())
					|| CollectionUtils.isNotEmpty(lexeme.getPos())
					|| CollectionUtils.isNotEmpty(lexeme.getDerivs())
					|| CollectionUtils.isNotEmpty(lexeme.getRegisters())
					|| CollectionUtils.isNotEmpty(lexeme.getGrammars())
					|| CollectionUtils.isNotEmpty(meaning.getDomains())
					|| CollectionUtils.isNotEmpty(meaning.getSemanticTypes());
			lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
		}
	}
}