package eki.wordweb.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.LexicalDecisionGameRow;
import eki.wordweb.data.LexicalDecisionGameStat;
import eki.wordweb.service.GameDataService;

@Controller
public class LexicalDecisionGameController implements WebConstant {

	@Autowired
	private GameDataService gameDataService;

	@GetMapping(GAMES_LEXICDECIS_URI)
	public String game(Model model) {

		return GAME_LEXICDECIS_PAGE;
	}

	@GetMapping(GAMES_LEXICDECIS_URI + GAMES_GETGAMEBATCH_URI)
	public @ResponseBody List<LexicalDecisionGameRow> getLexicDecisGameBatch() {

		List<LexicalDecisionGameRow> gameRows = gameDataService.getLexicDecisGameBatch(DISPLAY_LANG);

		return gameRows;
	}

	@PostMapping(GAMES_LEXICDECIS_URI + GAMES_SUBMITGAMEROW_URI)
	public @ResponseBody String submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow, HttpServletRequest request) {

		String remoteAddr = request.getRemoteAddr();
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		lexicalDecisionGameRow.setRemoteAddr(remoteAddr);
		lexicalDecisionGameRow.setSessionId(sessionId);

		gameDataService.submitLexicDecisGameRow(lexicalDecisionGameRow);

		return NOTHING;
	}

	@PostMapping(GAMES_LEXICDECIS_URI + GAMES_FINISH_URI)
	public String finishLexicDecisGame(@RequestParam String lexicDecisExitMode, Model model) {

		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		LexicalDecisionGameStat lexicDecisGameStat = gameDataService.getLexicDecisGameStat(sessionId, lexicDecisExitMode);

		model.addAttribute("lexicDecisGameStat", lexicDecisGameStat);

		return GAMES_PAGE;
	}
}
