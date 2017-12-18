package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Meaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "meaning_type_code")
	private String meaningTypeCode;

	@Column(name = "meaning_process_state_code")
	private String meaningProcessStateCode;

	@Column(name = "meaning_state_code")
	private String meaningStateCode;

	@Column(name = "lexeme_ids")
	private List<Long> lexemeIds;

	private List<Lexeme> lexemes;

	private List<Classifier> domains;

	private List<Definition> definitions;

	private List<FreeForm> meaningFreeforms;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getMeaningTypeCode() {
		return meaningTypeCode;
	}

	public void setMeaningTypeCode(String meaningTypeCode) {
		this.meaningTypeCode = meaningTypeCode;
	}

	public String getMeaningProcessStateCode() {
		return meaningProcessStateCode;
	}

	public void setMeaningProcessStateCode(String meaningProcessStateCode) {
		this.meaningProcessStateCode = meaningProcessStateCode;
	}

	public String getMeaningStateCode() {
		return meaningStateCode;
	}

	public void setMeaningStateCode(String meaningStateCode) {
		this.meaningStateCode = meaningStateCode;
	}

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
	}

	public List<Lexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<Lexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<Classifier> getDomains() {
		return domains;
	}

	public void setDomains(List<Classifier> domains) {
		this.domains = domains;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<FreeForm> getMeaningFreeforms() {
		return meaningFreeforms;
	}

	public void setMeaningFreeforms(List<FreeForm> meaningFreeforms) {
		this.meaningFreeforms = meaningFreeforms;
	}

}
