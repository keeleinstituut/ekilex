package eki.wordweb.web.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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
	public String learn(HttpServletRequest request, Model model) {
		populateCommonModel(model);
		return LEARN_PAGE;
	}

	@GetMapping(WORDGAME_URI)
	public String wordgame(HttpServletRequest request, Model model) {
		populateCommonModel(model);
		return WORDGAME_PAGE;
	}

	@GetMapping(GAMES_URI)
	public String games(HttpServletRequest request, Model model) {
		populateCommonModel(model);
		return GAMES_PAGE;
	}

	@GetMapping(CONTACTS_URI)
	public String contacts(HttpServletRequest request, Model model) {
		populateCommonModel(model);
		return CONTACTS_PAGE;
	}

	@GetMapping(COLLECTIONS_URI)
	public String collections(HttpServletRequest request, Model model) {
		populateCommonModel(model);
		List<Dataset> termDatasets = commonDataService.getTermDatasets();
		model.addAttribute("termDatasets", termDatasets);
		return COLLECTIONS_PAGE;
	}

	@GetMapping(ABOUT_URI)
	public String about(HttpServletRequest request, Model model, Locale locale) {
		populateCommonModel(model);
		return ABOUT_PAGE + "_" + locale.getLanguage();
	}

	@GetMapping(REGULATIONS_URI)
	public String regulations(HttpServletRequest request, Model model) {
		populateCommonModel(model);
		return REGULATIONS_PAGE;
	}

}
