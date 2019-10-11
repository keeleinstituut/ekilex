package eki.wordweb.web.controller;

import java.util.Locale;

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
		populateSearchModel("", new WordsData(SEARCH_MODE_DETAIL), model);
		return HOME_PAGE;
	}

	@GetMapping(LEARN_URI)
	public String learn(Model model) {
		populateGlobalData(model);
		return LEARN_PAGE;
	}

	@GetMapping(GAMES_URI)
	public String games(Model model) {
		populateGlobalData(model);
		return GAMES_PAGE;
	}

	@GetMapping(CONTACTS_URI)
	public String contacts(Model model) {
		populateGlobalData(model);
		return CONTACTS_PAGE;
	}

	@GetMapping(COLLECTIONS_URI)
	public String collections(Model model) {
		populateGlobalData(model);
		return COLLECTIONS_PAGE;
	}

	@GetMapping(ABOUT_URI)
	public String about(Model model, Locale locale) {
		populateGlobalData(model);
		return ABOUT_PAGE + "_" + locale.getLanguage();
	}

	@GetMapping(REGULATIONS_URI)
	public String regulations(Model model) {
		populateGlobalData(model);
		return REGULATIONS_PAGE;
	}

	@GetMapping(CONDITIONS_URI)
	public String conditions(Model model) {
		populateGlobalData(model);
		return CONDITIONS_PAGE;
	}

}
