package eki.ekilex.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
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
	private LexSearchService lexSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@RequestMapping(MEANING_JOIN_URI + "/{targetMeaningId}")
	public String search(@PathVariable("targetMeaningId") Long targetMeaningId, @RequestParam(name = "searchFilter", required = false) String searchFilter, Model model) {

		List<String> datasets = getUserPreferredDatasetsCodes();
		Long meaningFirstLexemeId = termSearchService.getMeaningFirstLexemeId(targetMeaningId, datasets);
		WordLexeme targetMeaningLexeme = commonDataService.getWordLexeme(meaningFirstLexemeId);
		String targetLexemeWord = targetMeaningLexeme.getWords()[0];
		if (searchFilter == null) {
			searchFilter = targetLexemeWord;
		}

		Optional<Integer> wordHomonymNumber;
		if (StringUtils.equals(searchFilter, targetLexemeWord)) {
			Integer sourceHomonymNumber = targetMeaningLexeme.getWordHomonymNumber();
			wordHomonymNumber = Optional.of(sourceHomonymNumber);
		} else {
			wordHomonymNumber = Optional.empty();
		}

		List<WordLexeme> sourceMeaningLexemes = lexSearchService.getWordLexemesWithMinimalData(searchFilter, datasets, wordHomonymNumber, targetMeaningId);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetMeaningLexeme", targetMeaningLexeme);
		model.addAttribute("sourceMeaningLexemes", sourceMeaningLexemes);

		return MEANING_JOIN_PAGE;
	}

	@PostMapping(MEANING_JOIN_URI)
	public String join(@RequestParam("targetMeaningId") Long targetMeaningId, @RequestParam("sourceMeaningIds") List<Long> sourceMeaningIds) {

		compositionService.joinMeanings(targetMeaningId, sourceMeaningIds);

		List<String> datasets = getUserPreferredDatasetsCodes();
		String wordValue = termSearchService.getMeaningFirstWordValue(targetMeaningId, datasets);
		String searchUri = searchHelper.composeSearchUri(datasets, wordValue);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping("/duplicatemeaning/{meaningId}")
	public String duplicateMeaning(@PathVariable("meaningId") Long meaningId) throws JsonProcessingException {

		Optional<Long> clonedMeaning = Optional.empty();
		try {
			clonedMeaning = compositionService.optionalDuplicateMeaning(meaningId);
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

	@ModelAttribute("iso2languages")
	public Map<String, String> getIso2Languages() {
		return commonDataService.getLanguagesIso2Map();
	}

}
