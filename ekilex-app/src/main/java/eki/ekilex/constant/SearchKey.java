package eki.ekilex.constant;

public enum SearchKey {

	ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.NUMERIC),
	VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.CONTAINS_WORD}, SearchValueType.TEXTUAL),
	LANGUAGE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	DOMAIN(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_REF(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS, SearchOperand.NOT_EXISTS}, SearchValueType.TEXTUAL),
	SOURCE_NAME(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	CREATED_OR_UPDATED_ON(new SearchOperand[] {SearchOperand.EARLIER_THAN, SearchOperand.LATER_THAN}, SearchValueType.TEXTUAL),
	CREATED_OR_UPDATED_BY(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH}, SearchValueType.TEXTUAL)
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

}
