package eki.stat.service.db;

import static eki.stat.data.db.Tables.WW_EXCEPTION;
import static eki.stat.data.db.Tables.WW_SEARCH;
import static eki.stat.data.db.Tables.WW_SEARCH_DEFAULT_COUNT;
import static eki.stat.data.db.Tables.WW_SEARCH_FILTERED_COUNT;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.RequestOrigin;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;
import eki.common.data.ValueCount;
import eki.stat.constant.SystemConstant;
import eki.stat.data.SearchDefaultCount;
import eki.stat.data.SearchFilteredCount;
import eki.stat.data.db.tables.WwSearch;
import eki.stat.data.db.tables.WwSearchDefaultCount;
import eki.stat.data.db.tables.WwSearchFilteredCount;
import eki.stat.data.db.tables.records.WwExceptionRecord;
import eki.stat.data.db.tables.records.WwSearchDefaultCountRecord;
import eki.stat.data.db.tables.records.WwSearchFilteredCountRecord;
import eki.stat.data.db.tables.records.WwSearchRecord;

@Component
public class StatDbService implements GlobalConstant, SystemConstant {

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Autowired
	private DSLContext create;

	public Long getWwSearchCount() {
		WwSearch wws = WW_SEARCH.as("wws");
		return create
				.select(DSL.count(wws.ID))
				.from(wws)
				.fetchOneInto(Long.class);
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "{#root.methodName, #statSearchFilter, #offset, #limit}")
	public StatSearchResult searchWwDefaultSearchStat(StatSearchFilter statSearchFilter, int offset, int limit) throws Exception {

		String dateFrom = statSearchFilter.getDateFrom();
		String dateUntil = statSearchFilter.getDateUntil();
		boolean isTrustworthyOnly = statSearchFilter.isTrustworthyOnly();
		boolean isNoResultsOnly = statSearchFilter.isNoResultsOnly();

		Condition where = DSL.noCondition();
		WwSearchDefaultCount sdc = WW_SEARCH_DEFAULT_COUNT.as("sdc");

		if (StringUtils.isNotBlank(dateFrom)) {
			LocalDate date = LocalDate.parse(dateFrom, dateFormatter);
			where = where.and(sdc.SEARCH_DATE.ge(date));
		}
		if (StringUtils.isNotBlank(dateUntil)) {
			LocalDate date = LocalDate.parse(dateFrom, dateFormatter);
			where = where.and(sdc.SEARCH_DATE.le(date));
		}
		if (isTrustworthyOnly) {
			where = where.and(sdc.REQUEST_ORIGIN.eq(RequestOrigin.SEARCH.name()));
		}
		if (isNoResultsOnly) {
			where = where.and(sdc.RESULT_EXISTS.isFalse());
		}

		List<ValueCount> valueCounts = create
				.select(
						sdc.SEARCH_WORD.as("value"),
						DSL.sum(sdc.SEARCH_COUNT).as("count"))
				.from(sdc)
				.where(where)
				.groupBy(sdc.SEARCH_WORD)
				.orderBy(DSL.field("count").desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(ValueCount.class);

		int totalResultCount = create
				.select(DSL.countDistinct(sdc.SEARCH_WORD))
				.from(sdc)
				.where(where)
				.fetchSingleInto(int.class);

		boolean resultsExist = totalResultCount > 0;

		StatSearchResult statSearchResult = new StatSearchResult();
		statSearchResult.setValueCounts(valueCounts);
		statSearchResult.setResultExists(resultsExist);
		statSearchResult.setTotalResultCount(totalResultCount);

		return statSearchResult;
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "{#root.methodName, #statSearchFilter, #offset, #limit}")
	public StatSearchResult searchWwFilteredSearchStat(StatSearchFilter statSearchFilter, int offset, int limit) throws Exception {

		String searchMode = statSearchFilter.getSearchMode();
		String datasetCode = statSearchFilter.getDatasetCode();
		String searchLang = statSearchFilter.getSearchLang();
		String dateFrom = statSearchFilter.getDateFrom();
		String dateUntil = statSearchFilter.getDateUntil();
		boolean isTrustworthyOnly = statSearchFilter.isTrustworthyOnly();
		boolean isNoResultsOnly = statSearchFilter.isNoResultsOnly();

		Condition where = DSL.noCondition();
		WwSearchFilteredCount sfc = WW_SEARCH_FILTERED_COUNT.as("sfc");

		if (StringUtils.isNotBlank(searchMode)) {
			where = where.and(sfc.SEARCH_MODE.eq(searchMode));
		}
		if (StringUtils.isNotBlank(datasetCode)) {
			String[] datasetCodeArr = {datasetCode};
			where = where.and(sfc.DATASET_CODES.contains(datasetCodeArr));
		}
		if (StringUtils.isNotBlank(searchLang)) {
			String[] langArr = {searchLang};
			where = where.and(sfc.DESTIN_LANGS.contains(langArr));
		}
		if (StringUtils.isNotBlank(dateFrom)) {
			LocalDate date = LocalDate.parse(dateFrom, dateFormatter);
			where = where.and(sfc.SEARCH_DATE.ge(date));
		}
		if (StringUtils.isNotBlank(dateUntil)) {
			LocalDate date = LocalDate.parse(dateFrom, dateFormatter);
			where = where.and(sfc.SEARCH_DATE.le(date));
		}
		if (isTrustworthyOnly) {
			where = where.and(sfc.REQUEST_ORIGIN.eq(RequestOrigin.SEARCH.name()));
		}
		if (isNoResultsOnly) {
			where = where.and(sfc.RESULT_EXISTS.isFalse());
		}

		List<ValueCount> valueCounts = create
				.select(
						sfc.SEARCH_WORD.as("value"),
						DSL.sum(sfc.SEARCH_COUNT).as("count"))
				.from(sfc)
				.where(where)
				.groupBy(sfc.SEARCH_WORD)
				.orderBy(DSL.field("count").desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(ValueCount.class);

		int totalResultCount = create
				.select(DSL.countDistinct(sfc.SEARCH_WORD))
				.from(sfc)
				.where(where)
				.fetchSingleInto(int.class);

		boolean resultsExist = totalResultCount > 0;

		StatSearchResult statSearchResult = new StatSearchResult();
		statSearchResult.setValueCounts(valueCounts);
		statSearchResult.setResultExists(resultsExist);
		statSearchResult.setTotalResultCount(totalResultCount);

		return statSearchResult;
	}

	public void createWwSearchStat(SearchStat searchStat) {

		WwSearchRecord wwSearchRecord = create.newRecord(WW_SEARCH);
		wwSearchRecord.setSearchWord(searchStat.getSearchWord());
		wwSearchRecord.setHomonymNr(searchStat.getHomonymNr());
		wwSearchRecord.setSearchMode(searchStat.getSearchMode());
		wwSearchRecord.setDestinLangs(toArr(searchStat.getDestinLangs()));
		wwSearchRecord.setDatasetCodes(toArr(searchStat.getDatasetCodes()));
		wwSearchRecord.setSearchUri(searchStat.getSearchUri());
		wwSearchRecord.setResultCount(searchStat.getResultCount());
		wwSearchRecord.setResultExists(searchStat.isResultExists());
		wwSearchRecord.setSingleResult(searchStat.isSingleResult());
		wwSearchRecord.setUserAgent(searchStat.getUserAgent());
		wwSearchRecord.setReferrerDomain(searchStat.getReferrerDomain());
		wwSearchRecord.setServerDomain(searchStat.getServerDomain());
		wwSearchRecord.setSessionId(searchStat.getSessionId());
		wwSearchRecord.setRequestOrigin(searchStat.getRequestOrigin().name());
		wwSearchRecord.store();
	}

	public void createWwExceptionStat(ExceptionStat exceptionStat) {

		WwExceptionRecord wwExceptionRecord = create.newRecord(WW_EXCEPTION);
		wwExceptionRecord.setExceptionName(exceptionStat.getExceptionName());
		wwExceptionRecord.setExceptionMessage(exceptionStat.getExceptionMessage());
		wwExceptionRecord.setRemoteHost(exceptionStat.getRemoteHost());
		wwExceptionRecord.store();
	}

	public void createOrIncrementCount(SearchDefaultCount searchCount) {

		String searchWord = searchCount.getSearchWord();
		boolean resultExists = searchCount.isResultExists();
		RequestOrigin requestOrigin = searchCount.getRequestOrigin();
		LocalDate searchDate = LocalDate.now();

		WwSearchDefaultCount sdc = WW_SEARCH_DEFAULT_COUNT.as("sdc");

		Long existingCountId = create
				.select(sdc.ID)
				.from(sdc)
				.where(
						sdc.SEARCH_WORD.eq(searchWord)
								.and(sdc.RESULT_EXISTS.eq(resultExists))
								.and(sdc.REQUEST_ORIGIN.eq(requestOrigin.name()))
								.and(sdc.SEARCH_DATE.eq(searchDate)))
				.fetchOptionalInto(Long.class)
				.orElse(null);

		if (existingCountId == null) {

			WwSearchDefaultCountRecord countRecord = create.newRecord(WW_SEARCH_DEFAULT_COUNT);
			countRecord.setSearchWord(searchWord);
			countRecord.setResultExists(resultExists);
			countRecord.setRequestOrigin(requestOrigin.name());
			countRecord.setSearchDate(searchDate);
			countRecord.setSearchCount(1);
			countRecord.store();

		} else {

			create
					.update(WW_SEARCH_DEFAULT_COUNT)
					.set(WW_SEARCH_DEFAULT_COUNT.SEARCH_COUNT, WW_SEARCH_DEFAULT_COUNT.SEARCH_COUNT.plus(1))
					.where(WW_SEARCH_DEFAULT_COUNT.ID.eq(existingCountId))
					.execute();
		}
	}

	public void createOrIncrementCount(SearchFilteredCount searchCount) {

		String searchWord = searchCount.getSearchWord();
		String searchMode = searchCount.getSearchMode();
		String[] destinLangs = toArr(searchCount.getDestinLangs());
		String[] datasetCodes = toArr(searchCount.getDatasetCodes());
		boolean resultExists = searchCount.isResultExists();
		RequestOrigin requestOrigin = searchCount.getRequestOrigin();
		LocalDate searchDate = LocalDate.now();

		WwSearchFilteredCount sfc = WW_SEARCH_FILTERED_COUNT.as("sfc");

		Long existingCountId = create
				.select(sfc.ID)
				.from(sfc)
				.where(
						sfc.SEARCH_WORD.eq(searchWord)
								.and(sfc.SEARCH_MODE.eq(searchMode))
								.and(sfc.DESTIN_LANGS.contains(destinLangs))
								.and(sfc.DATASET_CODES.contains(datasetCodes))
								.and(sfc.RESULT_EXISTS.eq(resultExists))
								.and(sfc.REQUEST_ORIGIN.eq(requestOrigin.name()))
								.and(sfc.SEARCH_DATE.eq(searchDate)))
				.fetchOptionalInto(Long.class)
				.orElse(null);

		if (existingCountId == null) {

			WwSearchFilteredCountRecord countRecord = create.newRecord(WW_SEARCH_FILTERED_COUNT);
			countRecord.setSearchWord(searchWord);
			countRecord.setSearchMode(searchMode);
			countRecord.setDestinLangs(destinLangs);
			countRecord.setDatasetCodes(datasetCodes);
			countRecord.setResultExists(resultExists);
			countRecord.setRequestOrigin(requestOrigin.name());
			countRecord.setSearchCount(1);
			countRecord.store();

		} else {

			create
					.update(WW_SEARCH_FILTERED_COUNT)
					.set(WW_SEARCH_FILTERED_COUNT.SEARCH_COUNT, WW_SEARCH_FILTERED_COUNT.SEARCH_COUNT.plus(1))
					.where(WW_SEARCH_FILTERED_COUNT.ID.eq(existingCountId))
					.execute();
		}
	}

	private String[] toArr(List<String> values) {
		String[] arr = null;
		if (values != null) {
			arr = values.stream().toArray(String[]::new);
		}
		return arr;
	}
}
