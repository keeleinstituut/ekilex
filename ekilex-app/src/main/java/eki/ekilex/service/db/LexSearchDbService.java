package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FORM_FREQUENCY;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREQUENCY;
import static eki.ekilex.data.db.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_POS_GROUP;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_REL_GROUP;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_RELATION;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.LayerName;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.Collocation;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.LexCollocPosGroup;
import eki.ekilex.data.db.tables.LexCollocRelGroup;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFrequency;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordEtymology;
import eki.ekilex.data.db.tables.WordEtymologyRelation;
import eki.ekilex.data.db.tables.WordEtymologySourceLink;
import eki.ekilex.data.db.tables.WordGroup;
import eki.ekilex.data.db.tables.WordGroupMember;
import eki.ekilex.data.db.tables.WordRelTypeLabel;
import eki.ekilex.data.db.tables.WordRelation;

@Component
public class LexSearchDbService extends AbstractSearchDbService {

	private DSLContext create;

	public LexSearchDbService(DSLContext context) {
		create = context;
	}

	public List<eki.ekilex.data.Word> getWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, boolean fetchAll, int offset)
			throws Exception {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();
		Word w1 = WORD.as("w1");
		Paradigm p = PARADIGM.as("p");
		Condition wordCondition = createSearchCondition(w1, searchCriteriaGroups, searchDatasetsRestriction);

		return execute(w1, p, wordCondition, LayerName.NONE, searchDatasetsRestriction, fetchAll, offset, create);
	}

	public int countWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Word w1 = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Condition wordCondition = createSearchCondition(w1, searchFilter.getCriteriaGroups(), searchDatasetsRestriction);

		return count(w1, p, wordCondition);
	}

	public List<eki.ekilex.data.Word> getWords(String wordWithMetaCharacters, SearchDatasetsRestriction searchDatasetsRestriction, boolean fetchAll, int offset) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Condition where = createSearchCondition(word, paradigm, wordWithMetaCharacters, searchDatasetsRestriction);

		return execute(word, paradigm, where, LayerName.NONE, searchDatasetsRestriction, fetchAll, offset, create);
	}

	public int countWords(String wordWithMetaCharacters, SearchDatasetsRestriction searchDatasetsRestriction) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Condition where = createSearchCondition(word, paradigm, wordWithMetaCharacters, searchDatasetsRestriction);

		return count(word, paradigm, where);
	}

	private int count(Word word, Paradigm paradigm, Condition where) {

		Form form = FORM.as("f");
		Table<Record> from = word.join(paradigm.join(form).on(form.PARADIGM_ID.eq(paradigm.ID).and(form.MODE.eq(FormMode.WORD.name())))).on(paradigm.WORD_ID.eq(word.ID));

		Table<Record1<Long>> w = create
				.select(word.ID.as("word_id"))
				.from(from)
				.where(where)
				.groupBy(word.ID)
				.asTable("w");

		Table<?> ww = create
				.select(w.field("word_id"))
				.from(w)
				.asTable("ww");

		return create.fetchCount(ww);
	}

	public List<ParadigmFormTuple> getParadigmFormTuples(Long wordId, String wordValue, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<String[]> ffreq = DSL
				.select(DSL.arrayAgg(DSL.concat(
						FORM_FREQUENCY.SOURCE_NAME, DSL.val(" - "),
						FORM_FREQUENCY.RANK, DSL.val(" - "),
						FORM_FREQUENCY.VALUE)))
				.from(FORM_FREQUENCY)
				.where(
						FORM_FREQUENCY.WORD_VALUE.eq(wordValue)
								.and(FORM_FREQUENCY.FORM_VALUE.eq(FORM.VALUE))
								.and(FORM_FREQUENCY.MORPH_CODE.eq(FORM.MORPH_CODE)))
				.asField();

		return create
				.select(
						PARADIGM.ID.as("paradigm_id"),
						PARADIGM.INFLECTION_TYPE_NR,
						FORM.ID.as("form_id"),
						FORM.VALUE.as("form"),
						FORM.MODE,
						FORM.COMPONENTS,
						FORM.DISPLAY_FORM,
						FORM.VOCAL_FORM,
						FORM.MORPH_CODE,
						MORPH_LABEL.VALUE.as("morph_value"),
						ffreq.as("form_frequencies"),
						FORM.ORDER_BY.as("form_order_by"))
				.from(PARADIGM, FORM, MORPH_LABEL)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(MORPH_LABEL.CODE.eq(FORM.MORPH_CODE))
								.and(MORPH_LABEL.LANG.eq(classifierLabelLang))
								.and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.orderBy(PARADIGM.ID, FORM.ORDER_BY)
				.fetchInto(ParadigmFormTuple.class);
	}

	public List<WordLexeme> getWordLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		LexemeFrequency lf = LEXEME_FREQUENCY.as("lf");

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w.ID);
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w.ID);

		Condition dsWhere = composeLexemeDatasetsCondition(l, searchDatasetsRestriction);

		Field<String[]> lff = DSL
				.select(DSL.arrayAgg(DSL.concat(
						lf.SOURCE_NAME, DSL.val(" - "),
						lf.RANK, DSL.val(" - "),
						lf.VALUE)))
				.from(lf)
				.where(lf.LEXEME_ID.eq(l.ID))
				.groupBy(lf.LEXEME_ID)
				.asField();

		return create.select(
				w.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')").as("word_value"),
				DSL.field("array_to_string(array_agg(distinct f.value_prese), ',', '*')").as("word_value_prese"),
				w.LANG.as("word_lang"),
				w.HOMONYM_NR.as("word_homonym_nr"),
				w.GENDER_CODE.as("word_gender_code"),
				w.ASPECT_CODE.as("word_aspect_code"),
				w.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
				wtf.as("word_type_codes"),
				wtpf.as("prefixoid"),
				wtsf.as("suffixoid"),
				wtz.as("foreign"),
				l.ID.as("lexeme_id"),
				l.MEANING_ID,
				ds.NAME.as("dataset_name"),
				l.DATASET_CODE,
				l.LEVEL1,
				l.LEVEL2,
				l.VALUE_STATE_CODE.as("lexeme_value_state_code"),
				l.PROCESS_STATE_CODE.as("lexeme_process_state_code"),
				l.FREQUENCY_GROUP_CODE.as("lexeme_frequency_group_code"),
				lff.as("lexeme_frequencies"),
				l.COMPLEXITY,
				l.WEIGHT)
				.from(f, p, w, l, m, ds)
				.where(
						w.ID.eq(wordId)
								.and(f.PARADIGM_ID.eq(p.ID))
								.and(f.MODE.eq(FormMode.WORD.name()))
								.and(p.WORD_ID.eq(w.ID))
								.and(l.WORD_ID.eq(w.ID))
								.and(l.MEANING_ID.eq(m.ID))
								.and(l.DATASET_CODE.eq(ds.CODE))
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(dsWhere))
				.groupBy(w.ID, l.ID, m.ID, ds.CODE)
				.orderBy(w.ID, ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public WordLexeme getLexeme(Long lexemeId) {

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		LexemeFrequency lf = LEXEME_FREQUENCY.as("lf");

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w.ID);
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w.ID);

		Field<String[]> lff = DSL
				.select(DSL.arrayAgg(DSL.concat(
						lf.SOURCE_NAME, DSL.val(" - "),
						lf.RANK, DSL.val(" - "),
						lf.VALUE)))
				.from(lf)
				.where(lf.LEXEME_ID.eq(l.ID))
				.groupBy(lf.LEXEME_ID)
				.asField();

		return create.select(
				w.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')").as("word_value"),
				DSL.field("array_to_string(array_agg(distinct f.value_prese), ',', '*')").as("word_value_prese"),
				w.LANG.as("word_lang"),
				w.HOMONYM_NR.as("word_homonym_nr"),
				w.GENDER_CODE.as("word_gender_code"),
				w.ASPECT_CODE.as("word_aspect_code"),
				w.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
				wtf.as("word_type_codes"),
				wtpf.as("prefixoid"),
				wtsf.as("suffixoid"),
				wtz.as("foreign"),
				l.ID.as("lexeme_id"),
				l.MEANING_ID,
				ds.NAME.as("dataset_name"),
				l.DATASET_CODE,
				l.LEVEL1,
				l.LEVEL2,
				l.VALUE_STATE_CODE.as("lexeme_value_state_code"),
				l.PROCESS_STATE_CODE.as("lexeme_process_state_code"),
				l.FREQUENCY_GROUP_CODE.as("lexeme_frequency_group_code"),
				lff.as("lexeme_frequencies"),
				l.COMPLEXITY,
				l.WEIGHT)
				.from(f, p, w, l, m, ds)
				.where(
						l.ID.eq(lexemeId)
								.and(l.WORD_ID.eq(w.ID))
								.and(p.WORD_ID.eq(w.ID))
								.and(f.PARADIGM_ID.eq(p.ID))
								.and(f.MODE.eq(FormMode.WORD.name()))
								.and(l.MEANING_ID.eq(m.ID))
								.and(l.DATASET_CODE.eq(ds.CODE)))
				.groupBy(w.ID, l.ID, m.ID, ds.CODE)
				.fetchSingleInto(WordLexeme.class);
	}

	public List<MeaningWord> getMeaningWords(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w2.ID);
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w2.ID);

		return create
				.select(
						w2.ID.as("word_id"),
						f2.VALUE.as("word_value"),
						f2.VALUE_PRESE.as("word_value_prese"),
						w2.HOMONYM_NR,
						w2.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						l2.ID.as("lexeme_id"),
						l2.WEIGHT.as("lexeme_weight"),
						l2.ORDER_BY)
				.from(l1, l2, w2, p2, f2)
				.where(
						l1.ID.eq(lexemeId)
						.and(l2.MEANING_ID.eq(l1.MEANING_ID))
						.and(l2.ID.ne(l1.ID))
						.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
						.and(l2.WORD_ID.eq(w2.ID))
						.and(p2.WORD_ID.eq(w2.ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.MODE.eq(FormMode.WORD.name()))
						)
				.groupBy(w2.ID, f2.VALUE, f2.VALUE_PRESE, l2.ID)
				.orderBy(w2.LANG, l2.ORDER_BY)
				.fetchInto(MeaningWord.class);
	}

	public eki.ekilex.data.Word getWord(Long wordId) {

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		Lexeme l = LEXEME.as("l");

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w.ID);
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w.ID);

		return create.select(
				w.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')").cast(String.class).as("word_value"),
				DSL.field("array_to_string(array_agg(distinct f.value_prese), ',', '*')").cast(String.class).as("word_value_prese"),
				DSL.field("array_to_string(array_agg(distinct f.vocal_form), ',')").cast(String.class).as("vocal_form"),
				w.HOMONYM_NR,
				w.LANG,
				w.WORD_CLASS,
				w.GENDER_CODE,
				w.ASPECT_CODE,
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
								.where(
										l.WORD_ID.eq(w.ID)
												.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY)))))
				.groupBy(w.ID)
				.fetchOptionalInto(eki.ekilex.data.Word.class)
				.orElse(null);
	}

	public List<Relation> getWordGroupMembers(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordGroupMember wgrm1 = WORD_GROUP_MEMBER.as("wgrm1");
		WordGroupMember wgrm2 = WORD_GROUP_MEMBER.as("wgrm2");
		WordGroup wgr = WORD_GROUP.as("wgr");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");
		WordRelTypeLabel wrtl = WORD_REL_TYPE_LABEL.as("wrtl");

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w2.ID);
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w2.ID);

		return create
				.selectDistinct(
						wgrm2.ID,
						wgr.ID.as("group_id"),
						w2.ID.as("word_id"),
						f2.VALUE.as("word_value"),
						f2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						wrtl.VALUE.as("rel_type_label"),
						wgrm2.ORDER_BY)
				.from(
						wgrm1
								.innerJoin(wgr).on(wgr.ID.eq(wgrm1.WORD_GROUP_ID))
								.innerJoin(wgrm2).on(wgrm2.WORD_GROUP_ID.eq(wgr.ID))
								.innerJoin(w2).on(w2.ID.eq(wgrm2.WORD_ID))
								.innerJoin(p2).on(p2.WORD_ID.eq(w2.ID))
								.innerJoin(f2).on(f2.PARADIGM_ID.eq(p2.ID).and(f2.MODE.eq(FormMode.WORD.name())))
								.leftOuterJoin(wrtl).on(
										wgr.WORD_REL_TYPE_CODE.eq(wrtl.CODE)
												.and(wrtl.LANG.eq(classifierLabelLang)
														.and(wrtl.TYPE.eq(classifierLabelTypeCode)))))
				.where(wgrm1.WORD_ID.eq(wordId))
				.orderBy(wgrm2.ORDER_BY)
				.fetchInto(Relation.class);
	}

	public List<Relation> getWordRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordRelation r = WORD_RELATION.as("r");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");
		WordRelTypeLabel rtl = WORD_REL_TYPE_LABEL.as("rtl");

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w2.ID);
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w2.ID);

		return create
				.selectDistinct(
						r.ID.as("id"),
						w2.ID.as("word_id"),
						f2.VALUE.as("word_value"),
						f2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						r.WORD_REL_TYPE_CODE.as("rel_type_code"),
						rtl.VALUE.as("rel_type_label"),
						r.RELATION_STATUS,
						r.ORDER_BY)
				.from(
						r
								.innerJoin(w2).on(w2.ID.eq(r.WORD2_ID).andExists(DSL.select(l2.ID).from(l2).where(l2.WORD_ID.eq(w2.ID))))
								.innerJoin(p2).on(p2.WORD_ID.eq(w2.ID))
								.innerJoin(f2).on(f2.PARADIGM_ID.eq(p2.ID).and(f2.MODE.eq(FormMode.WORD.name())))
								.leftOuterJoin(rtl).on(
										r.WORD_REL_TYPE_CODE.eq(rtl.CODE)
												.and(rtl.LANG.eq(classifierLabelLang)
														.and(rtl.TYPE.eq(classifierLabelTypeCode)))))
				.where(r.WORD1_ID.eq(wordId))
				.orderBy(r.ORDER_BY)
				.fetchInto(Relation.class);
	}

	public List<WordEtymTuple> getWordEtymology(Long wordId) {

		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologySourceLink wesl = WORD_ETYMOLOGY_SOURCE_LINK.as("wesl");
		WordEtymologyRelation wer = WORD_ETYMOLOGY_RELATION.as("wer");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						we.ID.as("word_etym_id"),
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.COMMENT_PRESE.as("word_etym_comment"),
						we.IS_QUESTIONABLE.as("word_etym_questionable"),
						wesl.ID.as("word_etym_source_link_id"),
						wesl.TYPE.as("word_etym_source_link_type"),
						wesl.VALUE.as("word_etym_source_link_value"),
						wer.ID.as("word_etym_rel_id"),
						wer.COMMENT_PRESE.as("word_etym_rel_comment"),
						wer.IS_QUESTIONABLE.as("word_etym_rel_questionable"),
						wer.IS_COMPOUND.as("word_etym_rel_compound"),
						w2.ID.as("related_word_id"),
						f2.VALUE.as("related_word"),
						w2.LANG.as("related_word_lang"))
				.from(we
						.leftOuterJoin(wesl).on(wesl.WORD_ETYM_ID.eq(we.ID))
						.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID))
						.leftOuterJoin(w2).on(w2.ID.eq(wer.RELATED_WORD_ID))
						.leftOuterJoin(p2).on(p2.WORD_ID.eq(w2.ID))
						.leftOuterJoin(f2).on(f2.PARADIGM_ID.eq(p2.ID).and(f2.MODE.eq(FormMode.WORD.name())))
						)
				.where(we.WORD_ID.eq(wordId))
				.orderBy(we.ORDER_BY, wesl.ORDER_BY, wer.ORDER_BY)
				.fetchInto(WordEtymTuple.class);
	}

	public List<CollocationTuple> getPrimaryCollocationTuples(Long lexemeId) {

		LexCollocPosGroup pgr1 = LEX_COLLOC_POS_GROUP.as("pgr1");
		LexCollocRelGroup rgr1 = LEX_COLLOC_REL_GROUP.as("rgr1");
		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		Collocation c = COLLOCATION.as("c");
		Lexeme l2 = LEXEME.as("l2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						pgr1.ID.as("pos_group_id"),
						pgr1.POS_GROUP_CODE.as("pos_group_code"),
						rgr1.ID.as("rel_group_id"),
						rgr1.NAME.as("rel_group_name"),
						rgr1.FREQUENCY.as("rel_group_frequency"),
						rgr1.SCORE.as("rel_group_score"),
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_member_word_id"),
						f2.VALUE.as("colloc_member_word"),
						f2.MODE.as("colloc_member_mode"),
						lc2.WEIGHT.as("colloc_member_weight"))
				.from(pgr1, rgr1, lc1, lc2, c, l2, p2, f2)
				.where(
						pgr1.LEXEME_ID.eq(lexemeId)
								.and(rgr1.POS_GROUP_ID.eq(pgr1.ID))
								.and(lc1.REL_GROUP_ID.eq(rgr1.ID))
								.and(lc1.COLLOCATION_ID.eq(c.ID))
								.and(lc2.COLLOCATION_ID.eq(c.ID))
								.and(lc2.LEXEME_ID.eq(l2.ID))
								.and(lc2.LEXEME_ID.ne(lc1.LEXEME_ID))
								.and(l2.WORD_ID.eq(p2.WORD_ID))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name())))
				.groupBy(c.ID, pgr1.ID, rgr1.ID, lc1.ID, lc2.ID, l2.ID, f2.VALUE, f2.MODE)
				.orderBy(pgr1.ORDER_BY, rgr1.ORDER_BY, lc1.GROUP_ORDER, c.ID, lc2.MEMBER_ORDER)
				.fetchInto(CollocationTuple.class);
	}

	public List<CollocationTuple> getSecondaryCollocationTuples(Long lexemeId) {

		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		Collocation c = COLLOCATION.as("c");
		Lexeme l2 = LEXEME.as("l2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_member_word_id"),
						f2.VALUE.as("colloc_member_word"),
						f2.MODE.as("colloc_member_mode"),
						lc2.WEIGHT.as("colloc_member_weight"))
				.from(lc1, lc2, c, l2, p2, f2)
				.where(
						lc1.LEXEME_ID.eq(lexemeId)
								.and(lc1.REL_GROUP_ID.isNull())
								.and(lc1.COLLOCATION_ID.eq(c.ID))
								.and(lc2.COLLOCATION_ID.eq(c.ID))
								.and(lc2.LEXEME_ID.eq(l2.ID))
								.and(lc2.LEXEME_ID.ne(lc1.LEXEME_ID))
								.and(l2.WORD_ID.eq(p2.WORD_ID))
								.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name())))
				.orderBy(c.ID, lc2.MEMBER_ORDER)
				.fetchInto(CollocationTuple.class);
	}

	public List<FreeForm> getOdWordRecommendations(Long wordId) {

		return create
				.select(
						FREEFORM.ID,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.ORDER_BY)
				.from(FREEFORM, WORD_FREEFORM)
				.where(
						WORD_FREEFORM.WORD_ID.eq(wordId)
								.and(FREEFORM.ID.eq(WORD_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.eq(FreeformType.OD_WORD_RECOMMENDATION.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}
}
