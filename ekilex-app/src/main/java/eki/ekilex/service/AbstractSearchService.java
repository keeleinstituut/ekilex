package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.PermConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.PagingResult;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;

public abstract class AbstractSearchService extends AbstractService implements PermConstant {

	@Autowired
	protected CommonDataDbService commonDataDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	protected boolean isValidSearchFilter(SearchFilter searchFilter) {

		if (searchFilter == null) {
			return false;
		}
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			return false;
		}
		for (SearchCriterionGroup searchCriterionGroup : searchFilter.getCriteriaGroups()) {
			if (CollectionUtils.isEmpty(searchCriterionGroup.getSearchCriteria())) {
				continue;
			}
			boolean searchCriteriaExists = searchCriterionGroup.getSearchCriteria().stream().anyMatch(searchCriterion -> StringUtils.isBlank(searchCriterion.getValidationMessage()));
			if (searchCriteriaExists) {
				return true;
			}
		}
		return false;
	}

	protected SearchDatasetsRestriction composeDatasetsRestriction(List<String> selectedDatasetCodes) {

		Long userId = userContext.getUserId();
		return composeDatasetsRestriction(selectedDatasetCodes, userId);
	}

	protected SearchDatasetsRestriction composeDatasetsRestriction(List<String> selectedDatasetCodes, Long userId) {

		SearchDatasetsRestriction searchDatasetsRestriction = new SearchDatasetsRestriction();
		List<Dataset> availableDatasets = permissionDbService.getUserVisibleDatasets(userId);
		List<String> availableDatasetCodes = availableDatasets.stream().map(Dataset::getCode).collect(Collectors.toList());
		int availableDatasetsCount = availableDatasets.size();
		int selectedDatasetsCount = selectedDatasetCodes.size();
		boolean noDatasetsFiltering = selectedDatasetCodes.isEmpty() || selectedDatasetsCount == availableDatasetsCount;
		List<String> filteringDatasetCodes;
		if (noDatasetsFiltering) {
			filteringDatasetCodes = Collections.emptyList();
		} else {
			filteringDatasetCodes = new ArrayList<>(selectedDatasetCodes);
		}
		boolean singleFilteringDataset = filteringDatasetCodes.size() == 1;
		searchDatasetsRestriction.setAvailableDatasetCodes(availableDatasetCodes);
		searchDatasetsRestriction.setFilteringDatasetCodes(filteringDatasetCodes);
		searchDatasetsRestriction.setNoDatasetsFiltering(noDatasetsFiltering);
		searchDatasetsRestriction.setSingleFilteringDataset(singleFilteringDataset);
		List<String> userPermDatasetCodes;
		boolean allDatasetsPermissions;
		if (userId == null) {
			userPermDatasetCodes = Collections.emptyList();
			allDatasetsPermissions = false;
		} else {
			List<Dataset> userPermDatasets = permissionDbService.getUserPermDatasets(userId);
			userPermDatasetCodes = userPermDatasets.stream().map(Dataset::getCode).collect(Collectors.toList());
			int userPermDatasetsCount = userPermDatasetCodes.size();
			allDatasetsPermissions = userPermDatasetsCount == availableDatasetsCount;
		}
		boolean singlePermDataset = userPermDatasetCodes.size() == 1;
		searchDatasetsRestriction.setUserPermDatasetCodes(userPermDatasetCodes);
		searchDatasetsRestriction.setAllDatasetsPermissions(allDatasetsPermissions);
		searchDatasetsRestriction.setSinglePermDataset(singlePermDataset);

		return searchDatasetsRestriction;
	}

	protected SearchLangsRestriction composeLangsRestriction(List<String> preferredLangs) {

		SearchLangsRestriction searchLangsRestriction = new SearchLangsRestriction();
		List<Classifier> allLangs = commonDataDbService.getLanguages(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		int allLangsCount = allLangs.size();
		int preferredLangsCount = preferredLangs.size();
		boolean noLangsFiltering = preferredLangs.isEmpty() || preferredLangsCount == allLangsCount;
		List<String> filteringLangs;
		if (noLangsFiltering) {
			filteringLangs = Collections.emptyList();
		} else {
			filteringLangs = new ArrayList<>(preferredLangs);
		}

		searchLangsRestriction.setFilteringLangs(filteringLangs);
		searchLangsRestriction.setNoLangsFiltering(noLangsFiltering);
		return searchLangsRestriction;
	}

	protected void setPagingData(int offset, int maxResultsLimit, int wordCount, PagingResult result) {

		int totalPages = (wordCount + maxResultsLimit - 1) / maxResultsLimit;
		int currentPage = offset / maxResultsLimit + 1;
		if (currentPage > totalPages) {
			currentPage = totalPages;
		}
		boolean previousPageExists = currentPage > 1;
		boolean nextPageExists = currentPage < totalPages;

		result.setCurrentPage(currentPage);
		result.setTotalPages(totalPages);
		result.setOffset(offset);
		result.setPreviousPageExists(previousPageExists);
		result.setNextPageExists(nextPageExists);
	}

	protected int getLastPageOffset(int resultCount) {

		int totalPages = (resultCount + DEFAULT_MAX_RESULTS_LIMIT - 1) / DEFAULT_MAX_RESULTS_LIMIT;
		int lastPageOffset = (totalPages - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		return lastPageOffset;
	}
}
