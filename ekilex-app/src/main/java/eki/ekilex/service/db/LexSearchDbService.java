package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FORM_FREQ;
import static eki.ekilex.data.db.main.Tables.FREQ_CORP;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.MORPH_FREQ;
import static eki.ekilex.data.db.main.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record19;
import org.jooq.Record9;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.FormFreq;
import eki.ekilex.data.db.main.tables.FreqCorp;
import eki.ekilex.data.db.main.tables.Language;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.MorphFreq;
import eki.ekilex.data.db.main.tables.MorphLabel;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.Word;
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

		Word w1 = WORD.as("w1");
		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();
		Condition wordCondition = lexSearchConditionComposer.createSearchCondition(w1, searchCriteriaGroups, searchDatasetsRestriction);

		return execute(w1, wordCondition, searchDatasetsRestriction, userRoleDatasetCode, tagNames, offset, maxResultsLimit, noLimit);
	}

	public int countWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Word w1 = WORD.as("w");
		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();
		Condition wordCondition = lexSearchConditionComposer.createSearchCondition(w1, searchCriteriaGroups, searchDatasetsRestriction);

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

	public List<ParadigmFormTuple> getParadigmFormTuples(Long wordId, String classifierLabelLang) {

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
								.and(ml.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.orderBy(p.ID, pf.ORDER_BY)
				.fetchInto(ParadigmFormTuple.class);
	}

	// TODO apply word?
	public List<eki.ekilex.data.Lexeme> getWordLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, String classifierLabelLang) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		List<Field<?>> lexemeFields = queryHelper.getLexemeFields(l, ds, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);
		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		return mainDb
				.select(lexemeFields)
				.from(l, ds)
				.where(
						l.WORD_ID.eq(wordId)
								.and(l.DATASET_CODE.eq(ds.CODE))
								.and(dsWhere))
				.orderBy(l.WORD_ID, ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetch(record -> {
					eki.ekilex.data.Lexeme pojo = record.into(eki.ekilex.data.Lexeme.class);
					queryHelper.replaceNullCollections(pojo);
					return pojo;
				});
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

		Field<String[]> wtf = queryHelper.getWordTagsField(w.field("word_id", Long.class));
		Field<String[]> wwtf = queryHelper.getWordTypeCodesField(w.field("word_id", Long.class));
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(w.field("word_id", Long.class));
		Field<Timestamp> wlaeof = queryHelper.getWordLastActivityEventOnField(w.field("word_id", Long.class));

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
		Table<Record19<Long, String, String, Integer, String, String, String, String, String, String[], String[], Boolean, Boolean, Boolean, Boolean, String[], String[], String[], Timestamp>> ww = DSL
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
						wtf.as("tags"),
						wwtf.as("word_type_codes"),
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
			return mainDb
					.selectFrom(ww)
					.fetchInto(eki.ekilex.data.Word.class);
		} else {
			return mainDb
					.selectFrom(ww)
					.limit(maxResultsLimit)
					.offset(offset)
					.fetchInto(eki.ekilex.data.Word.class);
		}
	}
}
