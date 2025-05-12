package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.LexemeLevel;

public class Lexeme extends AbstractGrantEntity implements LexemeLevel {

	private static final long serialVersionUID = 1L;

	private Word lexemeWord;

	private Meaning meaning;

	private Long lexemeId;

	private Long wordId;

	private Long meaningId;

	private String datasetCode;

	private String datasetName;

	private Integer level1;

	private Integer level2;

	private String levels;

	private String lexemeValueStateCode;

	private Classifier lexemeValueState;

	private String lexemeProficiencyLevelCode;

	private Classifier lexemeProficiencyLevel;

	private Integer reliability;

	private Float weight;

	private Long orderBy;

	private Complexity complexity;

	private boolean isWord;

	private boolean isCollocation;

	private List<String> tags;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Classifier> regions;

	private List<Government> governments;

	private List<Freeform> grammars;

	private List<Usage> usages;

	private List<Freeform> freeforms;

	private List<LexemeNote> notes;

	private List<NoteLangGroup> noteLangGroups;

	private List<LexemeRelation> lexemeRelations;

	private List<CollocPosGroup> primaryCollocations;

	private List<Colloc> secondaryCollocations;

	private List<CollocMember> collocationMembers;

	private List<SourceLink> sourceLinks;

	private List<MeaningWord> meaningWords;

	private List<SynonymLangGroup> synonymLangGroups;

	private List<Media> images;

	private boolean collocationsExist;

	private boolean lexemeOrMeaningClassifiersExist;

	private boolean classifiersExist;

	public Word getLexemeWord() {
		return lexemeWord;
	}

	public void setLexemeWord(Word lexemeWord) {
		this.lexemeWord = lexemeWord;
	}

	public Meaning getMeaning() {
		return meaning;
	}

	public void setMeaning(Meaning meaning) {
		this.meaning = meaning;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	@Override
	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	@Override
	public Integer getLevel1() {
		return level1;
	}

	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}

	@Override
	public Integer getLevel2() {
		return level2;
	}

	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}

	@Override
	public String getLevels() {
		return levels;
	}

	@Override
	public void setLevels(String levels) {
		this.levels = levels;
	}

	public String getLexemeValueStateCode() {
		return lexemeValueStateCode;
	}

	public void setLexemeValueStateCode(String lexemeValueStateCode) {
		this.lexemeValueStateCode = lexemeValueStateCode;
	}

	public Classifier getLexemeValueState() {
		return lexemeValueState;
	}

	public void setLexemeValueState(Classifier lexemeValueState) {
		this.lexemeValueState = lexemeValueState;
	}

	public String getLexemeProficiencyLevelCode() {
		return lexemeProficiencyLevelCode;
	}

	public void setLexemeProficiencyLevelCode(String lexemeProficiencyLevelCode) {
		this.lexemeProficiencyLevelCode = lexemeProficiencyLevelCode;
	}

	public Classifier getLexemeProficiencyLevel() {
		return lexemeProficiencyLevel;
	}

	public void setLexemeProficiencyLevel(Classifier lexemeProficiencyLevel) {
		this.lexemeProficiencyLevel = lexemeProficiencyLevel;
	}

	public Integer getReliability() {
		return reliability;
	}

	public void setReliability(Integer reliability) {
		this.reliability = reliability;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public boolean isWord() {
		return isWord;
	}

	public void setWord(boolean isWord) {
		this.isWord = isWord;
	}

	public boolean isCollocation() {
		return isCollocation;
	}

	public void setCollocation(boolean isCollocation) {
		this.isCollocation = isCollocation;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
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

	public List<Government> getGovernments() {
		return governments;
	}

	public void setGovernments(List<Government> governments) {
		this.governments = governments;
	}

	public List<Freeform> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<Freeform> grammars) {
		this.grammars = grammars;
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

	public List<Freeform> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<Freeform> freeforms) {
		this.freeforms = freeforms;
	}

	public List<LexemeNote> getNotes() {
		return notes;
	}

	public void setNotes(List<LexemeNote> notes) {
		this.notes = notes;
	}

	public List<NoteLangGroup> getNoteLangGroups() {
		return noteLangGroups;
	}

	public void setNoteLangGroups(List<NoteLangGroup> noteLangGroups) {
		this.noteLangGroups = noteLangGroups;
	}

	public List<LexemeRelation> getLexemeRelations() {
		return lexemeRelations;
	}

	public void setLexemeRelations(List<LexemeRelation> lexemeRelations) {
		this.lexemeRelations = lexemeRelations;
	}

	public List<CollocPosGroup> getPrimaryCollocations() {
		return primaryCollocations;
	}

	public void setPrimaryCollocations(List<CollocPosGroup> primaryCollocations) {
		this.primaryCollocations = primaryCollocations;
	}

	public List<Colloc> getSecondaryCollocations() {
		return secondaryCollocations;
	}

	public void setSecondaryCollocations(List<Colloc> secondaryCollocations) {
		this.secondaryCollocations = secondaryCollocations;
	}

	public List<CollocMember> getCollocationMembers() {
		return collocationMembers;
	}

	public void setCollocationMembers(List<CollocMember> collocationMembers) {
		this.collocationMembers = collocationMembers;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public List<MeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<MeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<SynonymLangGroup> getSynonymLangGroups() {
		return synonymLangGroups;
	}

	public void setSynonymLangGroups(List<SynonymLangGroup> synonymLangGroups) {
		this.synonymLangGroups = synonymLangGroups;
	}

	public List<Media> getImages() {
		return images;
	}

	public void setImages(List<Media> images) {
		this.images = images;
	}

	public boolean isCollocationsExist() {
		return collocationsExist;
	}

	public void setCollocationsExist(boolean collocationsExist) {
		this.collocationsExist = collocationsExist;
	}

	public boolean isLexemeOrMeaningClassifiersExist() {
		return lexemeOrMeaningClassifiersExist;
	}

	public void setLexemeOrMeaningClassifiersExist(boolean lexemeOrMeaningClassifiersExist) {
		this.lexemeOrMeaningClassifiersExist = lexemeOrMeaningClassifiersExist;
	}

	public boolean isClassifiersExist() {
		return classifiersExist;
	}

	public void setClassifiersExist(boolean classifiersExist) {
		this.classifiersExist = classifiersExist;
	}

}
