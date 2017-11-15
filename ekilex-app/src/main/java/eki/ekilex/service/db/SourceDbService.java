package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record15;
import org.jooq.Result;
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

	public Result<Record15<Long, String, Timestamp, String, Timestamp, String, String, String, Long, String, String, Long, String, String, Timestamp>> findSources(String searchFilterWithMetaCharacters) {

		String searchFilter = searchFilterWithMetaCharacters.replace("*", "%").replace("?", "_");

		Source s = SOURCE.as("s");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		Freeform sh = FREEFORM.as("sh");
		Freeform sp = FREEFORM.as("sp");

		return create
				.select(
						s.ID.as("source_id"),
						s.CONCEPT,
						s.CREATED_ON,
						s.CREATED_BY,
						s.MODIFIED_ON,
						s.MODIFIED_BY,
						s.ENTRY_CLASS_CODE,
						s.TYPE,
						sh.ID.as("source_heading_id"),
						sh.TYPE.as("source_heading_type"),
						sh.VALUE_TEXT.as("source_heading_value"),
						sp.ID.as("source_property_id"),
						sp.TYPE.as("source_property_type"),
						sp.VALUE_TEXT.as("source_property_value_text"),
						sp.VALUE_DATE.as("source_property_value_date"))
				.from(s, sff, sh, sp)
				.where(
						sff.SOURCE_ID.eq(s.ID)
						.and(sff.FREEFORM_ID.eq(sh.ID))
						.and(sp.PARENT_ID.eq(sh.ID))
						.and(sh.VALUE_TEXT.likeIgnoreCase(searchFilter).or(sp.VALUE_TEXT.likeIgnoreCase(searchFilter))))
				.orderBy(s.ID, sh.TYPE, sp.TYPE)
				.fetch();
	}
}
