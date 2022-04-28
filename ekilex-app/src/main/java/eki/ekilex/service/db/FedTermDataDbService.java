package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.MeaningLexemeWordTuple;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.SourceFreeform;
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

	public List<MeaningLexemeWordTuple> getMeaningLexemeWordTuples(String datasetCode) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Definition d = DEFINITION.as("d");
		WordWordType wt = WORD_WORD_TYPE.as("wt");
		LexemePos lp = LEXEME_POS.as("lp");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		Freeform ln = FREEFORM.as("ln");
		Freeform lu = FREEFORM.as("lu");
		Freeform sn = FREEFORM.as("sn");
		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");

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
										+ "row(d.id, d.value, sn.value_text, d.lang)::type_value_name_lang "
										+ "order by d.order_by, sn.order_by)",
								TypeValueNameLangRecord[].class))
				.from(d
						.leftOuterJoin(dsl).on(dsl.DEFINITION_ID.eq(d.ID))
						.leftOuterJoin(sff).on(sff.SOURCE_ID.eq(dsl.SOURCE_ID))
						.leftOuterJoin(sn).on(sn.ID.eq(sff.FREEFORM_ID).and(sn.TYPE.eq(FreeformType.SOURCE_NAME.name()))))
				.where(
						d.MEANING_ID.eq(m.ID)
								.and(d.IS_PUBLIC.isTrue()))
				.groupBy(d.MEANING_ID)
				.asField();

		Field<TypeValueNameLangRecord[]> lnasf = DSL
				.select(
						DSL.field(
								"array_agg("
										+ "row(ln.id, ln.value_text, sn.value_text, ln.lang)::type_value_name_lang "
										+ "order by ln.order_by, sn.order_by)",
								TypeValueNameLangRecord[].class))
				.from(lff
						.innerJoin(ln).on(ln.ID.eq(lff.FREEFORM_ID).and(ln.TYPE.eq(FreeformType.NOTE.name())).and(ln.IS_PUBLIC.isTrue()))
						.leftOuterJoin(ffsl).on(ffsl.FREEFORM_ID.eq(ln.ID))
						.leftOuterJoin(sff).on(sff.SOURCE_ID.eq(ffsl.SOURCE_ID))
						.leftOuterJoin(sn).on(sn.ID.eq(sff.FREEFORM_ID).and(sn.TYPE.eq(FreeformType.SOURCE_NAME.name()))))
				.where(lff.LEXEME_ID.eq(l.ID))
				.asField();

		Field<TypeValueNameLangRecord[]> luasf = DSL
				.select(
						DSL.field(
								"array_agg("
										+ "row(lu.id, lu.value_text, sn.value_text, lu.lang)::type_value_name_lang "
										+ "order by lu.order_by, sn.order_by)",
								TypeValueNameLangRecord[].class))
				.from(lff
						.innerJoin(lu).on(lu.ID.eq(lff.FREEFORM_ID).and(lu.TYPE.eq(FreeformType.USAGE.name())).and(lu.IS_PUBLIC.isTrue()))
						.leftOuterJoin(ffsl).on(ffsl.FREEFORM_ID.eq(lu.ID))
						.leftOuterJoin(sff).on(sff.SOURCE_ID.eq(ffsl.SOURCE_ID))
						.leftOuterJoin(sn).on(sn.ID.eq(sff.FREEFORM_ID).and(sn.TYPE.eq(FreeformType.SOURCE_NAME.name()))))
				.where(lff.LEXEME_ID.eq(l.ID))
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
								//TODO very much temporary
								/*
								l.ID.in(DSL.field(
										"select "
												+ "l.id "
												+ "from "
												+ "lexeme l "
												+ "where "
												+ "("
												+ "exists (select lp.id from lexeme_pos lp where lp.lexeme_id = l.id) "
												+ "and exists (select wwt.id from word_word_type wwt where l.word_id = wwt.word_id) "
												+ ") "
												+ "or ("
												+ "exists (select lf.id from lexeme_freeform lf, freeform f, freeform_source_link fsl where lf.lexeme_id = l.id and lf.freeform_id = f.id and f.\"type\" = 'NOTE' and fsl.freeform_id = f.id) "
												+ "and exists (select lf.id from lexeme_freeform lf, freeform f, freeform_source_link fsl where lf.lexeme_id = l.id and lf.freeform_id = f.id and f.\"type\" = 'USAGE' and fsl.freeform_id = f.id)"
												+ ")"))
								*/
								.and(l.WORD_ID.eq(w.ID))
								.and(l.MEANING_ID.eq(m.ID)))
				.orderBy(w.VALUE, l.ORDER_BY)
				.fetchInto(MeaningLexemeWordTuple.class);
	}
}
