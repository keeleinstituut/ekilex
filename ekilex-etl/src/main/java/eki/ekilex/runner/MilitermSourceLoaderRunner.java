package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.data.transform.Source;
import eki.ekilex.service.ReportComposer;

@Component
public class MilitermSourceLoaderRunner extends AbstractTermSourceLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermSourceLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String REPORT_ILLEGAL_CLASSIFIERS = "illegal_classifiers";

	private Map<String, String> processStateCodes;

	private ReportComposer reportComposer;

	@Override
	public String getDataset() {
		return "mil";
	}

	@Override
	public void deleteDatasetData() throws Exception {
	}

	@Override
	public void initialise() throws Exception {
		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
		processStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_ENTRY_CLASS, ClassifierName.PROCESS_STATE.name());
	}

	@Transactional
	public void execute(String milFilePath1, String milFilePath2, boolean doReports) throws Exception {

		logger.debug("Starting loading Militerm sources...");

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", REPORT_ILLEGAL_CLASSIFIERS);
		}
		start();

		String[] dataXmlFilePaths = new String[] {milFilePath1, milFilePath2};
		Document dataDoc;
		List<Node> allConceptGroupNodes = new ArrayList<>();
		List<Node> conceptGroupNodes;

		for (String dataXmlFilePath : dataXmlFilePaths) {

			logger.debug("Loading \"{}\"", dataXmlFilePath);
			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			conceptGroupNodes = dataDoc.selectNodes(sourceConceptGroupExp);
			allConceptGroupNodes.addAll(conceptGroupNodes);
		}

		int conceptGroupCount = allConceptGroupNodes.size();
		logger.debug("{} concept groups found", conceptGroupCount);

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Node conceptGroupNode : allConceptGroupNodes) {

			processConceptGroup(conceptGroupNode);

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		end();
	}

	private void processConceptGroup(Node conceptGroupNode) throws Exception {

		Long sourceId;

		Node extSourceIdNode = conceptGroupNode.selectSingleNode(conceptExp);
		Element extSourceIdElement = (Element) extSourceIdNode;
		String extSourceId = extSourceIdElement.getTextTrim();

		Node sourceNameNode = conceptGroupNode.selectSingleNode(termGroupExp + "/" + termValueExp);
		Element sourceNameElement = (Element) sourceNameNode;
		String sourceName = sourceNameElement.getTextTrim();

		sourceId = getSource(SourceType.UNKNOWN, extSourceId, sourceName);
		if (sourceId == null) {

			Source source = extractAndApplySourceProperties(conceptGroupNode, extSourceId);
			sourceId = createSource(source);

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
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.CREATED_BY, createdByExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.CREATED_ON, createdOnExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.MODIFIED_BY, modifiedByExp);
				extractAndSaveFreeforms(sourceId, termGroupNode, FreeformType.MODIFIED_ON, modifiedOnExp);
			}
		}
	}

	private Source extractAndApplySourceProperties(Node conceptGroupNode, String concept) throws Exception {

		Source source = new Source();

		Node valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		source.setType(SourceType.UNKNOWN);

		valueNode = conceptGroupNode.selectSingleNode(conceptExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			source.setExtSourceId(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(entryClassExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			if (processStateCodes.containsKey(valueStr)) {
				source.setProcessStateCode(valueStr);
			} else {
				appendToReport(true, REPORT_ILLEGAL_CLASSIFIERS, concept, "tundmatu entryClass väärtus: " + valueStr);
			}
		}

		valueNode = conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			source.setCreatedBy(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			source.setCreatedOn(valueTs);
		}

		valueNode = conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			source.setModifiedBy(valueStr);
		}

		valueNode = conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = ((Element) valueNode).getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			source.setModifiedOn(valueTs);
		}

		return source;
	}

	private void appendToReport(boolean doReports, String reportName, String... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

}
