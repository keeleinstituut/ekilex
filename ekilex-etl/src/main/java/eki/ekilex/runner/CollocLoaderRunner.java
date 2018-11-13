package eki.ekilex.runner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.exception.DataLoadingException;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class CollocLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(CollocLoaderRunner.class);

	private static final String SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_POS_PATH = "sql/select_lexeme_meaning_by_word_and_pos.sql";

	private static final String SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_NO_POS_PATH = "sql/select_lexeme_meaning_by_word_and_no_pos.sql";

	private static final String REPORT_ILLEGAL_DATA = "illegal_data";
	private static final String REPORT_UNKNOWN_CLASSIF = "unknown_classifier";
	private static final String REPORT_REPEATING_COLLOC_MEMBER = "repeating_colloc_member";
	private static final String REPORT_UNKNOWN_COLLOC_MEMBER = "unknown_colloc_member";
	private static final String REPORT_ILLEGAL_LEMPOSVK_REF = "illegal_lemposvk_ref";
	private static final String REPORT_INCORRECT_LEMPOSVK_LEMMA = "incorrect_lemposvk_lemma";
	private static final String REPORT_DIFFERENT_COLLOC_DEFINITION = "different_colloc_definition";
	private static final String REPORT_FAILING_HOMONYM_GUESS = "failing_homonym_guess";

	private final String dataLang = "est";

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
	private final String collocUsageExp = "x:cng/x:cn[not(@x:as='ab')]";
	private final String collocRelGroupNameExp = "x:reln";
	private final String collocRelGroupFreqExp = "x:rfr";
	private final String collocRelGroupScoreExp = "x:rsc";
	private final String collocFreqExp = "x:cfr";
	private final String collocScoreExp = "x:csc";
	private final String collocDefinitionExp = "x:cd";

	private final String wordHomonymNrAttr = "i";
	private final String lexemeLevelAttr = "tnr";
	private final String collocConjunctAttr = "jv";
	private final String lemmaDataAttr = "lemposvk";

	private final char lemmaDataDelim = '|';
	private final char lemmaDataCellDelim = ':';
	private final char compundWordCompDelim = '+';

	private final String defaultWordMorphCode = "??";

	private final String domainOriginBolan = "bolan";

	private final String prevWordCollocMemberName = "mse";
	private final String nextWordCollocMemberName = "msj";
	private final String colWordCollocMemberName = "col";
	private final String[] primaryCollocMemberNames = new String[] {prevWordCollocMemberName, nextWordCollocMemberName, colWordCollocMemberName};
	private final String[] contextCollocMemberNames = new String[] {"cnte", "cce", "ccj", "cnt"};
	private final String[] collocMemberNames = new String[] {
			prevWordCollocMemberName, nextWordCollocMemberName, colWordCollocMemberName, "cnte", "cce", "ccj", "cnt"};

	private final Float inboundPrimaryCollocMemberWeight = 1F;
	private final Float outboundPrimaryCollocMemberWeight = 0.8F;
	private final Float outboundSecondaryCollocMemberWeight = 0.5F;

	private ReportComposer reportComposer;

	private String sqlSelectLexemeMeaningByWordAndPos;

	private String sqlSelectLexemeMeaningByWordAndNoPos;

	private Map<String, String> posConversionMap;

	private Map<String, String> registerConversionMap;

	private Map<String, String> posMorphConversionMap;

	@Override
	public String getDataset() {
		return "kol";
	}

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

		posMorphConversionMap = new HashMap<>();
		posMorphConversionMap.put("prop", "SgN");
		posMorphConversionMap.put("ord", "SgN");
		posMorphConversionMap.put("num", "SgN");
		posMorphConversionMap.put("adj", "SgN");
		posMorphConversionMap.put("pron", "SgN");
		posMorphConversionMap.put("s", "SgN");
		posMorphConversionMap.put("v", "Sup");
		posMorphConversionMap.put("adjg", "ID");
		posMorphConversionMap.put("postp", "ID");
		posMorphConversionMap.put("prep", "ID");
		posMorphConversionMap.put("x", "ID");
		posMorphConversionMap.put("adv", "ID");
		posMorphConversionMap.put("interj", "ID");
		posMorphConversionMap.put("adp", "ID");
		posMorphConversionMap.put("konj", "ID");
		posMorphConversionMap.put("#", "ID");
	}

	@Transactional
	public void execute(String dataXmlFilePath, Map<String, List<Guid>> ssGuidMap, boolean doReports) throws Exception {

		logger.debug("Starting loading collocates...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("kol loader report",
					REPORT_ILLEGAL_DATA, REPORT_UNKNOWN_CLASSIF, REPORT_REPEATING_COLLOC_MEMBER, REPORT_UNKNOWN_COLLOC_MEMBER,
					REPORT_ILLEGAL_LEMPOSVK_REF, REPORT_INCORRECT_LEMPOSVK_LEMMA, REPORT_DIFFERENT_COLLOC_DEFINITION, REPORT_FAILING_HOMONYM_GUESS);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode, meaningDefinitionGroupNode, collocRelGroupNameNode, collocRelGroupFreqNode, collocRelGroupScoreNode;
		List<Node> meaningBlockNodes, collocPosGroupNodes, collocRelGroupNodes, collocGroupNodes;
		String word, collocPosGroupCode, collocRelGroupName;
		Attribute wordHomonymNumAttr;
		Integer wordHomonymNum;
		Word wordObj;
		CollocGroup collocGroup;
		List<CollocMember> collocMembers;
		List<String> collocUsages;
		Map<Integer, LexemeMeaning> levelMeaningMap;

		Count ignoredArticleCount = new Count();
		Count ignoredCollocGroupCount = new Count();
		Count repeatingCollocMemberCount = new Count();
		Count collocMemberOverloadGroupCount = new Count();
		Count collocMemberGuessingHomonymMeaningCount = new Count();
		Count collocMemberGuessedHomonymMeaningCount = new Count();
		Count collocMemberSuggestedHomonymMeaningCount = new Count();
		Count collocMemberCreatedHomonymMeaningCount = new Count();
		Count collocMemberCreatedMoreThanOneHomonymCount = new Count();
		Count collocMemberUniqueDummyHomonymCount = new Count();
		Count collocMemberDummyHomonymCount = new Count();
		Count collocateCount = new Count();
		Count collocationCount = new Count();
		Count reusedCollocationCount = new Count();
		Count updatedCollocCount = new Count();
		Count updatedCollocMemberCount = new Count();
		Count reusedWordCount = new Count();
		Count ssWordCount = new Count();

		Map<String, Count> countersMap = new HashMap<>();
		countersMap.put("ignoredArticleCount", ignoredArticleCount);
		countersMap.put("ignoredCollocGroupCount", ignoredCollocGroupCount);
		countersMap.put("repeatingCollocMemberCount", repeatingCollocMemberCount);
		countersMap.put("collocMemberOverloadGroupCount", collocMemberOverloadGroupCount);
		countersMap.put("collocMemberGuessingHomonymMeaningCount", collocMemberGuessingHomonymMeaningCount);
		countersMap.put("collocMemberGuessedHomonymMeaningCount", collocMemberGuessedHomonymMeaningCount);
		countersMap.put("collocMemberSuggestedHomonymMeaningCount", collocMemberSuggestedHomonymMeaningCount);
		countersMap.put("collocMemberCreatedHomonymMeaningCount", collocMemberCreatedHomonymMeaningCount);
		countersMap.put("collocMemberCreatedMoreThanOneHomonymCount", collocMemberCreatedMoreThanOneHomonymCount);
		countersMap.put("collocMemberUniqueDummyHomonymCount", collocMemberUniqueDummyHomonymCount);
		countersMap.put("collocMemberDummyHomonymCount", collocMemberDummyHomonymCount);
		countersMap.put("collocateCount", collocateCount);
		countersMap.put("collocationCount", collocationCount);
		countersMap.put("reusedCollocationCount", reusedCollocationCount);
		countersMap.put("updatedCollocCount", updatedCollocCount);
		countersMap.put("updatedCollocMemberCount", updatedCollocMemberCount);
		countersMap.put("reusedWordCount", reusedWordCount);
		countersMap.put("ssWordCount", ssWordCount);

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());

		Map<String, Map<Integer, Word>> wordMap = new HashMap<>();
		Map<Long, Map<Integer, LexemeMeaning>> meaningMap = new HashMap<>();
		Map<String, UnknownWord> dummyWordMap = new HashMap<>();
		extractAndSaveWordsLexemesMeanings(articleNodes, wordMap, meaningMap, ssGuidMap, countersMap);

		Map<String, CollocRecord> collocMap = new HashMap<>();

		for (Node articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);
			wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
			if (wordPosNode == null) {
				ignoredArticleCount.increment();
				continue;
			}
			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			wordHomonymNumAttr = wordNode.attribute(wordHomonymNrAttr);
			wordHomonymNum = 1;
			if (wordHomonymNumAttr != null) {
				wordHomonymNum = Integer.valueOf(wordHomonymNumAttr.getValue());
			}
			word = wordNode.getTextTrim();
			word = cleanEkiEntityMarkup(word);
			wordObj = wordMap.get(word).get(wordHomonymNum);
			Long wordId = wordObj.getId();
			String wordPosCode = wordPosNode.getTextTrim();
			wordPosCode = posConversionMap.get(wordPosCode);

			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);

			for (Node meaningBlockNode : meaningBlockNodes) {

				Element meaningBlockElement = (Element) meaningBlockNode;
				String level1Str = meaningBlockElement.attributeValue(lexemeLevelAttr);
				Integer level1 = Integer.valueOf(level1Str);
				levelMeaningMap = meaningMap.get(wordId);
				LexemeMeaning lexemeMeaning = levelMeaningMap.get(level1);
				if (lexemeMeaning == null) {
					logger.warn("No lexeme/meaning match headword \"{}\" homonym nr \"{}\" meaning nr \"{}\". Probable homonym mapping mismatch", word, wordHomonymNum, level1);
					appendToReport(doReports, REPORT_ILLEGAL_DATA, word, "?", "?", "x:tp", "x:tnr=" + level1Str, "sellel homonüümil puudub selline tähendus");
					continue;
				}
				Long lexemeId = lexemeMeaning.getLexemeId();
				Long meaningId = lexemeMeaning.getMeaningId();

				meaningDefinitionGroupNode = (Element) meaningBlockNode.selectSingleNode(meaningDefinitionGroupExp);
				if (meaningDefinitionGroupNode != null) {
					extractAndSaveLexemeRegisters(word, lexemeId, meaningDefinitionGroupNode, doReports);
					extractAndSaveMeaningDomains(word, meaningId, meaningDefinitionGroupNode, doReports);
					extractAndSaveMeaningDefinitions(meaningId, meaningDefinitionGroupNode);
				} else {
					//log??
				}

				extractAndSaveGrammar(lexemeId, meaningBlockNode);

				collocPosGroupNodes = meaningBlockNode.selectNodes(collocPosGroupExp);//x:colp/x:cmg

				for (Node colPosGroupNode : collocPosGroupNodes) {

					collocPosGroupCode = ((Element)colPosGroupNode).attributeValue(collocPosAttr);
					Long collocPosGroupId = createCollocPosGroup(lexemeId, collocPosGroupCode);

					collocRelGroupNodes = colPosGroupNode.selectNodes(collocRelGroupExp);//x:relg
					int collocRelGroupNum = 0;

					for (Node collocRelGroupNode : collocRelGroupNodes) {

						collocRelGroupNum++;
						collocRelGroupNameNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupNameExp);
						collocRelGroupName = collocRelGroupNameNode.getTextTrim();

						Float collocRelGroupFreq = null;
						collocRelGroupFreqNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupFreqExp);
						if (collocRelGroupFreqNode != null) {
							try {
								collocRelGroupFreq = Float.parseFloat(collocRelGroupFreqNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupCode, collocRelGroupName, "x:relg", "[" + collocRelGroupNum + "]", "sagedusel sobimatu formaat");
							}
						}

						Float collocRelGroupScore = null;
						collocRelGroupScoreNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupScoreExp);
						if (collocRelGroupScoreNode != null) {
							try {
								collocRelGroupScore = Float.parseFloat(collocRelGroupScoreNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupCode, collocRelGroupName, "x:relg", "[" + collocRelGroupNum + "]", "skooril sobimatu formaat");
							}
						}

						Long collocRelGroupId = createCollocRelGroup(collocPosGroupId, collocRelGroupName, collocRelGroupFreq, collocRelGroupScore);

						collocGroupNodes = collocRelGroupNode.selectNodes(collocGroupExp);//x:colg
						int collocGroupOrder = 0;

						for (Node collocGroupNode : collocGroupNodes) {

							collocGroupOrder++;
							collocUsages = extractCollocUsages(collocGroupNode);
							collocMembers = extractCollocMembers(collocGroupNode, ignoredCollocGroupCount);
							collocGroup = new CollocGroup(word, wordPosCode, lexemeId, collocPosGroupCode, collocPosGroupId, collocRelGroupName, collocRelGroupId, collocGroupOrder);
							saveCollocations(
									collocGroupNode, collocGroup, collocUsages, collocMembers,
									wordMap, meaningMap, dummyWordMap, collocMap, countersMap, doReports);
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
		logger.debug("Found {} ignored collocation groups", ignoredCollocGroupCount.getValue());
		logger.debug("Found {} repeating collocation member count", repeatingCollocMemberCount.getValue());
		logger.debug("Found {} overloaded colloc members groups", collocMemberOverloadGroupCount.getValue());
		logger.debug("Found {} colloc member homonym/meaning guess attempts", collocMemberGuessingHomonymMeaningCount.getValue());
		logger.debug("Found {} successfully guessed colloc member homonym/meaning", collocMemberGuessedHomonymMeaningCount.getValue());
		logger.debug("Found {} suggested on guessing colloc member homonym/meaning", collocMemberSuggestedHomonymMeaningCount.getValue());
		logger.debug("Found {} created missing colloc member homonym/meaning", collocMemberCreatedHomonymMeaningCount.getValue());
		logger.debug("Found {} created missing more than one colloc member homonym", collocMemberCreatedMoreThanOneHomonymCount.getValue());
		logger.debug("Found {} dummy colloc members", collocMemberDummyHomonymCount.getValue());
		logger.debug("Found {} unique dummy homonyms", collocMemberUniqueDummyHomonymCount.getValue());
		logger.debug("Found {} collocates", collocateCount.getValue());
		logger.debug("Found {} collocations", collocationCount.getValue());
		logger.debug("Found {} reused collocations", reusedCollocationCount.getValue());
		logger.debug("Found {} updated collocations", updatedCollocCount.getValue());
		logger.debug("Found {} updated collocation members", updatedCollocMemberCount.getValue());
		logger.debug("Found {} reused words", reusedWordCount.getValue());
		logger.debug("Found {} ss words", ssWordCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private void extractAndSaveWordsLexemesMeanings(
			List<Node> articleNodes,
			Map<String, Map<Integer, Word>> wordMap,
			Map<Long, Map<Integer, LexemeMeaning>> meaningMap,
			Map<String, List<Guid>> ssGuidMap,
			Map<String, Count> countersMap) throws Exception {

		Count ignoredArticleCount = countersMap.get("ignoredArticleCount");
		Count reusedWordCount = countersMap.get("reusedWordCount");
		Count ssWordCount = countersMap.get("ssWordCount");

		Element contentNode, guidNode, headerNode, wordGroupNode, wordNode, wordPosNode, wordDisplayMorphNode;
		List<Node> meaningBlockNodes;
		Attribute wordHomonymNumAttr;
		String guid, word, wordDisplayMorph;
		Integer wordHomonymNum;
		Word wordObj;
		Long wordId;
		Map<Integer, Word> homonymWordMap;

		for (Node articleNode : articleNodes) {

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
			word = cleanEkiEntityMarkup(word);
			wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(displayMorphExp);
			if (wordDisplayMorphNode == null) {
				wordDisplayMorph = null;
			} else {
				wordDisplayMorph = wordDisplayMorphNode.getTextTrim();
			}

			// pos
			wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
			if (wordPosNode == null) {
				logger.warn("Missing POS @ \"{}\". Skipping entire article", word);
				continue;
			}
			String wordPosCode = wordPosNode.getTextTrim();
			wordPosCode = posConversionMap.get(wordPosCode);

			// save word
			wordObj = createOrSelectWord(word, defaultWordMorphCode, guid, wordDisplayMorph, ssGuidMap, ssWordCount, reusedWordCount);
			wordId = wordObj.getId();
			homonymWordMap = wordMap.get(word);
			if (homonymWordMap == null) {
				homonymWordMap = new HashMap<>();
				wordMap.put(word, homonymWordMap);
			}
			homonymWordMap.put(wordHomonymNum, wordObj);

			// save lexemes + meanings
			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);

			HashMap<Integer, LexemeMeaning> levelMeaningMap = new HashMap<>();
			meaningMap.put(wordId, levelMeaningMap);
			
			for (Node meaningBlockNode : meaningBlockNodes) {

				Element meaningBlockElement = (Element)meaningBlockNode;
				String level1Str = meaningBlockElement.attributeValue(lexemeLevelAttr);
				Integer level1 = Integer.valueOf(level1Str);
				LexemeMeaning lexemeMeaning = createLexemeMeaning(wordId, level1, wordPosCode);
				levelMeaningMap.put(level1, lexemeMeaning);
			}
		}
	}

	private void extractAndSaveLexemeRegisters(String newWord, Long lexemeId, Element meaningDefinitionGroupNode, boolean doReports) throws Exception {
		List<Node> lexemeRegisterNodes = meaningDefinitionGroupNode.selectNodes(lexemeRegisterExp);
		for (Node lexemeRegisterNode : lexemeRegisterNodes) {
			String lexemeRegister = ((Element)lexemeRegisterNode).getTextTrim();
			if (registerConversionMap.containsKey(lexemeRegister)) {
				lexemeRegister = registerConversionMap.get(lexemeRegister);
				createLexemeRegister(lexemeId, lexemeRegister);
			} else {
				logger.warn("Unknown register \"{}\"", lexemeRegister);
				appendToReport(doReports, REPORT_UNKNOWN_CLASSIF, newWord, lexemeRegister, "tundmatu register");
			}
		}
	}

	private void extractAndSaveMeaningDomains(String newWord, Long meaningId, Element meaningDefinitionGroupNode, boolean doReports) throws Exception {
		List<Node> meaningDomainNodes = meaningDefinitionGroupNode.selectNodes(meaningDomainExp);
		List<String> domainCodes = new ArrayList<>();
		for (Node meaningDomainNode : meaningDomainNodes) {
			String domainCode = ((Element)meaningDomainNode).getTextTrim();
			if (domainCodes.contains(domainCode)) {
				logger.warn("Domain reference duplicate: \"{}\"", domainCode);
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, "?", "?", "x:relg", domainCode, "korduv valdkond");
				continue;
			}
			domainCodes.add(domainCode);
			createMeaningDomain(meaningId, domainCode, domainOriginBolan);
		}
	}

	private void extractAndSaveMeaningDefinitions(Long meaningId, Element meaningDefinitionGroupNode) throws Exception {
		List<Node> meaningDefinitionNodes = meaningDefinitionGroupNode.selectNodes(meaningDefinitionExp);
		for (Node meaningDefinitionNode : meaningDefinitionNodes) {
			String definition = ((Element)meaningDefinitionNode).getTextTrim();
			definition = cleanEkiEntityMarkup(definition);
			createDefinition(meaningId, definition, dataLang, getDataset());
		}
	}

	private void extractAndSaveGrammar(Long lexemeId, Node meaningBlockNode) throws Exception {
		List<Node> lexemeGrammarNodes = meaningBlockNode.selectNodes(lexemeGrammarExp);
		for (Node lexemeGrammarNode : lexemeGrammarNodes) {
			String grammar = ((Element)lexemeGrammarNode).getTextTrim();
			grammar = cleanEkiEntityMarkup(grammar);
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammar, dataLang);
		}
	}

	private List<CollocMember> extractCollocMembers(Node collocGroupNode, Count ignoredCollocGroupCount) {

		final String[] skippedNodeNames = new String[] {"colloc", "cfr", "csc", "cng", "cd", "s", "v", "rek"};

		List<CollocMember> collocMembers = new ArrayList<>();
		Iterator<Node> collocGroupNodeIter = ((Element)collocGroupNode).nodeIterator();
		List<CollocMember> lemmaDataCollocMembers;

		while (collocGroupNodeIter.hasNext()) {
			Node collocMemberAbstractNode = collocGroupNodeIter.next();
			if (collocMemberAbstractNode instanceof DefaultElement) {
				DefaultElement collocMemberNode = (DefaultElement) collocMemberAbstractNode;
				String collocMemberName = collocMemberNode.getName();
				if (ArrayUtils.contains(collocMemberNames, collocMemberName)) {

					String form = collocMemberNode.getTextTrim();
					form = cleanEkiEntityMarkup(form);
					String conjunct = collocMemberNode.attributeValue(collocConjunctAttr);
					String lemmaDataStr = collocMemberNode.attributeValue(lemmaDataAttr);
					conjunct = StringUtils.replace(conjunct, "v", "või");

					if (StringUtils.isBlank(lemmaDataStr)) {
						//col, mse, msj
						if (ArrayUtils.contains(primaryCollocMemberNames, collocMemberName)) {
							ignoredCollocGroupCount.increment();
							return Collections.emptyList();
						}
						continue;
					} else {
						lemmaDataCollocMembers = composeCollocMembers(collocMemberName, form, conjunct, lemmaDataStr);
						for (CollocMember lemmaDataCollocMember : lemmaDataCollocMembers) {
							//col
							if (StringUtils.equals(colWordCollocMemberName, collocMemberName) && (lemmaDataCollocMember.getRefNum() == null)) {
								ignoredCollocGroupCount.increment();
								return Collections.emptyList();
							}							
						}
						collocMembers.addAll(lemmaDataCollocMembers);
					}

				} else if (ArrayUtils.contains(skippedNodeNames, collocMemberName)) {
					//do nothing
				} else {
					logger.debug("Unknown colloc group element \"{}\"", collocMemberName);
				}
			}
		}
		return collocMembers;
	}

	private List<CollocMember> composeCollocMembers(String collocMemberName, String form, String conjunct, String lemmaDataStr) {

		List<CollocMember> collocMembers = new ArrayList<>();
		List<String> alreadyRegisteredCollocMembers = new ArrayList<>();
		String[] lemmaDataCandidatesArr = StringUtils.split(lemmaDataStr, lemmaDataDelim);
		CollocMember collocMember;
		for (String lemmaDataCandidateStr : lemmaDataCandidatesArr) {
			String[] lemmaDataCandidateCells = StringUtils.split(lemmaDataCandidateStr, lemmaDataCellDelim);
			String word = lemmaDataCandidateCells[0];
			word = StringUtils.remove(word, compundWordCompDelim);//deal with compound words later
			if (alreadyRegisteredCollocMembers.contains(word)) {
				continue;
			}
			alreadyRegisteredCollocMembers.add(word);
			String posCode = lemmaDataCandidateCells[1];
			posCode = posConversionMap.get(posCode);
			String morphCode = posMorphConversionMap.get(posCode);
			if (StringUtils.isBlank(morphCode)) {
				morphCode = defaultWordMorphCode;
			}
			if (lemmaDataCandidateCells.length == 4) {
				String[] refNumCandidatesArr = StringUtils.split(lemmaDataCandidateCells[3], ',');
				for (String refNumCandidateStr : refNumCandidatesArr) {
					String[] refNumCandidateCells = StringUtils.split(refNumCandidateStr, '.');
					Integer homonymNr = Integer.valueOf(refNumCandidateCells[0]);
					Integer meaningNr = Integer.valueOf(refNumCandidateCells[1]);
					RefNum refNum = new RefNum(homonymNr, meaningNr);
					collocMember = new CollocMember(collocMemberName, word, form, morphCode, posCode, conjunct, refNum);
					collocMembers.add(collocMember);
				}
			} else {
				collocMember = new CollocMember(collocMemberName, word, form, morphCode, posCode, conjunct, null);
				collocMembers.add(collocMember);
			}
		}
		return collocMembers;
	}

	private List<String> extractCollocUsages(Node collocGroupNode) {
		List<Node> collocUsageNodes = collocGroupNode.selectNodes(collocUsageExp);
		if (CollectionUtils.isEmpty(collocUsageNodes)) {
			return null;
		}
		List<String> collocUsages = new ArrayList<>();
		for (Node collocUsageNode : collocUsageNodes) {
			String collocUsage = ((Element)collocUsageNode).getTextTrim();
			collocUsage = cleanEkiEntityMarkup(collocUsage);
			collocUsages.add(collocUsage);
		}
		return collocUsages;
	}

	private void saveCollocations(
			Node collocGroupNode,
			CollocGroup collocGroup,
			List<String> collocUsages,
			List<CollocMember> collocMembers,
			Map<String, Map<Integer, Word>> wordMap,
			Map<Long, Map<Integer, LexemeMeaning>> meaningMap,
			Map<String, UnknownWord> dummyWordMap,
			Map<String, CollocRecord> collocMap,
			Map<String, Count> countersMap,
			boolean doReports) throws Exception {

		Count repeatingCollocMemberCount = countersMap.get("repeatingCollocMemberCount");
		Count collocMemberOverloadGroupCount = countersMap.get("collocMemberOverloadGroupCount");
		Count collocMemberGuessingHomonymMeaningCount = countersMap.get("collocMemberGuessingHomonymMeaningCount");
		Count collocMemberGuessedHomonymMeaningCount = countersMap.get("collocMemberGuessedHomonymMeaningCount");
		Count collocMemberSuggestedHomonymMeaningCount = countersMap.get("collocMemberSuggestedHomonymMeaningCount");
		Count collocMemberCreatedHomonymMeaningCount = countersMap.get("collocMemberCreatedHomonymMeaningCount");
		Count collocMemberCreatedMoreThanOneHomonymCount = countersMap.get("collocMemberCreatedMoreThanOneHomonymCount");
		Count collocMemberUniqueDummyHomonymCount = countersMap.get("collocMemberUniqueDummyHomonymCount");
		Count collocMemberDummyHomonymCount = countersMap.get("collocMemberDummyHomonymCount");
		Count collocateCount = countersMap.get("collocateCount");
		Count collocationCount = countersMap.get("collocationCount");
		Count reusedCollocationCount = countersMap.get("reusedCollocationCount");
		Count updatedCollocCount = countersMap.get("updatedCollocCount");
		Count updatedCollocMemberCount = countersMap.get("updatedCollocMemberCount");

		String word = collocGroup.getWord();
		Long lexemeId = collocGroup.getLexemeId();
		String collocPosGroupCode = collocGroup.getCollocPosGroupCode();
		String collocRelGroupName = collocGroup.getCollocRelGroupName();
		Long collocRelGroupId = collocGroup.getCollocRelGroupId();
		int collocGroupOrder = collocGroup.getCollocGroupOrder();

		if (CollectionUtils.isEmpty(collocMembers)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupCode, collocRelGroupName, "x:colg", "[" + collocGroupOrder + "]", "puuduvad valiidsed kollokaadid");
			return;
		}

		String collocDefinition = null;
		Element collocDefinitionNode = (Element) collocGroupNode.selectSingleNode(collocDefinitionExp);
		if (collocDefinitionNode != null) {
			collocDefinition = collocDefinitionNode.getTextTrim();
		}

		Float frequency = null;
		Element collocFreqNode = (Element) collocGroupNode.selectSingleNode(collocFreqExp);
		if (collocFreqNode != null) {
			try {
				frequency = Float.parseFloat(collocFreqNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupCode, collocRelGroupName, "x:colg", "[" + collocGroupOrder + "]", "sagedusel sobimatu formaat");
			}
		}

		Float score = null;
		Element collocScoreNode = (Element) collocGroupNode.selectSingleNode(collocScoreExp);
		if (collocScoreNode != null) {
			try {
				score = Float.parseFloat(collocScoreNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupCode, collocRelGroupName, "x:colg", "[" + collocGroupOrder + "]", "skooril sobimatu formaat");
			}
		}

		List<List<CollocMember>> collocMembersPermutations = composeCollocMembersPermutations(collocMembers, collocMemberOverloadGroupCount);
		if (collocMembersPermutations.size() > 1) {
			collocMemberOverloadGroupCount.increment();
		}

		List<Long> currentCollocMemberIds;
		List<CollocMemberRecord> currentCollocMemberRecords;
		CollocMemberRecord collocMemberRecord;

		for (List<CollocMember> collocMembersPermutation : collocMembersPermutations) {

			String collocation = composeCollocValue(collocMembersPermutation);

			currentCollocMemberRecords = new ArrayList<>();
			currentCollocMemberIds = new ArrayList<>();

			for (CollocMember collocMember : collocMembersPermutation) {

				String collocMemberName = collocMember.getName();
				String collocMemberWord = collocMember.getWord();
				String collocMemberForm = collocMember.getForm();
				String collocMemberConjunct = collocMember.getConjunct();
				String collocMemberPosCode = collocMember.getPosCode();
				RefNum collocMemberRefNum = collocMember.getRefNum();

				Long collocLexemeId = null;

				Map<Integer, Word> homonymWordMap = wordMap.get(collocMemberWord);
				if (homonymWordMap == null) {
					if (StringUtils.equals(collocMemberName, prevWordCollocMemberName)) {
						appendToReport(doReports, REPORT_INCORRECT_LEMPOSVK_LEMMA, word, collocation, collocMemberWord);
						collocMember.setWord(word);
						collocMemberWord = word;
						homonymWordMap = wordMap.get(word);
					} else if (StringUtils.equals(collocMemberName, nextWordCollocMemberName)) {
						appendToReport(doReports, REPORT_INCORRECT_LEMPOSVK_LEMMA, word, collocation, collocMemberWord);
						collocMember.setWord(word);
						collocMemberWord = word;
						homonymWordMap = wordMap.get(word);
					}					
				}
				if (homonymWordMap == null) {
					if (collocMemberRefNum == null) {
						UnknownWord unknownWord = handleUnknownWord(collocMemberWord, dummyWordMap, collocMemberUniqueDummyHomonymCount);
						collocLexemeId = unknownWord.getLexemeId();
						collocMemberDummyHomonymCount.increment();
						appendToReport(doReports, REPORT_UNKNOWN_COLLOC_MEMBER, word, collocation, collocMemberWord);
					} else {
						collocLexemeId = createMissingCollocMember(collocMember, wordMap, meaningMap);
						collocMemberCreatedHomonymMeaningCount.increment();
					}
				} else {
					if (StringUtils.equals(collocMemberName, prevWordCollocMemberName)) {
						if (currentCollocMemberIds.contains(lexemeId)) {
							repeatingCollocMemberCount.increment();
							appendToReport(doReports, REPORT_REPEATING_COLLOC_MEMBER, word, collocation, collocMemberForm);
						} else {
							collocMemberRecord = new CollocMemberRecord(
									lexemeId, collocRelGroupId, collocMemberForm, collocMemberConjunct, inboundPrimaryCollocMemberWeight, collocGroupOrder);
							currentCollocMemberIds.add(lexemeId);
							currentCollocMemberRecords.add(collocMemberRecord);
						}
					} else if (StringUtils.equals(collocMemberName, nextWordCollocMemberName)) {
						if (currentCollocMemberIds.contains(lexemeId)) {
							repeatingCollocMemberCount.increment();
							appendToReport(doReports, REPORT_REPEATING_COLLOC_MEMBER, word, collocation, collocMemberForm);
						} else {
							collocMemberRecord = new CollocMemberRecord(
									lexemeId, collocRelGroupId, collocMemberForm, collocMemberConjunct, inboundPrimaryCollocMemberWeight, collocGroupOrder);
							currentCollocMemberIds.add(lexemeId);
							currentCollocMemberRecords.add(collocMemberRecord);
						}
					} else if (collocMemberRefNum == null) {
						//just guessing here. should be determined by more intelligent logic
						if (homonymWordMap.size() == 1) {
							Word collocWordObj = homonymWordMap.get(1);
							if (collocWordObj == null) {
								collocWordObj = homonymWordMap.values().iterator().next();
								appendToReport(doReports, REPORT_FAILING_HOMONYM_GUESS, word, collocation, collocMemberForm, "homonüüme on üks, kuid see pole esimene");
							}
							Long collocWordId = collocWordObj.getId();
							List<LexemeMeaning> lexemeMeaningCandidates = getLexemeMeaningCandidates(collocWordId, collocMemberPosCode);
							if (CollectionUtils.isEmpty(lexemeMeaningCandidates)) {
								//none
								appendToReport(doReports, REPORT_FAILING_HOMONYM_GUESS, word, collocation, collocMemberForm, "homonüüme on üks, kuid sellel puudub tähendus");
							} else if (lexemeMeaningCandidates.size() == 1) {
								//success!
								LexemeMeaning collocLexemeMeaning = lexemeMeaningCandidates.get(0);
								collocLexemeId = collocLexemeMeaning.getLexemeId();
								collocMemberGuessedHomonymMeaningCount.increment();
							} else {
								//too many
								appendToReport(doReports, REPORT_FAILING_HOMONYM_GUESS, word, collocation, collocMemberForm, "homonüüme on üks, kuid sellel on mitu tähendust");
								// with Arvi's permission:
								LexemeMeaning collocLexemeMeaning = lexemeMeaningCandidates.get(0);
								collocLexemeId = collocLexemeMeaning.getLexemeId();
								collocMemberSuggestedHomonymMeaningCount.increment();
							}
						} else {
							UnknownWord unknownWord = handleUnknownWord(collocMemberWord, dummyWordMap, collocMemberUniqueDummyHomonymCount);
							collocLexemeId = unknownWord.getLexemeId();
							collocMemberDummyHomonymCount.increment();
							appendToReport(doReports, REPORT_FAILING_HOMONYM_GUESS, word, collocation, collocMemberForm, "rohkem kui üks homonüüm");
						}
						collocMemberGuessingHomonymMeaningCount.increment();
					} else {
						Integer collocMemberHomonymNr = collocMemberRefNum.getHomonymNr();
						Integer collocMemberMeaningNr = collocMemberRefNum.getMeaningNr();
						Word collocWordObj = homonymWordMap.get(collocMemberHomonymNr);
						if (collocWordObj == null) {
							//case where the word only exists in collocations with more than one homonym
							//statistically, just a precaution, not real situation
							collocLexemeId = createMissingCollocMember(collocMember, wordMap, meaningMap);
							collocMemberCreatedHomonymMeaningCount.increment();
							collocMemberCreatedMoreThanOneHomonymCount.increment();
						} else {
							Long collocWordId = collocWordObj.getId();
							Map<Integer, LexemeMeaning> levelMeaningMap = meaningMap.get(collocWordId);
							LexemeMeaning collocLexemeMeaning = levelMeaningMap.get(collocMemberMeaningNr);
							if (collocLexemeMeaning == null) {
								UnknownWord unknownWord = handleUnknownWord(collocMemberWord, dummyWordMap, collocMemberUniqueDummyHomonymCount);
								collocLexemeId = unknownWord.getLexemeId();
								collocMemberDummyHomonymCount.increment();
								appendToReport(doReports, REPORT_ILLEGAL_LEMPOSVK_REF, word, collocation, collocMemberWord, "i=" + collocMemberHomonymNr, "tnr=" + collocMemberMeaningNr);
							} else {
								collocLexemeId = collocLexemeMeaning.getLexemeId();
							}
						}
					}
				}

				if (collocLexemeId != null) {
					if (currentCollocMemberIds.contains(collocLexemeId)) {
						repeatingCollocMemberCount.increment();
						appendToReport(doReports, REPORT_REPEATING_COLLOC_MEMBER, word, collocation, collocMemberForm);
					} else if (StringUtils.equals(collocMemberName, colWordCollocMemberName)) {
						collocMemberRecord = new CollocMemberRecord(
								collocLexemeId, null, collocMemberForm, collocMemberConjunct, outboundPrimaryCollocMemberWeight, null);
						currentCollocMemberRecords.add(collocMemberRecord);
						currentCollocMemberIds.add(collocLexemeId);
					} else if (ArrayUtils.contains(contextCollocMemberNames, collocMemberName)) {
						collocMemberRecord = new CollocMemberRecord(
								collocLexemeId, null, collocMemberForm, collocMemberConjunct, outboundSecondaryCollocMemberWeight, null);
						currentCollocMemberRecords.add(collocMemberRecord);
						currentCollocMemberIds.add(collocLexemeId);
					} else {
						// something would be very wrong
						throw new DataLoadingException("Unexpected colloc member type at: " + collocMember.toString());
					}
				}
			}

			CollocRecord existingCollocRecord = collocMap.get(collocation);
			if (existingCollocRecord == null) {
				collocationCount.increment();
				collocateCount.increment(currentCollocMemberRecords.size());
				Long collocId = createCollocation(collocation, collocDefinition, frequency, score, collocUsages, currentCollocMemberRecords);
				existingCollocRecord = new CollocRecord(collocId, collocDefinition, collocUsages, currentCollocMemberRecords);
				collocMap.put(collocation, existingCollocRecord);
			} else {
				reusedCollocationCount.increment();
				compareAndUpdateCollocation(existingCollocRecord, collocUsages, collocDefinition, updatedCollocCount, word, collocation, doReports);
				compareAndUpdateCollocMembers(existingCollocRecord, currentCollocMemberRecords, updatedCollocMemberCount);
			}
		}
	}

	private String composeCollocValue(List<CollocMember> collocMembers) {

		StringBuffer collocBuf = new StringBuffer();
		for (CollocMember collocMember : collocMembers) {
			String collocMemberName = collocMember.getName();
			String collocMemberForm = collocMember.getForm();
			String conjunct = collocMember.getConjunct();
			if (StringUtils.isNotBlank(conjunct)) {
				if (StringUtils.equals(collocMemberName, prevWordCollocMemberName)) {
					collocBuf.append(collocMemberForm);
					collocBuf.append(' ');
					collocBuf.append(conjunct);
					collocBuf.append(' ');
				} else if (StringUtils.equals(collocMemberName, nextWordCollocMemberName)) {
					collocBuf.append(conjunct);
					collocBuf.append(' ');
					collocBuf.append(collocMemberForm);
					collocBuf.append(' ');
				} else {
					//illegal case
				}
			} else {
				collocBuf.append(collocMemberForm);
				collocBuf.append(' ');
			}
		}
		String collocation = StringUtils.trim(collocBuf.toString());
		return collocation;
	}

	private List<List<CollocMember>> composeCollocMembersPermutations(List<CollocMember> collocMembers, Count collocMemberPermutationGroupCount) {

		List<List<CollocMember>> collocMembersPermutations = new ArrayList<>();
		Map<String, List<CollocMember>> collocMemberAlternativesMap = new HashMap<>();
		List<String> collocMemberNames = new ArrayList<>();
		for (CollocMember collocMember : collocMembers) {
			String collocMemberName = collocMember.getName();
			if (!collocMemberNames.contains(collocMemberName)) {
				collocMemberNames.add(collocMemberName);
			}
			List<CollocMember> collocMemberAlternatives = collocMemberAlternativesMap.get(collocMemberName);
			if (CollectionUtils.isEmpty(collocMemberAlternatives)) {
				collocMemberAlternatives = new ArrayList<>();
				collocMemberAlternativesMap.put(collocMemberName, collocMemberAlternatives);
			}
			collocMemberAlternatives.add(collocMember);
		}
		permutateCollocMembers(0, collocMemberNames, collocMemberAlternativesMap, new ArrayList<>(), collocMembersPermutations);
		return collocMembersPermutations;
	}

	private void permutateCollocMembers(
			int collocMemberIndex, List<String> collocMemberNames, Map<String, List<CollocMember>> collocMemberAlternativesMap,
			List<CollocMember> collocMembersPermutation, List<List<CollocMember>> collocMembersPermutations) {

		String collocMemberName = collocMemberNames.get(collocMemberIndex);
		List<CollocMember> collocMemberAlternatives = collocMemberAlternativesMap.get(collocMemberName);
		List<CollocMember> collocMembersPermutationBranch;
		for (CollocMember collocMemberAlternative : collocMemberAlternatives) {
			collocMembersPermutationBranch = new ArrayList<>(collocMembersPermutation);
			collocMembersPermutationBranch.add(collocMemberAlternative);
			if (collocMemberIndex < collocMemberNames.size() - 1) {
				permutateCollocMembers(collocMemberIndex + 1, collocMemberNames, collocMemberAlternativesMap, collocMembersPermutationBranch, collocMembersPermutations);
			} else {
				collocMembersPermutations.add(collocMembersPermutationBranch);
			}
		}
	}

	private UnknownWord handleUnknownWord(String word, Map<String, UnknownWord> dummyWordMap, Count collocMemberUniqueDummyHomonymCount) throws Exception {

		UnknownWord unknownWord = dummyWordMap.get(word);
		if (unknownWord == null) {
			collocMemberUniqueDummyHomonymCount.increment();
			Map<String, Object> tableRowParamMap;
			Long wordId = basicDbService.create(WORD);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("word_id", wordId);
			Long paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("paradigm_id", paradigmId);
			tableRowParamMap.put("morph_code", defaultWordMorphCode);
			tableRowParamMap.put("mode", FormMode.UNKNOWN.name());
			tableRowParamMap.put("value", word);
			basicDbService.create(FORM, tableRowParamMap);
			Long meaningId = basicDbService.create(MEANING);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("word_id", wordId);
			tableRowParamMap.put("meaning_id", meaningId);
			tableRowParamMap.put("dataset_code", getDataset());
			Long lexemeId = basicDbService.create(LEXEME, tableRowParamMap);
			unknownWord = new UnknownWord(wordId, lexemeId, meaningId);
			dummyWordMap.put(word, unknownWord);
		}
		return unknownWord;
	}

	private Long createMissingCollocMember(
			CollocMember collocMember,
			Map<String, Map<Integer, Word>> wordMap,
			Map<Long, Map<Integer, LexemeMeaning>> meaningMap) throws Exception {

		String collocMemberWord = collocMember.getWord();
		String collocMemberMorphCode = collocMember.getMorphCode();
		String collocMemberPosCode = collocMember.getPosCode();
		RefNum collocMemberRefNum = collocMember.getRefNum();

		Long collocLexemeId;
		Map<Integer, Word> homonymWordMap;
		Integer collocMemberHomonymNr = collocMemberRefNum.getHomonymNr();
		Integer collocMemberMeaningNr = collocMemberRefNum.getMeaningNr();

		Word collocWordObj = createOrSelectWord(collocMemberWord, collocMemberMorphCode);
		Long collocWordId = collocWordObj.getId();
		LexemeMeaning collocLexemeMeaning = createOrSelectLexemeMeaning(collocWordId, collocMemberMeaningNr, collocMemberPosCode);
		collocLexemeId = collocLexemeMeaning.getLexemeId();

		homonymWordMap = new HashMap<>();
		homonymWordMap.put(collocMemberHomonymNr, collocWordObj);
		wordMap.put(collocMemberWord, homonymWordMap);
		Map<Integer, LexemeMeaning> levelMeaningMap = new HashMap<>();
		levelMeaningMap.put(collocMemberMeaningNr, collocLexemeMeaning);
		meaningMap.put(collocWordId, levelMeaningMap);

		return collocLexemeId;
	}

	private Long createCollocation(
			String collocation, String definition, Float frequency, Float score, List<String> collocUsages, List<CollocMemberRecord> collocMemberRecords) throws Exception {

		Long collocationId = createCollocation(collocation, definition, frequency, score, collocUsages);
		Integer memberOrder = 0;
		for (CollocMemberRecord collocMemberRecord : collocMemberRecords) {
			memberOrder++;
			createLexemeCollocation(collocationId, memberOrder, collocMemberRecord);
		}
		return collocationId;
	}

	private void compareAndUpdateCollocation(
			CollocRecord existingCollocRecord, List<String> collocUsages, String collocDefinition,
			Count updatedCollocCount, String word, String collocation, boolean doReports) throws Exception {

		Long collocId = existingCollocRecord.getId();
		String existingCollocDefinition = existingCollocRecord.getDefinition();
		List<String> existingCollocUsages = existingCollocRecord.getUsages();
		String newCollocDefinition = null;
		List<String> newCollocUsages = null;
		boolean doUpdate = false;
		// definition
		if (StringUtils.isBlank(existingCollocDefinition) && StringUtils.isBlank(collocDefinition)) {
			//do nothing
		} else if (StringUtils.isNotBlank(existingCollocDefinition) && StringUtils.isBlank(collocDefinition)) {
			//do nothing
		} else if (StringUtils.isBlank(existingCollocDefinition) && StringUtils.isNotBlank(collocDefinition)) {
			existingCollocRecord.setDefinition(collocDefinition);
			newCollocDefinition = collocDefinition;
			doUpdate = true;
		} else if (!StringUtils.equals(existingCollocDefinition, collocDefinition)) {
			appendToReport(doReports, REPORT_DIFFERENT_COLLOC_DEFINITION, word, collocation, existingCollocDefinition, collocDefinition);
		}
		// usages
		if (CollectionUtils.isEmpty(existingCollocUsages) && CollectionUtils.isEmpty(collocUsages)) {
			//do nothing
		} else if (CollectionUtils.isNotEmpty(existingCollocUsages) && CollectionUtils.isEmpty(collocUsages)) {
			//do nothing
		} else if (CollectionUtils.isEmpty(existingCollocUsages) && CollectionUtils.isNotEmpty(collocUsages)) {
			existingCollocRecord.setUsages(collocUsages);
			newCollocUsages = collocUsages;
			doUpdate = true;
		} else if (CollectionUtils.isNotEmpty(collocUsages) && !existingCollocUsages.equals(collocUsages)) {
			existingCollocUsages.forEach(existingCollocUsage -> collocUsages.remove(existingCollocUsage));
			if (CollectionUtils.isNotEmpty(collocUsages)) {
				existingCollocUsages.addAll(collocUsages);
				newCollocUsages = existingCollocUsages;
				doUpdate = true;
			}
		}
		if (doUpdate) {
			updatedCollocCount.increment();
			updateCollocation(collocId, newCollocDefinition, newCollocUsages);
		}
	}

	private void compareAndUpdateCollocMembers(
			CollocRecord existingCollocRecord,
			List<CollocMemberRecord> currentCollocMemberRecords,
			Count updatedCollocMemberCount) throws Exception {

		Long collocationId = existingCollocRecord.getId();
		List<CollocMemberRecord> existingCollocMemberRecords = existingCollocRecord.getMembers();

		for (CollocMemberRecord existingCollocMemberRecord : existingCollocMemberRecords) {
			Long existingCollocMemberLexemeId = existingCollocMemberRecord.getLexemeId();
			Long existingCollocMemberRelGroupId = existingCollocMemberRecord.getRelGroupId();
			Float existingCollocMemberWeight = existingCollocMemberRecord.getWeight();
			for (CollocMemberRecord currentCollocMemberRecord : currentCollocMemberRecords) {
				Long currentCollocMemberLexemeId = currentCollocMemberRecord.getLexemeId();
				if (existingCollocMemberLexemeId.equals(currentCollocMemberLexemeId)) {
					Long currentCollocMemberRelGroupId = currentCollocMemberRecord.getRelGroupId();
					Float currentCollocMemberWeight = currentCollocMemberRecord.getWeight();
					Integer currentCollocMemberGroupOrder = currentCollocMemberRecord.getGroupOrder();
					boolean doUpdate = false;
					Long newCollocMemberRelGroupId = null;
					Float newCollocMemberWeight = null;
					Integer newCollocMemberGroupOrder = null;
					// rel group ownership and group order must always be specified together
					if ((existingCollocMemberRelGroupId == null) && (currentCollocMemberRelGroupId != null)) {
						existingCollocMemberRecord.setRelGroupId(currentCollocMemberRelGroupId);
						existingCollocMemberRecord.setGroupOrder(currentCollocMemberGroupOrder);
						newCollocMemberRelGroupId = currentCollocMemberRelGroupId;
						newCollocMemberGroupOrder = currentCollocMemberGroupOrder;
						doUpdate = true;
					}
					if (currentCollocMemberWeight > existingCollocMemberWeight) {
						existingCollocMemberRecord.setWeight(currentCollocMemberWeight);
						newCollocMemberWeight = currentCollocMemberWeight;
						doUpdate = true;
					}
					if (doUpdate) {
						updatedCollocMemberCount.increment();
						updateLexemeCollocation(
								collocationId, existingCollocMemberLexemeId, newCollocMemberRelGroupId,
								newCollocMemberWeight, newCollocMemberGroupOrder);
					}
					break;
				}
			}
		}
	}

	private Word createOrSelectWord(String word, String morphCode) throws Exception {
		return createOrSelectWord(word, morphCode, null, null, null, null, null);
	}

	private Word createOrSelectWord(
			String word, String morphCode, String guid, String wordDisplayMorph,
			Map<String, List<Guid>> ssGuidMap, Count ssWordCount, Count reusedWordCount) throws Exception {

		int homonymNr = getWordMaxHomonymNr(word, dataLang);
		homonymNr++;
		Word wordObj = new Word(word, dataLang, homonymNr, morphCode, guid);
		wordObj.setDisplayMorph(wordDisplayMorph);
		Long wordId = createOrSelectWord(wordObj, null, getDataset(), ssGuidMap, ssWordCount, reusedWordCount);
		wordObj.setId(wordId);
		return wordObj;
	}

	private LexemeMeaning createLexemeMeaning(Long wordId, Integer level1, String posCode) throws Exception {

		Long meaningId = createMeaning();
		Lexeme lexemeObj = new Lexeme();
		lexemeObj.setWordId(wordId);
		lexemeObj.setMeaningId(meaningId);
		lexemeObj.setLevel1(level1);
		Long lexemeId = createLexeme(lexemeObj, getDataset());
		if (StringUtils.isNotBlank(posCode)) {
			createLexemePos(lexemeId, posCode);
		}
		LexemeMeaning lexemeMeaning = new LexemeMeaning(lexemeId, meaningId);
		return lexemeMeaning;
	}

	private LexemeMeaning createOrSelectLexemeMeaning(Long wordId, Integer level1, String posCode) throws Exception {

		List<LexemeMeaning> lexemeMeaningCandidates = getLexemeMeaningCandidates(wordId, posCode);
		if (CollectionUtils.size(lexemeMeaningCandidates) == 1) {
			LexemeMeaning lexemeMeaningCandidate = lexemeMeaningCandidates.get(0);
			return lexemeMeaningCandidate;
		}
		return createLexemeMeaning(wordId, level1, posCode);
	}

	private void createLexemePos(Long lexemeId, String posCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("pos_code", posCode);
		basicDbService.create(LEXEME_POS, tableRowParamMap);
	}

	private Long createCollocPosGroup(Long lexemeId, String posGroupCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("pos_group_code", posGroupCode);
		Long collocPosGroupId = basicDbService.create(LEX_COLLOC_POS_GROUP, tableRowParamMap);
		return collocPosGroupId;
	}

	private Long createCollocRelGroup(Long collocPosGroupId, String name, Float frequency, Float score) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("pos_group_id", collocPosGroupId);
		tableRowParamMap.put("name", name);
		if (frequency != null) {
			tableRowParamMap.put("frequency", frequency);
		}
		if (score != null) {
			tableRowParamMap.put("score", score);
		}
		Long collocRelGroupId = basicDbService.create(LEX_COLLOC_REL_GROUP, tableRowParamMap);
		return collocRelGroupId;
	}

	private Long createCollocation(String collocation, String definition, Float frequency, Float score, List<String> collocUsages) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("value", collocation);
		if (StringUtils.isNotBlank(definition)) {
			tableRowParamMap.put("definition", definition);
		}
		if (frequency != null) {
			tableRowParamMap.put("frequency", frequency);
		}
		if (score != null) {
			tableRowParamMap.put("score", score);
		}
		if (CollectionUtils.isNotEmpty(collocUsages)) {
			String[] collocUsagesArr = collocUsages.toArray(new String[0]);
			tableRowParamMap.put("usages", collocUsagesArr);
		}
		Long collocationId = basicDbService.create(COLLOCATION, tableRowParamMap);
		return collocationId;
	}

	private Long createLexemeCollocation(Long collocationId, Integer memberOrder, CollocMemberRecord collocMemberRecord) throws Exception {

		Long lexemeId = collocMemberRecord.getLexemeId();
		Long relGroupId = collocMemberRecord.getRelGroupId();
		String memberForm = collocMemberRecord.getMemberForm();
		String conjunct = collocMemberRecord.getConjunct();
		Float weight = collocMemberRecord.getWeight();
		Integer groupOrder = collocMemberRecord.getGroupOrder();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("collocation_id", collocationId);
		tableRowParamMap.put("lexeme_id", lexemeId);
		if (relGroupId != null) {
			tableRowParamMap.put("rel_group_id", relGroupId);
		}
		tableRowParamMap.put("member_form", memberForm);
		if (StringUtils.isNotBlank(conjunct)) {
			tableRowParamMap.put("conjunct", conjunct);
		}
		tableRowParamMap.put("weight", weight);
		tableRowParamMap.put("member_order", memberOrder);
		if (groupOrder != null) {
			tableRowParamMap.put("group_order", groupOrder);
		}
		Long lexCollocId = basicDbService.create(LEX_COLLOC, tableRowParamMap);
		return lexCollocId;
	}

	private void updateCollocation(Long collocId, String definition, List<String> collocUsages) throws Exception {
		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", collocId);
		Map<String, Object> valueParamMap = new HashMap<>();
		if (StringUtils.isNotEmpty(definition)) {
			valueParamMap.put("definition", definition);
		}
		if (CollectionUtils.isNotEmpty(collocUsages)) {
			String[] collocUsagesArr = collocUsages.toArray(new String[0]);
			valueParamMap.put("usages", collocUsagesArr);
		}
		basicDbService.update(COLLOCATION, criteriaParamMap, valueParamMap);
	}

	private void updateLexemeCollocation(Long collocId, Long lexemeId, Long relGroupId, Float weight, Integer groupOrder) throws Exception {
		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("collocation_id", collocId);
		criteriaParamMap.put("lexeme_id", lexemeId);
		Map<String, Object> valueParamMap = new HashMap<>();
		if (relGroupId != null) {
			valueParamMap.put("rel_group_id", relGroupId);
		}
		if (weight != null) {
			valueParamMap.put("weight", weight);
		}
		if (groupOrder != null) {
			valueParamMap.put("group_order", groupOrder);
		}
		basicDbService.update(LEX_COLLOC, criteriaParamMap, valueParamMap);
	}

	private List<LexemeMeaning> getLexemeMeaningCandidates(Long wordId, String posCode) throws Exception {

		List<LexemeMeaning> lexemeMeaningCandidates = getLexemeMeanings(wordId, posCode);
		if (CollectionUtils.isEmpty(lexemeMeaningCandidates) && StringUtils.isNotBlank(posCode)) {
			lexemeMeaningCandidates = getLexemeMeanings(wordId, null);
		}
		return lexemeMeaningCandidates;
	}

	private List<LexemeMeaning> getLexemeMeanings(Long wordId, String posCode) throws Exception {

		String sql;
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordId", wordId);
		tableRowParamMap.put("dataset", getDataset());
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

	private void appendToReport(boolean doReports, String reportName, Object ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	class CollocMember extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String name;

		private String word;

		private String form;

		private String morphCode;

		private String posCode;

		private String conjunct;

		private RefNum refNum;

		public CollocMember(String name, String word, String form, String morphCode, String posCode, String conjunct, RefNum refNum) {
			this.name = name;
			this.word = word;
			this.form = form;
			this.morphCode = morphCode;
			this.posCode = posCode;
			this.conjunct = conjunct;
			this.refNum = refNum;
		}

		public String getName() {
			return name;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
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

		public RefNum getRefNum() {
			return refNum;
		}
	}

	class CollocRecord extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long id;

		private String definition;

		private List<String> usages;

		private List<CollocMemberRecord> members;

		public CollocRecord(Long id, String definition, List<String> usages, List<CollocMemberRecord> members) {
			this.id = id;
			this.definition = definition;
			this.usages = usages;
			this.members = members;
		}

		public Long getId() {
			return id;
		}

		public String getDefinition() {
			return definition;
		}

		public void setDefinition(String definition) {
			this.definition = definition;
		}

		public List<String> getUsages() {
			return usages;
		}

		public void setUsages(List<String> usages) {
			this.usages = usages;
		}

		public List<CollocMemberRecord> getMembers() {
			return members;
		}

		public void setMembers(List<CollocMemberRecord> members) {
			this.members = members;
		}
	}

	class CollocMemberRecord extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long lexemeId;

		private Long relGroupId;

		private String memberForm;

		private String conjunct;

		private Float weight;

		private Integer groupOrder;

		public CollocMemberRecord(Long lexemeId, Long relGroupId, String memberForm, String conjunct, Float weight, Integer groupOrder) {
			this.lexemeId = lexemeId;
			this.relGroupId = relGroupId;
			this.memberForm = memberForm;
			this.conjunct = conjunct;
			this.weight = weight;
			this.groupOrder = groupOrder;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public void setLexemeId(Long lexemeId) {
			this.lexemeId = lexemeId;
		}

		public Long getRelGroupId() {
			return relGroupId;
		}

		public void setRelGroupId(Long relGroupId) {
			this.relGroupId = relGroupId;
		}

		public String getMemberForm() {
			return memberForm;
		}

		public void setMemberForm(String memberForm) {
			this.memberForm = memberForm;
		}

		public String getConjunct() {
			return conjunct;
		}

		public void setConjunct(String conjunct) {
			this.conjunct = conjunct;
		}

		public Float getWeight() {
			return weight;
		}

		public void setWeight(Float weight) {
			this.weight = weight;
		}

		public Integer getGroupOrder() {
			return groupOrder;
		}

		public void setGroupOrder(Integer groupOrder) {
			this.groupOrder = groupOrder;
		}
	}

	class CollocGroup extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String word;

		private String wordPosCode;

		private Long lexemeId;

		private String collocPosGroupCode;

		private Long collocPosGroupId;

		private String collocRelGroupName;

		private Long collocRelGroupId;

		private int collocGroupOrder;

		public CollocGroup(String word, String wordPosCode, Long lexemeId, String collocPosGroupCode, Long collocPosGroupId, String collocRelGroupName, Long collocRelGroupId, int collocGroupOrder) {
			this.word = word;
			this.wordPosCode = wordPosCode;
			this.lexemeId = lexemeId;
			this.collocPosGroupCode = collocPosGroupCode;
			this.collocPosGroupId = collocPosGroupId;
			this.collocRelGroupName = collocRelGroupName;
			this.collocRelGroupId = collocRelGroupId;
			this.collocGroupOrder = collocGroupOrder;
		}

		public String getWord() {
			return word;
		}

		public String getWordPosCode() {
			return wordPosCode;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public String getCollocPosGroupCode() {
			return collocPosGroupCode;
		}

		public Long getCollocPosGroupId() {
			return collocPosGroupId;
		}

		public String getCollocRelGroupName() {
			return collocRelGroupName;
		}

		public Long getCollocRelGroupId() {
			return collocRelGroupId;
		}

		public int getCollocGroupOrder() {
			return collocGroupOrder;
		}
	}

	class LexemeMeaning extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long lexemeId;

		private Long meaningId;

		public LexemeMeaning(Long lexemeId, Long meaningId) {
			this.lexemeId = lexemeId;
			this.meaningId = meaningId;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public Long getMeaningId() {
			return meaningId;
		}
	}

	class UnknownWord extends LexemeMeaning {

		private static final long serialVersionUID = 1L;

		private Long wordId;

		public UnknownWord(Long wordId, Long lexemeId, Long meaningId) {
			super(lexemeId, meaningId);
			this.wordId = wordId;
		}

		public Long getWordId() {
			return wordId;
		}
	}

	class RefNum extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Integer homonymNr;

		private Integer meaningNr;

		public RefNum(Integer homonymNr, Integer meaningNr) {
			this.homonymNr = homonymNr;
			this.meaningNr = meaningNr;
		}

		public Integer getHomonymNr() {
			return homonymNr;
		}

		public Integer getMeaningNr() {
			return meaningNr;
		}
	}
}
