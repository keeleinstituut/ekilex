package eki.ekilex.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.SourceType;
import eki.ekilex.data.LogData;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.service.db.SourceDbService;

@Component
public class SourceService extends AbstractService {

	private static final Logger logger = LoggerFactory.getLogger(SourceService.class);

	@Autowired
	private SourceDbService sourceDbService;

	@Transactional
	public Source getSource(Long sourceId) {

		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSource(sourceId);
		if (CollectionUtils.isEmpty(sourcePropertyTuples)) {
			logger.warn("No source found for id {}", sourceId);
			return null;
		}
		List<Source> sources = convert(sourcePropertyTuples);
		if (sources.size() > 1) {
			logger.error("Single source query for id {} returned several. Fix this!", sourceId);
		}
		Source source = sources.get(0);

		return source;
	}

	@Transactional
	public String getSourcePropertyValue(Long sourcePropertyId) {
		return sourceDbService.getSourcePropertyValue(sourcePropertyId);
	}

	@Transactional
	public String getSourceNameValue(Long sourceId) {
		return sourceDbService.getSourceNameValue(sourceId);
	}

	@Transactional
	public List<Source> getSources(String searchFilter) {
		return getSources(searchFilter, null);
	}

	@Transactional
	public List<Source> getSources(String searchFilter, SourceType sourceType) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSources(searchFilter, sourceType);
		List<Source> sources = convert(sourcePropertyTuples);

		return sources;
	}

	@Transactional
	public List<Source> getSourcesExcludingOne(String searchFilter, Source excludedSource) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		SourceType sourceType = excludedSource.getType();
		Long excludedSourceId = excludedSource.getSourceId();
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSources(searchFilter, sourceType, excludedSourceId);
		List<Source> sources = convert(sourcePropertyTuples);

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
	public Long createSource(SourceType sourceType, List<SourceProperty> sourceProperties) {

		Long sourceId = sourceDbService.createSource(sourceType, sourceProperties);
		LogData sourceValueLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.SOURCE, LifecycleProperty.VALUE, sourceId);
		createLifecycleLog(sourceValueLogData);
		LogData sourceTypeLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.SOURCE, LifecycleProperty.SOURCE_TYPE, sourceId, sourceType.name());
		createLifecycleLog(sourceTypeLogData);
		for (SourceProperty sourceProperty : sourceProperties) {
			LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(sourceProperty.getType().name());
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.SOURCE, lifecycleProperty, sourceId, sourceProperty.getValueText());
			createLifecycleLog(logData);
		}
		return sourceId;
	}

	@Transactional
	public void createSourceProperty(Long sourceId, FreeformType type, String valueText) {

		LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(type.name());
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.SOURCE, lifecycleProperty, sourceId, valueText);
		createLifecycleLog(logData);
		sourceDbService.createSourceProperty(sourceId, type, valueText);
	}

	@Transactional
	public void updateSourceProperty(Long sourcePropertyId, FreeformType type, String valueText) {

		LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(type.name());
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.SOURCE, lifecycleProperty, sourcePropertyId, valueText);
		createLifecycleLog(logData);
		sourceDbService.updateSourceProperty(sourcePropertyId, valueText);
	}

	@Transactional
	public void deleteSourceProperty(Long sourcePropertyId, FreeformType type) {

		LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(type.name());
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.SOURCE, lifecycleProperty, sourcePropertyId);
		createLifecycleLog(logData);
		sourceDbService.deleteSourceProperty(sourcePropertyId);
	}

	@Transactional
	public void updateSourceType(Long sourceId, SourceType type) {

		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.SOURCE, LifecycleProperty.SOURCE_TYPE, sourceId, type.name());
		createLifecycleLog(logData);
		sourceDbService.updateSourceType(sourceId, type);
	}

	@Transactional
	public boolean validateSourceDelete(Long sourceId) {
		return sourceDbService.validateSourceDelete(sourceId);
	}

	@Transactional
	public void deleteSource(Long sourceId) {

		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.SOURCE, LifecycleProperty.VALUE, sourceId);
		createLifecycleLog(logData);
		sourceDbService.deleteSource(sourceId);
	}

	@Transactional
	public void joinSources(Long firstSourceId, Long secondSourceId) {

		String firstSourceNames = joinSourceNames(firstSourceId);
		String secondSourceNames = joinSourceNames(secondSourceId);
		LogData logData = new LogData(
				LifecycleEventType.JOIN, LifecycleEntity.SOURCE, LifecycleProperty.VALUE, firstSourceId, secondSourceNames, firstSourceNames);
		createLifecycleLog(logData);

		sourceDbService.joinSources(firstSourceId, secondSourceId);
	}

	private String joinSourceNames(Long sourceId) {

		List<String> names = sourceDbService.getSourceAttributesByType(sourceId, FreeformType.SOURCE_NAME);
		String joinedNames = StringUtils.join(names, "; ");
		return joinedNames;
	}

	private List<Source> convert(List<SourcePropertyTuple> sourcePropertyTuples) {

		List<Source> sources = new ArrayList<>();
		Map<Long, Source> sourceMap = new HashMap<>();
		Map<Long, SourceProperty> sourcePropertyMap = new HashMap<>();

		for (SourcePropertyTuple tuple : sourcePropertyTuples) {

			Long sourceId = tuple.getSourceId();
			Long sourcePropertyId = tuple.getSourcePropertyId();
			FreeformType sourcePropertyType = tuple.getSourcePropertyType();
			String sourcePropertyValueText = tuple.getSourcePropertyValueText();
			Timestamp sourcePropertyValueDate = tuple.getSourcePropertyValueDate();
			boolean sourcePropertyMatch = tuple.isSourcePropertyMatch();

			Source source = sourceMap.get(sourceId);
			if (source == null) {
				SourceType type = tuple.getType();
				source = new Source();
				source.setSourceId(sourceId);
				source.setType(type);
				source.setSourceNames(new ArrayList<>());
				source.setSourceProperties(new ArrayList<>());
				sourceMap.put(sourceId, source);
				sources.add(source);
			}

			SourceProperty sourceProperty = sourcePropertyMap.get(sourcePropertyId);

			if (sourceProperty == null) {
				sourceProperty = new SourceProperty();
				sourceProperty.setId(sourcePropertyId);
				sourceProperty.setType(sourcePropertyType);
				sourceProperty.setValueText(sourcePropertyValueText);
				sourceProperty.setValueDate(sourcePropertyValueDate);
				sourceProperty.setValueMatch(sourcePropertyMatch);
				sourcePropertyMap.put(sourcePropertyId, sourceProperty);
			}

			if (FreeformType.SOURCE_NAME.equals(sourcePropertyType)) {
				source.getSourceNames().add(sourcePropertyValueText);
				source.getSourceProperties().add(0, sourceProperty);
			} else {
				source.getSourceProperties().add(sourceProperty);
			}

		}
		return sources;
	}

}
