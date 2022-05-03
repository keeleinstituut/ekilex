package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class MeaningLexemeWordTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private Long meaningId;

	private String datasetCode;

	private String wordValue;

	private String wordLanguageCode;

	private String wordGenderCode;

	private String wordDisplayMorphCode;

	private String wordTypeCode;

	private List<TypeValueNameLang> definitionValuesAndSourceNames;

	private boolean lexemeIsPublic;

	private String lexemePosCode;

	private String lexemeValueStateCode;

	private List<TypeValueNameLang> lexemeNoteValuesAndSourceNames;

	private List<TypeValueNameLang> lexemeUsageValuesAndSourceNames;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getWordLanguageCode() {
		return wordLanguageCode;
	}

	public void setWordLanguageCode(String wordLanguageCode) {
		this.wordLanguageCode = wordLanguageCode;
	}

	public String getWordGenderCode() {
		return wordGenderCode;
	}

	public void setWordGenderCode(String wordGenderCode) {
		this.wordGenderCode = wordGenderCode;
	}

	public String getWordDisplayMorphCode() {
		return wordDisplayMorphCode;
	}

	public void setWordDisplayMorphCode(String wordDisplayMorphCode) {
		this.wordDisplayMorphCode = wordDisplayMorphCode;
	}

	public String getWordTypeCode() {
		return wordTypeCode;
	}

	public void setWordTypeCode(String wordTypeCode) {
		this.wordTypeCode = wordTypeCode;
	}

	public List<TypeValueNameLang> getDefinitionValuesAndSourceNames() {
		return definitionValuesAndSourceNames;
	}

	public void setDefinitionValuesAndSourceNames(List<TypeValueNameLang> definitionValuesAndSourceNames) {
		this.definitionValuesAndSourceNames = definitionValuesAndSourceNames;
	}

	public boolean isLexemeIsPublic() {
		return lexemeIsPublic;
	}

	public void setLexemeIsPublic(boolean lexemeIsPublic) {
		this.lexemeIsPublic = lexemeIsPublic;
	}

	public String getLexemePosCode() {
		return lexemePosCode;
	}

	public void setLexemePosCode(String lexemePosCode) {
		this.lexemePosCode = lexemePosCode;
	}

	public String getLexemeValueStateCode() {
		return lexemeValueStateCode;
	}

	public void setLexemeValueStateCode(String lexemeValueStateCode) {
		this.lexemeValueStateCode = lexemeValueStateCode;
	}

	public List<TypeValueNameLang> getLexemeNoteValuesAndSourceNames() {
		return lexemeNoteValuesAndSourceNames;
	}

	public void setLexemeNoteValuesAndSourceNames(List<TypeValueNameLang> lexemeNoteValuesAndSourceNames) {
		this.lexemeNoteValuesAndSourceNames = lexemeNoteValuesAndSourceNames;
	}

	public List<TypeValueNameLang> getLexemeUsageValuesAndSourceNames() {
		return lexemeUsageValuesAndSourceNames;
	}

	public void setLexemeUsageValuesAndSourceNames(List<TypeValueNameLang> lexemeUsageValuesAndSourceNames) {
		this.lexemeUsageValuesAndSourceNames = lexemeUsageValuesAndSourceNames;
	}

}
