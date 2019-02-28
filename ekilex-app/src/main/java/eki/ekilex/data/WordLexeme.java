package eki.ekilex.data;

import java.util.List;

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

	@Column(name = "lexeme_frequencies")
	private List<String> lexemeFrequencies;

	@Column(name = "lexeme_process_state_code")
	private String lexemeProcessStateCode;

	@Column(name = "meaning_process_state_code")
	private String meaningProcessStateCode;

	@Column(name = "gender_code")
	private String genderCode;

	@Column(name = "word_aspect_code")
	private String wordAspectCode;

	private List<Classifier> wordTypes;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Classifier> meaningDomains;

	private List<Word> meaningWords;

	private List<Government> governments;

	private List<FreeForm> grammars;

	private List<Usage> usages;

	private List<Definition> definitions;

	private List<FreeForm> meaningFreeforms;

	private List<FreeForm> meaningLearnerComments;

	private List<Relation> meaningRelations;

	private List<List<Relation>> groupedMeaningRelations;

	private List<FreeForm> lexemeFreeforms;

	private List<FreeForm> lexemePublicNotes;

	private List<Relation> lexemeRelations;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<Collocation> secondaryCollocations;

	private List<SourceLink> sourceLinks;

	private boolean lexemeOrMeaningClassifiersExist;

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

	public List<String> getLexemeFrequencies() {
		return lexemeFrequencies;
	}

	public void setLexemeFrequencies(List<String> lexemeFrequencies) {
		this.lexemeFrequencies = lexemeFrequencies;
	}

	public String getLexemeProcessStateCode() {
		return lexemeProcessStateCode;
	}

	public void setLexemeProcessStateCode(String lexemeProcessStateCode) {
		this.lexemeProcessStateCode = lexemeProcessStateCode;
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

	public String getWordAspectCode() {
		return wordAspectCode;
	}

	public void setWordAspectCode(String wordAspectCode) {
		this.wordAspectCode = wordAspectCode;
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

	public List<FreeForm> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<FreeForm> grammars) {
		this.grammars = grammars;
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
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

	public List<FreeForm> getMeaningLearnerComments() {
		return meaningLearnerComments;
	}

	public void setMeaningLearnerComments(List<FreeForm> meaningLearnerComments) {
		this.meaningLearnerComments = meaningLearnerComments;
	}

	public List<Relation> getMeaningRelations() {
		return meaningRelations;
	}

	public void setMeaningRelations(List<Relation> meaningRelations) {
		this.meaningRelations = meaningRelations;
	}

	public List<List<Relation>> getGroupedMeaningRelations() {
		return groupedMeaningRelations;
	}

	public void setGroupedMeaningRelations(List<List<Relation>> groupedMeaningRelations) {
		this.groupedMeaningRelations = groupedMeaningRelations;
	}

	public List<FreeForm> getLexemeFreeforms() {
		return lexemeFreeforms;
	}

	public void setLexemeFreeforms(List<FreeForm> lexemeFreeforms) {
		this.lexemeFreeforms = lexemeFreeforms;
	}

	public List<FreeForm> getLexemePublicNotes() {
		return lexemePublicNotes;
	}

	public void setLexemePublicNotes(List<FreeForm> lexemePublicNotes) {
		this.lexemePublicNotes = lexemePublicNotes;
	}

	public List<Relation> getLexemeRelations() {
		return lexemeRelations;
	}

	public void setLexemeRelations(List<Relation> lexemeRelations) {
		this.lexemeRelations = lexemeRelations;
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

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public boolean isLexemeOrMeaningClassifiersExist() {
		return lexemeOrMeaningClassifiersExist;
	}

	public void setLexemeOrMeaningClassifiersExist(boolean lexemeOrMeaningClassifiersExist) {
		this.lexemeOrMeaningClassifiersExist = lexemeOrMeaningClassifiersExist;
	}
	
}
