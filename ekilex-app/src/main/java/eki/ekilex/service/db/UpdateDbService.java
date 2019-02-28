package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.Tables.FEEDBACK_LOG_COMMENT;
import static eki.ekilex.data.db.Tables.FORM;
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
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import eki.ekilex.data.db.tables.records.FeedbackLogCommentRecord;
import eki.ekilex.data.db.tables.records.FreeformSourceLinkRecord;
import eki.ekilex.data.db.tables.records.LexemeFreeformRecord;
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
import eki.ekilex.data.db.tables.records.LexRelationRecord;
import eki.ekilex.data.db.tables.records.LexemeDerivRecord;
import eki.ekilex.data.db.tables.records.LexemePosRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.data.db.tables.records.LexemeRegisterRecord;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningRelationRecord;
import eki.ekilex.data.db.tables.records.WordGroupMemberRecord;
import eki.ekilex.data.db.tables.records.WordGroupRecord;
import eki.ekilex.data.db.tables.records.WordRelationRecord;
import eki.ekilex.data.db.tables.records.WordWordTypeRecord;

@Component
public class UpdateDbService implements DbConstant {

	private DSLContext create;

	public UpdateDbService(DSLContext context) {
		create = context;
	}

	public void updateFreeformTextValue(Long id, String value, String valuePrese) {
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void updateDefinitionValue(Long id, String value, String valuePrese) {
		create.update(DEFINITION)
				.set(DEFINITION.VALUE, value)
				.set(DEFINITION.VALUE_PRESE, valuePrese)
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

	public void updateLexemeFrequencyGroup(Long lexemeId, String frequencyGroupCode) {
		create.update(LEXEME)
				.set(LEXEME.FREQUENCY_GROUP_CODE, frequencyGroupCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		create.update(LEXEME)
				.set(LEXEME.VALUE_STATE_CODE, valueStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {
		create.update(LEXEME)
				.set(LEXEME.PROCESS_STATE_CODE, processStateCode)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
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

	public Long addWord(String value, String valuePrese, String datasetCode, String language, String morphCode, Long meaningId) {
		Record1<Integer> currentHomonymNumber = create.select(DSL.max(WORD.HOMONYM_NR)).from(WORD, PARADIGM, FORM)
				.where(WORD.LANG.eq(language).and(FORM.MODE.eq(FormMode.WORD.name())).and(FORM.VALUE.eq(value)).and(PARADIGM.ID.eq(FORM.PARADIGM_ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))).fetchOne();
		int homonymNumber = 1;
		if (currentHomonymNumber.value1() != null) {
			homonymNumber = currentHomonymNumber.value1() + 1;
		}
		Long wordId = create.insertInto(WORD, WORD.HOMONYM_NR, WORD.LANG).values(homonymNumber, language).returning(WORD.ID).fetchOne().getId();
		Long paradigmId = create.insertInto(PARADIGM, PARADIGM.WORD_ID).values(wordId).returning(PARADIGM.ID).fetchOne().getId();
		create
				.insertInto(FORM, FORM.PARADIGM_ID, FORM.VALUE, FORM.DISPLAY_FORM, FORM.VALUE_PRESE, FORM.MODE, FORM.MORPH_CODE, FORM.MORPH_EXISTS)
				.values(paradigmId, value, value, valuePrese, FormMode.WORD.name(), morphCode, true)
				.execute();
		if (meaningId == null) {
			meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		}
		create
				.insertInto(LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.values(meaningId, wordId, datasetCode, 1, 1, 1)
				.execute();
		return wordId;
	}

	public Long addWordToDataset(Long wordId, String datasetCode, Long meaningId) {

		if (meaningId == null) {
			meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		}
		Long lexemeId = create
				.insertInto(LEXEME, LEXEME.MEANING_ID, LEXEME.WORD_ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.values(meaningId, wordId, datasetCode, 1, 1, 1)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
		return lexemeId;
	}

	public Long addWordType(Long wordId, String typeCode) {
		Record1<Long> wordWordType = create
				.select(WORD_WORD_TYPE.ID).from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordId)
						.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(typeCode)))
				.fetchOne();
		Long wordWordTypeId;
		if (wordWordType == null) {
			wordWordTypeId = create
				.insertInto(WORD_WORD_TYPE, WORD_WORD_TYPE.WORD_ID, WORD_WORD_TYPE.WORD_TYPE_CODE)
				.values(wordId, typeCode)
				.returning(WORD_WORD_TYPE.ID)
				.fetchOne()
				.getId();
		} else {
			wordWordTypeId = wordWordType.into(Long.class);
		}
		return wordWordTypeId;
	}

	public Long addWordRelation(Long wordId, Long targetWordId, String wordRelationCode) {

		Optional<WordRelationRecord> wordRelationRecord = create.fetchOptional(WORD_RELATION,
				WORD_RELATION.WORD1_ID.eq(wordId).and(
				WORD_RELATION.WORD2_ID.eq(targetWordId)).and(
				WORD_RELATION.WORD_REL_TYPE_CODE.eq(wordRelationCode)));
		if (wordRelationRecord.isPresent()) {
			return wordRelationRecord.get().getId();
		} else {
			WordRelationRecord newRelation = create.newRecord(WORD_RELATION);
			newRelation.setWord1Id(wordId);
			newRelation.setWord2Id(targetWordId);
			newRelation.setWordRelTypeCode(wordRelationCode);
			newRelation.store();
			return newRelation.getId();
		}
	}

	public Long findWordRelationGroupId(String groupType, Long wordId) {
		Optional<Record1<Long>> result = create.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.WORD_REL_TYPE_CODE.eq(groupType).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOptional();
		return result.map(Record1::value1).orElse(null);
	}

	public Long findWordRelationGroupId(Long relationId) {
		Optional<Record1<Long>> result = create.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP_MEMBER.ID.eq(relationId))
				.fetchOptional();
		return result.map(Record1::value1).orElse(null);
	}

	public boolean isMemberOfWordRelationGroup(Long groupId, Long wordId) {
		Optional<Record1<Long>> result = create.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.ID.eq(groupId).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOptional();
		return result.isPresent();
	}

	public List<Map<String, Object>> findWordRelationGroupMembers(Long groupId) {
		return create.selectDistinct(WORD_GROUP_MEMBER.ID, WORD_GROUP_MEMBER.WORD_ID, FORM.VALUE, WORD_GROUP.WORD_REL_TYPE_CODE)
				.from(WORD_GROUP_MEMBER)
				.join(WORD_GROUP).on(WORD_GROUP.ID.eq(WORD_GROUP_MEMBER.WORD_GROUP_ID))
				.join(PARADIGM).on(PARADIGM.WORD_ID.eq(WORD_GROUP_MEMBER.WORD_ID))
				.join(FORM).on(FORM.PARADIGM_ID.eq(PARADIGM.ID))
				.where(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(groupId)
						.and(FORM.MODE.eq("WORD")))
				.fetchMaps();
	}

	public Long addWordRelationGroup(String groupType) {
		WordGroupRecord wordGroupRecord = create.newRecord(WORD_GROUP);
		wordGroupRecord.setWordRelTypeCode(groupType);
		wordGroupRecord.store();
		return wordGroupRecord.getId();
	}

	public Long addWordRelationGroupMember(Long groupId, Long wordId) {
		WordGroupMemberRecord wordGroupMember = create.newRecord(WORD_GROUP_MEMBER);
		wordGroupMember.setWordGroupId(groupId);
		wordGroupMember.setWordId(wordId);
		wordGroupMember.store();
		return wordGroupMember.getId();
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

		LexemeRecord lexeme = create.fetchOne(LEXEME,LEXEME.ID.eq(lexemeId));
		LexemeRecord sourceLexeme = create.fetchOne(LEXEME,LEXEME.ID.eq(sourceLexemeId));
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
		create.update(LEXEME_FREEFORM).set(LEXEME_FREEFORM.LEXEME_ID, lexemeId).where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId)).execute();  // <- ??
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

	public LexemeRecord getLexeme(Long lexemeId) {
		return create.fetchOne(LEXEME,LEXEME.ID.eq(lexemeId));
	}

	public void deleteFreeform(Long id) {
		create.delete(FREEFORM)
				.where(FREEFORM.ID.eq(id))
				.execute();
	}

	public void deleteDefinition(Long id) {
		create.delete(DEFINITION)
				.where(DEFINITION.ID.eq(id))
				.execute();
	}

	public void deleteWordWordType(Long wordWordTypeId) {
		create.delete(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.ID.eq(wordWordTypeId))
				.execute();
	}

	public Long findWordWordTypeId(Long wordId, String typeCode) {
		WordWordTypeRecord wordWordTypeRecord = create.fetchOne(WORD_WORD_TYPE, WORD_WORD_TYPE.WORD_ID.eq(wordId).and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(typeCode)));
		return wordWordTypeRecord.getId();
	}

	public void deleteLexemePos(Long lexemePosId) {
		create.delete(LEXEME_POS)
				.where(LEXEME_POS.ID.eq(lexemePosId))
				.execute();
	}

	public Long findLexemePosId(Long lexemeId, String posCode) {
		LexemePosRecord lexemePosRecord = create.fetchOne(LEXEME_POS, LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(posCode)));
		return lexemePosRecord.getId();
	}

	public void deleteLexemeDeriv(Long lexemeDerivId) {
		create.delete(LEXEME_DERIV)
				.where(LEXEME_DERIV.ID.eq(lexemeDerivId))
				.execute();
	}

	public Long findLexemeDerivId(Long lexemeId, String derivCode) {
		LexemeDerivRecord lexemeDerivRecord = create.fetchOne(LEXEME_DERIV, LEXEME_DERIV.LEXEME_ID.eq(lexemeId).and(LEXEME_DERIV.DERIV_CODE.eq(derivCode)));
		return lexemeDerivRecord.getId();
	}

	public void deleteLexemeRegister(Long lexemeRegisterId) {
		create.delete(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.ID.eq(lexemeRegisterId))
				.execute();
	}

	public Long findLexemeRegisterId(Long lexemeId, String registerCode) {
		LexemeRegisterRecord lexemeRegisterRecord = create.fetchOne(LEXEME_REGISTER,
				LEXEME_REGISTER.LEXEME_ID.eq(lexemeId).and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode)));
		return lexemeRegisterRecord.getId();
	}

	public void deleteMeaningDomain(Long meaningDomainId) {
		create.delete(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.ID.eq(meaningDomainId))
				.execute();
	}

	public Long findMeaningDomainId(Long meaningId,  Classifier domain) {
		MeaningDomainRecord meaningDomainRecord = create.fetchOne(MEANING_DOMAIN,
				MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin()))
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode())));
		return meaningDomainRecord.getId();
	}

	public void deleteDefinitionRefLink(Long refLinkId) {
		create.delete(DEFINITION_SOURCE_LINK)
				.where(DEFINITION_SOURCE_LINK.ID.eq(refLinkId))
				.execute();
	}

	public void deleteFreeformRefLink(Long refLinkId) {
		create.delete(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.ID.eq(refLinkId))
				.execute();
	}

	public void deleteLexemeRefLink(Long refLinkId) {
		create.delete(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(refLinkId))
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

	public void deleteLexemeRelation(Long relationId) {
		create.delete(LEX_RELATION)
				.where(LEX_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteMeaningRelation(Long relationId) {
		create.delete(MEANING_RELATION)
				.where(MEANING_RELATION.ID.eq(relationId))
				.execute();
	}

	public void deleteLexeme(Long lexemeId) {
		create.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(LEXEME_FREEFORM.FREEFORM_ID).from(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId))))
				.execute();
		create.delete(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void deleteWord(Long wordId) {
		create.delete(WORD)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void deleteFeedback(Long id) {
		create.delete(FEEDBACK_LOG)
				.where(FEEDBACK_LOG.ID.eq(id))
				.execute();
	}

	public Long addDefinition(Long meaningId, String value, String valuePrese, String languageCode) {
		return create
				.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.LANG, DEFINITION.VALUE, DEFINITION.VALUE_PRESE)
				.values(meaningId, languageCode, value, valuePrese)
				.returning(DEFINITION.ID)
				.fetchOne()
				.getId();
	}

	public Long addUsage(Long lexemeId, String value, String valuePrese, String languageCode) {
		Long usageFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.LANG)
				.values(FreeformType.USAGE.name(), value, valuePrese, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(LEXEME_FREEFORM, LEXEME_FREEFORM.LEXEME_ID, LEXEME_FREEFORM.FREEFORM_ID).values(lexemeId, usageFreeformId).execute();
		return usageFreeformId;
	}

	public Long addUsageTranslation(Long usageId, String value, String valuePrese, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.LANG)
				.values(FreeformType.USAGE_TRANSLATION.name(), usageId, value, valuePrese, languageCode)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
	}

	public Long addUsageDefinition(Long usageId, String value, String valuePrese, String languageCode) {
		return create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.PARENT_ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.LANG)
				.values(FreeformType.USAGE_DEFINITION.name(), usageId, value, valuePrese, languageCode)
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

	public Long addLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) {
		LexRelationRecord lexemeRelation = create.newRecord(LEX_RELATION);
		lexemeRelation.setLexeme1Id(lexemeId1);
		lexemeRelation.setLexeme2Id(lexemeId2);
		lexemeRelation.setLexRelTypeCode(relationType);
		lexemeRelation.store();
		return lexemeRelation.getId();
	}

	public Long addMeaningRelation(Long meaningId1, Long meaningId2, String relationType) {
		MeaningRelationRecord meaningRelation = create.newRecord(MEANING_RELATION);
		meaningRelation.setMeaning1Id(meaningId1);
		meaningRelation.setMeaning2Id(meaningId2);
		meaningRelation.setMeaningRelTypeCode(relationType);
		meaningRelation.store();
		return meaningRelation.getId();
	}

	public Long addLearnerComment(Long meaningId, String value, String valuePrese, String languageCode) {
		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.LEARNER_COMMENT.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(languageCode);
		freeform.store();

		MeaningFreeformRecord meaningFreeform = create.newRecord(MEANING_FREEFORM);
		meaningFreeform.setMeaningId(meaningId);
		meaningFreeform.setFreeformId(freeform.getId());
		meaningFreeform.store();

		return freeform.getId();
	}

	public Long addPublicNote(Long lexemeId, String value, String valuePrese, String languageCode) {
		FreeformRecord freeform = create.newRecord(FREEFORM);
		freeform.setType(FreeformType.PUBLIC_NOTE.name());
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
		freeform.setLang(languageCode);
		freeform.store();

		LexemeFreeformRecord lexemeFreeform = create.newRecord(LEXEME_FREEFORM);
		lexemeFreeform.setLexemeId(lexemeId);
		lexemeFreeform.setFreeformId(freeform.getId());
		lexemeFreeform.store();

		return freeform.getId();
	}

	public Long addFeedbackComment(Long feedbackId, String comment, String userName) {
		FeedbackLogCommentRecord feedbackComment = create.newRecord(FEEDBACK_LOG_COMMENT);
		feedbackComment.setFeedbackLogId(feedbackId);
		feedbackComment.setComment(comment);
		feedbackComment.setUserName(userName);
		feedbackComment.store();
		return feedbackComment.getId();
	}

	public String getFirstDefinitionOfMeaning(Long meaningId) {
		Optional<Record1<String>> definition = create.select(DEFINITION.VALUE_PRESE).from(DEFINITION)
				.where(DEFINITION.MEANING_ID.eq(meaningId)).orderBy(DEFINITION.ORDER_BY).limit(1)
				.fetchOptional();
		return definition.map(Record1::value1).orElse(null);
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
				.insertInto(DEFINITION, DEFINITION.MEANING_ID, DEFINITION.VALUE, DEFINITION.VALUE_PRESE, DEFINITION.LANG)
				.values(newMeaningId, d.getValue(), d.getValuePrese(), d.getLang())
				.execute();
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
