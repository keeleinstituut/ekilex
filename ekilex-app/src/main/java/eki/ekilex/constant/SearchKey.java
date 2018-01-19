package eki.ekilex.constant;

public enum SearchKey {

	WORD_VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	FORM_VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	DEFINITION_VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	USAGE_VALUE(new SearchOperand[] {SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS}, SearchValueType.TEXTUAL),
	CONCEPT_ID(new SearchOperand[] {SearchOperand.EQUALS}, SearchValueType.TEXTUAL)
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
