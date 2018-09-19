package eki.ekilex.service;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import eki.ekilex.data.LexemeGroup;
import eki.ekilex.data.SourceLink;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FormRelation;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Service
public class LexSearchService implements SystemConstant {

	private final static String classifierLabelLang = "est";
	private final static String classifierLabelTypeDescrip = "descrip";
	private final static String classifierLabelTypeFull = "full";

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public WordsResult findWords(String searchFilter, List<String> datasets, boolean fetchAll) {

		List<Word> words;
		List<String> filteringDatasets;
		int wordCount;
		if (StringUtils.isBlank(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			int availableDatasetsCount = commonDataDbService.getDatasets().size();
			int selectedDatasetsCount = datasets.size();
			if (availableDatasetsCount == selectedDatasetsCount) {
				filteringDatasets = null;
			} else {
				filteringDatasets = new ArrayList<>(datasets);
			}
			words = lexSearchDbService.findWords(searchFilter, filteringDatasets, fetchAll).into(Word.class);
			wordCount = words.size();
			if (!fetchAll && wordCount == MAX_RESULTS_LIMIT) {
				wordCount = lexSearchDbService.countWords(searchFilter, filteringDatasets);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}

	@Transactional
	public WordsResult findWords(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) {

		List<Word> words;
		List<String> filteringDatasets;
		int wordCount;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			int availableDatasetsCount = commonDataDbService.getDatasets().size();
			int selectedDatasetsCount = datasets.size();
			if (availableDatasetsCount == selectedDatasetsCount) {
				filteringDatasets = null;
			} else {
				filteringDatasets = new ArrayList<>(datasets);
			}
			words = lexSearchDbService.findWords(searchFilter, filteringDatasets, fetchAll).into(Word.class);
			wordCount = words.size();
			if (!fetchAll && wordCount == MAX_RESULTS_LIMIT) {
				wordCount = lexSearchDbService.countWords(searchFilter, filteringDatasets);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}

	@Transactional
	public WordLexeme getWordLexeme(Long lexemeId) {

		WordLexeme lexeme = lexSearchDbService.findLexeme(lexemeId).into(WordLexeme.class);
		if (lexeme != null) {
			Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
			populateLexeme(singletonList(lexeme.getDatasetCode()), datasetNameMap, lexeme);
		}
		return lexeme;
	}

	@Transactional
	public List<WordLexeme> findWordLexemesWithMinimalData(String searchWord, List<String> selectedDatasets) {

		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchWord)) {
			String cleanedUpFilter = searchWord.replace("*", "").replace("?", "").replace("%", "").replace("_", "");
			WordsResult words = findWords(cleanedUpFilter, selectedDatasets, true);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.findWordLexemes(word.getWordId(), selectedDatasets).into(WordLexeme.class);
					wordLexemes.forEach(lexeme -> {
						Long meaningId = lexeme.getMeaningId();
						Long lexemeId = lexeme.getLexemeId();

						String datasetName = datasetNameMap.get(lexeme.getDatasetCode());
						List<Word> meaningWords = lexSearchDbService.findMeaningWords(lexeme.getWordId(), meaningId, selectedDatasets).into(Word.class);
						List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.findMeaningDefinitionRefTuples(meaningId).into(DefinitionRefTuple.class);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						List<Government> governments = commonDataDbService.findGovernments(lexemeId).into(Government.class);
						List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
								commonDataDbService.findUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
										.into(UsageTranslationDefinitionTuple.class);
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
	public WordDetails getWordDetails(Long wordId, List<String> selectedDatasets) {

		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = lexSearchDbService.findWordLexemes(wordId, selectedDatasets).into(WordLexeme.class);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.findParadigmFormTuples(wordId, classifierLabelLang, classifierLabelTypeDescrip).into(ParadigmFormTuple.class);
		List<FormRelation> wordFormRelations = lexSearchDbService.findWordFormRelations(wordId, classifierLabelLang, classifierLabelTypeFull).into(FormRelation.class);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples, wordFormRelations);
		List<Relation> wordRelations = lexSearchDbService.findWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
		List<WordEtym> wordEtymology = lexSearchDbService.findWordEtymology(wordId).into(WordEtym.class);

		lexemes.forEach(lexeme -> populateLexeme(selectedDatasets, datasetNameMap, lexeme));
		combineLevels(lexemes);

		return new WordDetails(d -> {
			d.setParadigms(paradigms);
			d.setLexemes(lexemes);
			d.setWordRelations(wordRelations);
			d.setWordEtymology(wordEtymology);
		});
	}

	@Transactional
	public List<Classifier> getAllLexemePos() {
		return commonDataDbService.getAllLexemePos(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordMorphCodes() {
		return commonDataDbService.getWordMorphCodes(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeRegisters() {
		return commonDataDbService.getLexemeRegisters(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeDerivs() {
		return commonDataDbService.getLexemeDerivs(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeGenders() {
		return commonDataDbService.getLexemeGenders(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	private void populateLexeme(List<String> selectedDatasets, Map<String, String> datasetNameMap, WordLexeme lexeme) {

		String datasetName = datasetNameMap.get(lexeme.getDatasetCode());
		lexeme.setDataset(datasetName);

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();

		List<String> vocalForms = lexeme.getVocalForms();
		vocalForms = cleanUpVocalForms(vocalForms);

		List<Word> meaningWords = lexSearchDbService.findMeaningWords(lexeme.getWordId(), meaningId, selectedDatasets).into(Word.class);
		List<Classifier> lexemePos = commonDataDbService.findLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
		List<Classifier> lexemeDerivs = commonDataDbService.findLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
		List<Classifier> lexemeRegisters = commonDataDbService.findLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
		List<Classifier> meaningDomains = commonDataDbService.findMeaningDomains(meaningId).into(Classifier.class);
		List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.findMeaningDefinitionRefTuples(meaningId).into(DefinitionRefTuple.class);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		List<FreeForm> meaningFreeforms = commonDataDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
		List<FreeForm> lexemeFreeforms = commonDataDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
		List<Government> governments = commonDataDbService.findGovernments(lexemeId).into(Government.class);
		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.findUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
						.into(UsageTranslationDefinitionTuple.class);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		List<Relation> lexemeRelations = lexSearchDbService.findLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
		List<Relation> meaningRelations = commonDataDbService.findMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip).into(Relation.class);
		List<FreeForm> lexemeGrammars = commonDataDbService.findLexemeGrammars(lexemeId).into(FreeForm.class);
		List<CollocationTuple> primaryCollocTuples = lexSearchDbService.findPrimaryCollocationTuples(lexemeId).into(CollocationTuple.class);
		List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
		List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.findSecondaryCollocationTuples(lexemeId).into(CollocationTuple.class);
		List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);
		List<SourceLink> lexemeSourceLinks = commonDataDbService.findLexemeSourceLinks(lexemeId).into(SourceLink.class);
		List<Relation> lexemeGroupMembers = lexSearchDbService.findLexemeGroupMembers(lexemeId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
		List<LexemeGroup> lexemeGroups = conversionUtil.composeLexemeGroups(lexemeGroupMembers);

		lexeme.setLexemePos(lexemePos);
		lexeme.setLexemeDerivs(lexemeDerivs);
		lexeme.setLexemeRegisters(lexemeRegisters);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setMeaningDomains(meaningDomains);
		lexeme.setDefinitions(definitions);
		lexeme.setMeaningFreeforms(meaningFreeforms);
		lexeme.setLexemeFreeforms(lexemeFreeforms);
		lexeme.setGovernments(governments);
		lexeme.setUsages(usages);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setMeaningRelations(meaningRelations);
		lexeme.setGrammars(lexemeGrammars);
		lexeme.setCollocationPosGroups(collocationPosGroups);
		lexeme.setSecondaryCollocations(secondaryCollocations);
		lexeme.setVocalForms(vocalForms);
		lexeme.setSourceLinks(lexemeSourceLinks);
		lexeme.setLexemeGroups(lexemeGroups);

		boolean lexemeOrMeaningClassifiersExist =
				StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
				|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
				|| StringUtils.isNotBlank(lexeme.getMeaningProcessStateCode())
				|| StringUtils.isNotBlank(lexeme.getGenderCode())
				|| CollectionUtils.isNotEmpty(lexemePos)
				|| CollectionUtils.isNotEmpty(lexemeDerivs)
				|| CollectionUtils.isNotEmpty(lexemeRegisters)
				|| CollectionUtils.isNotEmpty(meaningDomains)
				|| CollectionUtils.isNotEmpty(lexemeGrammars);
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
					.filter(otherLexeme ->
							otherLexeme.getLevel1().equals(lexeme.getLevel1())
							&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
					.count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				levels = String.valueOf(lexeme.getLevel1());
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(otherLexeme ->
								otherLexeme.getLevel1().equals(lexeme.getLevel1())
								&& otherLexeme.getLevel2().equals(lexeme.getLevel2())
								&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					int level2 = lexeme.getLevel2() - 1;
					levels = lexeme.getLevel1() + (level2 == 0 ? "" : "." + level2);
				} else {
					int level3 = lexeme.getLevel3() - 1;
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + (level3 == 0 ? "" : "." + level3);
				}
			}
			lexeme.setLevels(levels);
		});
	}

}