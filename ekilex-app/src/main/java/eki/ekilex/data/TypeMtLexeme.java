package eki.ekilex.data;

import java.util.List;

public class TypeMtLexeme extends AbstractCrudEntity {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private Long meaningId;

	private String datasetCode;

	private boolean isPublic;

	private TypeMtWord word;

	private List<TypeMtLexemeFreeform> usages;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public TypeMtWord getWord() {
		return word;
	}

	public void setWord(TypeMtWord word) {
		this.word = word;
	}

	public List<TypeMtLexemeFreeform> getUsages() {
		return usages;
	}

	public void setUsages(List<TypeMtLexemeFreeform> usages) {
		this.usages = usages;
	}

}
