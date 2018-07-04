package eki.ekilex.service.db;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import eki.ekilex.constant.DbConstant;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchCriterion;

public abstract class AbstractSearchDbService implements SystemConstant, DbConstant {

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
}
