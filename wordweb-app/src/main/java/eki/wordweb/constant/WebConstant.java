package eki.wordweb.constant;

public interface WebConstant {

	String REDIRECT_PREF = "redirect:";

	String DATASET_HOME_PAGE = "dataset-home";

	String UNIF_HOME_PAGE = "unif-home";

	String UNIF_SEARCH_PAGE = "unif-search";

	String UNIF_WORDS_PAGE = "unif-words";

	String LITE_HOME_PAGE = "simple-home";

	String LITE_SEARCH_PAGE = "simple-search";

	String LITE_WORDS_PAGE = "simple-words";

	String MORPHO_SHORT_PAGE = "morpho-short";

	String MORPHO_FULL_PAGE = "morpho-full";

	String OD_HOME_PAGE = "od-home";

	String OD_SEARCH_PAGE = "od-search";

	String COMMON_SEARCH_SIDEBAR_PAGE = "common-search-sidebar";

	String NEW_WORDS_PAGE = "new-words";

	String NEWS_PAGE = "news";

	String EKILEX_API_PAGE = "ekilex-api";

	String LEARN_PAGE = "learn";

	String WORDGAME_PAGE = "wordgame";

	String REGULATIONS_PAGE = "regulations";

	String GAMES_PAGE = "games";

	String COLLECTIONS_PAGE = "collections";

	String ABOUT_PAGE = "about";

	String CONTACT_PAGE = "contact";

	String GAME_LEXICDECIS_PAGE = "game-lexicdecis";

	String GAME_SIMILJUDGE_PAGE = "game-similjudge";

	String ERROR_PAGE = "error";

	String PAGE_FRAGMENT_ELEM = " :: ";

	String HOME_URI = "/";

	String DATASET_HOME_URI = "/ds";

	String UNIF_URI = "/unif";

	String LITE_URI = "/lite";

	String OD_URI = "/od";

	String SEARCH_URI = "/search";

	String SEARCH_WORD_FRAG_URI = "/searchwordfrag";

	String SEARCH_LINK_URI = "/searchlink";

	String WORD_DETAILS_URI = "/worddetails";

	String FEELING_LUCKY_URI = "/feelinglucky";

	String NEW_WORDS_URI = "/newwords";

	String NEWS_URI = "/news";

	String NEWS_ACCEPT_URI = "/newsaccept";

	String EKILEX_API_URI = "/ekilex-api";

	String MORPHO_URI = "/morpho";

	String LEARN_URI = "/learn";

	String WORDGAME_URI = "/wordgame";

	String REGULATIONS_URI = "/regulations";

	String ABOUT_URI = "/about";

	String CONTACT_URI = "/contact";

	String GAMES_URI = "/games";

	String COLLECTIONS_URI = "/collections";

	String CORP_URI = "/corp";

	String CORP_TRANS_URI = "/corptrans";

	String FILES_URI = "/files";

	String AUDIO_LINK_URI = "/audio-link";

	String USER_PREF_URI = "/user-pref";

	String PROTO_URI = "/proto";

	String GAMES_LEXICDECIS_URI = GAMES_URI + "/lexicdecis";

	String GAMES_SIMILJUDGE_URI = GAMES_URI + "/similjudge";

	String GAMES_GETGAMEBATCH_URI = "/getgamebatch";

	String GAMES_SUBMITGAMEROW_URI = "/submitgamerow";

	String GAMES_FINISH_URI = "/finish";

	String GAMES_LEXICDECIS_NAME = "lexicdecis";

	String GAMES_SIMILJUDGE_NAME = "similjudge";

	String SESSION_BEAN = "sessionBean";

	String SEARCH_FORM = "searchForm";

	String APP_DATA_MODEL_KEY = "appData";

	String VIEW_UTIL_MODEL_KEY = "viewUtil";

	String IE_USER_FLAG_KEY = "ieuser";

	String REQUEST_START_TIME_KEY = "request_start_time";

	String LANGUAGE_MUL_IMAGE_PATH = "/language_mul.svg";

	String NOTHING = "";

	int TYPICAL_COLLECTIONS_DISPLAY_LIMIT = 3;

	int ALT_WORDS_DISPLAY_LIMIT = 5;

	int SYN_WORDS_DISPLAY_LIMIT = 5;

	int WORD_RELATIONS_DISPLAY_LIMIT = 5;

	int CORPUS_SENTENCES_DISPLAY_LIMIT = 3;

	Integer DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL = 3;

	Integer SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL = 2;

	String ALTERNATIVE_FORMS_SEPARATOR = " ~ ";

	char UI_FILTER_VALUES_SEPARATOR = ',';

	char COOKIE_VALUES_SEPARATOR = '|';

	int AUTOCOMPLETE_MAX_RESULTS_LIMIT = 15;

	String GENERIC_EKI_MARKUP_OPENING_PREFIX = "<eki-";

	String GENERIC_EKI_MARKUP_CLOSING_PREFIX = "</eki-";

	int DEFINITION_OVERSIZE_LIMIT = 500;

	int NOTE_OVERSIZE_LIMIT = 500;

	int FORM_FREQ_SCALE = 5;
}
