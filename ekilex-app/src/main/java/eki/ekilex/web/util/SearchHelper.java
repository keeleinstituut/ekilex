package eki.ekilex.web.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.core.UserContext;

@Component
public class SearchHelper implements WebConstant, GlobalConstant {

	private static final char PATH_SEPARATOR = '/';
	private static final char DATASETS_SEPARATOR = ',';
	private static final char TRUNCATED_SYM = '.';
	private static final String EMPTY_VALUE = "-";
	private static final String SEARCH_MODE = "smode";
	private static final String RESULT_MODE = "rmode";
	private static final String RESULT_LANG = "rlang";
	private static final String DATASETS = "dicts";
	private static final String SIMPLE_SEARCH_FILTER = "sfilt";
	private static final String DETAIL_SEARCH_FILTER = "dfilt";
	private static final String CRITERIA_GROUP = "critgr";
	private static final String CRITERION = "crit";
	private static final String CRITERION_VALUE = "val";
	private static final String CRITERION_CLASSIFIER = "cla";
	private static final String CRITERION_NOT = "not";
	private static final String CRITERION_VAL_ANTI_TRUNC_MASK = "¤";

	@Autowired
	private UserContext userContext;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private ValueUtil valueUtil;

	@Autowired
	private MessageSource messageSource;

	public String composeSearchUri(List<String> datasets, String simpleSearchFilter) {
		return composeSearchUri(WebConstant.SEARCH_MODE_SIMPLE, datasets, simpleSearchFilter, null, SearchResultMode.WORD, null);
	}

	public String composeSearchUriAndAppendId(List<String> datasets, String simpleSearchFilter, Long entityId) {
		String searchUri = composeSearchUri(WebConstant.SEARCH_MODE_SIMPLE, datasets, simpleSearchFilter, null, SearchResultMode.WORD, null);
		searchUri += "?id=" + entityId;
		return searchUri;
	}

	public String composeSearchUri(
			String searchMode, 
			List<String> selectedDatasets,
			String simpleSearchFilter,
			SearchFilter detailSearchFilter,
			SearchResultMode resultMode,
			String resultLang) {

		StringBuffer uriBuf = new StringBuffer();

		// search mode
		uriBuf.append(PATH_SEPARATOR);
		uriBuf.append(SEARCH_MODE);
		uriBuf.append(PATH_SEPARATOR);
		uriBuf.append(searchMode);

		// result mode
		uriBuf.append(PATH_SEPARATOR);
		uriBuf.append(RESULT_MODE);
		uriBuf.append(PATH_SEPARATOR);
		uriBuf.append(resultMode.name());

		// result lang
		if (StringUtils.isNotEmpty(resultLang)) {
			uriBuf.append(PATH_SEPARATOR);
			uriBuf.append(RESULT_LANG);
			uriBuf.append(PATH_SEPARATOR);
			uriBuf.append(resultLang);
		}

		// datasets
		if (CollectionUtils.isNotEmpty(selectedDatasets)) {
			Long userId = userContext.getUserId();
			List<String> userVisibleDatasets = permissionService.getUserVisibleDatasetCodes(userId);
			List<String> validDatasetSelection = new ArrayList<>(CollectionUtils.intersection(selectedDatasets, userVisibleDatasets));
			Collection<String> datasetComparison = CollectionUtils.disjunction(validDatasetSelection, userVisibleDatasets);
			if (CollectionUtils.isNotEmpty(validDatasetSelection) && CollectionUtils.isNotEmpty(datasetComparison)) {
				List<String> encodedDatasets = validDatasetSelection.stream().map(dataset -> encode(dataset)).collect(Collectors.toList());
				String encodedDatasetsStr = StringUtils.join(encodedDatasets, DATASETS_SEPARATOR);
				uriBuf.append(PATH_SEPARATOR);
				uriBuf.append(DATASETS);
				uriBuf.append(PATH_SEPARATOR);
				uriBuf.append(encodedDatasetsStr);
			}
		}

		// search crit
		if (StringUtils.equals(WebConstant.SEARCH_MODE_SIMPLE, searchMode) && StringUtils.isNotBlank(simpleSearchFilter)) {
			String critValue = new String(simpleSearchFilter);
			critValue = StringUtils.trim(critValue);
			critValue = valueUtil.unifyToApostrophe(critValue);
			critValue = encode(critValue);
			uriBuf.append(PATH_SEPARATOR);
			uriBuf.append(SIMPLE_SEARCH_FILTER);
			uriBuf.append(PATH_SEPARATOR);
			uriBuf.append(critValue);
		} else if (StringUtils.equals(WebConstant.SEARCH_MODE_DETAIL, searchMode) && (detailSearchFilter != null)) {
			List<SearchCriterionGroup> criteriaGroups = detailSearchFilter.getCriteriaGroups();
			if (CollectionUtils.isNotEmpty(criteriaGroups)) {
				uriBuf.append(PATH_SEPARATOR);
				uriBuf.append(DETAIL_SEARCH_FILTER);
				for (SearchCriterionGroup criteriaGroup : criteriaGroups) {
					uriBuf.append(PATH_SEPARATOR);
					uriBuf.append(CRITERIA_GROUP);
					uriBuf.append(PATH_SEPARATOR);
					uriBuf.append(criteriaGroup.getEntity().name());
					for (SearchCriterion searchCriterion : criteriaGroup.getSearchCriteria()) {
						uriBuf.append(PATH_SEPARATOR);
						uriBuf.append(CRITERION);
						uriBuf.append(PATH_SEPARATOR);
						uriBuf.append(searchCriterion.getSearchKey().name());
						uriBuf.append(PATH_SEPARATOR);
						uriBuf.append(searchCriterion.getSearchOperand().name());
						Object critObj = searchCriterion.getSearchValue();
						if (critObj == null) {
							uriBuf.append(PATH_SEPARATOR);
							uriBuf.append(CRITERION_VALUE);
							uriBuf.append(PATH_SEPARATOR);
							uriBuf.append(EMPTY_VALUE);
						} else {
							String critValue = critObj.toString();
							if (StringUtils.isEmpty(critValue)) {
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(CRITERION_VALUE);
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(EMPTY_VALUE);
							} else if (critObj instanceof Classifier) {
								Classifier classif = (Classifier) critObj;
								String origin = classif.getOrigin();
								if (StringUtils.isEmpty(origin)) {
									origin = EMPTY_VALUE;
								}
								origin = encode(origin);
								String code = encode(classif.getCode());
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(CRITERION_CLASSIFIER);
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(classif.getName());
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(origin);
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(code);
							} else {
								critValue = StringUtils.trim(critValue);
								critValue = valueUtil.unifyToApostrophe(critValue);
								if (StringUtils.contains(critValue, TRUNCATED_SYM)) {
									critValue = critValue + CRITERION_VAL_ANTI_TRUNC_MASK;
								}
								critValue = encode(critValue);
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(CRITERION_VALUE);
								uriBuf.append(PATH_SEPARATOR);
								uriBuf.append(critValue);
							}
						}
						uriBuf.append(PATH_SEPARATOR);
						uriBuf.append(CRITERION_NOT);
						uriBuf.append(PATH_SEPARATOR);
						uriBuf.append(searchCriterion.isNot());
					}
				}
			}
		}

		return uriBuf.toString();
	}

	public void addValidationMessages(SearchFilter searchFilter) {

		List<SearchCriterionGroup> criteriaGroups = searchFilter.getCriteriaGroups();
		if (CollectionUtils.isEmpty(criteriaGroups)) {
			return;
		}
		for (SearchCriterionGroup criterionGroup : criteriaGroups) {
			List<SearchCriterion> searchCriteria = criterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			for (SearchCriterion searchCriterion : searchCriteria) {
				SearchOperand searchOperand = searchCriterion.getSearchOperand();
				Object searchValue = searchCriterion.getSearchValue();
				String validationMessageKey = null;
				if (SearchOperand.EXISTS.equals(searchOperand)) {
					continue;
				}
				if (searchValue == null) {
					validationMessageKey = "search.validation.empty-value";
				} else if (StringUtils.isBlank(searchValue.toString())) {
					validationMessageKey = "search.validation.empty-value";
				} else {
					String searchValueStr = StringUtils.trim(searchValue.toString());
					if (SearchOperand.CONTAINS_WORD.equals(searchOperand) && StringUtils.containsWhitespace(searchValueStr)) {
						validationMessageKey = "search.validation.contains-words";
					} else if (SearchOperand.REGEX.equals(searchOperand)) {
						try {
							Pattern.compile(searchValueStr);
						} catch (PatternSyntaxException e) {
							validationMessageKey = "search.validation.invalid-regex";
						}
					}
				}
				if (StringUtils.isNotBlank(validationMessageKey)) {
					String validationMessage = messageSource.getMessage(validationMessageKey, new Object[0], LocaleContextHolder.getLocale());
					searchCriterion.setValidationMessage(validationMessage);
				}
			}
		}
	}

	public SearchUriData parseSearchUri(String searchPage, String searchUri) {

		boolean isValid;
		String searchMode = null;
		List<String> selectedDatasets = null;
		String simpleSearchFilter = null;
		SearchFilter detailSearchFilter = null;
		SearchResultMode resultMode = null;
		String resultLang = null;

		String[] uriParts = StringUtils.split(searchUri, PATH_SEPARATOR);

		for (int uriPartIndex = 0; uriPartIndex < uriParts.length; uriPartIndex++) {
			String uriPart = uriParts[uriPartIndex];
			if (uriPartIndex == uriParts.length - 1) {
				break;
			}
			if (StringUtils.equals(SEARCH_MODE, uriPart)) {
				searchMode = uriParts[uriPartIndex + 1];
			} else if (StringUtils.equals(RESULT_MODE, uriPart)) {
				String resultModeStr = uriParts[uriPartIndex + 1];
				try {
					resultMode = SearchResultMode.valueOf(resultModeStr.toUpperCase());
				} catch (Exception e) {
					resultMode = SearchResultMode.WORD;
				}
			} else if (StringUtils.equals(RESULT_LANG, uriPart)) {
				resultLang = uriParts[uriPartIndex + 1];
			} else if (StringUtils.equals(DATASETS, uriPart)) {
				String selectedDatasetsStr = uriParts[uriPartIndex + 1];
				selectedDatasetsStr = decode(selectedDatasetsStr);
				selectedDatasetsStr = StringUtils.remove(selectedDatasetsStr, ' ');
				selectedDatasets = Arrays.asList(StringUtils.split(selectedDatasetsStr, DATASETS_SEPARATOR));
			} else if (StringUtils.equals(SIMPLE_SEARCH_FILTER, uriPart)) {
				simpleSearchFilter = uriParts[uriPartIndex + 1];
				simpleSearchFilter = decode(simpleSearchFilter);
			} else if (StringUtils.equals(DETAIL_SEARCH_FILTER, uriPart)) {
				detailSearchFilter = new SearchFilter();
				detailSearchFilter.setCriteriaGroups(new ArrayList<>());
			} else if (StringUtils.equals(CRITERIA_GROUP, uriPart)) {
				String searchEntityStr = uriParts[uriPartIndex + 1];
				SearchEntity entity = null;
				try {
					entity = SearchEntity.valueOf(searchEntityStr.toUpperCase());
				} catch (Exception e) {
					break;
				}
				SearchCriterionGroup criterionGroup = new SearchCriterionGroup();
				criterionGroup.setEntity(entity);
				criterionGroup.setSearchCriteria(new ArrayList<>());
				List<SearchCriterionGroup> criteriaGroups = detailSearchFilter.getCriteriaGroups();
				criteriaGroups.add(criterionGroup);
			} else if (StringUtils.equals(CRITERION, uriPart)) {
				if (uriPartIndex > uriParts.length - 5) {
					break;
				}
				// crit key
				String searchKeyStr = uriParts[uriPartIndex + 1];
				SearchKey searchKey = null;
				try {
					searchKey = SearchKey.valueOf(searchKeyStr.toUpperCase());
				} catch (Exception e) {
					break;
				}
				// crit operand
				String searchOperandStr = uriParts[uriPartIndex + 2];
				SearchOperand searchOperand = null;
				try {
					searchOperand = SearchOperand.valueOf(searchOperandStr.toUpperCase());
				} catch (Exception e) {
					break;
				}
				// crit value
				Object searchValueObj = null;
				String searchValueType = uriParts[uriPartIndex + 3];
				boolean isNot = false;
				if (StringUtils.equals(CRITERION_VALUE, searchValueType)) {
					String searchValueStr = uriParts[uriPartIndex + 4];
					searchValueStr = decode(searchValueStr);
					searchValueStr = StringUtils.stripEnd(searchValueStr, CRITERION_VAL_ANTI_TRUNC_MASK);
					searchValueStr = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchValueStr);
					searchValueStr = valueUtil.unifyToApostrophe(searchValueStr);
					if (StringUtils.equals(EMPTY_VALUE, searchValueStr)) {
						searchValueObj = null;
					} else {
						searchValueObj = searchValueStr;
					}
					if (uriPartIndex + 5 < uriParts.length) {
						String notPart = uriParts[uriPartIndex + 5];
						if (StringUtils.equals(CRITERION_NOT, notPart)) {
							String isNotString = uriParts[uriPartIndex + 6];
							isNot = BooleanUtils.toBoolean(isNotString);
						}
					}
				} else if (StringUtils.equals(CRITERION_CLASSIFIER, searchValueType)) {
					if (uriPartIndex > uriParts.length - 7) {
						break;
					}
					String classifName = uriParts[uriPartIndex + 4];
					String classifOrigin = decode(uriParts[uriPartIndex + 5]);
					String classifCode = decode(uriParts[uriPartIndex + 6]);
					if (StringUtils.equals(EMPTY_VALUE, classifOrigin)) {
						classifOrigin = null;
					}
					Classifier classif = new Classifier();
					classif.setName(classifName);
					classif.setOrigin(classifOrigin);
					classif.setCode(classifCode);
					searchValueObj = classif;
					if (uriPartIndex + 7 < uriParts.length) {
						String notPart = uriParts[uriPartIndex + 7];
						if (StringUtils.equals(CRITERION_NOT, notPart)) {
							String isNotString = uriParts[uriPartIndex + 8];
							isNot = BooleanUtils.toBoolean(isNotString);
						}
					}
				}
				SearchCriterion criterion = new SearchCriterion();
				criterion.setSearchKey(searchKey);
				criterion.setSearchOperand(searchOperand);
				criterion.setSearchValue(searchValueObj);
				criterion.setNot(isNot);
				List<SearchCriterionGroup> criteriaGroups = detailSearchFilter.getCriteriaGroups();
				SearchCriterionGroup criterionGroup = criteriaGroups.get(criteriaGroups.size() - 1);
				criterionGroup.getSearchCriteria().add(criterion);
			}
		}
		isValid = validateSearchFilter(simpleSearchFilter, detailSearchFilter);
		Long userId = userContext.getUserId();
		List<String> userVisibleDatasets = permissionService.getUserVisibleDatasetCodes(userId);
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			selectedDatasets = new ArrayList<>(userVisibleDatasets);
		} else {
			selectedDatasets = new ArrayList<>(CollectionUtils.intersection(selectedDatasets, userVisibleDatasets));
		}
		if (detailSearchFilter == null) {
			detailSearchFilter = initSearchFilter(searchPage);
		}
		if (resultMode == null) {
			resultMode = SearchResultMode.WORD;
		}
		return new SearchUriData(isValid, searchMode, selectedDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
	}

	public SearchFilter initSearchFilter(String searchPage) {

		SearchEntity searchEntity = null;
		SearchKey searchKey = null;
		SearchOperand searchOperand = null;
		if (StringUtils.equals(LEX_SEARCH_PAGE, searchPage)) {
			searchEntity = SearchEntity.getLexEntities().get(0);
		} else if (StringUtils.equals(SYN_SEARCH_PAGE, searchPage)) {
			searchEntity = SearchEntity.getLexEntities().get(0);
		} else if (StringUtils.equals(TERM_SEARCH_PAGE, searchPage)) {
			searchEntity = SearchEntity.getTermEntities().get(0);
		} else if (StringUtils.equals(LIM_TERM_SEARCH_PAGE, searchPage)) {
			searchEntity = SearchEntity.getTermEntities().get(0);
		} else if (StringUtils.equals(SOURCE_SEARCH_PAGE, searchPage)) {
			searchEntity = SearchEntity.getSourceEntities().get(0);
		}
		searchKey = searchEntity.getKeys()[0];
		searchOperand = searchKey.getOperands()[0];
		SearchFilter detailSearch = new SearchFilter();
		SearchCriterion defaultCriterion = new SearchCriterion();
		defaultCriterion.setSearchKey(searchKey);
		defaultCriterion.setSearchOperand(searchOperand);
		SearchCriterionGroup searchGroup = new SearchCriterionGroup();
		searchGroup.setEntity(searchEntity);
		searchGroup.setSearchCriteria(asList(defaultCriterion));
		detailSearch.setCriteriaGroups(asList(searchGroup));
		return detailSearch;
	}

	public SearchFilter createSourceDetailSearchFilter(Long sourceId) {

		SearchEntity searchEntity = SearchEntity.CLUELESS;
		SearchKey searchKey = SearchKey.SOURCE_ID;
		SearchOperand searchOperand = SearchOperand.EQUALS;

		SearchFilter detailSearchFilter = new SearchFilter();
		SearchCriterion criterion = new SearchCriterion();
		criterion.setSearchKey(searchKey);
		criterion.setSearchOperand(searchOperand);
		criterion.setSearchValue(sourceId);
		SearchCriterionGroup searchGroup = new SearchCriterionGroup();
		searchGroup.setEntity(searchEntity);
		searchGroup.setSearchCriteria(asList(criterion));
		detailSearchFilter.setCriteriaGroups(asList(searchGroup));
		return detailSearchFilter;
	}

	public Long getMeaningIdSearchMeaningId(SearchFilter detailSearchFilter) {

		List<SearchCriterionGroup> criteriaGroups = detailSearchFilter.getCriteriaGroups();
		for (SearchCriterionGroup criteriaGroup : criteriaGroups) {
			SearchEntity searchEntity = criteriaGroup.getEntity();
			if (SearchEntity.MEANING.equals(searchEntity)) {
				List<SearchCriterion> searchCriteria = criteriaGroup.getSearchCriteria();
				List<SearchCriterion> filteredCriteria = searchCriteria.stream()
						.filter(c -> c.getSearchKey().equals(SearchKey.ID) && c.getSearchValue() != null)
						.collect(toList());
				if (CollectionUtils.isNotEmpty(filteredCriteria)) {
					for (SearchCriterion criterion : filteredCriteria) {
						String meaningIdStr = criterion.getSearchValue().toString();
						meaningIdStr = RegExUtils.replaceAll(meaningIdStr, "[^0-9.]", "");
						if (StringUtils.isNotEmpty(meaningIdStr)) {
							Long meaningId = Long.valueOf(meaningIdStr);
							return meaningId;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean validateSearchFilter(String simpleSearchFilter, SearchFilter detailSearchFilter) {

		if (StringUtils.isNotBlank(simpleSearchFilter)) {
			return validateSimpleSearchFilter(simpleSearchFilter);
		}
		if (detailSearchFilter != null) {
			List<SearchCriterionGroup> criteriaGroups = detailSearchFilter.getCriteriaGroups();
			return validateDetailSearchCriteriaGroups(criteriaGroups);
		}
		return false;
	}

	private boolean validateSimpleSearchFilter(String simpleSearchFilter) {

		return !StringUtils.containsOnly(simpleSearchFilter, '*');
	}

	private boolean validateDetailSearchCriteriaGroups(List<SearchCriterionGroup> criteriaGroups) {

		if (CollectionUtils.isNotEmpty(criteriaGroups)) {
			for (SearchCriterionGroup criteriaGroup : criteriaGroups) {
				for (SearchCriterion criteria : criteriaGroup.getSearchCriteria()) {
					if (criteria.getSearchValue() != null) {
						return true;
					} else if (SearchOperand.EXISTS.equals(criteria.getSearchOperand())) {
						return true;
					} else if (SearchOperand.SINGLE.equals(criteria.getSearchOperand())) {
						return true;
					} else if (SearchOperand.MULTIPLE.equals(criteria.getSearchOperand())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private String encode(String value) {
		value = StringUtils.replace(value, "/", ENCODE_SYM_SLASH);
		value = StringUtils.replace(value, "\\", ENCODE_SYM_BACKSLASH);
		value = StringUtils.replace(value, "%", ENCODE_SYM_PERCENT);
		value = UriUtils.encode(value, UTF_8);
		return value;
	}

	private String decode(String value) {
		value = UriUtils.decode(value, UTF_8);
		value = StringUtils.replace(value, ENCODE_SYM_SLASH, "/");
		value = StringUtils.replace(value, ENCODE_SYM_BACKSLASH, "\\");
		value = StringUtils.replace(value, ENCODE_SYM_PERCENT, "%");
		return value;
	}
}
