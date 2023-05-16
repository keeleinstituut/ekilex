package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.WORD;

import java.sql.Timestamp;
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
import eki.ekilex.data.db.tables.Meaning;
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

	public MeaningTableRow getMeaningTableRow(Long meaningId) {

		Meaning m = MEANING.as("m");
		Language cll = LANGUAGE.as("cll");
		Definition d = DEFINITION.as("d");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff = FREEFORM.as("ff");

		Field<Long> meaningIdField = DSL.field(DSL.val(meaningId));

		Condition wherel = l.MEANING_ID.eq(meaningId);

		Field<TypeMtDefinitionRecord[]> df = getDefRowField(d, meaningIdField);
		Field<TypeMtLexemeRecord[]> lf = getLexRowField(cll, l, w, wherel);
		Field<TypeMtWordRecord[]> wf = gerWordRowField(cll, l, w, wherel);
		Field<TypeMtLexemeFreeformRecord[]> uf = getUsageRowField(l, lff, ff, wherel);

		return create
				.select(
						m.ID.as("meaning_id"),
						df.as("definitions"),
						lf.as("lexemes"),
						wf.as("words"),
						uf.as("usages"))
				.from(m)
				.where(m.ID.eq(meaningId))
				.fetchSingleInto(MeaningTableRow.class);
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

		Field<TypeMtDefinitionRecord[]> df = getDefRowField(d, meaningIdField);
		Field<TypeMtLexemeRecord[]> lf = getLexRowField(cll, l, w, wherel);
		Field<TypeMtWordRecord[]> wf = gerWordRowField(cll, l, w, wherel);
		Field<TypeMtLexemeFreeformRecord[]> uf = getUsageRowField(l, lff, ff, wherel);

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

	private static Field<TypeMtLexemeFreeformRecord[]> getUsageRowField(Lexeme l, LexemeFreeform lff, Freeform ff, Condition wherel) {

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
		return uf;
	}

	private static Field<TypeMtWordRecord[]> gerWordRowField(Language cll, Lexeme l, Word w, Condition wherel) {

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
		return wf;
	}

	private Field<TypeMtDefinitionRecord[]> getDefRowField(Definition d, Field<Long> meaningIdField) {

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
		return df;
	}

	private static Field<TypeMtLexemeRecord[]> getLexRowField(Language cll, Lexeme l, Word w, Condition wherel) {

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
		return lf;
	}

	private int executeCountMeaningsMeaningMode(Table<Record3<Long, Long, Long[]>> m) {
		return create.fetchCount(DSL.selectDistinct(m.field("meaning_id")).from(m));
	}

	public boolean isDefinitionUpdate(Long definitionId, String valuePrese, boolean isPublic) {

		return create
				.select(DSL.field(DSL.count(DEFINITION.ID).eq(0)).as("is_definition_update"))
				.from(DEFINITION)
				.where(
						DEFINITION.ID.eq(definitionId)
								.and(DEFINITION.VALUE_PRESE.eq(valuePrese))
								.and(DEFINITION.IS_PUBLIC.eq(isPublic)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isLexemeUpdate(Long lexemeId, boolean isPublic) {

		return create
				.select(DSL.field(DSL.count(LEXEME.ID).eq(0)).as("is_lexeme_update"))
				.from(LEXEME)
				.where(
						LEXEME.ID.eq(lexemeId)
								.and(LEXEME.IS_PUBLIC.eq(isPublic)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isUsageUpdate(Long usageId, String valuePrese, boolean isPublic) {

		return create
				.select(DSL.field(DSL.count(FREEFORM.ID).eq(0)).as("is_usage_update"))
				.from(FREEFORM)
				.where(
						FREEFORM.ID.eq(usageId)
								.and(FREEFORM.VALUE_PRESE.eq(valuePrese))
								.and(FREEFORM.IS_PUBLIC.eq(isPublic)))
				.fetchSingleInto(Boolean.class);
	}

	public void updateDefinition(Long definitionId, String value, String valuePrese, boolean isPublic) {

		create.update(DEFINITION)
				.set(DEFINITION.VALUE, value)
				.set(DEFINITION.VALUE_PRESE, valuePrese)
				.set(DEFINITION.IS_PUBLIC, isPublic)
				.where(DEFINITION.ID.eq(definitionId))
				.execute();
	}

	public void updateLexeme(Long lexemeId, boolean isPublic) {

		create.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateUsage(Long usageId, String value, String valuePrese, boolean isPublic, String userName) {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, value)
				.set(FREEFORM.VALUE_PRESE, valuePrese)
				.set(FREEFORM.IS_PUBLIC, isPublic)
				.set(FREEFORM.MODIFIED_BY, userName)
				.set(FREEFORM.MODIFIED_ON, timestamp)
				.where(FREEFORM.ID.eq(usageId))
				.execute();
	}
}
