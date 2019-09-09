package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.util.SearchHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexEditController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(LexEditController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@GetMapping("/lexjoin/{lexemeId}")
	public String show(@PathVariable("lexemeId") Long lexemeId, Model model) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		String defaultSearchFilter = lexeme.getWords()[0];
		List<String> datasets = getUserPreferredDatasetsCodes();
		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithMinimalData(defaultSearchFilter, datasets);
		model.addAttribute("sourceLexeme", lexeme);
		model.addAttribute("searchFilter", defaultSearchFilter);
		model.addAttribute("lexemes", lexemes);

		return LEX_JOIN_PAGE;
	}

	@PostMapping("/lexjoin/{lexemeId}")
	public String search(@PathVariable("lexemeId") Long lexemeId, @RequestParam(name = "searchFilter", required = false) String searchFilter, Model model) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		List<String> datasets = getUserPreferredDatasetsCodes();
		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithMinimalData(searchFilter, datasets);
		model.addAttribute("sourceLexeme", lexeme);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("lexemes", lexemes);

		return LEX_JOIN_PAGE;
	}

	@GetMapping("/lexjoin/{lexemeId}/{lexemeId2}")
	public String join(@PathVariable("lexemeId") Long lexemeId, @PathVariable("lexemeId2") Long lexemeId2) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		compositionService.joinLexemes(lexemeId, lexemeId2);

		List<String> datasets = getUserPreferredDatasetsCodes();
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(datasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping("/lexseparate/{lexemeId}")
	public String separate(@PathVariable("lexemeId") Long lexemeId) {

		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		compositionService.separateLexemeMeanings(lexemeId);

		List<String> datasets = getUserPreferredDatasetsCodes();
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(datasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping("/duplicatelexeme/{lexemeId}")
	public String duplicateLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId) throws Exception {

		List<Long> clonedLexemeIds = new ArrayList<>();
		try {
			clonedLexemeIds = compositionService.duplicateLexemeAndMeaningWithSameDatasetLexemes(lexemeId);
		} catch (Exception ignore) {
			logger.error("", ignore);
		}

		Map<String, String> response = new HashMap<>();
		if (CollectionUtils.isNotEmpty(clonedLexemeIds)) {
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
