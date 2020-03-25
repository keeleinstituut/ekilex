package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.Complexity;
import eki.ekilex.data.transform.WordLexemeMeaning;

public class WordLexemeMeaningRowMapper implements RowMapper<WordLexemeMeaning> {

	@Override
	public WordLexemeMeaning mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		String complexityStr = rs.getObject("complexity", String.class);
		Complexity complexity = Complexity.valueOf(complexityStr);
		Long wordId = rs.getObject("word_id", Long.class);
		String word = rs.getObject("word", String.class);
		String lang = rs.getObject("lang", String.class);
		Integer homonymNr = rs.getObject("homonym_nr", Integer.class);
		Long orderBy = rs.getObject("order_by", Long.class);
		Long meaningId = rs.getObject("meaning_id", Long.class);
		String datasetCode = rs.getObject("dataset_code", String.class);
		return new WordLexemeMeaning(lexemeId, complexity, wordId, word, lang, homonymNr, orderBy, meaningId, datasetCode);
	}
}