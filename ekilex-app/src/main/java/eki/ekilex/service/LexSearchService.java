package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.LayerName;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.DefSourceAndPublicNoteSourceTuple;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.ProcessDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class LexSearchService extends AbstractWordSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;
	
	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public WordDetails getWordDetails(
			Long wordId, List<String> selectedDatasetCodes, List<ClassifierSelect> languagesOrder,
			EkiUser user, EkiUserProfile userProfile, boolean isFullData) throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Word word = lexSearchDbService.getWord(wordId);
		if (word == null) {
			return null;
		}
		DatasetPermission userRole = user.getRecentRole();
		permCalculator.applyCrud(word, userRole);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, classifierLabelLang, classifierLabelTypeDescrip);
		List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, classifierLabelLang, classifierLabelTypeDescrip);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.getParadigmFormTuples(wordId, word.getWordValue(), classifierLabelLang, classifierLabelTypeDescrip);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		List<Relation> wordRelations = lexSearchDbService.getWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<Relation> wordGroupMembers = lexSearchDbService.getWordGroupMembers(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers);
		List<FreeForm> odWordRecommendations = lexSearchDbService.getOdWordRecommendations(wordId);
		Integer wordProcessLogCount = processDbService.getLogCountForWord(wordId);
		Timestamp latestLogEventTime = lifecycleLogDbService.getLatestLogTimeForWord(wordId);

		boolean isFullDataCorrection = isFullData | CollectionUtils.size(lexemes) == 1;
		for (WordLexeme lexeme : lexemes) {
			populateLexeme(lexeme, languagesOrder, user, userProfile, isFullDataCorrection);
		}
		lexemeLevelPreseUtil.combineLevels(lexemes);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setParadigms(paradigms);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordRelations(wordRelations);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setWordGroups(wordGroups);
		wordDetails.setOdWordRecommendations(odWordRecommendations);
		wordDetails.setWordProcessLogCount(wordProcessLogCount);
		wordDetails.setLastChangedOn(latestLogEventTime);

		return wordDetails;
	}

	@Transactional
	public WordLexeme getDefaultWordLexeme(Long lexemeId, List<ClassifierSelect> languagesOrder) throws Exception {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		if (lexeme != null) {
			populateLexeme(lexeme, languagesOrder, new EkiUser(), null, true);
		}
		return lexeme;
	}

	@Transactional
	public WordLexeme getWordLexeme(Long lexemeId, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile, EkiUser user, boolean isFullData) throws Exception {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		if (lexeme != null) {
			populateLexeme(lexeme, languagesOrder, user, userProfile, isFullData);
		}
		return lexeme;
	}

	@Transactional
	public List<WordLexeme> getWordLexemesWithDefinitionsData(String searchFilter, List<String> datasetCodes, Long userId, DatasetPermission userRole, LayerName layerName) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			WordsResult words = getWords(searchFilter, datasetCodes, userRole, layerName, false, DEFAULT_OFFSET);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, classifierLabelLang, classifierLabelTypeDescrip);
					wordLexemes.forEach(lexeme -> {
						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
						List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, lexeme.getWordLang());
						List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
						permCalculator.filterVisibility(definitions, userId);
						lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
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
				FreeformType.LEARNER_COMMENT.name(), FreeformType.SEMANTIC_TYPE.name(), FreeformType.PUBLIC_NOTE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {
				FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(),
				FreeformType.PUBLIC_NOTE.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};

		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();
		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		String wordLang = lexeme.getWordLang();
		Meaning meaning = new Meaning();

		permCalculator.applyCrud(lexeme, userRole);
		List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
		List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, wordLang);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId);
		meaningDomains = conversionUtil.removeOrderedClassifierDuplicates(meaningDomains);

		lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
		meaning.setMeaningId(meaningId);
		meaning.setDomains(meaningDomains);

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
		permCalculator.applyCrud(definitions, userRole);
		permCalculator.filterVisibility(definitions, userId);

		if (isFullData) {

			List<DefSourceAndPublicNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndPublicNoteSourceTuples(meaningId);
			conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);
			for (Definition definition : definitions) {
				List<DefinitionNote> definitionPublicNotes = definition.getPublicNotes();
				permCalculator.filterVisibility(definitionPublicNotes, userId);
			}
			List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
			List<FreeForm> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			permCalculator.applyCrud(usages, userRole);
			permCalculator.filterVisibility(usages, userId);
			List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
			List<NoteSourceTuple> lexemePublicNoteSourceTuples = commonDataDbService.getLexemePublicNoteSourceTuples(lexemeId);
			List<LexemeNote> lexemePublicNotes = conversionUtil.composeNotes(LexemeNote.class, lexemeId, lexemePublicNoteSourceTuples);
			permCalculator.filterVisibility(lexemePublicNotes, userId);
			List<NoteLangGroup> lexemePublicNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemePublicNotes, languagesOrder);
			List<FreeForm> odLexemeRecommendations = commonDataDbService.getOdLexemeRecommendations(lexemeId);
			List<Relation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull);
			List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			List<CollocationTuple> primaryCollocTuples = lexSearchDbService.getPrimaryCollocationTuples(lexemeId);
			List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
			List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.getSecondaryCollocationTuples(lexemeId);
			List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);

			List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
			List<FreeForm> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
			List<ImageSourceTuple> meaningImageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
			List<Image> meaningImages = conversionUtil.composeMeaningImages(meaningImageSourceTuples);
			List<NoteSourceTuple> meaningPublicNoteSourceTuples = commonDataDbService.getMeaningPublicNoteSourceTuples(meaningId);
			List<MeaningNote> meaningPublicNotes = conversionUtil.composeNotes(MeaningNote.class, meaningId, meaningPublicNoteSourceTuples);
			permCalculator.filterVisibility(meaningPublicNotes, userId);
			List<NoteLangGroup> meaningPublicNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningPublicNotes, languagesOrder);
			List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
			List<String> meaningWordPreferredOrderDatasetCodes = Arrays.asList(datasetCode);
			List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, meaningWordPreferredOrderDatasetCodes, classifierLabelLang, classifierLabelTypeDescrip);
			List<List<Relation>> viewMeaningRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, wordLang, languagesOrder);
			List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);

			lexeme.setGovernments(governments);
			lexeme.setGrammars(grammars);
			lexeme.setUsages(usages);
			lexeme.setLexemeFreeforms(lexemeFreeforms);
			lexeme.setLexemePublicNoteLangGroups(lexemePublicNoteLangGroups);
			lexeme.setOdLexemeRecommendations(odLexemeRecommendations);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setSourceLinks(lexemeSourceLinks);
			lexeme.setCollocationPosGroups(collocationPosGroups);
			lexeme.setSecondaryCollocations(secondaryCollocations);

			permCalculator.applyCrud(meaning, userRole);
			meaning.setFreeforms(meaningFreeforms);
			meaning.setLearnerComments(meaningLearnerComments);
			meaning.setImages(meaningImages);
			meaning.setPublicNoteLangGroups(meaningPublicNoteLangGroups);
			meaning.setSemanticTypes(meaningSemanticTypes);
			meaning.setRelations(meaningRelations);
			meaning.setViewRelations(viewMeaningRelations);
			meaning.setDefinitionLangGroups(definitionLangGroups);

			boolean lexemeOrMeaningClassifiersExist =
					StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
							|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
							|| StringUtils.isNotBlank(lexeme.getLexemeProcessStateCode())
							|| CollectionUtils.isNotEmpty(lexeme.getPos())
							|| CollectionUtils.isNotEmpty(lexeme.getDerivs())
							|| CollectionUtils.isNotEmpty(lexeme.getRegisters())
							|| CollectionUtils.isNotEmpty(lexeme.getGrammars())
							|| CollectionUtils.isNotEmpty(lexeme.getLexemeFrequencies())
							|| CollectionUtils.isNotEmpty(meaning.getDomains())
							|| CollectionUtils.isNotEmpty(meaning.getSemanticTypes());
			lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
		}

		meaning.setDefinitions(definitions);
		lexeme.setMeaning(meaning);
	}
}