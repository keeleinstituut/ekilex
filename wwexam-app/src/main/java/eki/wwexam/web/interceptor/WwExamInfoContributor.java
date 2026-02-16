package eki.wwexam.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import eki.wwexam.data.StatServiceStatus;
import eki.wwexam.service.StatDataCollector;

@Component
public class WwExamInfoContributor implements InfoContributor {

	@Autowired
	private StatDataCollector statDataCollector;

	@Override
	public void contribute(Builder builder) {

		StatServiceStatus statServiceStatus = statDataCollector.getStatServiceStatus();
		builder.withDetail("statServiceStatus", statServiceStatus);
	}
}
