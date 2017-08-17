package eki.ekilex.web.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.AppData;

//this is just a sample, not actual service
@RestController
public class DummyDataController {

	@Autowired
	private ServletContext servletContext;

	@RequestMapping(value = "/data/app", method = RequestMethod.GET)
	public ResponseEntity<AppData> getAppData(HttpServletRequest request) {

		String appName = "n/a";
		String appVersion = "n/a";
		int sessionTimeout = 0;
		InputStream pomStream = null;
		try {
			pomStream = servletContext.getResourceAsStream("META-INF/maven/eki.ekilex/ekilex-app/pom.properties");
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

		AppData appData = new AppData();
		appData.setAppName(appName);
		appData.setAppVersion(appVersion);
		appData.setSessionTimeout(sessionTimeout);

		return new ResponseEntity<>(appData, HttpStatus.OK);
	}
}
