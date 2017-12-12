package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_REF_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM_REF_LINK;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefLinkDbService {

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
				.where(FREEFORM_REF_LINK.ID.eq(refLinkId))
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
				.where(DEFINITION_REF_LINK.ID.eq(refLinkId))
				.fetchOne();
	}
}
