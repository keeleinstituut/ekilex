package eki.ekilex.web.controller;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.Complexity;
import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DeleteItemRequest;
import eki.ekilex.data.LexemeDeleteConfirmation;
import eki.ekilex.data.ListData;
import eki.ekilex.data.MeaningDeleteConfirmation;
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
		String sourceLinkValue;
		String datasetCode;

		switch (itemData.getOpCode()) {
		case "definition":
			datasetCode = itemData.getDataset();
			cudService.createDefinition(itemData.getId(), itemValue, language, datasetCode, itemData.getComplexity(), itemData.getItemType(), itemData.isPublic());
			break;
		case "definition_note":
			cudService.createDefinitionNote(itemData.getId(), itemValue, language, itemData.isPublic());
			break;
		case "usage":
			cudService.createUsage(itemData.getId(), itemValue, language, itemData.getComplexity(), itemData.isPublic());
			break;
		case "usage_translation":
			cudService.createUsageTranslation(itemData.getId(), itemValue, language);
			break;
		case "usage_definition":
			cudService.createUsageDefinition(itemData.getId(), itemValue, language);
			break;
		case "lexeme_pos":
			cudService.createLexemePos(itemData.getId(), itemValue);
			break;
		case "lexeme_tag":
			cudService.createLexemeTag(itemData.getId(), itemValue);
			break;
		case "meaning_tag":
			cudService.createMeaningTag(itemData.getId(), itemValue);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(itemValue);
			cudService.createMeaningDomain(itemData.getId2(), meaningDomain);
			break;
		case "government":
			cudService.createLexemeGovernment(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			sourceLinkValue = getSourcePropertyValue(itemData.getId3());
			sourceLinkService.createDefinitionSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceLinkValue, itemValue);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			sourceLinkValue = getSourcePropertyValue(itemData.getId3());
			sourceLinkService.createLexemeSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceLinkValue, itemValue);
			break;
		case ContentKey.FREEFORM_SOURCE_LINK:
			sourceLinkValue = getSourcePropertyValue(itemData.getId3());
			sourceLinkService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceLinkValue, itemValue);
			break;
		case "usage_author":
			sourceLinkValue = getSourceNameValue(itemData.getId2());
			ReferenceType refType = ReferenceType.valueOf(itemData.getItemType());
			sourceLinkService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), refType, sourceLinkValue, null);
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
		case "lexeme_reliability":
			cudService.updateLexemeReliability(itemData.getId(), itemValue);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(itemData.getId3(), itemValue, userId, userRole);
			break;
		case "word_type":
			cudService.createWordTypeWithDuplication(itemData.getId3(), itemValue, userId, userRole);
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemData.getId3(), itemValue);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(itemData.getId3(), itemValue);
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
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(itemData.getId(), itemValue);
			break;
		case "learner_comment":
			cudService.createMeaningLearnerComment(itemData.getId(), itemValue, language);
			break;
		case "lexeme_note":
			cudService.createLexemeNote(itemData.getId(), itemValue, language, itemData.getComplexity(), itemData.isPublic());
			break;
		case "meaning_note":
			cudService.createMeaningNote(itemData.getId(), itemValue, language, itemData.getComplexity(), itemData.isPublic());
			sessionBean.setRecentNoteLanguage(language);
			break;
		case "word_note":
			cudService.createWordNoteWithDuplication(itemData.getId(), itemValue, userId, userRole);
			break;
		case "meaning_image":
			cudService.createMeaningImage(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "image_title":
			cudService.createImageTitle(itemData.getId(), itemValue);
			break;
		case "meaning_media":
			cudService.createMeaningMedia(itemData.getId(), itemValue, itemData.getComplexity());
			break;
		case "create_syn_word":
			datasetCode = getDatasetCodeFromRole();
			cudService.createWordAndSynRelation(itemData.getId(), itemValue, datasetCode, language, itemData.getValue2());
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

		switch (itemData.getOpCode()) {
		case "user_lang_selection":
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
			ClassifierSelect langSelect = languagesOrder.stream().filter(classif -> StringUtils.equals(classif.getCode(), itemCode)).findFirst().get();
			langSelect.setSelected(!langSelect.isSelected());
			break;
		case "usage":
			cudService.updateUsageValue(itemId, itemValue, itemComplexity, isPublic);
			break;
		case "usage_translation":
			cudService.updateUsageTranslation(itemId, itemValue, itemLanguage);
			break;
		case "usage_definition":
			cudService.updateUsageDefinitionValue(itemId, itemValue);
			break;
		case "definition":
			cudService.updateDefinition(itemId, itemValue, itemLanguage, itemComplexity, itemCode, isPublic);
			break;
		case "definition_note":
			cudService.updateDefinitionNote(itemId, itemValue, itemLanguage, isPublic);
			break;
		case "lexeme_complexity":
			cudService.updateLexemeComplexity(itemId, itemValue);
			break;
		case "lexeme_pos":
			cudService.updateLexemePos(itemId, itemCurrentValue, itemValue);
			break;
		case "meaning_domain":
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(itemCurrentValue);
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			cudService.updateMeaningDomain(itemId, currentMeaningDomain, newMeaningDomain);
			break;
		case "government":
			cudService.updateLexemeGovernment(itemId, itemValue, itemComplexity);
			break;
		case "lexeme_deriv":
			cudService.updateLexemeDeriv(itemId, itemCurrentValue, itemValue);
			break;
		case "lexeme_register":
			cudService.updateLexemeRegister(itemId, itemCurrentValue, itemValue);
			break;
		case "lexeme_region":
			cudService.updateLexemeRegion(itemId, itemCurrentValue, itemValue);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(itemId, itemValue);
			break;
		case "lexeme_weight":
			cudService.updateLexemeWeight(itemId, itemValue);
			break;
		case "meaning_relation_weight":
			cudService.updateMeaningRelationWeight(itemId, itemValue);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(itemId, itemValue, userId, userRole);
			break;
		case "word_type":
			cudService.updateWordTypeWithDuplication(itemId, itemCurrentValue, itemValue, userId, userRole);
			break;
		case "lexeme_grammar":
			cudService.updateLexemeGrammar(itemId, itemValue, itemComplexity);
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemId, itemValue);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(itemId, itemValue);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(itemId, itemValue);
			break;
		case "word_lang":
			cudService.updateWordLang(itemId, itemValue);
			break;
		case "word_manual_event_on":
			Timestamp wordManualEventOn = conversionUtil.dateStrToTimestamp(itemValue);
			cudService.updateWordManualUpdateOn(itemId, wordManualEventOn);
			break;
		case "lexeme_publicity":
			cudService.updateLexemePublicity(itemId, isPublic);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemId, itemValue);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(itemId, itemValue);
			break;
		case "learner_comment":
			cudService.updateMeaningLearnerComment(itemId, itemValue);
			break;
		case "lexeme_note":
			cudService.updateLexemeNote(itemId, itemValue, itemLanguage, itemComplexity, isPublic);
			break;
		case "meaning_note":
			cudService.updateMeaningNote(itemId, itemValue, itemLanguage, itemComplexity, isPublic);
			break;
		case "word_note":
			cudService.updateWordNote(itemId, itemValue);
			break;
		case "meaning_image":
			cudService.updateMeaningImage(itemId, itemValue, itemComplexity);
			break;
		case "image_title":
			cudService.updateImageTitle(itemId, itemValue);
			break;
		case "meaning_media":
			cudService.updateMeaningMedia(itemId, itemValue, itemComplexity);
			break;
		case "meaning_semantic_type":
			cudService.updateMeaningSemanticType(itemId, itemCurrentValue, itemValue);
			break;
		case "meaning_manual_event_on":
			Timestamp meaningManualEventOn = conversionUtil.dateStrToTimestamp(itemValue);
			cudService.updateMeaningManualUpdateOn(itemId, meaningManualEventOn);
			break;
		case "od_word_recommendation":
			cudService.updateOdWordRecommendation(itemId, itemValue);
			break;
		case "od_lexeme_recommendation":
			cudService.updateOdLexemeRecommendation(itemId, itemValue);
			break;
		case "od_usage_definition":
			cudService.updateOdUsageDefinition(itemId, itemValue);
			break;
		case "od_usage_alternative":
			cudService.updateOdUsageAlternative(itemId, itemValue);
			break;
		case ContentKey.FREEFORM_SOURCE_LINK:
			String ffSourceLinkValue = getSourcePropertyValue(itemData.getId2());
			sourceLinkService.updateFreeformSourceLink(itemId, ffSourceLinkValue, itemValue);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			String lexSourceLinkValue = getSourcePropertyValue(itemData.getId2());
			sourceLinkService.updateLexemeSourceLink(itemId, lexSourceLinkValue, itemValue);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			String defSourceLinkValue = getSourcePropertyValue(itemData.getId2());
			sourceLinkService.updateDefinitionSourceLink(itemId, defSourceLinkValue, itemValue);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(UPDATE_ORDERING_URI)
	public String updateOrdering(@RequestBody UpdateListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

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
		case "lexeme_note":
			cudService.updateLexemeNoteOrdering(items);
			break;
		case "meaning_note":
			cudService.updateMeaningNoteOrdering(items);
			break;
		case "definition_note":
			cudService.updateDefinitionNoteOrdering(items);
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
	public String updateLexemeLevels(@RequestParam("id") Long lexemeId, @RequestParam("action") String action) throws Exception {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);

		cudService.updateLexemeLevels(lexemeId, action);

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@PostMapping(UPDATE_LEXEME_LEVELS_URI)
	public String updateLexemeLevels(@RequestBody UpdateLexemeLevelsRequest updateLexemeLevelsRequest) throws Exception {

		Long lexemeId = updateLexemeLevelsRequest.getLexemeId();
		Integer position = updateLexemeLevelsRequest.getPosition();
		logger.debug("Change lexeme levels for id {}, new position {}", lexemeId, position);

		cudService.updateLexemeLevels(lexemeId, position);

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
	public String deleteItem(
			@RequestParam("opCode") String opCode,
			@RequestParam("id") Long id,
			@RequestParam(value = "value", required = false) String valueToDelete) throws Exception {

		logger.debug("Delete operation : {} : for id {}, value {}", opCode, id, valueToDelete);

		Long userId = userContext.getUserId();
		DatasetPermission userRole = userContext.getUserRole();
		if (userRole == null) {
			return "NOK";
		}
		String datasetCode = userRole.getDatasetCode();

		switch (opCode) {
		case "definition":
			cudService.deleteDefinition(id);
			break;
		case "definition_note":
			cudService.deleteDefinitionNote(id);
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
		case ContentKey.FREEFORM_SOURCE_LINK:
			sourceLinkService.deleteFreeformSourceLink(id);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			sourceLinkService.deleteDefinitionSourceLink(id);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			sourceLinkService.deleteLexemeSourceLink(id);
			break;
		case "government":
			cudService.deleteLexemeGovernment(id);
			break;
		case "lexeme_note":
			cudService.deleteLexemeNote(id);
			break;
		case "lexeme_pos":
			cudService.deleteLexemePos(id, valueToDelete);
			break;
		case "lexeme_tag":
			cudService.deleteLexemeTag(id, valueToDelete);
			break;
		case "meaning_tag":
			cudService.deleteMeaningTag(id, valueToDelete);
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
		case "lexeme_reliability":
			cudService.updateLexemeReliability(id, null);
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
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(id, null);
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
		case "meaning_note":
			cudService.deleteMeaningNote(id);
			break;
		case "meaning_relation":
			cudService.deleteMeaningRelation(id);
			break;
		case "syn_meaning_relation":
			cudService.deleteSynMeaningRelation(id);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(id, null, userId, userRole);
			break;
		case "meaning_image":
			cudService.deleteMeaningImage(id);
			break;
		case "meaning_media":
			cudService.deleteMeaningMedia(id);
			break;
		case "word_type":
			cudService.deleteWordTypeWithDuplication(id, valueToDelete, userId, userRole);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id, null);
			break;
		case "word_relation":
			cudService.deleteWordRelation(id);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(id, null);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(id, null);
			break;
		case "word_note":
			cudService.deleteWordNote(id);
			break;
		case "image_title":
			cudService.deleteImageTitle(id);
			break;
		case "meaning_semantic_type":
			cudService.deleteMeaningSemanticType(id, valueToDelete);
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
		case "paradigm":
			cudService.deleteParadigm(id);
			break;
		}
		return RESPONSE_OK_VER1;
	}

	@PostMapping(UPDATE_WORD_VALUE_URI)
	@ResponseBody
	public String updateWordValue(@RequestParam("wordId") Long wordId, @RequestParam("value") String value) throws Exception {

		Long userId = userContext.getUserId();
		DatasetPermission userRole = userContext.getUserRole();
		value = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(value);

		logger.debug("Updating word value, wordId: \"{}\", valuePrese: \"{}\"", wordId, value);

		cudService.updateWordValueWithDuplication(wordId, value, userId, userRole);

		return value;
	}

	@PostMapping(CREATE_RELATIONS_URI)
	@ResponseBody
	public String createRelations(
			@RequestParam("opCode") String opCode,
			@RequestParam(name = "relationType", required = false) String relationType,
			@RequestParam(name = "oppositeRelationType", required = false) String oppositeRelationType,
			@RequestParam(name = "weight", required = false) String weightStr,
			@RequestParam("id") Long id1,
			@RequestParam("ids") List<Long> ids) throws Exception {

		String datasetCode = null;
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
			case "syn_meaning_relation":
				synSearchService.createSynMeaningRelation(id1, id2, weightStr);
				break;
			case "raw_relation":
				if (datasetCode == null) {
					datasetCode = getDatasetCodeFromRole();
				}
				cudService.createSynWordRelation(id1, id2, weightStr, datasetCode);
				break;
			}
		}
		return RESPONSE_OK_VER1;
	}

}
