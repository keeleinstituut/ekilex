package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.constant.TermSourceLoaderConstant;

public abstract class AbstractTermSourceLoaderRunner extends AbstractLoaderRunner implements TermSourceLoaderConstant {

	DateFormat defaultDateFormat;

	protected void extractAndSaveFreeforms(Long sourceId, Node termGroupNode, FreeformType freeformType, String sourceTermPropertyExp) throws Exception {

		List<Node> sourceTermPropertyNodes = termGroupNode.selectNodes(sourceTermPropertyExp);
		String valueStr;

		for (Node sourceTermPropertyNode : sourceTermPropertyNodes) {
			valueStr = ((Element) sourceTermPropertyNode).getTextTrim();
			if (StringUtils.isBlank(valueStr)) {
				continue;
			}
			createSourceFreeform(sourceId, freeformType, valueStr);
		}
	}

	protected void extractAndCreateSourceLifecycleLog(Long sourceId, Node conceptGroupNode) throws Exception {

		Node valueNode;
		String valueStr;
		long valueLong;

		String createdBy = null;
		Timestamp createdOn = null;
		String modifiedBy = null;
		Timestamp modifiedOn = null;

		valueNode = conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			createdBy = ((Element) valueNode).getTextTrim();
		}

		valueNode = conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			createdOn = new Timestamp(valueLong);
		}

		valueNode = conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			modifiedBy = ((Element) valueNode).getTextTrim();
		}

		valueNode = conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			modifiedOn = new Timestamp(valueLong);
		}

		createSourceLifecycleLog(sourceId, LifecycleEventType.CREATE, LifecycleProperty.VALUE, null, createdOn, createdBy);
		createSourceLifecycleLog(sourceId, LifecycleEventType.UPDATE, LifecycleProperty.VALUE, null, modifiedOn, modifiedBy);
	}

	private void createSourceLifecycleLog(Long sourceId, LifecycleEventType eventType, LifecycleProperty property, String entry, Timestamp eventOn,
			String eventBy) throws Exception {

		if (eventBy == null) {
			eventBy = "Ekileks " + getDataset() + "-laadur";
		}

		Map<String, Object> lifecycleLogMap = new HashMap<>();
		lifecycleLogMap.put("entity_id", sourceId);
		lifecycleLogMap.put("entity_name", LifecycleEntity.SOURCE.name());
		lifecycleLogMap.put("entity_prop", property.name());
		lifecycleLogMap.put("event_type", eventType.name());
		lifecycleLogMap.put("event_by", eventBy);
		lifecycleLogMap.put("entry", entry);
		if (eventOn != null) {
			lifecycleLogMap.put("event_on", eventOn);
		}
		Long lifecycleLogId = basicDbService.create(LIFECYCLE_LOG, lifecycleLogMap);


		Map<String, Object> sourceLifecycleLogMap = new HashMap<>();
		sourceLifecycleLogMap.put("source_id", sourceId);
		sourceLifecycleLogMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(SOURCE_LIFECYCLE_LOG, sourceLifecycleLogMap);
	}
}
