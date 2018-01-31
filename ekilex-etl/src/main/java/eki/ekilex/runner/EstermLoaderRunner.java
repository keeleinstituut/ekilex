package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.ContentKey;
import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleLogType;
import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.common.data.Count;
import eki.common.exception.DataLoadingException;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

/*
 * TODO lots of stuff on hold here until owners of Esterm dare to share full data files.
 */
@Component
public class EstermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String REPORT_DEFINITIONS_NOTES_MESS = "definitions_notes_mess";

	private static final String REPORT_CREATED_MODIFIED_MESS = "created_modified_mess";

	private static final String REPORT_ILLEGAL_CLASSIFIERS = "illegal_classifiers";

	private static final String REPORT_DEFINITIONS_AT_TERMS = "definitions_at_terms";

	private static final String REPORT_MISSING_SOURCE_REFS = "missing_source_refs";

	private static final String REPORT_MULTIPLE_DEFINITIONS = "multiple_definitions";

	private static final String REPORT_NOT_A_DEFINITION = "not_a_definition";

	private static final String REPORT_DEFINITIONS_NOTES_MISMATCH = "definitions_notes_mismatch";

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String LTB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String REVIEW_TIMESTAMP_PATTERN = "yy/MM/dd";

	private static final String SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";

	private static final String SQL_SELECT_SOURCE_BY_CODE_OR_NAME =
			"select s.id from " + SOURCE + " s, " + SOURCE_FREEFORM + " sf "
			+ "where sf.source_id = s.id and exists (select sn.id from " + FREEFORM + " sn where sf.freeform_id = sn.id and sn.value_text = :sourceCodeOrName)";

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

	private final String conceptGroupExp = "/mtf/conceptGrp";
	private final String langGroupExp = "languageGrp";
	private final String langExp = "language";
	private final String termGroupExp = "termGrp";
	private final String termExp = "term";
	private final String conceptExp = "concept";
	private final String domainExp = "descripGrp/descrip[@type='Valdkonnaviide']/xref[contains(@Tlink, 'Valdkond:')]";
	private final String subdomainExp = "descripGrp/descrip[@type='Alamvaldkond']";
	private final String sourceExp = "descripGrp/descrip[@type='Allikaviide']";
	private final String usageExp = "descripGrp/descrip[@type='Kontekst']";
	private final String definitionExp = "descripGrp/descrip[@type='Definitsioon']";
	private final String lexemeTypeExp = "descripGrp/descrip[@type='Keelenditüüp']";
	private final String entryClassExp = "system[@type='entryClass']";
	private final String meaningStateExp = "descripGrp/descrip[@type='Staatus']";
	private final String meaningTypeExp = "descripGrp/descrip[@type='Mõistetüüp']";
	private final String createdByExp = "transacGrp/transac[@type='origination']";
	private final String createdOnExp = "transacGrp[transac/@type='origination']/date";
	private final String modifiedByExp = "transacGrp/transac[@type='modification']";
	private final String modifiedOnExp = "transacGrp[transac/@type='modification']/date";
	private final String ltbIdExp = "descripGrp/descrip[@type='ID-number']";
	private final String ltbSourceExp = "descripGrp/descrip[@type='Päritolu']";
	private final String noteExp = "descripGrp/descrip[@type='Märkus']";
	private final String privateNoteExp = "descripGrp/descrip[@type='Sisemärkus']";
	private final String unclassifiedExp = "descripGrp/descrip[@type='Tunnus']";
	private final String worksheetExp = "descripGrp/descrip[@type='Tööleht']";
	private final String ltbCreatedByExp = "descripGrp/descrip[@type='Sisestaja']";//concept
	private final String eõkkCreatedByExp = "descripGrp/descrip[@type='Autor']";//term
	private final String ltbEõkkCreatedOnExp = "descripGrp/descrip[@type='Sisestusaeg']";//concept, term
	private final String ltbEõkkModifiedByExp = "descripGrp/descrip[@type='Muutja']";//concept, term
	private final String ltbEõkkModifiedOnExp = "descripGrp/descrip[@type='Muutmisaeg']";//concept, term
	private final String etEnReviewedByExp = "descripGrp/descrip[@type='et-en kontrollija']";
	private final String etEnReviewedOnExp = "descripGrp/descrip[@type='et-en kontrollitud']";
	private final String enEtReviewedByExp = "descripGrp/descrip[@type='en-et kontrollija']";
	private final String enEtReviewedOnExp = "descripGrp/descrip[@type='en-et kontrollitud']";
	private final String xrefExp = "xref";

	private final String langTypeAttr = "type";
	private final String xrefTlinkAttr = "Tlink";

	private final String xrefTlinkSourcePrefix = "Allikas:";

	private final String originLenoch = "lenoch";
	private final String originLtb = "ltb";

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
	public void execute(String dataXmlFilePath, String dataset, boolean doReports) throws Exception {

		logger.debug("Starting loading Esterm...");

		final String defaultGovernmentValue = "-";
		final String defaultWordMorphCode = "SgN";

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("esterm load report",
					REPORT_DEFINITIONS_NOTES_MESS, REPORT_CREATED_MODIFIED_MESS,
					REPORT_ILLEGAL_CLASSIFIERS, REPORT_DEFINITIONS_AT_TERMS, REPORT_MISSING_SOURCE_REFS,
					REPORT_MULTIPLE_DEFINITIONS, REPORT_NOT_A_DEFINITION, REPORT_DEFINITIONS_NOTES_MISMATCH);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		List<Element> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		Element valueNode;
		List<Element> valueNodes, langGroupNodes, termGroupNodes, domainNodes;
		Long wordId, meaningId, lexemeId, governmentId, usageMeaningId;
		List<Long> definitionIds;
		String valueStr, concept, term;
		String lang;
		int homonymNr;
		Word wordObj;
		Meaning meaningObj;
		Lexeme lexemeObj;

		Count dataErrorCount = new Count();
		Count definitionsWithSameNoteCount = new Count();

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
			saveDomains(domainNodes, meaningId, originLenoch);
			domainNodes = conceptGroupNode.selectNodes(subdomainExp);
			saveDomains(domainNodes, meaningId, originLtb);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Element langGroupNode : langGroupNodes) {

				valueNode = (Element) langGroupNode.selectSingleNode(langExp);
				valueStr = valueNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(valueStr);

				if (!isLang) {
					continue;
				}

				lang = unifyLang(valueStr);

				extractAndSaveDefinitionsAndFreeforms(meaningId, langGroupNode, lang, dataset, definitionsWithSameNoteCount);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					term = valueNode.getTextTrim();

					homonymNr = getWordMaxHomonymNr(term, lang);
					homonymNr++;
					wordObj = new Word(term, lang, null, null, null, null, homonymNr, defaultWordMorphCode, null);
					wordId = saveWord(wordObj, null, null, null);

					//lexeme
					lexemeObj = new Lexeme();
					lexemeObj.setWordId(wordId);
					lexemeObj.setMeaningId(meaningId);
					lexemeId = createLexeme(lexemeObj, dataset);

					extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);

					extractAndUpdateLexemeProperties(lexemeId, termGroupNode);

					//TODO split by links
					valueNodes = termGroupNode.selectNodes(definitionExp);
					for (Element definitionNode : valueNodes) {
						//definitionIds = new ArrayList<>();
						//extractAndSaveDefinitionsAndRefLinks(definitionNode, definitionIds, meaningId, lang, dataset);
						valueStr = definitionNode.getTextTrim();
						Long definitionId = createDefinition(meaningId, valueStr, lang, dataset);
						if (definitionNode.hasMixedContent()) {
							valueStr = handleRefLinks(definitionNode, ReferenceOwner.DEFINITION, definitionId);
							updateDefinitionValue(definitionId, valueStr);
						}
					}

					//TODO split by links
					valueNodes = termGroupNode.selectNodes(usageExp);
					if (CollectionUtils.isNotEmpty(valueNodes)) {
						governmentId = createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, defaultGovernmentValue);
						usageMeaningId = createFreeformTextOrDate(FreeformType.USAGE_MEANING, governmentId, null, null);
						for (Element usageNode : valueNodes) {
							//extractAndSaveUsagesAndRefLinks(usageNode, usageMeaningId, lang, dataset);
							valueStr = usageNode.getTextTrim();
							Long freeformId = createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, valueStr, lang);
							if (usageNode.hasMixedContent()) {
								valueStr = handleRefLinks(usageNode, ReferenceOwner.FREEFORM, freeformId);
								updateFreeformText(freeformId, valueStr);
							}
						}
					}

					valueNodes = termGroupNode.selectNodes(sourceExp);
					for (Element sourceNode : valueNodes) {
						valueStr = sourceNode.getTextTrim();
						Long freeformId = createLexemeFreeform(lexemeId, FreeformType.SOURCE, valueStr, null);
						if (sourceNode.hasMixedContent()) {
							valueStr = handleRefLinks(sourceNode, ReferenceOwner.FREEFORM, freeformId);
							updateFreeformText(freeformId, valueStr);
						}
					}

					if (doReports) {
						detectAndReportTermGrp(concept, term, homonymNr, lang, termGroupNode, dataErrorCount);
					}
				}

				if (doReports) {
					detectAndReportLanguageGrp(concept, langGroupNode, dataErrorCount);
				}
			}

			if (doReports) {
				//TODO put it back later
				//detectAndReportConceptGrp(concept, conceptGroupNode, dataErrorCount);
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
			logger.debug("Found {} data errors", dataErrorCount);
		}
		logger.debug("Found {} definitions with same notes", definitionsWithSameNoteCount.getValue());

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

	private void detectAndReportConceptGrp(String concept, Element conceptGroupNode, Count dataErrorCount) throws Exception {

		StringBuffer logBuf = new StringBuffer();

		Map<String, Object> tableRowParamMap;
		Map<String, Object> tableRowValueMap;
		List<Element> valueNodes;
		Element valueNode;
		List<String> values;
		String valueStr;

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (!meaningStateCodes.containsKey(valueStr) && !processStateCodes.containsKey(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("tundmatu staatus: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningTypeExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (!meaningTypeCodes.containsKey(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("tundmatu mõistetüüp: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(domainExp);
		values = new ArrayList<>();
		for (Element domainNode : valueNodes) {
			valueStr = domainNode.getTextTrim();
			if (values.contains(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("korduv valdkonnaviide: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
				continue;
			}
			values.add(valueStr);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("origin", originLenoch);
			tableRowParamMap.put("code", valueStr);
			tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
			boolean exists = ((Long) tableRowValueMap.get("cnt")) > 0;
			if (!exists) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("tundmatu valdkonnaviide: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(subdomainExp);
		values = new ArrayList<>();
		for (Element domainNode : valueNodes) {
			valueStr = domainNode.getTextTrim();
			if (values.contains(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("korduv alamvaldkonnaviide: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
				continue;
			}
			values.add(valueStr);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("origin", originLtb);
			tableRowParamMap.put("code", valueStr);
			tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
			boolean exists = ((Long) tableRowValueMap.get("cnt")) > 0;
			if (!exists) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("tundmatu alamvaldkonnaviide: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
			}
		}

		values = new ArrayList<>();
		List<Element> createdByNodes = conceptGroupNode.selectNodes(ltbCreatedByExp);
		List<Element> createdOnNodes = conceptGroupNode.selectNodes(ltbEõkkCreatedOnExp);
		int createdByCount = createdByNodes.size();
		int createdOnCount = createdOnNodes.size();
		boolean isTooManyCreates = createdByCount > 1 && createdOnCount > 1;
		boolean isIncompleteCreate = (createdByCount == 1 || createdOnCount == 1) && (createdByCount != createdOnCount);
		if (isTooManyCreates || isIncompleteCreate) {
			for (Element createdNode : createdByNodes) {
				valueStr = createdNode.getTextTrim();
				values.add(valueStr);
			}
			for (Element createdNode : createdOnNodes) {
				valueStr = createdNode.getTextTrim();
				values.add(valueStr);
			}
			if (isTooManyCreates) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("sisestajaid ja sisestusaegu on liiga palju: ");
				logBuf.append(values);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_CREATED_MODIFIED_MESS, logRow);
			}
			if (isIncompleteCreate) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("puudulik sisestaja ja sisestusaja komplekt: ");
				logBuf.append(values);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_CREATED_MODIFIED_MESS, logRow);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(".//descripGrp/descrip/xref[contains(@Tlink, 'Allikas:')]");
		for (Element sourceNode : valueNodes) {
			String tlinkAttrValue = sourceNode.attributeValue(xrefTlinkAttr);
			String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("sourceCodeOrName", sourceCodeOrName);
			List<Map<String, Object>> results = basicDbService.queryList(SQL_SELECT_SOURCE_BY_CODE_OR_NAME, tableRowParamMap);
			if (CollectionUtils.isEmpty(results)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append(sourceCodeOrName);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_MISSING_SOURCE_REFS, logRow);
			}
		}
	}

	private void detectAndReportLanguageGrp(String concept, Element langGroupNode, Count dataErrorCount) throws Exception {

		StringBuffer logBuf = new StringBuffer();

		/* 
		 * TODO put it back later
		 *
		List<Element> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Element> definitionNoteNodes = langGroupNode.selectNodes(noteExp);
		int definitionCount = definitionNodes.size();
		int definitionNoteCount = definitionNoteNodes.size();
		if (definitionCount > 1 && definitionNoteCount > 0) {
			dataErrorCount.increment();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("definitsioonid (");
			logBuf.append(definitionCount);
			logBuf.append(") ja nende märkused (");
			logBuf.append(definitionNoteCount);
			logBuf.append(") ei kohaldu");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_DEFINITIONS_NOTES_MESS, logRow);
		}
		*/

		composeDefinitionsAndNotesReports(concept, "*", langGroupNode);
	}

	private void detectAndReportTermGrp(String concept, String term, int homonymNr, String lang, Element termGroupNode, Count dataErrorCount) throws Exception {

		StringBuffer logBuf = new StringBuffer();

		List<Element> valueNodes;
		List<String> values;
		String valueStr;

		/* 
		 * TODO put it back later
		 * 
		valueNodes = termGroupNode.selectNodes(lexemeTypeExp);
		values = new ArrayList<>();
		for (Element lexemeTypeNode : valueNodes) {
			valueStr = lexemeTypeNode.getTextTrim();
			if (values.contains(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append(term);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("korduv keelenditüüp: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
				continue;
			}
			values.add(valueStr);
			if (!lexemeTypeCodes.containsKey(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append(term);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("tundmatu keelenditüüp: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
			}
		}
		if (valueNodes.size() > 1) {
			dataErrorCount.increment();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append(term);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("mitu keelenditüüpi");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
		}

		valueNodes = termGroupNode.selectNodes(definitionExp);
		if (CollectionUtils.isNotEmpty(valueNodes)) {
			dataErrorCount.increment();
			valueStr = valueNodes.get(0).getTextTrim();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append(term);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("definitsioonid on seotud termini juurde (");
			logBuf.append(valueStr);
			logBuf.append("...)");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_DEFINITIONS_AT_TERMS, logRow);
		}
		*/

		composeDefinitionsAndNotesReports(concept, term, termGroupNode);
	}

	private void composeDefinitionsAndNotesReports(String concept, String term, Element sourceNode) throws Exception {

		StringBuffer logBuf;
		String valueStr;
		List<Element> definitionNodes = sourceNode.selectNodes(definitionExp);

		if (CollectionUtils.isNotEmpty(definitionNodes)) {
			List<Element> notesSourceNodes = sourceNode.selectNodes("descripGrp/descrip[@type='Märkus']/xref[contains(@Tlink,'Allikas')]");

			List<String> definitionSourceCodes = new ArrayList<>();

			for (Element definitionNode : definitionNodes) {

				Iterator<Node> contentNodeIter = definitionNode.nodeIterator();
				DefaultText textContentNode;
				DefaultElement elemContentNode;
				String recentDefinition = null;
				boolean isMultipleDefinitions = false;
	
				while (contentNodeIter.hasNext()) {
					Node contentNode = contentNodeIter.next();
					if (contentNode instanceof DefaultText) {
						textContentNode = (DefaultText) contentNode;
						valueStr = textContentNode.getText();
						valueStr = StringUtils.trim(valueStr);
						if (StringUtils.equalsAny(valueStr, "[", "]", ";")) {
							continue;
						}
						boolean containsWord = containsWord(valueStr);
						if (containsWord) {
							if (StringUtils.isNotBlank(recentDefinition) && !isMultipleDefinitions) {
								isMultipleDefinitions = true;
								logBuf = new StringBuffer();
								logBuf.append(concept);
								logBuf.append(CSV_SEPARATOR);
								logBuf.append(term);
								logBuf.append(CSV_SEPARATOR);
								logBuf.append(definitionNode.asXML());
								String logRow = logBuf.toString();
								reportComposer.append(REPORT_MULTIPLE_DEFINITIONS, logRow);
							}
							recentDefinition = valueStr;
						} else {
							logBuf = new StringBuffer();
							logBuf.append(concept);
							logBuf.append(CSV_SEPARATOR);
							logBuf.append(term);
							logBuf.append(CSV_SEPARATOR);
							logBuf.append(definitionNode.asXML());
							String logRow = logBuf.toString();
							reportComposer.append(REPORT_NOT_A_DEFINITION, logRow);
						}
					} else if (contentNode instanceof DefaultElement) {
						elemContentNode = (DefaultElement) contentNode;
						valueStr = elemContentNode.getTextTrim();
						if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
							String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
							if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
								String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
								definitionSourceCodes.add(sourceCodeOrName);
							}
						}
					}
				}
			}

			for (Element noteSourceNode : notesSourceNodes) {
				String tlinkAttrValue = noteSourceNode.attributeValue(xrefTlinkAttr);
				String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
				if (definitionSourceCodes.contains(sourceCodeOrName)) {
					int sourceCodeOccCount = Collections.frequency(definitionSourceCodes, sourceCodeOrName);
					if (sourceCodeOccCount > 1) {
						logBuf = new StringBuffer();
						logBuf.append(concept);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(term);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(sourceCodeOrName);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("mitu selle märkuse allikaviitega seletust");
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_DEFINITIONS_NOTES_MISMATCH, logRow);
					}
				} else {
					logBuf = new StringBuffer();
					logBuf.append(concept);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(term);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(sourceCodeOrName);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("pole selle märkuse allikaviitega seletust");
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_DEFINITIONS_NOTES_MISMATCH, logRow);
				}
			}
		}
	}

	private boolean containsWord(String value) {

		if (StringUtils.length(value) == 1) {
			return false;
		}
		char[] chars = value.toCharArray();
		for (char ch : chars) {
			if (Character.isAlphabetic(ch)) {
				return true;
			}
		}
		return false;
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
				valueStr = handleRefLinks(noteValueNode, ReferenceOwner.FREEFORM, freeformId);
				updateFreeformText(freeformId, valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(privateNoteExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			Long freeformId = createMeaningFreeform(meaningId, FreeformType.PRIVATE_NOTE, valueStr);
			if (valueNode.hasMixedContent()) {
				valueStr = handleRefLinks(valueNode, ReferenceOwner.FREEFORM, freeformId);
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

	//TODO temporary solution, to be refactored
	private void extractAndSaveDefinitionsAndFreeforms(Long meaningId, Element langGroupNode, String lang, String dataset, Count definitionsWithSameNoteCount) throws Exception {

		List<Element> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Element> definitionNoteNodes = langGroupNode.selectNodes(noteExp);
		List<Long> definitionIds;
		for (Element definitionNode : definitionNodes) {
			/*
			definitionIds = new ArrayList<>();
			extractAndSaveDefinitionsAndRefLinks(definitionNode, definitionIds, meaningId, lang, dataset);
			if (definitionIds.size() > 1 && definitionNoteNodes.size() > 0) {
				definitionsWithSameNoteCount.increment();
			}
			*/
			String definition = definitionNode.getTextTrim();
			Long definitionId = createDefinition(meaningId, definition, lang, dataset);
			if (definitionNode.hasMixedContent()) {
				definition = handleRefLinks(definitionNode, ReferenceOwner.DEFINITION, definitionId);
				updateDefinitionValue(definitionId, definition);
			}
			for (Element definitionNoteNode : definitionNoteNodes) {
				String definitionNote = definitionNoteNode.getTextTrim();
				Long freeformId = createDefinitionFreeform(definitionId, FreeformType.PUBLIC_NOTE, definitionNote);
				if (definitionNoteNode.hasMixedContent()) {
					definitionNote = handleRefLinks(definitionNoteNode, ReferenceOwner.FREEFORM, freeformId);
					updateFreeformText(freeformId, definitionNote);
				}
			}
		}
	}

	//TODO temporary solution, to be refactored
	private void extractAndSaveDefinitionsAndRefLinks(Element definitionNode, List<Long> definitionIds, Long meaningId, String lang, String dataset) throws Exception {

		Iterator<Node> contentNodeIter = definitionNode.nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;
		Long definitionId = null;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				valueStr = StringUtils.trim(valueStr);
				definitionId = createDefinition(meaningId, valueStr, lang, dataset);
				definitionIds.add(definitionId);
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
						Long sourceId = getSource(sourceCodeOrName);
						if (sourceId != null) {
							if (definitionId == null) {
								definitionId = createDefinition(meaningId, valueStr, lang, dataset);
								definitionIds.add(definitionId);
							}
							createDefinitionRefLink(definitionId, ReferenceType.SOURCE, sourceId);
						}
					} else {
						//TODO not sure about this. should probably just save as definition instead
						//throw new DataLoadingException("Unsupported tlink node type: " + tlinkAttrValue);
						logger.debug("Unsupported tlink node type {}", tlinkAttrValue);
						definitionId = createDefinition(meaningId, valueStr, lang, dataset);
						definitionIds.add(definitionId);
					}
				} else {
					throw new DataLoadingException("Unsupported mixed content node name: " + contentNode.getName());
				}
			} else {
				throw new DataLoadingException("Unsupported mixed content node type: " + contentNode.getClass());
			}
		}
	}

	//TODO temporary solution, to be refactored
	private void extractAndSaveUsagesAndRefLinks(Element usageNode, Long usageMeaningId, String lang, String dataset) throws Exception {

		Iterator<Node> contentNodeIter = usageNode.nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;
		Long usageId = null;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				valueStr = StringUtils.trim(valueStr);
				usageId = createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, valueStr, lang);
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						String sourceCodeOrName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
						Long sourceId = getSource(sourceCodeOrName);
						if (sourceId != null) {
							if (usageId == null) {
								usageId = createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, valueStr, lang);
							}
							createFreeformRefLink(usageId, ReferenceType.SOURCE, sourceId);
						}
					} else {
						//TODO not sure about this. should probably just save as definition instead
						//throw new DataLoadingException("Unsupported tlink node type: " + tlinkAttrValue);
						logger.debug("Unsupported tlink node type {} ", tlinkAttrValue);
						usageId = createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, valueStr, lang);
					}
				} else {
					throw new DataLoadingException("Unsupported mixed content node name: " + contentNode.getName());
				}
			} else {
				throw new DataLoadingException("Unsupported mixed content node type: " + contentNode.getClass());
			}
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
				valueStr = handleRefLinks(valueNode, ReferenceOwner.FREEFORM, freeformId);
				updateFreeformText(freeformId, valueStr);
			}
		}
	}

	private void saveDomains(List<Element> domainNodes, Long meaningId, String domainOrigin) throws Exception {

		if (domainNodes == null) {
			return;
		}
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("origin", domainOrigin);

		String domainCode;
		Map<String, Object> tableRowValueMap;
		boolean domainExists;

		List<String> domainCodes = new ArrayList<>();

		for (Element domainNode : domainNodes) {
			domainCode = domainNode.getTextTrim();
			if (domainCodes.contains(domainCode)) {
				logger.warn("Domain reference duplicate: \"{}\"", domainCode);
				continue;
			}
			domainCodes.add(domainCode);
			tableRowParamMap.put("code", domainCode);
			tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
			domainExists = ((Long) tableRowValueMap.get("cnt")) > 0;
			if (domainExists) {
				createMeaningDomain(meaningId, domainCode, domainOrigin);
			} else {
				logger.warn("Incorrect domain reference: \"{}\"", domainCode);
			}
		}
	}

	private String handleRefLinks(Element mixedContentNode, ReferenceOwner owner, Long ownerId) throws Exception {

		Iterator<Node> contentNodeIter = mixedContentNode.nodeIterator();
		StringBuffer contentBuf = new StringBuffer();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;

		String refLinkKey = null;
		if (ReferenceOwner.FREEFORM.equals(owner)) {
			refLinkKey = ContentKey.FREEFORM_REF_LINK;
		} else if (ReferenceOwner.DEFINITION.equals(owner)) {
			refLinkKey = ContentKey.DEFINITION_REF_LINK;
		}

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
							Long refLinkId = null;
							if (ReferenceOwner.FREEFORM.equals(owner)) {
								refLinkId = createFreeformRefLink(ownerId, ReferenceType.SOURCE, sourceId);
							} else if (ReferenceOwner.DEFINITION.equals(owner)) {
								refLinkId = createDefinitionRefLink(ownerId, ReferenceType.SOURCE, sourceId);
							}
							//simulating markdown link syntax
							contentBuf.append("[");
							contentBuf.append(valueStr);
							contentBuf.append("]");
							contentBuf.append("(");
							contentBuf.append(refLinkKey);
							contentBuf.append(":");
							contentBuf.append(refLinkId);
							contentBuf.append(")");
						}
					} else {
						// TODO other types of links to be implemented
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

}
