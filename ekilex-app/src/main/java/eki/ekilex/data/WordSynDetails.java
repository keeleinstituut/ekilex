package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordSynDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String word;

	private String language;

	private String morphCode;

	private List<WordSynLexeme> lexemes;

	private List<SynRelation> relations;

	public String getWord() {
        		return word;
    }

    public void setWord(String word) {
        		this.word = word;
    }

	public List<SynRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<SynRelation> relations) {
		this.relations = relations;
	}

	public List<WordSynLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<WordSynLexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}
}
