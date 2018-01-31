package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchKey;
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

	private static final int MAX_RESULTS_LIMIT = 50;

	@Autowired
	private DSLContext create;

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public Result<Record2<String,String>> getDatasets() {

		return create.select(DATASET.CODE, DATASET.NAME).from(DATASET).fetch();
	}

	public Result<Record> findWords(SearchFilter searchFilter, List<String> datasets) throws Exception {

		List<SearchCriterion> searchCriteria = searchFilter.getSearchCriteria();

		Word w1 = WORD.as("w1");
		Paradigm p1 = PARADIGM.as("p1");
		Form f1 = FORM.as("f1");

		Table<Record> from1 = w1.join(p1.join(f1).on(f1.PARADIGM_ID.eq(p1.ID).and(f1.IS_WORD.isTrue()))).on(p1.WORD_ID.eq(w1.ID));

		Condition where1 = DSL.trueCondition();

		for (SearchCriterion searchCriterion : searchCriteria) {

			SearchKey searchKey = searchCriterion.getSearchKey();
			SearchOperand searchOperand = searchCriterion.getSearchOperand();
			Object searchValue = searchCriterion.getSearchValue();
			if (searchValue == null) {
				continue;
			}
			String searchValueStr = searchValue.toString();
			searchValueStr = StringUtils.lowerCase(searchValueStr);

			if (SearchKey.WORD_VALUE.equals(searchKey)) {

				where1 = applySearchValueFilter(searchValueStr, searchOperand, f1.VALUE, where1);

			} else if (SearchKey.FORM_VALUE.equals(searchKey)) {

				Paradigm p2 = PARADIGM.as("p2");
				Form f2 = FORM.as("f2");
				Condition where2 = f2.PARADIGM_ID.eq(p2.ID).and(p2.WORD_ID.eq(w1.ID));
				where2 = applySearchValueFilter(searchValueStr, searchOperand, f2.VALUE, where2);
				where1 = where1.and(DSL.exists(DSL.select(f2.ID).from(f2, p2).where(where2)));

			} else if (SearchKey.DEFINITION_VALUE.equals(searchKey)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				Definition d2 = DEFINITION.as("d2");
				Condition where2 = l2.WORD_ID.eq(w1.ID).and(l2.MEANING_ID.eq(m2.ID)).and(d2.MEANING_ID.eq(m2.ID));
				where2 = applySearchValueFilter(searchValueStr, searchOperand, d2.VALUE, where2);
				where1 = where1.and(DSL.exists(DSL.select(d2.ID).from(l2, m2, d2).where(where2)));

			} else if (SearchKey.USAGE_VALUE.equals(searchKey)) {

				Lexeme l2 = LEXEME.as("l2");
				LexemeFreeform l2ff = LEXEME_FREEFORM.as("l2ff");
				Freeform rect2 = FREEFORM.as("rect2");
				Freeform um2 = FREEFORM.as("um2");
				Freeform u2 = FREEFORM.as("u2");

				Condition where2 =
						l2.WORD_ID.eq(w1.ID)
						.and(l2ff.LEXEME_ID.eq(l2.ID))
						.and(l2ff.FREEFORM_ID.eq(rect2.ID))
						.and(rect2.TYPE.eq(FreeformType.GOVERNMENT.name()))
						.and(um2.PARENT_ID.eq(rect2.ID))
						.and(um2.TYPE.eq(FreeformType.USAGE_MEANING.name()))
						.and(u2.PARENT_ID.eq(um2.ID))
						.and(u2.TYPE.eq(FreeformType.USAGE.name()));

				where2 = applySearchValueFilter(searchValueStr, searchOperand, u2.VALUE_TEXT, where2);
				where1 = where1.and(DSL.exists(DSL.select(u2.ID).from(l2, l2ff, rect2, um2, u2).where(where2)));

			} else if (SearchKey.CONCEPT_ID.equals(searchKey)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				MeaningFreeform m2ff = MEANING_FREEFORM.as("m2ff");
				Freeform concept = FREEFORM.as("concept");

				Condition where2 =
						l2.WORD_ID.eq(w1.ID)
						.and(l2.MEANING_ID.eq(m2.ID))
						.and(m2ff.MEANING_ID.eq(m2.ID))
						.and(m2ff.FREEFORM_ID.eq(concept.ID))
						.and(concept.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				where2 = applySearchValueFilter(searchValueStr, searchOperand, concept.VALUE_TEXT, where2);
				where1 = where1.and(DSL.exists(DSL.select(concept.ID).from(l2, m2, m2ff, concept).where(where2)));
			}
		}
		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme l1 = LEXEME.as("l1");
			where1 = where1.andExists(
						DSL.select(l1.ID).from(l1)
						.where((l1.WORD_ID.eq(w1.ID))
						.and(l1.DATASET_CODE.in(datasets))));
		}

		Lexeme dscl = LEXEME.as("dscl");
		Field<String[]> dscf = DSL.field(DSL.select(DSL.arrayAggDistinct(dscl.DATASET_CODE)).from(dscl).where(dscl.WORD_ID.eq(w1.ID)).groupBy(w1.ID));
		Field<String> wf = DSL.field("(array_agg(distinct f1.value))[1]").cast(String.class);

		Table<Record5<Long,String,Integer,String,String[]>> wordsQuery = create
				.select(
						w1.ID.as("word_id"),
						wf.as("word"),
						w1.HOMONYM_NR,
						w1.LANG,
						dscf.as("dataset_codes"))
				.from(from1)
				.where(where1)
				.groupBy(w1.ID)
				.asTable("wq");

		return create
				.select(wordsQuery.fields())
				.from(wordsQuery)
				.orderBy(wordsQuery.field("word"), wordsQuery.field("homonym_nr"))
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
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

	public Result<Record> findWords(String wordWithMetaCharacters, List<String> datasets) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");

		Field<String[]> dscf = DSL.field(DSL.select(DSL.arrayAggDistinct(LEXEME.DATASET_CODE)).from(LEXEME).where(LEXEME.WORD_ID.eq(WORD.ID)).groupBy(WORD.ID));
		Field<String> wf = DSL.field("(array_agg(distinct form.value))[1]").cast(String.class);

		Table<Record5<Long,String,Integer,String,String[]>> wordsQuery = create
				.select(
						WORD.ID.as("word_id"),
						wf.as("word"),
						WORD.HOMONYM_NR,
						WORD.LANG,
						dscf.as("dataset_codes"))
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.likeIgnoreCase(theFilter)
						.and(FORM.IS_WORD.isTrue())
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.andExists(DSL.select(LEXEME.ID).from(LEXEME)
								.where((LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.DATASET_CODE.in(datasets)))
						)
				)
				.groupBy(WORD.ID)
				.asTable("wq");

		return create
				.select(wordsQuery.fields())
				.from(wordsQuery)
				.orderBy(wordsQuery.field("word"), wordsQuery.field("homonym_nr"))
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}
}
