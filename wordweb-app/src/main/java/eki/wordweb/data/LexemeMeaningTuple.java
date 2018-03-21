package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexemeMeaningTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Long definitionId;

	private String definition;

	private String datasetCode;

	private Integer level1;

	private Integer level2;

	private Integer level3;

	private List<String> registerCodes;

	private List<String> posCodes;

	private List<String> derivCodes;

	private List<TypeDomain> domainCodes;

	private Long meaningWordId;

	private String meaningWord;

	private Integer meaningWordHomonymNr;

	private String meaningWordLang;

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

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
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

	public List<String> getRegisterCodes() {
		return registerCodes;
	}

	public void setRegisterCodes(List<String> registerCodes) {
		this.registerCodes = registerCodes;
	}

	public List<String> getPosCodes() {
		return posCodes;
	}

	public void setPosCodes(List<String> posCodes) {
		this.posCodes = posCodes;
	}

	public List<String> getDerivCodes() {
		return derivCodes;
	}

	public void setDerivCodes(List<String> derivCodes) {
		this.derivCodes = derivCodes;
	}

	public List<TypeDomain> getDomainCodes() {
		return domainCodes;
	}

	public void setDomainCodes(List<TypeDomain> domainCodes) {
		this.domainCodes = domainCodes;
	}

	public Long getMeaningWordId() {
		return meaningWordId;
	}

	public void setMeaningWordId(Long meaningWordId) {
		this.meaningWordId = meaningWordId;
	}

	public String getMeaningWord() {
		return meaningWord;
	}

	public void setMeaningWord(String meaningWord) {
		this.meaningWord = meaningWord;
	}

	public Integer getMeaningWordHomonymNr() {
		return meaningWordHomonymNr;
	}

	public void setMeaningWordHomonymNr(Integer meaningWordHomonymNr) {
		this.meaningWordHomonymNr = meaningWordHomonymNr;
	}

	public String getMeaningWordLang() {
		return meaningWordLang;
	}

	public void setMeaningWordLang(String meaningWordLang) {
		this.meaningWordLang = meaningWordLang;
	}

}
