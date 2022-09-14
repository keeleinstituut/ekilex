package eki.ekilex.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.MeaningTableRow;
import eki.ekilex.data.MeaningTableSearchResult;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TypeMtLexeme;
import eki.ekilex.data.TypeMtLexemeFreeform;
import eki.ekilex.data.TypeMtWord;
import eki.ekilex.service.db.MeaningTableDbService;

@Component
public class MeaningTableService extends AbstractSearchService {

	@Autowired
	private MeaningTableDbService meaningTableDbService;

	@Transactional
	public MeaningTableSearchResult getMeaningTableSearchResult(
			String searchFilter, List<String> selectedDatasetCodes, String resultLang) {

		int offset = DEFAULT_OFFSET;
		boolean noLimit = false;

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		MeaningTableSearchResult meaningTableSearchResult = meaningTableDbService.getMeaningTableSearchResult(searchFilter, searchDatasetsRestriction, resultLang, offset, noLimit);
		aggregate(meaningTableSearchResult);

		return meaningTableSearchResult;
	}

	@Transactional
	public MeaningTableSearchResult getMeaningTableSearchResult(
			SearchFilter searchFilter, List<String> selectedDatasetCodes, String resultLang) throws Exception {

		int offset = DEFAULT_OFFSET;
		boolean noLimit = false;

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		MeaningTableSearchResult meaningTableSearchResult = meaningTableDbService.getMeaningTableSearchResult(searchFilter, searchDatasetsRestriction, resultLang, offset, noLimit);
		aggregate(meaningTableSearchResult);

		return meaningTableSearchResult;
	}

	private void aggregate(MeaningTableSearchResult meaningTableSearchResult) {

		List<MeaningTableRow> results = meaningTableSearchResult.getResults();

		for (MeaningTableRow meaningTableRow : results) {

			List<TypeMtLexeme> lexemes = meaningTableRow.getLexemes();
			List<TypeMtWord> words = meaningTableRow.getWords();
			List<TypeMtLexemeFreeform> usages = meaningTableRow.getUsages();
			Map<Long, TypeMtWord> wordMap = words.stream().collect(Collectors.toMap(TypeMtWord::getWordId, word -> word));
			Map<Long, List<TypeMtLexemeFreeform>> lexemeUsagesMap = Collections.emptyMap();
			if (CollectionUtils.isNotEmpty(usages)) {
				lexemeUsagesMap = usages.stream().collect(Collectors.groupingBy(TypeMtLexemeFreeform::getLexemeId));
			}
			for (TypeMtLexeme lexeme : lexemes) {

				Long lexemeId = lexeme.getLexemeId();
				Long wordId = lexeme.getWordId();
				TypeMtWord lexemeWord = wordMap.get(wordId);
				List<TypeMtLexemeFreeform> lexemeUsages = lexemeUsagesMap.get(lexemeId);
				lexeme.setWord(lexemeWord);
				lexeme.setUsages(lexemeUsages);
			}
		}
	}
}
