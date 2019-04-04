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
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
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

	private final static String POS_TYPE_DERIVATIVE = "adjga";

	private final static String MEANING_RELATION_ANTONYM = "antonüüm";
	private final static String MEANING_RELATION_COHYPONYM = "kaashüponüüm";

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
	public String getDataset() {
		return "ss1";
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
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
			boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", ARTICLES_REPORT_NAME, BASIC_WORDS_REPORT_NAME, SYNONYMS_REPORT_NAME,
					ANTONYMS_REPORT_NAME, ABBREVIATIONS_REPORT_NAME, COHYPONYMS_REPORT_NAME, TOKENS_REPORT_NAME,
					DESCRIPTIONS_REPORT_NAME, MEANINGS_REPORT_NAME, SUBWORDS_REPORT_NAME);
		}
		start();

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		long articleCount = rootElement.content().stream().filter(o -> o instanceof Element).count();
		long progressIndicator = articleCount / Math.min(articleCount, 100);
		long articleCounter = 0;
		logger.debug("{} articles found", articleCount);

		Context context = new Context();

		writeToLogFile("Artiklite töötlus", "", "");
		List<Node> articleNodes = rootElement.content().stream().filter(o -> o instanceof Element).collect(toList());
		for (Node articleNode : articleNodes) {
			processArticle(articleNode, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);

		processUnionWords(context);
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
		end();
	}

	void processArticle(Node articleNode, Context context) throws Exception {

		final String articleHeaderExp = "s:P";
		final String articleBodyExp = "s:S";
		final String articleGuidExp = "s:G";

		String guid = extractGuid(articleNode, articleGuidExp);
		String reportingId = extractReporingId(articleNode);
		if (articleHasMeanings(articleNode)) {
			List<WordData> newWords = new ArrayList<>();
			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			processArticleHeader(reportingId, headerNode, newWords, context, guid);
			List<CommentData> comments = extractArticleComments(articleNode);
			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(reportingId, contentNode, newWords, context, comments);
				processVariants(newWords);
			}
			context.importedWords.addAll(newWords);
		} else {
			logger.debug("Article does not have meanings, skipping : {}", reportingId);
			writeToLogFile(reportingId, "Artikkel ei sisalda mõisteid", "");
		}
	}

	private boolean articleHasMeanings(Node articleNode) {
		final String meaningGroupNodeExp = "s:S/s:tp/s:tg";
		return !articleNode.selectNodes(meaningGroupNodeExp).isEmpty();
	}

	private void processSeries(Context context, Node headerNode, List<WordData> newWords) throws Exception {

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

	private void processDerivativesInHeaderNode(Context context, Node headerNode, List<WordData> newWords) throws Exception {

		List<WordData> derivativesData = extractDerivativesData(headerNode);
		if (derivativesData.isEmpty()) {
			return;
		}

		for (WordData newWord : newWords) {
			for (WordData derivativeData : derivativesData) {
				Optional<WordData> connectedWord = findConnectedWord(context, derivativeData);
				if (connectedWord.isPresent()) {
					boolean newWordIsDerivative = newWord.posCodes.contains(POS_TYPE_DERIVATIVE);
					if (newWordIsDerivative) {
						createWordRelation(connectedWord.get().id, newWord.id, WORD_RELATION_DERIVATIVE);
						createWordRelation(newWord.id, connectedWord.get().id, WORD_RELATION_DERIVATIVE_BASE);
					} else {
						createWordRelation(newWord.id, connectedWord.get().id, WORD_RELATION_DERIVATIVE);
						createWordRelation(connectedWord.get().id, newWord.id, WORD_RELATION_DERIVATIVE_BASE);
					}
				}
			}
		}
	}

	private Optional<WordData> findConnectedWord(Context context, WordData connectedWordData) {
		return context.importedWords.stream()
				.filter(word -> word.value.equals(connectedWordData.value) && word.homonymNr == connectedWordData.homonymNr).findFirst();
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
		return newWords.stream().anyMatch(wordData -> wordData.wordTypeCodes.contains(wordTypeSeries));
	}

	private List<WordData> extractDerivativesData(Node headerNode) {
		final String wordLinkDerivativeType = "pnatr";
		return extractWordLinksOfType(wordLinkDerivativeType, headerNode);
	}

	private List<WordData> extractSeriesWords(Node headerNode) {
		final String wordLinkSeriesType = "srj";
		return extractWordLinksOfType(wordLinkSeriesType, headerNode);
	}

	private List<WordData> extractWordLinksOfType(String wordLinkType, Node node) {

		final String wordLinkExp = "s:mvtg/s:mvt";
		final String wordLinkTypeAttr = "mvtl";
		final String homonymNrAttr = "i";

		List<WordData> linkedWords = new ArrayList<>();
		List<Node> linkedWordNodes = node.selectNodes(wordLinkExp);
		for (Node linkedWordNode : linkedWordNodes) {
			Element linkedWordElement = (Element) linkedWordNode;
			if (Objects.equals(linkedWordElement.attributeValue(wordLinkTypeAttr), wordLinkType)) {
				WordData linkedWord = new WordData();
				linkedWord.value = cleanUpWord(linkedWordElement.getTextTrim());
				if (linkedWordElement.attributeValue(homonymNrAttr) != null) {
					linkedWord.homonymNr = Integer.parseInt(linkedWordElement.attributeValue(homonymNrAttr));
				}
				linkedWords.add(linkedWord);
			}
		}
		return linkedWords;
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
		createLexemeRelations(context, context.abbreviations, LEXEME_RELATION_ABBREVIATION, "Ei leitud ilmikut lühendile", true);
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

	private List<WordData> processSubWords(Node node, Context context, String reportingId) throws Exception {
		List<WordData> subWords = extractSubWords(node, reportingId);
		if (!subWords.isEmpty()) {
			for (WordData subWord: subWords) {
				subWord.id = getWordIdFor(subWord.value, subWord.homonymNr, context.importedWords, subWord.value);
				if (subWord.id == null) {
					WordData newWord = createDefaultWordFrom(subWord.value, subWord.displayForm, dataLang, subWord.displayMorph, null, subWord.wordTypeCodes, null);
					newWord.homonymNr = subWord.homonymNr;
					context.importedWords.add(newWord);
					subWord.id = newWord.id;
				}
			}
		}
		return subWords;
	}

	private Long createWordWithLexeme(Context context, WordData wordData) throws Exception {

		WordData newWord = createDefaultWordFrom(wordData.value, wordData.value, dataLang, wordData.displayMorph, null, wordData.wordTypeCodes, null);
		newWord.homonymNr = wordData.homonymNr;
		context.importedWords.add(newWord);

		Long meaningId = wordData.meaningId == null ? createMeaning() : wordData.meaningId;
		createLexemeForWordAndMeaning(newWord, meaningId, 1, 1);

		return newWord.id;
	}

	private Long createLexemeForWordAndMeaning(WordData wordData, Long meaningId, int level1, int level2) throws Exception {
		Lexeme lexeme = new Lexeme();
		lexeme.setWordId(wordData.id);
		lexeme.setMeaningId(meaningId);
		lexeme.setLevel1(level1);
		lexeme.setLevel2(level2);
		lexeme.setLevel3(1);
		lexeme.setFrequencyGroupCode(wordData.frequencyGroup);
		Long lexemeId = createLexeme(lexeme, getDataset());
		if (!wordData.governments.isEmpty()) {
			for (String government : wordData.governments) {
				createLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government, null);
			}
		}
		if (!wordData.posCodes.isEmpty()) {
			savePosCodes(lexemeId, wordData, Collections.emptyList(), wordData.value);
		}
		return lexemeId;
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
		final String conceptIdExp = "s:tpid";
		final String meaningNrAttr = "tahendusnr";

		List<WordData> derivativeWords = extractDerivativeWords(contentNode, newWords);
		context.derivativeWords.addAll(derivativeWords);

		List<Node> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
		for (Node meaningNumberGroupNode : meaningNumberGroupNodes) {
			String meaningNrForGroup = ((Element)meaningNumberGroupNode).attributeValue(meaningNrAttr);
			String lexemeLevel1Str = ((Element)meaningNumberGroupNode).attributeValue(lexemeLevel1Attr);
			int lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);
			List<Node> meanigGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
			Element conceptIdNode = (Element) meaningNumberGroupNode.selectSingleNode(conceptIdExp);
			String conceptId = conceptIdNode == null ? null : conceptIdNode.getTextTrim();

			int lexemeLevel2 = 0;
			for (Node meaningGroupNode : meanigGroupNodes) {
				List<WordData> subWords = processSubWords(meaningGroupNode, context, reportingId);
				if (!subWords.isEmpty()) {
					List<Long> createdLexemIds = processMeaning(meaningGroupNode, context, subWords, 1, 1, comments, null, meaningNrForGroup, reportingId);
					if (!createdLexemIds.isEmpty()) {
						lexemeLevel2++;
						for (WordData newWord : newWords) {
							Long meaningId = createMeaning();
							Long mainWordLexemeId = createLexemeForWordAndMeaning(newWord, meaningId, lexemeLevel1, lexemeLevel2);
							for (Long subWordLexemeId : createdLexemIds) {
								createLexemeRelation(mainWordLexemeId, subWordLexemeId, LEXEME_RELATION_SUB_WORD);
							}
						}
					}
				} else {
					lexemeLevel2++;
					processMeaning(meaningGroupNode, context, newWords, lexemeLevel1, lexemeLevel2, comments, conceptId, meaningNrForGroup, reportingId);
				}
			}
		}
	}

	private List<Long> processMeaning(
			Node meaningGroupNode,
			Context context,
			List<WordData> newWords,
			int lexemeLevel1,
			int lexemeLevel2,
			List<CommentData> comments,
			String conceptId,
			String meaningNrForGroup,
			String reportingId) throws Exception {

		final String meaningPosCodeExp = "s:grg/s:sl";
		final String meaningNrAttr = "tahendusnr";
		List<Long> createdLexemeIds = new ArrayList<>();

 		List<Usage> usages = extractUsages(meaningGroupNode);
		List<String> definitions = extractDefinitions(meaningGroupNode);
		List<String> meaningPosCodes = extractPosCodes(meaningGroupNode, meaningPosCodeExp);
		List<String> adviceNotes = extractAdviceNotes(meaningGroupNode);
		List<String> publicNotes = extractPublicNotes(meaningGroupNode);
		String meaningNr = ((Element)meaningGroupNode).attributeValue(meaningNrAttr);

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
				createOrSelectDefinition(meaningId, definition, dataLang, getDataset());
			}
			if (definitionsToAdd.size() > 1) {
				writeToLogFile(DESCRIPTIONS_REPORT_NAME, reportingId, "Leitud rohkem kui üks seletus <s:d>", newWords.get(0).value);
			}
		}
		List<WordToMeaningData> meaningAntonyms = extractAntonyms(meaningGroupNode, meaningId, newWords.get(0), lexemeLevel1, reportingId);
		context.antonyms.addAll(meaningAntonyms);
		List<WordToMeaningData> meaningCohyponyms = extractCohyponyms(meaningGroupNode, meaningId, newWords.get(0), lexemeLevel1, reportingId);
		context.cohyponyms.addAll(meaningCohyponyms);
		cacheMeaningRelatedData(context, meaningId, definitionsToCache, lexemeLevel1,
				newWords, meaningSynonyms, meaningAbbreviations, meaningAbbreviationFullWords, meaningTokens, meaningLatinTerms);

		if (isNotBlank(conceptId)) {
			createMeaningFreeform(meaningId, FreeformType.CONCEPT_ID, conceptId);
		}
		if (isNotBlank(meaningNrForGroup)) {
			createMeaningNr(meaningId, meaningNrForGroup, getDataset());
		}
		if (isNotBlank(meaningNr)) {
			createMeaningNr(meaningId, meaningNr, getDataset());
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
			lexeme.setFrequencyGroupCode(newWordData.frequencyGroup);
			Long lexemeId = createLexeme(lexeme, getDataset());
			if (lexemeId != null) {
				createdLexemeIds.add(lexemeId);
				lexeme.setLexemeId(lexemeId);
				createUsages(lexemeId, usages, dataLang);
				saveGovernments(meaningGroupNode, lexemeId, newWordData);
				savePosCodes(lexemeId, newWordData, meaningPosCodes, reportingId);
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
		return createdLexemeIds;
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
			Context context, Long meaningId, List<String> definitions, int level1,
			List<WordData> newWords,
			List<LexemeToWordData> synonyms,
			List<LexemeToWordData> abbreviations,
			List<LexemeToWordData> abbreviationFullWords,
			List<LexemeToWordData> tokens,
			List<LexemeToWordData> latinTerms
			) {
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
		words.addAll(newWords);
		words.forEach(word -> {
			context.meanings.addAll(convertToMeaningData(synonyms, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(abbreviations, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(abbreviationFullWords, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(tokens, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(latinTerms, word, level1, definitions));
		});
	}

	private void processSemanticData(Node node, Long meaningId) throws Exception {

		final String semanticTypeExp = "s:semg/s:st";
		final String semanticTypeGroupAttr = "sta";
		final String systematicPolysemyPatternExp = "s:semg/s:spm";

		List<Node> semanticTypeNodes = node.selectNodes(semanticTypeExp);
		for (Node semanticTypeNode : semanticTypeNodes) {
			String semanticType = ((Element)semanticTypeNode).getTextTrim();
			Long meaningFreeformId = createMeaningFreeform(meaningId, FreeformType.SEMANTIC_TYPE, semanticType);
			String semanticTypeGroup = ((Element)semanticTypeNode).attributeValue(semanticTypeGroupAttr);
			if (isNotBlank(semanticTypeGroup)) {
				createFreeformTextOrDate(FreeformType.SEMANTIC_TYPE_GROUP, meaningFreeformId, semanticTypeGroup, null);
			}
		}

		List<Node> systematicPolysemyPatternNodes = node.selectNodes(systematicPolysemyPatternExp);
		for (Node systematicPolysemyPatternNode : systematicPolysemyPatternNodes) {
			String systematicPolysemyPattern = ((Element)systematicPolysemyPatternNode).getTextTrim();
			createMeaningFreeform(meaningId, FreeformType.SYSTEMATIC_POLYSEMY_PATTERN, systematicPolysemyPattern);
		}
	}

	private List<CommentData> extractArticleComments(Node node) {

		final String commentGroupExp = "s:KOM/s:komg";
		final String commentValueExp = "s:kom";
		final String commentAuthorExp = "s:kaut";
		final String commentCreatedExp = "s:kaeg";

		List<CommentData> comments = new ArrayList<>();
		List<Node> commentGroupNodes = node.selectNodes(commentGroupExp);
		for (Node commentGroupNode : commentGroupNodes) {
			CommentData comment = new CommentData();
			comment.value = commentGroupNode.selectSingleNode(commentValueExp).getText();
			comment.author = commentGroupNode.selectSingleNode(commentAuthorExp).getText();
			comment.createdAt = commentGroupNode.selectSingleNode(commentCreatedExp).getText();
			comments.add(comment);
		}
		return comments;
	}

	private List<LexemeToWordData> extractLatinTerms(Node node, String reportingId) throws Exception {

		final String latinTermExp = "s:lig/s:ld";
		return extractLexemeMetadata(node, latinTermExp, null, reportingId);
	}

	private List<LexemeToWordData> extractTokens(Node node, String reportingId) throws Exception {

		final String tokenExp = "s:lig/s:ths";
		return extractLexemeMetadata(node, tokenExp, null, reportingId);
	}

	private List<LexemeToWordData> extractAbbreviations(Node node, String reportingId) throws Exception {

		final String abbreviationExp = "s:lig/s:lyh";
		return extractLexemeMetadata(node, abbreviationExp, null, reportingId);
	}

	private List<LexemeToWordData> extractAbbreviationFullWords(Node node, String reportingId) throws Exception {

		final String abbreviationFullWordExp = "s:dg/s:lhx";
		return extractLexemeMetadata(node, abbreviationFullWordExp, null, reportingId);
	}

	private void saveGrammars(Node node, Long lexemeId, WordData wordData) throws Exception {

		List<String> grammars = extractGrammar(node);
		grammars.addAll(wordData.grammars);
		for (String grammar : grammars) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammar, dataLang);
		}
	}

	//POS - part of speech
	private void savePosCodes(Long lexemeId, WordData newWordData, List<String> meaningPosCodes, String reportingId) {

		Set<String> lexemePosCodes = new HashSet<>();
		try {
			if (meaningPosCodes.isEmpty()) {
				lexemePosCodes.addAll(newWordData.posCodes);
				if (lexemePosCodes.size() > 1) {
					String posCodesStr = String.join(",", lexemePosCodes);
//					logger.debug("Found more than one POS code <s:mg/s:sl> : {} : {}", reportingId, posCodesStr);
					writeToLogFile(reportingId, "Märksõna juures leiti rohkem kui üks sõnaliik <s:mg/s:sl>", posCodesStr);
				}
			} else {
				lexemePosCodes.addAll(meaningPosCodes);
			}
			for (String code : lexemePosCodes) {
				if (posCodes.containsKey(code)) {
					Map<String, Object> params = new HashMap<>();
					params.put("lexeme_id", lexemeId);
					params.put("pos_code", posCodes.get(code));
					basicDbService.create(LEXEME_POS, params);
				}
			}
		} catch (Exception e) {
			logger.debug("lexemeId {} : newWord : {}, {}, {}",
					lexemeId, newWordData.value, newWordData.id, String.join(",", lexemePosCodes));
			logger.error("ERROR", e);
		}
	}

	private void saveGovernments(Node node, Long lexemeId, WordData wordData) throws Exception {

		List<String> governmentValues = extractGovernments(node);
		governmentValues.addAll(wordData.governments);
		for (String governmentValue : governmentValues) {
			createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, governmentValue);
		}
	}

	private List<String> extractGovernments(Node node) {

		final String governmentExp = "s:grg/s:r";

		List<String> governments = new ArrayList<>();
		List<Node> governmentNodes = node.selectNodes(governmentExp);
		if (CollectionUtils.isNotEmpty(governmentNodes)) {
			for (Node governmentNode : governmentNodes) {
				governments.add(((Element)governmentNode).getTextTrim());
			}
		}
		return governments;
	}

	private void processArticleHeader(String reportingId, Node headerNode, List<WordData> newWords, Context context, String guid) throws Exception {

		final String wordGroupExp = "s:mg";
		final String wordGrammarPosCodesExp = "s:grg/s:sl";
		String wordExp = xpathExpressions().get("word");//		final String wordExp = "s:m";

		List<Node> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Node wordGroupNode : wordGroupNodes) {
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

				List<String> posCodes = extractPosCodes(wordGroupNode, wordGrammarPosCodesExp);
				wordData.posCodes.addAll(posCodes);
				List<String> governments = extractGovernments(wordGroupNode);
				wordData.governments.addAll(governments);

				newWords.add(wordData);
			}
		}
		processSeries(context, headerNode, newWords);
		processDerivativesInHeaderNode(context, headerNode, newWords);
	}

	private List<WordToMeaningData> extractCohyponyms(Node node, Long meaningId, WordData wordData, int level1, String reportingId) throws Exception {

		final String cohyponymExp = "s:ssh/s:khy";

		List<LexemeToWordData> cohyponyms = extractLexemeMetadata(node, cohyponymExp, null, reportingId);
		cohyponyms.forEach(cohyponym -> cohyponym.meaningId = meaningId);
		return convertToMeaningData(cohyponyms, wordData, level1, Collections.emptyList());
	}

	private List<WordToMeaningData> extractAntonyms(Node node, Long meaningId, WordData wordData, int level1, String reportingId) throws Exception {

		final String antonymExp = "s:ssh/s:ant";

		List<LexemeToWordData> antonyms = extractLexemeMetadata(node, antonymExp, null, reportingId);
		antonyms.forEach(antonym -> antonym.meaningId = meaningId);
		return convertToMeaningData(antonyms, wordData, level1, Collections.emptyList());
	}

	private List<LexemeToWordData> extractSynonyms(Node node, String reportingId) throws Exception {

		final String synonymExp = "s:ssh/s:syn";
		return extractLexemeMetadata(node, synonymExp, null, reportingId);
	}

	private List<String> extractAdviceNotes(Node node) {

		final String registerValueExp = "s:lig/s:nb";
		return extractCleanValues(node, registerValueExp);
	}

	private List<String> extractPublicNotes(Node node) {

		final String registerValueExp = "s:lig/s:tx";
		return extractOriginalValues(node, registerValueExp);
	}

	private List<String> extractRegisters(Node node) {

		final String registerValueExp = "s:dg/s:regr/s:s";
		return extractCleanValues(node, registerValueExp);
	}

	private List<String> extractDefinitions(Node node) {

		final String definitionValueExp = "s:dg/s:d";
		return extractOriginalValues(node, definitionValueExp);
	}

	private List<Usage> extractUsages(Node node) {

		final String usageExp = "s:np/s:ng/s:n";
		final String usageTypeAttr = "nliik";
		final String quotationGroupExp = "s:np/s:cg";
		final String quotationExp = "s:c";
		final String quotationAuhorExp = "s:caut";
		final String quotationAuhorTypeAttr = "aliik";

		List<Usage> usageMeanings = new ArrayList<>();
		List<Node> usageNodes = node.selectNodes(usageExp);
		for (Node usageNode : usageNodes) {
			Element usageElement = (Element)usageNode;
			String usageValue = usageElement.getTextTrim();
			Usage usage = new Usage();
			usage.setValue(usageValue);
			usage.setDefinitions(new ArrayList<>());
			usage.setUsageType(usageElement.attributeValue(usageTypeAttr));
			usageMeanings.add(usage);
		}
		List<Node> quotationGroupNodes = node.selectNodes(quotationGroupExp);
		for (Node quotationGroupNode : quotationGroupNodes) {
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
		List<Node> wordGroupNodes = node.selectNodes(wordGroupExp);
		for (WordData mainWord : mainWords) {
			for (Node wordGroupNode: wordGroupNodes) {
				Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				WordData derivative = new WordData();
				derivative.id = mainWord.id;
				derivative.reportingId = mainWord.reportingId;
				derivative.value = cleanUpWord(wordNode.getTextTrim());
				if (((Element)wordGroupNode).attributeValue(homonymNrAttr) != null) {
					derivative.homonymNr = Integer.parseInt(((Element)wordGroupNode).attributeValue(homonymNrAttr));
				}
				Element frequencyGroupNode = (Element) node.selectSingleNode(frequencyGroupExp);
				if (frequencyGroupNode != null) {
					derivative.frequencyGroup = frequencyGroupCodes.get(frequencyGroupNode.getTextTrim());
				}
				List<String> posCodes = extractPosCodes(wordGroupNode, wordPosCodeExp);
				derivative.posCodes.addAll(posCodes);
				derivatives.add(derivative);
			}
		}
		return derivatives;
	}

	private List<WordData> extractSubWords(Node node, String reportingId) {

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
				logger.warn("Unknown display morph code : {} : {}", displayMorphNode.getTextTrim(), reportingId);
			}
		}

		List<WordData> subWords = new ArrayList<>();
		List<Node> subWordNodes = node.selectNodes(subWordExp);
		for (Node subWordNode : subWordNodes) {
			Element subWordElement = (Element) subWordNode;
			WordData subWord = new WordData();
			subWord.displayForm = cleanEkiEntityMarkup(subWordElement.getTextTrim());
			subWord.value = cleanUpWord(subWord.displayForm);
			if (subWordElement.attributeValue(homonymNrAttr) != null) {
				subWord.homonymNr = Integer.parseInt(subWordElement.attributeValue(homonymNrAttr));
			}
			if (subWordElement.hasMixedContent()) {
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

	private List<WordData> extractUnionWords(Node node, Long wordId, String reportingId) {

		final String unionWordExp = "s:ps";
		final String homonymNrAttr = "i";

		List<WordData> unionWords = new ArrayList<>();
		List<Node> unionWordNodes = node.selectNodes(unionWordExp);
		for (Node unionWordNode : unionWordNodes) {
			Element unionWordElement = (Element) unionWordNode;
			WordData unionWord = new WordData();
			unionWord.id = wordId;
			unionWord.value = cleanUpWord(unionWordElement.getTextTrim());
			unionWord.reportingId = reportingId;
			if (unionWordElement.attributeValue(homonymNrAttr) != null) {
				unionWord.homonymNr = Integer.parseInt(unionWordElement.attributeValue(homonymNrAttr));
			}
			unionWords.add(unionWord);
		}
		return unionWords;
	}

}
