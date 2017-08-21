package eki.common.data;

import java.io.Serializable;

public class AppData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String appName;

	private String appVersion;

	private int sessionTimeout;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
}
