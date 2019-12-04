package eki.ekilex.data;

import java.util.List;

public class MeaningWordLangGroup extends LangGroup {

	private static final long serialVersionUID = 1L;

	private List<MeaningWord> meaningWords;

	public List<MeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<MeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}
}
