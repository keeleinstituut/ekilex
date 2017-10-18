package eki.ekilex.runner;

import eki.common.data.Count;
import eki.ekilex.data.transform.Usage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PsvLoaderRunner extends AbstractLoaderRunner {

	private final String defaultWordMorphCode = "SgN";
	private final int defaultHomonymNr = 1;

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "csv/transform-morph-deriv.csv";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private Map<String, String> morphToMorphMap;

	private Map<String, String> morphToDerivMap;

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(TRANSFORM_MORPH_DERIV_FILE_PATH);
		List<String> morphDerivMapLines = getContentLines(resourceFileInputStream);
		morphToMorphMap = new HashMap<>();
		morphToDerivMap = new HashMap<>();
		for (String morphDerivMapLine : morphDerivMapLines) {
			if (StringUtils.isBlank(morphDerivMapLine)) {
				continue;
			}
			String[] morphDerivMapLineParts = StringUtils.split(morphDerivMapLine, CSV_SEPARATOR);
			String sourceMorphCode = morphDerivMapLineParts[0];
			String destinMorphCode = morphDerivMapLineParts[1];
			String destinDerivCode = morphDerivMapLineParts[2];
			morphToMorphMap.put(sourceMorphCode, destinMorphCode);
			if (!StringUtils.equals(destinDerivCode, String.valueOf(CSV_EMPTY_CELL))) {
				morphToDerivMap.put(sourceMorphCode, destinDerivCode);
			}
		}
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String[] datasets) throws Exception {

		final String articleExp = "/x:sr/x:A";
		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {
			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			List<Long> newWordIds = new ArrayList<>();

			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			saveWords(headerNode, dataLang, newWordIds, wordDuplicateCount);

			// new word lexeme grammars
			// createGrammars(wordIdGrammarMap, lexemeId, newWordId);

			processContent(contentNode, dataLang, newWordIds, datasets, wordDuplicateCount, lexemeDuplicateCount);

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private void processContent(Element contentNode, String dataLang, List<Long> newWordIds, String[] datasets, Count wordDuplicateCount, Count lexemeDuplicateCount) throws Exception {

		final String meaningNumberGroupExp = "x:tp";
		final String lexemeLevel1Attr = "tnr";
		final String meaningGroupExp = "x:tg";
		final String usageGroupExp = "x:ng";
		final String definitionValueExp = "x:dg/x:d";

		List<Element> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);

		for (Element meaningNumberGroupNode : meaningNumberGroupNodes) {

			String lexemeLevel1Str = meaningNumberGroupNode.attributeValue(lexemeLevel1Attr);
			Integer lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);

			List<Element> meaingGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);

			for (Element meaningGroupNode : meaingGroupNodes) {
				List<Element> usageGroupNodes = meaningGroupNode.selectNodes(usageGroupExp);
				List<Usage> usages = extractUsages(usageGroupNodes);

				Long meaningId = createMeaning(datasets);

				// definitions
				List<Element> definitionValueNodes = meaningGroupNode.selectNodes(definitionValueExp);
				saveDefinitions(definitionValueNodes, meaningId, dataLang, datasets);

				List<Long> synonymWordIds = saveSynonyms(meaningGroupNode, dataLang, wordDuplicateCount);

				int lexemeLevel2 = 0;

				// new words lexemes+rections+grammar
				for (Long newWordId : newWordIds) {
					lexemeLevel2++;
					Long lexemeId = createLexeme(newWordId, meaningId, lexemeLevel1, lexemeLevel2, 0, datasets);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						// new word lexeme rection, usages
						saveRectionsAndUsages(meaningNumberGroupNode, lexemeId, usages);
					}

					for (Long synonymWordId : synonymWordIds) {
						lexemeId = createLexeme(synonymWordId, meaningId, null, null, null, datasets);
						if (lexemeId == null) {
							lexemeDuplicateCount.increment();
						}
					}
				}
			}
		}
	}

	private void saveRectionsAndUsages(Element node, Long lexemeId, List<Usage> usages) throws Exception {

		final String rectionGroupExp = "x:rep/x:reg";
		final String usageGroupExp = "x:ng";
		final String rectionExp = "x:rek";
		final String defaultRection = "-";

		Long rectionId = createOrSelectRection(lexemeId, defaultRection);
		for (Usage usage : usages) {
				createUsage(rectionId, usage.getValue());
		}
		List<Element> rectionGroups = node.selectNodes(rectionGroupExp);
		for (Element rectionGroup : rectionGroups) {
			List usageGroupNodes = rectionGroup.selectNodes(usageGroupExp);
			List<Usage> rectionUsages = extractUsages(usageGroupNodes);
			List<Element> rections = rectionGroup.selectNodes(rectionExp);
			for (Element rection : rections) {
				rectionId = createOrSelectRection(lexemeId, rection.getTextTrim());
				for (Usage usage : rectionUsages) {
					createUsage(rectionId, usage.getValue());
				}
			}
		}
	}

	private List<Usage> extractUsages(List<Element> usageGroupNodes) {

		final String usageExp = "x:n";

		List<Usage> usages = new ArrayList<>();
		for (Element usageGroupNode : usageGroupNodes) {
			List<Element> usageNodes = usageGroupNode.selectNodes(usageExp);
			for (Element usageNode : usageNodes) {
				Usage newUsage = new Usage();
				newUsage.setValue(usageNode.getTextTrim());
				usages.add(newUsage);
			}
		}
		return usages;
	}

	private void saveWords(Element headerNode, String dataLang, List<Long> newWordIds, Count wordDuplicateCount) throws Exception {

		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String wordVocalFormExp = "x:hld";
		final String wordDisplayFormStripChars = ".+'`()¤:_";
		final String defaultWordMorphCode = "SgN";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			// word, form...
			Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			String word = wordNode.getTextTrim();
			String wordDisplayForm = word;
			word = StringUtils.replaceChars(word, wordDisplayFormStripChars, "");
			int homonymNr = getWordMaxHomonymNr(word, dataLang) + 1;
			Element wordVocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
			String wordVocalForm = wordVocalFormNode == null ? null : wordVocalFormNode.getTextTrim();
			String wordMorphCode = getWordMorphCode(defaultWordMorphCode, wordGroupNode);

			Long wordId = saveWord(word, wordDisplayForm, wordVocalForm, homonymNr, wordMorphCode, dataLang, null, wordDuplicateCount);
			newWordIds.add(wordId);
		}
	}

	private String getWordMorphCode(String defaultWordMorphCode, Element wordGroupNode) {

		final String wordMorphExp = "x:vk";
		Element morphNode = (Element) wordGroupNode.selectSingleNode(wordMorphExp);
		String destinMorphCode;
		if (morphNode == null) {
			destinMorphCode = defaultWordMorphCode;
		} else {
			String sourceMorphCode = morphNode.getTextTrim();
			destinMorphCode = morphToMorphMap.get(sourceMorphCode);
		}
		return destinMorphCode;
	}

	private List<Long> saveSynonyms(Element node, String lang, Count wordDuplicateCount) throws Exception {

		final String synonymExp = "x:syn";

		List<Long> synonymWordIds = new ArrayList<>();
		String synonym;
		Long wordId;
		List<Element> synonymNodes = node.selectNodes(synonymExp);

		for (Element synonymNode : synonymNodes) {

			synonym = synonymNode.getTextTrim();
			wordId = saveWord(synonym, null, null, defaultHomonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);
			synonymWordIds.add(wordId);
		}
		return synonymWordIds;
	}

	private List<String> getContentLines(InputStream resourceInputStream) throws Exception {

		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}


	private void saveDefinitions(List<Element> definitionValueNodes, Long meaningId, String wordMatchLang, String[] datasets) throws Exception {

		if (definitionValueNodes == null) {
			return;
		}
		for (Element definitionValueNode : definitionValueNodes) {
			String definition = definitionValueNode.getTextTrim();
			createDefinition(meaningId, definition, wordMatchLang, datasets);
		}
	}

}
