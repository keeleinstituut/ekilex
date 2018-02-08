package eki.ekilex.runner;

import java.util.ArrayList;
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
	private final String relationGroupExp = "x:relg";
	private final String collocGroupExp = "x:colg";
	private final String collocWordExp = "x:col";
	private final String prevWordExp = "x:mse";
	private final String nextWordExp = "x:msj";
	private final String collocUsageExp = "x:cng/x:cn[not(@x:as='ab')]";

	@Autowired
	private MabService mabService;

	private ReportComposer reportComposer;

	private Map<String, String> posCodes;

	@Override
	void initialise() throws Exception {

		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String dataset, boolean doReports) throws Exception {

		logger.debug("Starting loading collocates...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("kol loader report",
					REPORT_ILLEGAL_DATA, REPORT_AMBIGUOUS_HOMONYM, REPORT_MISSING_IN_MAB, REPORT_AMBIGUOUS_WORD_MATCH, REPORT_COLLOC_PAIR_UNMATCH);
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

		Element headerNode, contentNode, guidNode, wordNode, wordPosNode;
		List<Element> wordGroupNodes, meaningBlockNodes, collocPosGroupNodes, relationGroupNodes, collocGroupNodes, prevWordNodes, collocWordNodes, nextWordNodes, collocUsageNodes;
		String guid, word, wordPosCode, collocPosCode, convertedPosCode, collocUsage;
		List<String> newWords;
		List<Paradigm> paradigms;
		List<Long> collocationIds;
		Long lexemeId;
		StringBuffer logBuf;

		Count ignoredArticleCount = new Count();
		Count illegalPosCount = new Count();
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
			wordGroupNodes = headerNode.selectNodes(wordGroupExp);

			newWords = new ArrayList<>();
			for (Element wordGroupNode : wordGroupNodes) {
				wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				word = wordNode.getTextTrim();
				wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
				wordPosCode = null;
				if (wordLexemeMeaningIdMap.containsKey(word)) {
					//log duplicate?
					//REPORT_ILLEGAL_DATA
					continue;
				}
				boolean paradigmsExist = mabService.paradigmsExist(word);
				if (paradigmsExist) {
					boolean isSingleHomonym = mabService.isSingleHomonym(word);
					if (isSingleHomonym) {
						paradigms = mabService.getWordParadigms(word);
						WordLexemeMeaning wordLexemeMeaning = saveWordLexemeMeaning(word, dataLang, guid, paradigms, dataset);
						wordLexemeMeaningIdMap.put(word, wordLexemeMeaning);
						newWords.add(word);
						if (wordPosNode != null) {
							wordPosCode = wordPosNode.getTextTrim();
							convertedPosCode = posCodes.get(wordPosCode);
							lexemeId = wordLexemeMeaning.getLexemeId();
							saveLexemePos(lexemeId, convertedPosCode);
						}
					} else {
						if (doReports) {
							logBuf = new StringBuffer();
							logBuf.append("märksõnale");
							logBuf.append(CSV_SEPARATOR);
							logBuf.append(word);
							logBuf.append(CSV_SEPARATOR);
							logBuf.append("vastab mitu homonüümi");
							String logRow = logBuf.toString();
							reportComposer.append(REPORT_AMBIGUOUS_HOMONYM, logRow);
						}
						continue;
					}
				} else {
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append("märksõna");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(word);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("puudub MAB-st");
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_MISSING_IN_MAB, logRow);
					}
					continue;
				}
			}
			if (CollectionUtils.isEmpty(newWords)) {
				ignoredArticleCount.increment();
				continue;
			}

			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);

			for (Element meaningBlockNode : meaningBlockNodes) {

				collocPosGroupNodes = meaningBlockNode.selectNodes(collocPosGroupExp);

				for (Element colPosGroupNode : collocPosGroupNodes) {

					collocPosCode = colPosGroupNode.attributeValue(collocPosAttr);
					convertedPosCode = posCodes.get(collocPosCode);
					if (StringUtils.isBlank(convertedPosCode)) {
						illegalPosCount.increment();
					}

					relationGroupNodes = colPosGroupNode.selectNodes(relationGroupExp);

					for (Element relationGroupNode : relationGroupNodes) {

						collocGroupNodes = relationGroupNode.selectNodes(collocGroupExp);

						for (Element collocGroupNode : collocGroupNodes) {

							collocWordNodes = collocGroupNode.selectNodes(collocWordExp);
							prevWordNodes = collocGroupNode.selectNodes(prevWordExp);
							nextWordNodes = collocGroupNode.selectNodes(nextWordExp);

							collocationIds = handleAndSaveCollocations(
									newWords, wordLexemeMeaningIdMap,
									collocWordNodes, prevWordNodes, nextWordNodes,
									convertedPosCode, dataset, dataLang,
									successfulCollocationMatchCount, doReports);

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
		logger.debug("Found {} illegal POS codes", illegalPosCount.getValue());
		logger.debug("Found {} successful collocation matches", successfulCollocationMatchCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private List<Long> handleAndSaveCollocations(
			List<String> newWords, Map<String, WordLexemeMeaning> wordLexemeMeaningIdMap,
			List<Element> collocWordNodes, List<Element> prevWordNodes, List<Element> nextWordNodes,
			String collocPosCode, String dataset, String dataLang,
			Count successfulCollocationMatchCount,
			boolean doReports) throws Exception {

		List<Long> collocationIds = new ArrayList<>();
		List<SingleWordForm> collocWords = extractAndFindWord(null, collocWordNodes, "kollokaat", doReports);
		List<SingleWordForm> prevWords = extractAndFindWord(newWords, prevWordNodes, "eelsõna", doReports);
		List<SingleWordForm> nextWords = extractAndFindWord(newWords, nextWordNodes, "järelsõna", doReports);

		if (CollectionUtils.isEmpty(collocWords)) {
			//log missing colloc?
			return collocationIds;
		}
		if (CollectionUtils.isEmpty(prevWords) && CollectionUtils.isEmpty(nextWords)) {
			//log missing prev and next?
			return collocationIds;
		}
		if (CollectionUtils.isNotEmpty(prevWords) && CollectionUtils.isNotEmpty(nextWords)) {
			//log colloc overload?
			return collocationIds;
		}
		List<Paradigm> paradigms;

		for (SingleWordForm collocWordObj : collocWords) {
			String collocWord = collocWordObj.getWord();
			String collocForm = collocWordObj.getForm();
			WordLexemeMeaning collocWordLexemeMeaning = wordLexemeMeaningIdMap.get(collocWord);
			if (collocWordLexemeMeaning == null) {
				paradigms = mabService.getWordParadigms(collocWord);
				collocWordLexemeMeaning = saveWordLexemeMeaning(collocWord, dataLang, null, paradigms, dataset);
				wordLexemeMeaningIdMap.put(collocWord, collocWordLexemeMeaning);
				if (StringUtils.isNotBlank(collocPosCode)) {
					Long collocLexemeId = collocWordLexemeMeaning.getLexemeId();
					saveLexemePos(collocLexemeId, collocPosCode);
				}
			}
			Long collocLexemeId = collocWordLexemeMeaning.getLexemeId();
			if (CollectionUtils.isNotEmpty(prevWords)) {
				for (SingleWordForm prevWordObj : prevWords) {
					String prevWord = prevWordObj.getWord();
					String prevForm = prevWordObj.getForm();
					WordLexemeMeaning prevWordLexemeMeaning = wordLexemeMeaningIdMap.get(prevWord);
					Long prevLexemeId = prevWordLexemeMeaning.getLexemeId();
					String collocation = prevForm + ' ' + collocForm;
					Long collocId = createCollocation(prevLexemeId, collocLexemeId, collocation);
					collocationIds.add(collocId);
					successfulCollocationMatchCount.increment();
				}
			} else if (CollectionUtils.isNotEmpty(nextWords)) {
				for (SingleWordForm nextWordObj : nextWords) {
					String nextWord = nextWordObj.getWord();
					String nextForm = nextWordObj.getForm();
					WordLexemeMeaning nextWordLexemeMeaning = wordLexemeMeaningIdMap.get(nextWord);
					Long nextLexemeId = nextWordLexemeMeaning.getLexemeId();
					String collocation = collocForm + ' ' + nextForm;
					Long collocId = createCollocation(nextLexemeId, collocLexemeId, collocation);
					collocationIds.add(collocId);
					successfulCollocationMatchCount.increment();
				}
			}
		}
		return collocationIds;
	}

	private List<SingleWordForm> extractAndFindWord(List<String> newWords, List<Element> wordNodes, String source, boolean doReports) throws Exception {

		List<SingleWordForm> singleWordForms = new ArrayList<>();
		StringBuffer logBuf;

		for (Element wordNode : wordNodes) {
			String form = wordNode.getTextTrim();
			String word = null;
			if (newWords != null) {
				if (newWords.contains(form)) {
					word = form;
				} else {
					boolean isKnownForm = mabService.isKnownForm(form);
					if (isKnownForm) {
						List<String> formWords = mabService.getFormWords(form);
						List<String> matchingWithWords = new ArrayList<>(CollectionUtils.intersection(newWords, formWords));
						if (CollectionUtils.isEmpty(matchingWithWords)) {
							if (doReports) {
								logBuf = new StringBuffer();
								logBuf.append(source);
								logBuf.append(CSV_SEPARATOR);
								logBuf.append(form);
								logBuf.append(CSV_SEPARATOR);
								logBuf.append("vormi järgi ei leidu artikli märksõna");
								logBuf.append(CSV_SEPARATOR);
								logBuf.append(newWords);
								String logRow = logBuf.toString();
								reportComposer.append(REPORT_COLLOC_PAIR_UNMATCH, logRow);
							}
							continue;
						}
						word = matchingWithWords.get(0);
					} else {
						if (doReports) {
							logBuf = new StringBuffer();
							logBuf.append(source);
							logBuf.append(" vorm");							
							logBuf.append(CSV_SEPARATOR);
							logBuf.append(form);
							logBuf.append(CSV_SEPARATOR);
							logBuf.append("puudub MAB-st");
							String logRow = logBuf.toString();
							reportComposer.append(REPORT_MISSING_IN_MAB, logRow);
						}
						continue;
					}
				}
			} else {
				boolean isKnownForm = mabService.isKnownForm(form);
				if (isKnownForm) {
					boolean isSingleWordForm = mabService.isSingleWordForm(form);
					if (isSingleWordForm) {
						String wordCandidate = mabService.getSingleWordFormWord(form);
						boolean isSingleHomonym = mabService.isSingleHomonym(wordCandidate);
						if (isSingleHomonym) {
							word = wordCandidate;
						} else {
							if (doReports) {
								logBuf = new StringBuffer();
								logBuf.append(source);
								logBuf.append(CSV_SEPARATOR);
								logBuf.append(wordCandidate);
								logBuf.append(CSV_SEPARATOR);
								logBuf.append("vastab mitu homonüümi");
								String logRow = logBuf.toString();
								reportComposer.append(REPORT_AMBIGUOUS_HOMONYM, logRow);
							}
							continue;
						}
					} else {
						if (doReports) {
							List<String> formWords = mabService.getFormWords(form);
							logBuf = new StringBuffer();
							logBuf.append(source);
							logBuf.append(CSV_SEPARATOR);
							logBuf.append(form);
							logBuf.append(CSV_SEPARATOR);
							logBuf.append("vormile vastab mitu keelendit");
							logBuf.append(formWords);
							String logRow = logBuf.toString();
							reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
						}
						continue;
					}
				} else {
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(source);
						logBuf.append(" vorm");	
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(form);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("puudub MAB-st");
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_MISSING_IN_MAB, logRow);
					}
					continue;
				}
			}
			if (word == null) {
				continue;
			}
			SingleWordForm singleWordForm = new SingleWordForm(word, form);
			singleWordForms.add(singleWordForm);
		}
		return singleWordForms;
	}

	private void saveLexemePos(Long lexemeId, String posCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("pos_code", posCode);
		basicDbService.create(LEXEME_POS, tableRowParamMap);
	}

	private WordLexemeMeaning saveWordLexemeMeaning(String word, String dataLang, String guid, List<Paradigm> paradigms, String dataset) throws Exception {

		int homonymNr = getWordMaxHomonymNr(word, dataLang);
		homonymNr++;
		Word wordObj = new Word(word, dataLang, homonymNr, guid);
		Long wordId = saveWord(wordObj, paradigms, dataset, null);
		Long meaningId = createMeaning();
		Lexeme lexemeObj = new Lexeme();
		lexemeObj.setWordId(wordId);
		lexemeObj.setMeaningId(meaningId);
		Long lexemeId = createLexeme(lexemeObj, dataset);
		WordLexemeMeaning wordLexemeMeaning = new WordLexemeMeaning(wordId, lexemeId, meaningId);
		return wordLexemeMeaning;
	}

	private Long createCollocation(Long lexemeId1, Long lexemeId2, String collocation) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme1_id", lexemeId1);
		tableRowParamMap.put("lexeme2_id", lexemeId2);
		tableRowParamMap.put("value", collocation);
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

	class SingleWordForm {

		private String word;

		private String form;

		public SingleWordForm(String word, String form) {
			this.word = word;
			this.form = form;
		}

		public String getWord() {
			return word;
		}

		public String getForm() {
			return form;
		}
	}

	class WordLexemeMeaning {

		private Long wordId;

		private Long lexemeId;

		private Long meaningId;

		public WordLexemeMeaning(Long wordId, Long lexemeId, Long meaningId) {
			this.wordId = wordId;
			this.lexemeId = lexemeId;
			this.meaningId = meaningId;
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
