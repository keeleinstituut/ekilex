package eki.ekilex.service;

import static java.lang.Math.max;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.constant.FreeformType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
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

@Service
public class LexSearchService extends AbstractSearchService {

	private final static String classifierLabelLang = "est";
	private final static String classifierLabelTypeDescrip = "descrip";
	private final static String classifierLabelTypeFull = "full";

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Transactional
	public WordsResult findWords(String searchFilter, List<String> selectedDatasetCodes, boolean fetchAll) {

		List<Word> words;
		int wordCount;
		if (StringUtils.isBlank(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			words = lexSearchDbService.findWords(searchFilter, searchDatasetsRestriction, fetchAll);
			wordCount = words.size();
			if (!fetchAll && wordCount == MAX_RESULTS_LIMIT) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}

	@Transactional
	public WordsResult findWords(SearchFilter searchFilter, List<String> selectedDatasetCodes, boolean fetchAll) {

		List<Word> words;
		int wordCount;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			words = lexSearchDbService.findWords(searchFilter, searchDatasetsRestriction, fetchAll);
			wordCount = words.size();
			if (!fetchAll && wordCount == MAX_RESULTS_LIMIT) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}

	@Transactional
	public WordDetails getWordDetails(Long wordId, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Word word = commonDataDbService.getWord(wordId);
		List<Classifier> wordTypes = commonDataDbService.findWordTypes(wordId, classifierLabelLang, classifierLabelTypeDescrip);
		List<WordLexeme> lexemes = lexSearchDbService.findWordLexemes(wordId, searchDatasetsRestriction);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.findParadigmFormTuples(wordId, word.getValue(), classifierLabelLang, classifierLabelTypeDescrip);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		List<Relation> wordRelations = lexSearchDbService.findWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.findWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<Relation> wordGroupMembers = lexSearchDbService.findWordGroupMembers(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers);

		lexemes.forEach(lexeme -> populateLexeme(lexeme, searchDatasetsRestriction, datasetNameMap));
		combineLevels(lexemes);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setWordClass(word.getWordClass());
		wordDetails.setWordGenderCode(word.getGenderCode());
		wordDetails.setWordAspectCode(word.getAspectCode());
		wordDetails.setParadigms(paradigms);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordRelations(wordRelations);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setWordGroups(wordGroups);

		return wordDetails;
	}

	@Transactional
	public WordLexeme getWordLexeme(Long lexemeId) {

		WordLexeme lexeme = lexSearchDbService.findLexeme(lexemeId);
		if (lexeme != null) {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(singletonList(lexeme.getDatasetCode()));
			Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
			populateLexeme(lexeme, searchDatasetsRestriction, datasetNameMap);
		}
		return lexeme;
	}

	@Transactional
	public List<WordLexeme> findWordLexemesWithMinimalData(String searchWord, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchWord)) {
			String cleanedUpFilter = searchWord.replace("*", "").replace("?", "").replace("%", "").replace("_", "");
			WordsResult words = findWords(cleanedUpFilter, selectedDatasetCodes, true);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.findWordLexemes(word.getWordId(), searchDatasetsRestriction);
					wordLexemes.forEach(lexeme -> {
						Long meaningId = lexeme.getMeaningId();
						Long lexemeId = lexeme.getLexemeId();

						String datasetName = datasetNameMap.get(lexeme.getDatasetCode());
						List<Word> meaningWords = lexSearchDbService.findMeaningWords(lexeme.getWordId(), meaningId, searchDatasetsRestriction);
						List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.findMeaningDefinitionRefTuples(meaningId);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						List<Government> governments = commonDataDbService.findGovernments(lexemeId);
						List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
								commonDataDbService.findUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
						List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);

						lexeme.setDataset(datasetName);
						lexeme.setMeaningWords(meaningWords);
						lexeme.setDefinitions(definitions);
						lexeme.setGovernments(governments);
						lexeme.setUsages(usages);
					});
					combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		return lexemes;
	}

	@Transactional
	public List<WordLexeme> findWordLexemesWithDefinitionsData(String searchFilter, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			WordsResult words = findWords(searchFilter, selectedDatasetCodes, false);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.findWordLexemes(word.getWordId(), searchDatasetsRestriction);
					wordLexemes.forEach(lexeme -> {
						Long meaningId = lexeme.getMeaningId();
						List<Word> meaningWords = lexSearchDbService.findMeaningWords(lexeme.getWordId(), meaningId, searchDatasetsRestriction);
						List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.findMeaningDefinitionRefTuples(meaningId);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						lexeme.setMeaningWords(meaningWords);
						lexeme.setDefinitions(definitions);
					});
					combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		return lexemes;
	}

	@Transactional
	public boolean isTheOnlyLexemeForWord(Long lexemeId) {
		return lexSearchDbService.isOnlyLexemeForWord(lexemeId);
	}

	@Transactional
	public boolean isTheOnlyLexemeForMeaning(Long lexemeId) {
		return lexSearchDbService.isTheOnlyLexemeForMeaning(lexemeId);
	}

	private void populateLexeme(WordLexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction, Map<String, String> datasetNameMap) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(), FreeformType.PUBLIC_NOTE.name()};

		String datasetName = datasetNameMap.get(lexeme.getDatasetCode());
		lexeme.setDataset(datasetName);

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();

		List<String> vocalForms = lexeme.getVocalForms();
		vocalForms = cleanUpVocalForms(vocalForms);

		List<Word> meaningWords = lexSearchDbService.findMeaningWords(lexeme.getWordId(), meaningId, searchDatasetsRestriction);
		List<Classifier> lexemePos = commonDataDbService.findLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> lexemeDerivs = commonDataDbService.findLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> lexemeRegisters = commonDataDbService.findLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> meaningDomains = commonDataDbService.findMeaningDomains(meaningId);
		List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.findMeaningDefinitionRefTuples(meaningId);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		List<FreeForm> meaningFreeforms = commonDataDbService.findMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<FreeForm> meaningLearnerComments = commonDataDbService.findMeaningLearnerComments(meaningId);
		List<FreeForm> lexemeFreeforms = commonDataDbService.findLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
		List<FreeForm> lexemePublicNotes = commonDataDbService.findLexemePublicNotes(lexemeId);
		List<Government> governments = commonDataDbService.findGovernments(lexemeId);
		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.findUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		List<Relation> lexemeRelations = commonDataDbService.findLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull);
		List<Relation> meaningRelations = commonDataDbService.findMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
		List<List<Relation>> groupedMeaningRelations = conversionUtil.groupRelationsById(meaningRelations);
		List<FreeForm> lexemeGrammars = commonDataDbService.findGrammars(lexemeId);
		List<CollocationTuple> primaryCollocTuples = lexSearchDbService.findPrimaryCollocationTuples(lexemeId);
		List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
		List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.findSecondaryCollocationTuples(lexemeId);
		List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);
		List<SourceLink> lexemeSourceLinks = commonDataDbService.findLexemeSourceLinks(lexemeId);

		lexeme.setPos(lexemePos);
		lexeme.setDerivs(lexemeDerivs);
		lexeme.setRegisters(lexemeRegisters);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setMeaningDomains(meaningDomains);
		lexeme.setDefinitions(definitions);
		lexeme.setMeaningFreeforms(meaningFreeforms);
		lexeme.setMeaningLearnerComments(meaningLearnerComments);
		lexeme.setLexemeFreeforms(lexemeFreeforms);
		lexeme.setLexemePublicNotes(lexemePublicNotes);
		lexeme.setGovernments(governments);
		lexeme.setUsages(usages);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setMeaningRelations(meaningRelations);
		lexeme.setGrammars(lexemeGrammars);
		lexeme.setCollocationPosGroups(collocationPosGroups);
		lexeme.setSecondaryCollocations(secondaryCollocations);
		lexeme.setVocalForms(vocalForms);
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
				|| CollectionUtils.isNotEmpty(lexemeGrammars)
				|| CollectionUtils.isNotEmpty(lexeme.getLexemeFrequencies());
		lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
	}

	private List<String> cleanUpVocalForms(List<String> vocalForms) {
		return vocalForms.stream().filter(Objects::nonNull).collect(toList());
	}

	private void combineLevels(List<WordLexeme> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		lexemes.forEach(lexeme -> {
			if (lexeme.getLevel1() == 0) {
				lexeme.setLevels(null);
				return;
			}
			String levels;
			long nrOfLexemesWithSameLevel1 = lexemes.stream()
					.filter(otherLexeme -> otherLexeme.getLevel1().equals(lexeme.getLevel1())
							&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
					.count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				levels = String.valueOf(lexeme.getLevel1());
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(otherLexeme -> otherLexeme.getLevel1().equals(lexeme.getLevel1())
								&& otherLexeme.getLevel2().equals(lexeme.getLevel2())
								&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					int level2 = max(lexeme.getLevel2() - 1, 0);
					levels = lexeme.getLevel1() + (level2 == 0 ? "" : "." + level2);
				} else {
					int level3 = max(lexeme.getLevel3() - 1, 0);
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + (level3 == 0 ? "" : "." + level3);
				}
			}
			lexeme.setLevels(levels);
		});
	}

}