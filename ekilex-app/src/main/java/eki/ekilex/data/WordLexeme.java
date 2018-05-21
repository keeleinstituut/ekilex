package eki.ekilex.data;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class WordLexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "words")
	private String[] words;

	@Column(name = "vocal_forms")
	private List<String> vocalForms;

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

	private String dataset;

	@Column(name = "dataset")
	private String datasetCode;

	@Column(name = "level1")
	private Integer level1;

	@Column(name = "level2")
	private Integer level2;

	@Column(name = "level3")
	private Integer level3;

	private String levels;

	@Column(name = "lexeme_value_state_code")
	private String lexemeValueStateCode;

	@Column(name = "lexeme_frequency_group_code")
	private String lexemeFrequencyGroupCode;

	@Column(name = "meaning_process_state_code")
	private String meaningProcessStateCode;

	@Column(name = "gender_code")
	private String genderCode;

	private List<Classifier> lexemePos;

	private List<Classifier> lexemeDerivs;

	private List<Classifier> lexemeRegisters;

	private List<Classifier> meaningDomains;

	private List<Word> meaningWords;

	private List<Government> governments;

	private List<Definition> definitions;

	private List<FreeForm> meaningFreeforms;

	private List<FreeForm> lexemeFreeforms;

	private List<Relation> lexemeRelations;

	private List<Relation> meaningRelations;

	private List<FreeForm> grammars;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<Collocation> secondaryCollocations;

	private boolean lexemeOrMeaningClassifiersExist;

	public WordLexeme() {
	}

	public WordLexeme(Consumer<WordLexeme> builder) {
		builder.accept(this);
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public List<String> getVocalForms() {
		return vocalForms;
	}

	public void setVocalForms(List<String> vocalForms) {
		this.vocalForms = vocalForms;
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

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
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

	public String getLexemeValueStateCode() {
		return lexemeValueStateCode;
	}

	public void setLexemeValueStateCode(String lexemeValueStateCode) {
		this.lexemeValueStateCode = lexemeValueStateCode;
	}

	public String getLexemeFrequencyGroupCode() {
		return lexemeFrequencyGroupCode;
	}

	public void setLexemeFrequencyGroupCode(String lexemeFrequencyGroupCode) {
		this.lexemeFrequencyGroupCode = lexemeFrequencyGroupCode;
	}

	public String getMeaningProcessStateCode() {
		return meaningProcessStateCode;
	}

	public void setMeaningProcessStateCode(String meaningProcessStateCode) {
		this.meaningProcessStateCode = meaningProcessStateCode;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
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

	public List<Word> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<Word> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<Government> getGovernments() {
		return governments;
	}

	public void setGovernments(List<Government> governments) {
		this.governments = governments;
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

	public List<Relation> getMeaningRelations() {
		return meaningRelations;
	}

	public void setMeaningRelations(List<Relation> meaningRelations) {
		this.meaningRelations = meaningRelations;
	}

	public List<FreeForm> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<FreeForm> grammars) {
		this.grammars = grammars;
	}

	public List<CollocationPosGroup> getCollocationPosGroups() {
		return collocationPosGroups;
	}

	public void setCollocationPosGroups(List<CollocationPosGroup> collocationPosGroups) {
		this.collocationPosGroups = collocationPosGroups;
	}

	public List<Collocation> getSecondaryCollocations() {
		return secondaryCollocations;
	}

	public void setSecondaryCollocations(List<Collocation> secondaryCollocations) {
		this.secondaryCollocations = secondaryCollocations;
	}

	public boolean isLexemeOrMeaningClassifiersExist() {
		return lexemeOrMeaningClassifiersExist;
	}

	public void setLexemeOrMeaningClassifiersExist(boolean lexemeOrMeaningClassifiersExist) {
		this.lexemeOrMeaningClassifiersExist = lexemeOrMeaningClassifiersExist;
	}

}
