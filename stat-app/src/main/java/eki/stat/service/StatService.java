package eki.stat.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.SearchStat;
import eki.stat.service.db.StatDbService;

@Component
public class StatService {

	@Autowired
	private StatDbService statDbService;

	@Transactional
	public void createSearchStat(SearchStat searchStat) {
		statDbService.createSearchStat(searchStat);
	}
}
