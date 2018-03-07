package eki.ekilex.runner;

import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Government;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageMeaning;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.MabService;
import eki.ekilex.service.ReportComposer;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.replaceChars;

@Component
public class Ss1LoaderRunner extends AbstractLoaderRunner {

	private final static String dataLang = "est";
	private final static String dataset = "ss1";
	private final static String formStrCleanupChars = ".()¤:_|[]̄̆̇’\"'`´–+=";
	private final static String defaultWordMorphCode = "SgN";
	private final static String defaultGovernmentValue = "-";
	private final static String latinLang = "lat";
	private final static String lexemeTypeFormula = "valem";

	private final static String sqlWordLexemesByDataset = "select l.* from " + LEXEME + " l where l.word_id = :wordId and l.dataset_code = :dataset";
	private final static String sqlWordLexemesByMeaningAndDataset =
			"select l.* from " + LEXEME + " l where l.word_id = :wordId and l.dataset_code = :dataset and l.meaning_id = :meaningId";

	private final static String LEXEME_RELATION_BASIC_WORD = "head";
	private final static String LEXEME_RELATION_ABBREVIATION = "lyh";
	private final static String LEXEME_RELATION_SUB_WORD = "mm";

	private final static String MEANING_RELATION_ANTONYM = "ant";
	private final static String MEANING_RELATION_COHYPONYM = "cohyponym";

	private final static String WORD_RELATION_DERIVATIVE = "deriv";

	private final static String ARTICLES_REPORT_NAME = "keywords";
	private final static String BASIC_WORDS_REPORT_NAME = "basic_words";
	private final static String SUBWORDS_REPORT_NAME = "subkeywords";
	private final static String SYNONYMS_REPORT_NAME = "synonyms";
	private final static String ANTONYMS_REPORT_NAME = "antonyms";
	private final static String ABBREVIATIONS_REPORT_NAME = "abbreviations";
	private final static String COHYPONYMS_REPORT_NAME = "cohyponyms";
	private final static String TOKENS_REPORT_NAME = "tokens";
	private final static String DESCRIPTIONS_REPORT_NAME = "keywords_descriptions";
	private final static String MEANINGS_REPORT_NAME = "keywords_meanings";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private ReportComposer reportComposer;
	private boolean reportingEnabled;
	private boolean reportingPaused;

	private Map<String, String> lexemeTypes;
	private Map<String, String> posCodes;
	private Map<String, String> processStateCodes;
	private Map<String, String> displayMorpCodes;
	private Map<String, String> frequencyGroupCodes;
	private String lexemeTypeAbbreviation;
	private String lexemeTypeToken;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Autowired
	private MabService mabService;

	@Override
	void initialise() throws Exception {
		lexemeTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_LIIKTYYP);
		lexemeTypeAbbreviation = lexemeTypes.get("l");
		lexemeTypeToken = lexemeTypes.get("th");
		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
		processStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_ASTYYP);
		displayMorpCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_VKTYYP);
		frequencyGroupCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_MSAGTYYP);
	}

	@Transactional
	public void execute(
			String dataXmlFilePath,
			boolean isAddReporting) throws Exception {

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		reportingEnabled = isAddReporting;
		if (reportingEnabled) {
			reportComposer = new ReportComposer("SS1 import", ARTICLES_REPORT_NAME, BASIC_WORDS_REPORT_NAME, SYNONYMS_REPORT_NAME,
					ANTONYMS_REPORT_NAME, ABBREVIATIONS_REPORT_NAME, COHYPONYMS_REPORT_NAME, TOKENS_REPORT_NAME,
					DESCRIPTIONS_REPORT_NAME, MEANINGS_REPORT_NAME, SUBWORDS_REPORT_NAME);
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
			processArticle(articleNode, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);

		processBasicWords(context);
		processSubWords(context);
		processDerivativeWords(context);
		processSynonymsNotFoundInImportFile(context);
		processAbbreviations(context);
		processTokens(context);
		processFormulas(context);
		processLatinTerms(context);
		processAntonyms(context);
		processCohyponyms(context);

		logger.debug("Found {} word duplicates", context.wordDuplicateCount);

		if (reportComposer != null) {
			reportComposer.end();
		}
		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	@Transactional
	void processArticle(Element articleNode, Context context) throws Exception {

		final String articleHeaderExp = "s:P";
		final String articleBodyExp = "s:S";

		String guid = extractGuid(articleNode);
		String reportingId = extractReporingId(articleNode);
		List<WordData> newWords = new ArrayList<>();

		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		processArticleHeader(reportingId, headerNode, newWords, context, guid);

		List<CommentData> comments = extractArticleComments(articleNode);

		Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
		if (contentNode != null) {
			processArticleContent(reportingId, contentNode, newWords, context, comments);
		}
		context.importedWords.addAll(newWords);
	}

	private void processLatinTerms(Context context) throws Exception {

		logger.debug("Found {} latin terms <s:ld>.", context.latinTermins.size());
		logger.debug("Processing started.");
		reportingPaused = true;

		Count newLatinTermWordCount = processLexemeToWord(context, context.latinTermins, null, "Ei leitud ladina terminit, loome uue", latinLang);

		reportingPaused = false;
		logger.debug("Latin terms created {}", newLatinTermWordCount.getValue());
		logger.debug("Latin term import done.");
	}

	private void processFormulas(Context context) throws Exception {

		logger.debug("Found {} formulas <s:val>.", context.formulas.size());
		logger.debug("Processing started.");
		reportingPaused = true;

		Count newFormulaWordCount = processLexemeToWord(context, context.formulas, lexemeTypeFormula, "Ei leitud valemit, loome uue", dataLang);

		reportingPaused = false;
		logger.debug("Formula words created {}", newFormulaWordCount.getValue());
		logger.debug("Formulas import done.");
	}

	private void processTokens(Context context) throws Exception {

		logger.debug("Found {} tokens.", context.tokens.size());
		logger.debug("Processing started.");
		setActivateReport(TOKENS_REPORT_NAME);
		writeToLogFile("Tähiste töötlus <s:ths>", "", "");

		Count newTokenWordCount = processLexemeToWord(context, context.tokens, lexemeTypeToken, "Ei leitud tähist, loome uue", dataLang);

		logger.debug("Token words created {}", newTokenWordCount.getValue());
		logger.debug("Tokens import done.");
	}

	private Count processLexemeToWord(Context context, List<LexemeToWordData> items, String defaultLexemeType, String logMessage, String lang) throws Exception {
		Count newWordCount = new Count();
		for (LexemeToWordData itemData : items) {
			boolean isImported = context.importedWords.stream().anyMatch(w -> itemData.word.equals(w.value));
			if (!isImported) {
				WordData newWord = createDefaultWordFrom(itemData.word, itemData.displayForm, lang, null);
				context.importedWords.add(newWord);
				newWordCount.increment();
				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(newWord.id);
				lexeme.setMeaningId(itemData.meaningId);
				lexeme.setLevel1(itemData.lexemeLevel1);
				lexeme.setLevel2(1);
				lexeme.setLevel3(1);
				lexeme.setType(itemData.lexemeType == null ? defaultLexemeType : itemData.lexemeType);
				createLexeme(lexeme, dataset);
				if (!reportingPaused) {
					logger.debug("new word created : {}", itemData.word);
				}
				writeToLogFile(itemData.reportingId, logMessage, itemData.word);
			}
		}
		return newWordCount;
	}

	private void processAbbreviations(Context context) throws Exception {

		logger.debug("Found {} abbreviations <s:lyh> and <s:lhx>.", context.abbreviations.size());
		logger.debug("Processing started.");
		setActivateReport(ABBREVIATIONS_REPORT_NAME);
		writeToLogFile("Lühendite töötlus <s:lyh> ja <s:lhx>", "", "");

		Count newAbbreviationFullWordCount = processLexemeToWord(context, context.abbreviationFullWords, null, "Ei leitud sõna, loome uue", dataLang);
		Count newAbbreviationWordCount = processLexemeToWord(context, context.abbreviations, lexemeTypeAbbreviation, "Ei leitud lühendit, loome uue", dataLang);
		createLexemeRelations(context, context.abbreviations, LEXEME_RELATION_ABBREVIATION, "Ei leitud ilmikut lühendile");
		for (LexemeToWordData abbreviation : context.abbreviations) {
			String abbreviationFullWord = context.meanings.stream().filter(m -> m.word.equals(abbreviation.word)).findFirst().get().meaningWord;
			boolean hasFullWord = context.abbreviationFullWords.stream().anyMatch(a -> a.word.equals(abbreviationFullWord));
			if (!hasFullWord) {
				logger.debug("{} : Abbreviation '{}' do not have connected full word, tag <s:lhx> is missing", abbreviation.reportingId, abbreviation.word);
				writeToLogFile(abbreviation.reportingId, "Lühend ei ole seotud täis nimega, tag <s:lhx> puudub.", abbreviation.word);
			}
		}

		logger.debug("Words created {}", newAbbreviationFullWordCount.getValue());
		logger.debug("Abbreviation words created {}", newAbbreviationWordCount.getValue());
		logger.debug("Abbreviations import done.");
	}

	private void processSynonymsNotFoundInImportFile(Context context) throws Exception {

		logger.debug("Found {} synonyms <s:syn>", context.synonyms.size());
		logger.debug("Processing started.");
		setActivateReport(SYNONYMS_REPORT_NAME);
		writeToLogFile("Sünonüümide töötlus <s:syn>", "", "");

		Count newSynonymWordCount = processLexemeToWord(context, context.synonyms, null, "sünonüümi ei letud, lisame sõna", dataLang);

		logger.debug("Synonym words created {}", newSynonymWordCount.getValue());
		logger.debug("Synonyms import done.");
	}

	private void processCohyponyms(Context context) throws Exception {

		logger.debug("Found {} cohyponyms <s:kyh>.", context.cohyponyms.size());
		logger.debug("Processing started.");
		setActivateReport(COHYPONYMS_REPORT_NAME);
		writeToLogFile("Kaashüponüümide töötlus <s:kyh>", "", "");
		createMeaningRelations(context, context.cohyponyms, MEANING_RELATION_COHYPONYM, "Ei leitud mõistet kaashüponüümile");
		logger.debug("Cohyponyms import done.");
	}

	private void processAntonyms(Context context) throws Exception {

		logger.debug("Found {} antonyms <s:ant>.", context.antonyms.size());
		logger.debug("Processing started.");
		setActivateReport(ANTONYMS_REPORT_NAME);
		writeToLogFile("Antonüümide töötlus <s:ant>", "", "");
		createMeaningRelations(context, context.antonyms, MEANING_RELATION_ANTONYM, "Ei leitud mõistet antonüümile");
		logger.debug("Antonyms import done.");
	}

	private void createMeaningRelations(Context context, List<WordToMeaningData> items, String meaningRelationType, String logMessage) throws Exception {

		for (WordToMeaningData item : items) {
			Optional<WordToMeaningData> connectedItem = items.stream()
				.filter(i -> Objects.equals(item.word, i.meaningWord) &&
							 Objects.equals(item.homonymNr, i.meaningHomonymNr) &&
							 Objects.equals(item.lexemeLevel1, i.meaningLevel1))
				.findFirst();
			if (connectedItem.isPresent()) {
				createMeaningRelation(item.meaningId, connectedItem.get().meaningId, meaningRelationType);
			} else {
				List<WordData> existingWords = context.importedWords.stream().filter(w -> item.word.equals(w.value)).collect(Collectors.toList());
				Long wordId = getWordIdFor(item.word, item.homonymNr, existingWords, item.meaningWord);
				if (!existingWords.isEmpty() && wordId != null) {
					Map<String, Object> params = new HashMap<>();
					params.put("wordId", wordId);
					params.put("dataset", dataset);
					try {
						List<Map<String, Object>> lexemeObjects = basicDbService.queryList(sqlWordLexemesByDataset, params);
						Optional<Map<String, Object>> lexemeObject =
								lexemeObjects.stream().filter(l -> (Integer)l.get("level1") == item.lexemeLevel1).findFirst();
						if (lexemeObject.isPresent()) {
							createMeaningRelation(item.meaningId, (Long) lexemeObject.get().get("meaning_id"), meaningRelationType);
						} else {
							logger.debug("Meaning not found for word : {}, lexeme level1 : {}.", item.word, item.lexemeLevel1);
							writeToLogFile(item.meaningWord, logMessage, item.word + ", level1 " + item.lexemeLevel1);
						}
					} catch (Exception e) {
						logger.error("{} | {} | {}", e.getMessage(), item.word, wordId);
					}
				}
			}
		}
	}

	private void createLexemeRelations(Context context, List<LexemeToWordData> items, String lexemeRelationType, String logMessage) throws Exception {

		for (LexemeToWordData itemData : items) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> itemData.word.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(itemData.word, itemData.homonymNr, existingWords, itemData.reportingId);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("wordId", wordId);
				params.put("dataset", dataset);
				try {
					List<Map<String, Object>> lexemeObjects = basicDbService.queryList(sqlWordLexemesByDataset, params);
					Optional<Map<String, Object>> lexemeObject =
							lexemeObjects.stream().filter(l -> (Integer)l.get("level1") == itemData.lexemeLevel1).findFirst();
					if (lexemeObject.isPresent()) {
						createLexemeRelation(itemData.lexemeId, (Long) lexemeObject.get().get("id"), lexemeRelationType);
					} else {
						logger.debug("Lexeme not found for word : {}, lexeme level1 : {}.", itemData.word, itemData.lexemeLevel1);
						writeToLogFile(itemData.reportingId, logMessage, itemData.word + ", level1 " + itemData.lexemeLevel1);
					}
				} catch (Exception e) {
					logger.error("{} | {} | {}", e.getMessage(), itemData.word, wordId);
				}
			}
		}
	}

	private void processDerivativeWords(Context context) throws Exception {
		logger.debug("Found {} derivatives <s:ssp/s:mmg/s:mm>.", context.derivativeWords.size());
		logger.debug("Processing started.");
		reportingPaused = true;
		Count newWordsCounter = new Count();
		List<WordData> importedDerivatives = new ArrayList<>();
		for (WordData derivative : context.derivativeWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> derivative.value.equals(w.value)).collect(Collectors.toList());
			Long derivativeId = getWordIdFor(derivative.value, derivative.homonymNr, existingWords, derivative.reportingId);
			if (derivativeId != null) {
				logger.debug("derivative found for {} : {}", derivative.reportingId, derivative.value);
			} else {
				existingWords = importedDerivatives.stream().filter(w -> derivative.value.equals(w.value)).collect(Collectors.toList());
				derivativeId = getWordIdFor(derivative.value, derivative.homonymNr, existingWords, derivative.reportingId);
			}
			if (derivativeId == null) {
				WordData newWord = createDefaultWordFrom(derivative.value, derivative.value, dataLang, derivative.displayMorph);
				derivativeId = newWord.id;
				newWordsCounter.increment();
			}
			createWordRelation(derivative.id, derivativeId, WORD_RELATION_DERIVATIVE);
		}
		context.importedWords.addAll(importedDerivatives);
		reportingPaused = false;
		logger.debug("new words created {}.", newWordsCounter.getValue());
		logger.debug("Derivatives processing done.");
	}

	private void processSubWords(Context context) throws Exception {
		logger.debug("Found {} sub words <s:mm>.", context.subWords.size());
		logger.debug("Processing started.");
		setActivateReport(SUBWORDS_REPORT_NAME);
		writeToLogFile("Alammärksõna seoste töötlus <s:mm>", "", "");

		Count newWordsCounter = new Count();
		for (WordData subWord : context.subWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> subWord.value.equals(w.value)).collect(Collectors.toList());
			Long subWordId = getWordIdFor(subWord.value, subWord.homonymNr, existingWords, subWord.reportingId);
			if (subWordId == null) {
				WordData newWord = createDefaultWordFrom(subWord.value, subWord.value, dataLang, subWord.displayMorph);
				subWordId = newWord.id;
				newWord.homonymNr = subWord.homonymNr;
				context.importedWords.add(newWord);
				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(newWord.id);
				lexeme.setMeaningId(subWord.meaningId);
				lexeme.setLevel1(1);
				lexeme.setLevel2(1);
				lexeme.setLevel3(1);
				lexeme.setType(subWord.lexemeType);
				lexeme.setFrequencyGroup(subWord.frequencyGroup);
				Long lexemeId = createLexeme(lexeme, dataset);
				if (subWord.government != null) {
					createLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, subWord.government, null);
				}
				logger.debug("new word created : {}", subWord.value);
				newWordsCounter.increment();
			}

			Map<String, Object> params = new HashMap<>();
			params.put("wordId", subWord.id);
			params.put("dataset", dataset);
			params.put("meaningId", subWord.meaningId);
			List<Map<String, Object>> mainWordLexemes = basicDbService.queryList(sqlWordLexemesByMeaningAndDataset, params);
			for (Map<String, Object> mainWordLexeme : mainWordLexemes) {
				params.clear();
				params.put("wordId", subWordId);
				params.put("dataset", dataset);
				List<Map<String, Object>> subWordLexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
				for (Map<String, Object> subWordLexeme : subWordLexemes) {
					createLexemeRelation((Long) mainWordLexeme.get("id"), (Long) subWordLexeme.get("id"), LEXEME_RELATION_SUB_WORD);
				}
			}
		}

		logger.debug("Sub words created {}", newWordsCounter.getValue());
		logger.debug("Sub words processing done.");
	}

	void processBasicWords(Context context) throws Exception {

		logger.debug("Found {} basic words <s:ps>.", context.basicWords.size());
		logger.debug("Processing started.");
		setActivateReport(BASIC_WORDS_REPORT_NAME);
		writeToLogFile("Märksõna põhisõna seoste töötlus <s:ps>", "", "");

		for (WordData basicWord : context.basicWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> basicWord.value.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(basicWord.value, basicWord.homonymNr, existingWords, basicWord.reportingId);
			if (wordId != null) {
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

	private void processArticleContent(
			String reportingId, Element contentNode, List<WordData> newWords, Context context, List<CommentData> comments) throws Exception {

		final String meaningNumberGroupExp = "s:tp";
		final String lexemeLevel1Attr = "tnr";
		final String meaningGroupExp = "s:tg";
		final String meaningPosCodeExp = "s:grg/s:sl";
		final String meaningExternalIdExp = "s:tpid";

		List<WordData> derivativeWords = extractDerivativeWords(contentNode, newWords);
		context.derivativeWords.addAll(derivativeWords);

		List<Element> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
		for (Element meaningNumberGroupNode : meaningNumberGroupNodes) {
			String lexemeLevel1Str = meaningNumberGroupNode.attributeValue(lexemeLevel1Attr);
			Integer lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);
			List<Element> meanigGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
			Element meaningExternalIdNode = (Element) meaningNumberGroupNode.selectSingleNode(meaningExternalIdExp);
			String meaningExternalId = meaningExternalIdNode == null ? null : meaningExternalIdNode.getTextTrim();

			int lexemeLevel2 = 0;
			for (Element meaningGroupNode : meanigGroupNodes) {
				lexemeLevel2++;
				List<UsageMeaning> usages = extractUsages(meaningGroupNode);
				List<String> definitions = extractDefinitions(meaningGroupNode);
				List<PosData> meaningPosCodes = extractPosCodes(meaningGroupNode, meaningPosCodeExp);
				List<String> importantNotes = extractImportantNotes(meaningGroupNode);
				List<WordData> subWords = extractSubWords(meaningGroupNode, newWords.get(0));
				List<String> adviceNotes = extractAdviceNotes(meaningGroupNode);

				Long meaningId;
				List<String> definitionsToAdd = new ArrayList<>();
				List<String> definitionsToCache = new ArrayList<>();

				List<LexemeToWordData> meaningSynonyms = extractSynonyms(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningAbbreviations = extractAbbreviations(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningAbbreviationFullWords = extractAbbreviationFullWords(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningTokens = extractTokens(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningFormulas = extractFormulas(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningLatinTerms = extractLatinTerms(meaningGroupNode, reportingId);
				List<LexemeToWordData> connectedWords =
						Stream.of(
								meaningSynonyms.stream(),
								meaningAbbreviations.stream(),
								meaningAbbreviationFullWords.stream(),
								meaningTokens.stream(),
								meaningFormulas.stream(),
								meaningLatinTerms.stream()
						).flatMap(i -> i).collect(toList());
				WordToMeaningData meaningData = findExistingMeaning(context, newWords.get(0), lexemeLevel1, connectedWords, definitions);
				if (meaningData == null) {
					Meaning meaning = new Meaning();
					meaningId = createMeaning(meaning);
					definitionsToAdd.addAll(definitions);
					definitionsToCache.addAll(definitions);
				} else {
					meaningId = meaningData.meaningId;
					validateMeaning(meaningData, definitions, reportingId);
					definitionsToAdd = definitions.stream().filter(def -> !meaningData.meaningDefinitions.contains(def)).collect(toList());
					meaningData.meaningDefinitions.addAll(definitionsToAdd);
					definitionsToCache.addAll(meaningData.meaningDefinitions);
				}
				if (!definitionsToAdd.isEmpty()) {
					for (String definition : definitionsToAdd) {
						createDefinition(meaningId, definition, dataLang, dataset);
					}
					if (definitionsToAdd.size() > 1) {
						writeToLogFile(DESCRIPTIONS_REPORT_NAME, reportingId, "Leitud rohkem kui üks seletus <s:d>", newWords.get(0).value);
					}
				}
				List<WordToMeaningData> meaningAntonyms = extractAntonyms(meaningGroupNode, meaningId, newWords.get(0), lexemeLevel1, reportingId);
				context.antonyms.addAll(meaningAntonyms);
				List<WordToMeaningData> meaningCohyponyms = extractCohyponyms(meaningGroupNode, meaningId, newWords.get(0), lexemeLevel1, reportingId);
				context.cohyponyms.addAll(meaningCohyponyms);
				cacheMeaningRelatedData(context, meaningId, definitionsToCache, newWords.get(0), lexemeLevel1,
						subWords, meaningSynonyms, meaningAbbreviations, meaningAbbreviationFullWords, meaningTokens, meaningFormulas, meaningLatinTerms);

				if (isNotEmpty(meaningExternalId)) {
					createMeaningFreeform(meaningId, FreeformType.MEANING_EXTERNAL_ID, meaningExternalId);
				}
				List<String> registers = extractRegisters(meaningGroupNode);
				processSemanticData(meaningGroupNode, meaningId);
				processDomains(meaningGroupNode, meaningId);

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
					lexeme.setFrequencyGroup(newWordData.frequencyGroup);
					Long lexemeId = createLexeme(lexeme, dataset);
					if (lexemeId != null) {
						saveGovernmentsAndUsages(meaningGroupNode, lexemeId, usages);
						savePosAndDeriv(lexemeId, newWordData, meaningPosCodes, reportingId);
						saveGrammars(meaningGroupNode, lexemeId, newWordData);
						saveRegisters(lexemeId, registers);
						saveImportantNotes(lexemeId, importantNotes);
						saveAdviceNotes(lexemeId, adviceNotes);
						saveComments(lexemeId, comments);
						for (LexemeToWordData meaningAbbreviation : meaningAbbreviations) {
							LexemeToWordData abbreviationData = meaningAbbreviation.copy();
							abbreviationData.lexemeId = lexemeId;
							context.abbreviations.add(abbreviationData);
						}
					}
				}
			}
		}
	}

	private void saveComments(Long lexemeId, List<CommentData> comments) throws Exception {
		for (CommentData comment : comments) {
			Long commentFreeformId = createLexemeFreeform(lexemeId, FreeformType.PRIVATE_NOTE, comment.value, dataLang);
			createFreeformTextOrDate(FreeformType.CREATED_BY, commentFreeformId, comment.author, dataLang);
			Long valueLong = dateFormat.parse(comment.createdAt).getTime();
			Timestamp valueTs = new Timestamp(valueLong);
			createFreeformTextOrDate(FreeformType.CREATED_ON, commentFreeformId, valueTs, dataLang);
		}
	}

	private void saveAdviceNotes(Long lexemeId, List<String> notes) throws Exception {
		for (String note : notes) {
			createLexemeFreeform(lexemeId, FreeformType.ADVICE_NOTE, note, dataLang);
		}
	}

	private void saveImportantNotes(Long lexemeId, List<String> notes) throws Exception {
		for (String note : notes) {
			createLexemeFreeform(lexemeId, FreeformType.IMPORTANT_NOTE, note, dataLang);
		}
	}

	private void cacheMeaningRelatedData(
			Context context, Long meaningId, List<String> definitions, WordData keyword, int level1,
			List<WordData> subWords,
			List<LexemeToWordData> synonyms,
			List<LexemeToWordData> abbreviations,
			List<LexemeToWordData> abbreviationFullWords,
			List<LexemeToWordData> tokens,
			List<LexemeToWordData> formulas,
			List<LexemeToWordData> latinTerms
			) {
		subWords.forEach(data -> data.meaningId = meaningId);
		context.subWords.addAll(subWords);

		synonyms.forEach(data -> data.meaningId = meaningId);
		context.synonyms.addAll(synonyms);

		// abbreviations need also lexemeId, but this is added later, so we add them to context after assigning lexemeId
		abbreviations.forEach(data -> data.meaningId = meaningId);

		abbreviationFullWords.forEach(data -> data.meaningId = meaningId);
		context.abbreviationFullWords.addAll(abbreviationFullWords);

		tokens.forEach(data -> data.meaningId = meaningId);
		context.tokens.addAll(tokens);

		formulas.forEach(data -> data.meaningId = meaningId);
		context.formulas.addAll(formulas);

		latinTerms.forEach(data -> data.meaningId = meaningId);
		context.latinTermins.addAll(latinTerms);

		context.meanings.stream()
				.filter(m -> Objects.equals(m.meaningId, meaningId))
				.forEach(m -> {m.meaningDefinitions.clear(); m.meaningDefinitions.addAll(definitions);});
		List<WordData> words = new ArrayList<>();
		words.add(keyword);
		words.addAll(subWords);
		words.forEach(word -> {
			context.meanings.addAll(convertToMeaningData(synonyms, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(abbreviations, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(abbreviationFullWords, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(tokens, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(formulas, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(latinTerms, word, level1, definitions));
		});
	}

	private void processDomains(Element node, Long meaningId) throws Exception {

		final String domainOrigin = "bolan";
		final String domainExp = "s:dg/s:regr/s:v";

		List<String> domainCodes = extractValuesAsStrings(node, domainExp);
		for (String domainCode : domainCodes) {
			Map<String, Object> params = new HashMap<>();
			params.put("meaning_id", meaningId);
			params.put("domain_code", domainCode);
			params.put("domain_origin", domainOrigin);
			basicDbService.createIfNotExists(MEANING_DOMAIN, params);
		}
	}

	private void processSemanticData(Element node, Long meaningId) throws Exception {

		final String semanticTypeExp = "s:semg/s:st";
		final String semanticTypeGroupAttr = "sta";
		final String systematicPolysemyPatternExp = "s:semg/s:spm";

		List<Element> semanticTypeNodes = node.selectNodes(semanticTypeExp);
		for (Element semanticTypeNode : semanticTypeNodes) {
			String semanticType = semanticTypeNode.getTextTrim();
			Long meaningFreeformId = createMeaningFreeform(meaningId, FreeformType.SEMANTIC_TYPE, semanticType);
			String semanticTypeGroup = semanticTypeNode.attributeValue(semanticTypeGroupAttr);
			if (isNotEmpty(semanticTypeGroup)) {
				createFreeformTextOrDate(FreeformType.SEMANTIC_TYPE_GROUP, meaningFreeformId, semanticTypeGroup, null);
			}
		}

		List<Element> systematicPolysemyPatternNodes = node.selectNodes(systematicPolysemyPatternExp);
		for (Element systematicPolysemyPatternNode : systematicPolysemyPatternNodes) {
			String systematicPolysemyPattern = systematicPolysemyPatternNode.getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.SYSTEMATIC_POLYSEMY_PATTERN, systematicPolysemyPattern);
		}
	}

	private List<CommentData> extractArticleComments(Element node) {

		final String commentGroupExp = "s:KOM/s:komg";
		final String commentValueExp = "s:kom";
		final String commentAuthorExp = "s:kaut";
		final String commentCreatedExp = "s:kaeg";

		List<CommentData> comments = new ArrayList<>();
		List<Element> commentGroupNodes = node.selectNodes(commentGroupExp);
		for (Element commentGroupNode : commentGroupNodes) {
			CommentData comment = new CommentData();
			comment.value = commentGroupNode.selectSingleNode(commentValueExp).getText();
			comment.author = commentGroupNode.selectSingleNode(commentAuthorExp).getText();
			comment.createdAt = commentGroupNode.selectSingleNode(commentCreatedExp).getText();
			comments.add(comment);
		}
		return comments;
	}

	private List<LexemeToWordData> extractLatinTerms(Element node, String reportingId) throws Exception {

		final String latinTermExp = "s:lig/s:ld";
		return extractLexemeMetadata(node, latinTermExp, null, reportingId);
	}

	private List<LexemeToWordData> extractFormulas(Element node, String reportingId) throws Exception {

		final String tokenExp = "s:lig/s:val";
		return extractLexemeMetadata(node, tokenExp, null, reportingId);
	}

	private List<LexemeToWordData> extractTokens(Element node, String reportingId) throws Exception {

		final String tokenExp = "s:lig/s:ths";
		return extractLexemeMetadata(node, tokenExp, null, reportingId);
	}

	private List<LexemeToWordData> extractAbbreviations(Element node, String reportingId) throws Exception {

		final String abbreviationExp = "s:lig/s:lyh";

		List<LexemeToWordData> abbreviations = extractLexemeMetadata(node, abbreviationExp, lexemeTypeAbbreviation, reportingId);
		abbreviations.forEach(a -> {
			if (a.lexemeType == null) {
				a.lexemeType = lexemeTypeAbbreviation;
			}
		});
		return abbreviations;
	}

	private List<LexemeToWordData> extractAbbreviationFullWords(Element node, String reportingId) throws Exception {

		final String abbreviationFullWordExp = "s:dg/s:lhx";
		return extractLexemeMetadata(node, abbreviationFullWordExp, null, reportingId);
	}

	private void saveRegisters(Long lexemeId, List<String> registerCodes) throws Exception {
		for (String registerCode : registerCodes) {
			createLexemeRegister(lexemeId, registerCode);
		}
	}

	private void saveGrammars(Element node, Long lexemeId, WordData wordData) throws Exception {

		List<String> grammars = extractGrammar(node);
		grammars.addAll(wordData.grammars);
		for (String grammar : grammars) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammar, dataLang);
		}
	}

	private List<String> extractGrammar(Element node) {
		final String grammarValueExp = "s:grg/s:gki";
		return extractValuesAsStrings(node, grammarValueExp);
	}

	//POS - part of speech
	private void savePosAndDeriv(Long lexemeId, WordData newWordData, List<PosData> meaningPosCodes, String reportingId) {

		Set<PosData> lexemePosCodes = new HashSet<>();
		try {
			if (meaningPosCodes.isEmpty()) {
				lexemePosCodes.addAll(newWordData.posCodes);
				if (lexemePosCodes.size() > 1) {
					String posCodesStr = lexemePosCodes.stream().map(p -> p.code).collect(Collectors.joining(","));
//					logger.debug("Found more than one POS code <s:mg/s:sl> : {} : {}", reportingId, posCodesStr);
					writeToLogFile(reportingId, "Märksõna juures leiti rohkem kui üks sõnaliik <s:mg/s:sl>", posCodesStr);
				}
			} else {
				lexemePosCodes.addAll(meaningPosCodes);
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

	private void saveGovernmentsAndUsages(Element node, Long lexemeId, List<UsageMeaning> usageMeanings) throws Exception {

		final String governmentExp = "s:rep/s:reg/s:rek/s:kn";

		List<Element> governmentNodes = node.selectNodes(governmentExp);
		if (governmentNodes.isEmpty()) {
			if (!usageMeanings.isEmpty()) {
				Long governmentId = createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, defaultGovernmentValue);
				for (UsageMeaning usageMeaning : usageMeanings) {
					createUsageMeaning(governmentId, usageMeaning);
				}
			}
		} else {
			for (Element governmentNode : governmentNodes) {
				String governmentValue = governmentNode.getTextTrim();
				Long governmentId = createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, governmentValue);
				for (UsageMeaning usageMeaning : usageMeanings) {
					createUsageMeaning(governmentId, usageMeaning);
				}
			}
		}
	}

	private void createUsageMeaning(Long governmentId, UsageMeaning usageMeaning) throws Exception {
		Long usageMeaningId = createFreeformTextOrDate(FreeformType.USAGE_MEANING, governmentId, "", null);
		for (Usage usage : usageMeaning.getUsages()) {
			Long usageId = createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, usage.getValue(), dataLang);
			if (isNotEmpty(usage.getUsageType())) {
				createFreeformClassifier(FreeformType.USAGE_TYPE, usageId, usage.getUsageType());
			}
			if (isNotEmpty(usage.getAuthor())) {
				Long authorId = createOrSelectPerson(usage.getAuthor());
				FreeformType autorType = isNotEmpty(usage.getAuthorType()) ? FreeformType.USAGE_TRANSLATOR : FreeformType.USAGE_AUTHOR;
				Long authorFreeformId = createFreeformTextOrDate(autorType, usageId, usage.getAuthor(), dataLang);
				createFreeformRefLink(authorFreeformId, ReferenceType.PERSON, authorId, null);
			}
		}
		for (String definition : usageMeaning.getDefinitions()) {
			createFreeformTextOrDate(FreeformType.USAGE_DEFINITION, usageMeaningId, definition, dataLang);
		}
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context, String guid) throws Exception {

		final String wordGroupExp = "s:mg";
		final String wordPosCodeExp = "s:sl";
		final String wordGrammarPosCodesExp = "s:grg/s:sl";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
				wordData.id = saveWord(word, paradigms, dataset, context.wordDuplicateCount);
			}

			List<WordData> basicWordsOfTheWord = extractBasicWords(wordGroupNode, wordData.id, reportingId);
			context.basicWords.addAll(basicWordsOfTheWord);

			List<PosData> posCodes = extractPosCodes(wordGroupNode, wordPosCodeExp);
			wordData.posCodes.addAll(posCodes);
			posCodes = extractPosCodes(wordGroupNode, wordGrammarPosCodesExp);
			wordData.posCodes.addAll(posCodes);

			newWords.add(wordData);
		}
	}

	private List<PosData> extractPosCodes(Element node, String wordPosCodeExp) {

		final String asTyypAttr = "as";

		List<PosData> posCodes = new ArrayList<>();
		List<Element> posCodeNodes = node.selectNodes(wordPosCodeExp);
		for (Element posCodeNode : posCodeNodes) {
			PosData posData = new PosData();
			posData.code = posCodeNode.getTextTrim();
			posData.processStateCode = posCodeNode.attributeValue(asTyypAttr);
			posCodes.add(posData);
		}
		return posCodes;
	}

	private List<WordToMeaningData> extractCohyponyms(Element node, Long meaningId, WordData wordData, int level1, String reportingId) throws Exception {

		final String cohyponymExp = "s:ssh/s:khy";

		List<LexemeToWordData> cohyponyms = extractLexemeMetadata(node, cohyponymExp, null, reportingId);
		cohyponyms.forEach(cohyponym -> cohyponym.meaningId = meaningId);
		return convertToMeaningData(cohyponyms, wordData, level1, Collections.emptyList());
	}

	private List<WordToMeaningData> extractAntonyms(Element node, Long meaningId, WordData wordData, int level1, String reportingId) throws Exception {

		final String antonymExp = "s:ssh/s:ant";

		List<LexemeToWordData> antonyms = extractLexemeMetadata(node, antonymExp, null, reportingId);
		antonyms.forEach(antonym -> antonym.meaningId = meaningId);
		return convertToMeaningData(antonyms, wordData, level1, Collections.emptyList());
	}

	private List<LexemeToWordData> extractLexemeMetadata(Element node, String lexemeMetadataExp, String relationTypeAttr, String reportingId) throws Exception {

		final String lexemeLevel1Attr = "t";
		final String homonymNrAttr = "i";
		final String lexemeTypeAttr = "liik";
		final int defaultLexemeLevel1 = 1;

		List<LexemeToWordData> metadataList = new ArrayList<>();
		List<Element> metadataNodes = node.selectNodes(lexemeMetadataExp);
		for (Element metadataNode : metadataNodes) {
			if (isRestricted(metadataNode)) continue;
			LexemeToWordData lexemeMetadata = new LexemeToWordData();
			lexemeMetadata.displayForm = metadataNode.getTextTrim();
			lexemeMetadata.word = cleanUp(lexemeMetadata.displayForm);
			lexemeMetadata.reportingId = reportingId;
			String lexemeLevel1AttrValue = metadataNode.attributeValue(lexemeLevel1Attr);
			if (StringUtils.isBlank(lexemeLevel1AttrValue)) {
				lexemeMetadata.lexemeLevel1 = defaultLexemeLevel1;
			} else {
				lexemeMetadata.lexemeLevel1 = Integer.parseInt(lexemeLevel1AttrValue);
			}
			String homonymNrAttrValue = metadataNode.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAttrValue)) {
				lexemeMetadata.homonymNr = Integer.parseInt(homonymNrAttrValue);
			}
			if (relationTypeAttr != null) {
				lexemeMetadata.relationType = metadataNode.attributeValue(relationTypeAttr);
			}
			String lexemeTypeAttrValue = metadataNode.attributeValue(lexemeTypeAttr);
			if (StringUtils.isNotBlank(lexemeTypeAttrValue)) {
				lexemeMetadata.lexemeType = lexemeTypes.get(lexemeTypeAttrValue);
				if (lexemeMetadata.lexemeType == null) {
					logger.debug("unknown lexeme type {}", lexemeTypeAttrValue);
					writeToLogFile(reportingId, "Tundmatu märksõnaliik", lexemeTypeAttrValue);
				}
			}
			metadataList.add(lexemeMetadata);
		}
		return metadataList;
	}

	private List<LexemeToWordData> extractSynonyms(Element node, String reportingId) throws Exception {

		final String synonymExp = "s:ssh/s:syn";
		return extractLexemeMetadata(node, synonymExp, null, reportingId);
	}

	private List<String> extractImportantNotes(Element node) {

		final String registerValueExp = "s:lig/s:nb";
		return extractValuesAsStrings(node, registerValueExp);
	}

	private List<String> extractAdviceNotes(Element node) {

		final String registerValueExp = "s:lig/s:tx";
		return extractValuesAsStrings(node, registerValueExp);
	}

	private List<String> extractRegisters(Element node) {

		final String registerValueExp = "s:dg/s:regr/s:s";
		return extractValuesAsStrings(node, registerValueExp);
	}

	private List<String> extractDefinitions(Element node) {

		final String definitionValueExp = "s:dg/s:d";
		return extractValuesAsStrings(node, definitionValueExp);
	}

	private List<String> extractValuesAsStrings(Element node, String valueExp) {

		List<String> values = new ArrayList<>();
		List<Element> valueNodes = node.selectNodes(valueExp);
		for (Element valueNode : valueNodes) {
			if (!isRestricted(valueNode)) {
				String value = valueNode.getTextTrim();
				values.add(value);
			}
		}
		return values;
	}

	private boolean isRestricted(Element node) {

		final String restrictedAttr = "as";
		String restrictedValue = node.attributeValue(restrictedAttr);
		return asList("ab", "ap").contains(restrictedValue);
	}

	private List<UsageMeaning> extractUsages(Element node) {

		final String usageExp = "s:np/s:ng/s:n";
		final String usageTypeAttr = "nliik";
		final String deinitionExp = "s:nd";
		final String deinitionExp2 = "s:nk";
		final String quotationGroupExp = "s:np/s:cg";
		final String quotationExp = "s:c";
		final String quotationAuhorExp = "s:caut";
		final String quotationAuhorTypeAttr = "aliik";

		List<UsageMeaning> usageMeanings = new ArrayList<>();
		List<Element> usageNodes = node.selectNodes(usageExp);
		for (Element usageNode : usageNodes) {
			UsageMeaning usageMeaning = new UsageMeaning();
			Usage newUsage = new Usage();
			newUsage.setValue(usageNode.getTextTrim());
			if (usageNode.hasMixedContent()) {
				Element definitionNode = (Element) usageNode.selectSingleNode(deinitionExp);
				if (definitionNode == null) {
					definitionNode = (Element) usageNode.selectSingleNode(deinitionExp2);
				}
				if (definitionNode != null) {
					usageMeaning.getDefinitions().add(definitionNode.getText());
				}
			}
			newUsage.setUsageType(usageNode.attributeValue(usageTypeAttr));
			usageMeaning.getUsages().add(newUsage);
			usageMeanings.add(usageMeaning);
		}
		List<Element> quotationGroupNodes = node.selectNodes(quotationGroupExp);
		for (Element quotationGroupNode : quotationGroupNodes) {
			UsageMeaning usageMeaning = new UsageMeaning();
			Usage newUsage = new Usage();
			Element quotationNode = (Element) quotationGroupNode.selectSingleNode(quotationExp);
			Element quotationAutorNode = (Element) quotationGroupNode.selectSingleNode(quotationAuhorExp);
			newUsage.setValue(quotationNode.getTextTrim());
			newUsage.setAuthor(quotationAutorNode.getTextTrim());
			newUsage.setAuthorType(quotationAutorNode.attributeValue(quotationAuhorTypeAttr));
			usageMeaning.getUsages().add(newUsage);
			usageMeanings.add(usageMeaning);
		}
		return usageMeanings;
	}

	private Word extractWordData(Element wordGroupNode, WordData wordData, String guid) throws Exception {

		final String wordExp = "s:m";
		final String wordDisplayMorphExp = "s:vk";
		final String wordVocalFormExp = "s:hld";
		final String homonymNrAttr = "i";
		final String lexemeTypeAttr = "liik";
		final String wordFrequencyGroupExp = "s:msag";

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
		Element frequencyGroupNode = (Element) wordGroupNode.selectSingleNode(wordFrequencyGroupExp);
		if (frequencyGroupNode != null) {
			wordData.frequencyGroup = frequencyGroupCodes.get(frequencyGroupNode.getTextTrim());
		}
		wordData.grammars = extractGrammar(wordGroupNode);
		return word;
	}

	private List<Paradigm> extractParadigms(Element wordGroupNode, WordData word) throws Exception {

		final String morphGroupExp = "s:mfp/s:mtg";

		List<Paradigm> paradigms = new ArrayList<>();
		if (mabService.isMabLoaded() && mabService.paradigmsExist(word.value)) {
			Element morphGroupNode = (Element) wordGroupNode.selectSingleNode(morphGroupExp);
			List<Paradigm> paradigmsFromMab = fetchParadigmsFromMab(word.value, morphGroupNode);
			if (!paradigmsFromMab.isEmpty()) {
				paradigms.addAll(paradigmsFromMab);
			}
		}
		return paradigms;
	}

	private List<WordData> extractDerivativeWords(Element node, List<WordData> mainWords) {

		final String wordGroupExp = "s:ssp/s:mmg";
		final String wordExp = "s:mm";
		final String wordPosCodeExp = "s:sl";
		final String frequencyGroupExp = "s:msag";
		final String homonymNrAttr = "i";

		List<WordData> derivatives = new ArrayList<>();
		List<Element> wordGroupNodes = node.selectNodes(wordGroupExp);
		for (WordData mainWord : mainWords) {
			for (Element wordGroupNode: wordGroupNodes) {
				Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				WordData derivative = new WordData();
				derivative.id = mainWord.id;
				derivative.reportingId = mainWord.reportingId;
				derivative.value = cleanUp(wordNode.getTextTrim());
				if (wordGroupNode.attributeValue(homonymNrAttr) != null) {
					derivative.homonymNr = Integer.parseInt(wordGroupNode.attributeValue(homonymNrAttr));
				}
				Element frequencyGroupNode = (Element) node.selectSingleNode(frequencyGroupExp);
				if (frequencyGroupNode != null) {
					derivative.frequencyGroup = frequencyGroupCodes.get(frequencyGroupNode.getTextTrim());
				}
				List<PosData> posCodes = extractPosCodes(wordGroupNode, wordPosCodeExp);
				derivative.posCodes.addAll(posCodes);
				derivatives.add(derivative);
			}
		}
		return derivatives;
	}

	private List<WordData> extractSubWords(Element node, WordData mainWord) {

		final String subWordExp = "s:mmg/s:mm";
		final String frequencyGroupExp = "s:mmg/s:msag";
		final String displayPosExp = "s:mmg/s:vk";
		final String governmentExp = "s:r";
		final String homonymNrAttr = "i";

		Element frequencyGroupNode = (Element) node.selectSingleNode(frequencyGroupExp);
		String frequencyGroup = null;
		if (frequencyGroupNode != null) {
			frequencyGroup = frequencyGroupCodes.get(frequencyGroupNode.getTextTrim());
		}
		Element displayMorphNode = (Element) node.selectSingleNode(displayPosExp);
		String displayMorph = null;
		if (displayMorphNode != null) {
			displayMorph = displayMorpCodes.get(displayMorphNode.getTextTrim());
			if (displayMorph == null) {
				logger.warn("Unknown display morph code : {} : {}", displayMorphNode.getTextTrim(), mainWord.value);
			}
		}

		List<WordData> subWords = new ArrayList<>();
		List<Element> subWordNodes = node.selectNodes(subWordExp);
		for (Element subWordNode : subWordNodes) {
			WordData subWord = new WordData();
			subWord.id = mainWord.id;
			subWord.value = cleanUp(subWordNode.getTextTrim());
			subWord.reportingId = mainWord.reportingId;
			if (subWordNode.attributeValue(homonymNrAttr) != null) {
				subWord.homonymNr = Integer.parseInt(subWordNode.attributeValue(homonymNrAttr));
			}
			if (subWordNode.hasMixedContent()) {
				Element governmentNode = (Element) subWordNode.selectSingleNode(governmentExp);
				if (governmentNode != null) {
					subWord.government = governmentNode.getTextTrim();
				}
			}
			subWord.frequencyGroup = frequencyGroup;
			subWord.displayMorph = displayMorph;
			subWords.add(subWord);
		}
		return subWords;
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

	private WordData createDefaultWordFrom(String wordValue, String displayForm, String lang, String displayMorph) throws Exception {

		WordData createdWord = new WordData();
		createdWord.value = wordValue;
		int homonymNr = getWordMaxHomonymNr(wordValue, lang) + 1;
		Word word = new Word(wordValue, lang, null, null, displayForm, null, homonymNr, defaultWordMorphCode, null);
		word.setDisplayMorph(displayMorph);
		createdWord.id = saveWord(word, null, null, null);
		return createdWord;
	}

	private WordToMeaningData findExistingMeaning(Context context, WordData newWord, int level1, List<LexemeToWordData> connectedWords,
			List<String> definitions) {

		List<String> connectedWordValues = connectedWords.stream().map(w -> w.word).collect(toList());
		List<WordToMeaningData> existingMeanings = context.meanings.stream()
				.filter(cachedMeaning -> newWord.value.equals(cachedMeaning.word) &&
						newWord.homonymNr == cachedMeaning.homonymNr &&
						level1 == cachedMeaning.lexemeLevel1 &&
						connectedWordValues.contains(cachedMeaning.meaningWord))
				.collect(toList());
		Optional<WordToMeaningData> existingMeaning;
		if (existingMeanings.size() == 1) {
			return existingMeanings.get(0);
		} else {
			existingMeaning = existingMeanings.stream().filter(meaning -> meaning.meaningDefinitions.containsAll(definitions)).findFirst();
		}
		if (!existingMeaning.isPresent() && !connectedWords.isEmpty()) {
			LexemeToWordData connectedWord = connectedWords.get(0);
			existingMeaning = context.meanings.stream()
					.filter(cachedMeaning -> connectedWord.word.equals(cachedMeaning.meaningWord) &&
							connectedWord.homonymNr == cachedMeaning.meaningHomonymNr &&
							connectedWord.lexemeLevel1 == cachedMeaning.meaningLevel1)
					.findFirst();
		}
		return existingMeaning.orElse(null);
	}

	private boolean validateMeaning(WordToMeaningData meaningData, List<String> definitions, String reportingId) throws Exception {

		if (meaningData.meaningDefinitions.isEmpty() || definitions.isEmpty()) {
			return true;
		}
		String definition = definitions.isEmpty() ? null : definitions.get(0);
		String meaningDefinition = meaningData.meaningDefinitions.isEmpty() ? null : meaningData.meaningDefinitions.get(0);
		if (Objects.equals(meaningDefinition, definition)) {
			return true;
		}
//		logger.debug("meanings do not match for word {} | {} | {}", reportingId, definition, meaningDefinition);
		writeToLogFile(MEANINGS_REPORT_NAME, reportingId, "Tähenduse seletused on erinevad", definition + " : " + meaningDefinition);
		return false;
	}

	private List<WordToMeaningData> convertToMeaningData(List<LexemeToWordData> items, WordData meaningWord, int level1, List<String> definitions) {

		List<WordToMeaningData> meanings = new ArrayList<>();
		for (LexemeToWordData item : items) {
			WordToMeaningData meaning = new WordToMeaningData();
			meaning.meaningId = item.meaningId;
			meaning.meaningWord = meaningWord.value;
			meaning.meaningHomonymNr = meaningWord.homonymNr;
			meaning.meaningLevel1 = level1;
			meaning.meaningDefinitions.addAll(definitions);
			meaning.word = item.word;
			meaning.homonymNr = item.homonymNr;
			meaning.lexemeLevel1 = item.lexemeLevel1;
			meanings.add(meaning);
		}
		return meanings;
	}

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words, String reportingId) throws Exception {

		Long wordId = null;
		Optional<WordData> matchingWord = words.stream().filter(w -> w.homonymNr == homonymNr).findFirst();
		if (matchingWord.isPresent()) {
			wordId = matchingWord.get().id;
		}
		if (wordId == null) {
			if (!reportingPaused) {
				logger.debug("No matching word was found for {} word {}, {}", reportingId, wordValue, homonymNr);
			}
			writeToLogFile(reportingId, "Ei leitud sihtsõna", wordValue + " : " + homonymNr);
		}
		return wordId;
	}

	private List<Paradigm> fetchParadigmsFromMab(String wordValue, Element node) throws Exception {

		final String formsNodeExp = "s:mv";
		final String formsNodeExp2 = "s:hev";

		if (mabService.isSingleHomonym(wordValue)) {
			return mabService.getWordParadigms(wordValue);
		}

		List<String> formEndings = extractFormEndings(node, formsNodeExp);
		formEndings.addAll(extractFormEndings(node, formsNodeExp2));
		if (formEndings.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> morphCodesToCheck = asList("SgG", "Inf", "IndPrSg1");
		long bestFormValuesMatchCount = -1;
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : mabService.getWordParadigms(wordValue)) {
			long numberOfMachingEndings = paradigm.getForms().stream()
					.filter(form -> morphCodesToCheck.contains(form.getMorphCode())).map(Form::getValue)
					.filter(formValue -> formEndings.stream().anyMatch(formValue::endsWith))
					.count();
			if (numberOfMachingEndings > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = numberOfMachingEndings;
				matchingParadigm = paradigm;
			}
		}
		Integer matchingHomonymNumber = matchingParadigm.getHomonymNr();
		return mabService.getWordParadigmsForHomonym(wordValue, matchingHomonymNumber);
	}

	private List<String> extractFormEndings(Element node, String formsNodeExp) {

		List<String> formEndings = new ArrayList<>();
		if (node == null) {
			return formEndings;
		}

		Element formsNode = (Element) node.selectSingleNode(formsNodeExp);
		if (formsNode != null) {
			formEndings.addAll(Arrays.stream(formsNode.getTextTrim().split(","))
					.map(v -> v.substring(v.indexOf("-")+1).trim())
					.collect(Collectors.toList()));
		}

		return formEndings;
	}

	private String cleanUp(String value) {
		String cleanedWord = replaceChars(value, formStrCleanupChars, "");
		// FIXME: quick fix for removing subscript tags, better solution would be to use some markup for mathematical and chemical formulas
		return removePattern(cleanedWord, "[&]\\w+[;]");
	}

	private void writeToLogFile(String reportingId, String message, String values) throws Exception {
		writeToLogFile(null, reportingId, message, values);
	}

	private void writeToLogFile(String reportFile, String reportingId, String message, String values) throws Exception {
		if (reportingEnabled && !reportingPaused) {
			String logMessage = String.join(String.valueOf(CSV_SEPARATOR), asList(reportingId, message, values));
			if (reportFile == null) {
				reportComposer.append(logMessage);
			} else {
				reportComposer.append(reportFile, logMessage);
			}
		}
	}

	private void setActivateReport(String reportName) {
		if (reportComposer != null) {
			reportComposer.setActiveStream(reportName);
		}
	}

	private class CommentData {
		String value;
		String author;
		String createdAt;
	}

	private class WordData {
		Long id;
		String value;
		int homonymNr = 0;
		String reportingId;
		String lexemeType;
		List<PosData> posCodes = new ArrayList<>();
		String frequencyGroup;
		List<String> grammars = new ArrayList<>();
		Long meaningId;
		String government;
		String displayMorph;
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

	private class WordToMeaningData {
		String word;
		int homonymNr = 0;
		int lexemeLevel1 = 1;
		Long meaningId;
		List<String> meaningDefinitions = new ArrayList<>();
		String meaningWord;
		int meaningHomonymNr = 0;
		int meaningLevel1 = 1;
	}

	private class LexemeToWordData {
		Long lexemeId;
		String word;
		String displayForm;
		int lexemeLevel1 = 1;
		int homonymNr = 0;
		String relationType;
		Government government;
		List<Usage> usages = new ArrayList<>();
		String reportingId;
		String lexemeType;
		Long meaningId;

		LexemeToWordData copy() {
			LexemeToWordData newData = new LexemeToWordData();
			newData.lexemeId = this.lexemeId;
			newData.word = this.word;
			newData.displayForm = this.displayForm;
			newData.lexemeLevel1 = this.lexemeLevel1;
			newData.homonymNr = this.homonymNr;
			newData.relationType = this.relationType;
			newData.government = this.government;
			newData.reportingId = this.reportingId;
			newData.usages.addAll(this.usages);
			newData.lexemeType = this.lexemeType;
			newData.meaningId = this.meaningId;
			return newData;
		}
	}

	private class Context {
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> basicWords = new ArrayList<>();
		List<WordData> subWords = new ArrayList<>();
		List<WordData> derivativeWords = new ArrayList<>();
		Count wordDuplicateCount = new Count();
		List<LexemeToWordData> synonyms = new ArrayList<>();
		List<WordToMeaningData> antonyms = new ArrayList<>();
		List<LexemeToWordData> abbreviations = new ArrayList<>();
		List<LexemeToWordData> abbreviationFullWords = new ArrayList<>();
		List<WordToMeaningData> cohyponyms = new ArrayList<>();
		List<LexemeToWordData> tokens = new ArrayList<>();
		List<LexemeToWordData> formulas = new ArrayList<>();
		List<LexemeToWordData> latinTermins = new ArrayList<>();
		List<WordToMeaningData> meanings = new ArrayList<>();
	}

}
