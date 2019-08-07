package eki.ekilex.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexEditController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(LexEditController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private CommonDataService commonDataService;

	@GetMapping("/lexjoin/{lexemeId}")
	public String show(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		model.addAttribute("sourceLexeme", commonDataService.getWordLexeme(lexemeId));
		model.addAttribute("searchFilter", null);

		return LEX_JOIN_PAGE;
	}

	@PostMapping("/lexjoin/{lexemeId}")
	public String search(
			@PathVariable("lexemeId") Long lexemeId,
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		List<String> datasets = Collections.singletonList(lexeme.getDatasetCode());
		if (CollectionUtils.isNotEmpty(sessionBean.getSelectedDatasets())) {
			datasets = sessionBean.getSelectedDatasets();
		}
		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithMinimalData(searchFilter, datasets);
		model.addAttribute("sourceLexeme", lexeme);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("lexemes", lexemes);

		return LEX_JOIN_PAGE;
	}

	@GetMapping("/lexjoin/{lexemeId}/{lexemeId2}")
	public String join(
			@PathVariable("lexemeId") Long lexemeId,
			@PathVariable("lexemeId2") Long lexemeId2,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		List<String> validationMessages = compositionService.validateLexemeJoin(lexemeId, lexemeId2);
		if (!validationMessages.isEmpty()) {
			model.addAttribute("sourceLexeme", lexeme);
			model.addAttribute("validationMessages", validationMessages);
			return LEX_JOIN_PAGE;
		}
		compositionService.joinLexemes(lexemeId, lexemeId2);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping("/lexseparate/{lexemeId}")
	public String separate(
			@PathVariable("lexemeId") Long lexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		compositionService.separateLexemeMeanings(lexemeId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping("/duplicatelexeme/{lexemeId}")
	public String duplicateLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId) throws Exception {

		Optional<Long> clonedLexeme = Optional.empty();
		try {
			clonedLexeme = compositionService.optionalDuplicateLexemeAndMeaning(lexemeId);
		} catch (Exception ignore) {
			logger.error("", ignore);
		}

		Map<String, String> response = new HashMap<>();
		if (clonedLexeme.isPresent()) {
			response.put("message", "Lekseemi duplikaat lisatud");
			response.put("status", "ok");
		} else {
			response.put("message", "Duplikaadi lisamine ebaõnnestus");
			response.put("status", "error");
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}


	@ResponseBody
	@PostMapping("/duplicateemptylexeme/{lexemeId}")
	public String duplicateEmptyLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId) throws Exception {

		compositionService.duplicateEmptyLexemeAndMeaning(lexemeId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Uus tähendus loodud");
		response.put("status", "ok");

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@ModelAttribute("iso2languages")
	public Map<String, String> getIso2Languages() {
		return commonDataService.getLanguagesIso2Map();
	}
}
