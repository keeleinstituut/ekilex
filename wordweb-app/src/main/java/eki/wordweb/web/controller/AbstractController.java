package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.CommonDataService;
import eki.wordweb.web.bean.SessionBean;
import eki.wordweb.web.util.UserAgentUtil;

public abstract class AbstractController implements WebConstant, SystemConstant {

	@Autowired
	protected CommonDataService commonDataService;

	@Value("${speech.recognition.service.url:}")
	protected String speechRecognitionServiceUrl;

	@Value("${wordweb.feedback.service.url:}")
	protected String feedbackServiceUrl;

	@Autowired
	protected UserAgentUtil userAgentUtil;

	@ModelAttribute(IE_USER_FLAG_KEY)
	public boolean isIeUser(HttpServletRequest request) {
		boolean isIeUser = userAgentUtil.isTraditionalMicrosoftUser(request);
		return isIeUser;
	}

	protected void populateSearchModel(String searchWord, WordsData wordsData, Model model) {

		populateGlobalData(model);
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("wordsData", wordsData);
		model.addAttribute("wordData", new WordData());
	}

	protected void populateGlobalData(Model model) {

		boolean sessionBeanNotPresent = sessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(model);
		} else {
			sessionBean = getSessionBean(model);
		}

		// search mode
		if (StringUtils.isBlank(sessionBean.getSearchMode())) {
			sessionBean.setSearchMode(SEARCH_MODE_DETAIL);
		}

		// lang filter
		populateLangFilter(sessionBean, model);

		// dataset filter
		populateDatasetFilter(sessionBean, model);

		model.addAttribute("feedbackServiceUrl", feedbackServiceUrl);
	}

	private void populateLangFilter(SessionBean sessionBean, Model model) {
	
		List<UiFilterElement> langFilter = commonDataService.getLangFilter(DISPLAY_LANG);
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

	private void populateDatasetFilter(SessionBean sessionBean, Model model) {

		List<UiFilterElement> datasetFilter = commonDataService.getDatasetFilter();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		if (CollectionUtils.isEmpty(datasetCodes)) {
			datasetCodes = new ArrayList<>();
			datasetCodes.add(DATASET_ALL);
			sessionBean.setDatasetCodes(datasetCodes);
		}
		String selectedDatasetsStr = null;
		for (UiFilterElement datasetFilterElement : datasetFilter) {
			boolean isSelected = datasetCodes.contains(datasetFilterElement.getCode());
			datasetFilterElement.setSelected(isSelected);
			if (isSelected) {
				selectedDatasetsStr = datasetFilterElement.getValue();
			}
		}
		String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
		long selectedDatasetCount = datasetFilter.stream().filter(UiFilterElement::isSelected).count();
		if (selectedDatasetCount > 1) {
			selectedDatasetsStr = String.valueOf(selectedDatasetCount);
		}

		model.addAttribute("datasetFilter", datasetFilter);
		model.addAttribute("datasetCodesStr", datasetCodesStr);
		model.addAttribute("selectedDatasetsStr", selectedDatasetsStr);
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
