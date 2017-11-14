package eki.eve.data;

import java.util.List;
import java.util.function.Consumer;

import eki.common.data.AbstractDataObject;

public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Form> forms;

	private List<WordLexeme> lexemes;

	public WordDetails() {
	}

	public WordDetails(Consumer<WordDetails> builder) {
		builder.accept(this);
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public List<WordLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<WordLexeme> lexemes) {
		this.lexemes = lexemes;
	}
}
