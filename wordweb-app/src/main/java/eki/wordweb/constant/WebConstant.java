package eki.wordweb.constant;

public interface WebConstant {

	String HOME_PAGE = "index";

	String SEARCH_PAGE = "search";

	String LEARN_PAGE = "learn";

	String GAMES_PAGE = "games";

	String GAME_LEXICDECIS_PAGE = "game-lexicdecis";

	String HOME_URI = "/";

	String SEARCH_URI = "/search";

	String LEARN_URI = "/learn";

	String GAMES_URI = "/games";

	String GAMES_LEXICDECIS_URI = GAMES_URI + "/lexicdecis";

	String GAMES_LEXICDECIS_GETGAMEDBATCH_URI = GAMES_LEXICDECIS_URI + "/getgamebatch";

	String GAMES_LEXICDECIS_SUBMITGAMEROW_URI = GAMES_LEXICDECIS_URI + "/submitgamerow";

	String GAMES_LEXICDECIS_FINISH_URI = GAMES_LEXICDECIS_URI + "/finish";

	String ERROR_PAGE = "error";

	String SESSION_BEAN = "sessionBean";

	String APP_DATA_MODEL_KEY = "appData";

	String VIEW_UTIL_MODEL_KEY = "viewUtil";

	String NOTHING = "";
}
