package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.MeaningTableRow;
import eki.ekilex.data.MeaningTableSearchResult;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.db.Routines;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.udt.records.TypeMtDefinitionRecord;
import eki.ekilex.data.db.udt.records.TypeMtLexemeFreeformRecord;
import eki.ekilex.data.db.udt.records.TypeMtLexemeRecord;
import eki.ekilex.data.db.udt.records.TypeMtWordRecord;
import eki.ekilex.service.db.util.SearchFilterHelper;
import eki.ekilex.service.db.util.TermSearchConditionComposer;

@Component
public class MeaningTableDbService implements GlobalConstant, SystemConstant {

	@Autowired
	protected DSLContext create;

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	@Autowired
	private TermSearchConditionComposer termSearchConditionComposer;

	public MeaningTableSearchResult getMeaningTableSearchResult(
			String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang, int offset, boolean noLimit) {

		SearchResultMode resultMode = SearchResultMode.MEANING;
		Table<Record3<Long, Long, Long[]>> wm = termSearchConditionComposer.composeFilteredMeaning(searchFilter, searchDatasetsRestriction, resultMode);

		MeaningTableSearchResult result = composeResult(wm, searchDatasetsRestriction, resultLang, offset, noLimit);
		return result;
	}

	public MeaningTableSearchResult getMeaningTableSearchResult(
			SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang, int offset, boolean noLimit) throws Exception {

		SearchResultMode resultMode = SearchResultMode.MEANING;
		Table<Record3<Long, Long, Long[]>> wm = termSearchConditionComposer.composeFilteredMeaning(searchFilter, searchDatasetsRestriction, resultMode);

		MeaningTableSearchResult result = composeResult(wm, searchDatasetsRestriction, resultLang, offset, noLimit);
		return result;
	}

	private MeaningTableSearchResult composeResult(
			Table<Record3<Long, Long, Long[]>> wm, SearchDatasetsRestriction searchDatasetsRestriction,
			String resultLang, int offset, boolean noLimit) {

		List<MeaningTableRow> results = executeFetch(wm, searchDatasetsRestriction, resultLang, offset, noLimit);
		int resultCount = executeCountMeaningsMeaningMode(wm);

		MeaningTableSearchResult meaningTableSearchResult = new MeaningTableSearchResult();
		meaningTableSearchResult.setResults(results);
		meaningTableSearchResult.setResultCount(resultCount);

		return meaningTableSearchResult;
	}

	private List<MeaningTableRow> executeFetch(
			Table<Record3<Long, Long, Long[]>> m,
			SearchDatasetsRestriction searchDatasetsRestriction,
			String resultLang, int offset, boolean noLimit) {

		Language cll = LANGUAGE.as("cll");
		Definition d = DEFINITION.as("d");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff = FREEFORM.as("ff");

		Field<Long> meaningIdField = m.field("meaning_id", Long.class);

		Condition wherel = l.MEANING_ID.eq(meaningIdField);
		wherel = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, wherel);

		String drowsql = DSL
				.row(
						d.ID,
						d.DEFINITION_TYPE_CODE,
						Routines.encodeText(d.VALUE),
						d.VALUE_PRESE,
						d.LANG,
						d.COMPLEXITY,
						d.IS_PUBLIC)
				.toString();
		Field<TypeMtDefinitionRecord[]> daggf = DSL.field("array_agg(" + drowsql + "::type_mt_definition order by d.order_by)", TypeMtDefinitionRecord[].class);
		Field<TypeMtDefinitionRecord[]> df = DSL
				.select(daggf)
				.from(d)
				.where(d.MEANING_ID.eq(meaningIdField))
				.asField();

		String lrowsql = DSL
				.row(
						l.ID,
						l.WORD_ID,
						l.MEANING_ID,
						l.DATASET_CODE,
						l.IS_PUBLIC)
				.toString();
		Field<TypeMtLexemeRecord[]> laggf = DSL.field("array_agg(" + lrowsql + "::type_mt_lexeme order by cll.order_by, l.order_by)", TypeMtLexemeRecord[].class);
		Field<TypeMtLexemeRecord[]> lf = DSL
				.select(laggf)
				.from(l, w, cll)
				.where(
						wherel.and(l.WORD_ID.eq(w.ID))
								.and(w.LANG.eq(cll.CODE))
								.and(w.IS_PUBLIC.isTrue()))
				.asField();

		String wrowsql = DSL
				.row(
						l.ID,
						w.ID,
						w.VALUE,
						w.VALUE_PRESE,
						w.LANG,
						w.HOMONYM_NR,
						w.DISPLAY_MORPH_CODE,
						w.GENDER_CODE,
						w.ASPECT_CODE,
						w.VOCAL_FORM,
						w.MORPHOPHONO_FORM,
						w.MANUAL_EVENT_ON)
				.toString();
		Field<TypeMtWordRecord[]> waggf = DSL.field("array_agg(" + wrowsql + "::type_mt_word order by cll.order_by, l.order_by)", TypeMtWordRecord[].class);
		Field<TypeMtWordRecord[]> wf = DSL
				.select(waggf)
				.from(l, w, cll)
				.where(
						wherel.and(l.WORD_ID.eq(w.ID))
								.and(w.LANG.eq(cll.CODE))
								.and(w.IS_PUBLIC.isTrue()))
				.asField();

		String urowsql = DSL
				.row(
						l.ID,
						ff.ID,
						ff.TYPE,
						Routines.encodeText(ff.VALUE_TEXT),
						Routines.encodeText(ff.VALUE_PRESE),
						ff.LANG,
						ff.COMPLEXITY,
						ff.IS_PUBLIC,
						ff.CREATED_BY,
						ff.CREATED_ON,
						ff.MODIFIED_BY,
						ff.MODIFIED_ON)
				.toString();
		Field<TypeMtLexemeFreeformRecord[]> uaggf = DSL.field("array_agg(" + urowsql + "::type_mt_lexeme_freeform order by l.order_by, ff.order_by)", TypeMtLexemeFreeformRecord[].class);
		Field<TypeMtLexemeFreeformRecord[]> uf = DSL
				.select(uaggf)
				.from(l, lff, ff)
				.where(
						wherel.and(lff.LEXEME_ID.eq(l.ID))
								.and(lff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.USAGE.name())))
				.asField();

		Field<String> wof = DSL
				.select(DSL.field("({0})[1]", DSL.arrayAgg(w.VALUE).orderBy(cll.ORDER_BY, w.VALUE)))
				.from(l, w, cll)
				.where(wherel.and(l.WORD_ID.eq(w.ID)).and(w.LANG.eq(cll.CODE)).and(w.IS_PUBLIC.isTrue()))
				.asField();

		int limit = DEFAULT_MAX_RESULTS_LIMIT;
		if (noLimit) {
			limit = Integer.MAX_VALUE;
		}

		return create
				.select(
						meaningIdField.as("meaning_id"),
						df.as("definitions"),
						lf.as("lexemes"),
						wf.as("words"),
						uf.as("usages"),
						wof.as("order_by_word_value"))
				.from(m)
				.orderBy(DSL.field("order_by_word_value"), meaningIdField)
				.limit(limit)
				.offset(offset)
				.fetchInto(MeaningTableRow.class);
	}

	private int executeCountMeaningsMeaningMode(Table<Record3<Long, Long, Long[]>> m) {
		return create.fetchCount(DSL.selectDistinct(m.field("meaning_id")).from(m));
	}
}
