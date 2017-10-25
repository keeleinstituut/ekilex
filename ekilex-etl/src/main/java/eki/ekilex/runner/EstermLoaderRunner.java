package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Meaning;

@Component
public class EstermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String LTB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";

	private static final String SQL_SELECT_COUNT_LEXEME_TYPE_BY_CODE = "select count(code) cnt from " + LEXEME_TYPE + " where code = :code";

	private static final String SQL_UPDATE_LEXEME_TYPE = "update " + LEXEME + " set type = :lexemeType where id = :lexemeId";

	@Override
	void initialise() throws Exception {
		//Nothing...
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

		DateFormat defaultDateFormat = new SimpleDateFormat(DEFAULT_TIMESTAMP_PATTERN);
		DateFormat ltbDateFormat = new SimpleDateFormat(LTB_TIMESTAMP_PATTERN);
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
		long valueLong;
		Timestamp valueTs;
		int homonymNr;
		Meaning meaningObj;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count dataErrorCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Element conceptGroupNode : conceptGroupNodes) {

			meaningObj = new Meaning();

			valueNode = (Element) conceptGroupNode.selectSingleNode("system[@type='entryClass']");
			if (valueNode != null) {
				valueStr = valueNode.getTextTrim();
				meaningObj.setEntryClassCode(valueStr);
			}

			valueNode = (Element) conceptGroupNode.selectSingleNode("descripGrp/descrip[@type='Staatus']");
			if (valueNode != null) {
				valueStr = valueNode.getTextTrim();
				meaningObj.setMeaningStateCode(valueStr);
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

			// meaning
			meaningId = createMeaning(meaningObj, dataset);

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

			//...

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

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					valueStr = valueNode.getTextTrim();

					homonymNr = getWordMaxHomonymNr(valueStr, dataLang);
					homonymNr++;
					wordId = saveWord(valueStr, null, null, null, homonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);
					lexemeId = createLexeme(wordId, meaningId, null, null, null, dataset);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						valueNode = (Element) termGroupNode.selectSingleNode(usageExp);
						if (valueNode != null) {
							if (valueNode.hasMixedContent()) {
								//TODO get source
							}
							valueStr = valueNode.getTextTrim();
							rectionId = createOrSelectRection(lexemeId, defaultRectionValue);
							createUsage(rectionId, valueStr);
						}
						valueNode = (Element) termGroupNode.selectSingleNode(definitionExp);
						if (valueNode != null) {
							if (valueNode.hasMixedContent()) {
								//TODO get source
							}
							valueStr = valueNode.getTextTrim();
							createDefinition(meaningId, valueStr, lang, dataset);
						}
						valueNode = (Element) termGroupNode.selectSingleNode(lexemeTypeExp);
						if (valueNode != null) {
							valueStr = valueNode.getTextTrim();
							updateLexemeType(lexemeId, valueStr, dataErrorCount);
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
				logger.warn("Unable to bind domain code \"{}\"", domainCode);
			}
		}
	}

	private void updateLexemeType(Long lexemeId, String lexemeType, Count dataErrorCount) throws Exception {

		Map<String, Object> tableRowParamMap;
		Map<String, Object> tableRowValueMap;
		boolean lexemeTypeExists;

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", lexemeType);
		tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_LEXEME_TYPE_BY_CODE, tableRowParamMap);
		lexemeTypeExists = ((Long) tableRowValueMap.get("cnt")) > 0;
		if (lexemeTypeExists) {
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("lexemeId", lexemeId);
			tableRowParamMap.put("lexemeType", lexemeType);
			basicDbService.update(SQL_UPDATE_LEXEME_TYPE, tableRowParamMap);
		} else {
			dataErrorCount.increment();
			logger.warn("Unable to bind lexeme type code \"{}\"", lexemeType);
		}
	}
}
