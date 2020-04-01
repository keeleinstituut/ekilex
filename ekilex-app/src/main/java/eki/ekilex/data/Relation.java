package eki.ekilex.data;

import java.util.List;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class Relation extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long groupId;

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

	private List<String> wordLexemeDatasetCodes;

	private String relTypeCode;

	private String relTypeLabel;

	private Long orderBy;

	private RelationStatus relationStatus;

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

	public List<String> getWordLexemeDatasetCodes() {
		return wordLexemeDatasetCodes;
	}

	public void setWordLexemeDatasetCodes(List<String> wordLexemeDatasetCodes) {
		this.wordLexemeDatasetCodes = wordLexemeDatasetCodes;
	}

	public String getRelTypeCode() {
		return relTypeCode;
	}

	public void setRelTypeCode(String relTypeLabel) {
		this.relTypeCode = relTypeLabel;
	}

	public String getRelTypeLabel() {
		return relTypeLabel;
	}

	public void setRelTypeLabel(String relTypeLabel) {
		this.relTypeLabel = relTypeLabel;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public RelationStatus getRelationStatus() {
		return relationStatus;
	}

	public void setRelationStatus(RelationStatus relationStatus) {
		this.relationStatus = relationStatus;
	}

}
