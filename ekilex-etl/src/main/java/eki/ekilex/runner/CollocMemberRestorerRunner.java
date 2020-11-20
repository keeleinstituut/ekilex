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
import eki.common.exception.DataLoadingException;
import eki.common.service.AbstractLoaderCommons;
import eki.common.service.TextDecorationService;

@Component
public class CollocMemberRestorerRunner extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(CollocMemberRestorerRunner.class);

	private static final String SQL_SELECT_COLLOC_MEMBERS_BY_VALUE_PATH = "sql/select_colloc_members_by_value.sql";

	private static final String SQL_SELECT_COLLOC_MEMBERS_BY_ID_PATH = "sql/select_colloc_members_by_id.sql";

	private static final String SQL_SELECT_COLLOC_POS_REL_GROUPS = "sql/select_colloc_pos_rel_groups.sql";

	private static final String SQL_SELECT_WORD_ID_CANDIDATES_BY_VALUE_PATH = "sql/select_word_id_candidates_by_value.sql";

	private static final String SQL_SELECT_WORD_PATH = "sql/select_word.sql";

	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordPosExp = "x:sl";
	private final String articleBodyExp = "x:S";
	private final String meaningBlockExp = "x:tp[not(@x:as='ab')]";
	private final String collocPosGroupExp = "x:colp/x:cmg";
	private final String collocPosAttr = "csl";
	private final String collocRelGroupExp = "x:relg";
	private final String collocGroupExp = "x:colg";
	private final String collocRelGroupNameExp = "x:reln";
	private final String collocRelGroupFreqExp = "x:rfr";
	private final String collocRelGroupScoreExp = "x:rsc";

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

	private final Float inboundPrimaryCollocMemberWeight = 1F;

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

	private String sqlSelectCollocPosRelGroups;

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

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_COLLOC_POS_REL_GROUPS);
		sqlSelectCollocPosRelGroups = getContent(resourceFileInputStream);

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
		logger.debug("Extracted {} articles", articleNodes.size());

		Count invalidCollocCount = new Count();

		List<IncompleteColloc> incompleteCollocs = extractIncompleteCollocs(articleNodes, invalidCollocCount);

		for (IncompleteColloc incompleteColloc : incompleteCollocs) {

			Long collocationId = incompleteColloc.getCollocationId();
			String collocationValue = incompleteColloc.getCollocationValue();
			List<String> missingCollocMemberWords = incompleteColloc.getMissingCollocMemberWords();

			Object wordIdCandidatesCell;
			Object lexemeIdCandidatesCell;
			for (String missingCollocMemberWord : missingCollocMemberWords) {
				List<WordIdCandidate> allWordIdCandidates = getWordIdCandidates(missingCollocMemberWord);
				if (CollectionUtils.isEmpty(allWordIdCandidates)) {
					wordIdCandidatesCell = CSV_EMPTY_CELL;
					lexemeIdCandidatesCell = CSV_EMPTY_CELL;
				} else {
					List<Long> wordIdCandidates = allWordIdCandidates.stream().map(WordIdCandidate::getWordId).collect(Collectors.toList());
					List<WordIdCandidate> wordIdCandidatesWithLexemes = allWordIdCandidates.stream()
							.filter(wordIdCandidate -> CollectionUtils.isNotEmpty(wordIdCandidate.getLexemeIdCandidates()))
							.collect(Collectors.toList());
					if (CollectionUtils.isEmpty(wordIdCandidatesWithLexemes)) {
						wordIdCandidatesCell = StringUtils.join(wordIdCandidates, ',');
						lexemeIdCandidatesCell = CSV_EMPTY_CELL;
					} else {
						WordIdCandidate singleWordIdCandidate = wordIdCandidatesWithLexemes.get(0);
						List<Long> lexemeIdCandidates = singleWordIdCandidate.getLexemeIdCandidates();
						if ((wordIdCandidatesWithLexemes.size() == 1) && (lexemeIdCandidates.size() == 1)) {
							wordIdCandidatesCell = singleWordIdCandidate.getWordId();
							lexemeIdCandidatesCell = lexemeIdCandidates.get(0);
						} else if (wordIdCandidatesWithLexemes.size() == 1) {
							wordIdCandidatesCell = singleWordIdCandidate.getWordId();
							lexemeIdCandidatesCell = StringUtils.join(lexemeIdCandidates, ',');
						} else {
							wordIdCandidatesCell = StringUtils.join(wordIdCandidates, ',');
							lexemeIdCandidatesCell = CSV_EMPTY_CELL;
						}
					}
				}
				appendToReport(reportFileBufferedOutputStream, collocationId, collocationValue, missingCollocMemberWord, wordIdCandidatesCell, lexemeIdCandidatesCell);
			}
		}

		reportFileBufferedOutputStream.flush();
		reportFileBufferedOutputStream.close();

		logger.info("Invalid colloc count: {}", invalidCollocCount.getValue());
		logger.info("Incomplete colloc count: {}", incompleteCollocs.size());
	}

	private List<IncompleteColloc> extractIncompleteCollocs(List<Node> articleNodes, Count invalidCollocCount) throws Exception {

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode;
		List<Node> collocGroupNodes;
		String word;
		List<CollocMember> collocMembers;
		List<CollocWordsRecord> existingCollocRecords;
		List<String> sourceCollocMemberWords;
		List<String> existingCollocMemberWords;

		List<IncompleteColloc> incompleteCollocs = new ArrayList<>();

		long articleCount = articleNodes.size();
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
			word = wordNode.getTextTrim();
			word = textDecorationService.removeEkiEntityMarkup(word);

			String fullCollocGroupExp = meaningBlockExp + "/" + collocPosGroupExp + "/" + collocRelGroupExp + "/" + collocGroupExp;
			collocGroupNodes = contentNode.selectNodes(fullCollocGroupExp);

			for (Node collocGroupNode : collocGroupNodes) {

				collocMembers = extractCollocMembers(collocGroupNode);
				if (CollectionUtils.isEmpty(collocMembers)) {
					invalidCollocCount.increment();
					continue;
				}

				List<List<CollocMember>> collocMembersPermutations = composeCollocMembersPermutations(collocMembers);

				for (List<CollocMember> sourceCollocMembers : collocMembersPermutations) {

					String collocationValue = composeCollocValue(sourceCollocMembers);
					sourceCollocMemberWords = sourceCollocMembers.stream().map(CollocMember::getWord).collect(Collectors.toList());

					existingCollocRecords = getCollocWordsRecords(collocationValue);

					if (CollectionUtils.isEmpty(existingCollocRecords)) {
						logger.warn("No such collocation exists: {}", collocationValue);
					} else {
						for (CollocWordsRecord existingCollocRecord : existingCollocRecords) {

							existingCollocMemberWords = existingCollocRecord.getMemberWords();
							if (existingCollocMemberWords.size() < sourceCollocMembers.size()) {
								Collection<String> missingCollocMemberWords = CollectionUtils.disjunction(sourceCollocMemberWords, existingCollocMemberWords);
								if (missingCollocMemberWords.contains(word)) {
									Long collocationId = existingCollocRecord.getCollocationId();
									IncompleteColloc incompleteColloc = new IncompleteColloc(collocationId, collocationValue, new ArrayList<>(missingCollocMemberWords));
									incompleteCollocs.add(incompleteColloc);
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

		incompleteCollocs = incompleteCollocs.stream().distinct().collect(Collectors.toList());
		return incompleteCollocs;
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
					form = textDecorationService.removeEkiEntityMarkup(form);
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
						appendToReport(reportFileBufferedOutputStream, collocationId, collocationValue, sourceWordValue, resolvedWordIdCell, CSV_EMPTY_CELL);
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

	@Transactional(rollbackOn = Exception.class)
	public void restoreCollocData(String dataXmlFilePath, String collocRestoreMappingFilePath) throws Exception {

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());
		Map<String, List<RestorableWordCollocData>> sourceFileCollocDataMap = extractRestorableCollocFileData(articleNodes);

		List<RestorableCollocReportRow> reportFileCollocDataRows = extractRestorableCollocReportRows(collocRestoreMappingFilePath);

		Map<String, List<Long>> restoredWordLexemesMap = new HashMap<>();
		Count resolvedCollocationCount = new Count();
		Count existingPosGroupCount = new Count();
		Count existingRelGroupCount = new Count();
		Count createdPosGroupCount = new Count();
		Count createdRelGroupCount = new Count();
		Count createdLexCollocCount = new Count();
		Count duplicateCollocCount = new Count();

		List<String> restorableWords = new ArrayList<>(sourceFileCollocDataMap.keySet());
		Collections.sort(restorableWords);

		for (String restorableWord : restorableWords) {
			List<RestorableWordCollocData> sourceFileCollocWordEntries = sourceFileCollocDataMap.get(restorableWord);
			for (RestorableWordCollocData sourceFileCollocWordEntry : sourceFileCollocWordEntries) {
				List<CollocPosGroup> posGroups = sourceFileCollocWordEntry.getPosGroups();
				for (CollocPosGroup posGroup : posGroups) {
					String posGroupCode = posGroup.getCode();
					List<CollocRelGroup> relGroups = posGroup.getRelGroups();
					for (CollocRelGroup relGroup : relGroups) {
						String relGroupName = relGroup.getName();
						Float frequency = relGroup.getFrequency();
						Float score = relGroup.getScore();
						List<LexColloc> sourceFileLexCollocs = relGroup.getLexCollocs();
						for (LexColloc lexColloc : sourceFileLexCollocs) {
							Long collocationId = lexColloc.getCollocationId();
							String collocationValue = lexColloc.getCollocationValue();
							List<RestorableCollocReportRow> reportFileCollocDataMatches = getReportFileCollocDataMatches(restorableWord, collocationId, reportFileCollocDataRows);
							List<Long> lexemeIds = new ArrayList<>();
							if (CollectionUtils.isEmpty(reportFileCollocDataMatches)) {
								List<WordIdCandidate> wordIdCandidates = getWordIdCandidates(restorableWord);
								wordIdCandidates = wordIdCandidates.stream().filter(row -> CollectionUtils.isNotEmpty(row.getLexemeIdCandidates())).collect(Collectors.toList());
								if (CollectionUtils.isEmpty(wordIdCandidates)) {
									logger.info("No matching candidates in report nor in db file for \"{}\" in \"{}\"", restorableWord, collocationValue);
								} else if (wordIdCandidates.size() > 1) {
									logger.info("No matching candidates in report file, too many words in db for \"{}\" in \"{}\"", restorableWord, collocationValue);
								} else {
									WordIdCandidate singleWordIdCandidate = wordIdCandidates.get(0);
									List<Long> lexemeIdCandidates = singleWordIdCandidate.getLexemeIdCandidates();
									if (lexemeIdCandidates.size() > 1) {
										logger.info("No matching candidates in report file, too many lexemes in db for \"{}\" in \"{}\"", restorableWord, collocationValue);
									} else {
										Long dbLexemeId = lexemeIdCandidates.get(0);
										lexemeIds.add(dbLexemeId);
									}
								}
							} else {
								List<Long> reportLexemeIds = reportFileCollocDataMatches.stream().map(RestorableCollocReportRow::getLexemeId).collect(Collectors.toList());
								lexemeIds.addAll(reportLexemeIds);
							}
							if (CollectionUtils.isNotEmpty(lexemeIds)) {
								resolvedCollocationCount.increment();
							}
							for (Long lexemeId : lexemeIds) {
								Map<String, Object> existingLexColloc = getLexemeCollocation(lexemeId, collocationId);
								if (existingLexColloc != null) {
									duplicateCollocCount.increment();
									continue;
								}
								List<CollocPosRelGroupTuple> collocPosRelGroupTuples = getCollocPosRelGroupTuples(lexemeId);
								Map<String, Long> posGroupIdMap = collocPosRelGroupTuples.stream()
										.map(row -> new CollocPosRelGroupTuple(null, row.getPosGroupId(), row.getPosGroupCode(), null, null))
										.distinct()
										.collect(Collectors.toMap(CollocPosRelGroupTuple::getPosGroupCode, CollocPosRelGroupTuple::getPosGroupId));
								Map<String, Map<String, Long>> posRelGroupIdMap = collocPosRelGroupTuples.stream()
										.collect(Collectors.groupingBy(CollocPosRelGroupTuple::getPosGroupCode,
												Collectors.toMap(CollocPosRelGroupTuple::getRelGroupName, CollocPosRelGroupTuple::getRelGroupId)));
								Long posGroupId = posGroupIdMap.get(posGroupCode);
								if (posGroupId == null) {
									posGroupId = createCollocPosGroup(lexemeId, posGroupCode);
									createdPosGroupCount.increment();
								} else {
									existingPosGroupCount.increment();
								}
								Map<String, Long> relGroupIdMap = posRelGroupIdMap.get(posGroupCode);
								Long relGroupId = null;
								if (relGroupIdMap == null) {
									relGroupId = createCollocRelGroup(posGroupId, relGroupName, frequency, score);
									createdRelGroupCount.increment();
								} else {
									relGroupId = relGroupIdMap.get(relGroupName);
									if (relGroupId == null) {
										relGroupId = createCollocRelGroup(posGroupId, relGroupName, frequency, score);
										createdRelGroupCount.increment();
									} else {
										existingRelGroupCount.increment();
									}
								}
								createLexemeCollocation(lexemeId, relGroupId, lexColloc);
								createdLexCollocCount.increment();
								List<Long> mappedLexemes = restoredWordLexemesMap.get(restorableWord);
								if (mappedLexemes == null) {
									mappedLexemes = new ArrayList<>();
									restoredWordLexemesMap.put(restorableWord, mappedLexemes);
								}
								if (!mappedLexemes.contains(lexemeId)) {
									mappedLexemes.add(lexemeId);
								}
							}
						}
					}
				}
			}
		}

		logger.info("Resolved collocations: {}", resolvedCollocationCount.getValue());
		logger.info("Existing pos groups: {}", existingPosGroupCount.getValue());
		logger.info("Existing rel groups: {}", existingRelGroupCount.getValue());
		logger.info("Created pos groups: {}", createdPosGroupCount.getValue());
		logger.info("Created rel groups: {}", createdRelGroupCount.getValue());
		logger.info("Created lex collocs: {}", createdLexCollocCount.getValue());
		logger.info("Duplicate colloc mappings: {}", duplicateCollocCount.getValue());
		logger.info("Restored word count: {}", restoredWordLexemesMap.keySet().size());

		List<String> restoredWords = new ArrayList<>(restoredWordLexemesMap.keySet());
		Collections.sort(restoredWords);

		for (String restoredWord : restoredWords) {
			List<Long> restoredLexemeIds = restoredWordLexemesMap.get(restoredWord);
			logger.debug("{} -> {}", restoredWord, restoredLexemeIds);
		}

		logger.info("Done!");
	}

	private List<RestorableCollocReportRow> extractRestorableCollocReportRows(String collocRestoreMappingFilePath) throws Exception {
		List<RestorableCollocReportRow> restorableCollocReportRows = new ArrayList<>();
		FileInputStream collocRestoreMappingFileInputStream = new FileInputStream(collocRestoreMappingFilePath);
		List<String> collocRestoreMappingLines = getContentLines(collocRestoreMappingFileInputStream);
		collocRestoreMappingLines.remove(0);//remove heading

		for (String collocRestoreMappingLine : collocRestoreMappingLines) {
			if (StringUtils.isEmpty(collocRestoreMappingLine)) {
				continue;
			}
			String[] collocRestoreMappingCells = StringUtils.split(collocRestoreMappingLine, CSV_SEPARATOR);
			if (collocRestoreMappingCells.length != 5) {
				throw new DataLoadingException("Incorrect colloc restore mapping file row: " + collocRestoreMappingLine);
			}
			Long collocationId = Long.valueOf(collocRestoreMappingCells[0]);
			String collocationValue = collocRestoreMappingCells[1].trim();
			String word = collocRestoreMappingCells[2].trim();
			String wordIdStr = collocRestoreMappingCells[3].trim();
			String lexemeIdStr = collocRestoreMappingCells[4].trim();
			if (StringUtils.equals(wordIdStr, String.valueOf(CSV_EMPTY_CELL))) {
				continue;
			}
			if (StringUtils.equals(lexemeIdStr, String.valueOf(CSV_EMPTY_CELL))) {
				continue;
			}
			Long wordId = Long.valueOf(wordIdStr);
			Long lexemeId = Long.valueOf(lexemeIdStr);
			RestorableCollocReportRow restorableCollocReportRow = new RestorableCollocReportRow(collocationValue, collocationId, word, wordId, lexemeId);
			restorableCollocReportRows.add(restorableCollocReportRow);
		}
		return restorableCollocReportRows;
	}

	private List<RestorableCollocReportRow> getReportFileCollocDataMatches(String word, Long collocationId, List<RestorableCollocReportRow> allReportFileCollocDataRows) {
		List<RestorableCollocReportRow> reportFileCollocDataMatches = allReportFileCollocDataRows.stream()
				.filter(row -> row.getCollocationId().equals(collocationId) && StringUtils.equals(row.getWord(), word))
				.collect(Collectors.toList());
		return reportFileCollocDataMatches;
	}

	private Map<String, List<RestorableWordCollocData>> extractRestorableCollocFileData(List<Node> articleNodes) throws Exception {

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode, collocRelGroupNameNode, collocRelGroupFreqNode, collocRelGroupScoreNode;
		List<Node> meaningBlockNodes, collocPosGroupNodes, collocRelGroupNodes, collocGroupNodes;
		String word, collocPosGroupCode, collocRelGroupName;
		List<CollocMember> collocMembers;
		List<CollocWordsRecord> existingCollocRecords;
		List<String> existingCollocMemberWords;
		List<CollocPosGroup> collocPosGroups;
		List<CollocRelGroup> collocRelGroups;
		List<LexColloc> lexCollocs;

		Map<String, List<RestorableWordCollocData>> restorableWordCollocDataMap = new HashMap<>();

		long articleCount = articleNodes.size();
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
			word = wordNode.getTextTrim();
			word = textDecorationService.removeEkiEntityMarkup(word);

			meaningBlockNodes = contentNode.selectNodes(meaningBlockExp);
			collocPosGroups = new ArrayList<>();

			for (Node meaningBlockNode : meaningBlockNodes) {

				collocPosGroupNodes = meaningBlockNode.selectNodes(collocPosGroupExp);//x:colp/x:cmg

				for (Node colPosGroupNode : collocPosGroupNodes) {

					collocPosGroupCode = ((Element) colPosGroupNode).attributeValue(collocPosAttr);
					collocRelGroupNodes = colPosGroupNode.selectNodes(collocRelGroupExp);//x:relg
					collocRelGroups = new ArrayList<>();

					for (Node collocRelGroupNode : collocRelGroupNodes) {

						collocRelGroupNameNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupNameExp);
						collocRelGroupName = collocRelGroupNameNode.getTextTrim();

						Float collocRelGroupFreq = null;
						collocRelGroupFreqNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupFreqExp);
						if (collocRelGroupFreqNode != null) {
							try {
								collocRelGroupFreq = Float.parseFloat(collocRelGroupFreqNode.getTextTrim());
							} catch (Exception e) {
							}
						}

						Float collocRelGroupScore = null;
						collocRelGroupScoreNode = (Element) collocRelGroupNode.selectSingleNode(collocRelGroupScoreExp);
						if (collocRelGroupScoreNode != null) {
							try {
								collocRelGroupScore = Float.parseFloat(collocRelGroupScoreNode.getTextTrim());
							} catch (Exception e) {
							}
						}

						collocGroupNodes = collocRelGroupNode.selectNodes(collocGroupExp);//x:colg
						lexCollocs = new ArrayList<>();
						int collocGroupOrder = 0;

						List<String> handledCollocValues = new ArrayList<>();

						for (Node collocGroupNode : collocGroupNodes) {

							collocMembers = extractCollocMembers(collocGroupNode);
							if (CollectionUtils.isEmpty(collocMembers)) {
								continue;
							}

							collocGroupOrder++;
							List<List<CollocMember>> collocMembersPermutations = composeCollocMembersPermutations(collocMembers);

							for (List<CollocMember> sourceCollocMembers : collocMembersPermutations) {

								String collocationValue = composeCollocValue(sourceCollocMembers);
								if (handledCollocValues.contains(collocationValue)) {
									continue;
								}
								handledCollocValues.add(collocationValue);
								existingCollocRecords = getCollocWordsRecords(collocationValue);

								if (CollectionUtils.isEmpty(existingCollocRecords)) {
									continue;
								}

								boolean completeCollocVersionExists = existingCollocRecords.stream()
										.anyMatch(existingCollocRecord -> existingCollocRecord.getMemberWords().size() == sourceCollocMembers.size());

								if (completeCollocVersionExists) {
									continue;
								}
								for (CollocWordsRecord existingCollocRecord : existingCollocRecords) {

									existingCollocMemberWords = existingCollocRecord.getMemberWords();
									if (existingCollocMemberWords.size() < sourceCollocMembers.size()) {

										int memberOrder = 0;
										for (CollocMember sourceCollocMember : sourceCollocMembers) {

											memberOrder++;
											String sourceCollocMemberWord = sourceCollocMember.getWord();
											if (StringUtils.equals(sourceCollocMemberWord, word)) {
												if (!existingCollocMemberWords.contains(word)) {
													Long collocationId = existingCollocRecord.getCollocationId();
													String collocMemberForm = sourceCollocMember.getForm();
													String collocMemberConjunct = sourceCollocMember.getConjunct();
													LexColloc lexColloc = new LexColloc();
													lexColloc.setCollocationId(collocationId);
													lexColloc.setCollocationValue(collocationValue);
													lexColloc.setMemberForm(collocMemberForm);
													lexColloc.setConjunct(collocMemberConjunct);
													lexColloc.setWeight(inboundPrimaryCollocMemberWeight);
													lexColloc.setMemberOrder(memberOrder);
													lexColloc.setGroupOrder(collocGroupOrder);
													lexCollocs.add(lexColloc);
												}
											}
										}
									}
								}
							}
						}
						if (CollectionUtils.isNotEmpty(lexCollocs)) {
							CollocRelGroup collocRelGroup = new CollocRelGroup();
							collocRelGroup.setName(collocRelGroupName);
							collocRelGroup.setFrequency(collocRelGroupFreq);
							collocRelGroup.setScore(collocRelGroupScore);
							collocRelGroup.setLexCollocs(lexCollocs);
							collocRelGroups.add(collocRelGroup);
						}
					}
					if (CollectionUtils.isNotEmpty(collocRelGroups)) {
						CollocPosGroup collocPosGroup = new CollocPosGroup();
						collocPosGroup.setCode(collocPosGroupCode);
						collocPosGroup.setRelGroups(collocRelGroups);
						collocPosGroups.add(collocPosGroup);
					}
				}
			}

			if (CollectionUtils.isNotEmpty(collocPosGroups)) {
				RestorableWordCollocData restorableWordCollocData = new RestorableWordCollocData();
				restorableWordCollocData.setWord(word);
				restorableWordCollocData.setPosGroups(collocPosGroups);
				List<RestorableWordCollocData> restorableWordCollocsDatas = restorableWordCollocDataMap.get(word);
				if (restorableWordCollocsDatas == null) {
					restorableWordCollocsDatas = new ArrayList<>();
					restorableWordCollocDataMap.put(word, restorableWordCollocsDatas);
				}
				restorableWordCollocsDatas.add(restorableWordCollocData);
			}

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		//collect collcation ids near word
		for (String restorableWord : restorableWordCollocDataMap.keySet()) {
			List<RestorableWordCollocData> restorableWordCollocDatas = restorableWordCollocDataMap.get(restorableWord);
			for (RestorableWordCollocData restorableWordCollocData : restorableWordCollocDatas) {
				List<CollocPosGroup> restorableWordPosGroups = restorableWordCollocData.getPosGroups();
				List<Long> collocationIds = new ArrayList<>();
				restorableWordPosGroups.forEach(collocPosGr -> collocPosGr.getRelGroups()
						.forEach(collocRelGr -> collocRelGr.getLexCollocs()
								.forEach(lexColloc -> {
									collocationIds.add(lexColloc.getCollocationId());
								})));
				restorableWordCollocData.setCollocationIds(collocationIds);
			}
		}

		return restorableWordCollocDataMap;
	}

	private List<CollocPosRelGroupTuple> getCollocPosRelGroupTuples(Long lexemeId) throws Exception {
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<CollocPosRelGroupTuple> results = basicDbService.getResults(sqlSelectCollocPosRelGroups, paramMap, new CollocPosRelGroupTupleRowMapper());
		return results;
	}

	private Long createCollocPosGroup(Long lexemeId, String posGroupCode) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("lexeme_id", lexemeId);
		paramMap.put("pos_group_code", posGroupCode);
		Long collocPosGroupId = basicDbService.create(LEX_COLLOC_POS_GROUP, paramMap);
		return collocPosGroupId;
	}

	private Long createCollocRelGroup(Long collocPosGroupId, String name, Float frequency, Float score) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("pos_group_id", collocPosGroupId);
		paramMap.put("name", name);
		if (frequency != null) {
			paramMap.put("frequency", frequency);
		}
		if (score != null) {
			paramMap.put("score", score);
		}
		Long collocRelGroupId = basicDbService.create(LEX_COLLOC_REL_GROUP, paramMap);
		return collocRelGroupId;
	}

	private Long createLexemeCollocation(Long lexemeId, Long relGroupId, LexColloc collocMemberRecord) throws Exception {

		Long collocationId = collocMemberRecord.getCollocationId();
		String memberForm = collocMemberRecord.getMemberForm();
		String conjunct = collocMemberRecord.getConjunct();
		Float weight = collocMemberRecord.getWeight();
		Integer groupOrder = collocMemberRecord.getGroupOrder();
		Integer memberOrder = collocMemberRecord.getMemberOrder();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("lexeme_id", lexemeId);
		paramMap.put("collocation_id", collocationId);
		if (relGroupId != null) {
			paramMap.put("rel_group_id", relGroupId);
		}
		paramMap.put("member_form", memberForm);
		if (StringUtils.isNotBlank(conjunct)) {
			paramMap.put("conjunct", conjunct);
		}
		paramMap.put("weight", weight);
		paramMap.put("member_order", memberOrder);
		if (groupOrder != null) {
			paramMap.put("group_order", groupOrder);
		}
		Long lexCollocId = basicDbService.create(LEX_COLLOC, paramMap);
		return lexCollocId;
	}

	private Map<String, Object> getLexemeCollocation(Long lexemeId, Long collocationId) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("lexeme_id", lexemeId);
		paramMap.put("collocation_id", collocationId);
		Map<String, Object> result = basicDbService.select(LEX_COLLOC, paramMap);
		return result;
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

	class IncompleteColloc extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long collocationId;

		private String collocationValue;

		private List<String> missingCollocMemberWords;

		public IncompleteColloc(Long collocationId, String collocationValue, List<String> missingCollocMemberWords) {
			this.collocationId = collocationId;
			this.collocationValue = collocationValue;
			this.missingCollocMemberWords = missingCollocMemberWords;
		}

		public Long getCollocationId() {
			return collocationId;
		}

		public String getCollocationValue() {
			return collocationValue;
		}

		public List<String> getMissingCollocMemberWords() {
			return missingCollocMemberWords;
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

	class RestorableWordCollocData extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String word;

		private List<CollocPosGroup> posGroups;

		private List<Long> collocationIds;

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public List<CollocPosGroup> getPosGroups() {
			return posGroups;
		}

		public void setPosGroups(List<CollocPosGroup> posGroups) {
			this.posGroups = posGroups;
		}

		public List<Long> getCollocationIds() {
			return collocationIds;
		}

		public void setCollocationIds(List<Long> collocationIds) {
			this.collocationIds = collocationIds;
		}
	}

	class CollocPosGroup extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String code;

		private List<CollocRelGroup> relGroups;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public List<CollocRelGroup> getRelGroups() {
			return relGroups;
		}

		public void setRelGroups(List<CollocRelGroup> relGroups) {
			this.relGroups = relGroups;
		}
	}

	class CollocRelGroup extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String name;

		private Float frequency;

		private Float score;

		private List<LexColloc> lexCollocs;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Float getFrequency() {
			return frequency;
		}

		public void setFrequency(Float frequency) {
			this.frequency = frequency;
		}

		public Float getScore() {
			return score;
		}

		public void setScore(Float score) {
			this.score = score;
		}

		public List<LexColloc> getLexCollocs() {
			return lexCollocs;
		}

		public void setLexCollocs(List<LexColloc> lexCollocs) {
			this.lexCollocs = lexCollocs;
		}
	}

	class LexColloc extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long collocationId;

		private String collocationValue;

		private String memberForm;

		private String conjunct;

		private Float weight;

		private Integer memberOrder;

		private Integer groupOrder;

		public Long getCollocationId() {
			return collocationId;
		}

		public void setCollocationId(Long collocationId) {
			this.collocationId = collocationId;
		}

		public String getCollocationValue() {
			return collocationValue;
		}

		public void setCollocationValue(String collocationValue) {
			this.collocationValue = collocationValue;
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

		public Integer getMemberOrder() {
			return memberOrder;
		}

		public void setMemberOrder(Integer memberOrder) {
			this.memberOrder = memberOrder;
		}

		public Integer getGroupOrder() {
			return groupOrder;
		}

		public void setGroupOrder(Integer groupOrder) {
			this.groupOrder = groupOrder;
		}
	}

	class RestorableCollocReportRow extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String collocationValue;

		private Long collocationId;

		private String word;

		private Long wordId;

		private Long lexemeId;

		public RestorableCollocReportRow(String collocationValue, Long collocationId, String word, Long wordId, Long lexemeId) {
			this.collocationValue = collocationValue;
			this.collocationId = collocationId;
			this.word = word;
			this.wordId = wordId;
			this.lexemeId = lexemeId;
		}

		public String getCollocationValue() {
			return collocationValue;
		}

		public Long getCollocationId() {
			return collocationId;
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
	}

	class CollocPosRelGroupTuple extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long lexemeId;

		private Long posGroupId;

		private String posGroupCode;

		private Long relGroupId;

		private String relGroupName;

		public CollocPosRelGroupTuple(Long lexemeId, Long posGroupId, String posGroupCode, Long relGroupId, String relGroupName) {
			this.lexemeId = lexemeId;
			this.posGroupId = posGroupId;
			this.posGroupCode = posGroupCode;
			this.relGroupId = relGroupId;
			this.relGroupName = relGroupName;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public Long getPosGroupId() {
			return posGroupId;
		}

		public String getPosGroupCode() {
			return posGroupCode;
		}

		public Long getRelGroupId() {
			return relGroupId;
		}

		public String getRelGroupName() {
			return relGroupName;
		}
	}

	class CollocPosRelGroupTupleRowMapper implements RowMapper<CollocPosRelGroupTuple> {

		@Override
		public CollocPosRelGroupTuple mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long lexemeId = rs.getObject("lexeme_id", Long.class);
			Long posGroupId = rs.getObject("pos_group_id", Long.class);
			String posGroupCode = rs.getObject("pos_group_code", String.class);
			Long relGroupId = rs.getObject("rel_group_id", Long.class);
			String relGroupName = rs.getObject("rel_group_name", String.class);
			return new CollocPosRelGroupTuple(lexemeId, posGroupId, posGroupCode, relGroupId, relGroupName);
		}
	}
}
