package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.LexemeCollocationTuple;

public class LexemeCollocationTupleRowMapper extends AbstractRowMapper implements RowMapper<LexemeCollocationTuple> {

	@Override
	public LexemeCollocationTuple mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexCollocId = rs.getObject("lex_colloc_id", Long.class);
		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		Long collocationId = rs.getObject("collocation_id", Long.class);
		String memberForm = rs.getString("member_form");
		String conjunct = rs.getString("conjunct");
		Float weight = getFloat(rs, "weight");
		Integer memberOrder = rs.getObject("member_order", Integer.class);
		Integer groupOrder = rs.getObject("group_order", Integer.class);
		Long posGroupId = rs.getObject("pos_group_id", Long.class);
		String posGroupCode = rs.getString("pos_group_code");
		Long posGroupOrderBy = rs.getObject("pos_group_order_by", Long.class);
		Long relGroupId = rs.getObject("rel_group_id", Long.class);
		String relGroupName = rs.getString("rel_group_name");
		Float relGroupFrequency = getFloat(rs, "rel_group_frequency");
		Float relGroupScore = getFloat(rs, "rel_group_score");
		Long relGroupOrderBy = rs.getObject("rel_group_order_by", Long.class);

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
