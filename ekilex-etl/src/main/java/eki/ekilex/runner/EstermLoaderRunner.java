package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
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

	@Override
	void initialise() throws Exception {

		defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
		ltbDateFormat = new SimpleDateFormat(LTB_TIMESTAMP_PATTERN);
		reviewDateFormat = new SimpleDateFormat(REVIEW_TIMESTAMP_PATTERN);
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String dataset) throws Exception {

		logger.debug("Starting loading Esterm...");

		final String conceptGroupExp = "/mtf/conceptGrp";
		final String langGroupExp = "languageGrp";
		final String langExp = "language";
		final String termGroupExp = "termGrp";
		final String termExp = "term";
		final String domainExp = "descripGrp/descrip[@type='Valdkonnaviide']/xref";
		final String usageExp = "descripGrp/descrip[@type='Kontekst']";
		final String definitionExp = "descripGrp/descrip[@type='Definitsioon']";
		final String lexemeTypeExp = "descripGrp/descrip[@type='Keelenditüüp']";

		final String langTypeAttr = "type";

		final String domainOrigin = "lenoch";
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
		List<Element> langGroupNodes, termGroupNodes, domainNodes;
		Long wordId, meaningId, lexemeId, rectionId;
		String valueStr;
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

			// meaning
			meaningObj = new Meaning();
			extractAndApplyMeaningProperties(conceptGroupNode, meaningObj, dataErrorCount);
			meaningId = createMeaning(meaningObj, dataset);
			extractAndSaveMeaningFreeforms(meaningId, conceptGroupNode);

			domainNodes = conceptGroupNode.selectNodes(domainExp);
			saveDomains(conceptGroupNode, domainNodes, meaningId, domainOrigin, dataErrorCount);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Element langGroupNode : langGroupNodes) {

				valueNode = (Element) langGroupNode.selectSingleNode(langExp);
				valueStr = valueNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(valueStr);

				if (!isLang) {
					continue;
				}

				lang = unifyLang(valueStr);

				//TODO
				langGroupNode.selectNodes(definitionExp);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					valueStr = valueNode.getTextTrim();

					homonymNr = getWordMaxHomonymNr(valueStr, dataLang);
					homonymNr++;
					wordId = saveWord(valueStr, null, null, null, homonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);

					//lexeme
					lexemeObj = new Lexeme();
					extractAndApplyLexemeProperties(termGroupNode, lexemeObj, dataErrorCount);
					lexemeId = createLexeme(lexemeObj, dataset);

					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						extractAndSaveLexemeFreeforms(lexemeId, termGroupNode);

						//FIXME multiple
						valueNode = (Element) termGroupNode.selectSingleNode(usageExp);
						if (valueNode != null) {
							if (valueNode.hasMixedContent()) {
								//TODO get source
							}
							valueStr = valueNode.getTextTrim();
							rectionId = createOrSelectRection(lexemeId, defaultRectionValue);
							createUsage(rectionId, valueStr);
						}
						//FIXME multiple
						valueNode = (Element) termGroupNode.selectSingleNode(definitionExp);
						if (valueNode != null) {
							if (valueNode.hasMixedContent()) {
								//TODO get source
							}
							valueStr = valueNode.getTextTrim();
							createDefinition(meaningId, valueStr, lang, dataset);
						}
						//FIXME multiple
						valueNode = (Element) termGroupNode.selectSingleNode(lexemeTypeExp);
						if (valueNode != null) {
							valueStr = valueNode.getTextTrim();
							if (lexemeTypeCodes.contains(valueStr)) {
								updateLexemeType(lexemeId, valueStr);
							} else {
								dataErrorCount.increment();
								logger.warn("Incorrect lexeme type reference: \"{}\"", valueStr);
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

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private List<String> getClassifierCodes(String queryStr) throws Exception {

		Map<String, String> resultMap = basicDbService.queryListAsMap(queryStr, new HashMap<>());
		List<String> codes = new ArrayList<>(resultMap.keySet());
		return codes;
	}

	private void extractAndApplyMeaningProperties(Element conceptGroupNode, Meaning meaningObj, Count dataErrorCount) throws Exception {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;
	
		valueNode = (Element) conceptGroupNode.selectSingleNode("system[@type='entryClass']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setEntryClassCode(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Staatus']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningStateCodes.contains(valueStr)) {
				meaningObj.setMeaningStateCode(valueStr);
			} else {
				dataErrorCount.increment();
				logger.warn("Incorrect meaning state reference: \"{}\"", valueStr);
				//TODO should fix at source!!
				String replaceableMeaningStateCode = "komisjonis arutusel";
				String meaningStateCodeReplacement = "komisjonis arutlusel";
				if (StringUtils.equals(replaceableMeaningStateCode, valueStr)) {
					logger.debug("Assuming \"{}\" is actually \"{}\"", valueStr, meaningStateCodeReplacement);
					meaningObj.setMeaningStateCode(meaningStateCodeReplacement);
				}
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Mõistetüüp']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (meaningTypeCodes.contains(valueStr)) {
				meaningObj.setMeaningTypeCode(valueStr);
			} else {
				dataErrorCount.increment();
				logger.warn("Incorrect meaning type reference: \"{}\"", valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("transacGrp/transac[@type='origination']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setCreatedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("transacGrp[transac/@type='origination']/date");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setCreatedOn(valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("transacGrp/transac[@type='modification']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setModifiedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("transacGrp[transac/@type='modification']/date");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = defaultDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setModifiedOn(valueTs);
		}
	}

	private void extractAndSaveMeaningFreeforms(Long meaningId, Element conceptGroupNode) throws Exception {

		Element valueNode;
		String valueStr;
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

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisestaja']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_CREATED_BY, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisestusaeg']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = ltbDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			createMeaningFreeform(meaningId, FreeformType.LTB_CREATED_ON, valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutja']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_MODIFIED_BY, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutmisaeg']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = ltbDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			createMeaningFreeform(meaningId, FreeformType.LTB_MODIFIED_ON, valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Päritolu']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.LTB_SOURCE, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Märkus']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
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
			createMeaningFreeform(meaningId, FreeformType.IDENTIFIER, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='et-en kontrollija']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.ET_EN_REVIEWED_BY, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='et-en kontrollitud']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = reviewDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			createMeaningFreeform(meaningId, FreeformType.ET_EN_REVIEWED_ON, valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='en-et kontrollija']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.EN_ET_REVIEWED_BY, valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='en-et kontrollitud']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = reviewDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			createMeaningFreeform(meaningId, FreeformType.EN_ET_REVIEWED_ON, valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Tööleht']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.WORKSHEET, valueStr);
		}
	}

	//TODO impl
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

	//TODO impl
	private void extractAndSaveLexemeFreeforms(Long lexemeId, Element termGroupNode) throws Exception {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Autor']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.EÕKK_CREATED_BY, valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Sisestusaeg']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = ltbDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			createLexemeFreeform(lexemeId, FreeformType.EÕKK_CREATED_ON, valueTs);
		}

		valueNode = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutja']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.EÕKK_MODIFIED_BY, valueStr);
		}

		valueNode = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Muutmisaeg']");
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = ltbDateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			createLexemeFreeform(lexemeId, FreeformType.EÕKK_MODIFIED_ON, valueTs);
		}
	}

	private void saveDomains(Element parentNode, List<Element> domainNodes, Long meaningId, String domainOrigin, Count dataErrorCount) throws Exception {

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
				//logger.warn(parentNode.asXML());
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
		basicDbService.update(SQL_UPDATE_LEXEME_TYPE, tableRowParamMap);
	}
}
