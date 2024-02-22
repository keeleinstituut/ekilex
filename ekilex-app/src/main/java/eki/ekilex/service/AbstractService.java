package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.service.db.ActivityLogDbService;
import eki.ekilex.service.util.ConversionUtil;

public abstract class AbstractService implements GlobalConstant, SystemConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected ConversionUtil conversionUtil;

	@Autowired
	protected ActivityLogService activityLogService;

	@Autowired
	protected ActivityLogDbService activityLogDbService;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected TextDecorationService textDecorationService;
}
