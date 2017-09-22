package eki.eve.service;

import eki.eve.service.db.SearchDbService;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SearchService {

	@Autowired
	SearchDbService searchDbService;

	public Result<Record3<Long, String, Integer>> findWords(String searchFilter) {
		return searchDbService.findWords(searchFilter);
	}

	public Result<Record6<Long, String, String, String, String, String>> findConnectedForms(Long formId) {
		return searchDbService.findConnectedForms(formId);
	}

	public Result<Record6<String[], Integer, Integer, Long, String[], String[]>> findFormMeanings(Long formId) {
		return searchDbService.findFormMeanings(formId);
	}

	public Map<String, String> allDatasetsAsMap() {
		return searchDbService.allDatasetsAsMap();
	}

}