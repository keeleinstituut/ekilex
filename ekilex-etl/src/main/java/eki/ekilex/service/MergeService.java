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

	private static final String SQL_UPDATE_FORM_DISPLAY_FORM = "sql/update_form_display_form.sql";

	private final String sqlReassignLexemeToMeaning = "update lexeme set meaning_id = :targetMeaningId where id = :sourceLexemeId";

	private final String sqlReassignLexemeToWord = "update lexeme set word_id = :targetWordId where id = :sourceLexemeId";

	private final String sqlReassignLexemesToWord = "update lexeme set word_id = :targetWordId where id in (:sourceLexemeIds)";

	private final String sqlUpdateLexemeComplexity = "update " + LEXEME + " set complexity = :complexity where id = :lexemeId";

	private final String sqlSelectWordFreeforms = "select ff.* from " + FREEFORM + " ff, " + WORD_FREEFORM + " wff where wff.freeform_id = ff.id and wff.word_id = :wordId";

	private final String sqlUpdateWordFreeformIds = "update " + WORD_FREEFORM + " set word_id = :targetWordId where word_id = :sourceWordId and freeform_id in (:freeformIds)";

	private final String sqlSelectMeaningFreeforms = "select ff.* from " + FREEFORM + " ff, " + MEANING_FREEFORM + " mff where mff.freeform_id = ff.id and mff.meaning_id = :meaningId";

	private final String sqlUpdateMeaningFreeformIds = "update " + MEANING_FREEFORM + " set meaning_id = :targetMeaningId where meaning_id = :sourceMeaningId and freeform_id in (:freeformIds)";

	private final String sqlSelectLexemeFreeforms = "select ff.* from " + FREEFORM + " ff, " + LEXEME_FREEFORM + " lff where lff.freeform_id = ff.id and lff.lexeme_id = :lexemeId";

	private final String sqlUpdateLexemeFreeformIds = "update " + LEXEME_FREEFORM + " set lexeme_id = :targetLexemeId where lexeme_id = :sourceLexemeId and freeform_id in (:freeformIds)";

	private final String sqlDeleteWordFreeforms = "delete from " + FREEFORM + " ff where exists (select wff.id from " + WORD_FREEFORM + " wff where wff.freeform_id = ff.id and wff.word_id = :wordId)";

	private final String sqlDeleteLexemeFreeforms = "delete from " + FREEFORM + " ff where exists (select lff.id from " + LEXEME_FREEFORM
			+ " lff where lff.freeform_id = ff.id and lff.lexeme_id in (:lexemeIds))";

	private final String sqlDeleteMeaningFreeforms = "delete from " + FREEFORM + " ff where exists (select mff.id from " + MEANING_FREEFORM
			+ " mff where mff.freeform_id = ff.id and mff.meaning_id = :meaningId)";

	private final String sqlDeleteDefinitionFreeforms = "delete from " + FREEFORM + " ff where exists ("
			+ "select d.id from " + DEFINITION + " d, " + DEFINITION_FREEFORM + " dff "
			+ "where dff.freeform_id = ff.id and dff.definition_id = d.id and d.meaning_id = :meaningId)";

	private final String sqlDeleteMeaning = "delete from " + MEANING + " m where m.id = :meaningId and not exists (select l.id from " + LEXEME + " l where l.meaning_id = m.id)";

	private final String sqlDeleteWord = "delete from " + WORD + " w where w.id = :wordId and not exists (select l.id from " + LEXEME + " l where l.word_id = w.id)";

	private String sqlSelectWordLexemesMaxFirstLevel = "select max(lex.level1) from " + LEXEME + " lex where lex.word_id = :wordId";

	private String sqlSelectWordLexemeMeaningIdForDs;

	private String sqlSelectWordLexemeMeaningIdNoDs;

	private String sqlUpdateFormDisplayForm;

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

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_UPDATE_FORM_DISPLAY_FORM);
		sqlUpdateFormDisplayForm = IOUtils.toString(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();
	}

	@Autowired
	protected BasicDbService basicDbService;

	public Map<String, Count> getUpdateCountMap() {
		Map<String, Count> updateCountMap = new HashMap<>();
		updateCountMap.put(WORD_FREEFORM, new Count());
		updateCountMap.put(WORD_WORD_TYPE, new Count());
		updateCountMap.put(WORD_GUID, new Count());
		updateCountMap.put(WORD_ETYMOLOGY, new Count());
		updateCountMap.put(WORD_ETYMOLOGY_RELATION, new Count());
		updateCountMap.put(WORD_GROUP_MEMBER, new Count());
		updateCountMap.put(WORD_LIFECYCLE_LOG, new Count());
		updateCountMap.put(WORD_PROCESS_LOG, new Count());
		updateCountMap.put(WORD_RELATION, new Count());
		updateCountMap.put(MEANING_FREEFORM, new Count());
		updateCountMap.put(MEANING_NR, new Count());
		updateCountMap.put(MEANING_DOMAIN, new Count());
		updateCountMap.put(MEANING_SEMANTIC_TYPE, new Count());
		updateCountMap.put(MEANING_LIFECYCLE_LOG, new Count());
		updateCountMap.put(MEANING_PROCESS_LOG, new Count());
		updateCountMap.put(MEANING_RELATION, new Count());
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
		deleteCountMap.put(WORD, new Count());
		deleteCountMap.put(WORD_FREEFORM, new Count());
		deleteCountMap.put(LEXEME, new Count());
		deleteCountMap.put(LEXEME_FREEFORM, new Count());
		deleteCountMap.put(MEANING, new Count());
		deleteCountMap.put(MEANING_FREEFORM, new Count());
		deleteCountMap.put(DEFINITION_FREEFORM, new Count());
		return deleteCountMap;
	}

	public List<WordLexemeMeaning> getLexemesByMeanings(List<Long> allMeaningIds, String datasetCode) throws Exception {
		List<WordLexemeMeaning> allLexemes = new ArrayList<>();
		for (Long meaningId : allMeaningIds) {
			List<WordLexemeMeaning> lexemes = getLexemesByMeaning(meaningId, datasetCode);
			allLexemes.addAll(lexemes);
		}
		return allLexemes;
	}

	public List<WordLexemeMeaning> getLexemesByMeaning(Long meaningId, String datasetCode) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("datasetCode", datasetCode);
		paramMap.put("meaningId", meaningId);
		List<WordLexemeMeaning> lexemeIds = basicDbService.getResults(sqlSelectWordLexemeMeaningIdForDs, paramMap, new WordLexemeMeaningRowMapper());
		return lexemeIds;
	}

	public List<WordLexemeMeaning> getLexemesByWords(List<Long> wordIds) throws Exception {
		List<WordLexemeMeaning> allLexemes = new ArrayList<>();
		for (Long wordId : wordIds) {
			List<WordLexemeMeaning> lexemes = getLexemesByWord(wordId);
			allLexemes.addAll(lexemes);
		}
		return allLexemes;
	}

	public List<WordLexemeMeaning> getLexemesByWord(Long wordId) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		List<WordLexemeMeaning> lexemeIds = basicDbService.getResults(sqlSelectWordLexemeMeaningIdNoDs, paramMap, new WordLexemeMeaningRowMapper());
		return lexemeIds;
	}

	public Long getLexemeId(Long wordId, Long meaningId, String datasetCode) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("word_id", wordId);
		paramMap.put("meaning_id", meaningId);
		paramMap.put("dataset_code", datasetCode);
		Map<String, Object> lexemeResult = basicDbService.select(LEXEME, paramMap);
		if (lexemeResult == null) {
			return null;
		} else {
			Long lexemeId = (Long) lexemeResult.get("id");
			return lexemeId;
		}
	}

	public Integer getWordLexemesMaxFirstLevel(Long wordId) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		Map<String, Object> maxLevelMap = basicDbService.queryForMap(sqlSelectWordLexemesMaxFirstLevel, paramMap);
		return (Integer) maxLevelMap.get("max");
	}

	public void moveData(String dataFkName, Long targetDataId, List<Long> sourceDataIds, String dataTableName, String[] notExistsFieldName, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;
		List<String> notExistsFields = null;

		if (notExistsFieldName != null) {
			notExistsFields = Arrays.asList(notExistsFieldName);
		}

		int updateCount;

		for (Long sourceDataId : sourceDataIds) {
			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put(dataFkName, sourceDataId);
			valueParamMap = new HashMap<>();
			valueParamMap.put(dataFkName, targetDataId);
			if (CollectionUtils.isEmpty(notExistsFields)) {
				updateCount = basicDbService.update(dataTableName, criteriaParamMap, valueParamMap);
			} else {
				updateCount = basicDbService.updateIfNotExists(dataTableName, criteriaParamMap, valueParamMap, notExistsFields);
			}
			updateCountMap.get(dataTableName).increment(updateCount);
		}
	}

	public void moveWordsData(Long targetWordId, List<Long> sourceMergingWordIds, Map<String, Count> updateCountMap) throws Exception {

		//what about paradigms?
		moveWordFreeforms(targetWordId, sourceMergingWordIds, updateCountMap);
		moveData("word_id", targetWordId, sourceMergingWordIds, WORD_GUID, new String[] {"dataset_code"}, updateCountMap);
		moveData("word_id", targetWordId, sourceMergingWordIds, WORD_WORD_TYPE, new String[] {"word_type_code"}, updateCountMap);
		moveData("word_id", targetWordId, sourceMergingWordIds, WORD_ETYMOLOGY, null, updateCountMap);
		moveData("related_word_id", targetWordId, sourceMergingWordIds, WORD_ETYMOLOGY_RELATION, new String[] {"word_etym_id"}, updateCountMap);
		moveData("word_id", targetWordId, sourceMergingWordIds, WORD_GROUP_MEMBER, new String[] {"word_group_id"}, updateCountMap);
		moveData("word_id", targetWordId, sourceMergingWordIds, WORD_LIFECYCLE_LOG, null, updateCountMap);
		moveData("word_id", targetWordId, sourceMergingWordIds, WORD_PROCESS_LOG, null, updateCountMap);
		moveWordRelations(targetWordId, sourceMergingWordIds, updateCountMap);
	}

	public void moveWordFreeforms(Long targetWordId, List<Long> sourceWordIds, Map<String, Count> updateCountMap) throws Exception {

		FreeformRowMapper freeformRowMapper = new FreeformRowMapper();
		Map<String, Object> paramMap;

		paramMap = new HashMap<>();
		paramMap.put("wordId", targetWordId);
		List<Freeform> ssLexemeFreeforms = basicDbService.getResults(sqlSelectWordFreeforms, paramMap, freeformRowMapper);

		int updateCount;

		for (Long sourceWordId : sourceWordIds) {

			paramMap = new HashMap<>();
			paramMap.put("wordId", sourceWordId);
			List<Freeform> compLexemeFreeforms = basicDbService.getResults(sqlSelectWordFreeforms, paramMap, freeformRowMapper);
			List<Long> movableFreeformIds = filterNewFreeformIds(ssLexemeFreeforms, compLexemeFreeforms);

			if (CollectionUtils.isNotEmpty(movableFreeformIds)) {
				paramMap = new HashMap<>();
				paramMap.put("targetWordId", targetWordId);
				paramMap.put("sourceWordId", sourceWordId);
				paramMap.put("freeformIds", movableFreeformIds);
				updateCount = basicDbService.executeScript(sqlUpdateWordFreeformIds, paramMap);
				updateCountMap.get(WORD_FREEFORM).increment(updateCount);
			}
		}
	}

	public void moveWordRelations(Long targetWordId, List<Long> sourceWordIds, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;
		List<String> notExistsFields;

		int updateCount;

		for (Long sourceWordId : sourceWordIds) {

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("word1_id", sourceWordId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("word1_id", targetWordId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("word_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(WORD_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(WORD_RELATION).increment(updateCount);

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("word2_id", sourceWordId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("word2_id", targetWordId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("word_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(WORD_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(WORD_RELATION).increment(updateCount);
		}
	}

	public boolean deleteWord(Long wordId, Map<String, Count> deleteCountMap) {

		Map<String, Object> params = new HashMap<>();
		params.put("wordId", wordId);

		int deleteCount;

		deleteCount = basicDbService.executeScript(sqlDeleteWordFreeforms, params);
		deleteCountMap.get(WORD_FREEFORM).increment(deleteCount);

		deleteCount = basicDbService.executeScript(sqlDeleteWord, params);
		if (deleteCount == 0) {
			return false;
		} else {
			deleteCountMap.get(WORD).increment(deleteCount);
			return true;
		}
	}

	public void updateLexemeComplexityToSimple(Long lexemeId) {

		Map<String, Object> params = new HashMap<>();
		params.put("lexemeId", lexemeId);
		params.put("complexity", Complexity.SIMPLE.name());
		basicDbService.executeScript(sqlUpdateLexemeComplexity, params);
	}

	public void reassignLexemeToMeaning(Long targetMeaningId, Long sourceLexemeId) {

		Map<String, Object> paramMap;
		paramMap = new HashMap<>();
		paramMap.put("targetMeaningId", targetMeaningId);
		paramMap.put("sourceLexemeId", sourceLexemeId);
		basicDbService.executeScript(sqlReassignLexemeToMeaning, paramMap);
	}

	public void reassignLexemeToWord(Long targetWordId, Long sourceLexemeId, Map<String, Count> updateCountMap) {

		Map<String, Object> paramMap;
		paramMap = new HashMap<>();
		paramMap.put("targetWordId", targetWordId);
		paramMap.put("sourceLexemeId", sourceLexemeId);
		int updateCount = basicDbService.executeScript(sqlReassignLexemeToWord, paramMap);
		updateCountMap.get(LEXEME).increment(updateCount);
	}

	public void reassignLexemesToWord(Long targetWordId, List<Long> sourceLexemeIds, Map<String, Count> updateCountMap) {

		Map<String, Object> paramMap;
		paramMap = new HashMap<>();
		paramMap.put("targetWordId", targetWordId);
		paramMap.put("sourceLexemeIds", sourceLexemeIds);
		int updateCount = basicDbService.executeScript(sqlReassignLexemesToWord, paramMap);
		updateCountMap.get(LEXEME).increment(updateCount);
	}

	public void reassignLexemeToWordAndUpdateLevels(Long targetWordId, Long sourceLexemeId, int level1, int level2, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", sourceLexemeId);

		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", targetWordId);
		valueParamMap.put("level1", level1);
		valueParamMap.put("level2", level2);

		int updateCount = basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
		updateCountMap.get(LEXEME).increment(updateCount);
	}

	public void moveLexemesData(Long targetLexemeId, List<Long> sourceMergingLexemeIds, boolean isOverrideComplexity, Map<String, Count> updateCountMap) throws Exception {

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

	public void moveLexemeFreeforms(Long targetLexemeId, List<Long> sourceLexemeIds, Map<String, Count> updateCountMap) throws Exception {

		FreeformRowMapper freeformRowMapper = new FreeformRowMapper();
		Map<String, Object> paramMap;

		int updateCount;

		for (Long sourceLexemeId : sourceLexemeIds) {

			paramMap = new HashMap<>();
			paramMap.put("lexemeId", targetLexemeId);
			List<Freeform> ssLexemeFreeforms = basicDbService.getResults(sqlSelectLexemeFreeforms, paramMap, freeformRowMapper);

			paramMap = new HashMap<>();
			paramMap.put("lexemeId", sourceLexemeId);
			List<Freeform> compLexemeFreeforms = basicDbService.getResults(sqlSelectLexemeFreeforms, paramMap, freeformRowMapper);
			List<Long> movableFreeformIds = filterNewFreeformIds(ssLexemeFreeforms, compLexemeFreeforms);

			if (CollectionUtils.isNotEmpty(movableFreeformIds)) {
				paramMap = new HashMap<>();
				paramMap.put("targetLexemeId", targetLexemeId);
				paramMap.put("sourceLexemeId", sourceLexemeId);
				paramMap.put("freeformIds", movableFreeformIds);
				updateCount = basicDbService.executeScript(sqlUpdateLexemeFreeformIds, paramMap);
				updateCountMap.get(LEXEME_FREEFORM).increment(updateCount);
			}
		}
	}

	public void moveLexemeRelations(Long targetLexemeId, List<Long> sourceLexemeIds, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;
		List<String> notExistsFields;

		int updateCount;

		for (Long sourceLexemeId : sourceLexemeIds) {

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("lexeme1_id", sourceLexemeId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("lexeme1_id", targetLexemeId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("lex_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(LEXEME_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(LEXEME_RELATION).increment(updateCount);

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("lexeme2_id", sourceLexemeId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("lexeme2_id", targetLexemeId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("lex_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(LEXEME_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(LEXEME_RELATION).increment(updateCount);
		}
	}

	public void deleteLexemes(List<Long> lexemeIds, Map<String, Count> deleteCountMap) throws Exception {

		int deleteCount;

		// delete lexeme freeforms
		Map<String, Object> params = new HashMap<>();
		params.put("lexemeIds", lexemeIds);
		deleteCount = basicDbService.executeScript(sqlDeleteLexemeFreeforms, params);
		deleteCountMap.get(LEXEME_FREEFORM).increment(deleteCount);

		// delete lexemes
		deleteCount = basicDbService.delete(LEXEME, lexemeIds);
		deleteCountMap.get(LEXEME).increment(deleteCount);
	}

	public void moveMeaningsData(Long targetMeaningId, List<Long> sourceMeaningIds, Map<String, Count> updateCountMap) throws Exception {

		moveMeaningFreeforms(targetMeaningId, sourceMeaningIds, updateCountMap);
		moveData("meaning_id", targetMeaningId, sourceMeaningIds, MEANING_NR, new String[] {"dataset_code"}, updateCountMap);
		moveData("meaning_id", targetMeaningId, sourceMeaningIds, MEANING_DOMAIN, new String[] {"domain_origin", "domain_code"}, updateCountMap);
		moveData("meaning_id", targetMeaningId, sourceMeaningIds, MEANING_SEMANTIC_TYPE, new String[] {"semantic_type_code"}, updateCountMap);
		moveData("meaning_id", targetMeaningId, sourceMeaningIds, MEANING_LIFECYCLE_LOG, null, updateCountMap);
		moveData("meaning_id", targetMeaningId, sourceMeaningIds, MEANING_PROCESS_LOG, null, updateCountMap);
		moveData("meaning_id", targetMeaningId, sourceMeaningIds, DEFINITION, new String[] {"value", "complexity"}, updateCountMap);
		moveMeaningRelations(targetMeaningId, sourceMeaningIds, updateCountMap);
	}

	public void moveMeaningFreeforms(Long targetMeaningId, List<Long> sourceMeaningIds, Map<String, Count> updateCountMap) throws Exception {

		FreeformRowMapper freeformRowMapper = new FreeformRowMapper();
		Map<String, Object> paramMap;

		paramMap = new HashMap<>();
		paramMap.put("meaningId", targetMeaningId);
		List<Freeform> ssLexemeFreeforms = basicDbService.getResults(sqlSelectMeaningFreeforms, paramMap, freeformRowMapper);

		int updateCount;

		for (Long sourceMeaningId : sourceMeaningIds) {

			paramMap = new HashMap<>();
			paramMap.put("meaningId", sourceMeaningId);
			List<Freeform> compLexemeFreeforms = basicDbService.getResults(sqlSelectMeaningFreeforms, paramMap, freeformRowMapper);
			List<Long> movableFreeformIds = filterNewFreeformIds(ssLexemeFreeforms, compLexemeFreeforms);

			if (CollectionUtils.isNotEmpty(movableFreeformIds)) {
				paramMap = new HashMap<>();
				paramMap.put("targetMeaningId", targetMeaningId);
				paramMap.put("sourceMeaningId", sourceMeaningId);
				paramMap.put("freeformIds", movableFreeformIds);
				updateCount = basicDbService.executeScript(sqlUpdateMeaningFreeformIds, paramMap);
				updateCountMap.get(MEANING_FREEFORM).increment(updateCount);
			}
		}
	}

	public void moveMeaningRelations(Long targetMeaningId, List<Long> sourceMeaningIds, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;
		List<String> notExistsFields;

		int updateCount;

		for (Long sourceMeaningId : sourceMeaningIds) {

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("meaning1_id", sourceMeaningId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("meaning1_id", targetMeaningId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("meaning_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(MEANING_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(MEANING_RELATION).increment(updateCount);

			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("meaning2_id", sourceMeaningId);
			valueParamMap = new HashMap<>();
			valueParamMap.put("meaning2_id", targetMeaningId);
			notExistsFields = new ArrayList<>();
			notExistsFields.add("meaning_rel_type_code");
			updateCount = basicDbService.updateIfNotExists(MEANING_RELATION, criteriaParamMap, valueParamMap, notExistsFields);
			updateCountMap.get(MEANING_RELATION).increment(updateCount);
		}
	}

	public void moveDisplayForm(Long targetWordId, Long sourceWordId) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId1", targetWordId);
		paramMap.put("wordId2", sourceWordId);
		basicDbService.executeScript(sqlUpdateFormDisplayForm, paramMap);
	}

	public void deleteMeanings(List<Long> meaningIds, List<Long> failedDeleteMeaningIds, Map<String, Count> deleteCountMap) throws Exception {

		Map<String, Object> params;
		int deleteCount;

		for (Long meaningId : meaningIds) {

			params = new HashMap<>();
			params.put("meaningId", meaningId);

			deleteCount = basicDbService.executeScript(sqlDeleteDefinitionFreeforms, params);
			deleteCountMap.get(DEFINITION_FREEFORM).increment(deleteCount);

			deleteCount = basicDbService.executeScript(sqlDeleteMeaningFreeforms, params);
			deleteCountMap.get(MEANING_FREEFORM).increment(deleteCount);

			deleteCount = basicDbService.executeScript(sqlDeleteMeaning, params);
			if (deleteCount == 0) {
				failedDeleteMeaningIds.add(meaningId);
			} else {
				deleteCountMap.get(MEANING).increment(deleteCount);
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
}
