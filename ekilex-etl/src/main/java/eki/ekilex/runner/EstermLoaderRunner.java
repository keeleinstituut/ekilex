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

	@Override
	void initialise() throws Exception {

		//nothing yet...
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String[] datasets) throws Exception {

		logger.debug("Starting loading Esterm...");

		final String conceptGroupExp = "/mtf/conceptGrp";

		final String domainOrigin = "lenoch";
		final String defaultRection = "-";
		final String defaultWordMorphCode = "SgN";
		final int defaultHomonymNr = 1;

		long t1, t2;
		t1 = System.currentTimeMillis();

		/*
		FileInputStream fis = new FileInputStream(dataXmlFilePath);
		LineIterator lineIterator = IOUtils.lineIterator(fis, UTF_8);
		String line;

		int lineCount = 0;
		while (lineIterator.hasNext()) {
			line = lineIterator.nextLine();
			System.out.println(line);
			lineCount++;
			if (lineCount > 100) {
				break;
			}
		}
		lineIterator.close();
		fis.close();
		*/
		
		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		int conceptGroupCount = conceptGroupNodes.size();
		logger.debug("Extracted {} concept groups", conceptGroupCount);

		List<Element> langGroupNodes, termGroupNodes, domainNodes;
		Element languageNode, termNode, usageNode, definitionNode;
		String languageType, lang, word, usage, definition;
		Long wordId, meaningId, lexemeId, rectionId;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();

		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Element conceptGroupNode : conceptGroupNodes) {

			meaningId = createMeaning(datasets);

			domainNodes = conceptGroupNode.selectNodes("descripGrp/descrip[@type='Valdkonnaviide']/xref");
			saveDomains(conceptGroupNode, domainNodes, meaningId, domainOrigin);

			langGroupNodes = conceptGroupNode.selectNodes("languageGrp");

			for (Element langGroupNode : langGroupNodes) {

				languageNode = (Element) langGroupNode.selectSingleNode("language");
				languageType = languageNode.attributeValue("type");
				boolean isLang = isLang(languageType);

				if (!isLang) {
					logger.debug("Not a term entry \"{}\"", languageType);
					continue;
				}

				lang = unifyLang(languageType);

				termGroupNodes = langGroupNode.selectNodes("termGrp");

				for (Element termGroupNode : termGroupNodes) {

					termNode = (Element) termGroupNode.selectSingleNode("term");
					word = termNode.getTextTrim();

					wordId = saveWord(word, null, null, defaultHomonymNr, defaultWordMorphCode, lang, wordDuplicateCount);
					lexemeId = createLexeme(wordId, meaningId, null, null, null, datasets);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						usageNode = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Kontekst']");
						if (usageNode == null) {
							usage = null;
						} else {
							if (usageNode.hasMixedContent()) {
								//TODO get source
							}
							usage = usageNode.getTextTrim();

							rectionId = createRection(lexemeId, defaultRection);
							createUsage(rectionId, usage);
						}
						definitionNode = (Element) termGroupNode.selectSingleNode("descripGrp/descrip[@type='Definitsioon']");
						if (definitionNode == null) {
							definition = null;
						} else {
							if (definitionNode.hasMixedContent()) {
								//TODO get source
							}
							definition = definitionNode.getTextTrim();

							createDefinition(meaningId, definition, lang, datasets);
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

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private void saveDomains(Element parentNode, List<Element> domainNodes, Long meaningId, String domainOrigin) throws Exception {

		if (domainNodes == null) {
			return;
		}
		String domainExistsSql = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("origin", domainOrigin);

		String domainCode;
		Map<String, Object> tableRowValueMap;
		Long count;

		List<String> domainCodes = new ArrayList<>();

		for (Element domainNode : domainNodes) {
			domainCode = domainNode.getTextTrim();
			if (domainCodes.contains(domainCode)) {
				logger.warn("Duplicate bind entry for domain code \"{}\"", domainCode);
				logger.warn(parentNode.asXML());
				continue;
			}
			domainCodes.add(domainCode);
			tableRowParamMap.put("code", domainCode);
			tableRowValueMap = basicDbService.queryForMap(domainExistsSql, tableRowParamMap);
			count = (Long) tableRowValueMap.get("cnt");
			if (count > 0) {
				createMeaningDomain(meaningId, domainCode, domainOrigin);
			} else {
				logger.warn("Unable to bind domain code \"{}\"", domainCode);
			}
		}
	}
}
