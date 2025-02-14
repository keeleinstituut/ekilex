package eki.stat.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.ExceptionStat;
import eki.common.data.ValueCount;
import eki.common.data.SearchStat;
import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;
import eki.stat.service.db.StatDbService;

@Component
public class StatService {

	@Autowired
	private StatDbService statDbService;

	@Transactional
	public Long getWwSearchCount() {
		return statDbService.getWwSearchCount();
	}

	@Transactional
	public StatSearchResult searchWwSearchStat(StatSearchFilter statSearchFilter) throws Exception {
		List<ValueCount> valueCounts = statDbService.searchWwSearchStat(statSearchFilter);
		boolean resultsExist = CollectionUtils.isNotEmpty(valueCounts);
		StatSearchResult statSearchResult = new StatSearchResult();
		statSearchResult.setValueCounts(valueCounts);
		statSearchResult.setResultsExist(resultsExist);
		return statSearchResult;
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
