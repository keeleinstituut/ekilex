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

import java.util.Collections;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
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
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.LexCollocPosGroup;
import eki.ekilex.data.db.tables.LexCollocRelGroup;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordEtymology;
import eki.ekilex.data.db.tables.WordEtymologyRelation;
import eki.ekilex.data.db.tables.WordEtymologySourceLink;
import eki.ekilex.data.db.tables.WordGroup;
import eki.ekilex.data.db.tables.WordGroupMember;
import eki.ekilex.data.db.tables.WordRelTypeLabel;

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

		return execute(w1, p, wordCondition, null, Collections.emptyList(), fetchAll, offset, create);
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

		return execute(word, paradigm, where, null, Collections.emptyList(), fetchAll, offset, create);
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

	private SelectField<?>[] getWordLexemeSelectFields() {

		Field<String[]> lfreq = DSL
				.select(DSL.arrayAgg(DSL.concat(
						LEXEME_FREQUENCY.SOURCE_NAME, DSL.val(" - "),
						LEXEME_FREQUENCY.RANK, DSL.val(" - "),
						LEXEME_FREQUENCY.VALUE)))
				.from(LEXEME_FREQUENCY)
				.where(LEXEME_FREQUENCY.LEXEME_ID.eq(LEXEME.ID))
				.groupBy(LEXEME_FREQUENCY.LEXEME_ID)
				.asField();

		return new Field<?>[] {
				DSL.arrayAggDistinct(FORM.VALUE).as("words"),
				DSL.arrayAggDistinct(FORM.VOCAL_FORM).as("vocal_forms"),
				WORD.LANG.as("word_lang"),
				WORD.ID.as("word_id"),
				WORD.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
				WORD.GENDER_CODE,
				WORD.ASPECT_CODE.as("word_aspect_code"),
				WORD.HOMONYM_NR.as("word_homonym_number"),
				LEXEME.ID.as("lexeme_id"),
				LEXEME.MEANING_ID,
				LEXEME.DATASET_CODE.as("dataset"),
				LEXEME.LEVEL1,
				LEXEME.LEVEL2,
				LEXEME.VALUE_STATE_CODE.as("lexeme_value_state_code"),
				LEXEME.FREQUENCY_GROUP_CODE.as("lexeme_frequency_group_code"),
				lfreq.as("lexeme_frequencies"),
				LEXEME.PROCESS_STATE_CODE.as("lexeme_process_state_code"),
				LEXEME.WEIGHT.as("lexeme_weight"),
				LEXEME.COMPLEXITY.as("lexeme_complexity")
		};
	}

	public List<WordLexeme> getWordLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = composeLexemeDatasetsCondition(LEXEME, searchDatasetsRestriction);

		return create.select(getWordLexemeSelectFields())
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING, DATASET)
				.where(
						WORD.ID.eq(wordId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.DATASET_CODE.eq(DATASET.CODE))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(dsWhere)
				)
				.groupBy(WORD.ID, LEXEME.ID, MEANING.ID, DATASET.CODE)
				.orderBy(WORD.ID, DATASET.ORDER_BY, LEXEME.LEVEL1, LEXEME.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public WordLexeme getLexeme(Long lexemeId) {

		return create.select(getWordLexemeSelectFields())
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING)
				.where(
						LEXEME.ID.eq(lexemeId)
								.and(WORD.ID.eq(LEXEME.WORD_ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID)))
				.groupBy(WORD.ID, LEXEME.ID, MEANING.ID)
				.orderBy(WORD.ID)
				.fetchSingleInto(WordLexeme.class);
	}

	public List<MeaningWord> getMeaningWords(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						w2.ID.as("word_id"),
						f2.VALUE,
						w2.HOMONYM_NR.as("homonym_number"),
						w2.LANG.as("language"),
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
				.groupBy(w2.ID, f2.VALUE, l2.ID)
				.orderBy(w2.LANG, l2.ORDER_BY)
				.fetchInto(MeaningWord.class);
	}

	public eki.ekilex.data.Word getWord(Long wordId) {
		return create.select(
				WORD.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct form.value_prese), ',', '*')").cast(String.class).as("word"),
				DSL.field("array_to_string(array_agg(distinct form.vocal_form), ',')").cast(String.class).as("vocal_form"),
				WORD.HOMONYM_NR,
				WORD.LANG,
				WORD.WORD_CLASS,
				WORD.GENDER_CODE,
				WORD.ASPECT_CODE)
				.from(WORD, PARADIGM, FORM)
				.where(WORD.ID.eq(wordId)
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name()))
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME)
								.where(
										LEXEME.WORD_ID.eq(WORD.ID)
												.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))))
				.groupBy(WORD.ID)
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

		return create
				.selectDistinct(
						wgrm2.ID.as("id"),
						wgr.ID.as("group_id"),
						w2.ID.as("word_id"),
						f2.ID.as("form_id"),
						f2.VALUE.as("word"),
						w2.LANG.as("word_lang"),
						wrtl.VALUE.as("rel_type_label"),
						wgrm2.ORDER_BY.as("order_by"))
				.from(
						wgr.leftOuterJoin(wrtl).on(
								wgr.WORD_REL_TYPE_CODE.eq(wrtl.CODE)
										.and(wrtl.LANG.eq(classifierLabelLang)
												.and(wrtl.TYPE.eq(classifierLabelTypeCode)))),
						wgrm1,
						wgrm2,
						w2,
						p2,
						f2)
				.where(
						wgrm1.WORD_ID.eq(wordId)
								.and(wgrm1.WORD_GROUP_ID.eq(wgr.ID))
								.and(wgrm2.WORD_GROUP_ID.eq(wgr.ID))
								.and(w2.ID.eq(wgrm2.WORD_ID))
								.and(p2.WORD_ID.eq(w2.ID))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.eq(FormMode.WORD.name())))
				.orderBy(wgrm2.ORDER_BY)
				.fetchInto(Relation.class);
	}

	public List<Relation> getWordRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.selectDistinct(
						WORD_RELATION.ID.as("id"),
						FORM.VALUE.as("word"),
						WORD.ID.as("word_id"),
						WORD.LANG.as("word_lang"),
						WORD_RELATION.WORD_REL_TYPE_CODE.as("rel_type_code"),
						WORD_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						WORD_RELATION.RELATION_STATUS.as("relation_status"),
						WORD_RELATION.ORDER_BY.as("order_by"))
				.from(
						WORD_RELATION.leftOuterJoin(WORD_REL_TYPE_LABEL).on(
								WORD_RELATION.WORD_REL_TYPE_CODE.eq(WORD_REL_TYPE_LABEL.CODE)
										.and(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						WORD,
						PARADIGM,
						FORM)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
								.and(WORD_RELATION.WORD2_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.orderBy(WORD_RELATION.ORDER_BY)
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
