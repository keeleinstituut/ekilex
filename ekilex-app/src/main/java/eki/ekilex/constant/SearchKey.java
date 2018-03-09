package eki.ekilex.constant;

public enum SearchKey {

	VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD}, SearchValueType.TEXTUAL),
	LANGUAGE(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	DOMAIN(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL),
	SOURCE_CODE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	SOURCE_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL)
	;

	private SearchOperand[] operands;

	private SearchValueType valueType;

	SearchKey(SearchOperand[] operands, SearchValueType valueType) {
		this.operands = operands;
		this.valueType = valueType;
	}

	public SearchOperand[] getOperands() {
		return operands;
	}

	public SearchValueType getValueType() {
		return valueType;
	}

}
