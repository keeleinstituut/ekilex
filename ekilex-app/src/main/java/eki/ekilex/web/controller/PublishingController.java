package eki.ekilex.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.PublishingConstant;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.PublishItemRequest;
import eki.ekilex.data.Response;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class PublishingController extends AbstractMutableDataPageController implements PublishingConstant {

	private static final Logger logger = LoggerFactory.getLogger(PublishingController.class);

	@ResponseBody
	@PostMapping(PUBLISH_ITEM_URI)
	public Response publish(@RequestBody PublishItemRequest itemData) {

		logger.debug("Publish item: {}", itemData);

		String targetName = itemData.getTargetName();
		String entityName = itemData.getEntityName();
		Long entityId = itemData.getId();

		switch (entityName) {
		case ENTITY_NAME_LEXEME:
			// TODO implement
			break;
		case ENTITY_NAME_DEFINITION:
			// TODO implement
			break;
		case ENTITY_NAME_WORD_RELATION:
			// TODO implement
			break;
		}

		Locale locale = LocaleContextHolder.getLocale();
		String successMessage = messageSource.getMessage("common.data.update.success", new Object[0], locale);
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(successMessage);

		return response;
	}
}
