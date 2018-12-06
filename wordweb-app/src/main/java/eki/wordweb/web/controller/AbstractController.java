package eki.wordweb.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.web.bean.SessionBean;

public abstract class AbstractController implements WebConstant, SystemConstant {

	protected static final int AUTOCOMPLETE_MAX_RESULTS_LIMIT = 10;

	protected static final char LANGUAGE_PAIR_SEPARATOR = '-';

	@Value("${speech.recognition.service.url:}")
	protected String speechRecognitionServiceUrl;

	@Value("${wordweb.feedback.service.url:}")
	protected String feedbackServiceUrl;

	protected void populateSearchModel(String searchWord, WordsData wordsData, Model model) {

		populateGeneralData(model);
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("wordsData", wordsData);
		model.addAttribute("wordData", new WordData());
	}

	protected void populateGeneralData(Model model) {

		boolean sessionBeanNotPresent = sessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(model);
		} else {
			sessionBean = getSessionBean(model);
		}
		if (StringUtils.isBlank(sessionBean.getSourceLang())) {
			sessionBean.setSourceLang(DEFAULT_SOURCE_LANG);
		}
		if (StringUtils.isBlank(sessionBean.getDestinLang())) {
			sessionBean.setDestinLang(DEFAULT_DESTIN_LANG);
		}
		if (StringUtils.equals(sessionBean.getSourceLang(), "rus")
				&& StringUtils.equals(sessionBean.getDestinLang(), "rus")) {
			sessionBean.setSourceLang(DEFAULT_SOURCE_LANG);
		}
		if (StringUtils.isBlank(sessionBean.getSearchMode())) {
			sessionBean.setSearchMode(SEARCH_MODE_DETAIL);
		}
		model.addAttribute("feedbackServiceUrl", feedbackServiceUrl);
	}

	protected boolean sessionBeanNotPresent(Model model) {
		return !model.asMap().containsKey(SESSION_BEAN);
	}

	protected SessionBean createSessionBean(Model model) {
		SessionBean sessionBean = new SessionBean();
		model.addAttribute(SESSION_BEAN, sessionBean);
		return sessionBean;
	}

	protected SessionBean getSessionBean(Model model) {
		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		return sessionBean;
	}

}
