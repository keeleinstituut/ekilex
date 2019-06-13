package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeClassifier;

public class LexemeClassifierRowMapper implements RowMapper<LexemeClassifier> {

	@Override
	public LexemeClassifier mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getLong("lexeme_id");
		String code = rs.getString("code");

		LexemeClassifier lexemeClassifier = new LexemeClassifier();
		lexemeClassifier.setLexemeId(lexemeId);
		lexemeClassifier.setCode(code);
		return lexemeClassifier;
	}

}
