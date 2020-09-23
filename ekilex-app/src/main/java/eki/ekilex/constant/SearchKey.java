package eki.ekilex.constant;

import org.apache.commons.lang3.ArrayUtils;

public enum SearchKey {

	ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	VALUE(OperandSets.VALUE_OPERANDS, SearchValueType.TEXTUAL),
	VALUE_AND_EXISTS(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LANGUAGE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	TAG_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS, SearchOperand.HAS_BEEN}, SearchValueType.TEXTUAL),
	DOMAIN(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_REF(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	SOURCE_ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	CREATED_OR_UPDATED_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	CREATED_OR_UPDATED_BY(OperandSets.USER_NAME_OPERANDS, SearchValueType.TEXTUAL),
	CREATED_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	CREATED_BY(OperandSets.USER_NAME_OPERANDS, SearchValueType.TEXTUAL),
	UPDATED_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	UPDATED_BY(OperandSets.USER_NAME_OPERANDS, SearchValueType.TEXTUAL),
	DATASET_USAGE(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	SECONDARY_MEANING_WORD(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	PUBLICITY(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.BOOLEAN),
	LEXEME_GRAMMAR(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LEXEME_POS(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	LEXEME_FREQUENCY(new SearchOperand[] {SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN}, SearchValueType.NUMERIC),
	OD_RECOMMENDATION(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	RELATION_TYPE(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	COMPLEXITY(OperandSets.CLASSIFIER_VALUE_OPERANDS, SearchValueType.TEXTUAL)
	;

	private SearchOperand[] operands;

	private SearchValueType valueType;

	private SearchKey(SearchOperand[] operands, SearchValueType valueType) {
		this.operands = operands;
		this.valueType = valueType;
	}

	public SearchOperand[] getOperands() {
		return operands;
	}

	public SearchValueType getValueType() {
		return valueType;
	}

	private interface OperandSets {

		SearchOperand[] VALUE_OPERANDS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD};
		SearchOperand[] VALUE_AND_EXISTS_OPERANDS = ArrayUtils.addAll(VALUE_OPERANDS, SearchOperand.EXISTS, SearchOperand.NOT_EXISTS);
		SearchOperand[] CLASSIFIER_VALUE_OPERANDS = new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS};
		SearchOperand[] CLASSIFIER_VALUE_AND_EXISTS_OPERANDS = ArrayUtils.addAll(
				CLASSIFIER_VALUE_OPERANDS, SearchOperand.ZERO, SearchOperand.ONE, SearchOperand.MORE_THAN_ZERO, SearchOperand.MORE_THAN_ONE);
		SearchOperand[] DATUM_OPERANDS = new SearchOperand[] {SearchOperand.EARLIER_THAN, SearchOperand.LATER_THAN};
		SearchOperand[] USER_NAME_OPERANDS = new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH};
	}

}
