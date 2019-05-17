package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
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
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeLifecycleLog;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LifecycleLog;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningLifecycleLog;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordLifecycleLog;

public abstract class AbstractSearchDbService implements SystemConstant, DbConstant {

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

	protected Condition applyValueFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<String> valueField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValueStr = criterion.getSearchValue().toString();
			condition = applyValueFilter(searchValueStr, searchOperand, valueField, condition);
		}
		return condition;
	}

	protected Condition applyValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition) throws Exception {

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
		} else if (SearchOperand.EARLIER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<Timestamp> tsSearchField = (Field<Timestamp>) searchField;
			condition = condition.and(tsSearchField.le(new Timestamp(date.getTime())));
		} else if (SearchOperand.LATER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<Timestamp> tsSearchField = (Field<Timestamp>) searchField;
			condition = condition.and(tsSearchField.ge(new Timestamp(date.getTime())));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	protected Condition applyLexemeSourceFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		List<SearchCriterion> sourceCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());

		if (CollectionUtils.isEmpty(sourceCriteria)) {
			return condition;
		}
		if (SearchKey.SOURCE_NAME.equals(searchKey)) {
			return applyLexemeSourceNameFilter(sourceCriteria, lexemeIdField, condition);
		} else if (SearchKey.SOURCE_REF.equals(searchKey)) {
			return applyLexemeSourceRefFilter(sourceCriteria, lexemeIdField, condition);
		}
		return condition;
	}

	private Condition applyLexemeSourceRefFilter(List<SearchCriterion> sourceCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField);

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lsl.VALUE, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
	}

	private Condition applyLexemeSourceNameFilter(List<SearchCriterion> sourceCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition =
				lsl.LEXEME_ID.eq(lexemeIdField)
				.and(lsl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(lsl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyDefinitionSourceFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

		List<SearchCriterion> sourceCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());

		if (CollectionUtils.isEmpty(sourceCriteria)) {
			return condition;
		}
		if (SearchKey.SOURCE_NAME.equals(searchKey)) {
			return applyDefinitionSourceNameFilter(sourceCriteria, definitionIdField, condition);	
		} else if (SearchKey.SOURCE_REF.equals(searchKey)) {
			return applyDefinitionSourceRefFilter(sourceCriteria, definitionIdField, condition);
		}
		return condition;
	}

	private Condition applyDefinitionSourceRefFilter(List<SearchCriterion> sourceCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");

		Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField);

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), dsl.VALUE, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
	}

	private Condition applyDefinitionSourceNameFilter(List<SearchCriterion> sourceCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition =
				dsl.DEFINITION_ID.eq(definitionIdField)
				.and(dsl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(dsl, s, sff, ff).where(sourceCondition)));
	}

	protected Condition applyFreeformSourceFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

		List<SearchCriterion> sourceCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());

		if (CollectionUtils.isEmpty(sourceCriteria)) {
			return condition;
		}
		if (SearchKey.SOURCE_NAME.equals(searchKey)) {
			return applyFreeformSourceNameFilter(sourceCriteria, freeformIdField, condition);	
		} else if (SearchKey.SOURCE_REF.equals(searchKey)) {
			return applyFreeformSourceRefFilter(sourceCriteria, freeformIdField, condition);
		}
		return condition;
	}

	private Condition applyFreeformSourceRefFilter(List<SearchCriterion> sourceCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

		FreeformSourceLink usl = FREEFORM_SOURCE_LINK.as("usl");
		Condition sourceCondition = usl.FREEFORM_ID.eq(freeformIdField);

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), usl.VALUE, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(usl.ID).from(usl).where(sourceCondition)));
	}

	private Condition applyFreeformSourceNameFilter(List<SearchCriterion> sourceCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

		FreeformSourceLink usl = FREEFORM_SOURCE_LINK.as("usl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sff = SourceFreeform.SOURCE_FREEFORM.as("sff");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition =
				usl.FREEFORM_ID.eq(freeformIdField)
				.and(usl.SOURCE_ID.eq(s.ID))
				.and(sff.SOURCE_ID.eq(s.ID))
				.and(sff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		for (SearchCriterion criterion : sourceCriteria) {
			sourceCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(usl, s, sff, ff).where(sourceCondition)));
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

	protected Condition applyLexWordLifecycleLogFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Condition l1Where, Condition w1Where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.CREATED_OR_UPDATED_ON) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return w1Where;
		}

		WordLifecycleLog wll = WORD_LIFECYCLE_LOG.as("wll");
		LexemeLifecycleLog lll = LEXEME_LIFECYCLE_LOG.as("lll");
		LifecycleLog ll = LIFECYCLE_LOG.as("ll");

		Condition wllCondition = l1Where.and(wll.WORD_ID.eq(l1.WORD_ID).and(wll.LIFECYCLE_LOG_ID.eq(ll.ID)));
		Condition lllCondition = l1Where.and(lll.LEXEME_ID.eq(l1.ID).and(lll.LIFECYCLE_LOG_ID.eq(ll.ID)));

		for (SearchCriterion criterion : filteredCriteria) {
			wllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, wllCondition);
			lllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, lllCondition);
		}

		Condition wllExist = DSL.exists(DSL.select(wll.ID).from(l1, ll, wll).where(wllCondition));
		Condition lllExist = DSL.exists(DSL.select(lll.ID).from(l1, ll, lll).where(lllCondition));

		return w1Where.and(DSL.or(wllExist, lllExist));
	}

	protected Condition applyLexMeaningLifecycleLogFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Meaning m1, Condition m1Where, Condition w1Where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.CREATED_OR_UPDATED_ON) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return w1Where;
		}

		MeaningLifecycleLog mll = MEANING_LIFECYCLE_LOG.as("mll");
		LexemeLifecycleLog lll = LEXEME_LIFECYCLE_LOG.as("lll");
		LifecycleLog ll = LIFECYCLE_LOG.as("ll");

		Condition mllCondition = m1Where.and(mll.MEANING_ID.eq(l1.MEANING_ID).and(mll.LIFECYCLE_LOG_ID.eq(ll.ID)));
		Condition lllCondition = m1Where.and(lll.LEXEME_ID.eq(l1.ID).and(lll.LIFECYCLE_LOG_ID.eq(ll.ID)));

		for (SearchCriterion criterion : filteredCriteria) {
			mllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, mllCondition);
			lllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, lllCondition);
		}

		Condition mllExist = DSL.exists(DSL.select(mll.ID).from(m1, l1, ll, mll).where(mllCondition));
		Condition lllExist = DSL.exists(DSL.select(lll.ID).from(m1, l1, ll, lll).where(lllCondition));

		return w1Where.and(DSL.or(mllExist, lllExist));
	}

	protected Condition applyTermWordLifecycleLogFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Word w1, Condition w1Where, Condition m1Where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.CREATED_OR_UPDATED_ON) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return m1Where;
		}

		WordLifecycleLog wll = WORD_LIFECYCLE_LOG.as("wll");
		LexemeLifecycleLog lll = LEXEME_LIFECYCLE_LOG.as("lll");
		LifecycleLog ll = LIFECYCLE_LOG.as("ll");

		Condition wllCondition = w1Where.and(wll.WORD_ID.eq(w1.ID).and(wll.LIFECYCLE_LOG_ID.eq(ll.ID)));
		Condition lllCondition = w1Where.and(lll.LEXEME_ID.eq(l1.ID).and(lll.LIFECYCLE_LOG_ID.eq(ll.ID)));

		for (SearchCriterion criterion : filteredCriteria) {
			wllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, wllCondition);
			lllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, lllCondition);
		}

		Condition wllExist = DSL.exists(DSL.select(wll.ID).from(w1, l1, ll, wll).where(wllCondition));
		Condition lllExist = DSL.exists(DSL.select(lll.ID).from(w1, l1, ll, lll).where(lllCondition));

		return m1Where.and(DSL.or(wllExist, lllExist));
	}

	protected Condition applyTermMeaningLifecycleLogFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Condition l1Where, Condition m1Where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.CREATED_OR_UPDATED_ON) && c.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return m1Where;
		}

		MeaningLifecycleLog mll = MEANING_LIFECYCLE_LOG.as("mll");
		LexemeLifecycleLog lll = LEXEME_LIFECYCLE_LOG.as("lll");
		LifecycleLog ll = LIFECYCLE_LOG.as("ll");

		Condition mllCondition = l1Where.and(mll.MEANING_ID.eq(l1.MEANING_ID).and(mll.LIFECYCLE_LOG_ID.eq(ll.ID)));
		Condition lllCondition = l1Where.and(lll.LEXEME_ID.eq(l1.ID).and(lll.LIFECYCLE_LOG_ID.eq(ll.ID)));

		for (SearchCriterion criterion : filteredCriteria) {
			mllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, mllCondition);
			lllCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ll.EVENT_ON, lllCondition);
		}

		Condition mllExist = DSL.exists(DSL.select(mll.ID).from(l1, ll, mll).where(mllCondition));
		Condition lllExist = DSL.exists(DSL.select(lll.ID).from(l1, ll, lll).where(lllCondition));

		return m1Where.and(DSL.or(mllExist, lllExist));
	}
}
