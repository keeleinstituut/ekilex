package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.util.ConversionUtil;

public abstract class AbstractSearchService implements SystemConstant {

	@Autowired
	protected ConversionUtil conversionUtil;

	@Autowired
	protected CommonDataDbService commonDataDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private UserService userService;

	protected SearchDatasetsRestriction composeDatasetsRestriction(List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = new SearchDatasetsRestriction();
		List<Dataset> availableDatasets = commonDataDbService.getDatasets();
		int availableDatasetsCount = availableDatasets.size();
		int selectedDatasetsCount = selectedDatasetCodes.size();
		boolean noDatasetsFiltering = selectedDatasetsCount == availableDatasetsCount;
		List<String> filteringDatasetCodes;
		if (noDatasetsFiltering) {
			filteringDatasetCodes = Collections.emptyList();
		} else {
			filteringDatasetCodes = new ArrayList<>(selectedDatasetCodes);
		}
		searchDatasetsRestriction.setFilteringDatasetCodes(filteringDatasetCodes);
		searchDatasetsRestriction.setNoDatasetsFiltering(noDatasetsFiltering);
		Long userId = userService.getAuthenticatedUser().getId();
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
			if (allDatasetsPermissions) {
				userPermDatasetCodes = Collections.emptyList();
			}			
		}
		searchDatasetsRestriction.setUserPermDatasetCodes(userPermDatasetCodes);
		searchDatasetsRestriction.setAllDatasetsPermissions(allDatasetsPermissions);

		return searchDatasetsRestriction;
	}
}
