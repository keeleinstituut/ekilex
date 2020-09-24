package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.SelectHavingStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LifecycleLogOwner;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.tables.ActivityLog;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordActivityLog;
import eki.ekilex.data.db.tables.WordFreeform;

@Component
public class LexSearchConditionComposer implements GlobalConstant, ActivityFunct {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public Condition createSearchCondition(Word word, Paradigm paradigm, String searchWordCrit, SearchDatasetsRestriction searchDatasetsRestriction) {

		String theFilter = searchWordCrit.replace("*", "%").replace("?", "_").toLowerCase();

		Form form = FORM.as("f2");
		Condition where1 = form.PARADIGM_ID.eq(paradigm.ID);
		where1 = where1.and(form.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));
		if (StringUtils.containsAny(theFilter, '%', '_')) {
			where1 = where1.and(DSL.lower(form.VALUE).like(theFilter));
		} else {
			where1 = where1.and(DSL.lower(form.VALUE).eq(theFilter));
		}
		Condition where = composeWordDatasetsCondition(word, searchDatasetsRestriction);
		where = where.andExists(DSL.select(form.ID).from(form).where(where1));
		return where;
	}

	public Condition createSearchCondition(Word w1, List<SearchCriterionGroup> searchCriteriaGroups, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Condition where = composeWordDatasetsCondition(w1, searchDatasetsRestriction);

		for (SearchCriterionGroup searchCriterionGroup : searchCriteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				boolean containsSearchKeys;

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.VALUE);
				if (containsSearchKeys) {
					Paradigm p1 = Paradigm.PARADIGM.as("p1");
					Form f1 = Form.FORM.as("f1");

					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(p1.WORD_ID.eq(w1.ID))
							.and(f1.PARADIGM_ID.eq(p1.ID))
							.and(f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));
					where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
					where = where.andExists(DSL.select(l1.ID).from(l1, p1, f1).where(where1));
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria,
						SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME, SearchKey.PUBLICITY, SearchKey.LEXEME_POS, SearchKey.LEXEME_FREQUENCY,
						SearchKey.LEXEME_GRAMMAR);
				if (containsSearchKeys) {
					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
					where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = searchFilterHelper.applyLexemeSourceRefFilter(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeSourceNameFilter(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, l1.IS_PUBLIC, where1);
					where1 = searchFilterHelper.applyLexemePosFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeFrequencyFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeGrammarFilters(searchCriteria, l1.ID, where1);
					where = where.andExists(DSL.select(l1.ID).from(l1).where(where1));
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.COMPLEXITY);
				if (containsSearchKeys) {
					List<SearchCriterion> positiveValueSearchCriteria = searchFilterHelper.filterPositiveValueSearchCriteria(searchCriteria);
					List<SearchCriterion> negativeValueSearchCriteria = searchFilterHelper.filterNegativeValueSearchCriteria(searchCriteria);

					if (CollectionUtils.isNotEmpty(positiveValueSearchCriteria)) {
						Condition where1 = l1.WORD_ID.eq(w1.ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
						where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
						where1 = searchFilterHelper.applyValueFilters(SearchKey.COMPLEXITY, positiveValueSearchCriteria, l1.COMPLEXITY, where1, true);
						where = where.andExists(DSL.select(l1.ID).from(l1).where(where1));
					}

					if (CollectionUtils.isNotEmpty(negativeValueSearchCriteria)) {
						Condition where1 = l1.WORD_ID.eq(w1.ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
						where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
						where1 = searchFilterHelper.applyValueFilters(SearchKey.COMPLEXITY, negativeValueSearchCriteria, l1.COMPLEXITY, where1, true);
						where = where.andNotExists(DSL.select(l1.ID).from(l1).where(where1));
					}
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.SECONDARY_MEANING_WORD);
				if (containsSearchKeys) {
					Lexeme l2 = Lexeme.LEXEME.as("l2");
					Word w2 = Word.WORD.as("w2");
					Paradigm p2 = Paradigm.PARADIGM.as("p2");
					Form f2 = Form.FORM.as("f2");

					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(l2.TYPE.eq(LEXEME_TYPE_SECONDARY))
							.and(l1.MEANING_ID.eq(l2.MEANING_ID))
							.and(l2.WORD_ID.eq(w2.ID))
							.and(p2.WORD_ID.eq(w2.ID))
							.and(f2.PARADIGM_ID.eq(p2.ID))
							.and(f2.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));
					where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);

					boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.SECONDARY_MEANING_WORD, searchCriteria);
					if (isNotExistsSearch) {
						where = where.andNotExists(DSL.select(l1.ID).from(l1, l2, p2, f2, w2).where(where1));
					} else {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.SECONDARY_MEANING_WORD, searchCriteria, f2.VALUE, where1, true);
						where = where.andExists(DSL.select(l1.ID).from(l1, l2, p2, f2, w2).where(where1));
					}
				}

				where = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where, false);
				where = searchFilterHelper.applyWordOdRecommendationFilters(searchCriteria, w1.ID, where);
				where = applyWordActivityLogFilters(searchCriteria, w1.ID, where);

			} else if (SearchEntity.WORD.equals(searchEntity)) {

				List<SearchCriterion> positiveValueSearchCriteria = searchFilterHelper.filterPositiveValueSearchCriteria(searchCriteria);
				List<SearchCriterion> negativeValueSearchCriteria = searchFilterHelper.filterNegativeValueSearchCriteria(searchCriteria);

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Word w2 = Word.WORD.as("w2");

				boolean containsSearchKeys;

				if (CollectionUtils.isNotEmpty(positiveValueSearchCriteria)) {

					containsSearchKeys = searchFilterHelper.containsSearchKeys(positiveValueSearchCriteria,
							SearchKey.ID, SearchKey.LANGUAGE, SearchKey.VALUE, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME);

					if (containsSearchKeys) {
						Paradigm p2 = Paradigm.PARADIGM.as("p2");
						Form f2 = Form.FORM.as("f2");

						Condition where1 = l1.WORD_ID.eq(w1.ID)
								.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l1.MEANING_ID.eq(l2.MEANING_ID))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(p2.WORD_ID.eq(w2.ID))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));

						where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
						where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
						where1 = searchFilterHelper.applyIdFilters(SearchKey.ID, positiveValueSearchCriteria, w2.ID, where1);
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, positiveValueSearchCriteria, w2.LANG, where1, false);
						where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, positiveValueSearchCriteria, f2.VALUE, where1, true);
						where1 = searchFilterHelper.applyLexemeSourceRefFilter(positiveValueSearchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemeSourceNameFilter(positiveValueSearchCriteria, l2.ID, where1);

						where = where.andExists(DSL.select(l1.ID).from(l1, l2, p2, f2, w2).where(where1));
					}
				}

				if (CollectionUtils.isNotEmpty(negativeValueSearchCriteria)) {

					containsSearchKeys = searchFilterHelper.containsSearchKeys(negativeValueSearchCriteria, SearchKey.LANGUAGE);

					if (containsSearchKeys) {
						Condition where1 = l1.WORD_ID.eq(w1.ID)
								.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l1.MEANING_ID.eq(l2.MEANING_ID))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY));

						where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
						where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, negativeValueSearchCriteria, w2.LANG, where1, false);

						where = where.andNotExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
					}
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

				if (containsSearchKeys) {
					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(l1.MEANING_ID.eq(l2.MEANING_ID))
							.and(l2.WORD_ID.eq(w2.ID))
							.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY));
					where1 = applyWordActivityLogFilters(searchCriteria, w2.ID, where1);
					where = where.andExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
				}

			} else if (SearchEntity.TAG.equals(searchEntity)) {

				where = searchFilterHelper.applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, w1, where);
				where = searchFilterHelper.applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, ActivityEntity.TAG, w1, where);

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				Form f1 = Form.FORM.as("f1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(f1.PARADIGM_ID.eq(p1.ID));

				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1, false);

				where = where.andExists(DSL.select(l1.ID).from(l1, p1, f1).where(where1));

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");

				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1.MEANING_ID.eq(m1.ID));

				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyDomainFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningRelationFilters(searchCriteria, m1.ID, where1);
				where = where.andExists(DSL.select(m1.ID).from(l1, m1).where(where1));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				Definition d1 = DEFINITION.as("d1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(d1.MEANING_ID.eq(m1.ID));

				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					where = where.andNotExists(DSL.select(d1.ID).from(l1, m1, d1).where(where1));
				} else {
					where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
					where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, searchCriteria, d1.LANG, where1, false);
					where1 = searchFilterHelper.applyDefinitionSourceRefFilter(searchCriteria, d1.ID, where1);
					where1 = searchFilterHelper.applyDefinitionSourceNameFilter(searchCriteria, d1.ID, where1);

					where = where.andExists(DSL.select(d1.ID).from(l1, m1, d1).where(where1));
				}

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform u1 = FREEFORM.as("u1");

				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1ff.LEXEME_ID.eq(l1.ID))
						.and(l1ff.FREEFORM_ID.eq(u1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()));

				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					where = where.andNotExists(DSL.select(u1.ID).from(l1, l1ff, u1).where(where1));
				} else {
					where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE_TEXT, where1, true);
					where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, searchCriteria, u1.LANG, where1, false);
					where1 = searchFilterHelper.applyFreeformSourceNameFilter(searchCriteria, u1.ID, where1);
					where1 = searchFilterHelper.applyFreeformSourceRefFilter(searchCriteria, u1.ID, where1);

					where = where.andExists(DSL.select(u1.ID).from(l1, l1ff, u1).where(where1));
				}

			} else if (SearchEntity.NOTE.equals(searchEntity)) {

				Freeform nff3 = FREEFORM.as("nff3");
				Condition where3 = nff3.TYPE.eq(FreeformType.NOTE.name());

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (!isNotExistsSearch) {
					where3 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, nff3.VALUE_TEXT, where3, true);
					where3 = searchFilterHelper.applyFreeformSourceNameFilter(searchCriteria, nff3.ID, where3);
					where3 = searchFilterHelper.applyFreeformSourceRefFilter(searchCriteria, nff3.ID, where3);
				}

				Table<Record1<Long>> n2 = DSL.select(nff3.ID.as("freeform_id")).from(nff3).where(where3).asTable("n2");

				Lexeme l3 = LEXEME.as("l3");
				Meaning m3 = MEANING.as("m3");
				Definition d3 = DEFINITION.as("d3");

				// notes owner #1
				MeaningFreeform mff3 = MEANING_FREEFORM.as("mff3");
				Table<Record2<Long, Long>> mff2 = DSL
						.select(l3.WORD_ID, mff3.FREEFORM_ID)
						.from(l3, mff3, m3)
						.where(
								mff3.MEANING_ID.eq(m3.ID)
										.and(l3.MEANING_ID.eq(m3.ID))
										.and(l3.TYPE.eq(LEXEME_TYPE_PRIMARY)))
						.asTable("mff2");

				// notes owner #2
				DefinitionFreeform dff3 = DEFINITION_FREEFORM.as("dff3");
				Table<Record2<Long, Long>> dff2 = DSL
						.select(l3.WORD_ID, dff3.FREEFORM_ID)
						.from(l3, dff3, m3, d3)
						.where(
								dff3.DEFINITION_ID.eq(d3.ID)
										.and(d3.MEANING_ID.eq(m3.ID))
										.and(l3.MEANING_ID.eq(m3.ID))
										.and(l3.TYPE.eq(LEXEME_TYPE_PRIMARY)))
						.asTable("dff2");

				// notes owner #3
				LexemeFreeform lff3 = LEXEME_FREEFORM.as("lff3");
				Table<Record2<Long, Long>> lff2 = DSL
						.select(l3.WORD_ID, lff3.FREEFORM_ID)
						.from(l3, lff3)
						.where(lff3.LEXEME_ID.eq(l3.ID).and(l3.TYPE.eq(LEXEME_TYPE_PRIMARY)))
						.asTable("lff2");

				// notes owner #4
				WordFreeform wff2 = WORD_FREEFORM.as("wff2");

				// notes owners joined
				Table<Record1<Long>> n1 = DSL
						.select(DSL.coalesce(wff2.WORD_ID, mff2.field("word_id", Long.class), dff2.field("word_id"), lff2.field("word_id")).as("word_id"))
						.from(n2
								.leftOuterJoin(wff2).on(wff2.FREEFORM_ID.eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(mff2).on(mff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(dff2).on(dff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(lff2).on(lff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class))))
						.asTable("n1");

				if (isNotExistsSearch) {
					where = where.andNotExists(DSL.select(n1.field("word_id")).from(n1).where(n1.field("word_id", Long.class).eq(w1.ID)));
				} else {
					where = where.andExists(DSL.select(n1.field("word_id")).from(n1).where(n1.field("word_id", Long.class).eq(w1.ID)));
				}

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				// this type of search is not actually available

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				MeaningFreeform m1ff = MEANING_FREEFORM.as("m1ff");
				Freeform c1 = FREEFORM.as("c1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(m1ff.MEANING_ID.eq(m1.ID))
						.and(m1ff.FREEFORM_ID.eq(c1.ID))
						.and(c1.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.ID, searchCriteria, c1.VALUE_TEXT, where1, false);

				where = where.andExists(DSL.select(c1.ID).from(l1, m1, m1ff, c1).where(where1));

			} else if (SearchEntity.CLUELESS.equals(searchEntity)) {

				where = composeCluelessValueFilter(w1, searchCriteria, searchDatasetsRestriction, where);
				where = composeCluelessSourceFilter(w1, searchCriteria, searchDatasetsRestriction, where);
			}
		}
		return where;
	}

	private Condition composeWordDatasetsCondition(Word word, SearchDatasetsRestriction searchDatasetsRestriction) {

		Lexeme lfd = LEXEME.as("lfd");
		Condition dsFiltWhere = searchFilterHelper.applyDatasetRestrictions(lfd, searchDatasetsRestriction, null);
		Condition where = DSL.exists(DSL.select(lfd.ID).from(lfd).where(lfd.WORD_ID.eq(word.ID).and(lfd.TYPE.eq(LEXEME_TYPE_PRIMARY)).and(dsFiltWhere)));
		return where;
	}

	private Condition composeCluelessValueFilter(
			Word w1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		Definition d1 = DEFINITION.as("d1");
		Meaning m1 = MEANING.as("m1");
		Lexeme l1 = LEXEME.as("l1");
		Form f1 = FORM.as("f1");
		Paradigm p1 = PARADIGM.as("p1");
		MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
		DefinitionFreeform dff1 = DEFINITION_FREEFORM.as("dff1");
		LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
		WordFreeform wff1 = WORD_FREEFORM.as("wff1");
		Freeform ff1 = FREEFORM.as("ff1");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");
		Condition where1;

		// word and meaningword select
		where1 = l1.MEANING_ID.eq(l2.MEANING_ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(l2.WORD_ID.eq(w2.ID))
				.and(p2.WORD_ID.eq(w2.ID))
				.and(f2.PARADIGM_ID.eq(p2.ID))
				.and(f2.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f2.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectWordAndMeaningWord = DSL.select(l1.WORD_ID).from(l1, l2, p2, f2, w2).where(where1).groupBy(l1.WORD_ID);

		// definition select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(d1.MEANING_ID.eq(m1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinition = DSL.select(l1.WORD_ID).from(l1, m1, d1).where(where1).groupBy(l1.WORD_ID);

		// definition ff select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(d1.MEANING_ID.eq(m1.ID))
				.and(dff1.DEFINITION_ID.eq(d1.ID))
				.and(dff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.eq(FreeformType.NOTE.name()));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinitionFreeform = DSL.select(l1.WORD_ID).from(l1, dff1, ff1, m1, d1).where(where1).groupBy(l1.WORD_ID);

		// lexeme ff select
		String[] lexemeFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.USAGE.name(), FreeformType.GOVERNMENT.name(),
				FreeformType.GRAMMAR.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};
		where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY)
				.and(lff1.LEXEME_ID.eq(l1.ID))
				.and(lff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.in(lexemeFreeformTypes));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectLexemeFreeform = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// meaning ff select
		String[] meaningFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.CONCEPT_ID.name(), FreeformType.LEARNER_COMMENT.name()};
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(mff1.MEANING_ID.eq(m1.ID))
				.and(mff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.in(meaningFreeformTypes));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectMeaningFreeform = DSL.select(l1.WORD_ID).from(l1, m1, mff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// word ff select
		String[] wordFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.OD_WORD_RECOMMENDATION.name()};
		where1 = l1.WORD_ID.eq(w1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(wff1.WORD_ID.eq(w1.ID))
				.and(wff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.in(wordFreeformTypes));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectWordFreeform = DSL.select(l1.WORD_ID).from(l1, w1, wff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// usage ff select
		String[] usageFreeformTypes = new String[] {
				FreeformType.USAGE_TRANSLATION.name(), FreeformType.USAGE_DEFINITION.name(),
				FreeformType.OD_USAGE_ALTERNATIVE.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};
		where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY)
				.and(lff1.LEXEME_ID.eq(l1.ID))
				.and(lff1.FREEFORM_ID.eq(ff1.PARENT_ID))
				.and(ff1.TYPE.in(usageFreeformTypes));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectUsageFreeform = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// form select
		where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY)
				.and(p1.WORD_ID.eq(l1.WORD_ID))
				.and(f1.PARADIGM_ID.eq(p1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectForm = DSL.select(l1.WORD_ID).from(l1, p1, f1).where(where1).groupBy(l1.WORD_ID);

		Table<Record1<Long>> a1 = selectWordAndMeaningWord
				.unionAll(selectDefinition)
				.unionAll(selectDefinitionFreeform)
				.unionAll(selectLexemeFreeform)
				.unionAll(selectMeaningFreeform)
				.unionAll(selectWordFreeform)
				.unionAll(selectUsageFreeform)
				.unionAll(selectForm)
				.asTable("a1");

		where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		return where;
	}

	private Condition composeCluelessSourceFilter(
			Word w1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_ID);
		}

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> !crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());
		List<SearchCriterion> notExistsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());

		Lexeme l1 = LEXEME.as("l1");
		LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
		MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
		DefinitionFreeform dff1 = DEFINITION_FREEFORM.as("dff1");
		Freeform ff1 = FREEFORM.as("ff1");
		Definition d1 = DEFINITION.as("d1");
		FreeformSourceLink ffsl1 = FREEFORM_SOURCE_LINK.as("ffsl1");
		DefinitionSourceLink dsl1 = DEFINITION_SOURCE_LINK.as("dsl1");
		LexemeSourceLink lsl1 = LEXEME_SOURCE_LINK.as("lsl1");
		Condition where1;

		if (CollectionUtils.isNotEmpty(existsCriteria)) {

			where1 = lsl1.LEXEME_ID.eq(l1.ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, lsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, lsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1, lsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.USAGE.name())).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, lff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = dsl1.DEFINITION_ID.eq(d1.ID).and(l1.MEANING_ID.eq(d1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, dsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, lff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(mff1.FREEFORM_ID.eq(ff1.ID)).and(mff1.MEANING_ID.eq(l1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, mff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(dff1.FREEFORM_ID.eq(ff1.ID)).and(dff1.DEFINITION_ID.eq(d1.ID)).and(l1.MEANING_ID.eq(d1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.unionAll(selectLexemeNoteSourceLinks)
					.unionAll(selectMeaningNoteSourceLinks)
					.unionAll(selectDefinitionNoteSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		}

		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {

			where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY).andNotExists(DSL.select(lsl1.ID).from(lsl1).where(lsl1.LEXEME_ID.eq(l1.ID)));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1).where(where1).groupBy(l1.WORD_ID);

			where1 = ff1.TYPE.eq(FreeformType.USAGE.name())
					.and(lff1.FREEFORM_ID.eq(ff1.ID))
					.and(lff1.LEXEME_ID.eq(l1.ID))
					.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
					.andNotExists(DSL.select(ffsl1.ID).from(ffsl1).where(ffsl1.FREEFORM_ID.eq(ff1.ID)));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY)).andNotExists(DSL.select(dsl1.ID).from(dsl1).where(dsl1.DEFINITION_ID.eq(d1.ID)));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1).where(where1).groupBy(l1.WORD_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		}

		return where;
	}

	private Condition applyWordActivityLogFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition wherew) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherew;
		}

		WordActivityLog wal = WORD_ACTIVITY_LOG.as("wal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Condition where1 = wal.WORD_ID.eq(wordIdField).and(wal.ACTIVITY_LOG_ID.eq(al.ID));

		for (SearchCriterion criterion : filteredCriteria) {
			String critValue = criterion.getSearchValue().toString();
			if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_BY, where1, true);
			} else if (SearchKey.UPDATED_ON.equals(criterion.getSearchKey())) {
				where1 = where1
						.andNot(al.ENTITY_NAME.eq(ActivityEntity.WORD.name()).and(al.FUNCT_NAME.like(LIKE_CREATE)));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			} else if (SearchKey.CREATED_ON.equals(criterion.getSearchKey())) {
				where1 = where1
						.and(al.OWNER_NAME.eq(LifecycleLogOwner.WORD.name()))
						.and(al.OWNER_ID.eq(wordIdField))
						.and(al.ENTITY_NAME.eq(ActivityEntity.WORD.name()))
						.and(al.FUNCT_NAME.like(LIKE_CREATE));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			}
		}
		wherew = wherew.andExists(DSL.select(wal.ID).from(wal, al).where(where1));
		return wherew;
	}
}
