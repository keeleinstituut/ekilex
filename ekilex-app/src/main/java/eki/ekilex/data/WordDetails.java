package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "An entity that unites extensive details about the queried word.  \n" +
		"Includes a list of lexemes that contain this word and info about related words.")
public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Lexeme> lexemes;

	private WordRelationDetails wordRelationDetails;

	private String firstDefinitionValue;

	private boolean activeTagComplete;

	public WordDetails() {
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<Lexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<Lexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public WordRelationDetails getWordRelationDetails() {
		return wordRelationDetails;
	}

	public void setWordRelationDetails(WordRelationDetails wordRelationDetails) {
		this.wordRelationDetails = wordRelationDetails;
	}

	public String getFirstDefinitionValue() {
		return firstDefinitionValue;
	}

	public void setFirstDefinitionValue(String firstDefinitionValue) {
		this.firstDefinitionValue = firstDefinitionValue;
	}

	public boolean isActiveTagComplete() {
		return activeTagComplete;
	}

	public void setActiveTagComplete(boolean activeTagComplete) {
		this.activeTagComplete = activeTagComplete;
	}
}
