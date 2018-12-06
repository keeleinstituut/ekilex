package eki.wordweb.constant;

public interface SystemConstant {

	String UTF_8 = "UTF-8";

	String POM_PATH = "eki.wordweb/wordweb";

	String SEARCH_MODE_SIMPLE = "simple";

	String SEARCH_MODE_DETAIL = "detail";

	String GAME_DIFFICULTY_SIMPLE = "easy";

	String GAME_DIFFICULTY_HARD = "hard";

	long CACHE_EVICT_DELAY_5MIN = 5 * 60 * 1000;

	long CACHE_EVICT_DELAY_60MIN = 60 * 60 * 1000;

	String CACHE_KEY_CLASSIF = "classif";

	String CACHE_KEY_DATASET = "dataset";

	String CACHE_KEY_CORPORA = "corpora";
}
