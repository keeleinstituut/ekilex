package eki.ekilex.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.db.MaintenanceDbService;

@Component
public class MaintenanceService implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private MaintenanceDbService maintenanceDbService;

	@PreAuthorize("principal.admin")
	public void clearCache() {

		clearClassifCache();
		clearDatasetCache();
		clearUserCache();
		clearTagCache();
	}

	public void clearClassifCache() {
		cacheManager.getCache(CACHE_KEY_CLASSIF).clear();
	}

	public void clearDatasetCache() {
		cacheManager.getCache(CACHE_KEY_DATASET).clear();
	}

	private void clearUserCache() {
		cacheManager.getCache(CACHE_KEY_USER).clear();
	}

	private void clearTagCache() {
		cacheManager.getCache(CACHE_KEY_TAG).clear();
	}

	@CacheEvict(allEntries = true, value = {CACHE_KEY_CLASSIF, CACHE_KEY_DATASET})
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_60MIN, initialDelay = 5000)
	public void classifCacheEvict() {
	}

	@CacheEvict(allEntries = true, value = CACHE_KEY_USER)
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_5MIN, initialDelay = 5000)
	public void userCacheEvict() {
	}

	@CacheEvict(allEntries = true, value = CACHE_KEY_TAG)
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_60MIN, initialDelay = 5000)
	public void tagCacheEvict() {
	}

	@Scheduled(cron = MERGE_HOMONYMS_TIME_3_AM)
	@Transactional
	public void mergeHomonyms() {

		logger.info("Starting homonyms merge procedure...");
		String[] includedLangs = new String[] {LANGUAGE_CODE_EST, LANGUAGE_CODE_LAT};
		maintenanceDbService.mergeHomonymsToSss(includedLangs);
	}

	@Scheduled(cron = DELETE_FLOATING_DATA_TIME_4_AM)
	@Transactional
	public void deleteFloatingData() {

		logger.info("Deleting floating data...");
		int deletedFreeformCount = maintenanceDbService.deleteFloatingFreeforms();
		if (deletedFreeformCount > 0) {
			logger.debug("Maintenance service deleted {} floating freeforms", deletedFreeformCount);
		}

		int deletedLifecycleLogCount = maintenanceDbService.deleteFloatingLifecycleLogs();
		if (deletedLifecycleLogCount > 0) {
			logger.debug("Maintenance service deleted {} floating lifecycle logs", deletedLifecycleLogCount);
		}

		int deletedMeaningCount = maintenanceDbService.deleteFloatingMeanings();
		if (deletedMeaningCount > 0) {
			logger.debug("Maintenance service deleted {} floating meanings", deletedMeaningCount);
		}

		int deletedWordCount = maintenanceDbService.deleteFloatingWords();
		if (deletedWordCount > 0) {
			logger.debug("Maintenance service deleted {} floating words", deletedWordCount);
		}
	}
}
