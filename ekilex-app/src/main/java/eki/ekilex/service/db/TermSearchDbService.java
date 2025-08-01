package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record17;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LastActivityType;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Language;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordWordType;
import eki.ekilex.data.db.main.udt.records.TypeTermMeaningWordRecord;
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

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(wo.ID);
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(wo.ID);
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(wo.ID);
		Field<Boolean> wtzf = queryHelper.getWordIsForeignField(wo.ID);
		Field<Boolean> imwf = DSL.field(wo.ID.eq(DSL.any(m.field("match_word_ids", Long[].class))));
		Field<Boolean> lvsmpf = DSL.field(lo.VALUE_STATE_CODE.eq(VALUE_STATE_CODE_MOST_PREFERRED));
		Field<Boolean> lvslpf = DSL.field(lo.VALUE_STATE_CODE.eq(VALUE_STATE_CODE_LEAST_PREFERRED));

		Field<String[]> wds = DSL.field(DSL
				.select(DSL
						.arrayAgg(lds.DATASET_CODE)
						.orderBy(ds.ORDER_BY))
				.from(lds, ds)
				.where(
						lds.WORD_ID.eq(wo.ID)
								.and(lds.MEANING_ID.eq(m.field("meaning_id", Long.class)))
								.and(lds.DATASET_CODE.eq(ds.CODE))
								.and(lds.DATASET_CODE.in(availableDatasetCodes)))
				.groupBy(lds.DATASET_CODE, ds.ORDER_BY));

		Condition wherewolo = searchFilterHelper.applyDatasetRestrictions(lo, searchDatasetsRestriction, null)
				.and(lo.MEANING_ID.eq(m.field("meaning_id", Long.class)))
				.and(lo.WORD_ID.eq(wo.ID))
				.and(wo.LANG.eq(wol.CODE));

		if (StringUtils.isNotBlank(resultLang)) {
			wherewolo = wherewolo.and(wo.LANG.eq(resultLang));
		}

		Field<JSON> mdf = queryHelper.getMeaningDomainsField(m.field("meaning_id", Long.class), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

		Field<JSON> mwf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(wo.ID),
										DSL.key("wordValue").value(wo.VALUE),
										DSL.key("wordValuePrese").value(wo.VALUE_PRESE),
										DSL.key("homonymNr").value(wo.HOMONYM_NR),
										DSL.key("lang").value(wo.LANG),
										DSL.key("wordTypeCodes").value(wtf),
										DSL.key("prefixoid").value(wtpf),
										DSL.key("suffixoid").value(wtsf),
										DSL.key("foreign").value(wtzf),
										DSL.key("matchingWord").value(imwf),
										DSL.key("mostPreferred").value(lvsmpf),
										DSL.key("leastPreferred").value(lvslpf),
										DSL.key("public").value(lo.IS_PUBLIC),
										DSL.key("datasetCodes").value(wds)))
						.orderBy(
								wol.ORDER_BY,
								lo.ORDER_BY))
				.from(wo, lo, wol)
				.where(wherewolo)
				.asField();

		int limit = DEFAULT_MAX_RESULTS_LIMIT;
		if (noLimit) {
			limit = Integer.MAX_VALUE;
		}

		boolean fiCollationExists = fiCollationExists();
		Field<String> wvobf;
		if (fiCollationExists) {
			wvobf = wm.VALUE.collate("fi_FI");
		} else {
			wvobf = wm.VALUE;
		}

		return mainDb
				.select(
						m.field("meaning_id", Long.class),
						mdf.as("meaning_domains"),
						mwf.as("meaning_words"))
				.from(m
						.innerJoin(wm).on(wm.ID.eq(m.field("word_id", Long.class))))
				.orderBy(wvobf)
				.limit(limit)
				.offset(offset)
				.fetchInto(TermMeaning.class);
	}

	private int executeCountMeaningsMeaningMode(Table<Record3<Long, Long, Long[]>> m) {
		return mainDb.fetchCount(DSL.selectDistinct(m.field("meaning_id")).from(m));
	}

	private int executeCountWordsMeaningMode(Table<Record3<Long, Long, Long[]>> m, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang) {

		Lexeme lo = LEXEME.as("lo");
		Word wo = WORD.as("wo");

		Condition wherewo = wo.ID.eq(lo.WORD_ID);
		if (StringUtils.isNotBlank(resultLang)) {
			wherewo = wherewo.and(wo.LANG.eq(resultLang));
		}

		Condition wherelods = searchFilterHelper.applyDatasetRestrictions(lo, searchDatasetsRestriction, null);

		return mainDb
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

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(wmid.field("word_id", Long.class));
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(wmid.field("word_id", Long.class));
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(wmid.field("word_id", Long.class));
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(wmid.field("word_id", Long.class));

		Field<Boolean> lvsmpf = DSL.field(DSL.exists(DSL
				.select(lvs.ID)
				.from(lvs)
				.where(
						lvs.WORD_ID.eq(wmid.field("word_id", Long.class))
								.and(lvs.MEANING_ID.eq(wmid.field("meaning_id", Long.class)))
								.and(lvs.VALUE_STATE_CODE.eq(VALUE_STATE_CODE_MOST_PREFERRED)))));
		Field<Boolean> lvslpf = DSL.field(DSL.exists(DSL
				.select(lvs.ID)
				.from(lvs)
				.where(
						lvs.WORD_ID.eq(wmid.field("word_id", Long.class))
								.and(lvs.MEANING_ID.eq(wmid.field("meaning_id", Long.class)))
								.and(lvs.VALUE_STATE_CODE.eq(VALUE_STATE_CODE_LEAST_PREFERRED)))));
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

		Field<JSON> mdf = queryHelper.getMeaningDomainsField(wmm.field("meaning_id", Long.class), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

		// TODO refactor with json builder
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

		return mainDb
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
		return mainDb.fetchCount(DSL.selectDistinct(wm.field("meaning_id")).from(wm));
	}

	private int executeCountWordsWordMode(Table<Record3<Long, Long, Long[]>> wmid, String resultLang) {

		Word wm = WORD.as("wm");

		Condition wherewm = wm.ID.eq(wmid.field("word_id", Long.class));
		if (StringUtils.isNotBlank(resultLang)) {
			wherewm = wherewm.and(wm.LANG.eq(resultLang));
		}

		return mainDb.fetchCount(DSL.selectDistinct(wm.ID).from(wmid.innerJoin(wm).on(wherewm)));
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
		Field<LocalDateTime> mlacteof = queryHelper.getMeaningLastActivityEventOnField(m.ID, LastActivityType.EDIT);
		Field<LocalDateTime> mlappeof = queryHelper.getMeaningLastActivityEventOnField(m.ID, LastActivityType.APPROVE);

		return mainDb
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

	public String getMeaningFirstWordValue(Long meaningId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(LEXEME, searchDatasetsRestriction, null);

		return mainDb
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

		return mainDb
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
