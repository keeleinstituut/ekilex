package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.SourceType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;

@Component
public class SourceDbService implements SystemConstant {

	private DSLContext create;

	@Autowired
	public SourceDbService(DSLContext context) {
		create = context;
	}

	public List<SourcePropertyTuple> getSource(Long sourceId) {

		Source s = SOURCE.as("s");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		Freeform sp = FREEFORM.as("sp");

		return create
				.select(
						s.ID.as("source_id"),
						s.EXT_SOURCE_ID,
						s.CREATED_ON,
						s.CREATED_BY,
						s.MODIFIED_ON,
						s.MODIFIED_BY,
						s.PROCESS_STATE_CODE,
						s.TYPE,
						sp.ID.as("source_property_id"),
						sp.TYPE.as("source_property_type"),
						sp.VALUE_TEXT.as("source_property_value_text"),
						sp.VALUE_DATE.as("source_property_value_date")
						)
				.from(
						s
						.innerJoin(sff).on(s.ID.eq(sff.SOURCE_ID))
						.innerJoin(sp).on(sff.FREEFORM_ID.eq(sp.ID)))
				.where(s.ID.equal(sourceId))
				.orderBy(sp.ORDER_BY)
				.fetch()
				.into(SourcePropertyTuple.class);
	}

	public List<SourcePropertyTuple> findSourcesByNameAndType(String searchFilterWithMetaCharacters, SourceType sourceType) {

		String searchFilter = searchFilterWithMetaCharacters.replace("*", "%").replace("?", "_").toLowerCase();

		Source s = SOURCE.as("s");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		Freeform sp = FREEFORM.as("sp");
		SourceFreeform sffc = SOURCE_FREEFORM.as("sffc");
		Freeform spc = FREEFORM.as("spc");

		SelectConditionStep<Record1<Long>> existCondition =
				DSL
				.select(sffc.ID)
				.from(sffc, spc)
				.where(
						sffc.SOURCE_ID.eq(s.ID)
						.and(sffc.FREEFORM_ID.eq(spc.ID))
						.and(spc.VALUE_TEXT.lower().like(searchFilter)));
		if (sourceType != null) {
			existCondition = existCondition.and(s.TYPE.eq(sourceType.name()));
		}

		Condition sex = DSL.exists(existCondition);

		Field<Boolean> is_source_property_match = DSL.field(sp.VALUE_TEXT.lower().like(searchFilter));

		return create
				.select(
						s.ID.as("source_id"),
						s.EXT_SOURCE_ID,
						s.CREATED_ON,
						s.CREATED_BY,
						s.MODIFIED_ON,
						s.MODIFIED_BY,
						s.PROCESS_STATE_CODE,
						s.TYPE,
						sp.ID.as("source_property_id"),
						sp.TYPE.as("source_property_type"),
						sp.VALUE_TEXT.as("source_property_value_text"),
						sp.VALUE_DATE.as("source_property_value_date"),
						is_source_property_match.as("is_source_property_match")
						)
				.from(
						s
						.innerJoin(sff).on(s.ID.eq(sff.SOURCE_ID))
						.innerJoin(sp).on(sff.FREEFORM_ID.eq(sp.ID)))
				.where(sex)
				.orderBy(s.ID, sp.ORDER_BY)
				.fetch()
				.into(SourcePropertyTuple.class);
	}
}
