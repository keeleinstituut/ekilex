package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.FreeformType;
import eki.common.constant.WordRelationGroupType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class Ss1LoaderRunner extends SsBasedLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Ss1LoaderRunner.class);

	private final static String sqlWordLexemesByDataset = "select l.* from " + LEXEME + " l where l.word_id = :wordId and l.dataset_code = :dataset";
	private final static String sqlWordLexemesByMeaningAndDataset =
			"select l.* from " + LEXEME + " l where l.word_id = :wordId and l.dataset_code = :dataset and l.meaning_id = :meaningId";

	private final static String LEXEME_RELATION_ABBREVIATION = "lyh";

	private final static String MEANING_RELATION_ANTONYM = "ant";
	private final static String MEANING_RELATION_COHYPONYM = "cohyponym";

	private final static String WORD_RELATION_DERIVATIVE = "deriv";
	private final static String WORD_RELATION_DERIVATIVE_BASE = "deriv_base";
	private final static String WORD_RELATION_UNION = "ühend";

	private final static String BASIC_WORDS_REPORT_NAME = "basic_words";
	private final static String SUBWORDS_REPORT_NAME = "subkeywords";
	private final static String SYNONYMS_REPORT_NAME = "synonyms";
	private final static String ANTONYMS_REPORT_NAME = "antonyms";
	private final static String ABBREVIATIONS_REPORT_NAME = "abbreviations";
	private final static String COHYPONYMS_REPORT_NAME = "cohyponyms";
	private final static String TOKENS_REPORT_NAME = "tokens";

	private String wordTypeAbbreviation;
	private String wordTypeToken;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	protected Map<String, String> xpathExpressions() {
		Map<String, String> experssions = new HashMap<>();
		experssions.put("reportingId", "s:P/s:mg/s:m"); // use first word as id for reporting
		experssions.put("word", "s:m");
		experssions.put("wordDisplayMorph", "s:vk");
		experssions.put("wordVocalForm", "s:hld");
		experssions.put("wordFrequencyGroup", "s:msag");
		experssions.put("grammarValue", "s:grg/s:gki");
		experssions.put("morphGroup", "s:mfp/s:mtg");
		experssions.put("formsNode", "s:mv");
		experssions.put("formsNode2", "s:hev");
		experssions.put("domain", "s:dg/s:regr/s:v");
		return experssions;
	}

	@Override
	String getDataset() {
		return "ss1";
	}

	@Override
	void initialise() throws Exception {
		super.initialise();
		wordTypeAbbreviation = wordTypes.get("l");
		wordTypeToken = wordTypes.get("th");
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

		processUnionWords(context);
		processSubWords(context);
		processDerivativeWords(context);
		processSynonymsNotFoundInImportFile(context);
		processAbbreviations(context);
		processTokens(context);
		processLatinTerms(context);
		processAntonyms(context);
		processCohyponyms(context);

		logger.debug("Found {} word duplicates", context.reusedWordCount);

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
		final String articleGuidExp = "s:G";

		String guid = extractGuid(articleNode, articleGuidExp);
		String reportingId = extractReporingId(articleNode);
		List<WordData> newWords = new ArrayList<>();

		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		processArticleHeader(reportingId, headerNode, newWords, context, guid);

		List<CommentData> comments = extractArticleComments(articleNode);

		Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
		if (contentNode != null) {
			processArticleContent(reportingId, contentNode, newWords, context, comments);
			processVariants(newWords);
			processSeries(context, headerNode, newWords);
		}
		context.importedWords.addAll(newWords);
	}

	private void processSeries(Context context, Element headerNode, List<WordData> newWords) throws Exception {

		// check do we have series data in word links xml group
		List<WordData> seriesWordData = extractSeriesWords(headerNode);
		if (seriesWordData.isEmpty()) {
			return;
		}

		seriesWordData.addAll(newWords);
		List<WordSeries> seriesForWords = findSeriesForWords(context, newWords);
		// no series groups, so its first word in series, create group add words and store it for later use
		if (seriesForWords.isEmpty()) {
			Long wordGroup = createWordRelationGroup(WordRelationGroupType.SERIES);
			for (WordData wordData : newWords) {
				createWordRelationGroupMember(wordGroup, wordData.id);
			}
			WordSeries series = new WordSeries();
			series.groupId = wordGroup;
			series.words.addAll(seriesWordData);
			context.series.add(series);
		} else {
			// series groups found, so its next word in group, add it to group
			for (WordSeries series : seriesForWords) {
				for (WordData wordData : newWords) {
					createWordRelationGroupMember(series.groupId, wordData.id);
				}
			}
		}
	}

	private List<WordSeries> findSeriesForWords(Context context, List<WordData> words) {
		return context.series.stream()
				.filter(series ->
					words.stream().allMatch(word ->
							series.words.stream().anyMatch(seriesWord ->
									seriesWord.value.equals(word.value) && seriesWord.homonymNr == word.homonymNr
							)
					)
				)
				.collect(toList());
	}

	private void processVariants(List<WordData> newWords) throws Exception {
		if (isVariant(newWords)) {
			Long wordGroup = createWordRelationGroup(WordRelationGroupType.VARIANTS);
			for (WordData wordData : newWords) {
				createWordRelationGroupMember(wordGroup, wordData.id);
			}
		}
	}

	/*
		If in article header we have more than one word and word type is not series, then they are variants.
	 */
	private boolean isVariant(List<WordData> newWords) {
		return newWords.size() > 1 && !isSeries(newWords);
	}

	private boolean isSeries(List<WordData> newWords) {
		final String wordTypeSeries = "s";
		return newWords.stream().anyMatch(wordData -> Objects.equals(wordTypeSeries, wordData.wordType));
	}

	private List<WordData> extractSeriesWords(Element headerNode) {

		final String wordLinkExp = "s:mvtg/s:mvt";
		final String wordLinkTypeAttr = "mvtl";
		final String wordLinkSeriesType = "srj";
		final String homonymNrAttr = "i";

		List<WordData> seriesWords = new ArrayList<>();
		List<Element> seriesWordNodes = headerNode.selectNodes(wordLinkExp);
		for (Element seriesWordNode : seriesWordNodes) {
			if (Objects.equals(seriesWordNode.attributeValue(wordLinkTypeAttr), wordLinkSeriesType)) {
				WordData seriesWord = new WordData();
				seriesWord.value = cleanUp(seriesWordNode.getTextTrim());
				if (seriesWordNode.attributeValue(homonymNrAttr) != null) {
					seriesWord.homonymNr = Integer.parseInt(seriesWordNode.attributeValue(homonymNrAttr));
				}
				seriesWords.add(seriesWord);
			}
		}
		return seriesWords;
	}

	private void processTokens(Context context) throws Exception {

		logger.debug("Found {} tokens.", context.tokens.size());
		logger.debug("Processing started.");
		setActivateReport(TOKENS_REPORT_NAME);
		writeToLogFile("Tähiste töötlus <s:ths>", "", "");

		Count newTokenWordCount = processLexemeToWord(context, context.tokens, wordTypeToken, "Ei leitud tähist, loome uue", dataLang);

		logger.debug("Token words created {}", newTokenWordCount.getValue());
		logger.debug("Tokens import done.");
	}

	private void processAbbreviations(Context context) throws Exception {

		logger.debug("Found {} abbreviations <s:lyh> and <s:lhx>.", context.abbreviations.size());
		logger.debug("Processing started.");
		setActivateReport(ABBREVIATIONS_REPORT_NAME);
		writeToLogFile("Lühendite töötlus <s:lyh> ja <s:lhx>", "", "");

		Count newAbbreviationFullWordCount = processLexemeToWord(context, context.abbreviationFullWords, null, "Ei leitud sõna, loome uue", dataLang);
		Count newAbbreviationWordCount = processLexemeToWord(context, context.abbreviations, wordTypeAbbreviation, "Ei leitud lühendit, loome uue", dataLang);
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
				Long wordId = getWordIdFor(item.word, item.homonymNr, context.importedWords, item.meaningWord);
				if (wordId != null) {
					Map<String, Object> params = new HashMap<>();
					params.put("wordId", wordId);
					params.put("dataset", getDataset());
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
			Long wordId = getWordIdFor(itemData.word, itemData.homonymNr, context.importedWords, itemData.reportingId);
			if (wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("wordId", wordId);
				params.put("dataset", getDataset());
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
		for (WordData derivative : context.derivativeWords) {
			Long derivativeId = getWordIdFor(derivative.value, derivative.homonymNr, context.importedWords, derivative.reportingId);
			if (derivativeId != null) {
				logger.debug("derivative found for {} : {}", derivative.reportingId, derivative.value);
			} else {
				derivativeId = createWordWithLexeme(context, derivative);
				newWordsCounter.increment();
			}
			createWordRelation(derivative.id, derivativeId, WORD_RELATION_DERIVATIVE);
			createWordRelation(derivativeId, derivative.id, WORD_RELATION_DERIVATIVE_BASE);
		}
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
			Long subWordId = getWordIdFor(subWord.value, subWord.homonymNr, context.importedWords, subWord.reportingId);
			if (subWordId == null) {
				subWordId = createWordWithLexeme(context, subWord);
				newWordsCounter.increment();
			}

			Map<String, Object> params = new HashMap<>();
			params.put("wordId", subWord.id);
			params.put("dataset", getDataset());
			params.put("meaningId", subWord.meaningId);
			List<Map<String, Object>> mainWordLexemes = basicDbService.queryList(sqlWordLexemesByMeaningAndDataset, params);
			for (Map<String, Object> mainWordLexeme : mainWordLexemes) {
				params.clear();
				params.put("wordId", subWordId);
				params.put("dataset", getDataset());
				List<Map<String, Object>> subWordLexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
				for (Map<String, Object> subWordLexeme : subWordLexemes) {
					createLexemeRelation((Long) mainWordLexeme.get("id"), (Long) subWordLexeme.get("id"), LEXEME_RELATION_SUB_WORD);
				}
			}
		}

		logger.debug("Sub words created {}", newWordsCounter.getValue());
		logger.debug("Sub words processing done.");
	}

	private Long createWordWithLexeme(Context context, WordData wordData) throws Exception {

		WordData newWord = createDefaultWordFrom(wordData.value, wordData.value, dataLang, wordData.displayMorph, wordData.wordType, null);
		newWord.homonymNr = wordData.homonymNr;
		context.importedWords.add(newWord);

		Long meaningId = wordData.meaningId == null ? createMeaning() : wordData.meaningId;

		Lexeme lexeme = new Lexeme();
		lexeme.setWordId(newWord.id);
		lexeme.setMeaningId(meaningId);
		lexeme.setLevel1(1);
		lexeme.setLevel2(1);
		lexeme.setLevel3(1);
		lexeme.setFrequencyGroup(wordData.frequencyGroup);
		Long lexemeId = createLexeme(lexeme, getDataset());
		if (!wordData.governments.isEmpty()) {
			for (String government : wordData.governments) {
				createLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government, null);
			}
		}
		return newWord.id;
	}

	void processUnionWords(Context context) throws Exception {

		logger.debug("Found {} union words <s:ps>.", context.unionWords.size());
		logger.debug("Processing started.");
		setActivateReport(BASIC_WORDS_REPORT_NAME);
		writeToLogFile("Märksõna ühendite töötlus <s:ps>", "", "");

		for (WordData unionWord : context.unionWords) {
			Long wordId = getWordIdFor(unionWord.value, unionWord.homonymNr, context.importedWords, unionWord.reportingId);
			if (wordId != null) {
				createWordRelation(wordId, unionWord.id, WORD_RELATION_UNION);
			}
		}
		logger.debug("Union words processing done.");
	}

	private void processArticleContent(
			String reportingId,
			Element contentNode,
			List<WordData> newWords,
			Context context,
			List<CommentData> comments) throws Exception {

		final String meaningNumberGroupExp = "s:tp";
		final String lexemeLevel1Attr = "tnr";
		final String meaningGroupExp = "s:tg";
		final String meaningPosCodeExp = "s:grg/s:sl";
		final String conceptIdExp = "s:tpid";

		List<WordData> derivativeWords = extractDerivativeWords(contentNode, newWords);
		context.derivativeWords.addAll(derivativeWords);

		List<Element> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
		for (Element meaningNumberGroupNode : meaningNumberGroupNodes) {
			String lexemeLevel1Str = meaningNumberGroupNode.attributeValue(lexemeLevel1Attr);
			Integer lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);
			List<Element> meanigGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
			Element conceptIdNode = (Element) meaningNumberGroupNode.selectSingleNode(conceptIdExp);
			String conceptId = conceptIdNode == null ? null : conceptIdNode.getTextTrim();

			int lexemeLevel2 = 0;
			for (Element meaningGroupNode : meanigGroupNodes) {
				lexemeLevel2++;
				List<Usage> usages = extractUsages(meaningGroupNode, conceptId);
				List<String> definitions = extractDefinitions(meaningGroupNode);
				List<PosData> meaningPosCodes = extractPosCodes(meaningGroupNode, meaningPosCodeExp);
				List<String> adviceNotes = extractAdviceNotes(meaningGroupNode);
				List<WordData> subWords = extractSubWords(meaningGroupNode, newWords.get(0));
				List<String> publicNotes = extractPublicNotes(meaningGroupNode);

				Long meaningId;
				List<String> definitionsToAdd = new ArrayList<>();
				List<String> definitionsToCache = new ArrayList<>();

				List<LexemeToWordData> meaningSynonyms = extractSynonyms(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningAbbreviations = extractAbbreviations(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningAbbreviationFullWords = extractAbbreviationFullWords(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningTokens = extractTokens(meaningGroupNode, reportingId);
				List<LexemeToWordData> meaningLatinTerms = extractLatinTerms(meaningGroupNode, reportingId);
				List<LexemeToWordData> connectedWords =
						Stream.of(
								meaningSynonyms.stream(),
								meaningAbbreviations.stream(),
								meaningAbbreviationFullWords.stream(),
								meaningTokens.stream(),
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
						createDefinition(meaningId, definition, dataLang, getDataset());
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
						subWords, meaningSynonyms, meaningAbbreviations, meaningAbbreviationFullWords, meaningTokens, meaningLatinTerms);

				if (isNotBlank(conceptId)) {
					createMeaningFreeform(meaningId, FreeformType.CONCEPT_ID, conceptId);
				}
				List<String> registers = extractRegisters(meaningGroupNode);
				processSemanticData(meaningGroupNode, meaningId);
				processDomains(meaningGroupNode, meaningId, null);

				for (WordData newWordData : newWords) {
					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(newWordData.id);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(lexemeLevel1);
					lexeme.setLevel2(lexemeLevel2);
					lexeme.setLevel3(1);
					lexeme.setFrequencyGroup(newWordData.frequencyGroup);
					Long lexemeId = createLexeme(lexeme, getDataset());
					if (lexemeId != null) {
						lexeme.setLexemeId(lexemeId);
						createUsages(lexemeId, usages, dataLang);
						saveGovernments(meaningGroupNode, lexemeId);
						savePosAndDeriv(lexemeId, newWordData, meaningPosCodes, reportingId);
						saveGrammars(meaningGroupNode, lexemeId, newWordData);
						saveRegisters(lexemeId, registers, reportingId);
						saveAdviceNotes(lexemeId, adviceNotes);
						savePublicNotes(lexemeId, publicNotes);
						saveComments(lexemeId, comments);
						for (LexemeToWordData meaningAbbreviation : meaningAbbreviations) {
							LexemeToWordData abbreviationData = meaningAbbreviation.copy();
							abbreviationData.lexemeId = lexemeId;
							context.abbreviations.add(abbreviationData);
						}
					} else {
						// null is returned in case we already have lexeme for word and meaning, this is bad data in xml, so we need to log it
						writeToLogFile(MEANINGS_REPORT_NAME, newWordData.value, "Mõiste ja märksõna jaoks on juba ilmik olemas", definitions.get(0));
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

	private void savePublicNotes(Long lexemeId, List<String> notes) throws Exception {
		for (String note : notes) {
			createLexemeFreeform(lexemeId, FreeformType.PUBLIC_NOTE, note, dataLang);
		}
	}

	private void saveAdviceNotes(Long lexemeId, List<String> notes) throws Exception {
		for (String note : notes) {
			createLexemeFreeform(lexemeId, FreeformType.ADVICE_NOTE, note, dataLang);
		}
	}

	private void cacheMeaningRelatedData(
			Context context, Long meaningId, List<String> definitions, WordData keyword, int level1,
			List<WordData> subWords,
			List<LexemeToWordData> synonyms,
			List<LexemeToWordData> abbreviations,
			List<LexemeToWordData> abbreviationFullWords,
			List<LexemeToWordData> tokens,
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
			context.meanings.addAll(convertToMeaningData(latinTerms, word, level1, definitions));
		});
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
			if (isNotBlank(semanticTypeGroup)) {
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

	private List<LexemeToWordData> extractTokens(Element node, String reportingId) throws Exception {

		final String tokenExp = "s:lig/s:ths";
		return extractLexemeMetadata(node, tokenExp, null, reportingId);
	}

	private List<LexemeToWordData> extractAbbreviations(Element node, String reportingId) throws Exception {

		final String abbreviationExp = "s:lig/s:lyh";
		return extractLexemeMetadata(node, abbreviationExp, null, reportingId);
	}

	private List<LexemeToWordData> extractAbbreviationFullWords(Element node, String reportingId) throws Exception {

		final String abbreviationFullWordExp = "s:dg/s:lhx";
		return extractLexemeMetadata(node, abbreviationFullWordExp, null, reportingId);
	}

	private void saveGrammars(Element node, Long lexemeId, WordData wordData) throws Exception {

		List<String> grammars = extractGrammar(node);
		grammars.addAll(wordData.grammars);
		for (String grammar : grammars) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammar, dataLang);
		}
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

	private void saveGovernments(Element node, Long lexemeId) throws Exception {

		final String governmentExp = "s:grg/s:r";

		List<Element> governmentNodes = node.selectNodes(governmentExp);
		if (CollectionUtils.isNotEmpty(governmentNodes)) {
			for (Element governmentNode : governmentNodes) {
				String governmentValue = governmentNode.getTextTrim();
				createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, governmentValue);
			}
		}
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context, String guid) throws Exception {

		final String wordGroupExp = "s:mg";
		final String wordGrammarPosCodesExp = "s:grg/s:sl";
		String wordExp = xpathExpressions().get("word");//		final String wordExp = "s:m";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			int numberOfWordsInGroup = wordGroupNode.selectNodes(wordExp).size();

			for (int index = 0; index < numberOfWordsInGroup; index++) {
				WordData wordData = new WordData();
				wordData.reportingId = reportingId;
				Word word = extractWordData(wordGroupNode, wordData, guid, index);
				if (word != null) {
					List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
					wordData.id = createOrSelectWord(word, paradigms, getDataset(), context.reusedWordCount);
				}

				if (index == 0) {
					List<WordData> unionWordsOfTheWord = extractUnionWords(wordGroupNode, wordData.id, reportingId);
					context.unionWords.addAll(unionWordsOfTheWord);
				}

				List<PosData> posCodes = extractPosCodes(wordGroupNode, wordGrammarPosCodesExp);
				wordData.posCodes.addAll(posCodes);

				newWords.add(wordData);
			}
		}
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

	private List<LexemeToWordData> extractSynonyms(Element node, String reportingId) throws Exception {

		final String synonymExp = "s:ssh/s:syn";
		return extractLexemeMetadata(node, synonymExp, null, reportingId);
	}

	private List<String> extractAdviceNotes(Element node) {

		final String registerValueExp = "s:lig/s:nb";
		return extractValuesAsStrings(node, registerValueExp);
	}

	private List<String> extractPublicNotes(Element node) {

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

	private List<Usage> extractUsages(Element node, String conceptId) {

		final String usageExp = "s:np/s:ng/s:n";
		final String usageTypeAttr = "nliik";
		final String deinitionExp = "s:nd";
		final String deinitionExp2 = "s:nk";
		final String quotationGroupExp = "s:np/s:cg";
		final String quotationExp = "s:c";
		final String quotationAuhorExp = "s:caut";
		final String quotationAuhorTypeAttr = "aliik";

		List<Usage> usageMeanings = new ArrayList<>();
		List<Element> usageNodes = node.selectNodes(usageExp);
		for (Element usageNode : usageNodes) {
			String usageValue = usageNode.getTextTrim();
			usageValue = cleanEkiEntityMarkup(usageValue);
			Usage usage = new Usage();
			usage.setExtSourceId(conceptId);//disputable mitigation
			usage.setValue(usageValue);
			usage.setDefinitions(new ArrayList<>());
			if (usageNode.hasMixedContent()) {
				Element definitionNode = (Element) usageNode.selectSingleNode(deinitionExp);
				if (definitionNode == null) {
					definitionNode = (Element) usageNode.selectSingleNode(deinitionExp2);
				}
				if (definitionNode != null) {
					String usageDefinitionValue = definitionNode.getText();
					usageDefinitionValue = cleanEkiEntityMarkup(usageDefinitionValue);
					usage.getDefinitions().add(usageDefinitionValue);
				}
			}
			usage.setUsageType(usageNode.attributeValue(usageTypeAttr));
			usageMeanings.add(usage);
		}
		List<Element> quotationGroupNodes = node.selectNodes(quotationGroupExp);
		for (Element quotationGroupNode : quotationGroupNodes) {
			Usage usage = new Usage();
			Element quotationNode = (Element) quotationGroupNode.selectSingleNode(quotationExp);
			Element quotationAutorNode = (Element) quotationGroupNode.selectSingleNode(quotationAuhorExp);
			usage.setValue(quotationNode.getTextTrim());
			usage.setAuthor(quotationAutorNode.getTextTrim());
			usage.setAuthorType(quotationAutorNode.attributeValue(quotationAuhorTypeAttr));
			usageMeanings.add(usage);
		}
		return usageMeanings;
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
					subWord.governments.add(governmentNode.getTextTrim());
				}
			}
			subWord.frequencyGroup = frequencyGroup;
			subWord.displayMorph = displayMorph;
			subWords.add(subWord);
		}
		return subWords;
	}

	private List<WordData> extractUnionWords(Element node, Long wordId, String reportingId) {

		final String unionWordExp = "s:ps";
		final String homonymNrAttr = "i";

		List<WordData> unionWords = new ArrayList<>();
		List<Element> unionWordNodes = node.selectNodes(unionWordExp);
		for (Element unionWordNode : unionWordNodes) {
			WordData unionWord = new WordData();
			unionWord.id = wordId;
			unionWord.value = cleanUp(unionWordNode.getTextTrim());
			unionWord.reportingId = reportingId;
			if (unionWordNode.attributeValue(homonymNrAttr) != null) {
				unionWord.homonymNr = Integer.parseInt(unionWordNode.attributeValue(homonymNrAttr));
			}
			unionWords.add(unionWord);
		}
		return unionWords;
	}

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words, String reportingId) throws Exception {

		Long wordId = null;
		Optional<WordData> matchingWord = words.stream().filter(w -> w.value.equals(wordValue) && w.homonymNr == homonymNr).findFirst();
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

}
