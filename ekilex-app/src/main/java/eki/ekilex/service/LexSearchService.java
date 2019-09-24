package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.Government;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class LexSearchService extends AbstractWordSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private PermissionDbService permissionDbService;

	@Transactional
	public List<WordLexeme> getWordLexemesOfJoinCandidates(String searchWord, List<String> userPrefDatasetCodes, Optional<Integer> wordHomonymNumber,
			Long excludedMeaningId) {

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
						List<DefinitionRefTuple> definitionRefTuples =
								commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
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
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();
		lexemes.sort(Comparator.comparing(lexeme -> !permissionDbService.isGrantedForMeaning(lexeme.getMeaningId(), userPermDatasetCodes)));
		return lexemes;
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
	public Long getMeaningId(Long lexemeId) {
		return lexSearchDbService.getMeaningId(lexemeId);
	}
}