package eki.ekilex.service;

import static eki.common.constant.StatType.WW_SEARCH;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.StatData;
import eki.ekilex.data.StatDataRow;
import eki.ekilex.service.db.StatDataDbService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StatDataService implements InitializingBean, SystemConstant, GlobalConstant {

	private static final int LIFECYCLE_USER_STAT_DATA_LAST_DAYS_COUNT = 30;

	@Value("${scheduling.enabled:false}")
	private boolean isSchedulingEnabled;

	@Value("${ekistat.service.url}")
	private String serviceUrl;

	@Value("${ekistat.service.key}")
	private String serviceKey;

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

	public Map<String, Integer> getSearchStat(String datasetCode, String lang, String searchMode, String resultsFrom, String resultsUntil) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set(STAT_API_KEY_HEADER_NAME, serviceKey);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String serviceUriWithParameters = UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("statType", WW_SEARCH)
				.queryParam("searchMode", searchMode)
				.queryParam("datasetCode", datasetCode)
				.queryParam("lang", lang)
				.queryParam("resultsFrom", resultsFrom)
				.queryParam("resultsUntil", resultsUntil)
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> response = restTemplate.exchange(serviceUriWithParameters, HttpMethod.GET, entity, Map.class);
		return response.getBody();
	}
}
