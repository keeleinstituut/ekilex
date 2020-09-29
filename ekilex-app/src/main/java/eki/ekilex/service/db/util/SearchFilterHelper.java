package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_FREQUENCY;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeActivityLog;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeFrequency;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.LexemeRegister;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningRelation;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordFreeform;
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

	public Condition applyValueFilters(SearchKey searchKey, List<SearchCriterion> searchCriteria, Field<String> valueField, Condition condition, boolean isOnLowerValue) throws Exception {

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

	public Condition applyDomainFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition m1Where) {

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

	public Condition applyLexemeTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Word w1, Condition condition) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.WORD_ID.eq(w1.ID);
		return applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, l1, where1, condition);
	}

	public Condition applyLexemeTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Meaning m1, Condition condition) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeTagFilters(searchCriteria, searchDatasetsRestriction, l1, where1, condition);
	}

	public Condition applyLexemeTagFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Lexeme l1, Condition where1, Condition where) throws Exception {

		List<SearchCriterion> tagNameEqualsCrit = searchCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null)
				.filter(crit -> crit.getSearchKey().equals(SearchKey.TAG_NAME) && crit.getSearchOperand().equals(SearchOperand.EQUALS)).collect(toList());
		List<SearchCriterion> tagNameNotEqualsCrit = searchCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null)
				.filter(crit -> crit.getSearchKey().equals(SearchKey.TAG_NAME) && crit.getSearchOperand().equals(SearchOperand.NOT_EQUALS)).collect(toList());

		if (CollectionUtils.isEmpty(tagNameEqualsCrit) && CollectionUtils.isEmpty(tagNameNotEqualsCrit)) {
			return where;
		}

		LexemeTag lt = LEXEME_TAG.as("lt");
		where1 = where1
				.and(l1.TYPE.eq(LEXEME_TYPE_PRIMARY))
				.and(lt.LEXEME_ID.eq(l1.ID));

		where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);

		if (CollectionUtils.isNotEmpty(tagNameEqualsCrit)) {
			for (SearchCriterion criterion : tagNameEqualsCrit) {
				where1 = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lt.TAG_NAME, where1, false);
			}
			where = where.andExists(DSL.select(lt.ID).from(l1, lt).where(where1));
		}
		if (CollectionUtils.isNotEmpty(tagNameNotEqualsCrit)) {
			for (SearchCriterion criterion : tagNameNotEqualsCrit) {
				where1 = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), lt.TAG_NAME, where1, false);
			}
			where = where.andNotExists(DSL.select(lt.ID).from(l1, lt).where(where1));
		}
		return where;
	}

	public Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, ActivityEntity entityName, Meaning m1, Condition wherem1) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherem1);
	}

	public Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, ActivityEntity entityName, Word w1, Condition wherew1) throws Exception {

		Lexeme l1 = Lexeme.LEXEME.as("l1");
		Condition where1 = l1.WORD_ID.eq(w1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherew1);
	}

	private Condition applyLexemeActivityLogFilters(
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

	public Condition applyLexemePosValueFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.LEXEME_POS)
						&& (crit.getSearchOperand().equals(SearchOperand.EQUALS) || crit.getSearchOperand().equals(SearchOperand.NOT_EQUALS))
						&& crit.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		LexemePos lpos = LEXEME_POS.as("lpos");
		for (SearchCriterion criterion : filteredCriteria) {
			String lexemePosCode = criterion.getSearchValue().toString();
			Condition where1 = lpos.LEXEME_ID.eq(lexemeIdField)
					.and(lpos.POS_CODE.eq(lexemePosCode));
			condition = condition.and(DSL.exists(DSL.select(lpos.ID).from(lpos).where(where1)));
		}
		return condition;
	}

	public Condition applyLexemePosExistsFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Condition where1, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_POS))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemePos lpos1 = LEXEME_POS.as("lpos1");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();

			Table<Record2<Long, Integer>> lexPos = DSL
					.select(l1.ID.as("lexeme_id"), DSL.count(lpos1.ID).as("lpos_count"))
					.from(l1.leftOuterJoin(lpos1).on(lpos1.LEXEME_ID.eq(l1.ID)))
					.where(where1)
					.groupBy(l1.ID)
					.asTable("lexpos");

			Condition wherePosCount = createCountCondition(searchOperand, lexPos, "lpos_count");

			where = where.andExists(DSL.select(lexPos.field("lexeme_id", Long.class)).from(lexPos).where(wherePosCount));
		}
		return where;
	}

	public Condition applyLexemeRegisterValueFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.LEXEME_REGISTER)
						&& (crit.getSearchOperand().equals(SearchOperand.EQUALS) || crit.getSearchOperand().equals(SearchOperand.NOT_EQUALS))
						&& crit.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		LexemeRegister lr = LEXEME_REGISTER.as("lr");
		for (SearchCriterion criterion : filteredCriteria) {
			String lexemeRegisterCode = criterion.getSearchValue().toString();
			Condition where1 = lr.LEXEME_ID.eq(lexemeIdField)
					.and(lr.REGISTER_CODE.eq(lexemeRegisterCode));
			condition = condition.and(DSL.exists(DSL.select(lr.ID).from(lr).where(where1)));
		}
		return condition;
	}

	public Condition applyLexemeRegisterExistsFilters(List<SearchCriterion> searchCriteria, Lexeme l1, Condition where1, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_REGISTER))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemeRegister lr1 = LEXEME_REGISTER.as("lr");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();

			Table<Record2<Long, Integer>> lexReg = DSL
					.select(l1.ID.as("lexeme_id"), DSL.count(lr1.ID).as("lr_count"))
					.from(l1.leftOuterJoin(lr1).on(lr1.LEXEME_ID.eq(l1.ID)))
					.where(where1)
					.groupBy(l1.ID)
					.asTable("lexreg");

			Condition whereRegisterCount = createCountCondition(searchOperand, lexReg, "lr_count");

			where = where.andExists(DSL.select(lexReg.field("lexeme_id", Long.class)).from(lexReg).where(whereRegisterCount));
		}
		return where;
	}

	public Condition applyWordTypeExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.WORD_TYPE))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordWordType wwt = WORD_WORD_TYPE.as("wwt");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();

			Table<Record2<Long, Integer>> wordWordType = DSL
					.select(wordIdField.as("word_id"), DSL.count(wwt.ID).as("wt_count"))
					.from(wwt)
					.where(wwt.WORD_ID.eq(wordIdField))
					.groupBy(wordIdField)
					.asTable("word_word_type");

			Condition whereWordTypeCount = createCountCondition(searchOperand, wordWordType, "wt_count");

			where = where.andExists(DSL.select(wordWordType.field("word_id", Long.class)).from(wordWordType).where(whereWordTypeCount));
		}
		return where;
	}

	private Condition createCountCondition(SearchOperand searchOperand, Table<Record2<Long, Integer>> idAndCount, String countFieldName) {

		Condition whereItemCount;
		if (searchOperand.equals(SearchOperand.NOT_EXISTS)) {
			whereItemCount = idAndCount.field(countFieldName, Integer.class).eq(0);
		} else if (searchOperand.equals(SearchOperand.EXISTS)) {
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

	public Condition applyLexemeFrequencyFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.LEXEME_FREQUENCY)
						&& (crit.getSearchOperand().equals(SearchOperand.GREATER_THAN) || crit.getSearchOperand().equals(SearchOperand.LESS_THAN))
						&& crit.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		LexemeFrequency lfr = LEXEME_FREQUENCY.as("lfr");
		for (SearchCriterion criterion : filteredCriteria) {
			SearchOperand searchOperand = criterion.getSearchOperand();
			String rankValueStr = criterion.getSearchValue().toString();
			rankValueStr = RegExUtils.replaceAll(rankValueStr, "[^0-9.]", "");
			if (StringUtils.isEmpty(rankValueStr)) {
				continue;
			}

			BigInteger rankValue = new BigInteger(rankValueStr);
			boolean isGreaterThan = SearchOperand.GREATER_THAN.equals(searchOperand);

			Table<Record1<BigInteger>> lr = DSL
					.select(DSL.field("(array_agg(lfr.rank order by lfr.created_on desc))[1]", BigInteger.class).as("lex_rank"))
					.from(lfr)
					.where(lfr.LEXEME_ID.eq(lexemeIdField))
					.groupBy(lfr.LEXEME_ID).asTable("lr");

			Field<BigInteger> lexRankField = lr.field("lex_rank", BigInteger.class);

			Condition compareCondition;
			if (isGreaterThan) {
				compareCondition = lexRankField.greaterThan(rankValue);
			} else {
				compareCondition = lexRankField.lessThan(rankValue);
			}

			condition = condition.and(DSL.exists(DSL.select(lexRankField).from(lr).where(compareCondition)));
		}
		return condition;
	}

	public Condition applyLexemeGrammarFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_GRAMMAR))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff = FREEFORM.as("ff");
		Condition lexFreeformCondition = lff.LEXEME_ID.eq(lexemeIdField)
				.and(lff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.GRAMMAR.name()));

		boolean isNotExistsSearch = isNotExistsSearch(SearchKey.LEXEME_GRAMMAR, filteredCriteria);
		if (isNotExistsSearch) {
			condition = condition.and(DSL.notExists(DSL.select(lff.ID).from(lff, ff).where(lexFreeformCondition)));
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			if (criterion.getSearchValue() != null) {
				lexFreeformCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, lexFreeformCondition, true);
			}
		}
		condition = condition.and(DSL.exists(DSL.select(lff.ID).from(lff, ff).where(lexFreeformCondition)));
		return condition;
	}

	public Condition applyPublicityFilters(List<SearchCriterion> searchCriteria, Field<Boolean> lexemeIsPublicField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.PUBLICITY)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& crit.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			boolean isPublic = BooleanUtils.toBoolean(criterion.getSearchValue().toString());
			condition = condition.and(lexemeIsPublicField.eq(isPublic));
		}
		return condition;
	}

	public Condition applyMeaningRelationFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.RELATION_TYPE)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& (crit.getSearchValue() != null))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		MeaningRelation mr = MEANING_RELATION.as("mr");
		for (SearchCriterion criterion : filteredCriteria) {
			String relTypeCode = criterion.getSearchValue().toString();
			Condition where1 = mr.MEANING1_ID.eq(meaningIdField)
					.and(mr.MEANING_REL_TYPE_CODE.eq(relTypeCode));
			condition = condition.and(DSL.exists(DSL.select(mr.ID).from(mr).where(where1)));
		}
		return condition;
	}

	public Condition applyWordOdRecommendationFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition condition) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.OD_RECOMMENDATION))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		WordFreeform wff = WORD_FREEFORM.as("wff");
		Freeform ff = FREEFORM.as("ff");
		Condition wordFreeformCondition = wff.WORD_ID.eq(wordIdField)
				.and(wff.FREEFORM_ID.eq(ff.ID))
				.and(ff.TYPE.eq(FreeformType.OD_WORD_RECOMMENDATION.name()));

		boolean isNotExistsSearch = isNotExistsSearch(SearchKey.OD_RECOMMENDATION, filteredCriteria);
		if (isNotExistsSearch) {
			condition = condition.and(DSL.notExists(DSL.select(wff.ID).from(wff, ff).where(wordFreeformCondition)));
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			if (criterion.getSearchValue() != null) {
				wordFreeformCondition = applyValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, wordFreeformCondition, true);
			}
		}
		condition = condition.andExists(DSL.select(wff.WORD_ID).from(wff, ff).where(wordFreeformCondition));
		return condition;
	}

	public Condition applyWordAspectFilters(List<SearchCriterion> searchCriteria, Field<String> wordAspectField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.ASPECT))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		boolean isNotExistsSearch = filteredCriteria.stream().anyMatch(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS));
		if (isNotExistsSearch) {
			condition = condition.and(wordAspectField.isNull());
			return condition;
		}

		boolean isExistsSearch = filteredCriteria.stream().anyMatch(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS));
		if (isExistsSearch) {
			condition = condition.and(wordAspectField.isNotNull());
		}

		List<SearchCriterion> aspectEqualsCrit = filteredCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null)
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EQUALS))
				.collect(toList());
		List<SearchCriterion> aspectNotEqualsCrit = filteredCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null)
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.NOT_EQUALS))
				.collect(toList());

		if (CollectionUtils.isNotEmpty(aspectEqualsCrit)) {
			for (SearchCriterion criterion : aspectEqualsCrit) {
				String aspectCode = criterion.getSearchValue().toString();
				condition = condition.and(wordAspectField.eq(aspectCode));
			}
		}

		if (CollectionUtils.isNotEmpty(aspectNotEqualsCrit)) {
			for (SearchCriterion criterion : aspectNotEqualsCrit) {
				String aspectCode = criterion.getSearchValue().toString();
				condition = condition.and(wordAspectField.ne(aspectCode));
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

	public Condition applyValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition, boolean isOnLowerValue) throws Exception {

		searchValueStr = StringUtils.lowerCase(searchValueStr);
		if (SearchOperand.EQUALS.equals(searchOperand)) {
			Field<String> textTypeSearchFieldCase = getTextTypeSearchFieldCase(searchField, isOnLowerValue);
			condition = condition.and(textTypeSearchFieldCase.equal(searchValueStr));
		} else if (SearchOperand.NOT_EQUALS.equals(searchOperand)) {
			//by value comparison it is exactly the same operation as equals
			//the not equals operand rather translates into join condition elsewhere
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

	public Condition applyLexemeSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

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

	public Condition applyLexemeSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition condition) throws Exception {

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

	public Condition applyFreeformSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

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

	public Condition applyFreeformSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> freeformIdField, Condition condition) throws Exception {

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

	public Condition applyDefinitionSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

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

	public Condition applyDefinitionSourceNameFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition condition) throws Exception {

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

	@SuppressWarnings("unchecked")
	private Field<String> getTextTypeSearchFieldCase(Field<?> searchField, boolean isOnLowerValue) {
		Field<String> searchFieldStr = (Field<String>) searchField;
		if (isOnLowerValue) {
			return DSL.lower(searchFieldStr);
		}
		return searchFieldStr;
	}

	public List<SearchCriterion> filterSourceRefCriteria(List<SearchCriterion> searchCriteria) {
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

	public List<SearchCriterion> filterCriteriaBySearchKey(List<SearchCriterion> searchCriteria, SearchKey searchKey) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.filter(crit -> crit.getSearchKey().equals(searchKey))
				.collect(toList());
		return filteredCriteria;
	}

	public List<SearchCriterion> filterCriteriaBySearchKeys(List<SearchCriterion> searchCriteria, SearchKey... searchKeys) {
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.filter(crit -> ArrayUtils.contains(searchKeys, crit.getSearchKey()))
				.collect(toList());
		return filteredCriteria;
	}

	public List<SearchCriterion> filterPositiveValueSearchCriteria(List<SearchCriterion> searchCriteria) {
		// any other than NOT_EQUALS
		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> !SearchOperand.NOT_EQUALS.equals(crit.getSearchOperand()) && crit.getSearchValue() != null)
				.collect(Collectors.toList());
		return filteredCriteria;
	}

	public List<SearchCriterion> filterNegativeValueSearchCriteria(List<SearchCriterion> searchCriteria) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> SearchOperand.NOT_EQUALS.equals(crit.getSearchOperand()) && crit.getSearchValue() != null)
				.collect(Collectors.toList());
		return filteredCriteria;
	}

	public List<SearchCriterion> filterExistsSearchCriteria(List<SearchCriterion> searchCriteria) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> SearchOperand.NOT_EXISTS.equals(crit.getSearchOperand())
										|| SearchOperand.EXISTS.equals(crit.getSearchOperand())
										|| SearchOperand.SINGLE.equals(crit.getSearchOperand())
										|| SearchOperand.MULTIPLE.equals(crit.getSearchOperand()))
				.collect(Collectors.toList());
		return filteredCriteria;
	}

	public boolean containsSearchKeys(List<SearchCriterion> searchCriteria, SearchKey... searchKeys) {
		return searchCriteria.stream().map(SearchCriterion::getSearchKey).anyMatch(searchKey -> ArrayUtils.contains(searchKeys, searchKey));
	}

	public boolean isNotExistsSearch(SearchKey searchKey, List<SearchCriterion> searchCriteria) {

		return searchCriteria.stream()
				.anyMatch(crit -> crit.getSearchKey().equals(searchKey)
						&& crit.getSearchOperand().equals(SearchOperand.NOT_EXISTS)
						&& crit.getSearchValue() == null);
	}
}
