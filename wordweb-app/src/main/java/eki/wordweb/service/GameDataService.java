package eki.wordweb.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.LexicalDecisionGameRow;
import eki.wordweb.service.db.GameDataDbService;

@Component
public class GameDataService {

	@Autowired
	private GameDataDbService gameDataDbService;

	@Transactional
	public void submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow) {

		
	}
}
