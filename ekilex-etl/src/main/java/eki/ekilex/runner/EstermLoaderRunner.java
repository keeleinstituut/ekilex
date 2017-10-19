package eki.ekilex.runner;

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

import eki.common.data.Count;

@Component
public class EstermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EstermLoaderRunner.class);

	private static final String SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";

	private static final String SQL_SELECT_COUNT_LEXEME_TYPE_BY_CODE = "select count(code) cnt from " + LEXEME_TYPE + " where code = :code";

	private static final String SQL_UPDATE_LEXEME_TYPE = "update " + LEXEME + " set type = :lexemeType where id = :lexemeId";

	@Override
	void initialise() throws Exception {
		//Nothing...
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String[] datasets) throws Exception {

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
		final String defaultRection = "-";
		final String defaultWordMorphCode = "SgN";

		long t1, t2;
		t1 = System.currentTimeMillis();

		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		List<Element> langGroupNodes, termGroupNodes, domainNodes;
		Element languageNode, termNode, usageNode, definitionNode, lexemeTypeNode;
		String languageType, lang, word, usage, definition, lexemeType;
		Long wordId, meaningId, lexemeId, rectionId;
		int homonymNr;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count dataErrorCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Element conceptGroupNode : conceptGroupNodes) {

			meaningId = createMeaning(datasets);

			domainNodes = conceptGroupNode.selectNodes(domainExp);
			saveDomains(conceptGroupNode, domainNodes, meaningId, domainOrigin, dataErrorCount);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Element langGroupNode : langGroupNodes) {

				languageNode = (Element) langGroupNode.selectSingleNode(langExp);
				languageType = languageNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(languageType);

				if (!isLang) {
					//logger.debug("Not a term entry \"{}\"", languageType);
					continue;
				}

				lang = unifyLang(languageType);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Element termGroupNode : termGroupNodes) {

					termNode = (Element) termGroupNode.selectSingleNode(termExp);
					word = termNode.getTextTrim();

					homonymNr = getWordMaxHomonymNr(word, dataLang);
					homonymNr++;
					wordId = saveWord(word, null, null, null, homonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);
					lexemeId = createLexeme(wordId, meaningId, null, null, null, datasets);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						usageNode = (Element) termGroupNode.selectSingleNode(usageExp);
						if (usageNode == null) {
							usage = null;
						} else {
							if (usageNode.hasMixedContent()) {
								//TODO get source
							}
							usage = usageNode.getTextTrim();
							rectionId = createOrSelectRection(lexemeId, defaultRection);
							createUsage(rectionId, usage);
						}
						definitionNode = (Element) termGroupNode.selectSingleNode(definitionExp);
						if (definitionNode == null) {
							definition = null;
						} else {
							if (definitionNode.hasMixedContent()) {
								//TODO get source
							}
							definition = definitionNode.getTextTrim();
							createDefinition(meaningId, definition, lang, datasets);
						}
						lexemeTypeNode = (Element) termGroupNode.selectSingleNode(lexemeTypeExp);
						if (lexemeTypeNode == null) {
							lexemeType = null;
						} else {
							lexemeType = lexemeTypeNode.getTextTrim();
							updateLexemeType(lexemeId, lexemeType, dataErrorCount);
						}
					}
				}
			}

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				logger.debug("{} concept groups iterated", conceptGroupCounter);
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
