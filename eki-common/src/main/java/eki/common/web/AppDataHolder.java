package eki.common.web;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

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

		long sessionTimeoutSec = sessionTimeout.getSeconds();

		boolean isDevEnv = StringUtils.isNotEmpty(envName);

		appData = new AppData();
		appData.setDevEnv(isDevEnv);
		appData.setEnvName(envName);
		appData.setAppName(appName);
		appData.setAppVersion(appVersion);
		appData.setSessionTimeoutSec(sessionTimeoutSec);

		return appData;
	}

}
