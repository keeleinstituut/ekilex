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

	public synchronized void addSearchStat(String destinLang, String searchMode, boolean resultsExist, boolean isIeUser) {
		if (StringUtils.isBlank(destinLang)) {
			return;
		}
		if (StringUtils.isBlank(searchMode)) {
			return;
		}
		String searchCountKey = composeSearchCountKey(destinLang, searchMode, resultsExist, isIeUser);
		Count langSearchCount = searchCountMap.get(searchCountKey);
		if (langSearchCount == null) {
			langSearchCount = new Count();
			searchCountMap.put(searchCountKey, langSearchCount);
		}
		langSearchCount.increment();
		Count totalSearchCount = searchCountMap.get(TOTAL_SEARCH_COUNT_KEY);
		if (totalSearchCount == null) {
			totalSearchCount = new Count();
			searchCountMap.put(TOTAL_SEARCH_COUNT_KEY, totalSearchCount);
		}
		totalSearchCount.increment();
	}

	private String composeSearchCountKey(String destinLang, String searchMode, boolean resultsExist, boolean isIeUser) {
		StringBuffer keyBuf = new StringBuffer();
		keyBuf.append(destinLang);
		keyBuf.append(' ');
		keyBuf.append(searchMode.toLowerCase());
		keyBuf.append(' ');
		if (resultsExist) {
			keyBuf.append("pos");
		} else {
			keyBuf.append("neg");
		}
		if (isIeUser) {
			keyBuf.append(' ');
			keyBuf.append("ie");
		}
		return keyBuf.toString();
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
