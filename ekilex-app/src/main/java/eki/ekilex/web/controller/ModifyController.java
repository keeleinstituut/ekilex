package eki.ekilex.web.controller;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.AddItemRequest;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.ListData;
import eki.ekilex.data.ModifyItemRequest;
import eki.ekilex.data.ModifyListRequest;
import eki.ekilex.data.Source;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.SourceService;
import eki.ekilex.service.UpdateService;
import eki.ekilex.service.util.ConversionUtil;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ModifyController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(ModifyController.class);

	@Autowired
	private UpdateService updateService;

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Autowired
	private SourceService sourceService;

	@Autowired
	private SearchHelper searchHelper;

	@ResponseBody
	@PostMapping("/add_item")
	public String addItem(@RequestBody AddItemRequest itemData) {

		logger.debug("Add new item : {}", itemData);
		switch (itemData.getOpCode()) {
		case "definition":
			updateService.addDefinition(itemData.getId(), itemData.getValue(), itemData.getLanguage(), itemData.getDataset());
			break;
		case "usage":
			updateService.addUsage(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "usage_translation":
			updateService.addUsageTranslation(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "usage_definition":
			updateService.addUsageDefinition(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "lexeme_frequency_group":
			updateService.updateLexemeFrequencyGroup(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_pos":
			updateService.addLexemePos(itemData.getId(), itemData.getValue());
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			updateService.addMeaningDomain(itemData.getId2(), meaningDomain);
			break;
		case "government":
			updateService.addGovernment(itemData.getId(), itemData.getValue());
			break;
		case ContentKey.DEFINITION_SOURCE_LINK: {
			String sourceName = findSourceName(itemData.getId2());
			updateService.addDefinitionSourceLink(itemData.getId(), itemData.getId2(), sourceName, itemData.getValue());
			break;
		}
		case ContentKey.FREEFORM_SOURCE_LINK: {
			String sourceName = findSourceName(itemData.getId2());
			updateService.addFreeformSourceLink(itemData.getId(), itemData.getId2(), ReferenceType.ANY, sourceName, itemData.getValue());
			break;
		}
		case ContentKey.LEXEME_SOURCE_LINK: {
			String sourceName = findSourceName(itemData.getId2());
			updateService.addLexemeSourceLink(itemData.getId(), itemData.getId2(), sourceName, itemData.getValue());
			break;
		}
		case "lexeme_deriv":
			updateService.addLexemeDeriv(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_register":
			updateService.addLexemeRegister(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_region":
			updateService.addLexemeRegion(itemData.getId(), itemData.getValue());
			break;
		case "word_gender":
			updateService.updateWordGender(itemData.getId3(), itemData.getValue());
			break;
		case "word_type":
			updateService.addWordType(itemData.getId3(), itemData.getValue());
			break;
		case "word_aspect":
			updateService.updateWordAspect(itemData.getId3(), itemData.getValue());
			break;
		case "lexeme_grammar":
			updateService.addLexemeGrammar(itemData.getId(), itemData.getValue());
			break;
		case "word_relation":
			updateService.addWordRelation(itemData.getId(), itemData.getId2(), itemData.getValue());
			break;
		case "lexeme_relation":
			updateService.addLexemeRelation(itemData.getId(), itemData.getId2(), itemData.getValue());
			break;
		case "meaning_relation":
			updateService.addMeaningRelation(itemData.getId(), itemData.getId2(), itemData.getValue());
			break;
		case "lexeme_value_state":
			updateService.updateLexemeValueState(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_process_state":
			updateService.updateLexemeProcessState(itemData.getId(), itemData.getValue());
			break;
		case "usage_author":
			String sourceValue = findSourceName(itemData.getId2());
			ReferenceType refType = ReferenceType.valueOf(itemData.getItemType());
			updateService.addFreeformSourceLink(itemData.getId(), itemData.getId2(), refType, sourceValue, null);
			break;
		case "learner_comment":
			updateService.addLearnerComment(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "lexeme_public_note":
			updateService.addLexemePublicNote(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "meaning_public_note":
			updateService.addMeaningPublicNote(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "meaning_private_note":
			updateService.addMeaningPrivateNote(itemData.getId(), itemData.getValue(), itemData.getLanguage());
			break;
		case "feedback_comment":
			updateService.addFeedbackComment(itemData.getId(), itemData.getValue());
			break;
		}
		return "{}";
	}

	@ResponseBody
	@PostMapping("/modify_item")
	public String modifyItem(@RequestBody ModifyItemRequest itemData, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		logger.debug("Update operation for {}", itemData.getOpCode());
		switch (itemData.getOpCode()) {
		case "term_user_lang":
			updateLanguageSelection(itemData, sessionBean);
			break;
		case "usage":
			updateService.updateUsageValue(itemData.getId(), itemData.getValue());
			break;
		case "usage_translation":
			updateService.updateUsageTranslationValue(itemData.getId(), itemData.getValue());
			break;
		case "usage_definition":
			updateService.updateUsageDefinitionValue(itemData.getId(), itemData.getValue());
			break;
		case "definition":
			updateService.updateDefinitionValue(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_frequency_group":
			updateService.updateLexemeFrequencyGroup(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_pos":
			updateService.updateLexemePos(itemData.getId(), itemData.getCurrentValue(), itemData.getValue());
			break;
		case "meaning_domain":
			Classifier currentMeaningDomain = conversionUtil.classifierFromIdString(itemData.getCurrentValue());
			Classifier newMeaningDomain = conversionUtil.classifierFromIdString(itemData.getValue());
			updateService.updateMeaningDomain(itemData.getId(), currentMeaningDomain, newMeaningDomain);
			break;
		case "government":
			updateService.updateGovernment(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_deriv":
			updateService.updateLexemeDeriv(itemData.getId(), itemData.getCurrentValue(), itemData.getValue());
			break;
		case "lexeme_register":
			updateService.updateLexemeRegister(itemData.getId(), itemData.getCurrentValue(), itemData.getValue());
			break;
		case "lexeme_region":
			updateService.updateLexemeRegion(itemData.getId(), itemData.getCurrentValue(), itemData.getValue());
			break;
		case "word_gender":
			updateService.updateWordGender(itemData.getId(), itemData.getValue());
			break;
		case "word_type":
			updateService.updateWordType(itemData.getId(), itemData.getCurrentValue(), itemData.getValue());
			break;
		case "lexeme_grammar":
			updateService.updateGrammar(itemData.getId(), itemData.getValue());
			break;
		case "word_aspect":
			updateService.updateWordAspect(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_value_state":
			updateService.updateLexemeValueState(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_process_state":
			updateService.updateLexemeProcessState(itemData.getId(), itemData.getValue());
			break;
		case "learner_comment":
			updateService.updateLearnerComment(itemData.getId(), itemData.getValue());
			break;
		case "lexeme_public_note":
			updateService.updateLexemePublicNote(itemData.getId(), itemData.getValue());
			break;
		case "meaning_public_note":
			updateService.updateMeaningPublicNote(itemData.getId(), itemData.getValue());
			break;
		case "meaning_private_note":
			updateService.updateMeaningPrivateNote(itemData.getId(), itemData.getValue());
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
		case "definition":
			updateService.updateDefinitionOrdering(items);
			break;
		case "lexeme_relation":
			updateService.updateLexemeRelationOrdering(items);
			break;
		case "meaning_relation":
			updateService.updateMeaningRelationOrdering(items);
			break;
		case "word_relation":
			updateService.updateWordRelationOrdering(items);
			break;
		case "word_etymology":
			updateService.updateWordEtymologyOrdering(items);
			break;
		case "lexeme":
			updateService.updateLexemeOrdering(items);
			break;
		}
		return "{}";
	}

	private void updateLanguageSelection(ModifyItemRequest itemData, SessionBean sessionBean) {
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		ClassifierSelect langSelect = languagesOrder.stream().filter(classif -> StringUtils.equals(classif.getCode(), itemData.getCode())).findFirst().get();
		langSelect.setSelected(!langSelect.isSelected());
	}

	@ResponseBody
	@PostMapping("/modify_levels")
	public String modifyLexemeLevels(@RequestParam("id") Long lexemeId, @RequestParam("action") String action) {

		logger.debug("Change lexeme levels for id {}, action {}", lexemeId, action);
		updateService.updateLexemeLevels(lexemeId, action);
		return "OK";
	}

	@ResponseBody
	@PostMapping("/remove_item_validate")
	public String validateRemoveElement(
			@RequestParam("opCode") String opCode,
			@RequestParam("id") Long id) throws JsonProcessingException {

		logger.debug("Delete validation operation : {} : for id {}", opCode, id);
		Map<String, String> response = new HashMap<>();
		switch (opCode) {
		case "lexeme":
			if (lexSearchService.isTheOnlyLexemeForMeaning(id)) {
				response.put("status", "invalid");
				response.put("message", "Valitud ilmik on mõiste ainus ilmik. Seda ei saa eemaldada.");
			} else if (lexSearchService.isTheOnlyLexemeForWord(id)) {
				response.put("status", "confirm");
				response.put("question", "Valitud ilmik on keelendi ainus ilmik. Koos ilmikuga kustutatakse ka keelend, kas jätkan ?");
			} else {
				response.put("status", "ok");
			}
			break;
		default:
			response.put("status", "ok");
			break;
		}
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@ResponseBody
	@PostMapping("/remove_item")
	public String removeItem(
			@RequestParam("opCode") String opCode,
			@RequestParam("id") Long id,
			@RequestParam(value = "value", required = false) String valueToRemove) {

		logger.debug("Delete operation : {} : for id {}, value {}", opCode, id, valueToRemove);
		switch (opCode) {
		case "usage":
			updateService.deleteUsage(id);
			break;
		case "usage_translation":
			updateService.deleteUsageTranslation(id);
			break;
		case "usage_definition":
			updateService.deleteUsageDefinition(id);
			break;
		case "definition":
			updateService.deleteDefinition(id);
			break;
		case "lexeme_frequency_group":
			updateService.updateLexemeFrequencyGroup(id, null);
			break;
		case "lexeme_pos":
			updateService.deleteLexemePos(id, valueToRemove);
			break;
		case "meaning_domain":
			Classifier meaningDomain = conversionUtil.classifierFromIdString(valueToRemove);
			updateService.deleteMeaningDomain(id, meaningDomain);
			break;
		case "government":
			updateService.deleteGovernment(id);
			break;
		case ContentKey.DEFINITION_SOURCE_LINK:
			updateService.deleteDefinitionSourceLink(id);
			break;
		case ContentKey.FREEFORM_SOURCE_LINK:
			updateService.deleteFreeformSourceLink(id);
			break;
		case ContentKey.LEXEME_SOURCE_LINK:
			updateService.deleteLexemeSourceLink(id);
			break;
		case "lexeme_deriv":
			updateService.deleteLexemeDeriv(id, valueToRemove);
			break;
		case "lexeme_register":
			updateService.deleteLexemeRegister(id, valueToRemove);
			break;
		case "lexeme_region":
			updateService.deleteLexemeRegion(id, valueToRemove);
			break;
		case "word_gender":
			updateService.updateWordGender(id, null);
			break;
		case "lexeme_grammar":
			updateService.deleteGrammar(id);
			break;
		case "word_type":
			updateService.deleteWordType(id, valueToRemove);
			break;
		case "word_aspect":
			updateService.updateWordAspect(id, null);
			break;
		case "word_relation":
			updateService.deleteWordRelation(id);
			break;
		case "lexeme_relation":
			updateService.deleteLexemeRelation(id);
			break;
		case "meaning_relation":
			updateService.deleteMeaningRelation(id);
			break;
		case "lexeme_value_state":
			updateService.updateLexemeValueState(id, null);
			break;
		case "lexeme_process_state":
			updateService.updateLexemeProcessState(id, null);
			break;
		case "usage_author":
			updateService.deleteFreeformSourceLink(id);
			break;
		case "lexeme":
			updateService.deleteLexeme(id);
			break;
		case "learner_comment":
			updateService.deleteLearnerComment(id);
			break;
		case "lexeme_public_note":
			updateService.deleteLexemePublicNote(id);
			break;
		case "meaning_public_note":
			updateService.deleteMeaningPublicNote(id);
			break;
		case "meaning_private_note":
			updateService.deleteMeaningPrivateNote(id);
			break;
		case "feedback":
			updateService.deleteFeedback(id);
			break;
		}
		return "OK";
	}

	private String findSourceName(Long sourceId) {
		Source source = sourceService.getSource(sourceId);
		List<String> sourceNames = source.getSourceNames();
		if (CollectionUtils.isNotEmpty(sourceNames)) {
			String firstAvailableSourceName = sourceNames.get(0);
			return firstAvailableSourceName;
		}
		return "---";
	}

	@PostMapping("/add_word")
	public String addNewWord(
			@RequestParam("dataset") String dataset,
			@RequestParam("value") String wordValue,
			@RequestParam("language") String language,
			@RequestParam("morphCode") String morphCode,
			@RequestParam("meaningId") Long meaningId,
			@RequestParam("returnPage") String returnPage,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			sessionBean.setNewWordSelectedDataset(dataset);
			sessionBean.setNewWordSelectedLanguage(language);
			sessionBean.setNewWordSelectedMorphCode(morphCode);
			List<String> allDatasets = commonDataService.getDatasetCodes();
			WordsResult words = lexSearchService.findWords(wordValue, allDatasets, true);
			if (words.getTotalCount() == 0) {
				updateService.addWord(wordValue, dataset, language, morphCode, meaningId);
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

	@PostMapping("/add_homonym")
	public String addNewHomonym(
			@RequestParam("dataset") String dataset,
			@RequestParam("wordValue") String wordValue,
			@RequestParam("language") String language,
			@RequestParam("morphCode") String morphCode,
			@RequestParam("returnPage") String returnPage,
			@RequestParam("meaningId") Long meaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			updateService.addWord(wordValue, dataset, language, morphCode, meaningId);
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

	//TODO omg what is going on here?! implement optimal data aggregation
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
		WordsResult words = lexSearchService.findWords(wordValue, allDatasets, true);
		List<Word> wordsInDifferentDatasets = words.getWords().stream().filter(w -> !asList(w.getDatasetCodes()).contains(dataset)).collect(Collectors.toList());
		boolean hasWordInSameDataset = words.getWords().size() != wordsInDifferentDatasets.size();
		model.addAttribute("words", wordsInDifferentDatasets);
		model.addAttribute("hasWordInSameDataset", hasWordInSameDataset);
		Map<Long, WordDetails> wordDetailsMap = new HashMap<>();
		Map<Long, Boolean> wordHasDefinitions = new HashMap<>();
		for (Word word : words.getWords()) {
			WordDetails wordDetails = lexSearchService.getWordDetails(word.getWordId(), allDatasets);
			wordDetailsMap.put(word.getWordId(), wordDetails);
			boolean hasDefinitions = wordDetails.getLexemes().stream().anyMatch(d -> !d.getDefinitions().isEmpty());
			wordHasDefinitions.put(word.getWordId(), hasDefinitions);
		}
		model.addAttribute("details", wordDetailsMap);
		model.addAttribute("hasDefinitions", wordHasDefinitions);

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
		updateService.addWordToDataset(wordId, dataset, meaningId);
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

}
