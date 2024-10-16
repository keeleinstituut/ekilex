package eki.ekilex.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.client.EkistatClient;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.StatData;
import eki.ekilex.data.StatDataRow;
import eki.ekilex.service.db.StatDataDbService;

@Component
public class StatDataService implements InitializingBean, SystemConstant, GlobalConstant {

	private static final int ACTIVITY_STAT_DAYS_COUNT = 30;

	@Value("${scheduling.enabled:false}")
	private boolean isSchedulingEnabled;

	@Autowired
	private EkistatClient ekistatClient;

	@Autowired
	private StatDataDbService statDataDbService;

	private StatData mainEntityStatData;

	private List<StatDataRow> freeformStatData;

	private List<StatDataRow> lexemeDatasetStatData;

	private List<StatDataRow> activityStatData;

	private List<StatDataRow> apiRequestStatData;

	private List<StatDataRow> apiErrorStatData;

	@Override
	public void afterPropertiesSet() throws Exception {
		mainEntityStatData = new StatData();
		freeformStatData = Collections.emptyList();
		lexemeDatasetStatData = Collections.emptyList();
		activityStatData = Collections.emptyList();
	}

	@Scheduled(fixedDelay = UPDATE_STAT_DATA_DELAY, initialDelay = 60000)
	@Transactional
	public void updateStatData() {

		if (!isSchedulingEnabled) {
			return;
		}
		Timestamp from = getPastTimestamp(ACTIVITY_STAT_DAYS_COUNT);
		mainEntityStatData = statDataDbService.getMainEntityStatData();
		freeformStatData = statDataDbService.getFreeformStatData();
		lexemeDatasetStatData = statDataDbService.getLexemeDatasetStatData();
		activityStatData = statDataDbService.getActivityStatData(from);
		apiRequestStatData = statDataDbService.getApiRequestStat();
		apiErrorStatData = statDataDbService.getApiErrorStat();
	}

	private Timestamp getPastTimestamp(int daysInPast) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -daysInPast);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	public StatData getMainEntityStatData() {
		return mainEntityStatData;
	}

	public List<StatDataRow> getFreeformStatData() {
		return freeformStatData;
	}

	public List<StatDataRow> getLexemeDatasetStatData() {
		return lexemeDatasetStatData;
	}

	public List<StatDataRow> getActivityStatData() {
		return activityStatData;
	}

	public List<StatDataRow> getApiRequestStatData() {
		return apiRequestStatData;
	}

	public List<StatDataRow> getApiErrorStatData() {
		return apiErrorStatData;
	}

	public Map<String, Integer> getSearchStat(String datasetCode, String searchLang, String searchMode, String resultsFrom, String resultsUntil) {
		return ekistatClient.getSearchStat(datasetCode, searchLang, searchMode, resultsFrom, resultsUntil);
	}

}
