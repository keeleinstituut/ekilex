package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.USAGE;
import static eki.ekilex.data.db.Tables.USAGE_DEFINITION;
import static eki.ekilex.data.db.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.Tables.WORD;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.SelectHavingStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.ActivityOwner;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
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
import eki.ekilex.data.db.tables.DefinitionNote;
import eki.ekilex.data.db.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeNote;
import eki.ekilex.data.db.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningActivityLog;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningLastActivityLog;
import eki.ekilex.data.db.tables.MeaningNote;
import eki.ekilex.data.db.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.tables.Usage;
import eki.ekilex.data.db.tables.UsageDefinition;
import eki.ekilex.data.db.tables.UsageSourceLink;
import eki.ekilex.data.db.tables.UsageTranslation;
import eki.ekilex.data.db.tables.Word;

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
		Condition wherew = w1.IS_PUBLIC.isTrue().and(w1.IS_WORD.isTrue());
		Condition wherel = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, null);

		for (SearchCriterionGroup searchCriterionGroup : criteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			searchCriteria = searchCriteria.stream()
					.filter(searchCriterion -> StringUtils.isBlank(searchCriterion.getValidationMessage()))
					.collect(Collectors.toList());
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.TERM.equals(searchEntity)) {

				boolean containsSearchKeys;

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.VALUE);
				if (containsSearchKeys) {
					Condition wherew1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w1.VALUE, DSL.noCondition(), true);
					Condition wherew2 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w1.VALUE_AS_WORD, DSL.noCondition(), true);
					wherew = wherew.and(DSL.or(wherew1, wherew2));
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LANGUAGE);
				if (containsSearchKeys) {
					List<SearchCriterion> equalsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.EQUALS);
					List<SearchCriterion> notContainsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.NOT_CONTAINS);

					if (CollectionUtils.isNotEmpty(equalsValueCriteria)) {
						wherew = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, equalsValueCriteria, w1.LANG, wherew, false);
					}
					if (CollectionUtils.isNotEmpty(notContainsValueCriteria)) {
						Lexeme l2 = LEXEME.as("l2");
						Word w2 = WORD.as("w2");
						Condition wherew2 = l2.WORD_ID.eq(w2.ID).and(l2.MEANING_ID.eq(m1.ID));
						wherew2 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, notContainsValueCriteria, w2.LANG, wherew2, false);
						wherem = wherem.andNotExists(DSL.select(w2.ID).from(w2, l2).where(wherew2));
					}
				}

				wherew = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyCommaSeparatedIdsFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordActivityLogFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordTypeValueFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordTypeExistsFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordRelationValueFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordRelationExistsFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordForumFilters(searchCriteria, w1.ID, wherew);

				wherel = searchFilterHelper.applyPublicityFilters(searchCriteria, l1.IS_PUBLIC, wherel);
				wherel = searchFilterHelper.applyLexemeSourceFilters(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeSourceRefFilter(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeRegisterValueFilters(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeRegisterExistsFilters(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeNoteFilters(searchCriteria, l1.ID, wherel);
				wherel = searchFilterHelper.applyLexemeValueStateFilters(searchCriteria, l1.VALUE_STATE_CODE, wherel);

			} else if (SearchEntity.CONCEPT.equals(searchEntity)) {

				wherem = searchFilterHelper.applyDomainValueFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyDomainExistsFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyCommaSeparatedIdsFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyMeaningAttributeFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyMeaningRelationValueFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyMeaningRelationExistsFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyMeaningNoteFilters(searchCriteria, m1.ID, wherem);
				wherem = searchFilterHelper.applyMeaningForumFilters(searchCriteria, m1.ID, wherem);
				wherem = applyMeaningActivityLogFilters(searchCriteria, m1.ID, wherem);
				wherem = applyMeaningManualEventOnFilters(searchCriteria, m1.MANUAL_EVENT_ON, wherem);

			} else if (SearchEntity.TAG.equals(searchEntity)) {

				wherem = searchFilterHelper.applyTagFilters(searchCriteria, searchDatasetsRestriction, m1, wherem);
				wherem = searchFilterHelper.applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, ActivityEntity.TAG, m1, wherem);

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Definition d1 = DEFINITION.as("d1");
				Condition whered2 = d1.MEANING_ID.eq(m1.ID);
				Condition whered1;

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					wherem = wherem.andNotExists(DSL.select(d1.ID).from(d1).where(whered2));
				} else {
					whered1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, d1.VALUE, whered2, true);
					whered1 = searchFilterHelper.applyPublicityFilters(searchCriteria, d1.IS_PUBLIC, whered1);
					whered1 = searchFilterHelper.applyDefinitionSourceFilters(searchCriteria, d1.ID, whered1);
					whered1 = searchFilterHelper.applyDefinitionSourceRefFilter(searchCriteria, d1.ID, whered1);
					whered1 = searchFilterHelper.applyLexemeComplexityFilters(searchCriteria, d1.COMPLEXITY, whered1);
					whered1 = searchFilterHelper.applyDefinitionNoteFilters(searchCriteria, d1.ID, whered1);
					wherem = wherem.andExists(DSL.select(d1.ID).from(d1).where(whered1));
				}

				boolean containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LANGUAGE);
				if (containsSearchKeys) {
					List<SearchCriterion> equalsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.EQUALS);
					List<SearchCriterion> notContainsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.NOT_CONTAINS);

					if (CollectionUtils.isNotEmpty(equalsValueCriteria)) {
						whered1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, equalsValueCriteria, d1.LANG, whered2, false);
						wherem = wherem.andExists(DSL.select(d1.ID).from(d1).where(whered1));
					}
					if (CollectionUtils.isNotEmpty(notContainsValueCriteria)) {
						whered1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, notContainsValueCriteria, d1.LANG, whered2, false);
						wherem = wherem.andNotExists(DSL.select(d1.ID).from(d1).where(whered1));
					}
				}

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Usage u1 = USAGE.as("u1");
				Condition whereff2 = u1.LEXEME_ID.eq(l1.ID);
				Condition whereff1;

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					wherel = wherel.andNotExists(DSL.select(u1.ID).from(u1).where(whereff2));
				} else {
					whereff1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, u1.VALUE, whereff2, true);
					whereff1 = searchFilterHelper.applyFreeformSourceFilters(searchCriteria, u1.ID, whereff1);
					whereff1 = searchFilterHelper.applyFreeformSourceRefFilter(searchCriteria, u1.ID, whereff1);
					whereff1 = searchFilterHelper.applyPublicityFilters(searchCriteria, u1.IS_PUBLIC, whereff1);
					whereff1 = searchFilterHelper.applyLexemeComplexityFilters(searchCriteria, u1.COMPLEXITY, whereff1);
					wherel = wherel.andExists(DSL.select(u1.ID).from(u1).where(whereff1));
				}

				boolean containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LANGUAGE);
				if (containsSearchKeys) {
					List<SearchCriterion> equalsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.EQUALS);
					List<SearchCriterion> notContainsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.NOT_CONTAINS);

					if (CollectionUtils.isNotEmpty(equalsValueCriteria)) {
						whereff1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, equalsValueCriteria, u1.LANG, whereff2, false);
						wherel = wherel.andExists(DSL.select(u1.ID).from(u1).where(whereff1));
					}
					if (CollectionUtils.isNotEmpty(notContainsValueCriteria)) {
						whereff1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, notContainsValueCriteria, u1.LANG, whereff2, false);
						wherel = wherel.andNotExists(DSL.select(u1.ID).from(u1).where(whereff1));
					}
				}

			} else if (SearchEntity.NOTE.equals(searchEntity)) {

				Lexeme l11 = LEXEME.as("l11");
				LexemeNote ln1 = LEXEME_NOTE.as("ln1");
				Definition d1 = DEFINITION.as("d1");
				DefinitionNote dn1 = DEFINITION_NOTE.as("dn1");
				MeaningNote mn1 = MEANING_NOTE.as("mn1");

				Condition where1;

				// definition note select
				where1 = l11.MEANING_ID.eq(d1.MEANING_ID).and(dn1.DEFINITION_ID.eq(d1.ID));
				where1 = searchFilterHelper.applyDatasetRestrictions(l11, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, dn1.VALUE, where1, true);
				where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, dn1.IS_PUBLIC, where1);
				where1 = searchFilterHelper.applyDefinitionNoteSourceFilters(searchCriteria, dn1.ID, where1);
				where1 = searchFilterHelper.applyDefinitionNoteSourceRefFilter(searchCriteria, dn1.ID, where1);
				SelectHavingStep<Record1<Long>> selectDefinitionNote = DSL.select(l11.MEANING_ID).from(l11, d1, dn1).where(where1).groupBy(l11.MEANING_ID);

				// lexeme note select
				where1 = ln1.LEXEME_ID.eq(l11.ID);
				where1 = searchFilterHelper.applyDatasetRestrictions(l11, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, ln1.VALUE, where1, true);
				where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, ln1.IS_PUBLIC, where1);
				where1 = searchFilterHelper.applyLexemeNoteSourceFilters(searchCriteria, ln1.ID, where1);
				where1 = searchFilterHelper.applyLexemeNoteSourceRefFilter(searchCriteria, ln1.ID, where1);
				SelectHavingStep<Record1<Long>> selectLexemeNote = DSL.select(l11.MEANING_ID).from(l11, ln1).where(where1).groupBy(l11.MEANING_ID);

				// meaning note select
				where1 = l11.MEANING_ID.eq(mn1.MEANING_ID);
				where1 = searchFilterHelper.applyDatasetRestrictions(l11, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, mn1.VALUE, where1, true);
				where1 = searchFilterHelper.applyMeaningNoteSourceFilters(searchCriteria, mn1.ID, where1);
				where1 = searchFilterHelper.applyMeaningNoteSourceRefFilter(searchCriteria, mn1.ID, where1);
				SelectHavingStep<Record1<Long>> selectMeaningNote = DSL.select(l11.MEANING_ID).from(l11, mn1).where(where1).groupBy(l11.MEANING_ID);

				Table<Record1<Long>> n1 = selectDefinitionNote
						.unionAll(selectLexemeNote)
						.unionAll(selectMeaningNote)
						.asTable("n1");

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);

				if (isNotExistsSearch) {
					wherem = wherem.andNotExists(DSL.select(n1.field("meaning_id")).from(n1).where(n1.field("meaning_id", Long.class).eq(m1.ID)));
				} else {
					wherem = wherem.andExists(DSL.select(n1.field("meaning_id")).from(n1).where(n1.field("meaning_id", Long.class).eq(m1.ID)));
				}

			} else if (SearchEntity.OD_RECOMMENDATION.equals(searchEntity)) {

				wherew = searchFilterHelper.applyWordOdRecommendationValueFilters(searchCriteria, w1.ID, wherew);
				wherew = searchFilterHelper.applyWordOdRecommendationModificationFilters(searchCriteria, w1.ID, wherew);

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

		String maskedSearchFilter = searchFilter.replace(SEARCH_MASK_CHARS, "%").replace(SEARCH_MASK_CHAR, "_");
		Field<String> filterField = DSL.lower(maskedSearchFilter);

		Meaning m = MEANING.as("m");
		Lexeme l1 = LEXEME.as("l");
		Word w1 = WORD.as("w");

		Condition wherel = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, null);

		Condition wherew;
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			wherew = DSL.or(DSL.lower(w1.VALUE).like(filterField), DSL.lower(w1.VALUE_AS_WORD).like(filterField));
		} else {
			wherew = DSL.or(DSL.lower(w1.VALUE).eq(filterField), DSL.lower(w1.VALUE_AS_WORD).eq(filterField));
		}
		wherew = wherew.and(w1.IS_PUBLIC.isTrue()).and(w1.IS_WORD.isTrue());

		Table<Record1<Long>> w = DSL
				.select(w1.ID)
				.from(w1)
				.where(wherew)
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

	private Condition composeCluelessValueFilter(
			Word w1, Meaning m1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition wherem) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherem;
		}

		Definition d1 = DEFINITION.as("d1");
		DefinitionNote dn1 = DEFINITION_NOTE.as("dn1");
		Lexeme l1 = LEXEME.as("l1");
		LexemeNote ln1 = LEXEME_NOTE.as("ln1");
		LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
		Usage u1 = USAGE.as("u1");
		UsageDefinition ud1 = USAGE_DEFINITION.as("ud1");
		UsageTranslation ut1 = USAGE_TRANSLATION.as("ut1");
		MeaningNote mn1 = MEANING_NOTE.as("mn1");
		MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
		Freeform ff1 = FREEFORM.as("ff1");
		Condition where1, where2;

		// word select
		where2 = l1.WORD_ID.eq(w1.ID);
		Condition where3 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w1.VALUE, DSL.noCondition(), true);
		Condition where4 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w1.VALUE_AS_WORD, DSL.noCondition(), true);
		where2 = where2.and(DSL.or(where3, where4));
		where1 = DSL.exists(DSL.select(w1.ID).from(w1).where(where2));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectWord = DSL.select(l1.MEANING_ID).from(l1).where(where1).groupBy(l1.MEANING_ID);

		// definition select
		where1 = l1.MEANING_ID.eq(d1.MEANING_ID);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, d1.VALUE, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectDefinition = DSL.select(l1.MEANING_ID).from(l1, d1).where(where1).groupBy(l1.MEANING_ID);

		// definition note select
		where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dn1.DEFINITION_ID.eq(d1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, dn1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinitionNote = DSL.select(l1.MEANING_ID).from(l1, m1, d1, dn1).where(where1).groupBy(l1.MEANING_ID);

		// meaning ff select
		String[] meaningFreeformTypes = new String[] {FreeformType.CONCEPT_ID.name(), FreeformType.LEARNER_COMMENT.name()};
		where1 = ff1.TYPE.in(meaningFreeformTypes).and(mff1.FREEFORM_ID.eq(ff1.ID)).and(mff1.MEANING_ID.eq(l1.MEANING_ID));
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, ff1.VALUE_TEXT, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectMeaningFreeform = DSL.select(l1.MEANING_ID).from(l1, mff1, ff1).where(where1).groupBy(l1.MEANING_ID);

		// meaning note select
		where1 = l1.MEANING_ID.eq(mn1.MEANING_ID);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, mn1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectMeaningNote = DSL.select(l1.MEANING_ID).from(l1, mn1).where(where1).groupBy(l1.MEANING_ID);

		// lexeme ff select
		String[] lexemeFreeformTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name()};
		where1 = ff1.TYPE.in(lexemeFreeformTypes).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID));
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, filteredCriteria, ff1.VALUE_TEXT, where1, true);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		SelectHavingStep<Record1<Long>> selectLexemeFreeform = DSL.select(l1.MEANING_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.MEANING_ID);

		// lexeme note select
		where1 = ln1.LEXEME_ID.eq(l1.ID);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ln1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectLexemeNote = DSL.select(l1.MEANING_ID).from(l1, ln1).where(where1).groupBy(l1.MEANING_ID);

		// usage select
		where1 = u1.LEXEME_ID.eq(l1.ID);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectUsage = DSL.select(l1.MEANING_ID).from(l1, u1).where(where1).groupBy(l1.MEANING_ID);

		// usage definition select
		where1 = u1.LEXEME_ID.eq(l1.ID).and(ud1.USAGE_ID.eq(u1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ud1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectUsageDefinition = DSL.select(l1.MEANING_ID).from(l1, u1, ud1).where(where1).groupBy(l1.MEANING_ID);

		// usage translation select
		where1 = u1.LEXEME_ID.eq(l1.ID).and(ut1.USAGE_ID.eq(u1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ut1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectUsageTranslation = DSL.select(l1.MEANING_ID).from(l1, u1, ut1).where(where1).groupBy(l1.MEANING_ID);

		// union all
		Table<Record1<Long>> a1 = selectWord
				.unionAll(selectDefinition)
				.unionAll(selectDefinitionNote)
				.unionAll(selectMeaningFreeform)
				.unionAll(selectMeaningNote)
				.unionAll(selectLexemeFreeform)
				.unionAll(selectLexemeNote)
				.unionAll(selectUsage)
				.unionAll(selectUsageDefinition)
				.unionAll(selectUsageTranslation)
				.asTable("a1");

		wherem = wherem.andExists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID)));
		return wherem;
	}

	private Condition composeCluelessSourceFilter(Meaning m1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) throws Exception {

		Lexeme l1 = LEXEME.as("l1");
		LexemeSourceLink lsl1 = LEXEME_SOURCE_LINK.as("lsl1");
		LexemeNote ln1 = LEXEME_NOTE.as("ln1");
		LexemeNoteSourceLink lnsl1 = LEXEME_NOTE_SOURCE_LINK.as("lnsl1");
		Usage u1 = USAGE.as("u1");
		UsageSourceLink usl1 = USAGE_SOURCE_LINK.as("usl1");
		MeaningNote mn1 = MEANING_NOTE.as("mn1");
		MeaningNoteSourceLink mnsl1 = MEANING_NOTE_SOURCE_LINK.as("mnsl1");
		Definition d1 = DEFINITION.as("d1");
		DefinitionSourceLink dsl1 = DEFINITION_SOURCE_LINK.as("dsl1");
		DefinitionNote dn1 = DEFINITION_NOTE.as("dn1");
		DefinitionNoteSourceLink dnsl1 = DEFINITION_NOTE_SOURCE_LINK.as("dnsl1");

		Condition where1;

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isNotEmpty(filteredCriteria)) {

			where1 = lsl1.LEXEME_ID.eq(l1.ID);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1, lsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = ln1.LEXEME_ID.eq(l1.ID).and(lnsl1.LEXEME_NOTE_ID.eq(ln1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, ln1, lnsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = u1.LEXEME_ID.eq(l1.ID).and(usl1.USAGE_ID.eq(u1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, u1, usl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(mn1.MEANING_ID).and(mnsl1.MEANING_NOTE_ID.eq(mn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, mn1, mnsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dsl1.DEFINITION_ID.eq(d1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dn1.DEFINITION_ID.eq(d1.ID)).and(dnsl1.DEFINITION_NOTE_ID.eq(dn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dn1, dnsl1).where(where1).groupBy(l1.MEANING_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectLexemeNoteSourceLinks)
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectMeaningNoteSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.unionAll(selectDefinitionNoteSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID)));
		}

		filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_ID);

		if (CollectionUtils.isNotEmpty(filteredCriteria)) {

			where1 = lsl1.LEXEME_ID.eq(l1.ID);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, lsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1, lsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = ln1.LEXEME_ID.eq(l1.ID).and(lnsl1.LEXEME_NOTE_ID.eq(ln1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, lnsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, ln1, lnsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = u1.LEXEME_ID.eq(l1.ID).and(usl1.USAGE_ID.eq(u1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, usl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, u1, usl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(mn1.MEANING_ID).and(mnsl1.MEANING_NOTE_ID.eq(mn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, mnsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, mn1, mnsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dsl1.DEFINITION_ID.eq(d1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dsl1).where(where1).groupBy(l1.MEANING_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dn1.DEFINITION_ID.eq(d1.ID)).and(dnsl1.DEFINITION_NOTE_ID.eq(dn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dnsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dn1, dnsl1).where(where1).groupBy(l1.MEANING_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectLexemeNoteSourceLinks)
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectMeaningNoteSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.unionAll(selectDefinitionNoteSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("meaning_id")).from(a1).where(a1.field("meaning_id", Long.class).eq(m1.ID)));
		}

		return where;
	}

	private Condition applyMeaningActivityLogFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition wherem) throws Exception {

		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		MeaningLastActivityLog mlal = MEANING_LAST_ACTIVITY_LOG.as("mlal");
		ActivityLog al = ACTIVITY_LOG.as("al");

		List<SearchCriterion> filteredCriteriaByCreatedOrUpdatedByOnly = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY);
		boolean isFilterByCreatedOrUpdatedByOnly = CollectionUtils.isNotEmpty(filteredCriteriaByCreatedOrUpdatedByOnly);

		// by all logs
		boolean isFilterByAllLogs = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

		if (isFilterByAllLogs || isFilterByCreatedOrUpdatedByOnly) {

			List<SearchCriterion> filteredCriteriaByAllLogs = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

			if (CollectionUtils.isNotEmpty(filteredCriteriaByAllLogs)) {

				Condition where1 = mal.MEANING_ID.eq(meaningIdField).and(mal.ACTIVITY_LOG_ID.eq(al.ID));

				for (SearchCriterion criterion : filteredCriteriaByAllLogs) {
					String critValue = criterion.getSearchValue().toString();
					if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
						where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
					} else if (SearchKey.UPDATED_ON.equals(criterion.getSearchKey())) {
						where1 = where1
								.andNot(al.ENTITY_NAME.eq(ActivityEntity.GRAMMAR.name()))
								.andNot(al.FUNCT_NAME.eq(JOIN).and(al.ENTITY_NAME.eq(ActivityEntity.WORD.name())))
								.andNot(al.FUNCT_NAME.eq(JOIN_WORDS))
								.andNot(al.ENTITY_NAME.eq(ActivityEntity.MEANING.name()).and(al.FUNCT_NAME.like(LIKE_CREATE)));
						where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
					} else if (SearchKey.CREATED_ON.equals(criterion.getSearchKey())) {
						where1 = where1
								.and(al.OWNER_NAME.eq(ActivityOwner.MEANING.name()))
								.and(al.OWNER_ID.eq(meaningIdField))
								.and(al.ENTITY_NAME.eq(ActivityEntity.MEANING.name()))
								.and(al.FUNCT_NAME.like(LIKE_CREATE));
						where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
					}
				}
				wherem = wherem.andExists(DSL.select(mal.ID).from(mal, al).where(where1));
			}
		}

		// by last logs
		boolean isFilterByLastLogs = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LAST_UPDATE_ON);

		if (isFilterByLastLogs) {

			List<SearchCriterion> filteredCriteriaByLastLogs = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.LAST_UPDATE_ON);

			if (CollectionUtils.isNotEmpty(filteredCriteriaByLastLogs)) {

				Condition where1 = mlal.MEANING_ID.eq(meaningIdField).and(mlal.ACTIVITY_LOG_ID.eq(al.ID));

				for (SearchCriterion criterion : filteredCriteriaByLastLogs) {
					String critValue = criterion.getSearchValue().toString();
					if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
						where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
					} else if (SearchKey.LAST_UPDATE_ON.equals(criterion.getSearchKey())) {
						where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
					}
				}
				wherem = wherem.andExists(DSL.select(mlal.ID).from(mlal, al).where(where1));
			}
		}

		return wherem;
	}

	private Condition applyMeaningManualEventOnFilters(List<SearchCriterion> searchCriteria, Field<Timestamp> meaningManualEventOn, Condition wherem) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.MANUAL_UPDATE_ON);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherem;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			wherem = searchFilterHelper.applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), meaningManualEventOn, wherem, false);
		}

		return wherem;
	}
}
