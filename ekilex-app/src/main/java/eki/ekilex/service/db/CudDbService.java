package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.LEXEME_POS;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.LEX_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.main.Tables.MEANING_TAG;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.USAGE_DEFINITION;
import static eki.ekilex.data.db.main.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.main.Tables.WORD_FORUM;
import static eki.ekilex.data.db.main.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_OD_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OD_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_OD_USAGE;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.main.Tables.WORD_TAG;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.ListData;
import eki.ekilex.data.MeaningImage;
import eki.ekilex.data.Note;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageDefinition;
import eki.ekilex.data.UsageTranslation;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.db.main.tables.LexRelation;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordWordType;
import eki.ekilex.data.db.main.tables.records.FreeformRecord;
import eki.ekilex.data.db.main.tables.records.LexRelationRecord;
import eki.ekilex.data.db.main.tables.records.LexemeFreeformRecord;
import eki.ekilex.data.db.main.tables.records.LexemeTagRecord;
import eki.ekilex.data.db.main.tables.records.MeaningForumRecord;
import eki.ekilex.data.db.main.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.main.tables.records.MeaningRecord;
import eki.ekilex.data.db.main.tables.records.MeaningRelationRecord;
import eki.ekilex.data.db.main.tables.records.WordForumRecord;
import eki.ekilex.data.db.main.tables.records.WordFreeformRecord;
import eki.ekilex.data.db.main.tables.records.WordGroupMemberRecord;
import eki.ekilex.data.db.main.tables.records.WordGroupRecord;
import eki.ekilex.data.db.main.tables.records.WordRelationRecord;

@Component
public class CudDbService extends AbstractDataDbService {

	public void updateFreeform(Freeform freeform, String userName) {

		LocalDateTime now = LocalDateTime.now();

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(FREEFORM.VALUE, freeform.getValue());
		fieldAndValueMap.put(FREEFORM.VALUE_PRESE, freeform.getValuePrese());
		fieldAndValueMap.put(FREEFORM.MODIFIED_BY, userName);
		fieldAndValueMap.put(FREEFORM.MODIFIED_ON, now);
		fieldAndValueMap.put(FREEFORM.IS_PUBLIC, freeform.isPublic());
		if (freeform.getLang() != null) {
			fieldAndValueMap.put(FREEFORM.LANG, freeform.getLang());
		}
		if (freeform.getComplexity() != null) {
			fieldAndValueMap.put(FREEFORM.COMPLEXITY, freeform.getComplexity().name());
		}

		mainDb
				.update(FREEFORM)
				.set(fieldAndValueMap)
				.where(FREEFORM.ID.eq(freeform.getId()))
				.execute();
	}

	public void updateUsage(Usage usage) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(USAGE.VALUE, usage.getValue());
		fieldAndValueMap.put(USAGE.VALUE_PRESE, usage.getValuePrese());
		fieldAndValueMap.put(USAGE.MODIFIED_BY, usage.getModifiedBy());
		fieldAndValueMap.put(USAGE.MODIFIED_ON, usage.getModifiedOn());
		fieldAndValueMap.put(USAGE.IS_PUBLIC, usage.isPublic());
		if (StringUtils.isNotBlank(usage.getLang())) {
			fieldAndValueMap.put(USAGE.LANG, usage.getLang());
		}
		if (usage.getComplexity() != null) {
			fieldAndValueMap.put(USAGE.COMPLEXITY, usage.getComplexity().name());
		}

		mainDb
				.update(USAGE)
				.set(fieldAndValueMap)
				.where(USAGE.ID.eq(usage.getId()))
				.execute();
	}

	public void updateUsageTranslation(UsageTranslation usageTranslation) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(USAGE_TRANSLATION.VALUE, usageTranslation.getValue());
		fieldAndValueMap.put(USAGE_TRANSLATION.VALUE_PRESE, usageTranslation.getValuePrese());
		fieldAndValueMap.put(USAGE_TRANSLATION.MODIFIED_BY, usageTranslation.getModifiedBy());
		fieldAndValueMap.put(USAGE_TRANSLATION.MODIFIED_ON, usageTranslation.getModifiedOn());
		if (StringUtils.isNotBlank(usageTranslation.getLang())) {
			fieldAndValueMap.put(USAGE_TRANSLATION.LANG, usageTranslation.getLang());
		}

		mainDb
				.update(USAGE_TRANSLATION)
				.set(fieldAndValueMap)
				.where(USAGE_TRANSLATION.ID.eq(usageTranslation.getId()))
				.execute();
	}

	public void updateUsageDefinition(UsageDefinition usageDefinition) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(USAGE_DEFINITION.VALUE, usageDefinition.getValue());
		fieldAndValueMap.put(USAGE_DEFINITION.VALUE_PRESE, usageDefinition.getValuePrese());
		fieldAndValueMap.put(USAGE_DEFINITION.MODIFIED_BY, usageDefinition.getModifiedBy());
		fieldAndValueMap.put(USAGE_DEFINITION.MODIFIED_ON, usageDefinition.getModifiedOn());
		if (StringUtils.isNotBlank(usageDefinition.getLang())) {
			fieldAndValueMap.put(USAGE_DEFINITION.LANG, usageDefinition.getLang());
		}

		mainDb
				.update(USAGE_DEFINITION)
				.set(fieldAndValueMap)
				.where(USAGE_DEFINITION.ID.eq(usageDefinition.getId()))
				.execute();
	}

	public void updateDefinition(Long id, String value, String valuePrese, String lang, String typeCode, Complexity complexity, Boolean isPublic) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(DEFINITION.VALUE, value);
		fieldAndValueMap.put(DEFINITION.VALUE_PRESE, valuePrese);
		fieldAndValueMap.put(DEFINITION.LANG, lang);
		fieldAndValueMap.put(DEFINITION.DEFINITION_TYPE_CODE, typeCode);
		if (complexity != null) {
			fieldAndValueMap.put(DEFINITION.COMPLEXITY, complexity.name());
		}
		if (isPublic != null) {
			fieldAndValueMap.put(DEFINITION.IS_PUBLIC, isPublic);
		}

		mainDb
				.update(DEFINITION)
				.set(fieldAndValueMap)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void updateDefinitionValue(Long id, String value, String valuePrese) {
		mainDb
				.update(DEFINITION)
				.set(DEFINITION.VALUE, value)
				.set(DEFINITION.VALUE_PRESE, valuePrese)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void updateDefinitionOrderby(ListData item) {
		mainDb
				.update(DEFINITION)
				.set(DEFINITION.ORDER_BY, item.getOrderby())
				.where(DEFINITION.ID.eq(item.getId()))
				.execute();
	}

	public void updateDefinitionNote(Long definitionNoteId, Note note) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(DEFINITION_NOTE.VALUE, note.getValue());
		fieldAndValueMap.put(DEFINITION_NOTE.VALUE_PRESE, note.getValuePrese());
		fieldAndValueMap.put(DEFINITION_NOTE.IS_PUBLIC, note.isPublic());
		fieldAndValueMap.put(DEFINITION_NOTE.MODIFIED_BY, note.getModifiedBy());
		fieldAndValueMap.put(DEFINITION_NOTE.MODIFIED_ON, note.getModifiedOn());
		if (StringUtils.isNotBlank(note.getLang())) {
			fieldAndValueMap.put(DEFINITION_NOTE.LANG, note.getLang());
		}
		if (note.getComplexity() != null) {
			fieldAndValueMap.put(DEFINITION_NOTE.COMPLEXITY, note.getComplexity().name());
		}

		mainDb
				.update(DEFINITION_NOTE)
				.set(fieldAndValueMap)
				.where(DEFINITION_NOTE.ID.eq(definitionNoteId))
				.execute();
	}

	public void updateDefinitionNoteOrderby(ListData item) {
		mainDb
				.update(DEFINITION_NOTE)
				.set(DEFINITION_NOTE.ORDER_BY, item.getOrderby())
				.where(DEFINITION_NOTE.ID.eq(item.getId()))
				.execute();
	}

	public void updateMeaningNoteOrderby(ListData item) {
		mainDb
				.update(MEANING_NOTE)
				.set(MEANING_NOTE.ORDER_BY, item.getOrderby())
				.where(MEANING_NOTE.ID.eq(item.getId()))
				.execute();
	}

	public void updateLexemeNoteOrderby(ListData item) {
		mainDb
				.update(LEXEME_NOTE)
				.set(LEXEME_NOTE.ORDER_BY, item.getOrderby())
				.where(LEXEME_NOTE.ID.eq(item.getId()))
				.execute();
	}

	public void updateLexemeRelationOrderby(ListData item) {
		mainDb
				.update(LEX_RELATION)
				.set(LEX_RELATION.ORDER_BY, item.getOrderby())
				.where(LEX_RELATION.ID.eq(item.getId()))
				.execute();
	}

	public void updateMeaningRelationOrderby(ListData item) {
		mainDb
				.update(MEANING_RELATION)
				.set(MEANING_RELATION.ORDER_BY, item.getOrderby())
				.where(MEANING_RELATION.ID.eq(item.getId()))
				.execute();
	}

	public void updateWordRelationOrderby(ListData item) {
		mainDb
				.update(WORD_RELATION)
				.set(WORD_RELATION.ORDER_BY, item.getOrderby())
				.where(WORD_RELATION.ID.eq(item.getId()))
				.execute();
	}

	public void updateWordEtymologyOrderby(ListData item) {
		mainDb
				.update(WORD_ETYMOLOGY)
				.set(WORD_ETYMOLOGY.ORDER_BY, item.getOrderby())
				.where(WORD_ETYMOLOGY.ID.eq(item.getId()))
				.execute();
	}

	public void updateWordOdUsageOrderby(ListData item) {
		mainDb
				.update(WORD_OD_USAGE)
				.set(WORD_OD_USAGE.ORDER_BY, item.getOrderby())
				.where(WORD_OD_USAGE.ID.eq(item.getId()))
				.execute();
	}

	public void updateLexemeOrderby(ListData item) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.ORDER_BY, item.getOrderby())
				.where(LEXEME.ID.eq(item.getId()))
				.execute();
	}

	public void updateUsageOrderby(ListData item) {
		mainDb
				.update(USAGE)
				.set(USAGE.ORDER_BY, item.getOrderby())
				.where(USAGE.ID.eq(item.getId()))
				.execute();
	}

	public void updateMeaningDomainOrderby(ListData item) {
		mainDb
				.update(MEANING_DOMAIN)
				.set(MEANING_DOMAIN.ORDER_BY, item.getOrderby())
				.where(MEANING_DOMAIN.ID.eq(item.getId()))
				.execute();
	}

	public void updateFreeformOrderby(ListData item) {
		mainDb
				.update(FREEFORM)
				.set(FREEFORM.ORDER_BY, item.getOrderby())
				.where(FREEFORM.ID.eq(item.getId()))
				.execute();
	}

	public void updateMeaningRelationWeight(Long meaningRelationId, BigDecimal relationWeight) {
		mainDb
				.update(MEANING_RELATION)
				.set(MEANING_RELATION.WEIGHT, relationWeight)
				.where(MEANING_RELATION.ID.eq(meaningRelationId))
				.execute();
	}

	public void updateLexemeNote(Long lexemeNoteId, Note note) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(LEXEME_NOTE.VALUE, note.getValue());
		fieldAndValueMap.put(LEXEME_NOTE.VALUE_PRESE, note.getValuePrese());
		fieldAndValueMap.put(LEXEME_NOTE.IS_PUBLIC, note.isPublic());
		fieldAndValueMap.put(LEXEME_NOTE.MODIFIED_BY, note.getModifiedBy());
		fieldAndValueMap.put(LEXEME_NOTE.MODIFIED_ON, note.getModifiedOn());
		if (StringUtils.isNotBlank(note.getLang())) {
			fieldAndValueMap.put(LEXEME_NOTE.LANG, note.getLang());
		}
		if (note.getComplexity() != null) {
			fieldAndValueMap.put(LEXEME_NOTE.COMPLEXITY, note.getComplexity().name());
		}

		mainDb
				.update(LEXEME_NOTE)
				.set(fieldAndValueMap)
				.where(LEXEME_NOTE.ID.eq(lexemeNoteId))
				.execute();
	}

	public void updateLexemeReliability(Long lexemeId, Integer reliability) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.RELIABILITY, reliability)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeWeight(Long lexemeId, BigDecimal lexemeWeight) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.WEIGHT, lexemeWeight)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeLevels(Long lexemeId, Integer level1, Integer level2) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.LEVEL1, level1)
				.set(LEXEME.LEVEL2, level2)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeLevel1(Long lexemeId, Integer level1) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.LEVEL1, level1)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeLevel2(Long lexemeId, Integer level2) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.LEVEL2, level2)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemePublicity(Long lexemeId, boolean isPublic) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeIsWord(Long lexemeId, boolean isWord) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_WORD, isWord)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeIsCollocation(Long lexemeId, boolean isCollocation) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_COLLOCATION, isCollocation)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.VALUE_STATE_CODE, valueStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeProficiencyLevel(Long lexemeId, String proficiencyLevelCode) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.PROFICIENCY_LEVEL_CODE, proficiencyLevelCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeComplexity(Long lexemeId, String complexity) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.COMPLEXITY, complexity)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public Long updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		Long lexemePosId = mainDb
				.update(LEXEME_POS)
				.set(LEXEME_POS.POS_CODE, newPos)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(currentPos)))
				.returning(LEXEME_POS.ID)
				.fetchOne()
				.getId();
		return lexemePosId;
	}

	public Long updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) {
		Long lexemeDerivId = mainDb
				.update(LEXEME_DERIV)
				.set(LEXEME_DERIV.DERIV_CODE, newDeriv)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId).and(LEXEME_DERIV.DERIV_CODE.eq(currentDeriv)))
				.returning(LEXEME_DERIV.ID)
				.fetchOne()
				.getId();
		return lexemeDerivId;
	}

	public Long updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) {
		Long lexemeRegisterId = mainDb
				.update(LEXEME_REGISTER)
				.set(LEXEME_REGISTER.REGISTER_CODE, newRegister)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId).and(LEXEME_REGISTER.REGISTER_CODE.eq(currentRegister)))
				.returning(LEXEME_REGISTER.ID)
				.fetchOne()
				.getId();
		return lexemeRegisterId;
	}

	public Long updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) {
		Long lexemeRegionId = mainDb
				.update(LEXEME_REGION)
				.set(LEXEME_REGION.REGION_CODE, newRegion)
				.where(LEXEME_REGION.LEXEME_ID.eq(lexemeId).and(LEXEME_REGION.REGION_CODE.eq(currentRegion)))
				.returning(LEXEME_REGION.ID)
				.fetchOne()
				.getId();
		return lexemeRegionId;
	}

	public void updateLexemeWordId(Long lexemeId, Long wordId) {

		mainDb
				.update(LEXEME)
				.set(LEXEME.WORD_ID, wordId)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateWordValue(Long wordId, String value, String valuePrese) {
		mainDb
				.update(WORD)
				.set(WORD.VALUE, value)
				.set(WORD.VALUE_PRESE, valuePrese)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordValuePrese(Long wordId, String valuePrese) {
		mainDb
				.update(WORD)
				.set(WORD.VALUE_PRESE, valuePrese)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordLang(Long wordId, String langCode) {
		mainDb
				.update(WORD)
				.set(WORD.LANG, langCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordValueAndLang(Long wordId, String value, String valuePrese, String lang) {
		mainDb
				.update(WORD)
				.set(WORD.VALUE, value)
				.set(WORD.VALUE_PRESE, valuePrese)
				.set(WORD.LANG, lang)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordValueAndAsWordAndLang(Long wordId, String value, String valuePrese, String valueAsWord, String lang) {
		mainDb
				.update(WORD)
				.set(WORD.VALUE, value)
				.set(WORD.VALUE_PRESE, valuePrese)
				.set(WORD.VALUE_AS_WORD, valueAsWord)
				.set(WORD.LANG, lang)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateAsWordValue(Long wordId, String valueAsWord) {
		mainDb
				.update(WORD)
				.set(WORD.VALUE_AS_WORD, valueAsWord)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordVocalForm(Long wordId, String vocalForm) {
		mainDb
				.update(WORD)
				.set(WORD.VOCAL_FORM, vocalForm)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordMorphophonoForm(Long wordId, String morphophonoForm) {
		mainDb
				.update(WORD)
				.set(WORD.MORPHOPHONO_FORM, morphophonoForm)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordGender(Long wordId, String genderCode) {
		mainDb
				.update(WORD)
				.set(WORD.GENDER_CODE, genderCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public Long updateWordType(Long wordId, String currentTypeCode, String newTypeCode) {
		Long wordWordTypeId = mainDb
				.update(WORD_WORD_TYPE)
				.set(WORD_WORD_TYPE.WORD_TYPE_CODE, newTypeCode)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordId).and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(currentTypeCode)))
				.returning(WORD_WORD_TYPE.ID)
				.fetchOne()
				.getId();
		return wordWordTypeId;
	}

	public void updateWordAspect(Long wordId, String aspectCode) {
		mainDb
				.update(WORD)
				.set(WORD.ASPECT_CODE, aspectCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordDisplayMorph(Long wordId, String displayMorphCode) {
		mainDb
				.update(WORD)
				.set(WORD.DISPLAY_MORPH_CODE, displayMorphCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordMorphComment(Long wordId, String value) {
		mainDb
				.update(WORD)
				.set(WORD.MORPH_COMMENT, value)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordOdMorph(eki.ekilex.data.WordOdMorph wordOdMorph) {
		mainDb
				.update(WORD_OD_MORPH)
				.set(WORD_OD_MORPH.VALUE, wordOdMorph.getValue())
				.set(WORD_OD_MORPH.VALUE_PRESE, wordOdMorph.getValuePrese())
				.set(WORD_OD_MORPH.IS_PUBLIC, wordOdMorph.isPublic())
				.set(WORD_OD_MORPH.MODIFIED_BY, wordOdMorph.getModifiedBy())
				.set(WORD_OD_MORPH.MODIFIED_ON, wordOdMorph.getModifiedOn())
				.where(WORD_OD_MORPH.ID.eq(wordOdMorph.getId()))
				.execute();
	}

	public void updateWordOdRecommendation(eki.ekilex.data.WordOdRecommendation wordOdRecommendation) {
		mainDb
				.update(WORD_OD_RECOMMENDATION)
				.set(WORD_OD_RECOMMENDATION.VALUE, wordOdRecommendation.getValue())
				.set(WORD_OD_RECOMMENDATION.VALUE_PRESE, wordOdRecommendation.getValuePrese())
				.set(WORD_OD_RECOMMENDATION.OPT_VALUE, wordOdRecommendation.getOptValue())
				.set(WORD_OD_RECOMMENDATION.OPT_VALUE_PRESE, wordOdRecommendation.getOptValuePrese())
				.set(WORD_OD_RECOMMENDATION.IS_PUBLIC, wordOdRecommendation.isPublic())
				.set(WORD_OD_RECOMMENDATION.MODIFIED_BY, wordOdRecommendation.getModifiedBy())
				.set(WORD_OD_RECOMMENDATION.MODIFIED_ON, wordOdRecommendation.getModifiedOn())
				.where(WORD_OD_RECOMMENDATION.ID.eq(wordOdRecommendation.getId()))
				.execute();
	}

	public void updateWordOdUsage(eki.ekilex.data.WordOdUsage wordOdUsage) {
		mainDb
				.update(WORD_OD_USAGE)
				.set(WORD_OD_USAGE.VALUE, wordOdUsage.getValue())
				.set(WORD_OD_USAGE.VALUE_PRESE, wordOdUsage.getValuePrese())
				.set(WORD_OD_USAGE.IS_PUBLIC, wordOdUsage.isPublic())
				.set(WORD_OD_USAGE.MODIFIED_BY, wordOdUsage.getModifiedBy())
				.set(WORD_OD_USAGE.MODIFIED_ON, wordOdUsage.getModifiedOn())
				.where(WORD_OD_USAGE.ID.eq(wordOdUsage.getId()))
				.execute();
	}

	public void updateMeaningNote(Long meaningNoteId, Note note) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(MEANING_NOTE.VALUE, note.getValue());
		fieldAndValueMap.put(MEANING_NOTE.VALUE_PRESE, note.getValuePrese());
		fieldAndValueMap.put(MEANING_NOTE.IS_PUBLIC, note.isPublic());
		fieldAndValueMap.put(MEANING_NOTE.MODIFIED_BY, note.getModifiedBy());
		fieldAndValueMap.put(MEANING_NOTE.MODIFIED_ON, note.getModifiedOn());
		if (StringUtils.isNotBlank(note.getLang())) {
			fieldAndValueMap.put(MEANING_NOTE.LANG, note.getLang());
		}
		if (note.getComplexity() != null) {
			fieldAndValueMap.put(MEANING_NOTE.COMPLEXITY, note.getComplexity().name());
		}

		mainDb
				.update(MEANING_NOTE)
				.set(fieldAndValueMap)
				.where(MEANING_NOTE.ID.eq(meaningNoteId))
				.execute();
	}

	public void updateMeaningImage(Long meaningImageId, MeaningImage meaningImage) {

		Map<Field<?>, Object> fieldAndValueMap = new HashMap<>();
		fieldAndValueMap.put(MEANING_IMAGE.URL, meaningImage.getUrl());
		fieldAndValueMap.put(MEANING_IMAGE.TITLE, meaningImage.getTitle());
		//fieldAndValueMap.put(MEANING_IMAGE.IS_PUBLIC, meaningImage.isPublic()); not yet implemented in UI
		fieldAndValueMap.put(MEANING_IMAGE.MODIFIED_BY, meaningImage.getModifiedBy());
		fieldAndValueMap.put(MEANING_IMAGE.MODIFIED_ON, meaningImage.getModifiedOn());
		if (meaningImage.getComplexity() != null) {
			fieldAndValueMap.put(MEANING_IMAGE.COMPLEXITY, meaningImage.getComplexity().name());
		}

		mainDb
				.update(MEANING_IMAGE)
				.set(fieldAndValueMap)
				.where(MEANING_IMAGE.ID.eq(meaningImageId))
				.execute();
	}

	public Long updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		Long meaningDomainId = mainDb
				.update(MEANING_DOMAIN)
				.set(MEANING_DOMAIN.DOMAIN_CODE, newDomain.getCode())
				.set(MEANING_DOMAIN.DOMAIN_ORIGIN, newDomain.getOrigin())
				.where(
						MEANING_DOMAIN.MEANING_ID.eq(meaningId).and(
								MEANING_DOMAIN.DOMAIN_CODE.eq(currentDomain.getCode())).and(
										MEANING_DOMAIN.DOMAIN_ORIGIN.eq(currentDomain.getOrigin())))
				.returning(MEANING_DOMAIN.ID)
				.fetchOne()
				.getId();
		return meaningDomainId;
	}

	public Long updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType) {
		Long meaningSemanticTypeId = mainDb
				.update(MEANING_SEMANTIC_TYPE)
				.set(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE, newSemanticType)
				.where(MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId).and(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE.eq(currentSemanticType)))
				.returning(MEANING_SEMANTIC_TYPE.ID)
				.fetchOne()
				.getId();
		return meaningSemanticTypeId;
	}

	public void updateWordRelationOrderBy(Long relationId, Long orderBy) {
		mainDb
				.update(WORD_RELATION)
				.set(WORD_RELATION.ORDER_BY, orderBy)
				.where(WORD_RELATION.ID.eq(relationId))
				.execute();
	}

	public void updateMeaningLexemesPublicity(Long meaningId, String datasetCode, boolean isPublic) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.IS_PUBLIC.ne(isPublic)))
				.execute();
	}

	public void updateMeaningForum(Long meaningForumId, String value, String valuePrese, String userName) {

		LocalDateTime now = LocalDateTime.now();

		mainDb
				.update(MEANING_FORUM)
				.set(MEANING_FORUM.VALUE, value)
				.set(MEANING_FORUM.VALUE_PRESE, valuePrese)
				.set(MEANING_FORUM.MODIFIED_BY, userName)
				.set(MEANING_FORUM.MODIFIED_ON, now)
				.where(MEANING_FORUM.ID.eq(meaningForumId))
				.execute();
	}

	public void updateWordLexemesPublicity(Long wordId, String datasetCode, boolean isPublic) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.IS_PUBLIC.ne(isPublic)))
				.execute();
	}

	public void updateWordLexemesWordId(Long currentWordId, Long newWordId, String datasetCode) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.WORD_ID, newWordId)
				.where(
						LEXEME.WORD_ID.eq(currentWordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.execute();
	}

	public void updateWordForum(Long wordForumId, String value, String valuePrese, String userName) {

		LocalDateTime now = LocalDateTime.now();

		mainDb
				.update(WORD_FORUM)
				.set(WORD_FORUM.VALUE, value)
				.set(WORD_FORUM.VALUE_PRESE, valuePrese)
				.set(WORD_FORUM.MODIFIED_BY, userName)
				.set(WORD_FORUM.MODIFIED_ON, now)
				.where(WORD_FORUM.ID.eq(wordForumId))
				.execute();
	}

	public void adjustWordHomonymNrs(SimpleWord word) {

		String wordValue = word.getWordValue();
		String lang = word.getLang();

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		WordWordType wt = WORD_WORD_TYPE.as("wt");

		Field<Integer> dsobf = DSL
				.select(DSL.when(DSL.count(l.ID).gt(0), 1).otherwise(2))
				.from(l)
				.where(
						l.WORD_ID.eq(w.ID)
								.and(l.DATASET_CODE.eq(DATASET_EKI)))
				.asField();

		Field<Integer> afobf = DSL
				.select(DSL.when(DSL.count(wt.ID).gt(0), 2).otherwise(1))
				.from(wt)
				.where(
						wt.WORD_ID.eq(w.ID)
								.and(wt.WORD_TYPE_CODE.in(WORD_TYPE_CODE_PREFIXOID, WORD_TYPE_CODE_SUFFIXOID)))
				.asField();

		Table<Record4<Long, Integer, Integer, Integer>> ww = DSL
				.select(
						w.ID,
						w.HOMONYM_NR,
						dsobf.as("ds_order_by"),
						afobf.as("af_order_by"))
				.from(w)
				.where(
						w.LANG.eq(lang)
								.and(w.VALUE.eq(wordValue))
								.and(w.IS_PUBLIC.isTrue())
								.andExists(DSL
										.select(l.ID)
										.from(l)
										.where(l.WORD_ID.eq(w.ID))))
				.asTable("w");

		Result<Record2<Long, Integer>> homonyms = mainDb
				.select(
						ww.field("id", Long.class),
						ww.field("homonym_nr", Integer.class))
				.from(ww)
				.orderBy(
						ww.field("ds_order_by"),
						ww.field("af_order_by"),
						ww.field("homonym_nr"),
						ww.field("id"))
				.fetch();

		if (CollectionUtils.isNotEmpty(homonyms)) {
			int homonymNrIter = 1;
			for (Record2<Long, Integer> homonym : homonyms) {
				Long adjWordId = homonym.get("id", Long.class);
				Integer adjHomonymNr = homonym.get("homonym_nr", Integer.class);
				if (adjHomonymNr != homonymNrIter) {
					mainDb.update(WORD).set(WORD.HOMONYM_NR, homonymNrIter).where(WORD.ID.eq(adjWordId)).execute();
				}
				homonymNrIter++;
			}
		}
	}

	public WordLexemeMeaningIdTuple createPrivateWordAndLexemeAndMeaning(
			String value,
			String valuePrese,
			String valueAsWord,
			String morphophonoForm,
			String lang,
			String datasetCode) throws Exception {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();

		int homonymNr = 0;
		boolean isPublic = false;

		Long wordId = mainDb
				.insertInto(
						WORD,
						WORD.VALUE,
						WORD.VALUE_PRESE,
						WORD.VALUE_AS_WORD,
						WORD.MORPHOPHONO_FORM,
						WORD.HOMONYM_NR,
						WORD.LANG,
						WORD.IS_PUBLIC)
				.values(
						value,
						valuePrese,
						valueAsWord,
						morphophonoForm,
						homonymNr,
						lang,
						isPublic)
				.returning(WORD.ID)
				.fetchOne()
				.getId();

		Long meaningId = mainDb
				.insertInto(MEANING)
				.defaultValues()
				.returning(MEANING.ID)
				.fetchOne()
				.getId();

		Long lexemeId = createLexeme(meaningId, wordId, datasetCode, 1, null, isPublic);
		;

		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);

		return wordLexemeMeaningId;
	}

	public WordLexemeMeaningIdTuple createWordAndLexemeAndMeaning(
			String value,
			String valuePrese,
			String valueAsWord,
			String morphophonoForm,
			String lang,
			String datasetCode,
			boolean isPublic,
			Long meaningId) throws Exception {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();
		int homonymNr = getWordNextHomonymNr(value, lang);

		Long wordId = mainDb
				.insertInto(
						WORD,
						WORD.VALUE,
						WORD.VALUE_PRESE,
						WORD.VALUE_AS_WORD,
						WORD.MORPHOPHONO_FORM,
						WORD.HOMONYM_NR,
						WORD.LANG)
				.values(
						value,
						valuePrese,
						valueAsWord,
						morphophonoForm,
						homonymNr,
						lang)
				.returning(WORD.ID)
				.fetchOne()
				.getId();

		if (meaningId == null) {
			meaningId = mainDb
					.insertInto(MEANING)
					.defaultValues()
					.returning(MEANING.ID)
					.fetchOne()
					.getId();
		}

		Long lexemeId = createLexeme(meaningId, wordId, datasetCode, 1, null, isPublic);

		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);

		return wordLexemeMeaningId;
	}

	public Long createWord(String wordValue, String valuePrese, String valueAsWord, String lang, int homNr) {

		Long wordId = mainDb
				.insertInto(
						WORD,
						WORD.VALUE,
						WORD.VALUE_PRESE,
						WORD.VALUE_AS_WORD,
						WORD.MORPHOPHONO_FORM,
						WORD.HOMONYM_NR,
						WORD.LANG)
				.values(
						wordValue,
						valuePrese,
						valueAsWord,
						wordValue,
						homNr,
						lang)
				.returning(WORD.ID)
				.fetchOne()
				.getId();
		return wordId;
	}

	public Long createWordFreeform(Long wordId, Freeform freeform, String userName) {

		Long freeformId = createFreeform(freeform, userName);

		WordFreeformRecord wordFreeform = mainDb.newRecord(WORD_FREEFORM);
		wordFreeform.setWordId(wordId);
		wordFreeform.setFreeformId(freeformId);
		wordFreeform.store();

		return freeformId;
	}

	public void createWordForum(Long wordId, String value, String valuePrese, Long userId, String userName) {

		LocalDateTime now = LocalDateTime.now();

		WordForumRecord wordForumRecord = mainDb.newRecord(WORD_FORUM);
		wordForumRecord.setWordId(wordId);
		wordForumRecord.setValue(value);
		wordForumRecord.setValuePrese(valuePrese);
		wordForumRecord.setCreatorId(userId);
		wordForumRecord.setCreatedBy(userName);
		wordForumRecord.setCreatedOn(now);
		wordForumRecord.setModifiedBy(userName);
		wordForumRecord.setModifiedOn(now);
		wordForumRecord.store();
	}

	public Long createWordOdRecommendation(Long wordId, eki.ekilex.data.WordOdRecommendation wordOdRecommendation) {

		return mainDb
				.insertInto(
						WORD_OD_RECOMMENDATION,
						WORD_OD_RECOMMENDATION.WORD_ID,
						WORD_OD_RECOMMENDATION.VALUE,
						WORD_OD_RECOMMENDATION.VALUE_PRESE,
						WORD_OD_RECOMMENDATION.OPT_VALUE,
						WORD_OD_RECOMMENDATION.OPT_VALUE_PRESE,
						WORD_OD_RECOMMENDATION.IS_PUBLIC,
						WORD_OD_RECOMMENDATION.CREATED_BY,
						WORD_OD_RECOMMENDATION.CREATED_ON,
						WORD_OD_RECOMMENDATION.MODIFIED_BY,
						WORD_OD_RECOMMENDATION.MODIFIED_ON)
				.values(
						wordId,
						wordOdRecommendation.getValue(),
						wordOdRecommendation.getValuePrese(),
						wordOdRecommendation.getOptValue(),
						wordOdRecommendation.getOptValuePrese(),
						wordOdRecommendation.isPublic(),
						wordOdRecommendation.getCreatedBy(),
						wordOdRecommendation.getCreatedOn(),
						wordOdRecommendation.getModifiedBy(),
						wordOdRecommendation.getModifiedOn())
				.returning(WORD_OD_RECOMMENDATION.ID)
				.fetchOne()
				.getId();
	}

	public Long createWordOdUsage(Long wordId, eki.ekilex.data.WordOdUsage wordOdUsage) {

		return mainDb
				.insertInto(
						WORD_OD_USAGE,
						WORD_OD_USAGE.WORD_ID,
						WORD_OD_USAGE.VALUE,
						WORD_OD_USAGE.VALUE_PRESE,
						WORD_OD_USAGE.IS_PUBLIC,
						WORD_OD_USAGE.CREATED_BY,
						WORD_OD_USAGE.CREATED_ON,
						WORD_OD_USAGE.MODIFIED_BY,
						WORD_OD_USAGE.MODIFIED_ON)
				.values(
						wordId,
						wordOdUsage.getValue(),
						wordOdUsage.getValuePrese(),
						wordOdUsage.isPublic(),
						wordOdUsage.getCreatedBy(),
						wordOdUsage.getCreatedOn(),
						wordOdUsage.getModifiedBy(),
						wordOdUsage.getModifiedOn())
				.returning(WORD_OD_USAGE.ID)
				.fetchOne()
				.getId();
	}

	public Long createWordOdMorph(Long wordId, eki.ekilex.data.WordOdMorph wordOdMorph) {

		return mainDb
				.insertInto(
						WORD_OD_MORPH,
						WORD_OD_MORPH.WORD_ID,
						WORD_OD_MORPH.VALUE,
						WORD_OD_MORPH.VALUE_PRESE,
						WORD_OD_MORPH.IS_PUBLIC,
						WORD_OD_MORPH.CREATED_BY,
						WORD_OD_MORPH.CREATED_ON,
						WORD_OD_MORPH.MODIFIED_BY,
						WORD_OD_MORPH.MODIFIED_ON)
				.values(
						wordId,
						wordOdMorph.getValue(),
						wordOdMorph.getValuePrese(),
						wordOdMorph.isPublic(),
						wordOdMorph.getCreatedBy(),
						wordOdMorph.getCreatedOn(),
						wordOdMorph.getModifiedBy(),
						wordOdMorph.getModifiedOn())
				.returning(WORD_OD_MORPH.ID)
				.fetchOne()
				.getId();
	}

	public Long createWordType(Long wordId, String typeCode) {
		Long wordWordTypeId = mainDb
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordId)
						.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(typeCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (wordWordTypeId == null) {
			wordWordTypeId = mainDb
					.insertInto(WORD_WORD_TYPE, WORD_WORD_TYPE.WORD_ID, WORD_WORD_TYPE.WORD_TYPE_CODE)
					.values(wordId, typeCode)
					.returning(WORD_WORD_TYPE.ID)
					.fetchOne()
					.getId();
		}
		return wordWordTypeId;
	}

	public Long createWordTag(Long wordId, String tagName) {
		Long wordTagId = mainDb
				.select(WORD_TAG.ID)
				.from(WORD_TAG)
				.where(WORD_TAG.WORD_ID.eq(wordId)
						.and(WORD_TAG.TAG_NAME.eq(tagName)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (wordTagId == null) {
			wordTagId = mainDb
					.insertInto(
							WORD_TAG,
							WORD_TAG.WORD_ID,
							WORD_TAG.TAG_NAME)
					.values(wordId, tagName)
					.returning(WORD_TAG.ID)
					.fetchOne()
					.getId();
		}
		return wordTagId;
	}

	public Long createWordRelation(Long wordId, Long targetWordId, String wordRelationCode, String relationStatus) {

		Long wordRelationId = mainDb
				.select(WORD_RELATION.ID)
				.from(WORD_RELATION)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
								.and(WORD_RELATION.WORD2_ID.eq(targetWordId))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(wordRelationCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (wordRelationId == null) {
			WordRelationRecord newRelation = mainDb.newRecord(WORD_RELATION);
			newRelation.setWord1Id(wordId);
			newRelation.setWord2Id(targetWordId);
			newRelation.setWordRelTypeCode(wordRelationCode);
			newRelation.setRelationStatus(relationStatus);
			newRelation.store();
			wordRelationId = newRelation.getId();
		}
		return wordRelationId;
	}

	public Long createWordRelationGroup(String groupType) {
		WordGroupRecord wordGroupRecord = mainDb.newRecord(WORD_GROUP);
		wordGroupRecord.setWordRelTypeCode(groupType);
		wordGroupRecord.store();
		return wordGroupRecord.getId();
	}

	public Long createWordRelationGroupMember(Long groupId, Long wordId) {
		WordGroupMemberRecord wordGroupMember = mainDb.newRecord(WORD_GROUP_MEMBER);
		wordGroupMember.setWordGroupId(groupId);
		wordGroupMember.setWordId(wordId);
		wordGroupMember.store();
		return wordGroupMember.getId();
	}

	public void createWordRelationParam(Long wordRelationId, String paramName, BigDecimal paramValue) {

		mainDb
				.insertInto(WORD_RELATION_PARAM, WORD_RELATION_PARAM.WORD_RELATION_ID, WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.values(wordRelationId, paramName, paramValue)
				.execute();
	}

	public List<Long> createWordLexemesTag(Long wordId, String datasetCode, String tagName) {

		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");

		List<Long> lexemeIds = mainDb
				.insertInto(LEXEME_TAG, LEXEME_TAG.TAG_NAME, LEXEME_TAG.LEXEME_ID)
				.select(DSL
						.select(DSL.val(tagName), l.ID)
						.from(l)
						.where(
								l.WORD_ID.eq(wordId)
										.and(l.DATASET_CODE.eq(datasetCode)
												.andNotExists(DSL
														.select(lt.ID)
														.from(lt)
														.where(lt.LEXEME_ID.eq(l.ID).and(lt.TAG_NAME.eq(tagName)))))))
				.returning(LEXEME_TAG.LEXEME_ID)
				.fetch()
				.map(LexemeTagRecord::getLexemeId);

		return lexemeIds;
	}

	public List<Long> createMeaningLexemesTag(Long meaningId, String datasetCode, String tagName) {

		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");

		List<Long> lexemeIds = mainDb
				.insertInto(LEXEME_TAG, LEXEME_TAG.TAG_NAME, LEXEME_TAG.LEXEME_ID)
				.select(DSL
						.select(DSL.val(tagName), l.ID)
						.from(l)
						.where(
								l.MEANING_ID.eq(meaningId)
										.and(l.DATASET_CODE.eq(datasetCode)
												.andNotExists(DSL
														.select(lt.ID)
														.from(lt)
														.where(lt.LEXEME_ID.eq(l.ID).and(lt.TAG_NAME.eq(tagName)))))))
				.returning(LEXEME_TAG.LEXEME_ID)
				.fetch()
				.map(LexemeTagRecord::getLexemeId);

		return lexemeIds;
	}

	public Long createDefinition(Long meaningId, String value, String valuePrese, String languageCode, String definitionTypeCode, Complexity complexity, boolean isPublic) {
		return mainDb
				.insertInto(
						DEFINITION,
						DEFINITION.MEANING_ID,
						DEFINITION.LANG,
						DEFINITION.VALUE,
						DEFINITION.VALUE_PRESE,
						DEFINITION.DEFINITION_TYPE_CODE,
						DEFINITION.COMPLEXITY,
						DEFINITION.IS_PUBLIC)
				.values(
						meaningId,
						languageCode,
						value,
						valuePrese,
						definitionTypeCode,
						complexity.name(),
						isPublic)
				.returning(DEFINITION.ID)
				.fetchOne()
				.getId();
	}

	public void createDefinitionDataset(Long definitionId, String datasetCode) {
		mainDb.insertInto(DEFINITION_DATASET, DEFINITION_DATASET.DEFINITION_ID, DEFINITION_DATASET.DATASET_CODE)
				.values(definitionId, datasetCode)
				.execute();
	}

	public Long createDefinitionNote(Long definitionId, Note note) {

		return mainDb
				.insertInto(
						DEFINITION_NOTE,
						DEFINITION_NOTE.DEFINITION_ID,
						DEFINITION_NOTE.VALUE,
						DEFINITION_NOTE.VALUE_PRESE,
						DEFINITION_NOTE.LANG,
						DEFINITION_NOTE.COMPLEXITY,
						DEFINITION_NOTE.IS_PUBLIC,
						DEFINITION_NOTE.CREATED_BY,
						DEFINITION_NOTE.CREATED_ON,
						DEFINITION_NOTE.MODIFIED_BY,
						DEFINITION_NOTE.MODIFIED_ON)
				.values(
						definitionId,
						note.getValue(),
						note.getValuePrese(),
						note.getLang(),
						note.getComplexity().name(),
						note.isPublic(),
						note.getCreatedBy(),
						note.getCreatedOn(),
						note.getModifiedBy(),
						note.getModifiedOn())
				.returning(DEFINITION_NOTE.ID)
				.fetchOne()
				.getId();

	}

	public Long createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) {
		LexRelation lr = LEX_RELATION.as("lr");
		LexRelationRecord lexRelationRecord = mainDb
				.insertInto(lr, lr.LEXEME1_ID, lr.LEXEME2_ID, lr.LEX_REL_TYPE_CODE)
				.select(DSL
						.select(DSL.val(lexemeId1), DSL.val(lexemeId2), DSL.val(relationType))
						.whereNotExists(DSL
								.select(lr.ID)
								.from(lr)
								.where(lr.LEXEME1_ID.eq(lexemeId1).and(lr.LEXEME2_ID.eq(lexemeId2).and(lr.LEX_REL_TYPE_CODE.eq(relationType))))))
				.returning(lr.ID)
				.fetchOne();

		return lexRelationRecord != null ? lexRelationRecord.getId() : null;
	}

	public Long createMeaning() {
		MeaningRecord meaning = mainDb.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne();
		return meaning.getId();
	}

	public Long createMeaningRelation(Long meaningId1, Long meaningId2, String relationType) {
		return createMeaningRelation(meaningId1, meaningId2, relationType, null);
	}

	public Long createMeaningRelation(Long meaningId1, Long meaningId2, String relationType, Float relationWeight) {
		MeaningRelationRecord meaningRelation = mainDb.newRecord(MEANING_RELATION);
		meaningRelation.setMeaning1Id(meaningId1);
		meaningRelation.setMeaning2Id(meaningId2);
		meaningRelation.setMeaningRelTypeCode(relationType);
		if (relationWeight != null) {
			meaningRelation.setWeight(BigDecimal.valueOf(relationWeight));
		}
		meaningRelation.store();
		return meaningRelation.getId();
	}

	public Long createMeaningNote(Long meaningId, Note note) {

		return mainDb
				.insertInto(
						MEANING_NOTE,
						MEANING_NOTE.MEANING_ID,
						MEANING_NOTE.VALUE,
						MEANING_NOTE.VALUE_PRESE,
						MEANING_NOTE.LANG,
						MEANING_NOTE.COMPLEXITY,
						MEANING_NOTE.IS_PUBLIC,
						MEANING_NOTE.CREATED_BY,
						MEANING_NOTE.CREATED_ON,
						MEANING_NOTE.MODIFIED_BY,
						MEANING_NOTE.MODIFIED_ON)
				.values(
						meaningId,
						note.getValue(),
						note.getValuePrese(),
						note.getLang(),
						note.getComplexity().name(),
						note.isPublic(),
						note.getCreatedBy(),
						note.getCreatedOn(),
						note.getModifiedBy(),
						note.getModifiedOn())
				.returning(MEANING_NOTE.ID)
				.fetchOne()
				.getId();
	}

	public Long createMeaningImage(Long meaningId, MeaningImage meaningImage) {

		return mainDb
				.insertInto(
						MEANING_IMAGE,
						MEANING_IMAGE.MEANING_ID,
						MEANING_IMAGE.TITLE,
						MEANING_IMAGE.URL,
						MEANING_IMAGE.COMPLEXITY,
						MEANING_IMAGE.IS_PUBLIC,
						MEANING_IMAGE.CREATED_BY,
						MEANING_IMAGE.CREATED_ON,
						MEANING_IMAGE.MODIFIED_BY,
						MEANING_IMAGE.MODIFIED_ON)
				.values(
						meaningId,
						meaningImage.getTitle(),
						meaningImage.getUrl(),
						meaningImage.getComplexity().name(),
						meaningImage.isPublic(),
						meaningImage.getCreatedBy(),
						meaningImage.getCreatedOn(),
						meaningImage.getModifiedBy(),
						meaningImage.getModifiedOn())
				.returning(MEANING_IMAGE.ID)
				.fetchOne()
				.getId();
	}

	public Long createMeaningFreeform(Long meaningId, Freeform freeform, String userName) {

		Long freeformId = createFreeform(freeform, userName);

		MeaningFreeformRecord meaningFreeformRecord = mainDb.newRecord(MEANING_FREEFORM);
		meaningFreeformRecord.setMeaningId(meaningId);
		meaningFreeformRecord.setFreeformId(freeformId);
		meaningFreeformRecord.store();

		return freeformId;
	}

	public void createMeaningForum(Long meaningId, String value, String valuePrese, Long userId, String userName) {

		LocalDateTime now = LocalDateTime.now();

		MeaningForumRecord meaningForumRecord = mainDb.newRecord(MEANING_FORUM);
		meaningForumRecord.setMeaningId(meaningId);
		meaningForumRecord.setValue(value);
		meaningForumRecord.setValuePrese(valuePrese);
		meaningForumRecord.setCreatorId(userId);
		meaningForumRecord.setCreatedBy(userName);
		meaningForumRecord.setCreatedOn(now);
		meaningForumRecord.setModifiedBy(userName);
		meaningForumRecord.setModifiedOn(now);
		meaningForumRecord.store();
	}

	public Long createMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = mainDb
				.select(MEANING_DOMAIN.ID).from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode()))
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin())))
				.limit(1)
				.fetchOneInto(Long.class);
		if (meaningDomainId == null) {
			meaningDomainId = mainDb
					.insertInto(MEANING_DOMAIN, MEANING_DOMAIN.MEANING_ID, MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
					.values(meaningId, domain.getOrigin(), domain.getCode())
					.returning(MEANING_DOMAIN.ID)
					.fetchOne()
					.getId();
		}
		return meaningDomainId;
	}

	public Long createMeaningSemanticType(Long meaningId, String semanticTypeCode) {
		Long meaningSemanticTypeCodeId = mainDb
				.select(MEANING_SEMANTIC_TYPE.ID).from(MEANING_SEMANTIC_TYPE)
				.where(MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId)
						.and(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE.eq(semanticTypeCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (meaningSemanticTypeCodeId == null) {
			meaningSemanticTypeCodeId = mainDb
					.insertInto(MEANING_SEMANTIC_TYPE, MEANING_SEMANTIC_TYPE.MEANING_ID, MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE)
					.values(meaningId, semanticTypeCode)
					.returning(MEANING_SEMANTIC_TYPE.ID)
					.fetchOne()
					.getId();
		}
		return meaningSemanticTypeCodeId;
	}

	private Long createLexeme(Long meaningId, Long wordId, String datasetCode, int lexemeLevel1, String valueStateCode, boolean isPublic) {

		Long lexemeId = mainDb
				.insertInto(
						LEXEME,
						LEXEME.MEANING_ID,
						LEXEME.WORD_ID,
						LEXEME.DATASET_CODE,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.VALUE_STATE_CODE,
						LEXEME.IS_WORD,
						LEXEME.IS_COLLOCATION,
						LEXEME.IS_PUBLIC,
						LEXEME.COMPLEXITY)
				.values(
						meaningId,
						wordId,
						datasetCode,
						lexemeLevel1,
						1,
						valueStateCode,
						Boolean.TRUE,
						Boolean.FALSE,
						isPublic,
						COMPLEXITY_DETAIL)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
		return lexemeId;
	}

	public WordLexemeMeaningIdTuple createLexemeWithCreateOrSelectMeaning(
			Long wordId, String datasetCode, Long meaningId, int lexemeLevel1, String valueStateCode, boolean isPublic) throws Exception {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();
		if (meaningId == null) {
			meaningId = mainDb
					.insertInto(MEANING)
					.defaultValues()
					.returning(MEANING.ID)
					.fetchOne()
					.getId();
		} else {
			Long existingLexemeId = mainDb
					.select(LEXEME.ID)
					.from(LEXEME)
					.where(
							LEXEME.WORD_ID.eq(wordId)
									.and(LEXEME.DATASET_CODE.eq(datasetCode))
									.and(LEXEME.MEANING_ID.eq(meaningId)))
					.fetchOptionalInto(Long.class)
					.orElse(null);
			if (existingLexemeId != null) {
				return wordLexemeMeaningId;
			}
		}
		Long lexemeId = createLexeme(meaningId, wordId, datasetCode, lexemeLevel1, valueStateCode, isPublic);

		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);

		return wordLexemeMeaningId;
	}

	public Long createLexemeNote(Long lexemeId, Note note) {

		return mainDb
				.insertInto(
						LEXEME_NOTE,
						LEXEME_NOTE.LEXEME_ID,
						LEXEME_NOTE.VALUE,
						LEXEME_NOTE.VALUE_PRESE,
						LEXEME_NOTE.LANG,
						LEXEME_NOTE.COMPLEXITY,
						LEXEME_NOTE.IS_PUBLIC,
						LEXEME_NOTE.CREATED_BY,
						LEXEME_NOTE.CREATED_ON,
						LEXEME_NOTE.MODIFIED_BY,
						LEXEME_NOTE.MODIFIED_ON)
				.values(
						lexemeId,
						note.getValue(),
						note.getValuePrese(),
						note.getLang(),
						note.getComplexity().name(),
						note.isPublic(),
						note.getCreatedBy(),
						note.getCreatedOn(),
						note.getModifiedBy(),
						note.getModifiedOn())
				.returning(LEXEME_NOTE.ID)
				.fetchOne()
				.getId();
	}

	public Long createLexemeFreeform(Long lexemeId, Freeform freeform, String userName) {

		Long freeformId = createFreeform(freeform, userName);

		LexemeFreeformRecord lexemeFreeformRecord = mainDb.newRecord(LEXEME_FREEFORM);
		lexemeFreeformRecord.setLexemeId(lexemeId);
		lexemeFreeformRecord.setFreeformId(freeformId);
		lexemeFreeformRecord.store();

		return freeformId;
	}

	public Long createLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = mainDb
				.select(LEXEME_POS.ID).from(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_POS.POS_CODE.eq(posCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemePosId == null) {
			lexemePosId = mainDb
					.insertInto(LEXEME_POS, LEXEME_POS.LEXEME_ID, LEXEME_POS.POS_CODE)
					.values(lexemeId, posCode)
					.returning(LEXEME_POS.ID)
					.fetchOne()
					.getId();
		}
		return lexemePosId;
	}

	public Long createLexemeTag(Long lexemeId, String tagName) {
		Long lexemeTagId = mainDb
				.select(LEXEME_TAG.ID)
				.from(LEXEME_TAG)
				.where(LEXEME_TAG.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_TAG.TAG_NAME.eq(tagName)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeTagId == null) {
			lexemeTagId = mainDb
					.insertInto(LEXEME_TAG, LEXEME_TAG.LEXEME_ID, LEXEME_TAG.TAG_NAME)
					.values(lexemeId, tagName)
					.returning(LEXEME_TAG.ID)
					.fetchOne()
					.getId();
		}
		return lexemeTagId;
	}

	public Long createMeaningTag(Long meaningId, String tagName) {
		Long meaningTagId = mainDb
				.select(MEANING_TAG.ID)
				.from(MEANING_TAG)
				.where(MEANING_TAG.MEANING_ID.eq(meaningId)
						.and(MEANING_TAG.TAG_NAME.eq(tagName)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (meaningTagId == null) {
			meaningTagId = mainDb
					.insertInto(MEANING_TAG, MEANING_TAG.MEANING_ID, MEANING_TAG.TAG_NAME)
					.values(meaningId, tagName)
					.returning(MEANING_TAG.ID)
					.fetchOne()
					.getId();
		}
		return meaningTagId;
	}

	public Long createLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = mainDb
				.select(LEXEME_DERIV.ID).from(LEXEME_DERIV)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.eq(derivCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeDerivId == null) {
			lexemeDerivId = mainDb
					.insertInto(LEXEME_DERIV, LEXEME_DERIV.LEXEME_ID, LEXEME_DERIV.DERIV_CODE)
					.values(lexemeId, derivCode)
					.returning(LEXEME_DERIV.ID)
					.fetchOne()
					.getId();
		}
		return lexemeDerivId;
	}

	public Long createLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = mainDb
				.select(LEXEME_REGISTER.ID).from(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeRegisterId == null) {
			lexemeRegisterId = mainDb
					.insertInto(LEXEME_REGISTER, LEXEME_REGISTER.LEXEME_ID, LEXEME_REGISTER.REGISTER_CODE)
					.values(lexemeId, registerCode)
					.returning(LEXEME_REGISTER.ID)
					.fetchOne()
					.getId();
		}
		return lexemeRegisterId;
	}

	public Long createLexemeRegion(Long lexemeId, String regionCode) {
		Long lexemeRegionId = mainDb
				.select(LEXEME_REGION.ID).from(LEXEME_REGION)
				.where(LEXEME_REGION.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_REGION.REGION_CODE.eq(regionCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeRegionId == null) {
			lexemeRegionId = mainDb
					.insertInto(LEXEME_REGION, LEXEME_REGION.LEXEME_ID, LEXEME_REGION.REGION_CODE)
					.values(lexemeId, regionCode)
					.returning(LEXEME_REGION.ID)
					.fetchOne()
					.getId();
		}
		return lexemeRegionId;
	}

	public Long createUsage(Long lexemeId, Usage usage) {

		return mainDb
				.insertInto(
						USAGE,
						USAGE.LEXEME_ID,
						USAGE.VALUE,
						USAGE.VALUE_PRESE,
						USAGE.LANG,
						USAGE.COMPLEXITY,
						USAGE.CREATED_BY,
						USAGE.CREATED_ON,
						USAGE.MODIFIED_BY,
						USAGE.MODIFIED_ON,
						USAGE.IS_PUBLIC)
				.values(
						lexemeId,
						usage.getValue(),
						usage.getValuePrese(),
						usage.getLang(),
						usage.getComplexity().name(),
						usage.getCreatedBy(),
						usage.getCreatedOn(),
						usage.getModifiedBy(),
						usage.getModifiedOn(),
						usage.isPublic())
				.returning(USAGE.ID)
				.fetchOne()
				.getId();
	}

	public Long createUsageTranslation(Long usageId, UsageTranslation usageTranslation) {

		return mainDb
				.insertInto(
						USAGE_TRANSLATION,
						USAGE_TRANSLATION.USAGE_ID,
						USAGE_TRANSLATION.VALUE,
						USAGE_TRANSLATION.VALUE_PRESE,
						USAGE_TRANSLATION.LANG,
						USAGE_TRANSLATION.CREATED_BY,
						USAGE_TRANSLATION.CREATED_ON,
						USAGE_TRANSLATION.MODIFIED_BY,
						USAGE_TRANSLATION.MODIFIED_ON)
				.values(
						usageId,
						usageTranslation.getValue(),
						usageTranslation.getValuePrese(),
						usageTranslation.getLang(),
						usageTranslation.getCreatedBy(),
						usageTranslation.getCreatedOn(),
						usageTranslation.getModifiedBy(),
						usageTranslation.getModifiedOn())
				.returning(USAGE_TRANSLATION.ID)
				.fetchOne()
				.getId();
	}

	public Long createUsageDefinition(Long usageId, UsageDefinition usageDefinition) {

		return mainDb
				.insertInto(
						USAGE_TRANSLATION,
						USAGE_TRANSLATION.USAGE_ID,
						USAGE_TRANSLATION.VALUE,
						USAGE_TRANSLATION.VALUE_PRESE,
						USAGE_TRANSLATION.LANG,
						USAGE_TRANSLATION.CREATED_BY,
						USAGE_TRANSLATION.CREATED_ON,
						USAGE_TRANSLATION.MODIFIED_BY,
						USAGE_TRANSLATION.MODIFIED_ON)
				.values(
						usageId,
						usageDefinition.getValue(),
						usageDefinition.getValuePrese(),
						usageDefinition.getLang(),
						usageDefinition.getCreatedBy(),
						usageDefinition.getCreatedOn(),
						usageDefinition.getModifiedBy(),
						usageDefinition.getModifiedOn())
				.returning(USAGE_TRANSLATION.ID)
				.fetchOne()
				.getId();
	}

	public Long createChildFreeform(Freeform freeform, String userName) {

		Long freeformId = createFreeform(freeform, userName);
		return freeformId;
	}

	private Long createFreeform(Freeform freeform, String userName) {

		LocalDateTime now = LocalDateTime.now();
		String complexity = freeform.getComplexity() == null ? null : freeform.getComplexity().name();

		FreeformRecord freeformRecord = mainDb.newRecord(FREEFORM);
		freeformRecord.setParentId(freeform.getParentId());
		freeformRecord.setFreeformTypeCode(freeform.getFreeformTypeCode());
		freeformRecord.setValue(freeform.getValue());
		freeformRecord.setValuePrese(freeform.getValuePrese());
		freeformRecord.setLang(freeform.getLang());
		freeformRecord.setComplexity(complexity);
		freeformRecord.setIsPublic(freeform.isPublic());
		freeformRecord.setCreatedBy(userName);
		freeformRecord.setCreatedOn(now);
		freeformRecord.setModifiedBy(userName);
		freeformRecord.setModifiedOn(now);
		freeformRecord.store();

		return freeformRecord.getId();
	}

	public void deleteWord(SimpleWord word) {
		Long wordId = word.getWordId();
		mainDb.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(WORD_FREEFORM.FREEFORM_ID)
						.from(WORD_FREEFORM)
						.where(WORD_FREEFORM.WORD_ID.eq(wordId))))
				.execute();
		mainDb.delete(WORD)
				.where(WORD.ID.eq(wordId))
				.execute();
		adjustWordHomonymNrs(word);
	}

	public void deleteFloatingWord(Long wordId) {

		mainDb
				.delete(WORD)
				.where(
						WORD.ID.eq(wordId)
								.andNotExists(DSL
										.select(LEXEME.ID)
										.from(LEXEME)
										.where(LEXEME.WORD_ID.eq(WORD.ID))))
				.execute();
	}

	public void deleteWordWordType(Long wordWordTypeId) {
		mainDb
				.delete(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.ID.eq(wordWordTypeId))
				.execute();
	}

	public void deleteWordTag(Long wordTagId) {
		mainDb
				.delete(WORD_TAG)
				.where(WORD_TAG.ID.eq(wordTagId))
				.execute();
	}

	public void deleteWordTag(String tagName) {
		mainDb
				.delete(WORD_TAG)
				.where(WORD_TAG.TAG_NAME.eq(tagName))
				.execute();
	}

	public void deleteWordRelation(Long relationId) {
		mainDb
				.delete(WORD_RELATION)
				.where(WORD_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteWordRelationGroupMember(Long relationId) {
		mainDb
				.delete(WORD_GROUP_MEMBER)
				.where(WORD_GROUP_MEMBER.ID.eq(relationId))
				.execute();
	}

	public void deleteWordRelationGroup(Long groupId) {
		mainDb
				.delete(WORD_GROUP)
				.where(WORD_GROUP.ID.eq(groupId))
				.execute();
	}

	public List<Long> deleteWordLexemesTag(Long wordId, String datasetCode, String tagName) {

		List<Long> lexemeIds = mainDb
				.delete(LEXEME_TAG)
				.using(LEXEME)
				.where(
						LEXEME_TAG.TAG_NAME.eq(tagName)
								.and(LEXEME_TAG.LEXEME_ID.eq(LEXEME.ID))
								.and(LEXEME.WORD_ID.eq(wordId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.returning(LEXEME_TAG.LEXEME_ID)
				.fetch()
				.map(LexemeTagRecord::getLexemeId);

		return lexemeIds;
	}

	public void deleteWordForum(Long wordForumId) {
		mainDb
				.delete(WORD_FORUM)
				.where(WORD_FORUM.ID.eq(wordForumId))
				.execute();
	}

	public void deleteWordOdRecommendation(Long wordOdRecommendationId) {
		mainDb
				.delete(WORD_OD_RECOMMENDATION)
				.where(WORD_OD_RECOMMENDATION.ID.eq(wordOdRecommendationId))
				.execute();
	}

	public void deleteWordOdUsage(Long wordOdUsageId) {
		mainDb
				.delete(WORD_OD_USAGE)
				.where(WORD_OD_USAGE.ID.eq(wordOdUsageId))
				.execute();
	}

	public void deleteWordOdMorph(Long wordOdMorphId) {
		mainDb
				.delete(WORD_OD_MORPH)
				.where(WORD_OD_MORPH.ID.eq(wordOdMorphId))
				.execute();
	}

	public void deleteLexeme(Long lexemeId) {
		mainDb
				.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(LEXEME_FREEFORM.FREEFORM_ID)
						.from(LEXEME_FREEFORM)
						.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId))))
				.execute();
		mainDb
				.delete(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void deleteLexemeNote(Long lexemeNoteId) {
		mainDb
				.deleteFrom(LEXEME_NOTE)
				.where(LEXEME_NOTE.ID.eq(lexemeNoteId))
				.execute();
	}

	public void deleteUsage(Long usageId) {
		mainDb
				.deleteFrom(USAGE)
				.where(USAGE.ID.eq(usageId))
				.execute();
	}

	public void deleteUsageTranslation(Long usageTranslationId) {
		mainDb
				.deleteFrom(USAGE_TRANSLATION)
				.where(USAGE_TRANSLATION.ID.eq(usageTranslationId))
				.execute();
	}

	public void deleteUsageDefinition(Long usageDefinitionId) {
		mainDb
				.deleteFrom(USAGE_DEFINITION)
				.where(USAGE_DEFINITION.ID.eq(usageDefinitionId))
				.execute();
	}

	public void deleteLexemePos(Long lexemePosId) {
		mainDb
				.delete(LEXEME_POS)
				.where(LEXEME_POS.ID.eq(lexemePosId))
				.execute();
	}

	public void deleteLexemeTag(Long lexemeTagId) {
		mainDb
				.delete(LEXEME_TAG)
				.where(LEXEME_TAG.ID.eq(lexemeTagId))
				.execute();
	}

	public void deleteLexemeDeriv(Long lexemeDerivId) {
		mainDb
				.delete(LEXEME_DERIV)
				.where(LEXEME_DERIV.ID.eq(lexemeDerivId))
				.execute();
	}

	public void deleteLexemeRegister(Long lexemeRegisterId) {
		mainDb
				.delete(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.ID.eq(lexemeRegisterId))
				.execute();
	}

	public void deleteLexemeRegion(Long lexemeRegionId) {
		mainDb
				.delete(LEXEME_REGION)
				.where(LEXEME_REGION.ID.eq(lexemeRegionId))
				.execute();
	}

	public void deleteLexemeCollocMember(Long lexemeId) {
		mainDb
				.delete(COLLOCATION_MEMBER)
				.where(COLLOCATION_MEMBER.MEMBER_LEXEME_ID.eq(lexemeId))
				.execute();
	}

	public void deleteLexemeRelation(Long relationId) {
		mainDb
				.delete(LEX_RELATION)
				.where(LEX_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteDefinition(Long id) {
		mainDb
				.delete(DEFINITION)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void deleteDefinitionNote(Long id) {
		mainDb
				.delete(DEFINITION_NOTE)
				.where(DEFINITION_NOTE.ID.eq(id))
				.execute();
	}

	public void deleteFreeform(Long id) {
		mainDb
				.delete(FREEFORM)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public List<Long> deleteMeaningLexemesTag(Long meaningId, String datasetCode, String tagName) {

		List<Long> lexemeIds = mainDb
				.delete(LEXEME_TAG)
				.using(LEXEME)
				.where(
						LEXEME_TAG.TAG_NAME.eq(tagName)
								.and(LEXEME_TAG.LEXEME_ID.eq(LEXEME.ID))
								.and(LEXEME.MEANING_ID.eq(meaningId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.returning(LEXEME_TAG.LEXEME_ID)
				.fetch()
				.map(LexemeTagRecord::getLexemeId);

		return lexemeIds;
	}

	public void deleteMeaning(Long meaningId) {
		List<Long> definitionIds = getMeaningDefinitionIds(meaningId);
		for (Long definitionId : definitionIds) {
			deleteDefinitionFreeforms(definitionId);
			deleteDefinition(definitionId);
		}
		deleteMeaningFreeforms(meaningId);
		mainDb
				.delete(MEANING)
				.where(MEANING.ID.eq(meaningId))
				.execute();
	}

	public void deleteMeaningTag(Long meaningTagId) {
		mainDb
				.delete(MEANING_TAG)
				.where(MEANING_TAG.ID.eq(meaningTagId))
				.execute();
	}

	public void deleteMeaningNote(Long meaningNoteId) {
		mainDb
				.deleteFrom(MEANING_NOTE)
				.where(MEANING_NOTE.ID.eq(meaningNoteId))
				.execute();
	}

	public void deleteMeaningRelation(Long relationId) {
		mainDb
				.delete(MEANING_RELATION)
				.where(MEANING_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteMeaningDomain(Long meaningDomainId) {
		mainDb
				.delete(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.ID.eq(meaningDomainId))
				.execute();
	}

	public void deleteMeaningImage(Long meaningImageId) {
		mainDb
				.deleteFrom(MEANING_IMAGE)
				.where(MEANING_IMAGE.ID.eq(meaningImageId))
				.execute();
	}

	public void deleteMeaningSemanticType(Long meaningSemanticTypeId) {
		mainDb
				.delete(MEANING_SEMANTIC_TYPE)
				.where(MEANING_SEMANTIC_TYPE.ID.eq(meaningSemanticTypeId))
				.execute();
	}

	public void deleteMeaningForum(Long meaningForumId) {
		mainDb
				.delete(MEANING_FORUM)
				.where(MEANING_FORUM.ID.eq(meaningForumId))
				.execute();
	}

	public void deleteParadigm(Long paradigmId) {
		mainDb
				.delete(PARADIGM)
				.where(PARADIGM.ID.eq(paradigmId))
				.execute();
	}

	private List<Long> getMeaningDefinitionIds(Long meaningId) {
		return mainDb
				.select(DEFINITION.ID)
				.from(DEFINITION)
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.fetchInto(Long.class);
	}

	private void deleteDefinitionFreeforms(Long definitionId) {
		mainDb
				.delete(FREEFORM)
				.where(
						FREEFORM.ID.in(DSL.select(DEFINITION_FREEFORM.FREEFORM_ID)
								.from(DEFINITION_FREEFORM)
								.where(DEFINITION_FREEFORM.DEFINITION_ID.eq(definitionId))))
				.execute();
	}

	private void deleteMeaningFreeforms(Long meaningId) {
		mainDb
				.delete(FREEFORM)
				.where(
						FREEFORM.ID.in(DSL.select(MEANING_FREEFORM.FREEFORM_ID)
								.from(MEANING_FREEFORM)
								.where(MEANING_FREEFORM.MEANING_ID.eq(meaningId))))
				.execute();
	}

}
