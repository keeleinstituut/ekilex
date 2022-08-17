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

import eki.common.constant.ActivityOwner;
import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.LexemeWordTuple;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordCandidates;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.TermCreateWordAndMeaningDetails;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class LookupService extends AbstractWordSearchService {

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public SimpleWord getLexemeSimpleWord(Long lexemeId) {
		return lookupDbService.getLexemeSimpleWord(lexemeId);
	}

	@Transactional
	public String getLexemeDatasetCode(Long lexemeId) {
		return lookupDbService.getLexemeDatasetCode(lexemeId);
	}

	@Transactional
	public List<Word> getWords(String wordValue, String language) {
		return lookupDbService.getWords(wordValue, language);
	}

	@Transactional
	public boolean wordExists(String wordValue, String language) {
		return lookupDbService.wordExists(wordValue, language);
	}

	@Transactional
	public boolean meaningHasWord(Long meaningId, String wordValue, String language) {
		return lookupDbService.meaningHasWord(meaningId, wordValue, language);
	}

	@Transactional
	public boolean isOtherDatasetOnlyWord(String wordValue, String language, String excludedDatasetCode) {

		List<Word> words = lookupDbService.getWords(wordValue, language);
		if (words.size() == 1) {
			Word word = words.get(0);
			List<String> wordDatasetCodes = word.getDatasetCodes();
			if (!wordDatasetCodes.contains(excludedDatasetCode)) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public boolean isValidWordStressAndMarkup(Long sourceWordId, Long targetWordId) {

		String sourceValuePrese = lookupDbService.getWordValuePrese(sourceWordId);
		String targetValuePrese = lookupDbService.getWordValuePrese(targetWordId);
		if (StringUtils.equals(sourceValuePrese, targetValuePrese)) {
			return true;
		}

		boolean isSourceWordDecorated = textDecorationService.isDecorated(sourceValuePrese);
		boolean isTargetWordDecorated = textDecorationService.isDecorated(targetValuePrese);
		if (isSourceWordDecorated && isTargetWordDecorated) {
			return false;
		}
		return true;
	}

	@Transactional
	public MeaningWordCandidates getMeaningWordCandidates(
			DatasetPermission userRole, String wordValue, String language, Long sourceMeaningId, List<String> tagNames) throws Exception {

		boolean meaningHasWord = lookupDbService.meaningHasWord(sourceMeaningId, wordValue, language);
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		WordsResult words = getWords(wordValue, Collections.emptyList(), userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, true);
		List<WordDescript> wordCandidates = new ArrayList<>();
		for (Word word : words.getWords()) {
			List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
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
				List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
				lexeme.setMeaningWords(meaningWords);
				List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
				permCalculator.filterVisibility(userRole, definitions);
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
	public List<WordDetails> getWordDetailsOfJoinCandidates(DatasetPermission userRole, Word targetWord, List<String> userPrefDatasetCodes, List<String> userVisibleDatasetCodes) {

		List<WordDetails> wordDetailsList = new ArrayList<>();
		Long userId = userRole.getUserId();
		Long targetWordId = targetWord.getWordId();
		List<Long> sourceWordIds = lookupDbService.getWordIdsOfJoinCandidates(targetWord, userPrefDatasetCodes, userVisibleDatasetCodes);
		sourceWordIds.sort(Comparator.comparing(sourceWordId -> !isWordJoinGranted(userId, userRole, sourceWordId, targetWordId)));

		for (Long sourceWordId : sourceWordIds) {
			WordDetails wordDetails = getWordJoinDetails(userRole, sourceWordId);
			wordDetailsList.add(wordDetails);
		}
		return wordDetailsList;
	}

	@Transactional
	public TermCreateWordAndMeaningDetails getDetailsForMeaningAndWordCreate(
			DatasetPermission userRole, String wordValue, String language, String datasetCode, boolean withCandidates) {

		List<WordDescript> wordCandidates;
		if (withCandidates) {
			wordCandidates = getWordCandidates(userRole, wordValue, language, datasetCode);
		} else {
			wordCandidates = new ArrayList<>();
		}

		TermCreateWordAndMeaningDetails details = new TermCreateWordAndMeaningDetails();
		details.setWordValue(wordValue);
		details.setLanguage(language);
		details.setDatasetCode(datasetCode);
		details.setWordCandidates(wordCandidates);

		return details;
	}

	@Transactional
	public TermCreateWordAndMeaningDetails getDetailsForWordCreate(
			DatasetPermission userRole, Long meaningId, String wordValue, String language, boolean withCandidates) {

		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		String datasetCode = lookupDbService.getMeaningFirstDatasetCode(meaningId);
		String meaningdatasetname = datasetNameMap.get(datasetCode);
		List<Classifier> datasetLanguages = commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Definition> meaningDefinitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.filterVisibility(userRole, meaningDefinitions);

		List<WordDescript> wordCandidates;
		if (withCandidates) {
			wordCandidates = getWordCandidates(userRole, wordValue, language, datasetCode);
		} else {
			wordCandidates = new ArrayList<>();
		}

		TermCreateWordAndMeaningDetails details = new TermCreateWordAndMeaningDetails();
		details.setMeaningId(meaningId);
		details.setDatasetCode(datasetCode);
		details.setDatasetName(meaningdatasetname);
		details.setDatasetLanguages(datasetLanguages);
		details.setMeaningDomains(meaningDomains);
		details.setMeaningDefinitions(meaningDefinitions);
		details.setWordValue(wordValue);
		details.setLanguage(language);
		details.setWordCandidates(wordCandidates);

		return details;
	}

	private List<WordDescript> getWordCandidates(DatasetPermission userRole, String wordValue, String language, String datasetCode) {

		List<WordDescript> wordCandidates = new ArrayList<>();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		List<Word> words = lookupDbService.getWords(wordValue, language);

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

			List<WordLexeme> mainDatasetLexemes = wordLexemes.stream()
					.filter(lexeme -> StringUtils.equalsAny(lexeme.getDatasetCode(), datasetCode, DATASET_EKI))
					.sorted(Comparator.comparing(lexeme -> !StringUtils.equals(lexeme.getDatasetCode(), datasetCode)))
					.collect(Collectors.toList());

			List<WordLexeme> secondaryDatasetLexemes = wordLexemes.stream()
					.filter(lexeme -> !StringUtils.equalsAny(lexeme.getDatasetCode(), datasetCode, DATASET_EKI))
					.collect(Collectors.toList());

			WordDescript wordCandidate = new WordDescript();
			wordCandidate.setWord(word);
			if (mainDatasetLexemes.isEmpty()) {
				wordCandidate.setMainDatasetLexemes(secondaryDatasetLexemes);
				wordCandidate.setSecondaryDatasetLexemes(new ArrayList<>());
			} else {
				boolean primaryDatasetLexemeExists = mainDatasetLexemes.get(0).getDatasetCode().equals(datasetCode);
				wordCandidate.setMainDatasetLexemes(mainDatasetLexemes);
				wordCandidate.setSecondaryDatasetLexemes(secondaryDatasetLexemes);
				wordCandidate.setPrimaryDatasetLexemeExists(primaryDatasetLexemeExists);
			}
			wordCandidates.add(wordCandidate);
		}

		return wordCandidates;
	}

	private boolean isWordJoinGranted(Long userId, DatasetPermission userRole, Long sourceWordId, Long targetWordId) {

		if (!permissionDbService.isGrantedForWord(userId, userRole, sourceWordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD)) {
			return false;
		}
		return isValidWordStressAndMarkup(sourceWordId, targetWordId);
	}

	@Transactional
	public WordDetails getWordJoinDetails(DatasetPermission userRole, Long wordId) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		Word word = lexSearchDbService.getWord(wordId);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);

		lexemes.forEach(lexeme -> populateLexemeWithMinimalData(userRole, lexeme));
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
	public List<WordLexeme> getWordLexemesOfJoinCandidates(
			DatasetPermission userRole, List<String> userPrefDatasetCodes,
			String searchWord, Integer wordHomonymNumber, Long excludedMeaningId, List<String> tagNames) throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userPrefDatasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchWord)) {
			String cleanedUpFilter = searchWord.replace(QUERY_MULTIPLE_CHARACTERS_SYM, "").replace(QUERY_SINGLE_CHARACTER_SYM, "").replace("%", "").replace("_", "");
			WordsResult words = getWords(cleanedUpFilter, userPrefDatasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, true);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
				for (Word word : words.getWords()) {
					if ((wordHomonymNumber != null) && !word.getHomonymNr().equals(wordHomonymNumber)) {
						continue;
					}
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
					wordLexemes.removeIf(lex -> lex.getMeaningId().equals(excludedMeaningId));
					wordLexemes.forEach(lexeme -> {
						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						String datasetName = datasetNameMap.get(datasetCode);
						List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
						List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
						permCalculator.filterVisibility(userRole, definitions);
						List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
						List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
								commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
						List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
						permCalculator.filterVisibility(userRole, usages);

						lexeme.setDatasetName(datasetName);
						lexeme.setMeaningWords(meaningWords);
						lexeme.setGovernments(governments);
						lexeme.setUsages(usages);
						Meaning meaning = new Meaning();
						meaning.setMeaningId(meaningId);
						meaning.setDefinitions(definitions);
						permCalculator.applyCrud(userRole, meaning);
						lexeme.setMeaning(meaning);
					});
					lexemeLevelPreseUtil.combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		lexemes.sort(Comparator.comparing(lexeme -> !lexeme.getMeaning().isAnyGrant()));
		return lexemes;
	}

	@Transactional
	public Meaning getMeaningOfJoinTarget(DatasetPermission userRole, Long meaningId, List<ClassifierSelect> languagesOrder) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		composeMeaningSelectData(userRole, meaning, languagesOrder);
		return meaning;
	}

	@Transactional
	public List<Meaning> getMeaningsOfJoinCandidates(
			DatasetPermission userRole, List<String> userPrefDatasetCodes, String searchFilter, List<ClassifierSelect> languagesOrder, Long excludedMeaningId) {

		if (StringUtils.isBlank(searchFilter)) {
			return Collections.emptyList();
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userPrefDatasetCodes);
			List<Meaning> meanings = lookupDbService.getMeanings(searchFilter, searchDatasetsRestriction, excludedMeaningId);
			permCalculator.applyCrud(userRole, meanings);
			meanings.sort(Comparator.comparing(meaning -> !meaning.isAnyGrant()));
			meanings.forEach(meaning -> composeMeaningSelectData(userRole, meaning, languagesOrder));
			return meanings;
		}
	}

	@Transactional
	public Map<String, Integer[]> getMeaningsWordsWithMultipleHomonymNumbers(List<Long> meaningIds) {
		return lookupDbService.getMeaningsWordsWithMultipleHomonymNumbers(meaningIds);
	}

	@Transactional
	public Long getMeaningId(Long lexemeId) {
		return lookupDbService.getLexemeMeaningId(lexemeId);
	}

	@Transactional
	public List<Classifier> getOppositeRelations(ActivityOwner owner, String relationTypeCode) {

		List<Classifier> oppositeRelations = new ArrayList<>();
		if (ActivityOwner.WORD.equals(owner)) {
			oppositeRelations = lookupDbService.getWordOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		} else if (ActivityOwner.LEXEME.equals(owner)) {
			oppositeRelations = lookupDbService.getLexemeOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		} else if (ActivityOwner.MEANING.equals(owner)) {
			oppositeRelations = lookupDbService.getMeaningOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		}
		return oppositeRelations;
	}

	private void composeMeaningSelectData(DatasetPermission userRole, Meaning meaning, List<ClassifierSelect> languagesOrder) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.NOTE.name()};
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Long meaningId = meaning.getMeaningId();

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.filterVisibility(userRole, definitions);
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);

		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();
		for (Long lexemeId : lexemeIds) {
			LexemeWordTuple lexemeWordTuple = termSearchDbService.getLexemeWordTuple(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			Lexeme lexeme = conversionUtil.composeLexeme(lexemeWordTuple);
			List<Classifier> wordTypes = commonDataDbService.getWordTypes(lexeme.getWordId(), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			permCalculator.filterVisibility(userRole, usages);
			List<SourceLink> lexemeRefLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			String dataset = lexeme.getDatasetCode();
			dataset = datasetNameMap.get(dataset);

			lexeme.setDatasetCode(dataset);
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

	private void populateLexemeWithMinimalData(DatasetPermission userRole, WordLexeme lexeme) {

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
	}

	private String getFirstDefinitionValue(List<WordLexeme> wordLexemes) {

		Optional<WordLexeme> wordLexemeWithDefinition = wordLexemes.stream()
				.filter(lex -> CollectionUtils.isNotEmpty(lex.getMeaning().getDefinitions()) && Objects.nonNull(lex.getMeaning().getDefinitions().get(0)))
				.findFirst();

		if (wordLexemeWithDefinition.isPresent()) {
			String wordFirstDefinitionValue = wordLexemeWithDefinition.get().getMeaning().getDefinitions().get(0).getValue();
			return wordFirstDefinitionValue;
		} else {
			return null;
		}
	}

}
