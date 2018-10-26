package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.LEXICAL_DECISION_DATA;
import static eki.wordweb.data.db.Tables.LEXICAL_DECISION_RESULT;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.LexicalDecisionGameRow;

@Component
public class GameDataDbService {

	@Autowired
	private DSLContext create;

	public void submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow) {
		create
			.insertInto(LEXICAL_DECISION_RESULT)
			.columns(
					LEXICAL_DECISION_RESULT.DATA_ID,
					LEXICAL_DECISION_RESULT.REMOTE_ADDR,
					LEXICAL_DECISION_RESULT.LOCAL_ADDR,
					LEXICAL_DECISION_RESULT.SESSION_ID,
					LEXICAL_DECISION_RESULT.ANSWER,
					LEXICAL_DECISION_RESULT.DELAY
					)
			.values(
					lexicalDecisionGameRow.getDataId(),
					lexicalDecisionGameRow.getRemoteAddr(),
					lexicalDecisionGameRow.getLocalAddr(),
					lexicalDecisionGameRow.getSessionId(),
					lexicalDecisionGameRow.isAnswer(),
					lexicalDecisionGameRow.getDelay()
					)
			.execute();
	}
}
