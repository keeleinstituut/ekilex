package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.ProcessDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class LexSearchService extends AbstractWordSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;
	
	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Transactional
	public WordDetails getWordDetails(Long wordId, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Word word = lexSearchDbService.getWord(wordId);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, classifierLabelLang, classifierLabelTypeDescrip);
		conversionUtil.setWordTypeFlags(word, wordTypes);
		List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.getParadigmFormTuples(wordId, word.getValue(), classifierLabelLang, classifierLabelTypeDescrip);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		List<Relation> wordRelations = lexSearchDbService.getWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<Relation> wordGroupMembers = lexSearchDbService.getWordGroupMembers(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers);
		Integer wordProcessLogCount = processDbService.getLogCountForWord(wordId);
		Timestamp latestLogEventTime = lifecycleLogDbService.getLatestLogTimeForWord(wordId);

		lexemes.forEach(lexeme -> populateLexeme(lexeme, datasetNameMap));
		lexemeLevelCalcUtil.combineLevels(lexemes);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setParadigms(paradigms);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordRelations(wordRelations);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setWordGroups(wordGroups);
		wordDetails.setWordProcessLogCount(wordProcessLogCount);
		wordDetails.setLastChangedOn(latestLogEventTime);

		return wordDetails;
	}

	@Transactional
	public WordLexeme getWordLexeme(Long lexemeId) {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId);
		if (lexeme != null) {
			Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
			populateLexeme(lexeme, datasetNameMap);
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
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction);
					wordLexemes.forEach(lexeme -> {
						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
						List<DefinitionRefTuple> definitionRefTuples =
								commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						lexeme.setMeaningWords(meaningWords);
						lexeme.setDefinitions(definitions);
					});
					lexemeLevelCalcUtil.combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		return lexemes;
	}

	@Transactional
	public List<WordDescript> getWordDescripts(String searchFilter, List<String> datasets, Long excludingMeaningId) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		WordsResult words = getWords(searchFilter, datasets, true, DEFAULT_OFFSET);
		List<WordDescript> wordDescripts = new ArrayList<>();
		for (Word word : words.getWords()) {
			List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction);
			boolean lexemeAlreadyExists = false;
			if (excludingMeaningId != null) {
				lexemeAlreadyExists = lexemes.stream().anyMatch(lexeme -> lexeme.getMeaningId().equals(excludingMeaningId));
			}
			if (lexemeAlreadyExists) {
				continue;
			}
			List<String> allDefinitionValues = new ArrayList<>();
			lexemes.forEach(lexeme -> {
				Long lexemeId = lexeme.getLexemeId();
				Long meaningId = lexeme.getMeaningId();
				String datasetCode = lexeme.getDatasetCode();
				List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
				lexeme.setMeaningWords(meaningWords);
				List<DefinitionRefTuple> definitionRefTuples =
						commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
				List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
				List<String> lexemeDefinitionValues = definitions.stream().map(def -> def.getValue()).collect(Collectors.toList());
				allDefinitionValues.addAll(lexemeDefinitionValues);
			});
			List<String> distinctDefinitionValues = allDefinitionValues.stream().distinct().collect(Collectors.toList());
			WordDescript wordDescript = new WordDescript();
			wordDescript.setWord(word);
			wordDescript.setLexemes(lexemes);
			wordDescript.setDefinitions(distinctDefinitionValues);
			wordDescripts.add(wordDescript);
		}
		return wordDescripts;
	}

	@Transactional
	public Word getWord(Long wordId) {
		return lexSearchDbService.getWord(wordId);
	}

	private void populateLexeme(WordLexeme lexeme, Map<String, String> datasetNameMap) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.SEMANTIC_TYPE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(), FreeformType.PUBLIC_NOTE.name()};

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();

		String datasetName = datasetNameMap.get(datasetCode);
		List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
		List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> lexemeDerivs = commonDataDbService.getLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> lexemeRegisters = commonDataDbService.getLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId);
		meaningDomains = conversionUtil.removeOrderedClassifierDuplicates(meaningDomains);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<FreeForm> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
		List<ImageSourceTuple> meaningImageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
		List<Image> meaningImages = conversionUtil.composeMeaningImages(meaningImageSourceTuples);
		List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
		List<FreeForm> lexemePublicNotes = commonDataDbService.getLexemePublicNotes(lexemeId);
		List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		List<Relation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull);
		List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
		List<List<Relation>> groupedMeaningRelations = conversionUtil.groupRelationsById(meaningRelations);
		List<FreeForm> lexemeGrammars = commonDataDbService.getLexemeGrammars(lexemeId);
		List<CollocationTuple> primaryCollocTuples = lexSearchDbService.getPrimaryCollocationTuples(lexemeId);
		List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
		List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.getSecondaryCollocationTuples(lexemeId);
		List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);
		List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);

		lexeme.setDataset(datasetName);
		lexeme.setPos(lexemePos);
		lexeme.setDerivs(lexemeDerivs);
		lexeme.setRegisters(lexemeRegisters);
		lexeme.setMeaningSemanticTypes(meaningSemanticTypes);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setMeaningDomains(meaningDomains);
		lexeme.setDefinitions(definitions);
		lexeme.setMeaningFreeforms(meaningFreeforms);
		lexeme.setMeaningLearnerComments(meaningLearnerComments);
		lexeme.setMeaningImages(meaningImages);
		lexeme.setLexemeFreeforms(lexemeFreeforms);
		lexeme.setLexemePublicNotes(lexemePublicNotes);
		lexeme.setGovernments(governments);
		lexeme.setUsages(usages);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setMeaningRelations(meaningRelations);
		lexeme.setGrammars(lexemeGrammars);
		lexeme.setCollocationPosGroups(collocationPosGroups);
		lexeme.setSecondaryCollocations(secondaryCollocations);
		lexeme.setSourceLinks(lexemeSourceLinks);
		lexeme.setGroupedMeaningRelations(groupedMeaningRelations);

		boolean lexemeOrMeaningClassifiersExist =
				StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
						|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
						|| StringUtils.isNotBlank(lexeme.getLexemeProcessStateCode())
						|| CollectionUtils.isNotEmpty(lexemePos)
						|| CollectionUtils.isNotEmpty(lexemeDerivs)
						|| CollectionUtils.isNotEmpty(lexemeRegisters)
						|| CollectionUtils.isNotEmpty(meaningDomains)
						|| CollectionUtils.isNotEmpty(meaningSemanticTypes)
						|| CollectionUtils.isNotEmpty(lexemeGrammars)
						|| CollectionUtils.isNotEmpty(lexeme.getLexemeFrequencies());
		lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
	}

}