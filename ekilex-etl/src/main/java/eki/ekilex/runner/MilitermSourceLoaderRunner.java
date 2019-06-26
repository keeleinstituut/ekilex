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
public class MilitermSourceLoaderRunner extends AbstractTermSourceLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermSourceLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	@Override
	public String getDataset() {
		return "mil";
	}

	@Override
	public Complexity getComplexity() {
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
	public void execute(String milFilePath1, String milFilePath2, boolean doReports) throws Exception {

		logger.debug("Starting loading Militerm sources...");

		start();

		String[] dataXmlFilePaths = new String[] {milFilePath1, milFilePath2};
		Document dataDoc;
		List<Node> conceptGroupNodes;

		int fileCounter = 1;
		for (String dataXmlFilePath : dataXmlFilePaths) {

			int totalFiles = dataXmlFilePaths.length;
			String fileName = FilenameUtils.getName(dataXmlFilePath);
			logger.debug("Loading {} file of {} files. File name: \"{}\"", fileCounter, totalFiles, fileName);

			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			conceptGroupNodes = dataDoc.selectNodes(sourceConceptGroupExp);
			int conceptGroupCount = conceptGroupNodes.size();
			logger.debug("{} concept groups found", conceptGroupCount);

			int conceptGroupCounter = 0;
			int lastProgressPercent = 0;
			for (Node conceptGroupNode : conceptGroupNodes) {

				processConceptGroup(conceptGroupNode, fileName);

				conceptGroupCounter++;
				double progressPercent = ((double) conceptGroupCounter / conceptGroupCount) * 100;
				int progressPercentRounded = (int) Math.round(progressPercent);
				if (progressPercentRounded != lastProgressPercent) {
					lastProgressPercent = progressPercentRounded;
					logger.debug("File {}/{}. {}% - {} concept groups iterated", fileCounter, totalFiles, progressPercentRounded, conceptGroupCounter);
				}
			}
			fileCounter++;
		}

		end();
	}

	private void processConceptGroup(Node conceptGroupNode, String fileName) throws Exception {

		Node extSourceIdNode = conceptGroupNode.selectSingleNode(conceptExp);
		Element extSourceIdElement = (Element) extSourceIdNode;
		String extSourceId = extSourceIdElement.getTextTrim();

		Node sourceNameNode = conceptGroupNode.selectSingleNode(termGroupExp + "/" + termValueExp);
		Element sourceNameElement = (Element) sourceNameNode;
		String sourceName = sourceNameElement.getTextTrim();

		Long sourceId = getSource(SourceType.DOCUMENT, extSourceId, sourceName, fileName);
		if (sourceId == null) {

			Source source = new Source();
			source.setType(SourceType.DOCUMENT);
			sourceId = createSource(source);

			extractAndCreateSourceLifecycleLog(sourceId, conceptGroupNode);
			createSourceFreeform(sourceId, FreeformType.EXTERNAL_SOURCE_ID, extSourceId);
			createSourceFreeform(sourceId, FreeformType.SOURCE_FILE, fileName);

			List<Node> termGroupNodes = conceptGroupNode.selectNodes(termGroupExp);
			for (Node termGroupNode : termGroupNodes) {

				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_NAME, termValueExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.LTB_SOURCE, sourceLtbSourceExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_WWW, sourceWwwExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_AUTHOR, sourceAuthorExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_ISBN, sourceIsbnExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_ISSN, sourceIssnExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLISHER, sourcePublisherExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLICATION_YEAR, sourcePublicationYearExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.SOURCE_PUBLICATION_PLACE, sourcePublicationPlaceExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.PUBLIC_NOTE, sourceNoteExp);

				extractAndCreateSourceLifecycleLog(sourceId, termGroupNode);
			}
		}
	}

}
