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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityOwner;
import eki.common.constant.ClassifierName;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.Government;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordCandidates;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.TermCreateWordAndMeaningDetails;
import eki.ekilex.data.TermUpdateWordDetails;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordsResult;
import eki.ekilex.security.EkilexPermissionEvaluator;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class LookupService extends AbstractWordSearchService {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private PermCalculator permCalculator;

	@Autowired
	private EkilexPermissionEvaluator ekilexPermissionEvaluator;

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
	public boolean isOnlyValuePreseUpdate(Long wordId, String wordValuePrese) {

		String wordValue = textDecorationService.removeEkiElementMarkup(wordValuePrese);
		return lookupDbService.isOnlyValuePreseUpdate(wordId, wordValue, wordValuePrese);
	}

	@Transactional
	public MeaningWordCandidates getMeaningWordCandidates(
			EkiUser user, String wordValue, String language, Long sourceMeaningId, List<String> tagNames) throws Exception {

		boolean meaningHasWord = lookupDbService.meaningHasWord(sourceMeaningId, wordValue, language);
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		WordsResult words = getWords(wordValue, Collections.emptyList(), tagNames, user, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, true);
		List<WordDescript> wordCandidates = new ArrayList<>();

		for (Word word : words.getWords()) {

			if (!StringUtils.equals(language, word.getLang())) {
				continue;
			}
			List<Lexeme> lexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
			if (sourceMeaningId != null) {
				boolean lexemeAlreadyExists = lexemes.stream().anyMatch(lexeme -> sourceMeaningId.equals(lexeme.getMeaningId()));
				if (lexemeAlreadyExists) {
					continue;
				}
			}
			List<String> allDefinitionValues = new ArrayList<>();
			lexemes.forEach(lexeme -> {
				Long lexemeId = lexeme.getLexemeId();
				Long meaningId = lexeme.getMeaningId();
				String datasetCode = lexeme.getDatasetCode();
				List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
				lexeme.setMeaningWords(meaningWords);
				lexeme.setLexemeWord(word);
				List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
				permCalculator.filterVisibility(user, definitions);
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
	public List<WordDetails> getWordDetailsOfJoinCandidates(EkiUser user, Word targetWord, List<String> userPrefDatasetCodes, List<String> userVisibleDatasetCodes) {

		List<WordDetails> wordDetailsList = new ArrayList<>();
		Long targetWordId = targetWord.getWordId();
		List<Long> sourceWordIds = lookupDbService.getWordIdsOfJoinCandidates(targetWord, userPrefDatasetCodes, userVisibleDatasetCodes);
		sourceWordIds.sort(Comparator.comparing(sourceWordId -> !isWordJoinGranted(user, sourceWordId, targetWordId)));

		for (Long sourceWordId : sourceWordIds) {
			WordDetails wordDetails = getWordJoinDetails(user, sourceWordId);
			wordDetailsList.add(wordDetails);
		}
		return wordDetailsList;
	}

	@Transactional
	public TermCreateWordAndMeaningDetails getDetailsForMeaningAndWordCreate(
			EkiUser user, String wordValue, String language, String datasetCode, boolean withCandidates) {

		List<WordDescript> wordCandidates;
		if (withCandidates) {
			wordCandidates = getWordCandidates(wordValue, language, datasetCode, user);
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
			EkiUser user, Long meaningId, String wordValue, String language, boolean withCandidates) {

		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		String datasetCode = lookupDbService.getMeaningFirstDatasetCode(meaningId);
		String meaningdatasetname = datasetNameMap.get(datasetCode);
		List<Classifier> datasetLanguages = commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Definition> meaningDefinitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
		permCalculator.filterVisibility(user, meaningDefinitions);

		List<WordDescript> wordCandidates;
		if (withCandidates) {
			wordCandidates = getWordCandidates(wordValue, language, datasetCode, user);
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

	@Transactional
	public TermUpdateWordDetails getDetailsForWordUpdate(EkiUser user, Long lexemeId, String wordValuePrese, String language) {

		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		Long wordId = lookupDbService.getLexemeWordId(lexemeId);
		String originalWordValuePrese = lookupDbService.getWordValuePrese(wordId);
		String originalWordLang = lookupDbService.getWordLang(wordId);
		String wordValue = textDecorationService.removeEkiElementMarkup(wordValuePrese);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		String datasetCode = lookupDbService.getMeaningFirstDatasetCode(meaningId);
		String meaningdatasetname = datasetNameMap.get(datasetCode);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Definition> meaningDefinitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
		permCalculator.filterVisibility(user, meaningDefinitions);

		List<WordDescript> wordCandidates = getWordCandidates(wordValue, language, datasetCode, user);

		TermUpdateWordDetails details = new TermUpdateWordDetails();
		details.setLexemeId(lexemeId);
		details.setMeaningId(meaningId);
		details.setDatasetCode(datasetCode);
		details.setDatasetName(meaningdatasetname);
		details.setMeaningDomains(meaningDomains);
		details.setMeaningDefinitions(meaningDefinitions);
		details.setOriginalWordValuePrese(originalWordValuePrese);
		details.setOriginalWordLang(originalWordLang);
		details.setWordValuePrese(wordValuePrese);
		details.setWordLang(language);
		details.setWordCandidates(wordCandidates);

		return details;
	}

	private boolean isWordJoinGranted(EkiUser user, Long sourceWordId, Long targetWordId) {

		DatasetPermission userRole = user.getRecentRole();
		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, datasetCode, sourceWordId);
		if (!isWordCrudGrant) {
			return false;
		}
		return isValidWordStressAndMarkup(sourceWordId, targetWordId);
	}

	@Transactional
	public WordDetails getWordJoinDetails(EkiUser user, Long wordId) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		Word word = lexSearchDbService.getWord(wordId);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST);
		List<Lexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
		List<WordEtymTuple> wordEtymTuples = lexDataDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);

		lexemes.forEach(lexeme -> populateLexemeWithMinimalData(user, lexeme));
		lexemeLevelPreseUtil.combineLevels(lexemes);
		String firstDefinitionValue = getFirstDefinitionValue(lexemes);

		word.setWordTypes(wordTypes);
		word.setEtymology(wordEtymology);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setLexemes(lexemes);
		wordDetails.setFirstDefinitionValue(firstDefinitionValue);

		return wordDetails;
	}

	@Transactional
	public List<Lexeme> getWordLexemesOfJoinCandidates(
			EkiUser user, List<String> userPrefDatasetCodes, Lexeme targetLexeme, List<String> tagNames, String searchFilter) throws Exception {

		Word targetWord = targetLexeme.getLexemeWord();
		String targetWordValue = targetWord.getWordValue();
		Long excludedMeaningId = targetLexeme.getMeaningId();
		String preferredDatasetCode = targetLexeme.getDatasetCode();

		Integer targetHomonymNumber = null;
		if (StringUtils.equals(targetWordValue, searchFilter)) {
			targetHomonymNumber = targetWord.getHomonymNr();
		}

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userPrefDatasetCodes);
		List<Lexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {

			String cleanWordSearchFilter = StringUtils.trim(searchFilter);
			cleanWordSearchFilter = StringUtils.remove(cleanWordSearchFilter, SEARCH_MASK_CHARS);
			cleanWordSearchFilter = StringUtils.remove(cleanWordSearchFilter, SEARCH_MASK_CHAR);
			cleanWordSearchFilter = StringUtils.remove(cleanWordSearchFilter, '%');
			cleanWordSearchFilter = StringUtils.remove(cleanWordSearchFilter, '_');
			WordsResult wordsResult = getWords(cleanWordSearchFilter, userPrefDatasetCodes, tagNames, user, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, true);
			List<Word> words = wordsResult.getWords();

			if (CollectionUtils.isNotEmpty(words)) {

				for (Word word : words) {

					Long wordId = word.getWordId();
					Integer homonymNr = word.getHomonymNr();

					if ((targetHomonymNumber != null) && !homonymNr.equals(targetHomonymNumber)) {
						continue;
					}

					List<Lexeme> wordLexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
					wordLexemes.removeIf(lex -> lex.getMeaningId().equals(excludedMeaningId));
					wordLexemes.forEach(lexeme -> {

						Long lexemeId = lexeme.getLexemeId();
						Long meaningId = lexeme.getMeaningId();
						String datasetCode = lexeme.getDatasetCode();
						List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
						List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
						List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
						List<Usage> usages = lexeme.getUsages();
						permCalculator.filterVisibility(user, definitions);
						permCalculator.filterVisibility(user, usages);

						Meaning meaning = new Meaning();
						meaning.setMeaningId(meaningId);
						meaning.setDefinitions(definitions);
						permCalculator.applyCrud(user, meaning);

						lexeme.setMeaningWords(meaningWords);
						lexeme.setGovernments(governments);
						lexeme.setMeaning(meaning);
						lexeme.setLexemeWord(word);
					});
					lexemeLevelPreseUtil.combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		lexemes.sort(Comparator.comparing(lexeme -> !StringUtils.equals(lexeme.getDatasetCode(), preferredDatasetCode)));
		return lexemes;
	}

	@Transactional
	public Meaning getMeaningOfJoinTarget(EkiUser user, Long meaningId, List<ClassifierSelect> languagesOrder) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		composeMeaningSelectData(user, meaning, languagesOrder);
		return meaning;
	}

	@Transactional
	public List<Meaning> getMeaningsOfJoinCandidates(
			EkiUser user, List<String> userPrefDatasetCodes, String searchFilter, List<ClassifierSelect> languagesOrder, Long excludedMeaningId) {

		if (StringUtils.isBlank(searchFilter)) {
			return Collections.emptyList();
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userPrefDatasetCodes);
			List<Meaning> meanings = lookupDbService.getMeanings(searchFilter, searchDatasetsRestriction, excludedMeaningId);
			permCalculator.applyCrud(user, meanings);
			meanings.sort(Comparator.comparing(meaning -> !meaning.isAnyGrant()));
			meanings.forEach(meaning -> composeMeaningSelectData(user, meaning, languagesOrder));
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
	public Long getWordId(Long lexemeId) {
		return lookupDbService.getLexemeWordId(lexemeId);
	}

	@Transactional
	public WordLexemeMeaningIdTuple getWordLexemeMeaningId(Long lexemeId) {
		return lookupDbService.getWordLexemeMeaningIdByLexeme(lexemeId);
	}

	@Transactional
	public List<Classifier> getOppositeRelations(ActivityOwner owner, String relationTypeCode) {

		List<Classifier> oppositeRelations = new ArrayList<>();
		if (ActivityOwner.WORD.equals(owner)) {
			oppositeRelations = lookupDbService.getWordOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST);
		} else if (ActivityOwner.LEXEME.equals(owner)) {
			oppositeRelations = lookupDbService.getLexemeOppositeRelationTypes(relationTypeCode, CLASSIF_LABEL_LANG_EST);
		} else if (ActivityOwner.MEANING.equals(owner)) {
			oppositeRelations = lookupDbService.getMeaningOppositeRelations(relationTypeCode, CLASSIF_LABEL_LANG_EST);
		}
		return oppositeRelations;
	}

	private void composeMeaningSelectData(EkiUser user, Meaning meaning, List<ClassifierSelect> languagesOrder) {

		Long meaningId = meaning.getMeaningId();

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST);
		permCalculator.filterVisibility(user, definitions);
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Freeform> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, CLASSIF_LABEL_LANG_EST);

		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();

		for (Long lexemeId : lexemeIds) {

			Lexeme lexeme = termSearchDbService.getLexeme(lexemeId, CLASSIF_LABEL_LANG_EST);
			Long wordId = lexeme.getWordId();
			Word word = termSearchDbService.getWord(wordId);
			lexeme.setLexemeWord(word);
			List<Usage> usages = lexeme.getUsages();
			permCalculator.filterVisibility(user, usages);
			lexemes.add(lexeme);
		}
		List<LexemeLangGroup> lexemeLangGroups = conversionUtil.composeLexemeLangGroups(lexemes, languagesOrder);

		meaning.setDefinitionLangGroups(definitionLangGroups);
		meaning.setDomains(domains);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setLexemeLangGroups(lexemeLangGroups);
	}

	private void populateLexemeWithMinimalData(EkiUser user, Lexeme lexeme) {

		Long lexemeId = lexeme.getLexemeId();
		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		Word word = lexSearchDbService.getWord(wordId);
		List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
		Meaning meaning = new Meaning();
		meaning.setDefinitions(definitions);
		permCalculator.filterVisibility(user, definitions);

		lexeme.setMeaningWords(meaningWords);
		lexeme.setMeaning(meaning);
		lexeme.setLexemeWord(word);
	}

	private String getFirstDefinitionValue(List<Lexeme> wordLexemes) {

		Optional<Lexeme> wordLexemeWithDefinition = wordLexemes.stream()
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
