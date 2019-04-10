package eki.ekilex.constant;

public interface WebConstant {

	String LOGIN_URI = "/dologin";

	String LOGOUT_URI = "/dologout";

	String LOGIN_PAGE_URI = "/login";

	String LOGIN_ERROR_URI = "/loginerror";

	String LOGIN_PAGE = "login";

	String HOME_PAGE = "home";

	String HOME_URI = "/";

	String APPLY_PAGE = "apply";

	String APPLY_URI = "/apply";

	String PERMISSIONS_PAGE = "permissions";

	String PERMISSIONS_URI = "/permissions";

	String COMPONENT_URI = "/comp";

	String REST_SERVICES_URI = "/data";

	String VIEW_RESOURCES_URI = "/view";

	String LEX_SEARCH_PAGE = "lexsearch";

	String LEX_SEARCH_URI = "/lexsearch";

	String TERM_SEARCH_PAGE = "termsearch";

	String TERM_SEARCH_URI = "/termsearch";

	String WORD_DETAILS_URI = "/worddetails";

	String MEANING_DETAILS_URI = "/meaningdetails";

	String CLASSIFIERS_URI = "/classifiers";

	String DATASETS_URI = "/datasets";

	String WORD_SELECT_URI = "/wordselect";

	String WORD_SELECT_PAGE = "wordselect";

	String AUTH_ERROR_URI = "/autherror";

	String SEND_FEEDBACK_URI = "/send_feedback";

	String WW_FEEDBACK_URI = "/ww_feedback";

	String WW_FEEDBACK_PAGE = "ww_feedback";

	String LEX_JOIN_PAGE = "lexjoin";

	String MEANING_JOIN_URI = "/meaningjoin";

	String MEANING_JOIN_PAGE = "meaningjoin";

	String REGISTER_PAGE_URI = "/register";

	String REGISTER_PAGE = "register";

	String ACTIVATE_PAGE_URI = "/activate";

	String COMPONENTS_PAGE = "components";

	String COMMON_PAGE = "common";

	String LEXDIALOG_PAGE = "lexdialog";

	String TERMDIALOG_PAGE = "termdialog";

	String LIFECYCLELOGVIEW_PAGE = "lifecyclelogview";

	String SOURCEVIEW_PAGE = "sourceview";

	String ERROR_PAGE = "error";

	String PAGE_FRAGMENT_ELEM = " :: ";

	// mode flags

	String SEARCH_MODE_SIMPLE = "SIMPLE";

	String SEARCH_MODE_DETAIL = "DETAIL";

	String RETURN_PAGE_LEX_SEARCH = "lex_search";

	String RETURN_PAGE_TERM_SEARCH = "term_search";

	// model keys

	String SESSION_BEAN = "sessionBean";

	String APP_DATA_MODEL_KEY = "appData";

	String PERM_DATA_UTIL_KEY = "permDataUtil";

	String USER_KEY = "user";

	String SEARCH_WORD_KEY = "searchWord";

	String REQUEST_START_TIME_KEY = "request_start_time";
}
