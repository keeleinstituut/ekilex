package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeRelation;

public class LexemeRelationRowMapper implements RowMapper<LexemeRelation> {

	@Override
	public LexemeRelation mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexeme1Id = rs.getObject("lexeme1_id", Long.class);
		Long lexeme2Id = rs.getObject("lexeme2_id", Long.class);
		String lexemeRelationTypeCode = rs.getString("lex_rel_type_code");
		Long orderBy = rs.getObject("order_by", Long.class);

		LexemeRelation lexemeRelation = new LexemeRelation();
		lexemeRelation.setLexeme1Id(lexeme1Id);
		lexemeRelation.setLexeme2Id(lexeme2Id);
		lexemeRelation.setLexemeRelationTypeCode(lexemeRelationTypeCode);
		lexemeRelation.setOrderBy(orderBy);
		return lexemeRelation;
	}

}
