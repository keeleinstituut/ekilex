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

		populateModel("", new WordsData(emptyList(), emptyList()), model);

		return HOME_PAGE;
	}
}
