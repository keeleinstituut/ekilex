package eki.ekilex.constant;

import org.apache.commons.lang3.ArrayUtils;

public enum SearchKey {

	ID(new SearchOperand[] {SearchOperand.EQUALS}),
	VALUE(OperandSets.VALUE_OPERANDS),
	VALUE_AND_EXISTS(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	LANGUAGE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_CONTAINS}),
	WORD_TYPE(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS),
	ASPECT(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.EXISTS}),
	MORPHOPHONO_FORM(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	TAG_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_CONTAINS, SearchOperand.HAS_BEEN}),
	DOMAIN(OperandSets.CLASSIFIER_TREE_AND_EXISTS_OPERANDS),
	SEMANTIC_TYPE(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS),
	SOURCE_REF(new SearchOperand[] {SearchOperand.EXISTS}),
	SOURCE_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}),
	SOURCE_VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}),
	SOURCE_ID(new SearchOperand[] {SearchOperand.EQUALS}),
	CREATED_OR_UPDATED_ON(OperandSets.DATUM_OPERANDS),
	CREATED_OR_UPDATED_BY(OperandSets.USER_NAME_OPERANDS),
	CREATED_ON(OperandSets.DATUM_OPERANDS),
	CREATED_BY(OperandSets.USER_NAME_OPERANDS),
	UPDATED_ON(OperandSets.DATUM_OPERANDS),
	UPDATED_BY(OperandSets.USER_NAME_OPERANDS),
	LAST_UPDATE_ON(OperandSets.DATUM_OPERANDS),
	MANUAL_UPDATE_ON(OperandSets.DATUM_OPERANDS),
	DATASET_USAGE(new SearchOperand[] {SearchOperand.EQUALS}),
	SECONDARY_MEANING_WORD(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	PUBLICITY(new SearchOperand[] {SearchOperand.EQUALS}),
	LEXEME_GRAMMAR(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	LEXEME_GOVERNMENT(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	LEXEME_POS(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS),
	LEXEME_REGISTER(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS),
	LEXEME_VALUE_STATE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.EXISTS}),
	LEXEME_PROFICIENCY_LEVEL(new SearchOperand[] {SearchOperand.EQUALS}),
	WORD_RELATION(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS),
	MEANING_RELATION(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS),
	COMPLEXITY(new SearchOperand[] {SearchOperand.EQUALS}),
	FREQUENCY(new SearchOperand[] {SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN}),
	RANK(new SearchOperand[] {SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN}),
	ATTRIBUTE_NAME(new SearchOperand[] {SearchOperand.EQUALS}),
	ATTRIBUTE_VALUE(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	WORD_FORUM(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	MEANING_FORUM(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	LEXEME_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	MEANING_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	DEFINITION_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS),
	COMMA_SEPARATED_IDS(new SearchOperand[] {SearchOperand.EQUALS})
	;

	private SearchOperand[] operands;

	private SearchKey(SearchOperand[] operands) {
		this.operands = operands;
	}

	public SearchOperand[] getOperands() {
		return operands;
	}

	private interface OperandSets {

		SearchOperand[] VALUE_OPERANDS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD, SearchOperand.REGEX};
		SearchOperand[] VALUE_AND_EXISTS_OPERANDS = ArrayUtils.addAll(VALUE_OPERANDS, SearchOperand.EXISTS);
		SearchOperand[] CLASSIFIER_AND_EXISTS_OPERANDS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.EXISTS, SearchOperand.SINGLE, SearchOperand.MULTIPLE};
		SearchOperand[] CLASSIFIER_TREE_AND_EXISTS_OPERANDS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.SUB_CONTAINS, SearchOperand.EXISTS, SearchOperand.SINGLE, SearchOperand.MULTIPLE};
		SearchOperand[] DATUM_OPERANDS = new SearchOperand[] {SearchOperand.EARLIER_THAN, SearchOperand.LATER_THAN};
		SearchOperand[] USER_NAME_OPERANDS = new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH};
	}

}
