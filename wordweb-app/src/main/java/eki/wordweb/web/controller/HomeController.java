package eki.wordweb.web.controller;

import static java.util.Collections.emptyList;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.WordsData;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class HomeController extends AbstractController {

	@GetMapping(HOME_URI)
	public String home(Model model) {
		populateSearchModel("", new WordsData(emptyList(), emptyList(), SEARCH_MODE_DETAIL), model);
		return HOME_PAGE;
	}

	@GetMapping(LEARN_URI)
	public String learn(Model model) {
		populateGeneralData(model);
		return LEARN_PAGE;
	}

	@GetMapping(GAMES_URI)
	public String games(Model model) {
		populateGeneralData(model);
		return GAMES_PAGE;
	}
	
	@GetMapping(CONTACTS_URI)
	public String contacts(Model model) {
		populateGeneralData(model);
		return CONTACTS_PAGE;
	}
	
	@GetMapping(ABOUT_URI)
	public String about(Model model) {
		populateGeneralData(model);
		return ABOUT_PAGE;
	}

}
