package eki.wordweb.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.Classifier;
import eki.common.data.LexemeLevel;
import eki.common.util.LocalDateTimeDeserialiser;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeFreeform;
import eki.wordweb.data.type.TypeLexemeRelation;
import eki.wordweb.data.type.TypeMeaningRelation;
import eki.wordweb.data.type.TypeMeaningWord;
import eki.wordweb.data.type.TypeMediaFile;
import eki.wordweb.data.type.TypeNote;
import eki.wordweb.data.type.TypeSourceLink;
import eki.wordweb.data.type.TypeUsage;

public class LexemeWord extends WordTypeData implements LexemeLevel, ComplexityType {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime meaningManualEventOn;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime meaningLastActivityEventOn;

	private String datasetCode;

	private String datasetName;

	private DatasetType datasetType;

	private String valueStateCode;

	private Classifier valueState;

	private String proficiencyLevelCode;

	private Integer level1;

	private Integer level2;

	private String levels;

	private Complexity complexity;

	private Integer reliability;

	private Float weight;

	private Long datasetOrderBy;

	private Long lexemeOrderBy;

	private List<String> adviceNotes;

	private List<TypeNote> lexemeNotes;

	private Map<String, List<TypeNote>> lexemeNotesByLang;

	private List<TypeNote> meaningNotes;

	private Map<String, List<TypeNote>> meaningNotesByLang;

	private List<TypeFreeform> grammars;

	private List<TypeFreeform> governments;

	private List<TypeUsage> usages;

	private boolean moreUsages;

	private List<String> registerCodes;

	private List<Classifier> registers;

	private List<String> posCodes;

	private List<Classifier> poses;

	private List<String> regionCodes;

	private List<Classifier> regions;

	private List<String> derivCodes;

	private List<Classifier> derivs;

	private List<Classifier> domains;

	private List<TypeMediaFile> meaningImages;

	private List<TypeMediaFile> mediaFiles;

	private List<String> systematicPolysemyPatterns;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<TypeDefinition> definitions;

	private Map<String, List<TypeDefinition>> definitionsByLang;

	private TypeMeaningWord correctMeaningWord;

	private TypeMeaningWord preferredTermMeaningWord;

	private List<TypeMeaningWord> meaningWords;

	private List<TypeMeaningWord> sourceLangFullSynonyms;

	private List<TypeMeaningWord> sourceLangNearSynonyms;

	private List<TypeMeaningWord> destinLangSynonyms;

	private Map<String, List<TypeMeaningWord>> destinLangSynonymsByLang;

	private List<Long> sourceLangSynonymWordIds;

	private List<TypeLexemeRelation> relatedLexemes;

	private Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType;

	private List<TypeMeaningRelation> relatedMeanings;

	private Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType;

	private List<CollocPosGroup> collocPosGroups;

	private List<DisplayColloc> limitedPrimaryDisplayCollocs;

	private List<TypeSourceLink> lexemeSourceLinks;

	private List<LexemeWord> meaningLexemes;

	private Map<String, List<LexemeWord>> meaningLexemesByLang;

	private boolean missingMatchWords;

	private boolean emptyLexeme;

	private boolean valueStatePreferred;

	private boolean valueStateWarning;

	private boolean valueStatePriority;

	private boolean showSection1;

	private boolean showSection2;

	private boolean showSection3;

	private boolean showPoses;

	private boolean showWordDataAsHidden;

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

	public LocalDateTime getMeaningManualEventOn() {
		return meaningManualEventOn;
	}

	public void setMeaningManualEventOn(LocalDateTime meaningManualEventOn) {
		this.meaningManualEventOn = meaningManualEventOn;
	}

	public LocalDateTime getMeaningLastActivityEventOn() {
		return meaningLastActivityEventOn;
	}

	public void setMeaningLastActivityEventOn(LocalDateTime meaningLastActivityEventOn) {
		this.meaningLastActivityEventOn = meaningLastActivityEventOn;
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

	public String getProficiencyLevelCode() {
		return proficiencyLevelCode;
	}

	public void setProficiencyLevelCode(String proficiencyLevelCode) {
		this.proficiencyLevelCode = proficiencyLevelCode;
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

	public List<String> getAdviceNotes() {
		return adviceNotes;
	}

	public void setAdviceNotes(List<String> adviceNotes) {
		this.adviceNotes = adviceNotes;
	}

	public List<TypeNote> getLexemeNotes() {
		return lexemeNotes;
	}

	public void setLexemeNotes(List<TypeNote> lexemeNotes) {
		this.lexemeNotes = lexemeNotes;
	}

	public Map<String, List<TypeNote>> getLexemeNotesByLang() {
		return lexemeNotesByLang;
	}

	public void setLexemeNotesByLang(Map<String, List<TypeNote>> lexemeNotesByLang) {
		this.lexemeNotesByLang = lexemeNotesByLang;
	}

	public List<TypeNote> getMeaningNotes() {
		return meaningNotes;
	}

	public void setMeaningNotes(List<TypeNote> meaningNotes) {
		this.meaningNotes = meaningNotes;
	}

	public Map<String, List<TypeNote>> getMeaningNotesByLang() {
		return meaningNotesByLang;
	}

	public void setMeaningNotesByLang(Map<String, List<TypeNote>> meaningNotesByLang) {
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

	public List<String> getRegionCodes() {
		return regionCodes;
	}

	public void setRegionCodes(List<String> regionCodes) {
		this.regionCodes = regionCodes;
	}

	public List<Classifier> getRegions() {
		return regions;
	}

	public void setRegions(List<Classifier> regions) {
		this.regions = regions;
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

	public List<TypeMediaFile> getMeaningImages() {
		return meaningImages;
	}

	public void setMeaningImages(List<TypeMediaFile> meaningImages) {
		this.meaningImages = meaningImages;
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

	public TypeMeaningWord getCorrectMeaningWord() {
		return correctMeaningWord;
	}

	public void setCorrectMeaningWord(TypeMeaningWord correctMeaningWord) {
		this.correctMeaningWord = correctMeaningWord;
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

	public List<TypeMeaningWord> getSourceLangFullSynonyms() {
		return sourceLangFullSynonyms;
	}

	public void setSourceLangFullSynonyms(List<TypeMeaningWord> sourceLangFullSynonyms) {
		this.sourceLangFullSynonyms = sourceLangFullSynonyms;
	}

	public List<TypeMeaningWord> getSourceLangNearSynonyms() {
		return sourceLangNearSynonyms;
	}

	public void setSourceLangNearSynonyms(List<TypeMeaningWord> sourceLangNearSynonyms) {
		this.sourceLangNearSynonyms = sourceLangNearSynonyms;
	}

	public List<TypeMeaningWord> getDestinLangSynonyms() {
		return destinLangSynonyms;
	}

	public void setDestinLangSynonyms(List<TypeMeaningWord> destinLangSynonyms) {
		this.destinLangSynonyms = destinLangSynonyms;
	}

	public Map<String, List<TypeMeaningWord>> getDestinLangSynonymsByLang() {
		return destinLangSynonymsByLang;
	}

	public void setDestinLangSynonymsByLang(Map<String, List<TypeMeaningWord>> destinLangSynonymsByLang) {
		this.destinLangSynonymsByLang = destinLangSynonymsByLang;
	}

	public List<Long> getSourceLangSynonymWordIds() {
		return sourceLangSynonymWordIds;
	}

	public void setSourceLangSynonymWordIds(List<Long> sourceLangSynonymWordIds) {
		this.sourceLangSynonymWordIds = sourceLangSynonymWordIds;
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

	public List<CollocPosGroup> getCollocPosGroups() {
		return collocPosGroups;
	}

	public void setCollocPosGroups(List<CollocPosGroup> collocPosGroups) {
		this.collocPosGroups = collocPosGroups;
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

	public List<LexemeWord> getMeaningLexemes() {
		return meaningLexemes;
	}

	public void setMeaningLexemes(List<LexemeWord> meaningLexemes) {
		this.meaningLexemes = meaningLexemes;
	}

	public Map<String, List<LexemeWord>> getMeaningLexemesByLang() {
		return meaningLexemesByLang;
	}

	public void setMeaningLexemesByLang(Map<String, List<LexemeWord>> meaningLexemesByLang) {
		this.meaningLexemesByLang = meaningLexemesByLang;
	}

	public boolean isMissingMatchWords() {
		return missingMatchWords;
	}

	public void setMissingMatchWords(boolean missingMatchWords) {
		this.missingMatchWords = missingMatchWords;
	}

	public boolean isEmptyLexeme() {
		return emptyLexeme;
	}

	public void setEmptyLexeme(boolean emptyLexeme) {
		this.emptyLexeme = emptyLexeme;
	}

	public boolean isValueStatePreferred() {
		return valueStatePreferred;
	}

	public void setValueStatePreferred(boolean valueStatePreferred) {
		this.valueStatePreferred = valueStatePreferred;
	}

	public boolean isValueStateWarning() {
		return valueStateWarning;
	}

	public void setValueStateWarning(boolean valueStateWarning) {
		this.valueStateWarning = valueStateWarning;
	}

	public boolean isValueStatePriority() {
		return valueStatePriority;
	}

	public void setValueStatePriority(boolean valueStatePriority) {
		this.valueStatePriority = valueStatePriority;
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

	public boolean isShowPoses() {
		return showPoses;
	}

	public void setShowPoses(boolean showPoses) {
		this.showPoses = showPoses;
	}

	public boolean isShowWordDataAsHidden() {
		return showWordDataAsHidden;
	}

	public void setShowWordDataAsHidden(boolean showWordDataAsHidden) {
		this.showWordDataAsHidden = showWordDataAsHidden;
	}

}
