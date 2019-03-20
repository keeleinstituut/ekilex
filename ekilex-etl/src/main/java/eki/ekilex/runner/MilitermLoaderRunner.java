package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

import static java.util.stream.Collectors.toList;

@Component
public class MilitermLoaderRunner extends AbstractTermLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

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
			reportComposer = new ReportComposer(getDataset() + " loader", REPORT_ILLEGAL_SOURCE, REPORT_MISSING_SOURCE_REFS);
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

		illegalSourceValueCount = new Count();
		int conceptGroupCount = allConceptGroupNodes.size();
		logger.debug("{} concept groups found", conceptGroupCount);

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Node conceptGroupNode : allConceptGroupNodes) {

			boolean isLanguageTypeConcept = isLanguageTypeConcept(conceptGroupNode);
			if (isLanguageTypeConcept) {
				processConceptGroup(conceptGroupNode);
			}

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		logger.debug("Found {} illegal source values", illegalSourceValueCount.getValue());

		end();
	}

	private void processConceptGroup(Node conceptGroupNode) throws Exception {

		Meaning meaning = new Meaning();
		String term;
		String lang;

		extractAndApplyMeaningProperties(conceptGroupNode, meaning, defaultDateFormat); // TODO lifecycle log või mingi teine log? - logisse tähenduse looja ja loomisaeg
		Long meaningId = createMeaning(meaning);
		extractAndApplyMeaningFreeforms(meaningId, conceptGroupNode);

		List<Node> langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);
		for (Node langGroupNode : langGroupNodes) {

			Element languageNode = (Element) langGroupNode.selectSingleNode(langExp);
			String langTypeValue = languageNode.attributeValue(langTypeAttr);
			boolean isLang = isLang(langTypeValue); // other possible values are "Valdkond" and "Allikas"
			if (!isLang) {
				continue;
			}
			// TODO word.lang või definition.lang - kas sellega on siin vaja veel midagi teha?
			lang = unifyLang(langTypeValue);

			List<Node> termGroupNodes = langGroupNode.selectNodes(termGroupExp);

			for (Node termGroupNode : termGroupNodes) {

				Element termValueNode = (Element) termGroupNode.selectSingleNode(termExp);
				term = termValueNode.getTextTrim();

				// form+paradigm+word+lexeme
				// form ja paradigm - kas nendega tuleb kuidagi tegeleda või on korras?
				// mõistetüüpi (nagu estermis on extractWordType()) pole vist vaja, sest excelis pole märgitud?
				int homonymNr = getWordMaxHomonymNr(term, lang);
				homonymNr++;
				Word word = new Word(term, lang, null, null, null, null, homonymNr, DEFAULT_WORD_MORPH_CODE, null, null);
				Long wordId = createOrSelectWord(word, null, null, null);

				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(wordId);
				lexeme.setMeaningId(meaningId);
				// processStateCode pole vist vaja, sest excelis pole märgitud?
				Long lexemeId = createLexeme(lexeme, getDataset());

				extractAndApplyLexemeProperties(termGroupNode, lexeme); // TODO kas see sobib?

				// estermis on selliseid kasutatud, kas siin on ka vaja?
				// extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);
				// extractAndUpdateLexemeProperties(lexemeId, termGroupNode);

				List<Node> sourceValueNodes = termGroupNode.selectNodes(sourceExp);
				for (Node sourceValueNode : sourceValueNodes) {
					List<Content> sources = extractContentAndRefs(sourceValueNode, lang, term, false);
					saveLexemeSourceLinks(lexemeId, sources, term);
				}

				List<Node> definitionValueNodes = termGroupNode.selectNodes(definitionExp);
				for (Node definitionValueNode : definitionValueNodes) {
					// TODO sisaldab nurksulgudes allikaviiteid nagu esterm; kõik selles conceptGrpis sisalduvad defid lähevad kokku sama tähenduse juurde.
					//  type on uus klassifikaator defi juures, defil on 1 type
					List<Content> sources = extractContentAndRefs(definitionValueNode, lang, term, true);
					saveDefinitionsAndSourceLinks(meaningId, sources, term);
				}

				Element coincidentValueNode = (Element) termGroupNode.selectSingleNode(overlapExp);
				if (coincidentValueNode != null) {
					String coincidentValue = coincidentValueNode.getTextTrim();
					createLexemeFreeform(lexemeId, FreeformType.PRIVATE_NOTE, coincidentValue, lang);
				}

				List<Node> regionValueNodes = termGroupNode.selectNodes(regionExp);
				for (Node regionValueNode : regionValueNodes) {
					String regionValue = ((Element) regionValueNode).getTextTrim();
					// TODO word.region - uus klassifikaator sõna juurde
				}

				List<Node> usageValueNodes = termGroupNode.selectNodes(usageExp);
				if (CollectionUtils.isNotEmpty(usageValueNodes)) {
					for (Node usageValueNode : usageValueNodes) {
						List<Content> sources = extractContentAndRefs(usageValueNode, lang, term, true);
						saveUsagesAndSourceLinks(meaningId, sources, term);
					}
				}

				List<Node> noteValueNodes = conceptGroupNode.selectNodes(noteExp);
				for (Node noteValueNode : noteValueNodes) {
					String noteValue = ((Element) noteValueNode).getTextTrim();
					Long freeformId = createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, noteValue, lang);
					if (((Element) noteValueNode).hasMixedContent()) {
						String value = handleFreeformRefLinks(noteValueNode, freeformId);
						updateFreeformText(freeformId, value);
					}
				}

				List<Node> meaningRelationValueNodes = termGroupNode.selectNodes(meaningRelationExp);
				for (Node meaningRelationValueNode : meaningRelationValueNodes) {
					createMeaningRelations(meaningRelationValueNode, meaningId);
				}

				List<Node> explanationValueNodes = conceptGroupNode.selectNodes(explanationExp);
				for (Node explanationValueNode : explanationValueNodes) {
					// TODO sisaldab nurksulgudes allikaviiteid nagu esterm; kõik selles conceptGrpis sisalduvad defid lähevad kokku sama tähenduse juurde.
					//  type on uus klassifikaator defi juures, defil on 1 type
					List<Content> sources = extractContentAndRefs(explanationValueNode, lang, term, true);
					saveDefinitionsAndSourceLinks(meaningId, sources, term);
				}

			}
		}

		Element meaningDomainValueNode = (Element) conceptGroupNode.selectSingleNode(meaningDomainExp);
		if (meaningDomainValueNode != null) {
			//  sellel on erinevad variandid, võib sisaldada Tlinki (mixedContent) või on koodi väärtused semikooloniga eraldatud
			Iterator<Node> iter = meaningDomainValueNode.nodeIterator();
			DefaultText textContentNode;
			DefaultElement elemContentNode;
			StringBuilder valuesB = new StringBuilder();

			while (iter.hasNext()) {
				Node contentNode = iter.next();
				if (contentNode instanceof DefaultText) {
					textContentNode = (DefaultText) contentNode;
					valuesB.append(textContentNode.getText());
				} else if (contentNode instanceof DefaultElement) {
					elemContentNode = (DefaultElement) contentNode;
					valuesB.append(elemContentNode.getTextTrim());
				}
			}

			if (valuesB.length() > 0) {
				String values = valuesB.toString();
				int domainDelimCount = StringUtils.countMatches(values, meaningDomainDelimiter);
				if (domainDelimCount == 0) {
					handleDomain(meaningId, values, "TODO domainOrigin");
				} else {
					String[] separateDomainCodes = values.split(meaningDomainDelimiter + "\\s*");
					for (String separateDomainCode : separateDomainCodes) {
						handleDomain(meaningId, separateDomainCode, "TODO domainOrigin");
					}
				}
			}
		}
	}

	private void handleDomain(Long meaningId, String domainCode, String domainOrigin) throws Exception {
		// TODO domainOrigin on hetkel puudu
		boolean domainExists = domainExists(domainCode, domainOrigin);
		if (domainExists) {
			createMeaningDomain(meaningId, domainCode, domainOrigin);
			// domainCodes.add(domainCode); - kas kasutada seda ka?
		} else {
			logger.warn("Incorrect domain reference \"{}\"", domainCode);
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
		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNodes = conceptGroupNode.selectNodes(ltbSourceExp);
		for (Node ltbSourceValueNode : valueNodes) {
			// TODO päritolu node sees on erinevad väärtused, tuleb vaadata, millist vaja
			// TODO arvatavasti saab kasutada sarnast lahendust nagu allikaviite korral

			// TODO allikas kõigi selle tähenduse ilmikute juurde
			// erinevalt estermist ei ole see siin keelega seotud
			// extract refs

			// createMeaningFreeform(meaningId, FreeformType.LTB_SOURCE, "TODO");
		}

		valueNodes = conceptGroupNode.selectNodes(noteExp);
		for (Node publicNoteValueNode : valueNodes) {
			valueStr = ((Element) publicNoteValueNode).getTextTrim();
			Long freeformId = createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
			if (((Element) publicNoteValueNode).hasMixedContent()) {
				valueStr = handleFreeformRefLinks(publicNoteValueNode, freeformId); // TODO handleFreeformRefLinks on aegunud
				updateFreeformText(freeformId, valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(privateNoteExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			Long freeformId = createMeaningFreeform(meaningId, FreeformType.PRIVATE_NOTE, valueStr);
			if (valueNode.hasMixedContent()) {
				valueStr = handleFreeformRefLinks(valueNode, freeformId);
				updateFreeformText(freeformId, valueStr);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(listExp);
		for (Node listValueNode : valueNodes) {
			// TODO väärtused võivad olla püstkriipsuga | eraldatud, kas seda on vaja kuidagi töödelda?
			valueStr = ((Element) listValueNode).getTextTrim();
			// TODO meaningule uus freeformitüüp "LIST"
			createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
		}

		valueNodes = conceptGroupNode.selectNodes(imageExp);
		for (Node imageValueNode : valueNodes) {
			valueStr = ((Element) imageValueNode).getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.IMAGE_FILE, valueStr);
		}
	}

	private void createMeaningRelations(Node rootContentNode, Long meaningId) {
		// TODO tekita seos, kui näidatud keelend on ühene. kui on mitmene või puudub, siis logi
		// kui Tlink sisaldab keelt, siis kasutada seda. Kui ei sisalda, siis proovida ilma selleta

		// pooleli...
		List<String> words = new ArrayList<>();
		Iterator<Node> contentNodeIter = ((Element) rootContentNode).nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				logger.debug(valueStr);
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
			}
		}
	}

	private void saveLexemeSourceLinks(Long lexemeId, List<Content> sources, String term) throws Exception {

		for (Content sourceObj : sources) {
			List<Ref> refs = sourceObj.getRefs();
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.LEXEME, lexemeId, ref, term);
			}
		}
	}

	private void saveDefinitionsAndSourceLinks(Long meaningId, List<Content> definitions, String term) throws Exception {

		for (Content definitionObj : definitions) {
			String definition = definitionObj.getValue();
			String lang = definitionObj.getLang();
			List<Ref> refs = definitionObj.getRefs();
			Long definitionId = createOrSelectDefinition(meaningId, definition, lang, getDataset());
			definitionObj.setId(definitionId);
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.DEFINITION, definitionId, ref, term);
			}
		}
	}

	private void saveUsagesAndSourceLinks(Long lexemeId, List<Content> usages, String term) throws Exception {

		for (Content usageObj : usages) {
			String usage = usageObj.getValue();
			String lang = usageObj.getLang();
			List<Ref> refs = usageObj.getRefs();
			Long usageId = createLexemeFreeform(lexemeId, FreeformType.USAGE, usage, lang);
			usageObj.setId(usageId);
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.USAGE, usageId, ref, term);
			}
		}
	}

	// TODO refactor? - EstermLoaderRunner has similar method (but has concept value)
	private void createSourceLink(SourceOwner sourceOwner, Long ownerId, Ref ref, String term) throws Exception {

		String minorRef = ref.getMinorRef();
		String majorRef = ref.getMajorRef();
		ReferenceType refType = ref.getType();
		Long sourceId = getSource(majorRef);
		if (sourceId == null) {
			appendToReport(doReports, REPORT_MISSING_SOURCE_REFS, term, majorRef);
			return;
		}
		if (StringUtils.equalsIgnoreCase(refTypeExpert, majorRef)) {
			majorRef = minorRef;
			minorRef = null;
		}
		if (StringUtils.equalsIgnoreCase(refTypeQuery, majorRef)) {
			majorRef = minorRef;
			minorRef = null;
		}
		if (StringUtils.isBlank(majorRef)) {
			majorRef = "?";
		}
		if (SourceOwner.LEXEME.equals(sourceOwner)) {
			createLexemeSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		} else if (SourceOwner.DEFINITION.equals(sourceOwner)) {
			createDefinitionSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		} else if (SourceOwner.USAGE.equals(sourceOwner)) {
			createFreeformSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		}
	}
}
