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
import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class CollocLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(CollocLoaderRunner.class);

	@Deprecated
	private static final String SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_POS_PATH = "sql/select_lexeme_meaning_by_word_and_pos.sql";

	@Deprecated
	private static final String SQL_SELECT_LEXEME_MEANING_BY_WORD_AND_NO_POS_PATH = "sql/select_lexeme_meaning_by_word_and_no_pos.sql";

	private static final String REPORT_ILLEGAL_DATA = "illegal_data";

	private static final String REPORT_MISSING_DATA = "missing_data";

	private static final String REPORT_UNKNOWN_CLASSIF = "unknown_classifier";

	private static final String REPORT_REPEATING_COLLOC_MEMBER = "repeating_colloc_member";

	private static final String REPORT_UNKNOWN_COLLOC_MEMBER = "unknown_colloc_member";

	private static final String REPORT_ILLEGAL_LEMPOSVK_REF = "illegal_lemposvk_ref";

	@Deprecated
	private static final String REPORT_AMBIGUOUS_HOMONYM_MATCH = "ambiguous_homonym_match";

	@Deprecated
	private static final String REPORT_AMBIGUOUS_WORD_MATCH = "ambiguous_word_match";

	@Deprecated
	private static final String REPORT_AMBIGUOUS_LEXEME_MATCH = "ambiguous_lexeme_match";

	@Deprecated
	private static final String REPORT_UNKNOWN_WORD = "unknown_word";

	@Deprecated
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
	private final String collocUsageExp = "x:cng/x:cn[not(@x:as='ab')]";
	private final String collocRelGroupNameExp = "x:reln";
	private final String collocRelGroupFreqExp = "x:rfr";
	private final String collocRelGroupScoreExp = "x:rsc";
	private final String collocFreqExp = "x:cfr";
	private final String collocScoreExp = "x:csc";

	private final String wordHomonymNrAttr = "i";
	private final String lexemeLevelAttr = "tnr";
	private final String collocConjunctAttr = "jv";
	private final String lemmaDataAttr = "lemposvk";

	private final char lemmaDataDelim = '|';
	private final char lemmaDataCellDelim = ':';
	private final char compundWordCompDelim = '+';

	private final String defaultWordMorphCode = "??";

	private final String domainOriginBolan = "bolan";

	private final String[] collocMemberNodeNames = new String[] {
			"col", "mse", "msj", "cnte", "cce", "ccj", "cnt"};
	private final String[] textCleanupEnitites = new String[] {"&ba;", "&bl;"};
	private final String[] textCleanupEnityReplacements = new String[] {"", ""};

	private ReportComposer reportComposer;

	@Deprecated
	private String sqlSelectLexemeMeaningByWordAndPos;

	@Deprecated
	private String sqlSelectLexemeMeaningByWordAndNoPos;

	private Map<String, String> posConversionMap;

	private Map<String, String> registerConversionMap;

	private Map<String, String> morphConversionMap;

	@Override
	String getDataset() {
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

		morphConversionMap = new HashMap<>();
		morphConversionMap.put("SgN", "SgN");
		morphConversionMap.put("Sup", "Sup");
		morphConversionMap.put("#", "ID");
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, boolean doReports) throws Exception {

		logger.debug("Starting loading collocates...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("kol loader report",
					REPORT_ILLEGAL_DATA, REPORT_MISSING_DATA, REPORT_UNKNOWN_CLASSIF, REPORT_REPEATING_COLLOC_MEMBER, REPORT_UNKNOWN_COLLOC_MEMBER, REPORT_ILLEGAL_LEMPOSVK_REF,
					REPORT_AMBIGUOUS_HOMONYM_MATCH, REPORT_AMBIGUOUS_WORD_MATCH, REPORT_AMBIGUOUS_LEXEME_MATCH, REPORT_UNKNOWN_WORD, REPORT_COLLOC_PAIR_UNMATCH);
		}

		dataLang = unifyLang(dataLang);
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode, meaningDefinitionGroupNode, collocRelGroupNameNode, collocRelGroupFreqNode, collocRelGroupScoreNode;
		List<Element> meaningBlockNodes, collocPosGroupNodes, collocRelGroupNodes, collocGroupNodes;
		String word, collocPosGroupName, collocRelGroupName;
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
		Count successfulCollocateMatchCount = new Count();
		Count successfulCollocationMatchCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());

		Map<String, Map<Integer, Word>> wordMap = new HashMap<>();
		Map<Long, Map<Integer, LexemeMeaning>> meaningMap = new HashMap<>();
		extractAndSaveWordsLexemesMeanings(wordMap, meaningMap, articleNodes, dataLang, getDataset(), ignoredArticleCount);

		for (Element articleNode : articleNodes) {

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
			wordObj = wordMap.get(word).get(wordHomonymNum);
			Long wordId = wordObj.getId();
			String wordPosCode = wordPosNode.getTextTrim();
			wordPosCode = posConversionMap.get(wordPosCode);

			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);

			for (Element meaningBlockNode : meaningBlockNodes) {

				String level1Str = meaningBlockNode.attributeValue(lexemeLevelAttr);
				Integer level1 = Integer.valueOf(level1Str);
				levelMeaningMap = meaningMap.get(wordId);
				LexemeMeaning lexemeMeaning = levelMeaningMap.get(level1);
				Long lexemeId = lexemeMeaning.getLexemeId();
				Long meaningId = lexemeMeaning.getMeaningId();

				meaningDefinitionGroupNode = (Element) meaningBlockNode.selectSingleNode(meaningDefinitionGroupExp);
				if (meaningDefinitionGroupNode != null) {
					extractAndSaveLexemeRegisters(word, lexemeId, meaningDefinitionGroupNode, doReports);
					extractAndSaveMeaningDomains(word, meaningId, meaningDefinitionGroupNode, doReports);
					extractAndSaveMeaningDefinitions(meaningId, meaningDefinitionGroupNode, dataLang, getDataset());
				} else {
					//log??
				}

				extractAndSaveGrammar(lexemeId, meaningBlockNode, dataLang);

				collocPosGroupNodes = meaningBlockNode.selectNodes(collocPosGroupExp);//x:colp/x:cmg

				for (Element colPosGroupNode : collocPosGroupNodes) {

					collocPosGroupName = colPosGroupNode.attributeValue(collocPosAttr);
					Long collocPosGroupId = createCollocPosGroup(lexemeId, collocPosGroupName);

					collocRelGroupNodes = colPosGroupNode.selectNodes(collocRelGroupExp);//x:relg
					int collocRelGroupNum = 0;

					for (Element collocRelGroupNode : collocRelGroupNodes) {

						collocRelGroupNum++;
						collocRelGroupNameNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupNameExp);
						collocRelGroupName = collocRelGroupNameNode.getTextTrim();

						Float collocRelGroupFreq = null;
						collocRelGroupFreqNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupFreqExp);
						if (collocRelGroupFreqNode == null) {
							appendToReport(doReports, REPORT_MISSING_DATA, word, collocPosGroupName, collocRelGroupName, "x:relg", "[" + collocRelGroupNum + "]", "puudub sagedus");
						} else {
							try {
								collocRelGroupFreq = Float.parseFloat(collocRelGroupFreqNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupName, collocRelGroupName, "x:relg", "[" + collocRelGroupNum + "]", "sagedusel sobimatu formaat");
							}
						}

						Float collocRelGroupScore = null;
						collocRelGroupScoreNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupScoreExp);
						if (collocRelGroupScoreNode == null) {
							appendToReport(doReports, REPORT_MISSING_DATA, word, collocPosGroupName, collocRelGroupName, "x:relg", "[" + collocRelGroupNum + "]", "puudub skoor");
						} else {
							try {
								collocRelGroupScore = Float.parseFloat(collocRelGroupScoreNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, word, collocPosGroupName, collocRelGroupName, "x:relg", "[" + collocRelGroupNum + "]", "skooril sobimatu formaat");
							}
						}

						Long collocRelGroupId = createCollocRelGroup(collocPosGroupId, collocRelGroupName, collocRelGroupFreq, collocRelGroupScore);

						collocGroupNodes = collocRelGroupNode.selectNodes(collocGroupExp);//x:colg
						int collocGroupNum = 0;

						for (Element collocGroupNode : collocGroupNodes) {

							collocGroupNum++;
							collocUsages = extractCollocUsages(collocGroupNode);
							collocMembers = extractCollocMembers(collocGroupNode, ignoredCollocGroupCount);
							collocGroup = new CollocGroup(collocPosGroupName, collocPosGroupId, collocRelGroupName, collocRelGroupId, collocGroupNum);
							saveCollocations(
									collocGroupNode, collocGroup, word, wordPosCode, lexemeId,
									collocUsages, collocMembers, wordMap, meaningMap, dataLang,
									repeatingCollocMemberCount,
									collocMemberOverloadGroupCount,
									collocMemberGuessingHomonymMeaningCount,
									collocMemberGuessedHomonymMeaningCount,
									successfulCollocateMatchCount,
									successfulCollocationMatchCount,
									doReports);
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
		logger.debug("Found {} guess colloc member homonym/meaning attempts", collocMemberGuessingHomonymMeaningCount.getValue());
		logger.debug("Found {} successfully guessed colloc member homonym/meaning", collocMemberGuessedHomonymMeaningCount.getValue());
		logger.debug("Found {} successful collocate matches", successfulCollocateMatchCount.getValue());
		logger.debug("Found {} collocations", successfulCollocationMatchCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private void extractAndSaveWordsLexemesMeanings(
			Map<String, Map<Integer, Word>> wordMap,
			Map<Long, Map<Integer, LexemeMeaning>> meaningMap,
			List<Element> articleNodes,
			String dataLang, String dataset,
			Count ignoredArticleCount) throws Exception {

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

			HashMap<Integer, LexemeMeaning> levelMeaningMap = new HashMap<>();
			meaningMap.put(wordId, levelMeaningMap);
			
			for (Element meaningBlockNode : meaningBlockNodes) {

				String level1Str = meaningBlockNode.attributeValue(lexemeLevelAttr);
				Integer level1 = Integer.valueOf(level1Str);
				LexemeMeaning lexemeMeaning = createLexemeMeaning(wordId, level1, wordPosCode, dataset);
				levelMeaningMap.put(level1, lexemeMeaning);
			}
		}
	}

	private void extractAndSaveLexemeRegisters(String newWord, Long lexemeId, Element meaningDefinitionGroupNode, boolean doReports) throws Exception {
		List<Element> lexemeRegisterNodes = meaningDefinitionGroupNode.selectNodes(lexemeRegisterExp);
		for (Element lexemeRegisterNode : lexemeRegisterNodes) {
			String lexemeRegister = lexemeRegisterNode.getTextTrim();
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

	private List<CollocMember> extractCollocMembers(Element collocGroupNode, Count ignoredCollocGroupCount) {

		final String[] skippedNodeNames = new String[] {"colloc", "cfr", "csc", "cng", "cd", "s", "v", "rek"};
		final String collocNodeName = "col";
		final String[] lemmaDataRequiredNodeNames = new String[] {collocNodeName, "mse", "msj"};

		List<CollocMember> collocMembers = new ArrayList<>();
		Iterator<Node> collocGroupNodeIter = collocGroupNode.nodeIterator();
		List<CollocMember> lemmaDataCollocMembers;

		while (collocGroupNodeIter.hasNext()) {
			Node collocMemberAbstractNode = collocGroupNodeIter.next();
			if (collocMemberAbstractNode instanceof DefaultElement) {
				DefaultElement collocMemberNode = (DefaultElement) collocMemberAbstractNode;
				String collocMemberName = collocMemberNode.getName();
				if (ArrayUtils.contains(collocMemberNodeNames, collocMemberName)) {

					String form = collocMemberNode.getTextTrim();
					String conjunct = collocMemberNode.attributeValue(collocConjunctAttr);
					String lemmaDataStr = collocMemberNode.attributeValue(lemmaDataAttr);
					conjunct = StringUtils.replace(conjunct, "v", "või");

					if (StringUtils.isBlank(lemmaDataStr)) {
						//col, mse, msj
						if (ArrayUtils.contains(lemmaDataRequiredNodeNames, collocMemberName)) {
							ignoredCollocGroupCount.increment();
							return Collections.emptyList();
						}
						continue;
					} else {
						lemmaDataCollocMembers = composeCollocMembers(collocMemberName, form, conjunct, lemmaDataStr);
						for (CollocMember lemmaDataCollocMember : lemmaDataCollocMembers) {
							//col
							if (StringUtils.equals(collocNodeName, collocMemberName) && (lemmaDataCollocMember.getRefNum() == null)) {
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
		String[] lemmaDataCandidatesArr = StringUtils.split(lemmaDataStr, lemmaDataDelim);
		CollocMember collocMember;
		for (String lemmaDataCandidateStr : lemmaDataCandidatesArr) {
			String[] lemmaDataCandidateCells = StringUtils.split(lemmaDataCandidateStr, lemmaDataCellDelim);
			String word = lemmaDataCandidateCells[0];
			word = StringUtils.remove(word, compundWordCompDelim);//deal with compound words later
			String posCode = lemmaDataCandidateCells[1];
			posCode = posConversionMap.get(posCode);
			String morphCode = lemmaDataCandidateCells[2];
			morphCode = morphConversionMap.get(morphCode);
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

	private List<String> extractCollocUsages(Element collocGroupNode) {
		List<Element> collocUsageNodes = collocGroupNode.selectNodes(collocUsageExp);
		if (CollectionUtils.isEmpty(collocUsageNodes)) {
			return null;
		}
		List<String> collocUsages = new ArrayList<>();
		for (Element collocUsageNode : collocUsageNodes) {
			String collocUsage = collocUsageNode.getTextTrim();
			collocUsage = StringUtils.replaceEach(collocUsage, textCleanupEnitites, textCleanupEnityReplacements);
			collocUsages.add(collocUsage);
		}
		return collocUsages;
	}

	private void saveCollocations(
			Element collocGroupNode,
			CollocGroup collocGroup,
			String newWord,
			String newWordPosCode,
			Long lexemeId,
			List<String> collocUsages,
			List<CollocMember> collocMembers,
			Map<String, Map<Integer, Word>> wordMap,
			Map<Long, Map<Integer, LexemeMeaning>> meaningMap,
			String dataLang,
			Count repeatingCollocMemberCount,
			Count collocMemberOverloadGroupCount,
			Count collocMemberGuessingHomonymMeaningCount,
			Count collocMemberGuessedHomonymMeaningCount,
			Count successfulCollocateMatchCount,
			Count successfulCollocationMatchCount,
			boolean doReports) throws Exception {

		String collocPosGroupName = collocGroup.getCollocPosGroupName();
		String collocRelGroupName = collocGroup.getCollocRelGroupName();
		Long collocRelGroupId = collocGroup.getCollocRelGroupId();
		int collocGroupNum = collocGroup.getCollocGroupNum();

		if (CollectionUtils.isEmpty(collocMembers)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, collocPosGroupName, collocRelGroupName, "x:colg", "[" + collocGroupNum + "]", "puuduvad valiidsed kollokaadid");
			return;
		}

		final String prevWordCollocMemberName = "mse";
		final String nextWordCollocMemberName = "msj";

		Float frequency = null;
		Element collocFreqNode = (Element) collocGroupNode.selectSingleNode(collocFreqExp);
		if (collocFreqNode == null) {
			appendToReport(doReports, REPORT_MISSING_DATA, newWord, collocPosGroupName, collocRelGroupName, "x:colg", "[" + collocGroupNum + "]", "puudub sagedus");
		} else {
			try {
				frequency = Float.parseFloat(collocFreqNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, collocPosGroupName, collocRelGroupName, "x:colg", "[" + collocGroupNum + "]", "sagedusel sobimatu formaat");
			}
		}

		Float score = null;
		Element collocScoreNode = (Element) collocGroupNode.selectSingleNode(collocScoreExp);
		if (collocScoreNode == null) {
			appendToReport(doReports, REPORT_MISSING_DATA, newWord, collocPosGroupName, collocRelGroupName, "x:colg", "[" + collocGroupNum + "]", "puudub skoor");
		} else {
			try {
				score = Float.parseFloat(collocScoreNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord, collocPosGroupName, collocRelGroupName, "x:colg", "[" + collocGroupNum + "]", "skooril sobimatu formaat");
			}
		}

		List<List<CollocMember>> collocMembersPermutations = composeCollocMembersPermutations(collocMembers, collocMemberOverloadGroupCount);
		if (collocMembersPermutations.size() > 1) {
			collocMemberOverloadGroupCount.increment();
		}

		List<Long> bindedLexemeIds;
		StringBuffer collocBuf;

		for (List<CollocMember> collocMembersPermutation : collocMembersPermutations) {

			collocBuf = new StringBuffer();
			for (CollocMember collocMember : collocMembersPermutation) {
				String collocMemberName = collocMember.getName();
				String collocMemberForm = collocMember.getForm();
				String conjunct = collocMember.getConjunct();
				if (StringUtils.isNotBlank(conjunct)) {
					if (StringUtils.equals(collocMemberName, prevWordCollocMemberName)) {
						collocBuf.append(collocMemberForm);
						collocBuf.append(' ');
						collocBuf.append(conjunct);
					} else if (StringUtils.equals(collocMemberName, nextWordCollocMemberName)) {
						collocBuf.append(conjunct);
						collocBuf.append(' ');
						collocBuf.append(collocMemberForm);
					} else {
						//illegal case
					}
				} else {
					collocBuf.append(collocMemberForm);
					collocBuf.append(' ');
				}
			}
			String collocation = StringUtils.trim(collocBuf.toString());
			Long collocId = createCollocation(collocation, frequency, score, collocUsages);
			successfulCollocationMatchCount.increment();
			bindedLexemeIds = new ArrayList<>();
			bindedLexemeIds.add(lexemeId);

			for (CollocMember collocMember : collocMembersPermutation) {

				String collocMemberName = collocMember.getName();
				String collocMemberWord = collocMember.getWord();
				String collocMemberForm = collocMember.getForm();
				String collocMemberPosCode = collocMember.getPosCode();
				RefNum collocMemberRefNum = collocMember.getRefNum();

				Map<Integer, Word> homonymWordMap = wordMap.get(collocMemberWord);
				if (homonymWordMap == null) {
					// should be illegal case
					appendToReport(doReports, REPORT_UNKNOWN_COLLOC_MEMBER, newWord, collocation, collocMemberWord);
				} else {
					if (StringUtils.equals(collocMemberName, prevWordCollocMemberName)) {
						createLexemeCollocation(lexemeId, collocRelGroupId, collocId);
						successfulCollocateMatchCount.increment();
					} else if (StringUtils.equals(collocMemberName, nextWordCollocMemberName)) {
						createLexemeCollocation(lexemeId, collocRelGroupId, collocId);
						successfulCollocateMatchCount.increment();
					} else if (collocMemberRefNum == null) {
						//TODO just guessing here. should be determined by more intelligent logic
						if (homonymWordMap.size() == 1) {
							Word collocWordObj = homonymWordMap.get(1);
							Long collocWordId = collocWordObj.getId();
							List<LexemeMeaning> lexemeMeaningCandidates = getLexemeMeanings(collocWordId, collocMemberPosCode);
							if (CollectionUtils.isEmpty(lexemeMeaningCandidates)) {
								//none
							} else if (lexemeMeaningCandidates.size() == 1) {
								//success!
								LexemeMeaning collocLexemeMeaning = lexemeMeaningCandidates.get(0);
								Long collocLexemeId = collocLexemeMeaning.getLexemeId();
								if (bindedLexemeIds.contains(collocLexemeId)) {
									repeatingCollocMemberCount.increment();
									appendToReport(doReports, REPORT_REPEATING_COLLOC_MEMBER, newWord, collocation, collocMemberForm);
								} else {
									createLexemeCollocation(collocLexemeId, null, collocId);
									bindedLexemeIds.add(collocLexemeId);
									successfulCollocateMatchCount.increment();
								}
								collocMemberGuessedHomonymMeaningCount.increment();
							} else {
								//too many
							}
						}
						collocMemberGuessingHomonymMeaningCount.increment();
					} else {
						Integer collocMemberHomonymNr = collocMemberRefNum.getHomonymNr();
						Integer collocMemberMeaningNr = collocMemberRefNum.getMeaningNr();
						Word collocWordObj = homonymWordMap.get(collocMemberHomonymNr);
						Long collocWordId = collocWordObj.getId();
						Map<Integer, LexemeMeaning> levelMeaningMap = meaningMap.get(collocWordId);
						LexemeMeaning collocLexemeMeaning = levelMeaningMap.get(collocMemberMeaningNr);
						if (collocLexemeMeaning == null) {
							logger.debug("No lexeme/meaning match word \"{}\" homonym nr \"{}\" meaning nr \"{}\"",
									collocMemberWord, collocMemberHomonymNr, collocMemberMeaningNr);
							appendToReport(doReports, REPORT_ILLEGAL_LEMPOSVK_REF, newWord, collocation, collocMemberWord, "i=" + collocMemberHomonymNr, "tnr=" + collocMemberMeaningNr);
						} else {
							Long collocLexemeId = collocLexemeMeaning.getLexemeId();
							if (bindedLexemeIds.contains(collocLexemeId)) {
								repeatingCollocMemberCount.increment();
								appendToReport(doReports, REPORT_REPEATING_COLLOC_MEMBER, newWord, collocation, collocMemberForm);
							} else {
								createLexemeCollocation(collocLexemeId, null, collocId);
								bindedLexemeIds.add(collocLexemeId);
								successfulCollocateMatchCount.increment();
							}
						}
					}
				}
			}
		}
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

	private Long createCollocation(String collocation, Float frequency, Float score, List<String> collocUsages) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("value", collocation);
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

	private Long createLexemeCollocation(Long lexemeId, Long collocRelGroupId, Long collocId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		if (collocRelGroupId != null) {
			tableRowParamMap.put("rel_group_id", collocRelGroupId);
		}
		tableRowParamMap.put("collocation_id", collocId);
		Long lexCollocId = basicDbService.create(LEX_COLLOC, tableRowParamMap);
		return lexCollocId;
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

	class CollocMember {

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

	class CollocGroup {

		private String collocPosGroupName;

		private Long collocPosGroupId;

		private String collocRelGroupName;

		private Long collocRelGroupId;

		private int collocGroupNum;

		public CollocGroup(String collocPosGroupName, Long collocPosGroupId, String collocRelGroupName, Long collocRelGroupId, int collocGroupNum) {
			this.collocPosGroupName = collocPosGroupName;
			this.collocPosGroupId = collocPosGroupId;
			this.collocRelGroupName = collocRelGroupName;
			this.collocRelGroupId = collocRelGroupId;
			this.collocGroupNum = collocGroupNum;
		}

		public String getCollocPosGroupName() {
			return collocPosGroupName;
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

		public int getCollocGroupNum() {
			return collocGroupNum;
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

	class RefNum {

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
