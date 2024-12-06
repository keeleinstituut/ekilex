package eki.ekilex.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.Count;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SourceTargetIdTuple;
import eki.ekilex.data.db.main.tables.records.WordRecord;
import eki.ekilex.service.db.MaintenanceDbService;

@Component
public class MaintenanceService implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private MaintenanceDbService maintenanceDbService;

	public void clearCache() {

		clearClassifCache();
		clearDatasetCache();
		clearUserCache();
		clearTagCache();
		clearWorkloadReportCache();
	}

	public void clearClassifCache() {
		cacheManager.getCache(CACHE_KEY_CLASSIF).clear();
	}

	public void clearDatasetCache() {
		cacheManager.getCache(CACHE_KEY_DATASET).clear();
	}

	public void clearTagCache() {
		cacheManager.getCache(CACHE_KEY_TAG).clear();
	}

	private void clearUserCache() {
		cacheManager.getCache(CACHE_KEY_USER).clear();
	}

	private void clearWorkloadReportCache() {
		cacheManager.getCache(CACHE_KEY_WORKLOAD_REPORT).clear();
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

	@CacheEvict(allEntries = true, value = CACHE_KEY_WORKLOAD_REPORT)
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_60MIN, initialDelay = 5000)
	public void workloadreportCacheEvict() {
	}

	@Scheduled(cron = MERGE_HOMONYMS_TIME_3_AM)
	@Transactional(rollbackOn = Exception.class)
	public void mergeHomonyms() throws Exception {

		logger.info("Starting homonyms merge...");
		String[] includedLangs = new String[] {LANGUAGE_CODE_EST, LANGUAGE_CODE_LAT, LANGUAGE_CODE_RUS};
		boolean isManualEventOnUpdateEnabled = false;
		List<SourceTargetIdTuple> homonyms = maintenanceDbService.getHomonymsToMerge(includedLangs);
		logger.info("Found {} homonyms to merge", homonyms.size());

		for (SourceTargetIdTuple homonym : homonyms) {
			Long targetWordId = homonym.getTargetId();
			Long sourceWordId = homonym.getSourceId();
			compositionService.joinWords(targetWordId, sourceWordId, null, isManualEventOnUpdateEnabled);
		}

		logger.info("Homonyms merge finished");
	}

	@Scheduled(cron = ADJUST_HOMONYM_NRS_TIME_3_30_AM)
	@Transactional(rollbackOn = Exception.class)
	public void adjustHomonymNrs() {

		logger.info("Starting homonym numbers adjust procedure...");
		maintenanceDbService.adjustHomonymNrs();
	}

	@Transactional(rollbackOn = Exception.class)
	public Map<String, Count> unifyApostrophesAndRecalcAccents() {

		logger.info("Unifying apostrophes and updating accents...");

		Count updateCount = new Count();
		Map<String, Count> resultCounts = new HashMap<>();
		resultCounts.put("updateCount", updateCount);

		List<WordRecord> wordRecords = maintenanceDbService.getWordRecords();
		boolean updateExists;
		for (WordRecord wordRecord : wordRecords) {
			String value = wordRecord.getValue();
			String valueAsWordSrc = wordRecord.getValueAsWord();
			String valueClean = textDecorationService.unifyToApostrophe(value);
			String valueAsWordTrgt = textDecorationService.removeAccents(valueClean);
			updateExists = false;
			if (StringUtils.isBlank(valueAsWordSrc)) {
				// initial valuation
				if (StringUtils.isNotBlank(valueAsWordTrgt)) {
					wordRecord.setValueAsWord(valueAsWordTrgt);
					updateExists = true;
				} else if (!StringUtils.equals(value, valueClean)) {
					wordRecord.setValueAsWord(valueClean);
					updateExists = true;
				}
			} else {
				// compare with existing value
				if (StringUtils.isNotBlank(valueAsWordTrgt)) {
					if (!StringUtils.equals(valueAsWordSrc, valueAsWordTrgt)) {
						wordRecord.setValueAsWord(valueAsWordTrgt);
						updateExists = true;
					}
				} else if (!StringUtils.equals(valueAsWordSrc, valueClean)) {
					wordRecord.setValueAsWord(valueClean);
					updateExists = true;
				}
			}
			if (updateExists) {
				wordRecord.update();
				updateCount.increment();
			}
		}
		if (updateCount.getValue() > 0) {
			logger.info("Unified apostrophe and accent recalc update count: {}", updateCount.getValue());
		}

		logger.info("...apostrophes and accents done");

		return resultCounts;
	}

	@Scheduled(cron = DELETE_FLOATING_DATA_TIME_4_AM)
	@Transactional(rollbackOn = Exception.class)
	public void deleteFloatingData() {

		logger.info("Deleting floating data...");
		int deletedFreeformCount = maintenanceDbService.deleteFloatingFreeforms();
		if (deletedFreeformCount > 0) {
			logger.debug("Maintenance service deleted {} floating freeforms", deletedFreeformCount);
		}

		int deletedMeaningCount = maintenanceDbService.deleteFloatingMeanings();
		if (deletedMeaningCount > 0) {
			logger.debug("Maintenance service deleted {} floating meanings", deletedMeaningCount);
		}

		int deletedWordCount = maintenanceDbService.deleteFloatingWords();
		if (deletedWordCount > 0) {
			logger.debug("Maintenance service deleted {} floating words", deletedWordCount);
		}

		int deletedFormCount = maintenanceDbService.deleteFloatingForms();
		if (deletedFormCount > 0) {
			logger.debug("Maintenance service deleted {} floating forms", deletedFormCount);
		}
	}

	@Scheduled(cron = DELETE_OUTDATED_DATA_REQUESTS_TIME_5_AM)
	@Transactional(rollbackOn = Exception.class)
	public void deleteOutdatedDataRequests() {

		int deletedDataRequestCount = maintenanceDbService.deleteAccessedDataRequests(DELETE_OUTDATED_DATA_AFTER_ACCESS_HOURS);
		if (deletedDataRequestCount > 0) {
			logger.debug("Maintenance service deleted {} outdated data requests", deletedDataRequestCount);
		}
	}
}
