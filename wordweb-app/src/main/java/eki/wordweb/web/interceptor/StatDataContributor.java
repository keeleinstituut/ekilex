package eki.wordweb.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import eki.wordweb.service.StatDataCollector;

@Component
public class StatDataContributor implements InfoContributor {

	@Autowired
	private StatDataCollector statDataCollector;

	@Override
	public void contribute(Builder builder) {

		Map<String, Object> statData = new HashMap<>();
		statData.put("startTime", statDataCollector.getStartTime());
		statData.put("searchCounts", statDataCollector.getSearchCountMap());
		statData.put("exceptionCounts", statDataCollector.getExceptionCountMap());

		builder.withDetail("statData", statData);
	}

}
