package eki.ekilex.data;

import java.util.List;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class Relation extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long groupId;

	private String groupWordRelTypeCode;

	private Long lexemeId;

	private List<String> lexemeValueStateCodes;

	private List<String> lexemeRegisterCodes;

	private List<String> lexemeGovernmentValues;

	private Long meaningId;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private String wordLang;

	private String wordAspectCode;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private Integer wordHomonymNr;

	private boolean homonymsExist;

	private List<String> wordDefinitions;

	private List<String> wordLexemesPoses;

	private List<String> datasetCodes;

	private String relTypeCode;

	private String relTypeLabel;

	private RelationStatus relationStatus;

	private RelationStatus oppositeRelationStatus;

	private List<TypeWordRelParam> relationParams;

	private Float weight;

	private Long orderBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupWordRelTypeCode() {
		return groupWordRelTypeCode;
	}

	public void setGroupWordRelTypeCode(String groupWordRelTypeCode) {
		this.groupWordRelTypeCode = groupWordRelTypeCode;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public List<String> getLexemeValueStateCodes() {
		return lexemeValueStateCodes;
	}

	public void setLexemeValueStateCodes(List<String> lexemeValueStateCodes) {
		this.lexemeValueStateCodes = lexemeValueStateCodes;
	}

	public List<String> getLexemeRegisterCodes() {
		return lexemeRegisterCodes;
	}

	public void setLexemeRegisterCodes(List<String> lexemeRegisterCodes) {
		this.lexemeRegisterCodes = lexemeRegisterCodes;
	}

	public List<String> getLexemeGovernmentValues() {
		return lexemeGovernmentValues;
	}

	public void setLexemeGovernmentValues(List<String> lexemeGovernmentValues) {
		this.lexemeGovernmentValues = lexemeGovernmentValues;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

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

	public String getWordAspectCode() {
		return wordAspectCode;
	}

	public void setWordAspectCode(String wordAspectCode) {
		this.wordAspectCode = wordAspectCode;
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

	public Integer getWordHomonymNr() {
		return wordHomonymNr;
	}

	public void setWordHomonymNr(Integer wordHomonymNr) {
		this.wordHomonymNr = wordHomonymNr;
	}

	public boolean isHomonymsExist() {
		return homonymsExist;
	}

	public void setHomonymsExist(boolean homonymsExist) {
		this.homonymsExist = homonymsExist;
	}

	public List<String> getWordDefinitions() {
		return wordDefinitions;
	}

	public void setWordDefinitions(List<String> wordDefinitions) {
		this.wordDefinitions = wordDefinitions;
	}

	public List<String> getWordLexemesPoses() {
		return wordLexemesPoses;
	}

	public void setWordLexemesPoses(List<String> wordLexemesPoses) {
		this.wordLexemesPoses = wordLexemesPoses;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public String getRelTypeCode() {
		return relTypeCode;
	}

	public void setRelTypeCode(String relTypeCode) {
		this.relTypeCode = relTypeCode;
	}

	public String getRelTypeLabel() {
		return relTypeLabel;
	}

	public void setRelTypeLabel(String relTypeLabel) {
		this.relTypeLabel = relTypeLabel;
	}

	public RelationStatus getRelationStatus() {
		return relationStatus;
	}

	public void setRelationStatus(RelationStatus relationStatus) {
		this.relationStatus = relationStatus;
	}

	public RelationStatus getOppositeRelationStatus() {
		return oppositeRelationStatus;
	}

	public void setOppositeRelationStatus(RelationStatus oppositeRelationStatus) {
		this.oppositeRelationStatus = oppositeRelationStatus;
	}

	public List<TypeWordRelParam> getRelationParams() {
		return relationParams;
	}

	public void setRelationParams(List<TypeWordRelParam> relationParams) {
		this.relationParams = relationParams;
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
}
