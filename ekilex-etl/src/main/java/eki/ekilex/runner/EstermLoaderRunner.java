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

@Component
public class EstermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String LTB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String REVIEW_TIMESTAMP_PATTERN = "yy/MM/dd";

	private static final String SQL_SELECT_MEANING_STATES = "select code \"key\", code \"value\" from " + MEANING_STATE;

	private static final String SQL_SELECT_MEANING_TYPES = "select code \"key\", code \"value\" from " + MEANING_TYPE;

	private static final String SQL_SELECT_LEXEME_TYPES = "select code \"key\", code \"value\" from " + LEXEME_TYPE;

	private static final String SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";

	//FIXME mitu?
	private static final String SQL_UPDATE_LEXEME_TYPE = "update " + LEXEME + " set type = :lexemeType where id = :lexemeId";

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
	//TODO move others too

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
	public void execute(String dataXmlFilePath, String dataLang, String dataset) throws Exception {

		logger.debug("Starting loading Esterm...");

		final String langTypeAttr = "type";
		final String originLenoch = "lenoch";
		final String originLtb = "ltb";
		final String defaultRectionValue = "-";
		final String defaultWordMorphCode = "SgN";

		long t1, t2;
		t1 = System.currentTimeMillis();

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
		String valueStr;
		String lang;
		int homonymNr;
		Meaning meaningObj;
		Lexeme lexemeObj;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count dataErrorCount = new Count();
		Count unmatchingDefinitionsAndNotesCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Element conceptGroupNode : conceptGroupNodes) {

			// meaning
			meaningObj = new Meaning();
			extractAndApplyMeaningProperties(conceptGroupNode, meaningObj, dataErrorCount);
			meaningId = createMeaning(meaningObj, dataset);
			extractAndSaveMeaningFreeforms(meaningId, conceptGroupNode);

			domainNodes = conceptGroupNode.selectNodes(domainExp);
			saveDomains(domainNodes, meaningId, originLenoch, dataErrorCount);
			domainNodes = conceptGroupNode.selectNodes(subdomainExp);
			saveDomains(domainNodes, meaningId, originLtb, dataErrorCount);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Element langGroupNode : langGroupNodes) {

				valueNode = (Element) langGroupNode.selectSingleNode(langExp);
				valueStr = valueNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(valueStr);

				if (!isLang) {
					continue;
				}

				lang = unifyLang(valueStr);

				extractAndSaveDefinitionsAndFreeforms(meaningId, langGroupNode, lang, dataset, unmatchingDefinitionsAndNotesCount);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					valueStr = valueNode.getTextTrim();

					homonymNr = getWordMaxHomonymNr(valueStr, dataLang);
					homonymNr++;
					wordId = saveWord(valueStr, null, null, null, homonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);

					//lexeme
					lexemeObj = new Lexeme();
					lexemeObj.setWordId(wordId);
					lexemeObj.setMeaningId(meaningId);
					extractAndApplyLexemeProperties(termGroupNode, lexemeObj, dataErrorCount);
					lexemeId = createLexeme(lexemeObj, dataset);

					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);

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

						valueNode = (Element) termGroupNode.selectSingleNode(lexemeTypeExp);
						if (valueNode != null) {
							valueStr = valueNode.getTextTrim();
							if (lexemeTypeCodes.contains(valueStr)) {
								updateLexemeType(lexemeId, valueStr);
							} else {
								dataErrorCount.increment();
								logger.warn("Incorrect lexeme type reference: \"{}\"", valueStr);
								//TODO should fix at source!!
								String replacingClassifierCode = classifierCorrectionMap.get(valueStr);
								if (StringUtils.isNotBlank(replacingClassifierCode)) {
									logger.debug("Assuming \"{}\" is actually \"{}\"", valueStr, replacingClassifierCode);
									updateLexemeType(lexemeId, replacingClassifierCode);
								}
							}
						}
					}
				}
			}

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);
		logger.debug("Found {} data errors", dataErrorCount);
		logger.debug("Found {} unmatching definitions and notes", unmatchingDefinitionsAndNotesCount);

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private List<String> getClassifierCodes(String queryStr) throws Exception {

		Map<String, String> resultMap = basicDbService.queryListAsMap(queryStr, new HashMap<>());
		List<String> codes = new ArrayList<>(resultMap.keySet());
		return codes;
	}

	//TODO add to lifecycle log
	private void extractAndApplyMeaningProperties(Element conceptGroupNode, Meaning meaningObj, Count dataErrorCount) throws Exception {

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
				dataErrorCount.increment();
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
				dataErrorCount.increment();
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

		valueNode = (Element) conceptGroupNode.selectSingleNode("concept");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.CONCEPT_ID, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='ID-number']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_ID, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Päritolu']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_SOURCE, valueStr);
		}

		valueNodes = conceptGroupNode.selectNodes("descripGrp/descrip[@type='Märkus']");
		for (Element noteValueNode : valueNodes) {
			valueStr = noteValueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisemärkus']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.PRIVATE_NOTE, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Tunnus']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.UNCLASSIFIED, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Tööleht']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.WORKSHEET, valueStr);
		}

		valueNode1 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisestaja']");
		valueNode2 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisestusaeg']");
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

		valueNode1 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutja']");
		valueNode2 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutmisaeg']");
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

		valueNode1 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='et-en kontrollija']");
		valueNode2 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='et-en kontrollitud']");
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

		valueNode1 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='en-et kontrollija']");
		valueNode2 = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='en-et kontrollitud']");
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

	private void extractAndSaveDefinitionsAndFreeforms(Long meaningId, Element langGroupNode, String lang, String dataset, Count unmatchingDefinitionsAndNotesCount) throws Exception {

		List<Element> definitionNodes = langGroupNode.selectNodes("descripGrp/descrip[@type='Definitsioon']");
		List<Element> definitionNoteNodes = langGroupNode.selectNodes("descripGrp/descrip[@type='Märkus']");
		int definitionCount = 0;
		for (Element definitionNode : definitionNodes) {
			String definition = definitionNode.getTextTrim();
			Long definitionId = createDefinition(meaningId, definition, lang, dataset);
			for (Element definitionNoteNode : definitionNoteNodes) {
				String definitionNote = definitionNoteNode.getTextTrim();
				createDefinitionFreeform(definitionId, FreeformType.PUBLIC_NOTE, definitionNote);
			}
			definitionCount++;
		}
		if (definitionCount > 1 && definitionNoteNodes.size() > 0) {
			unmatchingDefinitionsAndNotesCount.increment();
		}
	}

	//TODO add to lifecycle log
	private void extractAndApplyLexemeProperties(Element termGroupNode, Lexeme lexemeObj, Count dataErrorCount) throws Exception {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) termGroupNode.selectSingleNode("transacGrp/transac[@type='origination']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			lexemeObj.setCreatedBy(valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode("transacGrp[transac/@type='origination']/date");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			lexemeObj.setCreatedOn(valueTs);
		}

		valueNode = (Element) termGroupNode.selectSingleNode("transacGrp/transac[@type='modification']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			lexemeObj.setModifiedBy(valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode("transacGrp[transac/@type='modification']/date");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			lexemeObj.setModifiedOn(valueTs);
		}
	}

	//TODO add error log
	private void extractAndSaveLexemeFreeforms(Long lexemeId, Element termGroupNode) throws Exception {

		List<Element> valueNodes;
		Element valueNode1, valueNode2;
		String valueStr, valueStr1, valueStr2;
		long valueLong;
		Timestamp valueTs;

		valueNode1 = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Autor']");
		valueNode2 = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisestusaeg']");
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

		valueNode1 = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutja']");
		valueNode2 = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutmisaeg']");
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

		valueNodes = termGroupNode.selectNodes("descripGrp/descrip[@type='Märkus']");
		for (Element valueNode : valueNodes) {
			if (valueNode.hasMixedContent()) {
				//TODO get link
			}
			valueStr = valueNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, valueStr);
		}
	}

	private void saveDomains(List<Element> domainNodes, Long meaningId, String domainOrigin, Count dataErrorCount) throws Exception {

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
				logger.warn("Duplicate bind entry for domain code \"{}\"", domainCode);
				dataErrorCount.increment();
				continue;
			}
			domainCodes.add(domainCode);
			tableRowParamMap.put("code", domainCode);
			tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
			domainExists = ((Long) tableRowValueMap.get("cnt")) > 0;
			if (domainExists) {
				createMeaningDomain(meaningId, domainCode, domainOrigin);
			} else {
				dataErrorCount.increment();
				logger.warn("Incorrect domain reference: \"{}\"", domainCode);
			}
		}
	}

	private void updateLexemeType(Long lexemeId, String lexemeType) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", lexemeType);
		tableRowParamMap.put("lexemeId", lexemeId);
		tableRowParamMap.put("lexemeType", lexemeType);
		basicDbService.executeScript(SQL_UPDATE_LEXEME_TYPE, tableRowParamMap);
	}
}
