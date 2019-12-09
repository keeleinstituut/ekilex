package eki.ekilex.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import eki.ekilex.data.RequestTimeInfo;

@Component
public class EkilexInfoContributor implements InfoContributor {

	private RequestTimeInfo requestTimeInfo;

	public EkilexInfoContributor() {
		requestTimeInfo = new RequestTimeInfo();
	}

	@Override
	public void contribute(Builder builder) {

		Map<String, Object> statData = new HashMap<>();
		statData.put("requestCount", requestTimeInfo.getRequestCount());
		statData.put("requestTimeAvg", requestTimeInfo.getRequestTimeAvg());
		builder.withDetail("statData", statData);
	}

	public synchronized void appendRequestTime(long requestTime) {

		long requestCount = requestTimeInfo.getRequestCount();
		long requestTimeSum = requestTimeInfo.getRequestTimeSum();
		requestCount++;
		requestTimeSum += requestTime;
		long requestTimeAvg = requestTimeSum / requestCount;
		requestTimeInfo.setRequestCount(requestCount);
		requestTimeInfo.setRequestTimeSum(requestTimeSum);
		requestTimeInfo.setRequestTimeAvg(requestTimeAvg);
	}
}
