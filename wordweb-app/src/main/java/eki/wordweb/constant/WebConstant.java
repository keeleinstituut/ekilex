package eki.wordweb.constant;

public interface WebConstant {

	//TODO should be set by defaults and/or ui
	String DISPLAY_LANG = "est";

	String DEFAULT_SOURCE_LANG = "est";

	String DEFAULT_DESTIN_LANG = "est";

	String EMPHASISE_DESTIN_LANG = "lat";

	String[] SUPPORTED_LANGUAGES = new String[] {"est", "rus"};

	String HOME_PAGE = "index";

	String SEARCH_PAGE = "search";

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

	String SEARCH_URI = "/search";

	String LEARN_URI = "/learn";

	String REGULATIONS_URI = "/regulations";

	String ABOUT_URI = "/about";

	String CONTACTS_URI = "/contacts";

	String GAMES_URI = "/games";
	
	String CONDITIONS_URI = "/conditions";

	String COLLECTIONS_URI = "/collections";

	String FILES_URI = "/files";

	String GAMES_LEXICDECIS_URI = GAMES_URI + "/lexicdecis";

	String GAMES_SIMILJUDGE_URI = GAMES_URI + "/similjudge";

	String GAMES_GETGAMEBATCH_URI = "/getgamebatch";

	String GAMES_SUBMITGAMEROW_URI = "/submitgamerow";

	String GAMES_FINISH_URI = "/finish";

	String GAMES_LEXICDECIS_NAME = "lexicdecis";

	String GAMES_SIMILJUDGE_NAME = "similjudge";

	String SESSION_BEAN = "sessionBean";

	String APP_DATA_MODEL_KEY = "appData";

	String VIEW_UTIL_MODEL_KEY = "viewUtil";

	String IE_USER_FLAG_KEY = "ieuser";

	String REQUEST_START_TIME_KEY = "request_start_time";

	String NOTHING = "";

	int TYPICAL_COLLECTIONS_DISPLAY_LIMIT = 3;

	int WORD_RELATIONS_DISPLAY_LIMIT = 10;

	String ALTERNATIVE_FORMS_SEPARATOR = " ~ ";
}
