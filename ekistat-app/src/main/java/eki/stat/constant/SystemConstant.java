package eki.stat.constant;

import eki.common.constant.StatSearchConstant;

public interface SystemConstant extends StatSearchConstant {

	int DEFAULT_MAX_RESULTS_LIMIT = 50;

	String CACHE_KEY_GENERIC = "generic";

	long CACHE_EVICT_DELAY_10MIN = 10 * 60 * 1000;
}
