package eki.ekilex.data;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class WordLexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "word")
	private String word;

	@Column(name = "word_lang")
	private String wordLang;

	@Column(name = "word_display_morph_code")
	private String wordDisplayMorphCode;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "lexeme_id")
	private Long lexemeId;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "dataset")
	private String dataset;

	@Column(name = "level1")
	private Integer level1;

	@Column(name = "level2")
	private Integer level2;

	@Column(name = "level3")
	private Integer level3;

	private String levels;

	@Column(name = "lexeme_type_code")
	private String lexemeTypeCode;

	@Column(name = "lexeme_frequency_group_code")
	private String lexemeFrequencyGroupCode;

	@Column(name = "meaning_type_code")
	private String meaningTypeCode;

	@Column(name = "meaning_process_state_code")
	private String meaningProcessStateCode;

	@Column(name = "meaning_state_code")
	private String meaningStateCode;

	private List<Classifier> lexemePos;

	private List<Classifier> lexemeDerivs;

	private List<Classifier> lexemeRegisters;

	private List<Classifier> meaningDomains;

	private List<Form> words;

	private List<Rection> rections;

	private List<Definition> definitions;

	private List<FreeForm> meaningFreeforms;

	private List<FreeForm> lexemeFreeforms;

	private List<Relation> lexemeRelations;

	private List<Relation> wordRelations;

	private boolean lexemeOrMeaningClassifiersExist;

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

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public String getWordDisplayMorphCode() {
		return wordDisplayMorphCode;
	}

	public void setWordDisplayMorphCode(String wordDisplayMorphCode) {
		this.wordDisplayMorphCode = wordDisplayMorphCode;
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

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
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

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
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

	public List<Classifier> getLexemePos() {
		return lexemePos;
	}

	public void setLexemePos(List<Classifier> lexemePos) {
		this.lexemePos = lexemePos;
	}

	public List<Classifier> getLexemeDerivs() {
		return lexemeDerivs;
	}

	public void setLexemeDerivs(List<Classifier> lexemeDerivs) {
		this.lexemeDerivs = lexemeDerivs;
	}

	public List<Classifier> getLexemeRegisters() {
		return lexemeRegisters;
	}

	public void setLexemeRegisters(List<Classifier> lexemeRegisters) {
		this.lexemeRegisters = lexemeRegisters;
	}

	public List<Classifier> getMeaningDomains() {
		return meaningDomains;
	}

	public void setMeaningDomains(List<Classifier> meaningDomains) {
		this.meaningDomains = meaningDomains;
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

	public List<Relation> getLexemeRelations() {
		return lexemeRelations;
	}

	public void setLexemeRelations(List<Relation> lexemeRelations) {
		this.lexemeRelations = lexemeRelations;
	}

	public List<Relation> getWordRelations() {
		return wordRelations;
	}

	public void setWordRelations(List<Relation> wordRelations) {
		this.wordRelations = wordRelations;
	}

	public boolean isLexemeOrMeaningClassifiersExist() {
		return lexemeOrMeaningClassifiersExist;
	}

	public void setLexemeOrMeaningClassifiersExist(boolean lexemeOrMeaningClassifiersExist) {
		this.lexemeOrMeaningClassifiersExist = lexemeOrMeaningClassifiersExist;
	}

	public boolean isFreeformsExist() {
		return (lexemeFreeforms != null && lexemeFreeforms.size() > 0) || (meaningFreeforms != null && meaningFreeforms.size() > 0);
	}

}
