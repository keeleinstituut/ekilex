package eki.ekilex.web.controller;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

public abstract class AbstractSearchController extends AbstractPageController {

	private static final String DEFAULT_DEFINITION_TYPE_CODE = "määramata";

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected SearchHelper searchHelper;

	@Autowired
	protected UserService userService;

	@ModelAttribute("domains")
	public Map<String, List<Classifier>> getDomainsInUse() {
		return commonDataService.getDomainsInUseByOrigin();
	}

	@ModelAttribute("datasetDomains")
	public Map<String, List<Classifier>> getDatasetDomains(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return Collections.emptyMap();
		}
		return commonDataService.getDatasetDomainsByOrigin(userRole.getDatasetCode());
	}

	@ModelAttribute("processStates")
	public List<Classifier> getProcessStates(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return Collections.emptyList();
		}
		return commonDataService.getProcessStatesByDataset(userRole.getDatasetCode());
	}

	@ModelAttribute("lexemeFrequencyGroups")
	public List<Classifier> getLexemeFrequencyGroups() {
		return commonDataService.getFrequencyGroups();
	}

	@ModelAttribute("wordMorphCodes")
	public List<Classifier> getWordMorphCodes() {
		return commonDataService.getMorphs();
	}

	@ModelAttribute("wordTypes")
	public List<Classifier> getWordTypes() {
		return commonDataService.getWordTypes();
	}

	@ModelAttribute("definitionTypes")
	public List<Classifier> getDefinitionTypes() {
		return commonDataService.getDefinitionTypes();
	}

	@ModelAttribute("allLexemePos")
	public List<Classifier> getLexemePos() {
		return commonDataService.getPoses();
	}

	@ModelAttribute("allLexemeRegisters")
	public List<Classifier> getLexemeRegisters() {
		return commonDataService.getRegisters();
	}

	@ModelAttribute("allLexemeRegions")
	public List<Classifier> getLexemeRegions() {
		return commonDataService.getRegions();
	}

	@ModelAttribute("allLexemeDerivs")
	public List<Classifier> getLexemeDerivs() {
		return commonDataService.getDerivs();
	}

	@ModelAttribute("lexemeValueStates")
	public List<Classifier> getLexemeValueStates() {
		return commonDataService.getValueStates();
	}

	@ModelAttribute("semanticTypes")
	public List<Classifier> getSemanticTypes() {
		return commonDataService.getSemanticTypes();
	}

	@ModelAttribute("defaultDefinitionTypeCode")
	public String getDefaultDefinitionTypeCode() {
		return DEFAULT_DEFINITION_TYPE_CODE;
	}

	protected void resetUserRole(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		if (sessionBean.getUserRole() == null) {
			EkiUser user = userService.getAuthenticatedUser();
			sessionBean.setUserRole(user.getRecentRole());
		}
	}

	protected void initSearchForms(String searchPage, Model model) {

		SessionBean sessionBean = getSessionBean(model);
		if (CollectionUtils.isEmpty(sessionBean.getLanguagesOrder())) {
			List<Classifier> allLanguages = commonDataService.getLanguages();
			List<ClassifierSelect> languagesOrder = convert(allLanguages);
			sessionBean.setLanguagesOrder(languagesOrder);
		}
		if (sessionBean.getTermSearchResultMode() == null) {
			sessionBean.setTermSearchResultMode(SearchResultMode.WORD);
		}
		SearchFilter detailSearchFilter = searchHelper.initSearchFilter(searchPage);
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
			String searchPage,
			List<String> selectedDatasets,
			SearchFilter detailSearchFilter,
			SessionBean sessionBean) throws Exception {

		if (CollectionUtils.isNotEmpty(selectedDatasets)) {
			userService.updateUserPreferredDatasets(selectedDatasets);
		}
		if (detailSearchFilter == null) {
			detailSearchFilter = searchHelper.initSearchFilter(searchPage);
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
