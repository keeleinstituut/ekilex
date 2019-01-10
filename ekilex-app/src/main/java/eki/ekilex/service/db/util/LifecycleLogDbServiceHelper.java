package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.MeaningFreeform;

@Component
public class LifecycleLogDbServiceHelper {

	public Map<String, Object> getFirstDepthFreeformData(DSLContext create, Long entityId, FreeformType freeformType) {
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						lff.LEXEME_ID,
						ff1.VALUE_TEXT,
						ff1.VALUE_PRESE
						)
				.from(lff, ff1)
				.where(
						lff.FREEFORM_ID.eq(ff1.ID)
						.and(ff1.ID.eq(entityId))
						.and(ff1.TYPE.eq(freeformType.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getSecondDepthFreeformData(DSLContext create, Long entityId, FreeformType freeformType) {
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff1 = FREEFORM.as("ff1");
		Freeform ff2 = FREEFORM.as("ff2");
		Map<String, Object> result = create
				.select(
						lff.LEXEME_ID,
						ff2.VALUE_TEXT,
						ff2.VALUE_PRESE
						)
				.from(lff, ff1, ff2)
				.where(
						lff.FREEFORM_ID.eq(ff1.ID)
						.and(ff2.PARENT_ID.eq(ff1.ID))
						.and(ff2.ID.eq(entityId))
						.and(ff2.TYPE.eq(freeformType.name()))
						)
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getWordData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.selectDistinct(
						WORD.GENDER_CODE,
						WORD.TYPE_CODE,
						WORD.ASPECT_CODE,
						FORM.VALUE,
						FORM.VALUE_PRESE
						)
				.from(WORD, PARADIGM, FORM)
				.where(WORD.ID.eq(entityId)
						.and(PARADIGM.WORD_ID.eq(entityId))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						LEXEME.FREQUENCY_GROUP,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.LEVEL3,
						LEXEME.VALUE_STATE_CODE,
						LEXEME.PROCESS_STATE_CODE
						)
				.from(LEXEME)
				.where(LEXEME.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeSourceLinkData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						LEXEME_SOURCE_LINK.LEXEME_ID,
						LEXEME_SOURCE_LINK.VALUE
						)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						DEFINITION.MEANING_ID,
						DEFINITION.VALUE,
						DEFINITION.VALUE_PRESE,
						DEFINITION.ORDER_BY
						)
				.from(DEFINITION)
				.where(DEFINITION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionSourceLinkData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						DEFINITION.MEANING_ID,
						DEFINITION_SOURCE_LINK.VALUE
						)
				.from(DEFINITION, DEFINITION_SOURCE_LINK)
				.where(
						DEFINITION.ID.eq(DEFINITION_SOURCE_LINK.DEFINITION_ID)
						.and(DEFINITION_SOURCE_LINK.ID.eq(entityId)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getFreeformSourceLinkData(DSLContext create, Long entityId) {
		Freeform ff = FREEFORM.as("ff");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");
		Table<Record2<Long, Long>> dff = DSL
				.select(DEFINITION.MEANING_ID, DEFINITION_FREEFORM.FREEFORM_ID)
				.from(DEFINITION, DEFINITION_FREEFORM)
				.where(DEFINITION_FREEFORM.DEFINITION_ID.eq(DEFINITION.ID))
				.asTable("dff");
		Map<String, Object> result = create
				.select(
						ffsl.VALUE,
						lff.LEXEME_ID,
						dff.field("meaning_id", Long.class).as("definition_meaning_id"),
						mff.MEANING_ID
						)
				.from(ffsl
						.innerJoin(ff).on(ff.ID.eq(ffsl.FREEFORM_ID))
						.leftOuterJoin(lff).on(lff.FREEFORM_ID.eq(ffsl.FREEFORM_ID))
						.leftOuterJoin(dff).on(dff.field("freeform_id", Long.class).eq(ff.ID))
						.leftOuterJoin(mff).on(mff.FREEFORM_ID.eq(ff.ID))
						)
				.where(ffsl.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeRelationData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						LEX_RELATION.LEXEME1_ID,
						LEX_RELATION.LEX_REL_TYPE_CODE,
						LEX_RELATION.ORDER_BY,
						LEXEME.WORD_ID.as("word2_id")
						)
				.from(LEX_RELATION).join(LEXEME).on(LEXEME.ID.eq(LEX_RELATION.LEXEME2_ID))
				.where(LEX_RELATION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getMeaningRelationData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						MEANING_RELATION.MEANING1_ID,
						MEANING_RELATION.MEANING2_ID,
						MEANING_RELATION.MEANING_REL_TYPE_CODE,
						MEANING_RELATION.ORDER_BY
						)
				.from(MEANING_RELATION)
				.where(MEANING_RELATION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getWordRelationData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						WORD_RELATION.WORD1_ID,
						WORD_RELATION.WORD2_ID,
						WORD_RELATION.WORD_REL_TYPE_CODE,
						WORD_RELATION.ORDER_BY
						)
				.from(WORD_RELATION)
				.where(WORD_RELATION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getWordRelationGroupMember(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						WORD_GROUP_MEMBER.WORD_ID
				)
				.from(WORD_GROUP_MEMBER)
				.where(WORD_GROUP_MEMBER.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public List<String> getMeaningWords(DSLContext create, Long entityId) {
		List<String> result = create
				.select(FORM.VALUE)
				.from(LEXEME)
				.join(PARADIGM).on(PARADIGM.WORD_ID.eq(LEXEME.WORD_ID))
				.join(FORM).on(FORM.PARADIGM_ID.eq(PARADIGM.ID))
				.where(
						LEXEME.MEANING_ID.eq(entityId)
						.and(FORM.MODE.eq(FormMode.WORD.name())))
				.fetch(0, String.class);
		return result;
	}

}
