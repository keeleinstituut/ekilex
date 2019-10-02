package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_FREQUENCY;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.data.MeaningsResult;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeFrequency;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.udt.records.TypeTermMeaningWordRecord;

@Component
public class TermSearchDbService extends AbstractSearchDbService {

	@Autowired
	private DSLContext create;

	// simple search

	public MeaningsResult getMeaningsResult(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang, boolean fetchAll, int offset) {

		Table<Record3<Long, Long, Long[]>> m = composeFilteredMeaning(searchFilter, searchDatasetsRestriction);
		List<TermMeaning> meanings = executeFetch(m, searchDatasetsRestriction, resultLang, fetchAll, offset);
		int meaningCount = executeCountMeanings(m);
		int wordCount = executeCountWords(m, searchDatasetsRestriction, resultLang);

		MeaningsResult meaningsResult = new MeaningsResult();
		meaningsResult.setMeanings(meanings);
		meaningsResult.setMeaningCount(meaningCount);
		meaningsResult.setWordCount(wordCount);

		return meaningsResult;
	}

	private Table<Record3<Long, Long, Long[]>> composeFilteredMeaning(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) {

		String maskedSearchFilter = searchFilter.replace("*", "%").replace("?", "_").toLowerCase();

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Paradigm p = PARADIGM.as("p");
		Word w1 = WORD.as("w");
		Form f1 = FORM.as("f");

		Condition wheref = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name());
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			wheref = wheref.and(f1.VALUE.lower().like(maskedSearchFilter));
		} else {
			wheref = wheref.and(f1.VALUE.lower().equal(maskedSearchFilter));
		}

		Condition wherelds = composeLexemeDatasetsCondition(l, searchDatasetsRestriction);

		Table<Record1<Long>> f = DSL
				.select(f1.PARADIGM_ID)
				.from(f1)
				.where(wheref)
				.asTable("f");

		Table<Record1<Long>> w = DSL
				.select(w1.ID)
				.from(f, p, w1)
				.where(
						f.field("paradigm_id", Long.class).eq(p.ID)
						.and(p.WORD_ID.eq(w1.ID))
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(
										l.WORD_ID.eq(w1.ID).and(wherelds)
										.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY)))))
				.asTable("w");

		Table<Record3<Long, Long, Long[]>> mm = DSL
				.select(
						m.ID,
						DSL.field("(array_agg(w.id order by l.order_by)) [1]", Long.class).as("order_by_word_id"),
						DSL.arrayAgg(w.field("id", Long.class)).as("match_word_ids"))
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(
										l.WORD_ID.eq(w.field("id", Long.class))
										.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY)))
								.and(wherelds))
				.groupBy(m.ID)
				.asTable("m");

		return mm;
	}

	// detail search

	public MeaningsResult getMeaningsResult(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang, boolean fetchAll, int offset) throws Exception {

		Table<Record3<Long, Long, Long[]>> m = composeFilteredMeaning(searchFilter, searchDatasetsRestriction);
		List<TermMeaning> meanings = executeFetch(m, searchDatasetsRestriction, resultLang, fetchAll, offset);
		int meaningCount = executeCountMeanings(m);
		int wordCount = executeCountWords(m, searchDatasetsRestriction, resultLang);

		MeaningsResult meaningsResult = new MeaningsResult();
		meaningsResult.setMeanings(meanings);
		meaningsResult.setMeaningCount(meaningCount);
		meaningsResult.setWordCount(wordCount);

		return meaningsResult;
	}

	private Table<Record3<Long, Long, Long[]>> composeFilteredMeaning(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		List<SearchCriterionGroup> criteriaGroups = searchFilter.getCriteriaGroups();

		Word w1 = WORD.as("w1");
		Meaning m1 = MEANING.as("m1");
		Lexeme l = LEXEME.as("l");

		Condition wherem = DSL.trueCondition();
		Condition wherew = DSL.trueCondition();

		for (SearchCriterionGroup searchCriterionGroup : criteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				Lexeme l1 = LEXEME.as("l1");

				Condition wheref1 = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name())
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(p1.WORD_ID.eq(w1.ID));
				wheref1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, wheref1, true);
				wheref1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, wheref1, false);
				wherew = wherew.andExists(DSL.select(f1.ID).from(f1, p1).where(wheref1));

				Condition wherel1 = l1.WORD_ID.eq(w1.ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
				wherel1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, wherel1);
				wherel1 = applyLexemeSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, l1.ID, wherel1);
				wherel1 = applyLexemeSourceFilters(SearchKey.SOURCE_REF, searchCriteria, l1.ID, wherel1);
				wherel1 = applyTermWordLifecycleLogFilters(searchCriteria, l1, w1, wherel1);
				wherew = wherew.andExists(DSL.select(l1.ID).from(l1).where(wherel1));

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");

				wherem = applyDomainFilters(searchCriteria, m1, wherem);

				Condition wherel1 = l1.MEANING_ID.eq(m1.ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
				wherel1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, wherel1);
				wherel1 = applyTermMeaningLifecycleLogFilters(searchCriteria, l1, m1, wherel1);

				wherem = wherem.andExists(DSL.select(l1.ID).from(l1).where(wherel1));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Definition d1 = DEFINITION.as("d1");

				Condition whered1 = d1.MEANING_ID.eq(m1.ID);
				whered1 = applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, whered1, true);
				whered1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, d1.LANG, whered1, false);
				whered1 = applyDefinitionSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, d1.ID, whered1);
				whered1 = applyDefinitionSourceFilters(SearchKey.SOURCE_REF, searchCriteria, d1.ID, whered1);

				wherem = wherem.andExists(DSL.select(d1.ID).from(d1).where(whered1));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform u1 = FREEFORM.as("u1");

				Condition wherel1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1ff.LEXEME_ID.eq(l1.ID))
						.and(l1ff.FREEFORM_ID.eq(u1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()));

				wherel1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, wherel1);
				wherel1 = applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE_TEXT, wherel1, true);
				wherel1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, u1.LANG, wherel1, false);
				wherel1 = applyFreeformSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, u1.ID, wherel1);
				wherel1 = applyFreeformSourceFilters(SearchKey.SOURCE_REF, searchCriteria, u1.ID, wherel1);

				wherew = wherew.andExists(DSL.select(l1.ID).from(l1, l1ff, u1).where(wherel1));

			} else if (SearchEntity.NOTE.equals(searchEntity)) {

				// notes
				Freeform nff3 = FREEFORM.as("nff3");
				Condition where3 = nff3.TYPE.eq(FreeformType.PUBLIC_NOTE.name());

				where3 = applyValueFilters(SearchKey.VALUE, searchCriteria, nff3.VALUE_TEXT, where3, true);
				where3 = applyFreeformSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, nff3.ID, where3);
				where3 = applyFreeformSourceFilters(SearchKey.SOURCE_REF, searchCriteria, nff3.ID, where3);

				Table<Record1<Long>> n2 = DSL.select(nff3.ID.as("freeform_id")).from(nff3).where(where3).asTable("n2");

				// notes owner #1
				MeaningFreeform mff2 = MEANING_FREEFORM.as("mff2");

				// notes owner #2
				Definition d3 = DEFINITION.as("d3");
				DefinitionFreeform dff3 = DEFINITION_FREEFORM.as("dff3");
				Table<Record2<Long, Long>> dff2 = DSL.select(d3.MEANING_ID, dff3.FREEFORM_ID).from(d3, dff3).where(dff3.DEFINITION_ID.eq(d3.ID)).asTable("dff2");

				// notes owner #3
				Lexeme l3 = LEXEME.as("l3");
				LexemeFreeform lff3 = LEXEME_FREEFORM.as("lff3");
				Table<Record2<Long, Long>> lff2 = DSL
						.select(l3.MEANING_ID, lff3.FREEFORM_ID)
						.from(l3, lff3)
						.where(
								lff3.LEXEME_ID.eq(l3.ID)
								.and(l3.TYPE.eq(LEXEME_TYPE_PRIMARY)))
						.asTable("lff2");

				// notes owners joined
				Table<Record1<Long>> n1 = DSL
						.select(DSL.coalesce(mff2.MEANING_ID, DSL.coalesce(dff2.field("meaning_id"), lff2.field("meaning_id"))).as("meaning_id"))
						.from(n2
								.leftOuterJoin(mff2).on(mff2.FREEFORM_ID.eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(dff2).on(dff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(lff2).on(lff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class))))
						.asTable("n1");

				wherem = wherem.andExists(DSL.select(n1.field("meaning_id")).from(n1).where(n1.field("meaning_id", Long.class).eq(m1.ID)));

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				MeaningFreeform m1ff = MEANING_FREEFORM.as("m1ff");
				Freeform c1 = FREEFORM.as("c1");

				Condition where1 = m1ff.MEANING_ID.eq(m1.ID)
						.and(m1ff.FREEFORM_ID.eq(c1.ID))
						.and(c1.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				where1 = applyValueFilters(SearchKey.ID, searchCriteria, c1.VALUE_TEXT, where1, false);

				wherem = wherem.andExists(DSL.select(c1.ID).from(m1ff, c1).where(where1));

			} else if (SearchEntity.CLUELESS.equals(searchEntity)) {

				Definition d1 = DEFINITION.as("d1");
				Lexeme l1 = LEXEME.as("l1");
				Lexeme lds = LEXEME.as("lds");
				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
				DefinitionFreeform dff1 = DEFINITION_FREEFORM.as("dff1");
				LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
				Freeform ff1 = FREEFORM.as("ff1");
				Condition where1, where2, whereDs;

				// word select
				where2 = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name())
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID))
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
				where2 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where2, true);

				where1 = DSL.exists(DSL.select(w1.ID).from(f1, p1, w1).where(where2));
				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				SelectHavingStep<Record1<Long>> selectWord = DSL.select(l1.MEANING_ID).from(l1).where(where1).groupBy(l1.MEANING_ID);

				// definition select
				where1 = DSL.trueCondition();
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
				whereDs = composeLexemeDatasetsCondition(lds, searchDatasetsRestriction);
				where1 = where1.andExists(DSL.select(lds.ID).from(lds).where(lds.MEANING_ID.eq(d1.MEANING_ID).and(whereDs)));
				SelectHavingStep<Record1<Long>> selectDefinition = DSL.select(d1.MEANING_ID).from(d1).where(where1).groupBy(d1.MEANING_ID);

				// meaning ff select
				String[] meaningFreeformTypes = new String[] {
						FreeformType.PUBLIC_NOTE.name(), FreeformType.CONCEPT_ID.name(), FreeformType.LEARNER_COMMENT.name()};
				where1 = ff1.TYPE.in(meaningFreeformTypes).and(mff1.FREEFORM_ID.eq(ff1.ID));
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
				whereDs = composeLexemeDatasetsCondition(lds, searchDatasetsRestriction);
				where1 = where1.andExists(DSL.select(lds.ID).from(lds).where(lds.MEANING_ID.eq(mff1.MEANING_ID).and(whereDs)));
				SelectHavingStep<Record1<Long>> selectMeaningFreeforms = DSL.select(mff1.MEANING_ID).from(mff1, ff1).where(where1).groupBy(mff1.MEANING_ID);

				// definition ff select
				where1 = ff1.TYPE.eq(FreeformType.PUBLIC_NOTE.name()).and(dff1.FREEFORM_ID.eq(ff1.ID)).and(dff1.DEFINITION_ID.eq(d1.ID));
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
				whereDs = composeLexemeDatasetsCondition(lds, searchDatasetsRestriction);
				where1 = where1.andExists(DSL.select(lds.ID).from(lds).where(lds.MEANING_ID.eq(d1.MEANING_ID).and(whereDs)));
				SelectHavingStep<Record1<Long>> selectDefinitionFreeforms = DSL.select(d1.MEANING_ID).from(d1, dff1, ff1).where(where1).groupBy(d1.MEANING_ID);

				// lexeme ff select
				String[] lexemeFreeformTypes = new String[] {
						FreeformType.PUBLIC_NOTE.name(), FreeformType.USAGE.name(), FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name()};
				where1 = ff1.TYPE.in(lexemeFreeformTypes).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				SelectHavingStep<Record1<Long>> selectLexemeFreeforms = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.MEANING_ID);

				// lexeme usage translation, definition select
				String[] lexemeFreeformSubTypes = new String[] {FreeformType.USAGE_TRANSLATION.name(), FreeformType.USAGE_DEFINITION.name()};
				where1 = ff1.TYPE.in(lexemeFreeformSubTypes).and(lff1.FREEFORM_ID.eq(ff1.PARENT_ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				SelectHavingStep<Record1<Long>> selectLexemeFreeformSubTypes = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.MEANING_ID);

				// union all
				Table<Record1<Long>> a1 = selectWord
						.unionAll(selectDefinition)
						.unionAll(selectMeaningFreeforms)
						.unionAll(selectDefinitionFreeforms)
						.unionAll(selectLexemeFreeforms)
						.unionAll(selectLexemeFreeformSubTypes)
						.asTable("a1");

				wherem = wherem.andExists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID)));
			}
		}

		Table<Record1<Long>> w = DSL.select(w1.ID).from(w1).where(wherew).asTable("w");
		Table<Record1<Long>> m = DSL.select(m1.ID).from(m1).where(wherem).asTable("m");
		Condition wherelds = composeLexemeDatasetsCondition(l, searchDatasetsRestriction);
		Condition wheremlw = l.MEANING_ID.eq(m.field("id", Long.class))
				.and(l.WORD_ID.eq(w.field("id", Long.class)))
				.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(wherelds);

		Table<Record3<Long, Long, Long[]>> mm = DSL
				.select(
						m.field("id", Long.class),
						DSL.field("(array_agg(w.id order by l.order_by)) [1]", Long.class).as("order_by_word_id"),
						DSL.arrayAgg(w.field("id", Long.class)).as("match_word_ids"))
				.from(m, l, w)
				.where(wheremlw)
				.groupBy(m.field("id"))
				.asTable("m");

		return mm;
	}

	private int executeCountMeanings(Table<Record3<Long, Long, Long[]>> m) {

		return create
				.fetchCount(DSL
						.select(m.field("id"))
						.from(m));
	}

	private int executeCountWords(Table<Record3<Long, Long, Long[]>> m, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang) {

		Lexeme lo = LEXEME.as("lo");
		Word wo = WORD.as("wo");

		Condition wherewo = wo.ID.eq(lo.WORD_ID);
		if (StringUtils.isNotBlank(resultLang)) {
			wherewo = wherewo.and(wo.LANG.eq(resultLang));
		}

		Condition wherelods = composeLexemeDatasetsCondition(lo, searchDatasetsRestriction);

		return create
				.fetchCount(DSL
						.select(wo.ID)
						.from(m
								.innerJoin(lo).on(
										lo.MEANING_ID.eq(m.field("id", Long.class))
										.and(lo.TYPE.eq(LEXEME_TYPE_PRIMARY))
										.and(wherelods))
								.innerJoin(wo).on(wherewo)));
	}

	// common search

	private List<TermMeaning> executeFetch(
			Table<Record3<Long, Long, Long[]>> m,
			SearchDatasetsRestriction searchDatasetsRestriction,
			String resultLang, boolean fetchAll, int offset) {

		Lexeme lo = LEXEME.as("lo");
		Word wo = WORD.as("wo");
		Paradigm po = PARADIGM.as("po");
		Form fo = FORM.as("fo");
		Paradigm pm = PARADIGM.as("pm");
		Form fm = FORM.as("fm");
		Lexeme lds = LEXEME.as("lds");
		Dataset ds = DATASET.as("ds");
		Freeform ff = FREEFORM.as("ff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		WordWordType wt = WORD_WORD_TYPE.as("wt");
		Language wol = LANGUAGE.as("wol");

		SelectHavingStep<Record1<String[]>> wts = DSL
				.select(DSL.arrayAgg(wt.WORD_TYPE_CODE))
				.from(wt).where(wt.WORD_ID.eq(wo.ID))
				.groupBy(wt.WORD_ID);

		Table<Record3<Long, String, Long>> wdsf = DSL
				.selectDistinct(lds.WORD_ID, lds.DATASET_CODE, ds.ORDER_BY)
				.from(lds, ds)
				.where(
						lds.WORD_ID.eq(wo.ID)
						.and(lds.MEANING_ID.eq(m.field("id", Long.class)))
						.and(lds.DATASET_CODE.eq(ds.CODE)))
				.asTable("wdsf");

		SelectJoinStep<Record1<String[]>> wds = DSL
				.select(DSL.arrayAgg(wdsf.field("dataset_code", String.class)).orderBy(wdsf.field("order_by")))
				.from(wdsf);

		Condition wherelods = composeLexemeDatasetsCondition(lo, searchDatasetsRestriction);

		Condition wherewo = wo.ID.eq(lo.WORD_ID);
		if (StringUtils.isNotBlank(resultLang)) {
			wherewo = wherewo.and(wo.LANG.eq(resultLang));
		}

		Table<Record12<Long,Long,String,Long,String,Integer,String,String[],String[],Boolean,Long,Long>> mm = DSL
				.select(
						m.field("id", Long.class),
						pm.WORD_ID.as("order_by_word_id"),
						fm.VALUE.as("order_by_word"),
						wo.ID.as("word_id"),
						fo.VALUE.as("word"),
						wo.HOMONYM_NR,
						wo.LANG,
						DSL.field(wts).as("word_type_codes"),
						DSL.field(wds).as("dataset_codes"),
						DSL.field(wo.ID.eq(DSL.any(m.field("match_word_ids", Long[].class)))).as("matching_word"),
						wol.ORDER_BY.as("lang_order_by"),
						lo.ORDER_BY.as("lex_order_by")
						)
				.from(m
						.innerJoin(pm).on(pm.WORD_ID.eq(m.field("order_by_word_id", Long.class)))
						.innerJoin(fm).on(fm.PARADIGM_ID.eq(pm.ID).and(fm.MODE.eq(FormMode.WORD.name())))
						.leftOuterJoin(lo).on(
								lo.MEANING_ID.eq(m.field("id", Long.class))
								.and(lo.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(wherelods))
						.leftOuterJoin(wo).on(wherewo)
						.leftOuterJoin(po).on(po.WORD_ID.eq(wo.ID))
						.leftOuterJoin(fo).on(fo.PARADIGM_ID.eq(po.ID).and(fo.MODE.eq(FormMode.WORD.name())))
						.leftOuterJoin(wol).on(wol.CODE.eq(wo.LANG))
						)
				.asTable("m");

		SelectHavingStep<Record1<String[]>> c = DSL
				.select(DSL.arrayAgg(ff.VALUE_TEXT))
				.from(mff, ff)
				.where(
						mff.MEANING_ID.eq(mm.field("id", Long.class))
								.and(mff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.CONCEPT_ID.name())))
				.groupBy(mff.MEANING_ID);

		Field<TypeTermMeaningWordRecord[]> mw = DSL
				.field("array_agg(row ("
						+ "m.word_id,"
						+ "m.word,"
						+ "m.homonym_nr,"
						+ "m.lang,"
						+ "m.word_type_codes,"
						+ "m.dataset_codes,"
						+ "m.matching_word"
						+ ")::type_term_meaning_word "
						+ "order by "
						+ "m.lang_order_by,"
						+ "m.lex_order_by)", TypeTermMeaningWordRecord[].class);

		int limit = MAX_RESULTS_LIMIT;
		if (fetchAll) {
			limit = Integer.MAX_VALUE;
		}

		/*
		 * meaning words of same homonym and same meaning for different datasets are repeating
		 * which is cleaned programmatically at ui conversion
		 */
		return create
				.select(
						mm.field("id", Long.class).as("meaning_id"),
						DSL.field(c).as("concept_ids"),
						mw.as("meaning_words"))
				.from(mm)
				.groupBy(
						mm.field("id"),
						mm.field("order_by_word_id"),
						mm.field("order_by_word"))
				.orderBy(mm.field("order_by_word"))
				.limit(limit)
				.offset(offset)
				.fetchInto(TermMeaning.class);
	}

	// getters

	public eki.ekilex.data.Meaning getMeaning(Long meaningId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = composeLexemeDatasetsCondition(LEXEME, searchDatasetsRestriction);

		return create
				.select(
						MEANING.ID.as("meaning_id"),
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"))
				.from(MEANING, LEXEME)
				.where(
						MEANING.ID.eq(meaningId)
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(dsWhere))
				.groupBy(MEANING.ID)
				.fetchSingleInto(eki.ekilex.data.Meaning.class);
	}

	public eki.ekilex.data.Lexeme getLexeme(Long lexemeId) {

		Lexeme l = LEXEME.as("l");
		LexemeFrequency lf = LEXEME_FREQUENCY.as("lf");
		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");

		Field<String[]> lfreq = DSL
				.select(DSL.arrayAgg(DSL.concat(
						lf.SOURCE_NAME, DSL.val(" - "),
						lf.RANK, DSL.val(" - "),
						lf.VALUE)))
				.from(lf)
				.where(lf.LEXEME_ID.eq(l.ID))
				.groupBy(lf.LEXEME_ID)
				.asField();

		return create
				.select(
						w.ID.as("word_id"),
						DSL.field("array_to_string(array_agg(distinct f.value_prese), ',', '*')", String.class).as("word"),
						w.HOMONYM_NR,
						w.LANG.as("word_lang"),
						w.GENDER_CODE.as("word_gender_code"),
						l.ID.as("lexeme_id"),
						l.MEANING_ID,
						l.DATASET_CODE.as("dataset"),
						l.LEVEL1,
						l.LEVEL2,
						l.LEVEL3,
						l.FREQUENCY_GROUP_CODE.as("lexeme_frequency_group_code"),
						lfreq.as("lexeme_frequencies"),
						l.VALUE_STATE_CODE.as("lexeme_value_state_code"),
						l.PROCESS_STATE_CODE.as("lexeme_process_state_code"),
						l.COMPLEXITY.as("lexeme_complexity"),
						l.ORDER_BY)
				.from(f, p, w, l)
				.where(
						l.ID.eq(lexemeId)
								.and(l.WORD_ID.eq(w.ID))
								.and(p.WORD_ID.eq(w.ID))
								.and(f.PARADIGM_ID.eq(p.ID))
								.and(f.MODE.eq(FormMode.WORD.name())))
				.groupBy(l.ID, w.ID)
				.orderBy(w.ID, l.DATASET_CODE, l.LEVEL1, l.LEVEL2, l.LEVEL3)
				.fetchSingleInto(eki.ekilex.data.Lexeme.class);
	}

	public String getMeaningFirstWord(Long meaningId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = composeLexemeDatasetsCondition(LEXEME, searchDatasetsRestriction);

		return create
				.select(FORM.VALUE)
				.from(FORM, PARADIGM, LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(PARADIGM.WORD_ID.eq(LEXEME.WORD_ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
								.and(dsWhere))
				.orderBy(LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3, LEXEME.WORD_ID, FORM.ID)
				.limit(1)
				.fetchSingleInto(String.class);
	}

	public List<eki.ekilex.data.Meaning> getMeaningsOfJoinCandidates(String searchFilter, List<String> userPrefDatasetCodes,
			List<String> userPermDatasetCodes, Long excludedMeaningId) {

		String maskedSearchFilter = searchFilter.replace("*", "%").replace("?", "_").toLowerCase();

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");

		Condition whereFormValue;
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			whereFormValue = f.VALUE.lower().like(maskedSearchFilter);
		} else {
			whereFormValue = f.VALUE.lower().equal(maskedSearchFilter);
		}

		Table<Record1<Long>> mid = DSL
				.selectDistinct(m.ID.as("meaning_id"))
				.from(m, l, w, p, f)
				.where(
						m.ID.ne(excludedMeaningId)
								.and(l.MEANING_ID.eq(m.ID))
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l.DATASET_CODE.in(userPrefDatasetCodes))
								.and(w.ID.eq(l.WORD_ID))
								.and(p.WORD_ID.eq(w.ID))
								.and(f.PARADIGM_ID.eq(p.ID))
								.and(f.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()))
								.and(whereFormValue))
				.asTable("mid");

		return create
				.select(
						MEANING.ID.as("meaning_id"),
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"))
				.from(MEANING, LEXEME, mid)
				.where(
						MEANING.ID.eq(mid.field("meaning_id", Long.class))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(LEXEME.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
										.or(LEXEME.DATASET_CODE.in(userPermDatasetCodes))))
				.groupBy(MEANING.ID)
				.fetchInto(eki.ekilex.data.Meaning.class);
	}

}
