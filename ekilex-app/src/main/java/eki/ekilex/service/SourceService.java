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
import eki.common.constant.SourceType;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.service.db.SourceDbService;

@Component
public class SourceService {

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
	public Long addSource(SourceType sourceType, String extSourceId, List<SourceProperty> sourceProperties) {
		Long sourceId = sourceDbService.addSource(sourceType, extSourceId, sourceProperties);
		// TODO lifecycleLog - Yogesh
		return sourceId;
	}

	@Transactional
	public void addSourceProperty(Long sourceId, FreeformType type, String valueText) {
		sourceDbService.addSourceProperty(sourceId, type, valueText);
		// TODO lifecycleLog - Yogesh
	}

	@Transactional
	public void editSourceProperty(Long sourcePropertyId, String valueText) {
		sourceDbService.updateSourceProperty(sourcePropertyId, valueText);
		// TODO lifecycleLog - Yogesh
	}

	@Transactional
	public void deleteSourceProperty(Long sourcePropertyId) {
		sourceDbService.deleteSourceProperty(sourcePropertyId);
		// TODO lifecycleLog - Yogesh
	}

	@Transactional
	public void editSourceType(Long sourceId, SourceType type) {
		sourceDbService.editSourceType(sourceId, type);
		// TODO lifecycleLog - Yogesh
	}

	@Transactional
	public boolean isSourceDeletePossible(Long sourceId) {
		return sourceDbService.isSourceDeletePossible(sourceId);
	}

	@Transactional
	public void deleteSource(Long sourceId) {
		sourceDbService.deleteSource(sourceId);
		// TODO lifecycleLog - Yogesh
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
				String extSourceId = tuple.getExtSourceId();
				Timestamp createdOn = tuple.getCreatedOn();
				String createdBy = tuple.getCreatedBy();
				Timestamp modifiedOn = tuple.getModifiedOn();
				String modifiedBy = tuple.getModifiedBy();
				String processStateCode = tuple.getProcessStateCode();
				SourceType type = tuple.getType();
				source = new Source();
				source.setSourceId(sourceId);
				source.setExtSourceId(extSourceId);
				source.setCreatedOn(createdOn);
				source.setCreatedBy(createdBy);
				source.setModifiedOn(modifiedOn);
				source.setModifiedBy(modifiedBy);
				source.setProcessStateCode(processStateCode);
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
			}

			source.getSourceProperties().add(sourceProperty);
		}
		return sources;
	}
}
