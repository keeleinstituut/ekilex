package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;

import eki.ekilex.data.LogData;
import eki.ekilex.service.db.ActivityLogDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.util.ConversionUtil;

public abstract class AbstractService {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected ConversionUtil conversionUtil;

	@Autowired
	protected ActivityLogService activityLogService;

	@Autowired
	protected ActivityLogDbService activityLogDbService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Deprecated
	protected void createLifecycleLog(LogData logData) {
		String userName = userContext.getUserName();
		logData.setUserName(userName);
		lifecycleLogDbService.createLog(logData);
	}

	@Deprecated
	protected void createListOrderingLifecycleLog(LogData logData) {
		String userName = userContext.getUserName();
		logData.setUserName(userName);
		lifecycleLogDbService.createListOrderingLog(logData);
	}

}
