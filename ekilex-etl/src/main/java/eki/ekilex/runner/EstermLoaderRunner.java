package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.ContentKey;
import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleLogType;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.exception.DataLoadingException;
import eki.ekilex.constant.EstermLoaderConstant;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Word;
import eki.ekilex.runner.util.EstermLoaderHelper;
import eki.ekilex.runner.util.EstermReportHelper;
import eki.ekilex.service.ReportComposer;

@Component
public class EstermLoaderRunner extends AbstractLoaderRunner implements EstermLoaderConstant {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String LTB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String REVIEW_TIMESTAMP_PATTERN = "yy/MM/dd";

	@Autowired
	private EstermLoaderHelper loaderHelper;

	@Autowired
	private EstermReportHelper reportHelper;

	private ReportComposer reportComposer;

	private DateFormat defaultDateFormat;

	private DateFormat ltbDateFormat;

	private DateFormat reviewDateFormat;

	//TODO obsolete?
	@Deprecated
	private Map<String, String> meaningStateCodes;

	private Map<String, String> processStateCodes;

	private Map<String, String> meaningTypeCodes;

	private Map<String, String> lexemeTypeCodes;

	@Override
	String getDataset() {
		return "est";
	}

	@Override
	void initialise() throws Exception {

		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
		ltbDateFormat = new SimpleDateFormat(LTB_TIMESTAMP_PATTERN);
		reviewDateFormat = new SimpleDateFormat(REVIEW_TIMESTAMP_PATTERN);

		meaningStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_STAATUS, ClassifierName.MEANING_STATE.name());
		processStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_STAATUS, ClassifierName.PROCESS_STATE.name());
		meaningTypeCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_MÕISTETÜÜP);
		lexemeTypeCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_KEELENDITÜÜP);
	}

	@Transactional
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Starting loading Esterm...");

		final String defaultGovernmentValue = "-";
		final String defaultWordMorphCode = "??";

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("esterm load report",
					REPORT_DEFINITIONS_NOTES_MESS, REPORT_CREATED_MODIFIED_MESS,
					REPORT_ILLEGAL_CLASSIFIERS, REPORT_DEFINITIONS_AT_TERMS, REPORT_MISSING_SOURCE_REFS,
					REPORT_MULTIPLE_DEFINITIONS, REPORT_NOT_A_DEFINITION, REPORT_DEFINITIONS_NOTES_MISMATCH,
					REPORT_MISSING_VALUE);
			reportHelper.setup(reportComposer, meaningStateCodes, processStateCodes, meaningTypeCodes, lexemeTypeCodes);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		List<Element> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		Element valueNode;
		List<Element> valueNodes, langGroupNodes, termGroupNodes, domainNodes;
		Long wordId, meaningId, lexemeId, governmentId, usageMeaningId;
		List<Content> definitions, usages;
		String valueStr, concept, term;
		String lang;
		int homonymNr;
		Word wordObj;
		Meaning meaningObj;
		Lexeme lexemeObj;

		Count dataErrorCount = new Count();
		Count definitionsWithSameNotesCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Element conceptGroupNode : conceptGroupNodes) {

			boolean isLanguageTypeConcept = isLanguageTypeConcept(conceptGroupNode);
			if (!isLanguageTypeConcept) {
				continue;
			}

			valueNode = (Element) conceptGroupNode.selectSingleNode(conceptExp);
			concept = valueNode.getTextTrim();

			// meaning
			meaningObj = new Meaning();
			extractAndApplyMeaningProperties(conceptGroupNode, meaningObj);
			meaningId = createMeaning(meaningObj);
			extractAndSaveMeaningFreeforms(meaningId, conceptGroupNode);

			// domains
			domainNodes = conceptGroupNode.selectNodes(domainExp);
			saveDomains(concept, domainNodes, meaningId, originLenoch);
			domainNodes = conceptGroupNode.selectNodes(subdomainExp);
			saveDomains(concept, domainNodes, meaningId, originLtb);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Element langGroupNode : langGroupNodes) {

				valueNode = (Element) langGroupNode.selectSingleNode(langExp);
				valueStr = valueNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(valueStr);

				if (!isLang) {
					continue;
				}

				lang = unifyLang(valueStr);

				//upper level definitions and notes
				extractAndSaveDefinitionsAndNotes(meaningId, langGroupNode, lang, concept, doReports, definitionsWithSameNotesCount);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					term = valueNode.getTextTrim();
					if (StringUtils.isBlank(term)) {
						reportHelper.appendToReport(doReports, REPORT_MISSING_VALUE, concept, "term puudub");
						continue;
					}

					homonymNr = getWordMaxHomonymNr(term, lang);
					homonymNr++;
					wordObj = new Word(term, lang, null, null, null, null, homonymNr, defaultWordMorphCode, null);
					wordId = saveWord(wordObj, null, null, null);

					//lexeme
					lexemeObj = new Lexeme();
					lexemeObj.setWordId(wordId);
					lexemeObj.setMeaningId(meaningId);
					lexemeId = createLexeme(lexemeObj, getDataset());

					extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);

					extractAndUpdateLexemeProperties(lexemeId, termGroupNode);

					// definitions
					valueNodes = termGroupNode.selectNodes(definitionExp);
					for (Element definitionNode : valueNodes) {
						definitions = extractContentAndRefs(definitionNode, lang, term);
						saveDefinitionsAndRefLinks(meaningId, definitions, concept, term, doReports);
					}

					// usages
					valueNodes = termGroupNode.selectNodes(usageExp);
					if (CollectionUtils.isNotEmpty(valueNodes)) {
						governmentId = createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, defaultGovernmentValue);
						usageMeaningId = createFreeformTextOrDate(FreeformType.USAGE_MEANING, governmentId, null, null);
						for (Element usageNode : valueNodes) {
							usages = extractContentAndRefs(usageNode, lang, term);
							saveUsagesAndRefLinks(usageMeaningId, usages, concept, term, doReports);
						}
					}

					// sources
					valueNodes = termGroupNode.selectNodes(sourceExp);
					for (Element sourceNode : valueNodes) {
						valueStr = sourceNode.getTextTrim();
						Long freeformId = createLexemeFreeform(lexemeId, FreeformType.SOURCE, valueStr, null);
						valueStr = handleFreeformRefLinks(sourceNode, freeformId);
						updateFreeformText(freeformId, valueStr);
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

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private boolean isLanguageTypeConcept(Element conceptGroupNode) {

		String valueStr;
		List<Element> valueNodes = conceptGroupNode.selectNodes(langGroupExp + "/" + langExp);
		for (Element langNode : valueNodes) {
			valueStr = langNode.attributeValue(langTypeAttr);
			boolean isLang = isLang(valueStr);
			if (isLang) {
				continue;
			}
			return false;
		}
		return true;
	}

	private void extractAndApplyMeaningProperties(Element conceptGroupNode, Meaning meaningObj) throws Exception {

		Element valueNode;
		String valueStr, mappedValueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(entryClassExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setProcessStateCode(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningStateCodes.containsKey(valueStr)) {
				mappedValueStr = meaningStateCodes.get(valueStr);
				meaningObj.setMeaningStateCode(mappedValueStr);
			} else if (processStateCodes.containsKey(valueStr)) {
				mappedValueStr = processStateCodes.get(valueStr);
				meaningObj.setProcessStateCode(mappedValueStr);
			} else {
				logger.warn("Incorrect meaning state/process state reference: \"{}\"", valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningTypeExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningTypeCodes.containsKey(valueStr)) {
				mappedValueStr = meaningTypeCodes.get(valueStr);
				meaningObj.setMeaningTypeCode(mappedValueStr);
			} else {
				logger.warn("Incorrect meaning type reference: \"{}\"", valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setCreatedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setCreatedOn(valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setModifiedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setModifiedOn(valueTs);
		}
	}

	private void extractAndSaveMeaningFreeforms(Long meaningId, Element conceptGroupNode) throws Exception {

		List<Element> valueNodes;
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
		for (Element ltbIdNode : valueNodes) {
			valueStr = ltbIdNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_ID, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(ltbSourceExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_SOURCE, valueStr);
		}

		valueNodes = conceptGroupNode.selectNodes(noteExp);
		for (Element noteValueNode : valueNodes) {
			valueStr = noteValueNode.getTextTrim();
			Long freeformId = createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
			if (noteValueNode.hasMixedContent()) {
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
			createLifecycleLog(meaningId, MEANING, LifecycleLogType.CREATED, valueStr1, valueTs);
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
			createLifecycleLog(meaningId, MEANING, LifecycleLogType.MODIFIED, valueStr1, valueTs);
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
			createLifecycleLog(meaningId, MEANING, LifecycleLogType.LTB_CREATED, valueStr1, valueTs);
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
			createLifecycleLog(meaningId, MEANING, LifecycleLogType.LTB_MODIFIED, valueStr1, valueTs);
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
			createLifecycleLog(meaningId, MEANING, LifecycleLogType.ET_EN_REVIEWED, valueStr1, valueTs);
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
			createLifecycleLog(meaningId, MEANING, LifecycleLogType.EN_ET_REVIEWED, valueStr1, valueTs);
		}
	}

	private List<Content> extractContentAndRefs(Element rootContentNode, String lang, String term) throws Exception {

		List<Content> contentList = new ArrayList<>();
		Iterator<Node> contentNodeIter = rootContentNode.nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;
		Content contentObj = null;
		Ref refObj = null;
		boolean isRefOn = false;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				valueStr = StringUtils.replaceChars(valueStr, '\n', ' ');
				valueStr = StringUtils.trim(valueStr);
				boolean isListing = loaderHelper.isListing(valueStr);
				boolean isRefEnd = loaderHelper.isRefEnd(valueStr);
				String content = loaderHelper.getContent(valueStr);
				boolean contentExists = StringUtils.isNotBlank(content);
				if (isListing) {
					continue;
				}
				if (!isRefOn && isRefEnd) {
					logger.warn("Illegal ref end notation @ \"{}\" : {}", term, rootContentNode.asXML());
				}
				if (isRefOn && isRefEnd) {
					String smallRef = loaderHelper.collectSmallRef(valueStr);
					if (StringUtils.isNotBlank(smallRef)) {
						refObj.setMinorRef(smallRef);
					}
				}
				if (contentExists) {
					contentObj = newContent(lang, content);
					contentList.add(contentObj);
				}
				isRefOn = false;
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				valueStr = StringUtils.replaceChars(valueStr, '\n', ' ');
				valueStr = StringUtils.trim(valueStr);
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						isRefOn = true;
						String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
						if (contentObj == null) {
							contentObj = newContent(lang, "-");
							contentList.add(contentObj);
							logger.warn("Source reference for empty content @ \"{}\"-\"{}\"", term, sourceCodeOrName);
						}
						refObj = new Ref();
						refObj.setMajorRef(sourceCodeOrName);
						contentObj.getRefs().add(refObj);
					} else {
						throw new DataLoadingException("Handling of " + tlinkAttrValue + " not supported!");
					}
				}
			}
		}
		return contentList;
	}

	private Content newContent(String lang, String content) {
		Content contentObj;
		contentObj = new Content();
		contentObj.setValue(content);
		contentObj.setLang(lang);
		contentObj.setRefs(new ArrayList<>());
		return contentObj;
	}

	private void saveDefinitionsAndRefLinks(Long meaningId, List<Content> definitions, String concept, String term, boolean doReports) throws Exception {

		for (Content definitionObj : definitions) {
			String definition = definitionObj.getValue();
			String lang = definitionObj.getLang();
			List<Ref> refs = definitionObj.getRefs();
			Long definitionId = createDefinition(meaningId, definition, lang, getDataset());
			definitionObj.setId(definitionId);
			for (Ref ref : refs) {
				String minorRef = ref.getMinorRef();
				String majorRef = ref.getMajorRef();
				Long sourceId = getSource(majorRef);
				if (sourceId == null) {
					reportHelper.appendToReport(doReports, REPORT_MISSING_SOURCE_REFS, concept, term, majorRef);
					continue;
				}
				createDefinitionRefLink(definitionId, ReferenceType.SOURCE, sourceId, minorRef, majorRef);	
			}
		}
	}

	private void saveUsagesAndRefLinks(Long usageMeaningId, List<Content> usages, String concept, String term, boolean doReports) throws Exception {

		for (Content usageObj : usages) {
			String usage = usageObj.getValue();
			String lang = usageObj.getLang();
			List<Ref> refs = usageObj.getRefs();
			Long freeformId = createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, usage, lang);
			usageObj.setId(freeformId);
			for (Ref ref : refs) {
				String minorRef = ref.getMinorRef();
				String majorRef = ref.getMajorRef();
				Long sourceId = getSource(majorRef);
				if (sourceId == null) {
					reportHelper.appendToReport(doReports, REPORT_MISSING_SOURCE_REFS, concept, term, majorRef);
					continue;
				}
				createFreeformRefLink(freeformId, ReferenceType.SOURCE, sourceId, minorRef, majorRef);
			}
		}
	}

	private void extractAndSaveDefinitionsAndNotes(
			Long meaningId, Element langGroupNode, String lang, String concept, boolean doReports, Count definitionsWithSameNotesCount) throws Exception {

		List<Element> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Element> definitionNoteNodes = langGroupNode.selectNodes(noteExp);
		List<Content> definitions;
		int totalDefinitionCount = 0;

		for (Element definitionNode : definitionNodes) {
			definitions = extractContentAndRefs(definitionNode, lang, concept);
			saveDefinitionsAndRefLinks(meaningId, definitions, concept, "*", doReports);
			totalDefinitionCount += definitions.size();
			for (Content definitionObj : definitions) {
				Long definitionId = definitionObj.getId();
				for (Element definitionNoteNode : definitionNoteNodes) {
					String definitionNote = definitionNoteNode.getTextTrim();
					Long freeformId = createDefinitionFreeform(definitionId, FreeformType.PUBLIC_NOTE, definitionNote);
					if (definitionNoteNode.hasMixedContent()) {
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

	private void extractAndUpdateLexemeProperties(Long lexemeId, Element termGroupNode) throws Exception {

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

		valueNode = (Element) termGroupNode.selectSingleNode(lexemeTypeExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (lexemeTypeCodes.containsKey(valueStr)) {
				mappedValueStr = lexemeTypeCodes.get(valueStr);
				valueParamMap.put("type_code", mappedValueStr);
			} else {
				logger.warn("Incorrect lexeme type reference: \"{}\"", valueStr);
			}
		}

		if (MapUtils.isNotEmpty(valueParamMap)) {
			basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
		}
	}

	private void extractAndSaveLexemeFreeforms(Long lexemeId, Element termGroupNode) throws Exception {

		List<Element> valueNodes;
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
			createLifecycleLog(lexemeId, LEXEME, LifecycleLogType.CREATED, valueStr1, valueTs);
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
			createLifecycleLog(lexemeId, LEXEME, LifecycleLogType.MODIFIED, valueStr1, valueTs);
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
			createLifecycleLog(lexemeId, LEXEME, LifecycleLogType.EÕKK_CREATED, valueStr1, valueTs);
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
			createLifecycleLog(lexemeId, LEXEME, LifecycleLogType.EÕKK_MODIFIED, valueStr1, valueTs);
		}

		valueNodes = termGroupNode.selectNodes(noteExp);
		for (Element valueNode : valueNodes) {
			valueStr = valueNode.getTextTrim();
			Long freeformId = createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, valueStr, null);
			if (valueNode.hasMixedContent()) {
				valueStr = handleFreeformRefLinks(valueNode, freeformId);
				updateFreeformText(freeformId, valueStr);
			}
		}
	}

	private void saveDomains(String concept, List<Element> domainNodes, Long meaningId, String domainOrigin) throws Exception {

		if (domainNodes == null) {
			return;
		}
		List<String> domainCodes = new ArrayList<>();
		String domainCode;

		for (Element domainNode : domainNodes) {
			domainCode = domainNode.getTextTrim();
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

	private boolean domainExists(String domainCode, String domainOrigin) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("origin", domainOrigin);
		tableRowParamMap.put("code", domainCode);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
		boolean domainExists = ((Long) tableRowValueMap.get("cnt")) > 0;
		return domainExists;
	}

	//TODO should be replaced by separate ref links handling later
	private String handleFreeformRefLinks(Element mixedContentNode, Long ownerId) throws Exception {

		Iterator<Node> contentNodeIter = mixedContentNode.nodeIterator();
		StringBuffer contentBuf = new StringBuffer();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				contentBuf.append(valueStr);
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
						Long sourceId = getSource(sourceCodeOrName);
						if (sourceId == null) {
							contentBuf.append(valueStr);
						} else {
							Long refLinkId = createFreeformRefLink(ownerId, ReferenceType.SOURCE, sourceId, null, null);
							//simulating markdown link syntax
							contentBuf.append("[");
							contentBuf.append(valueStr);
							contentBuf.append("]");
							contentBuf.append("(");
							contentBuf.append(ContentKey.FREEFORM_REF_LINK);
							contentBuf.append(":");
							contentBuf.append(refLinkId);
							contentBuf.append(")");
						}
					} else {
						// unknown ref type
						contentBuf.append(valueStr);
					}
				} else {
					throw new DataLoadingException("Unsupported mixed content node name: " + contentNode.getName());
				}
			} else {
				throw new DataLoadingException("Unsupported mixed content node type: " + contentNode.getClass());
			}
		}
		valueStr = contentBuf.toString();
		return valueStr;
	}

	private Long getSource(String sourceCodeOrName) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("sourceCodeOrName", sourceCodeOrName);
		List<Map<String, Object>> results = basicDbService.queryList(SQL_SELECT_SOURCE_BY_CODE_OR_NAME, tableRowParamMap);
		if (CollectionUtils.isEmpty(results)) {
			//logger.warn("Could not find matching source \"{}\"", sourceCodeOrName);
			return null;
		}
		if (results.size() > 1) {
			logger.warn("Several sources match the \"{}\"", sourceCodeOrName);
		}
		Map<String, Object> result = results.get(0);
		Long sourceId = Long.valueOf(result.get("id").toString());
		return sourceId;
	}

	class Content extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long id;

		private String value;

		private String lang;

		private List<Ref> refs;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public List<Ref> getRefs() {
			return refs;
		}

		public void setRefs(List<Ref> refs) {
			this.refs = refs;
		}
	}

	class Ref extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String minorRef;

		private String majorRef;

		public String getMinorRef() {
			return minorRef;
		}

		public void setMinorRef(String minorRef) {
			this.minorRef = minorRef;
		}

		public String getMajorRef() {
			return majorRef;
		}

		public void setMajorRef(String majorRef) {
			this.majorRef = majorRef;
		}

	}
}
