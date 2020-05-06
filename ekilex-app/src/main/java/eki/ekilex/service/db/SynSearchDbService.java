package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LAYER_STATE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.common.constant.LayerName;
import eki.common.constant.LexemeType;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.LayerState;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordRelationParam;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;
import eki.ekilex.data.db.udt.records.TypeWordRelParamRecord;

@Component
public class SynSearchDbService extends AbstractSearchDbService {

	public List<eki.ekilex.data.Word> getWords(String wordWithMetaCharacters, SearchDatasetsRestriction searchDatasetsRestriction, LayerName layerName, boolean fetchAll, int offset) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Condition where = createSearchCondition(word, paradigm, wordWithMetaCharacters, searchDatasetsRestriction);

		return execute(word, paradigm, where, layerName, searchDatasetsRestriction, fetchAll, offset, create);
	}

	public List<eki.ekilex.data.Word> getWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, LayerName layerName, boolean fetchAll, int offset)
			throws Exception {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();
		Word w1 = WORD.as("w1");
		Paradigm p = PARADIGM.as("p");
		Condition wordCondition = createSearchCondition(w1, searchCriteriaGroups, searchDatasetsRestriction);

		return execute(w1, p, wordCondition, layerName, searchDatasetsRestriction, fetchAll, offset, create);
	}

	public List<SynRelation> getWordSynRelations(Long wordId, String relationType, String datasetCode, List<String> wordLangs) {

		WordRelation r = WORD_RELATION.as("r");
		WordRelation oppr = WORD_RELATION.as("oppr");
		WordRelationParam rp = WORD_RELATION_PARAM.as("rp");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Paradigm p2 = PARADIGM.as("p2");
		Paradigm ph = PARADIGM.as("ph");
		Form f2 = FORM.as("f2");
		Form fh = FORM.as("fh");
		Lexeme l2 = LEXEME.as("l");
		Lexeme lh = LEXEME.as("lh");
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

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		Table<Record15<Long, String, String, TypeWordRelParamRecord[], Long, Long, Object, Object, Integer, String, String[], Boolean, Boolean, Boolean, String[]>> rr = DSL.select(
				r.ID,
				r.RELATION_STATUS,
				oppr.RELATION_STATUS.as("opposite_relation_status"),
				relp.as("relation_params"),
				r.ORDER_BY,
				w2.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct f2.value), ',', '*')").as("word_value"),
				DSL.field("array_to_string(array_agg(distinct f2.value_prese), ',', '*')").as("word_value_prese"),
				w2.HOMONYM_NR.as("word_homonym_nr"),
				w2.LANG.as("word_lang"),
				wtf.as("word_type_codes"),
				wtpf.as("prefixoid"),
				wtsf.as("suffixoid"),
				wtz.as("foreign"),
				rwd.as("word_definitions"))
				.from(r
						.innerJoin(w2).on(r.WORD2_ID.eq(w2.ID).andExists(DSL.select(l2.ID).from(l2).where(l2.WORD_ID.eq(w2.ID))))
						.innerJoin(p2).on(p2.WORD_ID.eq(w2.ID))
						.innerJoin(f2).on(f2.PARADIGM_ID.eq(p2.ID).and(f2.MODE.eq(FormMode.WORD.name())))
						.leftOuterJoin(oppr).on(
								oppr.WORD1_ID.eq(r.WORD2_ID)
										.and(oppr.WORD2_ID.eq(r.WORD1_ID))
										.and(oppr.WORD_REL_TYPE_CODE.eq(r.WORD_REL_TYPE_CODE))))
				.where(
						r.WORD1_ID.eq(wordId)
								.and(r.WORD_REL_TYPE_CODE.eq(relationType))
								.and(w2.LANG.in(wordLangs)))
				.groupBy(r.ID, w2.ID, oppr.RELATION_STATUS)
				.asTable("r");

		Field<Boolean> rwhe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(fh, ph, wh)
				.where(
						fh.VALUE.eq(rr.field("word_value", String.class))
								.and(fh.MODE.eq(FormMode.WORD.name()))
								.and(fh.PARADIGM_ID.eq(ph.ID))
								.and(ph.WORD_ID.eq(wh.ID))
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(lh.WORD_ID.eq(wh.ID).and(lh.DATASET_CODE.eq(datasetCode)))))
				.groupBy(fh.VALUE)
				.asField();

		return create.select(
				rr.field("id"),
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
				rr.field("word_definitions"))
				.from(rr)
				.orderBy(rr.field("order_by"))
				.fetchInto(SynRelation.class);
	}

	public List<WordSynLexeme> getWordPrimarySynonymLexemes(
			Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, LayerName layerName, String classifierLabelLang, String classifierLabelTypeCode) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		LayerState lst = LAYER_STATE.as("lst");

		Condition dsWhere = applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		Field<TypeClassifierRecord[]> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		return create.select(
				l.MEANING_ID,
				l.WORD_ID,
				l.ID.as("lexeme_id"),
				l.TYPE,
				l.DATASET_CODE,
				l.LEVEL1,
				l.LEVEL2,
				l.WEIGHT,
				lposf.as("pos"),
				lst.PROCESS_STATE_CODE.as("layer_process_state_code"))
				.from(l
						.innerJoin(ds).on(ds.CODE.eq(l.DATASET_CODE))
						.leftOuterJoin(lst).on(lst.LEXEME_ID.eq(l.ID).and(lst.LAYER_NAME.eq(layerName.name()))))
				.where(
						l.WORD_ID.eq(wordId)
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(dsWhere))
				.orderBy(ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordSynLexeme.class);
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
				.insertInto(LEXEME, LEXEME.WORD_ID, LEXEME.MEANING_ID, LEXEME.DATASET_CODE, LEXEME.TYPE, LEXEME.WEIGHT, LEXEME.COMPLEXITY, LEXEME.PROCESS_STATE_CODE)
				.values(wordId, meaningId, datasetCode, lexemeType.name(), BigDecimal.valueOf(lexemeWeight), complexity.name(), PROCESS_STATE_PUBLIC)
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
	}

	public WordSynDetails getWordDetails(Long wordId) {

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		Lexeme l = LEXEME.as("l");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);

		return create.select(
				w.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')", String.class).as("wordValue"),
				DSL.field("array_to_string(array_agg(distinct f.value_prese), ',', '*')", String.class).as("wordValuePrese"),
				w.LANG,
				DSL.field("array_to_string(array_agg(distinct f.morph_code), ',', '*')", String.class).as("morphCode"),
				wtf.as("word_type_codes"),
				wtpf.as("prefixoid"),
				wtsf.as("suffixoid"),
				wtz.as("foreign"))
				.from(w, p, f)
				.where(w.ID.eq(wordId)
						.and(p.WORD_ID.eq(w.ID))
						.and(f.PARADIGM_ID.eq(p.ID))
						.and(f.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name()))
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(l.WORD_ID.eq(w.ID)
										//TODO what lexeme type?
				)))
				.groupBy(w.ID)
				.fetchOneInto(WordSynDetails.class);
	}

	public List<MeaningWord> getSynMeaningWords(Long lexemeId, List<String> meaningWordLangs, List<LexemeType> lexemeTypes) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Paradigm p2 = PARADIGM.as("p2");
		Paradigm ph = PARADIGM.as("ph");
		Form f2 = FORM.as("f2");
		Form fh = FORM.as("fh");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		Field<Boolean> whe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(fh, ph, wh)
				.where(
						fh.VALUE.eq(f2.VALUE)
								.and(fh.MODE.eq(FormMode.WORD.name()))
								.and(fh.PARADIGM_ID.eq(ph.ID))
								.and(ph.WORD_ID.eq(wh.ID))
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(
												lh.WORD_ID.eq(wh.ID)
														.and(lh.DATASET_CODE.eq(l2.DATASET_CODE)))))
				.groupBy(fh.VALUE)
				.asField();

		return create
				.select(
						w2.ID.as("word_id"),
						f2.VALUE.as("word_value"),
						f2.VALUE_PRESE.as("word_value_prese"),
						f2.MORPH_CODE,
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
				.from(l1, l2, w2, p2, f2)
				.where(
						l1.ID.eq(lexemeId)
								.and(l2.MEANING_ID.eq(l1.MEANING_ID))
								.and(l2.ID.ne(l1.ID))
								.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(l2.TYPE.in(lexemeTypes))
								.and(p2.WORD_ID.eq(w2.ID))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.eq(FormMode.WORD.name()))
								.and(w2.LANG.in(meaningWordLangs)))
				.groupBy(w2.ID, f2.VALUE, f2.VALUE_PRESE, f2.MORPH_CODE, l2.ID)
				.orderBy(w2.LANG, l2.ORDER_BY)
				.fetchInto(MeaningWord.class);
	}

	public List<SynRelation> getExistingFollowingRelationsForWord(Long relationId, String relTypeCode) {
		WordRelation wr2 = WORD_RELATION.as("wr2");

		return create.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION, wr2)
				.where(
						WORD_RELATION.WORD1_ID.eq(wr2.WORD1_ID)
								.and(wr2.ID.eq(relationId))
								.and(WORD_RELATION.ORDER_BY.ge(wr2.ORDER_BY))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode)))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(SynRelation.class);
	}

	public List<TypeWordRelParam> getWordRelationParams(Long wordRelationId) {

		return create
				.select(WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId))
				.fetchInto(TypeWordRelParam.class);
	}
}
