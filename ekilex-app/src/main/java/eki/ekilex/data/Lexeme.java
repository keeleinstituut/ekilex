package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Lexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "word")
	private String word;

	@Column(name = "homonym_nr")
	private Integer homonymNumber;

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
	private String typeCode;

	@Column(name = "lexeme_frequency_group_code")
	private String frequencyGroupCode;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Rection> rections;

	private List<FreeForm> freeforms;

	private boolean classifiersExist;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
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

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getFrequencyGroupCode() {
		return frequencyGroupCode;
	}

	public void setFrequencyGroupCode(String frequencyGroupCode) {
		this.frequencyGroupCode = frequencyGroupCode;
	}

	public List<Classifier> getPos() {
		return pos;
	}

	public void setPos(List<Classifier> pos) {
		this.pos = pos;
	}

	public List<Classifier> getDerivs() {
		return derivs;
	}

	public void setDerivs(List<Classifier> derivs) {
		this.derivs = derivs;
	}

	public List<Classifier> getRegisters() {
		return registers;
	}

	public void setRegisters(List<Classifier> registers) {
		this.registers = registers;
	}

	public List<Rection> getRections() {
		return rections;
	}

	public void setRections(List<Rection> rections) {
		this.rections = rections;
	}

	public List<FreeForm> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<FreeForm> freeforms) {
		this.freeforms = freeforms;
	}

	public boolean isClassifiersExist() {
		return classifiersExist;
	}

	public void setClassifiersExist(boolean classifiersExist) {
		this.classifiersExist = classifiersExist;
	}

}
