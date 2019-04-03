package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.RelationPart;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class MilitermLoaderRunner extends AbstractTermLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private DateFormat defaultDateFormat;

	private Map<Long, List<RelationPart>> meaningRelationPartsMap;

	private Count illegalMeaningRelationReferenceValueCount;

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
			reportComposer = new ReportComposer(getDataset() + " loader", REPORT_ILLEGAL_SOURCE_REF, REPORT_MISSING_SOURCE_REFS,
					REPORT_ILLEGAL_MEANING_RELATION_REF);
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

		meaningRelationPartsMap = new HashMap<>();
		illegalSourceReferenceValueCount = new Count();
		illegalMeaningRelationReferenceValueCount = new Count();
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

		for (Map.Entry<Long, List<RelationPart>> meaningRelationPart : meaningRelationPartsMap.entrySet()) {

			List<RelationPart> initiatorRelationParts = meaningRelationPart.getValue();
			findSecondRelationPartAndCreateRelations(initiatorRelationParts);
		}

		logger.debug("Found {} illegal source reference values", illegalSourceReferenceValueCount.getValue());
		logger.debug("Found {} illegal meaning relation reference values", illegalMeaningRelationReferenceValueCount.getValue());

		end();
	}

	private void processConceptGroup(Node conceptGroupNode) throws Exception {

		Meaning meaning = new Meaning();
		String term;
		String lang;
		List<String> listValues = extractListValues(conceptGroupNode);
		List<Node> ltbSourceValueNodes = conceptGroupNode.selectNodes(ltbSourceExp);

		extractAndApplyMeaningProperties(conceptGroupNode, meaning, defaultDateFormat);
		Long meaningId = createMeaning(meaning);

		createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId,
				String.valueOf(meaningId), meaning.getCreatedOn(), meaning.getCreatedBy());
		createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId,
				String.valueOf(meaningId), meaning.getModifiedOn(), meaning.getModifiedBy());

		extractAndApplyMeaningFreeforms(meaningId, conceptGroupNode);
		extractListValues(conceptGroupNode);

		List<Node> langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);
		for (Node langGroupNode : langGroupNodes) {

			Element languageNode = (Element) langGroupNode.selectSingleNode(langExp);
			String langTypeValue = languageNode.attributeValue(langTypeAttr);
			boolean isLang = isLang(langTypeValue); // other possible values are "Valdkond" and "Allikas"
			if (!isLang) {
				continue;
			}
			lang = unifyLang(langTypeValue);

			List<Node> termGroupNodes = langGroupNode.selectNodes(termGroupExp);

			for (Node termGroupNode : termGroupNodes) {

				Element termValueNode = (Element) termGroupNode.selectSingleNode(termExp);
				term = termValueNode.getTextTrim();

				int homonymNr = getWordMaxHomonymNr(term, lang);
				homonymNr++;
				Word word = new Word(term, lang, null, null, null, null, homonymNr, DEFAULT_WORD_MORPH_CODE, null, null);
				Long wordId = createOrSelectWord(word, null, null, null);

				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(wordId);
				lexeme.setMeaningId(meaningId);
				Long lexemeId = createLexeme(lexeme, getDataset());

				saveListValueFreeforms(lang, listValues, lexemeId);

				for (Node ltbSourceValueNode : ltbSourceValueNodes) {
					List<Content> sources = extractContentAndRefs(ltbSourceValueNode, lang, term, false);
					saveLexemeSourceLinks(lexemeId, sources, term);
				}

				extractAndApplyLexemeProperties(termGroupNode, lexeme);

				createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId,
						term, lexeme.getCreatedOn(), lexeme.getCreatedBy());
				createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId,
						term, lexeme.getModifiedOn(), lexeme.getModifiedBy());

				List<Node> sourceValueNodes = termGroupNode.selectNodes(sourceExp);
				for (Node sourceValueNode : sourceValueNodes) {
					List<Content> sources = extractContentAndRefs(sourceValueNode, lang, term, false);
					saveLexemeSourceLinks(lexemeId, sources, term);
				}

				List<Node> definitionValueNodes = termGroupNode.selectNodes(definitionExp);
				for (Node definitionValueNode : definitionValueNodes) {
					List<Content> sources = extractContentAndRefs(definitionValueNode, lang, term, true);
					saveDefinitionsAndSourceLinks(meaningId, sources, term, definitionTypeCodeDefinition);
				}

				Element coincidentValueNode = (Element) termGroupNode.selectSingleNode(overlapExp);
				if (coincidentValueNode != null) {
					String coincidentValue = coincidentValueNode.getTextTrim();
					createLexemeFreeform(lexemeId, FreeformType.PRIVATE_NOTE, coincidentValue, lang);
				}

				List<Node> regionValueNodes = termGroupNode.selectNodes(regionExp);
				for (Node regionValueNode : regionValueNodes) {
					String regionValue = ((Element) regionValueNode).getTextTrim();
					// TODO word.region - uus klassifikaator ilmiku juurde
				}

				List<Node> usageValueNodes = termGroupNode.selectNodes(usageExp);
				if (CollectionUtils.isNotEmpty(usageValueNodes)) {
					for (Node usageValueNode : usageValueNodes) {
						List<Content> sources = extractContentAndRefs(usageValueNode, lang, term, true);
						saveUsagesAndSourceLinks(lexemeId, sources, term);
					}
				}

				List<Node> noteValueNodes = termGroupNode.selectNodes(noteExp);
				if (CollectionUtils.isNotEmpty(noteValueNodes)) {
					for (Node noteValueNode : noteValueNodes) {
						List<Content> sources = extractContentAndRefs(noteValueNode, lang, term, true);
						savePublicNotesAndSourceLinks(lexemeId, sources, term);
					}
				}

				List<Node> meaningRelationValueNodes = termGroupNode.selectNodes(meaningRelationExp);
				for (Node meaningRelationValueNode : meaningRelationValueNodes) {
					addPartialRelationToMap(meaningRelationValueNode, meaningId);
				}

				List<Node> explanationValueNodes = termGroupNode.selectNodes(explanationExp);
				for (Node explanationValueNode : explanationValueNodes) {
					List<Content> sources = extractContentAndRefs(explanationValueNode, lang, term, true);
					saveDefinitionsAndSourceLinks(meaningId, sources, term, definitionTypeCodeExplanation);
				}
			}
		}

		Element meaningDomainValueNode = (Element) conceptGroupNode.selectSingleNode(meaningDomainExp);
		saveDomains(meaningId, meaningDomainValueNode);
	}

	private List<String> extractListValues(Node conceptGroupNode) {

		List<String> listValues = new ArrayList<>();
		List<Node> listValueNodes = conceptGroupNode.selectNodes(listExp);
		for (Node listValueNode : listValueNodes) {
			String listValue = ((Element) listValueNode).getTextTrim();
			listValues.add(listValue);
		}
		return listValues;
	}

	private void saveListValueFreeforms(String lang, List<String> listValues, Long lexemeId) throws Exception {

		for (String listValue : listValues) {
			int domainDelimCount = StringUtils.countMatches(listValue, listingsDelimiter);
			if (domainDelimCount == 0) {
				createLexemeFreeform(lexemeId, FreeformType.BOOKMARK, listValue, lang);
			} else {
				String[] separateDomainCodes = StringUtils.split(listValue, listingsDelimiter);
				for (String separateDomainCode : separateDomainCodes) {
					createLexemeFreeform(lexemeId, FreeformType.BOOKMARK, separateDomainCode, lang);
				}
			}
		}
	}

	private void saveDomains(Long meaningId, Element meaningDomainValueNode) throws Exception {

		if (meaningDomainValueNode != null) {
			Iterator<Node> iter = meaningDomainValueNode.nodeIterator();
			List<String> domainCodes = new ArrayList<>();
			DefaultText textContentNode;
			DefaultElement elemContentNode;
			String valueStr;

			while (iter.hasNext()) {
				Node contentNode = iter.next();
				if (contentNode instanceof DefaultText) {
					textContentNode = (DefaultText) contentNode;
					valueStr = textContentNode.getText();
					boolean separated = StringUtils.countMatches(valueStr, meaningDelimiter) > 0;
					if (separated) {
						String[] separateDomainCodes = valueStr.split(meaningDelimiter + "\\s*");
						for (String separateDomainCode : separateDomainCodes) {
							handleDomain(meaningId, separateDomainCode, domainCodes);
						}
					} else {
						handleDomain(meaningId, valueStr, domainCodes);
					}
				} else if (contentNode instanceof DefaultElement) {
					elemContentNode = (DefaultElement) contentNode;
					valueStr = elemContentNode.getTextTrim();
					handleDomain(meaningId, valueStr, domainCodes);
				}
			}
		}
	}

	private void handleDomain(Long meaningId, String domainCode, List<String> domainCodes) throws Exception {

		if (domainCodes.contains(domainCode)) {
			logger.warn("Duplicate domain code - {}, meaningId - {}", domainCode, meaningId);
			return;
		}
		boolean domainExists = domainExists(domainCode, originMiliterm);
		if (domainExists) {
			createMeaningDomain(meaningId, domainCode, originMiliterm);
			domainCodes.add(domainCode);
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

		valueNodes = conceptGroupNode.selectNodes(imageExp);
		for (Node imageValueNode : valueNodes) {
			valueStr = ((Element) imageValueNode).getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.IMAGE_FILE, valueStr);
		}
	}

	private void addPartialRelationToMap(Node rootContentNode, Long meaningId) {

		Iterator<Node> contentNodeIter = ((Element) rootContentNode).nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		Map<String, String> termsAndLangMap = new HashMap<>();
		String valueStr;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				boolean separated = StringUtils.countMatches(valueStr, meaningDelimiter) > 0;
				if (separated) {
					String[] separateTerms = valueStr.split(meaningDelimiter + "\\s*");
					for (String term : separateTerms) {
						termsAndLangMap.putIfAbsent(term, null);
					}
				} else {
					termsAndLangMap.putIfAbsent(valueStr, null);
				}
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				termsAndLangMap.putIfAbsent(valueStr, null);
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					boolean separated = StringUtils.countMatches(tlinkAttrValue, tlinkDelimiter) > 0;
					if (separated) {
						String[] separatedTlinkValues = StringUtils.split(tlinkAttrValue, tlinkDelimiter);
						String lang = separatedTlinkValues[0];
						if (isLang(lang)) {
							lang = unifyLang(lang);
						} else {
							lang = null;
						}
						String term = separatedTlinkValues[1];
						termsAndLangMap.put(term, lang);
					} else {
						termsAndLangMap.putIfAbsent(tlinkAttrValue, null);
					}
				}
			}
		}

		List<RelationPart> meaningRelationParts = meaningRelationPartsMap.get(meaningId);
		if (meaningRelationParts == null) {
			meaningRelationParts = new ArrayList<>();
		}
		for (Map.Entry<String, String> termAndLang : termsAndLangMap.entrySet()) {
			String term = termAndLang.getKey();
			String lang = termAndLang.getValue();

			boolean relationPartExists = false;
			for (RelationPart meaningRelationPart : meaningRelationParts) {
				if (term.equals(meaningRelationPart.getRelatedTerm())) {
					if (lang != null && meaningRelationPart.getLang() == null) {
						meaningRelationPart.setLang(lang);
					}
					relationPartExists = true;
					break;
				}
			}
			if (!relationPartExists) {
				RelationPart relationPart = new RelationPart();
				relationPart.setMeaningId(meaningId);
				relationPart.setRelatedTerm(term);
				relationPart.setLang(lang);
				meaningRelationParts.add(relationPart);
			}
		}
		meaningRelationPartsMap.put(meaningId, meaningRelationParts);
	}

	private void findSecondRelationPartAndCreateRelations(List<RelationPart> initiatorRelationParts) throws Exception {

		for (RelationPart initiatorRelationPart : initiatorRelationParts) {
			String possibleRelatedTerm = initiatorRelationPart.getRelatedTerm();
			Long initialMeaningId = initiatorRelationPart.getMeaningId();
			List<RelationPart> possibleRelationParts = getMeaningRelationParts(possibleRelatedTerm);

			if (possibleRelationParts.isEmpty()) {
				illegalMeaningRelationReferenceValueCount.increment();
				appendToReport(doReports, REPORT_ILLEGAL_MEANING_RELATION_REF, String.valueOf(initialMeaningId), "Viide tundmatule terminile:",
						possibleRelatedTerm);
			} else if (possibleRelationParts.size() == 1) {
				Long secondMeaningId = possibleRelationParts.get(0).getMeaningId();
				createMeaningRelation(initialMeaningId, secondMeaningId, "ant"); // TODO relationType
			} else {
				String initiatorLang = initiatorRelationPart.getLang();
				if (initiatorLang != null) {
					List<RelationPart> sameLangRelationParts = new ArrayList<>();
					for (RelationPart possibleRelationPart : possibleRelationParts) {
						if (initiatorLang.equals(possibleRelationPart.getLang())) {
							sameLangRelationParts.add(possibleRelationPart);
						}
					}
					if (sameLangRelationParts.size() == 1) {
						Long secondMeaningId = sameLangRelationParts.get(0).getMeaningId();
						createMeaningRelation(initialMeaningId, secondMeaningId, "ant"); // TODO relationType
					} else {
						illegalMeaningRelationReferenceValueCount.increment();
						appendToReport(doReports, REPORT_ILLEGAL_MEANING_RELATION_REF, String.valueOf(initialMeaningId), "Viidatud termin kordub:",
								possibleRelatedTerm);
					}
				} else {
					illegalMeaningRelationReferenceValueCount.increment();
					appendToReport(doReports, REPORT_ILLEGAL_MEANING_RELATION_REF, String.valueOf(initialMeaningId), "Viidatud termin kordub:",
							possibleRelatedTerm);
				}
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

	private void saveDefinitionsAndSourceLinks(Long meaningId, List<Content> definitions, String term, String definitionTypeCode) throws Exception {

		for (Content definitionObj : definitions) {
			String definition = definitionObj.getValue();
			String lang = definitionObj.getLang();
			List<Ref> refs = definitionObj.getRefs();
			Long definitionId = createOrSelectDefinition(meaningId, definition, definitionTypeCode, lang, getDataset());
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

	private void savePublicNotesAndSourceLinks(Long lexemeId, List<Content> publicNotes, String term) throws Exception {

		for (Content publicNoteObj : publicNotes) {
			String publicNote = publicNoteObj.getValue();
			String lang = publicNoteObj.getLang();
			List<Ref> refs = publicNoteObj.getRefs();
			Long publicNoteId = createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, publicNote, lang);
			publicNoteObj.setId(publicNoteId);
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.PUBLIC_NOTE, publicNoteId, ref, term);
			}
		}
	}

	private void createSourceLink(SourceOwner sourceOwner, Long ownerId, Ref ref, String term) throws Exception {

		String minorRef = ref.getMinorRef();
		String majorRef = ref.getMajorRef();
		ReferenceType refType = ref.getType();
		Long sourceId = getSource(majorRef);
		if (sourceId == null) {
			appendToReport(doReports, REPORT_MISSING_SOURCE_REFS, term, majorRef);
			return;
		}
		if (StringUtils.isBlank(majorRef)) {
			majorRef = "?";
		}
		if (SourceOwner.LEXEME.equals(sourceOwner)) {
			createLexemeSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		} else if (SourceOwner.DEFINITION.equals(sourceOwner)) {
			createDefinitionSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		} else if (SourceOwner.USAGE.equals(sourceOwner) || SourceOwner.PUBLIC_NOTE.equals(sourceOwner)) {
			createFreeformSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		}
	}
}
