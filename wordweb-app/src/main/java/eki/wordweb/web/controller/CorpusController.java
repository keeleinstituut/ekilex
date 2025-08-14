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

	@GetMapping(value = {
			CORP_URI + "/{searchMode}/{wordValue}/{wordLang}",
			CORP_URI + "/{searchMode}/{wordValue}/{wordLang}/{posCodes}"})
	public String searchFromCorpus(
			@PathVariable("searchMode") String searchMode,
			@PathVariable("wordValue") String wordValue,
			@PathVariable("wordLang") String wordLang,
			@PathVariable(value = "posCodes", required = false) List<String> posCodes,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> uiSections = sessionBean.getUiSections();
		List<CorpusSentence> sentences = new ArrayList<>();
		if (StringUtils.equals(wordLang, DESTIN_LANG_EST)) {
			sentences = corpusEstService.getCorpusSentences(searchMode, wordValue, posCodes);
		}

		model.addAttribute("sentences", sentences);
		model.addAttribute("corpLang", wordLang);
		model.addAttribute("uiSections", uiSections);

		return COMMON_SEARCH_SIDEBAR_PAGE + PAGE_FRAGMENT_ELEM + "corp";
	}

	@GetMapping(CORP_TRANS_URI + "/{wordId}/{wordValue}/{wordLang}")
	public String searchFromTranslationCorpus(
			@PathVariable("wordId") Long wordId,
			@PathVariable("wordValue") String wordValue,
			@PathVariable("wordLang") String wordLang,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> uiSections = sessionBean.getUiSections();
		List<CorpusTranslation> translations = corpusTranslationService.getCorpusTranslations(wordId, wordValue, wordLang);

		model.addAttribute("translations", translations);
		model.addAttribute("uiSections", uiSections);

		return COMMON_SEARCH_SIDEBAR_PAGE + PAGE_FRAGMENT_ELEM + "corp_trans";
	}
}
