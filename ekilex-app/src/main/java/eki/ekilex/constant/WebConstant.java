package eki.ekilex.constant;

public interface WebConstant {

	String INDEX_URI = "/";

	String LOGIN_URI = "/dologin";

	String LOGOUT_URI = "/dologout";

	String LOGIN_PAGE_URI = "/login";

	String LOGIN_ERROR_URI = "/loginerror";

	String LOGIN_PAGE = "login";

	String HOME_PAGE = "home";

	String HOME_URI = "/home";

	String APPLY_PAGE = "apply";

	String APPLY_URI = "/apply";

	String REAPPLY_URI = "/reapply";

	String PERMISSIONS_PAGE = "permissions";

	String PERMISSIONS_URI = "/permissions";

	String COMPONENT_URI = "/comp";

	String REST_SERVICES_URI = "/data";

	String VIEW_RESOURCES_URI = "/view";

	String LEX_SEARCH_PAGE = "lexsearch";

	String LEX_SEARCH_URI = "/lexsearch";

	String LEX_PAGING_URI = "/lex_paging";

	String LEX_COMPONENTS_PAGE = "lexcomp";

	String TERM_SEARCH_PAGE = "termsearch";

	String TERM_SEARCH_URI = "/termsearch";

	String TERM_PAGING_URI = "/term_paging";

	String TERM_COMPONENTS_PAGE = "termcomp";

	String WORD_DETAILS_URI = "/worddetails";

	String MEANING_DETAILS_URI = "/meaningdetails";

	String CLASSIFIERS_URI = "/classifiers";

	String WORD_SELECT_URI = "/wordselect";

	String WORD_SELECT_PAGE = "wordselect";

	String AUTH_ERROR_URI = "/autherror";

	String SEND_FEEDBACK_URI = "/send_feedback";

	String WW_FEEDBACK_URI = "/wwfeedback";

	String WW_FEEDBACK_PAGE = "wwfeedback";

	String MORPHOLOGY_URI = "/morphology";

	String MORPHOLOGY_PAGE = "morphology";

	String LEX_JOIN_URI = "/lexjoin";

	String LEX_JOIN_PAGE = "lexjoin";

	String VALIDATE_LEX_JOIN_URI = "/validatelexjoin";

	String MEANING_JOIN_URI = "/meaningjoin";

	String MEANING_JOIN_PAGE = "meaningjoin";

	String VALIDATE_MEANING_JOIN_URI = "/validatemeaningjoin";

	String WORD_JOIN_URI = "/wordjoin";

	String WORD_JOIN_PAGE = "wordjoin";

	String FAKE_REGISTER_AND_PASSWORD_RECOVERY_URI = "/submit";

	String REGISTER_PAGE_URI = "/register";

	String REGISTER_PAGE = "register";

	String ACTIVATE_PAGE_URI = "/activate";

	String PASSWORD_RECOVERY_URI = "/passwordrecovery";

	String PASSWORD_RECOVERY_PAGE = "passwordrecovery";

	String PASSWORD_SET_PAGE_URI = "/passwordset";

	String PASSWORD_SET_PAGE = "passwordset";

	String COMPONENTS_PAGE = "components";

	String COMMON_PAGE = "common";

	String LEXDIALOG_PAGE = "lexdialog";

	String TERMDIALOG_PAGE = "termdialog";

	String LIFECYCLELOGVIEW_PAGE = "lifecyclelogview";

	String PROCESS_LOG_VIEW_PAGE = "processlogview";

	String SOURCEVIEW_PAGE = "sourceview";

	String ERROR_PAGE = "error";

	String CREATE_WORD_URI = "/create_word";

	String CREATE_HOMONYM_URI = "/create_homonym";

	String CREATE_RELATIONS_URI = "/create_relations";

	String OPPOSITE_RELATIONS_URI = "/oppositerelations";

	String CREATE_ITEM_URI = "/create_item";

	String UPDATE_ITEM_URI = "/update_item";

	String UPDATE_ORDERING_URI = "/update_ordering";

	String UPDATE_LEVELS_URI = "/update_levels";

	String UPDATE_WORD_VALUE_URI = "/update_word_value";

	String DELETE_ITEM_URI = "/delete_item";

	String CONFIRM_OP_URI = "/confirm_op";

	String SOURCE_COMPONENTS_PAGE = "sourcecomp";

	String SOURCE_SEARCH_URI = "/sourcesearch";

	String SOURCE_SEARCH_PAGE = "sourcesearch";

	String SOURCE_SEARCH_RESULT = "source_search_result";

	String UPDATE_SOURCE_PROPERTY_URI = "/update_source_property";

	String CREATE_SOURCE_PROPERTY_URI = "/create_source_property";

	String DELETE_SOURCE_PROPERTY_URI = "/delete_source_property";

	String UPDATE_SOURCE_TYPE_URI = "/update_source_type";

	String CREATE_SOURCE_URI = "/create_source";

	String VALIDATE_DELETE_SOURCE_URI = "/validate_delete_source";

	String DELETE_SOURCE_URI = "/delete_source";

	String SOURCE_JOIN_URI = "/source_join";

	String SOURCE_JOIN_PAGE = "sourcejoin";

	String JOIN_SOURCES_URI = "/join_sources";

	String SEARCH_SOURCES_URI = "/search_sources";

	String DATASETS_URI = "/datasets";

	String DATASET_URI = "/dataset";

	String DATASETS_PAGE = "datasets";

	String CREATE_DATASET_URI = "/create_dataset";

	String UPDATE_DATASET_URI = "/update_dataset";

	String DELETE_DATASET_URI = "/delete_dataset";

	String VALIDATE_CREATE_DATASET_URI = "/validate_create_dataset";

	String ORIGIN_DOMAINS_URI = "/origin_domains";

	String CHANGE_ROLE_URI = "/change_role";

	String SYN_SEARCH_PAGE = "synsearch";

	String SYN_SEARCH_URI = "/synsearch";

	String SYN_WORD_DETAILS_URI = "/syn_worddetails";

	String SYN_CHANGE_RELATION_STATUS = "/syn_relation_status";

	String SYN_CREATE_LEXEME = "/syn_create_lexeme";

	String SYN_SEARCH_WORDS = "/syn_search_words";

	String USER_PROFILE_PAGE = "userprofile";

	String USER_PROFILE_URI = "/userprofile";

	String WORD_BACK_URI = "/wordback";

	String LEX_BACK_URI = "/lexback";

	String MEANING_BACK_URI = "/meaningback";

	String WORD_VALUE_BACK_URI = "/wordvalueback";

	String PAGE_FRAGMENT_ELEM = " :: ";

	String REDIRECT_PREF = "redirect:";

	// mode flags

	String SEARCH_MODE_SIMPLE = "SIMPLE";

	String SEARCH_MODE_DETAIL = "DETAIL";

	String RETURN_PAGE_LEX_SEARCH = "lex_search";

	String RETURN_PAGE_TERM_SEARCH = "term_search";

	// model keys

	String SESSION_BEAN = "sessionBean";

	String APP_DATA_MODEL_KEY = "appData";

	String VIEW_UTIL_KEY = "viewUtil";

	String PERM_DATA_UTIL_KEY = "permDataUtil";

	String CLASSIFIER_UTIL_KEY = "classifierUtil";

	String USER_KEY = "user";

	String SEARCH_WORD_KEY = "searchWord";

	String REQUEST_START_TIME_KEY = "request_start_time";
}
