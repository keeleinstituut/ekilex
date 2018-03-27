package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.bean.SessionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexJoinController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(LexJoinController.class);

	private final LexSearchService lexSearchService;

	public LexJoinController(LexSearchService lexSearchService) {
		this.lexSearchService = lexSearchService;
	}

	@GetMapping("/lexjoin/{lexemeId}")
	public String show(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		model.addAttribute("lexeme", lexSearchService.getWordLexeme(lexemeId));
		model.addAttribute("searchFilter", "");
		return LEX_JOIN_PAGE;
	}

	@PostMapping("/lexjoin/{lexemeId}")
	public String search(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		model.addAttribute("lexeme", lexSearchService.getWordLexeme(lexemeId));
		model.addAttribute("searchFilter", "");
		return LEX_JOIN_PAGE;
	}

}
