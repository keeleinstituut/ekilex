package eki.wordweb.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Dataset;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class PagesController extends AbstractController {

	@GetMapping(LEARN_URI)
	public String learn(Model model) {
		populateCommonModel(model);
		return LEARN_PAGE;
	}

	@GetMapping(WORDGAME_URI)
	public String wordgame(Model model) {
		populateCommonModel(model);
		return WORDGAME_PAGE;
	}

	@GetMapping(GAMES_URI)
	public String games(Model model) {
		populateCommonModel(model);
		return GAMES_PAGE;
	}

	@GetMapping(CONTACT_URI)
	public String contacts(Model model) {
		populateCommonModel(model);
		return CONTACT_PAGE;
	}

	@GetMapping(COLLECTIONS_URI)
	public String collections(Model model) {
		populateCommonModel(model);
		List<Dataset> termDatasets = commonDataService.getTermDatasets();
		model.addAttribute("termDatasets", termDatasets);
		return COLLECTIONS_PAGE;
	}

	@GetMapping(ABOUT_URI)
	public String about(Model model, Locale locale) {
		String lang = locale.getLanguage();
		populateCommonModel(model);
		return ABOUT_PAGE + "_" + lang;
	}

	@GetMapping(REGULATIONS_URI)
	public String regulations(Model model) {
		populateCommonModel(model);
		return REGULATIONS_PAGE;
	}

	@GetMapping(EKILEX_API_URI)
	public String ekilexApi(Model model) {
		populateCommonModel(model);
		return EKILEX_API_PAGE;
	}
}
