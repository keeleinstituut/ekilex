package eki.common.constant;

public enum LifecycleEntity {

	WORD,
	LEXEME,
	MEANING,
	USAGE,
	USAGE_TRANSLATION,
	USAGE_DEFINITION,
	GOVERNMENT,
	GRAMMAR,
	DEFINITION,
	LEXEME_RELATION,
	MEANING_RELATION,
	WORD_RELATION,
	WORD_ETYMOLOGY,
	@Deprecated
	LEXEME_SOURCE_LINK,//FIXME should use LifecycleProperty.SOURCE_LINK instead
	@Deprecated
	DEFINITION_SOURCE_LINK,//FIXME should use LifecycleProperty.SOURCE_LINK instead
	@Deprecated
	FREEFORM_SOURCE_LINK,//FIXME should use LifecycleProperty.SOURCE_LINK instead
	WORD_RELATION_GROUP_MEMBER,
	LEARNER_COMMENT,
	@Deprecated
	LEXEME_PUBLIC_NOTE,//FIXME should use LifecycleProperty.PUBLIC_NOTE instead
	@Deprecated
	MEANING_PUBLIC_NOTE,//FIXME should use LifecycleProperty.PUBLIC_NOTE instead
	@Deprecated
	DEFINITION_PUBLIC_NOTE,//FIXME should use LifecycleProperty.PUBLIC_NOTE instead
	SOURCE,
	ATTRIBUTE_FREEFORM
}
