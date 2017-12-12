package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;
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

	public Result<Record17<Long, String, Timestamp, String, Timestamp, String, String, String, Long, String, String, Boolean, Long, String, String, Timestamp, Boolean>> findSources(
			String searchFilterWithMetaCharacters) {

		String searchFilter = searchFilterWithMetaCharacters.replace("*", "%").replace("?", "_");

		Source s = SOURCE.as("s");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		Freeform sh = FREEFORM.as("sh");
		Freeform sp = FREEFORM.as("sp");

		Table<Record1<Long>> m_s = create
				.select(s.ID)
				.from(
						s.innerJoin(sff).on(sff.SOURCE_ID.eq(s.ID))
						.innerJoin(sh).on(sff.FREEFORM_ID.eq(sh.ID))
						.leftOuterJoin(sp).on(sp.PARENT_ID.eq(sh.ID)))
				.where(
						sh.VALUE_TEXT.likeIgnoreCase(searchFilter)
						.or(sp.VALUE_TEXT.likeIgnoreCase(searchFilter)))
				.groupBy(s.ID)
				.asTable("m_s");

		Field<Object> m_s_id = DSL.field("m_s.ID");
		Field<Boolean> is_source_heading_match = DSL.field(sh.VALUE_TEXT.likeIgnoreCase(searchFilter));
		Field<Boolean> is_source_property_match = DSL.field(sp.VALUE_TEXT.likeIgnoreCase(searchFilter));

		return create
				.select(
						s.ID.as("source_id"),
						s.CONCEPT,
						s.CREATED_ON,
						s.CREATED_BY,
						s.MODIFIED_ON,
						s.MODIFIED_BY,
						s.PROCESS_STATE_CODE,
						s.TYPE,
						sh.ID.as("source_heading_id"),
						sh.TYPE.as("source_heading_type"),
						sh.VALUE_TEXT.as("source_heading_value"),
						is_source_heading_match.as("is_source_heading_match"),
						sp.ID.as("source_property_id"),
						sp.TYPE.as("source_property_type"),
						sp.VALUE_TEXT.as("source_property_value_text"),
						sp.VALUE_DATE.as("source_property_value_date"),
						is_source_property_match.as("is_source_property_match"))
				.from(
						m_s.innerJoin(s).on(m_s_id.eq(s.ID))
						.innerJoin(sff).on(m_s_id.eq(sff.SOURCE_ID))
						.innerJoin(sh).on(sff.FREEFORM_ID.eq(sh.ID))
						.leftOuterJoin(sp).on(sp.PARENT_ID.eq(sh.ID)))
				.orderBy(s.ID, sh.TYPE, sp.TYPE)
				.fetch();

	}
}
