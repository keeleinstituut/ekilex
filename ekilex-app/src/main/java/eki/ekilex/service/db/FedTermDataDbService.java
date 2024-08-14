package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.USAGE;
import static eki.ekilex.data.db.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.MeaningLexemeWordTuple;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeNote;
import eki.ekilex.data.db.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.Usage;
import eki.ekilex.data.db.tables.UsageSourceLink;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.udt.records.TypeValueNameLangRecord;

@Component
public class FedTermDataDbService {

	@Autowired
	protected DSLContext create;

	public int getMeaningCount(String datasetCode) {

		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");

		return create
				.fetchCount(DSL
						.select(m.ID)
						.from(m)
						.whereExists(DSL
								.select(l.ID)
								.from(l)
								.where(
										l.MEANING_ID.eq(m.ID)
												.and(l.DATASET_CODE.eq(datasetCode)))));
	}

	public List<Long> getMeaningIds(String datasetCode, int meaningOffset, int meaningLimit) {

		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");

		return create
				.select(m.ID)
				.from(m)
				.whereExists(DSL
						.select(l.ID)
						.from(l)
						.where(
								l.MEANING_ID.eq(m.ID)
										.and(l.DATASET_CODE.eq(datasetCode))))
				.orderBy(m.ID)
				.offset(meaningOffset)
				.limit(meaningLimit)
				.fetchInto(Long.class);
	}

	public List<MeaningLexemeWordTuple> getMeaningLexemeWordTuples(String datasetCode, List<Long> meaningIds) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Definition d = DEFINITION.as("d");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		WordWordType wt = WORD_WORD_TYPE.as("wt");
		LexemePos lp = LEXEME_POS.as("lp");
		LexemeNote ln = LEXEME_NOTE.as("ln");
		LexemeNoteSourceLink lnsl = LEXEME_NOTE_SOURCE_LINK.as("lnsl");
		Usage u = USAGE.as("u");
		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		Source s = SOURCE.as("s");

		Field<String> wtf = DSL
				.select(wt.WORD_TYPE_CODE)
				.from(wt)
				.where(wt.WORD_ID.eq(w.ID))
				.orderBy(wt.ORDER_BY)
				.limit(1)
				.asField();

		Field<String> lpf = DSL
				.select(lp.POS_CODE)
				.from(lp)
				.where(lp.LEXEME_ID.eq(l.ID))
				.orderBy(lp.ORDER_BY)
				.limit(1)
				.asField();

		Field<TypeValueNameLangRecord[]> dasf = DSL
				.select(
						DSL.field(
								"array_agg("
										+ "row(d.id, d.value, s.name, d.lang)::type_value_name_lang "
										+ "order by d.order_by, dsl.order_by)",
								TypeValueNameLangRecord[].class))
				.from(d
						.leftOuterJoin(dsl).on(dsl.DEFINITION_ID.eq(d.ID))
						.leftOuterJoin(s).on(s.ID.eq(dsl.SOURCE_ID)))
				.where(
						d.MEANING_ID.eq(m.ID)
								.and(d.IS_PUBLIC.isTrue()))
				.groupBy(d.MEANING_ID)
				.asField();

		Field<TypeValueNameLangRecord[]> lnasf = DSL
				.select(
						DSL.field(
								"array_agg("
										+ "row(ln.id, ln.value, s.name, ln.lang)::type_value_name_lang "
										+ "order by ln.order_by, lnsl.order_by)",
								TypeValueNameLangRecord[].class))
				.from(
						ln
								.leftOuterJoin(lnsl).on(lnsl.LEXEME_NOTE_ID.eq(ln.ID))
								.leftOuterJoin(s).on(s.ID.eq(lnsl.SOURCE_ID)))
				.where(
						ln.LEXEME_ID.eq(l.ID)
								.and(ln.IS_PUBLIC.isTrue()))
				.asField();

		Field<TypeValueNameLangRecord[]> luasf = DSL
				.select(
						DSL.field(
								"array_agg("
										+ "row(u.id, u.value, s.name, u.lang)::type_value_name_lang "
										+ "order by u.order_by, usl.order_by)",
								TypeValueNameLangRecord[].class))
				.from(
						u
								.leftOuterJoin(usl).on(usl.USAGE_ID.eq(u.ID))
								.leftOuterJoin(s).on(s.ID.eq(lnsl.SOURCE_ID)))
				.where(
						u.LEXEME_ID.eq(l.ID)
								.and(u.IS_PUBLIC.isTrue()))
				.asField();

		return create
				.select(
						l.WORD_ID,
						l.ID.as("lexeme_id"),
						l.MEANING_ID,
						l.DATASET_CODE,
						w.VALUE.as("word_value"),
						w.LANG.as("word_language_code"),
						w.GENDER_CODE.as("word_gender_code"),
						w.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
						wtf.as("word_type_code"),
						dasf.as("definition_values_and_source_names"),
						l.IS_PUBLIC.as("lexeme_is_public"),
						lpf.as("lexeme_pos_code"),
						l.VALUE_STATE_CODE.as("lexeme_value_state_code"),
						lnasf.as("lexeme_note_values_and_source_names"),
						luasf.as("lexeme_usage_values_and_source_names"))
				.from(w, l, m)
				.where(
						l.DATASET_CODE.eq(datasetCode)
								.and(l.MEANING_ID.in(meaningIds))
								.and(l.WORD_ID.eq(w.ID))
								.and(w.IS_PUBLIC.isTrue())
								.and(w.IS_WORD.isTrue())
								.and(l.MEANING_ID.eq(m.ID)))
				.orderBy(w.VALUE, l.ORDER_BY)
				.fetchInto(MeaningLexemeWordTuple.class);
	}
}
