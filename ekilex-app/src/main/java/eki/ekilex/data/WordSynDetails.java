package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordSynDetails extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private String lang;

	private String morphCode;

	private String[] wordTypeCodes;

	private List<WordSynLexeme> lexemes;

	private List<SynRelation> relations;

	private boolean synLayerComplete;

	private Integer wordProcessLogCount;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

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
	public void setWordValue(String word) {
		this.wordValue = word;
	}

	@Override
	public String getWordValuePrese() {
		return wordValuePrese;
	}

	@Override
	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	@Override
	public String[] getWordTypeCodes() {
		return wordTypeCodes;
	}

	@Override
	public void setWordTypeCodes(String[] wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	public List<WordSynLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<WordSynLexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<SynRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<SynRelation> relations) {
		this.relations = relations;
	}

	public boolean isSynLayerComplete() {
		return synLayerComplete;
	}

	public void setSynLayerComplete(boolean synLayerComplete) {
		this.synLayerComplete = synLayerComplete;
	}

	public Integer getWordProcessLogCount() {
		return wordProcessLogCount;
	}

	public void setWordProcessLogCount(Integer wordProcessLogCount) {
		this.wordProcessLogCount = wordProcessLogCount;
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

}
