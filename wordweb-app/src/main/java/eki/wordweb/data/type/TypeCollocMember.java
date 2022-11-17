package eki.wordweb.data.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.data.AbstractDataObject;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeCollocMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private String word;

	private String form;

	private Integer homonymNr;

	private String conjunct;

	private Float weight;

	private boolean headword;

	private boolean primary;

	private boolean context;

	private boolean preConjunct;

	private boolean postConjunct;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getConjunct() {
		return conjunct;
	}

	public void setConjunct(String conjunct) {
		this.conjunct = conjunct;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public boolean isHeadword() {
		return headword;
	}

	public void setHeadword(boolean headword) {
		this.headword = headword;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isContext() {
		return context;
	}

	public void setContext(boolean context) {
		this.context = context;
	}

	public boolean isPreConjunct() {
		return preConjunct;
	}

	public void setPreConjunct(boolean preConjunct) {
		this.preConjunct = preConjunct;
	}

	public boolean isPostConjunct() {
		return postConjunct;
	}

	public void setPostConjunct(boolean postConjunct) {
		this.postConjunct = postConjunct;
	}

}
