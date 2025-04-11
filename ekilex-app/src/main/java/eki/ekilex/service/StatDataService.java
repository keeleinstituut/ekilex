package eki.ekilex.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;
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
	@Transactional(rollbackOn = Exception.class)
	public void updateStatData() {

		if (!isSchedulingEnabled) {
			return;
		}
		LocalDateTime from = getPastDateTime(ACTIVITY_STAT_DAYS_COUNT);
		mainEntityStatData = statDataDbService.getMainEntityStatData();
		freeformStatData = statDataDbService.getFreeformStatData();
		lexemeDatasetStatData = statDataDbService.getLexemeDatasetStatData();
		activityStatData = statDataDbService.getActivityStatData(from);
		apiRequestStatData = statDataDbService.getApiRequestStat();
		apiErrorStatData = statDataDbService.getApiErrorStat();
	}

	private LocalDateTime getPastDateTime(int daysInPast) {

		LocalDateTime time = LocalDate
				.now()
				.minusDays(daysInPast)
				.atStartOfDay();

		return time;
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

	public StatSearchResult getStatSearchResult(StatSearchFilter statSearchFilter) {
		return ekistatClient.getStatSearchResult(statSearchFilter);
	}

}
