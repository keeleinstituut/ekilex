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

import eki.common.constant.FreeformType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningForum;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Media;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Tag;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordLexeme;
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
			Long wordId, Long fullDataMeaningId, List<String> selectedDatasetCodes, List<ClassifierSelect> languagesOrder, EkiUser user,
			EkiUserProfile userProfile, Tag activeTag, boolean isFullData) throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Word word = lexSearchDbService.getWord(wordId);
		if (word == null) {
			return null;
		}
		DatasetPermission userRole = user.getRecentRole();
		boolean isAdmin = user.isAdmin();
		permCalculator.applyCrud(userRole, word);
		String wordLang = word.getLang();
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.getParadigmFormTuples(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		List<WordRelation> wordRelations = lexSearchDbService.getWordRelations(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Classifier> allWordRelationTypes = commonDataDbService.getWordRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Classifier> allAspects = commonDataDbService.getAspects(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<WordRelation> wordGroupMembers = lexSearchDbService.getWordGroupMembers(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers, allAspects);
		WordRelationDetails wordRelationDetails = conversionUtil.composeWordRelationDetails(wordRelations, wordGroups, wordLang, allWordRelationTypes);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<FreeForm> odWordRecommendations = commonDataDbService.getOdWordRecommendations(wordId);
		List<WordForum> wordForums = commonDataDbService.getWordForums(wordId);
		permCalculator.applyCrud(userRole, isAdmin, wordForums);

		boolean isFullDataCorrection = isFullData | CollectionUtils.size(lexemes) == 1;
		boolean isFullDataByMeaningId = !isFullDataCorrection && fullDataMeaningId != null;
		for (WordLexeme lexeme : lexemes) {
			if (isFullDataByMeaningId) {
				Long lexemeMeaningId = lexeme.getMeaningId();
				if (fullDataMeaningId.equals(lexemeMeaningId)) {
					populateLexeme(lexeme, languagesOrder, user, userProfile, true);
					isFullDataByMeaningId = false;
					continue;
				}
			}
			populateLexeme(lexeme, languagesOrder, user, userProfile, isFullDataCorrection);
		}
		lexemeLevelPreseUtil.combineLevels(lexemes);
		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(userRole, lexemes, activeTag);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setParadigms(paradigms);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setOdWordRecommendations(odWordRecommendations);
		wordDetails.setWordRelationDetails(wordRelationDetails);
		word.setForums(wordForums);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	@Transactional
	public WordLexeme getWordLexeme(
			Long lexemeId, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile, EkiUser user, boolean isFullData) throws Exception {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		if (lexeme != null) {
			populateLexeme(lexeme, languagesOrder, user, userProfile, isFullData);
		}
		return lexeme;
	}

	@Transactional
	public List<WordLexeme> getWordLexemesWithDefinitionsData(
			String searchFilter, List<String> datasetCodes, DatasetPermission userRole, List<String> tagNames) throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			WordsResult words = getWords(searchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, false);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
					wordLexemes.forEach(lexeme -> {
						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
						List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
						permCalculator.filterVisibility(userRole, definitions);
						lexeme.setMeaningWords(meaningWords);
						Meaning meaning = new Meaning();
						meaning.setDefinitions(definitions);
						lexeme.setMeaning(meaning);
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
			WordLexeme lexeme, List<ClassifierSelect> languagesOrder, EkiUser user, EkiUserProfile userProfile, boolean isFullData) throws Exception {

		final String[] excludeMeaningAttributeTypes = new String[] {
				FreeformType.LEARNER_COMMENT.name(), FreeformType.SEMANTIC_TYPE.name(), FreeformType.NOTE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {
				FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(), FreeformType.NOTE.name()};

		DatasetPermission userRole = user.getRecentRole();
		boolean isAdmin = user.isAdmin();
		List<String> preferredMeaningWordLangs = new ArrayList<>();
		if (userProfile != null) {
			preferredMeaningWordLangs = userProfile.getPreferredSynLexMeaningWordLangs();
		}
		SearchLangsRestriction meaningWordLangsRestriction = composeLangsRestriction(preferredMeaningWordLangs);

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		String wordLang = lexeme.getWordLang();
		Meaning meaning = new Meaning();

		permCalculator.applyCrud(userRole, lexeme);
		List<String> lexemeTags = commonDataDbService.getLexemeTags(lexemeId);

		List<MeaningRelation> synMeaningRelations = commonDataDbService.getSynMeaningRelations(meaningId, datasetCode);
		appendLexemeLevels(synMeaningRelations);
		List<MeaningWord> synMeaningWords = commonDataDbService.getMeaningWords(lexemeId, meaningWordLangsRestriction);
		List<SynonymLangGroup> synonymLangGroups = conversionUtil.composeSynonymLangGroups(synMeaningRelations, synMeaningWords, userProfile, wordLang, languagesOrder);

		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.applyCrud(userRole, definitions);
		permCalculator.filterVisibility(userRole, definitions);
		List<MeaningForum> meaningForums = commonDataDbService.getMeaningForums(meaningId);
		permCalculator.applyCrud(userRole, isAdmin, meaningForums);

		lexeme.setTags(lexemeTags);
		lexeme.setSynonymLangGroups(synonymLangGroups);
		lexeme.setMeaning(meaning);

		meaning.setMeaningId(meaningId);
		meaning.setDomains(meaningDomains);
		meaning.setDefinitions(definitions);
		meaning.setForums(meaningForums);

		if (isFullData) {

			List<DefSourceAndNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndNoteSourceTuples(meaningId);
			conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);
			for (Definition definition : definitions) {
				List<DefinitionNote> definitionNotes = definition.getNotes();
				permCalculator.filterVisibility(userRole, definitionNotes);
			}
			List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
			List<FreeForm> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			permCalculator.applyCrud(userRole, usages);
			permCalculator.filterVisibility(userRole, usages);
			List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
			List<NoteSourceTuple> lexemeNoteSourceTuples = commonDataDbService.getLexemeNoteSourceTuples(lexemeId);
			List<LexemeNote> lexemeNotes = conversionUtil.composeNotes(LexemeNote.class, lexemeId, lexemeNoteSourceTuples);
			permCalculator.filterVisibility(userRole, lexemeNotes);
			List<NoteLangGroup> lexemeNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemeNotes, languagesOrder);
			List<LexemeRelation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			List<CollocationTuple> primaryCollocTuples = lexSearchDbService.getPrimaryCollocationTuples(lexemeId);
			List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
			List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.getSecondaryCollocationTuples(lexemeId);
			List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);

			List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
			List<FreeForm> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
			List<ImageSourceTuple> meaningImageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
			List<Media> meaningImages = conversionUtil.composeMeaningImages(meaningImageSourceTuples);
			List<Media> meaningMedias = commonDataDbService.getMeaningMedias(meaningId);
			List<NoteSourceTuple> meaningNoteSourceTuples = commonDataDbService.getMeaningNoteSourceTuples(meaningId);
			List<MeaningNote> meaningNotes = conversionUtil.composeNotes(MeaningNote.class, meaningId, meaningNoteSourceTuples);
			permCalculator.filterVisibility(userRole, meaningNotes);
			List<NoteLangGroup> meaningNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningNotes, languagesOrder);
			List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<String> meaningWordPreferredOrderDatasetCodes = Arrays.asList(datasetCode);
			List<MeaningRelation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, meaningWordPreferredOrderDatasetCodes, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<List<MeaningRelation>> viewMeaningRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, wordLang, languagesOrder);
			List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);

			lexeme.setGovernments(governments);
			lexeme.setGrammars(grammars);
			lexeme.setUsages(usages);
			lexeme.setLexemeFreeforms(lexemeFreeforms);
			lexeme.setLexemeNoteLangGroups(lexemeNoteLangGroups);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setSourceLinks(lexemeSourceLinks);
			lexeme.setCollocationPosGroups(collocationPosGroups);
			lexeme.setSecondaryCollocations(secondaryCollocations);

			permCalculator.applyCrud(userRole, meaning);
			meaning.setFreeforms(meaningFreeforms);
			meaning.setLearnerComments(meaningLearnerComments);
			meaning.setImages(meaningImages);
			meaning.setMedias(meaningMedias);
			meaning.setNoteLangGroups(meaningNoteLangGroups);
			meaning.setSemanticTypes(meaningSemanticTypes);
			meaning.setRelations(meaningRelations);
			meaning.setViewRelations(viewMeaningRelations);
			meaning.setDefinitionLangGroups(definitionLangGroups);

			boolean lexemeOrMeaningClassifiersExist =
					StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
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