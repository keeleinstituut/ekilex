package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordsResult;
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

		List<Word> words = commonDataDbService.findWords(searchFilter, datasets, fetchAll).into(Word.class);
		int wordCount = words.size();
		if (!fetchAll && wordCount == CommonDataDbService.MAX_RESULTS_LIMIT) {
			wordCount = commonDataDbService.countWords(searchFilter, datasets);
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}

	@Transactional
	public WordsResult findWords(String searchFilter, List<String> datasets, boolean fetchAll) {

		List<Word> words = commonDataDbService.findWords(searchFilter, datasets, fetchAll).into(Word.class);
		int wordCount = words.size();
		if (!fetchAll && wordCount == CommonDataDbService.MAX_RESULTS_LIMIT) {
			wordCount = commonDataDbService.countWords(searchFilter, datasets);
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		return result;
	}
}
