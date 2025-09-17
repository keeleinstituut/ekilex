package eki.wordweb.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.GameResult;
import eki.wordweb.data.GameStat;
import eki.wordweb.data.LexicalDecisionGameRow;
import eki.wordweb.data.SimilarityJudgementGameRow;
import eki.wordweb.data.WordPair;
import eki.wordweb.service.db.GameDataDbService;

@Component
public class GameDataService implements InitializingBean, SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(GameDataService.class);

	private static final String GAME_EXIT_MODE_BRAINLESS = "brainless";

	private static final int GAME_BATCH_SIZE = 50;

	@Autowired
	private GameDataDbService gameDataDbService;

	private Map<String, String> languagesDatasetMap;

	@Override
	public void afterPropertiesSet() {
		languagesDatasetMap = new HashMap<>();
		languagesDatasetMap.put("est-est-easy", "psv");
		languagesDatasetMap.put("est-rus-easy", "qq2");
		languagesDatasetMap.put("est-rus-hard", "ev2");
	}

	@Transactional
	public List<LexicalDecisionGameRow> getLexicDecisGameBatch(String displayLang) {
		return gameDataDbService.getLexicDecisGameBatch(displayLang, GAME_BATCH_SIZE);
	}

	@Transactional
	public void submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow) {
		gameDataDbService.submitLexicDecisGameRow(lexicalDecisionGameRow);
	}

	@Transactional
	public GameStat getLexicDecisGameStat(String sessionId, String gameExitMode) {

		List<GameResult> gameResults = gameDataDbService.getLexicDecisGameResults();
		GameStat gameStat = composeGameStat(sessionId, gameExitMode, gameResults);
		return gameStat;
	}

	@Transactional
	public List<SimilarityJudgementGameRow> getSimilJudgeGameBatch(String gameKey) {

		String dataset = languagesDatasetMap.get(gameKey);
		if (StringUtils.isBlank(dataset)) {
			return Collections.emptyList();
		}
		boolean isSameLang = !(StringUtils.contains(gameKey, "est") && StringUtils.contains(gameKey, "rus"));
		List<SimilarityJudgementGameRow> gameBatch = gameDataDbService.getSimilJudgeGameBatch(dataset, isSameLang, GAME_BATCH_SIZE);
		WordPair wordPair1, wordPair2;
		Random pairRandom = new Random();
		for (SimilarityJudgementGameRow gameRow : gameBatch) {
			gameRow.setGameKey(gameKey);
			wordPair1 = new WordPair(gameRow.getSynDataId1(), gameRow.getSynWord1(), gameRow.getSynDataId2(), gameRow.getSynWord2(), true);
			wordPair2 = new WordPair(gameRow.getRndDataId1(), gameRow.getRndWord1(), gameRow.getRndDataId2(), gameRow.getRndWord2(), false);
			if (pairRandom.nextBoolean()) {
				gameRow.setWordPair1(wordPair1);
				gameRow.setWordPair2(wordPair2);
			} else {
				gameRow.setWordPair1(wordPair2);
				gameRow.setWordPair2(wordPair1);
			}
		}
		return gameBatch;
	}

	@Transactional
	public void submitSimilJudgeGameRow(SimilarityJudgementGameRow similarityJudgementGameRow) {
		gameDataDbService.submitSimilJudgeGameRow(similarityJudgementGameRow);
	}

	@Transactional
	public GameStat getSimilJudgeGameStat(String gameKey, String sessionId, String gameExitMode) {

		List<GameResult> gameResults = gameDataDbService.getSimilJudgeGameResults(gameKey);
		GameStat gameStat = composeGameStat(sessionId, gameExitMode, gameResults);
		return gameStat;
	}

	private GameStat composeGameStat(String sessionId, String gameExitMode, List<GameResult> gameResults) {
		int playerCount = gameResults.size();

		logger.debug("Calculated results for altogether {} players", playerCount);

		Map<String, GameResult> gameResultsMap = gameResults.stream().collect(Collectors.toMap(GameResult::getSessionId, v -> v));
		GameResult gameResult = gameResultsMap.get(sessionId);

		List<String> sortedResults;

		sortedResults = gameResults.stream()
				.sorted(Comparator.comparing(GameResult::getCorrectAnswersPercent).reversed())
				.map(GameResult::getSessionId)
				.collect(Collectors.toList());

		int correctAnswersPosition = sortedResults.indexOf(sessionId) + 1;

		sortedResults = gameResults.stream()
				.sorted(Comparator.comparing(GameResult::getAverageAnswerDelay))
				.map(GameResult::getSessionId)
				.collect(Collectors.toList());

		int averageDelayPosition = sortedResults.indexOf(sessionId) + 1;

		boolean isBrainlessExit = StringUtils.equals(GAME_EXIT_MODE_BRAINLESS, gameExitMode);

		GameStat gameStat = new GameStat();
		gameStat.setGameResult(gameResult);
		gameStat.setCorrectAnswersPosition(correctAnswersPosition);
		gameStat.setAverageDelayPosition(averageDelayPosition);
		gameStat.setPlayerCount(playerCount);
		gameStat.setBrainlessExit(isBrainlessExit);
		return gameStat;
	}

}
