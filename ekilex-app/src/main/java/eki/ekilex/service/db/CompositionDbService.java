package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_POS_GROUP;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_RELATION;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_PROCESS_LOG;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;
import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.DbConstant;
import eki.common.constant.FormMode;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.tables.records.DefinitionDatasetRecord;
import eki.ekilex.data.db.tables.records.DefinitionFreeformRecord;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.DefinitionSourceLinkRecord;
import eki.ekilex.data.db.tables.records.FreeformRecord;
import eki.ekilex.data.db.tables.records.FreeformSourceLinkRecord;
import eki.ekilex.data.db.tables.records.LexRelationRecord;
import eki.ekilex.data.db.tables.records.LexemeDerivRecord;
import eki.ekilex.data.db.tables.records.LexemeFreeformRecord;
import eki.ekilex.data.db.tables.records.LexemePosRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.data.db.tables.records.LexemeRegisterRecord;
import eki.ekilex.data.db.tables.records.LexemeSourceLinkRecord;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningRecord;
import eki.ekilex.data.db.tables.records.MeaningRelationRecord;

@Component
public class CompositionDbService implements DbConstant {

	@Autowired
	private DSLContext create;

	public LexemeRecord getLexeme(Long lexemeId) {
		return create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
	}

	public List<LexemeRecord> getMeaningLexemes(Long meaningId) {
		return create
				.selectFrom(LEXEME).where(
					LEXEME.MEANING_ID.eq(meaningId)
					.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetch();
	}

	public List<LexemeRecord> getMeaningLexemes(Long meaningId, String datasetCode) {
		return create
				.selectFrom(LEXEME).where(
					LEXEME.MEANING_ID.eq(meaningId)
					.and(LEXEME.DATASET_CODE.eq(datasetCode))
					.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetch();
	}

	public List<LexemeRecord> getWordLexemes(Long wordId) {
		return create
				.selectFrom(LEXEME).where(
					LEXEME.WORD_ID.eq(wordId)
					.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetch();
	}

	public Long getLexemeId(Long wordId, Long meaningId, String datasetCode) {
		return create
				.select(LEXEME.ID)
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.MEANING_ID.eq(meaningId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public List<DefinitionRecord> getMeaningDefinitions(Long meaningId) {
		return create.selectFrom(DEFINITION).where(DEFINITION.MEANING_ID.eq(meaningId)).orderBy(DEFINITION.ORDER_BY).fetch();
	}

	public String getFirstDefinitionOfMeaning(Long meaningId) {
		String definition = create
				.select(DEFINITION.VALUE_PRESE)
				.from(DEFINITION)
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.orderBy(DEFINITION.ORDER_BY)
				.limit(1)
				.fetchOneInto(String.class);
		return definition;
	}

	public List<LexemeRecord> getLexemesWithHigherLevel1(Long wordId, Integer level1) {
		return create
				.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.LEVEL1.gt(level1))
						.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetch();
	}

	public List<LexemeRecord> getLexemesWithHigherLevel2(Long wordId, Integer level1, Integer level2) {
		return create
				.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.LEVEL1.eq(level1))
						.and(LEXEME.LEVEL2.gt(level2))
						.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetch();
	}

	public List<LexemeRecord> getLexemesWithHigherLevel3(Long wordId, Integer level1, Integer level2, Integer level3) {
		return create
				.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.LEVEL1.eq(level1))
						.and(LEXEME.LEVEL2.eq(level2))
						.and(LEXEME.LEVEL3.gt(level3))
						.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetch();
	}

	public List<IdPair> getMeaningsCommonWordsLexemeIdPairs(Long meaningId, Long sourceMeaningId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Meaning m1 = MEANING.as("m1");
		Meaning m2 = MEANING.as("m2");

		SelectConditionStep<Record1<Long>> wordIds = DSL.
				selectDistinct(l1.WORD_ID)
				.from(l1, m1)
				.where(l1.MEANING_ID.eq(meaningId)
						.and(
								l1.MEANING_ID.eq(m1.ID)
								.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY)))
						.andExists(DSL
								.select(l2.ID)
								.from(l2, m2)
								.where(m2.ID.eq(sourceMeaningId)
										.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY))
										.and(l2.MEANING_ID.eq(m2.ID))
										.and(l2.WORD_ID.eq(l1.WORD_ID)))));

		return create
				.select(l1.ID.as("id1"), l2.ID.as("id2"))
				.from(l1, l2)
				.where(l1.WORD_ID.in(wordIds)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1.DATASET_CODE.eq(l2.DATASET_CODE))
						.and(l1.MEANING_ID.eq(meaningId))
						.and(l2.MEANING_ID.eq(sourceMeaningId))
						.and(l2.WORD_ID.eq(l1.WORD_ID))
						.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetchInto(IdPair.class);
	}

	public void joinMeanings(Long meaningId, Long sourceMeaningId) {

		create.update(LEXEME).set(LEXEME.MEANING_ID, meaningId).where(LEXEME.MEANING_ID.eq(sourceMeaningId)).execute();
		joinMeaningDefinitions(meaningId, sourceMeaningId);
		joinMeaningDomains(meaningId, sourceMeaningId);
		joinMeaningFreeforms(meaningId, sourceMeaningId);
		joinMeaningRelations(meaningId, sourceMeaningId);
		create.update(MEANING_LIFECYCLE_LOG).set(MEANING_LIFECYCLE_LOG.MEANING_ID, meaningId).where(MEANING_LIFECYCLE_LOG.MEANING_ID.eq(sourceMeaningId)).execute();
		create.update(MEANING_PROCESS_LOG).set(MEANING_PROCESS_LOG.MEANING_ID, meaningId).where(MEANING_PROCESS_LOG.MEANING_ID.eq(sourceMeaningId)).execute();
		create.delete(MEANING).where(MEANING.ID.eq(sourceMeaningId)).execute();
	}

	public void joinLexemes(Long lexemeId, Long sourceLexemeId) {

		joinLexemeCollocations(lexemeId, sourceLexemeId);
		joinLexemeSourceLinks(lexemeId, sourceLexemeId);
		joinLexemeRegisters(lexemeId, sourceLexemeId);
		joinLexemePos(lexemeId, sourceLexemeId);
		joinLexemeFreeforms(lexemeId, sourceLexemeId);
		joinLexemeDerivs(lexemeId, sourceLexemeId);
		joinLexemeRegions(lexemeId, sourceLexemeId);
		create.update(LEXEME_LIFECYCLE_LOG).set(LEXEME_LIFECYCLE_LOG.LEXEME_ID, lexemeId).where(LEXEME_LIFECYCLE_LOG.LEXEME_ID.eq(sourceLexemeId)).execute();
		create.update(LEXEME_PROCESS_LOG).set(LEXEME_PROCESS_LOG.LEXEME_ID, lexemeId).where(LEXEME_PROCESS_LOG.LEXEME_ID.eq(sourceLexemeId)).execute();
		joinLexemeRelations(lexemeId, sourceLexemeId);
		create.delete(LEXEME).where(LEXEME.ID.eq(sourceLexemeId)).execute();
	}

	private void joinLexemeCollocations(Long lexemeId, Long sourceLexemeId) {

		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		create.update(lc1)
				.set(lc1.LEXEME_ID, lexemeId)
				.where(lc1.LEXEME_ID.eq(sourceLexemeId))
				.andNotExists(DSL
						.select(lc2.ID)
						.from(lc2)
						.where(lc2.LEXEME_ID.eq(lexemeId)
								.and(lc2.COLLOCATION_ID.eq(lc1.COLLOCATION_ID))))
				.execute();

		create.update(LEX_COLLOC_POS_GROUP).set(LEX_COLLOC_POS_GROUP.LEXEME_ID, lexemeId).where(LEX_COLLOC_POS_GROUP.LEXEME_ID.eq(sourceLexemeId)).execute();
	}

	private void joinLexemeRegions(Long lexemeId, Long sourceLexemeId) {

		create.update(LEXEME_REGION)
				.set(LEXEME_REGION.LEXEME_ID, lexemeId)
				.where(LEXEME_REGION.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_REGION.REGION_CODE.notIn(
								DSL.select(LEXEME_REGION.REGION_CODE).from(LEXEME_REGION).where(LEXEME_REGION.LEXEME_ID.eq(lexemeId)))))
				.execute();
	}

	private void joinLexemeRelations(Long lexemeId, Long sourceLexemeId) {

		create.update(LEX_RELATION).set(LEX_RELATION.LEXEME1_ID, lexemeId).where(LEX_RELATION.LEXEME1_ID.eq(sourceLexemeId)).execute();
		create.update(LEX_RELATION).set(LEX_RELATION.LEXEME2_ID, lexemeId).where(LEX_RELATION.LEXEME2_ID.eq(sourceLexemeId)).execute();
	}

	private void joinLexemeDerivs(Long lexemeId, Long sourceLexemeId) {

		create.update(LEXEME_DERIV)
				.set(LEXEME_DERIV.LEXEME_ID, lexemeId)
				.where(LEXEME_DERIV.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.notIn(
								DSL.select(LEXEME_DERIV.DERIV_CODE).from(LEXEME_DERIV).where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)))))
				.execute();
	}

	private void joinLexemeFreeforms(Long lexemeId, Long sourceLexemeId) {

		Result<FreeformRecord> lexemeFreeforms = create.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(LEXEME_FREEFORM.FREEFORM_ID).from(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId))))
				.fetch();
		Result<FreeformRecord> sourceLexemeFreeforms = create.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(LEXEME_FREEFORM.FREEFORM_ID).from(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId))))
				.fetch();

		List<Long> nonDublicateFreeformIds = sourceLexemeFreeforms.stream()
				.filter(slf -> lexemeFreeforms.stream()
						.noneMatch(
								lf -> lf.getType().equals(slf.getType()) &&
								((Objects.nonNull(lf.getValueText()) && lf.getValueText().equals(slf.getValueText())) ||
								(Objects.nonNull(lf.getValueNumber()) && lf.getValueNumber().equals(slf.getValueNumber())) ||
								(Objects.nonNull(lf.getClassifCode()) && lf.getClassifCode().equals(slf.getClassifCode())) ||
								(Objects.nonNull(lf.getValueDate()) && lf.getValueDate().equals(slf.getValueDate())))))
				.map(FreeformRecord::getId)
				.collect(Collectors.toList());

		create.update(LEXEME_FREEFORM)
				.set(LEXEME_FREEFORM.LEXEME_ID, lexemeId)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_FREEFORM.FREEFORM_ID.in(nonDublicateFreeformIds)))
				.execute();

		create.delete(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId)).execute();
	}

	private void joinLexemePos(Long lexemeId, Long sourceLexemeId) {

		create.update(LEXEME_POS)
				.set(LEXEME_POS.LEXEME_ID, lexemeId)
				.where(LEXEME_POS.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_POS.POS_CODE.notIn(
								DSL.select(LEXEME_POS.POS_CODE).from(LEXEME_POS).where(LEXEME_POS.LEXEME_ID.eq(lexemeId))))).execute();
	}

	private void joinLexemeRegisters(Long lexemeId, Long sourceLexemeId) {

		create.update(LEXEME_REGISTER)
				.set(LEXEME_REGISTER.LEXEME_ID, lexemeId)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.notIn(
								DSL.select(LEXEME_REGISTER.REGISTER_CODE).from(LEXEME_REGISTER).where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)))))
				.execute();
	}

	private void joinLexemeSourceLinks(Long lexemeId, Long sourceLexemeId) {

		LexemeSourceLink lsl1 = LEXEME_SOURCE_LINK.as("lsl1");
		LexemeSourceLink lsl2 = LEXEME_SOURCE_LINK.as("lsl2");
		create.update(lsl1)
				.set(lsl1.LEXEME_ID, lexemeId)
				.where(lsl1.LEXEME_ID.eq(sourceLexemeId))
				.andNotExists(DSL
						.select(lsl2.ID)
						.from(lsl2)
						.where(lsl2.LEXEME_ID.eq(lexemeId)
								.and(lsl2.TYPE.eq(lsl1.TYPE))
								.and(lsl2.VALUE.eq(lsl1.VALUE))))
				.execute();
	}

	private void joinMeaningRelations(Long meaningId, Long sourceMeaningId) {
		create.update(MEANING_RELATION).set(MEANING_RELATION.MEANING1_ID, meaningId).where(MEANING_RELATION.MEANING1_ID.eq(sourceMeaningId)).execute();
		create.update(MEANING_RELATION).set(MEANING_RELATION.MEANING2_ID, meaningId).where(MEANING_RELATION.MEANING2_ID.eq(sourceMeaningId)).execute();
	}

	private void joinMeaningFreeforms(Long meaningId, Long sourceMeaningId) {
		Result<FreeformRecord> meaningFreeforms = create.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(MEANING_FREEFORM.FREEFORM_ID).from(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(meaningId))))
				.fetch();
		Result<FreeformRecord> sourceMeaningFreeforms = create.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(MEANING_FREEFORM.FREEFORM_ID).from(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId))))
				.fetch();
		List<Long> nonDublicateFreeformIds = sourceMeaningFreeforms.stream()
				.filter(sf -> meaningFreeforms.stream()
						.noneMatch(mf -> mf.getType().equals(sf.getType()) && ((Objects.nonNull(mf.getValueText()) && mf.getValueText().equals(sf.getValueText())) ||
								(Objects.nonNull(mf.getValueNumber()) && mf.getValueNumber().equals(sf.getValueNumber())) ||
								(Objects.nonNull(mf.getClassifCode()) && mf.getClassifCode().equals(sf.getClassifCode())) ||
								(Objects.nonNull(mf.getValueDate()) && mf.getValueDate().equals(sf.getValueDate())))))
				.map(FreeformRecord::getId)
				.collect(Collectors.toList());
		create.update(MEANING_FREEFORM).set(MEANING_FREEFORM.MEANING_ID, meaningId)
				.where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId).and(MEANING_FREEFORM.FREEFORM_ID.in(nonDublicateFreeformIds))).execute();
		create.delete(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId)).execute();
	}

	private void joinMeaningDomains(Long meaningId, Long sourceMeaningId) {
		create.update(MEANING_DOMAIN).set(MEANING_DOMAIN.MEANING_ID, meaningId)
				.where(
						MEANING_DOMAIN.MEANING_ID.eq(sourceMeaningId)
								.and(DSL.row(MEANING_DOMAIN.DOMAIN_CODE, MEANING_DOMAIN.DOMAIN_ORIGIN).notIn(
										DSL.select(MEANING_DOMAIN.DOMAIN_CODE, MEANING_DOMAIN.DOMAIN_ORIGIN).from(MEANING_DOMAIN).where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)))))
				.execute();
		create.delete(MEANING_DOMAIN).where(MEANING_DOMAIN.MEANING_ID.eq(sourceMeaningId)).execute();
	}

	private void joinMeaningDefinitions(Long meaningId, Long sourceMeaningId) {
		create.update(DEFINITION).set(DEFINITION.MEANING_ID, meaningId)
				.where(DEFINITION.MEANING_ID.eq(sourceMeaningId)
						.and(DEFINITION.VALUE.notIn(DSL.select(DEFINITION.VALUE).from(DEFINITION).where(DEFINITION.MEANING_ID.eq(meaningId)))))
				.execute();
		create.delete(DEFINITION).where(DEFINITION.MEANING_ID.eq(sourceMeaningId)).execute();
	}

	public void separateLexemeMeanings(Long lexemeId) {

		Long newMeaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		Long lexemeMeaningId = create.select(LEXEME.MEANING_ID).from(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne().value1();

		separateMeaningDefinitions(newMeaningId, lexemeMeaningId);
		separateMeaningDomains(newMeaningId, lexemeMeaningId);
		separateMeaningRelations(newMeaningId, lexemeMeaningId);
		separateMeaningFreeforms(newMeaningId, lexemeMeaningId);
		create.update(LEXEME).set(LEXEME.MEANING_ID, newMeaningId).where(LEXEME.ID.eq(lexemeId)).execute();
	}

	private void separateMeaningFreeforms(Long newMeaningId, Long lexemeMeaningId) {
		Result<MeaningFreeformRecord> freeforms = create.selectFrom(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(lexemeMeaningId)).fetch();
		freeforms.forEach(f -> {
			Long freeformId = cloneFreeform(f.getFreeformId(), null);
			create.insertInto(MEANING_FREEFORM, MEANING_FREEFORM.MEANING_ID, MEANING_FREEFORM.FREEFORM_ID).values(newMeaningId, freeformId).execute();
		});
	}

	private void separateMeaningRelations(Long newMeaningId, Long lexemeMeaningId) {
		Result<MeaningRelationRecord> relations = create.selectFrom(MEANING_RELATION).where(MEANING_RELATION.MEANING1_ID.eq(lexemeMeaningId)).fetch();
		relations.forEach(r -> {
			create
					.insertInto(MEANING_RELATION, MEANING_RELATION.MEANING1_ID, MEANING_RELATION.MEANING2_ID, MEANING_RELATION.MEANING_REL_TYPE_CODE)
					.values(newMeaningId, r.getMeaning2Id(), r.getMeaningRelTypeCode())
					.execute();
		});
		relations = create.selectFrom(MEANING_RELATION).where(MEANING_RELATION.MEANING2_ID.eq(lexemeMeaningId)).fetch();
		relations.forEach(r -> {
			create
					.insertInto(MEANING_RELATION, MEANING_RELATION.MEANING1_ID, MEANING_RELATION.MEANING2_ID, MEANING_RELATION.MEANING_REL_TYPE_CODE)
					.values(r.getMeaning1Id(), newMeaningId, r.getMeaningRelTypeCode())
					.execute();
		});
	}

	private void separateMeaningDomains(Long newMeaningId, Long lexemeMeaningId) {
		Result<MeaningDomainRecord> domains = create.selectFrom(MEANING_DOMAIN).where(MEANING_DOMAIN.MEANING_ID.eq(lexemeMeaningId)).fetch();
		domains.forEach(d -> {
			create
					.insertInto(MEANING_DOMAIN, MEANING_DOMAIN.MEANING_ID, MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
					.values(newMeaningId, d.getDomainOrigin(), d.getDomainCode())
					.execute();
		});
	}

	private void separateMeaningDefinitions(Long newMeaningId, Long lexemeMeaningId) {
		Result<DefinitionRecord> definitions = create.selectFrom(DEFINITION).where(DEFINITION.MEANING_ID.eq(lexemeMeaningId)).fetch();
		definitions.forEach(d -> {
			create
					.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.VALUE, DEFINITION.VALUE_PRESE, DEFINITION.LANG, DEFINITION.DEFINITION_TYPE_CODE, DEFINITION.COMPLEXITY)
					.values(newMeaningId, d.getValue(), d.getValuePrese(), d.getLang(), d.getDefinitionTypeCode(), d.getComplexity())
					.execute();
		});
	}

	public Long cloneLexeme(Long lexemeId, Long meaningId) {

		LexemeRecord lexeme = create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
		LexemeRecord clonedLexeme = lexeme.copy();
		clonedLexeme.setMeaningId(meaningId);
		clonedLexeme.changed(LEXEME.ORDER_BY, false);
		clonedLexeme.store();
		return clonedLexeme.getId();
	}

	public Long cloneEmptyLexeme(Long lexemeId, Long meaningId) {

		LexemeRecord lexeme = create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
		LexemeRecord clonedLexeme = lexeme.copy();
		clonedLexeme.setMeaningId(meaningId);
		clonedLexeme.changed(LEXEME.ORDER_BY, false);
		clonedLexeme.changed(LEXEME.FREQUENCY_GROUP_CODE, false);
		clonedLexeme.changed(LEXEME.CORPUS_FREQUENCY, false);
		clonedLexeme.setProcessStateCode(PROCESS_STATE_IN_WORK);
		clonedLexeme.store();
		return clonedLexeme.getId();
	}

	public void cloneLexemeDerivs(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeDerivRecord> lexemeDerivatives = create.selectFrom(LEXEME_DERIV)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_DERIV.ID)
				.fetch();
		lexemeDerivatives.stream().map(LexemeDerivRecord::copy).forEach(clonedLexemeDeriv -> {
			clonedLexemeDeriv.setLexemeId(clonedLexemeId);
			clonedLexemeDeriv.store();
		});
	}

	public void cloneLexemeFreeforms(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeFreeformRecord> lexemeFreeforms = create.selectFrom(LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_FREEFORM.ID)
				.fetch();
		lexemeFreeforms.forEach(lexemeFreeform -> {
			Long clonedFreeformId = cloneFreeform(lexemeFreeform.getFreeformId(), null);
			LexemeFreeformRecord clonedLexemeFreeform = create.newRecord(LEXEME_FREEFORM);
			clonedLexemeFreeform.setLexemeId(clonedLexemeId);
			clonedLexemeFreeform.setFreeformId(clonedFreeformId);
			clonedLexemeFreeform.store();
		});
	}

	public void cloneLexemePoses(Long lexemeId, Long clonedLexemeId) {

		Result<LexemePosRecord> lexemePoses = create.selectFrom(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_POS.ORDER_BY)
				.fetch();
		lexemePoses.stream().map(LexemePosRecord::copy).forEach(clonedLexemePos -> {
			clonedLexemePos.setLexemeId(clonedLexemeId);
			clonedLexemePos.changed(LEXEME_POS.ORDER_BY, false);
			clonedLexemePos.store();
		});
	}

	public void cloneLexemeRegisters(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeRegisterRecord> lexemeRegisters = create.selectFrom(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_REGISTER.ORDER_BY)
				.fetch();
		lexemeRegisters.stream().map(LexemeRegisterRecord::copy).forEach(clonedLexemeRegister -> {
			clonedLexemeRegister.setLexemeId(clonedLexemeId);
			clonedLexemeRegister.changed(LEXEME_REGISTER.ORDER_BY, false);
			clonedLexemeRegister.store();
		});
	}

	public void cloneLexemeSoureLinks(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeSourceLinkRecord> lexemeSourceLinks = create.selectFrom(LEXEME_SOURCE_LINK)
						.where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(lexemeId))
						.orderBy(LEXEME_SOURCE_LINK.ORDER_BY)
						.fetch();
		lexemeSourceLinks.stream().map(LexemeSourceLinkRecord::copy).forEach(clonedLexemeSourceLink -> {
			clonedLexemeSourceLink.setLexemeId(clonedLexemeId);
			clonedLexemeSourceLink.changed(LEXEME_SOURCE_LINK.ORDER_BY, false);
			clonedLexemeSourceLink.store();
		});
	}

	public void cloneLexemeRelations(Long lexemeId, Long clonedLexemeId) {

		Result<LexRelationRecord> lexemeRelations = create.selectFrom(LEX_RELATION)
				.where(LEX_RELATION.LEXEME1_ID.eq(lexemeId).or(LEX_RELATION.LEXEME2_ID.eq(lexemeId)))
				.orderBy(LEX_RELATION.ORDER_BY)
				.fetch();
		lexemeRelations.stream().map(LexRelationRecord::copy).forEach(clonedRelation -> {
			if (clonedRelation.getLexeme1Id().equals(lexemeId)) {
				clonedRelation.setLexeme1Id(clonedLexemeId);
			} else {
				clonedRelation.setLexeme2Id(clonedLexemeId);
			}
			clonedRelation.changed(LEX_RELATION.ORDER_BY, false);
			clonedRelation.store();
		});
	}

	public Long cloneMeaning(Long meaningId) {

		MeaningRecord meaning = create.selectFrom(MEANING).where(MEANING.ID.eq(meaningId)).fetchOne();
		MeaningRecord clonedMeaning;
		if (meaning.fields().length == 1) {
			clonedMeaning = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne();
		} else {
			clonedMeaning = meaning.copy();
			clonedMeaning.store();
		}
		Long clonedMeaningId = clonedMeaning.getId();
		return clonedMeaningId;
	}

	public void cloneMeaningDomains(Long meaningId, Long clonedMeaningId) {

		Result<MeaningDomainRecord> meaningDomains = create.selectFrom(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId))
				.orderBy(MEANING_DOMAIN.ORDER_BY)
				.fetch();
		meaningDomains.stream().map(MeaningDomainRecord::copy).forEach(clonedDomain -> {
			clonedDomain.setMeaningId(clonedMeaningId);
			clonedDomain.changed(MEANING_DOMAIN.ORDER_BY, false);
			clonedDomain.store();
		});
	}

	public void cloneMeaningRelations(Long meaningId, Long clonedMeaningId) {

		Result<MeaningRelationRecord> meaningRelations = create.selectFrom(MEANING_RELATION)
				.where(MEANING_RELATION.MEANING1_ID.eq(meaningId).or(MEANING_RELATION.MEANING2_ID.eq(meaningId)))
				.orderBy(MEANING_RELATION.ORDER_BY)
				.fetch();
		meaningRelations.stream().map(MeaningRelationRecord::copy).forEach(clonedRelation -> {
			if (clonedRelation.getMeaning1Id().equals(meaningId)) {
				clonedRelation.setMeaning1Id(clonedMeaningId);
			} else {
				clonedRelation.setMeaning2Id(clonedMeaningId);
			}
			clonedRelation.changed(MEANING_RELATION.ORDER_BY, false);
			clonedRelation.store();
		});
	}

	public void cloneMeaningFreeforms(Long meaningId, Long clonedMeaningId) {

		Result<MeaningFreeformRecord> meaningFreeforms = create.selectFrom(MEANING_FREEFORM)
				.where(MEANING_FREEFORM.MEANING_ID.eq(meaningId))
				.orderBy(MEANING_FREEFORM.ID)
				.fetch();
		for (MeaningFreeformRecord meaningFreeform : meaningFreeforms) {
			Long clonedFreeformId = cloneFreeform(meaningFreeform.getFreeformId(), null);
			MeaningFreeformRecord clonedMeaningFreeform = create.newRecord(MEANING_FREEFORM);
			clonedMeaningFreeform.setMeaningId(clonedMeaningId);
			clonedMeaningFreeform.setFreeformId(clonedFreeformId);
			clonedMeaningFreeform.store();
		}
	}

	public Long cloneMeaningDefinition(Long definitionId, Long meaningId) {

		DefinitionRecord definition = create.selectFrom(DEFINITION).where(DEFINITION.ID.eq(definitionId)).fetchOne();
		DefinitionRecord clonedDefinition = definition.copy();
		clonedDefinition.setMeaningId(meaningId);
		clonedDefinition.changed(DEFINITION.ORDER_BY, false);
		clonedDefinition.store();
		return clonedDefinition.getId();
	}

	public void cloneDefinitionFreeforms(Long definitionId, Long clonedDefinitionId) {

		Result<DefinitionFreeformRecord> definitionFreeforms = create.selectFrom(DEFINITION_FREEFORM)
				.where(DEFINITION_FREEFORM.DEFINITION_ID.eq(definitionId))
				.orderBy(DEFINITION_FREEFORM.ID)
				.fetch();
		for (DefinitionFreeformRecord definitionFreeform : definitionFreeforms) {
			Long clonedFreeformId = cloneFreeform(definitionFreeform.getFreeformId(), null);
			DefinitionFreeformRecord clonedDefinitionFreeform = create.newRecord(DEFINITION_FREEFORM);
			clonedDefinitionFreeform.setDefinitionId(clonedDefinitionId);
			clonedDefinitionFreeform.setFreeformId(clonedFreeformId);
			clonedDefinitionFreeform.store();
		}
	}

	public void cloneDefinitionDatasets(Long definitionId, Long clonedDefinintionId) {

		Result<DefinitionDatasetRecord> definitionDatasets = create
				.selectFrom(DEFINITION_DATASET)
				.where(DEFINITION_DATASET.DEFINITION_ID.eq(definitionId)).fetch();
		for (DefinitionDatasetRecord definitionDataset : definitionDatasets) {
			DefinitionDatasetRecord clonedDefinitionDataset = definitionDataset.copy();
			clonedDefinitionDataset.setDefinitionId(clonedDefinintionId);
			clonedDefinitionDataset.setDatasetCode(definitionDataset.getDatasetCode());
			clonedDefinitionDataset.store();
		}
	}

	public void cloneDefinitionSourceLinks(Long definitionId, Long clonedDefinintionId) {

		Result<DefinitionSourceLinkRecord> definitionSourceLinks = create.selectFrom(DEFINITION_SOURCE_LINK)
				.where(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(definitionId))
				.orderBy(DEFINITION_SOURCE_LINK.ORDER_BY)
				.fetch();
		definitionSourceLinks.stream().map(DefinitionSourceLinkRecord::copy).forEach(clonedDefinitionSourceLink -> {
			clonedDefinitionSourceLink.setDefinitionId(clonedDefinintionId);
			clonedDefinitionSourceLink.changed(DEFINITION_SOURCE_LINK.ORDER_BY, false);
			clonedDefinitionSourceLink.store();
		});
	}

	public Long cloneFreeform(Long freeformId, Long parentFreeformId) {
		FreeformRecord freeform = create.selectFrom(FREEFORM).where(FREEFORM.ID.eq(freeformId)).fetchOne();
		FreeformRecord clonedFreeform = freeform.copy();
		clonedFreeform.setParentId(parentFreeformId);
		clonedFreeform.changed(FREEFORM.ORDER_BY, false);
		clonedFreeform.store();
		Result<FreeformSourceLinkRecord> freeformSourceLinks = create.selectFrom(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(freeformId))
				.orderBy(FREEFORM_SOURCE_LINK.ORDER_BY)
				.fetch();
		freeformSourceLinks.forEach(sl -> {
			FreeformSourceLinkRecord clonedSourceLink = sl.copy();
			clonedSourceLink.setFreeformId(clonedFreeform.getId());
			clonedSourceLink.changed(FREEFORM_SOURCE_LINK.ORDER_BY, false);
			clonedSourceLink.store();
		});
		List<FreeformRecord> childFreeforms = create.selectFrom(FREEFORM)
				.where(FREEFORM.PARENT_ID.eq(freeformId))
				.orderBy(FREEFORM.ORDER_BY)
				.fetch();
		childFreeforms.forEach(f -> {
			cloneFreeform(f.getId(), clonedFreeform.getId());
		});
		return clonedFreeform.getId();
	}

	public void joinWordData(Long wordId, Long sourceWordId) {

		joinWordRelations(wordId, sourceWordId);

		create.update(WORD_GROUP_MEMBER)
				.set(WORD_GROUP_MEMBER.WORD_ID, wordId)
				.where(WORD_GROUP_MEMBER.WORD_ID.eq(sourceWordId))
				.execute();

		create.update(WORD_ETYMOLOGY)
				.set(WORD_ETYMOLOGY.WORD_ID, wordId)
				.where(WORD_ETYMOLOGY.WORD_ID.eq(sourceWordId))
				.execute();

		create.update(WORD_ETYMOLOGY_RELATION)
				.set(WORD_ETYMOLOGY_RELATION.RELATED_WORD_ID, wordId)
				.where(WORD_ETYMOLOGY_RELATION.RELATED_WORD_ID.eq(sourceWordId))
				.execute();

		joinWordTypeCodes(wordId, sourceWordId);

		create.update(WORD_PROCESS_LOG)
				.set(WORD_PROCESS_LOG.WORD_ID, wordId)
				.where(WORD_PROCESS_LOG.WORD_ID.eq(sourceWordId))
				.execute();

		create.update(WORD_LIFECYCLE_LOG)
				.set(WORD_LIFECYCLE_LOG.WORD_ID, wordId)
				.where(WORD_LIFECYCLE_LOG.WORD_ID.eq(sourceWordId))
				.execute();
	}

	private void joinWordRelations(Long wordId, Long sourceWordId) {

		WordRelation wr1 = WORD_RELATION.as("wr1");
		WordRelation wr2 = WORD_RELATION.as("wr2");

		create.update(wr1)
				.set(wr1.WORD1_ID, wordId)
				.where(
						wr1.WORD1_ID.eq(sourceWordId))
				.andNotExists(DSL
						.select(wr2.ID)
						.from(wr2)
						.where(
								wr2.WORD1_ID.eq(wordId)
								.and(wr2.WORD2_ID.eq(wr1.WORD2_ID))
								.and(wr2.WORD_REL_TYPE_CODE.eq(wr1.WORD_REL_TYPE_CODE))))
				.execute();

		create.update(wr1)
				.set(wr1.WORD2_ID, wordId)
				.where(
						wr1.WORD2_ID.eq(sourceWordId))
				.andNotExists(DSL
						.select(wr2.ID)
						.from(wr2)
						.where(
								wr2.WORD2_ID.eq(wordId)
										.and(wr2.WORD1_ID.eq(wr1.WORD1_ID))
										.and(wr2.WORD_REL_TYPE_CODE.eq(wr1.WORD_REL_TYPE_CODE))))
				.execute();
	}

	private void joinWordTypeCodes(Long wordId, Long sourceWordId) {
		WordWordType wwt1 = WORD_WORD_TYPE.as("wwt1");
		WordWordType wwt2 = WORD_WORD_TYPE.as("wwt2");

		create.update(wwt1)
				.set(wwt1.WORD_ID, wordId)
				.where(wwt1.WORD_ID.eq(sourceWordId))
				.andNotExists(DSL
						.select(wwt2.ID)
						.from(wwt2)
						.where(wwt2.WORD_ID.eq(wordId)
								.and(wwt2.WORD_TYPE_CODE.eq(wwt1.WORD_TYPE_CODE))))
				.execute();
	}

	public Integer getWordHomonymNum(Long wordId) {

		return create.
				select(WORD.HOMONYM_NR)
				.from(WORD)
				.where(WORD.ID.eq(wordId))
				.fetchOneInto(Integer.class);
	}

	public Integer getWordLexemesMaxFirstLevel(Long wordId) {

		return create
				.select(DSL.max(LEXEME.LEVEL1))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetchOneInto(Integer.class);
	}

	public void updateLexemeWordIdAndLevels(Long lexemeId, Long wordId, int level1, int level2, int level3) {

		create.update(LEXEME)
				.set(LEXEME.WORD_ID, wordId)
				.set(LEXEME.LEVEL1, level1)
				.set(LEXEME.LEVEL2, level2)
				.set(LEXEME.LEVEL3, level3)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public boolean wordHasForms(Long wordId) {

		return create
				.select(field(DSL.count(FORM.ID).gt(0)).as("has_forms"))
				.from(PARADIGM, FORM)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.FORM.name())))
				.fetchSingleInto(Boolean.class);
	}

	public void joinParadigms(Long wordId, Long sourceWordId) {

		create.delete(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId))
				.execute();

		create.update(PARADIGM)
				.set(PARADIGM.WORD_ID, wordId)
				.where(PARADIGM.WORD_ID.eq(sourceWordId))
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

	public void updateLexemeLevel3(Long lexemeId, Integer level3) {

		create.update(LEXEME)
				.set(LEXEME.LEVEL3, level3)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}
}
