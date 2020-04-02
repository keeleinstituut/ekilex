package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.common.data.LexemeLevel;

public class WordLexeme extends AbstractDataObject implements LexemeLevel, DecoratedWordType {

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

	private String lexemeProcessStateCode;

	private String lexemeFrequencyGroupCode;

	private List<String> lexemeFrequencies;

	private Complexity complexity;

	private Float weight;

	private List<Classifier> wordTypes;

	private List<Classifier> pos;

	private List<Classifier> derivs;

	private List<Classifier> registers;

	private List<Classifier> meaningSemanticTypes;

	private List<OrderedClassifier> meaningDomains;

	private List<MeaningWordLangGroup> meaningWordLangGroups;

	private List<Government> governments;

	private List<FreeForm> grammars;

	private List<Usage> usages;

	private List<Definition> definitions;

	private List<Image> meaningImages;

	private List<FreeForm> meaningFreeforms;

	private List<FreeForm> meaningLearnerComments;

	private List<Relation> meaningRelations;

	private List<List<Relation>> viewMeaningRelations;

	private List<FreeForm> lexemeFreeforms;

	private List<FreeForm> lexemePublicNotes;

	private List<Relation> lexemeRelations;

	private List<FreeForm> odLexemeRecommendations;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<Collocation> secondaryCollocations;

	private List<SourceLink> sourceLinks;

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

	public String getLexemeProcessStateCode() {
		return lexemeProcessStateCode;
	}

	public void setLexemeProcessStateCode(String lexemeProcessStateCode) {
		this.lexemeProcessStateCode = lexemeProcessStateCode;
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

	public List<Classifier> getMeaningSemanticTypes() {
		return meaningSemanticTypes;
	}

	public void setMeaningSemanticTypes(List<Classifier> meaningSemanticTypes) {
		this.meaningSemanticTypes = meaningSemanticTypes;
	}

	public List<OrderedClassifier> getMeaningDomains() {
		return meaningDomains;
	}

	public void setMeaningDomains(List<OrderedClassifier> meaningDomains) {
		this.meaningDomains = meaningDomains;
	}

	public List<MeaningWordLangGroup> getMeaningWordLangGroups() {
		return meaningWordLangGroups;
	}

	public void setMeaningWordLangGroups(List<MeaningWordLangGroup> meaningWordLangGroups) {
		this.meaningWordLangGroups = meaningWordLangGroups;
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

	public List<Image> getMeaningImages() {
		return meaningImages;
	}

	public void setMeaningImages(List<Image> meaningImages) {
		this.meaningImages = meaningImages;
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

	public List<List<Relation>> getViewMeaningRelations() {
		return viewMeaningRelations;
	}

	public void setViewMeaningRelations(List<List<Relation>> viewMeaningRelations) {
		this.viewMeaningRelations = viewMeaningRelations;
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

	public List<FreeForm> getOdLexemeRecommendations() {
		return odLexemeRecommendations;
	}

	public void setOdLexemeRecommendations(List<FreeForm> odLexemeRecommendations) {
		this.odLexemeRecommendations = odLexemeRecommendations;
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
