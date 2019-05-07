package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.DbConstant;

@Component
public class RefLinkDbService implements DbConstant {

	private DSLContext create;

	@Autowired
	public RefLinkDbService(DSLContext context) {
		create = context;
	}

	public Record3<Long, String, Long> getFreeformSourceLink(Long refLinkId) {

		return create
				.select(
						FREEFORM_SOURCE_LINK.FREEFORM_ID.as("owner_id"),
						FREEFORM_SOURCE_LINK.TYPE,
						FREEFORM_SOURCE_LINK.SOURCE_ID)
				.from(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.ID.eq(refLinkId))
				.fetchOne();
	}

	public Record3<Long, String, Long> getDefinitionSourceLink(Long refLinkId) {

		return create
				.select(
						DEFINITION_SOURCE_LINK.DEFINITION_ID.as("owner_id"),
						DEFINITION_SOURCE_LINK.TYPE,
						DEFINITION_SOURCE_LINK.SOURCE_ID)
				.from(DEFINITION_SOURCE_LINK)
				.where(DEFINITION_SOURCE_LINK.ID.eq(refLinkId))
				.fetchOne();
	}

	public Record3<Long, String, Long> getLexemeSourceLink(Long refLinkId) {

		return create
				.select(
						LEXEME_SOURCE_LINK.LEXEME_ID.as("owner_id"),
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.SOURCE_ID)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(refLinkId))
				.fetchOne();
	}

}
