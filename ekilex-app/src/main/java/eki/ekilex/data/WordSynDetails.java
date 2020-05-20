package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordSynDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<WordSynLexeme> lexemes;

	private List<SynRelation> relations;

	private boolean synLayerComplete;

	private Integer wordProcessLogCount;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<WordSynLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<WordSynLexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<SynRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<SynRelation> relations) {
		this.relations = relations;
	}

	public boolean isSynLayerComplete() {
		return synLayerComplete;
	}

	public void setSynLayerComplete(boolean synLayerComplete) {
		this.synLayerComplete = synLayerComplete;
	}

	public Integer getWordProcessLogCount() {
		return wordProcessLogCount;
	}

	public void setWordProcessLogCount(Integer wordProcessLogCount) {
		this.wordProcessLogCount = wordProcessLogCount;
	}

}
