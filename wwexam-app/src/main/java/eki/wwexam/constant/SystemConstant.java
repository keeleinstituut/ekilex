package eki.wwexam.constant;

import eki.common.constant.StatSearchConstant;

public interface SystemConstant extends StatSearchConstant {

	String DESTIN_LANG_EST = "est";

	long CACHE_EVICT_DELAY_5MIN = 5 * 60 * 1000;

	long CACHE_EVICT_DELAY_60MIN = 60 * 60 * 1000;

	String CACHE_KEY_CLASSIF = "classif";

	String DEFAULT_CLASSIF_VALUE_LANG = "est";

	String DEFAULT_CLASSIF_VALUE_TYPE = "wordweb";

	String OS_CLASSIF_VALUE_TYPE = "os";

	String WORD_SEARCH_GROUP_WORD = "word";

	String WORD_SEARCH_GROUP_AS_WORD = "as_word";

	String WORD_SEARCH_GROUP_WORD_REL = "word_rel";

	String WORD_SEARCH_GROUP_WORD_REL_COMP = "word_rel_comp";

	String WORD_SEARCH_GROUP_WORD_REL_VALUE = "word_rel_value";

	int SEARCH_WORD_MAX_LENGTH = 250;

	int MASKED_SEARCH_RESULT_LIMIT = 100;
}
