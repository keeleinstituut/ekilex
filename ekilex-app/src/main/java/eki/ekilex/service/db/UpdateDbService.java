package eki.ekilex.service.db;

import eki.common.constant.FreeformType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.FreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningRelationRecord;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.tables.Freeform.FREEFORM;

@Component
public class UpdateDbService {

	private DSLContext create;

	public UpdateDbService(DSLContext context) {
		create = context;
	}

	public void updateFreeformTextValue(Long id, String value) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateDefinitionValue(Long id, String value) {
		create.update(DEFINITION)
				.set(DEFINITION.VALUE, value)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void updateDefinitionOrderby(List<ListData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (ListData item : items) {
			updateQueries.add(create.update(DEFINITION).set(DEFINITION.ORDER_BY, item.getOrderby()).where(DEFINITION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public void updateLexemeRelationOrderby(List<ListData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (ListData item : items) {
			updateQueries.add(create.update(LEX_RELATION).set(LEX_RELATION.ORDER_BY, item.getOrderby()).where(LEX_RELATION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public void updateMeaningRelationOrderby(List<ListData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (ListData item : items) {
			updateQueries.add(create.update(MEANING_RELATION).set(MEANING_RELATION.ORDER_BY, item.getOrderby()).where(MEANING_RELATION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public void updateWordRelationOrderby(List<ListData> items) {
		List<Query> updateQueries = new ArrayList<>();
		for (ListData item : items) {
			updateQueries.add(create.update(WORD_RELATION).set(WORD_RELATION.ORDER_BY, item.getOrderby()).where(WORD_RELATION.ID.eq(item.getId())));
		}
		create.batch(updateQueries).execute();
	}

	public Result<Record4<Long,Integer,Integer,Integer>> findConnectedLexemes(Long lexemeId) {
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		return create
				.select(
						l1.ID.as("lexeme_id"),
						l1.LEVEL1,
						l1.LEVEL2,
						l1.LEVEL3)
				.from(l1, l2)
				.where(l2.ID.eq(lexemeId).and(l1.WORD_ID.eq(l2.WORD_ID)).and(l1.DATASET_CODE.eq(l2.DATASET_CODE)))
				.orderBy(l1.LEVEL1, l1.LEVEL2, l1.LEVEL3)
				.fetch();
	}

	public void updateLexemeLevels(Long id, Integer level1, Integer level2, Integer level3) {
		create.update(LEXEME)
				.set(LEXEME.LEVEL1, level1)
				.set(LEXEME.LEVEL2, level2)
				.set(LEXEME.LEVEL3, level3)
				.where(LEXEME.ID.eq(id))
				.execute();
	}

	public void updateLexemeFrequencyGroup(Long lexemeId, String groupCode) {
		create.update(LEXEME)
				.set(LEXEME.FREQUENCY_GROUP, groupCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		create.update(LEXEME_POS)
				.set(LEXEME_POS.POS_CODE, newPos)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(currentPos)))
				.execute();
	}

	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		create.update(MEANING_DOMAIN)
				.set(MEANING_DOMAIN.DOMAIN_CODE, newDomain.getCode())
				.set(MEANING_DOMAIN.DOMAIN_ORIGIN, newDomain.getOrigin())
				.where(
						MEANING_DOMAIN.MEANING_ID.eq(meaningId).and(
						MEANING_DOMAIN.DOMAIN_CODE.eq(currentDomain.getCode())).and(
						MEANING_DOMAIN.DOMAIN_ORIGIN.eq(currentDomain.getOrigin())))
				.execute();
	}

	public void updateGovernment(Long governmentId, String government) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, government)
				.where(FREEFORM.ID.eq(governmentId))
				.execute();
	}

	public void addLexemePos(Long lexemeId, String posCode) {
		Record1<Long> lexemePos = create
				.select(LEXEME_POS.ID).from(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(posCode)))
				.fetchOne();
		if (lexemePos == null) {
			create
				.insertInto(LEXEME_POS, LEXEME_POS.LEXEME_ID, LEXEME_POS.POS_CODE)
				.values(lexemeId, posCode)
				.execute();
		}
	}

	public void addMeaningDomain(Long meaningId, Classifier domain) {
		Record1<Long> meaningDomain = create
				.select(MEANING_DOMAIN.ID).from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId).and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode())).and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin())))
				.fetchOne();
		if (meaningDomain == null) {
			create
					.insertInto(MEANING_DOMAIN, MEANING_DOMAIN.MEANING_ID, MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
					.values(meaningId, domain.getOrigin(), domain.getCode())
					.execute();
		}
	}

	public void addWord(String word, String datasetCode, String language, String morphCode) {

		Long wordId = create.insertInto(WORD, WORD.HOMONYM_NR, WORD.LANG).values(1, language).returning(WORD.ID).fetchOne().getId();
		Long paradigmId = create.insertInto(PARADIGM, PARADIGM.WORD_ID).values(wordId).returning(PARADIGM.ID).fetchOne().getId();
		create
				.insertInto(FORM, FORM.PARADIGM_ID, FORM.VALUE, FORM.DISPLAY_FORM, FORM.IS_WORD, FORM.MORPH_CODE)
				.values(paradigmId, word, word, true, morphCode)
				.execute();
		Long meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		create
				.insertInto(LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.values(meaningId, wordId, datasetCode, 1, 1, 1)
				.execute();
	}

	public void joinLexemeMeanings(Long lexemeId, Long sourceLexemeId) {

		Long meaningId = create.select(LEXEME.MEANING_ID).from(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne().value1();
		Long sourceMeaningId = create.select(LEXEME.MEANING_ID).from(LEXEME).where(LEXEME.ID.eq(sourceLexemeId)).fetchOne().value1();

		create.update(LEXEME).set(LEXEME.MEANING_ID, meaningId).where(LEXEME.MEANING_ID.eq(sourceMeaningId)).execute();
		joinMeaningDefinitions(meaningId, sourceMeaningId);
		joinMeaningDomains(meaningId, sourceMeaningId);
		joinMeaningFreeforms(meaningId, sourceMeaningId);
		joinMeaningRelations(meaningId, sourceMeaningId);
		create.delete(MEANING).where(MEANING.ID.eq(sourceMeaningId)).execute();
	}

	public void removeFreeform(Long id) {
		List<FreeformRecord> childFreeforms = create.selectFrom(FREEFORM).where(FREEFORM.PARENT_ID.eq(id)).fetch();
		childFreeforms.forEach(f -> {
			removeFreeform(f.getId());
		});
		create.delete(FREEFORM).where(FREEFORM.ID.eq(id)).execute();
	}

	public void removeDefinition(Long id) {
		create.delete(DEFINITION).where(DEFINITION.ID.eq(id)).execute();
	}

	public void removeLexemePos(Long lexemeId, String posCode) {
		create.delete(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_POS.POS_CODE.eq(posCode)))
				.execute();
	}

	public void removeMeaningDomain(Long meaningId,  Classifier domain) {
		create.delete(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin()))
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode())))
				.execute();
	}

	public void removeLexemeFreeform(Long freeformId) {
		create.delete(LEXEME_FREEFORM).where(LEXEME_FREEFORM.FREEFORM_ID.eq(freeformId)).execute();
	}

	public Long addDefinition(Long meaningId, String value, String languageCode) {
		return create
				.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.LANG, DEFINITION.VALUE)
				.values(meaningId, languageCode, value)
				.returning(DEFINITION.ID).fetchOne().getId();
	}

	public Long addUsageMeaning(Long governmentId) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID)
				.values(FreeformType.USAGE_MEANING.name(), governmentId).returning()
				.fetchOne().getId();
	}

	public Long addUsageMeaningMember(Long usageMeaningId, String usageMemberType, String value, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.LANG)
				.values(usageMemberType, usageMeaningId, value, languageCode).returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long addGovernment(Long lexemeId, String government) {
		Long governmentFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT)
				.values(FreeformType.GOVERNMENT.name(), government).returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(LEXEME_FREEFORM, LEXEME_FREEFORM.LEXEME_ID, LEXEME_FREEFORM.FREEFORM_ID).values(lexemeId, governmentFreeformId).execute();
		return governmentFreeformId;
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
						.noneMatch(mf -> mf.getType().equals(sf.getType()) && (
								(Objects.nonNull(mf.getValueText()) && mf.getValueText().equals(sf.getValueText())) ||
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
						.and(DEFINITION.VALUE.notIn(DSL.select(DEFINITION.VALUE).from(DEFINITION).where(DEFINITION.MEANING_ID.eq(meaningId))))).execute();
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
				.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.VALUE, DEFINITION.LANG)
				.values(newMeaningId, d.getValue(), d.getLang())
				.execute();
		});
	}

	private Long cloneFreeform(Long freeformId, Long parentFreeformId) {
		FreeformRecord freeform = create.selectFrom(FREEFORM).where(FREEFORM.ID.eq(freeformId)).fetchOne();
		FreeformRecord clonedFreeform = create.insertInto(FREEFORM)
				.set(FREEFORM.TYPE, freeform.getType())
				.set(FREEFORM.PARENT_ID, parentFreeformId)
				.set(FREEFORM.VALUE_TEXT, freeform.getValueText())
				.set(FREEFORM.LANG, freeform.getLang())
				.set(FREEFORM.VALUE_DATE, freeform.getValueDate())
				.set(FREEFORM.CLASSIF_CODE, freeform.getClassifCode())
				.set(FREEFORM.CLASSIF_NAME, freeform.getClassifName())
				.set(FREEFORM.VALUE_ARRAY, freeform.getValueArray())
				.set(FREEFORM.VALUE_NUMBER, freeform.getValueNumber())
				.returning(FREEFORM.ID).fetchOne();
		List<FreeformRecord> childFreeforms = create.selectFrom(FREEFORM).where(FREEFORM.PARENT_ID.eq(freeformId)).fetch();
		childFreeforms.forEach(f -> {
			cloneFreeform(f.getId(), clonedFreeform.getId());
		});
		return clonedFreeform.getId();
	}

}
