package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.common.data.util.AbstractRowMapper;
import eki.ekilex.data.transform.LexemeCollocationTuple;

public class LexemeCollocationTupleRowMapper extends AbstractRowMapper implements RowMapper<LexemeCollocationTuple> {

	@Override
	public LexemeCollocationTuple mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexCollocId = rs.getObject("lex_colloc_id", Long.class);
		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		Long relGroupId = rs.getObject("rel_group_id", Long.class);
		Long collocationId = rs.getObject("collocation_id", Long.class);
		String memberForm = rs.getString("member_form");
		String conjunct = rs.getString("conjunct");
		Float weight = getFloat(rs, "weight");
		Integer memberOrder = rs.getObject("member_order", Integer.class);
		Integer groupOrder = rs.getObject("group_order", Integer.class);

		LexemeCollocationTuple tuple = new LexemeCollocationTuple();
		tuple.setLexCollocId(lexCollocId);
		tuple.setLexemeId(lexemeId);
		tuple.setRelGroupId(relGroupId);
		tuple.setCollocationId(collocationId);
		tuple.setMemberForm(memberForm);
		tuple.setConjunct(conjunct);
		tuple.setWeight(weight);
		tuple.setMemberOrder(memberOrder);
		tuple.setGroupOrder(groupOrder);
		return tuple;
	}

}
