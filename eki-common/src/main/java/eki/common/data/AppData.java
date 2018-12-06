package eki.common.data;

public class AppData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String appName;

	private String appVersion;

	private long sessionTimeoutSec;

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

	public long getSessionTimeoutSec() {
		return sessionTimeoutSec;
	}

	public void setSessionTimeoutSec(long sessionTimeoutSec) {
		this.sessionTimeoutSec = sessionTimeoutSec;
	}

}
