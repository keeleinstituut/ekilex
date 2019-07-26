package eki.ekilex.runner;

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

import eki.common.constant.Complexity;
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
	public Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	public Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	public Complexity getFreeformComplexity() {
		return Complexity.DEFAULT;
	}

	@Override
	public void deleteDatasetData() {

	}

	@Override
	public void initialise() {

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

			sourceId = getSource(SourceType.DOCUMENT, extSourceId, sourceName, fileName);
			if (sourceId == null) {

				sourceObj = new Source();
				sourceObj.setType(SourceType.DOCUMENT);
				sourceId = createSource(sourceObj);

				extractAndCreateSourceLifecycleLog(sourceId, conceptGroupNode);
				createSourceFreeform(sourceId, FreeformType.EXTERNAL_SOURCE_ID, extSourceId);
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

					extractAndCreateSourceLifecycleLog(sourceId, termGroupNode);
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

}
