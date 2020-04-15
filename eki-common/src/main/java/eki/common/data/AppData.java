package eki.common.data;

public class AppData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean testEnv;

	private String appName;

	private String appVersion;

	private long sessionTimeoutSec;

	public boolean isTestEnv() {
		return testEnv;
	}

	public void setTestEnv(boolean testEnv) {
		this.testEnv = testEnv;
	}

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
