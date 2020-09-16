package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.TAG;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Tag;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.records.DefinitionFreeformRecord;
import eki.ekilex.data.db.tables.records.FreeformRecord;
import eki.ekilex.data.db.tables.records.LexRelationRecord;
import eki.ekilex.data.db.tables.records.LexemeFreeformRecord;
import eki.ekilex.data.db.tables.records.LexemeTagRecord;
import eki.ekilex.data.db.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningRecord;
import eki.ekilex.data.db.tables.records.MeaningRelationRecord;
import eki.ekilex.data.db.tables.records.WordFreeformRecord;
import eki.ekilex.data.db.tables.records.WordGroupMemberRecord;
import eki.ekilex.data.db.tables.records.WordGroupRecord;
import eki.ekilex.data.db.tables.records.WordRelationRecord;

@Component
public class CudDbService extends AbstractDataDbService {

	public void updateFreeformTextValue(Long id, String value, String valuePrese) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateFreeformTextValueAndComplexity(Long id, String value, String valuePrese, Complexity complexity) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.set(FREEFORM.COMPLEXITY, complexity.name())
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateFreeform(Long id, String value, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.set(FREEFORM.LANG, lang)
				.set(FREEFORM.COMPLEXITY, complexity.name())
				.set(FREEFORM.IS_PUBLIC, isPublic)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateFreeform(Long id, String value, String valuePrese, Complexity complexity, boolean isPublic) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.set(FREEFORM.COMPLEXITY, complexity.name())
				.set(FREEFORM.IS_PUBLIC, isPublic)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateFreeform(Long id, String value, String valuePrese, String lang, boolean isPublic) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.set(FREEFORM.LANG, lang)
				.set(FREEFORM.IS_PUBLIC, isPublic)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateDefinition(Long id, String value, String valuePrese, String lang, Complexity complexity, String typeCode, boolean isPublic) {
		create.update(DEFINITION)
				.set(DEFINITION.VALUE, value)
				.set(DEFINITION.VALUE_PRESE, valuePrese)
				.set(DEFINITION.LANG, lang)
				.set(DEFINITION.COMPLEXITY, complexity.name())
				.set(DEFINITION.DEFINITION_TYPE_CODE, typeCode)
				.set(DEFINITION.IS_PUBLIC, isPublic)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void updateDefinitionOrderby(ListData item) {
		create
				.update(DEFINITION)
				.set(DEFINITION.ORDER_BY, item.getOrderby())
				.where(DEFINITION.ID.eq(item.getId()))
				.execute();
	}

	public void updateLexemeRelationOrderby(ListData item) {
		create
				.update(LEX_RELATION)
				.set(LEX_RELATION.ORDER_BY, item.getOrderby())
				.where(LEX_RELATION.ID.eq(item.getId()))
				.execute();
	}

	public void updateMeaningRelationOrderby(ListData item) {
		create
				.update(MEANING_RELATION)
				.set(MEANING_RELATION.ORDER_BY, item.getOrderby())
				.where(MEANING_RELATION.ID.eq(item.getId()))
				.execute();
	}

	public void updateWordRelationOrderby(ListData item) {
		create
				.update(WORD_RELATION)
				.set(WORD_RELATION.ORDER_BY, item.getOrderby())
				.where(WORD_RELATION.ID.eq(item.getId()))
				.execute();
	}

	public void updateWordEtymologyOrderby(ListData item) {
		create
				.update(WORD_ETYMOLOGY)
				.set(WORD_ETYMOLOGY.ORDER_BY, item.getOrderby())
				.where(WORD_ETYMOLOGY.ID.eq(item.getId()))
				.execute();
	}

	public void updateLexemeOrderby(ListData item) {
		create
				.update(LEXEME)
				.set(LEXEME.ORDER_BY, item.getOrderby())
				.where(LEXEME.ID.eq(item.getId()))
				.execute();
	}

	public void updateMeaningDomainOrderby(ListData item) {
		create
				.update(MEANING_DOMAIN)
				.set(MEANING_DOMAIN.ORDER_BY, item.getOrderby())
				.where(MEANING_DOMAIN.ID.eq(item.getId()))
				.execute();
	}

	public void updateFreeformOrderby(ListData item) {
		create
				.update(FREEFORM)
				.set(FREEFORM.ORDER_BY, item.getOrderby())
				.where(FREEFORM.ID.eq(item.getId()))
				.execute();
	}

	public void updateLexemeLevels(Long lexemeId, Integer level1, Integer level2) {
		create.update(LEXEME)
				.set(LEXEME.LEVEL1, level1)
				.set(LEXEME.LEVEL2, level2)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeLevel1(Long lexemeId, Integer level1) {
		create.update(LEXEME)
				.set(LEXEME.LEVEL1, level1)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeLevel2(Long lexemeId, Integer level2) {
		create.update(LEXEME)
				.set(LEXEME.LEVEL2, level2)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeFrequencyGroup(Long lexemeId, String frequencyGroupCode) {
		create.update(LEXEME)
				.set(LEXEME.FREQUENCY_GROUP_CODE, frequencyGroupCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeProcessState(Long lexemeId, boolean isPublic) {
		create.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		create.update(LEXEME)
				.set(LEXEME.VALUE_STATE_CODE, valueStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateWordValue(Long wordId, String value, String valuePrese) {
		create.update(FORM)
				.set(FORM.VALUE, value)
				.set(FORM.VALUE_PRESE, valuePrese)
				.from(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name())))
				.execute();
	}

	public void updateAsWordValue(Long wordId, String valueAsWord) {
		create.update(FORM)
				.set(FORM.VALUE, valueAsWord)
				.set(FORM.VALUE_PRESE, valueAsWord)
				.from(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.AS_WORD.name())))
				.execute();
	}

	public Long updateWordVocalForm(Long wordId, String vocalForm) {
		return create.update(FORM)
				.set(FORM.VOCAL_FORM, vocalForm)
				.from(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name())))
				.returning(FORM.ID)
				.fetchOne()
				.getId();
	}

	public Long updateWordMorphCode(Long wordId, String morphCode) {
		return create.update(FORM)
				.set(FORM.MORPH_CODE, morphCode)
				.from(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name())))
				.returning(FORM.ID)
				.fetchOne()
				.getId();
	}

	public void updateWordGender(Long wordId, String genderCode) {
		create.update(WORD)
				.set(WORD.GENDER_CODE, genderCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public Long updateWordType(Long wordId, String currentTypeCode, String newTypeCode) {
		Long wordWordTypeId = create
				.update(WORD_WORD_TYPE)
				.set(WORD_WORD_TYPE.WORD_TYPE_CODE, newTypeCode)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordId).and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(currentTypeCode)))
				.returning(WORD_WORD_TYPE.ID)
				.fetchOne()
				.getId();
		return wordWordTypeId;
	}

	public void updateWordAspect(Long wordId, String aspectCode) {
		create.update(WORD)
				.set(WORD.ASPECT_CODE, aspectCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordLang(Long wordId, String langCode) {
		create.update(WORD)
				.set(WORD.LANG, langCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateLexemeComplexity(Long lexemeId, String complexity) {
		create.update(LEXEME)
				.set(LEXEME.COMPLEXITY, complexity)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeDataset(Long lexemeId, String dataset) {
		create.update(LEXEME)
				.set(LEXEME.DATASET_CODE, dataset)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public Long updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		Long lexemePosId = create
				.update(LEXEME_POS)
				.set(LEXEME_POS.POS_CODE, newPos)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(currentPos)))
				.returning(LEXEME_POS.ID)
				.fetchOne()
				.getId();
		return lexemePosId;
	}

	public Long updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) {
		Long lexemeDerivId = create
				.update(LEXEME_DERIV)
				.set(LEXEME_DERIV.DERIV_CODE, newDeriv)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId).and(LEXEME_DERIV.DERIV_CODE.eq(currentDeriv)))
				.returning(LEXEME_DERIV.ID)
				.fetchOne()
				.getId();
		return lexemeDerivId;
	}

	public Long updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) {
		Long lexemeRegisterId = create
				.update(LEXEME_REGISTER)
				.set(LEXEME_REGISTER.REGISTER_CODE, newRegister)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId).and(LEXEME_REGISTER.REGISTER_CODE.eq(currentRegister)))
				.returning(LEXEME_REGISTER.ID)
				.fetchOne()
				.getId();
		return lexemeRegisterId;
	}

	public Long updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) {
		Long lexemeRegionId = create
				.update(LEXEME_REGION)
				.set(LEXEME_REGION.REGION_CODE, newRegion)
				.where(LEXEME_REGION.LEXEME_ID.eq(lexemeId).and(LEXEME_REGION.REGION_CODE.eq(currentRegion)))
				.returning(LEXEME_REGION.ID)
				.fetchOne()
				.getId();
		return lexemeRegionId;
	}

	public Long updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		Long meaningDomainId = create
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
		Long meaningSemanticTypeId = create
				.update(MEANING_SEMANTIC_TYPE)
				.set(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE, newSemanticType)
				.where(MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId).and(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE.eq(currentSemanticType)))
				.returning(MEANING_SEMANTIC_TYPE.ID)
				.fetchOne()
				.getId();
		return meaningSemanticTypeId;
	}

	public void updateImageTitle(Long imageFreeformId, String title) {
		create
				.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, title)
				.set(FREEFORM.VALUE_PRESE, title)
				.where(FREEFORM.PARENT_ID.eq(imageFreeformId)
						.and(FREEFORM.TYPE.eq(FreeformType.IMAGE_TITLE.name())))
				.execute();
	}

	public void updateWordRelationOrderBy(Long relationId, Long orderBy) {
		create
				.update(WORD_RELATION)
				.set(WORD_RELATION.ORDER_BY, orderBy)
				.where(WORD_RELATION.ID.eq(relationId))
				.execute();
	}

	public void updateLexemeWeight(Long lexemeId, BigDecimal lexemeWeight) {
		create
				.update(LEXEME)
				.set(LEXEME.WEIGHT, lexemeWeight)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateFormDisplayForm(Long formId, String displayForm) {
		create
				.update(FORM)
				.set(FORM.DISPLAY_FORM, displayForm)
				.where(FORM.ID.eq(formId))
				.execute();
	}

	public void updateFormValuePrese(Long formId, String valuePrese) {
		create
				.update(FORM)
				.set(FORM.VALUE_PRESE, valuePrese)
				.where(FORM.ID.eq(formId))
				.execute();
	}

	public void updateMeaningLexemesPublicity(Long meaningId, boolean isPublic) {
		create
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(LEXEME.IS_PUBLIC.ne(isPublic)))
				.execute();
	}

	public void updateWordLexemesPublicity(Long wordId, boolean isPublic) {
		create
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(LEXEME.IS_PUBLIC.ne(isPublic)))
				.execute();
	}

	public void adjustWordHomonymNrs(SimpleWord word) {

		String wordValue = word.getWordValue();
		String lang = word.getLang();

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");

		Field<Integer> dsobf = DSL
				.select(DSL.when(DSL.count(l.ID).gt(0), 1).otherwise(2))
				.from(l)
				.where(
						l.WORD_ID.eq(w.ID)
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l.DATASET_CODE.eq(DATASET_SSS)))
				.asField();

		Table<Record3<Long, Integer, Integer>> ww = DSL
				.select(
						w.ID,
						w.HOMONYM_NR,
						dsobf.as("ds_order_by"))
				.from(w)
				.where(
						w.LANG.eq(lang)
								.andExists(DSL
										.select(l.ID)
										.from(l)
										.where(
												l.WORD_ID.eq(w.ID)
														.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))))
								.andExists(DSL
										.select(f.ID)
										.from(p, f)
										.where(
												p.WORD_ID.eq(w.ID)
														.and(f.PARADIGM_ID.eq(p.ID))
														.and(f.MODE.eq(FormMode.WORD.name()))
														.and(f.VALUE.eq(wordValue)))))
				.asTable("w");

		Result<Record2<Long, Integer>> homonyms = create
				.select(
						ww.field("id", Long.class),
						ww.field("homonym_nr", Integer.class))
				.from(ww)
				.orderBy(
						ww.field("ds_order_by"),
						ww.field("homonym_nr"),
						ww.field("id"))
				.fetch();

		if (CollectionUtils.isNotEmpty(homonyms)) {
			int homonymNrIter = 1;
			for (Record2<Long, Integer> homonym : homonyms) {
				Long adjWordId = homonym.get("id", Long.class);
				Integer adjHomonymNr = homonym.get("homonym_nr", Integer.class);
				if (adjHomonymNr != homonymNrIter) {
					create.update(WORD).set(WORD.HOMONYM_NR, homonymNrIter).where(WORD.ID.eq(adjWordId)).execute();
				}
				homonymNrIter++;
			}
		}
	}

	public void adjustWordSecondaryLexemesComplexity(Long wordId) {

		Lexeme lp = LEXEME.as("lp");
		Lexeme ls = LEXEME.as("ls");
		Field<String> complDetail = DSL.field(DSL.val(Complexity.DETAIL.name()));
		Field<String> complAny = DSL.field(DSL.val(Complexity.ANY.name()));

		Table<Record2<Long, String>> lc = DSL
				.select(
						ls.ID.as("lex_id"),
						DSL.when(complDetail.eq(DSL.all(DSL.arrayAgg(lp.COMPLEXITY))), complDetail).otherwise(complAny).as("lex_compl"))
				.from(ls, lp)
				.where(
						lp.WORD_ID.eq(wordId)
								.and(lp.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(lp.IS_PUBLIC.isTrue())
								.and(ls.WORD_ID.eq(lp.WORD_ID))
								.and(ls.TYPE.eq(LEXEME_TYPE_SECONDARY)))
				.groupBy(ls.ID)
				.asTable("lc");

		create
				.update(LEXEME)
				.set(LEXEME.COMPLEXITY, lc.field("lex_compl", String.class))
				.from(lc)
				.where(LEXEME.ID.eq(lc.field("lex_id", Long.class)))
				.execute();
	}

	public WordLexemeMeaningIdTuple createWordAndLexeme(
			String value, String valuePrese, String valueAsWord, String lang, String morphCode, String dataset, boolean isPublic, Long meaningId) {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();

		Integer currentHomonymNumber = create
				.select(DSL.max(WORD.HOMONYM_NR))
				.from(WORD, PARADIGM, FORM)
				.where(
						WORD.LANG.eq(lang)
								.and(FORM.MODE.eq(FormMode.WORD.name()))
								.and(FORM.VALUE.eq(value))
								.and(PARADIGM.ID.eq(FORM.PARADIGM_ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.fetchOneInto(Integer.class);

		int homonymNumber = 1;
		if (currentHomonymNumber != null) {
			homonymNumber = currentHomonymNumber + 1;
		}

		Long wordId = create.insertInto(WORD, WORD.HOMONYM_NR, WORD.LANG).values(homonymNumber, lang).returning(WORD.ID).fetchOne().getId();

		Long paradigmId = create.insertInto(PARADIGM, PARADIGM.WORD_ID).values(wordId).returning(PARADIGM.ID).fetchOne().getId();

		create
				.insertInto(FORM, FORM.PARADIGM_ID, FORM.MODE, FORM.MORPH_CODE, FORM.MORPH_EXISTS, FORM.VALUE, FORM.VALUE_PRESE, FORM.DISPLAY_FORM)
				.values(paradigmId, FormMode.WORD.name(), morphCode, true, value, valuePrese, value)
				.execute();
		if (StringUtils.isNotBlank(valueAsWord)) {
			create
					.insertInto(FORM, FORM.PARADIGM_ID, FORM.MODE, FORM.MORPH_CODE, FORM.MORPH_EXISTS, FORM.VALUE, FORM.VALUE_PRESE)
					.values(paradigmId, FormMode.AS_WORD.name(), morphCode, true, valueAsWord, valueAsWord)
					.execute();
		}
		if (meaningId == null) {
			meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		}

		Long lexemeId = create
				.insertInto(
						LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.TYPE,
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.IS_PUBLIC, LEXEME.COMPLEXITY)
				.values(meaningId, wordId, dataset, LEXEME_TYPE_PRIMARY,
						1, 1, isPublic, COMPLEXITY_DETAIL)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();

		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);
		return wordLexemeMeaningId;
	}

	public Long createWordType(Long wordId, String typeCode) {
		Long wordWordTypeId = create
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordId)
						.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(typeCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (wordWordTypeId == null) {
			wordWordTypeId = create
					.insertInto(WORD_WORD_TYPE, WORD_WORD_TYPE.WORD_ID, WORD_WORD_TYPE.WORD_TYPE_CODE)
					.values(wordId, typeCode)
					.returning(WORD_WORD_TYPE.ID)
					.fetchOne()
					.getId();
		}
		return wordWordTypeId;
	}

	public Long createWordRelation(Long wordId, Long targetWordId, String wordRelationCode, String relationStatus) {

		Long wordRelationId = create
				.select(WORD_RELATION.ID)
				.from(WORD_RELATION)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
								.and(WORD_RELATION.WORD2_ID.eq(targetWordId))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(wordRelationCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (wordRelationId == null) {
			WordRelationRecord newRelation = create.newRecord(WORD_RELATION);
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
		WordGroupRecord wordGroupRecord = create.newRecord(WORD_GROUP);
		wordGroupRecord.setWordRelTypeCode(groupType);
		wordGroupRecord.store();
		return wordGroupRecord.getId();
	}

	public Long createWordRelationGroupMember(Long groupId, Long wordId) {
		WordGroupMemberRecord wordGroupMember = create.newRecord(WORD_GROUP_MEMBER);
		wordGroupMember.setWordGroupId(groupId);
		wordGroupMember.setWordId(wordId);
		wordGroupMember.store();
		return wordGroupMember.getId();
	}

	public Long createWordNote(Long wordId, String value, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.NOTE.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(lang);
		freeform.setComplexity(complexity.name());
		freeform.setIsPublic(isPublic);
		freeform.store();

		WordFreeformRecord wordFreeform = create.newRecord(WORD_FREEFORM);
		wordFreeform.setWordId(wordId);
		wordFreeform.setFreeformId(freeform.getId());
		wordFreeform.store();

		return freeform.getId();
	}

	public List<Long> createWordLexemesTag(Long wordId, String datasetCode, String tagName) {

		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");

		List<Long> lexemeIds = create
				.insertInto(LEXEME_TAG, LEXEME_TAG.TAG_NAME, LEXEME_TAG.LEXEME_ID)
				.select(DSL
						.select(DSL.val(tagName), l.ID)
						.from(l)
						.where(
								l.WORD_ID.eq(wordId)
										.and(l.DATASET_CODE.eq(datasetCode)
												.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
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

		List<Long> lexemeIds = create
				.insertInto(LEXEME_TAG, LEXEME_TAG.TAG_NAME, LEXEME_TAG.LEXEME_ID)
				.select(DSL
						.select(DSL.val(tagName), l.ID)
						.from(l)
						.where(
								l.MEANING_ID.eq(meaningId)
										.and(l.DATASET_CODE.eq(datasetCode)
												.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
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
		return create
				.insertInto(
						DEFINITION,
						DEFINITION.MEANING_ID,
						DEFINITION.LANG,
						DEFINITION.VALUE,
						DEFINITION.VALUE_PRESE,
						DEFINITION.DEFINITION_TYPE_CODE,
						DEFINITION.COMPLEXITY,
						DEFINITION.IS_PUBLIC)
				.values(meaningId, languageCode, value, valuePrese, definitionTypeCode, complexity.name(), isPublic)
				.returning(DEFINITION.ID)
				.fetchOne()
				.getId();
	}

	public void createDefinitionDataset(Long definitionId, String datasetCode) {
		create.insertInto(DEFINITION_DATASET, DEFINITION_DATASET.DEFINITION_ID, DEFINITION_DATASET.DATASET_CODE)
				.values(definitionId, datasetCode)
				.execute();
	}

	public Long createDefinitionNote(Long definitionId, String value, String valuePrese, String lang, boolean isPublic) {
		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.NOTE.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(lang);
		freeform.setIsPublic(isPublic);
		freeform.store();

		DefinitionFreeformRecord definitionFreeform = create.newRecord(DEFINITION_FREEFORM);
		definitionFreeform.setDefinitionId(definitionId);
		definitionFreeform.setFreeformId(freeform.getId());
		definitionFreeform.store();

		return freeform.getId();
	}

	public Long createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) {
		LexRelationRecord lexemeRelation = create.newRecord(LEX_RELATION);
		lexemeRelation.setLexeme1Id(lexemeId1);
		lexemeRelation.setLexeme2Id(lexemeId2);
		lexemeRelation.setLexRelTypeCode(relationType);
		lexemeRelation.store();
		return lexemeRelation.getId();
	}

	public Long createMeaning() {
		MeaningRecord meaning = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne();
		return meaning.getId();
	}

	public Long createMeaningRelation(Long meaningId1, Long meaningId2, String relationType) {
		MeaningRelationRecord meaningRelation = create.newRecord(MEANING_RELATION);
		meaningRelation.setMeaning1Id(meaningId1);
		meaningRelation.setMeaning2Id(meaningId2);
		meaningRelation.setMeaningRelTypeCode(relationType);
		meaningRelation.store();
		return meaningRelation.getId();
	}

	public Long createMeaningLearnerComment(Long meaningId, String value, String valuePrese, String lang, Complexity complexity) {
		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.LEARNER_COMMENT.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(lang);
		freeform.setComplexity(complexity.name());
		freeform.store();

		MeaningFreeformRecord meaningFreeform = create.newRecord(MEANING_FREEFORM);
		meaningFreeform.setMeaningId(meaningId);
		meaningFreeform.setFreeformId(freeform.getId());
		meaningFreeform.store();

		return freeform.getId();
	}

	public Long createMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = create
				.select(MEANING_DOMAIN.ID).from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode()))
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin())))
				.limit(1)
				.fetchOneInto(Long.class);
		if (meaningDomainId == null) {
			meaningDomainId = create
					.insertInto(MEANING_DOMAIN, MEANING_DOMAIN.MEANING_ID, MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
					.values(meaningId, domain.getOrigin(), domain.getCode())
					.returning(MEANING_DOMAIN.ID)
					.fetchOne()
					.getId();
		}
		return meaningDomainId;
	}

	public Long createMeaningNote(Long meaningId, String value, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.NOTE.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(lang);
		freeform.setComplexity(complexity.name());
		freeform.setIsPublic(isPublic);
		freeform.store();

		MeaningFreeformRecord meaningFreeform = create.newRecord(MEANING_FREEFORM);
		meaningFreeform.setMeaningId(meaningId);
		meaningFreeform.setFreeformId(freeform.getId());
		meaningFreeform.store();

		return freeform.getId();
	}

	public Long createMeaningSemanticType(Long meaningId, String semanticTypeCode) {
		Long meaningSemanticTypeCodeId = create
				.select(MEANING_SEMANTIC_TYPE.ID).from(MEANING_SEMANTIC_TYPE)
				.where(MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId)
						.and(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE.eq(semanticTypeCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (meaningSemanticTypeCodeId == null) {
			meaningSemanticTypeCodeId = create
					.insertInto(MEANING_SEMANTIC_TYPE, MEANING_SEMANTIC_TYPE.MEANING_ID, MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE)
					.values(meaningId, semanticTypeCode)
					.returning(MEANING_SEMANTIC_TYPE.ID)
					.fetchOne()
					.getId();
		}
		return meaningSemanticTypeCodeId;
	}

	public Long createLexeme(Long wordId, String datasetCode, Long meaningId, int lexemeLevel1) {

		if (meaningId == null) {
			meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		} else {
			Long existingLexemeId = create
					.select(LEXEME.ID)
					.from(LEXEME)
					.where(
							LEXEME.WORD_ID.eq(wordId)
									.and(LEXEME.DATASET_CODE.eq(datasetCode))
									.and(LEXEME.MEANING_ID.eq(meaningId))
									.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
					.fetchOptionalInto(Long.class)
					.orElse(null);
			if (existingLexemeId != null) {
				return null;
			}
		}
		Long lexemeId = create
				.insertInto(
						LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.TYPE,
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.IS_PUBLIC, LEXEME.COMPLEXITY)
				.values(meaningId, wordId, datasetCode, LEXEME_TYPE_PRIMARY,
						lexemeLevel1, 1, PUBLICITY_PUBLIC, COMPLEXITY_DETAIL)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
		return lexemeId;
	}

	public Long createUsage(Long lexemeId, String value, String valuePrese, String languageCode, Complexity complexity, boolean isPublic) {
		Long usageFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.LANG, FREEFORM.COMPLEXITY, FREEFORM.IS_PUBLIC)
				.values(FreeformType.USAGE.name(), value, valuePrese, languageCode, complexity.name(), isPublic)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(LEXEME_FREEFORM, LEXEME_FREEFORM.LEXEME_ID, LEXEME_FREEFORM.FREEFORM_ID).values(lexemeId, usageFreeformId).execute();
		return usageFreeformId;
	}

	public Long createUsageTranslation(Long usageId, String value, String valuePrese, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.LANG)
				.values(FreeformType.USAGE_TRANSLATION.name(), usageId, value, valuePrese, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long createUsageDefinition(Long usageId, String value, String valuePrese, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.LANG)
				.values(FreeformType.USAGE_DEFINITION.name(), usageId, value, valuePrese, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long createLexemeNote(Long lexemeId, String value, String valuePrese, String lang, Complexity complexity, boolean isPublic) {

		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.NOTE.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(lang);
		freeform.setComplexity(complexity.name());
		freeform.setIsPublic(isPublic);
		freeform.store();

		LexemeFreeformRecord lexemeFreeform = create.newRecord(LEXEME_FREEFORM);
		lexemeFreeform.setLexemeId(lexemeId);
		lexemeFreeform.setFreeformId(freeform.getId());
		lexemeFreeform.store();

		return freeform.getId();
	}

	public Long createLexemeGovernment(Long lexemeId, String value, Complexity complexity) {

		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.GOVERNMENT.name());
		freeform.setValueText(value);
		freeform.setValuePrese(value);
		freeform.setComplexity(complexity.name());
		freeform.store();

		LexemeFreeformRecord lexemeFreeform = create.newRecord(LEXEME_FREEFORM);
		lexemeFreeform.setLexemeId(lexemeId);
		lexemeFreeform.setFreeformId(freeform.getId());
		lexemeFreeform.store();

		return freeform.getId();
	}

	public Long createLexemeGrammar(Long lexemeId, String value, String valuePrese, Complexity complexity) {

		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.GRAMMAR.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setComplexity(complexity.name());
		freeform.store();

		LexemeFreeformRecord lexemeFreeform = create.newRecord(LEXEME_FREEFORM);
		lexemeFreeform.setLexemeId(lexemeId);
		lexemeFreeform.setFreeformId(freeform.getId());
		lexemeFreeform.store();

		return freeform.getId();
	}

	public Long createLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = create
				.select(LEXEME_POS.ID).from(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_POS.POS_CODE.eq(posCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemePosId == null) {
			lexemePosId = create
					.insertInto(LEXEME_POS, LEXEME_POS.LEXEME_ID, LEXEME_POS.POS_CODE)
					.values(lexemeId, posCode)
					.returning(LEXEME_POS.ID)
					.fetchOne()
					.getId();
		}
		return lexemePosId;
	}

	public Long createLexemeTag(Long lexemeId, String tagName) {
		Long lexemeTagId = create
				.select(LEXEME_TAG.ID)
				.from(LEXEME_TAG)
				.where(LEXEME_TAG.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_TAG.TAG_NAME.eq(tagName)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeTagId == null) {
			lexemeTagId = create
					.insertInto(LEXEME_TAG, LEXEME_TAG.LEXEME_ID, LEXEME_TAG.TAG_NAME)
					.values(lexemeId, tagName)
					.returning(LEXEME_TAG.ID)
					.fetchOne()
					.getId();
		}
		return lexemeTagId;
	}

	public List<String> createLexemeAutomaticTags(Long lexemeId) {

		LexemeTag lt = LEXEME_TAG.as("lt");
		Tag t = TAG.as("t");

		List<String> createdTagNames = create
				.insertInto(LEXEME_TAG, LEXEME_TAG.LEXEME_ID, LEXEME_TAG.TAG_NAME)
				.select(DSL
						.select(DSL.val(lexemeId), t.NAME)
						.from(t)
						.where(
								t.SET_AUTOMATICALLY.isTrue()
										.andNotExists(DSL
												.select(lt.ID)
												.from(lt)
												.where(lt.LEXEME_ID.eq(lexemeId)
														.and(lt.TAG_NAME.eq(t.NAME))))))
				.returning(LEXEME_TAG.TAG_NAME)
				.fetch()
				.map(LexemeTagRecord::getTagName);

		return createdTagNames;
	}

	public Long createLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = create
				.select(LEXEME_DERIV.ID).from(LEXEME_DERIV)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.eq(derivCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeDerivId == null) {
			lexemeDerivId = create
					.insertInto(LEXEME_DERIV, LEXEME_DERIV.LEXEME_ID, LEXEME_DERIV.DERIV_CODE)
					.values(lexemeId, derivCode)
					.returning(LEXEME_DERIV.ID)
					.fetchOne()
					.getId();
		}
		return lexemeDerivId;
	}

	public Long createLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = create
				.select(LEXEME_REGISTER.ID).from(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeRegisterId == null) {
			lexemeRegisterId = create
					.insertInto(LEXEME_REGISTER, LEXEME_REGISTER.LEXEME_ID, LEXEME_REGISTER.REGISTER_CODE)
					.values(lexemeId, registerCode)
					.returning(LEXEME_REGISTER.ID)
					.fetchOne()
					.getId();
		}
		return lexemeRegisterId;
	}

	public Long createLexemeRegion(Long lexemeId, String regionCode) {
		Long lexemeRegionId = create
				.select(LEXEME_REGION.ID).from(LEXEME_REGION)
				.where(LEXEME_REGION.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_REGION.REGION_CODE.eq(regionCode)))
				.limit(1)
				.fetchOneInto(Long.class);
		if (lexemeRegionId == null) {
			lexemeRegionId = create
					.insertInto(LEXEME_REGION, LEXEME_REGION.LEXEME_ID, LEXEME_REGION.REGION_CODE)
					.values(lexemeId, regionCode)
					.returning(LEXEME_REGION.ID)
					.fetchOne()
					.getId();
		}
		return lexemeRegionId;
	}

	public Long createImageTitle(Long imageId, String value) {
		return create
				.insertInto(FREEFORM, FREEFORM.PARENT_ID, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE)
				.values(imageId, FreeformType.IMAGE_TITLE.name(), value, value)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long createOdWordRecommendation(Long wordId, String value, String valuePrese, Complexity complexity) {

		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.OD_WORD_RECOMMENDATION.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setComplexity(complexity.name());
		freeform.store();

		WordFreeformRecord wordFreeform = create.newRecord(WORD_FREEFORM);
		wordFreeform.setWordId(wordId);
		wordFreeform.setFreeformId(freeform.getId());
		wordFreeform.store();

		return freeform.getId();
	}

	public Long createOdLexemeRecommendation(Long lexemeId, String value, String valuePrese, Complexity complexity) {

		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.OD_LEXEME_RECOMMENDATION.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setComplexity(complexity.name());
		freeform.store();

		LexemeFreeformRecord lexemeFreeform = create.newRecord(LEXEME_FREEFORM);
		lexemeFreeform.setLexemeId(lexemeId);
		lexemeFreeform.setFreeformId(freeform.getId());
		lexemeFreeform.store();

		return freeform.getId();
	}

	public Long createOdUsageDefinition(Long usageId, String value, String valuePrese) {

		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE)
				.values(FreeformType.OD_USAGE_DEFINITION.name(), usageId, value, valuePrese)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long createOdUsageAlternative(Long usageId, String value, String valuePrese) {

		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE)
				.values(FreeformType.OD_USAGE_ALTERNATIVE.name(), usageId, value, valuePrese)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public void createWordRelationParam(Long wordRelationId, String paramName, BigDecimal paramValue) {

		create.insertInto(WORD_RELATION_PARAM, WORD_RELATION_PARAM.WORD_RELATION_ID, WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.values(wordRelationId, paramName, paramValue)
				.execute();
	}

	public void deleteWord(SimpleWord word) {
		Long wordId = word.getWordId();
		create.delete(LIFECYCLE_LOG)
				.where(LIFECYCLE_LOG.ID.in(DSL
						.select(WORD_LIFECYCLE_LOG.LIFECYCLE_LOG_ID)
						.from(WORD_LIFECYCLE_LOG)
						.where(WORD_LIFECYCLE_LOG.WORD_ID.eq(wordId))))
				.execute();
		create.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(WORD_FREEFORM.FREEFORM_ID)
						.from(WORD_FREEFORM)
						.where(WORD_FREEFORM.WORD_ID.eq(wordId))))
				.execute();
		create.delete(WORD)
				.where(WORD.ID.eq(wordId))
				.execute();
		adjustWordHomonymNrs(word);
	}

	public void deleteWordWordType(Long wordWordTypeId) {
		create.delete(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.ID.eq(wordWordTypeId))
				.execute();
	}

	public void deleteWordRelation(Long relationId) {
		create.delete(WORD_RELATION)
				.where(WORD_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteWordRelationGroupMember(Long relationId) {
		create.delete(WORD_GROUP_MEMBER)
				.where(WORD_GROUP_MEMBER.ID.eq(relationId))
				.execute();
	}

	public void deleteWordRelationGroup(Long groupId) {
		create.delete(WORD_GROUP)
				.where(WORD_GROUP.ID.eq(groupId))
				.execute();
	}

	public List<Long> deleteWordLexemesTag(Long wordId, String datasetCode, String tagName) {

		List<Long> lexemeIds = create
				.delete(LEXEME_TAG)
				.using(LEXEME)
				.where(
						LEXEME_TAG.TAG_NAME.eq(tagName)
								.and(LEXEME_TAG.LEXEME_ID.eq(LEXEME.ID))
								.and(LEXEME.WORD_ID.eq(wordId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.returning(LEXEME_TAG.LEXEME_ID)
				.fetch()
				.map(LexemeTagRecord::getLexemeId);

		return lexemeIds;
	}

	public List<Long> deleteMeaningLexemesTag(Long meaningId, String datasetCode, String tagName) {

		List<Long> lexemeIds = create
				.delete(LEXEME_TAG)
				.using(LEXEME)
				.where(
						LEXEME_TAG.TAG_NAME.eq(tagName)
								.and(LEXEME_TAG.LEXEME_ID.eq(LEXEME.ID))
								.and(LEXEME.MEANING_ID.eq(meaningId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.returning(LEXEME_TAG.LEXEME_ID)
				.fetch()
				.map(LexemeTagRecord::getLexemeId);

		return lexemeIds;
	}

	public void deleteLexeme(Long lexemeId) {
		create.delete(LIFECYCLE_LOG)
				.where(LIFECYCLE_LOG.ID.in(DSL
						.select(LEXEME_LIFECYCLE_LOG.LIFECYCLE_LOG_ID)
						.from(LEXEME_LIFECYCLE_LOG)
						.where(LEXEME_LIFECYCLE_LOG.LEXEME_ID.eq(lexemeId))))
				.execute();
		create.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(LEXEME_FREEFORM.FREEFORM_ID)
						.from(LEXEME_FREEFORM)
						.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId))))
				.execute();
		create.delete(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void deleteLexemePos(Long lexemePosId) {
		create.delete(LEXEME_POS)
				.where(LEXEME_POS.ID.eq(lexemePosId))
				.execute();
	}

	public void deleteLexemeTag(Long lexemeTagId) {
		create.delete(LEXEME_TAG)
				.where(LEXEME_TAG.ID.eq(lexemeTagId))
				.execute();
	}

	public void deleteLexemeDeriv(Long lexemeDerivId) {
		create.delete(LEXEME_DERIV)
				.where(LEXEME_DERIV.ID.eq(lexemeDerivId))
				.execute();
	}

	public void deleteLexemeRegister(Long lexemeRegisterId) {
		create.delete(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.ID.eq(lexemeRegisterId))
				.execute();
	}

	public void deleteLexemeRegion(Long lexemeRegionId) {
		create.delete(LEXEME_REGION)
				.where(LEXEME_REGION.ID.eq(lexemeRegionId))
				.execute();
	}

	public void deleteLexemeRelation(Long relationId) {
		create.delete(LEX_RELATION)
				.where(LEX_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteDefinition(Long id) {
		create.delete(DEFINITION)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void deleteFreeform(Long id) {
		create.delete(FREEFORM)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void deleteMeaning(Long meaningId) {
		List<Long> definitionIds = getMeaningDefinitionIds(meaningId);
		for (Long definitionId : definitionIds) {
			deleteDefinitionFreeforms(definitionId);
			deleteDefinition(definitionId);
		}
		deleteMeaningFreeforms(meaningId);

		create.delete(LIFECYCLE_LOG)
				.where(LIFECYCLE_LOG.ID.in(DSL
						.select(MEANING_LIFECYCLE_LOG.LIFECYCLE_LOG_ID)
						.from(MEANING_LIFECYCLE_LOG)
						.where(MEANING_LIFECYCLE_LOG.MEANING_ID.eq(meaningId))))
				.execute();
		create.delete(MEANING)
				.where(MEANING.ID.eq(meaningId))
				.execute();
	}

	public void deleteMeaningRelation(Long relationId) {
		create.delete(MEANING_RELATION)
				.where(MEANING_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteMeaningDomain(Long meaningDomainId) {
		create.delete(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.ID.eq(meaningDomainId))
				.execute();
	}

	public void deleteMeaningSemanticType(Long meaningSemanticTypeId) {
		create.delete(MEANING_SEMANTIC_TYPE)
				.where(MEANING_SEMANTIC_TYPE.ID.eq(meaningSemanticTypeId))
				.execute();
	}

	public Long deleteImageTitle(Long imageFreeformId) {
		return create.delete(FREEFORM)
				.where(FREEFORM.PARENT_ID.eq(imageFreeformId)
						.and(FREEFORM.TYPE.eq(FreeformType.IMAGE_TITLE.name())))
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	private List<Long> getMeaningDefinitionIds(Long meaningId) {
		return create
				.select(DEFINITION.ID)
				.from(DEFINITION)
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.fetchInto(Long.class);
	}

	private void deleteDefinitionFreeforms(Long definitionId) {
		create.delete(FREEFORM)
				.where(
						FREEFORM.ID.in(DSL.select(DEFINITION_FREEFORM.FREEFORM_ID)
								.from(DEFINITION_FREEFORM)
								.where(DEFINITION_FREEFORM.DEFINITION_ID.eq(definitionId))))
				.execute();
	}

	private void deleteMeaningFreeforms(Long meaningId) {
		create.delete(FREEFORM)
				.where(
						FREEFORM.ID.in(DSL.select(MEANING_FREEFORM.FREEFORM_ID)
								.from(MEANING_FREEFORM)
								.where(MEANING_FREEFORM.MEANING_ID.eq(meaningId))))
				.execute();
	}

}
