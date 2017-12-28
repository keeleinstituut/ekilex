package eki.ekilex.runner;

import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.replaceChars;

@Component
public class Ss1LoaderRunner extends AbstractLoaderRunner {

	private final static String dataLang = "est";
	private final static String dataset = "ss1";
	private final static String formStrCleanupChars = ".()¤:_|[]̄̆̇’\"'`´;–+=";
	private final String defaultWordMorphCode = "SgN";
	private final String defaultRectionValue = "-";

	private final static String sqlWordLexemesByDataset = "select l.* from " + LEXEME + " l where l.word_id = :wordId and l.dataset_code = :dataset";

	private final static String LEXEME_RELATION_BASIC_WORD = "head";

	private final static String ARTICLES_REPORT_NAME = "keywords";
	private final static String BASIC_WORDS_REPORT_NAME = "basic_words";
	private final static String SYNONYMS_REPORT_NAME = "synonyms";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private ReportComposer reportComposer;
	private boolean reportingEnabled;

	private Map<String, String> lexemeTypes;
	private Map<String, String> posCodes;
	private Map<String, String> processStateCodes;
	private Map<String, String> displayMorpCodes;

	@Override
	void initialise() throws Exception {
		lexemeTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_LIIKTYYP);
		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
		processStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_ASTYYP);
		displayMorpCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_VKTYYP);
	}

	@Transactional
	public void execute(
			String dataXmlFilePath,
			Map<String, List<Paradigm>> wordParadigmsMap,
			boolean isAddReporting) throws Exception {

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		reportingEnabled = isAddReporting;
		if (reportingEnabled) {
			reportComposer = new ReportComposer("SS1 import", ARTICLES_REPORT_NAME);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		long articleCount = rootElement.content().stream().filter(o -> o instanceof Element).count();
		long progressIndicator = articleCount / Math.min(articleCount, 100);
		long articleCounter = 0;
		logger.debug("{} articles found", articleCount);

		Context context = new Context();

		writeToLogFile("Artiklite töötlus", "", "");
		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(o -> o instanceof Element).collect(toList());
		for (Element articleNode : articleNodes) {
			processArticle(articleNode, wordParadigmsMap, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);

		processBasicWords(context);
		processSynonymsNotFoundInImportFile(context);

		logger.debug("Found {} word duplicates", context.wordDuplicateCount);

		if (reportComposer != null) {
			reportComposer.end();
		}
		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	@Transactional
	void processArticle(Element articleNode, Map<String, List<Paradigm>> wordParadigmsMap, Context context) throws Exception {

		final String articleHeaderExp = "s:P";
		final String articleBodyExp = "s:S";

		String guid = extractGuid(articleNode);
		String reportingId = extractReporingId(articleNode);
		List<WordData> newWords = new ArrayList<>();

		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		processArticleHeader(reportingId, headerNode, newWords, context, wordParadigmsMap, guid);

		Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
		if (contentNode != null) {
			processArticleContent(reportingId, contentNode, newWords, context);
		}
		context.importedWords.addAll(newWords);
	}

	private void processSynonymsNotFoundInImportFile(Context context) throws Exception {

		logger.debug("Found {} synonyms", context.synonyms.size());
		setActivateReport(SYNONYMS_REPORT_NAME);
		writeToLogFile("Sünonüümide töötlus <s:syn>", "", "");

		Count newSynonymWordCount = new Count();
		for (SynonymData synonymData : context.synonyms) {
			boolean isImported = context.importedWords.stream().anyMatch(w -> synonymData.word.equals(w.value));
			if (!isImported) {
				WordData newWord = createDefaultWordFrom(synonymData.word, synonymData.displayForm);
				context.importedWords.add(newWord);
				newSynonymWordCount.increment();
				Long wordId = newWord.id;

				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(wordId);
				lexeme.setMeaningId(synonymData.meaningId);
				lexeme.setLevel1(0);
				lexeme.setLevel2(0);
				lexeme.setLevel3(0);
				createLexeme(lexeme, dataset);
				logger.debug("synonym word created : {}", synonymData.word);
				writeToLogFile(synonymData.reportingId, "sünonüümi ei letud, lisame sõna", synonymData.word);
			}
		}
		logger.debug("Synonym words created {}", newSynonymWordCount.getValue());
		logger.debug("Synonyms import done.");
	}

	void processBasicWords(Context context) throws Exception {

		logger.debug("Found {} basic words.", context.basicWords.size());
		setActivateReport(BASIC_WORDS_REPORT_NAME);
		writeToLogFile("Märksõna põhisõna seoste töötlus <s:ps>", "", "");

		for (WordData basicWord : context.basicWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> basicWord.value.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(basicWord.value, basicWord.homonymNr, existingWords, basicWord.reportingId);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("wordId", basicWord.id);
				params.put("dataset", dataset);
				List<Map<String, Object>> secondaryWordLexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
				for (Map<String, Object> secondaryWordLexeme : secondaryWordLexemes) {
					params.put("wordId", wordId);
					List<Map<String, Object>> lexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
					for (Map<String, Object> lexeme : lexemes) {
						createLexemeRelation((Long) secondaryWordLexeme.get("id"), (Long) lexeme.get("id"), LEXEME_RELATION_BASIC_WORD);
					}
				}
			}
		}
		logger.debug("Basic words processing done.");
	}

	private void processArticleContent(String reportingId, Element contentNode, List<WordData> newWords, Context context) throws Exception {

		final String meaningNumberGroupExp = "s:tp";
		final String lexemeLevel1Attr = "tnr";
		final String meaningGroupExp = "s:tg";
		final String lexemePosCodeExp = "s:ssp/s:mmg/s:sl";
		final String meaningExternalIdExp = "s:tpid";
		final String asTyypAttr = "as";

		List<Element> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
//		List<LexemeToWordData> jointReferences = extractJointReferences(contentNode, reportingId);
//		List<LexemeToWordData> compoundReferences = extractCompoundReferences(contentNode, reportingId);
//		List<LexemeToWordData> articleVormels = extractVormels(commonInfoNode);

		for (Element meaningNumberGroupNode : meaningNumberGroupNodes) {
//			saveSymbol(meaningNumberGroupNode, context, reportingId);
//			WordData abbreviation = processAbbreviation(meaningNumberGroupNode, context);
			String lexemeLevel1Str = meaningNumberGroupNode.attributeValue(lexemeLevel1Attr);
			Integer lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);
			List<Element> meaingGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
//			List<String> compoundWords = extractCompoundWords(meaningNumberGroupNode);
//			List<LexemeToWordData> meaningReferences = extractMeaningReferences(meaningNumberGroupNode, reportingId);
//			List<LexemeToWordData> vormels = extractVormels(meaningNumberGroupNode);
//			List<LexemeToWordData> singleForms = extractSingleForms(meaningNumberGroupNode);
//			List<LexemeToWordData> compoundForms = extractCompoundForms(meaningNumberGroupNode, reportingId);
			List<Long> newLexemes = new ArrayList<>();
			List<Element> posCodeNodes = meaningNumberGroupNode.selectNodes(lexemePosCodeExp);
			List<PosData> meaningPosCodes = new ArrayList<>();
			for (Element posCodeNode : posCodeNodes) {
				PosData posData = new PosData();
				posData.code = posCodeNode.getTextTrim();
				posData.processStateCode = posCodeNode.attributeValue(asTyypAttr);
				meaningPosCodes.add(posData);
			}
			Element meaningExternalIdNode = (Element) meaningNumberGroupNode.selectSingleNode(meaningExternalIdExp);
			String meaningExternalId = meaningExternalIdNode == null ? null : meaningExternalIdNode.getTextTrim();

			int lexemeLevel2 = 0;
			for (Element meaningGroupNode : meaingGroupNodes) {
				lexemeLevel2++;
				List<Usage> usages = extractUsages(meaningGroupNode);
				List<String> definitions = extractDefinitions(meaningGroupNode);

				Long meaningId = findExistingMeaningId(context, newWords.get(0), definitions);
				if (meaningId == null) {
					Meaning meaning = new Meaning();
					meaningId = createMeaning(meaning);
					for (String definition : definitions) {
						createDefinition(meaningId, definition, dataLang, dataset);
					}
					if (definitions.size() > 1) {
						writeToLogFile(reportingId, "Leitud rohkem kui üks seletus <s:d>", newWords.get(0).value);
					}
				} else {
					logger.debug("synonym meaning found : {}", newWords.get(0).value);
				}

				if (isNotEmpty(meaningExternalId)) {
					createMeaningFreeform(meaningId, FreeformType.MEANING_EXTERNAL_ID, meaningExternalId);
				}
//				if (abbreviation != null) {
//					addAbbreviationLexeme(abbreviation, meaningId, dataset);
//				}

				List<SynonymData> meaningSynonyms = extractSynonyms(reportingId, meaningGroupNode, meaningId, definitions);
				context.synonyms.addAll(meaningSynonyms);

//				List<LexemeToWordData> meaningAntonyms = extractAntonyms(meaningGroupNode, reportingId);

				int lexemeLevel3 = 0;
				for (WordData newWordData : newWords) {
					lexemeLevel3++;
					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(newWordData.id);
					lexeme.setType(newWordData.lexemeType);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(lexemeLevel1);
					lexeme.setLevel2(lexemeLevel2);
					lexeme.setLevel3(lexemeLevel3);
					Long lexemeId = createLexeme(lexeme, dataset);
					if (lexemeId != null) {
						saveRectionsAndUsages(meaningGroupNode, lexemeId, usages);
						savePosAndDeriv(lexemeId, newWordData, meaningPosCodes, reportingId);
						saveGrammars(meaningGroupNode, lexemeId);
//						for (LexemeToWordData meaningAntonym : meaningAntonyms) {
//							LexemeToWordData antonymData = meaningAntonym.copy();
//							antonymData.lexemeId = lexemeId;
//							antonymData.reportingId = reportingId;
//							context.antonyms.add(antonymData);
//						}
//						for (String compoundWord : compoundWords) {
//							LexemeToWordData compData = new LexemeToWordData();
//							compData.word = compoundWord;
//							compData.lexemeId = lexemeId;
//							compData.reportingId = reportingId;
//							context.compoundWords.add(compData);
//						}
//						for (LexemeToWordData meaningReference : meaningReferences) {
//							LexemeToWordData referenceData = meaningReference.copy();
//							referenceData.lexemeId = lexemeId;
//							referenceData.reportingId = reportingId;
//							context.meaningReferences.add(referenceData);
//						}
//						for (LexemeToWordData vormel : vormels) {
//							LexemeToWordData vormelData = vormel.copy();
//							vormelData.lexemeId = lexemeId;
//							vormelData.reportingId = reportingId;
//							context.vormels.add(vormelData);
//						}
//						for (LexemeToWordData singleForm : singleForms) {
//							LexemeToWordData singleFormData = singleForm.copy();
//							singleFormData.lexemeId = lexemeId;
//							singleFormData.reportingId = reportingId;
//							context.singleForms.add(singleFormData);
//						}
//						for (LexemeToWordData compoundForm : compoundForms) {
//							LexemeToWordData compoundFormData = compoundForm.copy();
//							compoundFormData.lexemeId = lexemeId;
//							compoundFormData.reportingId = reportingId;
//							context.compoundForms.add(compoundFormData);
//						}
						newLexemes.add(lexemeId);
					}
				}
			}
//			for (Long lexemeId : newLexemes) {
//				for (LexemeToWordData jointReference : jointReferences) {
//					LexemeToWordData referenceData = jointReference.copy();
//					referenceData.lexemeId = lexemeId;
//					referenceData.reportingId = reportingId;
//					context.jointReferences.add(referenceData);
//				}
//				for (LexemeToWordData compoundReference : compoundReferences) {
//					LexemeToWordData referenceData = compoundReference.copy();
//					referenceData.lexemeId = lexemeId;
//					referenceData.reportingId = reportingId;
//					context.compoundReferences.add(referenceData);
//				}
//				for (LexemeToWordData vormel : articleVormels) {
//					LexemeToWordData vormelData = vormel.copy();
//					vormelData.lexemeId = lexemeId;
//					vormelData.reportingId = reportingId;
//					context.vormels.add(vormelData);
//				}
//			}
		}
	}

	private void saveGrammars(Element node, Long lexemeId) throws Exception {

		final String grammarValueExp = "s:grg/s:gki";

		List<Element> grammarNodes = node.selectNodes(grammarValueExp);
		for (Element grammarNode : grammarNodes) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammarNode.getTextTrim(), dataLang);
		}
	}

	//POS - part of speech
	private void savePosAndDeriv(Long lexemeId, WordData newWordData, List<PosData> meaningPosCodes, String reportingId) throws Exception {

		Set<PosData> lexemePosCodes = new HashSet<>();
		try {
			if (meaningPosCodes.isEmpty()) {
				lexemePosCodes.addAll(newWordData.posCodes);
			} else {
				lexemePosCodes.addAll(meaningPosCodes);
				if (lexemePosCodes.size() > 1) {
					logger.debug("Found more than one POS <s:tp/s:ssp/s:mmg/s:sl> : {} : {}",
							reportingId, lexemePosCodes.stream().map(p -> p.code).collect(Collectors.joining(",")));
					writeToLogFile(reportingId, "Tähenduse juures leiti rohkem kui üks sõnaliik <s:tp/s:ssp/s:mmg/s:sl>", "");
				}
			}
			for (PosData posCode : lexemePosCodes) {
				if (posCodes.containsKey(posCode.code)) {
					Map<String, Object> params = new HashMap<>();
					params.put("lexeme_id", lexemeId);
					params.put("pos_code", posCodes.get(posCode.code));
					params.put("process_state_code", processStateCodes.get(posCode.processStateCode));
					basicDbService.create(LEXEME_POS, params);
				}
			}
		} catch (Exception e) {
			logger.debug("lexemeId {} : newWord : {}, {}, {}",
					lexemeId, newWordData.value, newWordData.id, lexemePosCodes.stream().map(p -> p.code).collect(Collectors.joining(",")));
			logger.error("ERROR", e);
		}
	}

	private void saveRectionsAndUsages(Element node, Long lexemeId, List<Usage> usages) throws Exception {

		final String rectionExp = "s:rep/s:reg/s:rek/s:kn";

		List<Element> rectionNodes = node.selectNodes(rectionExp);
		if (rectionNodes.isEmpty()) {
			if (!usages.isEmpty()) {
				Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, defaultRectionValue);
				for (Usage usage : usages) {
					createUsage(rectionId, usage);
				}
			}
		} else {
			for (Element rectionNode : rectionNodes) {
				String rectionValue = rectionNode.getTextTrim();
				Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, rectionValue);
				for (Usage usage : usages) {
					createUsage(rectionId, usage);
				}
			}
		}
	}

	private void createUsage(Long rectionId, Usage usage) throws Exception {
		Long usageMeaningId = createFreeformTextOrDate(FreeformType.USAGE_MEANING, rectionId, "", null);
		createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, usage.getValue(), dataLang);
		if (isNotEmpty(usage.getDefinition())) {
			createFreeformTextOrDate(FreeformType.USAGE_DEFINITION, usageMeaningId, usage.getDefinition(), dataLang);
		}
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context,
			Map<String, List<Paradigm>> wordParadigmsMap, String guid) throws Exception {

		final String wordGroupExp = "s:mg";
		final String wordPosCodeExp = "s:sl";
		final String posAsTyypAttr = "as";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData, wordParadigmsMap);
				wordData.id = saveWord(word, paradigms, dataset, context.wordDuplicateCount);
			}

			List<WordData> basicWordsOfTheWord = extractBasicWords(wordGroupNode, wordData.id, reportingId);
			context.basicWords.addAll(basicWordsOfTheWord);

			List<Element> posCodeNodes = wordGroupNode.selectNodes(wordPosCodeExp);
			for (Element posCodeNode : posCodeNodes) {
				PosData posData = new PosData();
				posData.code = posCodeNode.getTextTrim();
				posData.processStateCode = posCodeNode.attributeValue(posAsTyypAttr);
				wordData.posCodes.add(posData);
			}

			newWords.add(wordData);
		}
	}

	private List<SynonymData> extractSynonyms(String reportingId, Element node, Long meaningId, List<String> definitions) {

		final String synonymExp = "s:ssh/s:syn";
		final String homonymNrAttr = "i";

		List<SynonymData> synonyms = new ArrayList<>();
		List<Element> synonymNodes = node.selectNodes(synonymExp);
		for (Element synonymNode : synonymNodes) {
			SynonymData data = new SynonymData();
			data.reportingId = reportingId;
			data.displayForm = synonymNode.getTextTrim();
			data.word = cleanUp(data.displayForm);
			data.meaningId = meaningId;
			String homonymNrAtrValue = synonymNode.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAtrValue)) {
				data.homonymNr = Integer.parseInt(homonymNrAtrValue);
			}
			if (!definitions.isEmpty()) {
				data.definition = definitions.get(0);
			}
			synonyms.add(data);
		}
		return synonyms;
	}

	private List<String> extractDefinitions(Element node) {

		final String definitionValueExp = "s:dg/s:d";

		List<String> definitions = new ArrayList<>();
		List<Element> definitionValueNodes = node.selectNodes(definitionValueExp);
		for (Element definitionValueNode : definitionValueNodes) {
			String definition = definitionValueNode.getTextTrim();
			definitions.add(definition);
		}
		return definitions;
	}

	private List<Usage> extractUsages(Element node) {

		final String usageExp = "s:np/s:ng/s:n";
		final String deinitionExp = "s:nd";
		final String deinitionExp2 = "s:nk";

		List<Usage> usages = new ArrayList<>();
		List<Element> usageNodes = node.selectNodes(usageExp);
		for (Element usageNode : usageNodes) {
			Usage newUsage = new Usage();
			newUsage.setValue(usageNode.getTextTrim());
			if (usageNode.hasMixedContent()) {
				Element definitionNode = (Element) usageNode.selectSingleNode(deinitionExp);
				if (definitionNode == null) {
					definitionNode = (Element) usageNode.selectSingleNode(deinitionExp2);
				}
				if (definitionNode != null) {
					newUsage.setDefinition(definitionNode.getText());
				}
			}
			usages.add(newUsage);
		}
		return usages;
	}

	private Word extractWordData(Element wordGroupNode, WordData wordData, String guid) throws Exception {

		final String wordExp = "s:m";
		final String wordDisplayMorphExp = "s:vk";
		final String wordVocalFormExp = "s:hld";
		final String homonymNrAttr = "i";
		final String lexemeTypeAttr = "liik";

		Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
		if (wordNode.attributeValue(homonymNrAttr) != null) {
			wordData.homonymNr = Integer.parseInt(wordNode.attributeValue(homonymNrAttr));
		}
		if (wordNode.attributeValue(lexemeTypeAttr) != null) {
			wordData.lexemeType = lexemeTypes.get(wordNode.attributeValue(lexemeTypeAttr));
		}
		String wordDisplayForm = wordNode.getTextTrim();
		String wordValue = cleanUp(wordDisplayForm);
		wordData.value = wordValue;
		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;

		String wordVocalForm = null;
		Element vocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
		if (vocalFormNode != null) {
			wordVocalForm = vocalFormNode.getTextTrim();
		}

		Word word = new Word(wordValue, dataLang, null, null, wordDisplayForm, wordVocalForm, homonymNr, defaultWordMorphCode, guid);

		Element wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(wordDisplayMorphExp);
		if (wordDisplayMorphNode != null) {
			word.setDisplayMorph(displayMorpCodes.get(wordDisplayMorphNode.getTextTrim()));
			if (displayMorpCodes.get(wordDisplayMorphNode.getTextTrim()) == null) {
				logger.warn("Unknown display morph code : {} : {}", wordDisplayMorphNode.getTextTrim(), wordValue);
			}
		}
		return word;
	}

	private List<Paradigm> extractParadigms(Element wordGroupNode, WordData word, Map<String, List<Paradigm>> wordParadigmsMap) {

		final String morphGroupExp = "s:mfp/s:mtg";
		final String inflectionTypeNrExp = "s:mt";

		List<Paradigm> paradigms = new ArrayList<>();
		boolean isAddForms = !wordParadigmsMap.isEmpty();
		List<Element> morphGroupNodes = wordGroupNode.selectNodes(morphGroupExp);
		for (Element morphGroupNode : morphGroupNodes) {
			Element inflectionTypeNrNode = (Element) morphGroupNode.selectSingleNode(inflectionTypeNrExp);
			if (inflectionTypeNrNode != null) {
				Paradigm paradigm = new Paradigm();
				paradigm.setInflectionTypeNr(inflectionTypeNrNode.getTextTrim());
				if (isAddForms) {
					Paradigm paradigmFromMab = fetchParadigmFromMab(word.value, paradigm.getInflectionTypeNr(), morphGroupNode, wordParadigmsMap);
					if (paradigmFromMab != null) {
						paradigm.setForms(paradigmFromMab.getForms());
					}
				}
				paradigms.add(paradigm);
			} else {
				if (isAddForms) {
					Paradigm paradigmFromMab = fetchParadigmFromMab(word.value, null, morphGroupNode, wordParadigmsMap);
					if (paradigmFromMab != null) {
						paradigms.add(paradigmFromMab);
					}
				}
			}
		}
		return paradigms;
	}

	private List<WordData> extractBasicWords(Element node, Long wordId, String reportingId) {

		final String basicWordExp = "s:ps";
		final String homonymNrAttr = "i";

		List<WordData> basicWords = new ArrayList<>();
		List<Element> basicWordNodes = node.selectNodes(basicWordExp);
		for (Element basicWordNode : basicWordNodes) {
			WordData basicWord = new WordData();
			basicWord.id = wordId;
			basicWord.value = cleanUp(basicWordNode.getTextTrim());
			basicWord.reportingId = reportingId;
			if (basicWordNode.attributeValue(homonymNrAttr) != null) {
				basicWord.homonymNr = Integer.parseInt(basicWordNode.attributeValue(homonymNrAttr));
			}
			basicWords.add(basicWord);
		}
		return basicWords;
	}

	private String extractGuid(Element node) {

		final String articleGuidExp = "s:G";

		Element guidNode = (Element) node.selectSingleNode(articleGuidExp);
		return guidNode != null ? StringUtils.lowerCase(guidNode.getTextTrim()) : null;
	}

	private String extractReporingId(Element node) {

		final String reportingIdExp = "s:P/s:mg/s:m"; // use first word as id for reporting

		Element reportingIdNode = (Element) node.selectSingleNode(reportingIdExp);
		String reportingId = reportingIdNode != null ? cleanUp(reportingIdNode.getTextTrim()) : "";
		return reportingId;
	}

	private WordData createDefaultWordFrom(String wordValue, String displayForm) throws Exception {

		WordData createdWord = new WordData();
		createdWord.value = wordValue;
		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;
		Word word = new Word(wordValue, dataLang, null, null, displayForm, null, homonymNr, defaultWordMorphCode, null);
		createdWord.id = saveWord(word, null, null, null);
		return createdWord;
	}

	private Long findExistingMeaningId(Context context, WordData newWord, List<String> definitions) {

		String definition = definitions.isEmpty() ? null : definitions.get(0);
		Optional<SynonymData> existingSynonym = context.synonyms.stream()
				.filter(s -> newWord.value.equals(s.word) && newWord.homonymNr == s.homonymNr && Objects.equals(definition, s.definition))
				.findFirst();
		return existingSynonym.orElse(new SynonymData()).meaningId;
	}

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words, String reportingId) throws Exception {

		Long wordId = null;
		if (words.size() == 1) {
			wordId = words.get(0).id;
		} else if (words.size() > 1) {
			Optional<WordData> matchingWord = words.stream().filter(w -> w.homonymNr == homonymNr).findFirst();
			if (matchingWord.isPresent()) {
				wordId = matchingWord.get().id;
			}
		}
		if (wordId == null) {
			logger.debug("No matching word was found for {} word {}, {}", reportingId, wordValue, homonymNr);
			writeToLogFile(reportingId, "Ei leitud sihtsõna", wordValue + " : " + homonymNr);
		}
		return wordId;
	}

	private Paradigm fetchParadigmFromMab(String wordValue, String inflectionTypeNr, Element node, Map<String, List<Paradigm>> wordParadigmsMap) {

		final String formsNodeExp = "s:mv";

		List<Paradigm> paradigms = wordParadigmsMap.get(wordValue);
		if (CollectionUtils.isEmpty(paradigms)) {
			return null;
		}

		if (isNotEmpty(inflectionTypeNr)) {
			long nrOfParadigmsMatchingInflectionType = paradigms.stream().filter(p -> Objects.equals(p.getInflectionTypeNr(), inflectionTypeNr)).count();
			if (nrOfParadigmsMatchingInflectionType == 1) {
				return paradigms.stream().filter(p -> Objects.equals(p.getInflectionTypeNr(), inflectionTypeNr)).findFirst().get();
			}
		}

		Element formsNode = (Element) node.selectSingleNode(formsNodeExp);
		if (formsNode == null) {
			return null;
		}
		// FIXME: 20.12.2017 its actually lot more complicated logic, change it when we get documentation about it
		List<String> formValues = Arrays.stream(formsNode.getTextTrim().split(",")).map(String::trim).collect(Collectors.toList());
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

	private String cleanUp(String value) {
		return replaceChars(value, formStrCleanupChars, "");
	}

	private void writeToLogFile(String reportingId, String message, String values) throws Exception {
		if (reportingEnabled) {
			String logMessage = String.join(String.valueOf(CSV_SEPARATOR), asList(reportingId, message, values));
			reportComposer.append(logMessage);
		}
	}

	private void setActivateReport(String reportName) {
		if (reportComposer != null) {
			reportComposer.setActiveStream(reportName);
		}
	}

	private class WordData {
		Long id;
		String value;
		int homonymNr = 0;
		String reportingId;
		String lexemeType;
		List<PosData> posCodes = new ArrayList<>();
	}

	private class PosData {
		String code;
		String processStateCode;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			PosData posData = (PosData) o;
			return Objects.equals(code, posData.code);
		}

		@Override
		public int hashCode() {
			return Objects.hash(code);
		}
	}

	private class SynonymData {
		String word;
		String displayForm;
		Long meaningId;
		int homonymNr = 0;
		String reportingId;
		String definition;
	}

	private class Context {
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> basicWords = new ArrayList<>();
		Count wordDuplicateCount = new Count();
		List<SynonymData> synonyms = new ArrayList<>();
	}

}
