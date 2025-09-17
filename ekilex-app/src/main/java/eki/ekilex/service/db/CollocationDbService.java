package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollocationDbService {

	@Autowired
	private DSLContext mainDb;

	public void moveCollocMember(List<Long> collocLexemeIds, Long sourceCollocMemberLexemeId, Long targetCollocMemberLexemeId) {

		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.MEMBER_LEXEME_ID, targetCollocMemberLexemeId)
				.where(
						COLLOCATION_MEMBER.MEMBER_LEXEME_ID.eq(sourceCollocMemberLexemeId)
								.and(COLLOCATION_MEMBER.COLLOC_LEXEME_ID.in(collocLexemeIds)))
				.execute();
	}
}
