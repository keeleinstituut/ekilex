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
	ENTRY_CLASS(false),
	MEANING_TYPE(false),
	MEANING_STATE(false),
	LEX_REL_TYPE(true),
	FORM_REL_TYPE(true),
	WORD_REL_TYPE(true);

	private boolean hasLabel;

	private ClassifierName(boolean hasLabel) {
		this.hasLabel = hasLabel;
	}

	public boolean hasLabel() {
		return hasLabel;
	}
}
