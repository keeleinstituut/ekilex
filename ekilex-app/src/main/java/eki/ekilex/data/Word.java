package eki.ekilex.data;

import java.util.List;

public class Word extends AbstractCrudEntity implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private String vocalForm;

	private Integer homonymNr;

	private String lang;

	private String wordClass;

	private String genderCode;

	private String aspectCode;

	private String morphCode;

	private String displayMorphCode;

	private List<WordNote> notes;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private Boolean lexemesArePublic;

	private List<String> lexemesValueStateCodes;

	private List<String> lexemesTagNames;

	private List<String> datasetCodes;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	@Override
	public String getWordValue() {
		return wordValue;
	}

	@Override
	public void setWordValue(String value) {
		this.wordValue = value;
	}

	@Override
	public String getWordValuePrese() {
		return wordValuePrese;
	}

	@Override
	public void setWordValuePrese(String valuePrese) {
		this.wordValuePrese = valuePrese;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getAspectCode() {
		return aspectCode;
	}

	public void setAspectCode(String aspectCode) {
		this.aspectCode = aspectCode;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public String getDisplayMorphCode() {
		return displayMorphCode;
	}

	public void setDisplayMorphCode(String displayMorphCode) {
		this.displayMorphCode = displayMorphCode;
	}

	public List<WordNote> getNotes() {
		return notes;
	}

	public void setNotes(List<WordNote> notes) {
		this.notes = notes;
	}

	@Override
	public String[] getWordTypeCodes() {
		return wordTypeCodes;
	}

	@Override
	public void setWordTypeCodes(String[] wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	@Override
	public boolean isPrefixoid() {
		return prefixoid;
	}

	@Override
	public void setPrefixoid(boolean prefixoid) {
		this.prefixoid = prefixoid;
	}

	@Override
	public boolean isSuffixoid() {
		return suffixoid;
	}

	@Override
	public void setSuffixoid(boolean suffixoid) {
		this.suffixoid = suffixoid;
	}

	@Override
	public boolean isForeign() {
		return foreign;
	}

	@Override
	public void setForeign(boolean foreign) {
		this.foreign = foreign;
	}

	public Boolean getLexemesArePublic() {
		return lexemesArePublic;
	}

	public void setLexemesArePublic(Boolean lexemesArePublic) {
		this.lexemesArePublic = lexemesArePublic;
	}

	public List<String> getLexemesValueStateCodes() {
		return lexemesValueStateCodes;
	}

	public void setLexemesValueStateCodes(List<String> lexemesValueStateCodes) {
		this.lexemesValueStateCodes = lexemesValueStateCodes;
	}

	public List<String> getLexemesTagNames() {
		return lexemesTagNames;
	}

	public void setLexemesTagNames(List<String> lexemesTagNames) {
		this.lexemesTagNames = lexemesTagNames;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

}
