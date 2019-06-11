package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeFrequency;

public class LexemeFrequencyRowMapper implements RowMapper<LexemeFrequency> {

	@Override
	public LexemeFrequency mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getLong("lexeme_id");
		String sourceName = rs.getString("source_name");
		Timestamp createdOn = rs.getTimestamp("created_on");
		Long rank = rs.getLong("rank");
		Float value = rs.getFloat("value");

		LexemeFrequency lexemeFrequency = new LexemeFrequency();
		lexemeFrequency.setLexemeId(lexemeId);
		lexemeFrequency.setSourceName(sourceName);
		lexemeFrequency.setCreatedOn(createdOn);
		lexemeFrequency.setRank(rank);
		lexemeFrequency.setValue(value);
		return lexemeFrequency;
	}

}
