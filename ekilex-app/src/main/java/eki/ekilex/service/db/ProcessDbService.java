package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LEXEME;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessDbService {

	// TODO move? - yogesh

	@Autowired
	private DSLContext create;

	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {

		create.update(LEXEME)
				.set(LEXEME.PROCESS_STATE_CODE, processStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

}
