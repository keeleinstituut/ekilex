package eki.ekilex.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import eki.ekilex.data.RequestTimeInfo;

@Component
public class EkilexInfoContributor implements InfoContributor {

	private RequestTimeInfo uiRequestTimeInfo;

	private RequestTimeInfo apiRequestTimeInfo;

	public EkilexInfoContributor() {
		uiRequestTimeInfo = new RequestTimeInfo();
		apiRequestTimeInfo = new RequestTimeInfo();
	}

	@Override
	public void contribute(Builder builder) {

		Map<String, Object> statData = new HashMap<>();
		statData.put("uiRequestCount", uiRequestTimeInfo.getRequestCount());
		statData.put("uiRuestTimeAvg", uiRequestTimeInfo.getRequestTimeAvg());
		statData.put("apiRequestCount", apiRequestTimeInfo.getRequestCount());
		statData.put("apiRuestTimeAvg", apiRequestTimeInfo.getRequestTimeAvg());
		builder.withDetail("statData", statData);
	}

	public synchronized void appendUiRequestTime(long requestTime) {
		appendUiRequestTime(uiRequestTimeInfo, requestTime);
	}

	public synchronized void appendApiRequestTime(long requestTime) {
		appendUiRequestTime(apiRequestTimeInfo, requestTime);
	}

	private synchronized void appendUiRequestTime(RequestTimeInfo requestTimeInfo, long requestTime) {

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
