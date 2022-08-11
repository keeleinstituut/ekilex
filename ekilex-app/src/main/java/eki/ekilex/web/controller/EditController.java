package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.Complexity;
import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DeleteItemRequest;
import eki.ekilex.data.LexemeDeleteConfirmation;
import eki.ekilex.data.ListData;
import eki.ekilex.data.MeaningDeleteConfirmation;
import eki.ekilex.data.Response;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.data.UpdateLexemeLevelsRequest;
import eki.ekilex.data.UpdateListRequest;
import eki.ekilex.service.ComplexOpService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.SourceLinkService;
import eki.ekilex.service.SourceService;
import eki.ekilex.service.SynSearchService;
import eki.ekilex.service.util.ConversionUtil;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class EditController extends AbstractMutableDataPageController {

	private static final Logger logger = LoggerFactory.getLogger(EditController.class);

	@Autowired
	private CudService cudService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Autowired
	private SourceService sourceService;

	@Autowired
	private SourceLinkService sourceLinkService;

	@Autowired
	private ComplexOpService complexOpService;

	@Autowired
	private SynSearchService synSearchService;

	@ResponseBody
	@PostMapping(CREATE_ITEM_URI)
	public String createItem(@RequestBody CreateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Add new item : {}", itemData);

		Long userId = userContext.getUserId();
		DatasetPermission userRole = userContext.getUserRole();
		String itemValue = itemData.getValue();
		itemValue = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(itemValue);
		String language = itemData.getLanguage();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String sourceLinkValue;
		String datasetCode;

		switch (itemData.getOpCode()) {
		case "definition":
			datasetCode = itemData.getDataset();
			cudService.createDefinition(itemData.getId(), itemValue, language, datasetCode, itemData.getComplexity(), itemData.getItemType(), itemData.isPublic(), isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.createDefinitionNote(itemData.getId(), itemValue, language, itemData.isPublic(), isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.createUsage(itemData.getId(), itemValue, language, itemData.getComplexity(), itemData.isPublic(), isManualEventOnUpdateEnabled);
			break;
		case "usage_translation":
			cudService.createUsageTranslation(itemData.getId(), itemValue, language, isManualEventOnUpdateEnabled);
			break;
		case "usage_definition":
			cudService.createUsageDefinition(itemData.getId(), itemValue, language, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_pos":
			cudService.createLexemePos(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_tag":
			cudService.createLexemeTag(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_tag":
			cudService.createMeaningTag(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(itemValue);
			cudService.createMeaningDomain(itemData.getId2(), meaningDomain, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.createLexemeGovernment(itemData.getId(), itemValue, itemData.getComplexity(), isManualEventOnUpdateEnabled);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			sourceLinkValue = getSourcePropertyValue(itemData.getId3());
			sourceLinkService.createDefinitionSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceLinkValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			sourceLinkValue = getSourcePropertyValue(itemData.getId3());
			sourceLinkService.createLexemeSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceLinkValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.FREEFORM_SOURCE_LINK:
			sourceLinkValue = getSourcePropertyValue(itemData.getId3());
			sourceLinkService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceLinkValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "usage_author":
			sourceLinkValue = getSourceNameValue(itemData.getId2());
			ReferenceType refType = ReferenceType.valueOf(itemData.getItemType());
			sourceLinkService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), refType, sourceLinkValue, null, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_deriv":
			cudService.createLexemeDeriv(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_register":
			cudService.createLexemeRegister(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_region":
			cudService.createLexemeRegion(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(itemData.getId3(), itemValue, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "word_type":
			cudService.createWordTypeWithDuplication(itemData.getId3(), itemValue, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemData.getId3(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(itemData.getId3(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_morphophono_form":
			cudService.updateWordMorphophonoForm(itemData.getId3(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(itemData.getId3(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_lang":
			cudService.updateWordLang(itemData.getId3(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_grammar":
			cudService.createLexemeGrammar(itemData.getId(), itemValue, itemData.getComplexity(), isManualEventOnUpdateEnabled);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "learner_comment":
			cudService.createMeaningLearnerComment(itemData.getId(), itemValue, language, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.createLexemeNote(itemData.getId(), itemValue, language, itemData.getComplexity(), itemData.isPublic(), isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.createMeaningNote(itemData.getId(), itemValue, language, itemData.getComplexity(), itemData.isPublic(), isManualEventOnUpdateEnabled);
			sessionBean.setRecentNoteLanguage(language);
			break;
		case "word_note":
			cudService.createWordNoteWithDuplication(itemData.getId(), itemValue, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "meaning_image":
			cudService.createMeaningImage(itemData.getId(), itemValue, itemData.getComplexity(), isManualEventOnUpdateEnabled);
			break;
		case "image_title":
			cudService.createImageTitle(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_media":
			cudService.createMeaningMedia(itemData.getId(), itemValue, itemData.getComplexity(), isManualEventOnUpdateEnabled);
			break;
		case "create_syn_word":
			datasetCode = getDatasetCodeFromRole();
			cudService.createWordAndSynRelation(itemData.getId(), itemValue, datasetCode, language, itemData.getValue2(), isManualEventOnUpdateEnabled);
			break;
		case "meaning_semantic_type":
			cudService.createMeaningSemanticType(itemData.getId2(), itemValue, isManualEventOnUpdateEnabled);
			break;
		case "od_word_recommendation":
			cudService.createOdWordRecommendation(itemData.getId(), itemValue, isManualEventOnUpdateEnabled);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	private String getSourcePropertyValue(Long sourcePropertyId) {
		SourceProperty sourceProperty = sourceService.getSourceProperty(sourcePropertyId);
		return sourceProperty.getValueText();
	}

	private String getSourceNameValue(Long sourceId) {
		return sourceService.getSourceNameValue(sourceId);
	}

	@ResponseBody
	@PostMapping(UPDATE_ITEM_URI)
	public String updateItem(@RequestBody UpdateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Update item : {}", itemData);

		Long userId = userContext.getUserId();
		DatasetPermission userRole = userContext.getUserRole();
		Long itemId = itemData.getId();
		String itemValue = itemData.getValue();
		itemValue = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(itemValue);
		String itemCurrentValue = itemData.getCurrentValue();
		String itemLanguage = itemData.getLanguage();
		String itemCode = itemData.getCode();
		Complexity itemComplexity = itemData.getComplexity();
		boolean isPublic = itemData.isPublic();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		switch (itemData.getOpCode()) {
		case "user_lang_selection":
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
			ClassifierSelect langSelect = languagesOrder.stream().filter(classif -> StringUtils.equals(classif.getCode(), itemCode)).findFirst().get();
			langSelect.setSelected(!langSelect.isSelected());
			break;
		case "usage":
			cudService.updateUsageValue(itemId, itemValue, itemComplexity, isPublic, isManualEventOnUpdateEnabled);
			break;
		case "usage_translation":
			cudService.updateUsageTranslation(itemId, itemValue, itemLanguage, isManualEventOnUpdateEnabled);
			break;
		case "usage_definition":
			cudService.updateUsageDefinitionValue(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "definition":
			cudService.updateDefinition(itemId, itemValue, itemLanguage, itemComplexity, itemCode, isPublic, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.updateDefinitionNote(itemId, itemValue, itemLanguage, isPublic, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_complexity":
			cudService.updateLexemeComplexity(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_pos":
			cudService.updateLexemePos(itemId, itemCurrentValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(itemCurrentValue);
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			cudService.updateMeaningDomain(itemId, currentMeaningDomain, newMeaningDomain, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.updateLexemeGovernment(itemId, itemValue, itemComplexity, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_deriv":
			cudService.updateLexemeDeriv(itemId, itemCurrentValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_register":
			cudService.updateLexemeRegister(itemId, itemCurrentValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_region":
			cudService.updateLexemeRegion(itemId, itemCurrentValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_weight":
			cudService.updateLexemeWeight(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_relation_weight":
			cudService.updateMeaningRelationWeight(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_value":
			cudService.updateWordValueWithDuplication(itemId, itemValue, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(itemId, itemValue, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "word_type":
			cudService.updateWordTypeWithDuplication(itemId, itemCurrentValue, itemValue, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "word_data_and_lexeme_weight":
			cudService.updateWordDataAndLexemeWeight(itemId, itemData.getId2(), itemValue, itemData.getLexemeWeight(), isManualEventOnUpdateEnabled);
			break;
		case "lexeme_grammar":
			cudService.updateLexemeGrammar(itemId, itemValue, itemComplexity, isManualEventOnUpdateEnabled);
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_morphophono_form":
			cudService.updateWordMorphophonoForm(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_lang":
			cudService.updateWordLang(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "word_manual_event_on":
			cudService.updateWordManualEventOn(itemId, itemValue);
			break;
		case "lexeme_publicity":
			cudService.updateLexemePublicity(itemId, isPublic, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "learner_comment":
			cudService.updateMeaningLearnerComment(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.updateLexemeNote(itemId, itemValue, itemLanguage, itemComplexity, isPublic, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.updateMeaningNote(itemId, itemValue, itemLanguage, itemComplexity, isPublic, isManualEventOnUpdateEnabled);
			break;
		case "word_note":
			cudService.updateWordNote(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_image":
			cudService.updateMeaningImage(itemId, itemValue, itemComplexity, isManualEventOnUpdateEnabled);
			break;
		case "image_title":
			cudService.updateImageTitle(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_media":
			cudService.updateMeaningMedia(itemId, itemValue, itemComplexity, isManualEventOnUpdateEnabled);
			break;
		case "meaning_semantic_type":
			cudService.updateMeaningSemanticType(itemId, itemCurrentValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case "meaning_manual_event_on":
			cudService.updateMeaningManualEventOn(itemId, itemValue);
			break;
		case "od_word_recommendation":
			cudService.updateOdWordRecommendation(itemId, itemValue, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.FREEFORM_SOURCE_LINK:
			String ffSourceLinkValue = getSourcePropertyValue(itemData.getId2());
			sourceLinkService.updateFreeformSourceLink(itemId, ffSourceLinkValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			String lexSourceLinkValue = getSourcePropertyValue(itemData.getId2());
			sourceLinkService.updateLexemeSourceLink(itemId, lexSourceLinkValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			String defSourceLinkValue = getSourcePropertyValue(itemData.getId2());
			sourceLinkService.updateDefinitionSourceLink(itemId, defSourceLinkValue, itemValue, isManualEventOnUpdateEnabled);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(UPDATE_ORDERING_URI)
	public String updateOrdering(@RequestBody UpdateListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Update ordering {}", listData);

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		List<ListData> items = listData.getItems();

		switch (listData.getOpCode()) {
		case "definition":
			cudService.updateDefinitionOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_relation":
			cudService.updateLexemeRelationOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "meaning_relation":
			cudService.updateMeaningRelationOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "word_relation":
			cudService.updateWordRelationOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "word_etymology":
			cudService.updateWordEtymologyOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "lexeme":
			cudService.updateLexemeOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			cudService.updateMeaningDomainOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.updateGovernmentOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.updateUsageOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.updateLexemeNoteOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.updateMeaningNoteOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.updateDefinitionNoteOrdering(items, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_meaning_word":
			Long lexemeId = listData.getAdditionalInfo();
			cudService.updateLexemeMeaningWordOrdering(items, lexemeId, isManualEventOnUpdateEnabled);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(UPDATE_LEVELS_URI)
	public String updateLexemeLevels(
			@RequestParam("id") Long lexemeId,
			@RequestParam("action") String action,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		cudService.updateLexemeLevels(lexemeId, action, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@PostMapping(UPDATE_LEXEME_LEVELS_URI)
	public String updateLexemeLevels(
			@RequestBody UpdateLexemeLevelsRequest updateLexemeLevelsRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		Long lexemeId = updateLexemeLevelsRequest.getLexemeId();
		Integer position = updateLexemeLevelsRequest.getPosition();
		logger.debug("Change lexeme levels for id {}, new position {}", lexemeId, position);

		cudService.updateLexemeLevels(lexemeId, position, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}

	@PostMapping(CONFIRM_OP_URI)
	public String confirmOperation(@RequestBody DeleteItemRequest deleteItemRequest, Model model) {

		String opName = deleteItemRequest.getOpName();
		String opCode = deleteItemRequest.getOpCode();
		Long id = deleteItemRequest.getId();

		logger.debug("Confirmation request: {} {} {}", opName, opCode, id);

		DatasetPermission userRole = userContext.getUserRole();

		switch (opName) {
		case "delete":
			switch (opCode) {
			case "lexeme":
				LexemeDeleteConfirmation lexemeDeleteConfirmation = complexOpService.validateLexemeDelete(id, userRole);
				model.addAttribute("lexemeDeleteConfirmation", lexemeDeleteConfirmation);
				return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lexeme_delete_confirmation";
			case "meaning":
				MeaningDeleteConfirmation meaningDeleteConfirmation = complexOpService.validateMeaningDelete(id, userRole);
				model.addAttribute("meaningDeleteConfirmation", meaningDeleteConfirmation);
				return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "meaning_delete_confirmation";
			case "rus_meaning_lexemes":
				LexemeDeleteConfirmation meaningLexemesDeleteConfirmation = complexOpService.validateLexemeAndMeaningLexemesDelete(id, LANGUAGE_CODE_RUS, userRole);
				model.addAttribute("lexemeDeleteConfirmation", meaningLexemesDeleteConfirmation);
				return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lexeme_delete_confirmation";
			}
		}
		throw new UnsupportedOperationException("Unsupported confirm operation: " + opName + " " + opCode);
	}

	@ResponseBody
	@PostMapping(DELETE_ITEM_URI)
	public Response deleteItem(
			@RequestParam("opCode") String opCode,
			@RequestParam("id") Long id,
			@RequestParam(value = "value", required = false) String valueToDelete,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Delete operation : {} : for id {}, value {}", opCode, id, valueToDelete);

		Response response = new Response();
		Long userId = userContext.getUserId();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		DatasetPermission userRole = userContext.getUserRole();
		if (userRole == null) {
			response.setStatus(ResponseStatus.ERROR);
			return response;
		}

		String datasetCode = userRole.getDatasetCode();
		response.setStatus(ResponseStatus.OK);

		switch (opCode) {
		case "definition":
			cudService.deleteDefinition(id, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.deleteDefinitionNote(id, isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.deleteUsage(id, isManualEventOnUpdateEnabled);
			break;
		case "usage_translation":
			cudService.deleteUsageTranslation(id, isManualEventOnUpdateEnabled);
			break;
		case "usage_definition":
			cudService.deleteUsageDefinition(id, isManualEventOnUpdateEnabled);
			break;
		case "usage_author":
		case ContentKey.FREEFORM_SOURCE_LINK:
			sourceLinkService.deleteFreeformSourceLink(id, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			sourceLinkService.deleteDefinitionSourceLink(id, isManualEventOnUpdateEnabled);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			sourceLinkService.deleteLexemeSourceLink(id, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.deleteLexemeGovernment(id, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.deleteLexemeNote(id, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_pos":
			cudService.deleteLexemePos(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_tag":
			cudService.deleteLexemeTag(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "meaning_tag":
			cudService.deleteMeaningTag(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_deriv":
			cudService.deleteLexemeDeriv(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_register":
			cudService.deleteLexemeRegister(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_region":
			cudService.deleteLexemeRegion(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(id, null, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_grammar":
			cudService.deleteLexemeGrammar(id, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_relation":
			cudService.deleteLexemeRelation(id, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(id, null, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(id, null, isManualEventOnUpdateEnabled);
			break;
		case "lexeme":
			cudService.deleteLexeme(id, isManualEventOnUpdateEnabled);
			break;
		case "rus_meaning_lexemes":
			cudService.deleteLexemeAndMeaningLexemes(id, LANGUAGE_CODE_RUS, datasetCode, isManualEventOnUpdateEnabled);
			break;
		case "learner_comment":
			cudService.deleteMeaningLearnerComment(id, isManualEventOnUpdateEnabled);
			break;
		case "meaning":
			cudService.deleteMeaningAndLexemes(id, datasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(valueToDelete);
			cudService.deleteMeaningDomain(id, meaningDomain, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.deleteMeaningNote(id, isManualEventOnUpdateEnabled);
			break;
		case "meaning_relation":
			response = cudService.deleteMeaningRelation(id, response, isManualEventOnUpdateEnabled);
			break;
		case "syn_meaning_relation":
			response = cudService.deleteMeaningRelation(id, response, isManualEventOnUpdateEnabled);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(id, null, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "meaning_image":
			cudService.deleteMeaningImage(id, isManualEventOnUpdateEnabled);
			break;
		case "meaning_media":
			cudService.deleteMeaningMedia(id, isManualEventOnUpdateEnabled);
			break;
		case "word_type":
			cudService.deleteWordTypeWithDuplication(id, valueToDelete, userId, userRole, isManualEventOnUpdateEnabled);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id, null, isManualEventOnUpdateEnabled);
			break;
		case "word_relation":
			cudService.deleteWordRelation(id, isManualEventOnUpdateEnabled);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(id, null, isManualEventOnUpdateEnabled);
			break;
		case "word_morphophono_form":
			cudService.updateWordMorphophonoForm(id, null, isManualEventOnUpdateEnabled);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(id, null, isManualEventOnUpdateEnabled);
			break;
		case "word_note":
			cudService.deleteWordNote(id, isManualEventOnUpdateEnabled);
			break;
		case "image_title":
			cudService.deleteImageTitle(id, isManualEventOnUpdateEnabled);
			break;
		case "meaning_semantic_type":
			cudService.deleteMeaningSemanticType(id, valueToDelete, isManualEventOnUpdateEnabled);
			break;
		case "od_word_recommendation":
			cudService.deleteOdWordRecommendation(id, isManualEventOnUpdateEnabled);
			break;
		case "paradigm":
			cudService.deleteParadigm(id, isManualEventOnUpdateEnabled);
			break;
		}
		return response;
	}

	@PostMapping(CREATE_RELATIONS_URI)
	@ResponseBody
	public String createRelations(
			@RequestParam("opCode") String opCode,
			@RequestParam(name = "relationType", required = false) String relationType,
			@RequestParam(name = "oppositeRelationType", required = false) String oppositeRelationType,
			@RequestParam(name = "weight", required = false) String weightStr,
			@RequestParam("id") Long id1,
			@RequestParam("ids") List<Long> ids,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String datasetCode = null;
		for (Long id2 : ids) {
			switch (opCode) {
			case "meaning_relation":
				cudService.createMeaningRelation(id1, id2, relationType, oppositeRelationType, isManualEventOnUpdateEnabled);
				break;
			case "lexeme_relation":
				cudService.createLexemeRelation(id1, id2, relationType, oppositeRelationType, isManualEventOnUpdateEnabled);
				break;
			case "word_relation":
				cudService.createWordRelation(id1, id2, relationType, oppositeRelationType, isManualEventOnUpdateEnabled);
				break;
			case "syn_meaning_relation":
				synSearchService.createSynMeaningRelation(id1, id2, weightStr, isManualEventOnUpdateEnabled);
				break;
			case "raw_relation":
				if (datasetCode == null) {
					datasetCode = getDatasetCodeFromRole();
				}
				cudService.createSynWordRelation(id1, id2, weightStr, datasetCode, isManualEventOnUpdateEnabled);
				break;
			}
		}
		return RESPONSE_OK_VER1;
	}

	@GetMapping(MANUAL_EVENT_ON_UPDATE_URI + "/{isEnabled}")
	public String updateManualEventOnEnabled(
			@PathVariable("isEnabled") boolean isEnabled,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Updating manual event enabled to {}", isEnabled);
		sessionBean.setManualEventOnUpdateEnabled(isEnabled);
		return SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "manual_event_on_chk";
	}

}
