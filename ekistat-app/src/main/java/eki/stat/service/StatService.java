package eki.stat.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.RequestOrigin;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;
import eki.stat.constant.SystemConstant;
import eki.stat.data.SearchDefaultCount;
import eki.stat.data.SearchFilteredCount;
import eki.stat.service.db.StatDbService;

@Component
public class StatService implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(StatService.class);

	@Autowired
	private StatDbService statDbService;

	@Transactional
	public Long getWwSearchCount() {
		return statDbService.getWwSearchCount();
	}

	@Transactional
	public StatSearchResult searchWwSearchStat(StatSearchFilter searchFilter) throws Exception {

		String searchMode = searchFilter.getSearchMode();
		String searchLang = searchFilter.getSearchLang();
		String datasetCode = searchFilter.getDatasetCode();
		int pageNum = Math.max(1, searchFilter.getPageNum());
		int offset = (pageNum - 1) * DEFAULT_MAX_RESULTS_LIMIT;

		StatSearchResult statSearchResult = null;

		if (StringUtils.equals(searchMode, SEARCH_MODE_DETAIL)
				&& StringUtils.equals(searchLang, DESTIN_LANG_ALL)
				&& StringUtils.equals(datasetCode, DATASET_ALL)) {

			statSearchResult = statDbService.searchWwDefaultSearchStat(searchFilter, offset, DEFAULT_MAX_RESULTS_LIMIT);

		} else {

			statSearchResult = statDbService.searchWwFilteredSearchStat(searchFilter, offset, DEFAULT_MAX_RESULTS_LIMIT);
		}

		int totalResultCount = statSearchResult.getTotalResultCount();
		int pageCount = totalResultCount / DEFAULT_MAX_RESULTS_LIMIT;
		if (totalResultCount % DEFAULT_MAX_RESULTS_LIMIT != 0) {
			pageCount = pageCount + 1;
		}
		boolean prevPageExist = pageNum > 1;
		boolean nextPageExist = pageNum < pageCount;
		statSearchResult.setPageNum(pageNum);
		statSearchResult.setPageCount(pageCount);
		statSearchResult.setPrevPageExists(prevPageExist);
		statSearchResult.setNextPageExists(nextPageExist);

		return statSearchResult;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWwSearchStat(SearchStat searchStat) {

		String searchWord = searchStat.getSearchWord();
		String searchMode = searchStat.getSearchMode();
		List<String> destinLangs = searchStat.getDestinLangs();
		List<String> datasetCodes = searchStat.getDatasetCodes();
		String referrerDomain = searchStat.getReferrerDomain();
		boolean resultExists = searchStat.isResultExists();
		RequestOrigin requestOrigin = searchStat.getRequestOrigin();

		logger.info("{} - {} - {}", requestOrigin, referrerDomain, searchWord);

		statDbService.createWwSearchStat(searchStat);

		if (StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)
				|| !destinLangs.contains(DESTIN_LANG_ALL)
				|| !datasetCodes.contains(DATASET_ALL)) {

			SearchFilteredCount searchCount = new SearchFilteredCount();
			searchCount.setSearchWord(searchWord);
			searchCount.setSearchMode(searchMode);
			searchCount.setDestinLangs(destinLangs);
			searchCount.setDatasetCodes(datasetCodes);
			searchCount.setResultExists(resultExists);
			searchCount.setRequestOrigin(requestOrigin);

			statDbService.createOrIncrementCount(searchCount);

		} else {

			SearchDefaultCount searchCount = new SearchDefaultCount();
			searchCount.setSearchWord(searchWord);
			searchCount.setResultExists(resultExists);
			searchCount.setRequestOrigin(requestOrigin);

			statDbService.createOrIncrementCount(searchCount);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWwExceptionStat(ExceptionStat exceptionStat) {
		logger.info(exceptionStat.getExceptionName());
		statDbService.createWwExceptionStat(exceptionStat);
	}
}
