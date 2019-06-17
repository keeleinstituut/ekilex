package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeCollocationTuple;

public class LexemeCollocationTupleRowMapper implements RowMapper<LexemeCollocationTuple> {

	@Override
	public LexemeCollocationTuple mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexCollocId = rs.getLong("lex_colloc_id");
		Long lexemeId = rs.getLong("lexeme_id");
		Long collocationId = rs.getLong("collocation_id");
		String memberForm = rs.getString("member_form");
		String conjunct = rs.getString("conjunct");
		Float weight = rs.getFloat("weight");
		Integer memberOrder = rs.getInt("member_order");
		Integer groupOrder = rs.getInt("group_order");
		Long posGroupId = rs.getLong("pos_group_id");
		String posGroupCode = rs.getString("pos_group_code");
		Long posGroupOrderBy = rs.getLong("pos_group_order_by");
		Long relGroupId = rs.getLong("rel_group_id");
		String relGroupName = rs.getString("rel_group_name");
		Float relGroupFrequency = rs.getFloat("rel_group_frequency");
		Float relGroupScore = rs.getFloat("rel_group_score");
		Long relGroupOrderBy = rs.getLong("rel_group_order_by");

		LexemeCollocationTuple tuple = new LexemeCollocationTuple();
		tuple.setLexCollocId(lexCollocId);
		tuple.setLexemeId(lexemeId);
		tuple.setCollocationId(collocationId);
		tuple.setMemberForm(memberForm);
		tuple.setConjunct(conjunct);
		tuple.setWeight(weight);
		tuple.setMemberOrder(memberOrder);
		tuple.setGroupOrder(groupOrder);
		tuple.setPosGroupId(posGroupId);
		tuple.setPosGroupCode(posGroupCode);
		tuple.setPosGroupOrderBy(posGroupOrderBy);
		tuple.setRelGroupId(relGroupId);
		tuple.setRelGroupName(relGroupName);
		tuple.setRelGroupFrequency(relGroupFrequency);
		tuple.setRelGroupScore(relGroupScore);
		tuple.setRelGroupOrderBy(relGroupOrderBy);
		return tuple;
	}

}
