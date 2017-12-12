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
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceHeadingPropertyTuple;
import eki.ekilex.data.SourceMember;
import eki.ekilex.service.db.SourceDbService;

@Component
public class SourceService {

	private static final Logger logger = LoggerFactory.getLogger(SourceService.class);

	@Autowired
	private SourceDbService sourceDbService;

	@Transactional
	public Source getSource(Long sourceId) {

		List<SourceHeadingPropertyTuple> sourceHeadingPropertyTuples = sourceDbService.getSource(sourceId).into(SourceHeadingPropertyTuple.class);
		if (CollectionUtils.isEmpty(sourceHeadingPropertyTuples)) {
			logger.warn("No source found for id {}", sourceId);
			return null;
		}
		List<Source> sources = convert(sourceHeadingPropertyTuples);
		if (sources.size() > 1) {
			logger.error("Single source query for id {} returned several. Fix this!", sourceId);
		}
		Source source = sources.get(0);

		return source;
	}

	@Transactional
	public List<Source> findSourcesByNameOrCode(String searchFilter) {

		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		List<SourceHeadingPropertyTuple> sourceHeadingPropertyTuples = sourceDbService.findSources(searchFilter).into(SourceHeadingPropertyTuple.class);
		List<Source> sources = convert(sourceHeadingPropertyTuples);

		return sources;
	}

	private List<Source> convert(List<SourceHeadingPropertyTuple> sourceHeadingPropertyTuples) {

		List<Source> sources = new ArrayList<>();
		Map<Long, Source> sourceMap = new HashMap<>();
		Map<Long, SourceMember> sourceHeadingMap = new HashMap<>();

		for (SourceHeadingPropertyTuple tuple : sourceHeadingPropertyTuples) {

			Long sourceId = tuple.getSourceId();
			Long sourceHeadingId = tuple.getSourceHeadingId();
			FreeformType sourceHeadingType = tuple.getSourceHeadingType();
			String sourceHeadingValue = tuple.getSourceHeadingValue();
			boolean sourceHeadingMatch = tuple.isSourceHeadingMatch();
			Long sourcePropertyId = tuple.getSourcePropertyId();
			FreeformType sourcePropertyType = tuple.getSourcePropertyType();
			String sourcePropertyValueText = tuple.getSourcePropertyValueText();
			Timestamp sourcePropertyValueDate = tuple.getSourcePropertyValueDate();
			boolean sourcePropertyMatch = tuple.isSourcePropertyMatch();

			List<SourceMember> sourceHeadings;
			List<SourceMember> sourceProperties;

			Source source = sourceMap.get(sourceId);
			if (source == null) {
				String concept = tuple.getConcept();
				Timestamp createdOn = tuple.getCreatedOn();
				String createdBy = tuple.getCreatedBy();
				Timestamp modifiedOn = tuple.getModifiedOn();
				String modifiedBy = tuple.getModifiedBy();
				String processStateCode = tuple.getProcessStateCode();
				String type = tuple.getType();
				sourceHeadings = new ArrayList<>();
				source = new Source();
				source.setSourceId(sourceId);
				source.setConcept(concept);
				source.setCreatedOn(createdOn);
				source.setCreatedBy(createdBy);
				source.setModifiedOn(modifiedOn);
				source.setModifiedBy(modifiedBy);
				source.setProcessStateCode(processStateCode);
				source.setType(type);
				source.setSourceHeadings(sourceHeadings);
				sourceMap.put(sourceId, source);
				sources.add(source);
			} else {
				sourceHeadings = source.getSourceHeadings();
			}

			SourceMember sourceHeading = sourceHeadingMap.get(sourceHeadingId);
			if (sourceHeading == null) {
				sourceProperties = new ArrayList<>();
				sourceHeading = new SourceMember();
				sourceHeading.setId(sourceHeadingId);
				sourceHeading.setType(sourceHeadingType);
				sourceHeading.setValueText(sourceHeadingValue);
				sourceHeading.setValueMatch(sourceHeadingMatch);
				sourceHeading.setChildren(sourceProperties);
				sourceHeadingMap.put(sourceHeadingId, sourceHeading);
				sourceHeadings.add(sourceHeading);
			} else {
				sourceProperties = sourceHeading.getChildren();
			}

			if (sourcePropertyId == null) {
				continue;
			}

			SourceMember sourceProperty = new SourceMember();
			sourceProperty.setId(sourcePropertyId);
			sourceProperty.setType(sourcePropertyType);
			sourceProperty.setValueText(sourcePropertyValueText);
			sourceProperty.setValueDate(sourcePropertyValueDate);
			sourceProperty.setValueMatch(sourcePropertyMatch);
			sourceProperties.add(sourceProperty);
		}
		return sources;
	}
}
