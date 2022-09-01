package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermUpdateWordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private List<Definition> meaningDefinitions;

	private List<OrderedClassifier> meaningDomains;

	private String datasetCode;

	private String datasetName;

	private String originalWordValuePrese;

	private String originalWordLang;

	private String wordValuePrese;

	private String wordLang;

	private List<WordDescript> wordCandidates;

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

	public List<Definition> getMeaningDefinitions() {
		return meaningDefinitions;
	}

	public void setMeaningDefinitions(List<Definition> meaningDefinitions) {
		this.meaningDefinitions = meaningDefinitions;
	}

	public List<OrderedClassifier> getMeaningDomains() {
		return meaningDomains;
	}

	public void setMeaningDomains(List<OrderedClassifier> meaningDomains) {
		this.meaningDomains = meaningDomains;
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

	public String getOriginalWordValuePrese() {
		return originalWordValuePrese;
	}

	public void setOriginalWordValuePrese(String originalWordValuePrese) {
		this.originalWordValuePrese = originalWordValuePrese;
	}

	public String getOriginalWordLang() {
		return originalWordLang;
	}

	public void setOriginalWordLang(String originalWordLang) {
		this.originalWordLang = originalWordLang;
	}

	public String getWordValuePrese() {
		return wordValuePrese;
	}

	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public List<WordDescript> getWordCandidates() {
		return wordCandidates;
	}

	public void setWordCandidates(List<WordDescript> wordCandidates) {
		this.wordCandidates = wordCandidates;
	}
}