package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.SearchCriterionGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.web.bean.SessionBean;
import org.springframework.web.bind.annotation.ModelAttribute;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AbstractSearchController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSearchController.class);

	@Autowired
	protected CommonDataService commonDataService;

	@ModelAttribute("allDatasets")
	public List<Dataset> getAllDatasets() {
		return commonDataService.getDatasets();
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("domains")
	public Map<String,List<Classifier>> getDomainsInUse() {
		return commonDataService.getDomainsInUseByOrigin();
	}

	@ModelAttribute("addDomains")
	public Map<String,List<Classifier>> getAllDomains() {
		return commonDataService.getAllDomainsByOrigin();
	}

	@ModelAttribute("lexemeFrequencyGroups")
	public List<Classifier> getLexemeFrequencyGroups() {
		return commonDataService.getLexemeFrequencyGroups();
	}

	protected void initSearchForms(Model model) {

		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
			model.addAttribute(SESSION_BEAN, sessionBean);
		}
		List<Dataset> allDatasets = getAllDatasets();
		List<String> allDatasetCodes = allDatasets.stream().map(dataset -> dataset.getCode()).collect(Collectors.toList());
		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			sessionBean.setSelectedDatasets(allDatasetCodes);
		}
		List<Classifier> allLanguages = commonDataService.getLanguages();
		if (CollectionUtils.isEmpty(sessionBean.getLanguagesOrder())) {
			List<ClassifierSelect> languagesOrder = convert(allLanguages);
			sessionBean.setLanguagesOrder(languagesOrder);
		}
		Map<String,List<Classifier>> domains = commonDataService.getDomainsInUseByOrigin();
		SearchFilter detailSearchFilter = initSearchFilter();

		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
	}

	private List<ClassifierSelect> convert(List<Classifier> allLanguages) {
		List<ClassifierSelect> languagesOrder = new ArrayList<>();
		for (Classifier language : allLanguages) {
			ClassifierSelect languageSelect = new ClassifierSelect();
			languageSelect.setCode(language.getCode());
			languageSelect.setValue(language.getValue());
			languageSelect.setSelected(true);
			languagesOrder.add(languageSelect);
		}
		return languagesOrder;
	}

	protected void cleanup(
			List<String> selectedDatasets,
			String resultLang,
			String simpleSearchFilter,
			SearchFilter detailSearchFilter,
			SessionBean sessionBean, Model model) throws Exception {

		List<Dataset> allDatasets = getAllDatasets();
		List<String> allDatasetCodes = allDatasets.stream().map(dataset -> dataset.getCode()).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			selectedDatasets = sessionBean.getSelectedDatasets();
			if (CollectionUtils.isEmpty(selectedDatasets)) {
				sessionBean.setSelectedDatasets(allDatasetCodes);
			}
		}
		sessionBean.setResultLang(resultLang);

		if (detailSearchFilter == null) {
			detailSearchFilter = initSearchFilter();
		} else {
			if (CollectionUtils.isEmpty(detailSearchFilter.getCriteriaGroups())) {
				detailSearchFilter.setCriteriaGroups(Collections.emptyList());
			} else {
				List<SearchCriterionGroup> criteriaCroups = detailSearchFilter.getCriteriaGroups().stream()
						.filter(group -> group.getEntity() != null)
						.collect(Collectors.toList());
				detailSearchFilter.setCriteriaGroups(criteriaCroups);
			}
			for (SearchCriterionGroup group : detailSearchFilter.getCriteriaGroups()) {
				if (CollectionUtils.isEmpty(group.getSearchCriteria())) {
					group.setSearchCriteria(Collections.emptyList());
				} else {
					List<SearchCriterion> searchCriteria = group.getSearchCriteria().stream().filter(criterion -> criterion.getSearchKey() != null)
							.collect(Collectors.toList());
					for (SearchCriterion c : searchCriteria) {
						if (c.getSearchKey().equals(SearchKey.DOMAIN)) {
							covertValueToClassifier(c);
						}
					}
					group.setSearchCriteria(searchCriteria);
				}
			}
		}

		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
	}

	private void covertValueToClassifier(SearchCriterion crit) throws Exception {
		if (crit.getSearchValue() != null) {
			if (isNotBlank(crit.getSearchValue().toString())) {
				ObjectMapper mapper = new ObjectMapper();
				Classifier domain = mapper.readValue(crit.getSearchValue().toString(), Classifier.class);
				crit.setSearchValue(domain);
			} else {
				crit.setSearchValue(null);
			}
		}
	}

	protected SearchFilter initSearchFilter() {

		SearchFilter detailSearch = new SearchFilter();
		SearchCriterion defaultCriterion = new SearchCriterion();
		defaultCriterion.setSearchKey(SearchKey.VALUE);
		defaultCriterion.setSearchOperand(SearchKey.VALUE.getOperands()[0]);
		SearchCriterionGroup searchGroup = new SearchCriterionGroup();
		searchGroup.setEntity(SearchEntity.WORD);
		searchGroup.setSearchCriteria(asList(defaultCriterion));
		detailSearch.setCriteriaGroups(asList(searchGroup));
		return detailSearch;
	}
}
