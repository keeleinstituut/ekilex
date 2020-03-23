package eki.ekilex.runner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.FormMode;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.service.TextDecorationService;

@Component
public class CollocMemberRestorerRunner extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(CollocMemberRestorerRunner.class);

	private static final String SQL_SELECT_COLLOC_MEMBERS_BY_VALUE_PATH = "sql/select_colloc_members_by_value.sql";

	private static final String SQL_SELECT_COLLOC_MEMBERS_BY_ID_PATH = "sql/select_colloc_members_by_id.sql";

	private static final String SQL_SELECT_WORD_ID_CANDIDATES_BY_VALUE_PATH = "sql/select_word_id_candidates_by_value.sql";

	private static final String SQL_SELECT_WORD_PATH = "sql/select_word.sql";

	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordPosExp = "x:sl";
	private final String articleBodyExp = "x:S";
	private final String meaningBlockExp = "x:tp[not(@x:as='ab')]";
	private final String collocPosGroupExp = "x:colp/x:cmg";
	private final String collocRelGroupExp = "x:relg";
	private final String collocGroupExp = "x:colg";

	private final String wordHomonymNrAttr = "i";
	private final String collocConjunctAttr = "jv";
	private final String lemmaDataAttr = "lemposvk";

	private final char lemmaDataDelim = '|';
	private final char lemmaDataCellDelim = ':';
	private final char compundWordCompDelim = '+';

	private final String prevWordCollocMemberName = "mse";
	private final String nextWordCollocMemberName = "msj";
	private final String colWordCollocMemberName = "col";
	private final String[] primaryCollocMemberNames = new String[] {prevWordCollocMemberName, nextWordCollocMemberName, colWordCollocMemberName};
	private final String[] collocMemberNames = new String[] {
			prevWordCollocMemberName, nextWordCollocMemberName, colWordCollocMemberName, "cnte", "cce", "ccj", "cnt"};

	protected static final String EXT_SOURCE_ID_NA = "n/a";
	protected static final String EKI_CLASSIFIER_STAATUS = "staatus";
	protected static final String EKI_CLASSIFIER_MÕISTETÜÜP = "mõistetüüp";
	protected static final String EKI_CLASSIFIER_KEELENDITÜÜP = "keelenditüüp";
	protected static final String EKI_CLASSIFIER_LIIKTYYP = "liik_tyyp";
	protected static final String EKI_CLASSIFIER_DKTYYP = "dk_tyyp";
	protected static final String EKI_CLASSIFIER_SLTYYP = "sl_tyyp";
	protected static final String EKI_CLASSIFIER_ASTYYP = "as_tyyp";
	protected static final String EKI_CLASSIFIER_VKTYYP = "vk_tyyp";
	protected static final String EKI_CLASSIFIER_MSAGTYYP = "msag_tyyp";
	protected static final String EKI_CLASSIFIER_STYYP = "s_tyyp";
	protected static final String EKI_CLASSIFIER_ETYMPLTYYP = "etympl_tyyp";
	protected static final String EKI_CLASSIIFER_ETYMKEELTYYP = "etymkeel_tyyp";
	protected static final String EKI_CLASSIFIER_ENTRY_CLASS = "entry class";
	protected static final String EKI_CLASSIFIER_VALMIDUS = "valmidus";

	private String sqlSelectCollocMembersByValue;

	private String sqlSelectCollocMembersById;

	private String sqlSelectWordIdCandidatesByValue;

	private String sqlSelectWord;

	@Autowired
	private TextDecorationService textDecorationService;

	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_COLLOC_MEMBERS_BY_VALUE_PATH);
		sqlSelectCollocMembersByValue = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_COLLOC_MEMBERS_BY_ID_PATH);
		sqlSelectCollocMembersById = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_ID_CANDIDATES_BY_VALUE_PATH);
		sqlSelectWordIdCandidatesByValue = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_PATH);
		sqlSelectWord = getContent(resourceFileInputStream);
	}

	@Transactional
	public void analyseOriginalSourceFile(String dataXmlFilePath, String reportFilePath) throws Exception {

		FileOutputStream reportFileOutputStream = new FileOutputStream(reportFilePath);
		BufferedOutputStream reportFileBufferedOutputStream = new BufferedOutputStream(reportFileOutputStream);

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());
		long articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Count invalidCollocCount = new Count();

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode;
		List<Node> collocGroupNodes;
		Attribute wordHomonymNumAttr;
		String word;
		Integer wordHomonymNum;
		List<CollocMember> collocMembers;
		List<CollocWordsRecord> existingCollocRecords;
		List<String> parsedCollocMemberWords;
		List<String> existingCollocMemberWords;

		List<WordCollocAss> missingWordCollocAssots = new ArrayList<>();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Node articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);
			wordPosNode = (Element) wordGroupNode.selectSingleNode(wordPosExp);
			if (wordPosNode == null) {
				continue;
			}
			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			wordHomonymNumAttr = wordNode.attribute(wordHomonymNrAttr);
			wordHomonymNum = 1;
			if (wordHomonymNumAttr != null) {
				wordHomonymNum = Integer.valueOf(wordHomonymNumAttr.getValue());
			}
			word = wordNode.getTextTrim();
			word = textDecorationService.cleanEkiEntityMarkup(word);

			String fullCollocGroupExp = meaningBlockExp + "/" + collocPosGroupExp + "/" + collocRelGroupExp + "/" + collocGroupExp;
			collocGroupNodes = contentNode.selectNodes(fullCollocGroupExp);

			for (Node collocGroupNode : collocGroupNodes) {

				collocMembers = extractCollocMembers(collocGroupNode);
				if (CollectionUtils.isEmpty(collocMembers)) {
					invalidCollocCount.increment();
					continue;
				}

				List<List<CollocMember>> collocMembersPermutations = composeCollocMembersPermutations(collocMembers);

				for (List<CollocMember> collocMembersPermutation : collocMembersPermutations) {

					String collocation = composeCollocValue(collocMembersPermutation);
					parsedCollocMemberWords = collocMembersPermutation.stream().map(CollocMember::getWord).collect(Collectors.toList());

					existingCollocRecords = getCollocWordsRecords(collocation);

					if (CollectionUtils.isEmpty(existingCollocRecords)) {
						logger.warn("No such collocation exists: {}", collocation);
					} else {
						for (CollocWordsRecord existingCollocRecord : existingCollocRecords) {

							existingCollocMemberWords = existingCollocRecord.getMemberWords();
							if (collocMembersPermutation.size() > existingCollocMemberWords.size()) {
								Collection<String> missingCollocMemberWords = CollectionUtils.disjunction(parsedCollocMemberWords, existingCollocMemberWords);
								if (missingCollocMemberWords.contains(word)) {
									missingWordCollocAssots.add(new WordCollocAss(word, existingCollocRecord.getCollocationId()));
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

		for (WordCollocAss missingWordCollocAss : missingWordCollocAssots) {

			String missingCollocMemberWord = missingWordCollocAss.getWord();
			Long collocationId = missingWordCollocAss.getCollocationId();
			List<WordIdCandidate> allWordIdCandidates = getWordIdCandidates(missingCollocMemberWord);
			if (CollectionUtils.isEmpty(allWordIdCandidates)) {
				appendToReport(reportFileBufferedOutputStream, missingCollocMemberWord, CSV_EMPTY_CELL, CSV_EMPTY_CELL, collocationId);
			} else {
				List<WordIdCandidate> wordIdCandidatesWithLexemes = allWordIdCandidates.stream()
						.filter(wordIdCandidate -> CollectionUtils.isNotEmpty(wordIdCandidate.getLexemeIdCandidates()))
						.collect(Collectors.toList());
				if (CollectionUtils.isEmpty(wordIdCandidatesWithLexemes)) {
					List<Long> wordIdCandidates = allWordIdCandidates.stream().map(WordIdCandidate::getWordId).collect(Collectors.toList());
					String wordIdCandidatesStr = StringUtils.join(wordIdCandidates, ", ");
					appendToReport(reportFileBufferedOutputStream, missingCollocMemberWord, wordIdCandidatesStr, CSV_EMPTY_CELL, collocationId);
				} else {
					for (WordIdCandidate wordIdCandidate : wordIdCandidatesWithLexemes) {
						List<Long> lexemeIdCandidates = wordIdCandidate.getLexemeIdCandidates();
						String lexemeIdCandidatesStr = StringUtils.join(lexemeIdCandidates, ", ");
						appendToReport(reportFileBufferedOutputStream, missingCollocMemberWord, wordIdCandidate.getWordId(), lexemeIdCandidatesStr, collocationId);
					}
				}
			}
		}

		reportFileBufferedOutputStream.flush();
		reportFileBufferedOutputStream.close();

		logger.info("Invalid colloc count: {}", invalidCollocCount.getValue());
		logger.info("Missing word colloc ass count: {}", missingWordCollocAssots.size());
	}

	private List<CollocWordsRecord> getCollocWordsRecords(String collocation) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("collocation", collocation);
		List<CollocWordsRecord> collocWordsRecords = basicDbService.getResults(sqlSelectCollocMembersByValue, paramMap, new CollocWordsRecordRowMapper());
		return collocWordsRecords;
	}

	private List<WordIdCandidate> getWordIdCandidates(String word) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("word", word);
		paramMap.put("datasetCode", DATASET_SSS);
		List<WordIdCandidate> wordIdCandidates = basicDbService.getResults(sqlSelectWordIdCandidatesByValue, paramMap, new WordIdCandidateRowMapper());
		return wordIdCandidates;
	}

	private List<CollocMember> extractCollocMembers(Node collocGroupNode) {

		final String[] skippedNodeNames = new String[] {"colloc", "cfr", "csc", "cng", "cd", "s", "v", "rek"};

		List<CollocMember> collocMembers = new ArrayList<>();
		Iterator<Node> collocGroupNodeIter = ((Element) collocGroupNode).nodeIterator();
		List<CollocMember> lemmaDataCollocMembers;

		while (collocGroupNodeIter.hasNext()) {
			Node collocMemberAbstractNode = collocGroupNodeIter.next();
			if (collocMemberAbstractNode instanceof DefaultElement) {
				DefaultElement collocMemberNode = (DefaultElement) collocMemberAbstractNode;
				String collocMemberName = collocMemberNode.getName();
				if (ArrayUtils.contains(collocMemberNames, collocMemberName)) {

					String form = collocMemberNode.getTextTrim();
					form = textDecorationService.cleanEkiEntityMarkup(form);
					String conjunct = collocMemberNode.attributeValue(collocConjunctAttr);
					String lemmaDataStr = collocMemberNode.attributeValue(lemmaDataAttr);
					conjunct = StringUtils.replace(conjunct, "v", "või");

					if (StringUtils.isBlank(lemmaDataStr)) {
						//col, mse, msj
						if (ArrayUtils.contains(primaryCollocMemberNames, collocMemberName)) {
							return Collections.emptyList();
						}
					} else {
						lemmaDataCollocMembers = composeCollocMembers(collocMemberName, form, conjunct, lemmaDataStr);
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
			collocMember = new CollocMember(collocMemberName, word, form, conjunct);
			collocMembers.add(collocMember);
		}
		return collocMembers;
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

	private List<List<CollocMember>> composeCollocMembersPermutations(List<CollocMember> collocMembers) {

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

	@SuppressWarnings("unchecked")
	public void transformLexemeExportFile(String originalLexemeDataFilePath, String transformedDataFilePath) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		FileOutputStream jsonFileOutputStream = new FileOutputStream(transformedDataFilePath);
		BufferedOutputStream jsonFileBufferedOutputStream = new BufferedOutputStream(jsonFileOutputStream);
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(jsonFileBufferedOutputStream);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonGenerator.setCodec(objectMapper);

		jsonGenerator.writeStartObject();

		Map<String, Object> rootData = getFileData(objectMapper, originalLexemeDataFilePath);
		List<Map<String, Object>> lexemes = (List<Map<String, Object>>) rootData.get(LEXEME);
		logger.info("Extracted {} lexemes", lexemes.size());
		extractCollocData(lexemes, jsonGenerator);

		jsonGenerator.writeEndObject();

		jsonGenerator.close();
		jsonFileBufferedOutputStream.flush();
		jsonFileBufferedOutputStream.close();
		jsonFileOutputStream.flush();
		jsonFileOutputStream.close();

		logger.info("Done!");
	}

	@SuppressWarnings("unchecked")
	public void transformParadigmExportFile(String originalParadigmDataFilePath, String transformedDataFilePath) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		FileOutputStream jsonFileOutputStream = new FileOutputStream(transformedDataFilePath);
		BufferedOutputStream jsonFileBufferedOutputStream = new BufferedOutputStream(jsonFileOutputStream);
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(jsonFileBufferedOutputStream);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonGenerator.setCodec(objectMapper);

		jsonGenerator.writeStartObject();

		Map<String, Object> rootData = getFileData(objectMapper, originalParadigmDataFilePath);
		List<Map<String, Object>> paradigms = (List<Map<String, Object>>) rootData.get(PARADIGM);
		logger.info("Extracted {} paradigms", paradigms.size());
		extractWordValueData(paradigms, jsonGenerator);

		jsonGenerator.writeEndObject();

		jsonGenerator.close();
		jsonFileBufferedOutputStream.flush();
		jsonFileBufferedOutputStream.close();
		jsonFileOutputStream.flush();
		jsonFileOutputStream.close();

		logger.info("Done!");
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getFileData(ObjectMapper objectMapper, String dataFilePath) throws Exception {
		FileInputStream jsonFileInputStream = new FileInputStream(dataFilePath);
		BufferedInputStream jsonFileBufferedInputStream = new BufferedInputStream(jsonFileInputStream);
		Map<String, Object> rootData = objectMapper.readValue(jsonFileBufferedInputStream, Map.class);
		jsonFileBufferedInputStream.close();
		jsonFileInputStream.close();
		return rootData;
	}

	@SuppressWarnings("unchecked")
	private void extractCollocData(List<Map<String, Object>> lexemes, JsonGenerator jsonGenerator) throws Exception {

		logger.info("Starting collecting colloc data...");

		int recordCount = lexemes.size();
		long recordCounter = 0;
		long progressIndicator = recordCount / Math.min(recordCount, 100);

		List<Map<String, Object>> allCompactLexemes = new ArrayList<>();
		List<Map<String, Object>> allLexCollocs = new ArrayList<>();
		List<Map<String, Object>> allLexCollocPosGroups = new ArrayList<>();
		List<Map<String, Object>> allCollocations = new ArrayList<>();

		List<Map<String, Object>> lexCollocs;
		List<Map<String, Object>> lexCollocPosGroups;
		Map<String, Object> collocation;
		Map<String, Object> compactLexeme;

		for (Map<String, Object> lexeme : lexemes) {

			//lexeme
			compactLexeme = new HashMap<>();
			compactLexeme.put("id", lexeme.get("id"));
			compactLexeme.put("word_id", lexeme.get("word_id"));
			compactLexeme.put("meaning_id", lexeme.get("meaning_id"));
			allCompactLexemes.add(compactLexeme);

			//lex colloc + colloc
			lexCollocs = (List<Map<String, Object>>) lexeme.remove(LEX_COLLOC);
			if (lexCollocs != null) {
				for (Map<String, Object> lexColloc : lexCollocs) {
					collocation = (Map<String, Object>) lexColloc.remove(COLLOCATION);
					allCollocations.add(collocation);
				}
				allLexCollocs.addAll(lexCollocs);
			}

			//lex colloc pos gr + lex colloc rel gr
			lexCollocPosGroups = (List<Map<String, Object>>) lexeme.remove(LEX_COLLOC_POS_GROUP);
			if (lexCollocPosGroups != null) {
				allLexCollocPosGroups.addAll(lexCollocPosGroups);
			}

			// progress
			recordCounter++;
			if (recordCounter % progressIndicator == 0) {
				long progressPercent = recordCounter / progressIndicator;
				logger.debug("{}% - {} records iterated", progressPercent, recordCounter);
			}
		}

		allCollocations = allCollocations.stream().distinct().collect(Collectors.toList());

		writeData(LEXEME, allCompactLexemes, jsonGenerator);
		allCompactLexemes.clear();
		writeData(LEX_COLLOC, allLexCollocs, jsonGenerator);
		allLexCollocs.clear();
		writeData(LEX_COLLOC_POS_GROUP, allLexCollocPosGroups, jsonGenerator);
		allLexCollocPosGroups.clear();
		writeData(COLLOCATION, allCollocations, jsonGenerator);
		allCollocations.clear();
	}

	@SuppressWarnings("unchecked")
	private void extractWordValueData(List<Map<String, Object>> paradigms, JsonGenerator jsonGenerator) throws Exception {

		logger.info("Starting collecting paradigm data...");

		int recordCount = paradigms.size();
		long recordCounter = 0;
		long progressIndicator = recordCount / Math.min(recordCount, 100);

		List<Map<String, Object>> allCompactWords = new ArrayList<>();
		Map<String, Object> compactWord;
		List<Map<String, Object>> forms;

		for (Map<String, Object> paradigm : paradigms) {

			forms = (List<Map<String, Object>>) paradigm.remove(FORM);
			List<String> wordValues = forms.stream().filter(form -> {
				FormMode formMode = FormMode.valueOf(form.get("mode").toString());
				if (FormMode.WORD.equals(formMode)) {
					return true;
				}
				if (FormMode.UNKNOWN.equals(formMode)) {
					return true;
				}
				return false;
			}).map(form -> form.get("value").toString()).collect(Collectors.toList());

			if (CollectionUtils.isEmpty(wordValues)) {
				continue;
			}
			String wordValue = wordValues.get(0);

			compactWord = new HashMap<>();
			compactWord.put("word_id", paradigm.get("word_id"));
			compactWord.put("word", wordValue);
			allCompactWords.add(compactWord);

			// progress
			recordCounter++;
			if (recordCounter % progressIndicator == 0) {
				long progressPercent = recordCounter / progressIndicator;
				logger.debug("{}% - {} records iterated", progressPercent, recordCounter);
			}
		}
		allCompactWords = allCompactWords.stream().distinct().collect(Collectors.toList());

		writeData(WORD, allCompactWords, jsonGenerator);
		allCompactWords.clear();
	}

	private void writeData(String fieldName, List<Map<String, Object>> dataRecords, JsonGenerator jsonGenerator) throws Exception {

		logger.info("Starting writing {} records of {}", fieldName, dataRecords.size());

		jsonGenerator.writeFieldName(fieldName);
		jsonGenerator.writeStartArray();
		for (Map<String, Object> dataRow : dataRecords) {
			jsonGenerator.writeObject(dataRow);
		}
		jsonGenerator.writeEndArray();
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public void analyseOriginalImportFile(String lexemeDataFilePath, String wordValueDataFilePath, String reportFilePath) throws Exception {

		FileOutputStream reportFileOutputStream = new FileOutputStream(reportFilePath);
		BufferedOutputStream reportFileBufferedOutputStream = new BufferedOutputStream(reportFileOutputStream);

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> rootData;

		rootData = getFileData(objectMapper, lexemeDataFilePath);
		List<Map<String, Object>> lexemes = (List<Map<String, Object>>) rootData.get(LEXEME);
		List<Map<String, Object>> collocations = (List<Map<String, Object>>) rootData.get(COLLOCATION);
		List<Map<String, Object>> lexCollocs = (List<Map<String, Object>>) rootData.get(LEX_COLLOC);

		rootData = getFileData(objectMapper, wordValueDataFilePath);
		List<Map<String, Object>> wordValues = (List<Map<String, Object>>) rootData.get(WORD);

		Map<Long, Map<String, Object>> sourceAllLexemeIdMap = lexemes.stream().collect(Collectors.toMap(lexeme -> Long.valueOf(lexeme.get("id").toString()), lexeme -> lexeme));
		Map<Long, List<Map<String, Object>>> lexCollocsByCollocIdMap = lexCollocs.stream().collect(Collectors.groupingBy(lexColloc -> Long.valueOf(lexColloc.get("collocation_id").toString())));
		Map<Long, List<Map<String, Object>>> lexCollocsByLexemeIdMap = lexCollocs.stream().collect(Collectors.groupingBy(lexColloc -> Long.valueOf(lexColloc.get("lexeme_id").toString())));
		Map<Long, String> sourceAllWordIdMap = wordValues.stream().collect(Collectors.toMap(word -> Long.valueOf(word.get("word_id").toString()), word -> word.get("word").toString()));

		int recordCount = collocations.size();
		long recordCounter = 0;
		long progressIndicator = recordCount / Math.min(recordCount, 100);

		for (Map<String, Object> sourceCollocation : collocations) {

			String collocationValue = sourceCollocation.get("value").toString();
			Long collocationId = Long.valueOf(sourceCollocation.get("id").toString());
			List<Map<String, Object>> sourceLexCollocs = lexCollocsByCollocIdMap.get(collocationId);
			List<Long> sourceLexemeIds = sourceLexCollocs.stream().map(lexColloc -> Long.valueOf(lexColloc.get("lexeme_id").toString())).collect(Collectors.toList());
			CollocLexemesRecord collocLexemesRecord = getCollocLexemesRecord(collocationId);
			if (collocLexemesRecord != null) {
				List<CollocLexemeRecord> existingMemberLexemes = collocLexemesRecord.getMemberLexemes();
				Map<Long, CollocLexemeRecord> existingMemberLexemeMap = existingMemberLexemes.stream()
						.collect(Collectors.toMap(CollocLexemeRecord::getLexemeId, lexeme -> lexeme));
				Set<Long> existingMemberLexemeIds = existingMemberLexemeMap.keySet();
				if (existingMemberLexemeIds.size() < sourceLexemeIds.size()) {
					Collection<Long> isolatedSourceLexemeIds = CollectionUtils.subtract(sourceLexemeIds, existingMemberLexemeIds);
					Collection<Long> isolatedExistingLexemeIds = CollectionUtils.subtract(existingMemberLexemeIds, sourceLexemeIds);
					List<CollocMemberMatch> resolvedCollocMemberMatches = resolveMatches(
							isolatedSourceLexemeIds, isolatedExistingLexemeIds, sourceAllLexemeIdMap, sourceAllWordIdMap, existingMemberLexemeMap);
					List<CollocMemberMatch> missingCollocMemberMatches = resolvedCollocMemberMatches.stream()
							.filter(collocMember -> collocMember.getExistingLexemeId() == null).collect(Collectors.toList());

					for (CollocMemberMatch missingCollocMember : missingCollocMemberMatches) {
						String sourceWordValue = missingCollocMember.getSourceWordValue();
						Long resolvedWordId = missingCollocMember.getResolvedWordId();
						Object resolvedWordIdCell;
						if (resolvedWordId == null) {
							resolvedWordIdCell = CSV_EMPTY_CELL;
						} else {
							resolvedWordIdCell = resolvedWordId;
						}
						appendToReport(reportFileBufferedOutputStream, collocationId, collocationValue, sourceWordValue, resolvedWordIdCell);
					}
				}
			}

			// progress
			recordCounter++;
			if (recordCounter % progressIndicator == 0) {
				long progressPercent = recordCounter / progressIndicator;
				logger.debug("{}% - {} records iterated", progressPercent, recordCounter);
			}
		}

		reportFileBufferedOutputStream.flush();
		reportFileBufferedOutputStream.close();

		logger.info("Done!");
	}

	private List<CollocMemberMatch> resolveMatches(
			Collection<Long> isolatedSourceLexemeIds,
			Collection<Long> isolatedExistingLexemeIds,
			Map<Long, Map<String, Object>> sourceAllLexemeIdMap,
			Map<Long, String> sourceAllWordIdMap,
			Map<Long, CollocLexemeRecord> existingMemberLexemeMap) {

		List<CollocMemberMatch> resolvedCollocMemberMatches = new ArrayList<>();
		List<Long> resolvedSourceLexemeIds;
		List<Long> resolvedExistingLexemeIds;
		Collection<Long> unresolvedSourceLexemeIds;
		Collection<Long> unresolvedExistingLexemeIds;
		CollocMemberMatch collocMemberMatch;

		// attempt to resolve by common word or meaning ids
		for (Long sourceLexemeId : isolatedSourceLexemeIds) {
			Map<String, Object> sourceLexeme = sourceAllLexemeIdMap.get(sourceLexemeId);
			Long sourceWordId = Long.valueOf(sourceLexeme.get("word_id").toString());
			Long sourceMeaningId = Long.valueOf(sourceLexeme.get("meaning_id").toString());
			String sourceWordValue = sourceAllWordIdMap.get(sourceWordId);
			for (Long existingLexemeId : isolatedExistingLexemeIds) {
				CollocLexemeRecord existingLexeme = existingMemberLexemeMap.get(existingLexemeId);
				Long existingWordId = existingLexeme.getWordId();
				Long existingMeaningId = existingLexeme.getMeaningId();
				if (sourceWordId.equals(existingWordId)) {
					collocMemberMatch = new CollocMemberMatch();
					collocMemberMatch.setSourceWordValue(sourceWordValue);
					collocMemberMatch.setSourceLexemeId(sourceLexemeId);
					collocMemberMatch.setExistingLexemeId(existingLexemeId);
					collocMemberMatch.setResolvedWordId(existingWordId);
					resolvedCollocMemberMatches.add(collocMemberMatch);
					break;
				} else if (sourceMeaningId.equals(existingMeaningId)) {
					collocMemberMatch = new CollocMemberMatch();
					collocMemberMatch.setSourceWordValue(sourceWordValue);
					collocMemberMatch.setSourceLexemeId(sourceLexemeId);
					collocMemberMatch.setExistingLexemeId(existingLexemeId);
					collocMemberMatch.setResolvedMeaningId(existingMeaningId);
					resolvedCollocMemberMatches.add(collocMemberMatch);
					break;
				}
			}
		}

		resolvedSourceLexemeIds = resolvedCollocMemberMatches.stream().map(CollocMemberMatch::getSourceLexemeId).collect(Collectors.toList());
		resolvedExistingLexemeIds = resolvedCollocMemberMatches.stream().map(CollocMemberMatch::getExistingLexemeId).collect(Collectors.toList());
		unresolvedSourceLexemeIds = CollectionUtils.subtract(isolatedSourceLexemeIds, resolvedSourceLexemeIds);
		unresolvedExistingLexemeIds = CollectionUtils.subtract(isolatedExistingLexemeIds, resolvedExistingLexemeIds);

		// attempt to resolve by common word values
		if (CollectionUtils.isNotEmpty(unresolvedSourceLexemeIds)) {
			for (Long sourceLexemeId : unresolvedSourceLexemeIds) {
				Map<String, Object> sourceLexeme = sourceAllLexemeIdMap.get(sourceLexemeId);
				Long sourceWordId = Long.valueOf(sourceLexeme.get("word_id").toString());
				String sourceWordValue = sourceAllWordIdMap.get(sourceWordId);
				for (Long existingLexemeId : unresolvedExistingLexemeIds) {
					CollocLexemeRecord existingLexeme = existingMemberLexemeMap.get(existingLexemeId);
					Long existingWordId = existingLexeme.getWordId();
					String existingWordValue = existingLexeme.getWord();
					if (StringUtils.equals(sourceWordValue, existingWordValue)) {
						collocMemberMatch = new CollocMemberMatch();
						collocMemberMatch.setSourceWordValue(sourceWordValue);
						collocMemberMatch.setSourceLexemeId(sourceLexemeId);
						collocMemberMatch.setExistingLexemeId(existingLexemeId);
						collocMemberMatch.setResolvedWordId(existingWordId);
						resolvedCollocMemberMatches.add(collocMemberMatch);
						break;
					}
				}
			}
		}

		resolvedSourceLexemeIds = resolvedCollocMemberMatches.stream().map(CollocMemberMatch::getSourceLexemeId).collect(Collectors.toList());
		unresolvedSourceLexemeIds = CollectionUtils.subtract(isolatedSourceLexemeIds, resolvedSourceLexemeIds);

		// attempt to resolve by locating existing word by source id and add remaining members as unassociated word values
		if (CollectionUtils.isNotEmpty(unresolvedSourceLexemeIds)) {
			for (Long sourceLexemeId : unresolvedSourceLexemeIds) {
				Map<String, Object> sourceLexeme = sourceAllLexemeIdMap.get(sourceLexemeId);
				Long sourceWordId = Long.valueOf(sourceLexeme.get("word_id").toString());
				String sourceWordValue = sourceAllWordIdMap.get(sourceWordId);
				Map<String, Object> existingWord = getWord(sourceWordId);
				collocMemberMatch = new CollocMemberMatch();
				collocMemberMatch.setSourceWordValue(sourceWordValue);
				collocMemberMatch.setSourceLexemeId(sourceLexemeId);
				if (existingWord != null) {
					collocMemberMatch.setResolvedWordId(sourceWordId);
				}
				resolvedCollocMemberMatches.add(collocMemberMatch);
			}
		}

		return resolvedCollocMemberMatches;
	}

	private CollocLexemesRecord getCollocLexemesRecord(Long collocationId) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("collocationId", collocationId);
		List<CollocLexemesRecord> collocLexemesRecords = basicDbService.getResults(sqlSelectCollocMembersById, paramMap, new CollocLexemesRecordRowMapper());
		if (CollectionUtils.isEmpty(collocLexemesRecords)) {
			return null;
		}
		CollocLexemesRecord singleResult = collocLexemesRecords.get(0);
		return singleResult;
	}

	private Map<String, Object> getWord(Long wordId) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		List<Map<String, Object>> results = basicDbService.queryList(sqlSelectWord, paramMap);
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		return results.get(0);
	}

	private void appendToReport(OutputStream reportStream, Object... reportCells) throws Exception {
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		IOUtils.write(logRow + '\n', reportStream, StandardCharsets.UTF_8);
	}

	class CollocMember extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String name;

		private String word;

		private String form;

		private String conjunct;

		public CollocMember(String name, String word, String form, String conjunct) {
			this.name = name;
			this.word = word;
			this.form = form;
			this.conjunct = conjunct;
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

		public String getConjunct() {
			return conjunct;
		}
	}

	class CollocWordsRecord extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long collocationId;

		private List<String> memberWords;

		public CollocWordsRecord(Long collocationId, List<String> memberWords) {
			this.collocationId = collocationId;
			this.memberWords = memberWords;
		}

		public Long getCollocationId() {
			return collocationId;
		}

		public List<String> getMemberWords() {
			return memberWords;
		}
	}

	class CollocWordsRecordRowMapper implements RowMapper<CollocWordsRecord> {

		@Override
		public CollocWordsRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long collocationId = rs.getObject("collocation_id", Long.class);
			Array valueArrayObj = rs.getArray("member_words");
			String[] valueArray = (String[]) valueArrayObj.getArray();
			List<String> memberWords = Arrays.asList(valueArray);
			return new CollocWordsRecord(collocationId, memberWords);
		}
	}

	class CollocLexemeRecord extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String word;

		private Long wordId;

		private Long lexemeId;

		private Long meaningId;

		public CollocLexemeRecord(String word, Long wordId, Long lexemeId, Long meaningId) {
			super();
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

		public Long getLexemeId() {
			return lexemeId;
		}

		public Long getMeaningId() {
			return meaningId;
		}
	}

	class CollocLexemesRecord extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long collocationId;

		private List<CollocLexemeRecord> memberLexemes;

		public CollocLexemesRecord(Long collocationId, List<CollocLexemeRecord> memberLexemes) {
			this.collocationId = collocationId;
			this.memberLexemes = memberLexemes;
		}

		public Long getCollocationId() {
			return collocationId;
		}

		public List<CollocLexemeRecord> getMemberLexemes() {
			return memberLexemes;
		}
	}

	class CollocLexemesRecordRowMapper implements RowMapper<CollocLexemesRecord> {

		@Override
		public CollocLexemesRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long collocationId = rs.getObject("collocation_id", Long.class);
			Array valueArrayObj = rs.getArray("member_lexemes");
			Object[] arrayObjects = (Object[]) valueArrayObj.getArray();
			List<CollocLexemeRecord> memberLexemes = new ArrayList<>();
			CollocLexemeRecord lexemeRecord;
			for (Object arrayObject : arrayObjects) {
				String arrayObjectStr = arrayObject.toString();
				String[] idStrs = StringUtils.split(arrayObjectStr, '|');
				String word = idStrs[0];
				Long wordId = Long.valueOf(idStrs[1]);
				Long lexemeId = Long.valueOf(idStrs[2]);
				Long meaningId = Long.valueOf(idStrs[3]);
				lexemeRecord = new CollocLexemeRecord(word, wordId, lexemeId, meaningId);
				memberLexemes.add(lexemeRecord);
			}
			return new CollocLexemesRecord(collocationId, memberLexemes);
		}
	}

	class WordIdCandidate extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private long wordId;

		private List<Long> lexemeIdCandidates;

		public WordIdCandidate(long wordId, List<Long> lexemeIdCandidates) {
			this.wordId = wordId;
			this.lexemeIdCandidates = lexemeIdCandidates;
		}

		public long getWordId() {
			return wordId;
		}

		public List<Long> getLexemeIdCandidates() {
			return lexemeIdCandidates;
		}
	}

	class WordIdCandidateRowMapper implements RowMapper<WordIdCandidate> {

		@Override
		public WordIdCandidate mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long wordId = rs.getObject("word_id", Long.class);
			Array valueArrayObj = rs.getArray("lexeme_id_candidates");
			Long[] valueArray = (Long[]) valueArrayObj.getArray();
			List<Long> lexemeIdCandidates = Arrays.asList(valueArray);
			return new WordIdCandidate(wordId, lexemeIdCandidates);
		}
	}

	class WordCollocAss extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String word;

		private Long collocationId;

		public WordCollocAss(String word, Long collocationId) {
			this.word = word;
			this.collocationId = collocationId;
		}

		public String getWord() {
			return word;
		}

		public Long getCollocationId() {
			return collocationId;
		}
	}

	class CollocMemberMatch extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String sourceWordValue;

		private Long sourceLexemeId;

		private Long existingLexemeId;

		private Long resolvedWordId;

		private Long resolvedMeaningId;

		private List<Long> resolvedWordIdCandidates;

		private List<Long> resolvedMeaningIdCandidates;

		public String getSourceWordValue() {
			return sourceWordValue;
		}

		public void setSourceWordValue(String sourceWordValue) {
			this.sourceWordValue = sourceWordValue;
		}

		public Long getSourceLexemeId() {
			return sourceLexemeId;
		}

		public void setSourceLexemeId(Long sourceLexemeId) {
			this.sourceLexemeId = sourceLexemeId;
		}

		public Long getExistingLexemeId() {
			return existingLexemeId;
		}

		public void setExistingLexemeId(Long existingLexemeId) {
			this.existingLexemeId = existingLexemeId;
		}

		public Long getResolvedWordId() {
			return resolvedWordId;
		}

		public void setResolvedWordId(Long resolvedWordId) {
			this.resolvedWordId = resolvedWordId;
		}

		public Long getResolvedMeaningId() {
			return resolvedMeaningId;
		}

		public void setResolvedMeaningId(Long resolvedMeaningId) {
			this.resolvedMeaningId = resolvedMeaningId;
		}

		public List<Long> getResolvedWordIdCandidates() {
			return resolvedWordIdCandidates;
		}

		public void setResolvedWordIdCandidates(List<Long> resolvedWordIdCandidates) {
			this.resolvedWordIdCandidates = resolvedWordIdCandidates;
		}

		public List<Long> getResolvedMeaningIdCandidates() {
			return resolvedMeaningIdCandidates;
		}

		public void setResolvedMeaningIdCandidates(List<Long> resolvedMeaningIdCandidates) {
			this.resolvedMeaningIdCandidates = resolvedMeaningIdCandidates;
		}
	}
}
