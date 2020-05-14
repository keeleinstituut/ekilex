package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import eki.common.constant.ContentKey;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.ConfirmationRequest;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.ListData;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.data.UpdateListRequest;
import eki.ekilex.service.ComplexOpService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.SourceService;
import eki.ekilex.service.util.ConversionUtil;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class EditController extends AbstractPageController implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(EditController.class);

	@Autowired
	private CudService cudService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Autowired
	private SourceService sourceService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private ComplexOpService complexOpService;

	@ResponseBody
	@PostMapping(CREATE_ITEM_URI)
	public String createItem(@RequestBody CreateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Add new item : {}", itemData);

		String itemValue = itemData.getValue();
		itemValue = valueUtil.trimAndCleanAndRemoveHtml(itemValue);
		String sourceValue;
		String datasetCode;

		switch (itemData.getOpCode()) {
		case "definition":
			datasetCode = sessionBean.getUserRole().getDatasetCode();
			cudService.createDefinition(itemData.getId(), itemValue, itemData.getLanguage(), datasetCode, itemData.getComplexity(), itemData.getItemType(), itemData.isPublic());
			break;
		case "definition_public_note":
			cudService.createDefinitionPublicNote(itemData.getId(), itemValue);
			break;
		case "usage":
			cudService.createUsage(itemData.getId(), itemValue, itemData.getLanguage(), itemData.getComplexity(), itemData.isPublic());
			break;
		case "usage_translation":
			cudService.createUsageTranslation(itemData.getId(), itemValue, itemData.getLanguage());
			break;
		case "usage_definition":
			cudService.createUsageDefinition(itemData.getId(), itemValue, itemData.getLanguage());
			break;
		case "lexeme_frequency_group":
			cudService.updateLexemeFrequencyGroup(itemData.getId(), itemValue);
			break;
		case "lexeme_pos":
			cudService.createLexemePos(itemData.getId(), itemValue);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(itemValue);
			cudService.createMeaningDomain(itemData.getId2(), meaningDomain);
			break;
		case "government":
			cudService.createLexemeGovernment(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case ContentKey.DEFINITION_SOURCE_LINK: {
			String sourcePropertyValue = getSourcePropertyValue(itemData.getId3());
			cudService.createDefinitionSourceLink(itemData.getId(), itemData.getId2(), sourcePropertyValue, itemValue);
			break;
		}
		case ContentKey.LEXEME_SOURCE_LINK: {
			String sourcePropertyValue = getSourcePropertyValue(itemData.getId3());
			cudService.createLexemeSourceLink(itemData.getId(), itemData.getId2(), sourcePropertyValue, itemValue);
			break;
		}
		case "usage_author":
			sourceValue = getSourceNameValue(itemData.getId2());
			ReferenceType refType = ReferenceType.valueOf(itemData.getItemType());
			cudService.createUsageSourceLink(itemData.getId(), itemData.getId2(), refType, sourceValue, null);
			break;
		case "usage_source_link":
			sourceValue = getSourcePropertyValue(itemData.getId3());
			cudService.createUsageSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceValue, itemValue);
			break;
		case "lexeme_ff_source_link":
			sourceValue = getSourcePropertyValue(itemData.getId3());
			cudService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceValue, itemValue, LifecycleEntity.LEXEME);
			break;
		case "meaning_ff_source_link":
			sourceValue = getSourcePropertyValue(itemData.getId3());
			cudService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceValue, itemValue, LifecycleEntity.MEANING);
			break;
		case "definition_ff_source_link":
			sourceValue = getSourcePropertyValue(itemData.getId3());
			cudService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceValue, itemValue, LifecycleEntity.DEFINITION);
			break;
		case "lexeme_deriv":
			cudService.createLexemeDeriv(itemData.getId(), itemValue);
			break;
		case "lexeme_register":
			cudService.createLexemeRegister(itemData.getId(), itemValue);
			break;
		case "lexeme_region":
			cudService.createLexemeRegion(itemData.getId(), itemValue);
			break;
		case "word_gender":
			cudService.updateWordGender(itemData.getId3(), itemValue);
			break;
		case "word_type":
			cudService.createWordType(itemData.getId3(), itemValue);
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemData.getId3(), itemValue);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(itemData.getId3(), itemValue);
			break;
		case "word_lang":
			cudService.updateWordLang(itemData.getId3(), itemValue);
			break;
		case "lexeme_grammar":
			cudService.createLexemeGrammar(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemData.getId(), itemValue);
			break;
		case "learner_comment":
			cudService.createMeaningLearnerComment(itemData.getId(), itemValue, itemData.getLanguage());
			break;
		case "lexeme_public_note":
			cudService.createLexemePublicNote(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "meaning_public_note":
			cudService.createMeaningPublicNote(itemData.getId(), itemValue);
			break;
		case "image_title":
			cudService.createImageTitle(itemData.getId(), itemValue);
			break;
		case "create_raw_relation":
			datasetCode = sessionBean.getUserRole().getDatasetCode();
			cudService.createSynRelation(itemData.getId(), itemData.getId2(), itemData.getValue2(), datasetCode);
			break;
		case "create_syn_word":
			datasetCode = sessionBean.getUserRole().getDatasetCode();
			cudService.createWordAndSynRelation(itemData.getId(), itemValue, datasetCode, itemData.getLanguage(), itemData.getItemType(), itemData.getValue2());
			break;
		case "meaning_semantic_type":
			cudService.createMeaningSemanticType(itemData.getId2(), itemValue);
			break;
		case "od_word_recommendation":
			cudService.createOdWordRecommendation(itemData.getId(), itemValue);
			break;
		case "od_lexeme_recommendation":
			cudService.createOdLexemeRecommendation(itemData.getId(), itemValue);
			break;
		case "od_usage_definition":
			cudService.createOdUsageDefinition(itemData.getId(), itemValue);
			break;
		case "od_usage_alternative":
			cudService.createOdUsageAlternative(itemData.getId(), itemValue);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	private String getSourcePropertyValue(Long sourcePropertyId) {
		return sourceService.getSourcePropertyValue(sourcePropertyId);
	}

	private String getSourceNameValue(Long sourceId) {
		return sourceService.getSourceNameValue(sourceId);
	}

	@ResponseBody
	@PostMapping(UPDATE_ITEM_URI)
	public String updateItem(@RequestBody UpdateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update item : {}", itemData);

		String itemValue = itemData.getValue();
		itemValue = valueUtil.trimAndCleanAndRemoveHtml(itemValue);

		switch (itemData.getOpCode()) {
		case "term_user_lang":
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
			ClassifierSelect langSelect = languagesOrder.stream().filter(classif -> StringUtils.equals(classif.getCode(), itemData.getCode())).findFirst().get();
			langSelect.setSelected(!langSelect.isSelected());
			break;
		case "usage":
			cudService.updateUsageValue(itemData.getId(), itemValue, itemData.getComplexity(), itemData.isPublic());
			break;
		case "usage_translation":
			cudService.updateUsageTranslationValue(itemData.getId(), itemValue);
			break;
		case "usage_definition":
			cudService.updateUsageDefinitionValue(itemData.getId(), itemValue);
			break;
		case "definition":
			cudService.updateDefinition(itemData.getId(), itemValue, itemData.getComplexity(), itemData.getCode(), itemData.isPublic());
			break;
		case "definition_public_note":
			cudService.updateDefinitionPublicNote(itemData.getId(), itemValue);
			break;
		case "lexeme_frequency_group":
			cudService.updateLexemeFrequencyGroup(itemData.getId(), itemValue);
			break;
		case "lexeme_complexity":
			cudService.updateLexemeComplexity(itemData.getId(), itemValue);
			break;
		case "lexeme_pos":
			cudService.updateLexemePos(itemData.getId(), itemData.getCurrentValue(), itemValue);
			break;
		case "meaning_domain":
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(itemData.getCurrentValue());
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			cudService.updateMeaningDomain(itemData.getId(), currentMeaningDomain, newMeaningDomain);
			break;
		case "government":
			cudService.updateLexemeGovernment(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "lexeme_deriv":
			cudService.updateLexemeDeriv(itemData.getId(), itemData.getCurrentValue(), itemValue);
			break;
		case "lexeme_register":
			cudService.updateLexemeRegister(itemData.getId(), itemData.getCurrentValue(), itemValue);
			break;
		case "lexeme_region":
			cudService.updateLexemeRegion(itemData.getId(), itemData.getCurrentValue(), itemValue);
			break;
		case "lexeme_weight":
			cudService.updateLexemeWeight(itemData.getId(), itemValue);
			break;
		case "word_gender":
			cudService.updateWordGender(itemData.getId(), itemValue);
			break;
		case "word_type":
			cudService.updateWordType(itemData.getId(), itemData.getCurrentValue(), itemValue);
			break;
		case "lexeme_grammar":
			cudService.updateLexemeGrammar(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemData.getId(), itemValue);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(itemData.getId(), itemValue);
			break;
		case "word_lang":
			cudService.updateWordLang(itemData.getId(), itemValue);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemData.getId(), itemValue);
			break;
		case "learner_comment":
			cudService.updateMeaningLearnerComment(itemData.getId(), itemValue);
			break;
		case "lexeme_public_note":
			cudService.updateLexemePublicNote(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "meaning_public_note":
			cudService.updateMeaningPublicNote(itemData.getId(), itemValue);
			break;
		case "image_title":
			cudService.updateImageTitle(itemData.getId(), itemValue);
			break;
		case "meaning_semantic_type":
			cudService.updateMeaningSemanticType(itemData.getId(), itemData.getCurrentValue(), itemValue);
			break;
		case "od_word_recommendation":
			cudService.updateOdWordRecommendation(itemData.getId(), itemValue);
			break;
		case "od_lexeme_recommendation":
			cudService.updateOdLexemeRecommendation(itemData.getId(), itemValue);
			break;
		case "od_usage_definition":
			cudService.updateOdUsageDefinition(itemData.getId(), itemValue);
			break;
		case "od_usage_alternative":
			cudService.updateOdUsageAlternative(itemData.getId(), itemValue);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(UPDATE_ORDERING_URI)
	public String updateOrdering(@RequestBody UpdateListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update ordering {}", listData);

		List<ListData> items = listData.getItems();
		switch (listData.getOpCode()) {
		case "definition":
			cudService.updateDefinitionOrdering(items);
			break;
		case "lexeme_relation":
			cudService.updateLexemeRelationOrdering(items);
			break;
		case "meaning_relation":
			cudService.updateMeaningRelationOrdering(items);
			break;
		case "word_relation":
			cudService.updateWordRelationOrdering(items);
			break;
		case "word_etymology":
			cudService.updateWordEtymologyOrdering(items);
			break;
		case "lexeme":
			cudService.updateLexemeOrdering(items);
			break;
		case "meaning_domain":
			cudService.updateMeaningDomainOrdering(items);
			break;
		case "government":
			cudService.updateGovernmentOrdering(items);
			break;
		case "usage":
			cudService.updateUsageOrdering(items);
			break;
		case "lexeme_meaning_word":
			Long lexemeId = listData.getAdditionalInfo();
			cudService.updateLexemeMeaningWordOrdering(items, lexemeId);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(UPDATE_LEVELS_URI)
	public String updateLexemeLevels(@RequestParam("id") Long lexemeId, @RequestParam("action") String action) {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);

		cudService.updateLexemeLevels(lexemeId, action);

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@PostMapping(CONFIRM_OP_URI)
	public ConfirmationRequest confirmOperation(@RequestBody ConfirmationRequest confirmationRequest, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String opName = confirmationRequest.getOpName();
		String opCode = confirmationRequest.getOpCode();
		Long id = confirmationRequest.getId();

		logger.debug("Confirmation request: {} {} {}", opName, opCode, id);

		DatasetPermission userRole;

		switch (opName) {
		case "delete":
			switch (opCode) {
			case "lexeme":
				return complexOpService.validateLexemeDelete(id);
			case "meaning":
				userRole = sessionBean.getUserRole();
				return complexOpService.validateMeaningDelete(id, userRole);
			case "rus_meaning_lexemes":
				userRole = sessionBean.getUserRole();
				return complexOpService.validateLexemeAndMeaningLexemesDelete(id, LANGUAGE_CODE_RUS, userRole);
			}
		}
		throw new UnsupportedOperationException("Unsupported confirm operation: " + opName + " " + opCode);
	}

	@ResponseBody
	@PostMapping(DELETE_ITEM_URI)
	public String deleteItem(
			@RequestParam("opCode") String opCode,
			@RequestParam("id") Long id,
			@RequestParam(value = "value", required = false) String valueToDelete,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Delete operation : {} : for id {}, value {}", opCode, id, valueToDelete);

		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return "NOK";
		}
		String datasetCode = userRole.getDatasetCode();

		switch (opCode) {
		case "definition":
			cudService.deleteDefinition(id);
			break;
		case "definition_public_note":
			cudService.deleteDefinitionPublicNote(id);
			break;
		case "usage":
			cudService.deleteUsage(id);
			break;
		case "usage_translation":
			cudService.deleteUsageTranslation(id);
			break;
		case "usage_definition":
			cudService.deleteUsageDefinition(id);
			break;
		case "usage_author":
		case "usage_source_link":
			cudService.deleteUsageSourceLink(id);
			break;
		case "lexeme_ff_source_link":
			cudService.deleteFreeformSourceLink(id, LifecycleEntity.LEXEME);
			break;
		case "meaning_ff_source_link":
			cudService.deleteFreeformSourceLink(id, LifecycleEntity.MEANING);
			break;
		case "definition_ff_source_link":
			cudService.deleteFreeformSourceLink(id, LifecycleEntity.DEFINITION);
			break;
		case "government":
			cudService.deleteLexemeGovernment(id);
			break;
		case "lexeme_public_note":
			cudService.deleteLexemePublicNote(id);
			break;
		case "lexeme_frequency_group":
			cudService.updateLexemeFrequencyGroup(id, null);
			break;
		case "lexeme_pos":
			cudService.deleteLexemePos(id, valueToDelete);
			break;
		case "lexeme_deriv":
			cudService.deleteLexemeDeriv(id, valueToDelete);
			break;
		case "lexeme_register":
			cudService.deleteLexemeRegister(id, valueToDelete);
			break;
		case "lexeme_region":
			cudService.deleteLexemeRegion(id, valueToDelete);
			break;
		case "lexeme_grammar":
			cudService.deleteLexemeGrammar(id);
			break;
		case "lexeme_relation":
			cudService.deleteLexemeRelation(id);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(id, null);
			break;
		case "lexeme":
			cudService.deleteLexeme(id);
			break;
		case "rus_meaning_lexemes":
			cudService.deleteLexemeAndMeaningLexemes(id, LANGUAGE_CODE_RUS, datasetCode);
			break;
		case "learner_comment":
			cudService.deleteMeaningLearnerComment(id);
			break;
		case "meaning":
			cudService.deleteMeaningAndLexemes(id, datasetCode);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(valueToDelete);
			cudService.deleteMeaningDomain(id, meaningDomain);
			break;
		case "meaning_public_note":
			cudService.deleteMeaningPublicNote(id);
			break;
		case "meaning_relation":
			cudService.deleteMeaningRelation(id);
			break;
		case "word_gender":
			cudService.updateWordGender(id, null);
			break;
		case "meaning_image":
			cudService.deleteMeaningImage(id);
			break;
		case "word_type":
			cudService.deleteWordType(id, valueToDelete);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id, null);
			break;
		case "word_relation":
			cudService.deleteWordRelation(id);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(id, null);
			break;
		case "image_title":
			cudService.deleteImageTitle(id);
			break;
		case "meaning_semantic_type":
			cudService.deleteMeaningSemanticType(id, valueToDelete);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			cudService.deleteDefinitionSourceLink(id);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			cudService.deleteLexemeSourceLink(id);
			break;
		case "od_word_recommendation":
			cudService.deleteOdWordRecommendation(id);
			break;
		case "od_lexeme_recommendation":
			cudService.deleteOdLexemeRecommendation(id);
			break;
		case "od_usage_definition":
			cudService.deleteOdUsageDefinition(id);
			break;
		case "od_usage_alternative":
			cudService.deleteOdUsageAlternative(id);
			break;
		}
		return RESPONSE_OK_VER1;
	}

	@PostMapping(UPDATE_WORD_VALUE_URI)
	@ResponseBody
	public String updateWordValue(@RequestParam("wordId") Long wordId, @RequestParam("value") String value) {

		value = valueUtil.trimAndCleanAndRemoveHtml(value);

		logger.debug("Updating word value, wordId: \"{}\", valuePrese: \"{}\"", wordId, value);

		cudService.updateWordValue(wordId, value);

		return value;
	}

	@PostMapping(CREATE_RELATIONS_URI)
	@ResponseBody
	public String createRelations(
			@RequestParam("opCode") String opCode,
			@RequestParam("relationType") String relationType,
			@RequestParam(name = "oppositeRelationType", required = false) String oppositeRelationType,
			@RequestParam("id1") Long id1,
			@RequestParam("ids") List<Long> ids) {

		for (Long id2 : ids) {
			switch (opCode) {
			case "meaning_relation":
				cudService.createMeaningRelation(id1, id2, relationType, oppositeRelationType);
				break;
			case "lexeme_relation":
				cudService.createLexemeRelation(id1, id2, relationType, oppositeRelationType);
				break;
			case "word_relation":
				cudService.createWordRelation(id1, id2, relationType, oppositeRelationType);
				break;
			}
		}
		return RESPONSE_OK_VER1;
	}

	@PostMapping(OPPOSITE_RELATIONS_URI)
	@ResponseBody
	public List<Classifier> getOppositeRelations(@RequestParam("entity") LifecycleEntity entity, @RequestParam("relationType") String relationTypeCode) {
		return lookupService.getOppositeRelations(entity, relationTypeCode);
	}

}
