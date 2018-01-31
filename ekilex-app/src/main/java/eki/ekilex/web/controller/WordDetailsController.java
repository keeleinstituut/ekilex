package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.OrderingData;
import eki.ekilex.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

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

	@ResponseBody
	@PostMapping(value = "/modify_ordering")
	public String modifyOrdering(@RequestBody ModifyOrderingRequest orderingData) {

		logger.debug("Update operation for {}", orderingData.getOpcode());
		switch (orderingData.getOpcode()) {
			case "definition" :
				updateService.updateDefinitionOrdering(orderingData.getItems());
				break;
		}
		return "{}";
	}

	static public class ModifyOrderingRequest {

		private String opcode;
		private List<OrderingData> items;

		public String getOpcode() {
			return opcode;
		}

		public void setOpcode(String opcode) {
			this.opcode = opcode;
		}

		public List<OrderingData> getItems() {
			return items;
		}

		public void setItems(List<OrderingData> items) {
			this.items = items;
		}
	}
}
