package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LAYER_STATE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.MEANING_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.PROCESS_LOG;
import static eki.ekilex.data.db.Tables.WORD_PROCESS_LOG;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LayerName;
import eki.ekilex.data.LexemeData;
import eki.ekilex.data.ProcessLog;

@Component
public class ProcessDbService {

	@Autowired
	private DSLContext create;

	public List<ProcessLog> getLogForMeaning(Long meaningId) {

		List<ProcessLog> results = create
				.select(
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.EVENT_ON,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.LAYER_NAME)
				.from(MEANING_PROCESS_LOG, PROCESS_LOG)
				.where(
						MEANING_PROCESS_LOG.MEANING_ID.eq(meaningId)
								.and(MEANING_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.orderBy(PROCESS_LOG.EVENT_ON.desc())
				.fetchInto(ProcessLog.class);

		return results;
	}

	public Integer getLogCountForMeaning(Long meaningId) {

		return create
				.fetchCount(DSL
						.select(MEANING_PROCESS_LOG.ID)
						.from(MEANING_PROCESS_LOG)
						.where(MEANING_PROCESS_LOG.MEANING_ID.eq(meaningId)));
	}

	public List<ProcessLog> getLogForWord(Long wordId) {

		List<ProcessLog> results = create
				.select(
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.EVENT_ON,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.LAYER_NAME)
				.from(WORD_PROCESS_LOG, PROCESS_LOG)
				.where(
						WORD_PROCESS_LOG.WORD_ID.eq(wordId)
								.and(WORD_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.orderBy(PROCESS_LOG.EVENT_ON.desc())
				.fetchInto(ProcessLog.class);

		return results;
	}

	public Integer getLogCountForWord(Long wordId) {

		return create
				.fetchCount(DSL
						.select(WORD_PROCESS_LOG.ID)
						.from(WORD_PROCESS_LOG)
						.where(WORD_PROCESS_LOG.WORD_ID.eq(wordId)));
	}

	public List<ProcessLog> getLogForLexemeAndMeaning(Long lexemeId) {

		Table<Record1<Long>> m = DSL
				.select(LEXEME.MEANING_ID.as("id"))
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.asTable("m");

		List<ProcessLog> results = create
				.select(
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.EVENT_ON,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.LAYER_NAME)
				.from(
						MEANING_PROCESS_LOG,
						PROCESS_LOG,
						m)
				.where(
						MEANING_PROCESS_LOG.MEANING_ID.eq(m.field("id", Long.class))
								.and(MEANING_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.unionAll(create
						.select(
								PROCESS_LOG.EVENT_BY,
								PROCESS_LOG.EVENT_ON,
								PROCESS_LOG.COMMENT,
								PROCESS_LOG.COMMENT_PRESE,
								PROCESS_LOG.PROCESS_STATE_CODE,
								PROCESS_LOG.DATASET_CODE,
								PROCESS_LOG.LAYER_NAME)
						.from(
								LEXEME_PROCESS_LOG,
								PROCESS_LOG)
						.where(
								LEXEME_PROCESS_LOG.LEXEME_ID.eq(lexemeId)
										.and(LEXEME_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID))))
				.orderBy(PROCESS_LOG.EVENT_ON.desc())
				.fetchInto(ProcessLog.class);

		return results;
	}

	public List<ProcessLog> getLogForLexemeAndWord(Long lexemeId) {

		Table<Record1<Long>> w = DSL
				.select(LEXEME.WORD_ID.as("id"))
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.asTable("w");

		List<ProcessLog> results = create
				.select(
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.EVENT_ON,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.LAYER_NAME)
				.from(
						WORD_PROCESS_LOG,
						PROCESS_LOG,
						w)
				.where(
						WORD_PROCESS_LOG.WORD_ID.eq(w.field("id", Long.class))
								.and(WORD_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.unionAll(create
						.select(
								PROCESS_LOG.EVENT_BY,
								PROCESS_LOG.EVENT_ON,
								PROCESS_LOG.COMMENT,
								PROCESS_LOG.COMMENT_PRESE,
								PROCESS_LOG.PROCESS_STATE_CODE,
								PROCESS_LOG.DATASET_CODE,
								PROCESS_LOG.LAYER_NAME)
						.from(
								LEXEME_PROCESS_LOG,
								PROCESS_LOG)
						.where(
								LEXEME_PROCESS_LOG.LEXEME_ID.eq(lexemeId)
										.and(LEXEME_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID))))
				.orderBy(PROCESS_LOG.EVENT_ON.desc())
				.fetchInto(ProcessLog.class);

		return results;
	}

	public LexemeData getLexemeData(Long entityId) {

		LexemeData lexemeData = create
				.select(
						LEXEME.ID,
						LEXEME.PROCESS_STATE_CODE,
						LEXEME.DATASET_CODE
				)
				.from(LEXEME)
				.where(LEXEME.ID.eq(entityId))
				.fetchSingleInto(LexemeData.class);
		return lexemeData;
	}

	public LexemeData getLexemeData(Long lexemeId, LayerName layerName) {

		LexemeData lexemeData = create
				.select(
						LEXEME.ID,
						LEXEME.PROCESS_STATE_CODE,
						LEXEME.DATASET_CODE,
						LAYER_STATE.PROCESS_STATE_CODE.as("layer_process_state_code"))
				.from(LEXEME.leftOuterJoin(LAYER_STATE).on(LAYER_STATE.LEXEME_ID.eq(LEXEME.ID).and(LAYER_STATE.LAYER_NAME.eq(layerName.name()))))
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(LexemeData.class);
		return lexemeData;
	}

	public List<LexemeData> getLexemeDatas(Long wordId, String datasetCode, LayerName layerName) {

		return create
				.select(
						LEXEME.ID,
						LEXEME.PROCESS_STATE_CODE,
						LEXEME.DATASET_CODE,
						LAYER_STATE.PROCESS_STATE_CODE.as("layer_process_state_code"))
				.from(LEXEME.leftOuterJoin(LAYER_STATE).on(LAYER_STATE.LEXEME_ID.eq(LEXEME.ID).and(LAYER_STATE.LAYER_NAME.eq(layerName.name()))))
				.where(LEXEME.WORD_ID.eq(wordId).and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchInto(LexemeData.class);
	}

	public void createLexemeProcessLog(Long lexemeId, String eventBy, String comment, String commentPrese, String processStateCode, String datasetCode) {

		Long processLogId = createProcessLog(eventBy, comment, commentPrese, processStateCode, datasetCode);
		createLexemeProcessLog(lexemeId, processLogId);
	}

	public void createLexemeProcessLog(Long lexemeId, String eventBy, String comment, String commentPrese, String processStateCode, String datasetCode, LayerName layerName) {

		Long processLogId = createProcessLog(eventBy, comment, commentPrese, processStateCode, datasetCode, layerName);
		createLexemeProcessLog(lexemeId, processLogId);
	}

	private void createLexemeProcessLog(Long lexemeId, Long processLogId) {

		create
				.insertInto(
						LEXEME_PROCESS_LOG,
						LEXEME_PROCESS_LOG.LEXEME_ID,
						LEXEME_PROCESS_LOG.PROCESS_LOG_ID)
				.values(lexemeId, processLogId)
				.execute();
	}

	public void createMeaningProcessLog(Long meaningId, String eventBy, String comment, String commentPrese, String datasetCode) {

		Long processLogId = createProcessLog(eventBy, comment, commentPrese, null, datasetCode);
		createMeaningProcessLog(meaningId, processLogId);
	}

	private void createMeaningProcessLog(Long meaningId, Long processLogId) {

		create
				.insertInto(
						MEANING_PROCESS_LOG,
						MEANING_PROCESS_LOG.MEANING_ID,
						MEANING_PROCESS_LOG.PROCESS_LOG_ID)
				.values(meaningId, processLogId)
				.execute();
	}

	public void createWordProcessLog(Long wordId, String eventBy, String comment, String commentPrese, String datasetCode) {

		Long processLogId = createProcessLog(eventBy, comment, commentPrese, null, datasetCode);
		createWordProcessLog(wordId, processLogId);
	}

	private void createWordProcessLog(Long wordId, Long processLogId) {

		create
				.insertInto(
						WORD_PROCESS_LOG,
						WORD_PROCESS_LOG.WORD_ID,
						WORD_PROCESS_LOG.PROCESS_LOG_ID)
				.values(wordId, processLogId)
				.execute();
	}

	private Long createProcessLog(String eventBy, String comment, String commentPrese, String processStateCode, String datasetCode) {
	
		Long processLogId = create
				.insertInto(
						PROCESS_LOG,
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE)
				.values(
						eventBy,
						comment,
						commentPrese,
						processStateCode,
						datasetCode)
				.returning(PROCESS_LOG.ID)
				.fetchOne()
				.getId();
	
		return processLogId;
	}

	private Long createProcessLog(String eventBy, String comment, String commentPrese, String processStateCode, String datasetCode, LayerName layerName) {

		if (LayerName.NONE.equals(layerName)) {
			return createProcessLog(eventBy, comment, commentPrese, processStateCode, datasetCode);
		}
	
		Long processLogId = create
				.insertInto(
						PROCESS_LOG,
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.LAYER_NAME)
				.values(
						eventBy,
						comment,
						commentPrese,
						processStateCode,
						datasetCode,
						layerName.name())
				.returning(PROCESS_LOG.ID)
				.fetchOne()
				.getId();
	
		return processLogId;
	}

	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {

		create.update(LEXEME)
				.set(LEXEME.PROCESS_STATE_CODE, processStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void createOrUpdateLayerProcessState(Long lexemeId, LayerName layerName, String processStateCode) {

		int updateCount = create
				.update(LAYER_STATE)
				.set(LAYER_STATE.PROCESS_STATE_CODE, processStateCode)
				.where(LAYER_STATE.LEXEME_ID.eq(lexemeId).and(LAYER_STATE.LAYER_NAME.eq(layerName.name())))
				.execute();

		if (updateCount == 0) {
			create
				.insertInto(
						LAYER_STATE,
						LAYER_STATE.LEXEME_ID,
						LAYER_STATE.LAYER_NAME,
						LAYER_STATE.PROCESS_STATE_CODE)
				.values(
						lexemeId,
						layerName.name(),
						processStateCode)
				.execute();
		}
	}
}
