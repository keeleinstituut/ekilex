package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_REF_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM_REF_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_REF_LINK;

import eki.ekilex.constant.DbConstant;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefLinkDbService implements DbConstant {

	private DSLContext create;

	@Autowired
	public RefLinkDbService(DSLContext context) {
		create = context;
	}

	public Record3<Long, String, Long> getFreeformRefLink(Long refLinkId) {

		return create
				.select(
						FREEFORM_REF_LINK.FREEFORM_ID.as("owner_id"),
						FREEFORM_REF_LINK.REF_TYPE,
						FREEFORM_REF_LINK.REF_ID
						)
				.from(FREEFORM_REF_LINK)
				.where(FREEFORM_REF_LINK.ID.eq(refLinkId).and(FREEFORM_REF_LINK.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
	}

	public Record3<Long, String, Long> getDefinitionRefLink(Long refLinkId) {

		return create
				.select(
						DEFINITION_REF_LINK.DEFINITION_ID.as("owner_id"),
						DEFINITION_REF_LINK.REF_TYPE,
						DEFINITION_REF_LINK.REF_ID
						)
				.from(DEFINITION_REF_LINK)
				.where(DEFINITION_REF_LINK.ID.eq(refLinkId).and(DEFINITION_REF_LINK.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
	}

	public Record3<Long, String, Long> getLexemeRefLink(Long refLinkId) {

		return create
				.select(
						LEXEME_REF_LINK.LEXEME_ID.as("owner_id"),
						LEXEME_REF_LINK.REF_TYPE,
						LEXEME_REF_LINK.REF_ID
						)
				.from(LEXEME_REF_LINK)
				.where(LEXEME_REF_LINK.ID.eq(refLinkId).and(LEXEME_REF_LINK.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
	}

}
