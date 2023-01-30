package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class MeaningWord extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private Integer homonymNr;

	private boolean homonymsExist;

	private String lang;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private Long lexemeId;

	private Float lexemeWeight;

	private Complexity lexemeComplexity;

	private List<String> lexRegisterCodes;

	private Long orderBy;

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

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public boolean isHomonymsExist() {
		return homonymsExist;
	}

	public void setHomonymsExist(boolean homonymsExist) {
		this.homonymsExist = homonymsExist;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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

	public Float getLexemeWeight() {
		return lexemeWeight;
	}

	public void setLexemeWeight(Float lexemeWeight) {
		this.lexemeWeight = lexemeWeight;
	}

	public Complexity getLexemeComplexity() {
		return lexemeComplexity;
	}

	public void setLexemeComplexity(Complexity lexemeComplexity) {
		this.lexemeComplexity = lexemeComplexity;
	}

	public List<String> getLexRegisterCodes() {
		return lexRegisterCodes;
	}

	public void setLexRegisterCodes(List<String> lexRegisterCodes) {
		this.lexRegisterCodes = lexRegisterCodes;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
