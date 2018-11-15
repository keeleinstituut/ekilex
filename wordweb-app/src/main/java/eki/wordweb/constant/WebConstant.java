package eki.wordweb.constant;

public interface WebConstant {

	//TODO should be set by defaults and/or ui
	String DISPLAY_LANG = "est";

	String DEFAULT_SOURCE_LANG = "est";

	String DEFAULT_DESTIN_LANG = "est";

	String[] SUPPORTED_LANGUAGES = new String[] {"est", "rus"};

	String HOME_PAGE = "index";

	String SEARCH_PAGE = "search";

	String LEARN_PAGE = "learn";

	String GAMES_PAGE = "games";
	
	String ABOUT_PAGE = "about";
	
	String CONTACTS_PAGE = "contacts";

	String GAME_LEXICDECIS_PAGE = "game-lexicdecis";

	String GAME_SIMILJUDGE_PAGE = "game-similjudge";

	String HOME_URI = "/";

	String SEARCH_URI = "/search";

	String LEARN_URI = "/learn";

	String ABOUT_URI = "/about";
	
	String CONTACTS_URI = "/contacts";

	String GAMES_URI = "/games";

	String GAMES_LEXICDECIS_URI = GAMES_URI + "/lexicdecis";

	String GAMES_SIMILJUDGE_URI = GAMES_URI + "/similjudge";

	String GAMES_GETGAMEBATCH_URI = "/getgamebatch";

	String GAMES_SUBMITGAMEROW_URI = "/submitgamerow";

	String GAMES_FINISH_URI = "/finish";

	String ERROR_PAGE = "error";

	String SESSION_BEAN = "sessionBean";

	String APP_DATA_MODEL_KEY = "appData";

	String VIEW_UTIL_MODEL_KEY = "viewUtil";

	String NOTHING = "";
}
