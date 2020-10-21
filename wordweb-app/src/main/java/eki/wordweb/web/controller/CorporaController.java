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

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CorporaSentence;
import eki.wordweb.service.CorporaServiceEst;
import eki.wordweb.service.CorporaServiceRus;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class CorporaController implements WebConstant, SystemConstant {

	@Autowired
	private CorporaServiceEst corporaServiceEst;

	@Autowired
	private CorporaServiceRus corporaServiceRus;

	@GetMapping(CORP_URI + "/{searchMode}/{lang}/{word}")
	public String searchFromCorpora(
			@PathVariable("searchMode") String searchMode,
			@PathVariable("lang") String language,
			@PathVariable("word") String word,
			Model model) {

		List<CorporaSentence> textCorpus = new ArrayList<>();
		if (StringUtils.equals(language, DESTIN_LANG_EST)) {
			textCorpus = corporaServiceEst.getSentences(word, searchMode);
		} else if (StringUtils.equals(language, DESTIN_LANG_RUS)) {
			textCorpus = corporaServiceRus.getSentences(word);
		}

		model.addAttribute("sentences", textCorpus);
		model.addAttribute("sentence", word);
		model.addAttribute("corp_language", language);

		return "common-search :: corp";
	}
}
