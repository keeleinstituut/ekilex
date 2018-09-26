package eki.ekilex.runner;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import java.io.InputStream;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
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

	private final static String SQL_SELECT_WORD_BY_DATASET = "sql/select_word_by_dataset.sql";
	private final static String russianLang = "rus";

	private String sqlSelectWordByDataset;

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

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_DATASET);
		sqlSelectWordByDataset = getContent(resourceFileInputStream);
	}

	@Override
	public String getDataset() {
		return "ev2";
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataXmlFilePath2, Map<String, List<Guid>> ssGuidMap, boolean doReports) throws Exception {

		long t1, t2;
		t1 = System.currentTimeMillis();
		logger.debug("Loading EV2...");

		reportingEnabled = doReports;
		if (reportingEnabled) {
			reportComposer = new ReportComposer("EV2 import", ARTICLES_REPORT_NAME, DESCRIPTIONS_REPORT_NAME, MEANINGS_REPORT_NAME);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Document dataDoc2 = xmlReader.readDocument(dataXmlFilePath2);
		Element rootElement = dataDoc.getRootElement();
		Element rootElement2 = dataDoc2.getRootElement();

		writeToLogFile("Artiklite töötlus", "", "");
		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(o -> o instanceof Element).collect(toList());
		articleNodes.addAll((List<Element>) rootElement2.content().stream().filter(o -> o instanceof Element).collect(toList()));

		long articleCount = articleNodes.size();
		logger.debug("{} articles found", articleCount);

		Context context = new Context();
		long progressIndicator = articleCount / Math.min(articleCount, 100);
		long articleCounter = 0;
		for (Element articleNode : articleNodes) {
			processArticle(articleNode, ssGuidMap, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);
		processLatinTerms(context);

		logger.debug("Found {} reused words", context.reusedWordCount.getValue());
		logger.debug("Found {} ss words", context.ssWordCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	@Transactional
	void processArticle(
			Element articleNode,
			Map<String, List<Guid>> ssGuidMap,
			Context context) throws Exception {

		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";
		final String articleGuidExp = "x:G";
		final String articlePhraseologyExp = "x:F";

		String reportingId = extractReporingId(articleNode);
		String guid = extractGuid(articleNode, articleGuidExp);
		List<WordData> newWords = new ArrayList<>();

		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		processArticleHeader(headerNode, guid, reportingId, newWords, ssGuidMap, context);
		try {
			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(reportingId, contentNode, newWords, context);
			}
			Element phraseologyNode = (Element) articleNode.selectSingleNode(articlePhraseologyExp);
			if (phraseologyNode != null) {
				processPhraseology(reportingId, phraseologyNode, context);
			}
		} catch (Exception e) {
			logger.debug("KEYWORD : {}", reportingId);
			throw e;
		}
		context.importedWords.addAll(newWords);
	}

	private void processPhraseology(String reportingId, Element node, Context context) throws Exception {

		final String phraseologyGroupExp = "x:fg";
		final String phraseologyValueExp = "x:f";
		final String meaningGroupExp = "x:fqnp";
		final String governmentsExp = "x:r";
		final String domainsExp = "x:v";
		final String registersExp = "x:s";
		final String definitionsExp = "x:fd";
		final String translationGroupExp = "x:fqng";
		final String translationValueExp = "x:qf";

		List<Element> groupNodes = node.selectNodes(phraseologyGroupExp);
		for (Element groupNode : groupNodes) {
			List<String> wordValues = extractValuesAsStrings(groupNode, phraseologyValueExp);
			for (String wordValue : wordValues) {
				String word = cleanUp(wordValue);
				if (isNotWordInSs1(word)) {
					continue;
				}
				WordData wordData = findOrCreateWord(context, word, wordValue, dataLang);
				List<Element> meaningGroupNodes = groupNode.selectNodes(meaningGroupExp);
				int lexemeLevel1 = 1;
				for (Element meaningGroupNode: meaningGroupNodes) {
					Long meaningId = createMeaning(new Meaning());

					List<String> definitions = extractValuesAsStrings(meaningGroupNode, definitionsExp);
					for (String definition : definitions) {
						createDefinition(meaningId, definition, dataLang, getDataset());
					}

					List<String> domains = extractValuesAsStrings(meaningGroupNode, domainsExp);
					processDomains(null, meaningId, domains);

					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(wordData.id);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(lexemeLevel1);
					lexeme.setLevel2(1);
					lexeme.setLevel3(1);
					Long lexemeId = createLexeme(lexeme, getDataset());
					if (lexemeId != null) {
						List<String> governments = extractValuesAsStrings(meaningGroupNode, governmentsExp);
						List<String> registers = extractValuesAsStrings(meaningGroupNode, registersExp);
						saveGovernments(wordData, governments, lexemeId);
						saveRegisters(lexemeId, registers, reportingId);
					}
					lexemeLevel1++;

					List<Element> translationGroupNodes = meaningGroupNode.selectNodes(translationGroupExp);
					for(Element transalationGroupNode : translationGroupNodes) {
						String russianWord = extractAsString(transalationGroupNode, translationValueExp);
						WordData russianWordData = findOrCreateWord(context, cleanUp(russianWord), russianWord, russianLang);
						List<String> russianRegisters = extractValuesAsStrings(transalationGroupNode, registersExp);
						Lexeme russianLexeme = new Lexeme();
						russianLexeme.setWordId(russianWordData.id);
						russianLexeme.setMeaningId(meaningId);
						russianLexeme.setLevel1(russianWordData.level1);
						russianWordData.level1++;
						russianLexeme.setLevel2(1);
						russianLexeme.setLevel3(1);
						Long russianLexemeId = createLexeme(russianLexeme, getDataset());
						if (russianLexemeId != null) {
							saveRegisters(russianLexemeId, russianRegisters, reportingId);
						}
						List<String> additionalDomains = extractValuesAsStrings(transalationGroupNode, domainsExp);
						processDomains(null, meaningId, additionalDomains);
					}
				}
			}
		}
	}

	private void processArticleHeader(
			Element headerNode,
			String guid,
			String reportingId,
			List<WordData> newWords,
			Map<String, List<Guid>> ssGuidMap,
			Context context) throws Exception {

		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordGrammarPosCodesExp = "x:grk/x:sl";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid, 0);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
				wordData.id = createOrSelectWord(word, paradigms, getDataset(), ssGuidMap, context.ssWordCount, context.reusedWordCount);
			}

			List<PosData> posCodes = extractPosCodes(wordGroupNode, wordPosCodeExp);
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

	private void processArticleContent(String reportingId, Element contentNode, List<WordData> newWords, Context context) throws Exception {

		final String meaningNumberGroupExp = "x:tp";
		final String meaningPosCodeExp = "x:sl";
		final String meaningPosCode2Exp = "x:grk/x:sl";
		final String meaningGroupExp = "x:tg";
		final String meaningDefinitionExp = "x:dg/x:d";
		final String registerExp = "x:dg/x:s";
		final String latinTermExp = "x:dg/x:ld";

		List<Element> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
		int lexemeLevel1 = 0;
		for (Element meaningNumberGroupNode : meaningNumberGroupNodes) {
			lexemeLevel1++;
			List<PosData> meaningPosCodes = extractPosCodes(meaningNumberGroupNode, meaningPosCodeExp);
			meaningPosCodes.addAll(extractPosCodes(meaningNumberGroupNode, meaningPosCode2Exp));
			List<String> meaningGovernments = extractGovernments(meaningNumberGroupNode);
			List<String> meaningGrammars = extractGrammar(meaningNumberGroupNode);
			List<Element> meaningGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
			List<Usage> usages = extractUsages(meaningNumberGroupNode);

			int lexemeLevel2 = 0;
			List<Long> mainLexemeIds = new ArrayList<>();
			for (Element meaningGroupNode : meaningGroupNodes) {
				lexemeLevel2++;
				List<PosData> lexemePosCodes =  extractPosCodes(meaningGroupNode, meaningPosCodeExp);
				List<String> lexemeGrammars = extractGrammar(meaningGroupNode);
				List<String> registers = extractValuesAsStrings(meaningGroupNode, registerExp);

				Long meaningId;
				List<String> definitionsToAdd = new ArrayList<>();
				List<String> definitionsToCache = new ArrayList<>();
				List<String> definitions = extractValuesAsStrings(meaningGroupNode, meaningDefinitionExp);
				List<LexemeToWordData> meaningLatinTerms = extractLatinTerms(meaningGroupNode, latinTermExp, reportingId);
				List<String> additionalDomains = new ArrayList<>();
				List<LexemeToWordData> meaningRussianWords = extractRussianWords(meaningGroupNode, additionalDomains, reportingId);
				List<LexemeToWordData> connectedWords =
						Stream.of(
								meaningLatinTerms.stream()
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
						createDefinition(meaningId, definition, dataLang, getDataset());
					}
					if (definitionsToAdd.size() > 1) {
						writeToLogFile(DESCRIPTIONS_REPORT_NAME, reportingId, "Leitud rohkem kui üks seletus <s:d>", newWords.get(0).value);
					}
				}
				cacheMeaningRelatedData(context, meaningId, definitionsToCache, newWords.get(0), lexemeLevel1, meaningLatinTerms);

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
							mainLexemeIds.add(lexemeId);
						}
						saveGovernments(newWordData, meaningGovernments, lexemeId);
						savePosAndDeriv(newWordData, meaningPosCodes, lexemePosCodes, lexemeId, reportingId);
						saveGrammars(newWordData, meaningGrammars, lexemeGrammars, lexemeId);
						saveRegisters(lexemeId, registers, reportingId);
					}
				}
				for (LexemeToWordData russianWord : meaningRussianWords) {
					WordData russianWordData = findOrCreateWord(context, russianWord.word, russianWord.displayForm, russianLang);
					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(russianWordData.id);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(russianWordData.level1);
					russianWordData.level1++;
					lexeme.setLevel2(1);
					lexeme.setLevel3(1);
					Long lexemeId = createLexeme(lexeme, getDataset());
					if (lexemeId != null) {
						if (StringUtils.isNotBlank(russianWord.government)) {
							createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, russianWord.government);
						}
						if (StringUtils.isNotBlank(russianWord.register)) {
							saveRegisters(lexemeId, asList(russianWord.register), russianWord.word);
						}
					}
				}
			}
			processWordsInUsageGroups(context, meaningNumberGroupNode, mainLexemeIds, reportingId);
		}
	}

	private void processWordsInUsageGroups(Context context, Element node, List<Long> mainLexemeIds, String reportingId) throws Exception {

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

		List<Element> usageBlockNodes = node.selectNodes(usageBlockExp);
		for (Element usageBlockNode : usageBlockNodes) {
			if (isRestricted(usageBlockNode)) continue;
			List<Element> usageGroupNodes = usageBlockNode.selectNodes(usageGroupExp);
			for (Element usageGroupNode : usageGroupNodes) {
				List<String> wordValues = extractValuesAsStrings(usageGroupNode, usageExp);
				for (String wordValue : wordValues) {
					if (!isUsage(cleanUp(wordValue))) {
						int level1 = 1;
						WordData wordData = findOrCreateWord(context, cleanUp(wordValue), wordValue, dataLang);
						List<Element> meaningGroupNodes = usageGroupNode.selectNodes(meaningGroupExp);
						for (Element meaningGroupNode: meaningGroupNodes) {
							Long meaningId = createMeaning(new Meaning());

							List<LexemeToWordData> latinTerms = extractLatinTerms(meaningGroupNode, latinTermExp, reportingId);
							latinTerms.forEach(term -> term.meaningId = meaningId);
							context.latinTermins.addAll(latinTerms);

							List<String> domains = extractValuesAsStrings(meaningGroupNode, domainsExp);
							processDomains(null, meaningId, domains);

							List<String> definitions = extractValuesAsStrings(meaningGroupNode, definitionExp);
							for (String definition : definitions) {
								createDefinition(meaningId, definition, dataLang, getDataset());
							}

							Lexeme lexeme = new Lexeme();
							lexeme.setWordId(wordData.id);
							lexeme.setMeaningId(meaningId);
							lexeme.setLevel1(level1);
							lexeme.setLevel2(1);
							lexeme.setLevel3(1);
							Long lexemeId = createLexeme(lexeme, getDataset());
							if (lexemeId != null) {
								List<String> registers = extractValuesAsStrings(meaningGroupNode, registersExp);
								saveRegisters(lexemeId, registers, reportingId);
								for (Long mainLexemeId : mainLexemeIds) {
									createLexemeRelation(mainLexemeId, lexemeId, LEXEME_RELATION_SUB_WORD);
								}
							}
							level1++;

							List<Element> translationGroupNodes = meaningGroupNode.selectNodes(translationGroupExp);
							for(Element transalationGroupNode : translationGroupNodes) {
								String russianWord = extractAsString(transalationGroupNode, translationValueExp);
								WordData russianWordData = findOrCreateWord(context, cleanUp(russianWord), russianWord, russianLang);
								List<String> russianRegisters = extractValuesAsStrings(transalationGroupNode, registersExp);
								Lexeme russianLexeme = new Lexeme();
								russianLexeme.setWordId(russianWordData.id);
								russianLexeme.setMeaningId(meaningId);
								russianLexeme.setLevel1(russianWordData.level1);
								russianWordData.level1++;
								russianLexeme.setLevel2(1);
								russianLexeme.setLevel3(1);
								Long russianLexemeId = createLexeme(russianLexeme, getDataset());
								if (russianLexemeId != null) {
									saveRegisters(russianLexemeId, russianRegisters, reportingId);
								}
								List<String> additionalDomains = extractValuesAsStrings(transalationGroupNode, domainsExp);
								processDomains(null, meaningId, additionalDomains);
							}
						}
					}
				}
			}
		}
	}

	private WordData findOrCreateWord(Context context, String wordValue, String wordDisplayForm, String wordLanguage) throws Exception {
		Optional<WordData> word = context.importedWords.stream().filter(w -> wordValue.equals(w.value)).findFirst();
		if (word.isPresent()) {
			return word.get();
		} else {
			WordData newWord = createDefaultWordFrom(wordValue, wordDisplayForm, wordLanguage, null, null);
			context.importedWords.add(newWord);
			return newWord;
		}
	}

	private String cleanRussianUsage(String usageValue) {
		return replaceChars(usageValue, "\"", "");
	}

	private void cacheMeaningRelatedData(
			Context context, Long meaningId, List<String> definitions, WordData keyword, int level1,
			List<LexemeToWordData> latinTerms
	) {
		latinTerms.forEach(data -> data.meaningId = meaningId);
		context.latinTermins.addAll(latinTerms);

		context.meanings.stream()
				.filter(m -> Objects.equals(m.meaningId, meaningId))
				.forEach(m -> {m.meaningDefinitions.clear(); m.meaningDefinitions.addAll(definitions);});
		List<WordData> words = new ArrayList<>();
		words.add(keyword);
		words.forEach(word -> {
			context.meanings.addAll(convertToMeaningData(latinTerms, word, level1, definitions));
		});
	}

	private void savePosAndDeriv(WordData newWordData, List<PosData> meaningPosCodes, List<PosData> lexemePosCodes, Long lexemeId, String reportingId) {

		Set<PosData> combinedPosCodes = new HashSet<>();
		try {
			combinedPosCodes.addAll(lexemePosCodes);
			if (combinedPosCodes.isEmpty()) {
				combinedPosCodes.addAll(meaningPosCodes);
			}
			if (combinedPosCodes.isEmpty()) {
				combinedPosCodes.addAll(newWordData.posCodes);
			}
			if (combinedPosCodes.size() > 1) {
				String posCodesStr = combinedPosCodes.stream().map(p -> p.code).collect(Collectors.joining(","));
				//					logger.debug("Found more than one POS code <s:mg/s:sl> : {} : {}", reportingId, posCodesStr);
				writeToLogFile(reportingId, "Leiti rohkem kui üks sõnaliik <x:sl>", posCodesStr);
			}
			for (PosData posCode : combinedPosCodes) {
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
					lexemeId, newWordData.value, newWordData.id, combinedPosCodes.stream().map(p -> p.code).collect(Collectors.joining(",")));
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

	private List<Usage> extractUsages(Element node) {

		final String usageBlockExp = "x:np";
		final String usageGroupExp = "x:ng";
		final String usageExp = "x:n";
		final String usageDefinitionExp = "x:qnp/x:nd";

		List<Usage> usages = new ArrayList<>();
		List<Element> usageBlockNodes = node.selectNodes(usageBlockExp);
		for (Element usageBlockNode : usageBlockNodes) {
			if (isRestricted(usageBlockNode)) continue;
			List<Element> usageGroupNodes = usageBlockNode.selectNodes(usageGroupExp);
			for (Element usageGroupNode : usageGroupNodes) {
				List<String> usageValues = extractValuesAsStrings(usageGroupNode, usageExp);
				for (String usageValue : usageValues) {
					if (isUsage(usageValue)) {
						Usage usage = new Usage();
						usage.setValue(usageValue);
						usage.setDefinitions(extractValuesAsStrings(usageGroupNode, usageDefinitionExp));
						usage.setUsageTranslations(extractUsageTranslations(usageGroupNode));
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

	private List<UsageTranslation> extractUsageTranslations(Element node) {

		final String usageTranslationExp = "x:qnp/x:qng/x:qn";

		List<UsageTranslation> translations = new ArrayList<>();
		List<Element> usageTranslationNodes = node.selectNodes(usageTranslationExp);
		for (Element usageTranslationNode : usageTranslationNodes) {
			UsageTranslation translation = new UsageTranslation();
			translation.setValue(cleanRussianUsage(usageTranslationNode.getTextTrim()));
			translation.setLang(russianLang);
			translations.add(translation);
		}

		return translations;
	}

	private List<LexemeToWordData> extractLatinTerms(Element node, String latinTermExp, String reportingId) throws Exception {
		return extractLexemeMetadata(node, latinTermExp, null, reportingId);
	}

	private List<String> extractGovernments(Element node) {
		final String wordGovernmentExp = "x:grk/x:r";
		return extractValuesAsStrings(node, wordGovernmentExp);
	}

	private List<LexemeToWordData> extractRussianWords(Element node, List<String> additionalDomains, String reportingId) {

		final String wordGroupExp = "x:xp/x:xg";
		final String wordExp = "x:x";
		final String registerExp = "x:s";
		final String governmentExp = "x:vrek";
		final String domainExp = "x:v";

		List<LexemeToWordData> dataList = new ArrayList<>();
		List<Element> wordGroupNodes = node.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			String word = extractAsString(wordGroupNode, wordExp);
			LexemeToWordData lexemeData = new LexemeToWordData();
			lexemeData.word = cleanUp(word);

			if (StringUtils.isBlank(lexemeData.word)) continue;

			lexemeData.displayForm = word;
			lexemeData.reportingId = reportingId;
			lexemeData.register = extractAsString(wordGroupNode, registerExp);
			lexemeData.government = extractAsString(wordGroupNode, governmentExp);
			String domainCode = extractAsString(wordGroupNode, domainExp);
			if (domainCode != null) {
				additionalDomains.add(domainCode);
			}
			dataList.add(lexemeData);
		}
		return dataList;
	}

	private String extractAsString(Element node, String xpathExp) {
		Element wordNode = (Element) node.selectSingleNode(xpathExp);
		return wordNode == null ? null : wordNode.getTextTrim();
	}

	private boolean isNotWordInSs1(String word) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("word", word);
		paramMap.put("dataset", "ss1");
		List<Map<String, Object>> words = basicDbService.queryList(sqlSelectWordByDataset, paramMap);
		return CollectionUtils.isEmpty(words);
	}

}
