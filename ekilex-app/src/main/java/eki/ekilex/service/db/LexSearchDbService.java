package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FORM_FREQ;
import static eki.ekilex.data.db.Tables.FREQ_CORP;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_POS_GROUP;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_REL_GROUP;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MORPH_FREQ;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_RELATION;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.WORD_FREQ;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Param;
import org.jooq.Record18;
import org.jooq.Record9;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.Collocation;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.FormFreq;
import eki.ekilex.data.db.tables.FreqCorp;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.LexCollocPosGroup;
import eki.ekilex.data.db.tables.LexCollocRelGroup;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MorphFreq;
import eki.ekilex.data.db.tables.MorphLabel;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.ParadigmForm;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordEtymology;
import eki.ekilex.data.db.tables.WordEtymologyRelation;
import eki.ekilex.data.db.tables.WordEtymologySourceLink;
import eki.ekilex.data.db.tables.WordFreq;
import eki.ekilex.data.db.tables.WordGroup;
import eki.ekilex.data.db.tables.WordGroupMember;
import eki.ekilex.data.db.tables.WordRelTypeLabel;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.service.db.util.LexSearchConditionComposer;
import eki.ekilex.service.db.util.SearchFilterHelper;

@Component
public class LexSearchDbService extends AbstractDataDbService {

	@Autowired
	private LexSearchConditionComposer lexSearchConditionComposer;

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public List<eki.ekilex.data.Word> getWords(
			SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String userRoleDatasetCode,
			List<String> tagNames, int offset, int maxResultsLimit, boolean noLimit) throws Exception {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();
		Word w1 = WORD.as("w1");
		Condition wordCondition = lexSearchConditionComposer.createSearchCondition(w1, searchCriteriaGroups, searchDatasetsRestriction);

		return execute(w1, wordCondition, searchDatasetsRestriction, userRoleDatasetCode, tagNames, offset, maxResultsLimit, noLimit);
	}

	public int countWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Word w1 = WORD.as("w");
		Condition wordCondition = lexSearchConditionComposer.createSearchCondition(w1, searchFilter.getCriteriaGroups(), searchDatasetsRestriction);

		return count(w1, wordCondition);
	}

	public List<eki.ekilex.data.Word> getWords(
			String searchWordCrit, SearchDatasetsRestriction searchDatasetsRestriction, String userRoleDatasetCode,
			List<String> tagNames, int offset, int maxResultsLimit, boolean noLimit) {

		Word word = WORD.as("w");
		Condition where = lexSearchConditionComposer.createSearchCondition(word, searchWordCrit, searchDatasetsRestriction);

		return execute(word, where, searchDatasetsRestriction, userRoleDatasetCode, tagNames, offset, maxResultsLimit, noLimit);
	}

	public int countWords(String wordWithMetaCharacters, SearchDatasetsRestriction searchDatasetsRestriction) {

		Word word = WORD.as("w");
		Condition where = lexSearchConditionComposer.createSearchCondition(word, wordWithMetaCharacters, searchDatasetsRestriction);

		return count(word, where);
	}

	private int count(Word word, Condition where) {

		Table<?> w = create
				.select(word.ID.as("word_id"))
				.from(word)
				.where(where)
				//.groupBy(word.ID)
				.asTable("w");

		return create.fetchCount(w);
	}

	public List<ParadigmFormTuple> getParadigmFormTuples(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		Paradigm p = PARADIGM.as("p");
		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");
		MorphLabel ml = MORPH_LABEL.as("ml");
		FreqCorp fc = FREQ_CORP.as("fc");
		FreqCorp fca = FREQ_CORP.as("fca");
		MorphFreq mf = MORPH_FREQ.as("mf");
		MorphFreq mfa = MORPH_FREQ.as("mfa");
		FormFreq ff = FORM_FREQ.as("ff");
		FormFreq ffa = FORM_FREQ.as("ffa");

		final Param<String> freqFieldSep = DSL.val(" - ");

		Field<String> mff = DSL
				.select(DSL.concat(fc.NAME, freqFieldSep, mf.RANK, freqFieldSep, mf.VALUE))
				.from(fc, mf)
				.where(
						mf.MORPH_CODE.eq(f.MORPH_CODE)
								.and(mf.FREQ_CORP_ID.eq(fc.ID))
								.and(fc.ID.eq(DSL
										.select(fca.ID)
										.from(fca, mfa)
										.where(
												mfa.MORPH_CODE.eq(mf.MORPH_CODE)
														.and(mfa.FREQ_CORP_ID.eq(fca.ID)))
										.orderBy(fca.CORP_DATE.desc())
										.limit(1))))
				.asField();

		Field<String> fff = DSL
				.select(DSL.concat(fc.NAME, freqFieldSep, ff.RANK, freqFieldSep, ff.VALUE))
				.from(fc, ff)
				.where(
						ff.FORM_ID.eq(f.ID)
								.and(ff.FREQ_CORP_ID.eq(fc.ID))
								.and(fc.ID.eq(DSL
										.select(fca.ID)
										.from(fca, ffa)
										.where(
												ffa.FORM_ID.eq(ff.FORM_ID)
														.and(ffa.FREQ_CORP_ID.eq(fca.ID)))
										.orderBy(fca.CORP_DATE.desc())
										.limit(1))))
				.asField();

		return create
				.select(
						p.ID.as("paradigm_id"),
						p.COMMENT.as("paradigm_comment"),
						p.INFLECTION_TYPE,
						p.INFLECTION_TYPE_NR,
						p.WORD_CLASS,
						f.ID.as("form_id"),
						f.VALUE.as("form_value"),
						f.VALUE_PRESE.as("form_value_prese"),
						f.MORPH_CODE,
						pf.DISPLAY_FORM,
						ml.VALUE.as("morph_value"),
						mff.as("morph_frequency"),
						fff.as("form_frequency"),
						pf.ORDER_BY.as("form_order_by"))
				.from(p, pf, f, ml)
				.where(
						p.WORD_ID.eq(wordId)
								.and(pf.PARADIGM_ID.eq(p.ID))
								.and(pf.FORM_ID.eq(f.ID))
								.and(ml.CODE.eq(f.MORPH_CODE))
								.and(ml.LANG.eq(classifierLabelLang))
								.and(ml.TYPE.eq(classifierLabelTypeCode)))
				.orderBy(p.ID, pf.ORDER_BY)
				.fetchInto(ParadigmFormTuple.class);
	}

	public List<WordLexeme> getWordLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, String classifierLabelLang, String classifierLabelTypeCode) {

		Word w = WORD.as("w");
		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);

		Field<JSON> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lderf = getLexemeDerivsField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lregf = getLexemeRegistersField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lvalstf = getLexemeValueStateField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lproflf = getLexemeProficiencyLevelField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
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
						lvalstf.as("lexeme_value_state"),
						l.PROFICIENCY_LEVEL_CODE.as("lexeme_proficiency_level_code"),
						lproflf.as("lexeme_proficiency_level"),
						l.IS_PUBLIC,
						l.COMPLEXITY,
						l.WEIGHT,
						lposf.as("pos"),
						lderf.as("derivs"),
						lregf.as("registers"))
				.from(w, l, m, ds)
				.where(
						w.ID.eq(wordId)
								.and(l.WORD_ID.eq(w.ID))
								.and(l.MEANING_ID.eq(m.ID))
								.and(l.DATASET_CODE.eq(ds.CODE))
								.and(dsWhere))
				.groupBy(w.ID, l.ID, m.ID, ds.CODE)
				.orderBy(w.ID, ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public WordLexeme getLexeme(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		Word w = WORD.as("w");
		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);

		Field<JSON> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lderf = getLexemeDerivsField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lregf = getLexemeRegistersField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lvalstf = getLexemeValueStateField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lproflf = getLexemeProficiencyLevelField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
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
						lvalstf.as("lexeme_value_state"),
						l.PROFICIENCY_LEVEL_CODE.as("lexeme_proficiency_level_code"),
						lproflf.as("lexeme_proficiency_level"),
						l.RELIABILITY,
						l.IS_PUBLIC,
						l.COMPLEXITY,
						l.WEIGHT,
						lposf.as("pos"),
						lderf.as("derivs"),
						lregf.as("registers"))
				.from(w, l, m, ds)
				.where(
						l.ID.eq(lexemeId)
								.and(l.WORD_ID.eq(w.ID))
								.and(l.MEANING_ID.eq(m.ID))
								.and(l.DATASET_CODE.eq(ds.CODE)))
				.groupBy(w.ID, l.ID, m.ID, ds.CODE)
				.fetchOptionalInto(WordLexeme.class)
				.orElse(null);
	}

	public eki.ekilex.data.Word getWord(Long wordId) {

		Word w = WORD.as("w");
		FreqCorp fc = FREQ_CORP.as("fc");
		FreqCorp fca = FREQ_CORP.as("fca");
		WordFreq wf = WORD_FREQ.as("wf");
		WordFreq wfa = WORD_FREQ.as("wfa");
		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");

		final Param<String> freqFieldSep = DSL.val(" - ");
		Field<String> wff = DSL
				.select(DSL.concat(fc.NAME, freqFieldSep, wf.RANK, freqFieldSep, wf.VALUE))
				.from(fc, wf)
				.where(
						wf.WORD_ID.eq(w.ID)
								.and(wf.FREQ_CORP_ID.eq(fc.ID))
								.and(fc.ID.eq(DSL
										.select(fca.ID)
										.from(fca, wfa)
										.where(
												wfa.WORD_ID.eq(wf.WORD_ID)
														.and(wfa.FREQ_CORP_ID.eq(fca.ID)))
										.orderBy(fca.CORP_DATE.desc())
										.limit(1))))
				.asField();

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);
		Field<String[]> lxtnf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(DSL.coalesce(lt.TAG_NAME, "!")))
				.from(l.leftOuterJoin(lt).on(lt.LEXEME_ID.eq(l.ID)))
				.where(l.WORD_ID.eq(w.ID))
				.groupBy(w.ID));
		Field<Timestamp> wlaeof = getWordLastActivityEventOnField(w.ID);

		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
						w.VOCAL_FORM,
						w.HOMONYM_NR,
						w.LANG,
						w.GENDER_CODE,
						w.ASPECT_CODE,
						w.DISPLAY_MORPH_CODE,
						w.MORPHOPHONO_FORM,
						w.MANUAL_EVENT_ON,
						w.IS_PUBLIC.as("is_word_public"),
						wff.as("word_frequency"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						lxtnf.as("lexemes_tag_names"),
						wlaeof.as("last_activity_event_on"))
				.from(w)
				.where(w.ID.eq(wordId)
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(l.WORD_ID.eq(w.ID))))
				.fetchOptionalInto(eki.ekilex.data.Word.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.WordRelation> getWordGroupMembers(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordGroupMember wgrm1 = WORD_GROUP_MEMBER.as("wgrm1");
		WordGroupMember wgrm2 = WORD_GROUP_MEMBER.as("wgrm2");
		WordGroup wgr = WORD_GROUP.as("wgr");
		Word w2 = WORD.as("w2");
		WordRelTypeLabel wrtl = WORD_REL_TYPE_LABEL.as("wrtl");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		return create
				.selectDistinct(
						wgrm2.ID,
						wgr.ID.as("group_id"),
						wgr.WORD_REL_TYPE_CODE.as("group_word_rel_type_code"),
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						w2.ASPECT_CODE.as("word_aspect_code"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						wrtl.VALUE.as("rel_type_label"),
						wgrm2.ORDER_BY)
				.from(
						wgr
								.innerJoin(wgrm1).on(wgrm1.WORD_GROUP_ID.eq(wgr.ID))
								.innerJoin(wgrm2).on(wgrm2.WORD_GROUP_ID.eq(wgr.ID))
								.innerJoin(w2).on(w2.ID.eq(wgrm2.WORD_ID))
								.leftOuterJoin(wrtl).on(
										wgr.WORD_REL_TYPE_CODE.eq(wrtl.CODE)
												.and(wrtl.LANG.eq(classifierLabelLang))
												.and(wrtl.TYPE.eq(classifierLabelTypeCode))))
				.where(wgrm1.WORD_ID.eq(wordId))
				.orderBy(wgrm2.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	public List<eki.ekilex.data.WordRelation> getWordRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordRelation r = WORD_RELATION.as("r");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		WordRelTypeLabel rtl = WORD_REL_TYPE_LABEL.as("rtl");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		return create
				.selectDistinct(
						r.ID.as("id"),
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
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
								.leftOuterJoin(rtl).on(
										r.WORD_REL_TYPE_CODE.eq(rtl.CODE)
												.and(rtl.LANG.eq(classifierLabelLang))
												.and(rtl.TYPE.eq(classifierLabelTypeCode))))
				.where(r.WORD1_ID.eq(wordId))
				.orderBy(r.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	@Deprecated
	public List<WordEtymTuple> getWordEtymology(Long wordId) {

		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologySourceLink wesl = WORD_ETYMOLOGY_SOURCE_LINK.as("wesl");
		WordEtymologyRelation wer = WORD_ETYMOLOGY_RELATION.as("wer");
		Word w2 = WORD.as("w2");
		Source s = SOURCE.as("s");

		return create
				.select(
						we.ID.as("word_etym_id"),
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.COMMENT_PRESE.as("word_etym_comment"),
						we.IS_QUESTIONABLE.as("word_etym_questionable"),
						wesl.ID.as("word_etym_source_link_id"),
						wesl.TYPE.as("word_etym_source_link_type"),
						s.ID.as("word_etym_source_id"),
						s.NAME.as("word_etym_source_name"),
						wer.ID.as("word_etym_rel_id"),
						wer.COMMENT_PRESE.as("word_etym_rel_comment"),
						wer.IS_QUESTIONABLE.as("word_etym_rel_questionable"),
						wer.IS_COMPOUND.as("word_etym_rel_compound"),
						w2.ID.as("related_word_id"),
						w2.VALUE.as("related_word"),
						w2.LANG.as("related_word_lang"))
				.from(we
						.leftOuterJoin(wesl).on(wesl.WORD_ETYM_ID.eq(we.ID))
						.leftOuterJoin(s).on(wesl.SOURCE_ID.eq(s.ID))
						.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID))
						.leftOuterJoin(w2).on(w2.ID.eq(wer.RELATED_WORD_ID)))
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
		Word w2 = WORD.as("w2");

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
						w2.VALUE.as("colloc_member_word_value"),
						lc2.WEIGHT.as("colloc_member_weight"))
				.from(pgr1, rgr1, lc1, lc2, c, l2, w2)
				.where(
						pgr1.LEXEME_ID.eq(lexemeId)
								.and(rgr1.POS_GROUP_ID.eq(pgr1.ID))
								.and(lc1.REL_GROUP_ID.eq(rgr1.ID))
								.and(lc1.COLLOCATION_ID.eq(c.ID))
								.and(lc2.COLLOCATION_ID.eq(c.ID))
								.and(lc2.LEXEME_ID.eq(l2.ID))
								.and(lc2.LEXEME_ID.ne(lc1.LEXEME_ID))
								.and(l2.WORD_ID.eq(w2.ID)))
				.groupBy(c.ID, pgr1.ID, rgr1.ID, lc1.ID, lc2.ID, l2.ID, w2.ID)
				.orderBy(pgr1.ORDER_BY, rgr1.ORDER_BY, lc1.GROUP_ORDER, c.ID, lc2.MEMBER_ORDER)
				.fetchInto(CollocationTuple.class);
	}

	public List<CollocationTuple> getSecondaryCollocationTuples(Long lexemeId) {

		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		Collocation c = COLLOCATION.as("c");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");

		return create
				.select(
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_member_word_id"),
						w2.VALUE.as("colloc_member_word_value"),
						lc2.WEIGHT.as("colloc_member_weight"))
				.from(lc1, lc2, c, l2, w2)
				.where(
						lc1.LEXEME_ID.eq(lexemeId)
								.and(lc1.REL_GROUP_ID.isNull())
								.and(lc1.COLLOCATION_ID.eq(c.ID))
								.and(lc2.COLLOCATION_ID.eq(c.ID))
								.and(lc2.LEXEME_ID.eq(l2.ID))
								.and(lc2.LEXEME_ID.ne(lc1.LEXEME_ID))
								.and(l2.WORD_ID.eq(w2.ID)))
				.orderBy(c.ID, lc2.MEMBER_ORDER)
				.fetchInto(CollocationTuple.class);
	}

	private List<eki.ekilex.data.Word> execute(
			Word w1, Condition where, SearchDatasetsRestriction searchDatasetsRestriction,
			String userRoleDatasetCode, List<String> tagNames, int offset, int maxResultsLimit, boolean noLimit) {

		List<String> availableDatasetCodes = searchDatasetsRestriction.getAvailableDatasetCodes();

		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");
		Language ln = LANGUAGE.as("ln");

		Field<Long> lnobf = DSL
				.select(ln.ORDER_BY)
				.from(ln)
				.where(ln.CODE.eq(w1.LANG))
				.asField();

		Table<Record9<Long, String, String, Integer, String, String, String, Boolean, Long>> w = DSL
				.select(
						w1.ID.as("word_id"),
						w1.VALUE.as("word_value"),
						w1.VALUE_PRESE.as("word_value_prese"),
						w1.HOMONYM_NR,
						w1.LANG,
						w1.GENDER_CODE,
						w1.ASPECT_CODE,
						w1.IS_PUBLIC.as("is_word_public"),
						lnobf.as("lang_order_by"))
				.from(w1)
				.where(where)
				.asTable("w");

		Field<String[]> wtf = getWordTypesField(w.field("word_id", Long.class));
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtz = getWordIsForeignField(w.field("word_id", Long.class));
		Field<Timestamp> wlaeof = getWordLastActivityEventOnField(w.field("word_id", Long.class));

		Field<String[]> lxvslvf;
		Field<Boolean> lxpsf;
		Field<String[]> lxtnf;
		if (StringUtils.isBlank(userRoleDatasetCode)) {
			lxvslvf = DSL.field(DSL.val(new String[0]));
			lxpsf = DSL.field(DSL.val((Boolean) null));
			lxtnf = DSL.field(DSL.val(new String[0]));
		} else {
			lxvslvf = DSL.field(DSL
					.select(DSL.arrayAggDistinct(VALUE_STATE_LABEL.VALUE))
					.from(l, VALUE_STATE_LABEL)
					.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
							.and(l.DATASET_CODE.eq(userRoleDatasetCode))
							.and(l.VALUE_STATE_CODE.isNotNull())
							.and(VALUE_STATE_LABEL.CODE.eq(l.VALUE_STATE_CODE))
							.and(VALUE_STATE_LABEL.LANG.eq(CLASSIF_LABEL_LANG_EST))
							.and(VALUE_STATE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
					.groupBy(w.field("word_id")));
			lxpsf = DSL.field(DSL
					.select(DSL.field(DSL.val(PUBLICITY_PUBLIC).eq(DSL.all(DSL.arrayAggDistinct(l.IS_PUBLIC)))))
					.from(l)
					.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
							.and(l.DATASET_CODE.eq(userRoleDatasetCode)))
					.groupBy(w.field("word_id")));

			if (CollectionUtils.isEmpty(tagNames)) {
				lxtnf = DSL.field(DSL.val(new String[0]));
			} else {
				lxtnf = DSL.field(DSL
						.select(DSL.arrayAggDistinct(DSL.coalesce(lt.TAG_NAME, "!")))
						.from(l
								.leftOuterJoin(lt).on(lt.LEXEME_ID.eq(l.ID).and(lt.TAG_NAME.in(tagNames))))
						.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
								.and(l.DATASET_CODE.eq(userRoleDatasetCode)))
						.groupBy(w.field("word_id")));
			}
		}

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(l.DATASET_CODE))
				.from(l)
				.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
						.and(l.DATASET_CODE.in(availableDatasetCodes)))
				.groupBy(w.field("word_id")));

		boolean fiCollationExists = fiCollationExists();

		Field<?> wvobf;
		if (fiCollationExists) {
			wvobf = w.field("word_value").collate("fi_FI");
		} else {
			wvobf = w.field("word_value");
		}
		Table<Record18<Long, String, String, Integer, String, String, String, String, String, String[], Boolean, Boolean, Boolean, Boolean, String[], String[], String[], Timestamp>> ww = DSL
				.select(
						w.field("word_id", Long.class),
						w.field("word_value", String.class),
						w.field("word_value_prese", String.class),
						w.field("homonym_nr", Integer.class),
						w.field("lang", String.class),
						w.field("word_class", String.class),
						w.field("gender_code", String.class),
						w.field("aspect_code", String.class),
						w.field("is_word_public", String.class),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						lxpsf.as("lexemes_are_public"),
						lxvslvf.as("lexemes_value_state_labels"),
						lxtnf.as("lexemes_tag_names"),
						dscf.as("dataset_codes"),
						wlaeof.as("last_activity_event_on"))
				.from(w)
				.orderBy(
						w.field("lang_order_by"),
						wvobf,
						w.field("is_word_public").desc(),
						w.field("homonym_nr"))
				.asTable("ww");

		if (noLimit) {
			return create.selectFrom(ww).fetchInto(eki.ekilex.data.Word.class);
		} else {
			return create.selectFrom(ww).limit(maxResultsLimit).offset(offset).fetchInto(eki.ekilex.data.Word.class);
		}
	}
}
