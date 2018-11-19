package eki.wordweb.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.LexicalDecisionGameResult;
import eki.wordweb.data.LexicalDecisionGameRow;
import eki.wordweb.data.LexicalDecisionGameStat;
import eki.wordweb.service.db.GameDataDbService;

@Component
public class GameDataService {

	private static final Logger logger = LoggerFactory.getLogger(GameDataService.class);

	private static final String GAME_EXIT_MODE_BRAINLESS = "brainless";

	private static final int GAME_BATCH_SIZE = 100;

	@Autowired
	private GameDataDbService gameDataDbService;

	@Transactional
	public List<LexicalDecisionGameRow> getLexicDecisGameBatch(String displayLang) {
		return gameDataDbService.getLexicDecisGameBatch(displayLang, GAME_BATCH_SIZE);
	}

	@Transactional
	public void submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow) {
		gameDataDbService.submitLexicDecisGameRow(lexicalDecisionGameRow);
	}

	@Transactional
	public LexicalDecisionGameStat getLexicDecisGameStat(String sessionId, String gameExitMode) {

		List<LexicalDecisionGameResult> gameResults = gameDataDbService.getLexicDecisGameResults();

		int playerCount = gameResults.size();

		logger.debug("Calculated results for altogether {} players", playerCount);

		Map<String, LexicalDecisionGameResult> gameResultsMap = gameResults.stream().collect(Collectors.toMap(LexicalDecisionGameResult::getSessionId, v -> v));
		LexicalDecisionGameResult gameResult = gameResultsMap.get(sessionId);

		List<String> sortedResults;

		sortedResults = gameResults.stream()
				.sorted(Comparator.comparing(LexicalDecisionGameResult::getCorrectAnswersPercent).reversed())
				.map(LexicalDecisionGameResult::getSessionId)
				.collect(Collectors.toList());

		int correctAnswersPosition = sortedResults.indexOf(sessionId) + 1;

		sortedResults = gameResults.stream()
				.sorted(Comparator.comparing(LexicalDecisionGameResult::getAverageAnswerDelay))
				.map(LexicalDecisionGameResult::getSessionId)
				.collect(Collectors.toList());

		int averageDelayPosition = sortedResults.indexOf(sessionId) + 1;

		boolean isBrainlessExit = StringUtils.equals(GAME_EXIT_MODE_BRAINLESS, gameExitMode);

		LexicalDecisionGameStat gameStat = new LexicalDecisionGameStat();
		gameStat.setGameResult(gameResult);
		gameStat.setCorrectAnswersPosition(correctAnswersPosition);
		gameStat.setAverageDelayPosition(averageDelayPosition);
		gameStat.setPlayerCount(playerCount);
		gameStat.setBrainlessExit(isBrainlessExit);

		return gameStat;
	}
}
