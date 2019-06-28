package eki.ekilex.service;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;

@Component
public abstract class AbstractWordSearchService extends AbstractSearchService {

	protected final static String classifierLabelLang = "est";
	protected final static String classifierLabelTypeDescrip = "descrip";
	protected final static String classifierLabelTypeFull = "full";

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Transactional
	public WordsResult getWords(SearchFilter searchFilter, List<String> selectedDatasetCodes, boolean fetchAll) throws Exception {

		List<Word> words;
		int wordCount;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, fetchAll);
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
	public WordsResult getWords(String searchFilter, List<String> selectedDatasetCodes, boolean fetchAll) {

		List<Word> words;
		int wordCount;
		if (StringUtils.isBlank(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, fetchAll);
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


	// protected void populateSynLexeme(WordLexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction, Map<String, String> datasetNameMap) {
	//
	// 	Long lexemeId = lexeme.getLexemeId();
	// 	Long wordId = lexeme.getWordId();
	// 	Long meaningId = lexeme.getMeaningId();
	//
	// 	List<Word> meaningWords = lexSearchDbService.getMeaningWords(wordId, meaningId, searchDatasetsRestriction);
	// 	List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
	// 	List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.getMeaningDefinitionRefTuples(meaningId);
	// 	List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
	//
	// 	List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
	// 			commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
	// 	List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
	//
	// 	lexeme.setPos(lexemePos);
	// 	lexeme.setMeaningWords(meaningWords);
	// 	lexeme.setDefinitions(definitions);
	// 	lexeme.setUsages(usages);
	//
	// }

}
