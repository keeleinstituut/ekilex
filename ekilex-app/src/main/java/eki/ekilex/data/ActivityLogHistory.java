package eki.ekilex.data;

import java.util.List;

public class ActivityLogHistory extends ActivityLog {

	private static final long serialVersionUID = 1L;

	private List<String> wordValues;

	private List<String> definitionValues;

	private List<Long> lexemeIds;

	public List<String> getWordValues() {
		return wordValues;
	}

	public void setWordValues(List<String> wordValues) {
		this.wordValues = wordValues;
	}

	public List<String> getDefinitionValues() {
		return definitionValues;
	}

	public void setDefinitionValues(List<String> definitionValues) {
		this.definitionValues = definitionValues;
	}

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
	}

}
