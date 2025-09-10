package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.WordDetails;
import eki.ekilex.service.LexSearchService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class PageSectionController extends AbstractPrivateSearchController {

	@Autowired
	private LexSearchService lexSearchService;

	@GetMapping(LEXEME_COLLOCATION_URI + "/{lexemeId}")
	public String lexemeCollocations(@PathVariable("lexemeId") Long lexemeId, Model model) {

		EkiUser user = userContext.getUser();
		Lexeme lexeme = lexSearchService.getLexemeCollocations(lexemeId, user);
		model.addAttribute("lexeme", lexeme);

		return LEX_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "collocation";
	}

	@GetMapping(WORD_RELATION_URI + "/{wordId}")
	public String wordRelationDetails(@PathVariable("wordId") Long wordId, Model model) {

		EkiUser user = userContext.getUser();
		WordDetails details = lexSearchService.getWordRelationDetails(wordId, user);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);

		return WORD_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + WORD_RELATION_FRAGMENT;
	}

	@GetMapping(WORD_OS_RECOMMENDATION_URI + "/{wordId}")
	public String wordOsRecommendationDetails(@PathVariable("wordId") Long wordId, Model model) {

		EkiUser user = userContext.getUser();
		WordDetails details = lexSearchService.getWordOsRecommendationDetails(wordId, user);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);

		return WORD_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + WORD_OS_RECOMMENDATION_FRAGMENT;
	}

}
