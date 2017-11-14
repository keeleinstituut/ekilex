package eki.eve.data;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class WordLexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Classifier> domains;

	private List<Form> words;

	private List<Rection> rections;

	@Column(name = "datasets")
	private List<String> datasets;

	private List<Definition> definitions;

	private List<FreeForm> meaningFreeforms;

	private List<FreeForm> lexemeFreeforms;

	public WordLexeme() {
	}

	public WordLexeme(Consumer<WordLexeme> builder) {
		builder.accept(this);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

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

	public Integer getLevel1() {
		return level1;
	}

	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}

	public Integer getLevel2() {
		return level2;
	}

	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}

	public Integer getLevel3() {
		return level3;
	}

	public void setLevel3(Integer level3) {
		this.level3 = level3;
	}

	public String getLexemeTypeCode() {
		return lexemeTypeCode;
	}

	public void setLexemeTypeCode(String lexemeTypeCode) {
		this.lexemeTypeCode = lexemeTypeCode;
	}

	public String getLexemeFrequencyGroupCode() {
		return lexemeFrequencyGroupCode;
	}

	public void setLexemeFrequencyGroupCode(String lexemeFrequencyGroupCode) {
		this.lexemeFrequencyGroupCode = lexemeFrequencyGroupCode;
	}

	public String getMeaningTypeCode() {
		return meaningTypeCode;
	}

	public void setMeaningTypeCode(String meaningTypeCode) {
		this.meaningTypeCode = meaningTypeCode;
	}

	public String getMeaningEntryClassCode() {
		return meaningEntryClassCode;
	}

	public void setMeaningEntryClassCode(String meaningEntryClassCode) {
		this.meaningEntryClassCode = meaningEntryClassCode;
	}

	public String getMeaningStateCode() {
		return meaningStateCode;
	}

	public void setMeaningStateCode(String meaningStateCode) {
		this.meaningStateCode = meaningStateCode;
	}

	public List<Classifier> getDomains() {
		return domains;
	}

	@Column(name = "word")
	private String word;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "lexeme_id")
	private Long lexemeId;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "level1")
	private Integer level1;

	@Column(name = "level2")
	private Integer level2;

	@Column(name = "level3")
	private Integer level3;

	@Column(name = "lexeme_type_code")
	private String lexemeTypeCode;

	@Column(name = "lexeme_frequency_group_code")
	private String lexemeFrequencyGroupCode;

	@Column(name = "meaning_type_code")
	private String meaningTypeCode;

	@Column(name = "meaning_entry_class_code")
	private String meaningEntryClassCode;

	@Column(name = "meaning_state_code")
	private String meaningStateCode;

	public void setDomains(List<Classifier> domains) {
		this.domains = domains;
	}

	public List<Form> getWords() {
		return words;
	}

	public void setWords(List<Form> words) {
		this.words = words;
	}

	public List<Rection> getRections() {
		return rections;
	}

	public void setRections(List<Rection> rections) {
		this.rections = rections;
	}

	public List<String> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<String> datasets) {
		this.datasets = datasets;
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

	public List<FreeForm> getLexemeFreeforms() {
		return lexemeFreeforms;
	}

	public void setLexemeFreeforms(List<FreeForm> lexemeFreeforms) {
		this.lexemeFreeforms = lexemeFreeforms;
	}

	public boolean isFreeformsExist() {
		return (lexemeFreeforms != null && lexemeFreeforms.size() > 0) || (meaningFreeforms != null && meaningFreeforms.size() > 0);
	}

}
