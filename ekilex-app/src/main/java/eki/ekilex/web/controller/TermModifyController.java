package eki.ekilex.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import eki.ekilex.service.CloningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.service.UpdateService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermModifyController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(TermModifyController.class);

	private final TermSearchService termSearchService;

	private final LexSearchService lexSearchService;

	private final UpdateService updateService;

	private final SearchHelper searchHelper;

	private final CloningService cloningService;


	public TermModifyController(TermSearchService termSearchService,
			LexSearchService lexSearchService,
			UpdateService updateService,
			SearchHelper searchHelper,
			CloningService cloningService) {
		this.termSearchService = termSearchService;
		this.lexSearchService = lexSearchService;
		this.updateService = updateService;
		this.searchHelper = searchHelper;
		this.cloningService = cloningService;
	}

	@GetMapping(MEANING_JOIN_URI + "/{meaningId}")
	public String show(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		Long meaningFirstLexemeId = termSearchService.getMeaningFirstLexemeId(meaningId, sessionBean.getSelectedDatasets());
		model.addAttribute("sourceLexeme", lexSearchService.getWordLexeme(meaningFirstLexemeId));
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
		model.addAttribute("sourceLexeme", lexSearchService.getWordLexeme(meaningFirstLexemeId));
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("meaningId", meaningId);
		List<WordLexeme> lexemes = lexSearchService.findWordLexemesWithMinimalData(searchFilter, sessionBean.getSelectedDatasets());
		model.addAttribute("meaningLexemes", lexemes);

		return MEANING_JOIN_PAGE;
	}

	@GetMapping(MEANING_JOIN_URI + "/{meaningId}/{meaningId2}")
	public String join(
			@PathVariable("meaningId") Long meaningId,
			@PathVariable("meaningId2") Long sourceMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		updateService.joinMeanings(meaningId, sourceMeaningId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		String wordValue = termSearchService.getMeaningFirstWordValue(meaningId, selectedDatasets);
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping("/meaningcopy/{meaningId}")
	public String meaningCopy(@PathVariable("meaningId") Long meaningId) throws JsonProcessingException {

		logger.debug("meaningId : {}", meaningId);

		Map<String, String> response = new HashMap<>();
		Optional<Long> clonedMeaning = cloningService.cloneMeaning(meaningId);
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
