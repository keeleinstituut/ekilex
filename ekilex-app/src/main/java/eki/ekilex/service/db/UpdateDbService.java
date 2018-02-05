package eki.ekilex.service.db;

import eki.ekilex.data.OrderingData;
import eki.ekilex.data.db.tables.Lexeme;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record4;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
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

	public void updateMeaningRelationOrderby(List<OrderingData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (OrderingData item : items) {
			updateQueries.add(create.update(MEANING_RELATION).set(MEANING_RELATION.ORDER_BY, item.getOrderby()).where(MEANING_RELATION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public void updateWordRelationOrderby(List<OrderingData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (OrderingData item : items) {
			updateQueries.add(create.update(WORD_RELATION).set(WORD_RELATION.ORDER_BY, item.getOrderby()).where(WORD_RELATION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public Result<Record4<Long,Integer,Integer,Integer>> findConnectedLexemes(Long lexemeId) {
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		return create
				.select(
						l1.ID.as("lexeme_id"),
						l1.LEVEL1,
						l1.LEVEL2,
						l1.LEVEL3)
				.from(l1, l2)
				.where(l2.ID.eq(lexemeId).and(l1.WORD_ID.eq(l2.WORD_ID)).and(l1.DATASET_CODE.eq(l2.DATASET_CODE)))
				.orderBy(l1.LEVEL1, l1.LEVEL2, l1.LEVEL3)
				.fetch();
	}

	public void updateLexemeLevels(Long id, Integer level1, Integer level2, Integer level3) {
		create.update(LEXEME)
				.set(LEXEME.LEVEL1, level1)
				.set(LEXEME.LEVEL2, level2)
				.set(LEXEME.LEVEL3, level3)
				.where(LEXEME.ID.eq(id))
				.execute();
	}
}
