package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.Tables.USAGE;
import static eki.ekilex.data.db.Tables.USAGE_DEFINITION;
import static eki.ekilex.data.db.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectHavingStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.FreeformConstant;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionNote;
import eki.ekilex.data.db.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeNote;
import eki.ekilex.data.db.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningNote;
import eki.ekilex.data.db.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.tables.MeaningRelation;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.ParadigmForm;
import eki.ekilex.data.db.tables.Usage;
import eki.ekilex.data.db.tables.UsageDefinition;
import eki.ekilex.data.db.tables.UsageSourceLink;
import eki.ekilex.data.db.tables.UsageTranslation;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordFreeform;

@Component
public class LexSearchConditionComposer implements GlobalConstant, ActivityFunct, FreeformConstant {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	public Condition createSearchCondition(Word word, String searchWordCrit, SearchDatasetsRestriction searchDatasetsRestriction) {

		String maskedSearchFilter = searchWordCrit.replace(SEARCH_MASK_CHARS, "%").replace(SEARCH_MASK_CHAR, "_");
		Field<String> filterField = DSL.lower(maskedSearchFilter);

		Condition where = composeWordDatasetsCondition(word, searchDatasetsRestriction);
		where = where.and(word.IS_WORD.isTrue());
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			where = where.and(DSL.or(DSL.lower(word.VALUE).like(filterField), DSL.lower(word.VALUE_AS_WORD).like(filterField)));
		} else {
			where = where.and(DSL.or(DSL.lower(word.VALUE).eq(filterField), DSL.lower(word.VALUE_AS_WORD).eq(filterField)));
		}
		return where;
	}

	public Condition createSearchCondition(Word w1, List<SearchCriterionGroup> searchCriteriaGroups, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Condition where = composeWordDatasetsCondition(w1, searchDatasetsRestriction);
		where = where.and(w1.IS_WORD.isTrue());

		for (SearchCriterionGroup searchCriterionGroup : searchCriteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			searchCriteria = searchCriteria.stream().filter(searchCriterion -> StringUtils.isBlank(searchCriterion.getValidationMessage())).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				boolean containsSearchKeys;

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.VALUE);

				if (containsSearchKeys) {

					Condition where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w1.VALUE, DSL.noCondition(), true);
					Condition where2 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w1.VALUE_AS_WORD, DSL.noCondition(), true);
					where = where.and(DSL.or(where1, where2));
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria,
						SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE, SearchKey.PUBLICITY, SearchKey.LEXEME_GRAMMAR,
						SearchKey.LEXEME_VALUE_STATE, SearchKey.LEXEME_PROFICIENCY_LEVEL, SearchKey.LEXEME_RELATION, SearchKey.LEXEME_GOVERNMENT,
						SearchKey.COMPLEXITY, SearchKey.LEXEME_POS, SearchKey.LEXEME_REGISTER, SearchKey.LEXEME_DERIV, SearchKey.LEXEME_NOTE);

				if (containsSearchKeys) {

					Condition where1 = l1.WORD_ID.eq(w1.ID);
					where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, l1.IS_PUBLIC, where1);
					where1 = searchFilterHelper.applyLexemeSourceRefFilter(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeSourceFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeFreeformFilters(SearchKey.LEXEME_GRAMMAR, GRAMMAR_CODE, searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeFreeformFilters(SearchKey.LEXEME_GOVERNMENT, GOVERNMENT_CODE, searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeNoteFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeRegisterValueFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeRegisterExistsFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeDerivValueFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeDerivExistsFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeRelationValueFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeRelationExistsFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemePosValueFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemePosExistsFilters(searchCriteria, l1.ID, where1);
					where1 = searchFilterHelper.applyLexemeComplexityFilters(searchCriteria, l1.COMPLEXITY, where1);
					where1 = searchFilterHelper.applyLexemeValueStateFilters(searchCriteria, l1.VALUE_STATE_CODE, where1);
					where1 = searchFilterHelper.applyLexemeProficiencyLevelFilters(searchCriteria, l1.PROFICIENCY_LEVEL_CODE, where1);
					where = where.andExists(DSL.select(l1.ID).from(l1).where(where1));
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.SECONDARY_MEANING_WORD);
				if (containsSearchKeys) {

					MeaningRelation mr = MEANING_RELATION.as("mr");
					Lexeme l2 = Lexeme.LEXEME.as("l2");
					Word w2 = Word.WORD.as("w2");

					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(mr.MEANING1_ID.eq(l1.MEANING_ID))
							.and(mr.MEANING2_ID.eq(l2.MEANING_ID))
							.and(mr.MEANING_REL_TYPE_CODE.eq(MEANING_REL_TYPE_CODE_SIMILAR))
							.and(l2.WORD_ID.eq(w2.ID))
							.and(w2.LANG.eq(w1.LANG))
							.and(w2.IS_PUBLIC.isTrue())
							.and(w2.IS_WORD.isTrue());

					where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);

					boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.SECONDARY_MEANING_WORD, searchCriteria);
					if (isNotExistsSearch) {
						where = where.andNotExists(DSL.select(l1.ID).from(l1, l2, w2, mr).where(where1));
					} else {
						Condition where2 = searchFilterHelper.applyValueFilters(SearchKey.SECONDARY_MEANING_WORD, searchCriteria, w2.VALUE, DSL.noCondition(), true);
						Condition where3 = searchFilterHelper.applyValueFilters(SearchKey.SECONDARY_MEANING_WORD, searchCriteria, w2.VALUE_AS_WORD, DSL.noCondition(), true);
						where1 = where1.and(DSL.or(where2, where3));
						where = where.andExists(DSL.select(l1.ID).from(l1, l2, w2, mr).where(where1));
					}
				}

				where = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyCommaSeparatedIdsFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where, false);
				where = searchFilterHelper.applyWordDisplayMorphFilters(searchCriteria, w1.DISPLAY_MORPH_CODE, where);
				where = searchFilterHelper.applyWordAspectFilters(searchCriteria, w1.ASPECT_CODE, where);
				where = searchFilterHelper.applyWordVocalFormFilters(searchCriteria, w1.VOCAL_FORM, where);
				where = searchFilterHelper.applyWordMorphophonoFormFilters(searchCriteria, w1.MORPHOPHONO_FORM, where);
				where = searchFilterHelper.applyWordTypeValueFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordTypeExistsFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordFrequencyFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordRelationValueFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordRelationExistsFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordForumFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordActivityLogFilters(searchCriteria, w1.ID, where);

			} else if (SearchEntity.WORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Word w2 = Word.WORD.as("w2");

				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.MEANING_ID.eq(l2.MEANING_ID))
						.and(l2.WORD_ID.eq(w2.ID))
						.and(w2.IS_PUBLIC.isTrue())
						.and(w2.IS_WORD.isTrue())
						.and(l1.ID.ne(l2.ID));
				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);

				boolean containsSearchKeys;

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria,
						SearchKey.VALUE, SearchKey.ID, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE, SearchKey.ASPECT, SearchKey.WORD_TYPE,
						SearchKey.PUBLICITY, SearchKey.LEXEME_GRAMMAR, SearchKey.LEXEME_GOVERNMENT, SearchKey.LEXEME_POS,
						SearchKey.LEXEME_REGISTER, SearchKey.LEXEME_VALUE_STATE, SearchKey.COMPLEXITY,
						SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

				if (containsSearchKeys) {

					boolean containsSearchKeys1 = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.VALUE);
					if (containsSearchKeys1) {
						Condition where2 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w2.VALUE, DSL.noCondition(), true);
						Condition where3 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w2.VALUE_AS_WORD, DSL.noCondition(), true);
						where1 = where1.and(DSL.or(where2, where3));
					}
					boolean containsSearchKeys2 = searchFilterHelper.containsSearchKeys(searchCriteria,
							SearchKey.ID, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE, SearchKey.ASPECT, SearchKey.WORD_TYPE,
							SearchKey.PUBLICITY, SearchKey.LEXEME_GRAMMAR, SearchKey.LEXEME_GOVERNMENT, SearchKey.LEXEME_POS,
							SearchKey.LEXEME_REGISTER, SearchKey.LEXEME_VALUE_STATE, SearchKey.COMPLEXITY);
					if (containsSearchKeys2) {
						where1 = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, w2.ID, where1);
						where1 = searchFilterHelper.applyWordAspectFilters(searchCriteria, w2.ASPECT_CODE, where1);
						where1 = searchFilterHelper.applyWordTypeValueFilters(searchCriteria, w2.ID, where1);
						where1 = searchFilterHelper.applyWordTypeExistsFilters(searchCriteria, w2.ID, where1);
						where1 = searchFilterHelper.applyLexemeSourceRefFilter(searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemeSourceFilters(searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, l2.IS_PUBLIC, where1);
						where1 = searchFilterHelper.applyLexemeFreeformFilters(SearchKey.LEXEME_GRAMMAR, GRAMMAR_CODE, searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemeFreeformFilters(SearchKey.LEXEME_GOVERNMENT, GOVERNMENT_CODE, searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemePosValueFilters(searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemePosExistsFilters(searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemeRegisterValueFilters(searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemeRegisterExistsFilters(searchCriteria, l2.ID, where1);
						where1 = searchFilterHelper.applyLexemeValueStateFilters(searchCriteria, l2.VALUE_STATE_CODE, where1);
						where1 = searchFilterHelper.applyLexemeComplexityFilters(searchCriteria, l2.COMPLEXITY, where1);
					}
					boolean containsSearchKeys3 = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);
					if (containsSearchKeys3) {
						where1 = searchFilterHelper.applyWordActivityLogFilters(searchCriteria, w2.ID, where1);
					}
					where = where.andExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
				}

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LANGUAGE);
				if (containsSearchKeys) {
					List<SearchCriterion> equalsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(
							searchCriteria, SearchKey.LANGUAGE, SearchOperand.EQUALS);
					List<SearchCriterion> notContainsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(
							searchCriteria, SearchKey.LANGUAGE, SearchOperand.NOT_CONTAINS);

					if (CollectionUtils.isNotEmpty(equalsValueCriteria)) {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, equalsValueCriteria, w2.LANG, where1, false);
						where = where.andExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
					}
					if (CollectionUtils.isNotEmpty(notContainsValueCriteria)) {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, notContainsValueCriteria, w2.LANG, where1, false);
						where = where.andNotExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
					}
				}

			} else if (SearchEntity.TAG.equals(searchEntity)) {

				where = searchFilterHelper.applyTagFilters(searchCriteria, searchDatasetsRestriction, w1, where);
				where = searchFilterHelper.applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, ActivityEntity.TAG, w1, where);

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				ParadigmForm pf1 = PARADIGM_FORM.as("pf1");
				Form f1 = Form.FORM.as("f1");
				Condition where1 = p1.WORD_ID.eq(w1.ID)
						.and(pf1.PARADIGM_ID.eq(p1.ID))
						.and(pf1.FORM_ID.eq(f1.ID))
						.and(pf1.MORPH_EXISTS.isTrue())
						.and(pf1.IS_QUESTIONABLE.isFalse())
						.and(f1.MORPH_CODE.ne(UNKNOWN_FORM_CODE));

				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
				where1 = searchFilterHelper.applyFormFrequencyFilters(searchCriteria, f1.ID, where1);
				where = where.andExists(DSL.select(f1.ID).from(p1, pf1, f1).where(where1));

				where = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where, false);

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");

				Condition where1 = l1.WORD_ID.eq(w1.ID).and(l1.MEANING_ID.eq(m1.ID));

				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyDomainValueFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyDomainExistsFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningSemanticTypeValueFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningSemanticTypeExistsFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyIdFilters(SearchKey.ID, searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyCommaSeparatedIdsFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningRelationValueFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningRelationExistsFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningForumFilters(searchCriteria, m1.ID, where1);
				where1 = searchFilterHelper.applyMeaningNoteFilters(searchCriteria, m1.ID, where1);
				where = where.andExists(DSL.select(m1.ID).from(l1, m1).where(where1));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				Definition d1 = DEFINITION.as("d1");
				Condition where2 = l1.WORD_ID.eq(w1.ID)
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(d1.MEANING_ID.eq(m1.ID));
				where2 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where2);
				Condition where1;

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					where = where.andNotExists(DSL.select(d1.ID).from(l1, m1, d1).where(where2));
				} else {
					where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, d1.VALUE, where2, true);
					where1 = searchFilterHelper.applyDefinitionSourceRefFilter(searchCriteria, d1.ID, where1);
					where1 = searchFilterHelper.applyDefinitionSourceFilters(searchCriteria, d1.ID, where1);
					where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, d1.IS_PUBLIC, where1);
					where1 = searchFilterHelper.applyLexemeComplexityFilters(searchCriteria, d1.COMPLEXITY, where1);
					where1 = searchFilterHelper.applyDefinitionNoteFilters(searchCriteria, d1.ID, where1);
					where = where.andExists(DSL.select(d1.ID).from(l1, m1, d1).where(where1));
				}

				boolean containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LANGUAGE);
				if (containsSearchKeys) {
					List<SearchCriterion> equalsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.EQUALS);
					List<SearchCriterion> notContainsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.NOT_CONTAINS);

					if (CollectionUtils.isNotEmpty(equalsValueCriteria)) {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, equalsValueCriteria, d1.LANG, where2, false);
						where = where.andExists(DSL.select(d1.ID).from(l1, m1, d1).where(where1));
					}
					if (CollectionUtils.isNotEmpty(notContainsValueCriteria)) {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, notContainsValueCriteria, d1.LANG, where2, false);
						where = where.andNotExists(DSL.select(d1.ID).from(l1, m1, d1).where(where1));
					}
				}

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Usage u1 = USAGE.as("u1");
				Lexeme l1 = LEXEME.as("l1");

				Condition where2 = l1.WORD_ID.eq(w1.ID)
						.and(u1.LEXEME_ID.eq(l1.ID));
				where2 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where2);
				Condition where1;

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);
				if (isNotExistsSearch) {
					where = where.andNotExists(DSL.select(u1.ID).from(l1, u1).where(where2));
				} else {
					where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, u1.VALUE, where2, true);
					where1 = searchFilterHelper.applyUsageSourceFilters(searchCriteria, u1.ID, where1);
					where1 = searchFilterHelper.applyUsageSourceRefFilter(searchCriteria, u1.ID, where1);
					where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, u1.IS_PUBLIC, where1);
					where1 = searchFilterHelper.applyLexemeComplexityFilters(searchCriteria, u1.COMPLEXITY, where1);

					where = where.andExists(DSL.select(u1.ID).from(l1, u1).where(where1));
				}

				boolean containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.LANGUAGE);
				if (containsSearchKeys) {
					List<SearchCriterion> equalsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.EQUALS);
					List<SearchCriterion> notContainsValueCriteria = searchFilterHelper.filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LANGUAGE, SearchOperand.NOT_CONTAINS);

					if (CollectionUtils.isNotEmpty(equalsValueCriteria)) {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, equalsValueCriteria, u1.LANG, where2, false);
						where = where.andExists(DSL.select(u1.ID).from(l1, u1).where(where1));
					}
					if (CollectionUtils.isNotEmpty(notContainsValueCriteria)) {
						where1 = searchFilterHelper.applyValueFilters(SearchKey.LANGUAGE, notContainsValueCriteria, u1.LANG, where2, false);
						where = where.andNotExists(DSL.select(u1.ID).from(l1, u1).where(where1));
					}
				}

			} else if (SearchEntity.NOTE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeNote ln1 = LEXEME_NOTE.as("ln1");
				Definition d1 = DEFINITION.as("d1");
				DefinitionNote dn1 = DEFINITION_NOTE.as("dn1");
				Meaning m1 = MEANING.as("m1");
				MeaningNote mn1 = MEANING_NOTE.as("mn1");

				Condition where1;

				// definition note select
				where1 = l1.MEANING_ID.eq(m1.ID)
						.and(d1.MEANING_ID.eq(m1.ID))
						.and(dn1.DEFINITION_ID.eq(d1.ID));
				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, dn1.VALUE, where1, true);
				where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, dn1.IS_PUBLIC, where1);
				where1 = searchFilterHelper.applyDefinitionNoteSourceFilters(searchCriteria, dn1.ID, where1);
				where1 = searchFilterHelper.applyDefinitionNoteSourceRefFilter(searchCriteria, dn1.ID, where1);
				SelectHavingStep<Record1<Long>> selectDefinitionNote = DSL.select(l1.WORD_ID).from(l1, m1, d1, dn1).where(where1).groupBy(l1.WORD_ID);

				// lexeme note select
				where1 = ln1.LEXEME_ID.eq(l1.ID);
				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, ln1.VALUE, where1, true);
				where1 = searchFilterHelper.applyPublicityFilters(searchCriteria, ln1.IS_PUBLIC, where1);
				where1 = searchFilterHelper.applyLexemeNoteSourceFilters(searchCriteria, ln1.ID, where1);
				where1 = searchFilterHelper.applyLexemeNoteSourceRefFilter(searchCriteria, ln1.ID, where1);
				SelectHavingStep<Record1<Long>> selectLexemeNote = DSL.select(l1.WORD_ID).from(l1, ln1).where(where1).groupBy(l1.WORD_ID);

				// meaning note select
				where1 = l1.MEANING_ID.eq(m1.ID).and(mn1.MEANING_ID.eq(m1.ID));
				where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE_AND_EXISTS, searchCriteria, mn1.VALUE, where1, true);
				where1 = searchFilterHelper.applyMeaningNoteSourceFilters(searchCriteria, mn1.ID, where1);
				where1 = searchFilterHelper.applyMeaningNoteSourceRefFilter(searchCriteria, mn1.ID, where1);
				SelectHavingStep<Record1<Long>> selectMeaningNote = DSL.select(l1.WORD_ID).from(l1, m1, mn1).where(where1).groupBy(l1.WORD_ID);

				Table<Record1<Long>> n1 = selectDefinitionNote
						.unionAll(selectLexemeNote)
						.unionAll(selectMeaningNote)
						.asTable("n1");

				boolean isNotExistsSearch = searchFilterHelper.isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, searchCriteria);

				if (isNotExistsSearch) {
					where = where.andNotExists(DSL.select(n1.field("word_id")).from(n1).where(n1.field("word_id", Long.class).eq(w1.ID)));
				} else {
					where = where.andExists(DSL.select(n1.field("word_id")).from(n1).where(n1.field("word_id", Long.class).eq(w1.ID)));
				}

			} else if (SearchEntity.OD_RECOMMENDATION.equals(searchEntity)) {

				where = searchFilterHelper.applyWordOdRecommendationValueFilters(searchCriteria, w1.ID, where);
				where = searchFilterHelper.applyWordOdRecommendationModificationFilters(searchCriteria, w1.ID, where);

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
		Condition where = DSL.exists(DSL.select(lfd.ID).from(lfd).where(lfd.WORD_ID.eq(word.ID).and(dsFiltWhere)));
		return where;
	}

	private Condition composeCluelessValueFilter(
			Word w1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		Definition d1 = DEFINITION.as("d1");
		DefinitionNote dn1 = DEFINITION_NOTE.as("dn1");
		Lexeme l1 = LEXEME.as("l1");
		LexemeNote ln1 = LEXEME_NOTE.as("ln1");
		Usage u1 = USAGE.as("u1");
		UsageDefinition ud1 = USAGE_DEFINITION.as("ud1");
		UsageTranslation ut1 = USAGE_TRANSLATION.as("ut1");
		Meaning m1 = MEANING.as("m1");
		MeaningNote mn1 = MEANING_NOTE.as("mn1");
		MeaningFreeform mff1 = MEANING_FREEFORM.as("mff1");
		LexemeFreeform lff1 = LEXEME_FREEFORM.as("lff1");
		WordFreeform wff1 = WORD_FREEFORM.as("wff1");
		Freeform ff1 = FREEFORM.as("ff1");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Form f1 = FORM.as("f1");
		Paradigm p1 = PARADIGM.as("p1");
		ParadigmForm pf1 = PARADIGM_FORM.as("pf1");

		Condition where1;

		// word and meaningword select
		where1 = l1.MEANING_ID.eq(l2.MEANING_ID)
				.and(l2.WORD_ID.eq(w2.ID))
				.and(w2.IS_PUBLIC.isTrue());
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
		Condition where2 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w2.VALUE, DSL.noCondition(), true);
		Condition where3 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, w2.VALUE_AS_WORD, DSL.noCondition(), true);
		where1 = where1.and(DSL.or(where2, where3));
		SelectHavingStep<Record1<Long>> selectWordAndMeaningWord = DSL.select(l1.WORD_ID).from(l1, l2, w2).where(where1).groupBy(l1.WORD_ID);

		// definition select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(d1.MEANING_ID.eq(m1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinition = DSL.select(l1.WORD_ID).from(l1, m1, d1).where(where1).groupBy(l1.WORD_ID);

		// definition note select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(d1.MEANING_ID.eq(m1.ID))
				.and(dn1.DEFINITION_ID.eq(d1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, dn1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinitionNote = DSL.select(l1.WORD_ID).from(l1, m1, d1, dn1).where(where1).groupBy(l1.WORD_ID);

		// lexeme note select
		where1 = ln1.LEXEME_ID.eq(l1.ID);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ln1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectLexemeNote = DSL.select(l1.WORD_ID).from(l1, ln1).where(where1).groupBy(l1.WORD_ID);

		// usage select
		where1 = u1.LEXEME_ID.eq(l1.ID);
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectUsage = DSL.select(l1.WORD_ID).from(l1, u1).where(where1).groupBy(l1.WORD_ID);

		// usage definition select
		where1 = u1.LEXEME_ID.eq(l1.ID).and(ud1.USAGE_ID.eq(u1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ud1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectUsageDefinition = DSL.select(l1.WORD_ID).from(l1, u1, ud1).where(where1).groupBy(l1.WORD_ID);

		// usage translation select
		where1 = u1.LEXEME_ID.eq(l1.ID).and(ut1.USAGE_ID.eq(u1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ut1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectUsageTranslation = DSL.select(l1.WORD_ID).from(l1, u1, ut1).where(where1).groupBy(l1.WORD_ID);

		// lexeme ff select
		where1 = lff1.LEXEME_ID.eq(l1.ID)
				.and(lff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.FREEFORM_TYPE_CODE.in(CLUELESS_SEARCH_LEXEME_FF_TYPE_CODES));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectLexemeFreeform = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// meaning note select
		where1 = l1.MEANING_ID.eq(m1.ID).and(mn1.MEANING_ID.eq(m1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, mn1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectMeaningNote = DSL.select(l1.WORD_ID).from(l1, m1, mn1).where(where1).groupBy(l1.WORD_ID);

		// meaning ff select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(mff1.MEANING_ID.eq(m1.ID))
				.and(mff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.FREEFORM_TYPE_CODE.in(CLUELESS_SEARCH_MEANING_FF_TYPE_CODES));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectMeaningFreeform = DSL.select(l1.WORD_ID).from(l1, m1, mff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// form select
		where1 = p1.WORD_ID.eq(l1.WORD_ID)
				.and(pf1.PARADIGM_ID.eq(p1.ID))
				.and(pf1.FORM_ID.eq(f1.ID));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectForm = DSL.select(l1.WORD_ID).from(l1, p1, pf1, f1).where(where1).groupBy(l1.WORD_ID);

		// word od recoomendation select
		where1 = l1.WORD_ID.eq(w1.ID)
				.and(wff1.WORD_ID.eq(w1.ID))
				.and(wff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.FREEFORM_TYPE_CODE.eq(OD_WORD_RECOMMENDATION_CODE));
		where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectWordOdRecommendation = DSL.select(l1.WORD_ID).from(l1, w1, wff1, ff1).where(where1).groupBy(l1.WORD_ID);

		Table<Record1<Long>> a1 = selectWordAndMeaningWord
				.unionAll(selectDefinition)
				.unionAll(selectDefinitionNote)
				.unionAll(selectMeaningNote)
				.unionAll(selectMeaningFreeform)
				.unionAll(selectLexemeNote)
				.unionAll(selectUsage)
				.unionAll(selectUsageDefinition)
				.unionAll(selectUsageTranslation)
				.unionAll(selectLexemeFreeform)
				.unionAll(selectForm)
				.unionAll(selectWordOdRecommendation)
				.asTable("a1");

		where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		return where;
	}

	private Condition composeCluelessSourceFilter(
			Word w1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) throws Exception {

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
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1, lsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ln1.LEXEME_ID.eq(l1.ID).and(lnsl1.LEXEME_NOTE_ID.eq(ln1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, ln1, lnsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = u1.LEXEME_ID.eq(l1.ID).and(usl1.USAGE_ID.eq(u1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, u1, usl1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(mn1.MEANING_ID).and(mnsl1.MEANING_NOTE_ID.eq(mn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, mn1, mnsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dsl1.DEFINITION_ID.eq(d1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dn1.DEFINITION_ID.eq(d1.ID)).and(dnsl1.DEFINITION_NOTE_ID.eq(dn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dn1, dnsl1).where(where1).groupBy(l1.WORD_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectLexemeNoteSourceLinks)
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectMeaningNoteSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.unionAll(selectDefinitionNoteSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		}

		filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_ID);

		if (CollectionUtils.isNotEmpty(filteredCriteria)) {

			where1 = lsl1.LEXEME_ID.eq(l1.ID);
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, lsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1, lsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ln1.LEXEME_ID.eq(l1.ID).and(lnsl1.LEXEME_NOTE_ID.eq(ln1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, lnsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, ln1, lnsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = u1.LEXEME_ID.eq(l1.ID).and(usl1.USAGE_ID.eq(u1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, usl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, u1, usl1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(mn1.MEANING_ID).and(mnsl1.MEANING_NOTE_ID.eq(mn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, mnsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, mn1, mnsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dsl1.DEFINITION_ID.eq(d1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(dn1.DEFINITION_ID.eq(d1.ID)).and(dnsl1.DEFINITION_NOTE_ID.eq(dn1.ID));
			where1 = searchFilterHelper.applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			where1 = searchFilterHelper.applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dnsl1.SOURCE_ID, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dn1, dnsl1).where(where1).groupBy(l1.WORD_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectLexemeNoteSourceLinks)
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectMeaningNoteSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.unionAll(selectDefinitionNoteSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		}

		return where;
	}
}
