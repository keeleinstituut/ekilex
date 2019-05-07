package eki.ekilex.service;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.StatData;
import eki.ekilex.data.StatDataRow;
import eki.ekilex.service.db.StatDataDbService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Component
public class StatDataService implements InitializingBean, SystemConstant {

	private static final int LIFECYCLE_USER_STAT_DATA_LAST_DAYS_COUNT = 30;

	@Value("${scheduling.enabled:false}")
	private boolean isSchedulingEnabled;

	@Autowired
	private StatDataDbService statDataDbService;

	private StatData mainEntityStatData;

	private List<StatDataRow> freeformStatData;

	private List<StatDataRow> lexemeDatasetStatData;

	private List<StatDataRow> lifecycleUserStatData;

	@Override
	public void afterPropertiesSet() throws Exception {
		mainEntityStatData = new StatData();
		freeformStatData = Collections.emptyList();
		lexemeDatasetStatData = Collections.emptyList();
		lifecycleUserStatData = Collections.emptyList();
	}

	@Scheduled(fixedDelay = UPDATE_STAT_DATA_DELAY, initialDelay = 60000)
	@Transactional
	public void updateStatData() {

		if (!isSchedulingEnabled) {
			return;
		}
		Timestamp from = getPastTimestamp(LIFECYCLE_USER_STAT_DATA_LAST_DAYS_COUNT);
		mainEntityStatData = statDataDbService.getMainEntityStatData();
		freeformStatData = statDataDbService.getFreeformStatData();
		lexemeDatasetStatData = statDataDbService.getLexemeDatasetStatData();
		lifecycleUserStatData = statDataDbService.getLifecycleUserStatData(from);
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

	public List<StatDataRow> getLifecycleUserStatData() {
		return lifecycleUserStatData;
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
}
