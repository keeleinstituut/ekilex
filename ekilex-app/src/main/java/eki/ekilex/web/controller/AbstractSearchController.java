package eki.ekilex.web.controller;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

public abstract class AbstractSearchController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSearchController.class);

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected SearchHelper searchHelper;

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

	@ModelAttribute("allDomains")
	public Map<String,List<Classifier>> getAllDomains() {
		return commonDataService.getAllDomainsByOrigin();
	}

	@ModelAttribute("lexemeFrequencyGroups")
	public List<Classifier> getLexemeFrequencyGroups() {
		return commonDataService.getFrequencyGroups();
	}

	@ModelAttribute("wordMorphCodes")
	public List<Classifier> getWordMorphCodes() {
		return commonDataService.getMorphs();
	}

	@ModelAttribute("wordGenders")
	public List<Classifier> getWordGenders() {
		return commonDataService.getGenders();
	}

	@ModelAttribute("wordTypes")
	public List<Classifier> getWordTypes() {
		return commonDataService.getWordTypes();
	}

	@ModelAttribute("wordAspects")
	public List<Classifier> getWordAspect() {
		return commonDataService.getAspects();
	}

	@ModelAttribute("wordRelationTypes")
	public List<Classifier> getWordRelationTypes() {
		return commonDataService.getWordRelationTypes();
	}

	@ModelAttribute("lexemeRelationTypes")
	public List<Classifier> getLexemeRelationTypes() {
		return commonDataService.getLexemeRelationTypes();
	}

	@ModelAttribute("meaningRelationTypes")
	public List<Classifier> getMeaningRelationTypes() {
		return commonDataService.getMeaningRelationTypes();
	}

	@ModelAttribute("allLexemePos")
	public List<Classifier> getLexemePos() {
		return commonDataService.getPoses();
	}

	@ModelAttribute("allLexemeRegisters")
	public List<Classifier> getLexemeRegisters() {
		return commonDataService.getRegisters();
	}

	@ModelAttribute("allLexemeDerivs")
	public List<Classifier> getLexemeDerivs() {
		return commonDataService.getDerivs();
	}

	@ModelAttribute("lexemeValueStates")
	public List<Classifier> getLexemeValueStates() {
		return commonDataService.getValueStates();
	}

	@ModelAttribute("processStates")
	public List<Classifier> getProcessStates() {
		return commonDataService.getProcessStates();
	}

	protected void initSearchForms(Model model) {

		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
			model.addAttribute(SESSION_BEAN, sessionBean);
		}
		List<String> allDatasetCodes = commonDataService.getDatasetCodes();
		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			sessionBean.setSelectedDatasets(allDatasetCodes);
		}
		List<Classifier> allLanguages = commonDataService.getLanguages();
		if (CollectionUtils.isEmpty(sessionBean.getLanguagesOrder())) {
			List<ClassifierSelect> languagesOrder = convert(allLanguages);
			sessionBean.setLanguagesOrder(languagesOrder);
		}
		SearchFilter detailSearchFilter = searchHelper.initSearchFilter();

		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
	}

	private List<ClassifierSelect> convert(List<Classifier> allLanguages) {
		List<ClassifierSelect> languagesOrder = allLanguages.stream().map(language -> {
			ClassifierSelect languageSelect = new ClassifierSelect();
			languageSelect.setCode(language.getCode());
			languageSelect.setValue(language.getValue());
			languageSelect.setSelected(true);
			return languageSelect;
		}).collect(Collectors.toList());
		return languagesOrder;
	}

	protected void formDataCleanup(
			List<String> selectedDatasets,
			String simpleSearchFilter,
			SearchFilter detailSearchFilter,
			String resultLang,
			SessionBean sessionBean, Model model) throws Exception {

		List<String> allDatasetCodes = commonDataService.getDatasetCodes();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			selectedDatasets = sessionBean.getSelectedDatasets();
			if (CollectionUtils.isEmpty(selectedDatasets)) {
				sessionBean.setSelectedDatasets(allDatasetCodes);
			}
		}
		sessionBean.setResultLang(resultLang);

		if (detailSearchFilter == null) {
			detailSearchFilter = searchHelper.initSearchFilter();
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
					List<SearchCriterion> searchCriteria = group.getSearchCriteria().stream()
							.filter(criterion -> criterion.getSearchKey() != null)
							.collect(Collectors.toList());
					for (SearchCriterion crit : searchCriteria) {
						if (crit.getSearchKey().equals(SearchKey.DOMAIN)) {
							convertJsonToClassifier(crit);
						}
					}
					group.setSearchCriteria(searchCriteria);
				}
			}
		}
	}

	private void convertJsonToClassifier(SearchCriterion crit) throws Exception {
		if (crit.getSearchValue() != null) {
			if (isNotBlank(crit.getSearchValue().toString())) {
				ObjectMapper mapper = new ObjectMapper();
				Classifier classifier = mapper.readValue(crit.getSearchValue().toString(), Classifier.class);
				crit.setSearchValue(classifier);
			} else {
				crit.setSearchValue(null);
			}
		}
	}
}
