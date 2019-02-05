package eki.wordweb.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.data.Count;

@Component
public class StatDataCollector {

	private static final String TOTAL_SEARCH_COUNT_KEY = "*";

	private String startTime;

	private Map<String, Count> searchCountMap;

	private Map<String, Count> exceptionCountMap;

	public StatDataCollector() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.startTime = dateFormat.format(new Date());
		this.searchCountMap = new HashMap<>();
		this.exceptionCountMap = new HashMap<>();
	}

	public synchronized void addSearchStat(String langPair) {
		if (StringUtils.isBlank(langPair)) {
			return;
		}
		Count langSearchCount = searchCountMap.get(langPair);
		if (langSearchCount == null) {
			langSearchCount = new Count();
			searchCountMap.put(langPair, langSearchCount);
		}
		langSearchCount.increment();
		Count totalSearchCount = searchCountMap.get(TOTAL_SEARCH_COUNT_KEY);
		if (totalSearchCount == null) {
			totalSearchCount = new Count();
			searchCountMap.put(TOTAL_SEARCH_COUNT_KEY, totalSearchCount);
		}
		totalSearchCount.increment();
	}

	public synchronized void addExceptionStat(Throwable exception) {
		if (exception == null) {
			return;
		}
		String exceptionName = exception.getClass().getName();
		Count exceptionCount = exceptionCountMap.get(exceptionName);
		if (exceptionCount == null) {
			exceptionCount = new Count();
			exceptionCountMap.put(exceptionName, exceptionCount);
		}
		exceptionCount.increment();
	}

	public String getStartTime() {
		return startTime;
	}

	public Map<String, Count> getSearchCountMap() {
		return searchCountMap;
	}

	public Map<String, Count> getExceptionCountMap() {
		return exceptionCountMap;
	}

}
