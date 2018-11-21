package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class GameStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private GameResult gameResult;

	private int correctAnswersPosition;

	private int averageDelayPosition;

	private int playerCount;

	private boolean brainlessExit;

	public GameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(GameResult gameResult) {
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
