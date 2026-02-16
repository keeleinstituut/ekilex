package eki.wwexam.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import eki.common.constant.FeedbackConstant;
import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.wwexam.constant.SystemConstant;
import eki.wwexam.constant.WebConstant;
import eki.wwexam.service.StatDataCollector;
import eki.wwexam.web.bean.SessionBean;
import eki.wwexam.web.util.WebUtil;

public abstract class AbstractController implements WebConstant, SystemConstant, GlobalConstant, FeedbackConstant {

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected WebUtil webUtil;

	@Autowired
	protected StatDataCollector statDataCollector;

	protected SessionBean getSessionBean(Model model) {
		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		return sessionBean;
	}

	protected boolean isSessionBeanNotPresent(Model model) {
		return !model.asMap().containsKey(SESSION_BEAN);
	}

	protected SessionBean createSessionBean(boolean isSearchFilterPresent, Model model) {

		SessionBean sessionBean = new SessionBean();
		model.addAttribute(SESSION_BEAN, sessionBean);

		return sessionBean;
	}
}
