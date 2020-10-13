package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREQUENCY;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.LexemeType;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFrequency;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordRelTypeLabel;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordRelationParam;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;
import eki.ekilex.data.db.udt.records.TypeWordRelParamRecord;
import eki.ekilex.service.db.util.SearchFilterHelper;

@Component
public class SynSearchDbService extends AbstractDataDbService {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public List<Relation> getWordSynRelations(Long wordId, String relationType, String datasetCode, List<String> wordLangs, String classifierLabelLang, String classifierLabelTypeCode) {

		WordRelation r = WORD_RELATION.as("r");
		WordRelation oppr = WORD_RELATION.as("oppr");
		WordRelationParam rp = WORD_RELATION_PARAM.as("rp");
		WordRelTypeLabel rtl = WORD_REL_TYPE_LABEL.as("rtl");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Lexeme l2 = LEXEME.as("l");
		Lexeme lh = LEXEME.as("lh");
		LexemePos lp = LEXEME_POS.as("lp");
		LexemeFrequency lf = LEXEME_FREQUENCY.as("lf");
		Definition d = DEFINITION.as("d");

		Field<TypeWordRelParamRecord[]> relp = DSL
				.select(DSL.field("array_agg(row(rp.name, rp.value)::type_word_rel_param)", TypeWordRelParamRecord[].class))
				.from(rp)
				.where(rp.WORD_RELATION_ID.eq(r.ID))
				.groupBy(rp.WORD_RELATION_ID)
				.asField();

		Field<String[]> rwd = DSL
				.select(DSL.arrayAgg(d.VALUE).orderBy(l2.ORDER_BY, d.ORDER_BY))
				.from(l2, d)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.DATASET_CODE.eq(datasetCode))
								.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l2.MEANING_ID.eq(d.MEANING_ID))
								.and(DSL.or(d.COMPLEXITY.like(Complexity.DETAIL.name() + "%"), d.COMPLEXITY.like(Complexity.SIMPLE.name() + "%"))))
				.groupBy(l2.WORD_ID)
				.asField();

		Field<String[]> rwlp = DSL
				.select(DSL.arrayAggDistinct(lp.POS_CODE))
				.from(lp, l2)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l2.DATASET_CODE.eq(datasetCode))
								.and(lp.LEXEME_ID.eq(l2.ID)))
				.asField();

		Field<Float> rwlf = DSL
				.select(DSL.max(DSL.field("value")))
				.from(DSL
						.select(DSL.field("distinct on (lf.lexeme_id) lf.value", Float.class))
						.from(lf, l2)
						.where(
								l2.WORD_ID.eq(r.WORD2_ID)
										.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY))
										.and(l2.DATASET_CODE.eq(datasetCode))
										.and(lf.LEXEME_ID.eq(l2.ID)))
						.orderBy(lf.LEXEME_ID, lf.CREATED_ON.desc()))
				.asField();

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		Table<Record19<Long, String, String, String, String, TypeWordRelParamRecord[], Long, Long, String, String, Integer, String, String[], Boolean, Boolean, Boolean, String[], String[], Float>> rr = DSL.select(
				r.ID,
				r.WORD_REL_TYPE_CODE.as("rel_type_code"),
				rtl.VALUE.as("rel_type_label"),
				r.RELATION_STATUS,
				oppr.RELATION_STATUS.as("opposite_relation_status"),
				relp.as("relation_params"),
				r.ORDER_BY,
				w2.ID.as("word_id"),
				w2.VALUE.as("word_value"),
				w2.VALUE_PRESE.as("word_value_prese"),
				w2.HOMONYM_NR.as("word_homonym_nr"),
				w2.LANG.as("word_lang"),
				wtf.as("word_type_codes"),
				wtpf.as("prefixoid"),
				wtsf.as("suffixoid"),
				wtz.as("foreign"),
				rwd.as("word_definitions"),
				rwlp.as("word_lexemes_poses"),
				rwlf.as("word_lexemes_max_frequency"))
				.from(r
						.innerJoin(w2).on(r.WORD2_ID.eq(w2.ID).andExists(DSL.select(l2.ID).from(l2).where(l2.WORD_ID.eq(w2.ID))))
						.leftOuterJoin(oppr).on(
								oppr.WORD1_ID.eq(r.WORD2_ID)
										.and(oppr.WORD2_ID.eq(r.WORD1_ID))
										.and(oppr.WORD_REL_TYPE_CODE.eq(r.WORD_REL_TYPE_CODE)))
						.leftOuterJoin(rtl).on(
								r.WORD_REL_TYPE_CODE.eq(rtl.CODE)
										.and(rtl.LANG.eq(classifierLabelLang)
												.and(rtl.TYPE.eq(classifierLabelTypeCode)))))
				.where(
						r.WORD1_ID.eq(wordId)
								.and(r.WORD_REL_TYPE_CODE.eq(relationType))
								.and(w2.LANG.in(wordLangs)))
				.groupBy(r.ID, rtl.VALUE, w2.ID, oppr.RELATION_STATUS)
				.asTable("r");

		Field<Boolean> rwhe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(wh)
				.where(
						wh.VALUE.eq(rr.field("word_value", String.class))
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(lh.WORD_ID.eq(wh.ID).and(lh.DATASET_CODE.eq(datasetCode)))))
				.groupBy(wh.VALUE)
				.asField();

		return create.select(
				rr.field("id"),
				rr.field("rel_type_code"),
				rr.field("rel_type_label"),
				rr.field("relation_status"),
				rr.field("opposite_relation_status"),
				rr.field("relation_params"),
				rr.field("order_by"),
				rr.field("word_id"),
				rr.field("word_value"),
				rr.field("word_value_prese"),
				rr.field("word_homonym_nr"),
				rwhe.as("homonyms_exist"),
				rr.field("word_lang"),
				rr.field("word_type_codes"),
				rr.field("prefixoid"),
				rr.field("suffixoid"),
				rr.field("foreign"),
				rr.field("word_definitions"),
				rr.field("word_lexemes_poses"),
				rr.field("word_lexemes_max_frequency"))
				.from(rr)
				.orderBy(rr.field("order_by"))
				.fetchInto(Relation.class);
	}

	public List<WordLexeme> getWordPrimarySynonymLexemes(
			Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, String classifierLabelLang, String classifierLabelTypeCode) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		Field<TypeClassifierRecord[]> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		return create.select(
				l.MEANING_ID,
				l.WORD_ID,
				l.ID.as("lexeme_id"),
				l.DATASET_CODE,
				l.LEVEL1,
				l.LEVEL2,
				l.WEIGHT,
				lposf.as("pos"))
				.from(l.innerJoin(ds).on(ds.CODE.eq(l.DATASET_CODE)))
				.where(
						l.WORD_ID.eq(wordId)
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(dsWhere))
				.orderBy(ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public void changeRelationStatus(Long id, String status) {
		create.update(WORD_RELATION)
				.set(WORD_RELATION.RELATION_STATUS, status)
				.where(WORD_RELATION.ID.eq(id))
				.execute();
	}

	public Long getRelationId(Long word1Id, Long word2Id, String relationType) {
		Record1<Long> relationRecord = create.select(WORD_RELATION.ID)
				.from(WORD_RELATION)
				.where(WORD_RELATION.WORD1_ID.eq(word1Id)
						.and(WORD_RELATION.WORD2_ID.eq(word2Id))
						.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType)))
				.fetchOne();

		return relationRecord != null ? relationRecord.get(WORD_RELATION.ID) : null;

	}

	public Long createLexeme(Long wordId, Long meaningId, String datasetCode, LexemeType lexemeType, Float lexemeWeight, Complexity complexity) {
		return create
				.insertInto(LEXEME, LEXEME.WORD_ID, LEXEME.MEANING_ID, LEXEME.DATASET_CODE, LEXEME.TYPE, LEXEME.WEIGHT, LEXEME.COMPLEXITY, LEXEME.IS_PUBLIC)
				.values(wordId, meaningId, datasetCode, lexemeType.name(), BigDecimal.valueOf(lexemeWeight), complexity.name(), PUBLICITY_PUBLIC)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
	}

	public eki.ekilex.data.Word getWord(Long wordId) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);
		Field<String> fmcf = getFormMorphCodeField(w.ID);

		return create.select(
				w.ID.as("word_id"),
				w.VALUE.as("word_value"),
				w.VALUE_PRESE.as("word_value_prese"),
				w.LANG,
				fmcf.as("morph_code"),
				wtf.as("word_type_codes"),
				wtpf.as("prefixoid"),
				wtsf.as("suffixoid"),
				wtz.as("foreign"))
				.from(w)
				.where(w.ID.eq(wordId)
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(l.WORD_ID.eq(w.ID)
				)))
				.fetchOneInto(eki.ekilex.data.Word.class);
	}

	public List<MeaningWord> getSynMeaningWords(Long lexemeId, List<String> meaningWordLangs, List<LexemeType> lexemeTypes) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);
		Field<String> fmcf = getFormMorphCodeField(w2.ID);

		Field<Boolean> whe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(wh)
				.where(
						wh.VALUE.eq(w2.VALUE)
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(
												lh.WORD_ID.eq(wh.ID)
														.and(lh.DATASET_CODE.eq(l2.DATASET_CODE)))))
				.groupBy(wh.VALUE)
				.asField();

		return create
				.select(
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						fmcf.as("morph_code"),
						w2.HOMONYM_NR,
						whe.as("homonyms_exist"),
						w2.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						l2.ID.as("lexeme_id"),
						l2.TYPE.as("lexeme_type"),
						l2.WEIGHT.as("lexeme_weight"),
						l2.ORDER_BY)
				.from(l1, l2, w2)
				.where(
						l1.ID.eq(lexemeId)
								.and(l2.MEANING_ID.eq(l1.MEANING_ID))
								.and(l2.ID.ne(l1.ID))
								.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(l2.TYPE.in(lexemeTypes))
								.and(w2.LANG.in(meaningWordLangs)))
				.groupBy(w2.ID, l2.ID)
				.orderBy(w2.LANG, l2.ORDER_BY)
				.fetchInto(MeaningWord.class);
	}

	public List<Relation> getExistingFollowingRelationsForWord(Long relationId, String relTypeCode) {
		WordRelation wr2 = WORD_RELATION.as("wr2");

		return create.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION, wr2)
				.where(
						WORD_RELATION.WORD1_ID.eq(wr2.WORD1_ID)
								.and(wr2.ID.eq(relationId))
								.and(WORD_RELATION.ORDER_BY.ge(wr2.ORDER_BY))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode)))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(Relation.class);
	}

	public List<TypeWordRelParam> getWordRelationParams(Long wordRelationId) {

		return create
				.select(WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId))
				.fetchInto(TypeWordRelParam.class);
	}
}
