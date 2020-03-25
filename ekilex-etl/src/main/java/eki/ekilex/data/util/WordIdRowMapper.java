package eki.ekilex.data.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.WordId;

public class WordIdRowMapper implements RowMapper<WordId> {

	@Override
	public WordId mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long wordId = rs.getObject("word_id", Long.class);
		Array simWordIdsPgArr = rs.getArray("sim_word_ids");
		Long[] simWordIdsArr = (Long[]) simWordIdsPgArr.getArray();
		List<Long> simWordIds = Arrays.asList(simWordIdsArr);
		return new WordId(wordId, simWordIds);
	}
}
