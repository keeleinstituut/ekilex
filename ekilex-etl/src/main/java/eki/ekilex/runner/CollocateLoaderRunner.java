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
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.ekilex.service.ReportComposer;

@Component
public class CollocateLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(CollocateLoaderRunner.class);

	private static final String SQL_SELECT_LEXEME_BY_DATASET_POS_WORD_FORM = "sql/select_lexeme_by_dataset_and_pos_and_word_and_form.sql";

	private static final String SQL_SELECT_LEXEME_BY_DATASET_POS_WORD = "sql/select_lexeme_by_dataset_and_pos_and_word.sql";

	private static final String SQL_SELECT_LEXEME_BY_DATASET_POS_FORM = "sql/select_lexeme_by_dataset_and_pos_and_form.sql";

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

	private String sqlSelectLexemeByDatasetPosWordForm;

	private String sqlSelectLexemeByDatasetPosWord;

	private String sqlSelectLexemeByDatasetPosForm;

	private Map<String, String> posCodes;

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_BY_DATASET_POS_WORD_FORM);
		sqlSelectLexemeByDatasetPosWordForm = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_BY_DATASET_POS_WORD);
		sqlSelectLexemeByDatasetPosWord = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_BY_DATASET_POS_FORM);
		sqlSelectLexemeByDatasetPosForm = getContent(resourceFileInputStream);

		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String targetDataset, boolean doReports) throws Exception {

		logger.debug("Starting loading collocates...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("kol load report",
					REPORT_ILLEGAL_DATA, REPORT_MISSING_WORD, REPORT_AMBIGUOUS_MEANING_MATCH, REPORT_AMBIGUOUS_WORD_MATCH);
		}

		dataLang = unifyLang(dataLang);
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Element headerNode, contentNode, wordNode, wordPosNode;
		List<Element> wordGroupNodes, meaningBlockNodes, collocPosGroupNodes, relationGroupNodes, collocGroupNodes, prevWordNodes, collocWordNodes, nextWordNodes;
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

		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNodes = headerNode.selectNodes(wordGroupExp);

			wordMap = new HashMap<>();
			for (Element wordGroupNode : wordGroupNodes) {
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
					continue;
				}
				wordPosCode = wordPosNode.getTextTrim();
				wordPosCode = posCodes.get(wordPosCode);
				lexemes = getLexemes(targetDataset, wordPosCode, word, null, dataLang);
				wordLexemeData = new WordLexemeData(word, wordPosCode, lexemes);
				wordMap.put(word, wordLexemeData);
			}
			if (MapUtils.isEmpty(wordMap)) {
				continue;
			}

			meaningBlockNodes = contentNode.selectNodes("x:tp"); // TODO ask: why many?

			for (Element meaningBlockNode : meaningBlockNodes) {

				collocPosGroupNodes = meaningBlockNode.selectNodes("x:colp/x:cmg");

				for (Element colPosGroupNode : collocPosGroupNodes) {

					collocPosCode = colPosGroupNode.attributeValue("csl");
					collocPosCode = posCodes.get(collocPosCode);

					relationGroupNodes = colPosGroupNode.selectNodes("x:relg"); // TODO ask: why many?

					for (Element relationGroupNode : relationGroupNodes) {

						collocGroupNodes = relationGroupNode.selectNodes("x:colg");

						for (Element collocGroupNode : collocGroupNodes) {

							collocWordNodes = collocGroupNode.selectNodes("x:col");
							prevWordNodes = collocGroupNode.selectNodes("x:mse");
							nextWordNodes = collocGroupNode.selectNodes("x:msj");
							//TODO generate collocation value
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
			List<Element> collocWordNodes,
			List<Element> prevWordNodes,
			List<Element> nextWordNodes,
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
		List<LexemeMeaningData> lexemes;
		LexemeMeaningData lexeme;
		StringBuffer logBuf;

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
			lexemes = getLexemes(targetDataset, collocPosCode, null, collocWord, dataLang);
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
					logBuf.append(collocPosCode);
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
					logBuf.append("sõnale on mitu vastet");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocWord);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocPosCode);
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
				}
				continue;
			}
			lexeme = lexemes.get(0);
			if (lexeme.getMeaningIds().length > 1) {
				ambiguousDataCount.increment();
				logger.warn("Many meanings for collocate word {}", collocWord);
				if (doReports) {
					logBuf = new StringBuffer();
					logBuf.append(words);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("kollokaat");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("sõnal on mitu tähendust");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocWord);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(collocPosCode);
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
					for (WordLexemeData wordDataCand : wordMap.values()) {
						lexemes = getLexemes(targetDataset, wordDataCand.getPosCode(), wordDataCand.getWord(), prevWord, dataLang);
						if (CollectionUtils.isNotEmpty(lexemes)) {
							break;
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
						logBuf.append("sõnale on mitu vastet");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(prevWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(collocPosCode);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
					}
					continue;
				}
				lexeme = lexemes.get(0);
				if (lexeme.getMeaningIds().length > 1) {
					ambiguousDataCount.increment();
					logger.warn("Many meanings for previous word {}", prevWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("eel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnal on mitu tähendust");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(prevWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(collocPosCode);
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
					//TODO create collocation prev + colloc
					successfulCollocationMatchCount.increment();
				}
			}

		} else if (CollectionUtils.isEmpty(prevWords) && CollectionUtils.isNotEmpty(nextWords)) {

			Map<String, LexemeMeaningData> nextWordLexemeMap = new HashMap<>();
			for (String nextWord : nextWords) {
				WordLexemeData wordData = wordMap.get(nextWord);
				lexemes = null;
				if (wordData == null) {
					for (WordLexemeData wordDataCand : wordMap.values()) {
						lexemes = getLexemes(targetDataset, wordDataCand.getPosCode(), wordDataCand.getWord(), nextWord, dataLang);
						if (CollectionUtils.isNotEmpty(lexemes)) {
							break;
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
						logBuf.append("sõnale on mitu vastet");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(nextWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(collocPosCode);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_AMBIGUOUS_WORD_MATCH, logRow);
					}
					continue;
				}
				lexeme = lexemes.get(0);
				if (lexeme.getMeaningIds().length > 1) {
					ambiguousDataCount.increment();
					logger.warn("Many meanings for next word {}", nextWord);
					if (doReports) {
						logBuf = new StringBuffer();
						logBuf.append(words);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("järel");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("sõnal on mitu tähendust");
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(nextWord);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(collocPosCode);
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
					//TODO create collocation colloc + next
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

	private List<String> collectTextValues(List<Element> nodes) {
		List<String> textValues = nodes.stream().map(node -> node.getTextTrim()).collect(Collectors.toList());
		return new ArrayList<>(textValues);
	}

	private List<LexemeMeaningData> getLexemes(String dataset, String posCode, String word, String form, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		String sql;
		tableRowParamMap.put("dataset", dataset);
		tableRowParamMap.put("posCode", posCode);
		if (StringUtils.isBlank(form)) {
			tableRowParamMap.put("word", word);
			sql = sqlSelectLexemeByDatasetPosWord;
		} else if (StringUtils.isBlank(word)) {
			tableRowParamMap.put("form", form);
			sql = sqlSelectLexemeByDatasetPosForm;
		} else {
			tableRowParamMap.put("word", word);
			tableRowParamMap.put("form", form);
			sql = sqlSelectLexemeByDatasetPosWordForm;
		}
		tableRowParamMap.put("lang", lang);
		List<Map<String, Object>> results = basicDbService.queryList(sql, tableRowParamMap);
		List<LexemeMeaningData> lexemes =
				results.stream().map(result -> {
					try {
						Long lexemeId = (Long) result.get("lexeme_id");
						PgArray meaningIdsProxy = (PgArray) result.get("meaning_ids");
						Long[] meaningIds = (Long[]) meaningIdsProxy.getArray();
						return new LexemeMeaningData(lexemeId, meaningIds);
					} catch (SQLException e) {
						e.printStackTrace();
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
