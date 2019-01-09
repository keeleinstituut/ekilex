package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.service.UpdateService;
import eki.ekilex.web.bean.SessionBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermModifyController implements WebConstant {

	private final TermSearchService termSearchService;

	private final LexSearchService lexSearchService;

	private final UpdateService updateService;

	public TermModifyController(TermSearchService termSearchService, LexSearchService lexSearchService, UpdateService updateService) {
		this.termSearchService = termSearchService;
		this.lexSearchService = lexSearchService;
		this.updateService = updateService;
	}

	@GetMapping("/meaningjoin/{meaningId}")
	public String show(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		Long meaningFirstLexemeId = termSearchService.getMeaningFirstLexemeId(meaningId, sessionBean.getSelectedDatasets());
		model.addAttribute("sourceLexeme", lexSearchService.getWordLexeme(meaningFirstLexemeId));
		model.addAttribute("searchFilter", null);
		model.addAttribute("meaningId", meaningId);

		return MEANING_JOIN_PAGE;
	}

	@PostMapping("/meaningjoin/{meaningId}")
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

	@GetMapping("/meaningjoin/{meaningId}/{meaningId2}")
	public String join(
			@PathVariable("meaningId") Long meaningId,
			@PathVariable("meaningId2") Long sourceMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		updateService.joinMeanings(meaningId, sourceMeaningId);

		String word = termSearchService.getMeaningFirstWord(meaningId, sessionBean.getSelectedDatasets());
		attributes.addFlashAttribute(SEARCH_WORD_KEY, word);

		return "redirect:" + TERM_SEARCH_URI;
	}

}
