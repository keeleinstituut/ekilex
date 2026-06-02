package eki.ekilex.cli.util;

public class ProgressCounter {

	private int iterationCounter;

	private int progressIndicator;

	private int progressPercent;

	public ProgressCounter(int totalCount) {
		this.iterationCounter = 0;
		this.progressIndicator = totalCount / Math.min(totalCount, 100);
	}

	public void iterate() {
		iterationCounter++;
		this.progressPercent = iterationCounter / progressIndicator;
	}

	public boolean isProgressStep() {
		return (iterationCounter % progressIndicator == 0);
	}

	public int getIterationCounter() {
		return iterationCounter;
	}

	public int getProgressPercent() {
		return progressPercent;
	}

}
