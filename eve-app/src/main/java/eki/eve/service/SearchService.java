package eki.eve.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Record4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.eve.data.Form;
import eki.eve.data.Meaning;
import eki.eve.data.Word;
import eki.eve.data.WordDetails;
import eki.eve.service.db.SearchDbService;

import static java.util.Collections.emptyList;

@Service
public class SearchService {

	@Autowired
	private SearchDbService searchDbService;

	public List<Word> findWords(String searchFilter) {
		return searchDbService.findWords(searchFilter).into(Word.class);
	}

	public WordDetails findWordDetails(Long formId) {

		List<Form> connectedForms = searchDbService.findConnectedForms(formId).into(Form.class);
		List<Meaning> meanings = searchDbService.findFormMeanings(formId).into(Meaning.class);
		Map<String, String> datasetNameMap = searchDbService.getDatasetNameMap();
		meanings.forEach(meaning -> {
			List<String> datasets = meaning.getDatasets();
			datasets = convertToNames(datasets, datasetNameMap);
			meaning.setDatasets(datasets);
		});
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setMeanings(meanings);
		});
	}

	private List<String> convertToNames(List<String> datasets, Map<String, String> datasetMap) {

		if (datasets == null) {
			return emptyList();
		}
		return datasets.stream().map(datasetMap::get).collect(Collectors.toList());
	}

	public Word getWord(Long wordId) {

		Record4<Long, String, Integer, String> word = searchDbService.getWord(wordId);
		return word == null ? null : word.into(Word.class);
	}

}