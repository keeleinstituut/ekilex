package eki.ekilex.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.PublishingConstant;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.PublishItemRequest;
import eki.ekilex.data.Response;
import eki.ekilex.service.PublishingService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class PublishingController extends AbstractMutableDataPageController implements PublishingConstant {

	private static final Logger logger = LoggerFactory.getLogger(PublishingController.class);

	@Autowired
	private PublishingService publishingService;

	@ResponseBody
	@PostMapping(PUBLISH_ITEM_URI)
	public Response publish(@RequestBody PublishItemRequest publishingItem, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Publish item: {}", publishingItem);

		EkiUser user = userContext.getUser();
		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		publishingService.publish(publishingItem, user, roleDatasetCode, isManualEventOnUpdateEnabled);

		Locale locale = LocaleContextHolder.getLocale();
		String successMessage = messageSource.getMessage("common.data.update.success", new Object[0], locale);
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(successMessage);

		return response;
	}
}
