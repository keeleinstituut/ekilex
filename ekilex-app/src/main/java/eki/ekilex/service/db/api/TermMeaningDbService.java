package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionDataset;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningForum;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordWordType;

@Component
public class TermMeaningDbService {

	@Autowired
	private DSLContext create;

	public TermMeaning getTermMeaning(Long meaningId, String datasetCode) {

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");
		Freeform ff = FREEFORM.as("ff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		MeaningDomain md = MEANING_DOMAIN.as("md");
		MeaningForum mfor = MEANING_FORUM.as("mfor");
		WordWordType wwt = WORD_WORD_TYPE.as("wwt");

		Field<JSON> df = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("definitionId").value(d.ID),
										DSL.key("value").value(d.VALUE),
										DSL.key("lang").value(d.LANG),
										DSL.key("definitionTypeCode").value(d.DEFINITION_TYPE_CODE)))
						.orderBy(d.ORDER_BY))
				.from(d)
				.where(
						d.MEANING_ID.eq(m.ID)
								.andExists(DSL
										.select(dd.DEFINITION_ID)
										.from(dd)
										.where(
												dd.DEFINITION_ID.eq(d.ID)
														.and(dd.DATASET_CODE.eq(datasetCode)))))
				.asField();

		Field<JSON> lnf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ff.ID),
										DSL.key("value").value(ff.VALUE_TEXT),
										DSL.key("lang").value(ff.LANG),
										DSL.key("publicity").value(ff.IS_PUBLIC)))
						.orderBy(ff.ORDER_BY))
				.from(ff, lff)
				.where(
						lff.LEXEME_ID.eq(l.ID)
								.and(lff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.NOTE.name())))
				.asField();

		Field<JSON> uf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ff.ID),
										DSL.key("value").value(ff.VALUE_TEXT),
										DSL.key("lang").value(ff.LANG),
										DSL.key("publicity").value(ff.IS_PUBLIC)))
						.orderBy(ff.ORDER_BY))
				.from(ff, lff)
				.where(
						lff.LEXEME_ID.eq(l.ID)
								.and(lff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.USAGE.name())))
				.asField();

		Field<JSON> wwtf = DSL
				.select(DSL
						.jsonArrayAgg(wwt.WORD_TYPE_CODE)
						.orderBy(wwt.ORDER_BY))
				.from(wwt)
				.where(wwt.WORD_ID.eq(w.ID))
				.asField();

		Field<JSON> wf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(w.ID),
										DSL.key("lexemeId").value(l.ID),
										DSL.key("value").value(w.VALUE),
										DSL.key("lang").value(w.LANG),
										DSL.key("wordTypeCodes").value(wwtf),
										DSL.key("lexemeValueStateCode").value(l.VALUE_STATE_CODE),
										DSL.key("lexemeNotes").value(lnf),
										DSL.key("lexemePublicity").value(l.IS_PUBLIC),
										DSL.key("usages").value(uf)))
						.orderBy(l.ORDER_BY))
				.from(w, l)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(l.WORD_ID.eq(w.ID))
								.and(l.DATASET_CODE.eq(datasetCode)))
				.asField();

		Field<JSON> mdf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("code").value(md.DOMAIN_CODE),
										DSL.key("origin").value(md.DOMAIN_ORIGIN)))
						.orderBy(md.ORDER_BY))
				.from(md)
				.where(md.MEANING_ID.eq(m.ID))
				.asField();

		Field<JSON> mnf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ff.ID),
										DSL.key("value").value(ff.VALUE_TEXT),
										DSL.key("lang").value(ff.LANG),
										DSL.key("publicity").value(ff.IS_PUBLIC)))
						.orderBy(ff.ORDER_BY))
				.from(ff, mff)
				.where(
						mff.MEANING_ID.eq(m.ID)
								.and(mff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.NOTE.name())))
				.asField();

		Field<JSON> mforf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(mfor.ID),
										DSL.key("value").value(mfor.VALUE)))
						.orderBy(mfor.ORDER_BY))
				.from(mfor)
				.where(mfor.MEANING_ID.eq(m.ID))
				.asField();


		return create
				.select(
						m.ID.as("meaning_id"),
						DSL.val(datasetCode).as("dataset_code"),
						df.as("definitions"),
						mdf.as("domains"),
						mnf.as("notes"),
						mforf.as("forums"),
						wf.as("words"))
				.from(m)
				.where(m.ID.eq(meaningId))
				.fetchOptionalInto(TermMeaning.class)
				.orElse(null);
	}
}
