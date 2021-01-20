package eki.wordweb.constant;

public interface WebConstant {

	//TODO should be set by defaults and/or ui
	String DISPLAY_LANG = "est";

	String UNIF_HOME_PAGE = "unif-home";

	String UNIF_SEARCH_PAGE = "unif-search";

	String LITE_HOME_PAGE = "simple-home";

	String LITE_SEARCH_PAGE = "simple-search";

	String MORPHO_SHORT_PAGE = "morpho-short";

	String MORPHO_FULL_PAGE = "morpho-full";

	String LEARN_PAGE = "learn";

	String REGULATIONS_PAGE = "regulations";

	String GAMES_PAGE = "games";

	String CONDITIONS_PAGE = "conditions";

	String COLLECTIONS_PAGE = "collections";

	String ABOUT_PAGE = "about";
	
	String CONTACTS_PAGE = "contacts";

	String GAME_LEXICDECIS_PAGE = "game-lexicdecis";

	String GAME_SIMILJUDGE_PAGE = "game-similjudge";

	String ERROR_PAGE = "error";

	String HOME_URI = "/";

	@Deprecated
	String LEX_URI = "/lex";

	@Deprecated
	String TERM_URI = "/term";

	String UNIF_URI = "/unif";

	String LITE_URI = "/lite";

	String SEARCH_URI = "/search";

	String SEARCH_WORD_FRAG_URI = "/searchwordfrag";

	String WORD_DETAILS_URI = "/worddetails";

	String MORPHO_URI = "/morpho";

	String LEARN_URI = "/learn";

	String REGULATIONS_URI = "/regulations";

	String ABOUT_URI = "/about";

	String CONTACTS_URI = "/contacts";

	String GAMES_URI = "/games";
	
	String CONDITIONS_URI = "/conditions";

	String COLLECTIONS_URI = "/collections";

	String CORP_URI = "/corp";

	String FILES_URI = "/files";

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

	String NOTHING = "";

	int TYPICAL_COLLECTIONS_DISPLAY_LIMIT = 3;

	int MEANING_WORDS_DISPLAY_LIMIT = 5;

	int WORD_RELATIONS_DISPLAY_LIMIT = 5;

	Integer DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL = 3;

	Integer SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL = 2;

	String ALTERNATIVE_FORMS_SEPARATOR = " ~ ";

	char UI_FILTER_VALUES_SEPARATOR = ',';

	int AUTOCOMPLETE_MAX_RESULTS_LIMIT = 15;

	String GENERIC_EKI_MARKUP_PREFIX = "<eki-";

	int DEFINITION_OVERSIZE_LIMIT = 1000;

	int FORM_FREQ_SCALE = 5;
}
