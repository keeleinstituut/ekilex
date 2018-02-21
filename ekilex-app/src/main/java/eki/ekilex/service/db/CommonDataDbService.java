package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LANG;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Map;

import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.data.SearchCriterionGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

@Component
public class CommonDataDbService {

	public static final int MAX_RESULTS_LIMIT = 50;

	@Autowired
	private DSLContext create;

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public Result<Record2<String,String>> getDatasets() {
		return create.select(DATASET.CODE, DATASET.NAME).from(DATASET).fetch();
	}

	public Map<String, String> getLanguagesMap() {
		return create.select().from(LANG).fetchMap(LANG.CODE, LANG.VALUE);
	}

	public Result<Record2<String,String>> getLanguages() {
		return create.select(LANG.CODE, LANG.VALUE).from(LANG).fetch();
	}

	public Result<Record> findWords(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) throws Exception {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(searchCriteriaGroups, word, form);

		return execute(word, paradigm, form, where, datasets, fetchAll);
	}

	public int countWords(SearchFilter searchFilter, List<String> datasets) throws Exception {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(searchFilter.getCriteriaGroups(), word, form);

		return count(word, paradigm, form, where, datasets);
	}

	private Condition createCondition(List<SearchCriterionGroup> searchCriterionGroups, Word word, Form form) throws Exception {

		Condition where = DSL.trueCondition();

		for (SearchCriterionGroup searchCriterionGroup : searchCriterionGroups) {

			List<SearchCriterion> searchCriterions = searchCriterionGroup.getSearchCriteria();
			if (searchCriterions.isEmpty()) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			List<SearchCriterion> valueCriterions = searchCriterions.stream()
					.filter(c -> c.getSearchKey().equals(SearchKey.VALUE) && c.getSearchValue() != null)
					.collect(toList());
			List<SearchCriterion> languageCriterions = searchCriterions.stream()
					.filter(c -> c.getSearchKey().equals(SearchKey.LANGUAGE) && c.getSearchValue() != null && isNotBlank(c.getSearchValue().toString()))
					.collect(toList());
			List<SearchCriterion> idCriterions = searchCriterions.stream()
					.filter(c -> c.getSearchKey().equals(SearchKey.ID) && c.getSearchValue() != null)
					.collect(toList());

			if (SearchEntity.WORD.equals(searchEntity)) {

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where = applySearchValueFilter(searchValueStr, searchOperand, form.VALUE, where);
				}
				for (SearchCriterion criterion : languageCriterions) {
					where = where.and(word.LANG.eq(criterion.getSearchValue().toString()));
				}

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Paradigm p2 = PARADIGM.as("p2");
				Form f2 = FORM.as("f2");
				Condition where2 = f2.PARADIGM_ID.eq(p2.ID).and(p2.WORD_ID.eq(word.ID));
				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, f2.VALUE, where2);
				}
				where = where.and(DSL.exists(DSL.select(f2.ID).from(f2, p2).where(where2)));
				for (SearchCriterion criterion : languageCriterions) {
					where = where.and(word.LANG.eq(criterion.getSearchValue().toString()));
				}

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				Definition d2 = DEFINITION.as("d2");
				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(m2.ID)).and(d2.MEANING_ID.eq(m2.ID));
				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, d2.VALUE, where2);
				}
				for (SearchCriterion criterion : languageCriterions) {
					where2 = where2.and(d2.LANG.eq(criterion.getSearchValue().toString()));
				}
				where = where.and(DSL.exists(DSL.select(d2.ID).from(l2, m2, d2).where(where2)));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				LexemeFreeform l2ff = LEXEME_FREEFORM.as("l2ff");
				Freeform rect2 = FREEFORM.as("rect2");
				Freeform um2 = FREEFORM.as("um2");
				Freeform u2 = FREEFORM.as("u2");

				Condition where2 =
						l2.WORD_ID.eq(word.ID)
								.and(l2ff.LEXEME_ID.eq(l2.ID))
								.and(l2ff.FREEFORM_ID.eq(rect2.ID))
								.and(rect2.TYPE.eq(FreeformType.GOVERNMENT.name()))
								.and(um2.PARENT_ID.eq(rect2.ID))
								.and(um2.TYPE.eq(FreeformType.USAGE_MEANING.name()))
								.and(u2.PARENT_ID.eq(um2.ID))
								.and(u2.TYPE.eq(FreeformType.USAGE.name()));

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, u2.VALUE_TEXT, where2);
				}
				for (SearchCriterion criterion : languageCriterions) {
					where2 = where2.and(u2.LANG.eq(criterion.getSearchValue().toString()));
				}

				where = where.and(DSL.exists(DSL.select(u2.ID).from(l2, l2ff, rect2, um2, u2).where(where2)));

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				MeaningFreeform m2ff = MEANING_FREEFORM.as("m2ff");
				Freeform concept = FREEFORM.as("concept");

				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(m2.ID)).and(m2ff.MEANING_ID.eq(m2.ID)).and(m2ff.FREEFORM_ID.eq(concept.ID)).and(concept.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				for (SearchCriterion criterion : idCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, concept.VALUE_TEXT, where2);
				}

				where = where.and(DSL.exists(DSL.select(concept.ID).from(l2, m2, m2ff, concept).where(where2)));
			}
		}
		return where;
	}

	private Condition applySearchValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition) throws Exception {

		if (SearchOperand.EQUALS.equals(searchOperand)) {
			condition = condition.and(searchField.equalIgnoreCase(searchValueStr));
		} else if (SearchOperand.STARTS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().startsWith(searchValueStr));
		} else if (SearchOperand.ENDS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().endsWith(searchValueStr));
		} else if (SearchOperand.CONTAINS.equals(searchOperand)) {
			condition = condition.and(searchField.lower().contains(searchValueStr));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	public Result<Record> findWords(String wordWithMetaCharacters, List<String> datasets, boolean fetchAll) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(wordWithMetaCharacters, form);

		return execute(word, paradigm, form, where, datasets, fetchAll);
	}

	public int countWords(String searchFilter, List<String> datasets) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(searchFilter, form);

		return count(word, paradigm, form, where, datasets);
	}

	private Condition createCondition(String wordWithMetaCharacters, Form form) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");

		Condition where = DSL.trueCondition();

		if (StringUtils.containsAny(theFilter, '%', '_')) {
			where = where.and(form.VALUE.likeIgnoreCase(theFilter));
		} else {
			where = where.and(form.VALUE.equalIgnoreCase(theFilter));
		}
		return where;
	}

	private Result<Record> execute(Word word, Paradigm paradigm, Form form, Condition where, List<String> datasets, boolean fetchAll) {

		Field<String> wf = DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')").cast(String.class);

		Table<Record> from = word.join(paradigm.join(form).on(form.PARADIGM_ID.eq(paradigm.ID).and(form.IS_WORD.isTrue()))).on(paradigm.WORD_ID.eq(word.ID));

		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme ld = LEXEME.as("ld");
			where = where.andExists(
						DSL.select(ld.ID).from(ld)
						.where((ld.WORD_ID.eq(word.ID))
						.and(ld.DATASET_CODE.in(datasets))));
		}

		Table<Record4<Long,String,Integer,String>> w = DSL
				.select(
					word.ID.as("word_id"),
					wf.as("word"),
					word.HOMONYM_NR,
					word.LANG)
				.from(from)
				.where(where)
				.groupBy(word.ID)
				.asTable("w");

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(LEXEME.DATASET_CODE))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(w.field("word_id").cast(Long.class)))
				.groupBy(w.field("word_id")));

		Table<?> ww = DSL
				.select(
					w.field("word_id"),
					w.field("word"),
					w.field("homonym_nr"),
					w.field("lang"),
					dscf.as("dataset_codes")
						)
				.from(w)
				.orderBy(
					w.field("word"),
					w.field("homonym_nr"))
				.asTable("ww");

		if (fetchAll) {
			return create.select(ww.fields()).from(ww).fetch();
		} else {
			return create.select(ww.fields()).from(ww).limit(MAX_RESULTS_LIMIT).fetch();
		}
	}

	private int count(Word word, Paradigm paradigm, Form form, Condition where, List<String> datasets) {

		Table<Record> from = word.join(paradigm.join(form).on(form.PARADIGM_ID.eq(paradigm.ID).and(form.IS_WORD.isTrue()))).on(paradigm.WORD_ID.eq(word.ID));

		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme ld = LEXEME.as("ld");
			where = where.andExists(
					DSL.select(ld.ID).from(ld)
							.where((ld.WORD_ID.eq(word.ID))
									.and(ld.DATASET_CODE.in(datasets))));
		}

		Table<Record1<Long>> w = create
				.select(
					word.ID.as("word_id")
				)
				.from(from)
				.where(where)
				.groupBy(word.ID)
				.asTable("w");

		Table<?> ww = create
				.select(
					w.field("word_id")
				)
				.from(w)
				.asTable("ww");

		return create.fetchCount(ww);
	}
}
