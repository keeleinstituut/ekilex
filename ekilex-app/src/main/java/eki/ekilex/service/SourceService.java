package eki.ekilex.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceHeadingPropertyTuple;
import eki.ekilex.service.db.SourceDbService;

@Component
public class SourceService {

	@Autowired
	private SourceDbService sourceDbService;

	public List<Source> findSources(String searchFilter) {

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
		Map<Long, FreeForm> sourceHeadingMap = new HashMap<>();

		for (SourceHeadingPropertyTuple tuple : sourceHeadingPropertyTuples) {

			Long sourceId = tuple.getSourceId();
			Long sourceHeadingId = tuple.getSourceHeadingId();
			FreeformType sourceHeadingType = tuple.getSourceHeadingType();
			String sourceHeadingValue = tuple.getSourceHeadingValue();
			Long sourcePropertyId = tuple.getSourcePropertyId();
			FreeformType sourcePropertyType = tuple.getSourcePropertyType();
			String sourcePropertyValueText = tuple.getSourcePropertyValueText();
			Timestamp sourcePropertyValueDate = tuple.getSourcePropertyValueDate();

			List<FreeForm> sourceHeadings;
			List<FreeForm> sourceProperties;

			Source source = sourceMap.get(sourceId);
			if (source == null) {
				String concept = tuple.getConcept();
				Timestamp createdOn = tuple.getCreatedOn();
				String createdBy = tuple.getCreatedBy();
				Timestamp modifiedOn = tuple.getModifiedOn();
				String modifiedBy = tuple.getModifiedBy();
				String entryClassCode = tuple.getEntryClassCode();
				String type = tuple.getType();
				sourceHeadings = new ArrayList<>();
				source = new Source();
				source.setSourceId(sourceId);
				source.setConcept(concept);
				source.setCreatedOn(createdOn);
				source.setCreatedBy(createdBy);
				source.setModifiedOn(modifiedOn);
				source.setModifiedBy(modifiedBy);
				source.setEntryClassCode(entryClassCode);
				source.setType(type);
				source.setSourceHeadings(sourceHeadings);
				sourceMap.put(sourceId, source);
				sources.add(source);
			} else {
				sourceHeadings = source.getSourceHeadings();
			}

			FreeForm sourceHeading = sourceHeadingMap.get(sourceHeadingId);
			if (sourceHeading == null) {
				sourceProperties = new ArrayList<>();
				sourceHeading = new FreeForm();
				sourceHeading.setId(sourceHeadingId);
				sourceHeading.setType(sourceHeadingType);
				sourceHeading.setValueText(sourceHeadingValue);
				sourceHeading.setChildren(sourceProperties);
				sourceHeadingMap.put(sourceHeadingId, sourceHeading);
				sourceHeadings.add(sourceHeading);
			} else {
				sourceProperties = sourceHeading.getChildren();
			}

			FreeForm sourceProperty = new FreeForm();
			sourceProperty.setId(sourcePropertyId);
			sourceProperty.setType(sourcePropertyType);
			sourceProperty.setValueText(sourcePropertyValueText);
			sourceProperty.setValueDate(sourcePropertyValueDate);
			sourceProperties.add(sourceProperty);
		}
		return sources;
	}
}
