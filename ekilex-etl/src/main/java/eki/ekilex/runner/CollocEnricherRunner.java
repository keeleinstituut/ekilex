package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.ekilex.service.ReportComposer;

//obsolete logic
@Deprecated
@Component
public class CollocEnricherRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(CollocEnricherRunner.class);

	private static final String SQL_SELECT_LEXEME_BY_DATASET_AND_LANG = "sql/select_lexeme_by_dataset_and_lang.sql";

	private static final String SQL_EXISTS_WORD_WORD = "sql/exists_word_word.sql";

	private static final String SQL_EXISTS_WORD_FORM = "sql/exists_word_form.sql";

	private static final String SQL_EXISTS_LEXEME_POS = "sql/exists_lexeme_pos.sql";

	private static final String REPORT_ILLEGAL_DATA = "illegal_data";

	private static final String REPORT_MISSING_WORD = "missing_word";

	private static final String REPORT_AMBIGUOUS_MEANING_MATCH = "ambiguous_meaning_match";

	private static final String REPORT_AMBIGUOUS_WORD_MATCH = "ambiguous_word_match";

	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordPosExp = "x:sl";
	private final String articleBodyExp = "x:S";

	private ReportComposer reportComposer;

	private String sqlSelectLexemeByDatasetAndLang;

	private String sqlExistsWordWord;

	private String sqlExistsWordForm;

	private String sqlExistsLexemePos;

	private Map<String, String> posCodes;

	@Override
	String getDataset() {
		return "kol";
	}

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_BY_DATASET_AND_LANG);
		sqlSelectLexemeByDatasetAndLang = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_EXISTS_WORD_WORD);
		sqlExistsWordWord = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_EXISTS_WORD_FORM);
		sqlExistsWordForm = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_EXISTS_LEXEME_POS);
		sqlExistsLexemePos = getContent(resourceFileInputStream);

		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String targetDataset, boolean doReports) throws Exception {

		logger.debug("Starting loading collocates...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("kol enricher report",
					REPORT_ILLEGAL_DATA, REPORT_MISSING_WORD, REPORT_AMBIGUOUS_MEANING_MATCH, REPORT_AMBIGUOUS_WORD_MATCH);
		}

		dataLang = unifyLang(dataLang);
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Element headerNode, contentNode, wordNode, wordPosNode;
		List<Node> wordGroupNodes, meaningBlockNodes, collocPosGroupNodes, relationGroupNodes, collocGroupNodes, prevWordNodes, collocWordNodes, nextWordNodes;
		String word, wordPosCode, collocPosCode;
		List<LexemeMeaningData> lexemes;
		WordLexemeData wordLexemeData;
		Map<String, WordLexemeData> wordMap;
		StringBuffer logBuf;

		Count illegalDataCount = new Count();
		Count missingDataCount = new Count();
		Count ambiguousDataCount = new Count();
		Count potentialCollocationCount = new Count();
		Count successfulCollocationMatchCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());

		for (Node articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNodes = headerNode.selectNodes(wordGroupExp);

			wordMap = new HashMap<>();
			for (Node wordGroupNode : wordGroupNodes) {
				wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				word = wordNode.getTextTrim();
				wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
				if (wordPosNode == null) {
					illegalDataCount.increment();
					logger.warn("No POS specified for word {}", word);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(word);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnal puudub sõnaliik");
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_ILLEGAL_DATA, logRow);
					}
					//continue;
					wordPosCode = null;
				} else {
					wordPosCode = wordPosNode.getTextTrim();
					wordPosCode = posCodes.get(wordPosCode);
				}
				lexemes = getLexemes(targetDataset, wordPosCode, word, null, dataLang);
				wordLexemeData = new WordLexemeData(word, wordPosCode, lexemes);
				wordMap.put(word, wordLexemeData);
			}
			if (MapUtils.isEmpty(wordMap)) {
				continue;
			}

			meaningBlockNodes = contentNode.selectNodes("x:tp");

			for (Node meaningBlockNode : meaningBlockNodes) {

				collocPosGroupNodes = meaningBlockNode.selectNodes("x:colp/x:cmg");

				for (Node colPosGroupNode : collocPosGroupNodes) {

					collocPosCode = ((Element)colPosGroupNode).attributeValue("csl");
					collocPosCode = posCodes.get(collocPosCode);

					relationGroupNodes = colPosGroupNode.selectNodes("x:relg");

					for (Node relationGroupNode : relationGroupNodes) {

						collocGroupNodes = relationGroupNode.selectNodes("x:colg");

						for (Node collocGroupNode : collocGroupNodes) {

							collocWordNodes = collocGroupNode.selectNodes("x:col");
							prevWordNodes = collocGroupNode.selectNodes("x:mse");
							nextWordNodes = collocGroupNode.selectNodes("x:msj");
							//x:cng/x:cn - usages
							createCollocations(
									wordMap, collocWordNodes, prevWordNodes, nextWordNodes, collocPosCode, targetDataset, dataLang,
									illegalDataCount, missingDataCount, ambiguousDataCount, potentialCollocationCount, successfulCollocationMatchCount, doReports);
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

		logger.debug("Found {} potential collocations", potentialCollocationCount.getValue());
		logger.debug("Found {} successful collocation matches", successfulCollocationMatchCount.getValue());
		logger.debug("Found {} illegal source data cases", illegalDataCount.getValue());
		logger.debug("Found {} missing data references", missingDataCount.getValue());
		logger.debug("Found {} ambiguous data references", ambiguousDataCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private void createCollocations(
			Map<String, WordLexemeData> wordMap,
			List<Node> collocWordNodes,
			List<Node> prevWordNodes,
			List<Node> nextWordNodes,
			String collocPosCode,
			String targetDataset,
			String dataLang,
			Count illegalDataCount,
			Count missingDataCount,
			Count ambiguousDataCount,
			Count potentialCollocationCount,
			Count successfulCollocationMatchCount,
			boolean doReports) throws Exception {

		List<String> words = new ArrayList<>(wordMap.keySet());
		List<String> wordPos = wordMap.values().stream().map(word -> word.getPosCode()).collect(Collectors.toList());
		List<String> collocWords = collectTextValues(collocWordNodes);
		List<String> prevWords = collectTextValues(prevWordNodes);
		List<String> nextWords = collectTextValues(nextWordNodes);
		boolean collocPosCodeExists = StringUtils.isNotBlank(collocPosCode);
		String loggedCollocPosCode = collocPosCodeExists ? collocPosCode : "-";
		List<LexemeMeaningData> lexemes;
		LexemeMeaningData lexeme;
		StringBuffer logBuf;
		int meaningCount;

		int potentialCollocationCounter = Math.max(collocWordNodes.size(), 1) * Math.max(prevWordNodes.size(), 1) * Math.max(nextWordNodes.size(), 1);
		potentialCollocationCount.increment(potentialCollocationCounter);

		if (CollectionUtils.isEmpty(collocWords)) {
			illegalDataCount.increment();
			logger.warn("No collocates in this block for word(s) {}", words);
			if (doReports) {
				logBuf = new StringBuffer();
				logBuf.append(words);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("kollokatsiooni grupp ei sisalda kollokaate");
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_DATA, logRow);
			}
			return;
		}

		Map<String, LexemeMeaningData> collocWordLexemeMap = new HashMap<>();
		for (String collocWord : collocWords) {
			// first attempt with pos if exists
			lexemes = getLexemes(targetDataset, collocPosCode, null, collocWord, dataLang);
			if (CollectionUtils.isEmpty(lexemes) && collocPosCodeExists) {
				// second attempt without pos
				lexemes = getLexemes(targetDataset, null, null, collocWord, dataLang);
			}
			if (CollectionUtils.isEmpty(lexemes)) {
				missingDataCount.increment();
				logger.warn("Could not find collocate word {}", collocWord);
				if (doReports) {
					logBuf = new StringBuffer();
					logBuf.append(words);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("kollokaat");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("sõna ei leidu");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocWord);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(loggedCollocPosCode);
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_MISSING_WORD, logRow);
				}
				continue;
			} else if (lexemes.size() > 1) {
				ambiguousDataCount.increment();
				logger.warn("Many homonyms match collocate word {}", collocWord);
				if (doReports) {
					logBuf = new StringBuffer();
					logBuf.append(words);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("kollokaat");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("sõnale on vasteid ");
					logBuf.append(lexemes.size());
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocWord);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(loggedCollocPosCode);
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
				}
				continue;
			}
			lexeme = lexemes.get(0);
			meaningCount = lexeme.getMeaningIds().length;
			if (meaningCount > 1) {
				ambiguousDataCount.increment();
				logger.warn("Many meanings for collocate word {}", collocWord);
				if (doReports) {
					logBuf = new StringBuffer();
					logBuf.append(words);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("kollokaat");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("sõnal on tähendusi ");
					logBuf.append(meaningCount);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocWord);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(loggedCollocPosCode);
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_AMBIGUOUS_MEANING_MATCH, logRow);
				}
				continue;
			}
			collocWordLexemeMap.put(collocWord, lexeme);
		}
		if (MapUtils.isEmpty(collocWordLexemeMap)) {
			return;
		}

		if (CollectionUtils.isEmpty(prevWords) && CollectionUtils.isEmpty(nextWords)) {
			illegalDataCount.increment();
			logger.warn("No prev nor next words were specified in this block for word(s) {}", words);
			if (doReports) {
				logBuf = new StringBuffer();
				logBuf.append(words);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("kollokatsiooni grupp ei sisalda kollokaadi paarilist");
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_DATA, logRow);
			}
			return;

		} else if (CollectionUtils.isNotEmpty(prevWords) && CollectionUtils.isEmpty(nextWords)) {

			Map<String, LexemeMeaningData> prevWordLexemeMap = new HashMap<>();
			for (String prevWord : prevWords) {
				WordLexemeData wordData = wordMap.get(prevWord);
				lexemes = null;
				if (wordData == null) {
					// first attempt with pos if exists
					boolean posExists = false;
					for (WordLexemeData wordDataCand : wordMap.values()) {
						if (StringUtils.isNotBlank(wordDataCand.getPosCode())) {
							posExists = true;
						}
						lexemes = getLexemes(targetDataset, wordDataCand.getPosCode(), wordDataCand.getWord(), prevWord, dataLang);
						if (CollectionUtils.isNotEmpty(lexemes)) {
							break;
						}
					}
					// second attempt without pos
					if (CollectionUtils.isEmpty(lexemes) && posExists) {
						for (WordLexemeData wordDataCand : wordMap.values()) {
							lexemes = getLexemes(targetDataset, null, wordDataCand.getWord(), prevWord, dataLang);
							if (CollectionUtils.isNotEmpty(lexemes)) {
								break;
							}
						}
					}
				} else {
					lexemes = wordData.getLexemes();
				}
				if (CollectionUtils.isEmpty(lexemes)) {
					missingDataCount.increment();
					logger.warn("Could not find previous word {}", prevWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("eel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõna ei leidu");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(prevWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordPos);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_MISSING_WORD, logRow);
					}
					continue;
				} else if (lexemes.size() > 1) {
					ambiguousDataCount.increment();
					logger.warn("Many homonyms match previous word {}", prevWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("eel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnale on vasteid ");
						logBuf.append(lexemes.size());
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(prevWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordPos);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
					}
					continue;
				}
				lexeme = lexemes.get(0);
				meaningCount = lexeme.getMeaningIds().length;
				if (meaningCount > 1) {
					ambiguousDataCount.increment();
					logger.warn("Many meanings for previous word {}", prevWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("eel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnal on tähendusi ");
						logBuf.append(meaningCount);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(prevWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordPos);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_AMBIGUOUS_MEANING_MATCH, logRow);
					}
					continue;
				}
				prevWordLexemeMap.put(prevWord, lexeme);
			}
			if (MapUtils.isEmpty(prevWordLexemeMap)) {
				return;
			}
			for (String prevWord : prevWords) {
				LexemeMeaningData prevWordLexeme = prevWordLexemeMap.get(prevWord);
				if (prevWordLexeme == null) {
					continue;
				}
				for (String collocWord : collocWords) {
					LexemeMeaningData collocWordLexeme = collocWordLexemeMap.get(collocWord);
					if (collocWordLexeme == null) {
						continue;
					}
					successfulCollocationMatchCount.increment();
				}
			}

		} else if (CollectionUtils.isEmpty(prevWords) && CollectionUtils.isNotEmpty(nextWords)) {

			Map<String, LexemeMeaningData> nextWordLexemeMap = new HashMap<>();
			for (String nextWord : nextWords) {
				WordLexemeData wordData = wordMap.get(nextWord);
				lexemes = null;
				if (wordData == null) {
					// first attempt with pos if exists
					boolean posExists = false;
					for (WordLexemeData wordDataCand : wordMap.values()) {
						if (StringUtils.isNotBlank(wordDataCand.getPosCode())) {
							posExists = true;
						}
						lexemes = getLexemes(targetDataset, wordDataCand.getPosCode(), wordDataCand.getWord(), nextWord, dataLang);
						if (CollectionUtils.isNotEmpty(lexemes)) {
							break;
						}
					}
					// second attempt without pos
					if (CollectionUtils.isEmpty(lexemes) && posExists) {
						for (WordLexemeData wordDataCand : wordMap.values()) {
							lexemes = getLexemes(targetDataset, null, wordDataCand.getWord(), nextWord, dataLang);
							if (CollectionUtils.isNotEmpty(lexemes)) {
								break;
							}
						}
					}
				} else {
					lexemes = wordData.getLexemes();
				}
				if (CollectionUtils.isEmpty(lexemes)) {
					missingDataCount.increment();
					logger.warn("Could not find next word {}", nextWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("järel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõna ei leidu");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(nextWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordPos);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_MISSING_WORD, logRow);
					}
					continue;
				} else if (lexemes.size() > 1) {
					ambiguousDataCount.increment();
					logger.warn("Many homonyms match next word {}", nextWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("järel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnale on vasteid ");
						logBuf.append(lexemes.size());
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(nextWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordPos);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
					}
					continue;
				}
				lexeme = lexemes.get(0);
				meaningCount = lexeme.getMeaningIds().length;
				if (meaningCount > 1) {
					ambiguousDataCount.increment();
					logger.warn("Many meanings for next word {}", nextWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("järel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnal on tähendusi ");
						logBuf.append(meaningCount);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(nextWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordPos);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_AMBIGUOUS_MEANING_MATCH, logRow);
					}
					continue;
				}
				nextWordLexemeMap.put(nextWord, lexeme);
			}
			if (MapUtils.isEmpty(nextWordLexemeMap)) {
				return;
			}
			for (String nextWord : nextWords) {
				LexemeMeaningData nextWordLexeme = nextWordLexemeMap.get(nextWord);
				if (nextWordLexeme == null) {
					continue;
				}
				for (String collocWord : collocWords) {
					LexemeMeaningData collocWordLexeme = collocWordLexemeMap.get(collocWord);
					if (collocWordLexeme == null) {
						continue;
					}
					successfulCollocationMatchCount.increment();
				}
			}

		} else {
			illegalDataCount.increment();
			logger.warn("Illegal case of collocation overload {} - {} - {}", prevWords, collocWords, nextWords);
			if (doReports) {
				logBuf = new StringBuffer();
				logBuf.append(words);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("kollokatsiooni grupis korraga eel- ja järelsõnad");
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_DATA, logRow);
			}
		}
	}

	private List<String> collectTextValues(List<Node> nodes) {
		List<String> textValues = nodes.stream().map(node -> ((Element)node).getTextTrim()).collect(Collectors.toList());
		return new ArrayList<>(textValues);
	}

	private List<LexemeMeaningData> getLexemes(String dataset, String posCode, String word, String form, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		String sql = new String(sqlSelectLexemeByDatasetAndLang);
		StringBuffer sqlBuf = new StringBuffer();
		tableRowParamMap.put("dataset", dataset);
		tableRowParamMap.put("lang", lang);
		if (StringUtils.isNotBlank(posCode)) {
			tableRowParamMap.put("posCode", posCode);
			sqlBuf.append(sqlExistsLexemePos);
		}
		if (StringUtils.isNotBlank(word)) {
			tableRowParamMap.put("word", word);
			sqlBuf.append(sqlExistsWordWord);
		}
		if (StringUtils.isNotBlank(form)) {
			tableRowParamMap.put("form", form);
			sqlBuf.append(sqlExistsWordForm);
		}
		sql = StringUtils.replace(sql, "{placeholder}", sqlBuf.toString());

		List<Map<String, Object>> results = basicDbService.queryList(sql, tableRowParamMap);
		List<LexemeMeaningData> lexemes =
				results.stream().map(result -> {
					try {
						Long lexemeId = (Long) result.get("lexeme_id");
						PgArray meaningIdsProxy = (PgArray) result.get("meaning_ids");
						Long[] meaningIds = (Long[]) meaningIdsProxy.getArray();
						return new LexemeMeaningData(lexemeId, meaningIds);
					} catch (SQLException e) {
						//hardly
						return null;
					}
				}).collect(Collectors.toList());
		return lexemes;
	}

	class WordLexemeData {

		private String word;

		private String posCode;

		private List<LexemeMeaningData> lexemes;

		public WordLexemeData(String word, String posCode, List<LexemeMeaningData> lexemes) {
			this.word = word;
			this.posCode = posCode;
			this.lexemes = lexemes;
		}

		public String getWord() {
			return word;
		}

		public String getPosCode() {
			return posCode;
		}

		public List<LexemeMeaningData> getLexemes() {
			return lexemes;
		}
	}

	class LexemeMeaningData {
		
		private Long lexemeId;

		private Long[] meaningIds;

		public LexemeMeaningData(Long lexemeId, Long[] meaningIds) {
			this.lexemeId = lexemeId;
			this.meaningIds = meaningIds;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public Long[] getMeaningIds() {
			return meaningIds;
		}
	}
}
