package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FORM_FREQ;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREQ_CORP;
import static eki.ekilex.data.db.Tables.LEXEME_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_FREQ;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.tables.ActivityLog;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.FormFreq;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.FreqCorp;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeActivityLog;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.LexemeRegister;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningRelation;
import eki.ekilex.data.db.tables.MeaningSemanticType;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordFreeform;
import eki.ekilex.data.db.tables.WordFreq;
import eki.ekilex.data.db.tables.WordGroup;
import eki.ekilex.data.db.tables.WordGroupMember;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordWordType;

@Component
public class SearchFilterHelper implements GlobalConstant {

	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public Condition applyDatasetRestrictions(Lexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction, Condition where) {

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

	public Condition applyIdFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<Long> idField, Condition condition) throws Exception {

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

	private Condition applyIdFilter(Long searchId, SearchOperand searchOperand, Field<Long> searchField, Condition condition) throws Exception {

		if (SearchOperand.EQUALS.equals(searchOperand)) {
			condition = condition.and(searchField.eq(searchId));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	public Condition applyValueFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<String> valueField, Condition condition, boolean isCaseInsensitive) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, searchKey);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValueStr = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			condition = applyValueFilter(searchValueStr, isNot, searchOperand, valueField, condition, isCaseInsensitive);
		}
		return condition;
	}

	public Condition applyPublicityFilters(List<SearchCriterion> searchCriteria, Field<Boolean> entityIsPublicField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.PUBLICITY)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& crit.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			boolean isPublic = BooleanUtils.toBoolean(criterion.getSearchValue().toString());
			boolean isNot = criterion.isNot();
			Condition critWhere = entityIsPublicField.eq(isPublic);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyWordTypeValueFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.WORD_TYPE, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordWordType wwt = WORD_WORD_TYPE.as("wwt");

		Condition where1 = wwt.WORD_ID.eq(wordIdField);
		for (SearchCriterion criterion : filteredCriteria) {
			String wordTypeCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = wwt.WORD_TYPE_CODE.eq(wordTypeCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.andExists(DSL.select(wwt.ID).from(wwt).where(where1));
		return where;
	}

	public Condition applyWordTypeExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.WORD_TYPE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordWordType wwt = WORD_WORD_TYPE.as("wwt");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(wwt.ID).as(countFieldName))
				.from(wwt)
				.where(wwt.WORD_ID.eq(wordIdField))
				.asTable("wwtcnt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyWordAspectFilters(List<SearchCriterion> searchCriteria, Field<String> wordAspectField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.ASPECT))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());
		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String aspectCode = criterion.getSearchValue().toString();
					boolean isNot = criterion.isNot();
					Condition critWhere = wordAspectField.eq(aspectCode);
					if (isNot) {
						critWhere = DSL.not(critWhere);
					}
					where = where.and(critWhere);
				}
			}
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = wordAspectField.isNotNull();
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyWordMorphophonoFormFilters(List<SearchCriterion> searchCriteria, Field<String> wordMorphophonoFormField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.MORPHOPHONO_FORM))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());
		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String morphophonoForm = criterion.getSearchValue().toString();
					where = applyValueFilter(morphophonoForm, criterion.isNot(), criterion.getSearchOperand(), wordMorphophonoFormField, where, true);
				}
			}
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = wordMorphophonoFormField.isNotNull();
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyWordFrequencyFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeys(searchCriteria, SearchKey.FREQUENCY, SearchKey.RANK);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		FreqCorp fc = FREQ_CORP.as("fc");
		WordFreq wf = WORD_FREQ.as("wf");

		Condition where1 = wf.WORD_ID.eq(wordIdField)
				.and(wf.FREQ_CORP_ID.in(DSL
						.select(fc.ID)
						.from(fc)
						.where(fc.IS_PUBLIC.isTrue())));

		for (SearchCriterion criterion : filteredCriteria) {
			SearchKey searchKey = criterion.getSearchKey();
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValue = criterion.getSearchValue().toString();
			if (SearchKey.FREQUENCY.equals(searchKey)) {
				BigDecimal searchValueDec = new BigDecimal(searchValue);
				if (SearchOperand.GREATER_THAN.equals(searchOperand)) {
					where1 = where1.and(wf.VALUE.ge(searchValueDec));
				} else if (SearchOperand.LESS_THAN.equals(searchOperand)) {
					where1 = where1.and(wf.VALUE.le(searchValueDec));
				}
			} else if (SearchKey.RANK.equals(searchKey)) {
				Long searchValueLong = Long.valueOf(searchValue);
				if (SearchOperand.GREATER_THAN.equals(searchOperand)) {
					where1 = where1.and(wf.RANK.ge(searchValueLong));
				} else if (SearchOperand.LESS_THAN.equals(searchOperand)) {
					where1 = where1.and(wf.RANK.le(searchValueLong));
				}
			}
		}
		where = where.andExists(DSL.select(wf.ID).from(wf).where(where1));
		return where;
	}

	public Condition applyWordOdRecommendationValueFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.VALUE_AND_EXISTS))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordFreeform wff = WORD_FREEFORM.as("wff");
		Freeform ff = FREEFORM.as("ff");
		Condition wordFreeformCondition = wff.WORD_ID.eq(wordIdField)
				.and(wff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.OD_WORD_RECOMMENDATION.name()));

		boolean isNotExistsSearch = isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, filteredCriteria);
		if (isNotExistsSearch) {
			where = where.and(DSL.notExists(DSL.select(wff.ID).from(wff, ff).where(wordFreeformCondition)));
		} else {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					wordFreeformCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, wordFreeformCondition, true);
				}
			}
			where = where.andExists(DSL.select(wff.WORD_ID).from(wff, ff).where(wordFreeformCondition));
		}
		return where;
	}

	public Condition applyWordOdRecommendationModificationFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.UPDATED_ON))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordFreeform wff = WORD_FREEFORM.as("wff");
		Freeform ff = FREEFORM.as("ff");
		Condition wordFreeformCondition = wff.WORD_ID.eq(wordIdField)
				.and(wff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.OD_WORD_RECOMMENDATION.name()));

		for (SearchCriterion criterion : filteredCriteria) {
			if (criterion.getSearchValue() != null) {
				String searchValueStr = criterion.getSearchValue().toString();
				wordFreeformCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.MODIFIED_ON, wordFreeformCondition, false);
			}
		}
		where = where.andExists(DSL.select(wff.WORD_ID).from(wff, ff).where(wordFreeformCondition));
		return where;
	}

	public Condition applyWordFreeformFilters(
			SearchKey searchKey,
			FreeformType freeformType,
			List<SearchCriterion> searchCriteria,
			Field<Long> wordIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		WordFreeform wff = WORD_FREEFORM.as("wff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = wff.WORD_ID.eq(wordIdField)
				.and(wff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(freeformType.name()));

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(wff.ID).from(wff, ff).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(wff.ID).from(wff, ff).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyWordRelationValueFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.WORD_RELATION, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordRelation wr = WORD_RELATION.as("wr");
		WordGroup wg = WORD_GROUP.as("wg");
		WordGroupMember wgm = WORD_GROUP_MEMBER.as("wgm");

		for (SearchCriterion criterion : filteredCriteria) {
			String relTypeCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();

			SelectJoinStep<Record2<Long, String>> selectWr = DSL
					.select(wr.WORD1_ID.as("word_id"), wr.WORD_REL_TYPE_CODE)
					.from(wr);

			SelectConditionStep<Record2<Long, String>> selectWg = DSL
					.select(wgm.WORD_ID, wg.WORD_REL_TYPE_CODE)
					.from(wgm, wg)
					.where(wgm.WORD_GROUP_ID.eq(wg.ID));

			Table<Record2<Long, String>> union = selectWr
					.unionAll(selectWg)
					.asTable("union");

			Condition critRelType = union.field("word_rel_type_code", String.class).eq(relTypeCode);
			if (isNot) {
				critRelType = DSL.not(critRelType);
			}

			where = where.and(DSL.exists(DSL
					.select(union.field("word_id"))
					.from(union)
					.where(union.field("word_id", Long.class).eq(wordIdField)
							.and(critRelType))));
		}
		return where;
	}

	public Condition applyWordRelationExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.WORD_RELATION);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordRelation wr = WORD_RELATION.as("wr");
		WordGroup wg = WORD_GROUP.as("wg");
		WordGroupMember wgm = WORD_GROUP_MEMBER.as("wgm");
		final String countFieldName = "cnt";

		SelectJoinStep<Record1<Long>> selectWr = DSL
				.select(wr.WORD1_ID.as("word_id"))
				.from(wr);

		SelectConditionStep<Record1<Long>> selectWg = DSL
				.select(wgm.WORD_ID)
				.from(wgm, wg)
				.where(wgm.WORD_GROUP_ID.eq(wg.ID));

		Table<Record1<Long>> union = selectWr
				.unionAll(selectWg)
				.asTable("union");

		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(union.field("word_id")).as(countFieldName))
				.from(union)
				.where(union.field("word_id", Long.class).eq(wordIdField))
				.asTable("wrcnt");

		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyFormFrequencyFilters(List<SearchCriterion> searchCriteria, Field<Long> formIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeys(searchCriteria, SearchKey.FREQUENCY, SearchKey.RANK);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		FreqCorp fc = FREQ_CORP.as("fc");
		FormFreq ff = FORM_FREQ.as("ff");

		Condition where1 = ff.FORM_ID.eq(formIdField)
				.and(ff.FREQ_CORP_ID.in(DSL
						.select(fc.ID)
						.from(fc)
						.where(fc.IS_PUBLIC.isTrue())));

		for (SearchCriterion criterion : filteredCriteria) {
			SearchKey searchKey = criterion.getSearchKey();
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValue = criterion.getSearchValue().toString();
			if (SearchKey.FREQUENCY.equals(searchKey)) {
				BigDecimal searchValueDec = new BigDecimal(searchValue);
				if (SearchOperand.GREATER_THAN.equals(searchOperand)) {
					where1 = where1.and(ff.VALUE.ge(searchValueDec));
				} else if (SearchOperand.LESS_THAN.equals(searchOperand)) {
					where1 = where1.and(ff.VALUE.le(searchValueDec));
				}
			} else if (SearchKey.RANK.equals(searchKey)) {
				Long searchValueLong = Long.valueOf(searchValue);
				if (SearchOperand.GREATER_THAN.equals(searchOperand)) {
					where1 = where1.and(ff.RANK.ge(searchValueLong));
				} else if (SearchOperand.LESS_THAN.equals(searchOperand)) {
					where1 = where1.and(ff.RANK.le(searchValueLong));
				}
			}
		}
		where = where.andExists(DSL.select(ff.ID).from(ff).where(where1));
		return where;
	}

	public Condition applyLexemeTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Word w1, Condition where) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.WORD_ID.eq(w1.ID);
		return applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, l1, where1, where);
	}

	public Condition applyLexemeTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Meaning m1, Condition where) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, l1, where1, where);
	}

	public Condition applyLexemeTagFilters(
			List<SearchCriterion> searchCriteria,
			SearchDatasetsRestriction searchDatasetsRestriction,
			Lexeme l1,
			Condition where1,
			Condition where) throws Exception {

		List<SearchCriterion> tagNameEqualsCrit = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.TAG_NAME, SearchOperand.EQUALS);
		List<SearchCriterion> tagNameNotEqualsCrit = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.TAG_NAME, SearchOperand.NOT_CONTAINS);

		if (CollectionUtils.isEmpty(tagNameEqualsCrit) && CollectionUtils.isEmpty(tagNameNotEqualsCrit)) {
			return where;
		}

		LexemeTag lt = LEXEME_TAG.as("lt");
		where1 = where1.and(lt.LEXEME_ID.eq(l1.ID));

		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);

		if (CollectionUtils.isNotEmpty(tagNameEqualsCrit)) {
			for (SearchCriterion criterion : tagNameEqualsCrit) {
				String searchValueStr = criterion.getSearchValue().toString();
				where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lt.TAG_NAME, where1, true);
			}
			where = where.andExists(DSL.select(lt.ID).from(l1, lt).where(where1));
		}
		if (CollectionUtils.isNotEmpty(tagNameNotEqualsCrit)) {
			for (SearchCriterion criterion : tagNameNotEqualsCrit) {
				String searchValueStr = criterion.getSearchValue().toString();
				where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lt.TAG_NAME, where1, true);
			}
			where = where.andNotExists(DSL.select(lt.ID).from(l1, lt).where(where1));
		}
		return where;
	}

	public Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria,
			SearchDatasetsRestriction searchDatasetsRestriction,
			ActivityEntity entityName,
			Meaning m1,
			Condition wherem1) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherem1);
	}

	public Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria,
			SearchDatasetsRestriction searchDatasetsRestriction,
			ActivityEntity entityName,
			Word w1,
			Condition wherew1) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.WORD_ID.eq(w1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherew1);
	}

	private Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria,
			SearchDatasetsRestriction searchDatasetsRestriction,
			ActivityEntity entityName,
			Lexeme l1,
			Condition where1,
			Condition where) throws Exception {

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
				where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			} else if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
				where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
			} else if (SearchOperand.HAS_BEEN.equals(criterion.getSearchOperand())) {
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

	public Condition applyLexemeComplexityFilters(List<SearchCriterion> searchCriteria, Field<String> entityComplexityField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.COMPLEXITY, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			String complexity = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = entityComplexityField.eq(complexity);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemePosValueFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LEXEME_POS, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemePos lpos = LEXEME_POS.as("lpos");

		Condition where1 = lpos.LEXEME_ID.eq(lexemeIdField);
		for (SearchCriterion criterion : filteredCriteria) {
			String lexemePosCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = lpos.POS_CODE.eq(lexemePosCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(lpos.ID).from(lpos).where(where1)));
		return where;
	}

	public Condition applyLexemePosExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.LEXEME_POS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemePos lpos = LEXEME_POS.as("lpos");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(lpos.ID).as(countFieldName))
				.from(lpos)
				.where(lpos.LEXEME_ID.eq(lexemeIdField))
				.asTable("lposcnt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeRegisterValueFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LEXEME_REGISTER, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemeRegister lreg = LEXEME_REGISTER.as("lreg");

		Condition where1 = lreg.LEXEME_ID.eq(lexemeIdField);
		for (SearchCriterion criterion : filteredCriteria) {
			String lexemeRegisterCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = lreg.REGISTER_CODE.eq(lexemeRegisterCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(lreg.ID).from(lreg).where(where1)));
		return where;
	}

	public Condition applyLexemeRegisterExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.LEXEME_REGISTER);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemeRegister lreg = LEXEME_REGISTER.as("lreg");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(lreg.ID).as(countFieldName))
				.from(lreg)
				.where(lreg.LEXEME_ID.eq(lexemeIdField))
				.asTable("lregcnt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeValueStateFilters(List<SearchCriterion> searchCriteria, Field<String> lexemeValueStateField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_VALUE_STATE))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());
		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String valueStateCode = criterion.getSearchValue().toString();
					boolean isNot = criterion.isNot();
					Condition critWhere = lexemeValueStateField.eq(valueStateCode);
					if (isNot) {
						critWhere = DSL.not(critWhere);
					}
					where = where.and(critWhere);
				}
			}
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = lexemeValueStateField.isNotNull();
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeProficiencyLevelFilters(List<SearchCriterion> searchCriteria, Field<String> lexemeProficiencyLevelField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.LEXEME_PROFICIENCY_LEVEL);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			String proficiencyLevelCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = lexemeProficiencyLevelField.eq(proficiencyLevelCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeFreeformFilters(
			SearchKey searchKey,
			FreeformType freeformType,
			List<SearchCriterion> searchCriteria,
			Field<Long> lexemeIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = lff.LEXEME_ID.eq(lexemeIdField)
				.and(lff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(freeformType.name()));

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(lff.ID).from(lff, ff).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(lff.ID).from(lff, ff).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				String searchValueStr = criterion.getSearchValue().toString();
				sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lsl.VALUE, sourceCondition, true);
			}
			where = where.and(DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
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
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition, true);
		}
		where = where.and(DSL.exists(DSL.select(ff.ID).from(lsl, s, sff, ff).where(sourceCondition)));
		return where;
	}

	public Condition applyMeaningSemanticTypeExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.SEMANTIC_TYPE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningSemanticType mst = MEANING_SEMANTIC_TYPE.as("mst");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(mst.ID).as(countFieldName))
				.from(mst)
				.where(mst.MEANING_ID.eq(meaningIdField))
				.asTable("mstcnt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyMeaningSemanticTypeValueFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.SEMANTIC_TYPE, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningSemanticType mst = MEANING_SEMANTIC_TYPE.as("mst");

		Condition where1 = mst.MEANING_ID.eq(meaningIdField);
		for (SearchCriterion criterion : filteredCriteria) {
			String semanticTypeCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = mst.SEMANTIC_TYPE_CODE.eq(semanticTypeCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(mst.ID).from(mst).where(where1)));
		return where;
	}

	public Condition applyMeaningAttributeFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredValueCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.ATTRIBUTE_VALUE))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredValueCriteria)) {
			return where;
		}

		List<SearchCriterion> filteredNameCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.ATTRIBUTE_NAME))
				.collect(toList());

		List<String> meaningAttributes = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(filteredNameCriteria)) {
			for (SearchCriterion criterion : filteredNameCriteria) {
				String searchValueStr = criterion.getSearchValue().toString();
				meaningAttributes.add(searchValueStr);
			}
		} else {
			meaningAttributes = Arrays.asList(MEANING_ATTRIBUTES);
		}

		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		Freeform ff = FREEFORM.as("ff");

		Condition meaningFreeformCondition = mff.MEANING_ID.eq(meaningIdField)
				.and(mff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.in(meaningAttributes));

		boolean isNotExistsSearch = isNotExistsSearch(SearchKey.ATTRIBUTE_VALUE, filteredValueCriteria);
		if (isNotExistsSearch) {
			where = where.and(DSL.notExists(DSL.select(mff.ID).from(mff, ff).where(meaningFreeformCondition)));
		} else {
			for (SearchCriterion criterion : filteredValueCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					meaningFreeformCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, meaningFreeformCondition, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(mff.ID).from(mff, ff).where(meaningFreeformCondition)));
		}
		return where;
	}

	public Condition applyMeaningRelationValueFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.MEANING_RELATION, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningRelation mr = MEANING_RELATION.as("mr");

		Condition where1 = mr.MEANING1_ID.eq(meaningIdField);
		for (SearchCriterion criterion : filteredCriteria) {
			String relTypeCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = mr.MEANING_REL_TYPE_CODE.eq(relTypeCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(mr.ID).from(mr).where(where1)));
		return where;
	}

	public Condition applyMeaningRelationExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.MEANING_RELATION);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningRelation mr = MEANING_RELATION.as("mr");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(mr.ID).as(countFieldName))
				.from(mr)
				.where(mr.MEANING1_ID.eq(meaningIdField)
						.and(mr.MEANING_REL_TYPE_CODE.ne(MEANING_REL_TYPE_CODE_SIMILAR)))
				.asTable("mrcnt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyMeaningFreeformFilters(
			SearchKey searchKey,
			FreeformType freeformType,
			List<SearchCriterion> searchCriteria,
			Field<Long> meaningIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = mff.MEANING_ID.eq(meaningIdField)
				.and(mff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(freeformType.name()));

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(mff.ID).from(mff, ff).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(mff.ID).from(mff, ff).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyDomainValueFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.DOMAIN, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningDomain md = MEANING_DOMAIN.as("md");

		Condition where1 = md.MEANING_ID.eq(meaningIdField);
		for (SearchCriterion criterion : filteredCriteria) {
			Classifier domain = (Classifier) criterion.getSearchValue();
			boolean isNot = criterion.isNot();

			Condition critWhere = md.DOMAIN_CODE.eq(domain.getCode()).and(md.DOMAIN_ORIGIN.eq(domain.getOrigin()));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(md.ID).from(md).where(where1)));
		return where;
	}

	public Condition applyDomainExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.DOMAIN);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningDomain md = MEANING_DOMAIN.as("md");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(md.ID).as(countFieldName))
				.from(md)
				.where(md.MEANING_ID.eq(meaningIdField))
				.asTable("mdcnt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			boolean isNot = criterion.isNot();
			Condition cntWhere = createCountCondition(searchOperand, cntTbl, countFieldName);
			Condition critWhere = DSL.exists(DSL.selectFrom(cntTbl).where(cntWhere));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyValueFilter(
			String searchValueStr,
			boolean isNot,
			SearchOperand searchOperand,
			Field<?> searchField,
			Condition condition,
			boolean isCaseInsensitive) throws Exception {

		Field<String> searchValueFieldLower = DSL.lower(searchValueStr);
		String searchValueStrLower = StringUtils.lowerCase(searchValueStr);
		if (SearchOperand.EQUALS.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isCaseInsensitive);
			Condition critWhere = textTypeSearchFieldCase.eq(searchValueFieldLower);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			condition = condition.and(critWhere);
		} else if (SearchOperand.NOT_CONTAINS.equals(searchOperand)) {
			//by value comparison it is exactly the same operation as equals
			//the not contains operand rather translates into join condition elsewhere
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isCaseInsensitive);
			condition = condition.and(textTypeSearchFieldCase.eq(searchValueFieldLower));
		} else if (SearchOperand.STARTS_WITH.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isCaseInsensitive);
			Condition critWhere = textTypeSearchFieldCase.startsWith(searchValueFieldLower);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			condition = condition.and(critWhere);
		} else if (SearchOperand.ENDS_WITH.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isCaseInsensitive);
			Condition critWhere = textTypeSearchFieldCase.endsWith(searchValueFieldLower);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			condition = condition.and(critWhere);
		} else if (SearchOperand.CONTAINS.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isCaseInsensitive);
			Condition critWhere = textTypeSearchFieldCase.contains(searchValueFieldLower);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			condition = condition.and(critWhere);
		} else if (SearchOperand.CONTAINS_WORD.equals(searchOperand)) {
			Field<Boolean> containsWord = DSL.field("to_tsvector('simple', {0}) @@ to_tsquery('simple', {1})", Boolean.class, searchField, DSL.inline(searchValueStrLower));
			if (isNot) {
				containsWord = DSL.not(containsWord);
			}
			condition = condition.and(containsWord);
		} else if (SearchOperand.EARLIER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<Timestamp> tsSearchField = (Field<Timestamp>) searchField;
			Field<Boolean> earlierThan = DSL.field("(date_part('epoch', {0}) * 1000) <= {1}", Boolean.class, tsSearchField, DSL.inline(date.getTime()));
			if (isNot) {
				earlierThan = DSL.not(earlierThan);
			}
			condition = condition.and(earlierThan);
		} else if (SearchOperand.LATER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<Timestamp> tsSearchField = (Field<Timestamp>) searchField;
			Field<Boolean> laterThan = DSL.field("(date_part('epoch', {0}) * 1000) >= {1}", Boolean.class, tsSearchField, DSL.inline(date.getTime()));
			if (isNot) {
				laterThan = DSL.not(laterThan);
			}
			condition = condition.and(laterThan);
		} else if (SearchOperand.REGEX.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, false);
			Condition critWhere = textTypeSearchFieldCase.likeRegex(searchValueStr);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			condition = condition.and(critWhere);
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	public Condition applyFreeformSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");
		Condition sourceCondition = ffsl.FREEFORM_ID.eq(freeformIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				String searchValueStr = criterion.getSearchValue().toString();
				sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ffsl.VALUE, sourceCondition, true);
			}
			where = where.and(DSL.exists(DSL.select(ffsl.ID).from(ffsl).where(sourceCondition)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(ffsl.ID).from(ffsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyFreeformSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
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
			boolean isNot = criterion.isNot();
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, isNot, criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(ff.ID).from(usl, s, sff, ff).where(sourceCondition)));
	}

	public Condition applyDefinitionSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				String searchValueStr = criterion.getSearchValue().toString();
				sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), dsl.VALUE, sourceCondition, true);
			}
			where = where.and(DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyDefinitionSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
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
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(ff.ID).from(dsl, s, sff, ff).where(sourceCondition)));
	}

	public Condition applyDefinitionFreeformFilters(
			SearchKey searchKey,
			FreeformType freeformType,
			List<SearchCriterion> searchCriteria,
			Field<Long> definitionIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(searchKey))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = dff.DEFINITION_ID.eq(definitionIdField)
				.and(dff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(freeformType.name()));

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE_TEXT, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(dff.ID).from(dff, ff).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(dff.ID).from(dff, ff).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	@SuppressWarnings("unchecked")
	private Field<String> getTextTypeSearchFieldCase(Field<?> searchField, boolean isCaseInsensitive) {
		Field<String> searchFieldStr = (Field<String>) searchField;
		if (isCaseInsensitive) {
			return DSL.lower(searchFieldStr);
		}
		return searchFieldStr;
	}

	public List<SearchCriterion> filterSourceRefCriteria(List<SearchCriterion> searchCriteria) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> {
					if (crit.getSearchKey().equals(SearchKey.SOURCE_REF)) {
						if (crit.getSearchOperand().equals(SearchOperand.EXISTS)) {
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

	public List<SearchCriterion> filterCriteriaBySearchKey(List<SearchCriterion> searchCriteria, SearchKey searchKey) {
		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeys(searchCriteria, searchKey);
		return filteredCriteria;
	}

	public List<SearchCriterion> filterCriteriaBySearchKeys(List<SearchCriterion> searchCriteria, SearchKey... searchKeys) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.filter(crit -> ArrayUtils.contains(searchKeys, crit.getSearchKey()))
				.collect(toList());
		return filteredCriteria;
	}

	public List<SearchCriterion> filterCriteriaBySearchKeyAndOperands(List<SearchCriterion> searchCriteria, SearchKey searchKey, SearchOperand... searchOperands) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> ArrayUtils.contains(searchOperands, crit.getSearchOperand()) && crit.getSearchValue() != null)
				.filter(crit -> searchKey.equals(crit.getSearchKey()))
				.collect(Collectors.toList());
		return filteredCriteria;
	}

	public List<SearchCriterion> filterExistsSearchCriteria(List<SearchCriterion> searchCriteria, SearchKey... searchKeys) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> SearchOperand.EXISTS.equals(crit.getSearchOperand())
						|| SearchOperand.SINGLE.equals(crit.getSearchOperand())
						|| SearchOperand.MULTIPLE.equals(crit.getSearchOperand()))
				.filter(crit -> ArrayUtils.contains(searchKeys, crit.getSearchKey()))
				.collect(Collectors.toList());
		return filteredCriteria;
	}

	public boolean containsSearchKeys(List<SearchCriterion> searchCriteria, SearchKey... searchKeys) {
		return searchCriteria.stream().map(SearchCriterion::getSearchKey).anyMatch(searchKey -> ArrayUtils.contains(searchKeys, searchKey));
	}

	public boolean isNotExistsSearch(SearchKey searchKey, List<SearchCriterion> searchCriteria) {

		return searchCriteria.stream()
				.anyMatch(crit -> crit.getSearchKey().equals(searchKey)
						&& crit.getSearchOperand().equals(SearchOperand.EXISTS)
						&& crit.getSearchValue() == null
						&& crit.isNot());
	}

	private Condition createCountCondition(SearchOperand searchOperand, Table<Record1<Integer>> idAndCount, String countFieldName) {

		Condition whereItemCount;
		if (searchOperand.equals(SearchOperand.EXISTS)) {
			whereItemCount = idAndCount.field(countFieldName, Integer.class).gt(0);
		} else if (searchOperand.equals(SearchOperand.SINGLE)) {
			whereItemCount = idAndCount.field(countFieldName, Integer.class).eq(1);
		} else if (searchOperand.equals(SearchOperand.MULTIPLE)) {
			whereItemCount = idAndCount.field(countFieldName, Integer.class).gt(1);
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return whereItemCount;
	}

}
