package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.util.UriUtils;

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

		return populateCommonModel(false, null, model);
	}

	protected SessionBean populateCommonModel(boolean isSearchFilterPresent, HttpServletRequest request, Model model) {

		boolean isSessionBeanNotPresent = isSessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (isSessionBeanNotPresent) {
			sessionBean = createSessionBean(isSearchFilterPresent, request, model);
		} else {
			sessionBean = getSessionBean(model);
		}
		String ekilexLimTermSearchUrl = webUtil.getEkilexLimTermSearchUrl();
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("feedbackServiceUrl", feedbackServiceUrl);
		model.addAttribute("ekilexLimTermSearchUrl", ekilexLimTermSearchUrl);
		return sessionBean;
	}

	protected SessionBean getSessionBean(Model model) {
		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		return sessionBean;
	}

	protected boolean isSessionBeanNotPresent(Model model) {
		return !model.asMap().containsKey(SESSION_BEAN);
	}

	protected SessionBean createSessionBean(boolean isSearchFilterPresent, HttpServletRequest request, Model model) {

		SessionBean sessionBean = new SessionBean();
		sessionBean.setDestinLangs(new ArrayList<>());
		sessionBean.setDatasetCodes(new ArrayList<>());
		sessionBean.setUiSections(new ArrayList<>());

		if (request != null) {

			Cookie[] cookies = request.getCookies();

			if (ArrayUtils.isNotEmpty(cookies)) {

				Map<String, String> cookieMap = Arrays.stream(cookies)
						.filter(cookie -> StringUtils.startsWithIgnoreCase(cookie.getName(), COOKIE_NAME_PREFIX))
						.collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
				String destinLangsStr = cookieMap.get(COOKIE_NAME_DESTIN_LANGS);
				String datasetCodesStr = cookieMap.get(COOKIE_NAME_DATASETS);
				String uiSectionsStr = cookieMap.get(COOKIE_NAME_UI_SECTIONS);

				if (!isSearchFilterPresent) {

					if (StringUtils.isNotBlank(destinLangsStr)) {
						String[] destinLangsArr = StringUtils.split(destinLangsStr, COOKIE_VALUES_SEPARATOR);
						List<String> destinLangs = Arrays.stream(destinLangsArr)
								.filter(destinLang -> StringUtils.equalsAny(destinLang, SUPPORTED_DETAIL_DESTIN_LANG_FILTERS))
								.collect(Collectors.toList());
						sessionBean.setDestinLangs(destinLangs);
					}

					if (StringUtils.isNotBlank(datasetCodesStr)) {
						List<String> supportedDatasetCodes = commonDataService.getSupportedDatasetCodes();
						String[] datasetCodesArr = StringUtils.split(datasetCodesStr, COOKIE_VALUES_SEPARATOR);
						List<String> datasetCodes = Arrays.stream(datasetCodesArr)
								.map(datasetCode -> UriUtils.decode(datasetCode, UTF_8))
								.filter(datasetCode -> supportedDatasetCodes.contains(datasetCode))
								.collect(Collectors.toList());
						sessionBean.setDatasetCodes(datasetCodes);
					}
				}

				if (StringUtils.isNotBlank(uiSectionsStr)) {
					String[] uiSectionsArr = StringUtils.split(uiSectionsStr, COOKIE_VALUES_SEPARATOR);
					List<String> uiSections = new ArrayList<>(Arrays.asList(uiSectionsArr));
					sessionBean.setUiSections(uiSections);
				}
			}
		}

		model.addAttribute(SESSION_BEAN, sessionBean);

		return sessionBean;
	}

}
