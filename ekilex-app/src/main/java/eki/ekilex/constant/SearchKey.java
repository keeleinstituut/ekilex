package eki.ekilex.constant;

public enum SearchKey {

	ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	VALUE(SearchOperandConstant.VALUE_OPERANDS_WITHOUT_EXISTS, SearchValueType.TEXTUAL),
	LANGUAGE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	TAG_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS, SearchOperand.HAS_BEEN}, SearchValueType.TEXTUAL),
	DOMAIN(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_REF(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	SOURCE_ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	CREATED_OR_UPDATED_ON(new SearchOperand[] {SearchOperand.EARLIER_THAN, SearchOperand.LATER_THAN}, SearchValueType.TEXTUAL),
	CREATED_OR_UPDATED_BY(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH}, SearchValueType.TEXTUAL),
	CREATED_ON(new SearchOperand[] {SearchOperand.EARLIER_THAN, SearchOperand.LATER_THAN}, SearchValueType.TEXTUAL),
	CREATED_BY(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH}, SearchValueType.TEXTUAL),
	DATASET_USAGE(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	SECONDARY_MEANING_WORD(SearchOperandConstant.VALUE_OPERANDS, SearchValueType.TEXTUAL),
	PUBLICITY(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.BOOLEAN),
	LEXEME_GRAMMAR(SearchOperandConstant.VALUE_OPERANDS_WITHOUT_EXISTS, SearchValueType.TEXTUAL),
	LEXEME_POS(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	LEXEME_FREQUENCY(new SearchOperand[] {SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN}, SearchValueType.NUMERIC),
	OD_RECOMMENDATION(SearchOperandConstant.VALUE_OPERANDS_WITHOUT_EXISTS, SearchValueType.TEXTUAL),
	RELATION_TYPE(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL)
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

	private interface SearchOperandConstant {

		SearchOperand[] VALUE_OPERANDS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD,
				SearchOperand.EXISTS, SearchOperand.NOT_EXISTS};

		// temp - yogesh
		SearchOperand[] VALUE_OPERANDS_WITHOUT_EXISTS = new SearchOperand[] {
				SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD};
	}

}
