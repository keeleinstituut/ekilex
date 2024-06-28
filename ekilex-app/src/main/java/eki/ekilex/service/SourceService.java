package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.SourceType;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.data.SourceSearchResult;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class SourceService extends AbstractSourceService {

	private static final Logger logger = LoggerFactory.getLogger(SourceService.class);

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public Source getSource(Long sourceId) {

		Source source = sourceDbService.getSource(sourceId);
		if (source == null) {
			logger.warn("No source found for id {}", sourceId);
			return null;
		}

		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSourcePropertyTuples(sourceId);
		conversionUtil.composeSource(source, sourcePropertyTuples);
		return source;
	}

	@Transactional
	public Source getSource(Long sourceId, EkiUser user) {

		Source source = sourceDbService.getSource(sourceId);
		if (source == null) {
			logger.warn("No source found for id {}", sourceId);
			return null;
		}

		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSourcePropertyTuples(sourceId);
		conversionUtil.composeSource(source, sourcePropertyTuples);
		permCalculator.applyCrud(user, source);
		return source;
	}

	@Transactional
	public SourceSearchResult getSourceSearchResult(String searchFilter, EkiUser user) {

		if (StringUtils.isBlank(searchFilter)) {
			return new SourceSearchResult();
		}
		Long userId = user.getId();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userId);
		SourceSearchResult sourceSearchResult = sourceDbService.getSourceSearchResult(searchFilter, searchDatasetsRestriction);
		return convertAndApplyCrud(sourceSearchResult, user);
	}

	@Transactional
	public List<Source> getSourcesBasedOnExcludedOne(String searchFilter, Source excludedSource, EkiUser user) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		String datasetCode = excludedSource.getDatasetCode();
		SourceType sourceType = excludedSource.getType();
		Long excludedSourceId = excludedSource.getId();
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSourcePropertyTuples(searchFilter, datasetCode, sourceType, excludedSourceId);
		List<Source> sources = conversionUtil.composeSources(sourcePropertyTuples);
		permCalculator.applyCrud(user, sources);

		return sources;
	}

	@Transactional
	public SourceSearchResult getSourceSearchResult(SearchFilter searchFilter, EkiUser user) throws Exception {

		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			return new SourceSearchResult();
		}
		Long userId = user.getId();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(userId);
		SourceSearchResult sourceSearchResult = sourceDbService.getSourceSearchResult(searchFilter, searchDatasetsRestriction);
		return convertAndApplyCrud(sourceSearchResult, user);
	}

	private SearchDatasetsRestriction composeDatasetsRestriction(Long userId) {

		List<Dataset> availableDatasets = permissionDbService.getUserVisibleDatasets(userId);
		List<String> availableDatasetCodes = availableDatasets.stream().map(Dataset::getCode).collect(Collectors.toList());
		int availableDatasetsCount = availableDatasets.size();
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
		SearchDatasetsRestriction searchDatasetsRestriction = new SearchDatasetsRestriction();
		searchDatasetsRestriction.setAvailableDatasetCodes(availableDatasetCodes);
		searchDatasetsRestriction.setUserPermDatasetCodes(userPermDatasetCodes);
		searchDatasetsRestriction.setAllDatasetsPermissions(allDatasetsPermissions);
		searchDatasetsRestriction.setSinglePermDataset(singlePermDataset);

		return searchDatasetsRestriction;
	}

	private SourceSearchResult convertAndApplyCrud(SourceSearchResult sourceSearchResult, EkiUser user) {

		List<Source> sources = sourceSearchResult.getSources();

		if (CollectionUtils.isNotEmpty(sources)) {
			// TODO should be removed soon
			for (Source source : sources) {
				Long sourceId = source.getId();
				List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSourcePropertyTuples(sourceId);
				conversionUtil.composeSource(source, sourcePropertyTuples);
			}
			permCalculator.applyCrud(user, sources);
		}

		return sourceSearchResult;
	}

	@Transactional
	public List<String> getSourceNames(String nameSearchFilter, int limit) {

		if (StringUtils.isBlank(nameSearchFilter)) {
			return Collections.emptyList();
		}
		return sourceDbService.getSourceNames(nameSearchFilter, limit);
	}

	@Transactional
	public void updateSource(Source source, String roleDatasetCode) throws Exception {

		Long sourceId = source.getId();
		String valuePrese = source.getValuePrese();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		source.setValue(value);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateSource", sourceId, ActivityOwner.SOURCE, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		sourceDbService.updateSource(source);
		activityLogService.createActivityLog(activityLog, sourceId, ActivityEntity.SOURCE);
	}

	@Transactional
	public boolean validateSourceDelete(Long sourceId) {
		return sourceDbService.validateSourceDelete(sourceId);
	}

	@Transactional
	public void deleteSource(Long sourceId, String roleDatasetCode) throws Exception {

		activityLogService.createActivityLog("deleteSource", sourceId, ActivityOwner.SOURCE, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		sourceDbService.deleteSource(sourceId);
	}

	@Transactional
	public void joinSources(Long targetSourceId, Long originSourceId, String roleDatasetCode) throws Exception {

		// TODO remove this functionality after removing source properties?
		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinSources", originSourceId, ActivityOwner.SOURCE, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinSources", targetSourceId, ActivityOwner.SOURCE, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);

		sourceDbService.joinSources(targetSourceId, originSourceId);

		activityLogService.createActivityLog(activityLog1, originSourceId, ActivityEntity.SOURCE);
		activityLogService.createActivityLog(activityLog2, targetSourceId, ActivityEntity.SOURCE);
	}

}
