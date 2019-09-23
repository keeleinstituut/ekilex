package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_FREQUENCY;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.SelectHavingStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TermMeaningWordTuple;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeFrequency;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

@Component
public class TermSearchDbService extends AbstractSearchDbService {

	private static final String NUMERIC_VALUE_PATTERN = "^([0-9]+[.]?[0-9]*|[.][0-9]+)$";

	@Autowired
	private DSLContext create;

	// simple search

	public List<TermMeaningWordTuple> getMeanings(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang, boolean fetchAll,
			int offset) {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, searchDatasetsRestriction);
		return executeFetch(m1, meaningCondition, resultLang, fetchAll, offset);
	}

	public int countMeanings(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, searchDatasetsRestriction);
		return executeCountMeanings(m1, meaningCondition);
	}

	public int countWords(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang) {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, searchDatasetsRestriction);
		return executeCountWords(m1, meaningCondition, resultLang);
	}

	private Condition composeMeaningCondition(Meaning m1, String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) {

		String maskedSearchFilter = searchFilter.replace("*", "%").replace("?", "_").toLowerCase();

		Lexeme l1 = LEXEME.as("l1");
		Word w1 = WORD.as("w1");
		Paradigm p1 = PARADIGM.as("p1");

		Condition where1 = FORM.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name());
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			where1 = where1.and(FORM.VALUE.lower().like(maskedSearchFilter));
		} else {
			where1 = where1.and(FORM.VALUE.lower().equal(maskedSearchFilter));
		}
		Table<Record1<Long>> f1 = DSL
				.select(FORM.PARADIGM_ID)
				.from(FORM)
				.where(where1)
				.asTable("f1");

		Condition where2 = f1.field("paradigm_id", Long.class).eq(p1.ID).and(p1.WORD_ID.eq(w1.ID)).and(l1.WORD_ID.eq(w1.ID)).and(l1.MEANING_ID.eq(m1.ID));
		Condition where3 = composeLexemeDatasetsCondition(l1, searchDatasetsRestriction);
		Condition where = DSL.exists(DSL.select(l1.ID).from(f1, p1, w1, l1).where(where2.and(where3)));
		return where;
	}

	// detail search

	public List<TermMeaningWordTuple> getMeanings(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang,
			boolean fetchAll, int offset) throws Exception {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, searchDatasetsRestriction);
		return executeFetch(m1, meaningCondition, resultLang, fetchAll, offset);
	}

	public int countMeanings(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, searchDatasetsRestriction);
		return executeCountMeanings(m1, meaningCondition);
	}

	public int countWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, String resultLang) throws Exception {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, searchDatasetsRestriction);
		return executeCountWords(m1, meaningCondition, resultLang);
	}

	private Condition composeMeaningCondition(Meaning m1, SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		List<SearchCriterionGroup> criteriaGroups = searchFilter.getCriteriaGroups();

		Lexeme m1ds = LEXEME.as("m1ds");
		Condition dsFiltWhere = composeLexemeDatasetsCondition(m1ds, searchDatasetsRestriction);
		Condition where = DSL.exists(DSL.select(m1ds.ID).from(m1ds).where(m1ds.MEANING_ID.eq(m1.ID).and(dsFiltWhere)));

		for (SearchCriterionGroup searchCriterionGroup : criteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				Word w1 = WORD.as("w1");
				Lexeme l1 = LEXEME.as("l1");
				Condition where1 = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name())
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID))
						.and(l1.MEANING_ID.eq(m1.ID));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1, false);
				where1 = applyLexemeSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, l1.ID, where1);
				where1 = applyLexemeSourceFilters(SearchKey.SOURCE_REF, searchCriteria, l1.ID, where1);

				where = where.and(DSL.exists(DSL.select(w1.ID).from(f1, p1, w1, l1).where(where1)));

				Condition where2 = l1.MEANING_ID.eq(m1.ID).and(l1.WORD_ID.eq(w1.ID));
				where2 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where2);
				where = applyTermWordLifecycleLogFilters(searchCriteria, l1, w1, where2, where);

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				// this type of search is not actually available

				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				Word w1 = WORD.as("w1");
				Lexeme l1 = LEXEME.as("l1");
				Condition where1 = f1.PARADIGM_ID.eq(p1.ID)
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID))
						.and(l1.MEANING_ID.eq(m1.ID));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1, false);

				where = where.and(DSL.exists(DSL.select(w1.ID).from(f1, p1, w1, l1).where(where1)));

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Condition where1 = l1.MEANING_ID.eq(m1.ID);

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where = applyTermMeaningLifecycleLogFilters(searchCriteria, l1, where1, where);
				where = applyDomainFilters(searchCriteria, m1, where);

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Definition d1 = DEFINITION.as("d1");
				Condition where1 = d1.MEANING_ID.eq(m1.ID)
						.and(l1.MEANING_ID.eq(m1.ID));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, d1.LANG, where1, false);
				where1 = applyDefinitionSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, d1.ID, where1);
				where1 = applyDefinitionSourceFilters(SearchKey.SOURCE_REF, searchCriteria, d1.ID, where1);

				where = where.and(DSL.exists(DSL.select(d1.ID).from(d1, l1).where(where1)));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform u1 = FREEFORM.as("u1");

				Condition where1 = l1.MEANING_ID.eq(m1.ID)
						.and(l1ff.LEXEME_ID.eq(l1.ID))
						.and(l1ff.FREEFORM_ID.eq(u1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE_TEXT, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, u1.LANG, where1, false);
				where1 = applyFreeformSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, u1.ID, where1);
				where1 = applyFreeformSourceFilters(SearchKey.SOURCE_REF, searchCriteria, u1.ID, where1);

				where = where.and(DSL.exists(DSL.select(u1.ID).from(l1, l1ff, u1).where(where1)));

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
				Table<Record2<Long, Long>> lff2 = DSL.select(l3.MEANING_ID, lff3.FREEFORM_ID).from(l3, lff3).where(lff3.LEXEME_ID.eq(l3.ID)).asTable("lff2");

				// notes owners joined
				Table<Record1<Long>> n1 = DSL
						.select(DSL.coalesce(mff2.MEANING_ID, DSL.coalesce(dff2.field("meaning_id"), lff2.field("meaning_id"))).as("meaning_id"))
						.from(n2
								.leftOuterJoin(mff2).on(mff2.FREEFORM_ID.eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(dff2).on(dff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(lff2).on(lff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class))))
						.asTable("n1");

				where = where.and(DSL.exists(DSL.select(n1.field("meaning_id")).from(n1).where(n1.field("meaning_id", Long.class).eq(m1.ID))));

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				MeaningFreeform m1ff = MEANING_FREEFORM.as("m1ff");
				Freeform c1 = FREEFORM.as("c1");

				Condition where1 = m1ff.MEANING_ID.eq(m1.ID)
						.and(m1ff.FREEFORM_ID.eq(c1.ID))
						.and(c1.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				where1 = applyValueFilters(SearchKey.ID, searchCriteria, c1.VALUE_TEXT, where1, false);

				where = where.and(DSL.exists(DSL.select(c1.ID).from(m1ff, c1).where(where1)));

			} else if (SearchEntity.CLUELESS.equals(searchEntity)) {

				Definition d1 = DEFINITION.as("d1");
				Lexeme l1 = LEXEME.as("l1");
				Lexeme lds = LEXEME.as("lds");
				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				Word w1 = WORD.as("w1");
				MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
				DefinitionFreeform dff1 = DEFINITION_FREEFORM.as("dff1");
				LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
				Freeform ff1 = FREEFORM.as("ff1");
				Condition where1, where2, whereDs;

				// word select
				where2 = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name())
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID));
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
				where1 = ff1.TYPE.in(lexemeFreeformTypes).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID));
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				SelectHavingStep<Record1<Long>> selectLexemeFreeforms = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.MEANING_ID);

				// lexeme usage translation, definition select
				String[] lexemeFreeformSubTypes = new String[] {FreeformType.USAGE_TRANSLATION.name(), FreeformType.USAGE_DEFINITION.name()};
				where1 = ff1.TYPE.in(lexemeFreeformSubTypes).and(lff1.FREEFORM_ID.eq(ff1.PARENT_ID)).and(lff1.LEXEME_ID.eq(l1.ID));
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

				where = where.and(DSL.exists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID))));
			}
		}
		return where;
	}

	private int executeCountMeanings(Meaning m1, Condition meaningCondition) {

		Table<Record1<Long>> m = DSL
				.select(m1.ID.as("meaning_id"))
				.from(m1)
				.where(meaningCondition)
				.asTable("m");

		int count = create.fetchCount(DSL.selectDistinct(m.field("meaning_id")).from(m));
		return count;
	}

	private int executeCountWords(Meaning m1, Condition meaningCondition, String resultLang) {

		Lexeme l1 = LEXEME.as("l1");
		Word w1 = WORD.as("w1");

		Table<Record1<Long>> m = DSL
				.select(m1.ID.as("meaning_id"))
				.from(m1)
				.where(meaningCondition)
				.asTable("m");

		Condition where3 = l1.WORD_ID.eq(w1.ID);
		if (StringUtils.isNotBlank(resultLang)) {
			where3 = where3.and(w1.LANG.eq(resultLang));
		}
		Table<Record2<Long, Long>> w = DSL
				.select(l1.MEANING_ID, l1.WORD_ID)
				.from(w1, l1)
				.where(where3)
				.asTable("w");

		int count = create
				.fetchCount(DSL.selectDistinct(w.field("word_id"))
						.from(m.innerJoin(w).on(w.field("meaning_id", Long.class).eq(m.field("meaning_id", Long.class)))));
		return count;
	}

	// common search

	private List<TermMeaningWordTuple> executeFetch(Meaning m1, Condition meaningCondition, String resultLang, boolean fetchAll, int offset) {

		int limit = MAX_RESULTS_LIMIT;
		if (fetchAll) {
			limit = Integer.MAX_VALUE;
		}

		Lexeme l1 = LEXEME.as("l1");
		Word w1 = WORD.as("w1");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		Freeform ff = FREEFORM.as("ff");

		Table<Record1<Long>> m = DSL
				.select(m1.ID.as("meaning_id"))
				.from(m1)
				.where(meaningCondition)
				.asTable("m");

		Condition where3 = l1.WORD_ID.eq(w1.ID);
		if (StringUtils.isNotBlank(resultLang)) {
			where3 = where3.and(w1.LANG.eq(resultLang));
		}
		Table<Record3<Long, Long, Long>> w = DSL
				.select(l1.MEANING_ID, l1.WORD_ID, l1.ORDER_BY)
				.from(w1, l1)
				.where(where3)
				.asTable("w");

		Table<Record2<Long, String>> c = DSL
				.select(
						mff.MEANING_ID,
						ff.VALUE_TEXT.as("concept_id"))
				.from(mff, ff)
				.where(
						mff.FREEFORM_ID.eq(ff.ID)
								.and(ff.TYPE.eq(FreeformType.CONCEPT_ID.name()))
								.and(ff.VALUE_TEXT.likeRegex(NUMERIC_VALUE_PATTERN)))
				.asTable("c");

		Table<Record3<Long, Long, Long>> mw = DSL
				.select(
						m.field("meaning_id", Long.class),
						DSL.field("(array_agg(w.word_id order by w.order_by)) [1]", Long.class).as("word_id"),
						DSL.field("(array_agg(c.concept_id order by c.concept_id)) [1]", Long.class).as("concept_id"))
				.from(m
						.leftOuterJoin(w).on(w.field("meaning_id", Long.class).eq(m.field("meaning_id", Long.class)))
						.leftOuterJoin(c).on(c.field("meaning_id", Long.class).eq(m.field("meaning_id", Long.class))))
				.groupBy(m.fields("meaning_id"))
				.asTable("mw");

		Paradigm mwp = PARADIGM.as("mwp");
		Word mwv = WORD.as("mwv");
		Form mwf = FORM.as("mwf");

		Table<Record6<Long, String, Long, String, Integer, String>> mmw = DSL
				.select(
						mw.field("meaning_id", Long.class),
						mw.field("concept_id", String.class),
						mw.field("word_id", Long.class),
						DSL.field("(array_agg(distinct mwf.value)) [1]", String.class).as("word"),
						mwv.HOMONYM_NR,
						mwv.LANG.as("word_lang"))
				.from(mw
						.leftOuterJoin(mwv).on(mwv.ID.eq(mw.field("word_id", Long.class)))
						.leftOuterJoin(mwp).on(mwp.WORD_ID.eq(mwv.ID))
						.leftOuterJoin(mwf).on(mwf.PARADIGM_ID.eq(mwp.ID).and(mwf.MODE.eq(FormMode.WORD.name()))))
				.groupBy(
						mw.field("meaning_id"),
						mw.field("concept_id"),
						mw.field("word_id"),
						mwv.ID)
				.orderBy(
						DSL.field(DSL.name("word")),
						mwv.HOMONYM_NR,
						DSL.field("concept_id::int"))
				.limit(limit)
				.offset(offset)
				.asTable("mmw");

		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		Condition where4 = l2.WORD_ID.eq(w2.ID).and(p2.WORD_ID.eq(w2.ID)).and(f2.PARADIGM_ID.eq(p2.ID)).and(f2.MODE.eq(FormMode.WORD.name()));
		if (StringUtils.isNotBlank(resultLang)) {
			where4 = where4.and(w2.LANG.eq(resultLang));
		}

		Table<Record6<Long, Long, String, Integer, String, Long>> mow = DSL
				.select(
						l2.MEANING_ID,
						l2.WORD_ID,
						f2.VALUE.as("word"),
						w2.HOMONYM_NR,
						w2.LANG.as("word_lang"),
						l2.ORDER_BY)
				.from(l2, w2, p2, f2)
				.where(where4)
				.asTable("mow");

		Lexeme l = LEXEME.as("l");

		Field<String> mmwds = DSL
				.select(DSL.field("array_to_string(array_agg(distinct l.dataset_code order by l.dataset_code), ', ', '*')", String.class))
				.from(l)
				.where(l.WORD_ID.eq(mmw.field("word_id", Long.class)))
				.groupBy(mmw.field("word_id"))
				.asField("main_word_dataset_codes_wrapup");

		Field<String> mowds = DSL
				.select(DSL.field("array_to_string(array_agg(distinct l.dataset_code order by l.dataset_code), ', ', '*')", String.class))
				.from(l)
				.where(l.WORD_ID.eq(mow.field("word_id", Long.class)))
				.groupBy(mow.field("word_id"))
				.asField("other_word_dataset_codes_wrapup");

		List<TermMeaningWordTuple> result = create
				.select(
						mmw.field("meaning_id", Long.class),
						mmw.field("concept_id", String.class),
						mmw.field("word_id", Long.class).as("main_word_id"),
						mmw.field("word", String.class).as("main_word"),
						mmw.field("homonym_nr", Integer.class).as("main_word_homonym_nr"),
						mmw.field("word_lang", String.class).as("main_word_lang"),
						mmwds,
						mow.field("word_id", Long.class).as("other_word_id"),
						mow.field("word", String.class).as("other_word"),
						mow.field("homonym_nr", Integer.class).as("other_word_homonym_nr"),
						mow.field("word_lang", String.class).as("other_word_lang"),
						mow.field("order_by", Long.class).as("other_word_order_by"),
						mowds)
				.from(mmw
						.leftOuterJoin(mow)
						.on(mow.field("meaning_id", Long.class).eq(mmw.field("meaning_id", Long.class))
								.and(mow.field("word_id", Long.class).ne(mmw.field("word_id", Long.class)))))
				.fetchInto(TermMeaningWordTuple.class);

		return result;
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
								.and(PARADIGM.WORD_ID.eq(LEXEME.WORD_ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
								.and(dsWhere))
				.orderBy(LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3, LEXEME.WORD_ID, FORM.ID)
				.limit(1)
				.fetchSingleInto(String.class);
	}

	public List<Long> getMeaningIds(String searchFilter, SearchDatasetsRestriction userPrefDatasetCodes) {

		Meaning m1 = MEANING.as("m1");
		Condition meaningCondition = composeMeaningCondition(m1, searchFilter, userPrefDatasetCodes);
		return create
				.select(m1.ID.as("meaning_id"))
				.from(m1)
				.where(meaningCondition)
				.fetchInto(Long.class);
	}

}
