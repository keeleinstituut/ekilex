package eki.common.constant;

public interface PublishingConstant {

	String TARGET_IS_PUBLIC = "is_public";

	String TARGET_NAME_WW_UNIF = "ww_unif";

	String TARGET_NAME_WW_LITE = "ww_lite";

	String TARGET_NAME_WW_OS = "ww_os";

	String[] PUBLISHING_TARGET_NAMES = {TARGET_NAME_WW_UNIF, TARGET_NAME_WW_LITE, TARGET_NAME_WW_OS};

	String ENTITY_NAME_LEXEME = "lexeme";

	String ENTITY_NAME_LEXEME_NOTE = "lexeme_note";

	String ENTITY_NAME_MEANING_NOTE = "meaning_note";

	String ENTITY_NAME_MEANING_IMAGE = "meaning_image";

	String ENTITY_NAME_MEANING_MEDIA = "meaning_media";

	String ENTITY_NAME_DEFINITION = "definition";

	String ENTITY_NAME_USAGE = "usage";

	String ENTITY_NAME_GRAMMAR = "grammar";

	String ENTITY_NAME_GOVERNMENT = "government";

	String ENTITY_NAME_WORD_RELATION = "word_relation";

	String[] PUBLISHING_ENTITY_NAMES = {
			ENTITY_NAME_LEXEME,
			ENTITY_NAME_LEXEME_NOTE,
			ENTITY_NAME_MEANING_NOTE,
			ENTITY_NAME_MEANING_IMAGE,
			ENTITY_NAME_MEANING_MEDIA,
			ENTITY_NAME_DEFINITION,
			ENTITY_NAME_USAGE,
			ENTITY_NAME_GRAMMAR,
			ENTITY_NAME_GOVERNMENT,
			ENTITY_NAME_WORD_RELATION
	};
}
