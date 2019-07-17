package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;

import eki.ekilex.data.LogData;
import eki.ekilex.service.db.LifecycleLogDbService;

public abstract class AbstractService {

	@Autowired
	private UserService userService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	protected void createLifecycleLog(LogData logData) {
		String userName = userService.getAuthenticatedUser().getName();
		logData.setUserName(userName);
		lifecycleLogDbService.createLog(logData);
	}

	protected void createListOrderingLifecycleLog(LogData logData) {
		String userName = userService.getAuthenticatedUser().getName();
		logData.setUserName(userName);
		lifecycleLogDbService.createListOrderingLog(logData);
	}

}
