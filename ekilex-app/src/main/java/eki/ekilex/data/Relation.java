package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class Relation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "lexeme_id")
	private Long lexemeId;

	@Column(name = "lex_value_state_codes")
	private List<String> lexemeValueStateCodes;

	@Column(name = "lex_register_codes")
	private List<String> lexemeRegisterCodes;

	@Column(name = "lex_government_values")
	private List<String> lexemeGovernmentValues;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "word")
	private String word;

	@Column(name = "word_lang")
	private String wordLang;

	@Column(name = "word_aspect_code")
	private String wordAspectCode;

	@Column(name = "word_lexeme_dataset_codes")
	private List<String> wordLexemeDatasetCodes;

	@Column(name = "rel_type_code")
	private String relationTypeCode;

	@Column(name = "rel_type_label")
	private String relationTypeLabel;

	@Column(name = "order_by")
	private Long orderBy;

	@Column(name = "relation_status")
	private RelationStatus relationStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public List<String> getLexemeValueStateCodes() {
		return lexemeValueStateCodes;
	}

	public void setLexemeValueStateCodes(List<String> lexemeValueStateCodes) {
		this.lexemeValueStateCodes = lexemeValueStateCodes;
	}

	public List<String> getLexemeRegisterCodes() {
		return lexemeRegisterCodes;
	}

	public void setLexemeRegisterCodes(List<String> lexemeRegisterCodes) {
		this.lexemeRegisterCodes = lexemeRegisterCodes;
	}

	public List<String> getLexemeGovernmentValues() {
		return lexemeGovernmentValues;
	}

	public void setLexemeGovernmentValues(List<String> lexemeGovernmentValues) {
		this.lexemeGovernmentValues = lexemeGovernmentValues;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
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

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public String getWordAspectCode() {
		return wordAspectCode;
	}

	public void setWordAspectCode(String wordAspectCode) {
		this.wordAspectCode = wordAspectCode;
	}

	public List<String> getWordLexemeDatasetCodes() {
		return wordLexemeDatasetCodes;
	}

	public void setWordLexemeDatasetCodes(List<String> wordLexemeDatasetCodes) {
		this.wordLexemeDatasetCodes = wordLexemeDatasetCodes;
	}

	public String getRelationTypeLabel() {
		return relationTypeLabel;
	}

	public void setRelationTypeLabel(String relationTypeLabel) {
		this.relationTypeLabel = relationTypeLabel;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public String getRelationTypeCode() {
		return relationTypeCode;
	}

	public void setRelationTypeCode(String relationTypeCode) {
		this.relationTypeCode = relationTypeCode;
	}

	public RelationStatus getRelationStatus() {
		return relationStatus;
	}

	public void setRelationStatus(RelationStatus relationStatus) {
		this.relationStatus = relationStatus;
	}
}
