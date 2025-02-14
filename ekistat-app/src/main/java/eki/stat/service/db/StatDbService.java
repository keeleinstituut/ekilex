package eki.stat.service.db;

import static eki.stat.data.db.Tables.WW_EXCEPTION;
import static eki.stat.data.db.Tables.WW_SEARCH;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.RequestOrigin;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.common.data.StatSearchFilter;
import eki.common.data.ValueCount;
import eki.stat.data.db.tables.WwSearch;
import eki.stat.data.db.tables.records.WwExceptionRecord;
import eki.stat.data.db.tables.records.WwSearchRecord;

@Component
public class StatDbService implements GlobalConstant {

	private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	private final static int RESULT_LIMIT = 100;

	@Autowired
	private DSLContext create;

	public Long getWwSearchCount() {
		WwSearch wws = WW_SEARCH.as("wws");
		return create
				.select(DSL.count(wws.ID))
				.from(wws)
				.fetchOneInto(Long.class);
	}

	public List<ValueCount> searchWwSearchStat(StatSearchFilter statSearchFilter) throws Exception {

		String searchMode = statSearchFilter.getSearchMode();
		String datasetCode = statSearchFilter.getDatasetCode();
		String searchLang = statSearchFilter.getSearchLang();
		String dateFrom = statSearchFilter.getDateFrom();
		String dateUntil = statSearchFilter.getDateUntil();
		boolean isTrustworthyOnly = statSearchFilter.isTrustworthyOnly();

		Condition where = DSL.noCondition();
		WwSearch wws = WW_SEARCH.as("wws");

		if (StringUtils.isNotBlank(searchMode)) {
			where = where.and(wws.SEARCH_MODE.eq(searchMode));
		}
		if (StringUtils.isNotBlank(datasetCode)) {
			String[] datasetCodeArr = {datasetCode};
			where = where.and(wws.DATASET_CODES.contains(datasetCodeArr));
		}
		if (StringUtils.isNotBlank(searchLang)) {
			String[] langArr = {searchLang};
			where = where.and(wws.DESTIN_LANGS.contains(langArr));
		}
		if (StringUtils.isNotBlank(dateFrom)) {
			Date date = dateFormat.parse(dateFrom);
			Timestamp timestamp = new Timestamp(date.getTime());
			where = where.and(wws.EVENT_ON.ge(timestamp));
		}
		if (StringUtils.isNotBlank(dateUntil)) {
			Date date = dateFormat.parse(dateUntil);
			Timestamp timestamp = new Timestamp(date.getTime());
			where = where.and(wws.EVENT_ON.le(timestamp));
		}
		if (isTrustworthyOnly) {
			where = where.and(wws.REQUEST_ORIGIN.ne(RequestOrigin.OUTSIDE_NAVIGATION.name()));
		}

		return create
				.select(
						wws.SEARCH_WORD.as("value"),
						DSL.count(wws.SEARCH_WORD).as("count"))
				.from(wws)
				.where(where)
				.groupBy(wws.SEARCH_WORD)
				.orderBy(DSL.field("count").desc())
				.limit(RESULT_LIMIT)
				.fetchInto(ValueCount.class);
	}

	public void createWwSearchStat(SearchStat searchStat) {

		String[] destinLangs = searchStat.getDestinLangs() == null ? null : searchStat.getDestinLangs().toArray(new String[0]);
		String[] datasetCodes = searchStat.getDatasetCodes() == null ? null : searchStat.getDatasetCodes().toArray(new String[0]);

		WwSearchRecord wwSearchRecord = create.newRecord(WW_SEARCH);
		wwSearchRecord.setSearchWord(searchStat.getSearchWord());
		wwSearchRecord.setHomonymNr(searchStat.getHomonymNr());
		wwSearchRecord.setSearchMode(searchStat.getSearchMode());
		wwSearchRecord.setDestinLangs(destinLangs);
		wwSearchRecord.setDatasetCodes(datasetCodes);
		wwSearchRecord.setSearchUri(searchStat.getSearchUri());
		wwSearchRecord.setResultCount(searchStat.getResultCount());
		wwSearchRecord.setResultsExist(searchStat.isResultsExist());
		wwSearchRecord.setSingleResult(searchStat.isSingleResult());
		wwSearchRecord.setUserAgent(searchStat.getUserAgent());
		wwSearchRecord.setReferrerDomain(searchStat.getReferrerDomain());
		wwSearchRecord.setSessionId(searchStat.getSessionId());
		wwSearchRecord.setRequestOrigin(searchStat.getRequestOrigin().name());
		wwSearchRecord.store();
	}

	public void createWwExceptionStat(ExceptionStat exceptionStat) {

		WwExceptionRecord wwExceptionRecord = create.newRecord(WW_EXCEPTION);
		wwExceptionRecord.setExceptionName(exceptionStat.getExceptionName());
		wwExceptionRecord.setExceptionMessage(exceptionStat.getExceptionMessage());
		wwExceptionRecord.store();
	}
}
