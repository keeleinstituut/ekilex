package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.SourceType;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.ActivityLogOwnerEntityDescr;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.service.util.PermCalculator;

@Component
public class SourceService extends AbstractSourceService implements GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(SourceService.class);

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public Source getSource(Long sourceId) {
		return getSource(sourceId, null);
	}

	@Transactional
	public Source getSource(Long sourceId, DatasetPermission userRole) {

		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSource(sourceId);
		if (CollectionUtils.isEmpty(sourcePropertyTuples)) {
			logger.warn("No source found for id {}", sourceId);
			return null;
		}
		List<Source> sources = conversionUtil.composeSources(sourcePropertyTuples);
		permCalculator.applyCrud(userRole, sources);
		if (sources.size() > 1) {
			logger.error("Single source query for id {} returned several. Fix this!", sourceId);
		}
		Source source = sources.get(0);

		return source;
	}

	@Transactional
	public Long getSourceId(Long sourcePropertyId) {
		return sourceDbService.getSourceId(sourcePropertyId);
	}

	@Transactional
	public SourceProperty getSourceProperty(Long sourcePropertyId) {
		return sourceDbService.getSourceProperty(sourcePropertyId);
	}

	@Transactional
	public String getSourceNameValue(Long sourceId) {
		return sourceDbService.getSourceNameValue(sourceId);
	}

	@Transactional
	public List<Source> getSources(String searchFilter) {
		return getSources(searchFilter, null, null);
	}

	@Transactional
	public List<Source> getSources(String searchFilter, DatasetPermission userRole) {
		return getSources(searchFilter, null, userRole);
	}

	@Transactional
	public List<Source> getSources(String searchFilter, SourceType sourceType) {
		return getSources(searchFilter, sourceType, null);
	}

	@Transactional
	public List<Source> getSources(String searchFilter, SourceType sourceType, DatasetPermission userRole) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSources(searchFilter, sourceType);
		List<Source> sources = conversionUtil.composeSources(sourcePropertyTuples);
		permCalculator.applyCrud(userRole, sources);

		return sources;
	}

	@Transactional
	public List<Source> getSourcesExcludingOne(String searchFilter, Source excludedSource, DatasetPermission userRole) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		SourceType sourceType = excludedSource.getType();
		Long excludedSourceId = excludedSource.getId();
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSources(searchFilter, sourceType, excludedSourceId);
		List<Source> sources = conversionUtil.composeSources(sourcePropertyTuples);
		permCalculator.applyCrud(userRole, sources);

		return sources;
	}

	@Transactional
	public List<Source> getSources(SearchFilter searchFilter, DatasetPermission userRole) throws Exception {

		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			return new ArrayList<>();
		}
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSources(searchFilter);
		List<Source> sources = conversionUtil.composeSources(sourcePropertyTuples);
		permCalculator.applyCrud(userRole, sources);

		return sources;
	}

	@Transactional
	public List<String> getSourceNames(String nameSearchFilter, int limit) {

		if (StringUtils.isBlank(nameSearchFilter)) {
			return Collections.emptyList();
		}
		return sourceDbService.getSourceNames(nameSearchFilter, limit);
	}

	@Transactional
	public void createSourceProperty(Long sourceId, FreeformType freeformType, String valueText) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSourceProperty", sourceId, ActivityOwner.SOURCE, MANUAL_EVENT_ON_UPDATE_DISABLED);
		Long sourcePropertyId = sourceDbService.createSourceProperty(sourceId, freeformType, valueText);
		activityLogService.createActivityLog(activityLog, sourcePropertyId, freeformType);
	}

	@Transactional
	public void updateSourceProperty(Long sourcePropertyId, String valueText) throws Exception {

		SourceProperty sourceProperty = sourceDbService.getSourceProperty(sourcePropertyId);
		if (sourceProperty == null) {
			throw new OperationDeniedException();
		}
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformOwnerDescr(sourcePropertyId);
		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("updateSourceProperty", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName(), MANUAL_EVENT_ON_UPDATE_DISABLED);
		sourceDbService.updateSourceProperty(sourcePropertyId, valueText);
		activityLogService.createActivityLog(activityLog, sourcePropertyId, freeformOwnerDescr.getEntityName());
	}

	@Transactional
	public void deleteSourceProperty(Long sourcePropertyId) throws Exception {

		SourceProperty sourceProperty = sourceDbService.getSourceProperty(sourcePropertyId);
		if (sourceProperty == null) {
			throw new OperationDeniedException();
		}
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformOwnerDescr(sourcePropertyId);
		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("deleteSourceProperty", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName(), MANUAL_EVENT_ON_UPDATE_DISABLED);
		sourceDbService.deleteSourceProperty(sourcePropertyId);
		activityLogService.createActivityLog(activityLog, sourcePropertyId, freeformOwnerDescr.getEntityName());
	}

	@Transactional
	public void updateSource(Long sourceId, SourceType type) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateSource", sourceId, ActivityOwner.SOURCE, MANUAL_EVENT_ON_UPDATE_DISABLED);
		sourceDbService.updateSourceType(sourceId, type);
		activityLogService.createActivityLog(activityLog, sourceId, ActivityEntity.SOURCE);
	}

	@Transactional
	public boolean validateSourceDelete(Long sourceId) {
		return sourceDbService.validateSourceDelete(sourceId);
	}

	@Transactional
	public void deleteSource(Long sourceId) throws Exception {

		activityLogService.createActivityLog("deleteSource", sourceId, ActivityOwner.SOURCE, MANUAL_EVENT_ON_UPDATE_DISABLED);
		sourceDbService.deleteSource(sourceId);
	}

	@Transactional
	public void joinSources(Long targetSourceId, Long originSourceId) throws Exception {

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("joinSources", originSourceId, ActivityOwner.SOURCE, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("joinSources", targetSourceId, ActivityOwner.SOURCE, MANUAL_EVENT_ON_UPDATE_DISABLED);

		sourceDbService.joinSources(targetSourceId, originSourceId);

		activityLogService.createActivityLog(activityLog1, originSourceId, ActivityEntity.SOURCE);
		activityLogService.createActivityLog(activityLog2, targetSourceId, ActivityEntity.SOURCE);
	}

}
