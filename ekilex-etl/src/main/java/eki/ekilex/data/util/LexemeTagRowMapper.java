package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeTag;

public class LexemeTagRowMapper extends AbstractRowMapper implements RowMapper<LexemeTag> {

	@Override
	public LexemeTag mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		String tagName = rs.getString("tag_name");

		LexemeTag lexemeTag = new LexemeTag();
		lexemeTag.setLexemeId(lexemeId);
		lexemeTag.setTagName(tagName);
		return lexemeTag;
	}
}
