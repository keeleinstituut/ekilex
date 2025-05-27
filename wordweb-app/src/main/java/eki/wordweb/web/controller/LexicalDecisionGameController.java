package eki.wordweb.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.GameStat;
import eki.wordweb.data.LexicalDecisionGameRow;
import eki.wordweb.service.GameDataService;

@Controller
public class LexicalDecisionGameController implements WebConstant, GlobalConstant {

	@Autowired
	private GameDataService gameDataService;

	@GetMapping(GAMES_LEXICDECIS_URI)
	public String game(Model model) {

		model.addAttribute("gameName", GAMES_LEXICDECIS_NAME);

		return GAME_LEXICDECIS_PAGE;
	}

	@GetMapping(GAMES_LEXICDECIS_URI + GAMES_GETGAMEBATCH_URI)
	public @ResponseBody List<LexicalDecisionGameRow> getLexicDecisGameBatch() {

		List<LexicalDecisionGameRow> gameRows = gameDataService.getLexicDecisGameBatch(LANGUAGE_CODE_EST);

		return gameRows;
	}

	@PostMapping(GAMES_LEXICDECIS_URI + GAMES_SUBMITGAMEROW_URI)
	public @ResponseBody String submitLexicDecisGameRow(@RequestBody LexicalDecisionGameRow lexicalDecisionGameRow, HttpServletRequest request) {

		String remoteAddr = request.getRemoteAddr();
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		lexicalDecisionGameRow.setRemoteAddr(remoteAddr);
		lexicalDecisionGameRow.setSessionId(sessionId);

		gameDataService.submitLexicDecisGameRow(lexicalDecisionGameRow);

		return NOTHING;
	}

	@PostMapping(GAMES_LEXICDECIS_URI + GAMES_FINISH_URI)
	public String finishLexicDecisGame(@RequestParam String gameExitMode, Model model) {

		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		GameStat gameStat = gameDataService.getLexicDecisGameStat(sessionId, gameExitMode);

		model.addAttribute("gameName", GAMES_LEXICDECIS_NAME);
		model.addAttribute("gameStat", gameStat);

		return GAMES_PAGE;
	}
}
