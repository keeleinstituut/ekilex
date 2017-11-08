package eki.ekilex.runner;

import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;
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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class PsvLoaderRunner extends AbstractLoaderRunner {

	private final String dataLang = "est";
	private final String wordDisplayFormStripChars = ".+'`()¤:_|[]/";
	private final String defaultWordMorphCode = "SgN";
	private final static String REPORT_NAME = "report";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private Map<String, String> posCodes;
	private ReportComposer reportComposer;

	@Override
	void initialise() throws Exception {
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataset, Map<String, List<Paradigm>> wordParadigmsMap) throws Exception {

		final String articleExp = "/x:sr/x:A";
		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";
		final String articleGuidExp = "x:G";

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		reportComposer = new ReportComposer("PSV import", REPORT_NAME);

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
		Context context = new Context();

		writeToLogFile("Artiklite töötlus", "", "");
		for (Element articleNode : articleNodes) {
			List<WordData> newWords = new ArrayList<>();
			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			Element guidNode = (Element) articleNode.selectSingleNode(articleGuidExp);
			String guid = guidNode.getTextTrim();
			processArticleHeader(guid, headerNode, newWords, context, wordParadigmsMap, wordDuplicateCount);

			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(guid, contentNode, newWords, dataset, lexemeDuplicateCount, context);
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
			context.importedWords.addAll(newWords);
		}

		processSynonyms(context, dataset);
		processAntonyms(context, dataset);
		processBasicWords(context, dataset);
		processReferenceForms(context);
		processCompoundWords(context, dataset);

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);

		reportComposer.end();
		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private void processCompoundWords(Context context, String dataset) throws Exception {

		logger.debug("Found {} compound words.", context.compoundWords.size());
		writeToLogFile("Liitsõnade töötlus <x:ls>", "", "");
		for (LexemeToWordData compData: context.compoundWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> compData.word.equals(w.value)).collect(Collectors.toList());
			if (existingWords.size() > 1) {
				logger.debug("Found more than one word : {}.", compData.word);
				writeToLogFile("Leiti rohkem kui üks vaste sõnale", compData.guid, compData.word);
			}
			Long lexemeId;
			if (existingWords.isEmpty()) {
				logger.debug("No word found, adding word : {}.", compData.word);
				lexemeId = createLexemeAndRelatedObjects(compData.word, context, dataset);
			} else {
				lexemeId = findLexemeIdForWord(existingWords.get(0).id, compData);
				if (lexemeId == null) {
					continue;
				}
			}
			createLexemeRelation(compData.lexemeId, lexemeId, "comp", dataset);
		}
		logger.debug("Compound words processing done.");
	}

	private Long createLexemeAndRelatedObjects(String wordValue, Context context, String dataset) throws Exception {

		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;
		Word word = new Word(wordValue, dataLang, null, null, null, homonymNr, defaultWordMorphCode);
		Long wordId = saveWord(word, null, null);
		WordData newWord = new WordData();
		newWord.id = wordId;
		newWord.value = wordValue;
		context.importedWords.add(newWord);
		Long meaningId = createMeaning(dataset);
		Lexeme lexeme = new Lexeme();
		lexeme.setMeaningId(meaningId);
		lexeme.setWordId(wordId);
		lexeme.setLevel1(0);
		lexeme.setLevel2(0);
		lexeme.setLevel3(0);
		return createLexeme(lexeme, dataset);
	}

	private Long findLexemeIdForWord(Long wordId, LexemeToWordData data) throws Exception {

		Long lexemeId = null;
		Map<String, Object> params = new HashMap<>();
		params.put("word_id", wordId);
		List<Map<String, Object>> lexemes = basicDbService.selectAll(LEXEME, params);
		if (lexemes.isEmpty()) {
			logger.debug("Lexeme not found for word : {}.", data.word);
			writeToLogFile("Ei leitud ilmikut sõnale", data.guid, data.word);
		} else {
			if (lexemes.size() > 1) {
				logger.debug("Found more than one lexeme for : {}.", data.word);
				writeToLogFile("Leiti rohkem kui üks ilmik sõnale", data.guid, data.word);
			}
			lexemeId = (Long)lexemes.get(0).get("id");
		}
		return lexemeId;
	}

	private void processReferenceForms(Context context) throws Exception {

		logger.debug("Found {} reference forms.", context.referenceForms.size());
		writeToLogFile("Vormid mis viitavad põhisõnale töötlus <x:mvt>", "", "");
		for (ReferenceFormData referenceForm : context.referenceForms) {
			Optional<WordData> word = context.importedWords.stream()
					.filter(w -> referenceForm.wordValue.equals(w.value) && referenceForm.wordHomonymNr == w.homonymNr).findFirst();
			if (word.isPresent()) {
				Map<String, Object> params = new HashMap<>();
				params.put("word_id", word.get().id);
				List<Map<String, Object>> forms = basicDbService
						.queryList("select f.* from form f, paradigm p where p.word_id = :word_id and f.paradigm_id = p.id", params);
				List<Map<String, Object>> wordForms = forms.stream().filter(f -> (boolean) f.get("is_word")).collect(Collectors.toList());
				if (wordForms.size() > 1) {
					logger.debug("More than one word form found for word : {}, id : {}", referenceForm.wordValue, word.get().id);
					continue;
				}
				Map<String, Object> wordForm = wordForms.get(0);
				Optional<Map<String, Object>> form = forms.stream().filter(f -> referenceForm.formValue.equals(f.get("value"))).findFirst();
				if (!form.isPresent()) {
					logger.debug("Form not found for {}, {} -> {}", referenceForm.guid, referenceForm.formValue, referenceForm.wordValue);
					writeToLogFile("Vormi ei leitud", referenceForm.guid, referenceForm.formValue + " -> " + referenceForm.wordValue);
					continue;
				}
				params.clear();
				params.put("form1_id", form.get().get("id"));
				params.put("form2_id", wordForm.get("id"));
				params.put("form_rel_type_code", "mvt");
				basicDbService.create(FORM_RELATION, params);
			} else {
				logger.debug("Word not found {}, {}, {}", referenceForm.guid, referenceForm.wordValue, referenceForm.wordHomonymNr);
				writeToLogFile("Sihtsõna ei leitud", referenceForm.guid, referenceForm.wordValue + ", " + referenceForm.wordHomonymNr);
			}
		}
		logger.debug("Reference forms processing done.");
	}

	private void processBasicWords(Context context, String dataset) throws Exception {

		logger.debug("Found {} basic words.", context.basicWords.size());
		writeToLogFile("Märksõna põhisõna seoste töötlus <x:ps>", "", "");
		for (WordData basicWord: context.basicWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> basicWord.value.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(basicWord.value, basicWord.homonymNr, existingWords, basicWord.guid);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("word_id", basicWord.id);
				List<Map<String, Object>> secondaryWordLexemes = basicDbService.selectAll(LEXEME, params);
				for (Map<String, Object> secondaryWordLexeme : secondaryWordLexemes) {
					params.put("word_id", wordId);
					List<Map<String, Object>> lexemes = basicDbService.selectAll(LEXEME, params);
					for (Map<String, Object> lexeme : lexemes) {
						createLexemeRelation((Long) secondaryWordLexeme.get("id"), (Long)lexeme.get("id"), "head", dataset);
					}
				}
			}
		}
		logger.debug("Basic words processing done.");
	}

	private void processAntonyms(Context context, String dataset) throws Exception {

		logger.debug("Found {} antonyms.", context.antonyms.size());
		writeToLogFile("Antonüümide töötlus <x:ant>", "", "");
		for (LexemeToWordData antonymData: context.antonyms) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> antonymData.word.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(antonymData.word, antonymData.homonymNr, existingWords, antonymData.guid);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("word_id", wordId);
				params.put("level1", antonymData.lexemeLevel1);
				Map<String, Object> lexemeObject = basicDbService.select(LEXEME, params);
				if (lexemeObject != null) {
					createLexemeRelation(antonymData.lexemeId, (Long) lexemeObject.get("id"), "ant", dataset);
				} else {
					logger.debug("Lexeme not found for antonym : {}, lexeme level1 : {}.", antonymData.word, antonymData.lexemeLevel1);
					writeToLogFile("Ei leitud ilmikut antaonüümile", antonymData.guid, antonymData.word + ", level1 " + antonymData.lexemeLevel1);
				}
			}
		}
		logger.debug("Antonyms import done.");
	}

	private void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType, String dataset) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("lexeme1_id", lexemeId1);
		relationParams.put("lexeme2_id", lexemeId2);
		relationParams.put("lex_rel_type_code", relationType);
		Long relationId = basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);
		if (relationId != null) {
			relationParams.clear();
			relationParams.put("lex_relation_id", relationId);
			relationParams.put("dataset_code", dataset);
			basicDbService.createWithoutId(LEX_RELATION_DATASET, relationParams);
		}
	}

	private void processSynonyms(Context context, String dataset) throws Exception {

		logger.debug("Found {} synonyms", context.synonyms.size());
		writeToLogFile("Sünonüümide töötlus <x:syn>", "", "");

		Count newSynonymWordCount = new Count();
		for (SynonymData synonymData : context.synonyms) {
			Long wordId;
			List<WordData> existingWords = context.importedWords.stream().filter(w -> synonymData.word.equals(w.value)).collect(Collectors.toList());
			if (existingWords.isEmpty()) {
				int homonymNr = getWordMaxHomonymNr(synonymData.word, dataLang) + 1;
				Word word = new Word(synonymData.word, dataLang, null, null, null, homonymNr, defaultWordMorphCode);
				wordId = saveWord(word, null, null);
				WordData newWord = new WordData();
				newWord.id = wordId;
				newWord.value = synonymData.word;
				context.importedWords.add(newWord);
				newSynonymWordCount.increment();
			} else {
				wordId = getWordIdFor(synonymData.word, synonymData.homonymNr, existingWords, synonymData.guid);
				if (wordId == null) continue;
			}
			Lexeme lexeme = new Lexeme();
			lexeme.setWordId(wordId);
			lexeme.setMeaningId(synonymData.meaningId);
			lexeme.setLevel1(0);
			lexeme.setLevel2(0);
			lexeme.setLevel3(0);
			createLexeme(lexeme, dataset);
		}
		logger.debug("Synonym words created {}", newSynonymWordCount.getValue());
		logger.debug("Synonyms import done.");
	}

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words, String guid) throws Exception {

		Long wordId = null;
		if (words.size() > 1) {
			Optional<WordData> matchingWord = words.stream().filter(w -> w.homonymNr == homonymNr).findFirst();
			if (matchingWord.isPresent()) {
				wordId = matchingWord.get().id;
			} else {
				logger.debug("No matching word was found for {} word {}, {}", guid, wordValue, homonymNr);
				writeToLogFile("Ei leitud sihtsõna", guid, wordValue + " : " + homonymNr);
			}
		} else {
			wordId = words.get(0).id;
		}
		return wordId;
	}

	private void processArticleContent(String guid, Element contentNode, List<WordData> newWords, String dataset, Count lexemeDuplicateCount,
			Context context) throws Exception {

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
			List<String> compoundWords = extractCompoundWords(meaningNumberGroupNode);

			for (Element meaningGroupNode : meaingGroupNodes) {
				List<Element> usageGroupNodes = meaningGroupNode.selectNodes(usageGroupExp);
				List<Usage> usages = extractUsages(usageGroupNodes);

				Long meaningId = createMeaning(dataset);

				List<Element> definitionValueNodes = meaningGroupNode.selectNodes(definitionValueExp);
				saveDefinitions(definitionValueNodes, meaningId, dataLang, dataset);
				if (definitionValueNodes.size() > 1) {
					writeToLogFile("Leitud rohkem kui üks seletus <x:d>", guid, newWords.get(0).value);
				}

				List<SynonymData> meaningSynonyms = extractSynonyms(guid, meaningGroupNode, meaningId);
				context.synonyms.addAll(meaningSynonyms);

				List<LexemeToWordData> meaningAntonyms = extractAntonyms(meaningGroupNode);

				int lexemeLevel2 = 0;
				for (WordData newWordData : newWords) {
					lexemeLevel2++;
					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(newWordData.id);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(lexemeLevel1);
					lexeme.setLevel2(lexemeLevel2);
					lexeme.setLevel3(0);
					lexeme.setFrequencyGroup(newWordData.frequencyGroup);
					Long lexemeId = createLexeme(lexeme, dataset);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						saveRectionsAndUsages(meaningNumberGroupNode, lexemeId, usages);
						savePosAndDeriv(lexemeId, newWordData);
						saveGrammars(meaningNumberGroupNode, lexemeId, dataset, newWordData);
						for (LexemeToWordData meaningAntonym : meaningAntonyms) {
							LexemeToWordData antonymData = new LexemeToWordData();
							antonymData.word = meaningAntonym.word;
							antonymData.lexemeLevel1 = meaningAntonym.lexemeLevel1;
							antonymData.lexemeId = lexemeId;
							antonymData.homonymNr = meaningAntonym.homonymNr;
							antonymData.guid = guid;
							context.antonyms.add(antonymData);
						}
						for (String compoundWord: compoundWords) {
							LexemeToWordData compData = new LexemeToWordData();
							compData.word = compoundWord;
							compData.lexemeId = lexemeId;
							compData.guid = guid;
							context.compoundWords.add(compData);
						}
					}
				}
			}
		}
	}

	private List<String> extractCompoundWords(Element node) {

		final String compoundWordExp = "x:smp/x:lsg/x:ls";

		List<String> compoundWords = new ArrayList<>();
		List<Element> compoundWordNodes = node.selectNodes(compoundWordExp);
		for (Element compoundWordNode: compoundWordNodes) {
			compoundWords.add(compoundWordNode.getTextTrim());
		}
		return compoundWords;
	}

	private List<LexemeToWordData> extractAntonyms(Element node) {

		final String antonymExp = "x:ant";
		final String lexemeLevel1Attr = "t";
		final String homonymNrAttr = "i";
		final int defaultLexemeLevel1 = 1;

		List<LexemeToWordData> antonyms = new ArrayList<>();
		List<Element> antonymNodes = node.selectNodes(antonymExp);
		for (Element antonymNode : antonymNodes) {
			LexemeToWordData antonymData = new LexemeToWordData();
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

	private List<SynonymData> extractSynonyms(String guid, Element node, Long meaningId) {

		final String synonymExp = "x:syn";
		final String homonymNrAttr = "i";

		List<SynonymData> synonyms = new ArrayList<>();
		List<Element> synonymNodes = node.selectNodes(synonymExp);
		for (Element synonymNode : synonymNodes) {
			SynonymData data = new SynonymData();
			data.guid = guid;
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

		if (!usages.isEmpty()) {
			Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, defaultRection);
			for (Usage usage : usages) {
				createUsage(rectionId, usage);
			}
		}
		List<Element> rectionGroups = node.selectNodes(rectionGroupExp);
		for (Element rectionGroup : rectionGroups) {
			List<Element> usageGroupNodes = rectionGroup.selectNodes(usageGroupExp);
			List<Usage> rectionUsages = extractUsages(usageGroupNodes);
			List<Element> rections = rectionGroup.selectNodes(rectionExp);
			for (Element rection : rections) {
				Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, rection.getTextTrim());
				for (Usage usage : rectionUsages) {
					createUsage(rectionId, usage);
				}
			}
		}
	}

	private void createUsage(Long rectionId, Usage usage) throws Exception {
		Long usageMeaningId = createFreeform(FreeformType.USAGE_MEANING, rectionId, "", null);
		createFreeform(FreeformType.USAGE, usageMeaningId, usage.getValue(), dataLang);
		if (isNotEmpty(usage.getDefinition())) {
			createFreeform(FreeformType.USAGE_DEFINITION, usageMeaningId, usage.getDefinition(), dataLang);
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
				if (usageNode.hasMixedContent()) {
					newUsage.setDefinition(usageNode.selectSingleNode("x:nd").getText());
				}
				usages.add(newUsage);
			}
		}
		return usages;
	}

	private void processArticleHeader(
			String guid,
			Element headerNode,
			List<WordData> newWords,
			Context context,
			Map<String, List<Paradigm>> wordParadigmsMap,
			Count wordDuplicateCount) throws Exception {

		final String referenceFormExp = "x:mvt";

		List<Element> referenceFormNodes = headerNode.selectNodes(referenceFormExp);
		boolean isReferenceForm = !referenceFormNodes.isEmpty();

		if (isReferenceForm) {
			processAsForm(guid, headerNode, referenceFormNodes, context.referenceForms);
		} else {
			processAsWord(guid, headerNode, newWords, context.basicWords, wordParadigmsMap, wordDuplicateCount);
		}
	}

	private void processAsForm(String guid, Element headerNode, List<Element> referenceFormNodes, List<ReferenceFormData> referenceForms) {

		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String homonymNrAttr = "i";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			String formValue = wordNode.getTextTrim();
			formValue = StringUtils.replaceChars(formValue, wordDisplayFormStripChars, "");
			for (Element referenceFormNode : referenceFormNodes) {
				ReferenceFormData referenceFormData = new ReferenceFormData();
				referenceFormData.formValue = formValue;
				referenceFormData.guid = guid;
				referenceFormData.wordValue = referenceFormNode.getTextTrim();
				if (referenceFormNode.attributeValue(homonymNrAttr) != null) {
					referenceFormData.wordHomonymNr = Integer.parseInt(referenceFormNode.attributeValue(homonymNrAttr));
				}
				referenceForms.add(referenceFormData);
			}
		}
	}

	private void processAsWord(
			String guid,
			Element headerNode,
			List<WordData> newWords,
			List<WordData> basicWords,
			Map<String, List<Paradigm>> wordParadigmsMap,
			Count wordDuplicateCount) throws Exception {

		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordDerivCodeExp = "x:dk";
		final String wordGrammarExp = "x:mfp/x:gki";
		final String wordFrequencyGroupExp = "x:sag";

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

			List<WordData> basicWordsOfTheWord = extractBasicWords(wordGroupNode, wordData.id, guid);
			basicWords.addAll(basicWordsOfTheWord);

			Element posCodeNode = (Element) wordGroupNode.selectSingleNode(wordPosCodeExp);
			wordData.posCode = posCodeNode == null ? null : posCodeNode.getTextTrim();

			Element derivCodeNode = (Element) wordGroupNode.selectSingleNode(wordDerivCodeExp);
			wordData.derivCode = derivCodeNode == null ? null : derivCodeNode.getTextTrim();

			Element grammarNode = (Element) wordGroupNode.selectSingleNode(wordGrammarExp);
			wordData.grammar = grammarNode == null ? null : grammarNode.getTextTrim();

			Element frequencyNode = (Element) wordGroupNode.selectSingleNode(wordFrequencyGroupExp);
			wordData.frequencyGroup = frequencyNode == null ? null : frequencyNode.getTextTrim();

			wordData.value = word.getValue();
			newWords.add(wordData);
		}
	}

	private List<WordData> extractBasicWords(Element node, Long wordId, String guid) {

		final String basicWordExp = "x:ps";
		final String homonymNrAttr = "i";

		List<WordData> basicWords = new ArrayList<>();
		List<Element> basicWordNodes = node.selectNodes(basicWordExp);
		for (Element basicWordNode : basicWordNodes) {
			WordData basicWord = new WordData();
			basicWord.id = wordId;
			basicWord.value = basicWordNode.getTextTrim();
			basicWord.guid = guid;
			if (basicWordNode.attributeValue(homonymNrAttr) != null) {
				basicWord.homonymNr = Integer.parseInt(basicWordNode.attributeValue(homonymNrAttr));
			}
			basicWords.add(basicWord);
		}
		return basicWords;
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
		String wordMorphCode = getWordMorphCode(wordValue, wordGroupNode);

		return new Word(wordValue, dataLang, null, wordDisplayForm, wordVocalForm, homonymNr, wordMorphCode);
	}

	private String getWordMorphCode(String word, Element wordGroupNode) {

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

	private void writeToLogFile(String message, String guid, String values) throws Exception {

		String logMessage = String.join(String.valueOf(CSV_SEPARATOR), asList(message, guid, values));
		reportComposer.append(REPORT_NAME, logMessage);
	}

	private class WordData {
		Long id;
		String posCode;
		String derivCode;
		String grammar;
		String value;
		int homonymNr = 0;
		String guid;
		String frequencyGroup;
	}

	private class SynonymData {
		String word;
		Long meaningId;
		int homonymNr = 0;
		String guid;
	}

	private class LexemeToWordData {
		Long lexemeId;
		String word;
		int lexemeLevel1 = 1;
		int homonymNr = 0;
		String guid;
	}

	private class ReferenceFormData {
		String formValue;
		String wordValue;
		int wordHomonymNr = 0;
		String guid;
	}

	private class Context {
		List<SynonymData> synonyms = new ArrayList<>();
		List<LexemeToWordData> antonyms = new ArrayList<>();
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> basicWords = new ArrayList<>();
		List<ReferenceFormData> referenceForms = new ArrayList<>(); // viitemärksõna
		List<LexemeToWordData> compoundWords = new ArrayList<>(); // liitsõnad
	}

}
