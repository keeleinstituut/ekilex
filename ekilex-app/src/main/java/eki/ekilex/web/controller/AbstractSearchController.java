package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import eki.ekilex.constant.SearchEntity;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterionGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import static java.util.Arrays.asList;

public abstract class AbstractSearchController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSearchController.class);

	@Autowired
	protected CommonDataService commonDataService;

	protected void initSearchForms(Model model) {

		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
			model.addAttribute(SESSION_BEAN, sessionBean);
		}
		List<Dataset> allDatasets = commonDataService.getDatasets();
		List<String> allDatasetCodes = allDatasets.stream().map(dataset -> dataset.getCode()).collect(Collectors.toList());
		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			sessionBean.setSelectedDatasets(allDatasetCodes);
		}
		List<Classifier> allLanguages = commonDataService.getLanguages();
		SearchFilter detailSearchFilter = initSearchFilter();

		model.addAttribute("allDatasets", allDatasets);
		model.addAttribute("allLanguages", allLanguages);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
	}

	protected void cleanup(
			List<String> selectedDatasets,
			String resultLang,
			String simpleSearchFilter,
			SearchFilter detailSearchFilter,
			SessionBean sessionBean, Model model) {

		List<Dataset> allDatasets = commonDataService.getDatasets();
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
			List<SearchCriterion> searchCriteria = detailSearchFilter.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				searchCriteria = Collections.emptyList();
			} else {
				searchCriteria = searchCriteria.stream()
						.filter(criterion ->
								(criterion.getSearchKey() != null)
								&& (criterion.getSearchValue() != null)
								&& StringUtils.isNotBlank(criterion.getSearchValue().toString()))
						.collect(Collectors.toList());
			}
			detailSearchFilter.setSearchCriteria(searchCriteria);

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
					searchCriteria = group.getSearchCriteria().stream()
							.filter(criterion -> criterion.getSearchKey() != null)
							.collect(Collectors.toList());
					group.setSearchCriteria(searchCriteria);
				}
			}
		}

		List<Classifier> allLanguages = commonDataService.getLanguages();

		model.addAttribute("allDatasets", allDatasets);
		model.addAttribute("allLanguages", allLanguages);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
	}

	protected SearchFilter initSearchFilter() {

		SearchFilter detailSearch = new SearchFilter();
		detailSearch.setSearchCriteria(new ArrayList<>());
		SearchCriterion defaultCriterion = new SearchCriterion();
		defaultCriterion.setSearchKey(SearchKey.VALUE);
		defaultCriterion.setSearchOperand(SearchKey.VALUE.getOperands()[0]);
		detailSearch.getSearchCriteria().add(defaultCriterion);
		SearchCriterionGroup searchGroup = new SearchCriterionGroup();
		searchGroup.setEntity(SearchEntity.WORD);
		searchGroup.setSearchCriteria(asList(defaultCriterion));
		detailSearch.setCriteriaGroups(asList(searchGroup));
		return detailSearch;
	}
}
