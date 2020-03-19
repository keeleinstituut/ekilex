package eki.ekilex.runner;

import java.io.InputStream;
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
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
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

import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.service.TextDecorationService;
import eki.ekilex.service.ReportComposer;

@Component
public class CollocMemberRestorerRunner extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(CollocMemberRestorerRunner.class);

	private static final String REPORT_MISSING_COLLOC_MEMBER_CANDIDATES = "missing_colloc_member_candidates";

	private static final String SQL_SELECT_COLLOC_MEMBERS_PATH = "sql/select_colloc_members.sql";

	private static final String SQL_SELECT_WORD_ID_CANDIDATES_BY_VALUE_PATH = "sql/select_word_id_candidates_by_value.sql";

	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordPosExp = "x:sl";
	private final String articleBodyExp = "x:S";
	private final String meaningBlockExp = "x:tp[not(@x:as='ab')]";
	private final String collocPosGroupExp = "x:colp/x:cmg";
	private final String collocRelGroupExp = "x:relg";
	private final String collocGroupExp = "x:colg";

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

	private String sqlSelectCollocMembers;

	private String sqlSelectWordIdCandidatesByValue;

	@Autowired
	private TextDecorationService textDecorationService;

	private ReportComposer reportComposer;

	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_COLLOC_MEMBERS_PATH);
		sqlSelectCollocMembers = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_ID_CANDIDATES_BY_VALUE_PATH);
		sqlSelectWordIdCandidatesByValue = getContent(resourceFileInputStream);
	}

	@Transactional
	public void execute(String dataXmlFilePath) throws Exception {

		reportComposer = new ReportComposer("colloc member restorer", REPORT_MISSING_COLLOC_MEMBER_CANDIDATES);

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());
		long articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Count invalidCollocCount = new Count();

		Element headerNode, contentNode, wordNode, wordGroupNode, wordPosNode;
		List<Node> collocGroupNodes;
		String word;
		List<CollocMember> collocMembers;
		List<CollocRecord> existingCollocRecords;
		List<String> parsedCollocMemberWords;
		List<String> existingCollocMemberWords;

		List<String> allMissingCollocMemberWords = new ArrayList<>();
		List<Long> affectedCollocIds = new ArrayList<>();

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

					existingCollocRecords = getCollocRecords(collocation);

					if (CollectionUtils.isEmpty(existingCollocRecords)) {
						logger.warn("No such collocation exists: {}", collocation);
					} else {
						for (CollocRecord existingCollocRecord : existingCollocRecords) {

							existingCollocMemberWords = existingCollocRecord.getMemberWords();
							if (collocMembersPermutation.size() > existingCollocMemberWords.size()) {
								Collection<String> missingCollocMemberWords = CollectionUtils.disjunction(parsedCollocMemberWords, existingCollocMemberWords);
								allMissingCollocMemberWords.addAll(missingCollocMemberWords);
								affectedCollocIds.add(existingCollocRecord.getCollocationId());
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

		allMissingCollocMemberWords = allMissingCollocMemberWords.stream().distinct().sorted().collect(Collectors.toList());
		affectedCollocIds = affectedCollocIds.stream().distinct().collect(Collectors.toList());

		for (String missingCollocMemberWord : allMissingCollocMemberWords) {
			List<WordIdCandidate> allWordIdCandidates = getWordIdCandidates(missingCollocMemberWord);
			if (CollectionUtils.isEmpty(allWordIdCandidates)) {
				appendToReport(REPORT_MISSING_COLLOC_MEMBER_CANDIDATES, missingCollocMemberWord, CSV_EMPTY_CELL, CSV_EMPTY_CELL);
			} else {
				List<WordIdCandidate> wordIdCandidatesWithLexemes = allWordIdCandidates.stream()
						.filter(wordIdCandidate -> CollectionUtils.isNotEmpty(wordIdCandidate.getLexemeIdCandidates()))
						.collect(Collectors.toList());
				if (CollectionUtils.isEmpty(wordIdCandidatesWithLexemes)) {
					List<Long> wordIdCandidates = allWordIdCandidates.stream().map(WordIdCandidate::getWordId).collect(Collectors.toList());
					String wordIdCandidatesStr = StringUtils.join(wordIdCandidates, ", ");
					appendToReport(REPORT_MISSING_COLLOC_MEMBER_CANDIDATES, missingCollocMemberWord, wordIdCandidatesStr, CSV_EMPTY_CELL);
				} else {
					for (WordIdCandidate wordIdCandidate : wordIdCandidatesWithLexemes) {
						List<Long> lexemeIdCandidates = wordIdCandidate.getLexemeIdCandidates();
						String lexemeIdCandidatesStr = StringUtils.join(lexemeIdCandidates, ", ");
						appendToReport(REPORT_MISSING_COLLOC_MEMBER_CANDIDATES, missingCollocMemberWord, wordIdCandidate.getWordId(), lexemeIdCandidatesStr);
					}
				}
			}
		}

		reportComposer.end();

		logger.info("Invalid colloc count: {}", invalidCollocCount.getValue());
		logger.info("Missing colloc member count: {}", allMissingCollocMemberWords.size());
		logger.info("Affected colloc count: {}", affectedCollocIds.size());
	}

	private List<CollocRecord> getCollocRecords(String collocation) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("collocation", collocation);
		List<CollocRecord> collocRecords = basicDbService.getResults(sqlSelectCollocMembers, paramMap, new CollocRecordRowMapper());
		return collocRecords;
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

	private void appendToReport(String reportName, Object... reportCells) throws Exception {
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
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

	class CollocRecord extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long collocationId;

		private List<String> memberWords;

		public CollocRecord(Long collocationId, List<String> memberWords) {
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

	class CollocRecordRowMapper implements RowMapper<CollocRecord> {

		@Override
		public CollocRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long collocationId = rs.getObject("collocation_id", Long.class);
			Array valueArrayObj = rs.getArray("member_words");
			String[] valueArray = (String[]) valueArrayObj.getArray();
			List<String> memberWords = Arrays.asList(valueArray);
			return new CollocRecord(collocationId, memberWords);
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
}
