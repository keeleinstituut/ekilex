package eki.wordweb.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.service.CommonDataService;
import eki.wordweb.service.StatDataCollector;
import eki.wordweb.web.bean.SessionBean;
import eki.wordweb.web.util.UserAgentUtil;
import eki.wordweb.web.util.WebUtil;

public abstract class AbstractController implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	protected CommonDataService commonDataService;

	@Value("${speech.recognition.service.url:}")
	protected String speechRecognitionServiceUrl;

	@Value("${wordweb.feedback.service.url:}")
	protected String feedbackServiceUrl;

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected WebUtil webUtil;

	@Autowired
	protected UserAgentUtil userAgentUtil;

	@Autowired
	protected StatDataCollector statDataCollector;

	protected SessionBean populateCommonModel(Model model) {

		boolean sessionBeanNotPresent = sessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(model);
		} else {
			sessionBean = getSessionBean(model);
		}
		String ekilexLimTermSearchUrl = webUtil.getEkilexLimTermSearchUrl();
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("feedbackServiceUrl", feedbackServiceUrl);
		model.addAttribute("ekilexLimTermSearchUrl", ekilexLimTermSearchUrl);
		return sessionBean;
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
