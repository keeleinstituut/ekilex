package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
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
public class EstermSourceLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermSourceLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private final String sourceConceptGroupExp = "/mtf/conceptGrp[languageGrp/language/@type='Allikas']";
	private final String conceptExp = "concept";
	private final String entryClassExp = "system[@type='entryClass']";
	private final String createdByExp = "transacGrp/transac[@type='origination']";
	private final String createdOnExp = "transacGrp[transac/@type='origination']/date";
	private final String modifiedByExp = "transacGrp/transac[@type='modification']";
	private final String modifiedOnExp = "transacGrp[transac/@type='modification']/date";
	/*
	 * Such field has never been found.
	 * Instead, source type is set programmatically.
	 * If the field will be valued in the future, some sort of mapping must be implemented.
	 */
	@Deprecated
	private final String sourceTypeExp = "descripGrp/descrip[@type='Tüüp']";
	private final String termGroupExp = "languageGrp/termGrp";
	private final String termValueExp = "term";
	private final String sourceLtbSourceExp = "descripGrp/descrip[@type='Päritolu']";
	private final String sourceRtExp = "descripGrp/descrip[@type='RT']";
	private final String sourceCelexExp = "descripGrp/descrip[@type='CELEX']";
	private final String sourceWwwExp = "descripGrp/descrip[@type='WWW']";
	private final String sourceAuthorExp = "descripGrp/descrip[@type='Autor']";
	private final String sourceIsbnExp = "descripGrp/descrip[@type='ISBN']";
	private final String sourceIssnExp = "descripGrp/descrip[@type='ISSN']";
	private final String sourcePublisherExp = "descripGrp/descrip[@type='Kirjastus']";
	private final String sourcePublicationYearExp = "descripGrp/descrip[@type='Ilmumisaasta']";
	private final String sourcePublicationPlaceExp = "descripGrp/descrip[@type='Ilmumiskoht']";
	private final String sourcePublicationNameExp = "descripGrp/descrip[@type='Väljaande nimi, nr']";
	private final String sourceNoteExp = "descripGrp/descrip[@type='Märkus']";

	private DateFormat defaultDateFormat;

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

		long t1, t2;
		t1 = System.currentTimeMillis();

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

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

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
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

	private void extractAndSaveFreeforms(Long sourceId, Node termGroupNode, FreeformType freeformType, String sourceTermPropertyExp) throws Exception {

		List<Node> sourceTermPropertyNodes = termGroupNode.selectNodes(sourceTermPropertyExp);
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		for (Node sourceTermPropertyNode : sourceTermPropertyNodes) {
			valueStr = ((Element)sourceTermPropertyNode).getTextTrim();
			if (StringUtils.isBlank(valueStr)) {
				continue;
			}
			if (FreeformType.CREATED_ON.equals(freeformType)
					|| FreeformType.MODIFIED_ON.equals(freeformType)) {
				valueLong = defaultDateFormat.parse(valueStr).getTime();
				valueTs = new Timestamp(valueLong);
				createSourceFreeform(sourceId, freeformType, valueTs);
			} else {
				createSourceFreeform(sourceId, freeformType, valueStr);
			}
		}
	}
}
