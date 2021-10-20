package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION_FREEFORM;
import static eki.ekilex.data.db.Tables.DATA_REQUEST;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_GUID;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.db.Routines;
import eki.ekilex.data.db.tables.DataRequest;
import eki.ekilex.data.db.tables.records.WordRecord;

@Component
public class MaintenanceDbService implements GlobalConstant {

	private static final String DATASET_CODE_MAB = "mab";

	@Autowired
	private DSLContext create;

	public List<WordRecord> getWordRecords() {
		return create.selectFrom(WORD).fetchInto(WordRecord.class);
	}

	public void mergeHomonymsToEki(String[] includedLangs) {

		Routines.mergeHomonymsToEki(create.configuration(), includedLangs);
	}

	public void adjustHomonymNrs() {

		Routines.adjustHomonymNrs(create.configuration());
	}

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
				.andNotExists(DSL
						.select(WORD_FREEFORM.ID)
						.from(WORD_FREEFORM)
						.where(WORD_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.andExists(DSL.select(DSL.field(IGNORE_QUERY_LOG)))
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
				.andExists(DSL.select(DSL.field(IGNORE_QUERY_LOG)))
				.execute();
	}

	public int deleteFloatingWords() {

		return create
				.delete(WORD)
				.whereNotExists(DSL
						.select(LEXEME.ID)
						.from(LEXEME)
						.where(LEXEME.WORD_ID.eq(WORD.ID)))
				.andNotExists(DSL
						.select(WORD_GUID.ID)
						.from(WORD_GUID)
						.where(WORD_GUID.WORD_ID.eq(WORD.ID)
								.and(WORD_GUID.DATASET_CODE.eq(DATASET_CODE_MAB))))
				.andExists(DSL.select(DSL.field(IGNORE_QUERY_LOG)))
				.execute();
	}

	public int deleteAccessedDataRequests(int hours) {

		DataRequest dr = DATA_REQUEST.as("dr");
		return create
				.delete(dr)
				.where(
						dr.ACCESSED.isNotNull()
						.and(DSL.condition("(current_timestamp - dr.accessed) >= (interval '" + hours + " hour')")))
				.execute();
	}
}
