package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import java.util.List;
import java.util.function.Consumer;

public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Form> forms;

	private List<WordLexeme> meanings;

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

	public List<WordLexeme> getMeanings() {
		return meanings;
	}

	public void setMeanings(List<WordLexeme> meanings) {
		this.meanings = meanings;
	}
}
