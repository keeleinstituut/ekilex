package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.main.Tables.LEXEME_POS;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.LEX_RELATION;
import static eki.ekilex.data.db.main.Tables.LEX_REL_MAPPING;
import static eki.ekilex.data.db.main.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING_REL_MAPPING;
import static eki.ekilex.data.db.main.Tables.MEANING_REL_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.main.Tables.MEANING_TAG;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_REL_MAPPING;
import static eki.ekilex.data.db.main.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;
import static eki.ekilex.data.db.main.Tables.WORD_TAG;
import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Record8;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.InexactSynonym;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordRelation;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningRelMapping;
import eki.ekilex.data.db.main.tables.MeaningRelation;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.records.LexRelationRecord;
import eki.ekilex.data.db.main.tables.records.LexemeDerivRecord;
import eki.ekilex.data.db.main.tables.records.LexemePosRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRegionRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRegisterRecord;
import eki.ekilex.data.db.main.tables.records.LexemeTagRecord;
import eki.ekilex.data.db.main.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.main.tables.records.MeaningSemanticTypeRecord;
import eki.ekilex.data.db.main.tables.records.MeaningTagRecord;
import eki.ekilex.data.db.main.tables.records.WordRecord;
import eki.ekilex.data.db.main.tables.records.WordWordTypeRecord;
import eki.ekilex.service.db.util.SearchFilterHelper;

//associated data lookup for complex business functions
@Component
public class LookupDbService extends AbstractDataDbService {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public String getWordValuePrese(Long wordId) {
		return mainDb.select(WORD.VALUE_PRESE).from(WORD).where(WORD.ID.eq(wordId)).fetchOptionalInto(String.class).orElse(null);
	}

	public String getWordLang(Long wordId) {
		return mainDb.select(WORD.LANG).from(WORD).where(WORD.ID.eq(wordId)).fetchOptionalInto(String.class).orElse(null);
	}

	public Long getWordWordTypeId(Long wordId, String typeCode) {
		WordWordTypeRecord wordWordTypeRecord = mainDb.fetchOne(
				WORD_WORD_TYPE,
				WORD_WORD_TYPE.WORD_ID.eq(wordId)
						.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(typeCode)));
		return wordWordTypeRecord.getId();
	}

	public Long getWordTagId(Long wordId, String tagName) {
		return mainDb
				.select(WORD_TAG.ID)
				.from(WORD_TAG)
				.where(
						WORD_TAG.WORD_ID.eq(wordId)
								.and(WORD_TAG.TAG_NAME.eq(tagName)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.Lexeme> getWordLexemes(Long lexemeId) {
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		return mainDb
				.select(
						l2.ID.as("lexeme_id"),
						l2.LEVEL1,
						l2.LEVEL2)
				.from(l2, l1)
				.where(
						l1.ID.eq(lexemeId)
								.and(l1.WORD_ID.eq(l2.WORD_ID))
								.and(l1.DATASET_CODE.eq(l2.DATASET_CODE)))
				.orderBy(l2.LEVEL1, l2.LEVEL2)
				.fetchInto(eki.ekilex.data.Lexeme.class);
	}

	public Long getWordRelationGroupId(String groupType, Long wordId) {
		Long id = mainDb
				.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.WORD_REL_TYPE_CODE.eq(groupType).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOneInto(Long.class);
		return id;
	}

	public Long getWordRelationGroupId(Long relationId) {
		Long id = mainDb.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP_MEMBER.ID.eq(relationId))
				.fetchOneInto(Long.class);
		return id;
	}

	public List<Map<String, Object>> getWordRelationGroupMembers(Long groupId) {
		return mainDb
				.selectDistinct(
						WORD_GROUP_MEMBER.ID,
						WORD_GROUP_MEMBER.WORD_ID,
						WORD.VALUE,
						WORD_GROUP.WORD_REL_TYPE_CODE)
				.from(WORD_GROUP
						.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID))
						.join(WORD).on(WORD.ID.eq(WORD_GROUP_MEMBER.WORD_ID)))
				.where(WORD_GROUP.ID.eq(groupId))
				.fetchMaps();
	}

	public List<Long> getWordIdsOfJoinCandidates(eki.ekilex.data.Word targetWord, List<String> userPrefDatasetCodes, List<String> userVisibleDatasetCodes) {

		String wordValue = targetWord.getWordValue();
		String wordLang = targetWord.getLang();
		Long wordIdToExclude = targetWord.getWordId();
		boolean isPrefixoid = targetWord.isPrefixoid();
		boolean isSuffixoid = targetWord.isSuffixoid();

		Condition whereCondition = WORD.VALUE.like(wordValue)
				.and(WORD.ID.ne(wordIdToExclude))
				.and(WORD.LANG.eq(wordLang))
				.and(WORD.IS_PUBLIC.isTrue())
				.andExists(DSL
						.select(LEXEME.ID)
						.from(LEXEME)
						.where(
								LEXEME.WORD_ID.eq(WORD.ID)
										.and(LEXEME.DATASET_CODE.in(userPrefDatasetCodes))));

		if (isPrefixoid) {
			whereCondition = whereCondition.andExists(DSL
					.select(WORD_WORD_TYPE.ID)
					.from(WORD_WORD_TYPE)
					.where(WORD_WORD_TYPE.WORD_ID.eq(WORD.ID))
					.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODE_PREFIXOID)));
		} else if (isSuffixoid) {
			whereCondition = whereCondition.andExists(DSL
					.select(WORD_WORD_TYPE.ID)
					.from(WORD_WORD_TYPE)
					.where(WORD_WORD_TYPE.WORD_ID.eq(WORD.ID))
					.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODE_SUFFIXOID)));
		} else {
			whereCondition = whereCondition.andNotExists(DSL
					.select(WORD_WORD_TYPE.ID)
					.from(WORD_WORD_TYPE)
					.where(WORD_WORD_TYPE.WORD_ID.eq(WORD.ID))
					.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODE_PREFIXOID, WORD_TYPE_CODE_SUFFIXOID)));
		}

		Table<Record4<Long, Long, String, Boolean>> wl = DSL
				.select(
						WORD.ID.as("word_id"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.DATASET_CODE.as("dataset_code"),
						LEXEME.IS_PUBLIC.as("is_public"))
				.from(WORD, LEXEME)
				.where(whereCondition)
				.asTable("wl");

		return mainDb
				.selectDistinct(wl.field("word_id"))
				.from(wl)
				.where(wl.field("is_public", boolean.class).eq(PUBLICITY_PUBLIC)
						.or(wl.field("dataset_code", String.class).in(userVisibleDatasetCodes)))
				.fetchInto(Long.class);
	}

	public List<eki.ekilex.data.Word> getWords(String wordValue, String wordLang) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		List<Field<?>> wordFields = queryHelper.getWordFields(w);

		return mainDb
				.select(wordFields)
				.from(w)
				.where(
						w.VALUE.eq(wordValue)
								.and(w.IS_PUBLIC.isTrue())
								.and(w.LANG.eq(wordLang))
								.andExists(DSL
										.select(l.ID)
										.from(l)
										.where(l.WORD_ID.eq(w.ID))))
				.fetchInto(eki.ekilex.data.Word.class);
	}

	public List<Long> getWordDatasetCodes(Long wordId) {

		return mainDb
				.selectDistinct(LEXEME.DATASET_CODE)
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId))
				.fetchInto(Long.class);
	}

	public List<String> getWordTypeCodes(Long wordId) {

		return mainDb
				.select(WORD_WORD_TYPE.WORD_TYPE_CODE)
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordId))
				.orderBy(WORD_WORD_TYPE.ORDER_BY)
				.fetchInto(String.class);
	}

	public List<WordRelation> getWordRelations(Long wordId, String relTypeCode) {
		return mainDb.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION)
				.where(WORD_RELATION.WORD1_ID.eq(wordId))
				.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(WordRelation.class);
	}

	public List<Classifier> getWordOppositeRelations(String relationTypeCode, String classifLabelLang) {

		return mainDb
				.select(WORD_REL_TYPE_LABEL.CODE, WORD_REL_TYPE_LABEL.VALUE)
				.from(WORD_REL_MAPPING, WORD_REL_TYPE_LABEL)
				.where(
						WORD_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(WORD_REL_TYPE_LABEL.CODE.eq(WORD_REL_MAPPING.CODE2))
								.and(WORD_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(WORD_REL_TYPE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.fetchInto(Classifier.class);
	}

	public int getWordLexemesMaxLevel1(Long wordId, String datasetCode) {

		return mainDb
				.select(DSL.max(LEXEME.LEVEL1))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchOptionalInto(Integer.class)
				.orElse(0);
	}

	public List<eki.ekilex.data.Lexeme> getWordLexemesLevels(Long wordId) {

		Lexeme l = LEXEME.as("l");
		return mainDb
				.select(
						l.ID.as("lexeme_id"),
						l.DATASET_CODE,
						l.LEVEL1,
						l.LEVEL2)
				.from(l)
				.where(l.WORD_ID.eq(wordId))
				.orderBy(
						l.DATASET_CODE,
						l.LEVEL1,
						l.LEVEL2)
				.fetchInto(eki.ekilex.data.Lexeme.class);
	}

	public WordLexemeMeaningIdTuple getWordLexemeMeaningIdByLexeme(Long lexemeId) {

		return mainDb
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(WordLexemeMeaningIdTuple.class);
	}

	public List<WordLexemeMeaningIdTuple> getWordLexemeMeaningIdsByMeaning(Long meaningId) {

		return mainDb
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId))
				.fetchInto(WordLexemeMeaningIdTuple.class);
	}

	public List<WordLexemeMeaningIdTuple> getWordLexemeMeaningIdsByMeaning(Long meaningId, String datasetCode) {

		return mainDb
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchInto(WordLexemeMeaningIdTuple.class);
	}

	public List<WordLexemeMeaningIdTuple> getWordLexemeMeaningIdsByWord(Long wordId, String datasetCode) {

		return mainDb
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchInto(WordLexemeMeaningIdTuple.class);
	}

	public WordRecord getWordRecord(Long wordId) {
		return mainDb.selectFrom(WORD).where(WORD.ID.eq(wordId)).fetchOne();
	}

	public String getLexemeDatasetCode(Long lexemeId) {

		return mainDb
				.select(LEXEME.DATASET_CODE)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchOneInto(String.class);
	}

	public Long getLexemeId(Long wordId, Long meaningId) {

		return mainDb
				.select(LEXEME.ID)
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId).and(LEXEME.MEANING_ID.eq(meaningId)))
				.fetchSingleInto(Long.class);
	}

	public Long getLexemeMeaningId(Long lexemeId) {

		return mainDb
				.select(LEXEME.MEANING_ID)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(Long.class);
	}

	public Long getLexemeWordId(Long lexemeId) {

		return mainDb
				.select(LEXEME.WORD_ID)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(Long.class);
	}

	public Integer getLexemeLevel2MinimumValue(Long wordId, String datasetCode, Integer level1) {
		return mainDb
				.select(DSL.min(LEXEME.LEVEL2))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.DATASET_CODE.eq(datasetCode))
						.and(LEXEME.LEVEL1.eq(level1)))
				.fetchOneInto(Integer.class);
	}

	public Long getLexemePosId(Long lexemeId, String posCode) {
		LexemePosRecord lexemePosRecord = mainDb.fetchOne(LEXEME_POS, LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(posCode)));
		return lexemePosRecord.getId();
	}

	public Long getLexemeTagId(Long lexemeId, String tagName) {
		LexemeTagRecord lexemeTagRecord = mainDb.fetchOne(LEXEME_TAG, LEXEME_TAG.LEXEME_ID.eq(lexemeId).and(LEXEME_TAG.TAG_NAME.eq(tagName)));
		return lexemeTagRecord.getId();
	}

	public Long getMeaningTagId(Long meaningId, String tagName) {
		MeaningTagRecord meaningTagRecord = mainDb.fetchOne(MEANING_TAG, MEANING_TAG.MEANING_ID.eq(meaningId).and(MEANING_TAG.TAG_NAME.eq(tagName)));
		return meaningTagRecord.getId();
	}

	public Long getLexemeDerivId(Long lexemeId, String derivCode) {
		LexemeDerivRecord lexemeDerivRecord = mainDb.fetchOne(LEXEME_DERIV, LEXEME_DERIV.LEXEME_ID.eq(lexemeId).and(LEXEME_DERIV.DERIV_CODE.eq(derivCode)));
		return lexemeDerivRecord.getId();
	}

	public Long getLexemeRegisterId(Long lexemeId, String registerCode) {
		LexemeRegisterRecord lexemeRegisterRecord = mainDb.fetchOne(LEXEME_REGISTER,
				LEXEME_REGISTER.LEXEME_ID.eq(lexemeId).and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode)));
		return lexemeRegisterRecord.getId();
	}

	public Long getLexemeRegionId(Long lexemeId, String regionCode) {
		LexemeRegionRecord lexemeRegionRecord = mainDb.fetchOne(LEXEME_REGION,
				LEXEME_REGION.LEXEME_ID.eq(lexemeId).and(LEXEME_REGION.REGION_CODE.eq(regionCode)));
		return lexemeRegionRecord.getId();
	}

	public List<Classifier> getLexemeOppositeRelationTypes(String relationTypeCode, String classifLabelLang) {

		return mainDb
				.select(
						LEX_REL_TYPE_LABEL.CODE,
						LEX_REL_TYPE_LABEL.VALUE)
				.from(
						LEX_REL_MAPPING,
						LEX_REL_TYPE_LABEL)
				.where(
						LEX_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(LEX_REL_TYPE_LABEL.CODE.eq(LEX_REL_MAPPING.CODE2))
								.and(LEX_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(LEX_REL_TYPE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.fetchInto(Classifier.class);
	}

	public List<String> getLexemeCollocValuesByMembership(Long lexemeId) {

		Word cw = WORD.as("cw");
		Lexeme cl = LEXEME.as("cl");
		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.select(cw.VALUE)
				.from(cw, cl)
				.where(
						cl.WORD_ID.eq(cw.ID)
								.andExists(DSL
										.select(cm.ID)
										.from(cm)
										.where(
												cm.COLLOC_LEXEME_ID.eq(cl.ID)
														.and(cm.MEMBER_LEXEME_ID.eq(lexemeId)))))
				.groupBy(cw.VALUE)
				.orderBy(cw.VALUE)
				.fetchInto(String.class);
	}

	public LexemeRecord getLexemeRecord(Long lexemeId) {
		return mainDb.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
	}

	public LexemeRecord getLexemeRecord(Long wordId, Long meaningId, String datasetCode) {
		return mainDb
				.selectFrom(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.MEANING_ID.eq(meaningId))
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchOne();
	}

	public List<LexemeRecord> getLexemeRecordsWithHigherLevel1(Long wordId, String datasetCode, Integer level1) {
		return mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.DATASET_CODE.eq(datasetCode))
						.and(LEXEME.LEVEL1.gt(level1)))
				.fetch();
	}

	public List<LexemeRecord> getLexemeRecordsWithHigherLevel2(Long wordId, String datasetCode, Integer level1, Integer level2) {
		return mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.DATASET_CODE.eq(datasetCode))
						.and(LEXEME.LEVEL1.eq(level1))
						.and(LEXEME.LEVEL2.gt(level2)))
				.fetch();
	}

	public List<LexemeRecord> getLexemeRecordsByWord(Long wordId) {
		return mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId))
				.orderBy(LEXEME.ORDER_BY)
				.fetch();
	}

	public List<LexemeRecord> getLexemeRecordsByMeaning(Long meaningId) {
		return mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId))
				.orderBy(LEXEME.ORDER_BY)
				.fetch();
	}

	public List<LexemeRecord> getLexemeRecordsByMeaning(Long meaningId, String datasetCode) {
		return mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId)
						.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.orderBy(LEXEME.ORDER_BY)
				.fetch();
	}

	public List<LexRelationRecord> getLexRelationRecords(Long lexemeId) {

		return mainDb
				.selectFrom(LEX_RELATION)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId)
								.or(LEX_RELATION.LEXEME2_ID.eq(lexemeId)))
				.orderBy(LEX_RELATION.ORDER_BY)
				.fetch();
	}

	public List<eki.ekilex.data.Meaning> getMeanings(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, Long excludedMeaningId) {

		String maskedSearchFilter = searchFilter.replace(SEARCH_MASK_CHARS, "%").replace(SEARCH_MASK_CHAR, "_");
		Field<String> filterField = DSL.lower(maskedSearchFilter);
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Condition whereWordValue;
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			whereWordValue = DSL.lower(w.VALUE).like(filterField);
		} else {
			whereWordValue = DSL.lower(w.VALUE).equal(filterField);
		}

		Condition whereLexemeDataset = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		Condition whereExcludeMeaningId = DSL.noCondition();
		if (excludedMeaningId != null) {
			whereExcludeMeaningId = m.ID.ne(excludedMeaningId);
		}

		Table<Record1<Long>> mid = DSL
				.selectDistinct(m.ID.as("meaning_id"))
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(w.ID.eq(l.WORD_ID))
								.and(whereWordValue)
								.and(w.IS_PUBLIC.isTrue())
								.and(whereLexemeDataset)
								.and(whereExcludeMeaningId))
				.asTable("mid");

		return mainDb
				.select(
						MEANING.ID.as("meaning_id"),
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"),
						DSL.arrayAggDistinct(LEXEME.DATASET_CODE).as("lexeme_dataset_codes"))
				.from(MEANING, LEXEME, mid)
				.where(
						MEANING.ID.eq(mid.field("meaning_id", Long.class))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.IS_PUBLIC.eq(PUBLICITY_PUBLIC)
										.or(LEXEME.DATASET_CODE.in(userPermDatasetCodes))))
				.groupBy(MEANING.ID)
				.fetchInto(eki.ekilex.data.Meaning.class);
	}

	public List<Long> getMeaningIds(String wordValue, String wordLang, String datasetCode) {

		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		return mainDb
				.select(l.MEANING_ID)
				.from(l, w)
				.where(
						w.VALUE.eq(wordValue)
								.and(w.LANG.eq(wordLang))
								.and(l.WORD_ID.eq(w.ID))
								.and(l.DATASET_CODE.eq(datasetCode)))
				.orderBy(l.MEANING_ID)
				.fetchInto(Long.class);
	}

	public List<eki.ekilex.data.Lexeme> getMeaningLexemes(Long meaningId, String datasetCode, String wordLang) {

		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Condition where = l.MEANING_ID.eq(meaningId)
				.and(l.DATASET_CODE.eq(datasetCode))
				.and(l.WORD_ID.eq(w.ID));

		if (StringUtils.isNotBlank(wordLang)) {
			where = where.and(w.LANG.eq(wordLang));
		}

		return mainDb
				.select(
						l.WORD_ID,
						l.MEANING_ID,
						l.ID.as("lexeme_id"))
				.from(l, w)
				.where(where)
				.orderBy(l.ORDER_BY)
				.fetchInto(eki.ekilex.data.Lexeme.class);
	}

	public List<IdPair> getMeaningsCommonWordsLexemeIdPairs(Long meaningId, Long sourceMeaningId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Meaning m1 = MEANING.as("m1");
		Meaning m2 = MEANING.as("m2");

		SelectConditionStep<Record1<Long>> wordIds = DSL.selectDistinct(l1.WORD_ID)
				.from(l1, m1)
				.where(l1.MEANING_ID.eq(meaningId)
						.and(l1.MEANING_ID.eq(m1.ID))
						.andExists(DSL
								.select(l2.ID)
								.from(l2, m2)
								.where(m2.ID.eq(sourceMeaningId)
										.and(l2.MEANING_ID.eq(m2.ID))
										.and(l2.WORD_ID.eq(l1.WORD_ID)))));

		return mainDb
				.select(l1.ID.as("id1"), l2.ID.as("id2"))
				.from(l1, l2)
				.where(l1.WORD_ID.in(wordIds)
						.and(l1.DATASET_CODE.eq(l2.DATASET_CODE))
						.and(l1.MEANING_ID.eq(meaningId))
						.and(l2.MEANING_ID.eq(sourceMeaningId))
						.and(l2.WORD_ID.eq(l1.WORD_ID)))
				.fetchInto(IdPair.class);
	}

	public List<Long> getMeaningDefinitionIds(Long meaningId, boolean publicDataOnly) {

		Condition where = DEFINITION.MEANING_ID.eq(meaningId);
		if (publicDataOnly) {
			where = where.and(DEFINITION.IS_PUBLIC.isTrue());
		}
		return mainDb.select(DEFINITION.ID).from(DEFINITION).where(where).orderBy(DEFINITION.ORDER_BY).fetchInto(Long.class);
	}

	public Long getMeaningDomainId(Long meaningId, Classifier domain) {
		MeaningDomainRecord meaningDomainRecord = mainDb.fetchOne(MEANING_DOMAIN,
				MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin()))
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode())));
		return meaningDomainRecord.getId();
	}

	public Long getMeaningSemanticTypeId(Long meaningId, String semanticTypeCode) {
		MeaningSemanticTypeRecord meaningSemanticTypeRecord = mainDb
				.fetchOne(MEANING_SEMANTIC_TYPE, MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId).and(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE.eq(semanticTypeCode)));
		return meaningSemanticTypeRecord.getId();
	}

	public List<Long> getMeaningLexemeIds(Long meaningId, String lang, String datasetCode) {

		return mainDb
				.select(LEXEME.ID)
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(WORD.ID.eq(LEXEME.WORD_ID))
								.and(WORD.LANG.eq(lang))
								.and(WORD.IS_PUBLIC.isTrue()))
				.fetchInto(Long.class);
	}

	public List<Long> getMeaningRelationOppositeRelationIds(Long relationId) {

		MeaningRelation mr1 = MEANING_RELATION.as("mr1");
		MeaningRelation mr2 = MEANING_RELATION.as("mr2");
		MeaningRelMapping mrm = MEANING_REL_MAPPING.as("mrm");

		return mainDb
				.select(mr2.ID)
				.from(mr1, mr2, mrm)
				.where(
						mr1.ID.eq(relationId)
								.and(mr1.MEANING1_ID.eq(mr2.MEANING2_ID))
								.and(mr1.MEANING2_ID.eq(mr2.MEANING1_ID))
								.and(mr1.MEANING_REL_TYPE_CODE.eq(mrm.CODE1))
								.and(mr2.MEANING_REL_TYPE_CODE.eq(mrm.CODE2))
								.and(mr2.ID.ne(mr1.ID)))
				.fetchInto(Long.class);
	}

	public Map<String, Integer[]> getMeaningsWordsWithMultipleHomonymNumbers(List<Long> meaningIds) {

		Field<String> wordValue = WORD.VALUE.as("word_value");
		Field<Integer[]> homonymNumbers = DSL.arrayAggDistinct(WORD.HOMONYM_NR).as("homonym_numbers");

		Table<Record2<String, Integer[]>> wv = DSL
				.select(wordValue, homonymNumbers)
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.in(meaningIds)
								.and(WORD.ID.eq(LEXEME.WORD_ID))
								.and(WORD.IS_PUBLIC.isTrue()))
				.groupBy(WORD.VALUE)
				.asTable("wv");

		return mainDb
				.selectFrom(wv)
				.where(PostgresDSL.arrayLength(wv.field(homonymNumbers)).gt(1))
				.fetchMap(wordValue, homonymNumbers);
	}

	public String getMeaningFirstDatasetCode(Long meaningId) {

		return mainDb
				.select(LEXEME.DATASET_CODE)
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId))
				.limit(1)
				.fetchSingleInto(String.class);
	}

	public Map<Long, String[]> getMeaningRelationDatasetCodes(Long meaningId) {

		MeaningRelation mr = MEANING_RELATION.as("mr");
		Lexeme l = LEXEME.as("l");

		Table<Record2<Long, String>> rmds = DSL
				.select(mr.MEANING2_ID.as("rel_meaning_id"), l.DATASET_CODE)
				.from(mr, l)
				.where(mr.MEANING1_ID.eq(meaningId)).and(l.MEANING_ID.eq(mr.MEANING2_ID))
				.unionAll(DSL
						.select(mr.MEANING1_ID.as("rel_meaning_id"), l.DATASET_CODE)
						.from(mr, l)
						.where(mr.MEANING2_ID.eq(meaningId)).and(l.MEANING_ID.eq(mr.MEANING1_ID)))
				.asTable("rmds");

		Field<Long> meaningIdField = rmds.field("rel_meaning_id", Long.class);
		Field<String> datasetCodeField = rmds.field("dataset_code", String.class);

		return mainDb
				.select(meaningIdField, DSL.arrayAggDistinct(datasetCodeField))
				.from(rmds)
				.groupBy(meaningIdField)
				.fetchMap(meaningIdField, DSL.arrayAggDistinct(datasetCodeField));
	}

	public List<Classifier> getMeaningOppositeRelations(String relationTypeCode, String classifLabelLang) {

		return mainDb
				.select(MEANING_REL_TYPE_LABEL.CODE, MEANING_REL_TYPE_LABEL.VALUE)
				.from(MEANING_REL_MAPPING, MEANING_REL_TYPE_LABEL)
				.where(
						MEANING_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(MEANING_REL_TYPE_LABEL.CODE.eq(MEANING_REL_MAPPING.CODE2))
								.and(MEANING_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(MEANING_REL_TYPE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.fetchInto(Classifier.class);
	}

	public List<InexactSynonym> getMeaningInexactSynonyms(Long meaningId, String targetLang, String datasetCode) {

		MeaningRelation mr = MEANING_RELATION.as("mr");
		Word wtarget = WORD.as("wtarget");
		Word wtrans = WORD.as("wtrans");
		Lexeme ltrans = LEXEME.as("ltrans");
		Lexeme ltarget = LEXEME.as("ltarget");
		Definition def = DEFINITION.as("def");

		Table<Record8<Long, Long, String, Long, String, Long, Long, JSON>> syn = DSL
				.select(
						ltrans.MEANING_ID,
						ltrans.WORD_ID,
						wtrans.VALUE.as("translation_lang_word_value"),
						wtrans.ID.as("translation_lang_word_id"),
						wtrans.LANG.as("translation_lang"),
						mr.ID.as("relation_id"),
						mr.ORDER_BY,
						field("json_agg(json_build_object('wordId', wtarget.id, 'wordValue', wtarget.value) order by ltarget.order_by) filter (where wtarget.id is not null)", JSON.class)
								.as("target_lang_words"))
				.from(
						ltrans,
						wtrans,
						mr
								.leftOuterJoin(ltarget).on(ltarget.MEANING_ID.eq(mr.MEANING2_ID).and(ltarget.DATASET_CODE.eq(datasetCode)))
								.leftOuterJoin(wtarget).on(wtarget.ID.eq(ltarget.WORD_ID).and(wtarget.LANG.eq(targetLang))))
				.where(
						mr.MEANING1_ID.eq(meaningId)
								.and(mr.MEANING_REL_TYPE_CODE.in(MEANING_REL_TYPE_CODE_NARROW, MEANING_REL_TYPE_CODE_WIDE))
								.and(ltrans.MEANING_ID.eq(mr.MEANING2_ID))
								.and(wtrans.ID.eq(ltrans.WORD_ID))
								.and(wtrans.LANG.ne(targetLang)))
				.groupBy(ltrans.ID, wtrans.ID, mr.ID)
				.orderBy(mr.ORDER_BY)
				.asTable("syn");

		return mainDb
				.select(
						syn.field("translation_lang_word_value", String.class),
						syn.field("translation_lang_word_id", Long.class),
						syn.field("meaning_id", Long.class),
						syn.field("word_id", Long.class),
						syn.field("translation_lang", String.class),
						syn.field("target_lang_words", JSON.class),
						syn.field("relation_id", Long.class),
						syn.field("order_by", Long.class),
						def.VALUE.as("inexact_definition_value"))
				.from(syn.leftOuterJoin(def).on(
						def.MEANING_ID.eq(syn.field("meaning_id", Long.class))
								.and(def.DEFINITION_TYPE_CODE.eq(DEFINITION_TYPE_CODE_INEXACT_SYN))))
				.fetchInto(InexactSynonym.class);
	}

	public boolean meaningDomainExists(Long meaningId, String domainCode, String domainOrigin) {

		return mainDb
				.select(DSL.field(DSL.count(MEANING_DOMAIN.ID).gt(0)).as("meaning_domain_exists"))
				.from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domainCode))
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domainOrigin)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningFreeformExists(Long meaningId, String freeformValue, String freeformTypeCode) {

		return mainDb
				.select(DSL.field(DSL.count(FREEFORM.ID).gt(0)).as("meaning_freeform_exists"))
				.from(MEANING_FREEFORM, FREEFORM)
				.where(MEANING_FREEFORM.MEANING_ID.eq(meaningId)
						.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.VALUE.eq(freeformValue))
						.and(FREEFORM.FREEFORM_TYPE_CODE.eq(freeformTypeCode)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningPublicLexemeExists(Long meaningId, String datasetCode) {

		return mainDb
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("public_lexeme_exists"))
				.from(LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.IS_PUBLIC.eq(PUBLICITY_PUBLIC)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordPublicLexemeExists(Long wordId, String datasetCode) {

		return mainDb
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("public_lexeme_exists"))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.IS_PUBLIC.eq(PUBLICITY_PUBLIC)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningHasWord(Long meaningId, String wordValue, String language) {

		return mainDb
				.select(DSL.field(DSL.count(WORD.ID).gt(0)).as("word_exists"))
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.LANG.eq(language))
								.and(WORD.IS_PUBLIC.isTrue()))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningHasWord(Long meaningId, String datasetCode, String wordValue, String language) {

		return mainDb
				.select(DSL.field(DSL.count(WORD.ID).gt(0)).as("word_exists"))
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.LANG.eq(language))
								.and(WORD.IS_PUBLIC.isTrue()))
				.fetchSingleInto(Boolean.class);
	}

	public boolean lexemeTagExists(Long lexemeId, String tagName) {

		return mainDb
				.select(field(DSL.count(LEXEME_TAG.ID).gt(0)).as("lexeme_tag_exists"))
				.from(LEXEME_TAG)
				.where(
						LEXEME_TAG.LEXEME_ID.eq(lexemeId)
								.and(LEXEME_TAG.TAG_NAME.eq(tagName)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningTagExists(Long meaningId, String tagName) {

		return mainDb
				.select(field(DSL.count(MEANING_TAG.ID).gt(0)).as("meaning_tag_exists"))
				.from(MEANING_TAG)
				.where(
						MEANING_TAG.MEANING_ID.eq(meaningId)
								.and(MEANING_TAG.TAG_NAME.eq(tagName)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningRelationExists(Long meaningId1, Long meaningId2, String relationType) {

		return mainDb
				.select(field(DSL.count(MEANING_RELATION.ID).eq(1)).as("relation_exists"))
				.from(MEANING_RELATION)
				.where(
						MEANING_RELATION.MEANING1_ID.eq(meaningId1)
								.and(MEANING_RELATION.MEANING2_ID.eq(meaningId2))
								.and(MEANING_RELATION.MEANING_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean lexemeRelationExists(Long lexemeId1, Long lexemeId2, String relationType) {

		return mainDb
				.select(field(DSL.count(LEX_RELATION.ID).eq(1)).as("relation_exists"))
				.from(LEX_RELATION)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId1)
								.and(LEX_RELATION.LEXEME2_ID.eq(lexemeId2))
								.and(LEX_RELATION.LEX_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordRelationExists(Long wordId1, Long wordId2, String relationType) {

		return mainDb
				.select(field(DSL.count(WORD_RELATION.ID).eq(1)).as("relation_exists"))
				.from(WORD_RELATION)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId1)
								.and(WORD_RELATION.WORD2_ID.eq(wordId2))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordHasForms(Long wordId) {

		return mainDb
				.select(field(DSL.count(FORM.ID).gt(0)).as("forms_exist"))
				.from(PARADIGM, PARADIGM_FORM, FORM)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
								.and(PARADIGM_FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(PARADIGM_FORM.FORM_ID.eq(FORM.ID)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordLexemeExists(Long wordId, String datasetCode) {

		return mainDb
				.select(field(DSL.count(WORD.ID).gt(0)).as("word_lexeme_exists"))
				.from(WORD)
				.where(
						WORD.ID.eq(wordId)
								.andExists(DSL
										.select(LEXEME.ID)
										.from(LEXEME)
										.where(LEXEME.WORD_ID.eq(WORD.ID)
												.and(LEXEME.DATASET_CODE.eq(datasetCode)))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordExists(String wordValue, String wordLang) {

		Word w = WORD.as("w");

		return mainDb
				.select(field(DSL.count(w.ID).gt(0)).as("word_exists"))
				.from(w)
				.where(
						w.VALUE.eq(wordValue)
								.and(w.LANG.eq(wordLang))
								.and(w.IS_PUBLIC.isTrue()))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isOnlyLexemeForWord(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = mainDb
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.eq(lexemeId)
										.and(l1.WORD_ID.eq(l2.WORD_ID))
										.and(l1.ID.ne(l2.ID))));
		return count == 0;
	}

	public boolean isOnlyLexemeForMeaning(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = mainDb
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.eq(lexemeId)
										.and(l1.MEANING_ID.eq(l2.MEANING_ID))
										.and(l1.ID.ne(l2.ID))));
		return count == 0;
	}

	public boolean areOnlyLexemesForMeaning(List<Long> lexemeIds) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = mainDb
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.in(lexemeIds)
										.and(l1.MEANING_ID.eq(l2.MEANING_ID))
										.and(l1.ID.ne(l2.ID))));
		return count == lexemeIds.size();
	}

	public boolean isOnlyLexemesForMeaning(Long meaningId, String datasetCode) {

		boolean otherDatasetLexemesExist = mainDb
				.fetchExists(DSL
						.select(LEXEME.ID)
						.from(LEXEME)
						.where(
								LEXEME.MEANING_ID.eq(meaningId)
										.and(LEXEME.DATASET_CODE.ne(datasetCode))));
		boolean noOtherDatasetLexemesExist = !otherDatasetLexemesExist;
		return noOtherDatasetLexemesExist;
	}

	public boolean isMemberOfWordRelationGroup(Long groupId, Long wordId) {
		Long id = mainDb
				.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.ID.eq(groupId).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOneInto(Long.class);
		boolean exists = (id != null);
		return exists;
	}

	public boolean isOnlyValuePreseUpdate(Long wordId, String wordValue, String wordValuePrese) {

		return mainDb
				.select(field(DSL.count(WORD.ID).eq(1)).as("is_only_value_prese_update"))
				.from(WORD)
				.where(
						WORD.ID.eq(wordId)
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.VALUE_PRESE.ne(wordValuePrese)))
				.fetchSingleInto(Boolean.class);
	}

}
