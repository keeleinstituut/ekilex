package eki.wordweb.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Lexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private String datasetCode;

	private String datasetName;

	private Integer level1;

	private Integer level2;

	private Integer level3;

	private String levels;

	private List<String> adviceNotes;

	private List<String> publicNotes;

	private List<String> grammars;

	private List<String> governments;

	private List<TypeUsage> usages;

	private List<Classifier> registers;

	private List<Classifier> poses;

	private List<Classifier> derivs;

	private List<Classifier> domains;

	private List<String> imageFiles;

	private List<String> systematicPolysemyPatterns;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<TypeDefinition> definitions;

	private List<Word> synonymWords;

	private List<Word> destinLangMatchWords;

	private List<Word> otherLangMatchWords;

	private List<TypeLexemeRelation> relatedLexemes;

	private List<TypeMeaningRelation> relatedMeanings;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<Collocation> secondaryCollocations;

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

	public List<Word> getSynonymWords() {
		return synonymWords;
	}

	public void setSynonymWords(List<Word> synonymWords) {
		this.synonymWords = synonymWords;
	}

	public List<Word> getDestinLangMatchWords() {
		return destinLangMatchWords;
	}

	public void setDestinLangMatchWords(List<Word> destinLangMatchWords) {
		this.destinLangMatchWords = destinLangMatchWords;
	}

	public List<Word> getOtherLangMatchWords() {
		return otherLangMatchWords;
	}

	public void setOtherLangMatchWords(List<Word> otherLangMatchWords) {
		this.otherLangMatchWords = otherLangMatchWords;
	}

	public List<TypeLexemeRelation> getRelatedLexemes() {
		return relatedLexemes;
	}

	public void setRelatedLexemes(List<TypeLexemeRelation> relatedLexemes) {
		this.relatedLexemes = relatedLexemes;
	}

	public List<TypeMeaningRelation> getRelatedMeanings() {
		return relatedMeanings;
	}

	public void setRelatedMeanings(List<TypeMeaningRelation> relatedMeanings) {
		this.relatedMeanings = relatedMeanings;
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

	public Map<Classifier, List<TypeLexemeRelation>> getRelatedLexemesByType() {
		return relatedLexemes == null ?
				Collections.emptyMap() :
				relatedLexemes.stream().collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
	}

	public Map<Classifier, List<TypeMeaningRelation>> getRelatedMeaningsByType() {
		return relatedMeanings == null ?
				Collections.emptyMap() :
				relatedMeanings.stream().collect(Collectors.groupingBy(TypeMeaningRelation::getMeaningRelType));
	}

}
