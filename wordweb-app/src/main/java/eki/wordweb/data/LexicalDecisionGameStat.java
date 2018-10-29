package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class LexicalDecisionGameStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private LexicalDecisionGameResult gameResult;

	private int correctAnswersPosition;

	private int averageDelayPosition;

	private int playerCount;

	private boolean brainlessExit;

	public LexicalDecisionGameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(LexicalDecisionGameResult gameResult) {
		this.gameResult = gameResult;
	}

	public int getCorrectAnswersPosition() {
		return correctAnswersPosition;
	}

	public void setCorrectAnswersPosition(int correctAnswersPosition) {
		this.correctAnswersPosition = correctAnswersPosition;
	}

	public int getAverageDelayPosition() {
		return averageDelayPosition;
	}

	public void setAverageDelayPosition(int averageDelayPosition) {
		this.averageDelayPosition = averageDelayPosition;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public boolean isBrainlessExit() {
		return brainlessExit;
	}

	public void setBrainlessExit(boolean brainlessExit) {
		this.brainlessExit = brainlessExit;
	}

}
