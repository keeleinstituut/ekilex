package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
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
import eki.ekilex.data.transform.RelationPart;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class MilitermLoaderRunner extends AbstractTermLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private final static String MEANING_RELATION_UNSPECIFIED = "määramata";

	private DateFormat defaultDateFormat;

	private Map<Long, List<RelationPart>> meaningRelationPartsMap;

	private Count illegalMeaningRelationReferenceValueCount;

	private List<String> processLogSourceRefNames = Arrays.asList("Cancelled", "NATO Agreed");

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
	public void initialise() {
		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
	}

	@Transactional
	public void execute(String milFilePath1, String milFilePath2, boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", REPORT_MISSING_SOURCE_REFS, REPORT_ILLEGAL_MEANING_RELATION_REF);
		}
		start();

		String[] dataXmlFilePaths = new String[] {milFilePath1, milFilePath2};
		Document dataDoc;
		Element rootElement;
		List<Node> conceptGroupNodes;
		meaningRelationPartsMap = new HashMap<>();
		illegalMeaningRelationReferenceValueCount = new Count();

		int fileCounter = 1;
		for (String dataXmlFilePath : dataXmlFilePaths) {

			int totalFiles = dataXmlFilePaths.length;
			String fileName = FilenameUtils.getName(dataXmlFilePath);
			logger.debug("Loading {} file of {} files. File name: \"{}\"", fileCounter, totalFiles, fileName);

			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			rootElement = dataDoc.getRootElement();
			conceptGroupNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
			int conceptGroupCount = conceptGroupNodes.size();
			logger.debug("{} concept groups found", conceptGroupCount);

			int conceptGroupCounter = 0;
			int lastProgressPercent = 0;
			for (Node conceptGroupNode : conceptGroupNodes) {

				boolean isLanguageTypeConcept = isLanguageTypeConcept(conceptGroupNode);
				if (isLanguageTypeConcept) {
					processConceptGroup(conceptGroupNode, fileName);
				}

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

		for (Map.Entry<Long, List<RelationPart>> meaningRelationPart : meaningRelationPartsMap.entrySet()) {

			List<RelationPart> initiatorRelationParts = meaningRelationPart.getValue();
			findSecondRelationPartAndCreateRelations(initiatorRelationParts);
		}

		logger.debug("Found {} illegal meaning relation reference values", illegalMeaningRelationReferenceValueCount.getValue());

		end();
	}

	private void processConceptGroup(Node conceptGroupNode, String fileName) throws Exception {

		String term;
		String lang;
		List<String> listValues = extractListValues(conceptGroupNode);
		List<Node> ltbSourceValueNodes = conceptGroupNode.selectNodes(ltbSourceExp);

		Long meaningId = createMeaning();
		extractAndSaveMeaningFreeforms(meaningId, conceptGroupNode, fileName);
		createMeaningLifecycleLog(meaningId, conceptGroupNode);

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
				Long wordId = createOrSelectWord(word, null, null);

				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(wordId);
				lexeme.setMeaningId(meaningId);
				Long lexemeId = createLexemeIfNotExists(lexeme);
				createLexemeLifecycleLog(lexemeId, termGroupNode, term);

				saveListValueFreeforms(lang, listValues, lexemeId);

				for (Node ltbSourceValueNode : ltbSourceValueNodes) {
					List<Content> sources = extractContentAndRefs(ltbSourceValueNode, lang, term, false);
					saveLexemeSourceLinks(lexemeId, sources, term, fileName);
				}

				List<Node> sourceValueNodes = termGroupNode.selectNodes(sourceExp);
				for (Node sourceValueNode : sourceValueNodes) {
					List<Content> sources = extractContentAndRefs(sourceValueNode, lang, term, false);
					saveLexemeSourceLinks(lexemeId, sources, term, fileName);
				}

				List<Node> definitionValueNodes = termGroupNode.selectNodes(definitionExp);
				for (Node definitionValueNode : definitionValueNodes) {
					List<Content> sources = extractContentAndRefs(definitionValueNode, lang, term, false);
					saveDefinitionsAndSourceLinks(meaningId, sources, term, definitionTypeCodeDefinition, fileName);
				}

				Element coincidentValueNode = (Element) termGroupNode.selectSingleNode(overlapExp);
				if (coincidentValueNode != null) {
					String coincidentValue = coincidentValueNode.getTextTrim();
					createLexemeProcessLog(lexemeId, coincidentValue);
				}

				List<Node> regionValueNodes = termGroupNode.selectNodes(regionExp);
				for (Node regionValueNode : regionValueNodes) {
					Iterator<Node> regionNodeIter = ((Element) regionValueNode).nodeIterator();
					while (regionNodeIter.hasNext()) {
						Node regionNode = regionNodeIter.next();
						if (regionNode instanceof DefaultText) {
							DefaultText textNode = (DefaultText) regionNode;
							String regionValue = textNode.getText();
							boolean valuesSeparated = StringUtils.countMatches(regionValue, listingsDelimiter) > 0;
							if (valuesSeparated) {
								String[] separateRegionValues = StringUtils.split(regionValue, listingsDelimiter);
								for (String region : separateRegionValues) {
									createLexemeRegion(lexemeId, region);
								}
							} else {
								createLexemeRegion(lexemeId, regionValue);
							}
						}
					}
				}

				List<Node> usageValueNodes = termGroupNode.selectNodes(usageExp);
				if (CollectionUtils.isNotEmpty(usageValueNodes)) {
					for (Node usageValueNode : usageValueNodes) {
						List<Content> sources = extractContentAndRefs(usageValueNode, lang, term, true);
						saveUsagesAndSourceLinks(lexemeId, sources, term, fileName);
					}
				}

				List<Node> noteValueNodes = termGroupNode.selectNodes(noteExp);
				if (CollectionUtils.isNotEmpty(noteValueNodes)) {
					for (Node noteValueNode : noteValueNodes) {
						List<Content> sources = extractContentAndRefs(noteValueNode, lang, term, false);
						savePublicNotesAndSourceLinks(lexemeId, sources, term, fileName);
					}
				}

				List<Node> meaningRelationValueNodes = termGroupNode.selectNodes(meaningRelationExp);
				for (Node meaningRelationValueNode : meaningRelationValueNodes) {
					addPartialRelationToMap(meaningRelationValueNode, meaningId);
				}

				List<Node> explanationValueNodes = termGroupNode.selectNodes(explanationExp);
				for (Node explanationValueNode : explanationValueNodes) {
					List<Content> sources = extractContentAndRefs(explanationValueNode, lang, term, true);
					saveDefinitionsAndSourceLinks(meaningId, sources, term, definitionTypeCodeExplanation, fileName);
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
			boolean valuesSeparated = StringUtils.countMatches(listValue, listingsDelimiter) > 0;
			if (valuesSeparated) {
				String[] separateDomainCodes = StringUtils.split(listValue, listingsDelimiter);
				for (String separateDomainCode : separateDomainCodes) {
					createLexemeFreeform(lexemeId, FreeformType.BOOKMARK, separateDomainCode, lang);
				}
			} else {
				createLexemeFreeform(lexemeId, FreeformType.BOOKMARK, listValue, lang);
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
					boolean valuesSeparated = StringUtils.countMatches(valueStr, meaningDelimiter) > 0;
					if (valuesSeparated) {
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
			createMeaningDomain(meaningId, originMiliterm, domainCode);
			domainCodes.add(domainCode);
		} else {
			logger.warn("Incorrect domain reference \"{}\"", domainCode);
		}
	}

	private void extractAndSaveMeaningFreeforms(Long meaningId, Node conceptGroupNode, String fileName) throws Exception {

		List<Node> valueNodes;
		Element valueNode;
		String valueStr;

		valueNodes = conceptGroupNode.selectNodes(noteExp);
		for (Node publicNoteValueNode : valueNodes) {
			valueStr = ((Element) publicNoteValueNode).getTextTrim();
			Long freeformId = createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
			if (((Element) publicNoteValueNode).hasMixedContent()) {
				valueStr = handleFreeformTextSourceLinks(publicNoteValueNode, freeformId, fileName);
				updateFreeformText(freeformId, valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(privateNoteExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			Long processLogId = createMeaningProcessLog(meaningId, valueStr);
			if (valueNode.hasMixedContent()) {
				valueStr = handleProcessLogTextSourceLinks(valueNode, processLogId, fileName);
				updateProcessLogText(processLogId, valueStr);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(imageExp);
		for (Node imageValueNode : valueNodes) {
			valueStr = ((Element) imageValueNode).getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.IMAGE_FILE, valueStr);
		}
	}

	private void createMeaningLifecycleLog(Long meaningId, Node conceptGroupNode) throws Exception {

		Element valueNode1;
		Element valueNode2;
		String valueStr1;
		String valueStr2;
		long valueLong;
		Timestamp valueTs;

		valueNode1 = (Element) conceptGroupNode.selectSingleNode(createdByExp);
		valueNode2 = (Element) conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, meaningId, LifecycleEntity.MEANING, LifecycleProperty.VALUE, LifecycleEventType.CREATE,
					valueStr1, valueTs, null);
		}

		valueNode1 = (Element) conceptGroupNode.selectSingleNode(modifiedByExp);
		valueNode2 = (Element) conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, meaningId, LifecycleEntity.MEANING, LifecycleProperty.VALUE, LifecycleEventType.UPDATE,
					valueStr1, valueTs, null);
		}
	}

	private void createLexemeLifecycleLog(Long lexemeId, Node termGroupNode, String term) throws Exception {

		Element valueNode1;
		Element valueNode2;
		String valueStr1;
		String valueStr2;
		long valueLong;
		Timestamp valueTs;

		valueNode1 = (Element) termGroupNode.selectSingleNode(createdByExp);
		valueNode2 = (Element) termGroupNode.selectSingleNode(createdOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, LifecycleEventType.CREATE, valueStr1,
					valueTs, term);
		}

		valueNode1 = (Element) termGroupNode.selectSingleNode(modifiedByExp);
		valueNode2 = (Element) termGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, LifecycleEventType.UPDATE, valueStr1,
					valueTs, term);
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
				boolean valuesSeparated = StringUtils.countMatches(valueStr, meaningDelimiter) > 0;
				if (valuesSeparated) {
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
					boolean valuesSeparated = StringUtils.countMatches(tlinkAttrValue, tlinkDelimiter) > 0;
					if (valuesSeparated) {
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
				createMeaningRelation(initialMeaningId, secondMeaningId, MEANING_RELATION_UNSPECIFIED);
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
						createMeaningRelation(initialMeaningId, secondMeaningId, MEANING_RELATION_UNSPECIFIED);
					} else {
						illegalMeaningRelationReferenceValueCount.increment();
						appendToReport(doReports, REPORT_ILLEGAL_MEANING_RELATION_REF, String.valueOf(initialMeaningId), "Viidatud termin kordub:",
								possibleRelatedTerm);
					}
				} else {
					illegalMeaningRelationReferenceValueCount.increment();
					appendToReport(doReports, REPORT_ILLEGAL_MEANING_RELATION_REF, String.valueOf(initialMeaningId),
							"Viidatud termin kordub, keel ei ole määratud:", possibleRelatedTerm);
				}
			}
		}
	}

	private void saveLexemeSourceLinks(Long lexemeId, List<Content> sources, String term, String fileName) throws Exception {

		for (Content sourceObj : sources) {
			List<Ref> refs = sourceObj.getRefs();
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.LEXEME, lexemeId, ref, term, fileName);
			}
		}
	}

	private void saveDefinitionsAndSourceLinks(Long meaningId, List<Content> definitions, String term, String definitionTypeCode, String fileName) throws Exception {

		for (Content definitionObj : definitions) {
			String definition = definitionObj.getValue();
			String lang = definitionObj.getLang();
			List<Ref> refs = definitionObj.getRefs();
			Long definitionId = null;
			if (EMPTY_CONTENT.equals(definition)) {
				boolean definitonHasAtLeastOneValidRef = containsValidRef(refs);
				if (definitonHasAtLeastOneValidRef) {
					definitionId = createOrSelectDefinition(meaningId, definition, definitionTypeCode, lang);
					definitionObj.setId(definitionId);
				}
			} else {
				definitionId = createOrSelectDefinition(meaningId, definition, definitionTypeCode, lang);
				definitionObj.setId(definitionId);
			}
			for (Ref ref : refs) {
				String majorRef = ref.getMajorRef();
				if (processLogSourceRefNames.contains(majorRef)) {
					createMeaningProcessLog(meaningId, majorRef);
				} else {
					createSourceLink(SourceOwner.DEFINITION, definitionId, ref, term, fileName);
				}
			}
		}
	}

	private void saveUsagesAndSourceLinks(Long lexemeId, List<Content> usages, String term, String fileName) throws Exception {

		for (Content usageObj : usages) {
			String usage = usageObj.getValue();
			String lang = usageObj.getLang();
			List<Ref> refs = usageObj.getRefs();
			Long usageId = createLexemeFreeform(lexemeId, FreeformType.USAGE, usage, lang);
			usageObj.setId(usageId);
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.USAGE, usageId, ref, term, fileName);
			}
		}
	}

	private void savePublicNotesAndSourceLinks(Long lexemeId, List<Content> publicNotes, String term, String fileName) throws Exception {

		for (Content publicNoteObj : publicNotes) {
			String publicNote = publicNoteObj.getValue();
			String lang = publicNoteObj.getLang();
			List<Ref> refs = publicNoteObj.getRefs();
			Long publicNoteId = null;
			if (EMPTY_CONTENT.equals(publicNote)) {
				boolean publicNoteHasAtLeastOneValidRef = containsValidRef(refs);
				if (publicNoteHasAtLeastOneValidRef) {
					publicNoteId = createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, publicNote, lang);
					publicNoteObj.setId(publicNoteId);
				}
			}  else {
				publicNoteId = createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, publicNote, lang);
				publicNoteObj.setId(publicNoteId);
			}
			for (Ref ref : refs) {
				String majorRef = ref.getMajorRef();
				if (processLogSourceRefNames.contains(majorRef)) {
					createLexemeProcessLog(lexemeId, majorRef);
				} else {
					createSourceLink(SourceOwner.PUBLIC_NOTE, publicNoteId, ref, term, fileName);
				}
			}
		}
	}

	private boolean containsValidRef(List<Ref> refs) {

		for (Ref ref : refs) {
			String majorRef = ref.getMajorRef();
			if (!processLogSourceRefNames.contains(majorRef)) {
				return true;
			}
		}
		return false;
	}

	private void createSourceLink(SourceOwner sourceOwner, Long ownerId, Ref ref, String term, String fileName) throws Exception {

		String minorRef = ref.getMinorRef();
		String majorRef = ref.getMajorRef();
		ReferenceType refType = ref.getType();
		Long sourceId = getSource(majorRef, fileName);
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
