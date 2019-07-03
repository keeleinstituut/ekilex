package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceType;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.ConfirmationRequest;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.ListData;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.data.UpdateListRequest;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.SourceService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.service.util.ConversionUtil;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.PermDataUtil;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class EditController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(EditController.class);

	@Autowired
	private CudService cudService;

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Autowired
	private SourceService sourceService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private PermDataUtil permDataUtil;

	@ResponseBody
	@PostMapping(CREATE_ITEM_URI)
	public String createItem(@RequestBody CreateItemRequest itemData) {

		logger.debug("Add new item : {}", itemData);

		String valuePrese = textDecorationService.cleanEkiElementHtmlMarkup(itemData.getValue());

		switch (itemData.getOpCode()) {
		case "definition":
			cudService.createDefinition(itemData.getId(), valuePrese, itemData.getLanguage(), itemData.getDataset(), itemData.getComplexity(),
					itemData.getItemType());
			break;
		case "definition_public_note":
			cudService.createDefinitionPublicNote(itemData.getId(), valuePrese);
			break;
		case "usage":
			cudService.createUsage(itemData.getId(), valuePrese, itemData.getLanguage(), itemData.getComplexity());
			break;
		case "usage_translation":
			cudService.createUsageTranslation(itemData.getId(), valuePrese, itemData.getLanguage());
			break;
		case "usage_definition":
			cudService.createUsageDefinition(itemData.getId(), valuePrese, itemData.getLanguage());
			break;
		case "lexeme_frequency_group":
			cudService.updateLexemeFrequencyGroup(itemData.getId(), valuePrese);
			break;
		case "lexeme_pos":
			cudService.createLexemePos(itemData.getId(), valuePrese);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(valuePrese);
			cudService.createMeaningDomain(itemData.getId2(), meaningDomain);
			break;
		case "government":
			cudService.createLexemeGovernment(itemData.getId(), valuePrese, itemData.getComplexity());
			break;
		case ContentKey.DEFINITION_SOURCE_LINK: {
			String sourcePropertyValue = getSourcePropertyValue(itemData.getId3());
			cudService.createDefinitionSourceLink(itemData.getId(), itemData.getId2(), sourcePropertyValue, valuePrese);
			break;
		}
		case ContentKey.FREEFORM_SOURCE_LINK: {
			String sourcePropertyValue = getSourcePropertyValue(itemData.getId3());
			cudService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourcePropertyValue, valuePrese);
			break;
		}
		case ContentKey.LEXEME_SOURCE_LINK: {
			String sourcePropertyValue = getSourcePropertyValue(itemData.getId3());
			cudService.createLexemeSourceLink(itemData.getId(), itemData.getId2(), sourcePropertyValue, valuePrese);
			break;
		}
		case "lexeme_deriv":
			cudService.createLexemeDeriv(itemData.getId(), valuePrese);
			break;
		case "lexeme_register":
			cudService.createLexemeRegister(itemData.getId(), valuePrese);
			break;
		case "lexeme_region":
			cudService.createLexemeRegion(itemData.getId(), valuePrese);
			break;
		case "word_gender":
			cudService.updateWordGender(itemData.getId3(), valuePrese);
			break;
		case "word_type":
			cudService.createWordType(itemData.getId3(), valuePrese);
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemData.getId3(), valuePrese);
			break;
		case "lexeme_grammar":
			cudService.createLexemeGrammar(itemData.getId(), valuePrese, itemData.getComplexity());
			break;
		case "word_relation":
			cudService.createWordRelation(itemData.getId(), itemData.getId2(), valuePrese);
			break;
		case "lexeme_relation":
			cudService.createLexemeRelation(itemData.getId(), itemData.getId2(), valuePrese);
			break;
		case "meaning_relation":
			cudService.createMeaningRelation(itemData.getId(), itemData.getId2(), valuePrese);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemData.getId(), valuePrese);
			break;
		case "usage_author":
			String sourcePropertyValue = getSourcePropertyValue(itemData.getId3());
			ReferenceType refType = ReferenceType.valueOf(itemData.getItemType());
			cudService.createFreeformSourceLink(itemData.getId(), itemData.getId2(), refType, sourcePropertyValue, null);
			break;
		case "learner_comment":
			cudService.createMeaningLearnerComment(itemData.getId(), valuePrese, itemData.getLanguage());
			break;
		case "lexeme_public_note":
			cudService.createLexemePublicNote(itemData.getId(), valuePrese);
			break;
		case "meaning_public_note":
			cudService.createMeaningPublicNote(itemData.getId(), valuePrese);
			break;
		case "image_title":
			cudService.createImageTitle(itemData.getId(), valuePrese);
			break;
		}
		return "{}";
	}

	private String getSourcePropertyValue(Long sourcePropertyId) {
		return sourceService.getSourcePropertyValue(sourcePropertyId);
	}

	@ResponseBody
	@PostMapping(UPDATE_ITEM_URI)
	public String updateItem(@RequestBody UpdateItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String valuePrese = textDecorationService.cleanEkiElementHtmlMarkup(itemData.getValue());

		logger.debug("Update operation for {}", itemData.getOpCode());
		switch (itemData.getOpCode()) {
		case "term_user_lang":
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
			ClassifierSelect langSelect = languagesOrder.stream().filter(classif -> StringUtils.equals(classif.getCode(), itemData.getCode())).findFirst().get();
			langSelect.setSelected(!langSelect.isSelected());
			break;
		case "usage":
			cudService.updateUsageValue(itemData.getId(), valuePrese, itemData.getComplexity());
			break;
		case "usage_translation":
			cudService.updateUsageTranslationValue(itemData.getId(), valuePrese);
			break;
		case "usage_definition":
			cudService.updateUsageDefinitionValue(itemData.getId(), valuePrese);
			break;
		case "definition":
			cudService.updateDefinition(itemData.getId(), valuePrese, itemData.getComplexity(), itemData.getCode());
			break;
		case "definition_public_note":
			cudService.updateDefinitionPublicNote(itemData.getId(), valuePrese);
			break;
		case "lexeme_frequency_group":
			cudService.updateLexemeFrequencyGroup(itemData.getId(), valuePrese);
			break;
		case "lexeme_pos":
			cudService.updateLexemePos(itemData.getId(), itemData.getCurrentValue(), valuePrese);
			break;
		case "meaning_domain":
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(itemData.getCurrentValue());
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(valuePrese);
			cudService.updateMeaningDomain(itemData.getId(), currentMeaningDomain, newMeaningDomain);
			break;
		case "government":
			cudService.updateLexemeGovernment(itemData.getId(), valuePrese, itemData.getComplexity());
			break;
		case "lexeme_deriv":
			cudService.updateLexemeDeriv(itemData.getId(), itemData.getCurrentValue(), valuePrese);
			break;
		case "lexeme_register":
			cudService.updateLexemeRegister(itemData.getId(), itemData.getCurrentValue(), valuePrese);
			break;
		case "lexeme_region":
			cudService.updateLexemeRegion(itemData.getId(), itemData.getCurrentValue(), valuePrese);
			break;
		case "word_gender":
			cudService.updateWordGender(itemData.getId(), valuePrese);
			break;
		case "word_type":
			cudService.updateWordType(itemData.getId(), itemData.getCurrentValue(), valuePrese);
			break;
		case "lexeme_grammar":
			cudService.updateLexemeGrammar(itemData.getId(), valuePrese, itemData.getComplexity());
			break;
		case "word_aspect":
			cudService.updateWordAspect(itemData.getId(), valuePrese);
			break;
		case "lexeme_value_state":
			cudService.updateLexemeValueState(itemData.getId(), valuePrese);
			break;
		case "learner_comment":
			cudService.updateMeaningLearnerComment(itemData.getId(), valuePrese);
			break;
		case "lexeme_public_note":
			cudService.updateLexemePublicNote(itemData.getId(), valuePrese);
			break;
		case "meaning_public_note":
			cudService.updateMeaningPublicNote(itemData.getId(), valuePrese);
			break;
		case "image_title":
			cudService.updateImageTitle(itemData.getId(), valuePrese);
			break;
		}
		return "{}";
	}

	@ResponseBody
	@PostMapping(UPDATE_ORDERING_URI)
	public String updateOrdering(@RequestBody UpdateListRequest listData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update operation for {}", listData.getOpCode());
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
		}
		return "{}";
	}

	@ResponseBody
	@PostMapping(UPDATE_LEVELS_URI)
	public String updateLexemeLevels(@RequestParam("id") Long lexemeId, @RequestParam("action") String action) {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);
		cudService.updateLexemeLevels(lexemeId, action);
		return "OK";
	}

	@ResponseBody
	@PostMapping(CONFIRM_OP_URI)
	public ConfirmationRequest confirmOperation(@RequestBody ConfirmationRequest confirmationRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String opName = confirmationRequest.getOpName();
		String opCode = confirmationRequest.getOpCode();
		Long id = confirmationRequest.getId();
		List<String> questions = new ArrayList<>();
		String question;

		logger.debug("Confirmation request: {} {} {}", opName, opCode, id);

		switch (opName) {
		case "delete":
			switch (opCode) {
			case "lexeme":
				boolean isOnlyLexemeForMeaning = lexSearchService.isOnlyLexemeForMeaning(id);
				if (isOnlyLexemeForMeaning) {
					question = "Valitud ilmik on tähenduse ainus ilmik. Palun kinnita tähenduse kustutamine";
					questions.add(question);
				}
				boolean isOnlyLexemeForWord = lexSearchService.isOnlyLexemeForWord(id);
				if (isOnlyLexemeForWord) {
					question = "Valitud ilmik on keelendi ainus ilmik. Palun kinnita keelendi kustutamine";
					questions.add(question);
				}
				break;
			case "meaning":
				DatasetPermission userRole = sessionBean.getUserRole();
				if (userRole == null) {
					question = "Mõiste kustutamine pole ilma rollita õigustatud";
					questions.add(question);
					break;
				}
				String datasetCode = userRole.getDatasetCode();
				boolean isOnlyLexemesForMeaning = termSearchService.isOnlyLexemesForMeaning(id, datasetCode);
				if (isOnlyLexemesForMeaning) {
					question = "Valitud mõistel pole rohkem kasutust. Palun kinnita mõiste kustutamine";
					questions.add(question);
				}
				boolean isOnlyLexemesForWords = termSearchService.isOnlyLexemesForWords(id, datasetCode);
				if (isOnlyLexemesForWords) {
					question = "Valitud mõiste kustutamisel jäävad mõned terminid mõisteta. Palun kinnita terminite kustutamine";
					questions.add(question);
				}
				break;
			}
			break;
		}

		boolean unconfirmed = CollectionUtils.isNotEmpty(questions);
		confirmationRequest.setUnconfirmed(unconfirmed);
		confirmationRequest.setQuestions(questions);
		return confirmationRequest;
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
			cudService.deleteFreeformSourceLink(id);
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
		case "word_type":
			cudService.deleteWordType(id, valueToDelete);
			break;
		case "word_aspect":
			cudService.updateWordAspect(id, null);
			break;
		case "word_relation":
			cudService.deleteWordRelation(id);
			break;
		case "image_title":
			cudService.deleteImageTitle(id);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			cudService.deleteDefinitionSourceLink(id);
			break;
		case ContentKey.FREEFORM_SOURCE_LINK:
			cudService.deleteFreeformSourceLink(id);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			cudService.deleteLexemeSourceLink(id);
			break;
		}
		return "OK";
	}

	@PostMapping(CREATE_WORD_URI)
	public String createWord(
			@RequestParam("dataset") String dataset,
			@RequestParam("wordValue") String wordValue,
			@RequestParam("language") String language,
			@RequestParam("morphCode") String morphCode,
			@RequestParam("meaningId") Long meaningId,
			@RequestParam("returnPage") String returnPage,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			sessionBean.setNewWordSelectedLanguage(language);
			sessionBean.setNewWordSelectedMorphCode(morphCode);
			List<String> allDatasets = commonDataService.getDatasetCodes();
			WordsResult words = lexSearchService.getWords(wordValue, allDatasets, true);
			if (words.getTotalCount() == 0) {
				cudService.createWord(wordValue, dataset, language, morphCode, meaningId);
			} else {
				attributes.addFlashAttribute("dataset", dataset);
				attributes.addFlashAttribute("wordValue", wordValue);
				attributes.addFlashAttribute("language", language);
				attributes.addFlashAttribute("morphCode", morphCode);
				attributes.addFlashAttribute("returnPage", returnPage);
				attributes.addFlashAttribute("meaningId", meaningId);
				return "redirect:" + WORD_SELECT_URI;
			}
			List<String> selectedDatasets = sessionBean.getSelectedDatasets();
			if (!selectedDatasets.contains(dataset)) {
				selectedDatasets.add(dataset);
			}
			searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);
		}
		if (StringUtils.equals(returnPage, RETURN_PAGE_LEX_SEARCH)) {
			return "redirect:" + LEX_SEARCH_URI + searchUri;
		}
		if (StringUtils.equals(returnPage, RETURN_PAGE_TERM_SEARCH)) {
			return "redirect:" + TERM_SEARCH_URI + searchUri;
		}
		return null;
	}

	@PostMapping(CREATE_HOMONYM_URI)
	public String createWord(
			@RequestParam("dataset") String dataset,
			@RequestParam("wordValue") String wordValue,
			@RequestParam("language") String language,
			@RequestParam("morphCode") String morphCode,
			@RequestParam("meaningId") Long meaningId,
			@RequestParam("returnPage") String returnPage,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			cudService.createWord(wordValue, dataset, language, morphCode, meaningId);
			List<String> selectedDatasets = sessionBean.getSelectedDatasets();
			if (!selectedDatasets.contains(dataset)) {
				selectedDatasets.add(dataset);
			}
			searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);
		}
		if (StringUtils.equals(returnPage, RETURN_PAGE_LEX_SEARCH)) {
			return "redirect:" + LEX_SEARCH_URI + searchUri;
		}
		if (StringUtils.equals(returnPage, RETURN_PAGE_TERM_SEARCH)) {
			return "redirect:" + TERM_SEARCH_URI + searchUri;
		}
		return null;
	}

	@GetMapping(WORD_SELECT_URI)
	public String listSelectableWords(
			@ModelAttribute(name = "dataset") String dataset,
			@ModelAttribute(name = "wordValue") String wordValue,
			@ModelAttribute(name = "language") String language,
			@ModelAttribute(name = "morphCode") String morphCode,
			@ModelAttribute(name = "meaningId") Long meaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> allDatasets = commonDataService.getDatasetCodes();
		List<WordDescript> wordDescripts = lexSearchService.getWordDescripts(wordValue, allDatasets, meaningId);
		model.addAttribute("wordDescripts", wordDescripts);

		return WORD_SELECT_PAGE;
	}

	@GetMapping(WORD_SELECT_URI + "/{dataset}/{wordId}/{meaningId}/{returnPage}")
	public String selectWord(
			@PathVariable(name = "dataset") String dataset,
			@PathVariable(name = "wordId") Long wordId,
			@PathVariable(name = "meaningId") String meaningIdCode,
			@PathVariable(name = "returnPage") String returnPage,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		Long meaningId = NumberUtils.isDigits(meaningIdCode) ? NumberUtils.toLong(meaningIdCode) : null;
		cudService.createLexeme(wordId, dataset, meaningId);
		Word word = commonDataService.getWord(wordId);
		String wordValue = word.getValue();
		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (!selectedDatasets.contains(dataset)) {
			selectedDatasets.add(dataset);
		}
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);
		if (StringUtils.equals(returnPage, RETURN_PAGE_LEX_SEARCH)) {
			return "redirect:" + LEX_SEARCH_URI + searchUri;
		}
		if (StringUtils.equals(returnPage, RETURN_PAGE_TERM_SEARCH)) {
			return "redirect:" + TERM_SEARCH_URI + searchUri;
		}
		return null;
	}

	@PostMapping(UPDATE_WORD_VALUE_URI)
	@ResponseBody
	public String updateWordValue(@RequestParam("wordId") Long wordId, @RequestParam("value") String valuePrese) {

		valuePrese = textDecorationService.cleanEkiElementHtmlMarkup(valuePrese);
		logger.debug("Updating word value, wordId: \"{}\", valuePrese: \"{}\"", wordId, valuePrese);
		cudService.updateWordValue(wordId, valuePrese);
		return valuePrese;
	}
}
