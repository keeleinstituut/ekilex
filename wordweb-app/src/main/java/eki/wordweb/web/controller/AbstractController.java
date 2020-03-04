package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.CommonDataService;
import eki.wordweb.web.bean.SessionBean;
import eki.wordweb.web.util.UserAgentUtil;
import eki.wordweb.web.util.WebUtil;

public abstract class AbstractController implements WebConstant, SystemConstant {

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

	protected void populateDefaultSearchModel(Model model) {
		populateSearchModel("", new WordsData(SEARCH_MODE_DETAIL), model);
	}

	protected void populateSearchModel(String searchWord, WordsData wordsData, Model model) {

		SessionBean sessionBean = populateCommonModel(model);
		if (StringUtils.isBlank(sessionBean.getSearchMode())) {
			sessionBean.setSearchMode(SEARCH_MODE_DETAIL);
		}
		populateLangFilter(sessionBean, model);
		populateDatasetFilter(sessionBean, model);
		sessionBean.setRecentSearchMode(sessionBean.getSearchMode());

		model.addAttribute("searchWord", searchWord);
		model.addAttribute("wordsData", wordsData);
		model.addAttribute("wordData", new WordData());
	}

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

		boolean isSearchModeChange = !StringUtils.equals(sessionBean.getSearchMode(), sessionBean.getRecentSearchMode());
		List<UiFilterElement> datasetFilter = commonDataService.getDatasetFilter();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		if (CollectionUtils.isEmpty(datasetCodes) || isSearchModeChange) {
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
		model.addAttribute("supportedSimpleDatasets", SUPPORTED_SIMPLE_DATASETS);
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
