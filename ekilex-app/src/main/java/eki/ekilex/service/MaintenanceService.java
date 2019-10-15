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

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.db.MaintenanceDbService;

@Component
public class MaintenanceService implements SystemConstant {

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
	}

	private void clearClassifCache() {
		cacheManager.getCache(CACHE_KEY_CLASSIF).clear();
	}

	public void clearDatasetCache() {
		cacheManager.getCache(CACHE_KEY_DATASET).clear();
	}

	private void clearUserCache() {
		cacheManager.getCache(CACHE_KEY_USER).clear();
	}

	@CacheEvict(allEntries = true, value = {CACHE_KEY_CLASSIF, CACHE_KEY_DATASET})
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_60MIN, initialDelay = 5000)
	public void classifCacheEvict() {
	}

	@CacheEvict(allEntries = true, value = CACHE_KEY_USER)
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_5MIN, initialDelay = 5000)
	public void userCacheEvict() {
	}

	@Scheduled(fixedDelay = DELETE_FLOATING_DATA_DELAY, initialDelay = 5000)
	@Transactional
	public void deleteFloatingData() {

		int deletedFreeforms = maintenanceDbService.deleteFloatingFreeforms();
		int deletedProcessLogs = maintenanceDbService.deleteFloatingProcessLogs();
		int deletedLifecycleLogs = maintenanceDbService.deleteFloatingLifecycleLogs();
		int deletedMeanings = maintenanceDbService.deleteFloatingMeanings();
		int deletedWords = maintenanceDbService.deleteFloatingWords();

		logger.debug("Maintenance service deleted {} floating freeforms", deletedFreeforms);
		logger.debug("Maintenance service deleted {} floating process logs", deletedProcessLogs);
		logger.debug("Maintenance service deleted {} floating lifecycle logs", deletedLifecycleLogs);
		logger.debug("Maintenance service deleted {} floating meanings", deletedMeanings);
		logger.debug("Maintenance service deleted {} floating words", deletedWords);
	}
}
