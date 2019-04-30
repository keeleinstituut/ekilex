package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import eki.common.constant.DbConstant;
import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;

public abstract class AbstractSearchDbService implements SystemConstant, DbConstant {

	protected Condition composeLexemeDatasetsCondition(Lexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction) {

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();
		boolean noDatasetsFiltering = searchDatasetsRestriction.isNoDatasetsFiltering();
		boolean allDatasetsPermissions = searchDatasetsRestriction.isAllDatasetsPermissions();

		Condition dsFiltWhere;

		if (noDatasetsFiltering) {
			if (allDatasetsPermissions) {
				//no restrictions
				dsFiltWhere = DSL.trueCondition();
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//all ds, only public
				dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC);
			} else {
				//all ds, selected perm
				dsFiltWhere = DSL.or(
						lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC),
						lexeme.DATASET_CODE.in(userPermDatasetCodes));
			}
		} else {
			if (allDatasetsPermissions) {
				//selected ds, full perm
				dsFiltWhere = lexeme.DATASET_CODE.in(filteringDatasetCodes);
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//selected ds, only public
				dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes));
			} else {
				Collection<String> filteringPermDatasetCodes = CollectionUtils.intersection(filteringDatasetCodes, userPermDatasetCodes);
				if (CollectionUtils.isEmpty(filteringPermDatasetCodes)) {
					//selected ds, only public
					dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes));
				} else {
					//selected ds, some perm, some public
					dsFiltWhere = DSL.or(
							lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes)),
							lexeme.DATASET_CODE.in(filteringPermDatasetCodes));
				}
			}
		}
		return dsFiltWhere;
	}

	protected Condition applyDatasetRestrictions(Lexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) {

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();
		boolean noDatasetsFiltering = searchDatasetsRestriction.isNoDatasetsFiltering();
		boolean allDatasetsPermissions = searchDatasetsRestriction.isAllDatasetsPermissions();

		if (noDatasetsFiltering) {
			if (allDatasetsPermissions) {
				//no restrictions
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//all ds, only public
				where = where.and(lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC));
			} else {
				//all ds, selected perm
				where = where.and(DSL.or(
							lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC),
							lexeme.DATASET_CODE.in(userPermDatasetCodes))
						);
			}
		} else {
			if (allDatasetsPermissions) {
				//selected ds, full perm
				where = where.and(lexeme.DATASET_CODE.in(filteringDatasetCodes));
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//selected ds, only public
				where = where.and(lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes)));
			} else {
				Collection<String> filteringPermDatasetCodes = CollectionUtils.intersection(filteringDatasetCodes, userPermDatasetCodes);
				if (CollectionUtils.isEmpty(filteringPermDatasetCodes)) {
					//selected ds, only public
					where = where.and(lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes)));
				} else {
					//selected ds, some perm, some public
					where = where.and(DSL.or(
								lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes)),
								lexeme.DATASET_CODE.in(filteringPermDatasetCodes))
							);
				}
			}
		}
		return where;
	}

	protected Condition applyValueFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<String> valueField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey) && c.getSearchValue() != null)
				.collect(toList());

		if (filteredCriteria.isEmpty()) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValueStr = criterion.getSearchValue().toString();
			condition = applyValueFilter(searchValueStr, searchOperand, valueField, condition);
		}
		return condition;
	}

	protected Condition applyValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition) {

		searchValueStr = StringUtils.lowerCase(searchValueStr);
		if (SearchOperand.EQUALS.equals(searchOperand)) {
			condition = condition.and(searchField.lower().equal(searchValueStr));
		} else if (SearchOperand.STARTS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().startsWith(searchValueStr));
		} else if (SearchOperand.ENDS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().endsWith(searchValueStr));
		} else if (SearchOperand.CONTAINS.equals(searchOperand)) {
			condition = condition.and(searchField.lower().contains(searchValueStr));
		} else if (SearchOperand.CONTAINS_WORD.equals(searchOperand)) {
			condition = condition.and(DSL.field("to_tsvector('simple',{0}) @@ to_tsquery('simple',{1})",
					Boolean.class,
					searchField, DSL.inline(searchValueStr)));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	protected Condition applyLexemeSourceFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) {

		List<SearchCriterion> sourceCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		if (sourceCriteria.isEmpty()) {
			return condition;
		}
		if (SearchKey.SOURCE_NAME.equals(searchKey)) {
			return applyLexemeSourceNameFilter(sourceCriteria, lexemeIdField, condition);
		} else if (SearchKey.SOURCE_REF.equals(searchKey)) {
			return applyLexemeSourceRefFilter(sourceCriteria, lexemeIdField, condition);
		}
		return condition;
	}

	private Condition applyLexemeSourceRefFilter(List<SearchCriterion> sourceCriteria, Field<Long> lexemeIdField, Condition condition) {

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition sourceCondition =
				lsl.LEXEME_ID.eq(lexemeIdField)
				.and(lsl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lsl.VALUE, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
	}

	private Condition applyLexemeSourceNameFilter(List<SearchCriterion> sourceCriteria, Field<Long> lexemeIdField, Condition condition) {

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition =
				lsl.LEXEME_ID.eq(lexemeIdField)
				.and(lsl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
				.and(lsl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(lsl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyDefinitionSourceFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) {

		List<SearchCriterion> sourceCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		if (sourceCriteria.isEmpty()) {
			return condition;
		}
		if (SearchKey.SOURCE_NAME.equals(searchKey)) {
			return applyDefinitionSourceNameFilter(sourceCriteria, definitionIdField, condition);	
		} else if (SearchKey.SOURCE_REF.equals(searchKey)) {
			return applyDefinitionSourceRefFilter(sourceCriteria, definitionIdField, condition);
		}
		return condition;
	}

	private Condition applyDefinitionSourceRefFilter(List<SearchCriterion> sourceCriteria, Field<Long> definitionIdField, Condition condition) {

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");

		Condition sourceCondition =
				dsl.DEFINITION_ID.eq(definitionIdField)
				.and(dsl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), dsl.VALUE, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
	}

	private Condition applyDefinitionSourceNameFilter(List<SearchCriterion> sourceCriteria, Field<Long> definitionIdField, Condition condition) {

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition =
				dsl.DEFINITION_ID.eq(definitionIdField)
				.and(dsl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
				.and(dsl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(dsl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyFreeformSourceFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) {

		List<SearchCriterion> sourceCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		if (sourceCriteria.isEmpty()) {
			return condition;
		}
		if (SearchKey.SOURCE_NAME.equals(searchKey)) {
			return applyFreeformSourceNameFilter(sourceCriteria, freeformIdField, condition);	
		} else if (SearchKey.SOURCE_REF.equals(searchKey)) {
			return applyFreeformSourceRefFilter(sourceCriteria, freeformIdField, condition);
		}
		return condition;
	}

	private Condition applyFreeformSourceRefFilter(List<SearchCriterion> sourceCriteria, Field<Long> freeformIdField, Condition condition) {

		FreeformSourceLink usl = FREEFORM_SOURCE_LINK.as("usl");
		Condition sourceCondition =
				usl.FREEFORM_ID.eq(freeformIdField)
				.and(usl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), usl.VALUE, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(usl.ID).from(usl).where(sourceCondition)));
	}

	private Condition applyFreeformSourceNameFilter(List<SearchCriterion> sourceCriteria, Field<Long> freeformIdField, Condition condition) {

		FreeformSourceLink usl = FREEFORM_SOURCE_LINK.as("usl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition =
				usl.FREEFORM_ID.eq(freeformIdField)
				.and(usl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
				.and(usl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(usl, s, sff, ff).where(sourceCondition)));
	}
}
