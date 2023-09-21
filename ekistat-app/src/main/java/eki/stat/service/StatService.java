package eki.stat.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.stat.service.db.StatDbService;

@Component
public class StatService {

	@Autowired
	private StatDbService statDbService;

	@Transactional
	public long getWwSearchStatCount() {
		return statDbService.getWwSearchStatCount();
	}

	@Transactional
	public Map<String, Integer> getWwSearchStat(String searchMode, String datasetCode, String lang, String resultsFrom, String resultsUntil) throws Exception {
		return statDbService.getWwSearchStat(searchMode, datasetCode, lang, resultsFrom, resultsUntil);
	}

	@Transactional
	public void createWwSearchStat(SearchStat searchStat) {
		statDbService.createWwSearchStat(searchStat);
	}

	@Transactional
	public void createWwExceptionStat(ExceptionStat exceptionStat) {
		statDbService.createWwExceptionStat(exceptionStat);
	}
}
