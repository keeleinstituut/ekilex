package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
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
import eki.wordweb.service.GameDataService;

@Controller
public class LexicalDecisionGameController implements WebConstant {

	@Autowired
	private GameDataService gameDataService;

	@GetMapping(GAMES_LEXICDECIS_URI)
	public String games(Model model) {

		return GAME_LEXICDECIS_PAGE;
	}

	@GetMapping(GAMES_LEXICDECIS_GETGAMEDBATCH_URI)
	public @ResponseBody List<LexicalDecisionGameRow> getLexicDecisGameBatch() {

		//TODO dummy

		List<LexicalDecisionGameRow> gameRows = new ArrayList<>();
		LexicalDecisionGameRow gameRow;
		final int gameBatchSize = 5000;
		for (int rowIndex = 0; rowIndex < gameBatchSize; rowIndex++) {
			Long id = rowIndex + 1000L;
			String suggestedWordValue = RandomStringUtils.randomAlphabetic(10).toLowerCase();
			boolean isWord = new Random().nextBoolean();
			gameRow = new LexicalDecisionGameRow();
			gameRow.setDataId(id);
			gameRow.setSuggestedWordValue(suggestedWordValue);
			gameRow.setWord(isWord);
			gameRows.add(gameRow);
		}

		return gameRows;
	}

	@PostMapping(GAMES_LEXICDECIS_SUBMITGAMEROW_URI)
	public @ResponseBody String submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow, HttpServletRequest request) {

		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		String remoteAddr = request.getRemoteAddr();
		String localAddr = request.getLocalAddr();
		lexicalDecisionGameRow.setRemoteAddr(remoteAddr);
		lexicalDecisionGameRow.setLocalAddr(localAddr);
		lexicalDecisionGameRow.setSessionId(sessionId);

		gameDataService.submitLexicDecisGameRow(lexicalDecisionGameRow);

		return NOTHING;
	}

	@PostMapping(GAMES_LEXICDECIS_FINISH_URI)
	public String finishLexicDecisGame(@RequestParam String lexicDecisFinishMode) {

		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		//System.out.println("---> " + sessionId + " -- " + lexicDecisFinishMode);

		return GAMES_URI;
	}
}
