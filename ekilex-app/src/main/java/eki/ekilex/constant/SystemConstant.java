package eki.ekilex.constant;

public interface SystemConstant {

	int DEFAULT_MAX_RESULTS_LIMIT = 50;

	int AUTOCOMPLETE_MAX_RESULTS_LIMIT = 15;

	int DEFAULT_MAX_DOWNLOAD_LIMIT = 25;

	int DEFAULT_OFFSET = 0;

	long UPDATE_STAT_DATA_DELAY = 10 * 60 * 1000;

	long CACHE_EVICT_DELAY_5MIN = 5 * 60 * 1000;

	long CACHE_EVICT_DELAY_60MIN = 60 * 60 * 1000;

	String JOIN_HOMONYMS_TIME_3_AM = "0 0 3 * * *";

	String ADJUST_HOMONYM_NRS_TIME_3_30_AM = "0 30 3 * * *";

	String DELETE_FLOATING_DATA_TIME_4_AM = "0 0 4 * * *";

	String DELETE_OUTDATED_DATA_REQUESTS_TIME_5_AM = "0 0 5 * * *";

	String CLASSIF_LABEL_LANG_EST = "est";

	String CLASSIF_LABEL_TYPE_DESCRIP = "descrip";

	String CLASSIF_LABEL_TYPE_WORDWEB = "wordweb";

	String CLASSIF_LABEL_TYPE_OD = "od";

	String CLASSIF_LABEL_TYPE_COMMENT = "comment";

	String CLASSIF_LABEL_TYPE_ISO2 = "iso2";

	String CACHE_KEY_CLASSIF = "classif";

	String CACHE_KEY_DATASET = "dataset";

	String CACHE_KEY_USER = "user";

	String CACHE_KEY_TAG = "tag";

	String CACHE_KEY_WORKLOAD_REPORT = "workloadreport";

	int MAX_TEXT_LENGTH_LIMIT = 2000;

	int EXECUTE_QUEUE_DELAY_1_SEC = 1000;

	int DELETE_OUTDATED_DATA_AFTER_ACCESS_HOURS = 24;
}
