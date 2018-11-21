package eki.wordweb.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.GameStat;
import eki.wordweb.data.SimilarityJudgementGameRow;
import eki.wordweb.service.GameDataService;

@Controller
public class SimilarityJudgementGameController implements WebConstant {

	@Autowired
	private GameDataService gameDataService;

	@GetMapping(GAMES_SIMILJUDGE_URI + "/{gameKey}")
	public String game(@PathVariable(name = "gameKey") String gameKey, Model model) {

		model.addAttribute("gameName", GAMES_SIMILJUDGE_NAME);
		model.addAttribute("gameKey", gameKey);

		return GAME_SIMILJUDGE_PAGE;
	}

	@GetMapping(GAMES_SIMILJUDGE_URI + GAMES_GETGAMEBATCH_URI + "/{gameKey}")
	public @ResponseBody List<SimilarityJudgementGameRow> getSimilJudgeGameBatch(@PathVariable(name = "gameKey") String gameKey) {

		List<SimilarityJudgementGameRow> gameRows = gameDataService.getSimilJudgeGameBatch(gameKey);

		return gameRows;
	}

	@PostMapping(GAMES_SIMILJUDGE_URI + GAMES_SUBMITGAMEROW_URI)
	public @ResponseBody String submitSimilJudgeGameRow(@RequestBody SimilarityJudgementGameRow similarityJudgementGameRow, HttpServletRequest request) {

		String remoteAddr = request.getRemoteAddr();
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		similarityJudgementGameRow.setRemoteAddr(remoteAddr);
		similarityJudgementGameRow.setSessionId(sessionId);

		gameDataService.submitSimilJudgeGameRow(similarityJudgementGameRow);

		return NOTHING;
	}

	@PostMapping(GAMES_SIMILJUDGE_URI + GAMES_FINISH_URI)
	public String finishSimilJudgeGame(
			@RequestParam(name = "gameKey") String gameKey,
			@RequestParam(name = "gameExitMode") String gameExitMode,
			Model model) {

		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		GameStat gameStat = gameDataService.getSimilJudgeGameStat(gameKey, sessionId, gameExitMode);

		model.addAttribute("gameName", GAMES_SIMILJUDGE_NAME);
		model.addAttribute("gameKey", gameKey);
		model.addAttribute("gameStat", gameStat);

		return GAMES_PAGE;
	}
}
