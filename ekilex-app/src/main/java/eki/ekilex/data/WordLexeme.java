package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.LexemeLevel;

public class WordLexeme extends AbstractCrudEntity implements LexemeLevel, DecoratedWordType, LexemeTag {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private String wordLang;

	private Integer wordHomonymNr;

	private String wordGenderCode;

	private String wordAspectCode;

	private String wordDisplayMorphCode;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private Long lexemeId;

	private Long meaningId;

	private String datasetName;

	private String datasetCode;

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

	private Float weight;

	private List<Classifier> wordTypes;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Government> governments;

	private List<FreeForm> grammars;

	private List<Usage> usages;

	private List<FreeForm> lexemeFreeforms;

	private List<NoteLangGroup> lexemeNoteLangGroups;

	private List<LexemeRelation> lexemeRelations;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<Collocation> secondaryCollocations;

	private List<SourceLink> sourceLinks;

	private Meaning meaning;

	private List<MeaningWord> meaningWords;

	private List<SynonymLangGroup> synonymLangGroups;

	private boolean lexemeOrMeaningClassifiersExist;

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
	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	@Override
	public String getWordValuePrese() {
		return wordValuePrese;
	}

	@Override
	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public Integer getWordHomonymNr() {
		return wordHomonymNr;
	}

	public void setWordHomonymNr(Integer wordHomonymNr) {
		this.wordHomonymNr = wordHomonymNr;
	}

	public String getWordGenderCode() {
		return wordGenderCode;
	}

	public void setWordGenderCode(String wordGenderCode) {
		this.wordGenderCode = wordGenderCode;
	}

	public String getWordAspectCode() {
		return wordAspectCode;
	}

	public void setWordAspectCode(String wordAspectCode) {
		this.wordAspectCode = wordAspectCode;
	}

	public String getWordDisplayMorphCode() {
		return wordDisplayMorphCode;
	}

	public void setWordDisplayMorphCode(String wordDisplayMorphCode) {
		this.wordDisplayMorphCode = wordDisplayMorphCode;
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

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	@Override
	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
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

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
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

	public List<FreeForm> getLexemeFreeforms() {
		return lexemeFreeforms;
	}

	public void setLexemeFreeforms(List<FreeForm> lexemeFreeforms) {
		this.lexemeFreeforms = lexemeFreeforms;
	}

	public List<NoteLangGroup> getLexemeNoteLangGroups() {
		return lexemeNoteLangGroups;
	}

	public void setLexemeNoteLangGroups(List<NoteLangGroup> lexemeNoteLangGroups) {
		this.lexemeNoteLangGroups = lexemeNoteLangGroups;
	}

	public List<LexemeRelation> getLexemeRelations() {
		return lexemeRelations;
	}

	public void setLexemeRelations(List<LexemeRelation> lexemeRelations) {
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

	public Meaning getMeaning() {
		return meaning;
	}

	public void setMeaning(Meaning meaning) {
		this.meaning = meaning;
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

	public boolean isLexemeOrMeaningClassifiersExist() {
		return lexemeOrMeaningClassifiersExist;
	}

	public void setLexemeOrMeaningClassifiersExist(boolean lexemeOrMeaningClassifiersExist) {
		this.lexemeOrMeaningClassifiersExist = lexemeOrMeaningClassifiersExist;
	}

}
