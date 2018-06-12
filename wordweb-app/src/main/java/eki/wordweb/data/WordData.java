package eki.wordweb.data;

import java.util.List;
import java.util.Optional;

import eki.common.data.AbstractDataObject;
import org.apache.commons.collections4.CollectionUtils;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Lexeme> lexemes;

	private List<Paradigm> paradigms;

	private List<String> imageFiles;

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

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

	public List<String> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<String> imageFiles) {
		this.imageFiles = imageFiles;
	}

	public String getVocalForm() {
		Optional<Form> wordForm = getWordForm();
		return wordForm.isPresent() ? wordForm.get().getVocalForm() : null;
	}

	public String getSoundFile() {
		Optional<Form> wordForm = getWordForm();
		return wordForm.isPresent() ? wordForm.get().getSoundFile() : null;
	}

	private Optional<Form> getWordForm() {
		if (CollectionUtils.isNotEmpty(paradigms)) {
			if (paradigms.get(0).getForms() != null) {
				return paradigms.get(0).getForms().stream().filter(f -> f.isWord()).findFirst();
			}
		}
		return Optional.empty();
	}

}
