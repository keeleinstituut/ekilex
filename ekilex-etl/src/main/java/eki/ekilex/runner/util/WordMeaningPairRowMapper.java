package eki.ekilex.runner.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.WordMeaningPair;

public class WordMeaningPairRowMapper implements RowMapper<WordMeaningPair> {

	@Override
	public WordMeaningPair mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long wordId = rs.getObject("word_id", Long.class);
		Long meaningId = rs.getObject("meaning_id", Long.class);
		Array lexemeIdsPgArr = rs.getArray("lexeme_ids");
		Long[] lexemeIdsArr = (Long[]) lexemeIdsPgArr.getArray();
		List<Long> lexemeIds = Arrays.asList(lexemeIdsArr);
		WordMeaningPair wordMeaningPair = new WordMeaningPair(wordId, meaningId, lexemeIds);
		return wordMeaningPair;
	}
}