package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;

import eki.ekilex.service.db.ActivityLogDbService;
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

}
