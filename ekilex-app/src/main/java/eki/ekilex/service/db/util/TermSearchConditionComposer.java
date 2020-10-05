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
import static eki.ekilex.data.db.Tables.MEANING_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ACTIVITY_LOG;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
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
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
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
import eki.ekilex.data.db.tables.MeaningActivityLog;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordActivityLog;

@Component
public class TermSearchConditionComposer implements GlobalConstant, ActivityFunct {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public Table<Record3<Long, Long, Long[]>> composeFilteredMeaning(
			SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, SearchResultMode resultMode) throws Exception {

		List<SearchCriterionGroup> criteriaGroups = searchFilter.getCriteriaGroups();

		Word w1 = WORD.as("w1");
		Meaning m1 = MEANING.as("m1");
		Lexeme l1 = LEXEME.as("l1");

		Condition wherem = DSL.noCondition();
		Condition wherew = DSL.noCondition();
		Condition wherel = l1.TYPE.eq(LEXEME_TYPE_PRIMARY);
		wherel = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, wherel);

		for (SearchCriterionGroup searchCriterionGroup : criteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.TERM.equals(searchEntity)) {

				Form f1 = FORM.as("f1");
				Paradigm p1 = PARADIGM.as("p1");

				boolean containsSearchKeys;

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.VALUE);
				if (containsSearchKeys) {
					Condition wheref1 = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name())
							.and(f1.PARADIGM_ID.eq(p1.ID))
							.and(p1.WORD_ID.eq(w1.ID));
					wheref1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, wheref1, true);
					wherew = wherew.andExists(DSL.select(f1.ID).from(f1, p1).where(wheref1));
				}

				wherew = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyLangFilters(searchCriteria, w1.LANG, wherew);
				wherew = applyWordActivityLogFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordTypeValueFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordTypeExistsFilters(searchCriteria, w1.ID, wherew);

				wherel = searchFilterHelper.applyLexemeSourceNameFilter(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeSourceRefFilter(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyPublicityFilters(searchCriteria, l1.IS_PUBLIC, wherel);
				wherel = searchFilterHelper.applyLexemeRegisterValueFilters(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeRegisterExistsFilters(searchCriteria, l1.ID, wherel);

			} else if (SearchEntity.CONCEPT.equals(searchEntity)) {

				wherem = searchFilterHelper.applyDomainValueFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyDomainExistsFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, m1.ID, wherem);
				wherem = applyMeaningActivityLogFilters(searchCriteria, m1.ID, wherem);

			} else if (SearchEntity.TAG.equals(searchEntity)) {

				wherem = searchFilterHelper.applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, m1, wherem);
				wherem = searchFilterHelper.applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, ActivityEntity.TAG, m1, wherem);

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Definition d1 = DEFINITION.as("d1");

				Condition whered1 = d1.MEANING_ID.eq(m1.ID);

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					wherem = wherem.andNotExists(DSL.select(d1.ID).from(d1).where(whered1));
				} else {
					whered1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, whered1, true);
					whered1 = searchFilterHelper.applyLangFilters(searchCriteria, d1.LANG, whered1);
					whered1 = searchFilterHelper.applyDefinitionSourceNameFilter(searchCriteria, d1.ID, whered1);
					whered1 = searchFilterHelper.applyDefinitionSourceRefFilter(searchCriteria, d1.ID, whered1);

					wherem = wherem.andExists(DSL.select(d1.ID).from(d1).where(whered1));
				}

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform u1 = FREEFORM.as("u1");

				Condition whereff1 = l1ff.LEXEME_ID.eq(l1.ID)
						.and(l1ff.FREEFORM_ID.eq(u1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()));

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					wherel = wherel.andNotExists(DSL.select(l1ff.ID).from(l1ff, u1).where(whereff1));
				} else {
					whereff1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE_TEXT, whereff1, true);
					whereff1 = searchFilterHelper.applyLangFilters(searchCriteria, u1.LANG, whereff1);
					whereff1 = searchFilterHelper.applyFreeformSourceNameFilter(searchCriteria, u1.ID, whereff1);
					whereff1 = searchFilterHelper.applyFreeformSourceRefFilter(searchCriteria, u1.ID, whereff1);

					wherel = wherel.andExists(DSL.select(l1ff.ID).from(l1ff, u1).where(whereff1));
				}

			} else if (SearchEntity.NOTE.equals(searchEntity)) {

				// notes
				Freeform nff3 = FREEFORM.as("nff3");
				Condition where3 = nff3.TYPE.eq(FreeformType.NOTE.name());

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (!isNotExistsSearch) {
					where3 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, nff3.VALUE_TEXT, where3, true);
					where3 = searchFilterHelper.applyFreeformSourceNameFilter(searchCriteria, nff3.ID, where3);
					where3 = searchFilterHelper.applyFreeformSourceRefFilter(searchCriteria, nff3.ID, where3);
				}

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
						.where(lff3.LEXEME_ID.eq(l3.ID).and(l3.TYPE.eq(LEXEME_TYPE_PRIMARY)))
						.asTable("lff2");

				// notes owners joined
				Table<Record1<Long>> n1 = DSL
						.select(DSL.coalesce(mff2.MEANING_ID, DSL.coalesce(dff2.field("meaning_id"), lff2.field("meaning_id"))).as("meaning_id"))
						.from(n2
								.leftOuterJoin(mff2).on(mff2.FREEFORM_ID.eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(dff2).on(dff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class)))
								.leftOuterJoin(lff2).on(lff2.field("freeform_id", Long.class).eq(n2.field("freeform_id", Long.class))))
						.asTable("n1");

				if (isNotExistsSearch) {
					wherem = wherem.andNotExists(DSL.select(n1.field("meaning_id")).from(n1).where(n1.field("meaning_id", Long.class).eq(m1.ID)));
				} else {
					wherem = wherem.andExists(DSL.select(n1.field("meaning_id")).from(n1).where(n1.field("meaning_id", Long.class).eq(m1.ID)));
				}

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				MeaningFreeform m1ff = MEANING_FREEFORM.as("m1ff");
				Freeform c1 = FREEFORM.as("c1");

				Condition where1 = m1ff.MEANING_ID.eq(m1.ID)
						.and(m1ff.FREEFORM_ID.eq(c1.ID))
						.and(c1.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				where1 = searchFilterHelper.applyValueFilters(SearchKey.ID, searchCriteria, c1.VALUE_TEXT, where1, false);

				wherem = wherem.andExists(DSL.select(c1.ID).from(m1ff, c1).where(where1));

			} else if (SearchEntity.CLUELESS.equals(searchEntity)) {

				wherem = composeCluelessValueFilter(w1, m1, searchCriteria, searchDatasetsRestriction, wherem);
				wherem = composeCluelessSourceFilter(m1, searchCriteria, searchDatasetsRestriction, wherem);
			}
		}

		Table<Record1<Long>> w = DSL.select(w1.ID).from(w1).where(wherew).asTable("w");
		Table<Record1<Long>> m = DSL.select(m1.ID).from(m1).where(wherem).asTable("m");
		Table<Record4<Long, Long, Long, Long>> l = DSL
				.select(
						l1.ID.as("lexeme_id"),
						l1.WORD_ID,
						l1.MEANING_ID,
						l1.ORDER_BY)
				.from(l1)
				.where(wherel)
				.asTable("l");

		Condition wheremlw = l.field("meaning_id", Long.class).eq(m.field("id", Long.class)).and(l.field("word_id", Long.class).eq(w.field("id", Long.class)));

		Table<Record3<Long, Long, Long[]>> wm = null;

		if (SearchResultMode.MEANING.equals(resultMode)) {
			wm = DSL
					.select(
							m.field("id", Long.class).as("meaning_id"),
							DSL.field("(array_agg(w.id order by l.order_by)) [1]", Long.class).as("word_id"),
							DSL.arrayAgg(w.field("id", Long.class)).as("match_word_ids"))
					.from(m, l, w)
					.where(wheremlw)
					.groupBy(m.field("id"))
					.asTable("m");
		} else if (SearchResultMode.WORD.equals(resultMode)) {
			wm = DSL
					.select(
							m.field("id", Long.class).as("meaning_id"),
							w.field("id", Long.class).as("word_id"),
							DSL.array(new Long[0]))
					.from(m, l, w)
					.where(wheremlw)
					.groupBy(w.field("id"), m.field("id"))
					.asTable("wmid");
		}

		return wm;
	}

	public Table<Record3<Long, Long, Long[]>> composeFilteredMeaning(
			String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, SearchResultMode resultMode) {

		String maskedSearchFilter = searchFilter.replace("*", "%").replace("?", "_").toLowerCase();

		Meaning m = MEANING.as("m");
		Lexeme l1 = LEXEME.as("l");
		Paradigm p = PARADIGM.as("p");
		Word w1 = WORD.as("w");
		Form f1 = FORM.as("f");

		Condition wherel = l1.TYPE.eq(LEXEME_TYPE_PRIMARY);
		wherel = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, wherel);

		Condition wheref = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name());
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			wheref = wheref.and(DSL.lower(f1.VALUE).like(maskedSearchFilter));
		} else {
			wheref = wheref.and(DSL.lower(f1.VALUE).equal(maskedSearchFilter));
		}

		Table<Record1<Long>> f = DSL
				.select(f1.PARADIGM_ID)
				.from(f1)
				.where(wheref)
				.asTable("f");

		Table<Record1<Long>> w = DSL
				.select(w1.ID)
				.from(f, p, w1)
				.where(f.field("paradigm_id", Long.class).eq(p.ID).and(p.WORD_ID.eq(w1.ID)))
				.asTable("w");

		Table<Record4<Long, Long, Long, Long>> l = DSL
				.select(
						l1.ID.as("lexeme_id"),
						l1.WORD_ID,
						l1.MEANING_ID,
						l1.ORDER_BY)
				.from(l1)
				.where(wherel)
				.asTable("l");

		Condition wheremlw = l.field("meaning_id", Long.class).eq(m.ID).and(l.field("word_id", Long.class).eq(w.field("id", Long.class)));

		Table<Record3<Long, Long, Long[]>> wm = null;

		if (SearchResultMode.MEANING.equals(resultMode)) {
			wm = DSL
					.select(
							m.ID.as("meaning_id"),
							DSL.field("(array_agg(w.id order by l.order_by)) [1]", Long.class).as("word_id"),
							DSL.arrayAgg(w.field("id", Long.class)).as("match_word_ids"))
					.from(m, l, w)
					.where(wheremlw)
					.groupBy(m.ID)
					.asTable("m");
		} else if (SearchResultMode.WORD.equals(resultMode)) {
			wm = DSL
					.select(
							m.ID.as("meaning_id"),
							w.field("id", Long.class).as("word_id"),
							DSL.array(new Long[0]))
					.from(m, l, w)
					.where(wheremlw)
					.groupBy(w.field("id"), m.ID)
					.asTable("wmid");
		}

		return wm;
	}

	private Condition composeCluelessSourceFilter(Meaning m1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition wherem) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_ID);
		}

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherem;
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
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.MEANING_ID).from(l1, lsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.USAGE.name())).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1, ffsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = dsl1.DEFINITION_ID.eq(d1.ID).and(l1.MEANING_ID.eq(d1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, dsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(d1.MEANING_ID).from(l1, d1, dsl1).where(where1).groupBy(d1.MEANING_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1, ffsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(mff1.FREEFORM_ID.eq(ff1.ID)).and(mff1.MEANING_ID.eq(l1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.MEANING_ID).from(l1, mff1, ff1, ffsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(dff1.FREEFORM_ID.eq(ff1.ID)).and(dff1.DEFINITION_ID.eq(d1.ID)).and(l1.MEANING_ID.eq(d1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = searchFilterHelper.applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionNoteSourceLinks = DSL.select(d1.MEANING_ID).from(l1, d1, dff1, ff1, ffsl1).where(where1).groupBy(d1.MEANING_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.unionAll(selectLexemeNoteSourceLinks)
					.unionAll(selectMeaningNoteSourceLinks)
					.unionAll(selectDefinitionNoteSourceLinks)
					.asTable("a1");

			wherem = wherem.andExists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID)));
		}

		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {

			where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY).andNotExists(DSL.select(lsl1.ID).from(lsl1).where(lsl1.LEXEME_ID.eq(l1.ID)));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.MEANING_ID).from(l1).where(where1).groupBy(l1.MEANING_ID);

			where1 = ff1.TYPE.eq(FreeformType.USAGE.name())
					.and(lff1.FREEFORM_ID.eq(ff1.ID))
					.and(lff1.LEXEME_ID.eq(l1.ID))
					.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
					.andNotExists(DSL.select(ffsl1.ID).from(ffsl1).where(ffsl1.FREEFORM_ID.eq(ff1.ID)));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY)).andNotExists(DSL.select(dsl1.ID).from(dsl1).where(dsl1.DEFINITION_ID.eq(d1.ID)));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(d1.MEANING_ID).from(l1, d1).where(where1).groupBy(d1.MEANING_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.asTable("a1");

			wherem = wherem.andExists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID)));
		}

		return wherem;
	}

	private Condition composeCluelessValueFilter(Word w1, Meaning m1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition wherem) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherem;
		}

		Definition d1 = DEFINITION.as("d1");
		Lexeme l1 = LEXEME.as("l1");
		Form f1 = FORM.as("f1");
		Paradigm p1 = PARADIGM.as("p1");
		MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
		DefinitionFreeform dff1 = DEFINITION_FREEFORM.as("dff1");
		LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
		Freeform ff1 = FREEFORM.as("ff1");
		Condition where1, where2;

		// word select
		where2 = f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name())
				.and(f1.PARADIGM_ID.eq(p1.ID))
				.and(p1.WORD_ID.eq(w1.ID))
				.and(l1.WORD_ID.eq(w1.ID))
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
		where2 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, f1.VALUE, where2, true);

		where1 = DSL.exists(DSL.select(w1.ID).from(f1, p1, w1).where(where2));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectWord = DSL.select(l1.MEANING_ID).from(l1).where(where1).groupBy(l1.MEANING_ID);

		// definition select
		where1 = l1.MEANING_ID.eq(d1.MEANING_ID);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, d1.VALUE, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectDefinition = DSL.select(d1.MEANING_ID).from(l1, d1).where(where1).groupBy(d1.MEANING_ID);

		// meaning ff select
		String[] meaningFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.CONCEPT_ID.name(), FreeformType.LEARNER_COMMENT.name()};
		where1 = ff1.TYPE.in(meaningFreeformTypes).and(mff1.FREEFORM_ID.eq(ff1.ID)).and(mff1.MEANING_ID.eq(l1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, ff1.VALUE_TEXT, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectMeaningFreeforms = DSL.select(mff1.MEANING_ID).from(l1, mff1, ff1).where(where1).groupBy(mff1.MEANING_ID);

		// definition ff select
		where1 = ff1.TYPE.eq(FreeformType.NOTE.name()).and(dff1.FREEFORM_ID.eq(ff1.ID)).and(dff1.DEFINITION_ID.eq(d1.ID)).and(l1.MEANING_ID.eq(d1.MEANING_ID));
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, ff1.VALUE_TEXT, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectDefinitionFreeforms = DSL.select(d1.MEANING_ID).from(l1, d1, dff1, ff1).where(where1).groupBy(d1.MEANING_ID);

		// lexeme ff select
		String[] lexemeFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.USAGE.name(), FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name()};
		where1 = ff1.TYPE.in(lexemeFreeformTypes).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, ff1.VALUE_TEXT, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectLexemeFreeforms = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.MEANING_ID);

		// lexeme usage translation, definition select
		String[] lexemeFreeformSubTypes = new String[] {FreeformType.USAGE_TRANSLATION.name(), FreeformType.USAGE_DEFINITION.name()};
		where1 = ff1.TYPE.in(lexemeFreeformSubTypes).and(lff1.FREEFORM_ID.eq(ff1.PARENT_ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, ff1.VALUE_TEXT, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
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
		return wherem;
	}

	private Condition applyWordActivityLogFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition wherew) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON);

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
						.and(al.OWNER_NAME.in(LifecycleLogOwner.WORD.name(), LifecycleLogOwner.LEXEME.name()))
						.andNot(al.ENTITY_NAME.eq(ActivityEntity.GRAMMAR.name()))
						.andNot(al.FUNCT_NAME.eq(JOIN).and(al.ENTITY_NAME.eq(ActivityEntity.WORD.name())))
						.andNot(al.FUNCT_NAME.eq(JOIN_WORDS));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			}
		}
		wherew = wherew.andExists(DSL.select(wal.ID).from(wal, al).where(where1));
		return wherew;
	}

	private Condition applyMeaningActivityLogFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition wherem) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherem;
		}

		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Condition where1 = mal.MEANING_ID.eq(meaningIdField).and(mal.ACTIVITY_LOG_ID.eq(al.ID));

		for (SearchCriterion criterion : filteredCriteria) {
			String critValue = criterion.getSearchValue().toString();
			if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_BY, where1, true);
			} else if (SearchKey.UPDATED_ON.equals(criterion.getSearchKey())) {
				where1 = where1
						.andNot(al.ENTITY_NAME.eq(ActivityEntity.GRAMMAR.name()))
						.andNot(al.FUNCT_NAME.eq(JOIN).and(al.ENTITY_NAME.eq(ActivityEntity.WORD.name())))
						.andNot(al.FUNCT_NAME.eq(JOIN_WORDS))
						.andNot(al.ENTITY_NAME.eq(ActivityEntity.MEANING.name()).and(al.FUNCT_NAME.like(LIKE_CREATE)));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			} else if (SearchKey.CREATED_ON.equals(criterion.getSearchKey())) {
				where1 = where1
						.and(al.OWNER_NAME.eq(LifecycleLogOwner.MEANING.name()))
						.and(al.OWNER_ID.eq(meaningIdField))
						.and(al.ENTITY_NAME.eq(ActivityEntity.MEANING.name()))
						.and(al.FUNCT_NAME.like(LIKE_CREATE));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			}
		}
		wherem = wherem.andExists(DSL.select(mal.ID).from(mal, al).where(where1));
		return wherem;
	}
}
