package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.constant.TermSourceLoaderConstant;

public abstract class AbstractTermSourceLoaderRunner extends AbstractLoaderRunner implements TermSourceLoaderConstant {

	protected DateFormat defaultDateFormat;

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

		createLifecycleLog(LifecycleLogOwner.SOURCE, sourceId, sourceId, LifecycleEntity.SOURCE, LifecycleProperty.VALUE, LifecycleEventType.CREATE, createdBy, createdOn, null);
		createLifecycleLog(LifecycleLogOwner.SOURCE, sourceId, sourceId, LifecycleEntity.SOURCE, LifecycleProperty.VALUE, LifecycleEventType.UPDATE, modifiedBy, modifiedOn, null);
	}
}
