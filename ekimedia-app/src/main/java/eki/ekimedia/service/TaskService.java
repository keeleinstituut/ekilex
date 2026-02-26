package eki.ekimedia.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.ekimedia.constant.SystemConstant;

@Component
public class TaskService implements SystemConstant {

	@CacheEvict(allEntries = true, value = {CACHE_KEY_GENERIC})
	@Scheduled(fixedDelay = CACHE_EVICT_DELAY_10MIN, initialDelay = 5000)
	public void genericCacheEvict() {
	}
}
