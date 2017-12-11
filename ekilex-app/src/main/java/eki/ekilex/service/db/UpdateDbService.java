package eki.ekilex.service.db;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.tables.Freeform.FREEFORM;

@Component
public class UpdateDbService {

	private DSLContext create;

	@Autowired
	public UpdateDbService(DSLContext context) {
		create = context;
	}

	public void updateFreeformTextValue(Long id, String value) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateDefinitionValue(Long id, String value) {
		create.update(DEFINITION)
				.set(DEFINITION.VALUE, value)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}
}
