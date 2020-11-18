package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.common.data.util.AbstractRowMapper;
import eki.ekilex.data.transform.LexemeCollocationGroupTuple;

public class LexemeCollocationGroupTupleRowMapper extends AbstractRowMapper implements RowMapper<LexemeCollocationGroupTuple> {

	@Override
	public LexemeCollocationGroupTuple mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		Long posGroupId = rs.getObject("pos_group_id", Long.class);
		String posGroupCode = rs.getString("pos_group_code");
		Long posGroupOrderBy = rs.getObject("pos_group_order_by", Long.class);
		Long relGroupId = rs.getObject("rel_group_id", Long.class);
		String relGroupName = rs.getString("rel_group_name");
		Float relGroupFrequency = getFloat(rs, "rel_group_frequency");
		Float relGroupScore = getFloat(rs, "rel_group_score");
		Long relGroupOrderBy = rs.getObject("rel_group_order_by", Long.class);

		LexemeCollocationGroupTuple tuple = new LexemeCollocationGroupTuple();
		tuple.setLexemeId(lexemeId);
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
