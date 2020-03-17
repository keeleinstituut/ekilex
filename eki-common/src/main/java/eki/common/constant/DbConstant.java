package eki.common.constant;

public interface DbConstant {

	String PROCESS_STATE_PUBLIC = "avalik";

	String PROCESS_STATE_COMPLETE = "valmis";

	String LEXEME_TYPE_PRIMARY = LexemeType.PRIMARY.name();

	String COMPLEXITY_DETAIL = Complexity.DETAIL.name();

	String DATASET_TYPE_TERM = DatasetType.TERM.name();

	String WORD_TYPE_CODE_PREFIXOID = "pf";

	String WORD_TYPE_CODE_SUFFIXOID = "sf";

	String WORD_TYPE_CODE_ABBREVIATION = "l";

	String IGNORE_QUERY_LOG = "'ignore query log'";

	String FORCE_QUERY_LOG = "'force query log'";

	String LANGUAGE_CODE_EST = "est";
}
