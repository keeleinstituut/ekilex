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
public class ModifyController {

	private static final Logger logger = LoggerFactory.getLogger(ModifyController.class);

	@Autowired
	private UpdateService updateService;

	static public class ModifyOrderingRequest {

		private String opCode;
		private List<OrderingData> items;

		public String getOpcode() {
			return opCode;
		}

		public void setOpcode(String opcode) {
			this.opCode = opcode;
		}

		public List<OrderingData> getItems() {
			return items;
		}

		public void setItems(List<OrderingData> items) {
			this.items = items;
		}
	}

	@ResponseBody
	@PostMapping("/modify")
	public String modifyTextValue(@RequestParam("op_type") String opCode, @RequestParam("id") Long id, @RequestParam("modified_value") String value) {

		logger.debug("Update operation {} : {} : for id {}", opCode, value, id);
		switch (opCode) {
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
			case "lexeme_relation" :
				updateService.updateLexemeRelationOrdering(orderingData.getItems());
				break;
			case "meaning_relation" :
				updateService.updateMeaningRelationOrdering(orderingData.getItems());
				break;
			case "word_relation" :
				updateService.updateWordRelationOrdering(orderingData.getItems());
				break;
		}
		return "{}";
	}

	@ResponseBody
	@PostMapping("/modify_levels")
	public String modifyLexemeLevels(@RequestParam("id") Long lexemeId,	@RequestParam("action") String action) {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);
		updateService.updateLexemeLevels(lexemeId, action);
		return "OK";
	}

	@ResponseBody
	@PostMapping("/remove")
	public String removeElement(@RequestParam("op_type") String opCode, @RequestParam("id") Long id) {

		logger.debug("Delete operation : {} : for id {}", opCode, id);
		switch (opCode) {
		case "usage" :
			updateService.removeUsage(id);
			break;
		case "usage_translation" :
			updateService.removeUsageTranslation(id);
			break;
		case "usage_definition" :
			updateService.removeUsageDefinition(id);
			break;
		case "definition" :
			updateService.removeDefinition(id);
			break;
		}
		return "OK";
	}

	@ResponseBody
	@PostMapping("/add_definition")
	public String addNewDescription(@RequestParam("id") Long meaningId, @RequestParam("language") String languageCode, @RequestParam("value") String value) {

		logger.debug("Add new definition operation : {} : {} : {}", meaningId, languageCode, value);
		updateService.addDefinition(meaningId, languageCode, value);
		return "OK";
	}

}
