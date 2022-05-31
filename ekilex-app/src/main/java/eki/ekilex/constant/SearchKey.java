package eki.ekilex.constant;

import org.apache.commons.lang3.ArrayUtils;

public enum SearchKey {

	ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	VALUE(OperandSets.VALUE_OPERANDS, SearchValueType.TEXTUAL),
	VALUE_AND_EXISTS(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LANGUAGE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_CONTAINS}, SearchValueType.TEXTUAL),
	WORD_TYPE(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	ASPECT(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.EXISTS}, SearchValueType.TEXTUAL),
	MORPHOPHONO_FORM(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	TAG_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_CONTAINS, SearchOperand.HAS_BEEN}, SearchValueType.TEXTUAL),
	DOMAIN(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	SEMANTIC_TYPE(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	SOURCE_REF(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	SOURCE_ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	CREATED_OR_UPDATED_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	CREATED_OR_UPDATED_BY(OperandSets.USER_NAME_OPERANDS, SearchValueType.TEXTUAL),
	CREATED_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	CREATED_BY(OperandSets.USER_NAME_OPERANDS, SearchValueType.TEXTUAL),
	UPDATED_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	UPDATED_BY(OperandSets.USER_NAME_OPERANDS, SearchValueType.TEXTUAL),
	LAST_UPDATE_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	MANUAL_UPDATE_ON(OperandSets.DATUM_OPERANDS, SearchValueType.TEXTUAL),
	DATASET_USAGE(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	SECONDARY_MEANING_WORD(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	PUBLICITY(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.BOOLEAN),
	LEXEME_GRAMMAR(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LEXEME_GOVERNMENT(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LEXEME_POS(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LEXEME_REGISTER(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LEXEME_VALUE_STATE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.EXISTS}, SearchValueType.TEXTUAL),
	LEXEME_PROFICIENCY_LEVEL(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	WORD_RELATION(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	MEANING_RELATION(OperandSets.CLASSIFIER_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	COMPLEXITY(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	FREQUENCY(new SearchOperand[] {SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN}, SearchValueType.NUMERIC),
	RANK(new SearchOperand[] {SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN}, SearchValueType.NUMERIC),
	ATTRIBUTE_NAME(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	ATTRIBUTE_VALUE(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	WORD_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	LEXEME_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	MEANING_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL),
	DEFINITION_NOTE(OperandSets.VALUE_AND_EXISTS_OPERANDS, SearchValueType.TEXTUAL)
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
				SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD, SearchOperand.REGEX};
		SearchOperand[] VALUE_AND_EXISTS_OPERANDS = ArrayUtils.addAll(VALUE_OPERANDS, SearchOperand.EXISTS);
		SearchOperand[] CLASSIFIER_AND_EXISTS_OPERANDS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.EXISTS, SearchOperand.SINGLE, SearchOperand.MULTIPLE};
		SearchOperand[] DATUM_OPERANDS = new SearchOperand[] {SearchOperand.EARLIER_THAN, SearchOperand.LATER_THAN};
		SearchOperand[] USER_NAME_OPERANDS = new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH};
	}

}
