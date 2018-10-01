package eki.wordweb.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.SystemConstant;

@Component
public class TaskService implements SystemConstant {

	@CacheEvict(allEntries = true, value = {CACHE_KEY_CLASSIF, CACHE_KEY_DATASET, CACHE_KEY_CORPORA})
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY,  initialDelay = 5000)
	public void corporaCacheEvict() {
	}
}
