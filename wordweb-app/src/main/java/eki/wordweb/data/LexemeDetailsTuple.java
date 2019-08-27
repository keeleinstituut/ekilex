package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class LexemeDetailsTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Integer level1;

	private Integer level2;

	private Integer level3;

	private Complexity complexity;

	private List<String> registerCodes;

	private List<String> posCodes;

	private List<String> derivCodes;

	private List<String> adviceNotes;

	private List<TypePublicNote> publicNotes;

	private List<TypeGrammar> grammars;

	private List<TypeGovernment> governments;

	private List<TypeUsage> usages;

	private List<String> meaningLexemeRegisterCodes;

	private List<TypeGovernment> meaningLexemeGovernments;

	private Long meaningWordId;

	private String meaningWord;

	private Integer meaningWordHomonymNr;

	private String meaningWordLang;

	private List<String> meaningWordTypeCodes;

	private String meaningWordAspectCode;

	private List<TypeLexemeRelation> relatedLexemes;

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

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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

	public List<String> getMeaningLexemeRegisterCodes() {
		return meaningLexemeRegisterCodes;
	}

	public void setMeaningLexemeRegisterCodes(List<String> meaningLexemeRegisterCodes) {
		this.meaningLexemeRegisterCodes = meaningLexemeRegisterCodes;
	}

	public List<TypeGovernment> getMeaningLexemeGovernments() {
		return meaningLexemeGovernments;
	}

	public void setMeaningLexemeGovernments(List<TypeGovernment> meaningLexemeGovernments) {
		this.meaningLexemeGovernments = meaningLexemeGovernments;
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

	public List<String> getMeaningWordTypeCodes() {
		return meaningWordTypeCodes;
	}

	public void setMeaningWordTypeCodes(List<String> meaningWordTypeCodes) {
		this.meaningWordTypeCodes = meaningWordTypeCodes;
	}

	public String getMeaningWordAspectCode() {
		return meaningWordAspectCode;
	}

	public void setMeaningWordAspectCode(String meaningWordAspectCode) {
		this.meaningWordAspectCode = meaningWordAspectCode;
	}

	public List<TypeLexemeRelation> getRelatedLexemes() {
		return relatedLexemes;
	}

	public void setRelatedLexemes(List<TypeLexemeRelation> relatedLexemes) {
		this.relatedLexemes = relatedLexemes;
	}

}
