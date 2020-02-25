package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.LexemeType;
import eki.common.constant.LifecycleEntity;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordCandidates;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.TermSearchDbService;

@Component
public class LookupService extends AbstractWordSearchService {

	private final static String classifierLabelLang = "est";
	private final static String classifierLabelTypeDescrip = "descrip";

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Transactional
	public boolean meaningHasWord(Long meaningId, String wordValue, String language) {
		return lookupDbService.meaningHasWord(meaningId, wordValue, language);
	}

	@Transactional
	public MeaningWordCandidates getMeaningWordCandidates(Long sourceMeaningId, String wordValue, String language, List<String> datasets) {

		boolean meaningHasWord = lookupDbService.meaningHasWord(sourceMeaningId, wordValue, language);
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		WordsResult words = getWords(wordValue, datasets, true, DEFAULT_OFFSET);
		List<WordDescript> wordCandidates = new ArrayList<>();
		for (Word word : words.getWords()) {
			List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction);
			boolean lexemeAlreadyExists = false;
			if (sourceMeaningId != null) {
				lexemeAlreadyExists = lexemes.stream().anyMatch(lexeme -> lexeme.getMeaningId().equals(sourceMeaningId));
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
				List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, lexeme.getWordLang());
				lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
				List<DefinitionRefTuple> definitionRefTuples =
						commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
				List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
				List<String> lexemeDefinitionValues = definitions.stream().map(def -> def.getValue()).collect(Collectors.toList());
				allDefinitionValues.addAll(lexemeDefinitionValues);
			});
			List<String> distinctDefinitionValues = allDefinitionValues.stream().distinct().collect(Collectors.toList());
			WordDescript wordCandidate = new WordDescript();
			wordCandidate.setWord(word);
			wordCandidate.setLexemes(lexemes);
			wordCandidate.setDefinitions(distinctDefinitionValues);
			wordCandidates.add(wordCandidate);
		}
		boolean wordCandidatesExist = CollectionUtils.isNotEmpty(wordCandidates);
		MeaningWordCandidates meaningWordCandidates = new MeaningWordCandidates();
		meaningWordCandidates.setWordValue(wordValue);
		meaningWordCandidates.setMeaningHasWord(meaningHasWord);
		meaningWordCandidates.setWordCandidates(wordCandidates);
		meaningWordCandidates.setWordCandidatesExist(wordCandidatesExist);
		return meaningWordCandidates;
	}

	@Transactional
	public List<WordDetails> getWordDetailsOfJoinCandidates(Word targetWord, String roleDatasetCode, List<String> userPrefDatasetCodes, List<String> userPermDatasetCodes) {

		List<WordDetails> wordDetailsList = new ArrayList<>();
		List<Long> wordIds = lookupDbService.getWordIdsOfJoinCandidates(targetWord, userPrefDatasetCodes, userPermDatasetCodes);
		wordIds.sort(Comparator.comparing(wordId -> !permissionDbService.isGrantedForWord(wordId, roleDatasetCode, userPermDatasetCodes)));

		for (Long wordId : wordIds) {
			WordDetails wordDetails = getWordJoinDetails(wordId);
			wordDetailsList.add(wordDetails);
		}
		return wordDetailsList;
	}

	@Transactional
	public WordDetails getWordJoinDetails(Long wordId) {

		List<String> allDatasetCodes = getAllDatasetCodes();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(allDatasetCodes);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Word word = lexSearchDbService.getWord(wordId);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, classifierLabelLang, classifierLabelTypeDescrip);
		conversionUtil.setWordTypeFlags(word, wordTypes);
		List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);

		lexemes.forEach(lexeme -> populateLexemeWithMinimalData(lexeme, datasetNameMap));
		lexemeLevelPreseUtil.combineLevels(lexemes);
		String firstDefinitionValue = getFirstDefinitionValue(lexemes);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setFirstDefinitionValue(firstDefinitionValue);

		return wordDetails;
	}

	@Transactional
	public List<WordLexeme> getWordLexemesOfJoinCandidates(String searchWord, List<String> userPrefDatasetCodes, Optional<Integer> wordHomonymNumber,
			Long excludedMeaningId, Long userId) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userPrefDatasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchWord)) {
			String cleanedUpFilter = searchWord.replace("*", "").replace("?", "").replace("%", "").replace("_", "");
			WordsResult words = getWords(cleanedUpFilter, userPrefDatasetCodes, true, DEFAULT_OFFSET);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
				for (Word word : words.getWords()) {
					if (wordHomonymNumber.isPresent()) {
						if (!word.getHomonymNumber().equals(wordHomonymNumber.get())) {
							continue;
						}
					}
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction);
					wordLexemes.removeIf(lex -> lex.getMeaningId().equals(excludedMeaningId));
					wordLexemes.forEach(lexeme -> {
						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						String datasetName = datasetNameMap.get(datasetCode);
						List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
						List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, lexeme.getWordLang());
						List<DefinitionRefTuple> definitionRefTuples =
								commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
						List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
								commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
						List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);

						lexeme.setDataset(datasetName);
						lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
						lexeme.setDefinitions(definitions);
						lexeme.setGovernments(governments);
						lexeme.setUsages(usages);
					});
					lexemeLevelPreseUtil.combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		lexemes.sort(Comparator.comparing(lexeme -> !permissionDbService.isMeaningAnyLexemeCrudGranted(userId, lexeme.getMeaningId())));
		return lexemes;
	}

	@Transactional
	public Meaning getMeaningOfJoinTarget(Long meaningId, List<ClassifierSelect> languagesOrder) {

		List<Dataset> allDatasets = commonDataDbService.getDatasets();
		List<String> allDatasetCodes = allDatasets.stream().map(Dataset::getCode).collect(Collectors.toList());

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(allDatasetCodes);
		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		composeMeaningSelectData(meaning, languagesOrder);
		return meaning;
	}

	@Transactional
	public List<Meaning> getMeaningsOfJoinCandidates(String searchFilter, List<String> userPrefDatasetCodes, List<String> userPermDatasetCodes,
			List<ClassifierSelect> languagesOrder, Long excludedMeaningId, Long userId) {

		if (StringUtils.isBlank(searchFilter)) {
			return Collections.emptyList();
		} else {
			List<Meaning> meanings = lookupDbService.getMeanings(searchFilter, userPrefDatasetCodes, userPermDatasetCodes, excludedMeaningId);
			meanings.sort(Comparator.comparing(meaning -> !permissionDbService.isMeaningAnyLexemeCrudGranted(userId, meaning.getMeaningId())));
			meanings.forEach(meaning -> composeMeaningSelectData(meaning, languagesOrder));
			return meanings;
		}
	}

	@Transactional
	public List<Meaning> getMeaningsOfRelationCandidates(Long excludedMeaningId, String wordValue, List<String> userPermDatasetCodes, List<ClassifierSelect> languagesOrder) {

		List<String> allDatasetCodes = getAllDatasetCodes();

		if (StringUtils.isBlank(wordValue)) {
			return Collections.emptyList();
		} else {
			List<Meaning> meanings = lookupDbService.getMeanings(wordValue, allDatasetCodes, userPermDatasetCodes, excludedMeaningId);
			meanings.forEach(meaning -> composeMeaningSelectData(meaning, languagesOrder));
			return meanings;
		}
	}

	@Transactional
	public Map<String, Integer[]> getMeaningsWordsWithMultipleHomonymNumbers(List<Long> meaningIds) {
		return lookupDbService.getMeaningsWordsWithMultipleHomonymNumbers(meaningIds);
	}

	@Transactional
	public Long getMeaningId(Long lexemeId) {
		return lookupDbService.getMeaningId(lexemeId);
	}

	@Transactional
	public List<Long> getWordIdsToBeDeleted(Long meaningId, String datasetCode) {

		List<Long> wordIdsToBeDeleted = new ArrayList<>();
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			Long wordId = wordLexemeMeaningId.getWordId();
			boolean isOnlyPrimaryLexemeForWord = lookupDbService.isOnlyPrimaryLexemeForWord(lexemeId);
			if (isOnlyPrimaryLexemeForWord) {
				wordIdsToBeDeleted.add(wordId);
			}
		}
		return wordIdsToBeDeleted;
	}

	@Transactional
	public List<String> getWordsValues(List<Long> wordIds) {
		return lookupDbService.getWordsValues(wordIds);
	}

	@Transactional
	public List<Classifier> getOppositeRelations(LifecycleEntity entity, String relationTypeCode) {

		List<Classifier> oppositeRelations = new ArrayList<>();
		if (LifecycleEntity.WORD.equals(entity)) {
			oppositeRelations = lookupDbService.getWordOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		} else if (LifecycleEntity.LEXEME.equals(entity)) {
			oppositeRelations = lookupDbService.getLexemeOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		} else if (LifecycleEntity.MEANING.equals(entity)) {
			oppositeRelations = lookupDbService.getMeaningOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		}
		return oppositeRelations;
	}

	@Transactional
	public LexemeType getLexemeType(Long lexemeId) {
		return lookupDbService.getLexemeType(lexemeId);
	}

	private void composeMeaningSelectData(Meaning meaning, List<ClassifierSelect> languagesOrder) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.PUBLIC_NOTE.name()};
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Long meaningId = meaning.getMeaningId();

		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId);
		domains = conversionUtil.removeOrderedClassifierDuplicates(domains);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);

		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();
		for (Long lexemeId : lexemeIds) {
			Lexeme lexeme = termSearchDbService.getLexeme(lexemeId);
			List<Classifier> wordTypes = commonDataDbService.getWordTypes(lexeme.getWordId(), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			List<SourceLink> lexemeRefLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			String dataset = lexeme.getDataset();
			dataset = datasetNameMap.get(dataset);

			lexeme.setDataset(dataset);
			lexeme.setWordTypes(wordTypes);
			lexeme.setUsages(usages);
			lexeme.setSourceLinks(lexemeRefLinks);
			lexemes.add(lexeme);
		}
		List<LexemeLangGroup> lexemeLangGroups = conversionUtil.composeLexemeLangGroups(lexemes, languagesOrder);

		meaning.setDefinitionLangGroups(definitionLangGroups);
		meaning.setDomains(domains);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setLexemeLangGroups(lexemeLangGroups);
	}

	private void populateLexemeWithMinimalData(WordLexeme lexeme, Map<String, String> datasetNameMap) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		String datasetName = datasetNameMap.get(datasetCode);
		List<MeaningWord> meaningWords = lexSearchDbService.getMeaningWords(lexemeId);
		List<MeaningWordLangGroup> meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, lexeme.getWordLang());
		List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);

		lexeme.setDataset(datasetName);
		lexeme.setPos(lexemePos);
		lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
		lexeme.setDefinitions(definitions);
	}

	private List<String> getAllDatasetCodes() {

		List<Dataset> allDatasets = commonDataDbService.getDatasets();
		List<String> allDatasetCodes = allDatasets.stream().map(Dataset::getCode).collect(Collectors.toList());
		return allDatasetCodes;
	}

	private String getFirstDefinitionValue(List<WordLexeme> wordLexemes) {

		Optional<WordLexeme> wordLexemeWithDefinition = wordLexemes.stream()
				.filter(lex -> CollectionUtils.isNotEmpty(lex.getDefinitions()) && Objects.nonNull(lex.getDefinitions().get(0)))
				.findFirst();

		if (wordLexemeWithDefinition.isPresent()) {
			String wordFirstDefinitionValue = wordLexemeWithDefinition.get().getDefinitions().get(0).getValue();
			return wordFirstDefinitionValue;
		} else {
			return null;
		}
	}

	@Transactional
	public boolean isOnlyLexemeForWord(Long lexemeId) {
		return lookupDbService.isOnlyLexemeForWord(lexemeId);
	}

	@Transactional
	public boolean isOnlyLexemeForMeaning(Long lexemeId) {
		return lookupDbService.isOnlyLexemeForMeaning(lexemeId);
	}

	@Transactional
	public boolean isOnlyPrimaryLexemeForWord(Long lexemeId) {
		return lookupDbService.isOnlyPrimaryLexemeForWord(lexemeId);
	}

	@Transactional
	public boolean isOnlyPrimaryLexemeForMeaning(Long lexemeId) {
		return lookupDbService.isOnlyPrimaryLexemeForMeaning(lexemeId);
	}

	@Transactional
	public boolean isOnlyLexemesForMeaning(Long meaningId, String datasetCode) {
		return lookupDbService.isOnlyLexemesForMeaning(meaningId, datasetCode);
	}

	@Transactional
	public boolean isOnlyPrimaryLexemesForWords(Long meaningId, String datasetCode) {
		return lookupDbService.isOnlyPrimaryLexemesForWords(meaningId, datasetCode);
	}

	@Transactional
	public boolean secondaryMeaningLexemeExists(Long meaningId, String datasetCode) {
		return lookupDbService.secondaryMeaningLexemeExists(meaningId, datasetCode);
	}

	@Transactional
	public boolean secondaryWordLexemeExists(List<Long> wordIds, String datasetCode) {
		return lookupDbService.secondaryWordLexemeExists(wordIds, datasetCode);
	}
}
