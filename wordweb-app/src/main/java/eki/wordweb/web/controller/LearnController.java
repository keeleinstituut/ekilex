package eki.wordweb.web.controller;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.WordsData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import static java.util.Collections.emptyList;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LearnController extends AbstractController {

	@GetMapping(LEARN_URI)
	public String learn(Model model) {
		populateModel("", new WordsData(emptyList(), emptyList(), SEARCH_MODE_DETAIL), model);
		return LEARN_PAGE;
	}

}
