package eki.ekilex.runner;

import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.service.ReportComposer;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class MilitermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private final String languageGrpExp = "languageGrp";
	private final String languageExp = "language";
	private final String termGrpExp = "termGrp";
	private final String termExp = "term";

	private final String createdByExp = "transacGrp/transac[@type='origination']";
	private final String createdOnExp = "transacGrp[transac/@type='origination']/date";
	private final String modifiedByExp = "transacGrp/transac[@type='modification']";
	private final String modifiedOnExp = "transacGrp[transac/@type='modification']/date";

	private final String ltbSourceExp = "descripGrp/descrip[@type='Päritolu']";
	private final String noteExp = "descripGrp/descrip[@type='Märkus']";
	private final String privateNoteExp = "descripGrp/descrip[@type='Sisemärkus']";
	private final String listExp = "descripGrp/descrip[@type='Loend']";
	private final String imageExp = "descripGrp/descrip[@type='Joonis']";
	private final String meaningDomainExp = "descripGrp/descrip[@type='Valdkonnakood']";
	private final String sourceExp = "descripGrp/descrip[@type='Allikaviide']";
	private final String definitionExp = "descripGrp/descrip[@type='Definitsioon']";
	private final String coincidentExp = "descripGrp/descrip[@type='Kattuvus']";
	private final String regionExp = "descripGrp/descrip[@type='Kasutus']";
	private final String usageExp = "descripGrp/descrip[@type='Kontekst']";
	private final String meaningRelationExp = "descripGrp/descrip[@type='Seotud']";
	private final String explanationExp = "descripGrp/descrip[@type='Selgitus']";

	private ReportComposer reportComposer;

	private DateFormat defaultDateFormat;

	@Override
	public String getDataset() {
		return "mil";
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	public void initialise() throws Exception {
		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
	}

	@Transactional
	public void execute(String milFilePath1, String milFilePath2, boolean doReports) throws Exception {

		logger.debug("Starting loading Militerm...");

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", "TODO"); // TODO
		}
		start();

		String[] dataXmlFilePaths = new String[] {milFilePath1, milFilePath2};
		Document dataDoc;
		Element rootElement;
		List<Node> allConceptGroupNodes = new ArrayList<>();
		List<Node> conceptGroupNodes;

		for (String dataXmlFilePath : dataXmlFilePaths) {

			logger.debug("Loading \"{}\"", dataXmlFilePath);
			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			rootElement = dataDoc.getRootElement();
			conceptGroupNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
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

	@Transactional // kas see annotatsioon on siin vajalik?
	void processConceptGroup(Node conceptGroupNode) throws Exception {

		Meaning meaning = new Meaning();

		extractAndApplyMeaningProperties(conceptGroupNode, meaning);
		Long meaningId = createMeaning(meaning);
		extractAndApplyMeaningFreeforms(meaningId, conceptGroupNode);

		Element meaningDomainValueNode = (Element) conceptGroupNode.selectSingleNode(meaningDomainExp);
		if (meaningDomainValueNode != null) {
			String meaningDomainValue = meaningDomainValueNode.getTextTrim();
			// TODO meaning domain
		}

		List<Node> langGroupNodes = conceptGroupNode.selectNodes(languageGrpExp);
		for (Node langGroupNode : langGroupNodes) {

			Element languageNode = (Element) langGroupNode.selectSingleNode(languageExp);
			// TODO word.lang või definition.lang - pistab, et militerm-aap sees on keeled ja militerm-yld sees valdkonnad

			List<Node> termGroupNodes = langGroupNode.selectNodes(termGrpExp);

			for (Node termGroupNode : termGroupNodes) {

				Element termValueNode = (Element) termGroupNode.selectSingleNode(termExp);
				String term = termValueNode.getTextTrim();
				// TODO form+paradigm+word+lexeme
				Lexeme lexeme = new Lexeme();

				extractAndApplyLexemeProperties(termGroupNode, lexeme);

				List<Node> sourceValueNodes = termGroupNode.selectNodes(sourceExp);
				for (Node sourceValueNode : sourceValueNodes) {
					// TODO  lexeme source
					// sources = extractContentAndRefs(sourceNode, lang, term, false);
					// saveLexemeSourceLinks(lexemeId, sources, concept, term);
				}

				List<Node> definitionValueNodes = termGroupNode.selectNodes(definitionExp);
				for (Node definitionValueNode : definitionValueNodes) {
					// TODO sisaldab nurksulgudes allikaviiteid nagu esterm; kõik selles conceptGrpis sisalduvad defid lähevad kokku sama tähenduse juurde.
					//  type on uus klassifikaator defi juures, defil on 1 type
					// definitions = extractContentAndRefs(definitionNode, lang, term, true);
					// saveDefinitionsAndSourceLinks(meaningId, definitions, concept, term);
				}

				Element coincidentValueNode = (Element) termGroupNode.selectSingleNode(coincidentExp);
				if (coincidentValueNode != null) {
					String coincidentValue = coincidentValueNode.getTextTrim();
					// TODO lexeme freeform private_note
				}

				List<Node> usageValueNodes1 = termGroupNode.selectNodes(regionExp); // rename
				for (Node usageValueNode : usageValueNodes1) {
					String usageValue = ((Element) usageValueNode).getTextTrim();
					// TODO word.region
				}

				List<Node> usageValueNodes = termGroupNode.selectNodes(usageExp);
				if (CollectionUtils.isNotEmpty(usageValueNodes)) {
					for (Node usageValueNode : usageValueNodes) {
						// TODO sisaldab nurksulgudes allikaviiteid nagu esterm
						// usages = extractContentAndRefs(usageNode, lang, term, true);
						// saveUsagesAndSourceLinks(lexemeId, usages, concept, term);
					}
				}

				List<Node> noteValueNodes = conceptGroupNode.selectNodes(noteExp);
				for (Node noteValueNode : noteValueNodes) {
					String noteValue = ((Element) noteValueNode).getTextTrim();
					// TODO lexeme freeform public_note
					// Long freeformId = createDefinitionFreeform(definitionId, FreeformType.PUBLIC_NOTE, definitionNote);
					// if (((Element)definitionNoteNode).hasMixedContent()) {
					// 	definitionNote = handleFreeformRefLinks(definitionNoteNode, freeformId);
					// 	updateFreeformText(freeformId, definitionNote);
					// }
				}

				List<Node> meaningRelationValueNodes = conceptGroupNode.selectNodes(meaningRelationExp);
				for (Node meaningRelationValueNode : meaningRelationValueNodes) {
					// TODO sisaldab mitut väärtust
					// tekita seos, kui näidatud keelend on ühene. kui on mitmene või puudub, siis logi
				}

				List<Node> explanaitonValueNodes = conceptGroupNode.selectNodes(explanationExp);
				for (Node explanaitonValueNode : explanaitonValueNodes) {
					// TODO definitionExp sarnane
				}

			}
		}

	}

	private void extractAndApplyMeaningProperties(Node conceptGroupNode, Meaning meaning) throws ParseException {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaning.setCreatedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaning.setCreatedOn(valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaning.setModifiedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaning.setModifiedOn(valueTs);
		}
	}

	private void extractAndApplyLexemeProperties(Node termGroupNode, Lexeme lexeme) throws ParseException {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) termGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			lexeme.setCreatedBy(valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			lexeme.setCreatedOn(valueTs);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			lexeme.setModifiedBy(valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			lexeme.setModifiedOn(valueTs);
		}
	}

	private void extractAndApplyMeaningFreeforms(Long meaningId, Node conceptGroupNode) throws Exception {

		List<Node> valueNodes;
		Element valueNode, valueNode1, valueNode2;
		String valueStr, valueStr1, valueStr2;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(ltbSourceExp);
		// TODO päritolu node sees on erinevad väärtused, tuleb vaadata, millist vaja
		// TODO arvatavasti saab kasutada sarnast lahendust nagu allikaviite korral
		// if (valueNode != null) {
		// }

		valueNodes = conceptGroupNode.selectNodes(noteExp);
		for (Node publicNoteValueNode : valueNodes) {
			valueStr = ((Element) publicNoteValueNode).getTextTrim();
			// TODO
			// Long freeformId = createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
			// if (((Element)noteValueNode).hasMixedContent()) {
			// 	valueStr = handleFreeformRefLinks(noteValueNode, freeformId);
			// 	updateFreeformText(freeformId, valueStr);
			// }
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(privateNoteExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			// TODO
			// Long freeformId = createMeaningFreeform(meaningId, FreeformType.PRIVATE_NOTE, valueStr);
			// if (valueNode.hasMixedContent()) {
			// 	valueStr = handleFreeformRefLinks(valueNode, freeformId);
			// 	updateFreeformText(freeformId, valueStr);
			// }
		}

		valueNodes = conceptGroupNode.selectNodes(listExp);
		for (Node listValueNode : valueNodes) {
			valueStr = ((Element) listValueNode).getTextTrim();
			// TODO meaningule uus freeformitüüp "LIST"
		}

		valueNodes = conceptGroupNode.selectNodes(imageExp);
		for (Node imageValueNode : valueNodes) {
			valueStr = ((Element) imageValueNode).getTextTrim();
			// TODO meaning freeform image_file
		}
	}
}
