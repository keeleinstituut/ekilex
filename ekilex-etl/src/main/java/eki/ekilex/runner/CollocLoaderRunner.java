package eki.ekilex.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.MabService;
import eki.ekilex.service.ReportComposer;

@Component
public class CollocLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(CollocLoaderRunner.class);

	private static final String REPORT_ILLEGAL_DATA = "illegal_data";

	private static final String REPORT_MISSING_DATA = "missing_data";

	private static final String REPORT_AMBIGUOUS_HOMONYM = "ambiguous_homonym";

	private static final String REPORT_MISSING_IN_MAB = "missing_in_mab";

	private static final String REPORT_AMBIGUOUS_WORD_MATCH = "ambiguous_word_match";

	private static final String REPORT_COLLOC_PAIR_UNMATCH = "colloc_pair_unmatch";

	private final String guidExp = "x:G";
	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordPosExp = "x:sl";
	private final String articleBodyExp = "x:S";
	private final String meaningBlockExp = "x:tp";
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

	private final String defaultWordMorphCode = "??";

	@Autowired
	private MabService mabService;

	private ReportComposer reportComposer;

	private Map<String, String> posConversionMap;

	private Map<String, String> morphConversionMap;

	@Override
	void initialise() throws Exception {

		posConversionMap = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
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
					REPORT_ILLEGAL_DATA, REPORT_MISSING_DATA, REPORT_AMBIGUOUS_HOMONYM, REPORT_MISSING_IN_MAB, REPORT_AMBIGUOUS_WORD_MATCH, REPORT_COLLOC_PAIR_UNMATCH);
		}
		if (!mabService.isMabLoaded()) {
			logger.error("MAB loading is absolutely required!");
			return;
		}

		dataLang = unifyLang(dataLang);
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Map<String, WordLexemeMeaning> wordLexemeMeaningIdMap = new HashMap<>();

		Element headerNode, contentNode, guidNode, wordNode, wordGroupNode, wordPosNode, collocRelGroupNameNode, collocRelGroupFreqNode, collocRelGroupScoreNode;
		List<Element> meaningBlockNodes, collocPosGroupNodes, collocRelGroupNodes, collocGroupNodes, collocUsageNodes;
		String guid, word, collocPosCode, collocUsage, collocRelGroupName;
		List<Paradigm> paradigms;
		List<Long> collocationIds;
		WordLexemeMeaning wordLexemeMeaning;

		Count ignoredArticleCount = new Count();
		Count successfulCollocationMatchCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				ignoredArticleCount.increment();
				continue;
			}

			guidNode = (Element) articleNode.selectSingleNode(guidExp);
			guid = guidNode.getTextTrim();
			guid = StringUtils.lowerCase(guid);

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);

			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			word = wordNode.getTextTrim();
			wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
			paradigms = null;
			wordLexemeMeaning = wordLexemeMeaningIdMap.get(word);
			if (wordLexemeMeaning == null) {
				boolean paradigmsExist = mabService.paradigmsExist(word);
				if (paradigmsExist) {
					boolean isSingleHomonym = mabService.isSingleHomonym(word);
					if (isSingleHomonym) {
						paradigms = mabService.getWordParadigms(word);
						wordLexemeMeaning = saveWordLexemeMeaning(word, dataLang, defaultWordMorphCode, guid, paradigms, dataset);
						extractAndSaveLexemePos(wordPosNode, wordLexemeMeaning);
						wordLexemeMeaningIdMap.put(word, wordLexemeMeaning);
					} else {
						appendToReport(doReports, REPORT_AMBIGUOUS_HOMONYM, "märksõnale", "-", word, "vastab mitu homonüümi");
						continue;
					}
				} else {
					appendToReport(doReports, REPORT_MISSING_IN_MAB, "märksõna", "-", word, "puudub MAB-st");
					wordLexemeMeaning = saveWordLexemeMeaning(word, dataLang, defaultWordMorphCode, guid, paradigms, dataset);
					extractAndSaveLexemePos(wordPosNode, wordLexemeMeaning);
					wordLexemeMeaningIdMap.put(word, wordLexemeMeaning);
				}
			}

			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);

			for (Element meaningBlockNode : meaningBlockNodes) {

				collocPosGroupNodes = meaningBlockNode.selectNodes(collocPosGroupExp);//x:colp/x:cmg

				for (Element colPosGroupNode : collocPosGroupNodes) {

					collocPosCode = colPosGroupNode.attributeValue(collocPosAttr);
					Long collocPosGroupId = createCollocPosGroup(wordLexemeMeaning.getLexemeId(), collocPosCode);

					collocRelGroupNodes = colPosGroupNode.selectNodes(collocRelGroupExp);//x:relg
					int collocRelGroupNum = 0;

					for (Element collocRelGroupNode : collocRelGroupNodes) {

						collocRelGroupNum++;
						collocRelGroupNameNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupNameExp);
						collocRelGroupName = collocRelGroupNameNode.getTextTrim();

						Float collocRelGroupFreq = null;
						collocRelGroupFreqNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupFreqExp);
						if (collocRelGroupFreqNode == null) {
							appendToReport(doReports, REPORT_MISSING_DATA, wordLexemeMeaning.getWord(), "x:relg", "[" + collocRelGroupNum + "]", "puudub sagedus");
						} else {
							try {
								collocRelGroupFreq = Float.parseFloat(collocRelGroupFreqNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, wordLexemeMeaning.getWord(), "x:relg", "[" + collocRelGroupNum + "]", "sagedusel sobimatu formaat");
							}
						}

						Float collocRelGroupScore = null;
						collocRelGroupScoreNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupScoreExp);
						if (collocRelGroupScoreNode == null) {
							appendToReport(doReports, REPORT_MISSING_DATA, wordLexemeMeaning.getWord(), "x:relg", "[" + collocRelGroupNum + "]", "puudub skoor");
						} else {
							try {
								collocRelGroupScore = Float.parseFloat(collocRelGroupScoreNode.getTextTrim());
							} catch (Exception e) {
								appendToReport(doReports, REPORT_ILLEGAL_DATA, wordLexemeMeaning.getWord(), "x:relg", "[" + collocRelGroupNum + "]", "skooril sobimatu formaat");
							}
						}

						Long collocRelGroupId = createCollocRelGroup(collocPosGroupId, collocRelGroupName, collocRelGroupFreq, collocRelGroupScore);

						collocGroupNodes = collocRelGroupNode.selectNodes(collocGroupExp);//x:colg
						int collocGroupNum = 0;

						for (Element collocGroupNode : collocGroupNodes) {

							collocGroupNum++;
							collocationIds = extractAndSaveCollocations(
									collocGroupNode, collocGroupNum, wordLexemeMeaning, wordLexemeMeaningIdMap,
									collocRelGroupId, dataset, dataLang, successfulCollocationMatchCount, doReports);

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

	private List<Long> extractAndSaveCollocations(
			Element collocGroupNode,
			int collocGroupNum,
			WordLexemeMeaning newWord,
			Map<String, WordLexemeMeaning> wordLexemeMeaningIdMap,
			Long collocRelGroupId,
			String dataset,
			String dataLang,
			Count successfulCollocationMatchCount,
			boolean doReports) throws Exception {

		List<Element> collocWordNodes = collocGroupNode.selectNodes(collocWordExp);
		List<Element> prevWordNodes = collocGroupNode.selectNodes(prevWordExp);
		List<Element> nextWordNodes = collocGroupNode.selectNodes(nextWordExp);

		List<Long> collocationIds = new ArrayList<>();
		List<CollocElement> collocWords = extractAndFindWord(null, collocWordNodes, "kollokaat", doReports);
		List<CollocElement> prevWords = extractAndFindWord(newWord.getWord(), prevWordNodes, "eelsõna", doReports);
		List<CollocElement> nextWords = extractAndFindWord(newWord.getWord(), nextWordNodes, "järelsõna", doReports);

		if (CollectionUtils.isEmpty(collocWords)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "puudub kollokatsioon");
			return collocationIds;
		}
		if (CollectionUtils.isEmpty(prevWords) && CollectionUtils.isEmpty(nextWords)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "puuduvad eel- ja järelsõna");
			return collocationIds;
		}
		if (CollectionUtils.isNotEmpty(prevWords) && CollectionUtils.isNotEmpty(nextWords)) {
			appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "esinevad korraga eel- ja järelsõna");
			return collocationIds;
		}

		Float frequency = null;
		Element collocFreqNode = (Element) collocGroupNode.selectSingleNode(collocFreqExp);
		if (collocFreqNode == null) {
			appendToReport(doReports, REPORT_MISSING_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "puudub sagedus");
		} else {
			try {
				frequency = Float.parseFloat(collocFreqNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "sagedusel sobimatu formaat");
			}
		}

		Float score = null;
		Element collocScoreNode = (Element) collocGroupNode.selectSingleNode(collocScoreExp);
		if (collocScoreNode == null) {
			appendToReport(doReports, REPORT_MISSING_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "puudub skoor");
		} else {
			try {
				score = Float.parseFloat(collocScoreNode.getTextTrim());
			} catch (Exception e) {
				appendToReport(doReports, REPORT_ILLEGAL_DATA, newWord.getWord(), "x:colg", "[" + collocGroupNum + "]", "skooril sobimatu formaat");
			}
		}

		List<Paradigm> paradigms;

		for (CollocElement collocWordObj : collocWords) {
			String collocWord = collocWordObj.getWord();
			String collocForm = collocWordObj.getForm();
			String morphCode = collocWordObj.getMorphCode();
			WordLexemeMeaning collocWordLexemeMeaning = wordLexemeMeaningIdMap.get(collocWord);
			if (collocWordLexemeMeaning == null) {
				paradigms = mabService.getWordParadigms(collocWord);
				collocWordLexemeMeaning = saveWordLexemeMeaning(collocWord, dataLang, morphCode, null, paradigms, dataset);
				wordLexemeMeaningIdMap.put(collocWord, collocWordLexemeMeaning);
			}
			Long collocLexemeId = collocWordLexemeMeaning.getLexemeId();
			if (CollectionUtils.isNotEmpty(prevWords)) {
				for (CollocElement prevWordObj : prevWords) {
					String prevForm = prevWordObj.getForm();
					String conjunct = prevWordObj.getConjunct();
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
				for (CollocElement nextWordObj : nextWords) {
					String nextForm = nextWordObj.getForm();
					String conjunct = nextWordObj.getConjunct();
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

	private List<CollocElement> extractAndFindWord(String newWord, List<Element> wordNodes, String source, boolean doReports) throws Exception {

		List<CollocElement> collocElements = new ArrayList<>();

		for (Element wordNode : wordNodes) {

			String form = wordNode.getTextTrim();
			String word = null;
			String morphCode = defaultWordMorphCode;
			String conjunct = wordNode.attributeValue("jv");
			String lemmaDataAttr = wordNode.attributeValue("lemposvk");
			String lemmaDataLog;

			if (StringUtils.isBlank(lemmaDataAttr)) {
				lemmaDataLog = "-";
				if (newWord == null) {
					boolean isKnownForm = mabService.isKnownForm(form);
					if (isKnownForm) {
						boolean isSingleWordForm = mabService.isSingleWordForm(form);
						if (isSingleWordForm) {
							String wordCandidate = mabService.getSingleWordFormWord(form);
							boolean isSingleHomonym = mabService.isSingleHomonym(wordCandidate);
							if (isSingleHomonym) {
								word = wordCandidate;
							} else {
								appendToReport(doReports, REPORT_AMBIGUOUS_HOMONYM, source, lemmaDataLog, wordCandidate, "vastab mitu homonüümi");
								continue;
							}
						} else {
							List<String> formWords = mabService.getFormWords(form);
							appendToReport(doReports, REPORT_AMBIGUOUS_WORD_MATCH, source, lemmaDataLog, form, "vormile vastab mitu keelendit " + formWords);
							continue;
						}
					} else {
						appendToReport(doReports, REPORT_MISSING_IN_MAB, source + " vorm", lemmaDataLog, form, "puudub MAB-st");
						continue;
					}
				} else if (StringUtils.equals(newWord, form)) {
					word = form;
				} else {
					boolean isKnownForm = mabService.isKnownForm(form);
					if (isKnownForm) {
						List<String> formWords = mabService.getFormWords(form);
						if (!formWords.contains(newWord)) {
							appendToReport(doReports, REPORT_COLLOC_PAIR_UNMATCH, source, lemmaDataLog, form, "vormi järgi ei leidu artikli märksõna", newWord);
							continue;
						}
						word = formWords.get(0);
					} else {
						word = newWord;
					}
				}
			} else {
				lemmaDataLog = lemmaDataAttr;
				boolean isMultipleCandidates = StringUtils.contains(lemmaDataAttr, '|');
				if (isMultipleCandidates) {
					String[] lemmaDataCandidates = StringUtils.split(lemmaDataAttr, '|');
					List<String> wordCandidates = Arrays.stream(lemmaDataCandidates).map(lemmaDataCandidate -> {
							String wordCandidate = StringUtils.split(lemmaDataCandidate, ':')[0];
							wordCandidate = StringUtils.remove(wordCandidate, '+');
							return wordCandidate;
						}).collect(Collectors.toList());
					appendToReport(doReports, REPORT_AMBIGUOUS_HOMONYM, source, lemmaDataLog, wordCandidates.toString(), "vastab mitu keelendit või homonüümi");
					continue;
				} else {
					String[] lemmaDataParts = StringUtils.split(lemmaDataAttr, ':');
					word = lemmaDataParts[0];
					word = StringUtils.remove(word, '+');//deal with compound words later
					morphCode = lemmaDataParts[2];
					morphCode = morphConversionMap.get(morphCode);
					if (StringUtils.isBlank(morphCode)) {
						morphCode = defaultWordMorphCode;
					}
					if ((newWord != null) && !StringUtils.equals(newWord, word)) {
						appendToReport(doReports, REPORT_COLLOC_PAIR_UNMATCH, source, lemmaDataLog, form, "soovitatud lemma ei ole artikli märksõna", newWord);
						continue;
					}
					boolean isKnownForm = mabService.isKnownForm(word);
					if (!isKnownForm) {
						appendToReport(doReports, REPORT_MISSING_IN_MAB, source + " sõna", lemmaDataLog, word, "puudub MAB-st");
					}
				}
			}
			if (word == null) {
				continue;
			}
			CollocElement collocElement = new CollocElement(word, form, morphCode, conjunct);
			collocElements.add(collocElement);
		}
		return collocElements;
	}

	private void extractAndSaveLexemePos(Element wordPosNode, WordLexemeMeaning wordLexemeMeaning) throws Exception {
		if (wordPosNode != null) {
			String wordPosCode = wordPosNode.getTextTrim();
			String convertedPosCode = posConversionMap.get(wordPosCode);
			Long lexemeId = wordLexemeMeaning.getLexemeId();
			createLexemePos(lexemeId, convertedPosCode);
		}
	}

	private void createLexemePos(Long lexemeId, String posCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("pos_code", posCode);
		basicDbService.create(LEXEME_POS, tableRowParamMap);
	}

	private WordLexemeMeaning saveWordLexemeMeaning(String word, String dataLang, String morphCode, String guid, List<Paradigm> paradigms, String dataset) throws Exception {

		int homonymNr = getWordMaxHomonymNr(word, dataLang);
		homonymNr++;
		Word wordObj = new Word(word, dataLang, homonymNr, morphCode, guid);
		Long wordId = saveWord(wordObj, paradigms, dataset, null);
		Long meaningId = createMeaning();
		Lexeme lexemeObj = new Lexeme();
		lexemeObj.setWordId(wordId);
		lexemeObj.setMeaningId(meaningId);
		Long lexemeId = createLexeme(lexemeObj, dataset);
		WordLexemeMeaning wordLexemeMeaning = new WordLexemeMeaning(word, wordId, lexemeId, meaningId);
		return wordLexemeMeaning;
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

		private String conjunct;

		public CollocElement(String word, String form, String morphCode, String conjunct) {
			this.word = word;
			this.form = form;
			this.morphCode = morphCode;
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

		public String getConjunct() {
			return conjunct;
		}

	}

	class WordLexemeMeaning {

		private String word;

		private Long wordId;

		private Long lexemeId;

		private Long meaningId;

		public WordLexemeMeaning(String word, Long wordId, Long lexemeId, Long meaningId) {
			this.word = word;
			this.wordId = wordId;
			this.lexemeId = lexemeId;
			this.meaningId = meaningId;
		}

		public String getWord() {
			return word;
		}

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
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
