package eki.ekilex.runner;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.replaceChars;

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

import javax.transaction.Transactional;

import eki.common.data.Count;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.constant.WordRelationGroupType;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class Ev2LoaderRunner extends SsBasedLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Ev2LoaderRunner.class);

	private final static String LANG_RUS = "rus";
	private final static String ASPECT_TYPE_SOV = "сов.";
	private final static String ASPECT_TYPE_NESOV = "несов.";
	private final static String ASPECT_TYPE_SOV_NESOV = "сов. и несов.";
	private final static String POS_CODE_VERB = "v";
	private final static String meaningRefNodeExp = "x:S/x:tp/x:tvt";

	@Override
	protected Map<String, String> xpathExpressions() {
		Map<String, String> experssions = new HashMap<>();
		experssions.put("reportingId", "x:P/x:mg/x:m"); // use first word as id for reporting
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

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Transactional
	public void execute(String dataXmlFilePath1, String dataXmlFilePath2, Map<String, List<Guid>> ssGuidMap, boolean doReports) throws Exception {

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
		processLatinTerms(context);
		processAbbreviations(context);
		unusedMeaningReferencesReport(context);

		logger.debug("Found {} reused words", context.reusedWordCount.getValue());
		logger.debug("Found {} ss words", context.ssWordCount.getValue());

		end();
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

		logger.debug("Found abbreviations {} <x:lhx> : {} <x:xlhx>.", context.abbreviationFullWords.size(), context.abbreviationFullWordsRus);
		logger.debug("Processing started.");
		writeToLogFile("Lühendite töötlus <x:lhx> ja <x:xlhx>", "", "");

		Count newAbbreviationFullWordCount = processLexemeToWord(context, context.abbreviationFullWords, null, "Ei leitud märksõna, loome uue", dataLang);
		Count newAbbreviationFullWordRusCount = processLexemeToWord(context, context.abbreviationFullWordsRus, null, "Ei leitud märksõna, loome uue", LANG_RUS);
		createLexemeRelations(context, context.abbreviationFullWords, LEXEME_RELATION_ABBREVIATION, "Ei leitud ilmikut lühendi täis märksõnale", false);
		createLexemeRelations(context, context.abbreviationFullWordsRus, LEXEME_RELATION_ABBREVIATION, "Ei leitud ilmikut lühendi täis märksõnale", false);

		logger.debug("Words created {} {} : {} {}", newAbbreviationFullWordCount.getValue(), dataLang, newAbbreviationFullWordRusCount.getValue(), LANG_RUS);
		logger.debug("Abbreviations import done.");
	}


	@Transactional
	void processArticle(
			Node articleNode,
			Context context) throws Exception {

		final String articleBodyExp = "x:S";
		final String articlePhraseologyExp = "x:F";

		String reportingId = extractReporingId(articleNode);
		List<WordData> newWords = new ArrayList<>();
		if (articleHasMeanings(articleNode)) {
			processArticleHeader(context, articleNode, reportingId, newWords);
			try {
				Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
				if (contentNode != null) {
					processArticleContent(context, contentNode, reportingId, newWords);
				}
				Element phraseologyNode = (Element) articleNode.selectSingleNode(articlePhraseologyExp);
				if (phraseologyNode != null) {
					processPhraseology(context, phraseologyNode, reportingId, newWords);
				}
			} catch (Exception e) {
				logger.debug("KEYWORD : {}", reportingId);
				throw e;
			}
		} else if (articleHasMeaningReference(articleNode)) {
			List<WordData> referencedWords = extractMeaningReferences(articleNode);
			boolean atLeastOneReferenceExists = referencedWords.stream()
					.anyMatch(word -> context.importedWords.stream().anyMatch(iw -> Objects.equals(iw.value, word.value) && iw.homonymNr == word.homonymNr));
			if (atLeastOneReferenceExists) {
				processArticleHeader(context, articleNode, reportingId, newWords);
			}
			for (WordData refWord : referencedWords) {
				Optional<WordData> importedWord = context.importedWords.stream()
						.filter(w -> Objects.equals(w.value, refWord.value) && w.homonymNr == refWord.homonymNr).findFirst();
				if (importedWord.isPresent()) {
					Long meaningId = getMeaningIdOfTheFirstLexeme(importedWord.get().id);
					createLexemesForWords(newWords, meaningId);
					for (WordData newWord : newWords) {
						processMeaningReferences(context, newWord, meaningId);
					}
				} else {
					storeMeaningReference(context, refWord, newWords, articleNode);
				}
			}
		}
		context.importedWords.addAll(newWords);
	}

	private void storeMeaningReference(Context context, WordData refWord, List<WordData> targetWords, Node articleNode) {
		String key = generateRefKey(refWord);
		if (!context.referencesToMeanings.containsKey(key)) {
			context.referencesToMeanings.put(key, new MeaningReferenceData());
		}
		context.referencesToMeanings.get(key).words.addAll(targetWords);
		if(articleNode != null) {
			context.referencesToMeanings.get(key).articleNodes.add(articleNode);
		}
	}

	private Long getMeaningIdOfTheFirstLexeme(Long wordId) throws Exception {
		List<Map<String, Object>> lexemesForWord = findExistingLexemesForWord(wordId);
		Optional<Map<String, Object>> firstLexeme = lexemesForWord.stream()
				.filter(l -> Objects.equals(l.get("level1"), 1) && Objects.equals(l.get("level2"), 1) && Objects.equals(l.get("level3"), 1))
				.findFirst();
		return firstLexeme.map(lexemeMap -> (Long) lexemeMap.get("meaning_id")).orElse(null);
	}

	private String generateRefKey(WordData word) {
		return word.value + "|" + word.homonymNr;
	}

	private void createLexemesForWords(List<WordData> words, Long meaningId) throws Exception {
		if (meaningId != null) {
			for (WordData wordData : words) {
				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(wordData.id);
				lexeme.setMeaningId(meaningId);
				lexeme.setLevel1(wordData.level1);
				lexeme.setLevel2(1);
				lexeme.setLevel3(1);
				lexeme.setFrequencyGroupCode(wordData.frequencyGroup);
				wordData.level1++;
				Long lexemeId = createLexeme(lexeme, getDataset());
				if (lexemeId != null) {
					saveGovernments(wordData, Collections.emptyList(), lexemeId);
					savePosAndDeriv(wordData, Collections.emptyList(), Collections.emptyList(), lexemeId, wordData.value);
					saveGrammars(wordData, Collections.emptyList(), Collections.emptyList(), lexemeId);
				}
			}
		}
	}

	private List<WordData> extractMeaningReferences(Node articleNode) {
		String homonymNrAttr = "i";
		List<WordData> referencedWords = new ArrayList<>();
		List<Node> referenceNodes = articleNode.selectNodes(meaningRefNodeExp);
		for (Node refNode : referenceNodes) {
			Element refElement = (Element)refNode;
			WordData refWord = new WordData();
			refWord.value = cleanUpWord(refElement.getTextTrim());
			if (refElement.attributeValue(homonymNrAttr) != null) {
				refWord.homonymNr = Integer.parseInt(refElement.attributeValue(homonymNrAttr));
			}
			referencedWords.add(refWord);
		}
		return referencedWords;
	}

	private boolean articleHasMeaningReference(Node articleNode) {
		return !articleNode.selectNodes(meaningRefNodeExp).isEmpty();
	}

	private boolean articleHasMeanings(Node articleNode) {
		final String meaningGroupNodeExp = "x:S/x:tp/x:tg";
		return !articleNode.selectNodes(meaningGroupNodeExp).isEmpty();
	}

	private void processMeaningReferences(Context context, WordData newWordData, Long meaningId) throws Exception {
		String key = generateRefKey(newWordData);
		if (context.referencesToMeanings.containsKey(key)) {
			MeaningReferenceData refData = context.referencesToMeanings.get(key);
			context.referencesToMeanings.remove(key);
			List<WordData> referringWords = new ArrayList<>();
			if (refData.words.isEmpty()) {
				for(Node articleNode : refData.articleNodes) {
					String reportingId = extractReporingId(articleNode);
					processArticleHeader(context, articleNode, reportingId, referringWords);
				}
				context.importedWords.addAll(referringWords);
			} else {
				referringWords =  refData.words;
			}
			createLexemesForWords(referringWords, meaningId);
			for (WordData refWord : referringWords) {
				processMeaningReferences(context, refWord, meaningId);
			}
		}
	}

	private void processPhraseology(Context context, Element node, String reportingId, List<WordData> newWords) throws Exception {

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
				List<Map<String, Object>> lexemesForWord = findExistingLexemesForWord(wordData.id);
				int lexemeLevel1 = 1;
				List<Node> meaningGroupNodes = groupNode.selectNodes(meaningGroupExp);
				for (Node meaningGroupNode: meaningGroupNodes) {
					Optional<Map<String, Object>> existingLexeme = Optional.empty();
					if (!lexemesForWord.isEmpty()) {
						int level1 = lexemeLevel1;
						existingLexeme = lexemesForWord.stream().filter(lex -> Objects.equals(level1, lex.get("level1"))).findFirst();
					}
					Long meaningId;
					Long lexemeId;
					if (existingLexeme.isPresent()) {
						meaningId = (Long) existingLexeme.get().get("meaning_id");
						lexemeId = (Long) existingLexeme.get().get("id");
					} else {
						meaningId = createMeaning(new Meaning());
						Lexeme lexeme = new Lexeme();
						lexeme.setWordId(wordData.id);
						lexeme.setMeaningId(meaningId);
						lexeme.setLevel1(lexemeLevel1);
						lexeme.setLevel2(1);
						lexeme.setLevel3(1);
						lexemeId = createLexeme(lexeme, getDataset());
					}

					List<String> definitions = extractCleanValues(meaningGroupNode, definitionsExp);
					for (String definition : definitions) {
						createOrSelectDefinition(meaningId, definition, dataLang, getDataset());
					}

					List<String> domains = extractCleanValues(meaningGroupNode, domainsExp);
					processDomains(null, meaningId, domains);

					if (lexemeId != null) {
						List<String> governments = extractCleanValues(meaningGroupNode, governmentsExp);
						List<String> registers = extractCleanValues(meaningGroupNode, registersExp);
						saveGovernments(wordData, governments, lexemeId);
						saveRegisters(lexemeId, registers, reportingId);
					}
					lexemeLevel1++;

					List<Node> translationGroupNodes = meaningGroupNode.selectNodes(translationGroupExp);
					for(Node transalationGroupNode : translationGroupNodes) {
						String russianWord = extractAsString(transalationGroupNode, translationValueExp);
						WordData russianWordData = findOrCreateWord(context, cleanUpWord(russianWord), russianWord, LANG_RUS, null, null);
						List<String> russianRegisters = extractCleanValues(transalationGroupNode, registersExp);
						Lexeme russianLexeme = new Lexeme();
						russianLexeme.setWordId(russianWordData.id);
						russianLexeme.setMeaningId(meaningId);
						russianLexeme.setLevel1(russianWordData.level1);
						russianLexeme.setLevel2(1);
						russianLexeme.setLevel3(1);
						Long russianLexemeId = createLexeme(russianLexeme, getDataset());
						russianWordData.level1++;
						if (russianLexemeId != null) {
							saveRegisters(russianLexemeId, russianRegisters, reportingId);
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
			String reportingId,
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
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid, 0);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
				wordData.id = createOrSelectWord(word, paradigms, getDataset(), context.ssGuidMap, context.ssWordCount, context.reusedWordCount);
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

	private void processArticleContent(Context context, Element contentNode, String reportingId, List<WordData> newWords) throws Exception {

		final String meaningNumberGroupExp = "x:tp";
		final String meaningPosCodeExp = "x:sl";
		final String meaningPosCode2Exp = "x:grk/x:sl";
		final String meaningGroupExp = "x:tg";
		final String meaningDefinitionExp = "x:dg/x:d";
		final String registerExp = "x:dg/x:s";
		final String latinTermExp = "x:dg/x:ld";
		final String abbreviationFullWordExp = "x:dg/x:lhx";

		boolean isVerb = newWords.get(0).posCodes.contains(POS_CODE_VERB);
		List<Node> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
		int lexemeLevel1 = 0;
		for (Node meaningNumberGroupNode : meaningNumberGroupNodes) {
			lexemeLevel1++;
			List<String> meaningPosCodes = extractPosCodes(meaningNumberGroupNode, meaningPosCodeExp);
			meaningPosCodes.addAll(extractPosCodes(meaningNumberGroupNode, meaningPosCode2Exp));
			List<String> meaningGovernments = extractGovernments(meaningNumberGroupNode);
			List<String> meaningGrammars = extractGrammar(meaningNumberGroupNode);
			List<Node> meaningGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
			List<Usage> usages = extractUsages(meaningNumberGroupNode);

			int lexemeLevel2 = 0;
			for (Node meaningGroupNode : meaningGroupNodes) {
				lexemeLevel2++;
				List<String> lexemePosCodes =  extractPosCodes(meaningGroupNode, meaningPosCodeExp);
				List<String> lexemeGrammars = extractGrammar(meaningGroupNode);
				List<String> registers = extractCleanValues(meaningGroupNode, registerExp);

				Long meaningId;
				List<String> definitionsToAdd = new ArrayList<>();
				List<String> definitionsToCache = new ArrayList<>();
				List<String> definitions = extractCleanValues(meaningGroupNode, meaningDefinitionExp);
				List<LexemeToWordData> meaningLatinTerms = extractLatinTerms(meaningGroupNode, latinTermExp, reportingId);
				List<LexemeToWordData> meaningAbbreviationFullWords = extractAbbreviationFullWords(meaningGroupNode, abbreviationFullWordExp, reportingId);
				List<String> additionalDomains = new ArrayList<>();
				List<List<LexemeToWordData>> aspectGroups = new ArrayList<>();
				Map<String, List<LexemeToWordData>> russianAbbreviationWords = new HashMap<>();
				List<LexemeToWordData> meaningRussianWords = extractRussianWords(
						meaningGroupNode, additionalDomains, aspectGroups, reportingId, isVerb, russianAbbreviationWords);
				List<LexemeToWordData> connectedWords =
						Stream.of(
								meaningLatinTerms.stream(),
								meaningAbbreviationFullWords.stream()
						).flatMap(i -> i).collect(toList());
				WordToMeaningData meaningData = findExistingMeaning(context, newWords.get(0), lexemeLevel1, connectedWords, definitions);
				if (meaningData == null) {
					meaningId = createMeaning(new Meaning());
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
					Long lexemeId = createLexeme(lexeme, getDataset());
					if (lexemeId != null) {
						// FIXME: add usages and subword relations only to first lexeme on the second level
						// this is temporary solution, till EKI provides better one
						if (lexemeLevel2 == 1) {
							createUsages(lexemeId, usages, dataLang);
						}
						saveGovernments(newWordData, meaningGovernments, lexemeId);
						savePosAndDeriv(newWordData, meaningPosCodes, lexemePosCodes, lexemeId, reportingId);
						saveGrammars(newWordData, meaningGrammars, lexemeGrammars, lexemeId);
						saveRegisters(lexemeId, registers, reportingId);
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
			for (WordData w : newWords) {
				w.level1 = lexemeLevel1 + 1;
			}
			processWordsInUsageGroups(context, meaningNumberGroupNode, reportingId);
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
			Long lexemeId = createLexeme(lexeme, getDataset());
			russianWord.level1++;
			if (lexemeId != null) {
				for (String government : russianWordData.governments) {
					createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government);
				}
				if (isNotBlank(russianWordData.register)) {
					saveRegisters(lexemeId, Collections.singletonList(russianWordData.register), russianWordData.word);
				}
				for (String source : russianWordData.sources) {
					SourceType sourceType = StringUtils.startsWith(source, "http") ? SourceType.DOCUMENT : SourceType.UNKNOWN;
					Long sourceId = getSource(sourceType, EXT_SOURCE_ID_NA, source);
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
				Long wordGroup = createWordRelationGroup(WordRelationGroupType.ASPECTS);
				for (LexemeToWordData wordData : aspectGroup) {
					createWordRelationGroupMember(wordGroup, wordData.wordId);
				}
			}
		}
	}

	private void processWordsInUsageGroups(Context context, Node node, String reportingId) throws Exception {

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
				if (isRestricted(usageGroupNode)) continue;
				List<String> wordValues = extractCleanValues(usageGroupNode, usageExp);
				for (String wordValue : wordValues) {
					String word = cleanUpWord(wordValue);
					if (!isUsage(word)) {
						List<Node> meaningGroupNodes = usageGroupNode.selectNodes(meaningGroupExp);
						if (meaningGroupNodes.isEmpty()) {
							continue;
						}
						List<Map<String, Object>> wordInSs1 = getWordsInSs1(word);
						WordData wordData = findOrCreateWordUsingSs1(context, wordValue, word, wordInSs1);
						List<Map<String, Object>> lexemesForWord = findExistingLexemesForWord(wordData.id);
						int meaningNodeIndex = 1;
						for (Node meaningGroupNode: meaningGroupNodes) {
							Long meaningId;
							boolean useExistingLexeme = false;
							if (!lexemesForWord.isEmpty()) {
								int lexemeIndex = lexemesForWord.size() < meaningNodeIndex ? lexemesForWord.size() - 1 : meaningNodeIndex - 1;
								Map<String, Object> lexemeForWord = lexemesForWord.get(lexemeIndex);
								meaningNodeIndex++;
								useExistingLexeme = true;
								meaningId = (Long)lexemeForWord.get("meaning_id");
							} else {
								meaningId = createMeaning(new Meaning());
								List<String> domains = extractCleanValues(meaningGroupNode, domainsExp);
								processDomains(null, meaningId, domains);

								List<String> definitions = extractCleanValues(meaningGroupNode, definitionExp);
								for (String definition : definitions) {
									createOrSelectDefinition(meaningId, definition, dataLang, getDataset());
								}
								Lexeme lexeme = new Lexeme();
								lexeme.setWordId(wordData.id);
								lexeme.setMeaningId(meaningId);
								lexeme.setLevel1(wordData.level1);
								lexeme.setLevel2(1);
								lexeme.setLevel3(1);
								wordData.level1++;

								Long lexemeId = createLexeme(lexeme, getDataset());
								List<String> registers = extractCleanValues(meaningGroupNode, registersExp);
								saveRegisters(lexemeId, registers, reportingId);
							}

							List<LexemeToWordData> latinTerms = extractLatinTerms(meaningGroupNode, latinTermExp, reportingId);
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
							for(Node transalationGroupNode : translationGroupNodes) {
								String russianWord = extractAsString(transalationGroupNode, translationValueExp);
								WordData russianWordData = findOrCreateWord(context, cleanUpWord(russianWord), russianWord, LANG_RUS, null, null);
								List<String> russianRegisters = extractCleanValues(transalationGroupNode, registersExp);
								boolean createNewRussianLexeme = true;
								Long russianLexemeId = null;
								if (useExistingLexeme) {
									createNewRussianLexeme = findExistingLexemeForWordAndMeaning(russianWordData.id, meaningId) == null;
									if (createNewRussianLexeme) {
										writeToLogFile(reportingId, "lisan vene vaste olemasolevale mõistele", wordData.value + " : " + russianWordData.value);
									}
								}
								if (createNewRussianLexeme) {
									Lexeme russianLexeme = new Lexeme();
									russianLexeme.setWordId(russianWordData.id);
									russianLexeme.setMeaningId(meaningId);
									russianLexeme.setLevel1(russianWordData.level1);
									russianLexeme.setLevel2(1);
									russianLexeme.setLevel3(1);
									russianLexemeId = createLexeme(russianLexeme, getDataset());
									russianWordData.level1++;
								}
								if (russianLexemeId != null) {
									saveRegisters(russianLexemeId, russianRegisters, reportingId);
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
						(Objects.equals(w.displayForm, wordDisplayForm) || Objects.equals(wordValue, wordDisplayForm))).findFirst();
		if (word.isPresent()) {
			return word.get();
		} else {
			WordData newWord = createDefaultWordFrom(wordValue, wordDisplayForm, wordLanguage, null, aspect, null, vocalForm);
			context.importedWords.add(newWord);
			return newWord;
		}
	}

	private String cleanRussianTranslation(String usageValue) {
		return replaceChars(usageValue, "\"", "");
	}

	private void cacheMeaningRelatedData(
			Context context, Long meaningId, List<String> definitions, WordData keyword, int level1,
			List<LexemeToWordData> latinTerms,
			List<LexemeToWordData> abbreviationFullWords
	) {
		latinTerms.forEach(data -> data.meaningId = meaningId);
		context.latinTermins.addAll(latinTerms);
		abbreviationFullWords.forEach(data -> data.meaningId = meaningId);
		context.abbreviationFullWords.addAll(abbreviationFullWords);

		context.meanings.stream()
				.filter(m -> Objects.equals(m.meaningId, meaningId))
				.forEach(m -> {m.meaningDefinitions.clear(); m.meaningDefinitions.addAll(definitions);});
		List<WordData> words = new ArrayList<>();
		words.add(keyword);
		words.forEach(word -> {
			context.meanings.addAll(convertToMeaningData(latinTerms, word, level1, definitions));
			context.meanings.addAll(convertToMeaningData(abbreviationFullWords, word, level1, definitions));
		});
	}

	private void savePosAndDeriv(WordData newWordData, List<String> meaningPosCodes, List<String> lexemePosCodes, Long lexemeId, String reportingId) {

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
				writeToLogFile(reportingId, "Leiti rohkem kui üks sõnaliik <x:sl>", posCodesStr);
			}
			for (String posCode : combinedPosCodes) {
				if (posCodes.containsKey(posCode)) {
					Map<String, Object> params = new HashMap<>();
					params.put("lexeme_id", lexemeId);
					params.put("pos_code", posCodes.get(posCode));
					basicDbService.create(LEXEME_POS, params);
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
			if (isRestricted(usageBlockNode)) continue;
			List<Node> usageGroupNodes = usageBlockNode.selectNodes(usageGroupExp);
			for (Node usageGroupNode : usageGroupNodes) {
				if (isRestricted(usageGroupNode)) continue;
				List<String> usageOriginalValues = extractOriginalValues(usageGroupNode, usageExp);
				List<String> usageDefinitionValues = extractOriginalValues(usageGroupNode, usageDefinitionExp);
				List<UsageTranslation> usageTranslations = extractUsageTranslations(usageGroupNode);
				for (String usageOriginalValue : usageOriginalValues) {
					String usageCleanValue = cleanEkiEntityMarkup(usageOriginalValue);
					if (isUsage(usageCleanValue)) {
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

	private boolean isUsage(String usageValue) {
		return usageValue.contains(" ") && isNotWordInSs1(usageValue);
	}

	private List<UsageTranslation> extractUsageTranslations(Node node) {

		final String usageTranslationExp = "x:qnp/x:qng/x:qn";

		List<UsageTranslation> translations = new ArrayList<>();
		List<String> translationValues = extractOriginalValues(node, usageTranslationExp);
		for (String translationValue : translationValues) {
			UsageTranslation translation = new UsageTranslation();
			translation.setValue(cleanRussianTranslation(translationValue));
			translation.setLang(LANG_RUS);
			translations.add(translation);
		}

		return translations;
	}

	private List<LexemeToWordData> extractLatinTerms(Node node, String latinTermExp, String reportingId) throws Exception {
		return extractLexemeMetadata(node, latinTermExp, null, reportingId);
	}

	private List<LexemeToWordData> extractAbbreviationFullWords(Node node, String abbreviationFullWordExp, String reportingId) throws Exception {
		return extractLexemeMetadata(node, abbreviationFullWordExp, null, reportingId);
	}

	private List<String> extractGovernments(Node node) {
		final String wordGovernmentExp = "x:grk/x:r";
		return extractCleanValues(node, wordGovernmentExp);
	}

	private List<LexemeToWordData> extractRussianWords(
			Node node,
			List<String> additionalDomains,
			List<List<LexemeToWordData>> aspectGroups,
			String reportingId,
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
			String word = extractAsString(wordGroupNode, wordExp);
			String vocalForm = extractAsString(wordGroupNode, vocalFormExp);
			String aspectWord = extractAsString(wordGroupNode, aspectValueExp);
			LexemeToWordData wordData = new LexemeToWordData();
			wordData.word = cleanUpWord(word);

			if (isBlank(wordData.word)) continue;

			wordData.displayForm = word;
			wordData.reportingId = reportingId;
			wordData.vocalForm = vocalForm;
			wordData.register = extractAsString(wordGroupNode, registerExp);
			wordData.governments.addAll(extractCleanValues(wordGroupNode, governmentExp));
			wordData.sources.addAll(extractOriginalValues(wordGroupNode,sourceExp));
			wordData.corpFrequency = extractAsFloat(wordGroupNode, corpFrequencyExp);
			String domainCode = extractAsString(wordGroupNode, domainExp);
			if (domainCode != null) {
				additionalDomains.add(domainCode);
			}
			boolean wordHasAspectPair = isNotBlank(aspectWord);
			if (wordHasAspectPair || wordContainsAspectType(word) || isVerb) {
				wordData.aspect = calculateAspectType(word);
			}
			dataList.add(wordData);
			List<LexemeToWordData> abbreviationFullWords = extractAbbreviationFullWords(wordGroupNode, abbreviationFullWordExp, reportingId);
			if (!abbreviationFullWordExp.isEmpty()) {
				abbreviationWords.put(wordData.word, abbreviationFullWords);
			}

			if (wordHasAspectPair) {
				LexemeToWordData aspectData = new LexemeToWordData();
				aspectData.word = cleanUpWord(aspectWord);
				if (Objects.equals(wordData.word, aspectData.word)) {
					logger.warn("{} : words in aspect pair have same forms : {} : {}", reportingId, wordData.word, aspectData.word);
					writeToLogFile(reportingId, "Aspekti paari märksõnade vormid on samadsugused", wordData.word);
				} else {
					aspectData.aspect = calculateAspectType(aspectWord);
					aspectData.displayForm = aspectWord;
					aspectData.reportingId = reportingId;
					aspectData.register = wordData.register;
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

	private Float extractAsFloat(Node node, String xpathExp) {
		String numberAsString = extractAsString(node, xpathExp);
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

	private boolean wordContainsAspectType(String word) {
		return word.endsWith("[*]") || word.endsWith("*");
	}

	private String calculateAspectType(String word) {
		if (word.endsWith("[*]")) {
			return ASPECT_TYPE_SOV_NESOV;
		} else if (word.endsWith("*")) {
			return ASPECT_TYPE_SOV;
		}
		return ASPECT_TYPE_NESOV;
	}

	private String extractAsString(Node node, String xpathExp) {
		Element wordNode = (Element) node.selectSingleNode(xpathExp);
		return wordNode == null || isRestricted(wordNode) ? null : cleanEkiEntityMarkup(wordNode.getTextTrim());
	}

	private boolean isNotWordInSs1(String word) {
		List<Map<String, Object>> words = getWordsInSs1(word);
		return CollectionUtils.isEmpty(words);
	}

	private List<Map<String, Object>> getWordsInSs1(String word) {
		return getWords(word, "ss1");
	}

	private List<Map<String, Object>> findExistingLexemesForWord(Long wordId) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_id", wordId);
		params.put("dataset_code", getDataset());
		return basicDbService.selectAll(LEXEME, params);
	}

	private Map<String, Object> findExistingLexemeForWordAndMeaning(Long wordId, Long meaningId) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_id", wordId);
		params.put("level1", 1);
		params.put("meaning_id", meaningId);
		params.put("dataset_code", getDataset());
		return basicDbService.select(LEXEME, params);
	}

	protected class Context extends SsBasedLoaderRunner.Context {
		Map<String, MeaningReferenceData> referencesToMeanings = new HashMap<>();
		Map<String, List<Guid>> ssGuidMap;
		List<LexemeToWordData> abbreviationFullWordsRus = new ArrayList<>();
	}

	protected class MeaningReferenceData {
		List<WordData> words = new ArrayList<>();
		List<Node> articleNodes = new ArrayList<>();
	}

}
