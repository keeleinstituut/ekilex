package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.data.transform.Source;

@Component
public class EstermSourceLoaderRunner extends AbstractTermSourceLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermSourceLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	@Override
	public String getDataset() {
		return "est";
	}

	@Override
	public void deleteDatasetData() throws Exception {

	}

	@Override
	public void initialise() throws Exception {

		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
	}

	@Transactional
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Starting loading Esterm sources...");

		start();

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		String fileName = FilenameUtils.getName(dataXmlFilePath);

		List<Node> conceptGroupNodes = dataDoc.selectNodes(sourceConceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		List<Node> termGroupNodes;
		Source sourceObj;
		Long sourceId;

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Node conceptGroupNode : conceptGroupNodes) {

			Node extSourceIdNode = conceptGroupNode.selectSingleNode(conceptExp);
			Element extSourceIdElement = (Element) extSourceIdNode;
			String extSourceId = extSourceIdElement.getTextTrim();

			Node sourceNameNode = conceptGroupNode.selectSingleNode(termGroupExp + "/" + termValueExp);
			Element sourceNameElement = (Element) sourceNameNode;
			String sourceName = sourceNameElement.getTextTrim();

			sourceId = getSource(SourceType.UNKNOWN, extSourceId, sourceName);
			if (sourceId == null) {

				sourceObj = extractAndApplySourceProperties(conceptGroupNode);
				sourceId = createSource(sourceObj);

				createSourceFreeform(sourceId, FreeformType.SOURCE_FILE, fileName);

				termGroupNodes = conceptGroupNode.selectNodes(termGroupExp);
				for (Node termGroupNode : termGroupNodes) {

					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_NAME, termValueExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.LTB_SOURCE, sourceLtbSourceExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_RT, sourceRtExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_CELEX, sourceCelexExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_WWW, sourceWwwExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_AUTHOR, sourceAuthorExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_ISBN, sourceIsbnExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_ISSN, sourceIssnExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLISHER, sourcePublisherExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLICATION_YEAR, sourcePublicationYearExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLICATION_PLACE, sourcePublicationPlaceExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLICATION_NAME, sourcePublicationNameExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.PUBLIC_NOTE, sourceNoteExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.CREATED_BY, createdByExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.CREATED_ON, createdOnExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.MODIFIED_BY, modifiedByExp);
					extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.MODIFIED_ON, modifiedOnExp);
				}
			}

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		end();
	}

	private Source extractAndApplySourceProperties(Node conceptGroupNode) throws ParseException {

		Source sourceObj = new Source();

		Node valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		/*
		 * TODO missing logic to determine about correct source type.
		 * Info about source type is in one or many of the nesting term groups
		 */
		sourceObj.setType(SourceType.UNKNOWN);

		valueNode = conceptGroupNode.selectSingleNode(conceptExp);
		if (valueNode != null) {
			valueStr = ((Element)valueNode).getTextTrim();
			sourceObj.setExtSourceId(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(entryClassExp);
		if (valueNode != null) {
			valueStr = ((Element)valueNode).getTextTrim();
			sourceObj.setProcessStateCode(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = ((Element)valueNode).getTextTrim();
			sourceObj.setCreatedBy(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = ((Element)valueNode).getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			sourceObj.setCreatedOn(valueTs);
		}

		valueNode = conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = ((Element)valueNode).getTextTrim();
			sourceObj.setModifiedBy(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = ((Element)valueNode).getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			sourceObj.setModifiedOn(valueTs);
		}

		/* 
		 * Currently suspended
		 * Type now reserved for other logic
		 * 
		valueNode = (Element) conceptGroupNode.selectSingleNode(sourceTypeExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			sourceObj.setType(valueStr);
		}
		*/

		return sourceObj;
	}

}
