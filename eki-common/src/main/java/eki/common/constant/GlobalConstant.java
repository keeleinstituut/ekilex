package eki.common.constant;

public interface GlobalConstant {

	String UTF_8 = "UTF-8";

	String DATASET_SSS = "sss";

	boolean PUBLICITY_PUBLIC = true;

	boolean PUBLICITY_PRIVATE = false;

	String VALUE_STATE_MOST_PREFERRED = "eelistermin";

	String VALUE_STATE_LEAST_PREFERRED = "väldi";

	String DEFINITION_TYPE_UNDEFINED = "määramata";

	String LEXEME_TYPE_PRIMARY = LexemeType.PRIMARY.name();

	String COMPLEXITY_DETAIL = Complexity.DETAIL.name();

	String DATASET_TYPE_TERM = DatasetType.TERM.name();

	String WORD_TYPE_CODE_PREFIXOID = "pf";

	String WORD_TYPE_CODE_SUFFIXOID = "sf";

	String WORD_TYPE_CODE_ABBREVIATION = "l";

	String[] WORD_TYPE_CODES_ABBREVIATION = new String[] {"l", "th"};

	String[] WORD_TYPE_CODES_FOREIGN = new String[] {"z", "lz"};

	char DISPLAY_FORM_STRESS_SYMBOL = '"';

	String[] DISPLAY_FORM_IGNORE_SYMBOLS = new String[] {"[", "]", "*"};

	String IGNORE_QUERY_LOG = "'ignore query log'";

	String FORCE_QUERY_LOG = "'force query log'";

	String LANGUAGE_CODE_EST = "est";

	String LANGUAGE_CODE_RUS = "rus";
}
