package eki.wordweb.web.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.NewWordYear;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class NewWordController extends AbstractController {

	@GetMapping(NEW_WORDS_URI)
	public String newWords(Model model) {
		populateCommonModel(model);
		List<NewWordYear> newWordYears = ancillaryDataService.getNewWordYears();
		model.addAttribute("newWordYears", newWordYears);
		return NEW_WORDS_PAGE;
	}
}
