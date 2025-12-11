package eki.ekilex.constant;

public interface WebConstant {

	String INDEX_URI = "/";

	String DO_LOGIN_URI = "/dologin";

	String DO_LOGOUT_URI = "/dologout";

	String LOGIN_URI = "/login";

	String LOGIN_ERROR_URI = "/loginerror";

	String LOGIN_PAGE = "login";

	String HOME_PAGE = "home";

	String HOME_URI = "/home";

	String VIEW_RESOURCES_URI = "/view";

	String COMMON_PAGE = "common";

	String ERROR_PAGE = "error";

	String APPLY_PAGE = "apply";

	String APPLY_URI = "/apply";

	String REAPPLY_URI = "/reapply";

	String APPLY_READ = "/apply_read";

	String APPLY_LIMITED_URI = "/apply_limited";

	String PERMISSIONS_PAGE = "permissions";

	String PERMISSIONS_URI = "/permissions";

	String APPLICATIONS_URI = "/applications";

	String SEARCH_PAGE = "search";

	String SEARCH_URI = "/search";

	String DOWNLOAD_URI = "/download";

	String MAINTENANCE_URI = "/maintenance";

	String ACTIVITYLOGVIEW_PAGE = "activitylogview";

	String LEX_SEARCH_PAGE = "lexsearch";

	String LEX_SEARCH_URI = "/lexsearch";

	String LEX_PAGING_URI = "/lex_paging";

	String TERM_SEARCH_PAGE = "termsearch";

	String TERM_SEARCH_URI = "/termsearch";

	String TERM_PAGING_URI = "/term_paging";

	String TERM_SEARCH_RESULT_DOWNLOAD_URI = "/termsearch_result_download";

	String TERM_SEARCH_RESULT_QUEUE_URI = "/termsearch_result_queue";

	String TERM_SEARCH_RESULT_ACCESS_URI = "/termsearch_result_access";

	String TERM_MEANING_TABLE_URI = "/termmeaningtable";

	String TERM_MEANING_TABLE_PAGE = "termmeaningtable";

	String TERM_MEANING_TABLE_COMPONENTS_PAGE = "termmeaningtablecomp";

	String UPDATE_MEANING_URI = "/update_meaning";

	String UPDATE_LEXEMES_PUBLICITY_URI = "/update_lexemes_publicity";

	String UPDATE_DEFINITIONS_PUBLICITY_URI = "/update_definitions_publicity";

	String UPDATE_USAGES_PUBLICITY_URI = "/update_usages_publicity";

	String LIM_TERM_PAGING_URI = "/lim_term_paging";

	String COMPONENTS_PAGE = "components";

	String TERM_COMPONENTS_PAGE = "termcomp";

	String WORD_COMPONENTS_PAGE = "wordcomp";

	String LEX_COMPONENTS_PAGE = "lexcomp";

	String WORD_RELATION_FRAGMENT = "word_relation";

	String WORD_RELATION_URI = "/wordrelation";

	String WORD_EKI_RECOMMENDATION_FRAGMENT = "word_eki_recommendation";

	String WORD_EKI_RECOMMENDATION_URI = "/wordekirecommendation";

	String WORD_DETAILS_FRAGMENT = "word_details";

	String WORD_DETAILS_URI = "/worddetails";

	String LEXEME_DETAILS_URI = "/lexemedetails";

	String TERM_MEANING_DETAILS_URI = "/termmeaningdetails";

	String LIM_TERM_MEANING_DETAILS_URI = "/limtermmeaningdetails";

	String LEXEME_COLLOCATION_URI = "/lexemecolloc";

	String COLLOC_MEMBER_MOVE_URI = "/colloc_member_move";

	String WORD_SELECT_URI = "/wordselect";

	String WORD_SELECT_PAGE = "wordselect";

	String MEANING_INTERNAL_LINK_SEARCH_URI = "/meaning_internal_link_search";

	String WORD_INTERNAL_LINK_SEARCH_URI = "/word_internal_link_search";

	String UPDATE_MEANING_REL_PREFS_URI = "/update_meaning_rel_prefs";

	String UPDATE_TAG_PREFS_URI = "/update_tag_prefs";

	String TOGGLE_SECTION_EXPAND_URI = "/toggle_section_expand";

	String MEANING_IMAGE_EXPAND_URI = "/meaning_image_expand";

	String MEANING_MEDIA_EXPAND_URI = "/meaning_media_expand";

	String UPDATE_WORD_ACTIVE_TAG_COMPLETE_URI = "/update_word_active_tag_complete";

	String UPDATE_MEANING_ACTIVE_TAG_COMPLETE_URI = "/update_meaning_active_tag_complete";

	String APPROVE_MEANING = "/approve_meaning";

	String GENERATE_API_KEY = "/generate_api_key";

	String SEND_FEEDBACK_URI = "/send_feedback";

	String WW_FEEDBACK_URI = "/wwfeedback";

	String WW_FEEDBACK_PAGE = "wwfeedback";

	String WORKLOAD_REPORT_URI = "/workloadreport";

	String WORKLOAD_REPORT_PAGE = "workloadreport";

	String WORKLOAD_REPORT_COMPONENTS_PAGE = "workloadreportcomp";

	String LIM_TERM_SEARCH_URI = "/limtermsearch";

	String LIM_TERM_SEARCH_PAGE = "limtermsearch";

	String LEX_JOIN_SEARCH_URI = "/lexjoinsearch";

	String LEX_JOIN_INIT_URI = "/lexjoininit";

	String LEX_JOIN_URI = "/lexjoin";

	String LEX_JOIN_PAGE = "lexjoin";

	String LEX_JOIN_VALIDATE_URI = "/lexjoinvalidate";

	String LEX_DUPLICATE_URI = "/lexduplicate";

	String EMPTY_LEX_DUPLICATE_URI = "/emptylexduplicate";

	String MEANING_WORD_AND_LEX_DUPLICATE_URI = "/meaningwordandlexduplicate";

	String MEANING_DUPLICATE_URI = "/meaningduplicate";

	String MEANING_JOIN_URI = "/meaningjoin";

	String MEANING_JOIN_PAGE = "meaningjoin";

	String LIM_TERM_MEANING_JOIN_URI = "/limtermmeaningjoin";

	String LIM_TERM_MEANING_JOIN_PAGE = "limtermmeaningjoin";

	String VALIDATE_MEANING_JOIN_URI = "/validatemeaningjoin";

	String WORD_JOIN_URI = "/wordjoin";

	String WORD_JOIN_PAGE = "wordjoin";

	String WORD_DUPLICATE_URI = "/wordduplicate";

	String FAKE_REGISTER_AND_PASSWORD_RECOVERY_URI = "/submit";

	String REGISTER_URI = "/register";

	String REGISTER_PAGE = "register";

	String TERMS_OF_USE_URI = "/termsofuse";

	String TERMS_OF_USE_PAGE = "termsofuse";

	String TERMS_AGREEMENT_URI = "/termsagreement";

	String TERMS_AGREEMENT_PAGE = "termsagreement";

	String AGREE_TERMS_URI = "/agree_terms";

	String REFUSE_TERMS_URI = "/refuse_terms";

	String ACTIVATE_URI = "/activate";

	String PROTO_URI = "/proto";

	String PASSWORD_RECOVERY_URI = "/passwordrecovery";

	String PASSWORD_RECOVERY_PAGE = "passwordrecovery";

	String PASSWORD_SET_URI = "/passwordset";

	String PASSWORD_SET_PAGE = "passwordset";

	String SELECT_URI = "/select";

	String INIT_URI = "/init";

	String LEX_CREATE_WORD_URI = "/lex_create_word";

	String TERM_CREATE_WORD_URI = "/termcreateword";

	String TERM_CREATE_WORD_PAGE = "termcreateword";

	String TERM_CREATE_WORD_AND_MEANING_URI = "/termcreatewordandmeaning";

	String TERM_CREATE_WORD_AND_MEANING_PAGE = "termcreatewordandmeaning";

	String TERM_UPDATE_WORD_URI = "/termupdateword";

	String TERM_UPDATE_WORD_PAGE = "termupdateword";

	String LIM_TERM_CREATE_WORD_URI = "/lim_term_create_word";

	String CREATE_HOMONYM_URI = "/create_homonym";

	String CREATE_RELATIONS_URI = "/create_relations";

	String OPPOSITE_RELATIONS_URI = "/oppositerelations";

	String PUBLISH_ITEM_URI = "/publish_item";

	String CREATE_ITEM_URI = "/create_item";

	String UPDATE_ITEM_URI = "/update_item";

	String UPDATE_ORDERING_URI = "/update_ordering";

	String UPDATE_COLLOC_MEMBER_GROUP_ORDER_URI = "/update_colloc_member_group_order";

	String UPDATE_LEVELS_URI = "/update_levels";

	String UPDATE_LEXEME_LEVELS_URI = "/update_lexeme_levels";

	String DELETE_ITEM_URI = "/delete_item";

	String CONFIRM_OP_URI = "/confirm_op";

	String SOURCE_COMPONENTS_PAGE = "sourcecomp";

	String SOURCE_SEARCH_URI = "/sourcesearch";

	String SOURCE_SEARCH_PAGE = "sourcesearch";

	String SOURCE_DETAILS_FRAGMENT = "source_details";

	String SOURCE_QUICK_SEARCH_URI = "/sourcequicksearch";

	String SOURCE_NAME_SEARCH_URI = "/sourcenamesearch";

	String SOURCE_ID_SEARCH_URI = "/sourceidsearch";

	String SOURCE_DETAIL_SEARCH_URI = "/sourcedetailsearch";

	String SOURCE_JOIN_SEARCH_URI = "/sourcejoinsearch";

	String SOURCE_PAGING_URI = "/source_paging";

	String UPDATE_SOURCE_URI = "/update_source";

	String CREATE_SOURCE_URI = "/create_source";

	String EDIT_SOURCE_LINK_URI = "/edit_source_link";

	String CREATE_SOURCE_AND_SOURCE_LINK_URI = "/create_source_and_source_link";

	String VALIDATE_DELETE_SOURCE_URI = "/validate_delete_source";

	String DELETE_SOURCE_URI = "/delete_source";

	String OPEN_SOURCE_JOIN_URI = "/open_source_join";

	String SOURCE_JOIN_URI = "/source_join";

	String SOURCE_JOIN_PAGE = "sourcejoin";

	String DATASETS_URI = "/datasets";

	String DATASET_URI = "/dataset";

	String DATASETS_PAGE = "datasets";

	String CREATE_DATASET_URI = "/create_dataset";

	String UPDATE_DATASET_URI = "/update_dataset";

	String DELETE_DATASET_URI = "/delete_dataset";

	String VALIDATE_CREATE_DATASET_URI = "/validate_create_dataset";

	String FEDTERM_UPLOAD_URI = "/fedtermupload";

	String FEDTERM_UPLOAD_PAGE = "fedtermupload";

	String ORIGIN_DOMAINS_URI = "/origin_domains";

	String CLASSIFIERS_URI = "/classifiers";

	String CLASSIFIERS_PAGE = "classifiers";

	String CREATE_CLASSIFIER_URI = "/create_classifier";

	String UPDATE_CLASSIFIER_URI = "/update_classifier";

	String DELETE_CLASSIFIER_URI = "/delete_classifier";

	String TAGS_URI = "/tags";

	String TAGS_PAGE = "tags";

	String CREATE_TAG_URI = "/create_tag";

	String UPDATE_TAG_URI = "/update_tag";

	String DELETE_TAG_URI = "/delete_tag";

	String HISTORY_URI = "/history";

	String HISTORY_PAGE = "history";

	String NEWS_URI = "/news";

	String NEWS_PAGE = "news";

	String STAT_URI = "/stat";

	String STAT_PAGE = "stat";

	String STAT_COMPONENTS_PAGE = "statcomp";

	String WW_STAT_URI = "/ww_stat";

	String SELECT_ROLE_URI = "/select_role";

	String MANUAL_EVENT_ON_UPDATE_URI = "/manual_event_on_update";

	String PART_SYN_SEARCH_URI = "/partsynsearch";

	String PART_SYN_SEARCH_PAGE = "partsynsearch";

	String FULL_SYN_SEARCH_URI = "/fullsynsearch";

	String FULL_SYN_SEARCH_PAGE = "fullsynsearch";

	String SYN_PAGING_URI = "/syn_paging";

	String COMMON_SYN_COMPONENTS_PAGE = "syncomp";

	String PART_SYN_COMPONENTS_PAGE = "partsyncomp";

	String FULL_SYN_COMPONENTS_PAGE = "fullsyncomp";

	String INEXACT_SYN_COMPONENTS_PAGE = "inexactsyncomp";

	String PART_SYN_WORD_DETAILS_URI = "/partsyn_worddetails";

	String FULL_SYN_WORD_DETAILS_URI = "/fullsyn_worddetails";

	String SYN_RELATION_STATUS_URI = "/syn_relation_status";

	String SYN_CREATE_MEANING_RELATION_URI = "/syn_create_meaning_relation";

	String SYN_CREATE_MEANING_WORD_WITH_CANDIDATE_DATA_URI = "/syn_create_meaning_word_with_candidate_data";

	String SYN_CREATE_MEANING_WORD_URI = "/syn_create_meaning_word";

	String PART_SYN_SEARCH_WORDS_URI = "/part_syn_search_words";

	String FULL_SYN_SEARCH_WORDS_URI = "/full_syn_search_words";

	String INEXACT_SYN_INIT_URI = "/inexact_syn_init";

	String INEXACT_SYN_SEARCH_MEANINGS_URI = "/inexact_syn_search_meanings";

	String INEXACT_SYN_MEANING_URI = "/inexact_syn_meaning";

	String INEXACT_SYN_WORD_URI = "/inexact_syn_word";

	String INEXACT_SYN_MEANING_RELATION_URI = "/inexact_syn_meaning_relation";

	String USER_PROFILE_PAGE = "userprofile";

	String USER_PROFILE_URI = "/userprofile";

	String UPDATE_EMAIL_URI = "update_email";

	String WORD_BACK_URI = "/wordback";

	String LEX_BACK_URI = "/lexback";

	String MEANING_BACK_URI = "/meaningback";

	String LIM_TERM_MEANING_BACK_URI = "/limtermmeaningback";

	String PAGE_FRAGMENT_ELEM = " :: ";

	String REDIRECT_PREF = "redirect:";

	String RESPONSE_OK_VER1 = "OK";

	String RESPONSE_OK_VER2 = "{}";

	String RESPONSE_FAIL = "fail";

	// mode flags

	String SEARCH_MODE_SIMPLE = "SIMPLE";

	String SEARCH_MODE_DETAIL = "DETAIL";

	// model keys

	String SESSION_BEAN = "sessionBean";

	String PERM_SEARCH_BEAN = "permSearchBean";

	String WW_FEEDBACK_SEARCH_BEAN = "wwFeedbackSearchBean";

	String APP_DATA_MODEL_KEY = "appData";

	String VIEW_UTIL_KEY = "viewUtil";

	String PERM_DATA_UTIL_KEY = "permDataUtil";

	String CLASSIFIER_UTIL_KEY = "classifierUtil";

	String USER_PROFILE_KEY = "userProfile";

	String USER_ROLE_DATA_KEY = "userRoleData";

	String REQUEST_START_TIME_KEY = "request_start_time";

	// request parameters

	String REQUEST_PARAM_PAGE = "p";

	// OpenAPI / Swagger documentation URIs

	String API_DOCS_PATH = "/v3/api-docs";

	String API_DOCS_RESOURCES_PATH = "/v3/api-docs/**";

	String SWAGGER_UI_PATH = "/api-docs.html";

	String SWAGGER_UI_RESOURCES_PATH = "/swagger-ui/**";
}
