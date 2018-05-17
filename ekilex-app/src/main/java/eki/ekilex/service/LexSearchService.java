package eki.ekilex.service;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

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
import eki.ekilex.data.GovernmentUsageTranslationDefinitionTuple;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
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
	public WordsResult findWords(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) {

		List<Word> words = lexSearchDbService.findWords(searchFilter, datasets, fetchAll).into(Word.class);
		int wordCount = words.size();
		if (!fetchAll && wordCount == MAX_RESULTS_LIMIT) {
			wordCount = lexSearchDbService.countWords(searchFilter, datasets);
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}

	@Transactional
	public WordsResult findWords(String searchFilter, List<String> datasets, boolean fetchAll) {

		List<Word> words = lexSearchDbService.findWords(searchFilter, datasets, fetchAll).into(Word.class);
		int wordCount = words.size();
		if (!fetchAll && wordCount == MAX_RESULTS_LIMIT) {
			wordCount = lexSearchDbService.countWords(searchFilter, datasets);
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
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples, null);
						List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples =
								commonDataDbService.findGovernmentUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
										.into(GovernmentUsageTranslationDefinitionTuple.class);
						List<Government> governments = conversionUtil.composeGovernments(governmentUsageTranslationDefinitionTuples);

						lexeme.setDataset(datasetName);
						lexeme.setMeaningWords(meaningWords);
						lexeme.setDefinitions(definitions);
						lexeme.setGovernments(governments);
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

		lexemes.forEach(lexeme -> populateLexeme(selectedDatasets, datasetNameMap, lexeme));
		combineLevels(lexemes);

		return new WordDetails(d -> {
			d.setParadigms(paradigms);
			d.setLexemes(lexemes);
			d.setWordRelations(wordRelations);
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
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples, null);
		List<FreeForm> meaningFreeforms = commonDataDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
		List<FreeForm> lexemeFreeforms = commonDataDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
		List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples =
				commonDataDbService.findGovernmentUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
						.into(GovernmentUsageTranslationDefinitionTuple.class);
		List<Government> governments = conversionUtil.composeGovernments(governmentUsageTranslationDefinitionTuples);
		List<Relation> lexemeRelations = lexSearchDbService.findLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
		List<Relation> meaningRelations = commonDataDbService.findMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip).into(Relation.class);
		List<String> lexemeGrammars = commonDataDbService.findLexemeGrammars(lexemeId).into(String.class);
		List<CollocationTuple> primaryCollocTuples = lexSearchDbService.findPrimaryCollocationTuples(lexemeId).into(CollocationTuple.class);
		List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
		List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.findSecondaryCollocationTuples(lexemeId).into(CollocationTuple.class);
		List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);

		lexeme.setLexemePos(lexemePos);
		lexeme.setLexemeDerivs(lexemeDerivs);
		lexeme.setLexemeRegisters(lexemeRegisters);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setMeaningDomains(meaningDomains);
		lexeme.setDefinitions(definitions);
		lexeme.setMeaningFreeforms(meaningFreeforms);
		lexeme.setLexemeFreeforms(lexemeFreeforms);
		lexeme.setGovernments(governments);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setMeaningRelations(meaningRelations);
		lexeme.setGrammars(lexemeGrammars);
		lexeme.setCollocationPosGroups(collocationPosGroups);
		lexeme.setSecondaryCollocations(secondaryCollocations);
		lexeme.setVocalForms(vocalForms);

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
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2();
				} else {
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + "." + lexeme.getLevel3();
				}
			}
			lexeme.setLevels(levels);
		});
	}

}