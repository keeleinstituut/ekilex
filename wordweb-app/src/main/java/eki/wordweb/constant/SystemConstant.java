package eki.wordweb.constant;

import java.math.BigDecimal;

import eki.common.constant.GlobalConstant;
import eki.common.constant.StatSearchConstant;

public interface SystemConstant extends StatSearchConstant {

	String DESTIN_LANG_EST = "est";

	String DESTIN_LANG_ENG = "eng";

	String DESTIN_LANG_DEU = "deu";

	String DESTIN_LANG_FRA = "fra";

	String DESTIN_LANG_RUS = "rus";

	String DESTIN_LANG_UKR = "ukr";

	String DESTIN_LANG_OTHER = "other";

	String[] SUPPORTED_SIMPLE_DATASETS = new String[] {DATASET_ALL, GlobalConstant.DATASET_EKI};

	String[] SUPPORTED_DESTIN_LANGS = new String[] {DESTIN_LANG_EST, DESTIN_LANG_ENG, DESTIN_LANG_DEU, DESTIN_LANG_FRA, DESTIN_LANG_RUS, DESTIN_LANG_UKR};

	String[] SUPPORTED_DETAIL_DESTIN_LANG_FILTERS = new String[] {DESTIN_LANG_ALL, DESTIN_LANG_EST, DESTIN_LANG_ENG, DESTIN_LANG_DEU, DESTIN_LANG_FRA, DESTIN_LANG_RUS, DESTIN_LANG_UKR, DESTIN_LANG_OTHER};

	String[] SUPPORTED_SIMPLE_DESTIN_LANG_FILTERS = new String[] {DESTIN_LANG_ALL, DESTIN_LANG_EST, DESTIN_LANG_ENG, DESTIN_LANG_DEU, DESTIN_LANG_FRA, DESTIN_LANG_RUS, DESTIN_LANG_UKR};

	String[] DISABLED_MEANING_RELATION_TYPE_CODES = new String[] {GlobalConstant.MEANING_REL_TYPE_CODE_SIMILAR, GlobalConstant.MEANING_REL_TYPE_CODE_NARROW, GlobalConstant.MEANING_REL_TYPE_CODE_WIDE};

	String DATASET_TYPE_LEX = "lex";

	String DATASET_TYPE_TERM = "term";

	String GAME_DIFFICULTY_SIMPLE = "easy";

	String GAME_DIFFICULTY_HARD = "hard";

	long CACHE_EVICT_DELAY_5MIN = 5 * 60 * 1000;

	long CACHE_EVICT_DELAY_60MIN = 60 * 60 * 1000;

	String CACHE_KEY_NULL_WORD = "nullword";

	String CACHE_KEY_CLASSIF = "classif";

	String CACHE_KEY_GENERIC = "generic";

	String CACHE_KEY_CORPUS = "corpus";

	Float COLLOC_MEMBER_CONTEXT_WEIGHT = 0.5F;

	BigDecimal COLLOC_MEMBER_CONTEXT_WEIGHT_TRESHOLD = new BigDecimal(0.5F);

	String DEFAULT_CLASSIF_VALUE_LANG = "est";

	String DEFAULT_CLASSIF_VALUE_TYPE = "wordweb";

	String CLASSIF_VALUE_TYPE_ISO2 = "iso2";

	String WORD_SEARCH_GROUP_WORD = "word";

	String WORD_SEARCH_GROUP_AS_WORD = "as_word";

	String WORD_SEARCH_GROUP_FORM = "form";

	String ILLEGAL_FORM_VALUE = "-";

	int SEARCH_WORD_MAX_LENGTH = 250;

	int MASKED_SEARCH_RESULT_LIMIT = 100;

	int WORD_OVERFLOW_LENGTH = 19;

	int COOKIE_AGE_ONE_MONTH = 2629743;

	String COOKIE_NAME_PREFIX = "ww-";

	String COOKIE_NAME_DESTIN_LANGS = COOKIE_NAME_PREFIX + "dl";

	String COOKIE_NAME_DATASETS = COOKIE_NAME_PREFIX + "ds";

	String COOKIE_NAME_UI_LANG = COOKIE_NAME_PREFIX + "uil";

	String COOKIE_NAME_UI_SECTIONS = COOKIE_NAME_PREFIX + "uis";

	String COOKIE_NAME_NEWS_ID = COOKIE_NAME_PREFIX + "nid";
}
