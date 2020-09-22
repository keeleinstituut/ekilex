package eki.common.web;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import eki.common.data.AppData;

@ConditionalOnWebApplication
@Component
public class AppDataHolder {

	@Value("${info.env.name:#{null}}")
	private String envName;

	@Value("${info.app.name}")
	private String appName;

	@Value("${info.app.version}")
	private String appVersion;

	@Value("${server.servlet.session.timeout:30m}")
	private Duration sessionTimeout;

	private AppData appData = null;

	public AppData getAppData() {

		if (appData != null) {
			return appData;
		}

		String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		boolean isDevEnv = StringUtils.isNotEmpty(envName);
		long sessionTimeoutSec = sessionTimeout.getSeconds();

		appData = new AppData();
		appData.setBaseUrl(baseUrl);
		appData.setDevEnv(isDevEnv);
		appData.setEnvName(envName);
		appData.setAppName(appName);
		appData.setAppVersion(appVersion);
		appData.setSessionTimeoutSec(sessionTimeoutSec);

		return appData;
	}

}
