package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING_TAG;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.USAGE;
import static eki.ekilex.data.db.Tables.USAGE_SOURCE_LINK;
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
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeNote;
import eki.ekilex.data.db.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningForum;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningNote;
import eki.ekilex.data.db.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.tables.MeaningTag;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.Usage;
import eki.ekilex.data.db.tables.UsageSourceLink;
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
		MeaningNote mn = MEANING_NOTE.as("mn");
		MeaningNoteSourceLink mnsl = MEANING_NOTE_SOURCE_LINK.as("mnsl");
		MeaningTag mt = MEANING_TAG.as("mt");
		Lexeme l = LEXEME.as("l");
		Usage u = USAGE.as("u");
		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		LexemeNote ln = LEXEME_NOTE.as("ln");
		LexemeNoteSourceLink lnsl = LEXEME_NOTE_SOURCE_LINK.as("lnsl");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		LexemeTag lt = LEXEME_TAG.as("lt");
		Word w = WORD.as("w");
		WordWordType wwt = WORD_WORD_TYPE.as("wwt");
		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = SOURCE.as("s");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		Freeform ff = FREEFORM.as("ff");

		Field<JSON> lslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(lsl.ID),
										DSL.key("type").value(lsl.TYPE),
										DSL.key("name").value(lsl.NAME),
										DSL.key("sourceId").value(lsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME)))
						.orderBy(lsl.ORDER_BY))
				.from(lsl, s)
				.where(
						lsl.LEXEME_ID.eq(l.ID)
								.and(lsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> dslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(dsl.ID),
										DSL.key("type").value(dsl.TYPE),
										DSL.key("name").value(dsl.NAME),
										DSL.key("sourceId").value(dsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME)))
						.orderBy(dsl.ORDER_BY))
				.from(dsl, s)
				.where(
						dsl.DEFINITION_ID.eq(d.ID)
								.and(dsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> df = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(d.ID),
										DSL.key("value").value(d.VALUE),
										DSL.key("valuePrese").value(d.VALUE_PRESE),
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

		Field<JSON> lnslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(lnsl.ID),
										DSL.key("type").value(lnsl.TYPE),
										DSL.key("name").value(lnsl.NAME),
										DSL.key("sourceId").value(lnsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME)))
						.orderBy(lnsl.ORDER_BY))
				.from(lnsl, s)
				.where(
						lnsl.LEXEME_NOTE_ID.eq(ln.ID)
								.and(mnsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> lnf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ln.ID),
										DSL.key("lexemeId").value(ln.LEXEME_ID),
										DSL.key("value").value(ln.VALUE),
										DSL.key("valuePrese").value(ln.VALUE_PRESE),
										DSL.key("lang").value(ln.LANG),
										DSL.key("complexity").value(ln.COMPLEXITY),
										DSL.key("createdOn").value(ln.CREATED_ON),
										DSL.key("createdBy").value(ln.CREATED_BY),
										DSL.key("modifiedOn").value(ln.MODIFIED_ON),
										DSL.key("modifiedBy").value(ln.MODIFIED_BY),
										DSL.key("orderBy").value(ln.ORDER_BY),
										DSL.key("sourceLinks").value(lnslf)))
						.orderBy(ln.ORDER_BY))
				.from(ln)
				.where(ln.LEXEME_ID.eq(l.ID))
				.asField();

		Field<JSON> uslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(usl.ID),
										DSL.key("type").value(usl.TYPE),
										DSL.key("name").value(usl.NAME),
										DSL.key("sourceId").value(usl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME)))
						.orderBy(usl.ORDER_BY))
				.from(usl, s)
				.where(
						usl.USAGE_ID.eq(u.ID)
								.and(usl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> uf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(u.ID),
										DSL.key("lexemeId").value(u.LEXEME_ID),
										DSL.key("value").value(u.VALUE),
										DSL.key("valuePrese").value(u.VALUE_PRESE),
										DSL.key("lang").value(u.LANG),
										DSL.key("complexity").value(u.COMPLEXITY),
										DSL.key("createdOn").value(u.CREATED_ON),
										DSL.key("createdBy").value(u.CREATED_BY),
										DSL.key("modifiedOn").value(u.MODIFIED_ON),
										DSL.key("modifiedBy").value(u.MODIFIED_BY),
										DSL.key("orderBy").value(u.ORDER_BY),
										DSL.key("sourceLinks").value(uslf)))
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(l.ID))
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

		Field<JSON> mnslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(mnsl.ID),
										DSL.key("type").value(mnsl.TYPE),
										DSL.key("name").value(mnsl.NAME),
										DSL.key("sourceId").value(mnsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME)))
						.orderBy(mnsl.ORDER_BY))
				.from(mnsl, s)
				.where(
						mnsl.MEANING_NOTE_ID.eq(mn.ID)
								.and(mnsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> mnf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(mn.ID),
										DSL.key("meaningId").value(mn.MEANING_ID),
										DSL.key("value").value(mn.VALUE),
										DSL.key("valuePrese").value(mn.VALUE_PRESE),
										DSL.key("lang").value(mn.LANG),
										DSL.key("complexity").value(mn.COMPLEXITY),
										DSL.key("createdOn").value(mn.CREATED_ON),
										DSL.key("createdBy").value(mn.CREATED_BY),
										DSL.key("modifiedOn").value(mn.MODIFIED_ON),
										DSL.key("modifiedBy").value(mn.MODIFIED_BY),
										DSL.key("orderBy").value(mn.ORDER_BY),
										DSL.key("sourceLinks").value(mnslf)))
						.orderBy(mn.ORDER_BY))
				.from(mn)
				.where(mn.MEANING_ID.eq(m.ID))
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
						mdf.as("domains"),
						df.as("definitions"),
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
