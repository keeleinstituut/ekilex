package eki.common.constant;

public interface GlobalConstant {

	String UTF_8 = "UTF-8";

	String LANGUAGE_CODE_EST = "est";

	String LANGUAGE_CODE_RUS = "rus";

	String LANGUAGE_CODE_ENG = "eng";

	String LANGUAGE_CODE_DEU = "deu";

	String LANGUAGE_CODE_LAT = "lat";

	String LANGUAGE_CODE_FRA = "fra";

	String LANGUAGE_CODE_UKR = "ukr";

	String lANGUAGE_CODE_MUL = "mul";

	String DATASET_EKI = "eki";

	String DATASET_ETY = "ety";

	String DATASET_ESTERM = "esterm";

	String DATASET_LIMITED = "vrk";

	String DATASET_TEST = "kce";

	String DATASET_NA = "-";

	boolean PUBLICITY_PUBLIC = true;

	boolean PUBLICITY_PRIVATE = false;

	boolean MANUAL_EVENT_ON_UPDATE_DISABLED = false;

	boolean MANUAL_EVENT_ON_UPDATE_ENABLED = true;

	String VALUE_STATE_CODE_MOST_PREFERRED = "eelistatud";

	String VALUE_STATE_CODE_LEAST_PREFERRED = "väldi";

	String VALUE_STATE_CODE_INCORRECT = "vigane";

	String VALUE_STATE_CODE_FORMER = "endine";

	String DEFINITION_TYPE_CODE_UNDEFINED = "määramata";

	String DEFINITION_TYPE_CODE_INEXACT_SYN = "kitsam/laiem tähendus teises keeles";

	String MORPH_CODE_UNKNOWN = "??";

	String WORD_TYPE_CODE_PREFIXOID = "pf";

	String WORD_TYPE_CODE_SUFFIXOID = "sf";

	String WORD_TYPE_CODE_ABBREVIATION = "l";

	String WORD_TYPE_CODE_SYMBOL = "th";

	String WORD_TYPE_CODE_INCORRECT_WORD_FORM = "viga";

	String[] WORD_TYPE_CODES_ABBREVIATION = new String[] {"l", "th"};

	String[] WORD_TYPE_CODES_FOREIGN = new String[] {"z", "lz"};

	String LEXEME_POS_CODE_VERB = "v";

	String[] GOVERNMENT_VALUES_MULTIPLE_CASE = new String[] {"keda", "mida", "keda/mida", "mida/keda"};

	String[] GOVERNMENT_VALUES_PARTITIVE_CASE = new String[] {"keda*", "mida*", "keda/mida*", "mida/keda*"};

	String WORD_REL_TYPE_CODE_ASCPECTS = "ASPECTS";

	String WORD_REL_TYPE_CODE_DERIVATIVE_BASE = "deriv_base";

	String WORD_REL_TYPE_CODE_RAW = "raw";

	String WORD_REL_TYPE_CODE_COMP = "ühend";

	String WORD_REL_TYPE_CODE_HEAD = "head";

	String WORD_REL_TYPE_CODE_PRECOMP = "ls-esiosa";

	String WORD_REL_TYPE_CODE_WPRECOMP = "ls-esiosaga";

	String WORD_REL_TYPE_CODE_POSTCOMP = "ls-järelosa";

	String WORD_REL_TYPE_CODE_WPOSTCOMP = "ls-järelosaga";

	String[] PRIMARY_WORD_REL_TYPE_CODES = new String[] {
			WORD_REL_TYPE_CODE_COMP,
			WORD_REL_TYPE_CODE_RAW,
			WORD_REL_TYPE_CODE_WPRECOMP,
			WORD_REL_TYPE_CODE_WPOSTCOMP
	};

	String MEANING_REL_TYPE_CODE_SIMILAR = "sarnane";

	String MEANING_REL_TYPE_CODE_NARROW = "kitsam";

	String MEANING_REL_TYPE_CODE_WIDE = "laiem";

	String WORD_RELATION_PARAM_NAME_SYN_CANDIDATE = "syn candidate";

	String COMPLEXITY_DETAIL = Complexity.DETAIL.name();

	String COMPLEXITY_ANY = Complexity.ANY.name();

	String RELATION_STATUS_UNDEFINED = RelationStatus.UNDEFINED.name();

	char DISPLAY_FORM_STRESS_SYMBOL = '"';

	String[] DISPLAY_FORM_IGNORE_SYMBOLS = new String[] {"[", "]", "*"};

	String FUNCT_NAME_APPROVE_MEANING = "approveMeaning";

	String IGNORE_QUERY_LOG = "'ignore query log'";

	String FORCE_QUERY_LOG = "'force query log'";

	String SEARCH_MASK_CHARS = "*";

	String SEARCH_MASK_CHAR = "?";

	String STAT_API_KEY_HEADER_NAME = "stat-api-key";

	String EMPTY_API_KEY = "empty-api-key";

	String ENCODE_SYM_SLASH = "$2F";

	String ENCODE_SYM_PERCENT = "$25";

	String ENCODE_SYM_BACKSLASH = "$5C";

	String ENCODE_SYM_QUOTATION = "U+0022";
}
