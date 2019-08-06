package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Lexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Integer level1;

	private Integer level2;

	private Integer level3;

	private String levels;

	private Complexity complexity;

	private List<String> adviceNotes;

	private List<String> publicNotes;

	private List<String> grammars;

	private List<String> governments;

	private List<TypeUsage> usages;

	private boolean moreUsages;

	private List<Classifier> registers;

	private List<Classifier> poses;

	private List<Classifier> derivs;

	private List<Classifier> domains;

	private List<String> imageFiles;

	private List<String> systematicPolysemyPatterns;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<TypeDefinition> definitions;

	private List<MeaningWord> meaningWords;

	private List<MeaningWord> destinLangMatchWords;

	private List<MeaningWord> otherLangMatchWords;

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

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public List<String> getAdviceNotes() {
		return adviceNotes;
	}

	public void setAdviceNotes(List<String> adviceNotes) {
		this.adviceNotes = adviceNotes;
	}

	public List<String> getPublicNotes() {
		return publicNotes;
	}

	public void setPublicNotes(List<String> publicNotes) {
		this.publicNotes = publicNotes;
	}

	public List<String> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<String> grammars) {
		this.grammars = grammars;
	}

	public List<String> getGovernments() {
		return governments;
	}

	public void setGovernments(List<String> governments) {
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

	public List<Classifier> getRegisters() {
		return registers;
	}

	public void setRegisters(List<Classifier> registers) {
		this.registers = registers;
	}

	public List<Classifier> getPoses() {
		return poses;
	}

	public void setPoses(List<Classifier> poses) {
		this.poses = poses;
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

	public List<TypeDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TypeDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<MeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<MeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<MeaningWord> getDestinLangMatchWords() {
		return destinLangMatchWords;
	}

	public void setDestinLangMatchWords(List<MeaningWord> destinLangMatchWords) {
		this.destinLangMatchWords = destinLangMatchWords;
	}

	public List<MeaningWord> getOtherLangMatchWords() {
		return otherLangMatchWords;
	}

	public void setOtherLangMatchWords(List<MeaningWord> otherLangMatchWords) {
		this.otherLangMatchWords = otherLangMatchWords;
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
}
