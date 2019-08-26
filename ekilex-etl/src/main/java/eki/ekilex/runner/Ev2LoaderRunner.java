package eki.ekilex.runner;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
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

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.constant.WordRelationGroupType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Mnr;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.data.util.LexemeRowMapper;
import eki.ekilex.service.ReportComposer;

@Component
public class Ev2LoaderRunner extends SsBasedLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Ev2LoaderRunner.class);

	private final static String LANG_RUS = "rus";
	private final static String ASPECT_SOV = "сов.";
	private final static String ASPECT_NESOV = "несов.";
	private final static String ASPECT_SOV_NESOV = "сов. и несов.";
	private final static String POS_CODE_VERB = "v";
	private final static String MEANING_REF_EXP = "x:S/x:tp/x:tvt";

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	protected Map<String, String> xpathExpressions() {
		Map<String, String> experssions = new HashMap<>();
		experssions.put("headword", "x:P/x:mg/x:m"); // use first word as id for reporting
		experssions.put("word", "x:m");
		experssions.put("wordDisplayMorph", "x:vk");
		experssions.put("wordVocalForm", "x:hld");
		experssions.put("grammarValue", "x:gki");
		experssions.put("domain", "x:dg/x:v");
		return experssions;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}

	@Override
	public String getDataset() {
		return "ev2";
	}

	@Override
	public Complexity getLexemeComplexity() {
		return Complexity.DETAIL;
	}

	@Override
	public Complexity getDefinitionComplexity() {
		return Complexity.DETAIL2;
	}

	@Override
	public Complexity getFreeformComplexity() {
		return Complexity.DETAIL2;
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Transactional
	public void execute(String dataXmlFilePath1, String dataXmlFilePath2, Map<String, List<Guid>> ssGuidMap, Map<String, List<Mnr>> ssMnrMap, boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", ARTICLES_REPORT_NAME, DESCRIPTIONS_REPORT_NAME, MEANINGS_REPORT_NAME);
		}
		start();

		String[] dataXmlFilePaths = new String[] {dataXmlFilePath1, dataXmlFilePath2};
		Document dataDoc;
		Element rootElement;
		List<Node> allArticleNodes = new ArrayList<>();
		List<Node> articleNodes;
		for (String dataXmlFilePath : dataXmlFilePaths) {
			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			rootElement = dataDoc.getRootElement();
			articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
			allArticleNodes.addAll(articleNodes);
		}
		long articleCount = allArticleNodes.size();
		writeToLogFile("Artiklite töötlus", "", "");
		logger.debug("{} articles found", articleCount);

		Context context = new Context();
		context.ssGuidMap = ssGuidMap;
		context.ssMnrMap = ssMnrMap;

		long progressIndicator = articleCount / Math.min(articleCount, 100);
		long articleCounter = 0;
		for (Node articleNode : allArticleNodes) {
			processArticle(articleNode, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);
		processMeaningReferences(context);
		processLatinTerms(context);
		processAbbreviations(context);
		processFloatingWords(context);
		unusedMeaningReferencesReport(context);

		logger.debug("Found {} reused words", context.reusedWordCount.getValue());
		logger.debug("Found {} ss words", context.ssWordCount.getValue());
		logger.debug("Found {} ss meanings", context.ssMeaningCount.getValue());
		logger.debug("Found {} multiple meanings in group", context.multipleMeaningsGroupCount.getValue());

		end();
	}

	private void processFloatingWords(Context context) throws Exception {

		List<Long> floatingWordIds = basicDbService.queryList(sqls.getSqlSelectFloatingWordIds(), new HashMap<>(), Long.class);
		if (CollectionUtils.isEmpty(floatingWordIds)) {
			return;
		}
		logger.debug("Processing {} floating words created by this loader", floatingWordIds.size());
		for (Long floatingWordId : floatingWordIds) {
			for (WordData wordData : context.importedWords) {
				if (wordData.id.equals(floatingWordId)) {
					Long meaningId = createOrSelectMeaning(wordData.mnr, context.ssMnrMap, context.ssMeaningCount);
					createLexemeForWord(meaningId, wordData);
				}
			}
		}
	}

	private void processMeaningReferences(Context context) throws Exception {
		logger.debug("Processing {} meaning reference sets", context.meaningReferences.size());
		for (MeaningReferenceData meaningReference : context.meaningReferences) {
			for (WordData refWord : meaningReference.referencedWords) {
				Optional<WordData> importedWord = context.importedWords.stream()
						.filter(w -> Objects.equals(w.value, refWord.value) && (w.homonymNr == refWord.homonymNr)).findFirst();
				if (importedWord.isPresent()) {
					WordData existingWord = importedWord.get();
					Long meaningId = getMeaningIdOfTheFirstLexeme(existingWord.id);
					if (meaningId == null) {
						logger.debug("No lexeme found for {} {}", existingWord.headword, existingWord.homonymNr);
						continue;
					}
					createLexemesForWords(meaningId, meaningReference.words);
					createLexemeForWord(meaningId, existingWord);
				} else {
					logger.debug("Unable to locate referred word {} {}", refWord.value, refWord.homonymNr);
				}
			}
		}
	}

	private void unusedMeaningReferencesReport(Context context) throws Exception {
		if (context.referencesToMeanings.isEmpty()) {
			return;
		}
		logger.debug("Unused meaning references");
		for (MeaningReferenceData refData : context.referencesToMeanings.values()) {
			String logString;
			if (refData.words.isEmpty()) {
				List<String> words = new ArrayList<>();
				for (Node articleNode : refData.articleNodes) {
					List<Node> wordNodes = articleNode.selectNodes("x:P/x:mg/x:m");
					words.addAll(wordNodes.stream().map(n -> ((Element) n).getTextTrim()).collect(toList()));
				}
				logString = String.join(",", words);
				logger.debug("nodes -> {}", logString);
			} else {
				logString = refData.words.stream().map(w -> w.value).collect(joining(","));
				logger.debug("words -> {}", logString);
			}
			writeToLogFile(logString, "Ei leitud viidatavat mõistet", "");
		}
	}

	private void processAbbreviations(Context context) throws Exception {

		//logger.debug("Found abbreviations {} <x:lhx> : {} <x:xlhx>.", context.abbreviationFullWords.size(), context.abbreviationFullWordsRus);
		logger.debug("Found abbreviations {} <x:lhx>", context.abbreviationFullWords.size());
		logger.debug("Processing started.");
		writeToLogFile("Lühendite töötlus <x:lhx> ja <x:xlhx>", "", "");

		Count newAbbreviationFullWordCount = processLexemeToWord(context, context.abbreviationFullWords, null, "Ei leitud märksõna, loome uue", dataLang);
		Count newAbbreviationFullWordRusCount = processLexemeToWord(context, context.abbreviationFullWordsRus, null, "Ei leitud märksõna, loome uue", LANG_RUS);
		createLexemeRelations(context, context.abbreviationFullWords, LEXEME_RELATION_ABBREVIATION, "Ei leitud ilmikut lühendi täis märksõnale", false);
		createLexemeRelations(context, context.abbreviationFullWordsRus, LEXEME_RELATION_ABBREVIATION, "Ei leitud ilmikut lühendi täis märksõnale", false);

		logger.debug("Words created {} {} : {} {}", newAbbreviationFullWordCount.getValue(), dataLang, newAbbreviationFullWordRusCount.getValue(), LANG_RUS);
		logger.debug("Abbreviations import done.");
	}

	private void processArticle(
			Node articleNode,
			Context context) throws Exception {

		final String articleBodyExp = "x:S";
		final String articlePhraseologyExp = "x:F";

		String headword = extractHeadword(articleNode);
		List<WordData> newWords = new ArrayList<>();
		processArticleHeader(context, articleNode, headword, newWords);
		Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
		if (articleHasMeanings(articleNode)) {
			processArticleContent(context, contentNode, headword, newWords);
			Element phraseologyNode = (Element) articleNode.selectSingleNode(articlePhraseologyExp);
			if (phraseologyNode != null) {
				processPhraseology(context, phraseologyNode, headword, newWords);
			}
		}
		if (articleHasMeaningReference(articleNode)) {
			collectMinimalWordData(contentNode, newWords);
			List<WordData> referencedWords = extractMeaningReferences(articleNode);
			MeaningReferenceData meaningReference = new MeaningReferenceData();
			meaningReference.words.addAll(newWords);
			meaningReference.referencedWords.addAll(referencedWords);
			context.meaningReferences.add(meaningReference);
		}
		extractLogDataAndCreateLifecycleLog(articleNode, newWords);
		context.importedWords.addAll(newWords);
	}

	private Long getMeaningIdOfTheFirstLexeme(Long wordId) throws Exception {
		List<Lexeme> lexemesForWord = getExistingLexemesForWord(wordId);
		if (CollectionUtils.isEmpty(lexemesForWord)) {
			return null;
		}
		Lexeme firstLexeme = lexemesForWord.get(0);
		return firstLexeme.getMeaningId();
	}

	private String generateRefKey(WordData word) {
		return word.value + "|" + word.homonymNr;
	}

	private void createLexemesForWords(Long meaningId, List<WordData> words) throws Exception {
		if (meaningId != null) {
			for (WordData wordData : words) {
				createLexemeForWord(meaningId, wordData);
			}
		}
	}

	private void createLexemeForWord(Long meaningId, WordData wordData) throws Exception {
		Lexeme lexeme = new Lexeme();
		lexeme.setWordId(wordData.id);
		lexeme.setMeaningId(meaningId);
		lexeme.setLevel1(wordData.level1);
		lexeme.setLevel2(1);
		lexeme.setLevel3(1);
		lexeme.setFrequencyGroupCode(wordData.frequencyGroup);
		wordData.level1++;
		Long lexemeId = createLexemeIfNotExists(lexeme);
		if (lexemeId != null) {
			saveGovernments(wordData, Collections.emptyList(), lexemeId);
			savePosAndDeriv(wordData, Collections.emptyList(), Collections.emptyList(), lexemeId, wordData.value);
			saveGrammars(wordData, Collections.emptyList(), Collections.emptyList(), lexemeId);
			saveRegisters(lexemeId, wordData.registerCodes, wordData.headword);
			saveDomains(meaningId, wordData.domainCodes);
		}
	}

	private List<WordData> extractMeaningReferences(Node articleNode) {
		String homonymNrAttr = "i";
		List<WordData> referencedWords = new ArrayList<>();
		List<Node> referenceNodes = articleNode.selectNodes(MEANING_REF_EXP);
		for (Node refNode : referenceNodes) {
			Element refElement = (Element) refNode;
			WordData refWord = new WordData();
			refWord.value = cleanUpWord(refElement.getTextTrim());
			if (refElement.attributeValue(homonymNrAttr) != null) {
				refWord.homonymNr = Integer.parseInt(refElement.attributeValue(homonymNrAttr));
			}
			referencedWords.add(refWord);
		}
		return referencedWords;
	}

	private boolean articleHasMeanings(Node articleNode) {
		final String meaningGroupNodeExp = "x:S/x:tp[x:tg and not(x:tvt)]";
		return articleNode.selectSingleNode(meaningGroupNodeExp) != null;
	}

	private boolean articleHasMeaningReference(Node articleNode) {
		return articleNode.selectSingleNode(MEANING_REF_EXP) != null;
	}

	private void processMeaningReferences(Context context, WordData newWordData, Long meaningId) throws Exception {
		String key = generateRefKey(newWordData);
		if (context.referencesToMeanings.containsKey(key)) {
			MeaningReferenceData refData = context.referencesToMeanings.get(key);
			context.referencesToMeanings.remove(key);
			List<WordData> referringWords = new ArrayList<>();
			if (refData.words.isEmpty()) {
				for (Node articleNode : refData.articleNodes) {
					String headword = extractHeadword(articleNode);
					processArticleHeader(context, articleNode, headword, referringWords);
				}
				context.importedWords.addAll(referringWords);
			} else {
				referringWords = refData.words;
			}
			createLexemesForWords(meaningId, referringWords);
			for (WordData refWord : referringWords) {
				processMeaningReferences(context, refWord, meaningId);
			}
		}
	}

	private void processPhraseology(Context context, Element node, String headword, List<WordData> newWords) throws Exception {

		final String phraseologyGroupExp = "x:fg";
		final String phraseologyValueExp = "x:f";
		final String meaningGroupExp = "x:fqnp";
		final String governmentsExp = "x:r";
		final String domainsExp = "x:v";
		final String registersExp = "x:s";
		final String definitionsExp = "x:fd";
		final String translationGroupExp = "x:fqng";
		final String translationValueExp = "x:qf";

		List<Node> groupNodes = node.selectNodes(phraseologyGroupExp);
		for (Node groupNode : groupNodes) {
			List<String> wordValues = extractCleanValues(groupNode, phraseologyValueExp);
			for (String wordValue : wordValues) {
				String word = cleanUpWord(wordValue);
				List<Map<String, Object>> wordInSs1 = getWordsInSs1(word);
				if (CollectionUtils.isEmpty(wordInSs1)) {
					continue;
				}
				WordData wordData = findOrCreateWordUsingSs1(context, wordValue, word, wordInSs1);
				List<Lexeme> lexemesForWord = getExistingLexemesForWord(wordData.id);
				int lexemeLevel1 = 1;
				List<Node> meaningGroupNodes = groupNode.selectNodes(meaningGroupExp);
				for (Node meaningGroupNode : meaningGroupNodes) {
					Lexeme existingLexeme = null;
					if (CollectionUtils.isNotEmpty(lexemesForWord)) {
						int level1 = lexemeLevel1;
						existingLexeme = lexemesForWord.stream().filter(lex -> Objects.equals(level1, lex.getLevel1())).findFirst().orElse(null);
					}
					Long meaningId;
					Long lexemeId;
					if (existingLexeme == null) {
						meaningId = createMeaning();
						Lexeme lexeme = new Lexeme();
						lexeme.setWordId(wordData.id);
						lexeme.setMeaningId(meaningId);
						lexeme.setLevel1(lexemeLevel1);
						lexeme.setLevel2(1);
						lexeme.setLevel3(1);
						lexemeId = createLexemeIfNotExists(lexeme);
					} else {
						meaningId = (Long) existingLexeme.getMeaningId();
						lexemeId = (Long) existingLexeme.getLexemeId();
					}

					List<String> definitions = extractCleanValues(meaningGroupNode, definitionsExp);
					for (String definition : definitions) {
						createOrSelectDefinition(meaningId, definition, dataLang);
					}

					List<String> domains = extractCleanValues(meaningGroupNode, domainsExp);
					processDomains(null, meaningId, domains);

					if (lexemeId != null) {
						List<String> governments = extractCleanValues(meaningGroupNode, governmentsExp);
						List<String> registers = extractCleanValues(meaningGroupNode, registersExp);
						saveGovernments(wordData, governments, lexemeId);
						saveRegisters(lexemeId, registers, headword);
					}
					lexemeLevel1++;

					List<Node> translationGroupNodes = meaningGroupNode.selectNodes(translationGroupExp);
					for (Node transalationGroupNode : translationGroupNodes) {
						String russianWord = extractCleanValue(transalationGroupNode, translationValueExp);
						WordData russianWordData = findOrCreateWord(context, cleanUpWord(russianWord), russianWord, LANG_RUS, null, null);
						List<String> russianRegisters = extractCleanValues(transalationGroupNode, registersExp);
						Lexeme russianLexeme = new Lexeme();
						russianLexeme.setWordId(russianWordData.id);
						russianLexeme.setMeaningId(meaningId);
						russianLexeme.setLevel1(russianWordData.level1);
						russianLexeme.setLevel2(1);
						russianLexeme.setLevel3(1);
						Long russianLexemeId = createLexemeIfNotExists(russianLexeme);
						russianWordData.level1++;
						if (russianLexemeId != null) {
							saveRegisters(russianLexemeId, russianRegisters, headword);
						}
						List<String> additionalDomains = extractCleanValues(transalationGroupNode, domainsExp);
						processDomains(null, meaningId, additionalDomains);
					}
				}
				for (WordData newWord : newWords) {
					createWordRelation(newWord.id, wordData.id, WORD_RELATION_UNION);
				}
			}
		}
	}

	private WordData findOrCreateWordUsingSs1(Context context, String wordValue, String word, List<Map<String, Object>> wordInSs1) throws Exception {
		WordData wordData;
		if (wordInSs1.size() == 1) {
			wordData = new WordData();
			wordData.id = (Long) wordInSs1.get(0).get("id");
			wordData.value = word;
			wordData.displayForm = wordValue;
			wordData.language = dataLang;
			context.importedWords.add(wordData);
		} else {
			wordData = findOrCreateWord(context, word, wordValue, dataLang, null, null);
		}
		return wordData;
	}

	private void processArticleHeader(
			Context context,
			Node articleNode,
			String headword,
			List<WordData> newWords) throws Exception {

		final String articleHeaderExp = "x:P";
		final String articleGuidExp = "x:G";
		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordGrammarPosCodesExp = "x:grk/x:sl";

		String guid = extractGuid(articleNode, articleGuidExp);
		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		List<Node> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Node wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.headword = headword;

			Word word = extractWordData(wordGroupNode, wordData, guid, 0);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
				wordData.id = createOrSelectWord(word, paradigms, context.ssGuidMap, context.ssWordCount, context.reusedWordCount);
			}

			List<String> posCodes = extractPosCodes(wordGroupNode, wordPosCodeExp);
			wordData.posCodes.addAll(posCodes);
			posCodes = extractPosCodes(wordGroupNode, wordGrammarPosCodesExp);
			wordData.posCodes.addAll(posCodes);

			List<String> governments = extractGovernments(wordGroupNode);
			if (!governments.isEmpty()) {
				wordData.governments.addAll(governments);
			}

			newWords.add(wordData);
		}
	}

	private void processArticleContent(Context context, Element contentNode, String headword, List<WordData> newWords) throws Exception {

		final String meaningBlockExp = "x:tp";
		final String meaningNrAttr = "tahendusnr";
		final String meaningPosCodeExp = "x:sl";
		final String meaningPosCode2Exp = "x:grk/x:sl";
		final String meaningGroupExp = "x:tg";
		final String meaningDefinitionExp = "x:dg/x:d";
		final String registerExp = "x:dg/x:s";
		final String latinTermExp = "x:dg/x:ld";
		final String abbreviationFullWordExp = "x:dg/x:lhx";

		boolean isVerb = newWords.get(0).posCodes.contains(POS_CODE_VERB);
		List<Node> meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);
		int lexemeLevel1 = 0;
		for (Node meaningBlockNode : meaningBlockNodes) {

			lexemeLevel1++;
			Element meaningBlockElem = (Element) meaningBlockNode;
			String mnr = meaningBlockElem.attributeValue(meaningNrAttr);
			List<String> meaningPosCodes = new ArrayList<>();
			meaningPosCodes.addAll(extractPosCodes(meaningBlockNode, meaningPosCodeExp));
			meaningPosCodes.addAll(extractPosCodes(meaningBlockNode, meaningPosCode2Exp));
			List<String> meaningGovernments = extractGovernments(meaningBlockNode);
			List<String> meaningGrammars = extractGrammar(meaningBlockNode);
			List<Usage> usages = extractUsages(meaningBlockNode);
			List<Node> meaningGroupNodes = meaningBlockNode.selectNodes(meaningGroupExp);

			if (CollectionUtils.isNotEmpty(meaningGroupNodes)) {
				boolean isSingleMeaningGroup = CollectionUtils.size(meaningGroupNodes) == 1;
				if (!isSingleMeaningGroup) {
					context.multipleMeaningsGroupCount.increment();
				}
				int lexemeLevel2 = 0;
				for (Node meaningGroupNode : meaningGroupNodes) {
					lexemeLevel2++;
					List<String> lexemePosCodes = extractPosCodes(meaningGroupNode, meaningPosCodeExp);
					List<String> lexemeGrammars = extractGrammar(meaningGroupNode);
					List<String> registerCodes = extractCleanValues(meaningGroupNode, registerExp);

					Long meaningId;
					List<String> definitionsToAdd = new ArrayList<>();
					List<String> definitionsToCache = new ArrayList<>();
					List<String> definitions = extractCleanValues(meaningGroupNode, meaningDefinitionExp);
					List<LexemeToWordData> meaningLatinTerms = extractLatinTerms(meaningGroupNode, latinTermExp, headword);
					List<LexemeToWordData> meaningAbbreviationFullWords = extractAbbreviationFullWords(meaningGroupNode, abbreviationFullWordExp, headword);
					List<String> additionalDomains = new ArrayList<>();
					List<List<LexemeToWordData>> aspectGroups = new ArrayList<>();
					Map<String, List<LexemeToWordData>> russianAbbreviationWords = new HashMap<>();
					List<LexemeToWordData> meaningRussianWords = extractRussianWords(meaningGroupNode, additionalDomains, aspectGroups, headword, isVerb, russianAbbreviationWords);
					List<LexemeToWordData> connectedWords = Stream.of(meaningLatinTerms.stream(), meaningAbbreviationFullWords.stream()).flatMap(i -> i).collect(toList());
					WordToMeaningData meaningData = findExistingMeaning(context, newWords.get(0), lexemeLevel1, connectedWords, definitions);
					if (meaningData == null) {
						if (isSingleMeaningGroup) {
							meaningId = createOrSelectMeaning(mnr, context.ssMnrMap, context.ssMeaningCount);
						} else {
							meaningId = createMeaning();
						}
						definitionsToAdd.addAll(definitions);
						definitionsToCache.addAll(definitions);
					} else {
						meaningId = meaningData.meaningId;
						validateMeaning(meaningData, definitions, headword);
						definitionsToAdd = definitions.stream().filter(def -> !meaningData.meaningDefinitions.contains(def)).collect(toList());
						meaningData.meaningDefinitions.addAll(definitionsToAdd);
						definitionsToCache.addAll(meaningData.meaningDefinitions);
					}
					if (!definitionsToAdd.isEmpty()) {
						for (String definition : definitionsToAdd) {
							createOrSelectDefinition(meaningId, definition, dataLang);
						}
						if (definitionsToAdd.size() > 1) {
							writeToLogFile(DESCRIPTIONS_REPORT_NAME, headword, "Leitud rohkem kui üks seletus <s:d>", newWords.get(0).value);
						}
					}
					cacheMeaningRelatedData(context, meaningId, definitionsToCache, newWords.get(0), lexemeLevel1, meaningLatinTerms, meaningAbbreviationFullWords);

					processDomains(meaningGroupNode, meaningId, additionalDomains);

					int lexemeLevel3 = 1;
					for (WordData newWordData : newWords) {
						Lexeme lexeme = new Lexeme();
						lexeme.setWordId(newWordData.id);
						lexeme.setMeaningId(meaningId);
						lexeme.setLevel1(lexemeLevel1);
						lexeme.setLevel2(lexemeLevel2);
						lexeme.setLevel3(lexemeLevel3);
						Long lexemeId = createLexemeIfNotExists(lexeme);
						if (lexemeId != null) {
							// FIXME: add usages and subword relations only to first lexeme on the second level
							// this is temporary solution, till EKI provides better one
							if (lexemeLevel2 == 1) {
								createUsages(lexemeId, usages, dataLang);
							}
							saveGovernments(newWordData, meaningGovernments, lexemeId);
							savePosAndDeriv(newWordData, meaningPosCodes, lexemePosCodes, lexemeId, headword);
							saveGrammars(newWordData, meaningGrammars, lexemeGrammars, lexemeId);
							saveRegisters(lexemeId, registerCodes, headword);
							// connect abbreviation word data and created lexeme id for latter use in abbreviation processing
							for (LexemeToWordData meaningAbbreviationFullWord : meaningAbbreviationFullWords) {
								meaningAbbreviationFullWord.lexemeId = lexemeId;
							}
						}
						if (lexemeLevel1 == 1 && lexemeLevel2 == 1) {
							processMeaningReferences(context, newWordData, meaningId);
						}
					}

					processRussianWords(context, meaningRussianWords, aspectGroups, meaningId, russianAbbreviationWords);
				}
			}

			for (WordData wordData : newWords) {
				wordData.level1 = lexemeLevel1 + 1;
			}
			processWordsInUsageGroups(context, meaningBlockNode, headword);
		}
	}

	private void collectMinimalWordData(Element contentNode, List<WordData> newWords) {

		final String meaningNrAttr = "tahendusnr";
		final String meaningBlockExp = "x:tp";
		final String meaningGroupExp = "x:tg";
		//not gonna
		//final String meaningDefinitionExp = "x:dg/x:d";
		final String registerExp = "x:dg/x:s";
		final String domainExp = "x:dg/x:v";

		List<Node> meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);
		for (Node meaningBlockNode : meaningBlockNodes) {
			Element meaningBlockElem = (Element) meaningBlockNode;
			String mnr = meaningBlockElem.attributeValue(meaningNrAttr);
			List<Node> meaningGroupNodes = meaningBlockNode.selectNodes(meaningGroupExp);
			if (CollectionUtils.isNotEmpty(meaningGroupNodes)) {
				for (Node meaningGroupNode : meaningGroupNodes) {
					List<String> registerCodes = extractCleanValues(meaningGroupNode, registerExp);
					List<String> domainCodes = extractCleanValues(meaningGroupNode, domainExp);
					for (WordData wordData : newWords) {
						wordData.registerCodes.addAll(registerCodes);
						wordData.domainCodes.addAll(domainCodes);
						wordData.mnr = mnr;
					}
				}
			}
		}
	}

	private void processRussianWords(
			Context context,
			List<LexemeToWordData> meaningRussianWords,
			List<List<LexemeToWordData>> aspectGroups,
			Long meaningId,
			Map<String, List<LexemeToWordData>> russianAbbreviationWords) throws Exception {

		for (LexemeToWordData russianWordData : meaningRussianWords) {
			WordData russianWord = findOrCreateWord(context, russianWordData.word, russianWordData.displayForm, LANG_RUS, russianWordData.aspect, russianWordData.vocalForm);
			russianWordData.wordId = russianWord.id;
			Lexeme lexeme = new Lexeme();
			lexeme.setWordId(russianWord.id);
			lexeme.setMeaningId(meaningId);
			lexeme.setLevel1(russianWord.level1);
			lexeme.setLevel2(1);
			lexeme.setLevel3(1);
			lexeme.setCorpusFrequency(russianWordData.corpFrequency);
			Long lexemeId = createLexemeIfNotExists(lexeme);
			russianWord.level1++;
			if (lexemeId != null) {
				for (String government : russianWordData.governments) {
					createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government);
				}
				if (CollectionUtils.isNotEmpty(russianWordData.registerCodes)) {
					saveRegisters(lexemeId, russianWordData.registerCodes, russianWordData.word);
				}
				for (String source : russianWordData.sources) {
					SourceType sourceType = StringUtils.startsWith(source, "http") ? SourceType.DOCUMENT : SourceType.UNKNOWN;
					Long sourceId = getSource(sourceType, EXT_SOURCE_ID_NA, source, getDataset());
					if (sourceId == null) {
						sourceId = createSource(sourceType, EXT_SOURCE_ID_NA, source);
					}
					createLexemeSourceLink(lexemeId, ReferenceType.ANY, sourceId, null, source);
				}
				if (russianAbbreviationWords.containsKey(russianWord.value)) {
					for (LexemeToWordData abbreviationWord : russianAbbreviationWords.get(russianWord.value)) {
						abbreviationWord.lexemeId = lexemeId;
						abbreviationWord.meaningId = meaningId;
					}
					context.abbreviationFullWordsRus.addAll(russianAbbreviationWords.get(russianWord.value));
				}
			}
		}
		for (List<LexemeToWordData> aspectGroup : aspectGroups) {
			List<Long> memberIds = aspectGroup.stream().map(w -> w.wordId).collect(toList());
			if (hasNoWordRelationGroupWithMembers(WordRelationGroupType.ASPECTS, memberIds)) {
				Long wordGroupId = createWordGroup(WordRelationGroupType.ASPECTS);
				for (LexemeToWordData wordData : aspectGroup) {
					createWordGroupMember(wordGroupId, wordData.wordId);
				}
			}
		}
	}

	private void processWordsInUsageGroups(Context context, Node node, String headword) throws Exception {

		final String usageBlockExp = "x:np";
		final String usageGroupExp = "x:ng";
		final String usageExp = "x:n";
		final String meaningGroupExp = "x:qnp";
		final String registersExp = "x:s";
		final String domainsExp = "x:v";
		final String definitionExp = "x:nd";
		final String translationGroupExp = "x:qng";
		final String translationValueExp = "x:qn";
		final String latinTermExp = "x:ld";

		List<Node> usageBlockNodes = node.selectNodes(usageBlockExp);
		for (Node usageBlockNode : usageBlockNodes) {
			List<Node> usageGroupNodes = usageBlockNode.selectNodes(usageGroupExp);
			for (Node usageGroupNode : usageGroupNodes) {
				if (isRestricted(usageGroupNode)) {
					continue;
				}
				List<String> wordValues = extractCleanValues(usageGroupNode, usageExp);
				for (String wordValue : wordValues) {
					String word = cleanUpWord(wordValue);
					List<Map<String, Object>> wordInSs1 = getWordsInSs1(word);
					if (!isUsage(word, wordInSs1)) {
						List<Node> meaningGroupNodes = usageGroupNode.selectNodes(meaningGroupExp);
						if (meaningGroupNodes.isEmpty()) {
							continue;
						}
						WordData wordData = findOrCreateWordUsingSs1(context, wordValue, word, wordInSs1);
						List<Lexeme> lexemesForWord = getExistingLexemesForWord(wordData.id);
						int meaningNodeIndex = 1;
						for (Node meaningGroupNode : meaningGroupNodes) {
							Long meaningId;
							boolean useExistingLexeme = false;
							if (CollectionUtils.isEmpty(lexemesForWord)) {
								meaningId = createMeaning();
								List<String> domains = extractCleanValues(meaningGroupNode, domainsExp);
								processDomains(null, meaningId, domains);

								List<String> definitions = extractCleanValues(meaningGroupNode, definitionExp);
								for (String definition : definitions) {
									createOrSelectDefinition(meaningId, definition, dataLang);
								}
								Lexeme lexeme = new Lexeme();
								lexeme.setWordId(wordData.id);
								lexeme.setMeaningId(meaningId);
								lexeme.setLevel1(wordData.level1);
								lexeme.setLevel2(1);
								lexeme.setLevel3(1);
								wordData.level1++;

								Long lexemeId = createLexemeIfNotExists(lexeme);
								List<String> registers = extractCleanValues(meaningGroupNode, registersExp);
								saveRegisters(lexemeId, registers, headword);
							} else {
								int lexemeIndex = lexemesForWord.size() < meaningNodeIndex ? lexemesForWord.size() - 1 : meaningNodeIndex - 1;
								Lexeme lexemeForWord = lexemesForWord.get(lexemeIndex);
								meaningNodeIndex++;
								useExistingLexeme = true;
								meaningId = (Long) lexemeForWord.getMeaningId();
							}
							List<LexemeToWordData> latinTerms = extractLatinTerms(meaningGroupNode, latinTermExp, headword);
							latinTerms.forEach(term -> term.meaningId = meaningId);
							if (useExistingLexeme) {
								boolean latinTermExists = context.latinTermins.stream().anyMatch(latinTerm -> latinTerm.meaningId.equals(meaningId));
								if (!latinTermExists) {
									context.latinTermins.addAll(latinTerms);
								}
							} else {
								context.latinTermins.addAll(latinTerms);
							}

							List<Node> translationGroupNodes = meaningGroupNode.selectNodes(translationGroupExp);
							for (Node transalationGroupNode : translationGroupNodes) {
								String russianWord = extractCleanValue(transalationGroupNode, translationValueExp);
								WordData russianWordData = findOrCreateWord(context, cleanUpWord(russianWord), russianWord, LANG_RUS, null, null);
								List<String> russianRegisters = extractCleanValues(transalationGroupNode, registersExp);
								boolean createNewRussianLexeme = true;
								Long russianLexemeId = null;
								if (useExistingLexeme) {
									createNewRussianLexeme = getExistingLexemeForWordAndMeaning(russianWordData.id, meaningId) == null;
									if (createNewRussianLexeme) {
										writeToLogFile(headword, "lisan vene vaste olemasolevale mõistele", wordData.value + " : " + russianWordData.value);
									}
								}
								if (createNewRussianLexeme) {
									Lexeme russianLexeme = new Lexeme();
									russianLexeme.setWordId(russianWordData.id);
									russianLexeme.setMeaningId(meaningId);
									russianLexeme.setLevel1(russianWordData.level1);
									russianLexeme.setLevel2(1);
									russianLexeme.setLevel3(1);
									russianLexemeId = createLexemeIfNotExists(russianLexeme);
									russianWordData.level1++;
								}
								if (russianLexemeId != null) {
									saveRegisters(russianLexemeId, russianRegisters, headword);
								}
								List<String> additionalDomains = extractCleanValues(transalationGroupNode, domainsExp);
								processDomains(null, meaningId, additionalDomains);
							}
						}
					}
				}
			}
		}
	}

	private WordData findOrCreateWord(Context context, String wordValue, String wordDisplayForm, String wordLanguage, String aspect, String vocalForm) throws Exception {
		Optional<WordData> word = context.importedWords.stream()
				.filter(w -> Objects.equals(w.value, wordValue) &&
						(Objects.equals(w.displayForm, wordDisplayForm) || Objects.equals(wordValue, wordDisplayForm)))
				.findFirst();
		if (word.isPresent()) {
			return word.get();
		} else {
			WordData newWord = createDefaultWordFrom(wordValue, wordLanguage, null, wordDisplayForm, vocalForm, null, null, aspect);
			context.importedWords.add(newWord);
			return newWord;
		}
	}

	private void cacheMeaningRelatedData(
			Context context, Long meaningId, List<String> definitions, WordData keyword, int level1,
			List<LexemeToWordData> latinTerms,
			List<LexemeToWordData> abbreviationFullWords) {
		latinTerms.forEach(data -> data.meaningId = meaningId);
		context.latinTermins.addAll(latinTerms);
		abbreviationFullWords.forEach(data -> data.meaningId = meaningId);
		context.abbreviationFullWords.addAll(abbreviationFullWords);

		context.meanings.stream()
				.filter(m -> Objects.equals(m.meaningId, meaningId))
				.forEach(m -> {
					m.meaningDefinitions.clear();
					m.meaningDefinitions.addAll(definitions);
				});
		List<WordData> words = new ArrayList<>();
		words.add(keyword);
		words.forEach(word -> {
			context.meanings.addAll(convertToMeaningData(latinTerms, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(abbreviationFullWords, word, level1, definitions));
		});
	}

	private void savePosAndDeriv(WordData newWordData, List<String> meaningPosCodes, List<String> lexemePosCodes, Long lexemeId, String headword) {

		Set<String> combinedPosCodes = new HashSet<>();
		try {
			combinedPosCodes.addAll(lexemePosCodes);
			if (combinedPosCodes.isEmpty()) {
				combinedPosCodes.addAll(meaningPosCodes);
			}
			if (combinedPosCodes.isEmpty()) {
				combinedPosCodes.addAll(newWordData.posCodes);
			}
			if (combinedPosCodes.size() > 1) {
				String posCodesStr = String.join(",", combinedPosCodes);
				writeToLogFile(headword, "Leiti rohkem kui üks sõnaliik <x:sl>", posCodesStr);
			}
			for (String posCode : combinedPosCodes) {
				if (posCodes.containsKey(posCode)) {
					String mappedPosCode = posCodes.get(posCode);
					createLexemePos(lexemeId, mappedPosCode);
				}
			}
		} catch (Exception e) {
			logger.debug("lexemeId {} : newWord : {}, {}, {}",
					lexemeId, newWordData.value, newWordData.id, String.join(",", combinedPosCodes));
			logger.error("ERROR", e);
		}
	}

	private void saveGrammars(WordData newWordData, List<String> meaningGrammars, List<String> lexemeGrammars, Long lexemeId) throws Exception {

		Set<String> grammars = new HashSet<>();
		grammars.addAll(lexemeGrammars);
		grammars.addAll(meaningGrammars);
		grammars.addAll(newWordData.grammars);
		for (String grammar : grammars) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammar, dataLang);
		}
	}

	private void saveGovernments(WordData wordData, List<String> meaningGovernments, Long lexemeId) throws Exception {

		Set<String> governments = new HashSet<>();
		governments.addAll(meaningGovernments);
		if (governments.isEmpty()) {
			governments.addAll(wordData.governments);
		}
		for (String government : governments) {
			createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government);
		}
	}

	private List<Usage> extractUsages(Node node) {

		final String usageBlockExp = "x:np";
		final String usageGroupExp = "x:ng";
		final String usageExp = "x:n";
		final String usageDefinitionExp = "x:qnp/x:nd";

		List<Usage> usages = new ArrayList<>();
		List<Node> usageBlockNodes = node.selectNodes(usageBlockExp);
		for (Node usageBlockNode : usageBlockNodes) {
			if (isRestricted(usageBlockNode)) {
				continue;
			}
			List<Node> usageGroupNodes = usageBlockNode.selectNodes(usageGroupExp);
			for (Node usageGroupNode : usageGroupNodes) {
				if (isRestricted(usageGroupNode)) {
					continue;
				}
				List<String> usageOriginalValues = extractOriginalValues(usageGroupNode, usageExp);
				List<String> usageDefinitionValues = extractOriginalValues(usageGroupNode, usageDefinitionExp);
				List<UsageTranslation> usageTranslations = extractUsageTranslations(usageGroupNode);
				for (String usageOriginalValue : usageOriginalValues) {
					String usageCleanValue = cleanEkiEntityMarkup(usageOriginalValue);
					List<Map<String, Object>> wordInSs1 = getWordsInSs1(usageCleanValue);
					if (isUsage(usageCleanValue, wordInSs1)) {
						Usage usage = new Usage();
						usage.setValue(usageOriginalValue);
						usage.setDefinitions(usageDefinitionValues);
						usage.setUsageTranslations(usageTranslations);
						usages.add(usage);
					}
				}
			}
		}
		return usages;
	}

	private boolean isUsage(String usageValue, List<Map<String, Object>> wordsInSs) {
		return usageValue.contains(" ") && CollectionUtils.isEmpty(wordsInSs);
	}

	private List<UsageTranslation> extractUsageTranslations(Node node) {

		final String usageTranslationExp = "x:qnp/x:qng/x:qn";

		List<UsageTranslation> translations = new ArrayList<>();
		List<String> translationValues = extractOriginalValues(node, usageTranslationExp);
		for (String translationValue : translationValues) {
			UsageTranslation translation = new UsageTranslation();
			translation.setValue(translationValue);
			translation.setLang(LANG_RUS);
			translations.add(translation);
		}

		return translations;
	}

	private List<LexemeToWordData> extractLatinTerms(Node node, String latinTermExp, String headword) throws Exception {
		return extractLexemeMetadata(node, latinTermExp, null, headword);
	}

	private List<LexemeToWordData> extractAbbreviationFullWords(Node node, String abbreviationFullWordExp, String headword) throws Exception {
		return extractLexemeMetadata(node, abbreviationFullWordExp, null, headword);
	}

	private List<String> extractGovernments(Node node) {
		final String wordGovernmentExp = "x:grk/x:r";
		return extractCleanValues(node, wordGovernmentExp);
	}

	private List<LexemeToWordData> extractRussianWords(
			Node node,
			List<String> additionalDomains,
			List<List<LexemeToWordData>> aspectGroups,
			String headword,
			boolean isVerb,
			Map<String, List<LexemeToWordData>> abbreviationWords) throws Exception {

		final String wordGroupExp = "x:xp/x:xg";
		final String wordExp = "x:x";
		final String registerExp = "x:s";
		final String governmentExp = "x:vrek";
		final String domainExp = "x:v";
		final String aspectValueExp = "x:aspg/x:aspvst";
		final String vocalFormExp = "x:xhld";
		final String sourceExp = "x:vsall";
		final String corpFrequencyExp = "x:xfreq";
		final String abbreviationFullWordExp = "x:xlhx";

		List<LexemeToWordData> dataList = new ArrayList<>();
		List<Node> wordGroupNodes = node.selectNodes(wordGroupExp);
		for (Node wordGroupNode : wordGroupNodes) {
			String srcWord = extractCleanValueSkipStress(wordGroupNode, wordExp);
			String cleanWord = cleanUpWord(srcWord);
			if (isBlank(cleanWord)) {
				continue;
			}
			String vocalForm = extractCleanValue(wordGroupNode, vocalFormExp);
			String srcAspectWord = extractCleanValueSkipStress(wordGroupNode, aspectValueExp);
			LexemeToWordData wordData = new LexemeToWordData();
			wordData.word = cleanWord;
			wordData.displayForm = srcWord;
			wordData.headword = headword;
			wordData.vocalForm = vocalForm;
			wordData.registerCodes.addAll(extractCleanValues(wordGroupNode, registerExp));
			wordData.governments.addAll(extractCleanValues(wordGroupNode, governmentExp));
			wordData.sources.addAll(extractOriginalValues(wordGroupNode, sourceExp));
			wordData.corpFrequency = extractFloat(wordGroupNode, corpFrequencyExp);
			List<String> domainCodes = extractCleanValues(wordGroupNode, domainExp);
			if (CollectionUtils.isNotEmpty(domainCodes)) {
				additionalDomains.addAll(domainCodes);
			}
			boolean wordHasAspectPair = isNotBlank(srcAspectWord);
			if (wordHasAspectPair || wordContainsAspect(srcWord) || isVerb) {
				wordData.aspect = calculateAspect(srcWord);
			}
			dataList.add(wordData);
			List<LexemeToWordData> abbreviationFullWords = extractAbbreviationFullWords(wordGroupNode, abbreviationFullWordExp, headword);
			if (!abbreviationFullWordExp.isEmpty()) {
				abbreviationWords.put(wordData.word, abbreviationFullWords);
			}

			if (wordHasAspectPair) {
				String cleanAspectWord = cleanUpWord(srcAspectWord);
				if (Objects.equals(cleanWord, cleanAspectWord) && Objects.equals(srcWord, srcAspectWord)) {
					logger.warn("{} : words in aspect pair are the same : {}", headword, wordData.word);
					writeToLogFile(headword, "Aspekti paari märksõnad korduvad", wordData.word);
				} else {
					LexemeToWordData aspectData = new LexemeToWordData();
					aspectData.word = cleanAspectWord;
					aspectData.displayForm = srcAspectWord;
					aspectData.aspect = calculateAspect(srcAspectWord);
					aspectData.headword = headword;
					aspectData.registerCodes.addAll(wordData.registerCodes);
					aspectData.governments.addAll(wordData.governments);
					dataList.add(aspectData);
					List<LexemeToWordData> aspectGroup = new ArrayList<>();
					aspectGroup.add(wordData);
					aspectGroup.add(aspectData);
					aspectGroups.add(aspectGroup);
				}
			}
		}
		return dataList;
	}

	private void extractLogDataAndCreateLifecycleLog(Node articleNode, List<WordData> newWords) throws Exception {

		ArticleLogData logData = extractArticleLogData(articleNode);
		String dataset = "[" + getDataset() + "]";
		List<Long> wordIds = newWords.stream().map(id -> id.id).collect(Collectors.toList());
		createWordLifecycleLog(wordIds, logData, dataset);
	}

	private ArticleLogData extractArticleLogData(Node articleNode) throws ParseException {

		final String createdByExp = "x:K";
		final String createdOnExp = "x:KA";
		final String modifiedByExp = "x:T";
		final String modifiedOnExp = "x:TA";
		final String chiefEditedByExp = "x:PT";
		final String chiefEditedOnExp = "x:PTA";

		String createdBy = getNodeStringValue(articleNode, createdByExp);
		Timestamp createdOn = getNodeTimestampValue(articleNode, createdOnExp, dateFormat);
		String modifiedBy = getNodeStringValue(articleNode, modifiedByExp);
		Timestamp modifiedOn = getNodeTimestampValue(articleNode, modifiedOnExp, dateFormat);
		String chiefEditedBy = getNodeStringValue(articleNode, chiefEditedByExp);
		Timestamp chiefEditedOn = getNodeTimestampValue(articleNode, chiefEditedOnExp, dateFormat);

		ArticleLogData logData = new ArticleLogData();
		logData.setCreatedBy(createdBy);
		logData.setCreatedOn(createdOn);
		logData.setModifiedBy(modifiedBy);
		logData.setModifiedOn(modifiedOn);
		logData.setChiefEditedBy(chiefEditedBy);
		logData.setChiefEditedOn(chiefEditedOn);

		return logData;
	}

	private boolean wordContainsAspect(String word) {
		return word.endsWith("[*]") || word.endsWith("*");
	}

	private String calculateAspect(String word) {
		if (word.endsWith("[*]")) {
			return ASPECT_SOV_NESOV;
		} else if (word.endsWith("*")) {
			return ASPECT_SOV;
		}
		return ASPECT_NESOV;
	}

	private String extractCleanValueSkipStress(Node node, String xpathExp) {
		Element wordNode = (Element) node.selectSingleNode(xpathExp);
		return wordNode == null || isRestricted(wordNode) ? null : cleanEkiEntityMarkupSkipStress(wordNode.getTextTrim());
	}

	private String extractCleanValue(Node node, String xpathExp) {
		Element wordNode = (Element) node.selectSingleNode(xpathExp);
		return wordNode == null || isRestricted(wordNode) ? null : cleanEkiEntityMarkup(wordNode.getTextTrim());
	}

	private Float extractFloat(Node node, String xpathExp) {
		String numberAsString = extractCleanValue(node, xpathExp);
		if (numberAsString == null) {
			return null;
		} else {
			String cleanNumberString = numberAsString.replace(",", "");
			try {
				return Float.parseFloat(cleanNumberString);
			} catch (NumberFormatException ignored) {
				return null;
			}
		}
	}

	private List<Map<String, Object>> getWordsInSs1(String word) {
		return getWords(word, "ss1");
	}

	private List<Lexeme> getExistingLexemesForWord(Long wordId) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("wordId", wordId);
		params.put("datasetCode", getDataset());
		String sqlQueryStr = "select * from " + LEXEME + " where word_id = :wordId and dataset_code = :datasetCode order by level1, level2, level3";
		List<Lexeme> lexemes = basicDbService.getResults(sqlQueryStr, params, new LexemeRowMapper());
		return lexemes;
	}

	private Map<String, Object> getExistingLexemeForWordAndMeaning(Long wordId, Long meaningId) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_id", wordId);
		params.put("level1", 1);
		params.put("meaning_id", meaningId);
		params.put("dataset_code", getDataset());
		return basicDbService.select(LEXEME, params);
	}

	protected class Context extends SsBasedLoaderRunner.Context {
		private static final long serialVersionUID = 1L;

		Map<String, MeaningReferenceData> referencesToMeanings = new HashMap<>();
		Map<String, List<Guid>> ssGuidMap;
		Map<String, List<Mnr>> ssMnrMap;
		List<MeaningReferenceData> meaningReferences = new ArrayList<>();
		List<LexemeToWordData> abbreviationFullWordsRus = new ArrayList<>();
		Count multipleMeaningsGroupCount = new Count();
	}

	protected class MeaningReferenceData {
		List<WordData> words = new ArrayList<>();
		List<WordData> referencedWords = new ArrayList<>();
		List<Node> articleNodes = new ArrayList<>();
	}

}
