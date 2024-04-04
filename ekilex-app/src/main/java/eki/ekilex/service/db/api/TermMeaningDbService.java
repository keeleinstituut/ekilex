package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_TAG;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityFunct;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionDataset;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningForum;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningTag;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.tables.records.LexemeRecord;

@Component
public class TermMeaningDbService implements ActivityFunct, GlobalConstant {

	@Autowired
	private DSLContext create;

	public TermMeaning getTermMeaning(Long meaningId, String datasetCode) {

		Meaning m = MEANING.as("m");
		MeaningDomain md = MEANING_DOMAIN.as("md");
		MeaningForum mfor = MEANING_FORUM.as("mfor");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		MeaningTag mt = MEANING_TAG.as("mt");

		Lexeme l = LEXEME.as("l");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		LexemeTag lt = LEXEME_TAG.as("lt");

		Word w = WORD.as("w");
		WordWordType wwt = WORD_WORD_TYPE.as("wwt");

		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Freeform ff = FREEFORM.as("ff");
		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");
		Source s = SOURCE.as("s");

		Field<JSON> ffslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("sourceLinkId").value(ffsl.ID),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("sourceLinkName").value(ffsl.NAME),
										DSL.key("type").value(ffsl.TYPE)))
						.orderBy(ffsl.ORDER_BY))
				.from(ffsl, s)
				.where(
						ffsl.FREEFORM_ID.eq(ff.ID)
								.and(ffsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> dslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("sourceLinkId").value(dsl.ID),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("sourceLinkName").value(dsl.NAME),
										DSL.key("type").value(dsl.TYPE)))
						.orderBy(dsl.ORDER_BY))
				.from(dsl, s)
				.where(
						dsl.DEFINITION_ID.eq(d.ID)
								.and(dsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> lslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("sourceLinkId").value(lsl.ID),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("sourceLinkName").value(lsl.NAME),
										DSL.key("type").value(lsl.TYPE)))
						.orderBy(lsl.ORDER_BY))
				.from(lsl, s)
				.where(
						lsl.LEXEME_ID.eq(l.ID)
								.and(lsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> df = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("definitionId").value(d.ID),
										DSL.key("value").value(d.VALUE),
										DSL.key("lang").value(d.LANG),
										DSL.key("definitionTypeCode").value(d.DEFINITION_TYPE_CODE),
										DSL.key("sourceLinks").value(dslf)))
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
										DSL.key("publicity").value(ff.IS_PUBLIC),
										DSL.key("sourceLinks").value(ffslf)))
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
										DSL.key("publicity").value(ff.IS_PUBLIC),
										DSL.key("sourceLinks").value(ffslf)))
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

		Field<JSON> ltf = DSL
				.select(DSL
						.jsonArrayAgg(lt.TAG_NAME)
						.orderBy(lt.ID))
				.from(lt)
				.where(lt.LEXEME_ID.eq(l.ID))
				.asField();

		Field<JSON> wf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(w.ID),
										DSL.key("lexemeId").value(l.ID),
										DSL.key("value").value(w.VALUE),
										DSL.key("valuePrese").value(w.VALUE_PRESE),
										DSL.key("lang").value(w.LANG),
										DSL.key("wordTypeCodes").value(wwtf),
										DSL.key("lexemeValueStateCode").value(l.VALUE_STATE_CODE),
										DSL.key("lexemeNotes").value(lnf),
										DSL.key("lexemeTags").value(ltf),
										DSL.key("lexemePublicity").value(l.IS_PUBLIC),
										DSL.key("lexemeSourceLinks").value(lslf),
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
										DSL.key("publicity").value(ff.IS_PUBLIC),
										DSL.key("sourceLinks").value(ffslf)))
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

		Field<JSON> cidf = DSL
				.select(DSL
						.jsonArrayAgg(ff.VALUE_TEXT)
						.orderBy(ff.ORDER_BY))
				.from(ff, mff)
				.where(
						mff.MEANING_ID.eq(m.ID)
								.and(mff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.CONCEPT_ID.name())))
				.asField();

		Field<JSON> mtf = DSL
				.select(DSL
						.jsonArrayAgg(mt.TAG_NAME)
						.orderBy(mt.ID))
				.from(mt)
				.where(mt.MEANING_ID.eq(m.ID))
				.asField();

		return create
				.select(
						m.ID.as("meaning_id"),
						m.MANUAL_EVENT_ON.as("manual_event_on"),
						DSL.val(datasetCode).as("dataset_code"),
						df.as("definitions"),
						mdf.as("domains"),
						mnf.as("notes"),
						mforf.as("forums"),
						mtf.as("tags"),
						cidf.as("concept_ids"),
						wf.as("words"))
				.from(m)
				.where(m.ID.eq(meaningId))
				.fetchOptionalInto(TermMeaning.class)
				.orElse(null);
	}

	public LexemeRecord getLexeme(Long wordId, Long meaningId, String datasetCode) {
		return create
				.selectFrom(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.MEANING_ID.eq(meaningId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchOptional()
				.orElse(null);
	}
}
