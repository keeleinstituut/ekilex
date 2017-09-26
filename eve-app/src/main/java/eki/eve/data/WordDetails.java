package eki.eve.data;

import java.util.List;
import java.util.function.Consumer;

public class WordDetails extends DomainData {

	private List<Form> forms;

	private List<Meaning> meanings;

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

	public List<Meaning> getMeanings() {
		return meanings;
	}

	public void setMeanings(List<Meaning> meanings) {
		this.meanings = meanings;
	}
}
