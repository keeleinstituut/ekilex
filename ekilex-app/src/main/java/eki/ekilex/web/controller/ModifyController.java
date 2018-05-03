package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.OrderingData;
import eki.ekilex.service.UpdateService;
import eki.ekilex.service.util.ConversionUtil;
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

	@Autowired
	private ConversionUtil conversionUtil;

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
		case "lexeme_frequency_group" :
			updateService.updateLexemeFrequencyGroup(id, null);
			break;
		}
		return "OK";
	}

	@ResponseBody
	@PostMapping("/add_definition")
	public String addNewDescription(@RequestParam("id") Long meaningId, @RequestParam("language") String languageCode, @RequestParam("value") String value) {

		logger.debug("Add new definition operation : {} : {} : {}", meaningId, languageCode, value);
		updateService.addDefinition(meaningId, value, languageCode);
		return "OK";
	}

	@ResponseBody
	@PostMapping("/add_usage")
	public String addNewUsage(
			@RequestParam("id") Long governmentId,
			@RequestParam("usage_type") String usageMemberType,
			@RequestParam("language") String languageCode,
			@RequestParam("value") String value) {

		logger.debug("Add new usage operation : {} : {} : {}", governmentId, languageCode, value);
		updateService.addUsageMember(governmentId, usageMemberType, value, languageCode);
		return "OK";
	}

	@ResponseBody
	@PostMapping("/add_classifier")
	public String addLexemeClassifier(
			@RequestParam("classif_name") String classifierName,
			@RequestParam("lexeme_id") Long lexemeId,
			@RequestParam("meaning_id") Long meaningId,
			@RequestParam("value") String value) {

		logger.debug("Add classifier {} : {} : for lexemeId {}, meaningId {}", classifierName, value, lexemeId, meaningId);
		switch (classifierName) {
		case "lexeme_frequency_group" :
			updateService.updateLexemeFrequencyGroup(lexemeId, value);
			break;
		case "lexeme_pos" :
			updateService.addLexemePos(lexemeId, value);
			break;
		case "meaning_domain" :
			Classifier meaningDomain = conversionUtil.classifierFromIdString(value);
			updateService.addMeaningDomain(meaningId, meaningDomain);
			break;
		}
		return "OK";
	}

	@ResponseBody
	@PostMapping("/modify_classifier")
	public String modifyLexemeClassifier(
			@RequestParam("classif_name") String classifierName,
			@RequestParam("id") Long id,
			@RequestParam("current_value") String currentValue,
			@RequestParam("new_value") String newValue) {

		logger.debug("Modify classifier {} : {} : {} : for id {}", classifierName, currentValue, newValue, id);
		switch (classifierName) {
		case "lexeme_frequency_group" :
			updateService.updateLexemeFrequencyGroup(id, newValue);
			break;
		case "lexeme_pos" :
//			updateService.updateLexemePos(id, currentValue, newValue);
			break;
		case "meaning_domain" :
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(currentValue);
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(newValue);
//			updateService.updateMeaningDomain(id, currentMeaningDomain, newMeaningDomain);
			break;
		}
		return "OK";
	}

}
