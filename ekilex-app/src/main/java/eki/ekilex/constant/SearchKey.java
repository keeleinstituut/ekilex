package eki.ekilex.constant;

public enum SearchKey {

	ID(SearchOperand.EQUALS),
	VALUE(SearchOperandSet.VALUE_OPERANDS),
	VALUE_AND_EXISTS(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	CREATED_OR_UPDATED_ON(SearchOperandSet.DATUM_OPERANDS),
	CREATED_OR_UPDATED_BY(SearchOperandSet.USER_NAME_OPERANDS),
	CREATED_ON(SearchOperandSet.DATUM_OPERANDS),
	CREATED_BY(SearchOperandSet.USER_NAME_OPERANDS),
	UPDATED_ON(SearchOperandSet.DATUM_OPERANDS),
	UPDATED_BY(SearchOperandSet.USER_NAME_OPERANDS),
	LAST_UPDATE_ON(SearchOperandSet.DATUM_OPERANDS),
	MANUAL_UPDATE_ON(SearchOperandSet.DATUM_OPERANDS),
	LANGUAGE_DIRECT(SearchOperand.EQUALS),
	LANGUAGE_INDIRECT(SearchOperand.EQUALS, SearchOperand.NOT_CONTAINS),
	WORD_TYPE(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	DISPLAY_MORPH(SearchOperand.EQUALS, SearchOperand.EXISTS),
	ASPECT(SearchOperand.EQUALS, SearchOperand.EXISTS),
	VOCAL_FORM(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	MORPHOPHONO_FORM(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	TAG_NAME(SearchOperand.EQUALS, SearchOperand.NOT_CONTAINS, SearchOperand.HAS_BEEN),
	DOMAIN(SearchOperandSet.CLASSIFIER_TREE_AND_EXISTS_OPERANDS),
	SEMANTIC_TYPE(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	SECONDARY_MEANING_WORD(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	PUBLICITY(SearchOperand.EQUALS),
	LEXEME_GRAMMAR(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	LEXEME_GOVERNMENT(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	LEXEME_POS(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	LEXEME_REGISTER(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	LEXEME_DERIV(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	LEXEME_VALUE_STATE(SearchOperand.EQUALS, SearchOperand.EXISTS),
	LEXEME_PROFICIENCY_LEVEL(SearchOperand.EQUALS),
	LEXEME_RELATION(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	WORD_RELATION(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	MEANING_RELATION(SearchOperandSet.CLASSIFIER_AND_EXISTS_OPERANDS),
	COMPLEXITY(SearchOperand.EQUALS),
	FREQUENCY(SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN),
	RANK(SearchOperand.GREATER_THAN, SearchOperand.LESS_THAN),
	WORD_ATTRIBUTE_NAME(SearchOperand.EQUALS),
	TERM_ATTRIBUTE_NAME(SearchOperand.EQUALS),
	LEXEME_ATTRIBUTE_NAME(SearchOperand.EQUALS),
	MEANING_ATTRIBUTE_NAME(SearchOperand.EQUALS),
	CONCEPT_ATTRIBUTE_NAME(SearchOperand.EQUALS),
	ATTRIBUTE_VALUE(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	WORD_FORUM(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	MEANING_FORUM(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	LEXEME_NOTE(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	MEANING_NOTE(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	DEFINITION_NOTE(SearchOperandSet.VALUE_AND_EXISTS_OPERANDS),
	COMMA_SEPARATED_IDS(SearchOperand.EQUALS),
	DATASET_USAGE(SearchOperand.EQUALS),
	SOURCE_REF(SearchOperand.EXISTS),
	SOURCE_NAME(SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS),
	SOURCE_VALUE(SearchOperand.EQUALS, SearchOperand.STARTS_WITH, SearchOperand.ENDS_WITH, SearchOperand.CONTAINS),
	SOURCE_ID(SearchOperand.EQUALS),
	;

	private SearchOperand[] operands;

	private SearchKey(SearchOperand... operands) {
		this.operands = operands;
	}

	public SearchOperand[] getOperands() {
		return operands;
	}
}
