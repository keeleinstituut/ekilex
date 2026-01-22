package eki.common.constant;

public enum ClassifierName {

	LABEL_TYPE(false),
	LANGUAGE(true),
	DOMAIN(true),
	GOVERNMENT_TYPE(true),
	REGISTER(true),
	GENDER(true),
	POS(true),
	MORPH(true),
	DERIV(true),
	WORD_TYPE(true),
	ETYMOLOGY_TYPE(false),
	MEANING_REL_TYPE(true),
	LEX_REL_TYPE(true),
	WORD_REL_TYPE(true),
	DISPLAY_MORPH(true),
	USAGE_TYPE(true),
	VALUE_STATE(true),
	POS_GROUP(true),
	REL_GROUP(true),
	ASPECT(true),
	DEFINITION_TYPE(true),
	REGION(false),
	SEMANTIC_TYPE(true),
	PROFICIENCY_LEVEL(true),
	VARIANT_TYPE(true),
	FREEFORM_TYPE(true)
	;

	private boolean hasLabel;

	ClassifierName(boolean hasLabel) {
		this.hasLabel = hasLabel;
	}

	public boolean hasLabel() {
		return hasLabel;
	}
}
