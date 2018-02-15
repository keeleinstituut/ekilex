package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import eki.ekilex.data.WordsResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.CommonDataDbService;

@Component
public class CommonDataService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets().into(Dataset.class);
	}

	@Transactional
	public WordsResult findWords(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) throws Exception {

		WordsResult result = new WordsResult();
		result.setWords(commonDataDbService.findWords(searchFilter, datasets, fetchAll).into(Word.class));
		if (fetchAll || result.getWords().size() < CommonDataDbService.MAX_RESULTS_LIMIT) {
			result.setTotalCount(result.getWords().size());
		} else {
			result.setTotalCount(commonDataDbService.countWords(searchFilter, datasets));
		}
		return result;
	}

	@Transactional
	public WordsResult findWords(String searchFilter, List<String> datasets, boolean fetchAll) {

		WordsResult result = new WordsResult();
		if (StringUtils.isNotBlank(searchFilter)) {
			result.setWords(commonDataDbService.findWords(searchFilter, datasets, fetchAll).into(Word.class));
			if (fetchAll || result.getWords().size() < CommonDataDbService.MAX_RESULTS_LIMIT) {
				result.setTotalCount(result.getWords().size());
			} else {
				result.setTotalCount(commonDataDbService.countWords(searchFilter, datasets));
			}
		}
		return result;
	}
}
