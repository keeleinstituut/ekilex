package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_LIFECYCLE_LOG;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Record8;
import org.jooq.SelectHavingStep;
import org.jooq.SelectOrderByStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import eki.common.constant.ActivityEntity;
import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DatasetPermission;
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
import eki.ekilex.data.db.tables.LexemeActivityLog;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeLifecycleLog;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.LifecycleLog;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningLifecycleLog;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordFreeform;
import eki.ekilex.data.db.tables.WordLifecycleLog;

public abstract class AbstractSearchDbService extends AbstractDataDbService {

	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	protected Condition applyDatasetRestrictions(Lexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) {

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();
		boolean noDatasetsFiltering = searchDatasetsRestriction.isNoDatasetsFiltering();
		boolean allDatasetsPermissions = searchDatasetsRestriction.isAllDatasetsPermissions();
		boolean isSingleFilteringDataset = searchDatasetsRestriction.isSingleFilteringDataset();
		boolean isSinglePermDataset = searchDatasetsRestriction.isSinglePermDataset();

		Condition dsWhere = null;

		if (noDatasetsFiltering) {
			if (allDatasetsPermissions) {
				//no restrictions
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//all visible ds, only public
				dsWhere = lexeme.IS_PUBLIC.eq(PUBLICITY_PUBLIC)
						.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue())));
			} else {
				//all visible ds, selected perm
				Condition permDatasetCodeCond;
				if (isSinglePermDataset) {
					String singlePermDatasetCode = userPermDatasetCodes.get(0);
					permDatasetCodeCond = lexeme.DATASET_CODE.eq(singlePermDatasetCode);
				} else {
					permDatasetCodeCond = lexeme.DATASET_CODE.in(userPermDatasetCodes);
				}
				dsWhere = DSL.or(
						lexeme.IS_PUBLIC.eq(PUBLICITY_PUBLIC)
								.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue()))),
						permDatasetCodeCond);
			}
		} else {
			Condition filteringDatasetCodeCond;
			if (isSingleFilteringDataset) {
				String singleFilteringDatasetCode = filteringDatasetCodes.get(0);
				filteringDatasetCodeCond = lexeme.DATASET_CODE.eq(singleFilteringDatasetCode);
			} else {
				filteringDatasetCodeCond = lexeme.DATASET_CODE.in(filteringDatasetCodes);
			}
			if (allDatasetsPermissions) {
				//selected ds, full perm
				dsWhere = filteringDatasetCodeCond;
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//selected ds, only public
				dsWhere = lexeme.IS_PUBLIC.eq(PUBLICITY_PUBLIC).and(filteringDatasetCodeCond);
			} else {
				Collection<String> filteringPermDatasetCodes = CollectionUtils.intersection(filteringDatasetCodes, userPermDatasetCodes);
				if (CollectionUtils.isEmpty(filteringPermDatasetCodes)) {
					//selected ds, only public
					dsWhere = lexeme.IS_PUBLIC.eq(PUBLICITY_PUBLIC).and(filteringDatasetCodeCond);
				} else {
					//selected ds, some perm, some public
					boolean isSingleFilteringPermDataset = CollectionUtils.size(filteringPermDatasetCodes) == 1;
					Condition filteringPermDatasetCodeCond;
					if (isSingleFilteringPermDataset) {
						String filteringPermDatasetCode = filteringPermDatasetCodes.iterator().next();
						filteringPermDatasetCodeCond = lexeme.DATASET_CODE.eq(filteringPermDatasetCode);
					} else {
						filteringPermDatasetCodeCond = lexeme.DATASET_CODE.in(filteringPermDatasetCodes);
					}
					Collection<String> filteringNoPermDatasetCodes = CollectionUtils.subtract(filteringDatasetCodes, userPermDatasetCodes);
					if (CollectionUtils.isEmpty(filteringNoPermDatasetCodes)) {
						dsWhere = filteringPermDatasetCodeCond;
					} else {
						boolean isSingleFilteringNoPermDataset = CollectionUtils.size(filteringNoPermDatasetCodes) == 1;
						Condition filteringNoPermDatasetCodeCond;
						if (isSingleFilteringNoPermDataset) {
							String singleFilteringNoPermDatasetCode = filteringNoPermDatasetCodes.iterator().next();
							filteringNoPermDatasetCodeCond = lexeme.DATASET_CODE.eq(singleFilteringNoPermDatasetCode);
						} else {
							filteringNoPermDatasetCodeCond = lexeme.DATASET_CODE.in(filteringNoPermDatasetCodes);
						}
						dsWhere = DSL.or(
								lexeme.IS_PUBLIC.eq(PUBLICITY_PUBLIC).and(filteringNoPermDatasetCodeCond),
								filteringPermDatasetCodeCond);
					}
				}
			}
		}
		if ((where == null) && (dsWhere == null)) {
			where = DSL.trueCondition();
		} else if (where == null) {
			where = dsWhere;
		} else if (dsWhere == null) {
			//keep where as is
		} else {
			where = where.and(dsWhere);
		}
		return where;
	}

	protected Condition applyIdFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> idField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchIdStr = criterion.getSearchValue().toString();
			searchIdStr = RegExUtils.replaceAll(searchIdStr, "[^0-9.]", "");
			if (StringUtils.isNotEmpty(searchIdStr)) {
				Long searchId = Long.valueOf(searchIdStr);
				condition = applyIdFilter(searchId, searchOperand, idField, condition);
			}
		}
		return condition;
	}

	protected Condition applyIdFilter(Long searchId, SearchOperand searchOperand, Field<Long> searchField, Condition condition) throws Exception {

		if (SearchOperand.EQUALS.equals(searchOperand)) {
			condition = condition.and(searchField.eq(searchId));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	protected Condition applyValueFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<String> valueField, Condition condition, boolean isOnLowerValue) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValueStr = criterion.getSearchValue().toString();
			condition = applyValueFilter(searchValueStr, searchOperand, valueField, condition, isOnLowerValue);
		}
		return condition;
	}

	protected Condition applyValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition, boolean isOnLowerValue) throws Exception {

		searchValueStr = StringUtils.lowerCase(searchValueStr);
		if (SearchOperand.EQUALS.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isOnLowerValue);
			condition = condition.and(textTypeSearchFieldCase.equal(searchValueStr));
		} else if (SearchOperand.NOT_EXISTS.equals(searchOperand)) {
			//by value comparison it is exactly the same operation as equals
			//the not exists operand rather translates into join condition elsewhere
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isOnLowerValue);
			condition = condition.and(textTypeSearchFieldCase.equal(searchValueStr));
		} else if (SearchOperand.STARTS_WITH.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isOnLowerValue);
			condition = condition.and(textTypeSearchFieldCase.startsWith(searchValueStr));
		} else if (SearchOperand.ENDS_WITH.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isOnLowerValue);
			condition = condition.and(textTypeSearchFieldCase.endsWith(searchValueStr));
		} else if (SearchOperand.CONTAINS.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isOnLowerValue);
			condition = condition.and(textTypeSearchFieldCase.contains(searchValueStr));
		} else if (SearchOperand.CONTAINS_WORD.equals(searchOperand)) {
			condition = condition.and(DSL.field(
					"to_tsvector('simple', {0}) @@ to_tsquery('simple', {1})",
					Boolean.class, searchField, DSL.inline(searchValueStr)));
		} else if (SearchOperand.EARLIER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<Timestamp> tsSearchField = (Field<Timestamp>) searchField;
			condition = condition.and(DSL.field(
					"(date_part('epoch', {0}) * 1000) <= {1}",
					Boolean.class, tsSearchField, DSL.inline(date.getTime())));
		} else if (SearchOperand.LATER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<Timestamp> tsSearchField = (Field<Timestamp>) searchField;
			condition = condition.and(DSL.field(
					"(date_part('epoch', {0}) * 1000) >= {1}",
					Boolean.class, tsSearchField, DSL.inline(date.getTime())));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	@SuppressWarnings("unchecked")
	private Field<String> getTextTypeSearchFieldCase(Field<?> searchField, boolean isOnLowerValue) {
		Field<String> searchFieldStr = (Field<String>) searchField;
		if (isOnLowerValue) {
			return DSL.lower(searchFieldStr);
		}
		return searchFieldStr;
	}

	protected Condition applyLexemeSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> !crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());
		List<SearchCriterion> notExistsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField);
			for (SearchCriterion criterion : existsCriteria) {
				sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lsl.VALUE, sourceCondition, true);
			}
			condition = condition.and(DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
		}
		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {
			//not existing ref value is not supported
			//therefore specific crit of not exists operand does not matter
			Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField);
			condition = condition.and(DSL.notExists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
		}
		return condition;
	}

	protected Condition applyLexemeSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField)
				.and(lsl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : filteredCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition, true);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(lsl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyFreeformSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> !crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());
		List<SearchCriterion> notExistsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());

		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			Condition sourceCondition = ffsl.FREEFORM_ID.eq(freeformIdField);
			for (SearchCriterion criterion : existsCriteria) {
				sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ffsl.VALUE, sourceCondition, true);
			}
			condition = condition.and(DSL.exists(DSL.select(ffsl.ID).from(ffsl).where(sourceCondition)));
		}
		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {
			Condition sourceCondition = ffsl.FREEFORM_ID.eq(freeformIdField);
			condition = condition.and(DSL.notExists(DSL.select(ffsl.ID).from(ffsl).where(sourceCondition)));
		}
		return condition;
	}

	protected Condition applyFreeformSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		FreeformSourceLink usl = FREEFORM_SOURCE_LINK.as("usl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition = usl.FREEFORM_ID.eq(freeformIdField)
				.and(usl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : filteredCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition, true);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(usl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyDefinitionSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> !crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());
		List<SearchCriterion> notExistsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField);
			for (SearchCriterion criterion : existsCriteria) {
				sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), dsl.VALUE, sourceCondition, true);
			}
			condition = condition.and(DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
		}
		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {
			Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField);
			condition = condition.and(DSL.notExists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
		}
		return condition;
	}

	protected Condition applyDefinitionSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField)
				.and(dsl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : filteredCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition, true);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(dsl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyDomainFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition m1Where) {
	
		List<SearchCriterion> domainCriteriaWithExists = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& (crit.getSearchValue() != null))
				.collect(toList());
	
		boolean isNotExistsFilter = searchCriteria.stream()
				.anyMatch(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN)
						&& SearchOperand.NOT_EXISTS.equals(crit.getSearchOperand()));
	
		MeaningDomain md = MEANING_DOMAIN.as("md");
	
		if (CollectionUtils.isNotEmpty(domainCriteriaWithExists)) {
			Condition where1 = md.MEANING_ID.eq(meaningIdField);
			for (SearchCriterion criterion : domainCriteriaWithExists) {
				Classifier domain = (Classifier) criterion.getSearchValue();
				where1 = where1.and(md.DOMAIN_CODE.eq(domain.getCode())).and(md.DOMAIN_ORIGIN.eq(domain.getOrigin()));
			}
			m1Where = m1Where.and(DSL.exists(DSL.select(md.ID).from(md).where(where1)));
		}
	
		if (isNotExistsFilter) {
			Condition where1 = md.MEANING_ID.eq(meaningIdField);
			m1Where = m1Where.and(DSL.notExists(DSL.select(md.ID).from(md).where(where1)));
		}
		return m1Where;
	}

	protected Condition applyLexemeTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Word w1, Condition condition) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.WORD_ID.eq(w1.ID);
		return applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, l1, where1, condition);
	}

	protected Condition applyLexemeTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Meaning m1, Condition condition) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, l1, where1, condition);
	}

	protected Condition applyLexemeTagFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Lexeme l1, Condition where1, Condition where) throws Exception {

		List<SearchCriterion> tagNameEqualsCrit = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.TAG_NAME) && crit.getSearchOperand().equals(SearchOperand.EQUALS)).collect(toList());
		List<SearchCriterion> tagNameNotExistsCrit = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.TAG_NAME) && crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());

		if (CollectionUtils.isEmpty(tagNameEqualsCrit) && CollectionUtils.isEmpty(tagNameNotExistsCrit)) {
			return where;
		}

		LexemeTag lt = LEXEME_TAG.as("lt");
		where1 = where1
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(lt.LEXEME_ID.eq(l1.ID));

		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);

		if (CollectionUtils.isNotEmpty(tagNameEqualsCrit)) {
			for (SearchCriterion criterion : tagNameEqualsCrit) {
				if (criterion.getSearchValue() != null) {
					where1 = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lt.TAG_NAME, where1, false);
				}
			}
			where = where.andExists(DSL.select(lt.ID).from(l1, lt).where(where1));
		}
		if (CollectionUtils.isNotEmpty(tagNameNotExistsCrit)) {
			for (SearchCriterion criterion : tagNameNotExistsCrit) {
				if (criterion.getSearchValue() != null) {
					where1 = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lt.TAG_NAME, where1, false);
				}
			}
			where = where.andNotExists(DSL.select(lt.ID).from(l1, lt).where(where1));
		}
		return where;
	}

	protected Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, ActivityEntity entityName, Meaning m1, Condition wherem1) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherem1);
	}

	protected Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, ActivityEntity entityName, Word w1, Condition wherew1) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.WORD_ID.eq(w1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherew1);
	}

	protected Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, ActivityEntity entityName, Lexeme l1, Condition where1, Condition where) throws Exception {

		List<SearchCriterion> tagActivityLogCrit = searchCriteria.stream()
				.filter(crit -> {
					if (crit.getSearchKey().equals(SearchKey.CREATED_OR_UPDATED_BY)) {
						return true;
					}
					if (crit.getSearchKey().equals(SearchKey.CREATED_OR_UPDATED_ON)) {
						return true;
					}
					if (crit.getSearchOperand().equals(SearchOperand.HAS_BEEN)) {
						return true;
					}
					return false;
				}).collect(toList());

		if (CollectionUtils.isEmpty(tagActivityLogCrit)) {
			return where;
		}

		LexemeActivityLog lal = LEXEME_ACTIVITY_LOG.as("lal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		where1 = where1
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(lal.LEXEME_ID.eq(l1.ID))
				.and(lal.ACTIVITY_LOG_ID.eq(al.ID))
				.and(al.ENTITY_NAME.eq(entityName.name()));

		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);

		for (SearchCriterion criterion : searchCriteria) {
			Object critValueObj = criterion.getSearchValue();
			if (critValueObj == null) {
				continue;
			}
			String critValue = critValueObj.toString();
			if (SearchKey.CREATED_OR_UPDATED_ON.equals(criterion.getSearchKey())) {
				where1 = applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			} else if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
				where1 = applyValueFilter(critValue, criterion.getSearchOperand(), al.EVENT_BY, where1, true);
			} else if (criterion.getSearchOperand().equals(SearchOperand.HAS_BEEN)) {
				Table<?> alcdun = DSL.unnest(al.CURR_DIFFS).as("alcd", "op", "path", "value");
				Table<?> alpdun = DSL.unnest(al.PREV_DIFFS).as("alpd", "op", "path", "value");
				where1 = where1.andExists(DSL
						.select(alcdun.field("value", String.class))
						.from(alcdun)
						.where(alcdun.field("value", String.class).eq(critValue))
						.union(DSL
								.select(alpdun.field("value", String.class))
								.from(alpdun)
								.where(alpdun.field("value", String.class).eq(critValue))));
			}
		}
		return where.andExists(DSL.select(lal.ID).from(l1, lal, al).where(where1));
	}

	protected Condition createSearchCondition(Word word, Paradigm paradigm, String searchWordCrit, SearchDatasetsRestriction searchDatasetsRestriction) {

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

	protected Condition createSearchCondition(Word w1, List<SearchCriterionGroup> searchCriteriaGroups, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Condition where = composeWordDatasetsCondition(w1, searchDatasetsRestriction);

		for (SearchCriterionGroup searchCriterionGroup : searchCriteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				Form f1 = Form.FORM.as("f1");

				boolean containsSearchKeys;

				containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.VALUE);
				if (containsSearchKeys) {
					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(p1.WORD_ID.eq(w1.ID))
							.and(f1.PARADIGM_ID.eq(p1.ID))
							.and(f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));
					where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
					where = where.andExists(DSL.select(l1.ID).from(l1, p1, f1).where(where1));
				}

				containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME);
				if (containsSearchKeys) {
					Condition where2 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
					where2 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where2);
					where2 = applyLexemeSourceRefFilter(searchCriteria, l1.ID, where2);
					where2 = applyLexemeSourceNameFilter(searchCriteria, l1.ID, where2);
					where = where.andExists(DSL.select(l1.ID).from(l1).where(where2));
				}

				where = applyIdFilters(SearchKey.ID, searchCriteria, w1.ID, where);
				where = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where, false);
				where = composeWordLifecycleLogFilters(SearchKey.CREATED_OR_UPDATED_ON, searchCriteria, searchDatasetsRestriction, w1, where);
				where = composeWordLifecycleLogFilters(SearchKey.CREATED_OR_UPDATED_BY, searchCriteria, searchDatasetsRestriction, w1, where);

			} else if (SearchEntity.WORD.equals(searchEntity)) {

				List<SearchCriterion> positiveExistSearchCriteria = filterPositiveExistCriteria(searchCriteria);
				List<SearchCriterion> negativeExistSearchCriteria = filterNegativeExistCriteria(searchCriteria);

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Word w2 = Word.WORD.as("w2");

				boolean containsSearchKeys;

				if (CollectionUtils.isNotEmpty(positiveExistSearchCriteria)) {

					containsSearchKeys = containsSearchKeys(positiveExistSearchCriteria, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.VALUE, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME);

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

						where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
						where1 = applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
						where1 = applyIdFilters(SearchKey.ID, positiveExistSearchCriteria, w2.ID, where1);
						where1 = applyValueFilters(SearchKey.LANGUAGE, positiveExistSearchCriteria, w2.LANG, where1, false);
						where1 = applyValueFilters(SearchKey.VALUE, positiveExistSearchCriteria, f2.VALUE, where1, true);
						where1 = applyLexemeSourceRefFilter(positiveExistSearchCriteria, l2.ID, where1);
						where1 = applyLexemeSourceNameFilter(positiveExistSearchCriteria, l2.ID, where1);

						where = where.andExists(DSL.select(l1.ID).from(l1, l2, p2, f2, w2).where(where1));
					}
				}

				if (CollectionUtils.isNotEmpty(negativeExistSearchCriteria)) {

					containsSearchKeys = containsSearchKeys(negativeExistSearchCriteria, SearchKey.LANGUAGE);

					if (containsSearchKeys) {
						Condition where1 = l1.WORD_ID.eq(w1.ID)
								.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l1.MEANING_ID.eq(l2.MEANING_ID))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY));

						where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
						where1 = applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
						where1 = applyValueFilters(SearchKey.LANGUAGE, negativeExistSearchCriteria, w2.LANG, where1, false);

						where = where.andNotExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
					}
				}

			} else if (SearchEntity.TAG.equals(searchEntity)) {

				where = applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, w1, where);
				where = applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, ActivityEntity.TAG, w1, where);

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				Form f1 = Form.FORM.as("f1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(f1.PARADIGM_ID.eq(p1.ID));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1, false);

				where = where.andExists(DSL.select(l1.ID).from(l1, p1, f1).where(where1));

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");

				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1.MEANING_ID.eq(m1.ID));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyDomainFilters(searchCriteria, m1.ID, where1);
				where1 = applyIdFilters(SearchKey.ID, searchCriteria, m1.ID, where1);
				where = where.andExists(DSL.select(m1.ID).from(l1, m1).where(where1));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				Definition d1 = DEFINITION.as("d1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(d1.MEANING_ID.eq(m1.ID));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, d1.LANG, where1, false);
				where1 = applyDefinitionSourceRefFilter(searchCriteria, d1.ID, where1);
				where1 = applyDefinitionSourceNameFilter(searchCriteria, d1.ID, where1);

				where = where.andExists(DSL.select(d1.ID).from(l1, m1, d1).where(where1));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform u1 = FREEFORM.as("u1");

				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l1ff.LEXEME_ID.eq(l1.ID))
						.and(l1ff.FREEFORM_ID.eq(u1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE_TEXT, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, u1.LANG, where1, false);
				where1 = applyFreeformSourceNameFilter(searchCriteria, u1.ID, where1);
				where1 = applyFreeformSourceRefFilter(searchCriteria, u1.ID, where1);

				where = where.andExists(DSL.select(u1.ID).from(l1, l1ff, u1).where(where1));

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

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.ID, searchCriteria, c1.VALUE_TEXT, where1, false);

				where = where.andExists(DSL.select(c1.ID).from(l1, m1, m1ff, c1).where(where1));

			} else if (SearchEntity.CLUELESS.equals(searchEntity)) {

				where = composeCluelessValueFilter(w1, searchCriteria, searchDatasetsRestriction, where);
				where = composeCluelessSourceFilter(w1, searchCriteria, searchDatasetsRestriction, where);
			}
		}
		return where;
	}

	protected boolean containsSearchKeys(List<SearchCriterion> searchCriteria, SearchKey... searchKeys) {
		return searchCriteria.stream().map(SearchCriterion::getSearchKey).anyMatch(searchKey -> ArrayUtils.contains(searchKeys, searchKey));
	}

	protected List<SearchCriterion> filterCriteriaBySearchKey(List<SearchCriterion> searchCriteria, SearchKey searchKey) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		return filteredCriteria;
	}

	protected List<SearchCriterion> filterSourceRefCriteria(List<SearchCriterion> searchCriteria) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> {
					if (crit.getSearchKey().equals(SearchKey.SOURCE_REF)) {
						if (crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)) {
							return true;
						} else {
							if (crit.getSearchValue() == null) {
								return false;
							}
							return isNotBlank(crit.getSearchValue().toString());
						}
					}
					return false;
				})
				.collect(toList());
		return filteredCriteria;
	}

	private Condition composeWordDatasetsCondition(Word word, SearchDatasetsRestriction searchDatasetsRestriction) {

		Lexeme lfd = LEXEME.as("lfd");
		Condition dsFiltWhere = applyDatasetRestrictions(lfd, searchDatasetsRestriction, null);
		Condition where = DSL.exists(DSL.select(lfd.ID).from(lfd).where(lfd.WORD_ID.eq(word.ID).and(lfd.TYPE.eq(LEXEME_TYPE_PRIMARY)).and(dsFiltWhere)));
		return where;
	}

	private List<SearchCriterion> filterPositiveExistCriteria(List<SearchCriterion> searchCriteria) {
		// any other than NOT_EXISTS
		List<SearchCriterion> positiveExistCriteria = searchCriteria.stream().filter(crit -> !SearchOperand.NOT_EXISTS.equals(crit.getSearchOperand())).collect(
				Collectors.toList());
		return positiveExistCriteria;
	}

	private List<SearchCriterion> filterNegativeExistCriteria(List<SearchCriterion> searchCriteria) {
		List<SearchCriterion> negativeExistCriteria = searchCriteria.stream().filter(crit -> SearchOperand.NOT_EXISTS.equals(crit.getSearchOperand())).collect(Collectors.toList());
		return negativeExistCriteria;
	}

	@Deprecated
	private Condition composeWordLifecycleLogFilters(
			SearchKey searchKey, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Word w1, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		Lexeme l1 = LEXEME.as("l1");
		Meaning m1 = MEANING.as("m1");
		WordLifecycleLog wll = WORD_LIFECYCLE_LOG.as("wll");
		LexemeLifecycleLog lll = LEXEME_LIFECYCLE_LOG.as("lll");
		MeaningLifecycleLog mll = MEANING_LIFECYCLE_LOG.as("mll");
		LifecycleLog ll = LIFECYCLE_LOG.as("ll");

		Condition condwll = wll.LIFECYCLE_LOG_ID.eq(ll.ID);
		Condition condlll = lll.LEXEME_ID.eq(l1.ID).and(lll.LIFECYCLE_LOG_ID.eq(ll.ID));
		condlll = applyDatasetRestrictions(l1, searchDatasetsRestriction, condlll);
		Condition condmll = mll.MEANING_ID.eq(m1.ID).and(mll.LIFECYCLE_LOG_ID.eq(ll.ID)).and(l1.MEANING_ID.eq(m1.ID));
		condmll = applyDatasetRestrictions(l1, searchDatasetsRestriction, condmll);

		Field<?> searchField = null;
		boolean isOnLowerValue = false;
		if (searchKey.equals(SearchKey.CREATED_OR_UPDATED_ON)) {
			searchField = ll.EVENT_ON;
			isOnLowerValue = false;
		} else if (searchKey.equals(SearchKey.CREATED_OR_UPDATED_BY)) {
			searchField = ll.EVENT_BY;
			isOnLowerValue = true;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			condwll = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), searchField, condwll, isOnLowerValue);
			condlll = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), searchField, condlll, isOnLowerValue);
			condmll = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), searchField, condmll, isOnLowerValue);
		}

		SelectOrderByStep<Record1<Long>> wmlSelect = DSL
				.select(wll.WORD_ID).from(ll, wll).where(condwll)
				.unionAll(DSL.select(l1.WORD_ID).from(ll, lll, l1).where(condlll))
				.unionAll(DSL.select(l1.WORD_ID).from(ll, mll, l1, m1).where(condmll));

		return condition.and(w1.ID.in(wmlSelect));
	}

	private Condition composeCluelessValueFilter(
			Word w1, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.VALUE) && c.getSearchValue() != null)
				.collect(toList());

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
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f2.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectWordAndMeaningWord = DSL.select(l1.WORD_ID).from(l1, l2, p2, f2, w2).where(where1).groupBy(l1.WORD_ID);

		// definition select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(d1.MEANING_ID.eq(m1.ID));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinition = DSL.select(l1.WORD_ID).from(l1, m1, d1).where(where1).groupBy(l1.WORD_ID);

		// definition ff select
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(d1.MEANING_ID.eq(m1.ID))
				.and(dff1.DEFINITION_ID.eq(d1.ID))
				.and(dff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.eq(FreeformType.NOTE.name()));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectDefinitionFreeform = DSL.select(l1.WORD_ID).from(l1, dff1, ff1, m1, d1).where(where1).groupBy(l1.WORD_ID);

		// lexeme ff select
		String[] lexemeFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.USAGE.name(), FreeformType.GOVERNMENT.name(),
				FreeformType.GRAMMAR.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};
		where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY)
				.and(lff1.LEXEME_ID.eq(l1.ID))
				.and(lff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.in(lexemeFreeformTypes));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectLexemeFreeform = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// meaning ff select
		String[] meaningFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.CONCEPT_ID.name(), FreeformType.LEARNER_COMMENT.name()};
		where1 = l1.MEANING_ID.eq(m1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(mff1.MEANING_ID.eq(m1.ID))
				.and(mff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.in(meaningFreeformTypes));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectMeaningFreeform = DSL.select(l1.WORD_ID).from(l1, m1, mff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// word ff select
		String[] wordFreeformTypes = new String[] {
				FreeformType.NOTE.name(), FreeformType.OD_WORD_RECOMMENDATION.name()};
		where1 = l1.WORD_ID.eq(w1.ID)
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(wff1.WORD_ID.eq(w1.ID))
				.and(wff1.FREEFORM_ID.eq(ff1.ID))
				.and(ff1.TYPE.in(wordFreeformTypes));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectWordFreeform = DSL.select(l1.WORD_ID).from(l1, w1, wff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// usage ff select
		String[] usageFreeformTypes = new String[] {
				FreeformType.USAGE_TRANSLATION.name(), FreeformType.USAGE_DEFINITION.name(),
				FreeformType.OD_USAGE_ALTERNATIVE.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};
		where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY)
				.and(lff1.LEXEME_ID.eq(l1.ID))
				.and(lff1.FREEFORM_ID.eq(ff1.PARENT_ID))
				.and(ff1.TYPE.in(usageFreeformTypes));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, ff1.VALUE_TEXT, where1, true);
		SelectHavingStep<Record1<Long>> selectUsageFreeform = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

		// form select
		where1 = l1.TYPE.eq(LEXEME_TYPE_PRIMARY)
				.and(p1.WORD_ID.eq(l1.WORD_ID))
				.and(f1.PARADIGM_ID.eq(p1.ID));
		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
		where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
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

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_ID);
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
			where1 = applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, lsl1.VALUE, where1, true);
			where1 = applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, lsl1.SOURCE_ID, where1);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1, lsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.USAGE.name())).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, lff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = dsl1.DEFINITION_ID.eq(d1.ID).and(l1.MEANING_ID.eq(d1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, dsl1.VALUE, where1, true);
			where1 = applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, dsl1.SOURCE_ID, where1);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1, dsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(lff1.FREEFORM_ID.eq(ff1.ID)).and(lff1.LEXEME_ID.eq(l1.ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, lff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(mff1.FREEFORM_ID.eq(ff1.ID)).and(mff1.MEANING_ID.eq(l1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectMeaningNoteSourceLinks = DSL.select(l1.WORD_ID).from(l1, mff1, ff1, ffsl1).where(where1).groupBy(l1.WORD_ID);

			where1 = ffsl1.FREEFORM_ID.eq(ff1.ID).and(ff1.TYPE.eq(FreeformType.NOTE.name())).and(dff1.FREEFORM_ID.eq(ff1.ID)).and(dff1.DEFINITION_ID.eq(d1.ID)).and(l1.MEANING_ID.eq(d1.MEANING_ID)).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY));
			where1 = applyValueFilters(SearchKey.SOURCE_REF, filteredCriteria, ffsl1.VALUE, where1, true);
			where1 = applyIdFilters(SearchKey.SOURCE_ID, filteredCriteria, ffsl1.SOURCE_ID, where1);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
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
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL.select(l1.WORD_ID).from(l1).where(where1).groupBy(l1.WORD_ID);

			where1 = ff1.TYPE.eq(FreeformType.USAGE.name())
					.and(lff1.FREEFORM_ID.eq(ff1.ID))
					.and(lff1.LEXEME_ID.eq(l1.ID))
					.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
					.andNotExists(DSL.select(ffsl1.ID).from(ffsl1).where(ffsl1.FREEFORM_ID.eq(ff1.ID)));
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectUsageSourceLinks = DSL.select(l1.WORD_ID).from(l1, lff1, ff1).where(where1).groupBy(l1.WORD_ID);

			where1 = l1.MEANING_ID.eq(d1.MEANING_ID).and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY)).andNotExists(DSL.select(dsl1.ID).from(dsl1).where(dsl1.DEFINITION_ID.eq(d1.ID)));
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL.select(l1.WORD_ID).from(l1, d1).where(where1).groupBy(l1.WORD_ID);

			Table<Record1<Long>> a1 = selectLexemeSourceLinks
					.unionAll(selectUsageSourceLinks)
					.unionAll(selectDefinitionSourceLinks)
					.asTable("a1");

			where = where.andExists(DSL.select(a1.field("word_id")).from(a1).where(a1.field("word_id", Long.class).eq(w1.ID)));
		}

		return where;
	}

	protected List<eki.ekilex.data.Word> execute(
			Word w1, Paradigm p1, Condition where, SearchDatasetsRestriction searchDatasetsRestriction,
			DatasetPermission userRole, List<String> tagNames, boolean fetchAll, int offset, int maxResultsLimit) {

		List<String> availableDatasetCodes = searchDatasetsRestriction.getAvailableDatasetCodes();

		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");
		Form f1 = FORM.as("f1");
		Table<Record> from = w1.join(p1).on(p1.WORD_ID.eq(w1.ID)).join(f1).on(f1.PARADIGM_ID.eq(p1.ID).and(f1.MODE.eq(FormMode.WORD.name())));
		Field<String> wv = DSL.field("array_to_string(array_agg(distinct f1.value), ',', '*')").cast(String.class);
		Field<String> wvp = DSL.field("array_to_string(array_agg(distinct f1.value_prese), ',', '*')").cast(String.class);

		Table<Record8<Long, String, String, Integer, String, String, String, String>> w = DSL
				.select(
						w1.ID.as("word_id"),
						wv.as("word_value"),
						wvp.as("word_value_prese"),
						w1.HOMONYM_NR,
						w1.LANG,
						w1.WORD_CLASS,
						w1.GENDER_CODE,
						w1.ASPECT_CODE)
				.from(from)
				.where(where)
				.groupBy(w1.ID)
				.asTable("w");

		Field<String[]> wtf = getWordTypesField(w.field("word_id", Long.class));
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtz = getWordIsForeignField(w.field("word_id", Long.class));

		Field<String[]> lxvsf;
		Field<Boolean> lxpsf;
		Field<String[]> lxtnf;
		if (userRole == null) {
			lxvsf = DSL.field(DSL.val(new String[0]));
			lxpsf = DSL.field(DSL.val((Boolean) null));
			lxtnf = DSL.field(DSL.val(new String[0]));
		} else {
			String userRoleDatasetCode = userRole.getDatasetCode();
			lxvsf = DSL.field(DSL
					.select(DSL.arrayAggDistinct(l.VALUE_STATE_CODE))
					.from(l)
					.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
							.and(l.DATASET_CODE.eq(userRoleDatasetCode))
							.and(l.VALUE_STATE_CODE.isNotNull()))
					.groupBy(w.field("word_id")));
			lxpsf = DSL.field(DSL
					.select(DSL.field(DSL.val(PUBLICITY_PUBLIC).eq(DSL.all(DSL.arrayAggDistinct(l.IS_PUBLIC)))))
					.from(l)
					.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
							.and(l.DATASET_CODE.eq(userRoleDatasetCode)))
					.groupBy(w.field("word_id")));

			if (CollectionUtils.isEmpty(tagNames)) {
				lxtnf = DSL.field(DSL.val(new String[0]));
			} else {
				lxtnf = DSL.field(DSL
						.select(DSL.arrayAggDistinct(DSL.coalesce(lt.TAG_NAME, "!")))
						.from(l
								.leftOuterJoin(lt).on(lt.LEXEME_ID.eq(l.ID).and(lt.TAG_NAME.in(tagNames))))
						.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(l.DATASET_CODE.eq(userRoleDatasetCode)))
						.groupBy(w.field("word_id")));
			}
		}

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(l.DATASET_CODE))
				.from(l)
				.where(l.WORD_ID.eq(w.field("word_id").cast(Long.class))
						.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(l.DATASET_CODE.in(availableDatasetCodes)))
				.groupBy(w.field("word_id")));

		Table<Record16<Long, String, String, Integer, String, String, String, String, String[], Boolean, Boolean, Boolean, Boolean, String[], String[], String[]>> ww = DSL
				.select(
						w.field("word_id", Long.class),
						w.field("word_value", String.class),
						w.field("word_value_prese", String.class),
						w.field("homonym_nr", Integer.class),
						w.field("lang", String.class),
						w.field("word_class", String.class),
						w.field("gender_code", String.class),
						w.field("aspect_code", String.class),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						lxpsf.as("lexemes_are_public"),
						lxvsf.as("lexemes_value_state_codes"),
						lxtnf.as("lexemes_tag_names"),
						dscf.as("dataset_codes"))
				.from(w)
				.orderBy(
						w.field("word_value"),
						w.field("homonym_nr"))
				.asTable("ww");

		if (fetchAll) {
			return create.selectFrom(ww).fetchInto(eki.ekilex.data.Word.class);
		} else {
			return create.selectFrom(ww).limit(maxResultsLimit).offset(offset).fetchInto(eki.ekilex.data.Word.class);
		}
	}

}
