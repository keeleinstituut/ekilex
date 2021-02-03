package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.common.data.LexemeLevel;

public class Lexeme extends AbstractDataObject implements LexemeLevel, ComplexityType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private Long meaningId;

	private String datasetCode;

	private String datasetName;

	private DatasetType datasetType;

	private String valueStateCode;

	private Classifier valueState;

	private Integer level1;

	private Integer level2;

	private String levels;

	private Complexity complexity;

	private Float weight;

	private Long datasetOrderBy;

	private Long lexemeOrderBy;

	private Long valueStateOrderBy;

	private List<String> adviceNotes;

	private List<TypeFreeform> lexemeNotes;

	private Map<String, List<TypeFreeform>> lexemeNotesByLang;

	private List<TypeFreeform> meaningNotes;

	private Map<String, List<TypeFreeform>> meaningNotesByLang;

	private List<TypeFreeform> grammars;

	private List<TypeFreeform> governments;

	private List<TypeUsage> usages;

	private boolean moreUsages;

	private List<String> registerCodes;

	private List<Classifier> registers;

	private List<String> posCodes;

	private List<Classifier> poses;

	private List<String> derivCodes;

	private List<Classifier> derivs;

	private List<Classifier> domains;

	private List<TypeMediaFile> imageFiles;

	private List<TypeMediaFile> mediaFiles;

	private List<String> systematicPolysemyPatterns;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<String> odLexemeRecommendations;

	private List<TypeDefinition> definitions;

	private Map<String, List<TypeDefinition>> definitionsByLang;

	private TypeMeaningWord preferredTermMeaningWord;

	private List<TypeMeaningWord> meaningWords;

	private List<Synonym> sourceLangSynonyms;

	private List<Synonym> destinLangSynonyms;

	private Map<String, List<Synonym>> destinLangSynonymsByLang;

	private List<TypeLexemeRelation> relatedLexemes;

	private Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType;

	private List<TypeMeaningRelation> relatedMeanings;

	private Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType;

	private List<CollocationPosGroup> collocationPosGroups;

	private List<DisplayColloc> limitedPrimaryDisplayCollocs;

	private List<TypeSourceLink> lexemeSourceLinks;

	private List<TypeSourceLink> lexemeFreeformSourceLinks;

	private boolean missingMatchWords;

	private boolean moreSecondaryCollocs;

	private boolean emptyLexeme;

	private boolean showSection1;

	private boolean showSection2;

	private boolean showSection3;

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

	public DatasetType getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(DatasetType datasetType) {
		this.datasetType = datasetType;
	}

	public String getValueStateCode() {
		return valueStateCode;
	}

	public void setValueStateCode(String valueStateCode) {
		this.valueStateCode = valueStateCode;
	}

	public Classifier getValueState() {
		return valueState;
	}

	public void setValueState(Classifier valueState) {
		this.valueState = valueState;
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

	@Override
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

	public Long getDatasetOrderBy() {
		return datasetOrderBy;
	}

	public void setDatasetOrderBy(Long datasetOrderBy) {
		this.datasetOrderBy = datasetOrderBy;
	}

	public Long getLexemeOrderBy() {
		return lexemeOrderBy;
	}

	public void setLexemeOrderBy(Long lexemeOrderBy) {
		this.lexemeOrderBy = lexemeOrderBy;
	}

	public Long getValueStateOrderBy() {
		return valueStateOrderBy;
	}

	public void setValueStateOrderBy(Long valueStateOrderBy) {
		this.valueStateOrderBy = valueStateOrderBy;
	}

	public List<String> getAdviceNotes() {
		return adviceNotes;
	}

	public void setAdviceNotes(List<String> adviceNotes) {
		this.adviceNotes = adviceNotes;
	}

	public List<TypeFreeform> getLexemeNotes() {
		return lexemeNotes;
	}

	public void setLexemeNotes(List<TypeFreeform> lexemeNotes) {
		this.lexemeNotes = lexemeNotes;
	}

	public Map<String, List<TypeFreeform>> getLexemeNotesByLang() {
		return lexemeNotesByLang;
	}

	public void setLexemeNotesByLang(Map<String, List<TypeFreeform>> lexemeNotesByLang) {
		this.lexemeNotesByLang = lexemeNotesByLang;
	}

	public List<TypeFreeform> getMeaningNotes() {
		return meaningNotes;
	}

	public void setMeaningNotes(List<TypeFreeform> meaningNotes) {
		this.meaningNotes = meaningNotes;
	}

	public Map<String, List<TypeFreeform>> getMeaningNotesByLang() {
		return meaningNotesByLang;
	}

	public void setMeaningNotesByLang(Map<String, List<TypeFreeform>> meaningNotesByLang) {
		this.meaningNotesByLang = meaningNotesByLang;
	}

	public List<TypeFreeform> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<TypeFreeform> grammars) {
		this.grammars = grammars;
	}

	public List<TypeFreeform> getGovernments() {
		return governments;
	}

	public void setGovernments(List<TypeFreeform> governments) {
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

	public List<TypeMediaFile> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<TypeMediaFile> imageFiles) {
		this.imageFiles = imageFiles;
	}

	public List<TypeMediaFile> getMediaFiles() {
		return mediaFiles;
	}

	public void setMediaFiles(List<TypeMediaFile> mediaFiles) {
		this.mediaFiles = mediaFiles;
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

	public Map<String, List<TypeDefinition>> getDefinitionsByLang() {
		return definitionsByLang;
	}

	public void setDefinitionsByLang(Map<String, List<TypeDefinition>> definitionsByLang) {
		this.definitionsByLang = definitionsByLang;
	}

	public TypeMeaningWord getPreferredTermMeaningWord() {
		return preferredTermMeaningWord;
	}

	public void setPreferredTermMeaningWord(TypeMeaningWord preferredTermMeaningWord) {
		this.preferredTermMeaningWord = preferredTermMeaningWord;
	}

	public List<TypeMeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TypeMeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<Synonym> getSourceLangSynonyms() {
		return sourceLangSynonyms;
	}

	public void setSourceLangSynonyms(List<Synonym> sourceLangSynonyms) {
		this.sourceLangSynonyms = sourceLangSynonyms;
	}

	public List<Synonym> getDestinLangSynonyms() {
		return destinLangSynonyms;
	}

	public void setDestinLangSynonyms(List<Synonym> destinLangSynonyms) {
		this.destinLangSynonyms = destinLangSynonyms;
	}

	public Map<String, List<Synonym>> getDestinLangSynonymsByLang() {
		return destinLangSynonymsByLang;
	}

	public void setDestinLangSynonymsByLang(Map<String, List<Synonym>> destinLangSynonymsByLang) {
		this.destinLangSynonymsByLang = destinLangSynonymsByLang;
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

	public List<TypeSourceLink> getLexemeSourceLinks() {
		return lexemeSourceLinks;
	}

	public void setLexemeSourceLinks(List<TypeSourceLink> lexemeSourceLinks) {
		this.lexemeSourceLinks = lexemeSourceLinks;
	}

	public List<TypeSourceLink> getLexemeFreeformSourceLinks() {
		return lexemeFreeformSourceLinks;
	}

	public void setLexemeFreeformSourceLinks(List<TypeSourceLink> lexemeFreeformSourceLinks) {
		this.lexemeFreeformSourceLinks = lexemeFreeformSourceLinks;
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

	public boolean isShowSection1() {
		return showSection1;
	}

	public void setShowSection1(boolean showSection1) {
		this.showSection1 = showSection1;
	}

	public boolean isShowSection2() {
		return showSection2;
	}

	public void setShowSection2(boolean showSection2) {
		this.showSection2 = showSection2;
	}

	public boolean isShowSection3() {
		return showSection3;
	}

	public void setShowSection3(boolean showSection3) {
		this.showSection3 = showSection3;
	}

}
