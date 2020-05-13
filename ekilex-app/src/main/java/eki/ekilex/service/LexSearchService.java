package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
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

	@Transactional
	public WordDetails getWordDetails(Long wordId, List<String> selectedDatasetCodes, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile, boolean isFullData) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Word word = lexSearchDbService.getWord(wordId);
		if (word == null) {
			return null;
		}
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
		lexemes.forEach(lexeme -> populateLexeme(lexeme, languagesOrder, userProfile, isFullDataCorrection));
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
	public WordLexeme getDefaultWordLexeme(Long lexemeId) {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		if (lexeme != null) {
			populateLexeme(lexeme, null, null, true);
		}
		return lexeme;
	}

	@Transactional
	public WordLexeme getWordLexeme(Long lexemeId, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile, boolean isFullData) {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		if (lexeme != null) {
			populateLexeme(lexeme, languagesOrder, userProfile, isFullData);
		}
		return lexeme;
	}

	@Transactional
	public List<WordLexeme> getWordLexemesWithDefinitionsData(String searchFilter, List<String> datasets) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			WordsResult words = getWords(searchFilter, datasets, false, DEFAULT_OFFSET);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, classifierLabelLang, classifierLabelTypeDescrip);
					wordLexemes.forEach(lexeme -> {
						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
						List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, lexeme.getWordLang());
						List<DefinitionRefTuple> definitionRefTuples =
								commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
						lexeme.setDefinitions(definitions);
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

	private void populateLexeme(WordLexeme lexeme, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile, boolean isFullData) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.SEMANTIC_TYPE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {
				FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(),
				FreeformType.PUBLIC_NOTE.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		String wordLang = lexeme.getWordLang();

		List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
		List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, wordLang);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId);
		meaningDomains = conversionUtil.removeOrderedClassifierDuplicates(meaningDomains);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		lexeme.setMeaningDomains(meaningDomains);
		lexeme.setDefinitions(definitions);
		lexeme.setMeaningWordLangGroups(meaningWordLangGroups);

		if (isFullData) {

			List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
			List<FreeForm> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
			List<FreeForm> lexemePublicNotes = commonDataDbService.getLexemePublicNotes(lexemeId);
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
			List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
			List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
			List<List<Relation>> viewMeaningRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, wordLang, languagesOrder);

			lexeme.setGovernments(governments);
			lexeme.setGrammars(grammars);
			lexeme.setUsages(usages);
			lexeme.setLexemeFreeforms(lexemeFreeforms);
			lexeme.setLexemePublicNotes(lexemePublicNotes);
			lexeme.setOdLexemeRecommendations(odLexemeRecommendations);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setSourceLinks(lexemeSourceLinks);
			lexeme.setCollocationPosGroups(collocationPosGroups);
			lexeme.setSecondaryCollocations(secondaryCollocations);

			lexeme.setMeaningFreeforms(meaningFreeforms);
			lexeme.setMeaningLearnerComments(meaningLearnerComments);
			lexeme.setMeaningImages(meaningImages);
			lexeme.setMeaningSemanticTypes(meaningSemanticTypes);
			lexeme.setMeaningRelations(meaningRelations);
			lexeme.setViewMeaningRelations(viewMeaningRelations);

			boolean lexemeOrMeaningClassifiersExist =
					StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
							|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
							|| StringUtils.isNotBlank(lexeme.getLexemeProcessStateCode())
							|| CollectionUtils.isNotEmpty(lexeme.getPos())
							|| CollectionUtils.isNotEmpty(lexeme.getDerivs())
							|| CollectionUtils.isNotEmpty(lexeme.getRegisters())
							|| CollectionUtils.isNotEmpty(lexeme.getGrammars())
							|| CollectionUtils.isNotEmpty(lexeme.getLexemeFrequencies())
							|| CollectionUtils.isNotEmpty(lexeme.getMeaningDomains())
							|| CollectionUtils.isNotEmpty(lexeme.getMeaningSemanticTypes());
			lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
		}
	}
}