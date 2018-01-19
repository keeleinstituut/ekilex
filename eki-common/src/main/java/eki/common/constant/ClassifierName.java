package eki.common.constant;

public enum ClassifierName {

	LABEL_TYPE(false),
	LANG(true),
	DOMAIN(true),
	RECTION_TYPE(true),
	REGISTER(true),
	LEXEME_TYPE(true),
	LEXEME_FREQUENCY(false),
	GENDER(true),
	POS(true),
	MORPH(true),
	DERIV(true),
	MEANING_TYPE(false),
	MEANING_STATE(false),
	MEANING_REL_TYPE(true),
	LEX_REL_TYPE(true),
	WORD_REL_TYPE(true),
	FORM_REL_TYPE(true),
	DISPLAY_MORPH(true),
	PROCESS_STATE(false),
	USAGE_AUTHOR_TYPE(true),
	USAGE_TYPE(true)
	;

	private boolean hasLabel;

	ClassifierName(boolean hasLabel) {
		this.hasLabel = hasLabel;
	}

	public boolean hasLabel() {
		return hasLabel;
	}
}
