package eki.ekilex.service.db;

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
						PROCESS_LOG.DATASET_CODE)
				.from(MEANING_PROCESS_LOG, PROCESS_LOG)
				.where(
						MEANING_PROCESS_LOG.MEANING_ID.eq(meaningId)
								.and(MEANING_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.orderBy(PROCESS_LOG.EVENT_ON.desc())
				.fetchInto(ProcessLog.class);

		return results;
	}

	public List<ProcessLog> getLogForWord(Long wordId) {

		List<ProcessLog> results = create
				.select(
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.EVENT_ON,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE,
						PROCESS_LOG.DATASET_CODE)
				.from(WORD_PROCESS_LOG, PROCESS_LOG)
				.where(
						WORD_PROCESS_LOG.WORD_ID.eq(wordId)
								.and(WORD_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.orderBy(PROCESS_LOG.EVENT_ON.desc())
				.fetchInto(ProcessLog.class);

		return results;
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
						PROCESS_LOG.DATASET_CODE)
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
								PROCESS_LOG.DATASET_CODE)
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
						PROCESS_LOG.DATASET_CODE)
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
								PROCESS_LOG.DATASET_CODE)
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
						LEXEME.PROCESS_STATE_CODE,
						LEXEME.DATASET_CODE
				)
				.from(LEXEME)
				.where(LEXEME.ID.eq(entityId))
				.fetchSingleInto(LexemeData.class);
		return lexemeData;
	}

	public void createLexemeProcessLog(Long lexemeId, String eventBy, String datasetCode, String comment, String commentPrese, String processStateCode) {

		Long processLogId = createProcessLog(eventBy, datasetCode, comment, commentPrese, processStateCode);
		createLexemeProcessLog(lexemeId, processLogId);
	}

	public void createMeaningProcessLog(Long meaningId, String dataset, String eventBy, String comment, String commentPrese) {

		Long processLogId = createProcessLog(eventBy, dataset, comment, commentPrese, null);
		createMeaningProcessLog(meaningId, processLogId);
	}

	public void createWordProcessLog(Long wordId, String dataset, String eventBy, String comment, String commentPrese) {

		Long processLogId = createProcessLog(eventBy, dataset, comment, commentPrese, null);
		createWordProcessLog(wordId, processLogId);
	}

	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {

		create.
				update(LEXEME)
				.set(LEXEME.PROCESS_STATE_CODE, processStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	private Long createProcessLog(String eventBy, String datasetCode, String comment, String commentPrese, String processStateCode) {

		Long processLogId = create
				.insertInto(
						PROCESS_LOG,
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.COMMENT_PRESE,
						PROCESS_LOG.PROCESS_STATE_CODE)
				.values(
						eventBy,
						datasetCode,
						comment,
						commentPrese,
						processStateCode)
				.returning(PROCESS_LOG.ID)
				.fetchOne()
				.getId();

		return processLogId;
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

	private void createMeaningProcessLog(Long meaningId, Long processLogId) {

		create
				.insertInto(
						MEANING_PROCESS_LOG,
						MEANING_PROCESS_LOG.MEANING_ID,
						MEANING_PROCESS_LOG.PROCESS_LOG_ID)
				.values(meaningId, processLogId)
				.execute();
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
}
