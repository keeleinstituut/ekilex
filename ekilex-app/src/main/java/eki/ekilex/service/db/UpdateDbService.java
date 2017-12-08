package eki.ekilex.service.db;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static eki.ekilex.data.db.tables.Freeform.FREEFORM;

@Component
public class UpdateDbService {

	private DSLContext create;

	@Autowired
	public UpdateDbService(DSLContext context) {
		create = context;
	}

	public void updateUsageValue(Long id, String usageValue) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, usageValue)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

}
