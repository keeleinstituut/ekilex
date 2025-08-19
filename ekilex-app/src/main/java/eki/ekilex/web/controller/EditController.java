package eki.ekilex.web.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
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

import eki.common.constant.ContentKey;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DeleteItemRequest;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.LexemeDeleteConfirmation;
import eki.ekilex.data.ListData;
import eki.ekilex.data.MeaningDeleteConfirmation;
import eki.ekilex.data.Response;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.data.UpdateLexemeLevelsRequest;
import eki.ekilex.data.UpdateListRequest;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.ComplexOpService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.SourceLinkService;
import eki.ekilex.service.SynCandidateService;
import eki.ekilex.service.SynCudService;
import eki.ekilex.service.util.ConversionUtil;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class EditController extends AbstractMutableDataPageController implements ContentKey {

	private static final Logger logger = LoggerFactory.getLogger(EditController.class);

	@Autowired
	private CudService cudService;

	@Autowired
	private SynCudService synCudService;

	@Autowired
	private SynCandidateService synCandidateService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Autowired
	private SourceLinkService sourceLinkService;

	@Autowired
	private ComplexOpService complexOpService;

	@ResponseBody
	@PostMapping(CREATE_ITEM_URI)
	public Response createItem(@RequestBody CreateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		logger.debug("Create item: {}; auto update: {}", itemData, isManualEventOnUpdateEnabled);

		EkiUser user = userContext.getUser();
		String roleDatasetCode = getRoleDatasetCode();

		Long id = itemData.getId();
		Long id2 = itemData.getId2();
		Long id3 = itemData.getId3();
		String value = itemData.getValue();
		value = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(value);
		String value2 = itemData.getValue2();
		String type = itemData.getItemType();
		String languageCode = itemData.getLanguage();
		String datasetCode = itemData.getDataset();
		boolean isPublic = itemData.isPublic();

		switch (itemData.getOpCode()) {
		case "definition":
			cudService.createDefinition(id, value, languageCode, datasetCode, type, isPublic, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.createDefinitionNote(id, value, languageCode, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.createUsage(id, value, languageCode, isPublic, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage_translation":
			cudService.createUsageTranslation(id, value, languageCode, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage_definition":
			cudService.createUsageDefinition(id, value, languageCode, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_pos":
			cudService.createLexemePos(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_tag":
			cudService.createLexemeTag(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_freeform":
			cudService.createLexemeFreeform(id, value, type, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.createLexemeGovernment(id, value, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_deriv":
			cudService.createLexemeDeriv(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_register":
			cudService.createLexemeRegister(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_region":
			cudService.createLexemeRegion(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_grammar":
			cudService.createLexemeGrammar(id, value, languageCode, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.createLexemeNote(id, value, languageCode, isPublic, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(id3, value, user, isManualEventOnUpdateEnabled);
			break;
		case "word_type":
			cudService.createWordTypeWithDuplication(id3, value, user, isManualEventOnUpdateEnabled);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id3, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_tag":
			cudService.createWordTag(id3, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(id3, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_morphophono_form":
			cudService.updateWordMorphophonoForm(id3, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(id3, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_lang":
			cudService.updateWordLang(id3, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_forum":
			cudService.createWordForum(id, value, user);
			break;
		case "word_freeform":
			cudService.createWordFreeform(id, value, type, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_morph":
			cudService.createWordOsMorph(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_recommendation":
			cudService.createWordOsRecommendation(id, value, value2, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_usage":
			cudService.createWordOsUsage(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "create_syn_word":
			synCudService.createWordAndSynRelation(id, value, value2, languageCode, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "full_syn_candidate":
			UserContextData userContextData = getUserContextData();
			String candidateLang = userContextData.getFullSynCandidateLangCode();
			String candidateDatasetCode = userContextData.getFullSynCandidateDatasetCode();
			Response response = synCandidateService.createFullSynCandidate(id, value, candidateLang, candidateDatasetCode, roleDatasetCode);
			return response;
		case "learner_comment":
			cudService.createMeaningLearnerComment(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.createMeaningNote(id, value, languageCode, isPublic, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			sessionBean.setRecentNoteLanguage(languageCode);
			break;
		case "meaning_forum":
			cudService.createMeaningForum(id, value, user);
			break;
		case "meaning_image":
			cudService.createMeaningImage(id, value, value2, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_media":
			cudService.createMeaningMedia(id, value, user, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_freeform":
			cudService.createMeaningFreeform(id, value, type, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_semantic_type":
			cudService.createMeaningSemanticType(id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_tag":
			cudService.createMeaningTag(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(value);
			cudService.createMeaningDomain(id2, meaningDomain, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_SOURCE_LINK:
			sourceLinkService.createDefinitionSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_NOTE_SOURCE_LINK:
			sourceLinkService.createDefinitionNoteSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_SOURCE_LINK:
			sourceLinkService.createLexemeSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_NOTE_SOURCE_LINK:
			sourceLinkService.createLexemeNoteSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case USAGE_SOURCE_LINK:
			sourceLinkService.createUsageSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_IMAGE_SOURCE_LINK:
			sourceLinkService.createMeaningImageSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_NOTE_SOURCE_LINK:
			sourceLinkService.createMeaningNoteSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case FREEFORM_SOURCE_LINK:
			sourceLinkService.createFreeformSourceLink(id, id2, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		}

		Locale locale = LocaleContextHolder.getLocale();
		String message = messageSource.getMessage("common.create.success", new Object[0], locale);
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(message);

		return response;
	}

	@ResponseBody
	@PostMapping(UPDATE_ITEM_URI)
	public Response updateItem(@RequestBody UpdateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		logger.debug("Update item: {}; auto update: {}", itemData, isManualEventOnUpdateEnabled);

		EkiUser user = userContext.getUser();
		String roleDatasetCode = getRoleDatasetCode();
		Long id = itemData.getId();
		Long id2 = itemData.getId2();
		String value = itemData.getValue();
		value = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(value);
		String value2 = itemData.getValue2();
		value2 = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(value2);
		String currentValue = itemData.getCurrentValue();
		BigDecimal numberValue = itemData.getNumberValue();
		String languageCode = itemData.getLanguage();
		String classifCode = itemData.getCode();
		boolean isPublic = itemData.isPublic();

		switch (itemData.getOpCode()) {
		case "user_lang_selection":
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
			ClassifierSelect langSelect = languagesOrder.stream().filter(classif -> StringUtils.equals(classif.getCode(), classifCode)).findFirst().get();
			langSelect.setSelected(!langSelect.isSelected());
			break;
		case "definition":
			cudService.updateDefinition(id, value, languageCode, classifCode, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.updateDefinitionNote(id, value, languageCode, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.updateUsage(id, value, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage_translation":
			cudService.updateUsageTranslation(id, value, languageCode, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage_definition":
			cudService.updateUsageDefinition(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_publicity":
			cudService.updateLexemePublicity(id, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_is_word":
			boolean isWord = Boolean.valueOf(value);
			cudService.updateLexemeIsWord(id, isWord, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_is_colloc":
			boolean isCollocation = Boolean.valueOf(value);
			cudService.updateLexemeIsCollocation(id, isCollocation, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_pos":
			cudService.updateLexemePos(id, currentValue, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_freeform":
			cudService.updateLexemeFreeform(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.updateLexemeGovernment(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_deriv":
			cudService.updateLexemeDeriv(id, currentValue, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_register":
			cudService.updateLexemeRegister(id, currentValue, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_region":
			cudService.updateLexemeRegion(id, currentValue, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_weight":
			cudService.updateLexemeWeight(id, numberValue, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "learner_comment":
			cudService.updateMeaningLearnerComment(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_grammar":
			cudService.updateLexemeGrammar(id, value, languageCode, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.updateLexemeNote(id, value, languageCode, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_value":
			cudService.updateWordValue(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(id, value, user, isManualEventOnUpdateEnabled);
			break;
		case "word_type":
			cudService.updateWordTypeWithDuplication(id, currentValue, value, user, isManualEventOnUpdateEnabled);
			break;
		case "word_data_and_lexeme_weight":
			cudService.updateWordDataAndLexemeWeight(id, id2, value, numberValue, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_morphophono_form":
			cudService.updateWordMorphophonoForm(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_lang":
			cudService.updateWordLang(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_reg_year":
			cudService.updateWordRegYear(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_manual_event_on":
			cudService.updateWordManualEventOn(id, value, roleDatasetCode);
			break;
		case "word_forum":
			cudService.updateWordForum(id, value, user);
			break;
		case "word_freeform":
			cudService.updateWordFreeform(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_morph":
			cudService.updateWordOsMorph(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_recommendation":
			cudService.updateWordOsRecommendation(id, value, value2, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_usage":
			cudService.updateWordOsUsage(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_morph_comment":
			cudService.updateWordMorphComment(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(currentValue);
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			cudService.updateMeaningDomain(id, currentMeaningDomain, newMeaningDomain, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.updateMeaningNote(id, value, languageCode, isPublic, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_forum":
			cudService.updateMeaningForum(id, value, user);
			break;
		case "meaning_relation_weight":
			cudService.updateMeaningRelationWeight(id, numberValue, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_image":
			cudService.updateMeaningImage(id, value, value2, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_media":
			cudService.updateMeaningMedia(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_freeform":
			cudService.updateMeaningFreeform(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_semantic_type":
			cudService.updateMeaningSemanticType(id, currentValue, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_manual_event_on":
			cudService.updateMeaningManualEventOn(id, value, roleDatasetCode);
			break;
		case DEFINITION_SOURCE_LINK:
			sourceLinkService.updateDefinitionSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_NOTE_SOURCE_LINK:
			sourceLinkService.updateDefinitionNoteSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_SOURCE_LINK:
			sourceLinkService.updateLexemeSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_NOTE_SOURCE_LINK:
			sourceLinkService.updateLexemeNoteSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case USAGE_SOURCE_LINK:
			sourceLinkService.updateUsageSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_IMAGE_SOURCE_LINK:
			sourceLinkService.updateMeaningImageSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_NOTE_SOURCE_LINK:
			sourceLinkService.updateMeaningNoteSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case FREEFORM_SOURCE_LINK:
			sourceLinkService.updateFreeformSourceLink(id, value, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		}

		Locale locale = LocaleContextHolder.getLocale();
		String message = messageSource.getMessage("common.update.success", new Object[0], locale);
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(message);

		return response;
	}

	@ResponseBody
	@PostMapping(UPDATE_ORDERING_URI)
	public String updateOrdering(@RequestBody UpdateListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Update ordering {}", listData);

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		List<ListData> items = listData.getItems();

		switch (listData.getOpCode()) {
		case "definition":
			cudService.updateDefinitionOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_SOURCE_LINK:
			sourceLinkService.updateDefinitionSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.updateDefinitionNoteOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_NOTE_SOURCE_LINK:
			sourceLinkService.updateDefinitionNoteSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme":
			cudService.updateLexemeOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_SOURCE_LINK:
			sourceLinkService.updateLexemeSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.updateLexemeNoteOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_NOTE_SOURCE_LINK:
			sourceLinkService.updateLexemeNoteSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_freeform":
			cudService.updateLexemeFreeformOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_relation":
			cudService.updateLexemeRelationOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.updateUsageOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case USAGE_SOURCE_LINK:
			sourceLinkService.updateUsageSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.updateGovernmentOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			cudService.updateMeaningDomainOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.updateMeaningNoteOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_NOTE_SOURCE_LINK:
			sourceLinkService.updateMeaningNoteSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_IMAGE_SOURCE_LINK:
			sourceLinkService.updateMeaningImageSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_freeform":
			cudService.updateMeaningFreeformOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_relation":
			cudService.updateMeaningRelationOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_relation":
			cudService.updateWordRelationOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_etymology":
			cudService.updateWordEtymologyOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_usage":
			cudService.updateWordOsUsageOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_meaning_word":
			Long lexemeId = listData.getAdditionalInfo();
			cudService.updateLexemeMeaningWordOrdering(items, lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case FREEFORM_SOURCE_LINK:
			sourceLinkService.updateFreeformSourceLinkOrdering(items, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		}
		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(DELETE_ITEM_URI)
	public Response deleteItem(
			@RequestParam("opCode") String opCode,
			@RequestParam("id") Long id,
			@RequestParam(name = "value", required = false) String valueToDelete,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		logger.debug("Delete operation: {};  id: {}; value: {}; auto update: {}", opCode, id, valueToDelete, isManualEventOnUpdateEnabled);

		Locale locale = LocaleContextHolder.getLocale();
		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();

		if (userRole == null) {
			logger.warn("User role not selected. Interrupting operation");
			String errorMessage = messageSource.getMessage("common.delete.fail", new Object[0], locale);
			Response response = new Response();
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(errorMessage);
			return response;
		}

		String roleDatasetCode = userRole.getDatasetCode();

		String successMessage = messageSource.getMessage("common.delete.success", new Object[0], locale);
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(successMessage);

		switch (opCode) {
		case "definition":
			cudService.deleteDefinition(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "definition_note":
			cudService.deleteDefinitionNote(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage":
			cudService.deleteUsage(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage_translation":
			cudService.deleteUsageTranslation(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "usage_definition":
			cudService.deleteUsageDefinition(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_freeform":
			cudService.deleteLexemeFreeform(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "government":
			cudService.deleteLexemeGovernment(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_note":
			cudService.deleteLexemeNote(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_pos":
			cudService.deleteLexemePos(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_tag":
			cudService.deleteLexemeTag(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_deriv":
			cudService.deleteLexemeDeriv(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_register":
			cudService.deleteLexemeRegister(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_region":
			cudService.deleteLexemeRegion(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_reliability":
			cudService.updateLexemeReliability(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_grammar":
			cudService.deleteLexemeGrammar(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_relation":
			cudService.deleteLexemeRelation(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme_proficiency_level":
			cudService.updateLexemeProficiencyLevel(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "lexeme":
			cudService.deleteLexeme(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "colloc":
			cudService.deleteCollocMember(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "rus_meaning_lexemes":
			cudService.deleteLexemeAndMeaningLexemes(id, LANGUAGE_CODE_RUS, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_tag":
			cudService.deleteMeaningTag(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "learner_comment":
			cudService.deleteMeaningLearnerComment(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning":
			cudService.deleteMeaningAndLexemes(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(valueToDelete);
			cudService.deleteMeaningDomain(id, meaningDomain, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_note":
			cudService.deleteMeaningNote(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_forum":
			cudService.deleteMeaningForum(id);
			break;
		case "meaning_image":
			cudService.deleteMeaningImage(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_media":
			cudService.deleteMeaningMedia(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_freeform":
			cudService.deleteMeaningFreeform(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "meaning_relation":
			response = cudService.deleteMeaningRelation(id, response, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
			break;
		case "meaning_semantic_type":
			cudService.deleteMeaningSemanticType(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_forum":
			cudService.deleteWordForum(id);
			break;
		case "word_freeform":
			cudService.deleteWordFreeform(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_gender":
			cudService.updateWordGenderWithDuplication(id, null, user, isManualEventOnUpdateEnabled);
			break;
		case "word_type":
			cudService.deleteWordTypeWithDuplication(id, valueToDelete, user, isManualEventOnUpdateEnabled);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_tag":
			cudService.deleteWordTag(id, valueToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_relation":
			cudService.deleteWordRelation(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_display_morph":
			cudService.updateWordDisplayMorph(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_morphophono_form":
			cudService.updateWordMorphophonoForm(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_vocal_form":
			cudService.updateWordVocalForm(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_reg_year":
			cudService.updateWordRegYear(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_morph_comment":
			cudService.updateWordMorphComment(id, null, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_morph":
			cudService.deleteWordOsMorph(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_recommendation":
			cudService.deleteWordOsRecommendation(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case "word_os_usage":
			cudService.deleteWordOsUsage(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_SOURCE_LINK:
			sourceLinkService.deleteDefinitionSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case DEFINITION_NOTE_SOURCE_LINK:
			sourceLinkService.deleteDefinitionNoteSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_SOURCE_LINK:
			sourceLinkService.deleteLexemeSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case LEXEME_NOTE_SOURCE_LINK:
			sourceLinkService.deleteLexemeNoteSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case USAGE_SOURCE_LINK:
			sourceLinkService.deleteUsageSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_IMAGE_SOURCE_LINK:
			sourceLinkService.deleteMeaningImageSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case MEANING_NOTE_SOURCE_LINK:
			sourceLinkService.deleteMeaningNoteSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		case FREEFORM_SOURCE_LINK:
			sourceLinkService.deleteFreeformSourceLink(id, roleDatasetCode, isManualEventOnUpdateEnabled);
			break;
		}

		return response;
	}

	@ResponseBody
	@PostMapping(UPDATE_LEVELS_URI)
	public String updateLexemeLevels(
			@RequestParam("id") Long lexemeId,
			@RequestParam("action") String action,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();

		cudService.updateLexemeLevels(lexemeId, action, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@PostMapping(UPDATE_LEXEME_LEVELS_URI)
	public String updateLexemeLevels(
			@RequestBody UpdateLexemeLevelsRequest updateLexemeLevelsRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		Long lexemeId = updateLexemeLevelsRequest.getLexemeId();
		Integer position = updateLexemeLevelsRequest.getPosition();
		logger.debug("Change lexeme levels for id {}, new position {}", lexemeId, position);

		cudService.updateLexemeLevels(lexemeId, position, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}

	@PostMapping(CONFIRM_OP_URI)
	public String confirmOperation(@RequestBody DeleteItemRequest deleteItemRequest, Model model) {

		String opName = deleteItemRequest.getOpName();
		String opCode = deleteItemRequest.getOpCode();
		Long id = deleteItemRequest.getId();

		logger.debug("Confirmation request: {} {} {}", opName, opCode, id);

		EkiUser user = userContext.getUser();

		switch (opName) {
		case "delete":
			switch (opCode) {
			case "lexeme":
				LexemeDeleteConfirmation lexemeDeleteConfirmation = complexOpService.validateLexemeDelete(id, user);
				model.addAttribute("lexemeDeleteConfirmation", lexemeDeleteConfirmation);
				return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lexeme_delete_confirmation";
			case "meaning":
				MeaningDeleteConfirmation meaningDeleteConfirmation = complexOpService.validateMeaningDelete(id, user);
				model.addAttribute("meaningDeleteConfirmation", meaningDeleteConfirmation);
				return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "meaning_delete_confirmation";
			case "rus_meaning_lexemes":
				LexemeDeleteConfirmation meaningLexemesDeleteConfirmation = complexOpService.validateLexemeAndMeaningLexemesDelete(id, user);
				model.addAttribute("lexemeDeleteConfirmation", meaningLexemesDeleteConfirmation);
				return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lexeme_delete_confirmation";
			}
		}
		throw new UnsupportedOperationException("Unsupported confirm operation: " + opName + " " + opCode);
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

		EkiUser user = userContext.getUser();
		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		for (Long id2 : ids) {
			switch (opCode) {
			case "meaning_relation":
				cudService.createMeaningRelation(id1, id2, relationType, oppositeRelationType, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
				break;
			case "lexeme_relation":
				cudService.createLexemeRelation(id1, id2, relationType, oppositeRelationType, roleDatasetCode, isManualEventOnUpdateEnabled);
				break;
			case "word_relation":
				cudService.createWordRelation(id1, id2, relationType, oppositeRelationType, user, roleDatasetCode, isManualEventOnUpdateEnabled);
				break;
			case "syn_meaning_relation":
				synCudService.createSynMeaningRelation(id1, id2, weightStr, roleDatasetCode, isManualEventOnUpdateEnabled);
				break;
			case "raw_relation":
				synCudService.createSynWordRelation(id1, id2, weightStr, user, roleDatasetCode, isManualEventOnUpdateEnabled);
				break;
			}
		}
		return RESPONSE_OK_VER1;
	}

	@GetMapping(MANUAL_EVENT_ON_UPDATE_URI + "/{isEnabled}")
	public String updateManualEventOnEnabled(
			@PathVariable("isEnabled") boolean isEnabled,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Auto update is now: {}", isEnabled);
		sessionBean.setManualEventOnUpdateEnabled(isEnabled);

		return SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "manual_event_on_chk";
	}

}
