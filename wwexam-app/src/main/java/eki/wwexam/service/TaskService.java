package eki.wwexam.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.wwexam.constant.SystemConstant;

@Component
public class TaskService implements SystemConstant {

	@CacheEvict(allEntries = true, value = CACHE_KEY_CLASSIF)
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_60MIN,  initialDelay = 5000)
	public void classifCacheEvict() {
	}
}
