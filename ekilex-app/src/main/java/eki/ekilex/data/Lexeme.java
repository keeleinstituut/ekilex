package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Lexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "word")
	private String word;

	@Column(name = "homonym_nr")
	private Integer homonymNumber;

	@Column(name = "word_lang")
	private String wordLang;

	@Column(name = "word_gender_code")
	private String wordGenderCode;

	@Column(name = "word_display_morph_code")
	private String wordDisplayMorphCode;

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

	@Column(name = "lexeme_frequency_group_code")
	private String lexemeFrequencyGroupCode;

	@Column(name = "lexeme_frequencies")
	private List<String> lexemeFrequencies;

	@Column(name = "lexeme_value_state_code")
	private String lexemeValueStateCode;

	@Column(name = "lexeme_process_state_code")
	private String lexemeProcessStateCode;

	@Column(name = "order_by")
	private Long orderBy;

	private Boolean affixoid;

	private List<Classifier> wordTypes;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Classifier> regions;

	private List<Usage> usages;

	private List<Image> images;

	private List<FreeForm> freeforms;

	private List<Note> publicNotes;

	private List<FreeForm> grammars;

	private List<SourceLink> sourceLinks;

	private List<Relation> lexemeRelations;

	private boolean classifiersExist;

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

	public String getLexemeFrequencyGroupCode() {
		return lexemeFrequencyGroupCode;
	}

	public void setLexemeFrequencyGroupCode(String lexemeFrequencyGroupCode) {
		this.lexemeFrequencyGroupCode = lexemeFrequencyGroupCode;
	}

	public List<String> getLexemeFrequencies() {
		return lexemeFrequencies;
	}

	public void setLexemeFrequencies(List<String> lexemeFrequencies) {
		this.lexemeFrequencies = lexemeFrequencies;
	}

	public String getLexemeValueStateCode() {
		return lexemeValueStateCode;
	}

	public void setLexemeValueStateCode(String lexemeValueStateCode) {
		this.lexemeValueStateCode = lexemeValueStateCode;
	}

	public String getLexemeProcessStateCode() {
		return lexemeProcessStateCode;
	}

	public void setLexemeProcessStateCode(String lexemeProcessStateCode) {
		this.lexemeProcessStateCode = lexemeProcessStateCode;
	}

	public Boolean isAffixoid() {
		return affixoid;
	}

	public void setAffixoid(Boolean affixoid) {
		this.affixoid = affixoid;
	}

	public List<Classifier> getWordTypes() {
		return wordTypes;
	}

	public void setWordTypes(List<Classifier> wordTypes) {
		this.wordTypes = wordTypes;
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

	public List<Classifier> getRegions() {
		return regions;
	}

	public void setRegions(List<Classifier> regions) {
		this.regions = regions;
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<FreeForm> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<FreeForm> freeforms) {
		this.freeforms = freeforms;
	}

	public List<Note> getPublicNotes() {
		return publicNotes;
	}

	public void setPublicNotes(List<Note> publicNotes) {
		this.publicNotes = publicNotes;
	}

	public List<FreeForm> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<FreeForm> grammars) {
		this.grammars = grammars;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public boolean isClassifiersExist() {
		return classifiersExist;
	}

	public void setClassifiersExist(boolean classifiersExist) {
		this.classifiersExist = classifiersExist;
	}

	public List<Relation> getLexemeRelations() {
		return lexemeRelations;
	}

	public void setLexemeRelations(List<Relation> lexemeRelations) {
		this.lexemeRelations = lexemeRelations;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
