package eki.ekilex.web.controller;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

public abstract class AbstractSearchController extends AbstractAuthActionController {

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected SearchHelper searchHelper;

	protected String simpleSearchCleanup(String searchValue) {
		searchValue = StringUtils.trim(searchValue);
		searchValue = StringUtils.replaceChars(searchValue, "%", SEARCH_MASK_CHARS);
		return searchValue;
	}

	@ModelAttribute("domains")
	public Map<String, List<Classifier>> getDomainsInUse() {
		return commonDataService.getDomainsInUseByOrigin();
	}

	@ModelAttribute("datasetDomains")
	public Map<String, List<Classifier>> getDatasetDomains() {

		DatasetPermission userRole = userContext.getUserRole();
		if (userRole == null) {
			return Collections.emptyMap();
		}
		String datasetCode = userRole.getDatasetCode();
		return commonDataService.getDatasetDomainsByOrigin(datasetCode);
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

	@ModelAttribute("enabledDefinitionTypes")
	public List<Classifier> getEnabledDefinitionTypes() {
		List<Classifier> definitionTypes = commonDataService.getDefinitionTypes();
		definitionTypes = definitionTypes.stream()
				.filter(type -> !StringUtils.equals(type.getCode(), DEFINITION_TYPE_CODE_INEXACT_SYN))
				.collect(Collectors.toList());
		return definitionTypes;
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

	@ModelAttribute("lexemeProficiencyLevels")
	public List<Classifier> getLexemeProficiencyLevels() {
		return commonDataService.getProficiencyLevels();
	}

	@ModelAttribute("semanticTypes")
	public List<Classifier> getSemanticTypes() {
		return commonDataService.getSemanticTypes();
	}

	@ModelAttribute("defaultDefinitionTypeCode")
	public String getDefaultDefinitionTypeCode() {
		return DEFINITION_TYPE_CODE_UNDEFINED;
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

	protected void formDataCleanup(String searchPage, SearchFilter detailSearchFilter) throws Exception {

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

	protected int getPageNumAndCalculateOffset(HttpServletRequest request) {

		String pageNumParam = request.getParameter(REQUEST_PARAM_PAGE);
		if (StringUtils.isBlank(pageNumParam)) {
			return DEFAULT_OFFSET;
		}
		Integer pageNum = null;
		try {
			pageNum = Integer.valueOf(pageNumParam);
		} catch (NumberFormatException e) {
			return DEFAULT_OFFSET;
		}
		if (pageNum == null) {
			return DEFAULT_OFFSET;
		}
		if (pageNum < 1) {
			return DEFAULT_OFFSET;
		}
		int offset = (pageNum - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		return offset;
	}
}
