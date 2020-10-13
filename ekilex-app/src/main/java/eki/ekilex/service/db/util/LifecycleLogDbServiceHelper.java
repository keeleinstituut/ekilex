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
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.WordFreeform;

@Component
public class LifecycleLogDbServiceHelper implements GlobalConstant {

	public Map<String, Object> getFirstDepthFreeformData(DSLContext create, Long entityId, FreeformType freeformType) {

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						lff.LEXEME_ID,
						ff1.VALUE_TEXT,
						ff1.VALUE_PRESE,
						ff1.ORDER_BY)
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
						ff2.VALUE_PRESE,
						ff2.PARENT_ID)
				.from(lff, ff1, ff2)
				.where(
						lff.FREEFORM_ID.eq(ff1.ID)
								.and(ff2.PARENT_ID.eq(ff1.ID))
								.and(ff2.ID.eq(entityId))
								.and(ff2.TYPE.eq(freeformType.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getMeaningFreeformData(DSLContext create, Long freeformId, FreeformType freeformType) {

		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						mff.MEANING_ID,
						ff1.VALUE_TEXT,
						ff1.VALUE_PRESE,
						ff1.ORDER_BY)
				.from(mff, ff1)
				.where(
						mff.FREEFORM_ID.eq(ff1.ID)
								.and(ff1.ID.eq(freeformId))
								.and(ff1.TYPE.eq(freeformType.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getSourceFreeformData(DSLContext create, Long sourceFreeformId, FreeformType freeformType) {

		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						sff.SOURCE_ID,
						ff1.VALUE_TEXT,
						ff1.VALUE_PRESE)
				.from(sff, ff1)
				.where(
						sff.FREEFORM_ID.eq(ff1.ID)
								.and(ff1.ID.eq(sourceFreeformId))
								.and(ff1.TYPE.eq(freeformType.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getWordFreeformData(DSLContext create, Long wordFreeformId, FreeformType freeformType) {

		WordFreeform wff = WORD_FREEFORM.as("wff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						wff.WORD_ID,
						ff1.VALUE_TEXT,
						ff1.VALUE_PRESE)
				.from(wff, ff1)
				.where(
						wff.FREEFORM_ID.eq(ff1.ID)
								.and(ff1.ID.eq(wordFreeformId))
								.and(ff1.TYPE.eq(freeformType.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getSourceType(DSLContext create, Long entityId) {

		Map<String, Object> result = create
				.select(SOURCE.TYPE)
				.from(SOURCE)
				.where(SOURCE.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	//TODO should there be word.morph_code, word.vocal_form?
	public Map<String, Object> getWordData(DSLContext create, Long entityId) {

		Map<String, Object> result = create
				.selectDistinct(
						WORD.GENDER_CODE,
						WORD.ASPECT_CODE,
						WORD.LANG,
						DSL.field("array_to_string(array_agg(distinct form.value), ',', '*')").cast(String.class).as("value"),
						DSL.field("array_to_string(array_agg(distinct form.value_prese), ',', '*')").cast(String.class).as("value_prese"),
						DSL.field("array_to_string(array_agg(distinct form.vocal_form), ',')").cast(String.class).as("vocal_form"),
						DSL.field("array_to_string(array_agg(distinct form.morph_code), ',')").cast(String.class).as("morph_code"))
				.from(WORD, PARADIGM, FORM)
				.where(
						WORD.ID.eq(entityId)
								.and(PARADIGM.WORD_ID.eq(entityId))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.groupBy(WORD.ID)
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeData(DSLContext create, Long lexemeId) {

		Map<String, Object> result = create
				.select(
						LEXEME.FREQUENCY_GROUP_CODE,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.VALUE_STATE_CODE,
						LEXEME.IS_PUBLIC,
						LEXEME.COMPLEXITY,
						LEXEME.WEIGHT,
						LEXEME.ORDER_BY,
						WORD.VALUE)
				.from(LEXEME, WORD)
				.where(LEXEME.ID.eq(lexemeId).and(LEXEME.WORD_ID.eq(WORD.ID)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeUsageData(DSLContext create, Long entityId) {

		Freeform f1 = FREEFORM.as("f1");
		Map<String, Object> result = create
				.select(DSL.field("array_to_string(array_agg(f1.value_text), ' ', '*')").cast(String.class).as("value_text"))
				.from(LEXEME_FREEFORM, f1)
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(entityId)
								.and(f1.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(f1.TYPE.eq(FreeformType.USAGE.name())))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeSourceLinkData(DSLContext create, Long sourceLinkId) {

		Map<String, Object> result = create
				.select(
						LEXEME_SOURCE_LINK.LEXEME_ID,
						LEXEME_SOURCE_LINK.VALUE)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionData(DSLContext create, Long definitionId) {

		Map<String, Object> result = create
				.select(
						DEFINITION.MEANING_ID,
						DEFINITION.VALUE,
						DEFINITION.VALUE_PRESE,
						DEFINITION.ORDER_BY)
				.from(DEFINITION)
				.where(DEFINITION.ID.eq(definitionId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionSourceLinkData(DSLContext create, Long sourceLinkId) {

		Map<String, Object> result = create
				.select(
						DEFINITION.MEANING_ID,
						DEFINITION_SOURCE_LINK.VALUE,
						DEFINITION_SOURCE_LINK.DEFINITION_ID)
				.from(DEFINITION, DEFINITION_SOURCE_LINK)
				.where(
						DEFINITION.ID.eq(DEFINITION_SOURCE_LINK.DEFINITION_ID)
								.and(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getUsageSourceLinkData(DSLContext create, Long sourceLinkId) {

		Map<String, Object> result = create
				.select(LEXEME_FREEFORM.LEXEME_ID,
						FREEFORM_SOURCE_LINK.VALUE,
						FREEFORM_SOURCE_LINK.FREEFORM_ID.as("usage_id"))
				.from(LEXEME_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(
						LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID)
								.and(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionFreeformData(DSLContext create, Long entityId, FreeformType freeformType) {

		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");
		Freeform ff1 = FREEFORM.as("ff1");
		Map<String, Object> result = create
				.select(
						dff.DEFINITION_ID,
						ff1.VALUE_TEXT,
						ff1.VALUE_PRESE,
						ff1.ORDER_BY,
						DEFINITION.MEANING_ID)
				.from(dff, ff1, DEFINITION)
				.where(
						dff.FREEFORM_ID.eq(ff1.ID)
								.and(ff1.ID.eq(entityId))
								.and(ff1.TYPE.eq(freeformType.name()))
								.and(DEFINITION.ID.eq(dff.DEFINITION_ID)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeFreeformSourceLinkData(DSLContext create, Long sourceLinkId) {

		Map<String, Object> result = create
				.select(
						LEXEME_FREEFORM.LEXEME_ID,
						FREEFORM_SOURCE_LINK.VALUE)
				.from(LEXEME_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(
						LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID)
								.and(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getMeaningFreeformSourceLinkData(DSLContext create, Long sourceLinkId) {

		Map<String, Object> result = create
				.select(
						MEANING_FREEFORM.MEANING_ID,
						FREEFORM_SOURCE_LINK.VALUE)
				.from(MEANING_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(
						MEANING_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID)
								.and(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getDefinitionFreeformSourceLinkData(DSLContext create, Long sourceLinkId) {

		Map<String, Object> result = create
				.select(
						FREEFORM_SOURCE_LINK.VALUE,
						DEFINITION_FREEFORM.DEFINITION_ID,
						DEFINITION.MEANING_ID)
				.from(DEFINITION, DEFINITION_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(
						DEFINITION.ID.eq(DEFINITION_FREEFORM.DEFINITION_ID)
								.and(DEFINITION_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID))
								.and(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId)))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeRelationData(DSLContext create, Long entityId) {

		Map<String, Object> result = create
				.select(
						LEX_RELATION.LEXEME1_ID,
						LEX_RELATION.LEX_REL_TYPE_CODE,
						LEX_RELATION.ORDER_BY,
						LEXEME.WORD_ID.as("word2_id"))
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
						MEANING_RELATION.ORDER_BY)
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
						WORD_RELATION.RELATION_STATUS,
						WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION)
				.where(WORD_RELATION.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getWordRelationGroupMember(DSLContext create, Long entityId) {

		Map<String, Object> result = create
				.select(WORD_GROUP_MEMBER.WORD_ID)
				.from(WORD_GROUP_MEMBER)
				.where(WORD_GROUP_MEMBER.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public List<String> getMeaningWords(DSLContext create, Long entityId) {

		List<String> result = create
				.select(WORD.VALUE)
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(entityId)
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(LEXEME.WORD_ID.eq(WORD.ID)))
				.fetchInto(String.class);
		return result;
	}

	public Map<String, Object> getMeaningDomainData(DSLContext create, Long entityId) {

		Map<String, Object> result = create
				.select(
						MEANING_DOMAIN.MEANING_ID,
						MEANING_DOMAIN.DOMAIN_CODE,
						MEANING_DOMAIN.ORDER_BY)
				.from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.ID.eq(entityId))
				.fetchSingleMap();
		return result;
	}

	public Map<String, Object> getLexemeFreeformData(DSLContext create, Long freeformId) {

		Map<String, Object> result = create
				.select(
						FREEFORM.VALUE_TEXT,
						FREEFORM.ORDER_BY,
						LEXEME_FREEFORM.LEXEME_ID)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(FREEFORM.ID.eq(freeformId)
						.and(LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.fetchSingleMap();
		return result;
	}
}
