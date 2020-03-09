package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CorporaSentence;
import eki.wordweb.service.CorporaServiceEst;
import eki.wordweb.service.CorporaServiceRus;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class CorporaController {

	@Autowired
	private CorporaServiceEst corporaServiceEst;

	@Autowired
	private CorporaServiceRus corporaServiceRus;

	@GetMapping("/korp/{lang}/{sentence}")
	public String searchFromCorpora(@PathVariable("lang") String language, @PathVariable("sentence") String sentence, Model model) {

		List<CorporaSentence> textCorpus = new ArrayList<>();
		if (StringUtils.equalsIgnoreCase(language, "est")) {
			textCorpus = corporaServiceEst.getSentences(sentence);
		} else if (StringUtils.equalsIgnoreCase(language, "rus")) {
			textCorpus = corporaServiceRus.getSentences(sentence);
		}
		model.addAttribute("sentences", textCorpus);
		model.addAttribute("sentence", sentence);
		model.addAttribute("corp_language", language);

		return "common-search :: korp";
	}
}
