package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.Lexeme;

public class LexemeRowMapper extends AbstractRowMapper implements RowMapper<Lexeme> {

	@Override
	public Lexeme mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("id", Long.class);
		Long wordId = rs.getObject("word_id", Long.class);
		Long meaningId = rs.getObject("meaning_id", Long.class);
		String datasetCode = rs.getString("dataset_code");
		String frequencyGroupCode = rs.getString("frequency_group_code");
		Float corpusFrequency = getFloat(rs, "corpus_frequency");
		Integer level1 = rs.getObject("level1", Integer.class);
		Integer level2 = rs.getObject("level2", Integer.class);
		Integer level3 = rs.getObject("level3", Integer.class);
		String valueStateCode = rs.getString("value_state_code");
		String processStateCode = rs.getString("process_state_code");
		Long orderBy = rs.getObject("order_by", Long.class);

		Lexeme lexeme = new Lexeme();
		lexeme.setLexemeId(lexemeId);
		lexeme.setWordId(wordId);
		lexeme.setMeaningId(meaningId);
		lexeme.setDatasetCode(datasetCode);
		lexeme.setFrequencyGroupCode(frequencyGroupCode);
		lexeme.setCorpusFrequency(corpusFrequency);
		lexeme.setLevel1(level1);
		lexeme.setLevel2(level2);
		lexeme.setLevel3(level3);
		lexeme.setValueStateCode(valueStateCode);
		lexeme.setProcessStateCode(processStateCode);
		lexeme.setOrderBy(orderBy);
		return lexeme;
	}

}