package eki.ekilex.runner;

import eki.common.data.Count;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
	public void execute(String dataXmlFilePath, String dataset, Map<String, List<Paradigm>> wordParadigmsMap) throws Exception {

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
		List<WordData> importedWords = new ArrayList<>();

		for (Element articleNode : articleNodes) {
			List<WordData> newWords = new ArrayList<>();
			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			processArticleHeader(headerNode, newWords, wordParadigmsMap, wordDuplicateCount);

			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(contentNode, newWords, dataset, lexemeDuplicateCount, synonyms, antonyms);
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
			importedWords.addAll(newWords);
		}

		processSynonyms(synonyms, dataset, importedWords);
		processAntonyms(antonyms, dataset, importedWords);

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private void processAntonyms(List<AntonymData> antonyms, String dataset, List<WordData> importedWords) throws Exception {

		logger.debug("Found {} antonyms.", antonyms.size());
		for (AntonymData antonymData: antonyms) {
			List<WordData> existingWords = importedWords.stream().filter(w -> antonymData.word.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(antonymData.word, antonymData.homonymNr, existingWords);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("word_id", wordId);
				params.put("level1", antonymData.lexemeLevel1);
				Map<String, Object> lexemeObject = basicDbService.select(LEXEME, params);
				if (lexemeObject != null) {
					Map<String, Object> relationParams = new HashMap<>();
					relationParams.put("lexeme1_id", lexemeObject.get("id"));
					relationParams.put("lexeme2_id", antonymData.lexemeId);
					relationParams.put("lex_rel_type_code", "ant");
					Long relationId = basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);
					if (relationId != null) {
						relationParams.clear();
						relationParams.put("lex_relation_id", relationId);
						relationParams.put("dataset_code", dataset);
						basicDbService.createWithoutId(LEX_RELATION_DATASET, relationParams);
					}
				} else {
					logger.debug("Lexeme not found for antonym : {}, lexeme level1 : {}.", antonymData.word, antonymData.lexemeLevel1);
				}
			} else {
				logger.debug("Word not found for antonym : {}, lexeme level1 : {}.", antonymData.word, antonymData.lexemeLevel1);
			}
		}
		logger.debug("Antonyms import done.");
	}

	private void processSynonyms(List<SynonymData> synonyms, String dataset, List<WordData> importedWords) throws Exception {

		final String defaultWordMorphCode = "SgN";

		logger.debug("Found {} synonyms", synonyms.size());

		Count newSynonymWordCount = new Count();
		for (SynonymData synonymData : synonyms) {
			Long wordId;
			List<WordData> existingWords = importedWords.stream().filter(w -> synonymData.word.equals(w.value)).collect(Collectors.toList());
			if (existingWords.isEmpty()) {
				int homonymNr = getWordMaxHomonymNr(synonymData.word, dataLang) + 1;
				Word word = new Word(synonymData.word, dataLang, null, null, null, homonymNr, defaultWordMorphCode);
				wordId = saveWord(word, null, null);
				WordData newWord = new WordData();
				newWord.id = wordId;
				newWord.value = synonymData.word;
				importedWords.add(newWord);
				newSynonymWordCount.increment();
			} else {
				wordId = getWordIdFor(synonymData.word, synonymData.homonymNr, existingWords);
				if (wordId == null) continue;
			}
			createLexeme(wordId, synonymData.meaningId, 0, 0, 0, dataset);
		}
		logger.debug("Synonym words created {}", newSynonymWordCount.getValue());
		logger.debug("Synonyms import done.");
	}

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words) {
		Long wordId = null;
		if (words.size() > 1) {
			logger.debug("More than one word found: {}", wordValue);
			Optional<WordData> matchingWord = words.stream().filter(w -> w.homonymNr == homonymNr).findFirst();
			if (matchingWord.isPresent()) {
				wordId = matchingWord.get().id;
			} else {
				logger.debug("No matching word was found for: {}", wordValue);
			}
		} else {
			wordId = words.get(0).id;
		}
		return wordId;
	}

	private void processArticleContent(Element contentNode, List<WordData> newWords, String dataset, Count lexemeDuplicateCount,
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
							antonymData.homonymNr = meaningAntonym.homonymNr;
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
		final String homonymNrAttr = "i";
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
			String homonymNrAtrValue = antonymNode.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAtrValue)) {
				antonymData.homonymNr = Integer.parseInt(homonymNrAtrValue);
			}
			antonyms.add(antonymData);
		}
		return antonyms;
	}

	private List<SynonymData> extractSynonyms(Element node, Long meaningId) {

		final String synonymExp = "x:syn";
		final String homonymNrAttr = "i";

		List<SynonymData> synonyms = new ArrayList<>();
		List<Element> synonymNodes = node.selectNodes(synonymExp);
		for (Element synonymNode : synonymNodes) {
			SynonymData data = new SynonymData();
			data.word = synonymNode.getTextTrim();
			data.meaningId = meaningId;
			String homonymNrAtrValue = synonymNode.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAtrValue)) {
				data.homonymNr = Integer.parseInt(homonymNrAtrValue);
			}
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

	private void processArticleHeader(
			Element headerNode, List<WordData> newWords, Map<String, List<Paradigm>> wordParadigmsMap, Count wordDuplicateCount) throws Exception {

		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordDerivCodeExp = "x:dk";
		final String wordGrammarExp = "x:mfp/x:gki";

		boolean isAddForms = !wordParadigmsMap.isEmpty();
		Paradigm paradigmObj = null;
		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();

			Word word = extractWord(wordGroupNode, wordData);
			if (isAddForms) {
				paradigmObj = extractParadigm(word.getValue(), wordGroupNode, wordParadigmsMap);
			}
			wordData.id = saveWord(word, paradigmObj, wordDuplicateCount);

			Element posCodeNode = (Element) wordGroupNode.selectSingleNode(wordPosCodeExp);
			wordData.posCode = posCodeNode == null ? null : posCodeNode.getTextTrim();

			Element derivCodeNode = (Element) wordGroupNode.selectSingleNode(wordDerivCodeExp);
			wordData.derivCode = derivCodeNode == null ? null : derivCodeNode.getTextTrim();

			Element grammarNode = (Element) wordGroupNode.selectSingleNode(wordGrammarExp);
			wordData.grammar = grammarNode == null ? null : grammarNode.getTextTrim();

			wordData.value = word.getValue();
			newWords.add(wordData);
		}
	}

	private Paradigm extractParadigm(String word, Element node, Map<String, List<Paradigm>> wordParadigmsMap) {

		final String formsNodesExp = "x:mfp/x:gkg/x:mvg/x:mvgp/x:mvf";
		final String formStrCleanupChars = ".()¤:_|[]/̄̆̇’\"'`´,;–+=";

		List<Paradigm> paradigms = wordParadigmsMap.get(word);
		if (CollectionUtils.isEmpty(paradigms)) {
			return null;
		}
		List<Element> formsNodes = node.selectNodes(formsNodesExp);
		if (formsNodes.isEmpty()) {
			return null;
		}
		List<String> formValues = formsNodes.stream().map(n -> StringUtils.replaceChars(n.getTextTrim(), formStrCleanupChars, "")).collect(Collectors.toList());
		List<String> mabFormValues;
		Collection<String> formValuesIntersection;
		int bestFormValuesMatchCount = 0;
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : paradigms) {
			mabFormValues = paradigm.getFormValues();
			formValuesIntersection = CollectionUtils.intersection(formValues, mabFormValues);
			if (formValuesIntersection.size() > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = formValuesIntersection.size();
				matchingParadigm = paradigm;
			}
		}
		return matchingParadigm;
	}

	private Word extractWord(Element wordGroupNode, WordData wordData) throws Exception {

		final String wordExp = "x:m";
		final String wordVocalFormExp = "x:hld";
		final String homonymNrAttr = "i";
		final String defaultWordMorphCode = "SgN";

		Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
		if (wordNode.attributeValue(homonymNrAttr) != null) {
			wordData.homonymNr = Integer.parseInt(wordNode.attributeValue(homonymNrAttr));
		}
		String wordValue = wordNode.getTextTrim();
		String wordDisplayForm = wordValue;
		wordValue = StringUtils.replaceChars(wordValue, wordDisplayFormStripChars, "");
		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;
		Element wordVocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
		String wordVocalForm = wordVocalFormNode == null ? null : wordVocalFormNode.getTextTrim();
		String wordMorphCode = getWordMorphCode(wordValue, wordGroupNode, defaultWordMorphCode);

		return new Word(wordValue, dataLang, null, wordDisplayForm, wordVocalForm, homonymNr, wordMorphCode);
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
		String value;
		int homonymNr = 0;
	}

	private class SynonymData {
		String word;
		Long meaningId;
		int homonymNr = 0;
	}

	private class AntonymData {
		String word;
		Long lexemeId;
		int lexemeLevel1 = 1;
		int homonymNr = 0;
	}
}
