package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.LEX_REL_MAPPING;
import static eki.ekilex.data.db.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_REL_MAPPING;
import static eki.ekilex.data.db.Tables.MEANING_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.Tables.MEANING_TAG;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_MAPPING;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;
import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordRelation;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningRelation;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.records.LexemeDerivRecord;
import eki.ekilex.data.db.tables.records.LexemePosRecord;
import eki.ekilex.data.db.tables.records.LexemeRegionRecord;
import eki.ekilex.data.db.tables.records.LexemeRegisterRecord;
import eki.ekilex.data.db.tables.records.LexemeTagRecord;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.tables.records.MeaningSemanticTypeRecord;
import eki.ekilex.data.db.tables.records.MeaningTagRecord;
import eki.ekilex.data.db.tables.records.WordWordTypeRecord;
import eki.ekilex.service.db.util.SearchFilterHelper;

//associated data lookup for complex business functions
@Component
public class LookupDbService extends AbstractDataDbService {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public String getWordValuePrese(Long wordId) {
		return create.select(WORD.VALUE_PRESE).from(WORD).where(WORD.ID.eq(wordId)).fetchOptionalInto(String.class).orElse(null);
	}

	public Long getWordWordTypeId(Long wordId, String typeCode) {
		WordWordTypeRecord wordWordTypeRecord = create.fetchOne(WORD_WORD_TYPE, WORD_WORD_TYPE.WORD_ID.eq(wordId).and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(typeCode)));
		return wordWordTypeRecord.getId();
	}

	public List<WordLexeme> getWordLexemes(Long lexemeId) {
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		return create
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
				.fetchInto(WordLexeme.class);
	}

	public Long getWordRelationGroupId(String groupType, Long wordId) {
		Long id = create
				.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.WORD_REL_TYPE_CODE.eq(groupType).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOneInto(Long.class);
		return id;
	}

	public Long getWordRelationGroupId(Long relationId) {
		Long id = create.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP_MEMBER.ID.eq(relationId))
				.fetchOneInto(Long.class);
		return id;
	}

	public Long getWordId(Long freeformId) {
		Long wordId = create.select(WORD_FREEFORM.WORD_ID)
				.from(WORD_FREEFORM)
				.where(WORD_FREEFORM.FREEFORM_ID.eq(freeformId))
				.fetchOneInto(Long.class);
		return wordId;
	}

	public List<Map<String, Object>> getWordRelationGroupMembers(Long groupId) {
		return create
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
	
		Table<Record4<Long,Long,String,Boolean>> wl = DSL
				.select(
						WORD.ID.as("word_id"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.DATASET_CODE.as("dataset_code"),
						LEXEME.IS_PUBLIC.as("is_public"))
				.from(WORD, LEXEME)
				.where(whereCondition)
				.asTable("wl");
	
		return create
				.selectDistinct(wl.field("word_id"))
				.from(wl)
				.where(wl.field("is_public", boolean.class).eq(PUBLICITY_PUBLIC)
						.or(wl.field("dataset_code", String.class).in(userVisibleDatasetCodes)))
				.fetchInto(Long.class);
	}

	public List<WordRelation> getWordRelations(Long wordId, String relTypeCode) {
		return create.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION)
				.where(WORD_RELATION.WORD1_ID.eq(wordId))
				.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(WordRelation.class);
	}

	public int getWordLexemesMaxLevel1(Long wordId, String datasetCode) {
	
		return create
				.select(DSL.max(LEXEME.LEVEL1))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchOptionalInto(Integer.class)
				.orElse(0);
	}

	public List<WordLexeme> getWordLexemesLevels(Long wordId) {

		Lexeme l = LEXEME.as("l");
		return create
				.select(l.ID.as("lexeme_id"), l.DATASET_CODE, l.LEVEL1, l.LEVEL2)
				.from(l)
				.where(l.WORD_ID.eq(wordId))
				.orderBy(l.DATASET_CODE, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public String getLexemeDatasetCode(Long lexemeId) {

		return create
				.select(LEXEME.DATASET_CODE)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchOneInto(String.class);
	}

	public Long getLexemeMeaningId(Long lexemeId) {
	
		return create
				.select(LEXEME.MEANING_ID)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(Long.class);
	}

	public Long getLexemePosId(Long lexemeId, String posCode) {
		LexemePosRecord lexemePosRecord = create.fetchOne(LEXEME_POS, LEXEME_POS.LEXEME_ID.eq(lexemeId).and(LEXEME_POS.POS_CODE.eq(posCode)));
		return lexemePosRecord.getId();
	}

	public Long getLexemeTagId(Long lexemeId, String tagName) {
		LexemeTagRecord lexemeTagRecord = create.fetchOne(LEXEME_TAG, LEXEME_TAG.LEXEME_ID.eq(lexemeId).and(LEXEME_TAG.TAG_NAME.eq(tagName)));
		return lexemeTagRecord.getId();
	}

	public Long getMeaningTagId(Long meaningId, String tagName) {
		MeaningTagRecord meaningTagRecord = create.fetchOne(MEANING_TAG, MEANING_TAG.MEANING_ID.eq(meaningId).and(MEANING_TAG.TAG_NAME.eq(tagName)));
		return meaningTagRecord.getId();
	}

	public Long getLexemeDerivId(Long lexemeId, String derivCode) {
		LexemeDerivRecord lexemeDerivRecord = create.fetchOne(LEXEME_DERIV, LEXEME_DERIV.LEXEME_ID.eq(lexemeId).and(LEXEME_DERIV.DERIV_CODE.eq(derivCode)));
		return lexemeDerivRecord.getId();
	}

	public Long getLexemeRegisterId(Long lexemeId, String registerCode) {
		LexemeRegisterRecord lexemeRegisterRecord = create.fetchOne(LEXEME_REGISTER,
				LEXEME_REGISTER.LEXEME_ID.eq(lexemeId).and(LEXEME_REGISTER.REGISTER_CODE.eq(registerCode)));
		return lexemeRegisterRecord.getId();
	}

	public Long getLexemeRegionId(Long lexemeId, String regionCode) {
		LexemeRegionRecord lexemeRegionRecord = create.fetchOne(LEXEME_REGION,
				LEXEME_REGION.LEXEME_ID.eq(lexemeId).and(LEXEME_REGION.REGION_CODE.eq(regionCode)));
		return lexemeRegionRecord.getId();
	}

	public Long getMeaningDomainId(Long meaningId, Classifier domain) {
		MeaningDomainRecord meaningDomainRecord = create.fetchOne(MEANING_DOMAIN,
				MEANING_DOMAIN.MEANING_ID.eq(meaningId)
						.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(domain.getOrigin()))
						.and(MEANING_DOMAIN.DOMAIN_CODE.eq(domain.getCode())));
		return meaningDomainRecord.getId();
	}

	public Long getMeaningSemanticTypeId(Long meaningId, String semanticTypeCode) {
		MeaningSemanticTypeRecord meaningSemanticTypeRecord = create
				.fetchOne(MEANING_SEMANTIC_TYPE, MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId).and(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE.eq(semanticTypeCode)));
		return meaningSemanticTypeRecord.getId();
	}

	public List<Long> getMeaningLexemeIds(Long meaningId, String lang, String datasetCode) {
	
		return create
				.select(LEXEME.ID)
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(WORD.ID.eq(LEXEME.WORD_ID))
								.and(WORD.LANG.eq(lang)))
				.fetchInto(Long.class);
	}

	public Long getMeaningRelationSameTypeOppositeRelationId(Long relationId) {

		MeaningRelation mr1 = MEANING_RELATION.as("mr1");
		MeaningRelation mr2 = MEANING_RELATION.as("mr2");

		return create
				.select(mr2.ID)
				.from(mr1, mr2)
				.where(
						mr1.ID.eq(relationId)
								.and(mr1.MEANING1_ID.eq(mr2.MEANING2_ID))
								.and(mr1.MEANING2_ID.eq(mr2.MEANING1_ID))
								.and(mr1.MEANING_REL_TYPE_CODE.eq(mr2.MEANING_REL_TYPE_CODE))
								.and(mr2.ID.ne(mr1.ID)))
				.fetchOneInto(Long.class);
	}

	public Map<String, Integer[]> getMeaningsWordsWithMultipleHomonymNumbers(List<Long> meaningIds) {

		Field<String> wordValue = WORD.VALUE.as("word_value");
		Field<Integer[]> homonymNumbers = DSL.arrayAggDistinct(WORD.HOMONYM_NR).as("homonym_numbers");

		Table<Record2<String, Integer[]>> wv = DSL
				.select(wordValue, homonymNumbers)
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.in(meaningIds)
								.and(WORD.ID.eq(LEXEME.WORD_ID)))
				.groupBy(WORD.VALUE)
				.asTable("wv");

		return create
				.selectFrom(wv)
				.where(PostgresDSL.arrayLength(wv.field(homonymNumbers)).gt(1))
				.fetchMap(wordValue, homonymNumbers);
	}

	public List<WordLexemeMeaningIdTuple> getWordLexemeMeaningIds(Long meaningId) {

		return create
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId))
				.fetchInto(WordLexemeMeaningIdTuple.class);
	}

	public List<WordLexemeMeaningIdTuple> getWordLexemeMeaningIds(Long meaningId, String datasetCode) {

		return create
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

	public List<eki.ekilex.data.Meaning> getMeanings(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, Long excludedMeaningId) {

		String maskedSearchFilter = searchFilter.replace(QUERY_MULTIPLE_CHARACTERS_SYM, "%").replace(QUERY_SINGLE_CHARACTER_SYM, "_");
		Field<String> filterField = DSL.lower(maskedSearchFilter);
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Condition whereFormValue;
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			whereFormValue = DSL.lower(w.VALUE).like(filterField);
		} else {
			whereFormValue = DSL.lower(w.VALUE).equal(filterField);
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
								.and(whereFormValue)
								.and(whereLexemeDataset)
								.and(whereExcludeMeaningId))
				.asTable("mid");

		return create
				.select(
						MEANING.ID.as("meaning_id"),
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"))
				.from(MEANING, LEXEME, mid)
				.where(
						MEANING.ID.eq(mid.field("meaning_id", Long.class))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.IS_PUBLIC.eq(PUBLICITY_PUBLIC)
										.or(LEXEME.DATASET_CODE.in(userPermDatasetCodes))))
				.groupBy(MEANING.ID)
				.fetchInto(eki.ekilex.data.Meaning.class);
	}

	public List<Classifier> getLexemeOppositeRelations(String relationTypeCode, String classifLabelLang, String classifLabelType) {

		return create
				.select(LEX_REL_TYPE_LABEL.CODE, LEX_REL_TYPE_LABEL.VALUE)
				.from(LEX_REL_MAPPING, LEX_REL_TYPE_LABEL)
				.where(
						LEX_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(LEX_REL_TYPE_LABEL.CODE.eq(LEX_REL_MAPPING.CODE2))
								.and(LEX_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(LEX_REL_TYPE_LABEL.TYPE.eq(classifLabelType)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getWordOppositeRelations(String relationTypeCode, String classifLabelLang, String classifLabelType) {

		return create
				.select(WORD_REL_TYPE_LABEL.CODE, WORD_REL_TYPE_LABEL.VALUE)
				.from(WORD_REL_MAPPING, WORD_REL_TYPE_LABEL)
				.where(
						WORD_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(WORD_REL_TYPE_LABEL.CODE.eq(WORD_REL_MAPPING.CODE2))
								.and(WORD_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifLabelType)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getMeaningOppositeRelations(String relationTypeCode, String classifLabelLang, String classifLabelType) {

		return create
				.select(MEANING_REL_TYPE_LABEL.CODE, MEANING_REL_TYPE_LABEL.VALUE)
				.from(MEANING_REL_MAPPING, MEANING_REL_TYPE_LABEL)
				.where(
						MEANING_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(MEANING_REL_TYPE_LABEL.CODE.eq(MEANING_REL_MAPPING.CODE2))
								.and(MEANING_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifLabelType)))
				.fetchInto(Classifier.class);
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

		return create
				.select(meaningIdField, DSL.arrayAggDistinct(datasetCodeField))
				.from(rmds)
				.groupBy(meaningIdField)
				.fetchMap(meaningIdField, DSL.arrayAggDistinct(datasetCodeField));
	}

	public boolean meaningPublicLexemeExists(Long meaningId) {

		return create
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("public_lexeme_exists"))
				.from(LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.IS_PUBLIC.eq(PUBLICITY_PUBLIC)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordPublicLexemeExists(Long wordId) {

		return create
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("public_lexeme_exists"))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.IS_PUBLIC.eq(PUBLICITY_PUBLIC)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningHasWord(Long meaningId, String wordValue, String language) {

		return create
				.select(DSL.field(DSL.count(WORD.ID).gt(0)).as("word_exists"))
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.LANG.eq(language)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningRelationsExist(Long meaningId) {
	
		return create
				.select(DSL.field(DSL.count(MEANING_RELATION.ID).gt(0)).as("meaning_relations_exists"))
				.from(MEANING_RELATION)
				.where(
						MEANING_RELATION.MEANING1_ID.eq(meaningId)
						.or(MEANING_RELATION.MEANING2_ID.eq(meaningId)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningRelationExists(Long meaningId1, Long meaningId2, String relationType) {
	
		return create
				.select(field(DSL.count(MEANING_RELATION.ID).eq(1)).as("relation_exists"))
				.from(MEANING_RELATION)
				.where(
						MEANING_RELATION.MEANING1_ID.eq(meaningId1)
								.and(MEANING_RELATION.MEANING2_ID.eq(meaningId2))
								.and(MEANING_RELATION.MEANING_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean lexemeRelationExists(Long lexemeId1, Long lexemeId2, String relationType) {
	
		return create
				.select(field(DSL.count(LEX_RELATION.ID).eq(1)).as("relation_exists"))
				.from(LEX_RELATION)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId1)
								.and(LEX_RELATION.LEXEME2_ID.eq(lexemeId2))
								.and(LEX_RELATION.LEX_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordRelationExists(Long wordId1, Long wordId2, String relationType) {
	
		return create
				.select(field(DSL.count(WORD_RELATION.ID).eq(1)).as("relation_exists"))
				.from(WORD_RELATION)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId1)
								.and(WORD_RELATION.WORD2_ID.eq(wordId2))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordHasForms(Long wordId) {

		return create
				.select(field(DSL.count(FORM.ID).gt(0)).as("forms_exist"))
				.from(PARADIGM, FORM)
				.where(PARADIGM.WORD_ID.eq(wordId).and(FORM.PARADIGM_ID.eq(PARADIGM.ID)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordLexemeExists(Long wordId, String datasetCode) {
	
		return create
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

	public boolean isOnlyLexemeForWord(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = create
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
		int count = create
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
		int count = create
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

		boolean noOtherDatasetsExist = create
				.select(DSL.field(DSL.count(LEXEME.ID).eq(0)).as("no_other_datasets_exist"))
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId).and(LEXEME.DATASET_CODE.ne(datasetCode)))
				.fetchSingleInto(Boolean.class);

		return noOtherDatasetsExist;
	}

	public boolean isMemberOfWordRelationGroup(Long groupId, Long wordId) {
		Long id = create.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.ID.eq(groupId).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOneInto(Long.class);
		boolean exists = (id != null);
		return exists;
	}
}
