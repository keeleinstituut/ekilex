package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.AddItemRequest;
import eki.ekilex.data.Classifier;
import eki.ekilex.service.UpdateService;
import eki.ekilex.service.util.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.ModifyListRequest;
import eki.ekilex.data.ListData;
import eki.ekilex.data.ModifyItemRequest;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ModifyController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(ModifyController.class);

	@Autowired
	private UpdateService updateService;

	@Autowired
	private ConversionUtil conversionUtil;

	@ResponseBody
	@PostMapping("/modify_item")
	public String modifyItem(@RequestBody ModifyItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update operation for {}", itemData.getOpCode());
		switch (itemData.getOpCode()) {
			case "term_user_lang" :
				updateLanguageSelection(itemData, sessionBean);
				break;
			case "usage" :
				updateService.updateUsageValue(itemData.getId(), itemData.getValue());
				break;
			case "usage_translation" :
				updateService.updateUsageTranslationValue(itemData.getId(), itemData.getValue());
				break;
			case "usage_definition" :
				updateService.updateUsageDefinitionValue(itemData.getId(), itemData.getValue());
				break;
			case "definition" :
				updateService.updateDefinitionValue(itemData.getId(), itemData.getValue());
				break;
			case "lexeme_frequency_group" :
				updateService.updateLexemeFrequencyGroup(itemData.getId(), itemData.getValue());
				break;
			case "lexeme_pos" :
				updateService.updateLexemePos(itemData.getId(), itemData.getCurrentValue(), itemData.getValue());
				break;
			case "meaning_domain" :
				Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(itemData.getCurrentValue());
				Classifier newMeaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
				updateService.updateMeaningDomain(itemData.getId(), currentMeaningDomain, newMeaningDomain);
				break;
		}

		return "{}";
	}

	//currently empty placeholder
	@ResponseBody
	@PostMapping(value = "/modify_list")
	public String modifyList(@RequestBody ModifyListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update operation for {}", listData.getOpCode());
		List<ListData> items = listData.getItems();
		switch (listData.getOpCode()) {
			case "??" :
				//TODO implement
				break;
		}
		return "{}";
	}

	@ResponseBody
	@PostMapping(value = "/modify_ordering")
	public String modifyOrdering(@RequestBody ModifyListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update operation for {}", listData.getOpCode());
		List<ListData> items = listData.getItems();
		switch (listData.getOpCode()) {
			case "definition" :
				updateService.updateDefinitionOrdering(items);
				break;
			case "lexeme_relation" :
				updateService.updateLexemeRelationOrdering(items);
				break;
			case "meaning_relation" :
				updateService.updateMeaningRelationOrdering(items);
				break;
			case "word_relation" :
				updateService.updateWordRelationOrdering(items);
				break;
			case "term_user_lang" :
				updateLanguagesOrder(items, sessionBean);
				break;
		}
		return "{}";
	}

	private void updateLanguageSelection(ModifyItemRequest itemData, SessionBean sessionBean) {
		Integer itemIndex = itemData.getIndex();
		boolean itemSelected = itemData.isSelected();
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		ClassifierSelect language = languagesOrder.get(itemIndex);
		language.setSelected(itemSelected);
	}

	private void updateLanguagesOrder(List<ListData> items, SessionBean sessionBean) {

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<ClassifierSelect> newLanguagesOrder = new ArrayList<>();
		for (ListData item : items) {
			String langCode = item.getCode();
			int langOrderIndex = langCodeOrder.indexOf(langCode);
			ClassifierSelect lang = languagesOrder.get(langOrderIndex);
			newLanguagesOrder.add(lang);
		}
		sessionBean.setLanguagesOrder(newLanguagesOrder);
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
	public String removeElement(
			@RequestParam("op_type") String opCode,
			@RequestParam("id") Long id,
			@RequestParam(value = "value", required = false) String valueToRemove) {

		logger.debug("Delete operation : {} : for id {}, value {}", opCode, id, valueToRemove);
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
		case "lexeme_pos" :
			updateService.removeLexemePos(id, valueToRemove);
			break;
		case "meaning_domain" :
			Classifier meaningDomain = conversionUtil.classifierFromIdString(valueToRemove);
			updateService.removeMeaningDomain(id, meaningDomain);
			break;
		}
		return "OK";
	}

	@ResponseBody
	@PostMapping("/add_item")
	public String addItem(@RequestBody AddItemRequest itemData) {

		logger.debug("Add new item : {}", itemData.getOpCode());
		switch (itemData.getOpCode()) {
		case "definition" :
			updateService.addDefinition(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "USAGE" :
			updateService.addUsageMember(itemData.getId(), itemData.getOpCode(), itemData.getValue(), itemData.getLanguage());
			break;
		case "lexeme_frequency_group" :
			updateService.updateLexemeFrequencyGroup(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_pos" :
			updateService.addLexemePos(itemData.getId(), itemData.getValue());
			break;
		case "meaning_domain" :
			Classifier meaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			updateService.addMeaningDomain(itemData.getId2(), meaningDomain);
			break;
		}
		return "{}";
	}

}
