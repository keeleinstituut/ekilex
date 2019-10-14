package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.PROCESS_LOG;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_PROCESS_LOG;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceDbService {

	@Autowired
	private DSLContext create;

	public int deleteFloatingFreeforms() {

		return create
				.delete(FREEFORM)
				.where(FREEFORM.PARENT_ID.isNull())
				.andNotExists(DSL
						.select(SOURCE_FREEFORM.ID)
						.from(SOURCE_FREEFORM)
						.where(SOURCE_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.andNotExists(DSL
						.select(MEANING_FREEFORM.ID)
						.from(MEANING_FREEFORM)
						.where(MEANING_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.andNotExists(DSL
						.select(DEFINITION_FREEFORM.ID)
						.from(DEFINITION_FREEFORM)
						.where(DEFINITION_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.andNotExists(DSL
						.select(LEXEME_FREEFORM.ID)
						.from(LEXEME_FREEFORM)
						.where(LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.andNotExists(DSL
						.select(COLLOCATION_FREEFORM.ID)
						.from(COLLOCATION_FREEFORM)
						.where(COLLOCATION_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.returning(FREEFORM.ID)
				.execute();
	}

	public int deleteFloatingProcessLogs() {

		return create
				.delete(PROCESS_LOG)
				.whereNotExists(DSL
						.select(WORD_PROCESS_LOG.ID)
						.from(WORD_PROCESS_LOG)
						.where(WORD_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.andNotExists(DSL.
						select(MEANING_PROCESS_LOG.ID)
						.from(MEANING_PROCESS_LOG)
						.where(MEANING_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.andNotExists(DSL
						.select(LEXEME_PROCESS_LOG.ID)
						.from(LEXEME_PROCESS_LOG)
						.where(LEXEME_PROCESS_LOG.PROCESS_LOG_ID.eq(PROCESS_LOG.ID)))
				.returning(PROCESS_LOG.ID)
				.execute();
	}

	public int deleteFloatingLifecycleLogs() {

		return create
				.delete(LIFECYCLE_LOG)
				.whereNotExists(DSL
						.select(WORD_LIFECYCLE_LOG.ID)
						.from(WORD_LIFECYCLE_LOG)
						.where(WORD_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID)))
				.andNotExists(DSL
						.select(MEANING_LIFECYCLE_LOG.ID)
						.from(MEANING_LIFECYCLE_LOG)
						.where(MEANING_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID)))
				.andNotExists(DSL
						.select(LEXEME_LIFECYCLE_LOG.ID)
						.from(LEXEME_LIFECYCLE_LOG)
						.where(LEXEME_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID)))
				.andNotExists(DSL
						.select(SOURCE_LIFECYCLE_LOG.ID)
						.from(SOURCE_LIFECYCLE_LOG)
						.where(SOURCE_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID)))
				.returning(LIFECYCLE_LOG.ID)
				.execute();
	}

	public int deleteFloatingMeanings() {

		return create
				.delete(MEANING)
				.whereNotExists(DSL
						.select(LEXEME.ID)
						.from(LEXEME)
						.where(LEXEME.MEANING_ID.eq(MEANING.ID)))
				.andNotExists(DSL
						.select(DEFINITION.ID)
						.from(DEFINITION)
						.where(DEFINITION.MEANING_ID.eq(MEANING.ID)))
				.returning(MEANING.ID)
				.execute();
	}

}
