package eki.stat.service.db;

import static eki.stat.data.db.Tables.WW_EXCEPTION;
import static eki.stat.data.db.Tables.WW_SEARCH;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.stat.data.db.tables.records.WwExceptionRecord;
import eki.stat.data.db.tables.records.WwSearchRecord;

@Component
public class StatDbService {

	@Autowired
	private DSLContext create;

	public void createSearchStat(SearchStat searchStat) {

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

	public void createExceptionStat(ExceptionStat exceptionStat) {

		WwExceptionRecord wwExceptionRecord = create.newRecord(WW_EXCEPTION);
		wwExceptionRecord.setExceptionName(exceptionStat.getExceptionName());
		wwExceptionRecord.setExceptionMessage(exceptionStat.getExceptionMessage());
		wwExceptionRecord.store();
	}
}
