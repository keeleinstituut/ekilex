package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION;

import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.LexemeFreeform;

@Component
public class LifecycleLogDbServiceHelper {

	public Map<String, Object> getFirstDepthFreeformData(DSLContext create, Long entityId, FreeformType freeformType) {
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						lff.LEXEME_ID,
						ff1.VALUE_TEXT
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
						ff2.VALUE_TEXT
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
				.select(
						WORD.GENDER_CODE
						)
				.from(WORD)
				.where(WORD.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						LEXEME.FREQUENCY_GROUP,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.LEVEL3
						)
				.from(LEXEME)
				.where(LEXEME.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						DEFINITION.MEANING_ID,
						DEFINITION.VALUE,
						DEFINITION.ORDER_BY
						)
				.from(DEFINITION)
				.where(DEFINITION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeRelationData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						LEX_RELATION.LEXEME1_ID,
						LEX_RELATION.LEX_REL_TYPE_CODE,
						LEX_RELATION.ORDER_BY
						)
				.from(LEX_RELATION)
				.where(LEX_RELATION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getMeaningRelationData(DSLContext create, Long entityId) {
		Map<String, Object> result = create
				.select(
						MEANING_RELATION.MEANING1_ID,
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
						WORD_RELATION.WORD_REL_TYPE_CODE,
						WORD_RELATION.ORDER_BY
						)
				.from(WORD_RELATION)
				.where(WORD_RELATION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}
}
