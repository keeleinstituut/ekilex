package eki.wordweb.data;

public class LexicalDecisionGameRow extends AbstractGameRow {

	private static final long serialVersionUID = 1L;

	private Long dataId;

	private String suggestedWordValue;

	private boolean word;

	private boolean answer;

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getSuggestedWordValue() {
		return suggestedWordValue;
	}

	public void setSuggestedWordValue(String suggestedWordValue) {
		this.suggestedWordValue = suggestedWordValue;
	}

	public boolean isWord() {
		return word;
	}

	public void setWord(boolean word) {
		this.word = word;
	}

	public boolean isAnswer() {
		return answer;
	}

	public void setAnswer(boolean answer) {
		this.answer = answer;
	}

}
