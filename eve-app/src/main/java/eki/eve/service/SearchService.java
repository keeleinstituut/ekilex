package eki.eve.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.eve.data.Form;
import eki.eve.data.Meaning;
import eki.eve.data.Word;
import eki.eve.data.WordDetails;
import eki.eve.service.db.SearchDbService;

@Service
public class SearchService {

	@Autowired
	SearchDbService searchDbService;

	public List<Word> findWords(String searchFilter) {
		return searchDbService.findWords(searchFilter).into(Word.class);
	}

	public WordDetails findWordDetails(Long wordId) {
		List<Form> connectedForms = searchDbService.findConnectedForms(wordId).into(Form.class);
		List<Meaning> meanings = searchDbService.findFormMeanings(wordId).into(Meaning.class);
		Map<String, String> dataSets = searchDbService.allDatasetsAsMap();
		meanings.forEach(meaning -> {
			List<String> dataSetNames = meaning.getDatasets().stream().map(dataSets::get).collect(Collectors.toList());
			meaning.setDatasets(dataSetNames);
		});
		return new WordDetails( d -> {
			d.setForms(connectedForms);
			d.setMeanings(meanings);
		});
	}

}