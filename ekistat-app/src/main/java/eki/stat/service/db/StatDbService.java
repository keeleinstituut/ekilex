package eki.stat.service.db;

import static eki.stat.data.db.Tables.WW_EXCEPTION;
import static eki.stat.data.db.Tables.WW_SEARCH;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.stat.data.db.tables.records.WwExceptionRecord;
import eki.stat.data.db.tables.records.WwSearchRecord;

@Component
public class StatDbService {

	private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	private final static int RESULT_LIMIT = 100;

	@Autowired
	private DSLContext create;

	public long getWwSearchStatCount() {
		return create.selectCount().from(WW_SEARCH).fetchOneInto(Long.class);
	}

	public Map<String, Integer> getWwSearchStat(String searchMode, String datasetCode, String lang, String resultsFrom, String resultsUntil) throws Exception {

		Condition where = DSL.noCondition();

		if (StringUtils.isNotBlank(searchMode)) {
			where = where.and(WW_SEARCH.SEARCH_MODE.eq(searchMode));
		}
		if (StringUtils.isNotBlank(datasetCode)) {
			String[] datasetCodeArr = {datasetCode};
			where = where.and(WW_SEARCH.DATASET_CODES.contains(datasetCodeArr));
		}
		if (StringUtils.isNotBlank(lang)) {
			String[] langArr = {lang};
			where = where.and(WW_SEARCH.DESTIN_LANGS.contains(langArr));
		}
		if (StringUtils.isNotBlank(resultsFrom)) {
			Date date = dateFormat.parse(resultsFrom);
			Timestamp timestamp = new Timestamp(date.getTime());
			where = where.and(WW_SEARCH.EVENT_ON.greaterOrEqual(timestamp));
		}
		if (StringUtils.isNotBlank(resultsUntil)) {
			Date date = dateFormat.parse(resultsUntil);
			Timestamp timestamp = new Timestamp(date.getTime());
			where = where.and(WW_SEARCH.EVENT_ON.lessOrEqual(timestamp));
		}

		return create
				.select(WW_SEARCH.SEARCH_WORD, DSL.count(WW_SEARCH.SEARCH_WORD))
				.from(WW_SEARCH)
				.where(where)
				.groupBy(WW_SEARCH.SEARCH_WORD)
				.orderBy(DSL.count(WW_SEARCH.SEARCH_WORD).desc())
				.limit(RESULT_LIMIT)
				.fetchMap(WW_SEARCH.SEARCH_WORD, DSL.count(WW_SEARCH.SEARCH_WORD));
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
