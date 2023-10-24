package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CorpusSentence;
import eki.wordweb.data.CorpusTranslation;
import eki.wordweb.service.CorpusEstService;
import eki.wordweb.service.CorpusTranslationService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class CorpusController implements WebConstant, SystemConstant {

	@Autowired
	private CorpusEstService corpusEstService;

	@Autowired
	private CorpusTranslationService corpusTranslationService;

	@GetMapping(CORP_URI + "/{searchMode}/{wordLang}/{wordValue}")
	public String searchFromCorpus(
			@PathVariable("searchMode") String searchMode,
			@PathVariable("wordLang") String wordLang,
			@PathVariable("wordValue") String wordValue,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> uiSections = sessionBean.getUiSections();
		List<CorpusSentence> sentences = new ArrayList<>();
		if (StringUtils.equals(wordLang, DESTIN_LANG_EST)) {
			sentences = corpusEstService.getSentences(wordValue, searchMode);
		}

		model.addAttribute("sentences", sentences);
		model.addAttribute("corpLang", wordLang);
		model.addAttribute("uiSections", uiSections);

		return "common-search-sidebar :: corp";
	}

	@GetMapping(CORP_TRANS_URI + "/{wordId}/{wordLang}/{wordValue}")
	public String searchFromTranslationCorpus(
			@PathVariable("wordId") Long wordId,
			@PathVariable("wordLang") String wordLang,
			@PathVariable("wordValue") String wordValue,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> uiSections = sessionBean.getUiSections();
		List<CorpusTranslation> translations = corpusTranslationService.getTranslations(wordId, wordLang,wordValue);

		model.addAttribute("translations", translations);
		model.addAttribute("uiSections", uiSections);

		return "common-search-sidebar :: corp_trans";
	}
}
