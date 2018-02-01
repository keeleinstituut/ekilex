package eki.ekilex.service.db;

import eki.ekilex.data.OrderingData;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
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

	public void updateDefinitionOrderby(List<OrderingData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (OrderingData item : items) {
			updateQueries.add(create.update(DEFINITION).set(DEFINITION.ORDER_BY, item.getOrderby()).where(DEFINITION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public void updateLexemeRelationOrderby(List<OrderingData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (OrderingData item : items) {
			updateQueries.add(create.update(LEX_RELATION).set(LEX_RELATION.ORDER_BY, item.getOrderby()).where(LEX_RELATION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}
}
