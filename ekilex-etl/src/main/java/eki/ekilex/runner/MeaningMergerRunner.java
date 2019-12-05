package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.ekilex.data.transform.Freeform;
import eki.ekilex.data.util.FreeformRowMapper;

@Component
public class MeaningMergerRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MeaningMergerRunner.class);

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_PS = "sql/select_meaning_join_candidates_for_ps.sql";

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_COL = "sql/select_meaning_join_candidates_for_col.sql";

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_QQ = "sql/select_meaning_join_candidates_for_qq.sql";

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_EV = "sql/select_meaning_join_candidates_for_ev.sql";

	private List<String> sqlSelectMeaningJoinCandidatesForDatasets;

	private String sqlSelectMeaningFreeforms = "select ff.* from " + FREEFORM + " ff, " + MEANING_FREEFORM + " mff where mff.freeform_id = ff.id and mff.meaning_id = :meaningId";

	private String sqlUpdateMeaningFreeformIds = "update " + MEANING_FREEFORM + " set meaning_id = :targetMeaningId where meaning_id = :sourceMeaningId and freeform_id in (:freeformIds)";

	private String sqlSelectLexemeFreeforms = "select ff.* from " + FREEFORM + " ff, " + LEXEME_FREEFORM + " lff where lff.freeform_id = ff.id and lff.lexeme_id = :lexemeId";

	private String sqlUpdateLexemeFreeformIds = "update " + LEXEME_FREEFORM + " set lexeme_id = :targetLexemeId where lexeme_id = :sourceLexemeId and freeform_id in (:freeformIds)";

	private String compoundDatasetCode;

	@Override
	public String getDataset() {
		return compoundDatasetCode;
	}

	@Override
	protected String getLogEventBy() {
		return "Ekilex " + getDataset() + " tähenduste liitja";
	}

	@Override
	public Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	public Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	public Complexity getFreeformComplexity() {
		return null;
	}

	@Override
	public void deleteDatasetData() throws Exception {
	}

	@Override
	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;
		String sqlSelectMeaningJoinCandidatesForDataset;

		sqlSelectMeaningJoinCandidatesForDatasets = new ArrayList<>();

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_PS);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_COL);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_QQ);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_EV);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);
	}

	@Transactional
	public void execute(String compoundDatasetCode, boolean doReports) throws Exception {
		this.compoundDatasetCode = compoundDatasetCode;

		start();

		Map<String, List<MeaningJoinCandidate>> joinableWordMeaningPairMap = collectWordMeaningJoinCandidates(compoundDatasetCode);
		int wordMeaningPairCount = joinableWordMeaningPairMap.size();

		logger.debug("Total joinable word meaning pairs {}", wordMeaningPairCount);

		Map<String, Count> updateCountMap = new HashMap<>();
		updateCountMap.put(MEANING_FREEFORM, new Count());
		updateCountMap.put(MEANING_NR, new Count());
		updateCountMap.put(MEANING_DOMAIN, new Count());
		updateCountMap.put(MEANING_SEMANTIC_TYPE, new Count());
		updateCountMap.put(MEANING_LIFECYCLE_LOG, new Count());
		updateCountMap.put(MEANING_PROCESS_LOG, new Count());
		updateCountMap.put(DEFINITION, new Count());
		updateCountMap.put(LEXEME_FREEFORM, new Count());
		updateCountMap.put(LEXEME_FREQUENCY, new Count());
		updateCountMap.put(LEXEME_REGISTER, new Count());
		updateCountMap.put(LEXEME_POS, new Count());
		updateCountMap.put(LEXEME_DERIV, new Count());
		updateCountMap.put(LEXEME_REGION, new Count());
		updateCountMap.put(LEXEME_SOURCE_LINK, new Count());
		updateCountMap.put(LEX_COLLOC, new Count());
		updateCountMap.put(LEX_COLLOC_POS_GROUP, new Count());
		updateCountMap.put(LEXEME_LIFECYCLE_LOG, new Count());
		updateCountMap.put(LEXEME_PROCESS_LOG, new Count());
		updateCountMap.put(LEXEME_RELATION, new Count());

		long wordMeaningPairCounter = 0;
		long progressIndicator = wordMeaningPairCount / Math.min(wordMeaningPairCount, 100);

		for (List<MeaningJoinCandidate> wordMeaningJoinCandidates : joinableWordMeaningPairMap.values()) {

			MeaningJoinCandidate firstMeaningJoinCandidate = wordMeaningJoinCandidates.get(0);
			Long ssLexemeId = firstMeaningJoinCandidate.getSsLexemeId();
			Long ssMeaningId = firstMeaningJoinCandidate.getSsMeaningId();
			List<Long> compLexemeIds = wordMeaningJoinCandidates.stream().map(cand -> cand.getCompLexemeId()).collect(Collectors.toList());
			List<Long> compMeaningIds = wordMeaningJoinCandidates.stream().map(cand -> cand.getCompMeaningId()).collect(Collectors.toList());

			//TODO move meaning other lexemes

			moveMeaningFreeforms(ssMeaningId, compMeaningIds, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_NR, new String[] {"dataset_code"}, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_DOMAIN, new String[] {"domain_origin", "domain_code"}, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_SEMANTIC_TYPE, new String[] {"semantic_type_code"}, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_LIFECYCLE_LOG, null, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_PROCESS_LOG, null, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, DEFINITION, new String[] {"value"}, updateCountMap);

			handleSumComplexity(ssLexemeId, compLexemeIds);//TODO impl
			moveLexemeFreeforms(ssLexemeId, compLexemeIds, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_FREQUENCY, new String[] {"source_name"}, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_REGISTER, new String[] {"register_code"}, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_POS, new String[] {"pos_code"}, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_DERIV, new String[] {"deriv_code"}, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_REGION, new String[] {"region_code"}, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_SOURCE_LINK, new String[] {"source_id"}, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEX_COLLOC, null, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEX_COLLOC_POS_GROUP, null, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_LIFECYCLE_LOG, null, updateCountMap);
			moveData("lexeme_id", ssLexemeId, compLexemeIds, LEXEME_PROCESS_LOG, null, updateCountMap);
			moveLexemeRelations(ssLexemeId, compLexemeIds, updateCountMap);

			//TODO delete comp lexemes and potential floaters

			// progress
			wordMeaningPairCounter++;
			if (wordMeaningPairCounter % progressIndicator == 0) {
				long progressPercent = wordMeaningPairCounter / progressIndicator;
				logger.debug("{}% - {} word meaning pairs iterated", progressPercent, wordMeaningPairCounter);
			}
		}

		// TODO delete floating comp meanings

		for (Entry<String, Count> updateCountEntry : updateCountMap.entrySet()) {
			System.out.println(updateCountEntry);
		}

		end();
	}

	private Map<String, List<MeaningJoinCandidate>> collectWordMeaningJoinCandidates(String compoundDatasetCode) throws Exception {

		Map<String, List<MeaningJoinCandidate>> joinableWordMeaningPairMap = new HashMap<>();

		for (String sqlSelectMeaningJoinCandidatesForDataset : sqlSelectMeaningJoinCandidatesForDatasets) {

			List<MeaningJoinCandidate> joinCandidates = getJoinCandidatesForDataset(sqlSelectMeaningJoinCandidatesForDataset, compoundDatasetCode);
			logger.debug("{} join candidates collected for a dataset", joinCandidates.size());

			for (MeaningJoinCandidate joinCandidate : joinCandidates) {
				Long wordId = joinCandidate.getWordId();
				Long ssMeaningId = joinCandidate.getSsMeaningId();
				String wordMeaningPair = wordId + "-" + ssMeaningId;
				List<MeaningJoinCandidate> wordMeaningJoinCandidates = joinableWordMeaningPairMap.get(wordMeaningPair);
				if (wordMeaningJoinCandidates == null) {
					wordMeaningJoinCandidates = new ArrayList<>();
					joinableWordMeaningPairMap.put(wordMeaningPair, wordMeaningJoinCandidates);
				}
				wordMeaningJoinCandidates.add(joinCandidate);
			}
		}
		return joinableWordMeaningPairMap;
	}

	private List<MeaningJoinCandidate> getJoinCandidatesForDataset(String sqlSelectMeaningJoinCandidatesForDataset, String compoundDatasetCode) throws Exception {
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("compoundDatasetCode", compoundDatasetCode);
		List<MeaningJoinCandidate> joinCandidates = basicDbService.getResults(sqlSelectMeaningJoinCandidatesForDataset, tableRowParamMap, new MeaningJoinCandidateRowMapper());
		return joinCandidates;
	}

	private void moveMeaningFreeforms(Long ssMeaningId, List<Long> compMeaningIds, Map<String, Count> updateCountMap) throws Exception {

		FreeformRowMapper freeformRowMapper = new FreeformRowMapper();
		Map<String, Object> paramMap;

		paramMap = new HashMap<>();
		paramMap.put("meaningId", ssMeaningId);
		List<Freeform> ssLexemeFreeforms = basicDbService.getResults(sqlSelectMeaningFreeforms, paramMap, freeformRowMapper);

		int updateCount;

		for (Long compMeaningId : compMeaningIds) {

			paramMap = new HashMap<>();
			paramMap.put("meaningId", compMeaningId);
			List<Freeform> compLexemeFreeforms = basicDbService.getResults(sqlSelectMeaningFreeforms, paramMap, freeformRowMapper);
			List<Long> movableFreeformIds = filterNewFreeformIds(ssLexemeFreeforms, compLexemeFreeforms);

			if (CollectionUtils.isNotEmpty(movableFreeformIds)) {
				paramMap = new HashMap<>();
				paramMap.put("targetMeaningId", ssMeaningId);
				paramMap.put("sourceMeaningId", compMeaningId);
				paramMap.put("freeformIds", movableFreeformIds);
				updateCount = basicDbService.executeScript(sqlUpdateMeaningFreeformIds, paramMap);
				updateCountMap.get(MEANING_FREEFORM).increment(updateCount);
			}
		}
	}

	private void handleSumComplexity(Long ssLexemeId, List<Long> compLexemeIds) {
		// TODO impl
	}

	private void moveLexemeFreeforms(Long ssLexemeId, List<Long> compLexemeIds, Map<String, Count> updateCountMap) throws Exception {

		FreeformRowMapper freeformRowMapper = new FreeformRowMapper();
		Map<String, Object> paramMap;

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", ssLexemeId);
		List<Freeform> ssLexemeFreeforms = basicDbService.getResults(sqlSelectLexemeFreeforms, paramMap, freeformRowMapper);

		int updateCount;

		for (Long compLexemeId : compLexemeIds) {

			paramMap = new HashMap<>();
			paramMap.put("lexemeId", compLexemeId);
			List<Freeform> compLexemeFreeforms = basicDbService.getResults(sqlSelectLexemeFreeforms, paramMap, freeformRowMapper);
			List<Long> movableFreeformIds = filterNewFreeformIds(ssLexemeFreeforms, compLexemeFreeforms);

			if (CollectionUtils.isNotEmpty(movableFreeformIds)) {
				paramMap = new HashMap<>();
				paramMap.put("targetLexemeId", ssLexemeId);
				paramMap.put("sourceLexemeId", compLexemeId);
				paramMap.put("freeformIds", movableFreeformIds);
				updateCount = basicDbService.executeScript(sqlUpdateLexemeFreeformIds, paramMap);
				updateCountMap.get(LEXEME_FREEFORM).increment(updateCount);
			}
		}
	}

	private List<Long> filterNewFreeformIds(List<Freeform> existingFreeforms, List<Freeform> appliedFreeforms) {

		return appliedFreeforms.stream()
				.filter(sf -> existingFreeforms.stream()
						.noneMatch(
								tf -> tf.getType().equals(sf.getType()) &&
										((Objects.nonNull(tf.getValueText()) && tf.getValueText().equals(sf.getValueText())) ||
												(Objects.nonNull(tf.getValueNumber()) && tf.getValueNumber().equals(sf.getValueNumber())) ||
												(Objects.nonNull(tf.getClassifCode()) && tf.getClassifCode().equals(sf.getClassifCode())) ||
												(Objects.nonNull(tf.getValueDate()) && tf.getValueDate().equals(sf.getValueDate())))))
				.map(Freeform::getFreeformId)
				.collect(Collectors.toList());
	}

	private void moveLexemeRelations(Long ssLexemeId, List<Long> compLexemeIds, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;
		List<String> notExistsFields;

		int updateCount;

		for (Long compLexemeId : compLexemeIds) {

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("lexeme1_id", compLexemeId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("lexeme1_id", ssLexemeId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("lex_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(LEXEME_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(LEXEME_RELATION).increment(updateCount);

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("lexeme2_id", compLexemeId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("lexeme2_id", ssLexemeId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("lex_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(LEXEME_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(LEXEME_RELATION).increment(updateCount);
		}
	}

	private void moveData(String dataFkName, Long ssDataId, List<Long> compDataIds, String dataTableName, String[] notExistsFieldName, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;
		List<String> notExistsFields = null;

		if (notExistsFieldName != null) {
			notExistsFields = Arrays.asList(notExistsFieldName);
		}

		int updateCount;

		for (Long compLexemeId : compDataIds) {
			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put(dataFkName, compLexemeId);
			valueParamMap = new HashMap<>();
			valueParamMap.put(dataFkName, ssDataId);
			if (CollectionUtils.isEmpty(notExistsFields)) {
				updateCount = basicDbService.update(dataTableName, criteriaParamMap, valueParamMap);
			} else {
				updateCount = basicDbService.updateIfNotExists(dataTableName, criteriaParamMap, valueParamMap, notExistsFields);
			}
			updateCountMap.get(dataTableName).increment(updateCount);
		}
	}

	class MeaningJoinCandidate extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long wordId;

		private String word;

		private Long ssLexemeId;

		private Long ssMeaningId;

		private Long compLexemeId;

		private Long compMeaningId;

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public Long getSsLexemeId() {
			return ssLexemeId;
		}

		public void setSsLexemeId(Long ssLexemeId) {
			this.ssLexemeId = ssLexemeId;
		}

		public Long getSsMeaningId() {
			return ssMeaningId;
		}

		public void setSsMeaningId(Long ssMeaningId) {
			this.ssMeaningId = ssMeaningId;
		}

		public Long getCompLexemeId() {
			return compLexemeId;
		}

		public void setCompLexemeId(Long compLexemeId) {
			this.compLexemeId = compLexemeId;
		}

		public Long getCompMeaningId() {
			return compMeaningId;
		}

		public void setCompMeaningId(Long compMeaningId) {
			this.compMeaningId = compMeaningId;
		}

	}

	class MeaningJoinCandidateRowMapper implements RowMapper<MeaningJoinCandidate> {

		@Override
		public MeaningJoinCandidate mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long wordId = rs.getObject("word_id", Long.class);
			String word = rs.getObject("word", String.class);
			Long ssLexemeId = rs.getObject("ss_lexeme_id", Long.class);
			Long ssMeaningId = rs.getObject("ss_meaning_id", Long.class);
			Long compLexemeId = rs.getObject("comp_lexeme_id", Long.class);
			Long compMeaningId = rs.getObject("comp_meaning_id", Long.class);
			MeaningJoinCandidate meaningJoinCandidate = new MeaningJoinCandidate();
			meaningJoinCandidate.setWordId(wordId);
			meaningJoinCandidate.setWord(word);
			meaningJoinCandidate.setSsLexemeId(ssLexemeId);
			meaningJoinCandidate.setSsMeaningId(ssMeaningId);
			meaningJoinCandidate.setCompLexemeId(compLexemeId);
			meaningJoinCandidate.setCompMeaningId(compMeaningId);
			return meaningJoinCandidate;
		}
	}
}
