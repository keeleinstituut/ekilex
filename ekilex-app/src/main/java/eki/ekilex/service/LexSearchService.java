package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.Government;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class LexSearchService extends AbstractWordSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Transactional
	public List<WordLexeme> getWordLexemesWithMinimalData(String searchWord, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchWord)) {
			String cleanedUpFilter = searchWord.replace("*", "").replace("?", "").replace("%", "").replace("_", "");
			WordsResult words = getWords(cleanedUpFilter, selectedDatasetCodes, true);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction);
					wordLexemes.forEach(lexeme -> {
						Long meaningId = lexeme.getMeaningId();
						Long lexemeId = lexeme.getLexemeId();

						String datasetName = datasetNameMap.get(lexeme.getDatasetCode());
						List<Word> meaningWords = lexSearchDbService.getMeaningWords(lexeme.getWordId(), meaningId, searchDatasetsRestriction);
						List<DefinitionRefTuple> definitionRefTuples =
								commonDataDbService.getMeaningDefinitionRefTuples(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
						List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
						List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
						List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
								commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
						List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);

						lexeme.setDataset(datasetName);
						lexeme.setMeaningWords(meaningWords);
						lexeme.setDefinitions(definitions);
						lexeme.setGovernments(governments);
						lexeme.setUsages(usages);
					});
					lexemeLevelCalcUtil.combineLevels(wordLexemes);
					lexemes.addAll(wordLexemes);
				}
			}
		}
		return lexemes;
	}

	@Transactional
	public List<WordLexeme> getWordLexemesWithDefinitionsData(String searchFilter, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			WordsResult words = getWords(searchFilter, selectedDatasetCodes, false);
			if (CollectionUtils.isNotEmpty(words.getWords())) {
				for (Word word : words.getWords()) {
					List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(word.getWordId(), searchDatasetsRestriction);
					wordLexemes.forEach(lexeme -> {
						Long meaningId = lexeme.getMeaningId();
						List<Word> meaningWords = lexSearchDbService.getMeaningWords(lexeme.getWordId(), meaningId, searchDatasetsRestriction);
						List<DefinitionRefTuple> definitionRefTuples =
								commonDataDbService.getMeaningDefinitionRefTuples(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
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
		WordsResult words = getWords(searchFilter, datasets, true);
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
				Long meaningId = lexeme.getMeaningId();
				List<Word> meaningWords = lexSearchDbService.getMeaningWords(lexeme.getWordId(), meaningId, searchDatasetsRestriction);
				lexeme.setMeaningWords(meaningWords);
				List<DefinitionRefTuple> definitionRefTuples =
						commonDataDbService.getMeaningDefinitionRefTuples(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
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

}