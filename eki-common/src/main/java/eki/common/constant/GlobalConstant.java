package eki.common.constant;

public interface GlobalConstant {

	String UTF_8 = "UTF-8";

	String DATASET_SSS = "sss";

	boolean PUBLICITY_PUBLIC = true;

	boolean PUBLICITY_PRIVATE = false;

	String VALUE_STATE_MOST_PREFERRED = "eelistatud";

	String VALUE_STATE_LEAST_PREFERRED = "väldi";

	String DEFINITION_TYPE_UNDEFINED = "määramata";

	String LEXEME_TYPE_PRIMARY = LexemeType.PRIMARY.name();

	String LEXEME_TYPE_SECONDARY = LexemeType.SECONDARY.name();

	String COMPLEXITY_DETAIL = Complexity.DETAIL.name();

	String DATASET_TYPE_TERM = DatasetType.TERM.name();

	String WORD_TYPE_CODE_PREFIXOID = "pf";

	String WORD_TYPE_CODE_SUFFIXOID = "sf";

	String WORD_TYPE_CODE_ABBREVIATION = "l";

	String[] WORD_TYPE_CODES_ABBREVIATION = new String[] {"l", "th"};

	String[] WORD_TYPE_CODES_FOREIGN = new String[] {"z", "lz"};

	String WORD_REL_TYPE_CODE_ASCPECTS = "ASPECTS";

	String WORD_REL_TYPE_CODE_DERIVATIVE_BASE = "deriv_base";

	String WORD_REL_TYPE_CODE_RAW = "raw";

	String WORD_REL_TYPE_CODE_COMP = "ühend";

	String[] PRIMARY_WORD_REL_TYPE_CODES = new String[] {WORD_REL_TYPE_CODE_COMP, WORD_REL_TYPE_CODE_RAW};

	char DISPLAY_FORM_STRESS_SYMBOL = '"';

	String[] DISPLAY_FORM_IGNORE_SYMBOLS = new String[] {"[", "]", "*"};

	String IGNORE_QUERY_LOG = "'ignore query log'";

	String FORCE_QUERY_LOG = "'force query log'";

	String LANGUAGE_CODE_EST = "est";

	String LANGUAGE_CODE_RUS = "rus";

	String LANGUAGE_CODE_ENG = "eng";

	String STAT_API_KEY_HEADER_NAME = "stat-api-key";

	String EMPTY_API_KEY = "empty-api-key";
}
