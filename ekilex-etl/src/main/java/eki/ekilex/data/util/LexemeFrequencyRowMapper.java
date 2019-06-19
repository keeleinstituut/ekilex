package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeFrequency;

public class LexemeFrequencyRowMapper extends AbstractRowMapper implements RowMapper<LexemeFrequency> {

	@Override
	public LexemeFrequency mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		String sourceName = rs.getString("source_name");
		Timestamp createdOn = rs.getTimestamp("created_on");
		Long rank = rs.getObject("rank", Long.class);
		Float value = getFloat(rs, "value");

		LexemeFrequency lexemeFrequency = new LexemeFrequency();
		lexemeFrequency.setLexemeId(lexemeId);
		lexemeFrequency.setSourceName(sourceName);
		lexemeFrequency.setCreatedOn(createdOn);
		lexemeFrequency.setRank(rank);
		lexemeFrequency.setValue(value);
		return lexemeFrequency;
	}

}
