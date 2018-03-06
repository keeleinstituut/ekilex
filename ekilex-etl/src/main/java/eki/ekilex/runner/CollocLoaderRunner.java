package eki.ekilex.runner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class CollocLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(CollocLoaderRunner.class);

	private static final String SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_POS_PATH = "sql/select_lexeme_meaning_by_word_and_pos.sql";

	private static final String SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_NO_POS_PATH = "sql/select_lexeme_meaning_by_word_and_no_pos.sql";

	private static final String REPORT_ILLEGAL_DATA = "illegal_data";

	private static final String REPORT_MISSING_DATA = "missing_data";

	private static final String REPORT_UNKNOWN_CLASSIF = "unknown_classifier";

	private static final String REPORT_AMBIGUOUS_HOMONYM_MATCH = "ambiguous_homonym_match";

	private static final String REPORT_AMBIGUOUS_WORD_MATCH = "ambiguous_word_match";

	private static final String REPORT_AMBIGUOUS_LEXEME_MATCH = "ambiguous_lexeme_match";

	private static final String REPORT_UNKNOWN_WORD = "unknown_word";

	private static final String REPORT_COLLOC_PAIR_UNMATCH = "colloc_pair_unmatch";

	private final String guidExp = "x:G";
	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordPosExp = "x:sl";
	private final String displayMorphExp = "x:vk";
	private final String articleBodyExp = "x:S";
	private final String meaningBlockExp = "x:tp[not(@x:as='ab')]";
	private final String meaningDefinitionGroupExp = "x:tg/x:dg";
	private final String lexemeRegisterExp = "x:s";
	private final String meaningDomainExp = "x:v";
	private final String meaningDefinitionExp = "x:d";
	private final String lexemeGrammarExp = "x:grg/x:gki";
	private final String collocPosGroupExp = "x:colp/x:cmg";
	private final String collocPosAttr = "csl";
	private final String collocRelGroupExp = "x:relg";
	private final String collocGroupExp = "x:colg";
	private final String collocWordExp = "x:col";
	private final String prevWordExp = "x:mse";
	private final String nextWordExp = "x:msj";
	private final String collocUsageExp = "x:cng/x:cn[not(@x:as='ab')]";
	private final String collocRelGroupNameExp = "x:reln";
	private final String collocRelGroupFreqExp = "x:rfr";
	private final String collocRelGroupScoreExp = "x:rsc";
	private final String collocFreqExp = "x:cfr";
	private final String collocScoreExp = "x:csc";

	private final String wordHomonymNrAttr = "i";
	private final String lexemeLevelAttr = "tnr";
	private final String collocConjuctAttr = "jv";
	private final String lemmaDataAttr = "lemposvk";

	private final char lemmaDataDelim = '|';
	private final char lemmaDataCellDelim = ':';
	private final char compundWordCompDelim = '+';

	private final String defaultWordMorphCode = "??";

	private final String domainOriginBolan = "bolan";

	private ReportComposer reportComposer;

	private String sqlSelectLexemeMeaningByWordAndPos;

	private String sqlSelectLexemeMeaningByWordAndNoPos;

	private Map<String, String> posConversionMap;

	private Map<String, String> registerConversionMap;

	private Map<String, String> morphConversionMap;

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_POS_PATH);
		sqlSelectLexemeMeaningByWordAndPos = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_NO_POS_PATH);
		sqlSelectLexemeMeaningByWordAndNoPos = getContent(resourceFileInputStream);

		posConversionMap = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
		registerConversionMap = loadClassifierMappingsFor(EKI_CLASSIFIER_STYYP, ClassifierName.REGISTER.name());

		morphConversionMap = new HashMap<>();
		morphConversionMap.put("SgN", "SgN");
		morphConversionMap.put("Sup", "Sup");
		morphConversionMap.put("#", "ID");
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String dataset, boolean doReports) throws Exception {

		logger.debug("Starting loading collocates...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("kol loader report",
					REPORT_ILLEGAL_DATA, REPORT_MISSING_DATA, REPORT_UNKNOWN_CLASSIF, REPORT_AMBIGUOUS_HOMONYM_MATCH,
					REPORT_AMBIGUOUS_WORD_MATCH, REPORT_AMBIGUOUS_LEXEME_MATCH, REPORT_UNKNOWN_WORD, REPORT_COLLOC_PAIR_UNMATCH);
		}

		dataLang = unifyLang(dataLang);
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode, meaningDefinitionGroupNode, collocRelGroupNameNode, collocRelGroupFreqNode, collocRelGroupScoreNode;
		List<Element> meaningBlockNodes, grammarNodes, collocPosGroupNodes, collocRelGroupNodes, collocGroupNodes, collocUsageNodes;
		String word, collocPosCode, collocUsage, collocRelGroupName;
		List<Long> collocationIds;
		Attribute wordHomonymNumAttr;
		Integer wordHomonymNum;
		Word wordObj;

		Count ignoredArticleCount = new Count();
		Count successfulCollocationMatchCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());

		Map<String, Map<Integer, Word>> wordMap = extractAndSaveWordsLexemesMeanings(articleNodes, dataLang, dataset, ignoredArticleCount);

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);
			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			wordHomonymNumAttr = wordNode.attribute(wordHomonymNrAttr);
			wordHomonymNum = 1;
			if (wordHomonymNumAttr != null) {
				wordHomonymNum = Integer.valueOf(wordHomonymNumAttr.getValue());
			}
			word = wordNode.getTextTrim();
			wordObj = wordMap.get(word).get(wordHomonymNum);
			Long wordId = wordObj.getId();
			wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
			if (wordPosNode == null) {
				//TODO there should be POS!
				continue;
			}
			String wordPosCode = wordPosNode.getTextTrim();
			wordPosCode = posConversionMap.get(wordPosCode);

			List<LexemeMeaning> lexemeMeanings = getLexemeMeanings(wordId, wordPosCode);

			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);

			for (int meaningBlockIndex = 0; meaningBlockIndex < meaningBlockNodes.size(); meaningBlockIndex++) {

				Element meaningBlockNode = meaningBlockNodes.get(meaningBlockIndex);
				LexemeMeaning lexemeMeaning = lexemeMeanings.get(meaningBlockIndex);
				Long lexemeId = lexemeMeaning.getLexemeId();
				Long meaningId = lexemeMeaning.getMeaningId();

				meaningDefinitionGroupNode = (Element) meaningBlockNode.selectSingleNode(meaningDefinitionGroupExp);
				if (meaningDefinitionGroupNode != null) {
					extractAndSaveLexemeRegisters(word, lexemeId, meaningDefinitionGroupNode, doReports);
					extractAndSaveMeaningDomains(word, meaningId, meaningDefinitionGroupNode, doReports);
					extractAndSaveMeaningDefinitions(meaningId, meaningDefinitionGroupNode, dataLang, dataset);
				} else {
					//log??
				}

				extractAndSaveGrammar(lexemeId, meaningBlockNode, dataLang);

				collocPosGroupNodes = meaningBlockNode.selectNodes(collocPosGroupExp);//x:colp/x:cmg

				for (Element colPosGroupNode : collocPosGroupNodes) {

					collocPosCode = colPosGroupNode.attributeValue(collocPosAttr);
					Long collocPosGroupId = createCollocPosGroup(lexemeId, collocPosCode);

					collocRelGroupNodes = colPosGroupNode.selectNodes(collocRelGroupExp);//x:relg
					int collocRelGroupNum = 0;

					for (Element collocRelGroupNode : collocRelGroupNodes) {

						collocRelGroupNum++;
						collocRelGroupNameNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupNameExp);
						collocRelGroupName = collocRelGroupNameNode.getTextTrim();

						Float collocRelGroupFreq = null;
						collocRelGroupFreqNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupFreqExp);
						if (collocRelGroupFreqNode == null) {
							appendToReport(doReports, REPORT_MISSING_DATA, word, "x:relg", "[" + collocRelGroupNum + "]", "puudub sagedus");
						} else {
							try {
								collocRelGroupFreq = Float.parseFloat(collocRelGroupFreqNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, word, "x:relg", "[" + collocRelGroupNum + "]", "sagedusel sobimatu formaat");
							}
						}

						Float collocRelGroupScore = null;
						collocRelGroupScoreNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupScoreExp);
						if (collocRelGroupScoreNode == null) {
							appendToReport(doReports, REPORT_MISSING_DATA, word, "x:relg", "[" + collocRelGroupNum + "]", "puudub skoor");
						} else {
							try {
								collocRelGroupScore = Float.parseFloat(collocRelGroupScoreNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, word, "x:relg", "[" + collocRelGroupNum + "]", "skooril sobimatu formaat");
							}
						}

						Long collocRelGroupId = createCollocRelGroup(collocPosGroupId, collocRelGroupName, collocRelGroupFreq, collocRelGroupScore);

						collocGroupNodes = collocRelGroupNode.selectNodes(collocGroupExp);//x:colg
						int collocGroupNum = 0;

						for (Element collocGroupNode : collocGroupNodes) {

							collocGroupNum++;
							collocationIds = extractAndSaveCollocations(
									collocGroupNode, collocGroupNum, collocRelGroupId, word, wordPosCode, wordMap,
									dataset, dataLang, successfulCollocationMatchCount, doReports);

							if (CollectionUtils.isNotEmpty(collocationIds)) {

								collocUsageNodes = collocGroupNode.selectNodes(collocUsageExp);

								for (Element collocUsageNode : collocUsageNodes) {
									collocUsage = collocUsageNode.getTextTrim();
									createCollocUsage(collocationIds, collocUsage);
								}
							}
						}
					}
				}
			}

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		if (reportComposer != null) {
			reportComposer.end();
		}

		logger.debug("Found {} ignored articles", ignoredArticleCount.getValue());
		logger.debug("Found {} successful collocation matches", successfulCollocationMatchCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private Map<String, Map<Integer, Word>> extractAndSaveWordsLexemesMeanings(
			List<Element> articleNodes,
			String dataLang, String dataset,
			Count ignoredArticleCount) throws Exception {

		Map<String, Map<Integer, Word>> wordMap = new HashMap<>();

		Element contentNode, guidNode, headerNode, wordGroupNode, wordNode, wordPosNode, wordDisplayMorphNode;
		List<Element> meaningBlockNodes;
		Attribute wordHomonymNumAttr;
		String guid, word, wordDisplayMorph;
		Integer wordHomonymNum;
		Word wordObj;
		Long wordId;
		Map<Integer, Word> homonymWordMap;

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				ignoredArticleCount.increment();
				continue;
			}

			// guid
			guidNode = (Element) articleNode.selectSingleNode(guidExp);
			guid = guidNode.getTextTrim();
			guid = StringUtils.lowerCase(guid);

			// word
			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);
			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			wordHomonymNumAttr = wordNode.attribute(wordHomonymNrAttr);
			wordHomonymNum = 1;
			if (wordHomonymNumAttr != null) {
				wordHomonymNum = Integer.valueOf(wordHomonymNumAttr.getValue());
			}
			word = wordNode.getTextTrim();
			wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(displayMorphExp);
			if (wordDisplayMorphNode == null) {
				wordDisplayMorph = null;
			} else {
				wordDisplayMorph = wordDisplayMorphNode.getTextTrim();
			}

			// pos
			wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
			if (wordPosNode == null) {
				//TODO there should be POS!
				logger.warn("Missing POS @ \"{}\". Skipping entire article", word);
				continue;
			}
			String wordPosCode = wordPosNode.getTextTrim();
			wordPosCode = posConversionMap.get(wordPosCode);

			// save word
			wordObj = saveWord(word, dataLang, defaultWordMorphCode, guid, wordDisplayMorph, dataset);
			wordId = wordObj.getId();
			homonymWordMap = wordMap.get(word);
			if (homonymWordMap == null) {
				homonymWordMap = new HashMap<>();
				wordMap.put(word, homonymWordMap);
			}
			homonymWordMap.put(wordHomonymNum, wordObj);

			// save lexemes + meanings
			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);
			
			for (Element meaningBlockNode : meaningBlockNodes) {

				String level1Str = meaningBlockNode.attributeValue(lexemeLevelAttr);
				Integer level1 = Integer.valueOf(level1Str);
				createLexemeMeaning(wordId, level1, wordPosCode, dataset);
			}
		}
		return wordMap;
	}

	private void extractAndSaveLexemeRegisters(String newWord, Long lexemeId, Element meaningDefinitionGroupNode, boolean doReports) throws Exception {
		List<Element> lexemeRegisterNodes = meaningDefinitionGroupNode.selectNodes(lexemeRegisterExp);
		for (Element lexemeRegisterNode : lexemeRegisterNodes) {
			String lexemeRegister = lexemeRegisterNode.getTextTrim();
			if (registerConversionMap.containsKey(lexemeRegister)) {
				createLexemeRegister(lexemeId, lexemeRegister);
			} else {
				logger.warn("Unknown register \"{}\"", lexemeRegister);
				appendToReport(doReports, REPORT_UNKNOWN_CLASSIF, newWord, lexemeRegister, "tundmatu register");
			}
		}
	}

	private void extractAndSaveMeaningDomains(String newWord, Long meaningId, Element meaningDefinitionGroupNode, boolean doReports) throws Exception {
		List<Element> meaningDomainNodes = meaningDefinitionGroupNode.selectNodes(meaningDomainExp);
		List<String> domainCodes = new ArrayList<>();
		for (Element meaningDomainNode : meaningDomainNodes) {
			String domainCode = meaningDomainNode.getTextTrim();
			if (domainCodes.contains(domainCode)) {
				logger.warn("Domain reference duplicate: \"{}\"", domainCode);
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "x:relg", domainCode, "korduv valdkond");
				continue;
			}
			domainCodes.add(domainCode);
			createMeaningDomain(meaningId, domainCode, domainOriginBolan);
		}
	}

	private void extractAndSaveMeaningDefinitions(Long meaningId, Element meaningDefinitionGroupNode, String lang, String dataset) throws Exception {
		List<Element> meaningDefinitionNodes = meaningDefinitionGroupNode.selectNodes(meaningDefinitionExp);
		for (Element meaningDefinitionNode : meaningDefinitionNodes) {
			String definition = meaningDefinitionNode.getTextTrim();
			createDefinition(meaningId, definition, lang, dataset);
		}
	}

	private void extractAndSaveGrammar(Long lexemeId, Element meaningBlockNode, String dataLang) throws Exception {
		List<Element> lexemeGrammarNodes = meaningBlockNode.selectNodes(lexemeGrammarExp);
		for (Element lexemeGrammarNode : lexemeGrammarNodes) {
			String grammar = lexemeGrammarNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammar, dataLang);
		}
	}

	private List<Long> extractAndSaveCollocations(
			Element collocGroupNode,
			int collocGroupNum,
			Long collocRelGroupId,
			String newWord,
			String newWordPosCode,
			Map<String, Map<Integer, Word>> wordMap,
			String dataset,
			String dataLang,
			Count successfulCollocationMatchCount,
			boolean doReports) throws Exception {

		List<Element> collocWordNodes = collocGroupNode.selectNodes(collocWordExp);
		List<Element> prevWordNodes = collocGroupNode.selectNodes(prevWordExp);
		List<Element> nextWordNodes = collocGroupNode.selectNodes(nextWordExp);

		List<Long> collocationIds = new ArrayList<>();

		if (CollectionUtils.isEmpty(collocWordNodes)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "puudub kollokatsioon");
			return collocationIds;
		}
		if (CollectionUtils.isEmpty(prevWordNodes) && CollectionUtils.isEmpty(nextWordNodes)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "puuduvad eel- ja järelsõna");
			return collocationIds;
		}
		if (CollectionUtils.isNotEmpty(prevWordNodes) && CollectionUtils.isNotEmpty(nextWordNodes)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "esinevad korraga eel- ja järelsõna");
			return collocationIds;
		}

		List<CollocElement> collocWords = extractCollocWords(newWord, collocWordNodes, wordMap, dataset, dataLang, doReports);
		List<CollocElement> prevWords = extractCollocPairWords(newWord, newWordPosCode, prevWordNodes, doReports);
		List<CollocElement> nextWords = extractCollocPairWords(newWord, newWordPosCode, nextWordNodes, doReports);

		Float frequency = null;
		Element collocFreqNode = (Element) collocGroupNode.selectSingleNode(collocFreqExp);
		if (collocFreqNode == null) {
			appendToReport(doReports, REPORT_MISSING_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "puudub sagedus");
		} else {
			try {
				frequency = Float.parseFloat(collocFreqNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "sagedusel sobimatu formaat");
			}
		}

		Float score = null;
		Element collocScoreNode = (Element) collocGroupNode.selectSingleNode(collocScoreExp);
		if (collocScoreNode == null) {
			appendToReport(doReports, REPORT_MISSING_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "puudub skoor");
		} else {
			try {
				score = Float.parseFloat(collocScoreNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "x:colg", "[" + collocGroupNum + "]", "skooril sobimatu formaat");
			}
		}

		for (CollocElement collocWordElement : collocWords) {
			String collocWord = collocWordElement.getWord();
			String collocForm = collocWordElement.getForm();
			String collocMorphCode = collocWordElement.getMorphCode();
			String collocPosCode = collocWordElement.getPosCode();
			Map<Integer, Word> homonymWordMap = wordMap.get(collocWord);
			Word collocWordObj;
			Integer wordHomonymNum = 1;
			if (homonymWordMap == null) {
				collocWordObj = saveWord(collocWord, dataLang, collocMorphCode, null, null, dataset);
				homonymWordMap = new HashMap<>();
				homonymWordMap.put(wordHomonymNum, collocWordObj);
				wordMap.put(collocWord, homonymWordMap);
			} else {
				collocWordObj = homonymWordMap.get(wordHomonymNum);
			}
			Long collocWordId = collocWordObj.getId();
			List<LexemeMeaning> collocLexemeMeanings = getLexemeMeanings(collocWordId, collocPosCode);
			LexemeMeaning collocLexemeMeaning;
			if (CollectionUtils.isEmpty(collocLexemeMeanings)) {
				collocLexemeMeaning = createLexemeMeaning(collocWordId, null, collocPosCode, dataset);
			} else if (collocLexemeMeanings.size() == 1) {
				collocLexemeMeaning = collocLexemeMeanings.get(0);
			} else {
				appendToReport(doReports, REPORT_AMBIGUOUS_LEXEME_MATCH, newWord, collocWord, collocMorphCode, "kollokaadile vastab mitu ilmikut/tähendust");
				continue;
			}
			Long collocLexemeId = collocLexemeMeaning.getLexemeId();
			if (CollectionUtils.isNotEmpty(prevWords)) {
				for (CollocElement prevWordElement : prevWords) {
					String prevForm = prevWordElement.getForm();
					String conjunct = prevWordElement.getConjunct();
					String collocation;
					if (StringUtils.isBlank(conjunct)) {
						collocation = prevForm + ' ' + collocForm;
					} else {
						collocation = prevForm + ' ' + conjunct + ' ' + collocForm;
					}
					Long collocId = createCollocation(collocRelGroupId, collocLexemeId, collocation, frequency, score);
					collocationIds.add(collocId);
					successfulCollocationMatchCount.increment();
				}
			} else if (CollectionUtils.isNotEmpty(nextWords)) {
				for (CollocElement nextWordElement : nextWords) {
					String nextForm = nextWordElement.getForm();
					String conjunct = nextWordElement.getConjunct();
					String collocation;
					if (StringUtils.isBlank(conjunct)) {
						collocation = collocForm + ' ' + nextForm;
					} else {
						collocation = collocForm + ' ' + conjunct + ' ' + nextForm;
					}
					Long collocId = createCollocation(collocRelGroupId, collocLexemeId, collocation, frequency, score);
					collocationIds.add(collocId);
					successfulCollocationMatchCount.increment();
				}
			}
		}
		return collocationIds;
	}

	private List<CollocElement> extractCollocWords(
			String newWord, List<Element> collocWordNodes,
			Map<String, Map<Integer, Word>> wordMap,
			String dataset, String dataLang, boolean doReports) throws Exception {

		List<CollocElement> collocWords = new ArrayList<>();
		for (Element wordNode : collocWordNodes) {

			String form = wordNode.getTextTrim();
			String word = null;
			String posCode = null;
			String morphCode = defaultWordMorphCode;
			String conjunct = wordNode.attributeValue(collocConjuctAttr);
			String lemmaData = wordNode.attributeValue(lemmaDataAttr);

			if (StringUtils.isBlank(lemmaData)) {
				Map<Integer, Word> homonymWordMap = wordMap.get(form);
				if (CollectionUtils.size(homonymWordMap) == 1) {
					word = form;
					CollocElement collocElement = new CollocElement(word, form, morphCode, posCode, conjunct);
					collocWords.add(collocElement);
				} else {
					//TODO check at ss
					appendToReport(doReports, REPORT_UNKNOWN_WORD, newWord, form, lemmaData, "tundmatu kollokaat");
					continue;
				}
			} else {
				String[] lemmaDataCandidatesArr = StringUtils.split(lemmaData, lemmaDataDelim);
				List<String> lemmaWordCandidates = Arrays.stream(lemmaDataCandidatesArr)
							.map(lemmaDataCandidate -> StringUtils.remove(StringUtils.split(lemmaDataCandidate, lemmaDataCellDelim)[0], compundWordCompDelim))
							.distinct().collect(Collectors.toList());
				if (lemmaWordCandidates.size() > 1) {
					appendToReport(doReports, REPORT_AMBIGUOUS_WORD_MATCH, newWord, form, lemmaData, "lemposvk sisaldab erinevaid sõnu");
					continue;
				}
				String lemmaDataCandidate = lemmaDataCandidatesArr[0];
				String[] lemmaDataParts = StringUtils.split(lemmaDataCandidate, lemmaDataCellDelim);
				word = lemmaDataParts[0];
				word = StringUtils.remove(word, compundWordCompDelim);//deal with compound words later
				posCode = lemmaDataParts[1];
				posCode = posConversionMap.get(posCode);
				morphCode = lemmaDataParts[2];
				morphCode = morphConversionMap.get(morphCode);
				if (StringUtils.isBlank(morphCode)) {
					morphCode = defaultWordMorphCode;
				}
				Map<Integer, Word> homonymWordMap = wordMap.get(word);
				if (CollectionUtils.size(homonymWordMap) <= 1) {
					CollocElement collocElement = new CollocElement(word, form, morphCode, posCode, conjunct);
					collocWords.add(collocElement);
				} else {
					appendToReport(doReports, REPORT_AMBIGUOUS_HOMONYM_MATCH, newWord, form, lemmaData, "kollokaadile vastab mitu homonüümi");
					continue;
				}
			}
		}
		return collocWords;
	}

	private List<CollocElement> extractCollocPairWords(String newWord, String posCode, List<Element> collocPairWordNodes, boolean doReports) throws Exception {

		List<CollocElement> collocElements = new ArrayList<>();
		for (Element wordNode : collocPairWordNodes) {
			String form = wordNode.getTextTrim();
			String word = newWord;
			String morphCode = defaultWordMorphCode;
			String conjunct = wordNode.attributeValue(collocConjuctAttr);
			String lemmaData = wordNode.attributeValue(lemmaDataAttr);
			if (StringUtils.isNotBlank(lemmaData)) {
				String[] lemmaDataCandidatesArr = StringUtils.split(lemmaData, lemmaDataDelim);
				List<String> lemmaWordCandidates = Arrays.stream(lemmaDataCandidatesArr)
						.map(lemmaDataCandidate -> StringUtils.remove(StringUtils.split(lemmaDataCandidate, lemmaDataCellDelim)[0], compundWordCompDelim))
						.distinct().collect(Collectors.toList());
				if (!lemmaWordCandidates.contains(word)) {
					appendToReport(doReports, REPORT_COLLOC_PAIR_UNMATCH, newWord, form, lemmaData, "lemposvk ei klapi artikli märksõnaga");
					continue;
				}
			}
			CollocElement collocElement = new CollocElement(word, form, morphCode, posCode, conjunct);
			collocElements.add(collocElement);
		}
		return collocElements;
	}

	private Word saveWord(
			String word, String dataLang, String morphCode, String guid, String wordDisplayMorph, String dataset) throws Exception {

		int homonymNr = getWordMaxHomonymNr(word, dataLang);
		homonymNr++;
		Word wordObj = new Word(word, dataLang, homonymNr, morphCode, guid);
		wordObj.setDisplayMorph(wordDisplayMorph);
		Long wordId = saveWord(wordObj, null, dataset, null);
		wordObj.setId(wordId);
		return wordObj;
	}

	private LexemeMeaning createLexemeMeaning(Long wordId, Integer level1, String posCode, String dataset) throws Exception {

		Long meaningId = createMeaning();
		Lexeme lexemeObj = new Lexeme();
		lexemeObj.setWordId(wordId);
		lexemeObj.setMeaningId(meaningId);
		lexemeObj.setLevel1(level1);
		Long lexemeId = createLexeme(lexemeObj, dataset);
		if (StringUtils.isNotBlank(posCode)) {
			createLexemePos(lexemeId, posCode);
		}
		LexemeMeaning lexemeMeaning = new LexemeMeaning(lexemeId, meaningId);
		return lexemeMeaning;
	}

	private void createLexemePos(Long lexemeId, String posCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("pos_code", posCode);
		basicDbService.create(LEXEME_POS, tableRowParamMap);
	}

	private Long createCollocPosGroup(Long lexemeId, String name) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("name", name);
		Long collocPosGroupId = basicDbService.create(COLLOCATION_POS_GROUP, tableRowParamMap);
		return collocPosGroupId;
	}

	private Long createCollocRelGroup(Long collocPosGroupId, String name, Float frequency, Float score) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("collocation_pos_group_id", collocPosGroupId);
		tableRowParamMap.put("name", name);
		if (frequency != null) {
			tableRowParamMap.put("frequency", frequency);
		}
		if (score != null) {
			tableRowParamMap.put("score", score);
		}
		Long collocRelGroupId = basicDbService.create(COLLOCATION_REL_GROUP, tableRowParamMap);
		return collocRelGroupId;
	}

	private Long createCollocation(Long collocRelGroupId, Long lexemeId, String collocation, Float frequency, Float score) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("collocation_rel_group_id", collocRelGroupId);
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("value", collocation);
		if (frequency != null) {
			tableRowParamMap.put("frequency", frequency);
		}
		if (score != null) {
			tableRowParamMap.put("score", score);
		}
		Long collocationId = basicDbService.create(COLLOCATION, tableRowParamMap);
		return collocationId;
	}

	private void createCollocUsage(List<Long> collocationIds, String collocUsage) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("value", collocUsage);
		for (Long collocationId : collocationIds) {
			tableRowParamMap.put("collocation_id", collocationId);
			basicDbService.create(COLLOCATION_USAGE, tableRowParamMap);
		}
	}

	private List<LexemeMeaning> getLexemeMeanings(Long wordId, String posCode) throws Exception {

		String sql;
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordId", wordId);
		if (StringUtils.isBlank(posCode)) {
			sql = sqlSelectLexemeMeaningByWordAndNoPos;
		} else {
			sql = sqlSelectLexemeMeaningByWordAndPos;
			tableRowParamMap.put("posCode", posCode);
		}
		List<Map<String, Object>> resultRows = basicDbService.queryList(sql, tableRowParamMap);
		List<LexemeMeaning> lexemeMeanings = new ArrayList<>();
		for (Map<String, Object> resultRow : resultRows) {
			Long lexemeId = Long.valueOf(resultRow.get("lexeme_id").toString());
			Long meaningId = Long.valueOf(resultRow.get("meaning_id").toString());
			LexemeMeaning lexemeMeaning = new LexemeMeaning(lexemeId, meaningId);
			lexemeMeanings.add(lexemeMeaning);
		}
		return lexemeMeanings;
	}

	private void appendToReport(boolean doReports, String reportName, String ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	class CollocElement {

		private String word;

		private String form;

		private String morphCode;

		private String posCode;

		private String conjunct;

		public CollocElement(String word, String form, String morphCode, String posCode, String conjunct) {
			this.word = word;
			this.form = form;
			this.morphCode = morphCode;
			this.posCode = posCode;
			this.conjunct = conjunct;
		}

		public String getWord() {
			return word;
		}

		public String getForm() {
			return form;
		}

		public String getMorphCode() {
			return morphCode;
		}

		public String getPosCode() {
			return posCode;
		}

		public String getConjunct() {
			return conjunct;
		}
	}

	class LexemeMeaning {

		private Long lexemeId;

		private Long meaningId;

		public LexemeMeaning(Long lexemeId, Long meaningId) {
			this.lexemeId = lexemeId;
			this.meaningId = meaningId;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public void setLexemeId(Long lexemeId) {
			this.lexemeId = lexemeId;
		}

		public Long getMeaningId() {
			return meaningId;
		}

		public void setMeaningId(Long meaningId) {
			this.meaningId = meaningId;
		}
	}
}
