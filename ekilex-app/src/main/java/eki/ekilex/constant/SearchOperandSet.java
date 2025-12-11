package eki.ekilex.constant;

interface SearchOperandSet {

	SearchOperand[] VALUE_OPERANDS = new SearchOperand[] {
			SearchOperand.EQUALS,
			SearchOperand.STARTS_WITH,
			SearchOperand.ENDS_WITH,
			SearchOperand.CONTAINS,
			SearchOperand.CONTAINS_WORD,
			SearchOperand.REGEX};

	SearchOperand[] VALUE_AND_EXISTS_OPERANDS = new SearchOperand[] {
			SearchOperand.EQUALS,
			SearchOperand.STARTS_WITH,
			SearchOperand.ENDS_WITH,
			SearchOperand.CONTAINS,
			SearchOperand.CONTAINS_WORD,
			SearchOperand.REGEX,
			SearchOperand.EXISTS};

	SearchOperand[] CLASSIFIER_AND_EXISTS_OPERANDS = new SearchOperand[] {
			SearchOperand.EQUALS,
			SearchOperand.EXISTS,
			SearchOperand.SINGLE,
			SearchOperand.MULTIPLE};

	SearchOperand[] CLASSIFIER_TREE_AND_EXISTS_OPERANDS = new SearchOperand[] {
			SearchOperand.EQUALS,
			SearchOperand.SUB_CONTAINS,
			SearchOperand.EXISTS,
			SearchOperand.SINGLE,
			SearchOperand.MULTIPLE};

	SearchOperand[] DATUM_OPERANDS = new SearchOperand[] {
			SearchOperand.EARLIER_THAN,
			SearchOperand.LATER_THAN};

	SearchOperand[] USER_NAME_OPERANDS = new SearchOperand[] {
			SearchOperand.EQUALS,
			SearchOperand.STARTS_WITH,
			SearchOperand.ENDS_WITH};

	SearchOperand[] PUBLISHING_OPERANDS = new SearchOperand[] {
			SearchOperand.EQUALS,
			SearchOperand.NOT_CONTAINS,
			SearchOperand.EXISTS};
}