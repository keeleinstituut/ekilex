package eki.ekilex.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.TableName;
import eki.common.data.Count;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.transform.Freeform;
import eki.ekilex.data.transform.WordLexemeMeaning;
import eki.ekilex.data.util.FreeformRowMapper;
import eki.ekilex.data.util.WordLexemeMeaningRowMapper;

@Component
public class MergeService implements TableName, SystemConstant, InitializingBean {

	private static final String SQL_SELECT_WORD_LEXEME_MEANING_ID_FOR_DS = "sql/select_word_lexeme_meaning_id_for_dataset.sql";

	private static final String SQL_SELECT_WORD_LEXEME_MEANING_ID_NO_DS = "sql/select_word_lexeme_meaning_id.sql";

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

	private String sqlSelectWordLexemeMeaningIdForDs;

	private String sqlSelectWordLexemeMeaningIdNoDs;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_LEXEME_MEANING_ID_FOR_DS);
		sqlSelectWordLexemeMeaningIdForDs = IOUtils.toString(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();
		
		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_LEXEME_MEANING_ID_NO_DS);
		sqlSelectWordLexemeMeaningIdNoDs = IOUtils.toString(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();
	}

	@Autowired
	protected BasicDbService basicDbService;

	public Map<String, Count> getUpdateCountMap() {
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
		return updateCountMap;
	}

	public Map<String, Count> getDeleteCountMap() {
		Map<String, Count> deleteCountMap = new HashMap<>();
		deleteCountMap.put(LEXEME, new Count());
		deleteCountMap.put(LEXEME_FREEFORM, new Count());
		deleteCountMap.put(MEANING, new Count());
		deleteCountMap.put(MEANING_FREEFORM, new Count());
		deleteCountMap.put(DEFINITION_FREEFORM, new Count());
		return deleteCountMap;
	}

	public List<WordLexemeMeaning> getAllLexemes(List<Long> allMeaningIds, String datasetCode) throws Exception {
		List<WordLexemeMeaning> allLexemes = new ArrayList<>();
		for (Long meaningId : allMeaningIds) {
			List<WordLexemeMeaning> lexemes = getLexemes(meaningId, datasetCode);
			allLexemes.addAll(lexemes);
		}
		return allLexemes;
	}

	public List<WordLexemeMeaning> getLexemes(Long compMeaningId, String datasetCode) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("datasetCode", datasetCode);
		paramMap.put("meaningId", compMeaningId);
		List<WordLexemeMeaning> lexemeIds = basicDbService.getResults(sqlSelectWordLexemeMeaningIdForDs, paramMap, new WordLexemeMeaningRowMapper());
		return lexemeIds;
	}

	public List<WordLexemeMeaning> getLexemes(Long compMeaningId) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("meaningId", compMeaningId);
		List<WordLexemeMeaning> lexemeIds = basicDbService.getResults(sqlSelectWordLexemeMeaningIdNoDs, paramMap, new WordLexemeMeaningRowMapper());
		return lexemeIds;
	}

	public void moveMeaningFreeforms(Long ssMeaningId, List<Long> compMeaningIds, Map<String, Count> updateCountMap) throws Exception {

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

	public void moveLexeme(Long targetMeaningId, Long sourceLexemeId) {

		Map<String, Object> paramMap;
		paramMap = new HashMap<>();
		paramMap.put("targetMeaningId", targetMeaningId);
		paramMap.put("sourceLexemeId", sourceLexemeId);
		basicDbService.executeScript(sqlMoveLexeme, paramMap);
	}

	public void updateLexemeComplexityToSimple(Long lexemeId) {

		Map<String, Object> params = new HashMap<>();
		params.put("lexemeId", lexemeId);
		params.put("complexity", Complexity.SIMPLE.name());
		basicDbService.executeScript(sqlUpdateLexemeComplexity, params);
	}

	public void moveLexemeFreeforms(Long ssLexemeId, List<Long> compLexemeIds, Map<String, Count> updateCountMap) throws Exception {

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

	public List<Long> filterNewFreeformIds(List<Freeform> existingFreeforms, List<Freeform> appliedFreeforms) {

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

	public void moveLexemeRelations(Long ssLexemeId, List<Long> compLexemeIds, Map<String, Count> updateCountMap) throws Exception {

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

	public void moveData(String dataFkName, Long ssDataId, List<Long> compDataIds, String dataTableName, String[] notExistsFieldName, Map<String, Count> updateCountMap) throws Exception {

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

	public void moveLexemes(Long targetLexemeId, List<Long> sourceMergingLexemeIds, boolean isOverrideComplexity, Map<String, Count> updateCountMap) throws Exception {

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
	}

	public void deleteLexemes(List<Long> compLexemeIds, Map<String, Count> deleteCountMap) throws Exception {

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

	public void moveMeanings(Long ssMeaningId, List<Long> compMeaningIds, Map<String, Count> updateCountMap) throws Exception {
		moveMeaningFreeforms(ssMeaningId, compMeaningIds, updateCountMap);
		moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_NR, new String[] {"dataset_code"}, updateCountMap);
		moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_DOMAIN, new String[] {"domain_origin", "domain_code"}, updateCountMap);
		moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_SEMANTIC_TYPE, new String[] {"semantic_type_code"}, updateCountMap);
		moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_LIFECYCLE_LOG, null, updateCountMap);
		moveData("meaning_id", ssMeaningId, compMeaningIds, MEANING_PROCESS_LOG, null, updateCountMap);
		moveData("meaning_id", ssMeaningId, compMeaningIds, DEFINITION, new String[] {"value"}, updateCountMap);
	}

	public void deleteMeanings(List<Long> compMeaningIds, List<Long> failedDeleteCompMeaningIds, Map<String, Count> deleteCountMap) throws Exception {

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
}
