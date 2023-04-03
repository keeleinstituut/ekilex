package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record17;
import org.jooq.Record19;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LastActivityType;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.LexemeWordTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;
import eki.ekilex.data.db.udt.records.TypeTermMeaningWordRecord;
import eki.ekilex.service.db.util.SearchFilterHelper;
import eki.ekilex.service.db.util.TermSearchConditionComposer;

@Component
public class TermSearchDbService extends AbstractDataDbService {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	@Autowired
	private TermSearchConditionComposer termSearchConditionComposer;

	// simple search

	public TermSearchResult getTermSearchResult(
			String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction,
			SearchResultMode resultMode, String resultLang, int offset, boolean noLimit) {

		Table<Record3<Long, Long, Long[]>> wm = termSearchConditionComposer.composeFilteredMeaning(searchFilter, searchDatasetsRestriction, resultMode);
		return composeResult(wm, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
	}

	// detail search

	public TermSearchResult getTermSearchResult(
			SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction,
			SearchResultMode resultMode, String resultLang, int offset, boolean noLimit) throws Exception {

		Table<Record3<Long, Long, Long[]>> wm = termSearchConditionComposer.composeFilteredMeaning(searchFilter, searchDatasetsRestriction, resultMode);
		return composeResult(wm, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
	}

	// search commons

	private List<TermMeaning> executeFetchMeaningMode(
			Table<Record3<Long, Long, Long[]>> m,
			SearchDatasetsRestriction searchDatasetsRestriction,
			String resultLang, int offset, boolean noLimit) {

		List<String> availableDatasetCodes = searchDatasetsRestriction.getAvailableDatasetCodes();

		Lexeme lo = LEXEME.as("lo");
		Word wo = WORD.as("wo");
		Word wm = WORD.as("wm");
		Lexeme lds = LEXEME.as("lds");
		Dataset ds = DATASET.as("ds");
		Language wol = LANGUAGE.as("wol");

		Field<String[]> wtf = getWordTypesField(wo.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(wo.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(wo.ID);
		Field<Boolean> wtzf = getWordIsForeignField(wo.ID);
		Field<Boolean> imwf = DSL.field(wo.ID.eq(DSL.any(m.field("match_word_ids", Long[].class))));

		Field<Boolean> lvsmpf = DSL.field(lo.VALUE_STATE_CODE.eq(VALUE_STATE_MOST_PREFERRED));
		Field<Boolean> lvslpf = DSL.field(lo.VALUE_STATE_CODE.eq(VALUE_STATE_LEAST_PREFERRED));

		Table<Record3<Long, String, Long>> wdsf = DSL
				.selectDistinct(lds.WORD_ID, lds.DATASET_CODE, ds.ORDER_BY)
				.from(lds, ds)
				.where(
						lds.WORD_ID.eq(wo.ID)
								.and(lds.MEANING_ID.eq(m.field("meaning_id", Long.class)))
								.and(lds.DATASET_CODE.eq(ds.CODE))
								.and(lds.DATASET_CODE.in(availableDatasetCodes)))
				.asTable("wdsf");

		Field<String[]> wds = DSL.field(DSL
				.select(DSL.arrayAgg(wdsf.field("dataset_code", String.class)).orderBy(wdsf.field("order_by")))
				.from(wdsf));

		Condition wherelods = searchFilterHelper.applyDatasetRestrictions(lo, searchDatasetsRestriction, null);

		Condition wherewo = wo.ID.eq(lo.WORD_ID);
		if (StringUtils.isNotBlank(resultLang)) {
			wherewo = wherewo.and(wo.LANG.eq(resultLang));
		}

		Table<Record19<Long, Long, String, Long, String, String, Integer, String, String[], Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, String[], Long, Long>> mm = DSL
				.select(
						m.field("meaning_id", Long.class),
						wm.ID.as("order_by_word_id"),
						wm.VALUE.as("order_by_word"),
						wo.ID.as("word_id"),
						wo.VALUE.as("word_value"),
						wo.VALUE_PRESE.as("word_value_prese"),
						wo.HOMONYM_NR,
						wo.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtzf.as("foreign"),
						imwf.as("matching_word"),
						lvsmpf.as("most_preferred"),
						lvslpf.as("least_preferred"),
						lo.IS_PUBLIC,
						wds.as("dataset_codes"),
						wol.ORDER_BY.as("lang_order_by"),
						lo.ORDER_BY.as("lex_order_by"))
				.from(m
						.innerJoin(wm).on(wm.ID.eq(m.field("word_id", Long.class)))
						.innerJoin(lo).on(lo.MEANING_ID.eq(m.field("meaning_id", Long.class)).and(wherelods))
						.innerJoin(wo).on(wherewo)
						.innerJoin(wol).on(wol.CODE.eq(wo.LANG)))
				.asTable("m");

		Field<TypeClassifierRecord[]> mdf = getMeaningDomainsField(mm.field("meaning_id", Long.class), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

		Field<TypeTermMeaningWordRecord[]> mw = DSL
				.field("array_agg(row ("
						+ "m.word_id,"
						+ "' ' || m.word_value,"
						+ "' ' || m.word_value_prese,"
						+ "m.homonym_nr,"
						+ "m.lang,"
						+ "m.word_type_codes,"
						+ "m.prefixoid,"
						+ "m.suffixoid,"
						+ "m.foreign,"
						+ "m.matching_word,"
						+ "m.most_preferred,"
						+ "m.least_preferred,"
						+ "m.is_public,"
						+ "m.dataset_codes"
						+ ")::type_term_meaning_word "
						+ "order by "
						+ "m.lang_order_by,"
						+ "m.lex_order_by)", TypeTermMeaningWordRecord[].class);

		int limit = DEFAULT_MAX_RESULTS_LIMIT;
		if (noLimit) {
			limit = Integer.MAX_VALUE;
		}

		boolean fiCollationExists = fiCollationExists();
		Field<?> wvobf;
		if (fiCollationExists) {
			wvobf = mm.field("order_by_word").collate("fi_FI");			
		} else {
			wvobf = mm.field("order_by_word");
		}

		/*
		 * meaning words of same homonym and same meaning for different datasets are repeating
		 * which is cleaned programmatically at ui conversion
		 */
		return create
				.select(
						mm.field("meaning_id", Long.class),
						mdf.as("meaning_domains"),
						mw.as("meaning_words"))
				.from(mm)
				.groupBy(
						mm.field("meaning_id"),
						mm.field("order_by_word_id"),
						wvobf)
				.orderBy(wvobf)
				.limit(limit)
				.offset(offset)
				.fetchInto(TermMeaning.class);
	}

	private int executeCountMeaningsMeaningMode(Table<Record3<Long, Long, Long[]>> m) {
		return create.fetchCount(DSL.selectDistinct(m.field("meaning_id")).from(m));
	}

	private int executeCountWordsMeaningMode(Table<Record3<Long, Long, Long[]>> m, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang) {

		Lexeme lo = LEXEME.as("lo");
		Word wo = WORD.as("wo");

		Condition wherewo = wo.ID.eq(lo.WORD_ID);
		if (StringUtils.isNotBlank(resultLang)) {
			wherewo = wherewo.and(wo.LANG.eq(resultLang));
		}

		Condition wherelods = searchFilterHelper.applyDatasetRestrictions(lo, searchDatasetsRestriction, null);

		return create
				.fetchCount(DSL
						.selectDistinct(wo.ID)
						.from(m
								.innerJoin(lo).on(lo.MEANING_ID.eq(m.field("meaning_id", Long.class)).and(wherelods))
								.innerJoin(wo).on(wherewo)));
	}

	private List<TermMeaning> executeFetchWordMode(
			Table<Record3<Long, Long, Long[]>> wmid,
			String resultLang, int offset, boolean noLimit) {

		Lexeme lvs = LEXEME.as("lvs");
		Lexeme lw = LEXEME.as("lw");
		Word wm = WORD.as("wm");
		Lexeme lds = LEXEME.as("lds");
		Dataset ds = DATASET.as("ds");
		Language ln = LANGUAGE.as("ln");
		WordWordType wt = WORD_WORD_TYPE.as("wt");

		Field<String[]> wtf = getWordTypesField(wmid.field("word_id", Long.class));
		Field<Boolean> wtpf = getWordIsPrefixoidField(wmid.field("word_id", Long.class));
		Field<Boolean> wtsf = getWordIsSuffixoidField(wmid.field("word_id", Long.class));
		Field<Boolean> wtz = getWordIsForeignField(wmid.field("word_id", Long.class));

		Field<Boolean> lvsmpf = DSL.field(DSL.exists(DSL
				.select(lvs.ID)
				.from(lvs)
				.where(
						lvs.WORD_ID.eq(wmid.field("word_id", Long.class))
						.and(lvs.MEANING_ID.eq(wmid.field("meaning_id", Long.class)))
						.and(lvs.VALUE_STATE_CODE.eq(VALUE_STATE_MOST_PREFERRED)))));
		Field<Boolean> lvslpf = DSL.field(DSL.exists(DSL
				.select(lvs.ID)
				.from(lvs)
				.where(
						lvs.WORD_ID.eq(wmid.field("word_id", Long.class))
						.and(lvs.MEANING_ID.eq(wmid.field("meaning_id", Long.class)))
						.and(lvs.VALUE_STATE_CODE.eq(VALUE_STATE_LEAST_PREFERRED)))));
		Field<Boolean> lpf = DSL.field(DSL.exists(DSL
				.select(lvs.ID)
				.from(lvs)
				.where(
						lvs.WORD_ID.eq(wmid.field("word_id", Long.class))
						.and(lvs.MEANING_ID.eq(wmid.field("meaning_id", Long.class)))
						.and(lvs.IS_PUBLIC.isTrue()))));

		Field<Long> wmdsobf = DSL
				.select(DSL.min(ds.ORDER_BY))
				.from(lw, ds)
				.where(lw.WORD_ID.eq(wm.ID).and(lw.DATASET_CODE.eq(ds.CODE)))
				.asField();
		Field<Long> wlnobf = DSL
				.select(ln.ORDER_BY)
				.from(ln)
				.where(ln.CODE.eq(wm.LANG))
				.asField();
		Field<Long> wtobf = DSL
				.select(DSL.count(wt.ID))
				.from(wt)
				.where(wt.WORD_ID.eq(wm.ID).and(wt.WORD_TYPE_CODE.in(Arrays.asList(WORD_TYPE_CODE_PREFIXOID, WORD_TYPE_CODE_SUFFIXOID))))
				.asField();

		Table<Record3<Long, String, Long>> wdsf = DSL
				.selectDistinct(lds.WORD_ID, lds.DATASET_CODE, ds.ORDER_BY)
				.from(lds, ds)
				.where(
						lds.WORD_ID.eq(wmid.field("word_id", Long.class))
								.and(lds.MEANING_ID.eq(wmid.field("meaning_id", Long.class)))
								.and(lds.DATASET_CODE.eq(ds.CODE)))
				.asTable("wdsf");

		Field<String[]> wds = DSL.field(DSL
				.select(DSL.arrayAgg(wdsf.field("dataset_code", String.class)).orderBy(wdsf.field("order_by")))
				.from(wdsf));

		Condition wherewm = wm.ID.eq(wmid.field("word_id", Long.class));
		if (StringUtils.isNotBlank(resultLang)) {
			wherewm = wherewm.and(wm.LANG.eq(resultLang));
		}

		Table<Record17<Long, Long, String, String, Integer, String, String[], Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, String[], Long, Long, Long>> wmm = DSL
				.select(
						wmid.field("meaning_id", Long.class),
						wmid.field("word_id", Long.class),
						wm.VALUE.as("word_value"),
						wm.VALUE_PRESE.as("word_value_prese"),
						wm.HOMONYM_NR,
						wm.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						lvsmpf.as("most_preferred"),
						lvslpf.as("least_preferred"),
						lpf.as("is_public"),
						wds.as("dataset_codes"),
						wmdsobf.as("word_min_ds_order_by"),
						wlnobf.as("word_lang_order_by"),
						wtobf.as("word_type_order_by"))
				.from(wmid.innerJoin(wm).on(wherewm))
				.groupBy(
						wmid.field("word_id"),
						wmid.field("meaning_id"),
						wm.ID)
				.asTable("wm");

		Field<TypeClassifierRecord[]> mdf = getMeaningDomainsField(wmm.field("meaning_id", Long.class), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

		Field<TypeTermMeaningWordRecord[]> mw = DSL
				.field("array(select row ("
						+ "wm.word_id,"
						+ "' ' || wm.word_value,"
						+ "' ' || wm.word_value_prese,"
						+ "wm.homonym_nr,"
						+ "wm.lang,"
						+ "wm.word_type_codes,"
						+ "wm.prefixoid,"
						+ "wm.suffixoid,"
						+ "wm.foreign,"
						+ "true,"
						+ "wm.most_preferred,"
						+ "wm.least_preferred,"
						+ "wm.is_public,"
						+ "wm.dataset_codes"
						+ ")::type_term_meaning_word)", TypeTermMeaningWordRecord[].class);

		int limit = DEFAULT_MAX_RESULTS_LIMIT;
		if (noLimit) {
			limit = Integer.MAX_VALUE;
		}

		boolean fiCollationExists = fiCollationExists();
		Field<?> wvobf;
		if (fiCollationExists) {
			wvobf = wmm.field("word_value").collate("fi_FI");			
		} else {
			wvobf = wmm.field("word_value");
		}

		return create
				.select(
						wmm.field("meaning_id", Long.class),
						mdf.as("meaning_domains"),
						mw.as("meaning_words"))
				.from(wmm)
				.orderBy(
						wmm.field("word_min_ds_order_by"),
						wmm.field("word_lang_order_by"),
						wvobf,
						wmm.field("word_type_order_by"),
						wmm.field("homonym_nr"))
				.limit(limit)
				.offset(offset)
				.fetchInto(TermMeaning.class);
	}

	private int executeCountMeaningsWordMode(Table<Record3<Long, Long, Long[]>> wm) {
		return create.fetchCount(DSL.selectDistinct(wm.field("meaning_id")).from(wm));
	}

	private int executeCountWordsWordMode(Table<Record3<Long, Long, Long[]>> wmid, String resultLang) {

		Word wm = WORD.as("wm");

		Condition wherewm = wm.ID.eq(wmid.field("word_id", Long.class));
		if (StringUtils.isNotBlank(resultLang)) {
			wherewm = wherewm.and(wm.LANG.eq(resultLang));
		}

		return create.fetchCount(DSL.selectDistinct(wm.ID).from(wmid.innerJoin(wm).on(wherewm)));
	}

	private TermSearchResult composeResult(
			Table<Record3<Long, Long, Long[]>> wm, SearchDatasetsRestriction searchDatasetsRestriction,
			SearchResultMode resultMode, String resultLang, int offset, boolean noLimit) {

		List<TermMeaning> results = Collections.emptyList();
		int meaningCount = 0;
		int wordCount = 0;
		int resultCount = 0;
		if (SearchResultMode.MEANING.equals(resultMode)) {
			results = executeFetchMeaningMode(wm, searchDatasetsRestriction, resultLang, offset, noLimit);
			meaningCount = resultCount = executeCountMeaningsMeaningMode(wm);
			wordCount = executeCountWordsMeaningMode(wm, searchDatasetsRestriction, resultLang);
		} else if (SearchResultMode.WORD.equals(resultMode)) {
			results = executeFetchWordMode(wm, resultLang, offset, noLimit);
			meaningCount = executeCountMeaningsWordMode(wm);
			wordCount = resultCount = executeCountWordsWordMode(wm, resultLang);
		}

		TermSearchResult termSearchResult = new TermSearchResult();
		termSearchResult.setResults(results);
		termSearchResult.setMeaningCount(meaningCount);
		termSearchResult.setWordCount(wordCount);
		termSearchResult.setResultCount(resultCount);

		return termSearchResult;
	}

	// getters

	public eki.ekilex.data.Meaning getMeaning(Long meaningId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);
		Field<Timestamp> mlacteof = getMeaningLastActivityEventOnField(m.ID, LastActivityType.EDIT);
		Field<Timestamp> mlappeof = getMeaningLastActivityEventOnField(m.ID, LastActivityType.APPROVE);

		return create
				.select(
						m.ID.as("meaning_id"),
						m.MANUAL_EVENT_ON,
						mlacteof.as("last_activity_event_on"),
						mlappeof.as("last_approve_event_on"),
						DSL.arrayAggDistinct(l.ID).orderBy(l.ID).as("lexeme_ids"),
						DSL.arrayAggDistinct(l.DATASET_CODE).as("lexeme_dataset_codes"))
				.from(m, l)
				.where(
						m.ID.eq(meaningId)
								.and(l.MEANING_ID.eq(m.ID))
								.and(dsWhere))
				.groupBy(m.ID)
				.fetchOptionalInto(eki.ekilex.data.Meaning.class)
				.orElse(null);
	}

	public LexemeWordTuple getLexemeWordTuple(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);

		Field<TypeClassifierRecord[]> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<TypeClassifierRecord[]> lderf = getLexemeDerivsField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<TypeClassifierRecord[]> lregf = getLexemeRegistersField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<TypeClassifierRecord[]> lrgnf = getLexemeRegionsField(l.ID);
		Field<TypeClassifierRecord> lvalstf = getLexemeValueStateField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<TypeClassifierRecord> lproflf = getLexemeProficiencyLevelField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		return create
				.select(
						l.ID.as("lexeme_id"),
						l.MEANING_ID,
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
						l.ORDER_BY,
						lposf.as("pos"),
						lderf.as("derivs"),
						lregf.as("registers"),
						lrgnf.as("regions"),
						l.WORD_ID,
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
						w.HOMONYM_NR,
						w.LANG.as("word_lang"),
						w.GENDER_CODE.as("word_gender_code"),
						w.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"))
				.from(w, l)
				.where(l.ID.eq(lexemeId).and(l.WORD_ID.eq(w.ID)))
				.groupBy(l.ID, w.ID)
				.orderBy(w.ID, l.DATASET_CODE, l.LEVEL1, l.LEVEL2)
				.fetchSingleInto(LexemeWordTuple.class);
	}

	public String getMeaningFirstWordValue(Long meaningId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(LEXEME, searchDatasetsRestriction, null);

		return create
				.select(WORD.VALUE)
				.from(WORD, LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(WORD.IS_PUBLIC.isTrue())
								.and(dsWhere))
				.orderBy(LEXEME.LEVEL1, LEXEME.LEVEL2, WORD.ID)
				.limit(1)
				.fetchSingleInto(String.class);
	}

	public String getMeaningFirstWordValueOrderedByLang(Long meaningId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(LEXEME, searchDatasetsRestriction, null);

		return create
				.select(WORD.VALUE)
				.from(WORD, LEXEME, LANGUAGE)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(WORD.IS_PUBLIC.isTrue())
								.and(WORD.LANG.eq(LANGUAGE.CODE))
								.and(dsWhere))
				.orderBy(LANGUAGE.ORDER_BY, LEXEME.ORDER_BY)
				.limit(1)
				.fetchSingleInto(String.class);
	}

}
