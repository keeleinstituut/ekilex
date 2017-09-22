package eki.common.web;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.AppData;

@Component
public class AppDataHolder {

	@Autowired
	private ServletContext servletContext;

	private String appName;

	private String appVersion;

	private AppData appData = null;

	public AppData getAppData(HttpServletRequest request, String pomPath) {

		if (appData != null) {
			return appData;
		}

		int sessionTimeout = 0;
		InputStream pomStream = null;
		try {
			String fullPomPath = "META-INF/maven/" + pomPath + "/pom.properties";
			pomStream = servletContext.getResourceAsStream(fullPomPath);
		} catch (Exception e) {
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
		try {
			sessionTimeout = request.getSession().getMaxInactiveInterval() / 60;
		} catch (Exception e) {
		}
		if (StringUtils.isAllBlank(appName, appVersion)) {
			appName = "n/a";
			appVersion = "n/a";
		}

		appData = new AppData();
		appData.setAppName(appName);
		appData.setAppVersion(appVersion);
		appData.setSessionTimeout(sessionTimeout);

		return appData;
	}

}
