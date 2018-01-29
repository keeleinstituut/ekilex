package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class WordDetailsController {

	private static final Logger logger = LoggerFactory.getLogger(WordDetailsController.class);

	@Autowired
	private UpdateService updateService;

	@ResponseBody
	@PostMapping("/modify")
	public String details(@RequestParam("op_type") String opType, @RequestParam("id") Long id, @RequestParam("modified_value") String value) {

		logger.debug("Update operation {} : {} : for id {}", opType, value, id);
		switch (opType) {
			case "usage" :
				updateService.updateUsageValue(id, value);
				break;
			case "usage_translation" :
				updateService.updateUsageTranslationValue(id, value);
				break;
			case "usage_definition" :
				updateService.updateUsageDefinitionValue(id, value);
				break;
			case "definition" :
				updateService.updateDefinitionValue(id, value);
				break;
		}

		return "OK";
	}

}
