package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FORM_FREQ;
import static eki.ekilex.data.db.main.Tables.FREQ_CORP;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MORPH_FREQ;
import static eki.ekilex.data.db.main.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_FREQ;

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

import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.FormFreq;
import eki.ekilex.data.db.main.tables.FreqCorp;
import eki.ekilex.data.db.main.tables.Language;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MorphFreq;
import eki.ekilex.data.db.main.tables.MorphLabel;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordFreq;
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

		Table<?> w = mainDb
				.select(word.ID.as("word_id"))
				.from(word)
				.where(where)
				//.groupBy(word.ID)
				.asTable("w");

		return mainDb.fetchCount(w);
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

		return mainDb
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

		return mainDb
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

		return mainDb
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

		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
						w.LANG,
						w.HOMONYM_NR,
						w.DISPLAY_MORPH_CODE,
						w.GENDER_CODE,
						w.ASPECT_CODE,
						w.VOCAL_FORM,
						w.MORPHOPHONO_FORM,
						w.MORPH_COMMENT,
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
			return mainDb.selectFrom(ww).fetchInto(eki.ekilex.data.Word.class);
		} else {
			return mainDb.selectFrom(ww).limit(maxResultsLimit).offset(offset).fetchInto(eki.ekilex.data.Word.class);
		}
	}
}
