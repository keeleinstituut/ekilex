package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.PROCESS_LOG;

import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessLogDbService {

	@Autowired
	private DSLContext create;

	public Map<String, Object> getLexemeData(Long entityId) {

		Map<String, Object> result = create
				.select(
						LEXEME.PROCESS_STATE_CODE,
						LEXEME.DATASET_CODE
				)
				.from(LEXEME)
				.where(LEXEME.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Long createLexemeProcessLog(Long lexemeId, String eventBy, String datasetCode, String comment, String processStateCode) {

		Long processLogId = createProcessLog(eventBy, datasetCode, comment, processStateCode);
		createLexemeProcessLog(lexemeId, processLogId);
		return processLogId;
	}

	private Long createProcessLog(String eventBy, String datasetCode, String comment, String processStateCode) {

		Long processLogId = create
				.insertInto(
						PROCESS_LOG,
						PROCESS_LOG.EVENT_BY,
						PROCESS_LOG.DATASET_CODE,
						PROCESS_LOG.COMMENT,
						PROCESS_LOG.PROCESS_STATE_CODE)
				.values(
						eventBy,
						datasetCode,
						comment,
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
}
