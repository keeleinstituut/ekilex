package eki.common.web;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import eki.common.data.AppData;

@ConditionalOnWebApplication
@Component
public class AppDataHolder {

	@Value("${server.servlet.session.timeout:30m}") // default 30 min
	private Duration sessionTimeout;

	@Autowired
	private ServletContext servletContext;

	private String appName;

	private String appVersion;

	private AppData appData = null;

	public AppData getAppData(String pomPath) {

		if (appData != null) {
			return appData;
		}

		InputStream pomStream = null;
		String fullPomPath = "/META-INF/maven/" + pomPath + "/pom.properties";
		try {
			pomStream = servletContext.getResourceAsStream(fullPomPath);
		} catch (Exception e) {
		}
		if (pomStream == null) {
			try {
				pomStream = this.getClass().getResourceAsStream(fullPomPath);
			} catch (Exception e) {
			}
		}
		if (pomStream == null) {
			try {
				pomStream = new FileInputStream("target/maven-archiver/pom.properties");
			} catch (Exception e) {
			}
		}
		if (pomStream != null) {
			try {
				Properties pomProperties = new Properties();
				pomProperties.load(pomStream);
				appName = pomProperties.getProperty("artifactId");
				appVersion = pomProperties.getProperty("version");
				pomStream.close();
			} catch (Exception e) {
			}
		}
		long sessionTimeoutSec = sessionTimeout.getSeconds();
		if (StringUtils.isAllBlank(appName, appVersion)) {
			appName = "n/a";
			appVersion = "n/a";
		}

		appData = new AppData();
		appData.setAppName(appName);
		appData.setAppVersion(appVersion);
		appData.setSessionTimeoutSec(sessionTimeoutSec);

		return appData;
	}

}
