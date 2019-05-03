package eki.ekilex.service;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.SourceDbService;

@Component
public class SourceService {

	private static final Logger logger = LoggerFactory.getLogger(SourceService.class);

	@Autowired
	private SourceDbService sourceDbService;

	@Autowired
	private UserService userService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

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
	public List<Source> findSourcesByName(String searchFilter) {
		return findSourcesByNameAndType(searchFilter, null);
	}

	@Transactional
	public List<Source> findSourcesByNameAndType(String searchFilter, SourceType sourceType) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.findSourcesByNameAndType(searchFilter, sourceType);
		List<Source> sources = convert(sourcePropertyTuples);

		return sources;
	}

	@Transactional
	public List<Source> findSourcesToJoin(String searchFilter, Source initiatorSource) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		SourceType sourceType = initiatorSource.getType();
		Long sourceIdToExclude = initiatorSource.getSourceId();
		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService
				.findSourcesByNameAndTypeExcludingOneSource(searchFilter, sourceType, sourceIdToExclude);
		List<Source> sources = convert(sourcePropertyTuples);

		return sources;
	}

	@Transactional
	public Long addSource(SourceType sourceType, List<SourceProperty> sourceProperties) {

		Long sourceId = sourceDbService.addSource(sourceType, sourceProperties);
		addSourceLifecycleLog(LifecycleEventType.CREATE, LifecycleProperty.VALUE, sourceId, null);
		addSourceLifecycleLog(LifecycleEventType.CREATE, LifecycleProperty.SOURCE_TYPE, sourceId, sourceType.name());
		for (SourceProperty sourceProperty : sourceProperties) {
			LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(sourceProperty.getType().name());
			addSourceLifecycleLog(LifecycleEventType.CREATE, lifecycleProperty, sourceId, sourceProperty.getValueText());
		}
		return sourceId;
	}

	@Transactional
	public void addSourceProperty(Long sourceId, FreeformType type, String valueText) {

		LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(type.name());
		addSourceLifecycleLog(LifecycleEventType.CREATE, lifecycleProperty, sourceId, valueText);
		sourceDbService.addSourceProperty(sourceId, type, valueText);
	}

	@Transactional
	public void editSourceProperty(Long sourcePropertyId, FreeformType type, String valueText) {

		LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(type.name());
		addSourceLifecycleLog(LifecycleEventType.UPDATE, lifecycleProperty, sourcePropertyId, valueText);
		sourceDbService.updateSourceProperty(sourcePropertyId, valueText);
	}

	@Transactional
	public void deleteSourceProperty(Long sourcePropertyId, FreeformType type) {

		LifecycleProperty lifecycleProperty = LifecycleProperty.valueOf(type.name());
		addSourceLifecycleLog(LifecycleEventType.DELETE, lifecycleProperty, sourcePropertyId, null);
		sourceDbService.deleteSourceProperty(sourcePropertyId);
	}

	@Transactional
	public void editSourceType(Long sourceId, SourceType type) {

		addSourceLifecycleLog(LifecycleEventType.UPDATE, LifecycleProperty.SOURCE_TYPE, sourceId, type.name());
		sourceDbService.editSourceType(sourceId, type);
	}

	@Transactional
	public boolean validateSourceDelete(Long sourceId) {
		return sourceDbService.validateSourceDelete(sourceId);
	}

	@Transactional
	public void deleteSource(Long sourceId) {

		addSourceLifecycleLog(LifecycleEventType.DELETE, LifecycleProperty.VALUE, sourceId, null);
		sourceDbService.deleteSource(sourceId);
	}

	@Transactional
	public void joinSources(Long firstSourceId, Long secondSourceId) {

		addSourceLifecycleLog(LifecycleEventType.JOIN, LifecycleProperty.VALUE, firstSourceId, String.valueOf(secondSourceId), String.valueOf(firstSourceId));
		sourceDbService.joinSources(firstSourceId, secondSourceId);
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

	private void addSourceLifecycleLog(LifecycleEventType eventType, LifecycleProperty property, Long entityId, String entry) {
		addSourceLifecycleLog(eventType, property, entityId, null, entry);
	}

	private void addSourceLifecycleLog(LifecycleEventType eventType, LifecycleProperty property, Long entityId, String recent, String entry) {

		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.addLog(userName, eventType, LifecycleEntity.SOURCE, property, entityId, recent, entry);
	}

}
