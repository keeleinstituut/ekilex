package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record18;
import org.jooq.Record5;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.Routines;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.LexemeRegister;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordRelTypeLabel;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordRelationParam;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;
import eki.ekilex.data.db.udt.records.TypeWordRelMeaningRecord;
import eki.ekilex.data.db.udt.records.TypeWordRelParamRecord;
import eki.ekilex.service.db.util.JooqBugCompensator;
import eki.ekilex.service.db.util.SearchFilterHelper;

@Component
public class SynSearchDbService extends AbstractDataDbService {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	@Autowired
	private JooqBugCompensator jooqBugCompensator;

	public List<SynRelation> getWordSynRelations(Long wordId, String relationType, String datasetCode, List<String> wordLangs, String classifierLabelLang, String classifierLabelTypeCode) {

		WordRelation r = WORD_RELATION.as("r");
		WordRelation oppr = WORD_RELATION.as("oppr");
		WordRelationParam rp = WORD_RELATION_PARAM.as("rp");
		WordRelTypeLabel rtl = WORD_REL_TYPE_LABEL.as("rtl");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		LexemePos lp = LEXEME_POS.as("lp");
		LexemeRegister lr = LEXEME_REGISTER.as("lr");
		Definition d = DEFINITION.as("d");
		Freeform u = FREEFORM.as("u");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");

		Field<TypeWordRelParamRecord[]> relp = DSL
				.select(DSL.field("array_agg(row(rp.name, rp.value)::type_word_rel_param)", TypeWordRelParamRecord[].class))
				.from(rp)
				.where(rp.WORD_RELATION_ID.eq(r.ID))
				.groupBy(rp.WORD_RELATION_ID)
				.asField();

		Field<String[]> usages = DSL
				.select(DSL.arrayAgg(u.VALUE_PRESE).orderBy(u.ORDER_BY))
				.from(u, lff)
				.where(
						lff.LEXEME_ID.eq(l2.ID)
								.and(lff.FREEFORM_ID.eq(u.ID))
								.and(u.TYPE.eq(FreeformType.USAGE.name()))
								.and(u.IS_PUBLIC.isTrue()))
				.groupBy(l2.ID)
				.asField("usages");

		Field<String[]> definitions = DSL
				.select(DSL.arrayAgg(Routines.encodeText(d.VALUE_PRESE)).orderBy(d.ORDER_BY))
				.from(d)
				.where(d.MEANING_ID.eq(l2.MEANING_ID).and(d.IS_PUBLIC.eq(PUBLICITY_PUBLIC)))
				.groupBy(l2.MEANING_ID)
				.asField("definitions");

		Field<String[]> lexRegisterCodes = DSL
				.select(DSL.arrayAgg(lr.REGISTER_CODE).orderBy(lr.ORDER_BY))
				.from(lr)
				.where(lr.LEXEME_ID.eq(l2.ID))
				.groupBy(l2.ID)
				.asField("lex_register_codes");

		Field<String[]> lexPosCodes = DSL
				.select(DSL.arrayAgg(lp.POS_CODE).orderBy(lp.ORDER_BY))
				.from(lp)
				.where(lp.LEXEME_ID.eq(l2.ID))
				.groupBy(l2.ID)
				.asField("lex_pos_codes");

		Table<Record5<Long, String[], String[], String[], String[]>> relmt = DSL
				.select(
						l2.MEANING_ID,
						definitions,
						usages,
						lexRegisterCodes,
						lexPosCodes)
				.from(l2)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.DATASET_CODE.eq(datasetCode)))
				.groupBy(l2.ID)
				.asTable("relmt");

		Field<TypeWordRelMeaningRecord[]> relm = DSL
				.select(DSL.field("array_agg(row(relmt.meaning_id, relmt.definitions, relmt.usages, relmt.lex_register_codes, relmt.lex_pos_codes)::type_word_rel_meaning)", TypeWordRelMeaningRecord[].class))
				.from(relmt)
				.asField("relm");

		Field<String[]> rwlp = DSL
				.select(DSL.arrayAggDistinct(lp.POS_CODE))
				.from(lp, l2)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.DATASET_CODE.eq(datasetCode))
								.and(lp.LEXEME_ID.eq(l2.ID)))
				.asField();

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		Table<Record18<Long, String, String, String, String, TypeWordRelParamRecord[], Long, Long, String, String, Integer, String, String[], Boolean, Boolean, Boolean, TypeWordRelMeaningRecord[], String[]>> rr = DSL
				.select(
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
						relm.as("word_meanings"),
						rwlp.as("word_lexemes_poses"))
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
								.and(w2.LANG.in(wordLangs))
								.andExists(DSL.select(l2.ID).from(l2).where(l2.WORD_ID.eq(w2.ID).and(l2.DATASET_CODE.eq(datasetCode)))))
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

		return create
				.select(
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
						rr.field("word_meanings"),
						rr.field("word_lexemes_poses"),
						rr.field("word_lexemes_max_frequency"))
				.from(rr)
				.orderBy(rr.field("order_by"))
				.fetch(record -> {
					SynRelation pojo = record.into(SynRelation.class);
					jooqBugCompensator.decodeWordMeaning(pojo.getWordMeanings());
					return pojo;
				});
	}

	public List<WordLexeme> getWordPrimarySynonymLexemes(
			Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, String classifierLabelLang, String classifierLabelTypeCode) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		Field<TypeClassifierRecord[]> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<TypeClassifierRecord[]> lregf = getLexemeRegistersField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		return create
				.select(
						l.MEANING_ID,
						l.WORD_ID,
						l.ID.as("lexeme_id"),
						l.DATASET_CODE,
						l.LEVEL1,
						l.LEVEL2,
						l.WEIGHT,
						lposf.as("pos"),
						lregf.as("registers"))
				.from(l.innerJoin(ds).on(ds.CODE.eq(l.DATASET_CODE)))
				.where(l.WORD_ID.eq(wordId).and(dsWhere))
				.orderBy(ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public void updateRelationStatus(Long id, String status) {
		create.update(WORD_RELATION)
				.set(WORD_RELATION.RELATION_STATUS, status)
				.where(WORD_RELATION.ID.eq(id))
				.execute();
	}

	public eki.ekilex.data.Word getWord(Long wordId) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);

		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
						w.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"))
				.from(w)
				.where(w.ID.eq(wordId)
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(l.WORD_ID.eq(w.ID))))
				.fetchOneInto(eki.ekilex.data.Word.class);
	}

	public List<eki.ekilex.data.WordRelation> getExistingFollowingRelationsForWord(Long relationId, String relTypeCode) {
		WordRelation wr2 = WORD_RELATION.as("wr2");

		return create.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION, wr2)
				.where(
						WORD_RELATION.WORD1_ID.eq(wr2.WORD1_ID)
								.and(wr2.ID.eq(relationId))
								.and(WORD_RELATION.ORDER_BY.ge(wr2.ORDER_BY))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode)))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	public List<TypeWordRelParam> getWordRelationParams(Long wordRelationId) {

		return create
				.select(WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId))
				.fetchInto(TypeWordRelParam.class);
	}
}
