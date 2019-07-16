package eki.ekilex.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermEditController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(TermEditController.class);

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private CommonDataService commonDataService;

	@GetMapping(MEANING_JOIN_URI + "/{meaningId}")
	public String show(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		Long meaningFirstLexemeId = termSearchService.getMeaningFirstLexemeId(meaningId, sessionBean.getSelectedDatasets());
		model.addAttribute("sourceLexeme", commonDataService.getWordLexeme(meaningFirstLexemeId));
		model.addAttribute("searchFilter", null);
		model.addAttribute("meaningId", meaningId);

		return MEANING_JOIN_PAGE;
	}

	@PostMapping(MEANING_JOIN_URI + "/{meaningId}")
	public String search(
			@PathVariable("meaningId") Long meaningId,
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		Long meaningFirstLexemeId = termSearchService.getMeaningFirstLexemeId(meaningId, sessionBean.getSelectedDatasets());
		model.addAttribute("sourceLexeme", commonDataService.getWordLexeme(meaningFirstLexemeId));
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("meaningId", meaningId);
		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithMinimalData(searchFilter, sessionBean.getSelectedDatasets());
		model.addAttribute("meaningLexemes", lexemes);

		return MEANING_JOIN_PAGE;
	}

	@GetMapping(MEANING_JOIN_URI + "/{meaningId}/{meaningId2}")
	public String join(
			@PathVariable("meaningId") Long meaningId,
			@PathVariable("meaningId2") Long sourceMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		compositionService.joinMeanings(meaningId, sourceMeaningId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		String wordValue = termSearchService.getMeaningFirstWordValue(meaningId, selectedDatasets);
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);

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
			response.put("message", "Mõiste duplikaat lisatud");
			response.put("status", "ok");
		} else {
			response.put("message", "Duplikaadi lisamine ebaõnnestus");
			response.put("status", "error");
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}
}
