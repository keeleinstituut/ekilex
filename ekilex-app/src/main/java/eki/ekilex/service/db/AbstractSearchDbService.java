package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LAYER_STATE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
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
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Record8;
import org.jooq.SelectOrderByStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LayerName;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeLifecycleLog;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LifecycleLog;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningLifecycleLog;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordLifecycleLog;
import eki.ekilex.service.db.util.SubqueryHelper;

public abstract class AbstractSearchDbService implements SystemConstant, GlobalConstant {

	@Autowired
	protected SubqueryHelper subqueryHelper;

	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

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
				//all visible ds, only public
				dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
						.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue())));
			} else {
				//all visible ds, selected perm
				dsFiltWhere = DSL.or(
						lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
								.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue()))),
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
		boolean isSingleFilteringDataset = searchDatasetsRestriction.isSingleFilteringDataset();
		boolean isSinglePermDataset = searchDatasetsRestriction.isSinglePermDataset();

		if (noDatasetsFiltering) {
			if (allDatasetsPermissions) {
				//no restrictions
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//all visible ds, only public
				where = where.and(lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
						.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue()))));
			} else {
				//all visible ds, selected perm
				Condition permDatasetCodeCond;
				if (isSinglePermDataset) {
					String singlePermDatasetCode = userPermDatasetCodes.get(0);
					permDatasetCodeCond = lexeme.DATASET_CODE.eq(singlePermDatasetCode);
				} else {
					permDatasetCodeCond = lexeme.DATASET_CODE.in(userPermDatasetCodes);
				}
				where = where.and(DSL.or(
						lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
								.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue()))),
						permDatasetCodeCond));
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
				where = where.and(filteringDatasetCodeCond);
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//selected ds, only public
				where = where.and(lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(filteringDatasetCodeCond));
			} else {
				Collection<String> filteringPermDatasetCodes = CollectionUtils.intersection(filteringDatasetCodes, userPermDatasetCodes);
				if (CollectionUtils.isEmpty(filteringPermDatasetCodes)) {
					//selected ds, only public
					where = where.and(lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(filteringDatasetCodeCond));
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
						where = where.and(filteringPermDatasetCodeCond);
					} else {
						boolean isSingleFilteringNoPermDataset = CollectionUtils.size(filteringNoPermDatasetCodes) == 1;
						Condition filteringNoPermDatasetCodeCond;
						if (isSingleFilteringNoPermDataset) {
							String singleFilteringNoPermDatasetCode = filteringNoPermDatasetCodes.iterator().next();
							filteringNoPermDatasetCodeCond = lexeme.DATASET_CODE.eq(singleFilteringNoPermDatasetCode);
						} else {
							filteringNoPermDatasetCodeCond = lexeme.DATASET_CODE.in(filteringNoPermDatasetCodes);
						}
						where = where.and(DSL.or(
								lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(filteringNoPermDatasetCodeCond),
								filteringPermDatasetCodeCond));
					}
				}
			}
		}
		return where;
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
		if (isOnLowerValue) {
			return searchField.lower();
		}
		return (Field<String>) searchField;
	}

	protected Condition applyLexemeSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> !crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());
		List<SearchCriterion> notExistsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)).collect(toList());

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			for (SearchCriterion criterion : existsCriteria) {
				sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lsl.VALUE, sourceCondition, true);
			}
			condition = condition.and(DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
		}
		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {
			//not existing ref value is not supported
			//therefore specific crit of not exists operand does not matter
			condition = condition.and(DSL.notExists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
		}
		return condition;
	}

	protected Condition applyLexemeSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceNameCriteria(searchCriteria);

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
		Condition sourceCondition = ffsl.FREEFORM_ID.eq(freeformIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			for (SearchCriterion criterion : existsCriteria) {
				sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ffsl.VALUE, sourceCondition, true);
			}
			condition = condition.and(DSL.exists(DSL.select(ffsl.ID).from(ffsl).where(sourceCondition)));
		}
		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {
			condition = condition.and(DSL.notExists(DSL.select(ffsl.ID).from(ffsl).where(sourceCondition)));
		}
		return condition;
	}

	protected Condition applyFreeformSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceNameCriteria(searchCriteria);

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
		Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			for (SearchCriterion criterion : existsCriteria) {
				sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), dsl.VALUE, sourceCondition, true);
			}
			condition = condition.and(DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
		}
		if (CollectionUtils.isNotEmpty(notExistsCriteria)) {
			condition = condition.and(DSL.notExists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
		}
		return condition;
	}

	protected Condition applyDefinitionSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceNameCriteria(searchCriteria);

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

	private List<SearchCriterion> filterSourceNameCriteria(List<SearchCriterion> searchCriteria) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.SOURCE_NAME) && crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
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

	protected Condition applyLanguageNotExistFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Lexeme l2, Word w2, Condition w2Where) {

		List<SearchCriterion> languageNotExistCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.LANGUAGE)
						&& crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)
						&& (crit.getSearchValue() != null))
				.collect(toList());

		if (CollectionUtils.isNotEmpty(languageNotExistCriteria)) {

			for (SearchCriterion criterion : languageNotExistCriteria) {
				String lang = criterion.getSearchValue().toString();
				w2Where = w2Where.and(w2.LANG.eq(lang));
			}
		}

		return w2Where;
	}

	protected Condition applyDomainFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Meaning m1, Condition m1Where, Condition w1Where) {

		List<SearchCriterion> domainCriteriaWithExists = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& (crit.getSearchValue() != null))
				.collect(toList());

		boolean isNotExistsFilter = searchCriteria.stream()
				.anyMatch(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN)
						&& SearchOperand.NOT_EXISTS.equals(crit.getSearchOperand()));

		MeaningDomain m1d = MEANING_DOMAIN.as("m1d");

		if (CollectionUtils.isNotEmpty(domainCriteriaWithExists)) {
			m1Where = m1Where.and(m1d.MEANING_ID.eq(m1.ID));
			for (SearchCriterion criterion : domainCriteriaWithExists) {
				Classifier domain = (Classifier) criterion.getSearchValue();
				m1Where = m1Where.and(m1d.DOMAIN_CODE.eq(domain.getCode())).and(m1d.DOMAIN_ORIGIN.eq(domain.getOrigin()));
			}
			w1Where = w1Where.andExists(DSL.select(m1.ID).from(l1, m1, m1d).where(m1Where));
		}

		if (isNotExistsFilter) {
			m1Where = m1Where.andNotExists(DSL.select(m1d.ID).from(m1d).where(m1d.MEANING_ID.eq(m1.ID)));
			w1Where = w1Where.andExists(DSL.select(m1.ID).from(l1, m1).where(m1Where));
		}
		return w1Where;
	}

	protected Condition applyDomainFilters(List<SearchCriterion> searchCriteria, Meaning m1, Condition m1Where) {

		List<SearchCriterion> domainCriteriaWithExists = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& (crit.getSearchValue() != null))
				.collect(toList());

		boolean isNotExistsFilter = searchCriteria.stream()
				.anyMatch(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN)
						&& SearchOperand.NOT_EXISTS.equals(crit.getSearchOperand()));

		MeaningDomain m1d = MEANING_DOMAIN.as("m1d");

		if (CollectionUtils.isNotEmpty(domainCriteriaWithExists)) {
			Condition where1 = m1d.MEANING_ID.eq(m1.ID);
			for (SearchCriterion criterion : domainCriteriaWithExists) {
				Classifier domain = (Classifier) criterion.getSearchValue();
				where1 = where1.and(m1d.DOMAIN_CODE.eq(domain.getCode())).and(m1d.DOMAIN_ORIGIN.eq(domain.getOrigin()));
			}
			m1Where = m1Where.and(DSL.exists(DSL.select(m1d.ID).from(m1d).where(where1)));
		}

		if (isNotExistsFilter) {
			Condition where1 = m1d.MEANING_ID.eq(m1.ID);
			m1Where = m1Where.and(DSL.notExists(DSL.select(m1d.ID).from(m1d).where(where1)));
		}
		return m1Where;
	}

	protected Condition createSearchCondition(Word word, Paradigm paradigm, String wordWithMetaCharacters, SearchDatasetsRestriction searchDatasetsRestriction) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_").toLowerCase();

		Form form = FORM.as("f2");
		Condition where1 = form.PARADIGM_ID.eq(paradigm.ID);
		where1 = where1.and(form.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));
		if (StringUtils.containsAny(theFilter, '%', '_')) {
			where1 = where1.and(form.VALUE.lower().like(theFilter));
		} else {
			where1 = where1.and(form.VALUE.lower().eq(theFilter));
		}
		Condition where = composeWordDatasetsCondition(word, searchDatasetsRestriction);
		where = where.andExists(DSL.select(form.ID).from(form).where(where1));
		return where;
	}

	protected Condition createSearchCondition(Word w1, List<SearchCriterionGroup> searchCriteriaGroups, SearchDatasetsRestriction searchDatasetsRestriction) throws Exception {

		Condition where = composeWordDatasetsCondition(w1, searchDatasetsRestriction);

		for (SearchCriterionGroup searchCriterionGroup : searchCriteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (searchCriteria.isEmpty()) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				Form f1 = Form.FORM.as("f1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(f1.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));

				where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1, true);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1, false);
				where1 = applyLexemeSourceRefFilter(searchCriteria, l1.ID, where1);
				where1 = applyLexemeSourceNameFilter(searchCriteria, l1.ID, where1);

				where = where.andExists(DSL.select(l1.ID).from(l1, p1, f1).where(where1));

				Condition where2 = l1.WORD_ID.eq(w1.ID);
				where2 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where2);

				where = composeWordLifecycleLogFilters(SearchKey.CREATED_OR_UPDATED_ON, searchCriteria, searchDatasetsRestriction, w1, where);
				where = composeWordLifecycleLogFilters(SearchKey.CREATED_OR_UPDATED_BY, searchCriteria, searchDatasetsRestriction, w1, where);

			} else if (SearchEntity.WORD.equals(searchEntity)) {

				List<SearchCriterion> positiveExistSearchCriteria = filterPositiveExistCriteria(searchCriteria);
				List<SearchCriterion> negativeExistSearchCriteria = filterNegativeExistCriteria(searchCriteria);

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Word w2 = Word.WORD.as("w2");

				if (CollectionUtils.isNotEmpty(positiveExistSearchCriteria)) {

					Paradigm p2 = Paradigm.PARADIGM.as("p2");
					Form f2 = Form.FORM.as("f2");

					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(l1.MEANING_ID.eq(l2.MEANING_ID))
							.and(l2.WORD_ID.eq(w2.ID))
							.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(p2.WORD_ID.eq(w2.ID))
							.and(f2.PARADIGM_ID.eq(p2.ID))
							.and(f2.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()));

					where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
					where1 = applyValueFilters(SearchKey.VALUE, positiveExistSearchCriteria, f2.VALUE, where1, true);
					where1 = applyValueFilters(SearchKey.LANGUAGE, positiveExistSearchCriteria, w2.LANG, where1, false);
					where1 = applyLexemeSourceRefFilter(positiveExistSearchCriteria, l2.ID, where1);
					where1 = applyLexemeSourceNameFilter(positiveExistSearchCriteria, l2.ID, where1);

					where = where.andExists(DSL.select(l1.ID).from(l1, l2, p2, f2, w2).where(where1));
				}

				if (CollectionUtils.isNotEmpty(negativeExistSearchCriteria)) {

					Condition where1 = l1.WORD_ID.eq(w1.ID)
							.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(l1.MEANING_ID.eq(l2.MEANING_ID))
							.and(l2.WORD_ID.eq(w2.ID))
							.and(l2.TYPE.eq(LEXEME_TYPE_PRIMARY));

					where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
					where1 = applyDatasetRestrictions(l2, searchDatasetsRestriction, where1);
					where1 = applyLanguageNotExistFilters(negativeExistSearchCriteria, l1, l2, w2, where1);

					where = where.andNotExists(DSL.select(l1.ID).from(l1, l2, w2).where(where1));
				}

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
				where = applyDomainFilters(searchCriteria, l1, m1, where1, where);

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
				where1 = applyLexemeSourceNameFilter(searchCriteria, d1.ID, where1);

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
			}
		}
		return where;
	}

	private Condition composeWordDatasetsCondition(Word word, SearchDatasetsRestriction searchDatasetsRestriction) {

		Lexeme lfd = LEXEME.as("lfd");
		Condition dsFiltWhere = composeLexemeDatasetsCondition(lfd, searchDatasetsRestriction);
		Condition where = DSL.exists(DSL.select(lfd.ID).from(lfd).where(lfd.WORD_ID.eq(word.ID).and(dsFiltWhere)));
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

	private Condition composeWordLifecycleLogFilters(
			SearchKey searchKey, List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Word w1, Condition wherew1) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return wherew1;
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

		return wherew1.and(w1.ID.in(wmlSelect));
	}

	protected List<eki.ekilex.data.Word> execute(Word w1, Paradigm p1, Condition where, LayerName layerName, SearchDatasetsRestriction searchDatasetsRestriction, boolean fetchAll, int offset, DSLContext create) {

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		List<String> availableDatasetCodes = searchDatasetsRestriction.getAvailableDatasetCodes();

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

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(LEXEME.DATASET_CODE))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(w.field("word_id").cast(Long.class))
						.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
						.and(LEXEME.DATASET_CODE.in(availableDatasetCodes)))
				.groupBy(w.field("word_id")));

		Field<String[]> lpscf;
		if (LayerName.NONE.equals(layerName)) {
			lpscf = DSL.field(DSL.val(new String[0]));
		} else {
			lpscf = DSL.field(DSL
					.select(DSL.arrayAggDistinct(DSL.coalesce(LAYER_STATE.PROCESS_STATE_CODE, "!")))
					.from(LEXEME.leftOuterJoin(LAYER_STATE)
							.on(LAYER_STATE.LEXEME_ID.eq(LEXEME.ID)
									.and(LAYER_STATE.LAYER_NAME.eq(layerName.name()))))
					.where(LEXEME.WORD_ID.eq(w.field("word_id").cast(Long.class))
							.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
							.and(LEXEME.DATASET_CODE.in(filteringDatasetCodes)))
					.groupBy(w.field("word_id")));
		}

		Field<String[]> wtf = subqueryHelper.getWordTypesField(w.field("word_id", Long.class));
		Field<Boolean> wtpf = subqueryHelper.getWordIsPrefixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtsf = subqueryHelper.getWordIsSuffixoidField(w.field("word_id", Long.class));
		Field<Boolean> wtz = subqueryHelper.getWordIsForeignField(w.field("word_id", Long.class));

		Table<Record14<Long, String, String, Integer, String, String, String, String, String[], String[], String[], Boolean, Boolean, Boolean>> ww = DSL
				.select(
						w.field("word_id", Long.class),
						w.field("word_value", String.class),
						w.field("word_value_prese", String.class),
						w.field("homonym_nr", Integer.class),
						w.field("lang", String.class),
						w.field("word_class", String.class),
						w.field("gender_code", String.class),
						w.field("aspect_code", String.class),
						dscf.as("dataset_codes"),
						lpscf.as("layer_process_state_codes"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"))
				.from(w)
				.orderBy(
						w.field("word_value"),
						w.field("homonym_nr"))
				.asTable("ww");

		if (fetchAll) {
			return create.selectFrom(ww).fetchInto(eki.ekilex.data.Word.class);
		} else {
			return create.selectFrom(ww).limit(MAX_RESULTS_LIMIT).offset(offset).fetchInto(eki.ekilex.data.Word.class);
		}
	}

}
