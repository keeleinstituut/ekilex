package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordTuple;
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
public class TermSearchDbService implements SystemConstant {

	private DSLContext create;

	@Autowired
	public TermSearchDbService(DSLContext context) {
		create = context;
	}

	public Map<Long, List<WordTuple>> findMeaningsAsMap(SearchFilter searchFilter, List<String> datasets, String resultLang, boolean fetchAll) throws Exception {
		
		Table<Record1<Long>> m = getFilteredMeanings(searchFilter, datasets, fetchAll);

		return execute(m, resultLang);
	}

	public int countMeanings(SearchFilter searchFilter, List<String> datasets) throws Exception {

		Table<Record1<Long>> m = getFilteredMeanings(searchFilter, datasets, true);

		return create.fetchCount(m);
	}

	public Map<Long, List<WordTuple>> findMeaningsAsMap(String wordWithMetaCharacters, List<String> datasets, String resultLang, boolean fetchAll) {

		Table<Record1<Long>> m = getFilteredMeanings(wordWithMetaCharacters, datasets, fetchAll);

		return execute(m, resultLang);
	}

	public int countMeanings(String wordWithMetaCharacters, List<String> datasets) {

		Table<Record1<Long>> m = getFilteredMeanings(wordWithMetaCharacters, datasets, true);

		return create.fetchCount(m);
	}

	private Table<Record1<Long>> getFilteredMeanings(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) throws Exception {

		List<SearchCriterionGroup> criteriaGroups = searchFilter.getCriteriaGroups();

		Meaning m1 = MEANING.as("m1");

		Condition where = DSL.trueCondition();

		for (SearchCriterionGroup searchCriterionGroup : criteriaGroups) {

			List<SearchCriterion> searchCriterions = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriterions)) {
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

				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				Word w1 = WORD.as("w1");
				Lexeme l1 = LEXEME.as("l1");
				Condition where1 =
						f1.IS_WORD.isTrue()
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID))
						.and(l1.MEANING_ID.eq(m1.ID));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}
				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where1 = applySearchValueFilter(searchValueStr, searchOperand, f1.VALUE, where1);
				}
				for (SearchCriterion criterion : languageCriterions) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = where1.and(w1.LANG.eq(searchValueStr));
				}
				where = where.and(DSL.exists(DSL.select(w1.ID).from(f1, p1, w1, l1).where(where1)));

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");
				Word w1 = WORD.as("w1");
				Lexeme l1 = LEXEME.as("l1");
				Condition where1 =
						f1.PARADIGM_ID.eq(p1.ID)
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID))
						.and(l1.MEANING_ID.eq(m1.ID));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}
				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where1 = applySearchValueFilter(searchValueStr, searchOperand, f1.VALUE, where1);
				}
				for (SearchCriterion criterion : languageCriterions) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = where1.and(w1.LANG.eq(searchValueStr));
				}
				where = where.and(DSL.exists(DSL.select(w1.ID).from(f1, p1, w1, l1).where(where1)));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Definition d1 = DEFINITION.as("d1");
				Condition where1 = d1.MEANING_ID.eq(m1.ID);

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where1 = applySearchValueFilter(searchValueStr, searchOperand, d1.VALUE, where1);
				}
				for (SearchCriterion criterion : languageCriterions) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = where1.and(d1.LANG.eq(searchValueStr));
				}
				where = where.and(DSL.exists(DSL.select(d1.ID).from(d1).where(where1)));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform rect1 = FREEFORM.as("rect1");
				Freeform um1 = FREEFORM.as("um1");
				Freeform u1 = FREEFORM.as("u1");

				Condition where1 =
						l1.MEANING_ID.eq(m1.ID)
						.and(l1ff.LEXEME_ID.eq(l1.ID))
						.and(l1ff.FREEFORM_ID.eq(rect1.ID))
						.and(rect1.TYPE.eq(FreeformType.GOVERNMENT.name()))
						.and(um1.PARENT_ID.eq(rect1.ID))
						.and(um1.TYPE.eq(FreeformType.USAGE_MEANING.name()))
						.and(u1.PARENT_ID.eq(um1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}
				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where1 = applySearchValueFilter(searchValueStr, searchOperand, u1.VALUE_TEXT, where1);
				}
				for (SearchCriterion criterion : languageCriterions) {
					where1 = where1.and(u1.LANG.eq(criterion.getSearchValue().toString()));
				}

				where = where.and(DSL.exists(DSL.select(u1.ID).from(l1, l1ff, rect1, um1, u1).where(where1)));

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				MeaningFreeform m1ff = MEANING_FREEFORM.as("m1ff");
				Freeform concept = FREEFORM.as("concept");

				Condition where1 =
						m1ff.MEANING_ID.eq(m1.ID)
						.and(m1ff.FREEFORM_ID.eq(concept.ID))
						.and(concept.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				for (SearchCriterion criterion : idCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where1 = applySearchValueFilter(searchValueStr, searchOperand, concept.VALUE_TEXT, where1);
				}

				where = where.and(DSL.exists(DSL.select(concept.ID).from(m1ff, concept).where(where1)));
			}
		}

		int limit = MAX_RESULTS_LIMIT;
		if (fetchAll) {
			limit = Integer.MAX_VALUE;
		}

		Table<Record1<Long>> m = DSL
				.select(m1.ID.as("meaning_id"))
				.from(m1)
				.where(where)
				.groupBy(m1.ID)
				.orderBy(m1.ID)
				.limit(limit)
				.asTable("m");
		return m;
	}

	private Map<Long, List<WordTuple>> execute(Table<Record1<Long>> m, String resultLang) {

		Form f2 = FORM.as("f2");
		Paradigm p2 = PARADIGM.as("p2");
		Word w2 = WORD.as("w2");
		Lexeme l2 = LEXEME.as("l2");
		Condition whereRes = f2.IS_WORD.isTrue().and(f2.PARADIGM_ID.eq(p2.ID)).and(p2.WORD_ID.eq(w2.ID)).and(l2.WORD_ID.eq(w2.ID));
		if (StringUtils.isNotBlank(resultLang)) {
			whereRes = whereRes.and(w2.LANG.eq(resultLang));
		}

		Table<Record5<Long, Long, String, Integer, String>> w = DSL
				.select(
					l2.MEANING_ID,
					w2.ID.as("word_id"),
					w2.LANG.as("word_lang"),
					w2.HOMONYM_NR,
					f2.VALUE.as("word"))
				.from(f2, p2, w2, l2)
				.where(whereRes)
				.asTable("w");

		Table<Record2<Long, String>> c = DSL
				.select(
					MEANING_FREEFORM.MEANING_ID,
					FREEFORM.VALUE_TEXT.as("concept_id"))
				.from(MEANING_FREEFORM, FREEFORM)
				.where(
					MEANING_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)
					.and(FREEFORM.TYPE.eq(FreeformType.CONCEPT_ID.name()))
				).asTable("c");

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(LEXEME.DATASET_CODE))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(w.field("word_id").cast(Long.class)))
				.groupBy(w.field("word_id")));

		Table<Record7<Long, Long, String, Integer, String, String, String[]>> ww = DSL
				.select(
					m.field("meaning_id", Long.class),
					w.field("word_id", Long.class), 
					w.field("word_lang", String.class),
					w.field("homonym_nr", Integer.class),
					w.field("word", String.class),
					DSL.coalesce(c.field("concept_id"), "#").as("concept_id").cast(String.class),
					dscf.as("dataset_codes").cast(String[].class))
				.from(
					m.leftOuterJoin(w).on(w.field("meaning_id").cast(Long.class).eq(m.field("meaning_id").cast(Long.class)))
					.leftOuterJoin(c).on(c.field("meaning_id").cast(Long.class).eq(m.field("meaning_id").cast(Long.class)))
				).asTable("ww");

		Map<Long, List<WordTuple>> result = create
				.selectFrom(ww)
				.fetchGroups(ww.field("meaning_id", Long.class), WordTuple.class);
		return result;
	}

	private Table<Record1<Long>> getFilteredMeanings(String wordWithMetaCharacters, List<String> datasets, boolean fetchAll) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");

		Condition where1 = FORM.IS_WORD.isTrue();
		if (StringUtils.containsAny(theFilter, '%', '_')) {
			where1 = where1.and(FORM.VALUE.likeIgnoreCase(theFilter));			
		} else {
			where1 = where1.and(FORM.VALUE.equalIgnoreCase(theFilter));
		}
		Paradigm p1 = PARADIGM.as("p1");
		Word w1 = WORD.as("w1");
		Lexeme l1 = LEXEME.as("l1");
		Table<Record1<Long>> f1 = DSL.select(FORM.PARADIGM_ID).from(FORM).where(where1).asTable("f1");

		Condition where11 = f1.field(FORM.PARADIGM_ID).eq(p1.ID).and(p1.WORD_ID.eq(w1.ID)).and(l1.WORD_ID.eq(w1.ID));

		if (CollectionUtils.isNotEmpty(datasets)) {
			where11 = where11.and(l1.DATASET_CODE.in(datasets));
		}

		int limit = MAX_RESULTS_LIMIT;
		if (fetchAll) {
			limit = Integer.MAX_VALUE;
		}

		Table<Record1<Long>> m = DSL
				.select(l1.MEANING_ID)
				.from(f1, p1, w1, l1)
				.where(where11)
				.groupBy(l1.MEANING_ID)
				.orderBy(l1.MEANING_ID)
				.limit(limit)
				.asTable("m");
		return m;
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

	public Record5<Long,String,String,String,Long[]> getMeaning(Long meaningId, List<String> datasets) {

		return create
				.select(
						MEANING.ID.as("meaning_id"),
						MEANING.TYPE_CODE.as("meaning_type_code"),
						MEANING.PROCESS_STATE_CODE.as("meaning_process_state_code"),
						MEANING.STATE_CODE.as("meaning_state_code"),
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"))
				.from(MEANING, LEXEME)
				.where(
						MEANING.ID.eq(meaningId)
						.and(LEXEME.MEANING_ID.eq(MEANING.ID))
						.and(LEXEME.DATASET_CODE.in(datasets))
						)
				.groupBy(MEANING.ID)
				.fetchSingle();
	}

	public Result<Record13<String,Integer,String,Long,String,Long,Long,String,Integer,Integer,Integer,String,String>> getLexemeWords(Long lexemeId) {

		return create
				.select(
						FORM.VALUE.as("word"),
						WORD.HOMONYM_NR,
						WORD.LANG.as("word_lang"),
						WORD.ID.as("word_id"),
						WORD.GENDER_CODE.as("gender_code"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.MEANING_ID,
						LEXEME.DATASET_CODE.as("dataset"),
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME.TYPE_CODE.as("lexeme_type_code"),
						LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"))
				.from(FORM, PARADIGM, WORD, LEXEME)
				.where(
						LEXEME.ID.eq(lexemeId)
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						)
				.groupBy(LEXEME.ID, WORD.ID, FORM.VALUE)
				.orderBy(WORD.ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}

}
