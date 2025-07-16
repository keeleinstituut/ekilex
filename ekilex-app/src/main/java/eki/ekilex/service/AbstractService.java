package eki.ekilex.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import eki.common.constant.FreeformConstant;
import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.AbstractCreateUpdateEntity;
import eki.ekilex.data.ValueAndPrese;
import eki.ekilex.data.WordOdRecommendation;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.service.db.ActivityLogDbService;
import eki.ekilex.service.util.ConversionUtil;

public abstract class AbstractService implements GlobalConstant, SystemConstant, FreeformConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected ConversionUtil conversionUtil;

	@Autowired
	protected ActivityLogService activityLogService;

	@Autowired
	protected ActivityLogDbService activityLogDbService;

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected MessageSource messageSource;

	protected void applyCreateUpdate(AbstractCreateUpdateEntity entity) {

		String userName = userContext.getUserName();
		LocalDateTime now = LocalDateTime.now();
		entity.setCreatedBy(userName);
		entity.setCreatedOn(now);
		entity.setModifiedBy(userName);
		entity.setModifiedOn(now);
	}

	protected void applyUpdate(AbstractCreateUpdateEntity entity) {

		String userName = userContext.getUserName();
		LocalDateTime now = LocalDateTime.now();
		entity.setModifiedBy(userName);
		entity.setModifiedOn(now);
	}

	public void setValueAndPrese(ValueAndPrese entity) {

		String valuePrese = StringUtils.trim(entity.getValuePrese());
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		entity.setValue(value);
		entity.setValuePrese(valuePrese);

		if (entity instanceof WordOdRecommendation) {
			WordOdRecommendation wordOdRecommendation = (WordOdRecommendation) entity;
			String optValuePrese = StringUtils.trim(wordOdRecommendation.getOptValuePrese());
			String optValue = textDecorationService.removeEkiElementMarkup(optValuePrese);
			wordOdRecommendation.setOptValue(optValue);
			wordOdRecommendation.setOptValuePrese(optValuePrese);
		}
	}
}
