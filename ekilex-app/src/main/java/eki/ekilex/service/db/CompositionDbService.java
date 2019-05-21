package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class CompositionDbService {

	@Autowired
	private DSLContext create;

	public LexemeRecord getLexeme(Long lexemeId) {
		return create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
	}

	public List<LexemeRecord> getMeaningLexemes(Long meaningId) {
		return create.selectFrom(LEXEME).where(LEXEME.MEANING_ID.eq(meaningId)).fetch();
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

	public void joinLexemeMeanings(Long lexemeId, Long sourceLexemeId) {

		LexemeRecord lexeme = create.fetchOne(LEXEME, LEXEME.ID.eq(lexemeId));
		LexemeRecord sourceLexeme = create.fetchOne(LEXEME, LEXEME.ID.eq(sourceLexemeId));
		if (lexeme.getWordId().equals(sourceLexeme.getWordId()) && lexeme.getDatasetCode().equals(sourceLexeme.getDatasetCode())) {
			joinLexemes(lexemeId, sourceLexemeId);
		}
		joinMeanings(lexeme.getMeaningId(), sourceLexeme.getMeaningId());
	}

	public void joinMeanings(Long meaningId, Long sourceMeaningId) {
		create.update(LEXEME).set(LEXEME.MEANING_ID, meaningId).where(LEXEME.MEANING_ID.eq(sourceMeaningId)).execute();
		joinMeaningDefinitions(meaningId, sourceMeaningId);
		joinMeaningDomains(meaningId, sourceMeaningId);
		joinMeaningFreeforms(meaningId, sourceMeaningId);
		joinMeaningRelations(meaningId, sourceMeaningId);
		create.update(MEANING_LIFECYCLE_LOG).set(MEANING_LIFECYCLE_LOG.MEANING_ID, meaningId).where(MEANING_LIFECYCLE_LOG.MEANING_ID.eq(sourceMeaningId)).execute();
		create.delete(MEANING).where(MEANING.ID.eq(sourceMeaningId)).execute();
	}

	public void joinLexemes(Long lexemeId, Long sourceLexemeId) {

		create.update(LEXEME_SOURCE_LINK).set(LEXEME_SOURCE_LINK.LEXEME_ID, lexemeId).where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(sourceLexemeId)).execute();
		create.update(LEXEME_REGISTER)
				.set(LEXEME_REGISTER.LEXEME_ID, lexemeId)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.notIn(
								DSL.select(LEXEME_REGISTER.REGISTER_CODE).from(LEXEME_REGISTER).where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)))))
				.execute();
		create.update(LEXEME_POS)
				.set(LEXEME_POS.LEXEME_ID, lexemeId)
				.where(LEXEME_POS.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_POS.POS_CODE.notIn(
								DSL.select(LEXEME_POS.POS_CODE).from(LEXEME_POS).where(LEXEME_POS.LEXEME_ID.eq(lexemeId)))))
				.execute(); // <-unique
		create.update(LEXEME_FREEFORM).set(LEXEME_FREEFORM.LEXEME_ID, lexemeId).where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId)).execute(); // <- ??
		create.update(LEXEME_DERIV)
				.set(LEXEME_DERIV.LEXEME_ID, lexemeId)
				.where(LEXEME_DERIV.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.notIn(
								DSL.select(LEXEME_DERIV.DERIV_CODE).from(LEXEME_DERIV).where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)))))
				.execute();
		create.update(LEXEME_LIFECYCLE_LOG).set(LEXEME_LIFECYCLE_LOG.LEXEME_ID, lexemeId).where(LEXEME_LIFECYCLE_LOG.LEXEME_ID.eq(sourceLexemeId)).execute();
		create.update(LEX_RELATION).set(LEX_RELATION.LEXEME1_ID, lexemeId).where(LEX_RELATION.LEXEME1_ID.eq(sourceLexemeId)).execute();
		create.update(LEX_RELATION).set(LEX_RELATION.LEXEME2_ID, lexemeId).where(LEX_RELATION.LEXEME2_ID.eq(sourceLexemeId)).execute();
		create.delete(LEXEME).where(LEXEME.ID.eq(sourceLexemeId)).execute();
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
					.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.VALUE, DEFINITION.VALUE_PRESE, DEFINITION.LANG, DEFINITION.DEFINITION_TYPE_CODE)
					.values(newMeaningId, d.getValue(), d.getValuePrese(), d.getLang(), d.getDefinitionTypeCode())
					.execute();
		});
	}

	public Long cloneLexeme(Long lexemeId, Long meaningId) {

		LexemeRecord lexeme = create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
		LexemeRecord clonedLexeme = lexeme.copy();
		clonedLexeme.setMeaningId(meaningId);
		clonedLexeme.changed(LEXEME.ORDER_BY, false);
		clonedLexeme.setLevel1(lexeme.getLevel1());
		clonedLexeme.setLevel2(calculateLevel2(lexeme));
		clonedLexeme.setLevel3(1);
		clonedLexeme.store();
		return clonedLexeme.getId();
	}

	public void cloneLexemeDerivatives(Long lexemeId, Long clonedLexemeId) {

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

	private int calculateLevel2(LexemeRecord lexeme) {

		Result<LexemeRecord> lexemes = create.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(lexeme.getWordId()).and(LEXEME.LEVEL1.eq(lexeme.getLevel1())))
				.fetch();
		return lexemes.stream().mapToInt(LexemeRecord::getLevel2).max().orElse(0) + 1;
	}

	public Long cloneMeaning(Long meaningId) {

		MeaningRecord meaning = create.selectFrom(MEANING).where(MEANING.ID.eq(meaningId)).fetchOne();
		Long clonedMeaningId;
		if (meaning.fields().length == 1) {
			clonedMeaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		} else {
			MeaningRecord clonedMeaning = meaning.copy();
			clonedMeaning.store();
			clonedMeaningId = clonedMeaning.getId();
		}
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
}
