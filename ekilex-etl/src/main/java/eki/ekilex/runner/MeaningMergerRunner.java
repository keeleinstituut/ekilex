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

	private final String sqlSelectLexeme = "select l.id lexeme_id, l.word_id, l.meaning_id from lexeme l where l.meaning_id = :sourceMeaningId and l.dataset_code = :datasetCode";

	private final String sqlMoveLexeme = "update lexeme set meaning_id = :targetMeaningId where id = :sourceLexemeId";

	private final String sqlUpdateLexemeComplexity = "update " + LEXEME + " set complexity = :complexity where id = :lexemeId";

	private final String sqlSelectMeaningFreeforms = "select ff.* from " + FREEFORM + " ff, " + MEANING_FREEFORM + " mff where mff.freeform_id = ff.id and mff.meaning_id = :meaningId";

	private final String sqlUpdateMeaningFreeformIds = "update " + MEANING_FREEFORM + " set meaning_id = :targetMeaningId where meaning_id = :sourceMeaningId and freeform_id in (:freeformIds)";

	private final String sqlSelectLexemeFreeforms = "select ff.* from " + FREEFORM + " ff, " + LEXEME_FREEFORM + " lff where lff.freeform_id = ff.id and lff.lexeme_id = :lexemeId";

	private final String sqlUpdateLexemeFreeformIds = "update " + LEXEME_FREEFORM + " set lexeme_id = :targetLexemeId where lexeme_id = :sourceLexemeId and freeform_id in (:freeformIds)";

	private final String sqlDeleteLexemeFreeforms =
			"delete from " + FREEFORM + " ff where exists (select lff.id from " + LEXEME_FREEFORM + " lff where lff.freeform_id = ff.id and lff.lexeme_id in (:lexemeIds))";

	private final String sqlDeleteMeaningFreeforms =
			"delete from " + FREEFORM + " ff where exists (select mff.id from " + MEANING_FREEFORM + " mff where mff.freeform_id = ff.id and mff.meaning_id = :meaningId)";

	private final String sqlDeleteDefinitionFreeforms =
			"delete from " + FREEFORM + " ff where exists ("
					+ "select d.id from " + DEFINITION + " d, " + DEFINITION_FREEFORM + " dff "
							+ "where dff.freeform_id = ff.id and dff.definition_id = d.id and d.meaning_id = :meaningId)";

	private final String sqlDeleteMeaning = "delete from " + MEANING + " m where m.id = :meaningId and not exists (select l.id from " + LEXEME + " l where l.meaning_id = m.id)";

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
	public void execute(String compoundDatasetCode) throws Exception {
		this.compoundDatasetCode = compoundDatasetCode;

		start();

		Map<Long, List<MeaningJoinCandidate>> meaningJoinCandidatesMap = collectMeaningJoinCandidates(compoundDatasetCode);
		int joinedMeaningCount = meaningJoinCandidatesMap.size();

		logger.debug("Joined meaning count {}", joinedMeaningCount);

		Map<String, Count> updateCountMap = new HashMap<>();
		updateCountMap.put(MEANING_FREEFORM, new Count());
		updateCountMap.put(MEANING_NR, new Count());
		updateCountMap.put(MEANING_DOMAIN, new Count());
		updateCountMap.put(MEANING_SEMANTIC_TYPE, new Count());
		updateCountMap.put(MEANING_LIFECYCLE_LOG, new Count());
		updateCountMap.put(MEANING_PROCESS_LOG, new Count());
		updateCountMap.put(DEFINITION, new Count());
		updateCountMap.put(LEXEME, new Count());
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

		Map<String, Count> deleteCountMap = new HashMap<>();
		deleteCountMap.put(LEXEME, new Count());
		deleteCountMap.put(LEXEME_FREEFORM, new Count());
		deleteCountMap.put(MEANING, new Count());
		deleteCountMap.put(MEANING_FREEFORM, new Count());
		deleteCountMap.put(DEFINITION_FREEFORM, new Count());

		Map<Long, Long> resolvedCompMeaningIdMap = new HashMap<>();
		List<Long> failedDeleteCompMeaningIds = new ArrayList<>();

		long joinedMeaningCounter = 0;
		long progressIndicator = joinedMeaningCount / Math.min(joinedMeaningCount, 100);

		for (Entry<Long, List<MeaningJoinCandidate>> meaningJoinCandidatesEntry : meaningJoinCandidatesMap.entrySet()) {

			Long ssMeaningId = meaningJoinCandidatesEntry.getKey();
			List<MeaningJoinCandidate> meaningJoinCandidates = meaningJoinCandidatesEntry.getValue();
			meaningJoinCandidates = filterDuplicateJoins(ssMeaningId, meaningJoinCandidates, resolvedCompMeaningIdMap);

			boolean isOverrideComplexity = meaningJoinCandidates.stream().map(MeaningJoinCandidate::isOverrideComplexity).filter(is -> is == true).findAny().orElse(false);
			List<Long> compMeaningIds = meaningJoinCandidates.stream().map(MeaningJoinCandidate::getCompMeaningId).distinct().collect(Collectors.toList());
			List<Long> allMeaningIds = new ArrayList<>();
			allMeaningIds.add(ssMeaningId);
			allMeaningIds.addAll(compMeaningIds);

			List<LexemeId> allLexemes = getAllLexemes(allMeaningIds);
			Map<Long, List<LexemeId>> mergingLexemesMap = allLexemes.stream().collect(Collectors.groupingBy(LexemeId::getWordId));

			//merge, move, delete lexemes
			for (List<LexemeId> mergingLexemes : mergingLexemesMap.values()) {

				Long ssLexemeId = mergingLexemes.stream().map(LexemeId::getLexemeId).filter(mergingLexemeId -> mergingLexemeId.equals(ssMeaningId)).findAny().orElse(null);
				Long targetLexemeId;
				if (ssLexemeId == null) {

					//force move one to ss
					LexemeId forcedSsLexeme = mergingLexemes.get(0);
					targetLexemeId = forcedSsLexeme.getLexemeId();
					moveLexeme(ssMeaningId, targetLexemeId);
				} else {
					targetLexemeId = ssLexemeId;
				}
				List<Long> sourceMergingLexemeIds = mergingLexemes.stream().map(LexemeId::getLexemeId).filter(mergingLexemeId -> !mergingLexemeId.equals(targetLexemeId)).collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(sourceMergingLexemeIds)) {

					if (isOverrideComplexity) {
						updateLexemeComplexityToSimple(targetLexemeId);
					}
					moveLexemeFreeforms(targetLexemeId, sourceMergingLexemeIds, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_FREQUENCY, new String[] {"source_name"}, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_REGISTER, new String[] {"register_code"}, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_POS, new String[] {"pos_code"}, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_DERIV, new String[] {"deriv_code"}, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_REGION, new String[] {"region_code"}, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_SOURCE_LINK, new String[] {"source_id"}, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEX_COLLOC, null, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEX_COLLOC_POS_GROUP, null, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_LIFECYCLE_LOG, null, updateCountMap);
					moveData("lexeme_id", targetLexemeId, sourceMergingLexemeIds, LEXEME_PROCESS_LOG, null, updateCountMap);
					moveLexemeRelations(targetLexemeId, sourceMergingLexemeIds, updateCountMap);

					deleteLexemes(sourceMergingLexemeIds, deleteCountMap);
				}
			}

			//merge, delete meanings
			moveMeaningFreeforms(ssMeaningId, compMeaningIds, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_NR, new String[] {"dataset_code"}, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_DOMAIN, new String[] {"domain_origin", "domain_code"}, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_SEMANTIC_TYPE, new String[] {"semantic_type_code"}, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_LIFECYCLE_LOG, null, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_PROCESS_LOG, null, updateCountMap);
			moveData("meaning_id", ssMeaningId, compMeaningIds, DEFINITION, new String[] {"value"}, updateCountMap);

			deleteMeanings(compMeaningIds, failedDeleteCompMeaningIds, deleteCountMap);

			// progress
			joinedMeaningCounter++;
			if (joinedMeaningCounter % progressIndicator == 0) {
				long progressPercent = joinedMeaningCounter / progressIndicator;
				logger.debug("{}% - {} target meanings iterated", progressPercent, joinedMeaningCounter);
			}
		}

		logger.info(">>>> Update counts are as following:");
		for (Entry<String, Count> updateCountEntry : updateCountMap.entrySet()) {
			logger.info("{} : {}", updateCountEntry.getKey(), updateCountEntry.getValue().getValue());
		}

		logger.info(">>>> Delete counts are as following:");
		for (Entry<String, Count> deleteCountEntry : deleteCountMap.entrySet()) {
			logger.info("{} : {}", deleteCountEntry.getKey(), deleteCountEntry.getValue().getValue());
		}

		logger.info(">>>> Failed meaning delete count: {}", failedDeleteCompMeaningIds.size());
		if (CollectionUtils.isNotEmpty(failedDeleteCompMeaningIds)) {
			logger.debug("Meaning ids: {}", failedDeleteCompMeaningIds);
		}

		end();
	}

	private List<MeaningJoinCandidate> filterDuplicateJoins(Long ssMeaningId, List<MeaningJoinCandidate> compMeaningJoinCandidates, Map<Long, Long> resolvedCompMeaningIdMap) {
		List<MeaningJoinCandidate> filteredCompMeaningJoinCandidates = new ArrayList<>();
		for (MeaningJoinCandidate compMeaningJoinCandidate : compMeaningJoinCandidates) {
			Long compMeaningId = compMeaningJoinCandidate.getCompMeaningId();
			Long resolvingSsMeaningId = resolvedCompMeaningIdMap.get(compMeaningId);
			if (resolvingSsMeaningId == null) {
				resolvingSsMeaningId = ssMeaningId;
				resolvedCompMeaningIdMap.put(compMeaningId, resolvingSsMeaningId);
			}
			if (!resolvingSsMeaningId.equals(ssMeaningId)) {
				logger.debug("Component meaning {} is already joined with {}, skipping {}", compMeaningId, resolvingSsMeaningId, ssMeaningId);
				continue;
			}
			filteredCompMeaningJoinCandidates.add(compMeaningJoinCandidate);
		}
		return filteredCompMeaningJoinCandidates;
	}

	private Map<Long, List<MeaningJoinCandidate>> collectMeaningJoinCandidates(String compoundDatasetCode) throws Exception {

		Map<Long, List<MeaningJoinCandidate>> joinableWordMeaningPairMap = new HashMap<>();

		for (String sqlSelectMeaningJoinCandidatesForDataset : sqlSelectMeaningJoinCandidatesForDatasets) {

			List<MeaningJoinCandidate> joinCandidates = getJoinCandidatesForDataset(sqlSelectMeaningJoinCandidatesForDataset, compoundDatasetCode);
			logger.debug("{} join candidates collected for a dataset", joinCandidates.size());

			joinCandidates.stream().collect(Collectors.groupingBy(MeaningJoinCandidate::getSsMeaningId));

			for (MeaningJoinCandidate joinCandidate : joinCandidates) {
				Long ssMeaningId = joinCandidate.getSsMeaningId();
				List<MeaningJoinCandidate> wordMeaningJoinCandidates = joinableWordMeaningPairMap.get(ssMeaningId);
				if (wordMeaningJoinCandidates == null) {
					wordMeaningJoinCandidates = new ArrayList<>();
					joinableWordMeaningPairMap.put(ssMeaningId, wordMeaningJoinCandidates);
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

	private List<LexemeId> getAllLexemes(List<Long> allMeaningIds) throws Exception {
		List<LexemeId> allLexemes = new ArrayList<>();
		for (Long meaningId : allMeaningIds) {
			List<LexemeId> lexemes = getLexemes(meaningId);
			allLexemes.addAll(lexemes);
		}
		return allLexemes;
	}

	private List<LexemeId> getLexemes(Long compMeaningId) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("datasetCode", compoundDatasetCode);
		paramMap.put("sourceMeaningId", compMeaningId);
		List<LexemeId> lexemeIds = basicDbService.getResults(sqlSelectLexeme, paramMap, new LexemeIdRowMapper());
		return lexemeIds;
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

	private void moveLexeme(Long targetMeaningId, Long sourceLexemeId) {

		Map<String, Object> paramMap;
		paramMap = new HashMap<>();
		paramMap.put("targetMeaningId", targetMeaningId);
		paramMap.put("sourceLexemeId", sourceLexemeId);
		basicDbService.executeScript(sqlMoveLexeme, paramMap);
	}

	private void updateLexemeComplexityToSimple(Long lexemeId) {

		Map<String, Object> params = new HashMap<>();
		params.put("lexemeId", lexemeId);
		params.put("complexity", Complexity.SIMPLE.name());
		basicDbService.executeScript(sqlUpdateLexemeComplexity, params);
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

		for (Long compDataId : compDataIds) {
			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put(dataFkName, compDataId);
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

	private void deleteLexemes(List<Long> compLexemeIds, Map<String, Count> deleteCountMap) throws Exception {

		int deleteCount;

		// delete lexeme freeforms
		Map<String, Object> params = new HashMap<>();
		params.put("lexemeIds", compLexemeIds);
		deleteCount = basicDbService.executeScript(sqlDeleteLexemeFreeforms, params);
		deleteCountMap.get(LEXEME_FREEFORM).increment(deleteCount);

		// delete lexemes
		deleteCount = basicDbService.delete(LEXEME, compLexemeIds);
		deleteCountMap.get(LEXEME).increment(deleteCount);

		/*
		if (compLexemeIds.size() > deleteCount) {
			reportComposer.append(REPORT_LEX_MOVE_FAIL, compLexemeIds.toString());
		}
		*/
	}

	private void deleteMeanings(List<Long> compMeaningIds, List<Long> failedDeleteCompMeaningIds, Map<String, Count> deleteCountMap) throws Exception {

		Map<String, Object> params;
		int deleteCount;

		for (Long compMeaningId : compMeaningIds) {

			params = new HashMap<>();
			params.put("meaningId", compMeaningId);

			deleteCount = basicDbService.executeScript(sqlDeleteDefinitionFreeforms, params);
			deleteCountMap.get(DEFINITION_FREEFORM).increment(deleteCount);

			deleteCount = basicDbService.executeScript(sqlDeleteMeaningFreeforms, params);
			deleteCountMap.get(MEANING_FREEFORM).increment(deleteCount);

			deleteCount = basicDbService.executeScript(sqlDeleteMeaning, params);
			if (deleteCount == 0) {
				failedDeleteCompMeaningIds.add(compMeaningId);
			} else {
				deleteCountMap.get(MEANING).increment(deleteCount);
			}
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

		private boolean overrideComplexity;

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

		public boolean isOverrideComplexity() {
			return overrideComplexity;
		}

		public void setOverrideComplexity(boolean overrideComplexity) {
			this.overrideComplexity = overrideComplexity;
		}

	}

	class LexemeId extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long lexemeId;

		private Long wordId;

		private Long meaningId;

		public LexemeId(Long lexemeId, Long wordId, Long meaningId) {
			this.lexemeId = lexemeId;
			this.wordId = wordId;
			this.meaningId = meaningId;
		}

		public Long getLexemeId() {
			return lexemeId;
		}

		public void setLexemeId(Long lexemeId) {
			this.lexemeId = lexemeId;
		}

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
		}

		public Long getMeaningId() {
			return meaningId;
		}

		public void setMeaningId(Long meaningId) {
			this.meaningId = meaningId;
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
			boolean isOverrideComplexity = rs.getBoolean("is_override_complexity");
			MeaningJoinCandidate meaningJoinCandidate = new MeaningJoinCandidate();
			meaningJoinCandidate.setWordId(wordId);
			meaningJoinCandidate.setWord(word);
			meaningJoinCandidate.setSsLexemeId(ssLexemeId);
			meaningJoinCandidate.setSsMeaningId(ssMeaningId);
			meaningJoinCandidate.setCompLexemeId(compLexemeId);
			meaningJoinCandidate.setCompMeaningId(compMeaningId);
			meaningJoinCandidate.setOverrideComplexity(isOverrideComplexity);
			return meaningJoinCandidate;
		}
	}

	class LexemeIdRowMapper implements RowMapper<LexemeId> {

		@Override
		public LexemeId mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long lexemeId = rs.getObject("lexeme_id", Long.class);
			Long wordId = rs.getObject("word_id", Long.class);
			Long meaningId = rs.getObject("meaning_id", Long.class);
			return new LexemeId(lexemeId, wordId, meaningId);
		}
	}
}
