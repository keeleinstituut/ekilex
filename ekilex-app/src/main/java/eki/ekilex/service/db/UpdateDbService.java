package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_RELATION;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.DbConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.FreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningRelationRecord;

@Component
public class UpdateDbService implements DbConstant {

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

	public Result<Record4<Long,Integer,Integer,Integer>> findWordLexemes(Long lexemeId) {
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		return create
				.select(
						l2.ID.as("lexeme_id"),
						l2.LEVEL1,
						l2.LEVEL2,
						l2.LEVEL3)
				.from(l2, l1)
				.where(
						l1.ID.eq(lexemeId)
						.and(l1.WORD_ID.eq(l2.WORD_ID))
						.and(l1.DATASET_CODE.eq(l2.DATASET_CODE)))
				.orderBy(l2.LEVEL1, l2.LEVEL2, l2.LEVEL3)
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

	public void updateWordGender(Long wordId, String genderCode) {
		create.update(WORD)
				.set(WORD.GENDER_CODE, genderCode)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateWordType(Long wordId, String typeCode) {
		create.update(WORD)
				.set(WORD.TYPE_CODE, typeCode)
				.where(WORD.ID.eq(wordId))
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

	public Long addLexemePos(Long lexemeId, String posCode) {
		Record1<Long> lexemePos = create
				.select(LEXEME_POS.ID).from(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_POS.POS_CODE.eq(posCode))
						.and(LEXEME_POS.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
		Long lexemePosId;
		if (lexemePos == null) {
			lexemePosId = create
				.insertInto(LEXEME_POS, LEXEME_POS.LEXEME_ID, LEXEME_POS.POS_CODE)
				.values(lexemeId, posCode)
				.returning(LEXEME_POS.ID)
				.fetchOne()
				.getId();
		} else {
			lexemePosId = lexemePos.into(Long.class);
		}
		return lexemePosId;
	}

	public Long addLexemeDeriv(Long lexemeId, String derivCode) {
		Record1<Long> lexemeDeriv = create
				.select(LEXEME_DERIV.ID).from(LEXEME_DERIV)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.eq(derivCode))
						.and(LEXEME_DERIV.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
		Long lexemeDerivId;
		if (lexemeDeriv == null) {
			lexemeDerivId = create
				.insertInto(LEXEME_DERIV, LEXEME_DERIV.LEXEME_ID, LEXEME_DERIV.DERIV_CODE)
				.values(lexemeId, derivCode)
				.returning(LEXEME_DERIV.ID)
				.fetchOne()
				.getId();
		} else {
			lexemeDerivId = lexemeDeriv.into(Long.class);
		}
		return lexemeDerivId;
	}

	public Long addLexemeRegister(Long lexemeId, String registerCode) {
		Record1<Long> lexemeRegister = create
				.select(LEXEME_REGISTER.ID).from(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode))
						.and(LEXEME_REGISTER.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
		Long lexemeRegisterId;
		if (lexemeRegister == null) {
			lexemeRegisterId = create
				.insertInto(LEXEME_REGISTER, LEXEME_REGISTER.LEXEME_ID, LEXEME_REGISTER.REGISTER_CODE)
				.values(lexemeId, registerCode)
				.returning(LEXEME_REGISTER.ID)
				.fetchOne()
				.getId();
		} else {
			lexemeRegisterId = lexemeRegister.into(Long.class);
		}
		return lexemeRegisterId;
	}

	public Long addMeaningDomain(Long meaningId, Classifier domain) {
		Record1<Long> meaningDomain = create
				.select(MEANING_DOMAIN.ID).from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode()))
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin()))
						.and(MEANING_DOMAIN.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetchOne();
		Long meaningDomainId;
		if (meaningDomain == null) {
			meaningDomainId = create
					.insertInto(MEANING_DOMAIN, MEANING_DOMAIN.MEANING_ID, MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
					.values(meaningId, domain.getOrigin(), domain.getCode())
					.returning(MEANING_DOMAIN.ID)
					.fetchOne()
					.getId();
		} else {
			meaningDomainId = meaningDomain.into(Long.class);
		}
		return meaningDomainId;
	}

	public Long addWord(String word, String datasetCode, String language, String morphCode) {
		Record1<Integer> currentHomonymNumber = create.select(DSL.max(WORD.HOMONYM_NR)).from(WORD, PARADIGM, FORM)
				.where(WORD.LANG.eq(language).and(FORM.MODE.eq(FormMode.WORD.name())).and(FORM.VALUE.eq(word)).and(PARADIGM.ID.eq(FORM.PARADIGM_ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))).fetchOne();
		int homonymNumber = 1;
		if (currentHomonymNumber.value1() != null) {
			homonymNumber = currentHomonymNumber.value1() + 1;
		}
		Long wordId = create.insertInto(WORD, WORD.HOMONYM_NR, WORD.LANG).values(homonymNumber, language).returning(WORD.ID).fetchOne().getId();
		Long paradigmId = create.insertInto(PARADIGM, PARADIGM.WORD_ID).values(wordId).returning(PARADIGM.ID).fetchOne().getId();
		create
				.insertInto(FORM, FORM.PARADIGM_ID, FORM.VALUE, FORM.DISPLAY_FORM, FORM.MODE, FORM.MORPH_CODE)
				.values(paradigmId, word, word, FormMode.WORD.name(), morphCode)
				.execute();
		Long meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		create
				.insertInto(LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.values(meaningId, wordId, datasetCode, 1, 1, 1)
				.execute();
		return wordId;
	}

	public Long addWordToDataset(Long wordId, String datasetCode) {

		Long meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		Long lexemeId = create
				.insertInto(LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.values(meaningId, wordId, datasetCode, 1, 1, 1)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
		return lexemeId;
	}

	public Long addLexemeGrammar(Long lexemeId, String value) {

		Long grammarFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT)
				.values(FreeformType.GRAMMAR.name(), value)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(LEXEME_FREEFORM, LEXEME_FREEFORM.LEXEME_ID, LEXEME_FREEFORM.FREEFORM_ID).values(lexemeId, grammarFreeformId).execute();
		return grammarFreeformId;
	}

	public void joinLexemeMeanings(Long lexemeId, Long sourceLexemeId) {

		Long meaningId = create.select(LEXEME.MEANING_ID).from(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne().value1();
		Long sourceMeaningId = create.select(LEXEME.MEANING_ID).from(LEXEME).where(LEXEME.ID.eq(sourceLexemeId)).fetchOne().value1();

		// delete source meaning lexemes with words, that are already connected to target meaning
		create.delete(LEXEME)
				.where(LEXEME.MEANING_ID.eq(sourceMeaningId)
						.and(LEXEME.WORD_ID.in(create.select(LEXEME.WORD_ID).from(LEXEME).where(LEXEME.MEANING_ID.eq(meaningId))))).execute();

		create.update(LEXEME).set(LEXEME.MEANING_ID, meaningId).where(LEXEME.MEANING_ID.eq(sourceMeaningId)).execute();
		joinMeaningDefinitions(meaningId, sourceMeaningId);
		joinMeaningDomains(meaningId, sourceMeaningId);
		joinMeaningFreeforms(meaningId, sourceMeaningId);
		joinMeaningRelations(meaningId, sourceMeaningId);
		create.delete(MEANING).where(MEANING.ID.eq(sourceMeaningId)).execute();
	}

	public void deleteFreeform(Long id) {
		List<FreeformRecord> childFreeforms = create.selectFrom(FREEFORM).where(FREEFORM.PARENT_ID.eq(id)).fetch();
		childFreeforms.forEach(f -> {
			deleteFreeform(f.getId());
		});
		create.update(FREEFORM).set(FREEFORM.PROCESS_STATE_CODE, PROCESS_STATE_DELETED).where(FREEFORM.ID.eq(id)).execute();
	}

	public void deleteDefinition(Long id) {
		create.update(DEFINITION).set(DEFINITION.PROCESS_STATE_CODE, PROCESS_STATE_DELETED).where(DEFINITION.ID.eq(id)).execute();
	}

	public Long deleteLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = create.update(LEXEME_POS)
				.set(LEXEME_POS.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_POS.POS_CODE.eq(posCode)))
				.returning(LEXEME_POS.ID)
				.fetchOne()
				.getId();
		return lexemePosId;
	}

	public Long deleteLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = create.update(LEXEME_DERIV)
				.set(LEXEME_DERIV.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.eq(derivCode)))
				.returning(LEXEME_DERIV.ID)
				.fetchOne()
				.getId();
		return lexemeDerivId;
	}

	public Long deleteLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = create.update(LEXEME_REGISTER)
				.set(LEXEME_REGISTER.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode)))
				.returning(LEXEME_REGISTER.ID)
				.fetchOne()
				.getId();
		return lexemeRegisterId;
	}

	public Long deleteMeaningDomain(Long meaningId,  Classifier domain) {
		Long meaningDomainId = create.update(MEANING_DOMAIN)
				.set(MEANING_DOMAIN.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin()))
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode())))
				.returning(MEANING_DOMAIN.ID)
				.fetchOne()
				.getId();
		return meaningDomainId;
	}

	public void deleteDefinitionRefLink(Long refLinkId) {
		create.update(DEFINITION_SOURCE_LINK)
				.set(DEFINITION_SOURCE_LINK.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(DEFINITION_SOURCE_LINK.ID.eq(refLinkId))
				.execute();
	}

	public void deleteFreeformRefLink(Long refLinkId) {
		create.update(FREEFORM_SOURCE_LINK)
				.set(FREEFORM_SOURCE_LINK.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(FREEFORM_SOURCE_LINK.ID.eq(refLinkId))
				.execute();
	}

	public void deleteLexemeRefLink(Long refLinkId) {
		create.update(LEXEME_SOURCE_LINK)
				.set(LEXEME_SOURCE_LINK.PROCESS_STATE_CODE, PROCESS_STATE_DELETED)
				.where(LEXEME_SOURCE_LINK.ID.eq(refLinkId))
				.execute();
	}

	public Long addDefinition(Long meaningId, String value, String languageCode) {
		return create
				.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.LANG, DEFINITION.VALUE)
				.values(meaningId, languageCode, value)
				.returning(DEFINITION.ID)
				.fetchOne()
				.getId();
	}

	public Long addUsage(Long lexemeId, String value, String languageCode) {
		Long usageFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.LANG)
				.values(FreeformType.USAGE.name(), value, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(LEXEME_FREEFORM, LEXEME_FREEFORM.LEXEME_ID, LEXEME_FREEFORM.FREEFORM_ID).values(lexemeId, usageFreeformId).execute();
		return usageFreeformId;
	}

	public Long addUsageTranslation(Long usageId, String value, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.LANG)
				.values(FreeformType.USAGE_TRANSLATION.name(), usageId, value, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long addUsageDefinition(Long usageId, String value, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.LANG)
				.values(FreeformType.USAGE_DEFINITION.name(), usageId, value, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long addGovernment(Long lexemeId, String government) {
		Long governmentFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT)
				.values(FreeformType.GOVERNMENT.name(), government)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(LEXEME_FREEFORM, LEXEME_FREEFORM.LEXEME_ID, LEXEME_FREEFORM.FREEFORM_ID).values(lexemeId, governmentFreeformId).execute();
		return governmentFreeformId;
	}

	public Long addDefinitionSourceLink(Long definitionId, Long sourceId, ReferenceType refType, String sourceValue, String sourceName) {
		return create
				.insertInto(
						DEFINITION_SOURCE_LINK,
						DEFINITION_SOURCE_LINK.DEFINITION_ID,
						DEFINITION_SOURCE_LINK.SOURCE_ID,
						DEFINITION_SOURCE_LINK.TYPE,
						DEFINITION_SOURCE_LINK.VALUE,
						DEFINITION_SOURCE_LINK.NAME)
				.values(definitionId, sourceId, refType.name(), sourceValue, sourceName).returning(DEFINITION_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public Long addFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType refType, String sourceValue, String sourceName) {
		return create
				.insertInto(
						FREEFORM_SOURCE_LINK,
						FREEFORM_SOURCE_LINK.FREEFORM_ID,
						FREEFORM_SOURCE_LINK.SOURCE_ID,
						FREEFORM_SOURCE_LINK.TYPE,
						FREEFORM_SOURCE_LINK.VALUE,
						FREEFORM_SOURCE_LINK.NAME)
				.values(freeformId, sourceId, refType.name(), sourceValue, sourceName).returning(FREEFORM_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public Long addLexemeSourceLink(Long lexemeId, Long sourceId, ReferenceType refType, String sourceValue, String sourceName) {
		return create
				.insertInto(
						LEXEME_SOURCE_LINK,
						LEXEME_SOURCE_LINK.LEXEME_ID,
						LEXEME_SOURCE_LINK.SOURCE_ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.VALUE,
						LEXEME_SOURCE_LINK.NAME)
				.values(lexemeId, sourceId, refType.name(), sourceValue, sourceName).returning(LEXEME_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
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
