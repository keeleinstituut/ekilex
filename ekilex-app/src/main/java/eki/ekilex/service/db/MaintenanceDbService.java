package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION_FREEFORM;
import static eki.ekilex.data.db.Tables.DATA_REQUEST;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.SourceTargetIdTuple;
import eki.ekilex.data.db.Routines;
import eki.ekilex.data.db.tables.DataRequest;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.records.WordRecord;

@Component
public class MaintenanceDbService extends AbstractDataDbService {

	@Autowired
	private DSLContext create;

	public List<WordRecord> getWordRecords() {
		return create
				.selectFrom(WORD)
				.where(WORD.IS_PUBLIC.isTrue())
				.fetchInto(WordRecord.class);
	}

	public List<SourceTargetIdTuple> getHomonymsToMerge(String[] includedLangs) {

		Word w1 = WORD.as("w1");
		Word w2 = WORD.as("w2");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");

		Field<Boolean> isPrefixoid = getWordTypeExists(w1.ID, WORD_TYPE_CODE_PREFIXOID);
		Field<Boolean> isSuffixoid = getWordTypeExists(w1.ID, WORD_TYPE_CODE_SUFFIXOID);
		Field<Boolean> isSymbol = getWordTypeExists(w1.ID, WORD_TYPE_CODE_SYMBOL);
		Field<Boolean> isAbbreviation = getWordTypeExists(w1.ID, WORD_TYPE_CODE_ABBREVIATION);

		Table<Record7<Long, String, String, Boolean, Boolean, Boolean, Boolean>> wHom = create
				.select(
						w1.ID.as("word_id"),
						w1.VALUE.as("word_value"),
						w1.LANG.as("word_lang"),
						isPrefixoid.as("is_pf"),
						isSuffixoid.as("is_sf"),
						isSymbol.as("is_th"),
						isAbbreviation.as("is_l"))
				.from(w1, l1)
				.where(
						w1.LANG.in(includedLangs)
								.and(w1.IS_PUBLIC.isTrue())
								.and(l1.WORD_ID.eq(w1.ID))
								.and(l1.DATASET_CODE.notIn(DATASET_EKI, DATASET_ETY))
								.andNotExists(DSL
										.select(l2.ID)
										.from(l2)
										.where(
												l2.WORD_ID.eq(w1.ID)
														.and(l2.DATASET_CODE.eq(DATASET_EKI)))))
				.asTable("w_hom");

		Table<Record7<Long, String, String, Boolean, Boolean, Boolean, Boolean>> wEki = create
				.select(
						w1.ID.as("word_id"),
						w1.VALUE.as("word_value"),
						w1.LANG.as("word_lang"),
						isPrefixoid.as("is_pf"),
						isSuffixoid.as("is_sf"),
						isSymbol.as("is_th"),
						isAbbreviation.as("is_l"))
				.from(w1)
				.where(
						w1.LANG.in(includedLangs)
								.and(w1.IS_PUBLIC.isTrue())
								.andExists(DSL
										.select(l1.ID)
										.from(l1)
										.where(
												l1.WORD_ID.eq(w1.ID)
														.and(l1.DATASET_CODE.eq(DATASET_EKI))
														.and(l1.IS_PUBLIC.isTrue())))
								.andNotExists(DSL
										.select(w2.ID)
										.from(w2)
										.where(
												w2.VALUE.eq(w1.VALUE)
														.and(w2.IS_PUBLIC.isTrue())
														.and(w2.LANG.eq(w1.LANG))
														.and(w2.ID.ne(w1.ID))
														.andExists(DSL
																.select(l2.ID)
																.from(l2)
																.where(
																		l2.WORD_ID.eq(w2.ID)
																				.and(l2.DATASET_CODE.eq(DATASET_EKI))
																				.and(l2.IS_PUBLIC.isTrue()))))))
				.asTable("w_eki");

		return create
				.select(
						wHom.field("word_id", Long.class).as("source_id"),
						wEki.field("word_id", Long.class).as("target_id"))
				.from(wHom, wEki)
				.where(
						wHom.field("word_value", String.class).eq(wEki.field("word_value", String.class))
								.and(wHom.field("word_lang", String.class).eq(wEki.field("word_lang", String.class)))
								.and(wHom.field("word_id", Long.class).ne(wEki.field("word_id", Long.class)))
								.and(wHom.field("is_pf", Boolean.class).eq(wEki.field("is_pf", Boolean.class)))
								.and(wHom.field("is_sf", Boolean.class).eq(wEki.field("is_sf", Boolean.class)))
								.and(wHom.field("is_th", Boolean.class).eq(wEki.field("is_th", Boolean.class)))
								.and(wHom.field("is_l", Boolean.class).eq(wEki.field("is_l", Boolean.class))))
				.groupBy(wHom.field("word_id", Long.class), (wEki.field("word_id", Long.class)))
				.orderBy(wHom.field("word_id", Long.class), (wEki.field("word_id", Long.class)))
				.fetchInto(SourceTargetIdTuple.class);
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
				.andExists(DSL.select(DSL.field(IGNORE_QUERY_LOG)))
				.execute();
	}

	public int deleteFloatingForms() {

		return create
				.delete(FORM)
				.whereNotExists(DSL
						.select(PARADIGM_FORM.ID)
						.from(PARADIGM_FORM)
						.where(PARADIGM_FORM.FORM_ID.eq(FORM.ID)))
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
