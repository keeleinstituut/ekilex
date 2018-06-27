package eki.ekilex.runner;

import javax.transaction.Transactional;

import eki.common.constant.FreeformType;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.replaceChars;

@Component
public class Ev2LoaderRunner extends SsBasedLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Ev2LoaderRunner.class);

	private final static String russianLang = "rus";

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
	String getDataset() {
		return "ev2";
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataXmlFilePath2, boolean doReports) throws Exception {

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
			processArticle(articleNode, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);
		processLatinTerms(context);

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	@Transactional
	void processArticle(Element articleNode, Context context) throws Exception {

		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";
		final String articleGuidExp = "x:G";

		String reportingId = extractReporingId(articleNode);
		String guid = extractGuid(articleNode, articleGuidExp);
		List<WordData> newWords = new ArrayList<>();

		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		processArticleHeader(reportingId, headerNode, newWords, context, guid);

		Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
		if (contentNode != null) {
			try {
				processArticleContent(reportingId, contentNode, newWords, context);
			} catch (Exception e) {
				logger.debug("KEYWORD : {}", reportingId);
				throw e;
			}
		}
		context.importedWords.addAll(newWords);
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context, String guid) throws Exception {
		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordGrammarPosCodesExp = "x:grk/x:sl";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
				wordData.id = createOrSelectWord(word, paradigms, getDataset(), context.wordDuplicateCount);
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
			List<Usage> usagesInRussian = extractRussianUsages(meaningNumberGroupNode);

			int lexemeLevel2 = 0;
			for (Element meaningGroupNode : meaningGroupNodes) {
				lexemeLevel2++;
				List<PosData> lexemePosCodes =  extractPosCodes(meaningGroupNode, meaningPosCodeExp);
				List<String> lexemeGrammars = extractGrammar(meaningGroupNode);
				List<String> registers = extractRegisters(meaningGroupNode);

				Long meaningId;
				List<String> definitionsToAdd = new ArrayList<>();
				List<String> definitionsToCache = new ArrayList<>();
				List<String> definitions = extractDefinitions(meaningGroupNode);
				List<LexemeToWordData> meaningLatinTerms = extractLatinTerms(meaningGroupNode, reportingId);
				List<String> additionalDomains = new ArrayList<>();
				List<LexemeToWordData> meaningRussianWords = extractRussianWords(meaningGroupNode, additionalDomains, usagesInRussian, reportingId);
				List<LexemeToWordData> connectedWords =
						Stream.of(
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
						createUsages(lexemeId, usages, dataLang);
						saveGovernments(newWordData, meaningGovernments, lexemeId);
						savePosAndDeriv(newWordData, meaningPosCodes, lexemePosCodes, lexemeId, reportingId);
						saveGrammars(newWordData, meaningGrammars, lexemeGrammars, lexemeId);
						saveRegisters(lexemeId, registers, reportingId);
					}
				}
				for (LexemeToWordData russianWord : meaningRussianWords) {
					Long wordId;
					Optional<WordData> word = context.importedWords.stream().filter(w -> russianWord.word.equals(w.value)).findFirst();
					if (word.isPresent()) {
						wordId = word.get().id;
					} else {
						WordData newWord = createDefaultWordFrom(russianWord.word, russianWord.displayForm, russianLang, null, null);
						context.importedWords.add(newWord);
						wordId = newWord.id;
					}
					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(wordId);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(lexemeLevel1);
					lexeme.setLevel2(lexemeLevel2);
					lexeme.setLevel3(lexemeLevel3);
					Long lexemeId = createLexeme(lexeme, getDataset());
					if (lexemeId != null) {
						createUsages(lexemeId, usagesInRussian, russianLang);
						if (StringUtils.isNotBlank(russianWord.government)) {
							createOrSelectLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, russianWord.government);
						}
						if (StringUtils.isNotBlank(russianWord.register)) {
							saveRegisters(lexemeId, asList(russianWord.register), russianWord.word);
						}
					}
				}
			}
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
				Usage usage = new Usage();
				usage.setValue(extractAsString(usageGroupNode, usageExp));
				usage.setDefinitions(extractValuesAsStrings(usageGroupNode, usageDefinitionExp));
				usages.add(usage);
			}
		}
		return usages;
	}

	private List<Usage> extractRussianUsages(Element node) {

		final String usageBlockExp = "x:np";
		final String usageExp = "x:ng/x:qnp/x:qng/x:qn";

		List<Usage> usages = new ArrayList<>();
		List<Element> usageBlockNodes = node.selectNodes(usageBlockExp);
		for (Element usageBlockNode : usageBlockNodes) {
			if (isRestricted(usageBlockNode)) continue;
			List<Element> usageNodes = usageBlockNode.selectNodes(usageExp);
			for (Element usageNode : usageNodes) {
				Usage usage = new Usage();
				usage.setValue(cleanRussianUsage(usageNode.getTextTrim()));
				usages.add(usage);
			}
		}
		return usages;
	}

	private List<String> extractDefinitions(Element node) {
		final String definitionValueExp = "x:dg/x:d";
		return extractValuesAsStrings(node, definitionValueExp);
	}

	private List<String> extractRegisters(Element node) {
		final String registerValueExp = "x:dg/x:s";
		return extractValuesAsStrings(node, registerValueExp);
	}

	private List<LexemeToWordData> extractLatinTerms(Element node, String reportingId) throws Exception {
		final String latinTermExp = "x:dg/x:ld";
		return extractLexemeMetadata(node, latinTermExp, null, reportingId);
	}

	private List<String> extractGovernments(Element node) {
		final String wordGovernmentExp = "x:grk/x:r";
		return extractValuesAsStrings(node, wordGovernmentExp);
	}

	private List<LexemeToWordData> extractRussianWords(Element node, List<String> additionalDomains, List<Usage> usagesInRussian, String reportingId) {

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
			lexemeData.usages = usagesInRussian;
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

}
