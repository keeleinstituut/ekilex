package eki.common.data;

public class AppData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean devEnv;

	private String envName;

	private String appName;

	private String appVersion;

	private long sessionTimeoutSec;

	public boolean isDevEnv() {
		return devEnv;
	}

	public void setDevEnv(boolean devEnv) {
		this.devEnv = devEnv;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
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
