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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleLogType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.service.ReportComposer;

@Component
public class EstermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String REPORT_DEFINITIONS_NOTES_MESS = "definitions_notes_mess";

	private static final String REPORT_CREATED_MODIFIED_MESS = "created_modified_mess";

	private static final String REPORT_DUPLICATE_WORDS = "duplicate_words";

	private static final String REPORT_ILLEGAL_CLASSIFIERS = "illegal_classifiers";

	private static final String REPORT_DEFINITIONS_AT_TERMS = "definitions_at_terms";

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String LTB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String REVIEW_TIMESTAMP_PATTERN = "yy/MM/dd";

	private static final String SQL_SELECT_MEANING_STATES = "select code \"key\", code \"value\" from " + MEANING_STATE;

	private static final String SQL_SELECT_MEANING_TYPES = "select code \"key\", code \"value\" from " + MEANING_TYPE;

	private static final String SQL_SELECT_LEXEME_TYPES = "select code \"key\", code \"value\" from " + LEXEME_TYPE;

	private static final String SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";

	private ReportComposer reportComposer;

	private DateFormat defaultDateFormat;

	private DateFormat ltbDateFormat;

	private DateFormat reviewDateFormat;

	private List<String> meaningStateCodes;

	private List<String> meaningTypeCodes;

	private List<String> lexemeTypeCodes;

	private Map<String, String> classifierCorrectionMap;

	private final String conceptGroupExp = "/mtf/conceptGrp";
	private final String langGroupExp = "languageGrp";
	private final String langExp = "language";
	private final String termGroupExp = "termGrp";
	private final String termExp = "term";
	private final String conceptExp = "concept";
	private final String domainExp = "descripGrp/descrip[@type='Valdkonnaviide']/xref";
	private final String subdomainExp = "descripGrp/descrip[@type='Alamvaldkond']";
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

	private final String originLenoch = "lenoch";
	private final String originLtb = "ltb";

	@Override
	void initialise() throws Exception {

		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
		ltbDateFormat = new SimpleDateFormat(LTB_TIMESTAMP_PATTERN);
		reviewDateFormat = new SimpleDateFormat(REVIEW_TIMESTAMP_PATTERN);

		classifierCorrectionMap = new HashMap<>();
		classifierCorrectionMap.put("komisjonis arutusel", "komisjonis arutlusel");
		classifierCorrectionMap.put("organisatsioon|asutus", "organisatsioon, asutus");
		classifierCorrectionMap.put("lühend|sünonüüm", "lühend ja sünonüüm");
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String dataset, boolean doReports) throws Exception {

		logger.debug("Starting loading Esterm...");

		final String langTypeAttr = "type";
		final String defaultRectionValue = "-";
		final String defaultWordMorphCode = "SgN";

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("esterm load report",
					REPORT_DEFINITIONS_NOTES_MESS, REPORT_CREATED_MODIFIED_MESS, REPORT_DUPLICATE_WORDS,
					REPORT_ILLEGAL_CLASSIFIERS, REPORT_DEFINITIONS_AT_TERMS);
		}

		meaningStateCodes = getClassifierCodes(SQL_SELECT_MEANING_STATES);
		meaningTypeCodes = getClassifierCodes(SQL_SELECT_MEANING_TYPES);
		lexemeTypeCodes = getClassifierCodes(SQL_SELECT_LEXEME_TYPES);

		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		Element valueNode;
		List<Element> valueNodes, langGroupNodes, termGroupNodes, domainNodes;
		Long wordId, meaningId, lexemeId, rectionId;
		String valueStr, concept, term;
		String lang;
		int homonymNr;
		Meaning meaningObj;
		Lexeme lexemeObj;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count dataErrorCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Element conceptGroupNode : conceptGroupNodes) {

			valueNode = (Element) conceptGroupNode.selectSingleNode(conceptExp);
			concept = valueNode.getTextTrim();

			// meaning
			meaningObj = new Meaning();
			extractAndApplyMeaningProperties(conceptGroupNode, meaningObj);
			meaningId = createMeaning(meaningObj, dataset);
			extractAndSaveMeaningFreeforms(meaningId, conceptGroupNode);

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

				extractAndSaveDefinitionsAndFreeforms(meaningId, langGroupNode, lang, dataset);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					term = valueNode.getTextTrim();

					homonymNr = getWordMaxHomonymNr(term, dataLang);
					homonymNr++;
					int tempWordDuplicateCount = wordDuplicateCount.getValue();
					wordId = saveWord(term, null, null, null, homonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);
					boolean isDuplicateWord = tempWordDuplicateCount != wordDuplicateCount.getValue();

					//lexeme
					lexemeObj = new Lexeme();
					lexemeObj.setWordId(wordId);
					lexemeObj.setMeaningId(meaningId);
					lexemeId = createLexeme(lexemeObj, dataset);

					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {

						extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);

						extractAndUpdateLexemeProperties(lexemeId, termGroupNode);

						valueNodes = termGroupNode.selectNodes(definitionExp);
						for (Element definitionNode : valueNodes) {
							String definition = definitionNode.getTextTrim();
							createDefinition(meaningId, definition, lang, dataset);
						}

						valueNodes = termGroupNode.selectNodes(usageExp);
						if (CollectionUtils.isNotEmpty(valueNodes)) {
							rectionId = createOrSelectRection(lexemeId, defaultRectionValue);
							for (Element usageNode : valueNodes) {
								if (usageNode.hasMixedContent()) {
									//TODO get source
								}
								valueStr = usageNode.getTextTrim();
								createUsage(rectionId, valueStr);
							}
						}
					}

					if (doReports) {
						detectAndReportTermGrp(concept, term, homonymNr, lang, isDuplicateWord, termGroupNode, dataErrorCount);
					}
				}

				if (doReports) {
					detectAndReportLanguageGrp(concept, langGroupNode, dataErrorCount);
				}
			}

			if (doReports) {
				detectAndReportConceptGrp(concept, conceptGroupNode, dataErrorCount);
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

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);
		if (doReports) {
			logger.debug("Found {} data errors", dataErrorCount);
		}

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
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
			if (!meaningStateCodes.contains(valueStr)) {
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
			if (!meaningTypeCodes.contains(valueStr)) {
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
	}

	private void detectAndReportLanguageGrp(String concept, Element langGroupNode, Count dataErrorCount) throws Exception {

		StringBuffer logBuf = new StringBuffer();

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
	}

	private void detectAndReportTermGrp(String concept, String term, int homonymNr, String lang, boolean isDuplicateWord, Element termGroupNode, Count dataErrorCount) throws Exception {

		StringBuffer logBuf = new StringBuffer();

		List<Element> valueNodes;
		List<String> values;
		String valueStr;

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
			if (!lexemeTypeCodes.contains(valueStr)) {
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

		if (isDuplicateWord) {
			dataErrorCount.increment();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append(term);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("korduv termin");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_DUPLICATE_WORDS, logRow);
		}
	}

	private List<String> getClassifierCodes(String queryStr) throws Exception {

		Map<String, String> resultMap = basicDbService.queryListAsMap(queryStr, new HashMap<>());
		List<String> codes = new ArrayList<>(resultMap.keySet());
		return codes;
	}

	//TODO add to lifecycle log
	private void extractAndApplyMeaningProperties(Element conceptGroupNode, Meaning meaningObj) throws Exception {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(entryClassExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setEntryClassCode(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningStateCodes.contains(valueStr)) {
				meaningObj.setMeaningStateCode(valueStr);
			} else {
				logger.warn("Incorrect meaning state reference: \"{}\"", valueStr);
				//TODO should fix at source!!
				String replacingClassifierCode = classifierCorrectionMap.get(valueStr);
				if (StringUtils.isNotBlank(replacingClassifierCode)) {
					logger.debug("Assuming \"{}\" is actually \"{}\"", valueStr, replacingClassifierCode);
					meaningObj.setMeaningStateCode(replacingClassifierCode);
				}
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(meaningTypeExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningTypeCodes.contains(valueStr)) {
				meaningObj.setMeaningTypeCode(valueStr);
			} else {
				logger.warn("Incorrect meaning type reference: \"{}\"", valueStr);
				//TODO should fix at source!!
				String replacingClassifierCode = classifierCorrectionMap.get(valueStr);
				if (StringUtils.isNotBlank(replacingClassifierCode)) {
					logger.debug("Assuming \"{}\" is actually \"{}\"", valueStr, replacingClassifierCode);
					meaningObj.setMeaningTypeCode(replacingClassifierCode);
				}
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

	//TODO add error log
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
			createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(privateNoteExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.PRIVATE_NOTE, valueStr);
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
		createLifecycleLog(meaningId, MEANING, LifecycleLogType.LTB_CREATED, valueStr1, valueTs);

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
		createLifecycleLog(meaningId, MEANING, LifecycleLogType.LTB_MODIFIED, valueStr1, valueTs);

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
		createLifecycleLog(meaningId, MEANING, LifecycleLogType.ET_EN_REVIEWED, valueStr1, valueTs);

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
		createLifecycleLog(meaningId, MEANING, LifecycleLogType.EN_ET_REVIEWED, valueStr1, valueTs);
	}

	private void extractAndSaveDefinitionsAndFreeforms(Long meaningId, Element langGroupNode, String lang, String dataset) throws Exception {

		List<Element> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Element> definitionNoteNodes = langGroupNode.selectNodes(noteExp);
		for (Element definitionNode : definitionNodes) {
			String definition = definitionNode.getTextTrim();
			Long definitionId = createDefinition(meaningId, definition, lang, dataset);
			for (Element definitionNoteNode : definitionNoteNodes) {
				String definitionNote = definitionNoteNode.getTextTrim();
				createDefinitionFreeform(definitionId, FreeformType.PUBLIC_NOTE, definitionNote);
			}
		}
	}

	//TODO lifecycle log
	private void extractAndUpdateLexemeProperties(Long lexemeId, Element termGroupNode) throws Exception {

		Element valueNode;
		String valueStr;
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
			if (lexemeTypeCodes.contains(valueStr)) {
				valueParamMap.put("type", valueStr);
			} else {
				logger.warn("Incorrect lexeme type reference: \"{}\"", valueStr);
				//TODO should fix at source!!
				String replacingClassifierCode = classifierCorrectionMap.get(valueStr);
				if (StringUtils.isNotBlank(replacingClassifierCode)) {
					logger.debug("Assuming \"{}\" is actually \"{}\"", valueStr, replacingClassifierCode);
					valueParamMap.put("type", replacingClassifierCode);
				}
			}
		}

		if (MapUtils.isNotEmpty(valueParamMap)) {
			basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
		}
	}

	//TODO add error log
	private void extractAndSaveLexemeFreeforms(Long lexemeId, Element termGroupNode) throws Exception {

		List<Element> valueNodes;
		Element valueNode1, valueNode2;
		String valueStr, valueStr1, valueStr2;
		long valueLong;
		Timestamp valueTs;

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
		createLifecycleLog(lexemeId, LEXEME, LifecycleLogType.EÕKK_CREATED, valueStr1, valueTs);

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
		createLifecycleLog(lexemeId, LEXEME, LifecycleLogType.EÕKK_MODIFIED, valueStr1, valueTs);

		valueNodes = termGroupNode.selectNodes(noteExp);
		for (Element valueNode : valueNodes) {
			if (valueNode.hasMixedContent()) {
				//TODO get link
			}
			valueStr = valueNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, valueStr);
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
}
