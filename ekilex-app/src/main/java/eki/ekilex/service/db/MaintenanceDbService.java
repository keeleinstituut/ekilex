package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Public.PUBLIC;
import static eki.ekilex.data.db.main.Tables.DATA_REQUEST;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.PUBLISHING;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_FREEFORM;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.SourceTargetIdTuple;
import eki.ekilex.data.db.main.Routines;
import eki.ekilex.data.db.main.tables.DataRequest;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Publishing;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.records.WordRecord;

@Component
public class MaintenanceDbService extends AbstractDataDbService {

	@Autowired
	private DSLContext mainDb;

	public List<WordRecord> getWordRecords() {
		return mainDb
				.selectFrom(WORD)
				.where(WORD.IS_PUBLIC.isTrue())
				.fetchInto(WordRecord.class);
	}

	public List<SourceTargetIdTuple> getHomonymsToJoin(String[] includedLangs) {

		Word w1 = WORD.as("w1");
		Word w2 = WORD.as("w2");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");

		Field<Boolean> isPrefixoid = queryHelper.getWordTypeExists(w1.ID, WORD_TYPE_CODE_PREFIXOID);
		Field<Boolean> isSuffixoid = queryHelper.getWordTypeExists(w1.ID, WORD_TYPE_CODE_SUFFIXOID);
		Field<Boolean> isSymbol = queryHelper.getWordTypeExists(w1.ID, WORD_TYPE_CODE_SYMBOL);
		Field<Boolean> isAbbreviation = queryHelper.getWordTypeExists(w1.ID, WORD_TYPE_CODE_ABBREVIATION);

		Table<Record7<Long, String, String, Boolean, Boolean, Boolean, Boolean>> wHom = mainDb
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

		Table<Record7<Long, String, String, Boolean, Boolean, Boolean, Boolean>> wEki = mainDb
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
										.select(l1.ID, DSL.val(IGNORE_QUERY_LOG))
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

		return mainDb
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

		Routines.adjustHomonymNrs(mainDb.configuration());
	}

	public int deleteFloatingFreeforms() {

		return mainDb
				.delete(FREEFORM)
				.where(FREEFORM.PARENT_ID.isNull())
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
						.select(WORD_FREEFORM.ID)
						.from(WORD_FREEFORM)
						.where(WORD_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.execute();
	}

	public int deleteFloatingMeanings() {

		return mainDb
				.delete(MEANING)
				.whereNotExists(DSL
						.select(LEXEME.ID, DSL.value(IGNORE_QUERY_LOG))
						.from(LEXEME)
						.where(LEXEME.MEANING_ID.eq(MEANING.ID)))
				.andNotExists(DSL
						.select(DEFINITION.ID)
						.from(DEFINITION)
						.where(DEFINITION.MEANING_ID.eq(MEANING.ID)))
				.execute();
	}

	public int deleteFloatingWords() {

		return mainDb
				.delete(WORD)
				.whereNotExists(DSL
						.select(LEXEME.ID, DSL.value(IGNORE_QUERY_LOG))
						.from(LEXEME)
						.where(LEXEME.WORD_ID.eq(WORD.ID)))
				.execute();
	}

	public int deleteFloatingForms() {

		return mainDb
				.delete(FORM)
				.whereNotExists(DSL
						.select(PARADIGM_FORM.ID, DSL.value(IGNORE_QUERY_LOG))
						.from(PARADIGM_FORM)
						.where(PARADIGM_FORM.FORM_ID.eq(FORM.ID)))
				.execute();
	}

	public int deleteFloatingPublishing(String[] entityNames) {

		Publishing p = PUBLISHING.as("p");

		int totalDeleteCount = 0;

		for (String entityName : entityNames) {

			Table<?> e = PUBLIC.getTable(entityName).as("e");
			int entityDeleteCount = mainDb
					.deleteFrom(p)
					.where(
							p.ENTITY_NAME.eq(entityName)
									.andNotExists(DSL
											.select(e.field("id"))
											.from(e)
											.where(e.field("id", Long.class).eq(p.ENTITY_ID)))

					)
					.execute();
			totalDeleteCount = totalDeleteCount + entityDeleteCount;
		}
		return totalDeleteCount;
	}

	public int deleteAccessedDataRequests(int hours) {

		DataRequest dr = DATA_REQUEST.as("dr");
		return mainDb
				.delete(dr)
				.where(
						dr.ACCESSED.isNotNull()
								.and(DSL.condition("(current_timestamp - dr.accessed) >= (interval '" + hours + " hour')")))
				.execute();
	}
}
