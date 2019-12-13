package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.exception.ConstraintViolationException;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CreateWordAndMeaningAndRelationsData;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Meaning;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermEditController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(TermEditController.class);

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private CudService cudService;

	@RequestMapping(MEANING_JOIN_URI + "/{targetMeaningId}")
	public String search(@PathVariable("targetMeaningId") Long targetMeaningId, @RequestParam(name = "searchFilter", required = false) String searchFilter,
			Model model, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		Long userId = userService.getAuthenticatedUser().getId();
		List<String> userPermDatasetCodes = permissionService.getUserPermDatasetCodes(userId);
		List<String> userPreferredDatasetCodes = getUserPreferredDatasetCodes();
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();

		if (searchFilter == null) {
			String targetMeaningFirstWord = termSearchService.getMeaningFirstWordValue(targetMeaningId, userPreferredDatasetCodes);
			searchFilter = targetMeaningFirstWord;
		}

		Meaning targetMeaning = lookupService.getMeaningOfJoinTarget(targetMeaningId, languagesOrder);
		List<Meaning> sourceMeanings = lookupService
				.getMeaningsOfJoinCandidates(searchFilter, userPreferredDatasetCodes, userPermDatasetCodes, languagesOrder, targetMeaningId, userId);

		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetMeaning", targetMeaning);
		model.addAttribute("sourceMeanings", sourceMeanings);

		return MEANING_JOIN_PAGE;
	}

	@PostMapping(VALIDATE_MEANING_JOIN_URI)
	@ResponseBody
	public String validateMeaningJoin(@RequestParam("targetMeaningId") Long targetMeaningId, @RequestParam("sourceMeaningIds") List<Long> sourceMeaningIds)
			throws Exception {

		Map<String, String> response = new HashMap<>();
		List<Long> allMeaningIds = new ArrayList<>(sourceMeaningIds);
		allMeaningIds.add(targetMeaningId);
		Map<String, Integer[]> invalidWords = lookupService.getMeaningsWordsWithMultipleHomonymNumbers(allMeaningIds);

		if (MapUtils.isNotEmpty(invalidWords)) {
			String message = "Tähendusi ei saa ühendada, sest ühendatavatel tähendustel on järgnevad samakujulised, aga erineva homonüüminumbriga keelendid:";

			Iterator<Map.Entry<String, Integer[]>> wordIterator = invalidWords.entrySet().iterator();
			while (wordIterator.hasNext()) {
				String wordValue = wordIterator.next().getKey();
				message += " " + wordValue;
				wordIterator.remove();
				if (wordIterator.hasNext()) {
					message += ",";
				} else {
					message += ".";
				}
			}

			message += " Palun ühendage enne tähenduste ühendamist need homonüümid.";
			response.put("status", "invalid");
			response.put("message", message);
		} else {
			response.put("status", "valid");
		}
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@PostMapping(MEANING_JOIN_URI)
	public String joinMeanings(@RequestParam("targetMeaningId") Long targetMeaningId, @RequestParam("sourceMeaningIds") List<Long> sourceMeaningIds) {

		compositionService.joinMeanings(targetMeaningId, sourceMeaningIds);

		List<String> datasets = getUserPreferredDatasetCodes();
		String wordValue = termSearchService.getMeaningFirstWordValue(targetMeaningId, datasets);
		String searchUri = searchHelper.composeSearchUri(datasets, wordValue);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping("/duplicatemeaning/{meaningId}")
	public String duplicateMeaning(@PathVariable("meaningId") Long meaningId) throws JsonProcessingException {

		String userName = userService.getAuthenticatedUser().getName();
		Optional<Long> clonedMeaning = Optional.empty();
		try {
			clonedMeaning = compositionService.optionalDuplicateMeaningWithLexemes(meaningId, userName);
		} catch (Exception ignore) {
			logger.error("", ignore);
		}

		Map<String, String> response = new HashMap<>();
		if (clonedMeaning.isPresent()) {
			Long duplicateMeaningId = clonedMeaning.get();
			response.put("message", "Mõiste duplikaat lisatud");
			response.put("duplicateMeaningId", String.valueOf(duplicateMeaningId));
			response.put("status", "ok");
		} else {
			response.put("message", "Duplikaadi lisamine ebaõnnestus");
			response.put("status", "error");
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@PostMapping(TERM_CREATE_WORD_URI)
	public String createWord(
			@RequestParam("wordValue") String wordValue,
			@RequestParam("language") String language,
			@RequestParam("morphCode") String morphCode,
			@RequestParam("meaningId") Long meaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {

			Long userId = userService.getAuthenticatedUser().getId();
			String dataset = sessionBean.getUserRole().getDatasetCode();
			List<String> userPermDatasetCodes = permissionService.getUserPermDatasetCodes(userId);
			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();

			sessionBean.setNewWordSelectedLanguage(language);
			sessionBean.setNewWordSelectedMorphCode(morphCode);

			List<Meaning> relationCandidates = lookupService.getMeaningsOfRelationCandidates(wordValue, userPermDatasetCodes, languagesOrder, meaningId);
			if (CollectionUtils.isNotEmpty(relationCandidates)) {
				attributes.addFlashAttribute("dataset", dataset);
				attributes.addFlashAttribute("wordValue", wordValue);
				attributes.addFlashAttribute("language", language);
				attributes.addFlashAttribute("morphCode", morphCode);
				attributes.addFlashAttribute("meaningId", meaningId);
				attributes.addFlashAttribute("relationCandidates", relationCandidates);
				return "redirect:" + MEANING_REL_SELECT_URI;
			} else {
				cudService.createWord(wordValue, dataset, language, morphCode, meaningId);
			}

			if (!selectedDatasets.contains(dataset)) {
				selectedDatasets.add(dataset);
				userService.updateUserPreferredDatasets(selectedDatasets);
			}
			searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);
		}
		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@GetMapping(MEANING_REL_SELECT_URI)
	public String listMeaningRelationCandidates(
			@ModelAttribute(name = "wordValue") String wordValue,
			@ModelAttribute(name = "language") String language,
			@ModelAttribute(name = "morphCode") String morphCode,
			@ModelAttribute(name = "meaningId") Long meaningId,
			@ModelAttribute(name = "relationCandidates") List<Meaning> relationCandidates,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		return MEANING_REL_SELECT_PAGE;
	}

	@PostMapping(CREATE_WORD_AND_MEANING_AND_REL_URI)
	@ResponseBody
	public String createWordAndMeaningAndRelations(
			@RequestBody CreateWordAndMeaningAndRelationsData createWordAndMeaningAndRelationsData,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws JsonProcessingException {

		Map<String, String> response = new HashMap<>();
		String wordValue = createWordAndMeaningAndRelationsData.getWordValue();
		if (StringUtils.isNotBlank(wordValue)) {

			String dataset = sessionBean.getUserRole().getDatasetCode();
			EkiUser user = userService.getAuthenticatedUser();
			Long userId = user.getId();
			String userName = user.getName();
			List<String> userPermDatasetCodes = permissionService.getUserPermDatasetCodes(userId);

			createWordAndMeaningAndRelationsData.setDataset(dataset);
			createWordAndMeaningAndRelationsData.setUserName(userName);
			createWordAndMeaningAndRelationsData.setUserPermDatasetCodes(userPermDatasetCodes);

			try {
				compositionService.createWordAndMeaningAndRelations(createWordAndMeaningAndRelationsData);
			} catch (Exception exception) {
				if (exception instanceof ConstraintViolationException) {
					response.put("status", "invalid");
					response.put("message", "Tähenduse andmete kopeerimine ebaõnnestus. Kontrolli, et tähendusel ei oleks samakujulisi erineva sõnakogu termineid");
				} else {
					response.put("status", "invalid");
					response.put("message", "Viga! Termini loomine ebaõnnestus");
				}
				logger.error("", exception);
			}

			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			if (!selectedDatasets.contains(dataset)) {
				selectedDatasets.add(dataset);
				userService.updateUserPreferredDatasets(selectedDatasets);
			}
			String searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);
			response.put("status", "valid");
			response.put("searchUri", searchUri);
		} else {
			response.put("status", "invalid");
			response.put("message", "Viga! Terminil puudub väärtus");
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

}
