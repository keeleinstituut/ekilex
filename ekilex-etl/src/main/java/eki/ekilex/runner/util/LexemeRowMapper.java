package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transform.Lexeme;

public class LexemeRowMapper implements RowMapper<Lexeme> {

	@Override
	public Lexeme mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Long lexemeId = rs.getLong("id");
		Long wordId = rs.getLong("word_id");
		Long meaningId = rs.getLong("meaning_id");
		String datasetCode = rs.getString("dataset_code");
		String frequencyGroupCode = rs.getString("frequency_group_code");
		Float corpusFrequency = rs.getFloat("corpus_frequency");
		Integer level1 = rs.getInt("level1");
		Integer level2 = rs.getInt("level2");
		Integer level3 = rs.getInt("level3");
		String valueStateCode = rs.getString("value_state_code");
		String processStateCode = rs.getString("process_state_code");
		Long orderBy = rs.getLong("order_by");

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