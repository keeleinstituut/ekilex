package eki.wordweb.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.constant.DatasetType;
import eki.common.data.Classifier;
import eki.common.data.LexemeLevel;
import eki.common.util.LocalDateTimeDeserialiser;

public class LexemeWord extends WordTypeData implements LexemeLevel {

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

	private Integer reliability;

	private Float weight;

	private Long datasetOrderBy;

	private Long lexemeOrderBy;

	private List<Note> lexemeNotes;

	private Map<String, List<Note>> lexemeNotesByLang;

	private List<Note> meaningNotes;

	private Map<String, List<Note>> meaningNotesByLang;

	private List<Grammar> grammars;

	private List<Government> governments;

	private List<Usage> usages;

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

	private List<MeaningImage> meaningImages;

	private List<MeaningMedia> meaningMedias;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<Definition> definitions;

	private Map<String, List<Definition>> definitionsByLang;

	private MeaningWord correctMeaningWord;

	private MeaningWord preferredTermMeaningWord;

	private List<MeaningWord> meaningWords;

	private List<MeaningWord> sourceLangFullSynonyms;

	private List<MeaningWord> sourceLangNearSynonyms;

	private List<MeaningWord> destinLangSynonyms;

	private Map<String, List<MeaningWord>> destinLangSynonymsByLang;

	private List<Long> sourceLangSynonymWordIds;

	private List<LexemeRelation> relatedLexemes;

	private Map<Classifier, List<LexemeRelation>> relatedLexemesByType;

	private List<MeaningRelation> relatedMeanings;

	private Map<Classifier, List<MeaningRelation>> relatedMeaningsByType;

	private List<CollocPosGroup> collocPosGroups;

	private List<DisplayColloc> limitedPrimaryDisplayCollocs;

	private List<SourceLink> lexemeSourceLinks;

	private List<LexemeWord> meaningLexemes;

	private Map<String, List<LexemeWord>> meaningLexemesByLang;

	private boolean missingMatchWords;

	private boolean collocExists;

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

	public List<Note> getLexemeNotes() {
		return lexemeNotes;
	}

	public void setLexemeNotes(List<Note> lexemeNotes) {
		this.lexemeNotes = lexemeNotes;
	}

	public Map<String, List<Note>> getLexemeNotesByLang() {
		return lexemeNotesByLang;
	}

	public void setLexemeNotesByLang(Map<String, List<Note>> lexemeNotesByLang) {
		this.lexemeNotesByLang = lexemeNotesByLang;
	}

	public List<Note> getMeaningNotes() {
		return meaningNotes;
	}

	public void setMeaningNotes(List<Note> meaningNotes) {
		this.meaningNotes = meaningNotes;
	}

	public Map<String, List<Note>> getMeaningNotesByLang() {
		return meaningNotesByLang;
	}

	public void setMeaningNotesByLang(Map<String, List<Note>> meaningNotesByLang) {
		this.meaningNotesByLang = meaningNotesByLang;
	}

	public List<Grammar> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<Grammar> grammars) {
		this.grammars = grammars;
	}

	public List<Government> getGovernments() {
		return governments;
	}

	public void setGovernments(List<Government> governments) {
		this.governments = governments;
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
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

	public List<MeaningImage> getMeaningImages() {
		return meaningImages;
	}

	public void setMeaningImages(List<MeaningImage> meaningImages) {
		this.meaningImages = meaningImages;
	}

	public List<MeaningMedia> getMeaningMedias() {
		return meaningMedias;
	}

	public void setMeaningMedias(List<MeaningMedia> meaningMedias) {
		this.meaningMedias = meaningMedias;
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

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public Map<String, List<Definition>> getDefinitionsByLang() {
		return definitionsByLang;
	}

	public void setDefinitionsByLang(Map<String, List<Definition>> definitionsByLang) {
		this.definitionsByLang = definitionsByLang;
	}

	public MeaningWord getCorrectMeaningWord() {
		return correctMeaningWord;
	}

	public void setCorrectMeaningWord(MeaningWord correctMeaningWord) {
		this.correctMeaningWord = correctMeaningWord;
	}

	public MeaningWord getPreferredTermMeaningWord() {
		return preferredTermMeaningWord;
	}

	public void setPreferredTermMeaningWord(MeaningWord preferredTermMeaningWord) {
		this.preferredTermMeaningWord = preferredTermMeaningWord;
	}

	public List<MeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<MeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<MeaningWord> getSourceLangFullSynonyms() {
		return sourceLangFullSynonyms;
	}

	public void setSourceLangFullSynonyms(List<MeaningWord> sourceLangFullSynonyms) {
		this.sourceLangFullSynonyms = sourceLangFullSynonyms;
	}

	public List<MeaningWord> getSourceLangNearSynonyms() {
		return sourceLangNearSynonyms;
	}

	public void setSourceLangNearSynonyms(List<MeaningWord> sourceLangNearSynonyms) {
		this.sourceLangNearSynonyms = sourceLangNearSynonyms;
	}

	public List<MeaningWord> getDestinLangSynonyms() {
		return destinLangSynonyms;
	}

	public void setDestinLangSynonyms(List<MeaningWord> destinLangSynonyms) {
		this.destinLangSynonyms = destinLangSynonyms;
	}

	public Map<String, List<MeaningWord>> getDestinLangSynonymsByLang() {
		return destinLangSynonymsByLang;
	}

	public void setDestinLangSynonymsByLang(Map<String, List<MeaningWord>> destinLangSynonymsByLang) {
		this.destinLangSynonymsByLang = destinLangSynonymsByLang;
	}

	public List<Long> getSourceLangSynonymWordIds() {
		return sourceLangSynonymWordIds;
	}

	public void setSourceLangSynonymWordIds(List<Long> sourceLangSynonymWordIds) {
		this.sourceLangSynonymWordIds = sourceLangSynonymWordIds;
	}

	public List<LexemeRelation> getRelatedLexemes() {
		return relatedLexemes;
	}

	public void setRelatedLexemes(List<LexemeRelation> relatedLexemes) {
		this.relatedLexemes = relatedLexemes;
	}

	public Map<Classifier, List<LexemeRelation>> getRelatedLexemesByType() {
		return relatedLexemesByType;
	}

	public void setRelatedLexemesByType(Map<Classifier, List<LexemeRelation>> relatedLexemesByType) {
		this.relatedLexemesByType = relatedLexemesByType;
	}

	public List<MeaningRelation> getRelatedMeanings() {
		return relatedMeanings;
	}

	public void setRelatedMeanings(List<MeaningRelation> relatedMeanings) {
		this.relatedMeanings = relatedMeanings;
	}

	public Map<Classifier, List<MeaningRelation>> getRelatedMeaningsByType() {
		return relatedMeaningsByType;
	}

	public void setRelatedMeaningsByType(Map<Classifier, List<MeaningRelation>> relatedMeaningsByType) {
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

	public List<SourceLink> getLexemeSourceLinks() {
		return lexemeSourceLinks;
	}

	public void setLexemeSourceLinks(List<SourceLink> lexemeSourceLinks) {
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

	public boolean isCollocExists() {
		return collocExists;
	}

	public void setCollocExists(boolean collocExists) {
		this.collocExists = collocExists;
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
