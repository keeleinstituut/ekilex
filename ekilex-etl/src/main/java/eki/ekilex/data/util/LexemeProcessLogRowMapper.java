package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeProcessLog;

public class LexemeProcessLogRowMapper extends AbstractRowMapper implements RowMapper<LexemeProcessLog> {

	@Override
	public LexemeProcessLog mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		String eventBy = rs.getString("event_by");
		Timestamp eventOn = rs.getTimestamp("event_on");
		String comment = rs.getString("comment");
		String processStateCode = rs.getString("process_state_code");
		String datasetCode = rs.getString("dataset_code");
		boolean sourceLinksExist = rs.getBoolean("source_links_exist");

		LexemeProcessLog lexemeProcessLog = new LexemeProcessLog();
		lexemeProcessLog.setLexemeId(lexemeId);
		lexemeProcessLog.setEventBy(eventBy);
		lexemeProcessLog.setEventOn(eventOn);
		lexemeProcessLog.setComment(comment);
		lexemeProcessLog.setProcessStateCode(processStateCode);
		lexemeProcessLog.setDatasetCode(datasetCode);
		lexemeProcessLog.setSourceLinksExist(sourceLinksExist);
		return lexemeProcessLog;
	}

}
