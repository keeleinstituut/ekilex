package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.common.data.LexemeLevel;

public class Lexeme extends AbstractDataObject implements LexemeLevel {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private String datasetName;

	private DatasetType datasetType;

	private Integer level1;

	private Integer level2;

	private String levels;

	private Complexity complexity;

	private Float weight;

	private List<String> adviceNotes;

	private List<TypePublicNote> publicNotes;

	private List<TypeGrammar> grammars;

	private List<TypeGovernment> governments;

	private List<TypeUsage> usages;

	private boolean moreUsages;

	private List<String> registerCodes;

	private List<Classifier> registers;

	private List<String> posCodes;

	private List<Classifier> poses;

	private List<String> derivCodes;

	private List<Classifier> derivs;

	private List<Classifier> domains;

	private List<String> imageFiles;

	private List<String> systematicPolysemyPatterns;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<String> odLexemeRecommendations;

	private List<TypeDefinition> definitions;

	private List<TypeMeaningWord> meaningWords;

	private List<TypeMeaningWord> sourceLangMeaningWords;

	private List<TypeMeaningWord> destinLangMatchWords;

	private List<TypeLexemeRelation> relatedLexemes;

	private Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType;

	private List<TypeMeaningRelation> relatedMeanings;

	private Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<DisplayColloc> limitedPrimaryDisplayCollocs;

	private boolean missingMatchWords;

	private boolean moreSecondaryCollocs;

	private boolean emptyLexeme;

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

	public DatasetType getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(DatasetType datasetType) {
		this.datasetType = datasetType;
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

	public List<String> getAdviceNotes() {
		return adviceNotes;
	}

	public void setAdviceNotes(List<String> adviceNotes) {
		this.adviceNotes = adviceNotes;
	}

	public List<TypePublicNote> getPublicNotes() {
		return publicNotes;
	}

	public void setPublicNotes(List<TypePublicNote> publicNotes) {
		this.publicNotes = publicNotes;
	}

	public List<TypeGrammar> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<TypeGrammar> grammars) {
		this.grammars = grammars;
	}

	public List<TypeGovernment> getGovernments() {
		return governments;
	}

	public void setGovernments(List<TypeGovernment> governments) {
		this.governments = governments;
	}

	public List<TypeUsage> getUsages() {
		return usages;
	}

	public void setUsages(List<TypeUsage> usages) {
		this.usages = usages;
	}

	public boolean isMoreUsages() {
		return moreUsages;
	}

	public void setMoreUsages(boolean moreUsages) {
		this.moreUsages = moreUsages;
	}

	public List<String> getRegisterCodes() {
		return registerCodes;
	}

	public void setRegisterCodes(List<String> registerCodes) {
		this.registerCodes = registerCodes;
	}

	public List<Classifier> getRegisters() {
		return registers;
	}

	public void setRegisters(List<Classifier> registers) {
		this.registers = registers;
	}

	public List<String> getPosCodes() {
		return posCodes;
	}

	public void setPosCodes(List<String> posCodes) {
		this.posCodes = posCodes;
	}

	public List<Classifier> getPoses() {
		return poses;
	}

	public void setPoses(List<Classifier> poses) {
		this.poses = poses;
	}

	public List<String> getDerivCodes() {
		return derivCodes;
	}

	public void setDerivCodes(List<String> derivCodes) {
		this.derivCodes = derivCodes;
	}

	public List<Classifier> getDerivs() {
		return derivs;
	}

	public void setDerivs(List<Classifier> derivs) {
		this.derivs = derivs;
	}

	public List<Classifier> getDomains() {
		return domains;
	}

	public void setDomains(List<Classifier> domains) {
		this.domains = domains;
	}

	public List<String> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<String> imageFiles) {
		this.imageFiles = imageFiles;
	}

	public List<String> getSystematicPolysemyPatterns() {
		return systematicPolysemyPatterns;
	}

	public void setSystematicPolysemyPatterns(List<String> systematicPolysemyPatterns) {
		this.systematicPolysemyPatterns = systematicPolysemyPatterns;
	}

	public List<String> getSemanticTypes() {
		return semanticTypes;
	}

	public void setSemanticTypes(List<String> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}

	public List<String> getLearnerComments() {
		return learnerComments;
	}

	public void setLearnerComments(List<String> learnerComments) {
		this.learnerComments = learnerComments;
	}

	public List<String> getOdLexemeRecommendations() {
		return odLexemeRecommendations;
	}

	public void setOdLexemeRecommendations(List<String> odLexemeRecommendations) {
		this.odLexemeRecommendations = odLexemeRecommendations;
	}

	public List<TypeDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TypeDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<TypeMeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TypeMeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<TypeMeaningWord> getSourceLangMeaningWords() {
		return sourceLangMeaningWords;
	}

	public void setSourceLangMeaningWords(List<TypeMeaningWord> sourceLangMeaningWords) {
		this.sourceLangMeaningWords = sourceLangMeaningWords;
	}

	public List<TypeMeaningWord> getDestinLangMatchWords() {
		return destinLangMatchWords;
	}

	public void setDestinLangMatchWords(List<TypeMeaningWord> destinLangMatchWords) {
		this.destinLangMatchWords = destinLangMatchWords;
	}

	public List<TypeLexemeRelation> getRelatedLexemes() {
		return relatedLexemes;
	}

	public void setRelatedLexemes(List<TypeLexemeRelation> relatedLexemes) {
		this.relatedLexemes = relatedLexemes;
	}

	public Map<Classifier, List<TypeLexemeRelation>> getRelatedLexemesByType() {
		return relatedLexemesByType;
	}

	public void setRelatedLexemesByType(Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType) {
		this.relatedLexemesByType = relatedLexemesByType;
	}

	public List<TypeMeaningRelation> getRelatedMeanings() {
		return relatedMeanings;
	}

	public void setRelatedMeanings(List<TypeMeaningRelation> relatedMeanings) {
		this.relatedMeanings = relatedMeanings;
	}

	public Map<Classifier, List<TypeMeaningRelation>> getRelatedMeaningsByType() {
		return relatedMeaningsByType;
	}

	public void setRelatedMeaningsByType(Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType) {
		this.relatedMeaningsByType = relatedMeaningsByType;
	}

	public List<CollocationPosGroup> getCollocationPosGroups() {
		return collocationPosGroups;
	}

	public void setCollocationPosGroups(List<CollocationPosGroup> collocationPosGroups) {
		this.collocationPosGroups = collocationPosGroups;
	}

	public List<DisplayColloc> getLimitedPrimaryDisplayCollocs() {
		return limitedPrimaryDisplayCollocs;
	}

	public void setLimitedPrimaryDisplayCollocs(List<DisplayColloc> limitedPrimaryDisplayCollocs) {
		this.limitedPrimaryDisplayCollocs = limitedPrimaryDisplayCollocs;
	}

	public boolean isMissingMatchWords() {
		return missingMatchWords;
	}

	public void setMissingMatchWords(boolean missingMatchWords) {
		this.missingMatchWords = missingMatchWords;
	}

	public boolean isMoreSecondaryCollocs() {
		return moreSecondaryCollocs;
	}

	public void setMoreSecondaryCollocs(boolean moreSecondaryCollocs) {
		this.moreSecondaryCollocs = moreSecondaryCollocs;
	}

	public boolean isEmptyLexeme() {
		return emptyLexeme;
	}

	public void setEmptyLexeme(boolean emptyLexeme) {
		this.emptyLexeme = emptyLexeme;
	}

	@Override
	public String getDatasetCode() {
		return null;
	}
}
