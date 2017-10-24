package eki.ekilex.runner;

import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.ekilex.data.transform.Usage;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class PsvLoaderRunner extends AbstractLoaderRunner {

	private final String dataLang = "est";
	private final String wordDisplayFormStripChars = ".+'`()¤:_|[]/";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private Map<String, String> posCodes;

	@Override
	void initialise() throws Exception {

	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataset) throws Exception {

		final String articleExp = "/x:sr/x:A";
		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		String sqlPosCodeMappings = "select value as key, code as value from pos_label where lang='est' and type='capital'";
		posCodes = basicDbService.queryListAsMap(sqlPosCodeMappings, null);

		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);
		List<SynonymData> synonyms = new ArrayList<>();
		List<AntonymData> antonyms = new ArrayList<>();

		for (Element articleNode : articleNodes) {
			List<WordData> newWords = new ArrayList<>();
			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			processArticleHeader(headerNode, newWords, wordDuplicateCount);

			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(contentNode, newWords, dataset, wordDuplicateCount, lexemeDuplicateCount, synonyms, antonyms);
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		processSynonyms(synonyms, dataset);
// TODO: cant use it before we have dataset based detection of words
//		processAntonyms(antonyms, datasets);

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private void processAntonyms(List<AntonymData> antonyms, String[] datasets) throws Exception {
		logger.debug("Found {} antonyms.", antonyms.size());
		for (AntonymData antonymData: antonyms) {
			logger.debug("Looking for antonym : {}, lexeme level1 : {}.", antonymData.word, antonymData.lexemeLevel1);
			Map<String, Object> wordObject = getWord(antonymData.word, 1, dataLang);
			if (wordObject != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("word_id", wordObject.get("id"));
				params.put("level1", antonymData.lexemeLevel1);
				Map<String, Object> lexemeObject = basicDbService.select(LEXEME, params);
				if (lexemeObject != null) {
					Map<String, Object> relationParams = new HashMap<>();
					relationParams.put("lexeme1_id", lexemeObject.get("id"));
					relationParams.put("lexeme2_id", antonymData.lexemeId);
					relationParams.put("lex_rel_type_code", "ant");
					relationParams.put("datasets", new PgVarcharArray(datasets));
					basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);
				} else {
					logger.debug("Lexeme not found.");
				}
			} else {
				logger.debug("Word not found.");
			}
		}
	}

	private void processSynonyms(List<SynonymData> synonyms, String dataset) throws Exception {

		final String defaultWordMorphCode = "SgN";
		final int defaultHomonymNr = 1;

		logger.debug("Found {} synonyms", synonyms.size());

		Count existingWordCount = new Count();
		for (SynonymData synonymData : synonyms) {
			Long wordId = saveWord(synonymData.word, null, null, null, defaultHomonymNr, defaultWordMorphCode, dataLang, null, existingWordCount);
			createLexeme(wordId, synonymData.meaningId, 0, 0, 0, dataset);
		}
		logger.debug("Synonym words created {}", synonyms.size() - existingWordCount.getValue());
	}

	private void processArticleContent(Element contentNode, List<WordData> newWords, String dataset, Count wordDuplicateCount, Count lexemeDuplicateCount,
			List<SynonymData> synonyms, List<AntonymData> antonyms) throws Exception {

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

				Long meaningId = createMeaning(dataset);

				List<Element> definitionValueNodes = meaningGroupNode.selectNodes(definitionValueExp);
				saveDefinitions(definitionValueNodes, meaningId, dataLang, dataset);

				List<SynonymData> meaningSynonyms = extractSynonyms(meaningGroupNode, meaningId);
				synonyms.addAll(meaningSynonyms);

				List<AntonymData> meaningAntonyms = extractAntonyms(meaningGroupNode);

				int lexemeLevel2 = 0;
				for (WordData newWordData : newWords) {
					lexemeLevel2++;
					Long lexemeId = createLexeme(newWordData.id, meaningId, lexemeLevel1, lexemeLevel2, 0, dataset);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						saveRectionsAndUsages(meaningNumberGroupNode, lexemeId, usages);
						savePosAndDeriv(lexemeId, newWordData);
						saveGrammars(meaningNumberGroupNode, lexemeId, dataset, newWordData);
						for (AntonymData meaningAntonym : meaningAntonyms) {
							AntonymData antonymData = new AntonymData();
							antonymData.word = meaningAntonym.word;
							antonymData.lexemeLevel1 = meaningAntonym.lexemeLevel1;
							antonymData.lexemeId = lexemeId;
							antonyms.add(antonymData);
						}
					}
				}
			}
		}
	}

	private List<AntonymData> extractAntonyms(Element node) {

		final String antonymExp = "x:ant";
		final String lexemeLevel1Attr = "t";
		final int defaultLexemeLevel1 = 1;

		List<AntonymData> antonyms = new ArrayList<>();
		List<Element> antonymNodes = node.selectNodes(antonymExp);
		for (Element antonymNode : antonymNodes) {
			AntonymData antonymData = new AntonymData();
			antonymData.word = antonymNode.getTextTrim();
			String lexemeLevel1AttrValue = antonymNode.attributeValue(lexemeLevel1Attr);
			if (StringUtils.isBlank(lexemeLevel1AttrValue)) {
				antonymData.lexemeLevel1 = defaultLexemeLevel1;
			} else {
				antonymData.lexemeLevel1 = Integer.parseInt(lexemeLevel1AttrValue);
			}
			antonyms.add(antonymData);
		}
		return antonyms;
	}

	private List<SynonymData> extractSynonyms(Element node, Long meaningId) {

		final String synonymExp = "x:syn";

		List<SynonymData> synonyms = new ArrayList<>();
		List<Element> synonymNodes = node.selectNodes(synonymExp);
		for (Element synonymNode : synonymNodes) {
			SynonymData data = new SynonymData();
			data.word = synonymNode.getTextTrim();
			data.meaningId = meaningId;
			synonyms.add(data);
		}
		return synonyms;
	}

	private void saveGrammars(Element node, Long lexemeId, String dataset, WordData wordData) throws Exception {
		final String grammarValueExp = "x:grg/x:gki";

		List<Element> grammarNodes = node.selectNodes(grammarValueExp);
		for (Element grammarNode : grammarNodes) {
			createGrammar(lexemeId, dataset, grammarNode.getTextTrim());
		}
		if (isNotEmpty(wordData.grammar)) {
			createGrammar(lexemeId, dataset, wordData.grammar);
		}
	}

	private void createGrammar(Long lexemeId, String dataset, String value) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("lexeme_id", lexemeId);
		params.put("value", value);
		params.put("lang", dataLang);
		Long grammarId = basicDbService.createIfNotExists(GRAMMAR, params);
		if (grammarId != null) {
			params.clear();
			params.put("grammar_id", grammarId);
			params.put("dataset_code", dataset);
			basicDbService.createWithoutId(GRAMMAR_DATASET, params);
		}
	}

	//POS - part of speech
	private void savePosAndDeriv(Long lexemeId, WordData newWordData) throws Exception {

		if (posCodes.containsKey(newWordData.posCode)) {
			Map<String, Object> params = new HashMap<>();
			params.put("lexeme_id", lexemeId);
			params.put("pos_code", posCodes.get(newWordData.posCode));
			basicDbService.create(LEXEME_POS, params);
		}
		// TODO: add deriv code when we get the mappings between EKILEX and EKI data
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

	private void processArticleHeader(Element headerNode, List<WordData> newWords, Count wordDuplicateCount) throws Exception {

		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String wordVocalFormExp = "x:hld";
		final String wordPosCodeExp = "x:sl";
		final String wordDerivCodeExp = "x:dk";
		final String wordGrammarExp = "x:mfp/x:gki";
		final String defaultWordMorphCode = "SgN";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();

			Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			String word = wordNode.getTextTrim();
			String wordDisplayForm = word;
			word = StringUtils.replaceChars(word, wordDisplayFormStripChars, "");
			int homonymNr = getWordMaxHomonymNr(word, dataLang) + 1;
			Element wordVocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
			String wordVocalForm = wordVocalFormNode == null ? null : wordVocalFormNode.getTextTrim();
			String wordMorphCode = getWordMorphCode(word, wordGroupNode, defaultWordMorphCode);
			wordData.id = saveWord(word, null, wordDisplayForm, wordVocalForm, homonymNr, wordMorphCode, dataLang, null, wordDuplicateCount);

			Element posCodeNode = (Element) wordGroupNode.selectSingleNode(wordPosCodeExp);
			wordData.posCode = posCodeNode == null ? null : posCodeNode.getTextTrim();

			Element derivCodeNode = (Element) wordGroupNode.selectSingleNode(wordDerivCodeExp);
			wordData.derivCode = derivCodeNode == null ? null : derivCodeNode.getTextTrim();

			Element grammarNode = (Element) wordGroupNode.selectSingleNode(wordGrammarExp);
			wordData.grammar = grammarNode == null ? null : grammarNode.getTextTrim();

			newWords.add(wordData);
		}
	}

	private String getWordMorphCode(String word, Element wordGroupNode, String defaultWordMorphCode) {

		final String formGroupExp = "x:mfp/x:gkg/x:mvg";
		final String formExp = "x:mvgp/x:mvf";
		final String morphCodeAttributeExp = "vn";

		List<Element> formGroupNodes = wordGroupNode.selectNodes(formGroupExp);
		for (Element formGroup : formGroupNodes) {
			Element formElement = (Element) formGroup.selectSingleNode(formExp);
			String formValue = StringUtils.replaceChars(formElement.getTextTrim(), wordDisplayFormStripChars, "");
			if (word.equals(formValue)) {
				return formGroup.attributeValue(morphCodeAttributeExp);
			}
		}
		return defaultWordMorphCode;
	}

	private List<Long> saveSynonyms(Element node, String lang, Count wordDuplicateCount) throws Exception {

		final String synonymExp = "x:syn";
		final String defaultWordMorphCode = "SgN";
		final int defaultHomonymNr = 1;

		List<Long> synonymWordIds = new ArrayList<>();
		String synonym;
		Long wordId;
		List<Element> synonymNodes = node.selectNodes(synonymExp);

		for (Element synonymNode : synonymNodes) {

			synonym = synonymNode.getTextTrim();
			wordId = saveWord(synonym, null, null, null, defaultHomonymNr, defaultWordMorphCode, lang, null, wordDuplicateCount);
			synonymWordIds.add(wordId);
		}
		return synonymWordIds;
	}

	private void saveDefinitions(List<Element> definitionValueNodes, Long meaningId, String wordMatchLang, String dataset) throws Exception {

		if (definitionValueNodes == null) {
			return;
		}
		for (Element definitionValueNode : definitionValueNodes) {
			String definition = definitionValueNode.getTextTrim();
			createDefinition(meaningId, definition, wordMatchLang, dataset);
		}
	}

	private class WordData {
		Long id;
		String posCode;
		String derivCode;
		String grammar;
	}

	private class SynonymData {
		String word;
		Long meaningId;
	}

	private class AntonymData {
		String word;
		Long lexemeId;
		int lexemeLevel1 = 1;
	}
}
