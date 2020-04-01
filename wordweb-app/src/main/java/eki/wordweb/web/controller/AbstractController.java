package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.UiFilterElement;
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
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("feedbackServiceUrl", feedbackServiceUrl);
		return sessionBean;
	}

	protected void populateLangFilter(List<UiFilterElement> langFilter, SessionBean sessionBean, Model model) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> selectedLangs = new ArrayList<>();
		if (CollectionUtils.isEmpty(destinLangs)) {
			destinLangs = new ArrayList<>();
			destinLangs.add(DESTIN_LANG_ALL);
			sessionBean.setDestinLangs(destinLangs);
		}
		for (UiFilterElement langFilterElement : langFilter) {
			boolean isSelected = destinLangs.contains(langFilterElement.getCode());
			langFilterElement.setSelected(isSelected);
			if (isSelected) {
				selectedLangs.add(langFilterElement.getValue());
			}
		}
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		String selectedLangsStr = StringUtils.join(selectedLangs, ", ");

		model.addAttribute("langFilter", langFilter);
		model.addAttribute("destinLangsStr", destinLangsStr);
		model.addAttribute("selectedLangsStr", selectedLangsStr);
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

	protected Integer nullSafe(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (!StringUtils.isNumeric(value)) {
			return null;
		}
		return new Integer(value);
	}
}
