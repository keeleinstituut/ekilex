package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Word;
import eki.ekilex.runner.util.EstermReportHelper;
import eki.ekilex.service.ReportComposer;

@Component
public class EstermLoaderRunner extends AbstractTermLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String LTB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String REVIEW_TIMESTAMP_PATTERN = "yy/MM/dd";

	@Autowired
	private EstermReportHelper reportHelper;

	private DateFormat defaultDateFormat;

	private DateFormat ltbDateFormat;

	private DateFormat reviewDateFormat;

	private Map<String, String> meaningAndLexemeProcessStateCodes;

	private Map<String, String> lexemeValueStateCodes;

	private Map<String, String> wordTypeCodes;

	@Override
	public String getDataset() {
		return "est";
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	void initialise() throws Exception {

		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
		ltbDateFormat = new SimpleDateFormat(LTB_TIMESTAMP_PATTERN);
		reviewDateFormat = new SimpleDateFormat(REVIEW_TIMESTAMP_PATTERN);

		Map<String, String> tempCodes;

		// meaning/lexeme process state
		meaningAndLexemeProcessStateCodes = new HashMap<>();
		tempCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_ENTRY_CLASS, ClassifierName.PROCESS_STATE.name());
		meaningAndLexemeProcessStateCodes.putAll(tempCodes);
		tempCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_STAATUS, ClassifierName.PROCESS_STATE.name());
		meaningAndLexemeProcessStateCodes.putAll(tempCodes);

		// word type
		wordTypeCodes = new HashMap<>();
		tempCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_MÕISTETÜÜP, ClassifierName.WORD_TYPE.name());
		wordTypeCodes.putAll(tempCodes);
		tempCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_KEELENDITÜÜP, ClassifierName.WORD_TYPE.name());
		wordTypeCodes.putAll(tempCodes);

		// lexeme value state
		lexemeValueStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_KEELENDITÜÜP, ClassifierName.VALUE_STATE.name());

		//EKI_CLASSIFIER_MÕISTETÜÜP -> word_type
		//EKI_CLASSIFIER_ENTRY_CLASS -> meaning process_state or lexeme process_state of all lexemes of the meaning
		//EKI_CLASSIFIER_STAATUS -> meaning process_state or lexeme process_state of all lexemes of the meaning
		//EKI_CLASSIFIER_KEELENDITÜÜP -> lexeme value_state, word_type
		// count staatus & entry class conflicts
		// entry class overrides
		// mõistetüüp & keelenditüüp conflicts?
	}

	@Transactional
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Starting loading Esterm...");

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader",
					REPORT_DEFINITIONS_NOTES_MESS, REPORT_CREATED_MODIFIED_MESS,
					REPORT_ILLEGAL_CLASSIFIERS, REPORT_DEFINITIONS_AT_TERMS, REPORT_MISSING_SOURCE_REFS,
					REPORT_MULTIPLE_DEFINITIONS, REPORT_NOT_A_DEFINITION, REPORT_DEFINITIONS_NOTES_MISMATCH,
					REPORT_MISSING_VALUE, REPORT_ILLEGAL_SOURCE);
			reportHelper.setup(reportComposer, meaningAndLexemeProcessStateCodes, lexemeValueStateCodes);
		}
		start();

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		List<Node> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		Element valueNode;
		List<Node> valueNodes, langGroupNodes, termGroupNodes, domainNodes;
		Long wordId, meaningId, lexemeId;
		List<Content> definitions, usages, sources;
		List<String> termWordTypeCodes;
		String valueStr, concept, term, processStateCode, conceptWordTypeCode, termWordTypeCode;
		String lang;
		int homonymNr;
		Word wordObj;
		Meaning meaningObj;
		Lexeme lexemeObj;

		illegalSourceValueCount = new Count();
		Count dataErrorCount = new Count();
		Count definitionsWithSameNotesCount = new Count();
		Count processStateConflictCount = new Count();
		Count wordTypeConflictCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Node conceptGroupNode : conceptGroupNodes) {

			boolean isLanguageTypeConcept = isLanguageTypeConcept(conceptGroupNode);
			if (!isLanguageTypeConcept) {
				continue;
			}

			valueNode = (Element) conceptGroupNode.selectSingleNode(conceptExp);
			concept = valueNode.getTextTrim();

			// meaning
			meaningObj = new Meaning();
			extractAndApplyMeaningProperties(conceptGroupNode, meaningObj, defaultDateFormat);
			meaningId = createMeaning(meaningObj);
			extractAndSaveMeaningFreeforms(meaningId, conceptGroupNode);

			// domains
			domainNodes = conceptGroupNode.selectNodes(domainExp);
			saveDomains(concept, domainNodes, meaningId, originLenoch);
			domainNodes = conceptGroupNode.selectNodes(subdomainExp);
			saveDomains(concept, domainNodes, meaningId, originLtb);

			processStateCode = extractProcessState(conceptGroupNode, processStateConflictCount);
			conceptWordTypeCode = extractWordType(conceptGroupNode);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Node langGroupNode : langGroupNodes) {

				valueNode = (Element) langGroupNode.selectSingleNode(langExp);
				valueStr = valueNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(valueStr);

				if (!isLang) {
					continue;
				}

				lang = unifyLang(valueStr);

				//upper level definitions and notes
				extractAndSaveDefinitionsAndNotes(meaningId, langGroupNode, lang, concept, definitionsWithSameNotesCount);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Node termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					term = valueNode.getTextTrim();
					if (StringUtils.isBlank(term)) {
						reportHelper.appendToReport(doReports, REPORT_MISSING_VALUE, concept, "term puudub");
						continue;
					}

					termWordTypeCodes = new ArrayList<>();
					if (StringUtils.isNotBlank(conceptWordTypeCode)) {
						termWordTypeCodes.add(conceptWordTypeCode);
					}

					valueNode = (Element) termGroupNode.selectSingleNode(valueStateExp);
					if (valueNode != null) {
						valueStr = valueNode.getTextTrim();
						if (wordTypeCodes.containsKey(valueStr)) {
							termWordTypeCode = wordTypeCodes.get(valueStr);
							termWordTypeCodes.add(termWordTypeCode);
						} else if (lexemeValueStateCodes.containsKey(valueStr)) {
							// ok then, handled elsewhere
						}
					}

					homonymNr = getWordMaxHomonymNr(term, lang);
					homonymNr++;
					wordObj = new Word(term, lang, null, null, null, null, homonymNr, DEFAULT_WORD_MORPH_CODE, null, termWordTypeCodes);
					wordId = createOrSelectWord(wordObj, null, null, null);

					//lexeme
					lexemeObj = new Lexeme();
					lexemeObj.setWordId(wordId);
					lexemeObj.setMeaningId(meaningId);
					lexemeObj.setProcessStateCode(processStateCode);
					lexemeId = createLexeme(lexemeObj, getDataset());

					extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);

					extractAndUpdateLexemeProperties(lexemeId, termGroupNode);

					// definitions
					valueNodes = termGroupNode.selectNodes(definitionExp);
					for (Node definitionNode : valueNodes) {
						definitions = extractContentAndRefs(definitionNode, lang, term, true);
						saveDefinitionsAndSourceLinks(meaningId, definitions, concept, term);
					}

					// usages
					valueNodes = termGroupNode.selectNodes(usageExp);
					if (CollectionUtils.isNotEmpty(valueNodes)) {
						for (Node usageNode : valueNodes) {
							usages = extractContentAndRefs(usageNode, lang, term, true);
							saveUsagesAndSourceLinks(lexemeId, usages, concept, term);
						}
					}

					// sources
					valueNodes = termGroupNode.selectNodes(sourceExp);
					for (Node sourceNode : valueNodes) {
						sources = extractContentAndRefs(sourceNode, lang, term, false);
						saveLexemeSourceLinks(lexemeId, sources, concept, term);
					}

					if (doReports) {
						reportHelper.detectAndReportTermGrp(concept, term, homonymNr, lang, termGroupNode, dataErrorCount);
					}
				}

				if (doReports) {
					reportHelper.detectAndReportLanguageGrp(concept, langGroupNode, dataErrorCount);
				}
			}

			if (doReports) {
				reportHelper.detectAndReportConceptGrp(concept, conceptGroupNode, dataErrorCount);
			}

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		if (reportComposer != null) {
			reportComposer.end();
		}

		if (doReports) {
			logger.debug("Found {} data errors", dataErrorCount.getValue());
		}
		logger.debug("Found {} definitions with same notes", definitionsWithSameNotesCount.getValue());
		logger.debug("Found {} conflicting process state settings", processStateConflictCount.getValue());
		logger.debug("Found {} conflicting word type settings", wordTypeConflictCount.getValue());
		logger.debug("Found {} illegal source values", illegalSourceValueCount.getValue());

		end();
	}

	private String extractProcessState(Node conceptGroupNode, Count processStateConflictCount) {

		Element valueNode;
		String valueStr;
		String entryClass = null;
		String staatus = null;

		valueNode = (Element) conceptGroupNode.selectSingleNode(entryClassExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningAndLexemeProcessStateCodes.containsKey(valueStr)) {
				entryClass = meaningAndLexemeProcessStateCodes.get(valueStr);
			} else {
				logger.warn("Incorrect process state reference @ 'entry class': \"{}\"", valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(processStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningAndLexemeProcessStateCodes.containsKey(valueStr)) {
				staatus = meaningAndLexemeProcessStateCodes.get(valueStr);
			} else {
				logger.warn("Incorrect process state reference @ 'status': \"{}\"", valueStr);
			}
		}

		if (StringUtils.isNotBlank(entryClass) && StringUtils.isNotBlank(staatus)) {
			logger.warn("Conflicting process states: \"{}\" vs \"{}\"", entryClass, staatus);
			processStateConflictCount.increment();
		}
		if (StringUtils.isNotBlank(entryClass)) {
			return entryClass;
		}
		if (StringUtils.isNotBlank(staatus)) {
			return staatus;
		}
		return null;
	}

	private String extractWordType(Node conceptGroupNode) {

		Element valueNode;
		String valueStr;
		String wordType = null;

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningTypeExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (wordTypeCodes.containsKey(valueStr)) {
				wordType = wordTypeCodes.get(valueStr);
			} else {
				logger.warn("Incorrect word type reference: \"{}\"", valueStr);
			}
		}
		return wordType;
	}

	private void extractAndSaveMeaningFreeforms(Long meaningId, Node conceptGroupNode) throws Exception {

		List<Node> valueNodes;
		Element valueNode, valueNode1, valueNode2;
		String valueStr, valueStr1, valueStr2;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(conceptExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.CONCEPT_ID, valueStr);
		}

		valueNodes = conceptGroupNode.selectNodes(ltbIdExp);
		for (Node ltbIdNode : valueNodes) {
			valueStr = ((Element)ltbIdNode).getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_ID, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(ltbSourceExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_SOURCE, valueStr);
		}

		valueNodes = conceptGroupNode.selectNodes(noteExp);
		for (Node noteValueNode : valueNodes) {
			valueStr = ((Element)noteValueNode).getTextTrim();
			Long freeformId = createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
			if (((Element)noteValueNode).hasMixedContent()) {
				valueStr = handleFreeformRefLinks(noteValueNode, freeformId);
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

		valueNode = (Element) conceptGroupNode.selectSingleNode(unclassifiedExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.UNCLASSIFIED, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(worksheetExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.WORKSHEET, valueStr);
		}

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
			//TODO log
			//createLifecycleLog(meaningId, MEANING, LifecycleEventType.CREATE, valueStr1, valueTs);
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
			//TODO log
			//createLifecycleLog(meaningId, MEANING, LifecycleEventType.UPDATE, valueStr1, valueTs);
		}

		valueNode1 = (Element) conceptGroupNode.selectSingleNode(ltbCreatedByExp);
		valueNode2 = (Element) conceptGroupNode.selectSingleNode(ltbEõkkCreatedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = ltbDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			//TODO log
			//createLifecycleLog(meaningId, MEANING, LifecycleEventType.LTB_CREATED, valueStr1, valueTs);
		}

		valueNode1 = (Element) conceptGroupNode.selectSingleNode(ltbEõkkModifiedByExp);
		valueNode2 = (Element) conceptGroupNode.selectSingleNode(ltbEõkkModifiedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = ltbDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			//TODO log
			//createLifecycleLog(meaningId, MEANING, LifecycleEventType.LTB_MODIFIED, valueStr1, valueTs);
		}

		valueNode1 = (Element) conceptGroupNode.selectSingleNode(etEnReviewedByExp);
		valueNode2 = (Element) conceptGroupNode.selectSingleNode(etEnReviewedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = reviewDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			//TODO log
			//createLifecycleLog(meaningId, MEANING, LifecycleEventType.ET_EN_REVIEWED, valueStr1, valueTs);
		}

		valueNode1 = (Element) conceptGroupNode.selectSingleNode(enEtReviewedByExp);
		valueNode2 = (Element) conceptGroupNode.selectSingleNode(enEtReviewedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = reviewDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			//TODO log
			//createLifecycleLog(meaningId, MEANING, LifecycleEventType.EN_ET_REVIEWED, valueStr1, valueTs);
		}
	}

	private void saveLexemeSourceLinks(Long lexemeId, List<Content> sources, String concept, String term) throws Exception {

		for (Content sourceObj : sources) {
			List<Ref> refs = sourceObj.getRefs();
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.LEXEME, lexemeId, ref, concept, term);
			}
		}
	}

	private void saveDefinitionsAndSourceLinks(Long meaningId, List<Content> definitions, String concept, String term) throws Exception {

		for (Content definitionObj : definitions) {
			String definition = definitionObj.getValue();
			String lang = definitionObj.getLang();
			List<Ref> refs = definitionObj.getRefs();
			Long definitionId = createOrSelectDefinition(meaningId, definition, lang, getDataset());
			definitionObj.setId(definitionId);
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.DEFINITION, definitionId, ref, concept, term);
			}
		}
	}

	private void saveUsagesAndSourceLinks(Long lexemeId, List<Content> usages, String concept, String term) throws Exception {

		for (Content usageObj : usages) {
			String usage = usageObj.getValue();
			String lang = usageObj.getLang();
			List<Ref> refs = usageObj.getRefs();
			Long usageId = createLexemeFreeform(lexemeId, FreeformType.USAGE, usage, lang);
			usageObj.setId(usageId);
			for (Ref ref : refs) {
				createSourceLink(SourceOwner.USAGE, usageId, ref, concept, term);
			}
		}
	}

	private void createSourceLink(SourceOwner sourceOwner, Long ownerId, Ref ref, String concept, String term) throws Exception {

		String minorRef = ref.getMinorRef();
		String majorRef = ref.getMajorRef();
		ReferenceType refType = ref.getType();
		Long sourceId = getSource(majorRef);
		if (sourceId == null) {
			reportHelper.appendToReport(doReports, REPORT_MISSING_SOURCE_REFS, concept, term, majorRef);
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

	private void extractAndSaveDefinitionsAndNotes(
			Long meaningId, Node langGroupNode, String lang, String concept, Count definitionsWithSameNotesCount) throws Exception {

		List<Node> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Node> definitionNoteNodes = langGroupNode.selectNodes(noteExp);
		List<Content> definitions;
		int totalDefinitionCount = 0;

		for (Node definitionNode : definitionNodes) {
			definitions = extractContentAndRefs(definitionNode, lang, concept, true);
			saveDefinitionsAndSourceLinks(meaningId, definitions, concept, "*");
			totalDefinitionCount += definitions.size();
			for (Content definitionObj : definitions) {
				Long definitionId = definitionObj.getId();
				for (Node definitionNoteNode : definitionNoteNodes) {
					String definitionNote = ((Element)definitionNoteNode).getTextTrim();
					Long freeformId = createDefinitionFreeform(definitionId, FreeformType.PUBLIC_NOTE, definitionNote);
					if (((Element)definitionNoteNode).hasMixedContent()) {
						definitionNote = handleFreeformRefLinks(definitionNoteNode, freeformId);
						updateFreeformText(freeformId, definitionNote);
					}
				}
			}
		}
		if ((totalDefinitionCount > 1) && (definitionNoteNodes.size() > 1)) {
			definitionsWithSameNotesCount.increment(totalDefinitionCount);
		}
	}

	private void extractAndUpdateLexemeProperties(Long lexemeId, Node termGroupNode) throws Exception {

		Element valueNode;
		String valueStr, mappedValueStr;
		long valueLong;
		Timestamp valueTs;

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", lexemeId);

		Map<String, Object> valueParamMap = new HashMap<>();

		valueNode = (Element) termGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueParamMap.put("created_by", valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			valueParamMap.put("created_on", valueTs);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueParamMap.put("modified_by", valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			valueParamMap.put("modified_on", valueTs);
		}

		valueNode = (Element) termGroupNode.selectSingleNode(valueStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (lexemeValueStateCodes.containsKey(valueStr)) {
				mappedValueStr = lexemeValueStateCodes.get(valueStr);
				valueParamMap.put("value_state_code", mappedValueStr);
			} else if (wordTypeCodes.containsKey(valueStr)) {
				// ok then, handled elsewhere
			} else {
				logger.warn("Incorrect lexeme value state or word type reference: \"{}\"", valueStr);
			}
		}

		if (MapUtils.isNotEmpty(valueParamMap)) {
			basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
		}
	}

	private void extractAndSaveLexemeFreeforms(Long lexemeId, Node termGroupNode) throws Exception {

		List<Node> valueNodes;
		Element valueNode1, valueNode2;
		String valueStr, valueStr1, valueStr2;
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
			//TODO log
			//createLifecycleLog(lexemeId, LEXEME, LifecycleEventType.CREATE, valueStr1, valueTs);
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
			//TODO log
			//createLifecycleLog(lexemeId, LEXEME, LifecycleEventType.UPDATE, valueStr1, valueTs);
		}

		valueNode1 = (Element) termGroupNode.selectSingleNode(eõkkCreatedByExp);
		valueNode2 = (Element) termGroupNode.selectSingleNode(ltbEõkkCreatedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = reviewDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			//TODO log
			//createLifecycleLog(lexemeId, LEXEME, LifecycleEventType.EÕKK_CREATED, valueStr1, valueTs);
		}

		valueNode1 = (Element) termGroupNode.selectSingleNode(ltbEõkkModifiedByExp);
		valueNode2 = (Element) termGroupNode.selectSingleNode(ltbEõkkModifiedOnExp);
		if (valueNode1 != null) {
			valueStr1 = valueNode1.getTextTrim();
		} else {
			valueStr1 = null;
		}
		if (valueNode2 != null) {
			valueStr2 = valueNode2.getTextTrim();
			valueLong = reviewDateFormat.parse(valueStr2).getTime();
			valueTs = new Timestamp(valueLong);
		} else {
			valueTs = null;
		}
		if (StringUtils.isNotBlank(valueStr1) && (valueTs != null)) {
			//TODO log
			//createLifecycleLog(lexemeId, LEXEME, LifecycleEventType.EÕKK_MODIFIED, valueStr1, valueTs);
		}

		valueNodes = termGroupNode.selectNodes(noteExp);
		for (Node valueNode : valueNodes) {
			valueStr = ((Element)valueNode).getTextTrim();
			Long freeformId = createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, valueStr, null);
			if (((Element)valueNode).hasMixedContent()) {
				valueStr = handleFreeformRefLinks(valueNode, freeformId);
				updateFreeformText(freeformId, valueStr);
			}
		}
	}

	private void saveDomains(String concept, List<Node> domainNodes, Long meaningId, String domainOrigin) throws Exception {

		if (domainNodes == null) {
			return;
		}
		List<String> domainCodes = new ArrayList<>();
		String domainCode;

		for (Node domainNode : domainNodes) {
			domainCode = ((Element)domainNode).getTextTrim();
			int listingDelimCount = StringUtils.countMatches(domainCode, listingsDelimiter);
			if (listingDelimCount == 0) {
				handleDomain(concept, meaningId, domainCode, domainOrigin, domainCodes);
			} else if (listingDelimCount == 1) {
				String illDelimitedDomainCode = StringUtils.replace(domainCode, String.valueOf(listingsDelimiter), ", ");
				boolean domainExists = domainExists(illDelimitedDomainCode, domainOrigin);
				if (domainExists) {
					logger.warn("Recovered illdelimited domain value @ concept \"{}\" - \"{}\"", concept, domainCode);
				} else {
					handleDomainListing(concept, meaningId, domainCode, domainOrigin, domainCodes);
				}
			} else {
				handleDomainListing(concept, meaningId, domainCode, domainOrigin, domainCodes);
			}
		}
	}

	private void handleDomainListing(String concept, Long meaningId, String domainCodeListing, String domainOrigin, List<String> domainCodes) throws Exception {

		String[] separateDomainCodes = StringUtils.split(domainCodeListing, listingsDelimiter);
		for (String separateDomainCode : separateDomainCodes) {
			handleDomain(concept, meaningId, separateDomainCode, domainOrigin, domainCodes);
		}
	}

	private void handleDomain(String concept, Long meaningId, String domainCode, String domainOrigin, List<String> domainCodes) throws Exception {

		if (domainCodes.contains(domainCode)) {
			logger.warn("Domain reference duplicate @ concept \"{}\" - \"{}\"", concept, domainCode);
			return;
		}
		boolean domainExists = domainExists(domainCode, domainOrigin);
		if (domainExists) {
			createMeaningDomain(meaningId, domainCode, domainOrigin);
			domainCodes.add(domainCode);
		} else {
			logger.warn("Incorrect domain reference @ concept \"{}\" - \"{}\"", concept, domainCode);
		}
	}
}
