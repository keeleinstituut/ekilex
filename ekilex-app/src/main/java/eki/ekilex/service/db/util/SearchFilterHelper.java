package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DOMAIN;
import static eki.ekilex.data.db.main.Tables.FORM_FREQ;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.FREQ_CORP;
import static eki.ekilex.data.db.main.Tables.GOVERNMENT;
import static eki.ekilex.data.db.main.Tables.GRAMMAR;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_POS;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.LEX_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.main.Tables.MEANING_TAG;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.WORD_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD_FORUM;
import static eki.ekilex.data.db.main.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.main.Tables.WORD_FREQ;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD_OD_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_TAG;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.ActivityOwner;
import eki.common.constant.FreeformConstant;
import eki.common.constant.GlobalConstant;
import eki.common.constant.WordStatus;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.DefinitionNote;
import eki.ekilex.data.db.main.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.main.tables.DefinitionSourceLink;
import eki.ekilex.data.db.main.tables.Domain;
import eki.ekilex.data.db.main.tables.FormFreq;
import eki.ekilex.data.db.main.tables.Freeform;
import eki.ekilex.data.db.main.tables.FreqCorp;
import eki.ekilex.data.db.main.tables.Government;
import eki.ekilex.data.db.main.tables.Grammar;
import eki.ekilex.data.db.main.tables.LexRelation;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeActivityLog;
import eki.ekilex.data.db.main.tables.LexemeDeriv;
import eki.ekilex.data.db.main.tables.LexemeFreeform;
import eki.ekilex.data.db.main.tables.LexemeNote;
import eki.ekilex.data.db.main.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.main.tables.LexemePos;
import eki.ekilex.data.db.main.tables.LexemeRegister;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningDomain;
import eki.ekilex.data.db.main.tables.MeaningForum;
import eki.ekilex.data.db.main.tables.MeaningFreeform;
import eki.ekilex.data.db.main.tables.MeaningNote;
import eki.ekilex.data.db.main.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.main.tables.MeaningRelation;
import eki.ekilex.data.db.main.tables.MeaningSemanticType;
import eki.ekilex.data.db.main.tables.MeaningTag;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.UsageSourceLink;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordActivityLog;
import eki.ekilex.data.db.main.tables.WordForum;
import eki.ekilex.data.db.main.tables.WordFreeform;
import eki.ekilex.data.db.main.tables.WordFreq;
import eki.ekilex.data.db.main.tables.WordGroup;
import eki.ekilex.data.db.main.tables.WordGroupMember;
import eki.ekilex.data.db.main.tables.WordLastActivityLog;
import eki.ekilex.data.db.main.tables.WordOdRecommendation;
import eki.ekilex.data.db.main.tables.WordRelation;
import eki.ekilex.data.db.main.tables.WordTag;
import eki.ekilex.data.db.main.tables.WordWordType;

@Component
public class SearchFilterHelper implements GlobalConstant, ActivityFunct, FreeformConstant {

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

	public Condition applyCommaSeparatedIdsFilters(List<SearchCriterion> searchCriteria, Field<Long> idField, Condition condition) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.COMMA_SEPARATED_IDS)
						&& crit.getSearchOperand().equals(SearchOperand.EQUALS)
						&& crit.getSearchValue() != null)
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return condition;
		}

		for (SearchCriterion criterion : filteredCriteria) {
			String commaSeparatedSearchIdsStr = criterion.getSearchValue().toString();
			commaSeparatedSearchIdsStr = RegExUtils.replaceAll(commaSeparatedSearchIdsStr, "[^0-9.,]", "");
			List<String> idStrings = Arrays.asList(StringUtils.split(commaSeparatedSearchIdsStr, ','));

			criterion.setSearchValue(commaSeparatedSearchIdsStr);
			condition = condition.and(idField.in(idStrings));
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

	public Condition applyWordStatusFilters(List<SearchCriterion> searchCriteria, Lexeme l, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.WORD_STATUS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		Condition where1 = DSL.noCondition();

		for (SearchCriterion criterion : filteredCriteria) {

			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValueStr = criterion.getSearchValue().toString();
			WordStatus wordStatus = WordStatus.valueOf(searchValueStr);
			boolean isNot = criterion.isNot();

			if (SearchOperand.IS.equals(searchOperand)) {
				if (WordStatus.WORD.equals(wordStatus)) {
					where1 = where1.and(l.IS_WORD.isTrue());
				} else if (WordStatus.COLLOCATION.equals(wordStatus)) {
					where1 = where1.and(l.IS_COLLOCATION.isTrue());
				}
			} else if (SearchOperand.IS_NOT.equals(searchOperand)) {
				if (WordStatus.WORD.equals(wordStatus)) {
					where1 = where1.and(l.IS_WORD.isFalse());
				} else if (WordStatus.COLLOCATION.equals(wordStatus)) {
					where1 = where1.and(l.IS_COLLOCATION.isFalse());
				}
			}
			if (isNot) {
				where1 = DSL.not(where1);
			}
		}
		where = where.and(where1);
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

	public Condition applyWordDisplayMorphFilters(List<SearchCriterion> searchCriteria, Field<String> wordDisplayMorphField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.DISPLAY_MORPH))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());
		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String displayMorphCode = criterion.getSearchValue().toString();
					boolean isNot = criterion.isNot();
					Condition critWhere = wordDisplayMorphField.eq(displayMorphCode);
					if (isNot) {
						critWhere = DSL.not(critWhere);
					}
					where = where.and(critWhere);
				}
			}
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = wordDisplayMorphField.isNotNull();
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

	public Condition applyWordVocalFormFilters(List<SearchCriterion> searchCriteria, Field<String> wordVocalFormField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.VOCAL_FORM))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());
		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String vocalForm = criterion.getSearchValue().toString();
					where = applyValueFilter(vocalForm, criterion.isNot(), criterion.getSearchOperand(), wordVocalFormField, where, true);
				}
			}
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = wordVocalFormField.isNotNull();
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

	public Condition applyWordRegYearFilters(List<SearchCriterion> searchCriteria, Field<Integer> wordRegYearField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.REG_YEAR))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String regYearStr = criterion.getSearchValue().toString();
					if (!NumberUtils.isDigits(regYearStr)) {
						continue;
					}
					Integer regYear = Integer.valueOf(regYearStr);
					boolean isNot = criterion.isNot();
					Condition critWhere = wordRegYearField.eq(regYear);
					if (isNot) {
						critWhere = DSL.not(critWhere);
					}
					where = where.and(critWhere);
				}
			}
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = wordRegYearField.isNotNull();
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

		WordOdRecommendation wor = WORD_OD_RECOMMENDATION.as("wor");
		Condition where1 = wor.WORD_ID.eq(wordIdField);

		boolean isNotExistsSearch = isNotExistsSearch(SearchKey.VALUE_AND_EXISTS, filteredCriteria);
		if (isNotExistsSearch) {
			where = where.and(DSL.notExists(DSL.select(wor.ID).from(wor).where(where1)));
		} else {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wor.VALUE, where1, true);
				}
			}
			where = where.andExists(DSL.select(wor.ID).from(wor).where(where1));
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

		WordOdRecommendation wor = WORD_OD_RECOMMENDATION.as("wor");
		Condition where1 = wor.WORD_ID.eq(wordIdField);

		for (SearchCriterion criterion : filteredCriteria) {
			if (criterion.getSearchValue() != null) {
				String searchValueStr = criterion.getSearchValue().toString();
				where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wor.MODIFIED_ON, where1, false);
			}
		}
		where = where.andExists(DSL.select(wor.ID).from(wor).where(where1));
		return where;
	}

	public Condition applyWordForumFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.WORD_FORUM))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		WordForum wf = WORD_FORUM.as("wf");
		Condition where1 = wf.WORD_ID.eq(wordIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wf.VALUE, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(wf.ID).from(wf).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(wf.ID).from(wf).where(where1));
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
					.where(
							union.field("word_id", Long.class).eq(wordIdField)
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

	public Condition applyWordActivityLogFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition wherew) throws Exception {

		WordActivityLog wal = WORD_ACTIVITY_LOG.as("wal");
		WordLastActivityLog wlal = WORD_LAST_ACTIVITY_LOG.as("wlal");
		ActivityLog al = ACTIVITY_LOG.as("al");

		List<SearchCriterion> filteredCriteriaByCreatedOrUpdatedByOnly = filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY);
		boolean isFilterByCreatedOrUpdatedByOnly = CollectionUtils.isNotEmpty(filteredCriteriaByCreatedOrUpdatedByOnly);

		// by all logs
		boolean isFilterByAllLogs = containsSearchKeys(searchCriteria, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

		if (isFilterByAllLogs || isFilterByCreatedOrUpdatedByOnly) {

			List<SearchCriterion> filteredCriteriaByAllLogs = filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON);

			if (CollectionUtils.isNotEmpty(filteredCriteriaByAllLogs)) {

				Condition where1 = wal.WORD_ID.eq(wordIdField).and(wal.ACTIVITY_LOG_ID.eq(al.ID));

				for (SearchCriterion criterion : filteredCriteriaByAllLogs) {
					String critValue = criterion.getSearchValue().toString();
					if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
						where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
					} else if (SearchKey.UPDATED_ON.equals(criterion.getSearchKey())) {
						where1 = where1
								.andNot(al.ENTITY_NAME.eq(ActivityEntity.WORD.name()).and(al.FUNCT_NAME.like(LIKE_CREATE)));
						where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
					} else if (SearchKey.CREATED_ON.equals(criterion.getSearchKey())) {
						where1 = where1
								.and(al.OWNER_NAME.eq(ActivityOwner.WORD.name()))
								.and(al.OWNER_ID.eq(wordIdField))
								.and(al.ENTITY_NAME.eq(ActivityEntity.WORD.name()))
								.and(al.FUNCT_NAME.like(LIKE_CREATE));
						where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
					}
				}
				wherew = wherew.andExists(DSL.select(wal.ID).from(wal, al).where(where1));
			}
		}

		// by last logs
		boolean isFilterByLastLogs = containsSearchKeys(searchCriteria, SearchKey.LAST_UPDATE_ON);

		if (isFilterByLastLogs) {

			List<SearchCriterion> filteredCriteriaByLastLogs = filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.LAST_UPDATE_ON);

			if (CollectionUtils.isNotEmpty(filteredCriteriaByLastLogs)) {

				Condition where1 = wlal.WORD_ID.eq(wordIdField).and(wlal.ACTIVITY_LOG_ID.eq(al.ID));

				for (SearchCriterion criterion : filteredCriteriaByLastLogs) {
					String critValue = criterion.getSearchValue().toString();
					if (SearchKey.CREATED_OR_UPDATED_BY.equals(criterion.getSearchKey())) {
						where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
					} else if (SearchKey.LAST_UPDATE_ON.equals(criterion.getSearchKey())) {
						where1 = applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
					}
				}
				wherew = wherew.andExists(DSL.select(wlal.ID).from(wlal, al).where(where1));
			}
		}

		return wherew;
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

	public Condition applyTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Word w1, Condition where) throws Exception {

		List<SearchCriterion> tagNameEqualsCrit = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.TAG_NAME, SearchOperand.EQUALS);
		List<SearchCriterion> tagNameNotEqualsCrit = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.TAG_NAME, SearchOperand.NOT_CONTAINS);

		if (CollectionUtils.isEmpty(tagNameEqualsCrit) && CollectionUtils.isEmpty(tagNameNotEqualsCrit)) {
			return where;
		}

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		LexemeTag lt = LEXEME_TAG.as("lt");
		MeaningTag mt = MEANING_TAG.as("mt");
		WordTag wt = WORD_TAG.as("wt");

		if (CollectionUtils.isNotEmpty(tagNameEqualsCrit)) {

			Condition where1 = l1.WORD_ID.eq(w1.ID);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			Condition whereVal1 = DSL.noCondition();
			Condition whereVal2 = DSL.noCondition();
			Condition whereVal3 = DSL.noCondition();

			for (SearchCriterion criterion : tagNameEqualsCrit) {
				String searchValueStr = criterion.getSearchValue().toString();
				whereVal1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lt.TAG_NAME, whereVal1, true);
				whereVal2 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), mt.TAG_NAME, whereVal2, true);
				whereVal3 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wt.TAG_NAME, whereVal3, true);
			}
			where = where.andExists(DSL
					.select(l1.ID)
					.from(l1
							.leftOuterJoin(lt).on(lt.LEXEME_ID.eq(l1.ID))
							.leftOuterJoin(mt).on(mt.MEANING_ID.eq(l1.MEANING_ID))
							.leftOuterJoin(wt).on(wt.WORD_ID.eq(l1.WORD_ID)))
					.where(where1.and(DSL.or(whereVal1, whereVal2, whereVal3))));
		}
		if (CollectionUtils.isNotEmpty(tagNameNotEqualsCrit)) {

			Condition where1 = l1.WORD_ID.eq(w1.ID).and(lt.LEXEME_ID.eq(l1.ID));
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			Condition where2 = l2.WORD_ID.eq(w1.ID).and(l2.MEANING_ID.eq(mt.MEANING_ID));
			where2 = applyDatasetRestrictions(l2, searchDatasetsRestriction, where2);
			Condition where3 = wt.WORD_ID.eq(w1.ID);

			for (SearchCriterion criterion : tagNameNotEqualsCrit) {
				String searchValueStr = criterion.getSearchValue().toString();
				where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lt.TAG_NAME, where1, true);
				where2 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), mt.TAG_NAME, where2, true);
				where3 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wt.TAG_NAME, where3, true);
			}
			where = where.and(DSL
					.notExists(DSL.select(lt.ID).from(l1, lt).where(where1))
					.andNotExists(DSL.select(mt.ID).from(l2, mt).where(where2))
					.andNotExists(DSL.select(wt.ID).from(wt).where(where3)));
		}

		return where;
	}

	public Condition applyTagFilters(List<SearchCriterion> searchCriteria, SearchDatasetsRestriction searchDatasetsRestriction, Meaning m1, Condition where) throws Exception {

		List<SearchCriterion> tagNameEqualsCrit = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.TAG_NAME, SearchOperand.EQUALS);
		List<SearchCriterion> tagNameNotEqualsCrit = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.TAG_NAME, SearchOperand.NOT_CONTAINS);

		if (CollectionUtils.isEmpty(tagNameEqualsCrit) && CollectionUtils.isEmpty(tagNameNotEqualsCrit)) {
			return where;
		}

		Lexeme l1 = LEXEME.as("l1");
		LexemeTag lt = LEXEME_TAG.as("lt");
		MeaningTag mt = MEANING_TAG.as("mt");
		WordTag wt = WORD_TAG.as("wt");

		if (CollectionUtils.isNotEmpty(tagNameEqualsCrit)) {

			Condition where1 = l1.MEANING_ID.eq(m1.ID);
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			Condition whereVal1 = DSL.noCondition();
			Condition whereVal2 = DSL.noCondition();
			Condition whereVal3 = DSL.noCondition();

			for (SearchCriterion criterion : tagNameEqualsCrit) {
				String searchValueStr = criterion.getSearchValue().toString();
				whereVal1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lt.TAG_NAME, whereVal1, true);
				whereVal2 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), mt.TAG_NAME, whereVal2, true);
				whereVal3 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wt.TAG_NAME, whereVal3, true);
			}
			where = where.andExists(DSL
					.select(l1.ID)
					.from(l1
							.leftOuterJoin(lt).on(lt.LEXEME_ID.eq(l1.ID))
							.leftOuterJoin(mt).on(mt.MEANING_ID.eq(l1.MEANING_ID))
							.leftOuterJoin(wt).on(wt.WORD_ID.eq(l1.WORD_ID)))
					.where(where1.and(DSL.or(whereVal1, whereVal2, whereVal3))));
		}
		if (CollectionUtils.isNotEmpty(tagNameNotEqualsCrit)) {

			Condition where1 = l1.MEANING_ID.eq(m1.ID).and(lt.LEXEME_ID.eq(l1.ID));
			where1 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where1);
			Condition where2 = mt.MEANING_ID.eq(m1.ID);
			Condition where3 = l1.MEANING_ID.eq(m1.ID).and(wt.WORD_ID.eq(l1.WORD_ID));
			where3 = applyDatasetRestrictions(l1, searchDatasetsRestriction, where3);

			for (SearchCriterion criterion : tagNameNotEqualsCrit) {
				String searchValueStr = criterion.getSearchValue().toString();
				where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lt.TAG_NAME, where1, true);
				where2 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), mt.TAG_NAME, where2, true);
				where3 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), wt.TAG_NAME, where3, true);
			}
			where = where.and(DSL
					.notExists(DSL.select(lt.ID).from(l1, lt).where(where1))
					.andNotExists(DSL.select(mt.ID).from(mt).where(where2)))
					.andNotExists(DSL.select(wt.ID).from(l1, wt).where(where3));
		}

		return where;
	}

	public Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria,
			SearchDatasetsRestriction searchDatasetsRestriction,
			ActivityEntity entityName,
			Meaning m1,
			Condition wherem1) throws Exception {

		Lexeme l1 = LEXEME.as("l1");
		Condition where1 = l1.MEANING_ID.eq(m1.ID);
		return applyLexemeActivityLogFilters(searchCriteria, searchDatasetsRestriction, entityName, l1, where1, wherem1);
	}

	public Condition applyLexemeActivityLogFilters(
			List<SearchCriterion> searchCriteria,
			SearchDatasetsRestriction searchDatasetsRestriction,
			ActivityEntity entityName,
			Word w1,
			Condition wherew1) throws Exception {

		Lexeme l1 = LEXEME.as("l1");
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

	public Condition applyLexemeDerivValueFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LEXEME_DERIV, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemeDeriv lder = LEXEME_DERIV.as("lder");
		Condition where1 = lder.LEXEME_ID.eq(lexemeIdField);

		for (SearchCriterion criterion : filteredCriteria) {

			String lexemeDerivCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = lder.DERIV_CODE.eq(lexemeDerivCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(lder.ID).from(lder).where(where1)));
		return where;
	}

	public Condition applyLexemeDerivExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.LEXEME_DERIV);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemeDeriv lder = LEXEME_DERIV.as("lder");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(lder.ID).as(countFieldName))
				.from(lder)
				.where(lder.LEXEME_ID.eq(lexemeIdField))
				.asTable("ldercnt");

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

	public Condition applyLexemeRelationValueFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.LEXEME_RELATION, SearchOperand.EQUALS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexRelation lrel = LEX_RELATION.as("lrel");
		Condition where1 = lrel.LEXEME1_ID.eq(lexemeIdField);

		for (SearchCriterion criterion : filteredCriteria) {

			String lexemeRelationTypeCode = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			Condition critWhere = lrel.LEX_REL_TYPE_CODE.eq(lexemeRelationTypeCode);
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where1 = where1.and(critWhere);
		}
		where = where.and(DSL.exists(DSL.select(lrel.ID).from(lrel).where(where1)));
		return where;
	}

	public Condition applyLexemeRelationExistsFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterExistsSearchCriteria(searchCriteria, SearchKey.LEXEME_RELATION);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexRelation lrel = LEX_RELATION.as("lrel");
		final String countFieldName = "cnt";
		Table<Record1<Integer>> cntTbl = DSL
				.select(DSL.count(lrel.ID).as(countFieldName))
				.from(lrel)
				.where(lrel.LEXEME1_ID.eq(lexemeIdField))
				.asTable("lrelcnt");

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

	// TODO should be removed after entity move
	public Condition applyLexemeFreeformFilters(
			SearchKey searchKey,
			String freeformTypeCode,
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
				.and(ff.FREEFORM_TYPE_CODE.eq(freeformTypeCode));

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ff.VALUE, where1, true);
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

	public Condition applyLexemeGovernmentFilters(
			List<SearchCriterion> searchCriteria,
			Field<Long> lexemeIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_GOVERNMENT))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		Government lg = GOVERNMENT.as("lg");
		Condition where1 = lg.LEXEME_ID.eq(lexemeIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lg.VALUE, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(lg.ID).from(lg).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(lg.ID).from(lg).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeGrammarFilters(
			List<SearchCriterion> searchCriteria,
			Field<Long> lexemeIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_GRAMMAR))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		Grammar lg = GRAMMAR.as("lg");
		Condition where1 = lg.LEXEME_ID.eq(lexemeIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), lg.VALUE, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(lg.ID).from(lg).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(lg.ID).from(lg).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeNoteFilters(
			List<SearchCriterion> searchCriteria,
			Field<Long> lexemeIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.LEXEME_NOTE))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		LexemeNote ln = LEXEME_NOTE.as("ln");
		Condition where1 = ln.LEXEME_ID.eq(lexemeIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), ln.VALUE, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(ln.ID).from(ln).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(ln.ID).from(ln).where(where1));
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

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(lsl.ID).from(lsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyLexemeSourceFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) throws Exception {

		boolean containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE);
		if (!containsSearchKeys) {
			return where;
		}

		List<SearchCriterion> filteredByNameCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);
		List<SearchCriterion> filteredByValueCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_VALUE);

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Source s = Source.SOURCE.as("s");

		Condition sourceCondition = lsl.LEXEME_ID.eq(lexemeIdField).and(lsl.SOURCE_ID.eq(s.ID));

		for (SearchCriterion criterion : filteredByNameCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.NAME, sourceCondition, true);
		}
		for (SearchCriterion criterion : filteredByValueCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.VALUE, sourceCondition, true);
		}
		where = where.and(DSL.exists(DSL.select(s.ID).from(lsl, s).where(sourceCondition)));
		return where;
	}

	public Condition applyLexemeNoteSourceFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeNoteIdField, Condition where) throws Exception {

		boolean containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE);
		if (!containsSearchKeys) {
			return where;
		}

		List<SearchCriterion> filteredByNameCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);
		List<SearchCriterion> filteredByValueCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_VALUE);

		LexemeNoteSourceLink lnsl = LEXEME_NOTE_SOURCE_LINK.as("lnsl");
		Source s = Source.SOURCE.as("s");

		Condition sourceCondition = lnsl.LEXEME_NOTE_ID.eq(lexemeNoteIdField).and(lnsl.SOURCE_ID.eq(s.ID));

		for (SearchCriterion criterion : filteredByNameCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.NAME, sourceCondition, true);
		}
		for (SearchCriterion criterion : filteredByValueCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.VALUE, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(s.ID).from(lnsl, s).where(sourceCondition)));
	}

	public Condition applyLexemeNoteSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> lexemeNoteIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		LexemeNoteSourceLink lnsl = LEXEME_NOTE_SOURCE_LINK.as("lnsl");
		Condition sourceCondition = lnsl.LEXEME_NOTE_ID.eq(lexemeNoteIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(lnsl.ID).from(lnsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyUsageSourceFilters(List<SearchCriterion> searchCriteria, Field<Long> usageIdField, Condition where) throws Exception {

		boolean containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE);
		if (!containsSearchKeys) {
			return where;
		}

		List<SearchCriterion> filteredByNameCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);
		List<SearchCriterion> filteredByValueCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_VALUE);

		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		Source s = Source.SOURCE.as("s");

		Condition sourceCondition = usl.USAGE_ID.eq(usageIdField).and(usl.SOURCE_ID.eq(s.ID));

		for (SearchCriterion criterion : filteredByNameCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.NAME, sourceCondition, true);
		}
		for (SearchCriterion criterion : filteredByValueCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.VALUE, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(s.ID).from(usl, s).where(sourceCondition)));
	}

	public Condition applyUsageSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> usageIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		Condition sourceCondition = usl.USAGE_ID.eq(usageIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(usl.ID).from(usl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyMeaningNoteSourceFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningNoteIdField, Condition where) throws Exception {

		boolean containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE);
		if (!containsSearchKeys) {
			return where;
		}

		List<SearchCriterion> filteredByNameCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);
		List<SearchCriterion> filteredByValueCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_VALUE);

		MeaningNoteSourceLink mnsl = MEANING_NOTE_SOURCE_LINK.as("mnsl");
		Source s = Source.SOURCE.as("s");

		Condition sourceCondition = mnsl.MEANING_NOTE_ID.eq(meaningNoteIdField).and(mnsl.SOURCE_ID.eq(s.ID));

		for (SearchCriterion criterion : filteredByNameCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.NAME, sourceCondition, true);
		}
		for (SearchCriterion criterion : filteredByValueCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.VALUE, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(s.ID).from(mnsl, s).where(sourceCondition)));
	}

	public Condition applyMeaningNoteSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> meaningNoteIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		MeaningNoteSourceLink mnsl = MEANING_NOTE_SOURCE_LINK.as("mnsl");
		Condition sourceCondition = mnsl.MEANING_NOTE_ID.eq(meaningNoteIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(mnsl.ID).from(mnsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
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

	public Condition applyWordFreeformFilters(List<SearchCriterion> searchCriteria, Field<Long> wordIdField, Condition where) throws Exception {

		if (containsSearchKeys(
				searchCriteria,
				SearchKey.LEXEME_ATTRIBUTE_NAME,
				SearchKey.MEANING_ATTRIBUTE_NAME,
				SearchKey.CONCEPT_ATTRIBUTE_NAME)) {
			return where;
		}

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeys(searchCriteria, SearchKey.WORD_ATTRIBUTE_NAME, SearchKey.TERM_ATTRIBUTE_NAME, SearchKey.ATTRIBUTE_VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		WordFreeform wff = WORD_FREEFORM.as("wff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = wff.WORD_ID.eq(wordIdField)
				.and(wff.FREEFORM_ID.eq(ff.ID));
		where1 = applyFreeformFilters(filteredCriteria, "word", ff, where1);
		where = where.and(DSL.exists(DSL.select(wff.ID).from(wff, ff).where(where1)));
		return where;
	}

	public Condition applyLexemeFreeformFilters(List<SearchCriterion> searchCriteria, Field<Long> lexemeIdField, Condition where) throws Exception {

		if (containsSearchKeys(
				searchCriteria,
				SearchKey.WORD_ATTRIBUTE_NAME,
				SearchKey.TERM_ATTRIBUTE_NAME,
				SearchKey.MEANING_ATTRIBUTE_NAME,
				SearchKey.CONCEPT_ATTRIBUTE_NAME)) {
			return where;
		}

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeys(searchCriteria, SearchKey.LEXEME_ATTRIBUTE_NAME, SearchKey.ATTRIBUTE_VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = lff.LEXEME_ID.eq(lexemeIdField)
				.and(lff.FREEFORM_ID.eq(ff.ID));
		where1 = applyFreeformFilters(filteredCriteria, "lexeme", ff, where1);
		where = where.and(DSL.exists(DSL.select(lff.ID).from(lff, ff).where(where1)));
		return where;
	}

	public Condition applyMeaningFreeformFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) throws Exception {

		if (containsSearchKeys(
				searchCriteria,
				SearchKey.WORD_ATTRIBUTE_NAME,
				SearchKey.TERM_ATTRIBUTE_NAME,
				SearchKey.LEXEME_ATTRIBUTE_NAME)) {
			return where;
		}

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeys(searchCriteria, SearchKey.MEANING_ATTRIBUTE_NAME, SearchKey.CONCEPT_ATTRIBUTE_NAME, SearchKey.ATTRIBUTE_VALUE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		Freeform ff = FREEFORM.as("ff");
		Condition where1 = mff.MEANING_ID.eq(meaningIdField)
				.and(mff.FREEFORM_ID.eq(ff.ID));
		where1 = applyFreeformFilters(filteredCriteria, "meaning", ff, where1);
		where = where.and(DSL.exists(DSL.select(mff.ID).from(mff, ff).where(where1)));
		return where;
	}

	private Condition applyFreeformFilters(List<SearchCriterion> filteredCriteria, String context, Freeform ff, Condition where1) throws Exception {

		for (SearchCriterion criterion : filteredCriteria) {

			SearchKey searchKey = criterion.getSearchKey();
			SearchOperand searchOperand = criterion.getSearchOperand();
			String searchValueStr = criterion.getSearchValue().toString();
			boolean isNot = criterion.isNot();
			if (isAttributeNameSearchKey(searchKey)) {
				where1 = applyValueFilter(searchValueStr, isNot, searchOperand, ff.FREEFORM_TYPE_CODE, where1, true);
			} else if (SearchKey.ATTRIBUTE_VALUE.equals(searchKey)) {
				where1 = applyValueFilter(searchValueStr, isNot, searchOperand, ff.VALUE, where1, true);
			}
		}
		return where1;
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

	public Condition applyMeaningNoteFilters(
			List<SearchCriterion> searchCriteria,
			Field<Long> meaningIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.MEANING_NOTE))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		MeaningNote mn = MEANING_NOTE.as("mn");
		Condition where1 = mn.MEANING_ID.eq(meaningIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), mn.VALUE, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(mn.ID).from(mn).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(mn.ID).from(mn).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyMeaningForumFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.MEANING_FORUM))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		MeaningForum mf = MEANING_FORUM.as("mf");
		Condition where1 = mf.MEANING_ID.eq(meaningIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() == null) {
					continue;
				}
				String searchValueStr = criterion.getSearchValue().toString();
				where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), mf.VALUE, where1, true);
			}
			where = where.and(DSL.exists(DSL.select(mf.ID).from(mf).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(mf.ID).from(mf).where(where1));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyDomainValueFilters(List<SearchCriterion> searchCriteria, Field<Long> meaningIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = filterCriteriaBySearchKeyAndOperands(searchCriteria, SearchKey.DOMAIN, SearchOperand.EQUALS, SearchOperand.SUB_CONTAINS);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		MeaningDomain md = MEANING_DOMAIN.as("md");
		Domain d = DOMAIN.as("d");
		Condition where1 = md.MEANING_ID.eq(meaningIdField);

		for (SearchCriterion criterion : filteredCriteria) {

			boolean isNot = criterion.isNot();
			SearchOperand searchOperand = criterion.getSearchOperand();
			Classifier domainClassif = (Classifier) criterion.getSearchValue();
			String domainCode = domainClassif.getCode();
			String domainOrigin = domainClassif.getOrigin();
			Condition critWhere = null;

			if (SearchOperand.EQUALS.equals(searchOperand)) {

				critWhere = md.DOMAIN_ORIGIN.eq(domainOrigin).and(md.DOMAIN_CODE.eq(domainCode));

			} else if (SearchOperand.SUB_CONTAINS.equals(searchOperand)) {

				CommonTableExpression<Record3<String, String, String>> dt = DSL
						.name("dt")
						.fields("origin", "code", "parent_code")
						.as(DSL
								.select(
										d.ORIGIN,
										d.CODE,
										d.PARENT_CODE)
								.from(d)
								.where(
										d.ORIGIN.eq(domainOrigin)
												.and(d.CODE.eq(domainCode)))
								.unionAll(DSL
										.select(
												d.ORIGIN,
												d.CODE,
												d.PARENT_CODE)
										.from(DSL.table("dt"), d)
										.where(
												d.ORIGIN.eq(DSL.field("dt.origin", String.class))
														.and(d.PARENT_CODE.eq(DSL.field("dt.code", String.class))))));

				critWhere = md.DOMAIN_ORIGIN.eq(domainOrigin)
						.and(md.DOMAIN_CODE.in(
								DSL
										.withRecursive(dt)
										.select(dt.field("code", String.class))
										.from(dt)));

			}
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
			Field<LocalDateTime> tsSearchField = (Field<LocalDateTime>) searchField;
			Field<Boolean> earlierThan = DSL.field("(date_part('epoch', {0}) * 1000) <= {1}", Boolean.class, tsSearchField, DSL.inline(date.getTime()));
			if (isNot) {
				earlierThan = DSL.not(earlierThan);
			}
			condition = condition.and(earlierThan);
		} else if (SearchOperand.LATER_THAN.equals(searchOperand)) {
			Date date = dateFormat.parse(searchValueStr);
			@SuppressWarnings("unchecked")
			Field<LocalDateTime> tsSearchField = (Field<LocalDateTime>) searchField;
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

	public Condition applyDefinitionSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(dsl.ID).from(dsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyDefinitionSourceFilters(List<SearchCriterion> searchCriteria, Field<Long> definitionIdField, Condition where) throws Exception {

		boolean containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE);
		if (!containsSearchKeys) {
			return where;
		}

		List<SearchCriterion> filteredByNameCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);
		List<SearchCriterion> filteredByValueCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_VALUE);

		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = Source.SOURCE.as("s");

		Condition sourceCondition = dsl.DEFINITION_ID.eq(definitionIdField).and(dsl.SOURCE_ID.eq(s.ID));

		for (SearchCriterion criterion : filteredByNameCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.NAME, sourceCondition, true);
		}
		for (SearchCriterion criterion : filteredByValueCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.VALUE, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(s.ID).from(dsl, s).where(sourceCondition)));
	}

	public Condition applyDefinitionNoteSourceFilters(List<SearchCriterion> searchCriteria, Field<Long> definitionNoteIdField, Condition where) throws Exception {

		boolean containsSearchKeys = containsSearchKeys(searchCriteria, SearchKey.SOURCE_NAME, SearchKey.SOURCE_VALUE);
		if (!containsSearchKeys) {
			return where;
		}

		List<SearchCriterion> filteredByNameCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_NAME);
		List<SearchCriterion> filteredByValueCriteria = filterCriteriaBySearchKey(searchCriteria, SearchKey.SOURCE_VALUE);

		DefinitionNoteSourceLink dnsl = DEFINITION_NOTE_SOURCE_LINK.as("dnsl");
		Source s = Source.SOURCE.as("s");

		Condition sourceCondition = dnsl.DEFINITION_NOTE_ID.eq(definitionNoteIdField).and(dnsl.SOURCE_ID.eq(s.ID));

		for (SearchCriterion criterion : filteredByNameCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.NAME, sourceCondition, true);
		}
		for (SearchCriterion criterion : filteredByValueCriteria) {
			String searchValueStr = criterion.getSearchValue().toString();
			sourceCondition = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), s.VALUE, sourceCondition, true);
		}
		return where.and(DSL.exists(DSL.select(s.ID).from(dnsl, s).where(sourceCondition)));
	}

	public Condition applyDefinitionNoteSourceRefFilter(List<SearchCriterion> searchCriteria, Field<Long> definitionNoteIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = filterSourceRefCriteria(searchCriteria);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream()
				.filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS))
				.collect(toList());

		DefinitionNoteSourceLink dnsl = DEFINITION_NOTE_SOURCE_LINK.as("dnsl");
		Condition sourceCondition = dnsl.DEFINITION_NOTE_ID.eq(definitionNoteIdField);

		if (CollectionUtils.isNotEmpty(existsCriteria)) {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(dnsl.ID).from(dnsl).where(sourceCondition));
			if (isNot) {
				critWhere = DSL.not(critWhere);
			}
			where = where.and(critWhere);
		}
		return where;
	}

	public Condition applyDefinitionNoteFilters(
			List<SearchCriterion> searchCriteria,
			Field<Long> definitionIdField,
			Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchCriteria.stream()
				.filter(c -> c.getSearchKey().equals(SearchKey.DEFINITION_NOTE))
				.collect(toList());

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		List<SearchCriterion> existsCriteria = filteredCriteria.stream().filter(crit -> crit.getSearchOperand().equals(SearchOperand.EXISTS)).collect(toList());

		DefinitionNote dn = DEFINITION_NOTE.as("dn");
		Condition where1 = dn.DEFINITION_ID.eq(definitionIdField);

		if (CollectionUtils.isEmpty(existsCriteria)) {
			for (SearchCriterion criterion : filteredCriteria) {
				if (criterion.getSearchValue() != null) {
					String searchValueStr = criterion.getSearchValue().toString();
					where1 = applyValueFilter(searchValueStr, criterion.isNot(), criterion.getSearchOperand(), dn.VALUE, where1, true);
				}
			}
			where = where.and(DSL.exists(DSL.select(dn.ID).from(dn).where(where1)));
		} else {
			boolean isNot = existsCriteria.get(0).isNot();
			Condition critWhere = DSL.exists(DSL.select(dn.ID).from(dn).where(where1));
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
				.filter(crit -> (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
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

	private boolean isAttributeNameSearchKey(SearchKey searchKey) {
		return Arrays
				.asList(
						SearchKey.WORD_ATTRIBUTE_NAME,
						SearchKey.TERM_ATTRIBUTE_NAME,
						SearchKey.LEXEME_ATTRIBUTE_NAME,
						SearchKey.MEANING_ATTRIBUTE_NAME,
						SearchKey.CONCEPT_ATTRIBUTE_NAME)
				.contains(searchKey);
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
