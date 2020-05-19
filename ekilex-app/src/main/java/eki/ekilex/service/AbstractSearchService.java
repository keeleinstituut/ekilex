package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.PagingResult;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.util.ConversionUtil;

public abstract class AbstractSearchService extends AbstractService implements SystemConstant, GlobalConstant {

	@Autowired
	protected ConversionUtil conversionUtil;

	@Autowired
	protected CommonDataDbService commonDataDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	protected UserService userService;

	protected SearchDatasetsRestriction composeDatasetsRestriction(List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = new SearchDatasetsRestriction();
		EkiUser user = userService.getAuthenticatedUser();
		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();
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
		searchDatasetsRestriction.setUserRole(userRole);
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

	protected void setPagingData(int offset, int wordCount, PagingResult result) {

		int currentPage = offset / MAX_RESULTS_LIMIT + 1;
		int totalPages = (wordCount + MAX_RESULTS_LIMIT - 1) / MAX_RESULTS_LIMIT;
		boolean previousPageExists = currentPage > 1;
		boolean nextPageExists = currentPage < totalPages;

		result.setCurrentPage(currentPage);
		result.setTotalPages(totalPages);
		result.setPreviousPageExists(previousPageExists);
		result.setNextPageExists(nextPageExists);
	}
}
