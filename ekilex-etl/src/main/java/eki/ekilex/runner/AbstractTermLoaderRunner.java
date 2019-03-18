package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;

import org.dom4j.Element;
import org.dom4j.Node;

import eki.ekilex.constant.TermLoaderConstant;
import eki.ekilex.data.transform.Meaning;

public abstract class AbstractTermLoaderRunner extends AbstractLoaderRunner implements TermLoaderConstant {

	void extractAndApplyMeaningProperties(Node conceptGroupNode, Meaning meaningObj, DateFormat dateFormat) throws Exception {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setCreatedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = dateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setCreatedOn(valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setModifiedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = dateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setModifiedOn(valueTs);
		}
	}
}
