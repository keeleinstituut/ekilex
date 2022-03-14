package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;

public class Lexeme extends AbstractCrudEntity implements LexemeTag {

	private static final long serialVersionUID = 1L;

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

	private List<String> tags;

	private boolean isPublic;

	private Complexity complexity;

	private Long orderBy;

	private Word word;

	private List<Classifier> wordTypes;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Classifier> regions;

	private List<Usage> usages;

	private List<Media> images;

	private List<FreeForm> freeforms;

	private List<NoteLangGroup> noteLangGroups;

	private List<FreeForm> grammars;

	private List<SourceLink> sourceLinks;

	private List<LexemeRelation> lexemeRelations;

	private boolean classifiersExist;

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

	@Override
	public List<String> getTags() {
		return tags;
	}

	@Override
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
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

	public List<Media> getImages() {
		return images;
	}

	public void setImages(List<Media> images) {
		this.images = images;
	}

	public List<FreeForm> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<FreeForm> freeforms) {
		this.freeforms = freeforms;
	}

	public List<NoteLangGroup> getNoteLangGroups() {
		return noteLangGroups;
	}

	public void setNoteLangGroups(List<NoteLangGroup> noteLangGroups) {
		this.noteLangGroups = noteLangGroups;
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

	public List<LexemeRelation> getLexemeRelations() {
		return lexemeRelations;
	}

	public void setLexemeRelations(List<LexemeRelation> lexemeRelations) {
		this.lexemeRelations = lexemeRelations;
	}

	public boolean isClassifiersExist() {
		return classifiersExist;
	}

	public void setClassifiersExist(boolean classifiersExist) {
		this.classifiersExist = classifiersExist;
	}

}
