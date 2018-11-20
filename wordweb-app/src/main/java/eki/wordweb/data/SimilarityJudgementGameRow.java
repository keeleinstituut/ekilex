package eki.wordweb.data;

public class SimilarityJudgementGameRow extends AbstractGameRow {

	private static final long serialVersionUID = 1L;

	private String gameKey;

	private Long synDataId1;

	private String synWord1;

	private Long synDataId2;

	private String synWord2;

	private Long rndDataId1;

	private String rndWord1;

	private Long rndDataId2;

	private String rndWord2;

	private WordPair wordPair1;

	private WordPair wordPair2;

	private boolean answerPair1;

	private boolean answerPair2;

	public String getGameKey() {
		return gameKey;
	}

	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}

	public Long getSynDataId1() {
		return synDataId1;
	}

	public void setSynDataId1(Long synDataId1) {
		this.synDataId1 = synDataId1;
	}

	public String getSynWord1() {
		return synWord1;
	}

	public void setSynWord1(String synWord1) {
		this.synWord1 = synWord1;
	}

	public Long getSynDataId2() {
		return synDataId2;
	}

	public void setSynDataId2(Long synDataId2) {
		this.synDataId2 = synDataId2;
	}

	public String getSynWord2() {
		return synWord2;
	}

	public void setSynWord2(String synWord2) {
		this.synWord2 = synWord2;
	}

	public Long getRndDataId1() {
		return rndDataId1;
	}

	public void setRndDataId1(Long rndDataId1) {
		this.rndDataId1 = rndDataId1;
	}

	public String getRndWord1() {
		return rndWord1;
	}

	public void setRndWord1(String rndWord1) {
		this.rndWord1 = rndWord1;
	}

	public Long getRndDataId2() {
		return rndDataId2;
	}

	public void setRndDataId2(Long rndDataId2) {
		this.rndDataId2 = rndDataId2;
	}

	public String getRndWord2() {
		return rndWord2;
	}

	public void setRndWord2(String rndWord2) {
		this.rndWord2 = rndWord2;
	}

	public WordPair getWordPair1() {
		return wordPair1;
	}

	public void setWordPair1(WordPair wordPair1) {
		this.wordPair1 = wordPair1;
	}

	public WordPair getWordPair2() {
		return wordPair2;
	}

	public void setWordPair2(WordPair wordPair2) {
		this.wordPair2 = wordPair2;
	}

	public boolean isAnswerPair1() {
		return answerPair1;
	}

	public void setAnswerPair1(boolean answerPair1) {
		this.answerPair1 = answerPair1;
	}

	public boolean isAnswerPair2() {
		return answerPair2;
	}

	public void setAnswerPair2(boolean answerPair2) {
		this.answerPair2 = answerPair2;
	}

}
