package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.TermSourceLoaderConstant;

public abstract class AbstractTermSourceLoaderRunner extends AbstractLoaderRunner implements TermSourceLoaderConstant {

	DateFormat defaultDateFormat;

	void extractAndSaveFreeforms(Long sourceId, Node termGroupNode, FreeformType freeformType, String sourceTermPropertyExp) throws Exception {

		List<Node> sourceTermPropertyNodes = termGroupNode.selectNodes(sourceTermPropertyExp);
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		for (Node sourceTermPropertyNode : sourceTermPropertyNodes) {
			valueStr = ((Element) sourceTermPropertyNode).getTextTrim();
			if (StringUtils.isBlank(valueStr)) {
				continue;
			}
			if (FreeformType.CREATED_ON.equals(freeformType) || FreeformType.MODIFIED_ON.equals(freeformType)) {
				valueLong = defaultDateFormat.parse(valueStr).getTime();
				valueTs = new Timestamp(valueLong);
				createSourceFreeform(sourceId, freeformType, valueTs);
			} else {
				createSourceFreeform(sourceId, freeformType, valueStr);
			}
		}
	}
}
