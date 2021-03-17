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
import eki.ekilex.data.db.tables.records.WordRecord;
import eki.ekilex.service.db.MaintenanceDbService;

@Component
public class MaintenanceService implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private MaintenanceDbService maintenanceDbService;

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

	public void clearTagCache() {
		cacheManager.getCache(CACHE_KEY_TAG).clear();
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

	@CacheEvict(allEntries = true, value = CACHE_KEY_TAG)
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_60MIN, initialDelay = 5000)
	public void tagCacheEvict() {
	}

	@Scheduled(cron = MERGE_HOMONYMS_TIME_3_AM)
	@Transactional
	public void mergeHomonyms() {

		logger.info("Starting homonyms merge procedure...");
		String[] includedLangs = new String[] {LANGUAGE_CODE_EST, LANGUAGE_CODE_LAT, LANGUAGE_CODE_RUS};
		maintenanceDbService.mergeHomonymsToSss(includedLangs);
	}

	@Transactional
	public Map<String, Count> unifyApostrophesAndRecalcAccents() {

		logger.info("Unifying apostrophes and updating accents...");

		Count unifiedApostropheWordCount = new Count();
		Count accentRecalcWordCount = new Count();
		Map<String, Count> resultCounts = new HashMap<>();
		resultCounts.put("unifiedApostropheWordCount", unifiedApostropheWordCount);
		resultCounts.put("accentRecalcWordCount", accentRecalcWordCount);

		List<WordRecord> wordRecords = maintenanceDbService.getWordRecords();
		boolean updateExists;
		for (WordRecord wordRecord : wordRecords) {
			String value = wordRecord.getValue();
			String valueAsWordSrc = wordRecord.getValueAsWord();
			String valueClean = textDecorationService.unifyToApostrophe(value);
			updateExists = false;
			if (!StringUtils.equals(value, valueClean)) {
				wordRecord.setValueAsWord(valueClean);
				unifiedApostropheWordCount.increment();
				updateExists = true;
			}
			String valueAsWordTrgt = textDecorationService.removeAccents(valueClean);
			if (StringUtils.isNotBlank(valueAsWordTrgt) && !StringUtils.equals(valueAsWordSrc, valueAsWordTrgt)) {
				wordRecord.setValueAsWord(valueAsWordTrgt);
				accentRecalcWordCount.increment();
				updateExists = true;
			}
			if (updateExists) {
				wordRecord.update();
			}
		}
		if (unifiedApostropheWordCount.getValue() > 0) {
			logger.info("Unified apostrophe word count: {}", unifiedApostropheWordCount.getValue());
		}
		if (accentRecalcWordCount.getValue() > 0) {
			logger.info("Accent recalc word count: {}", accentRecalcWordCount.getValue());
		}
		logger.info("...apostrophes and accents done");
		return resultCounts;
	}

	@Scheduled(cron = DELETE_FLOATING_DATA_TIME_4_AM)
	@Transactional
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
	}
}
