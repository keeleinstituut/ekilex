package eki.stat.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.StatType;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;
import eki.stat.constant.ApiConstant;
import eki.stat.service.StatService;

@ConditionalOnWebApplication
@RestController
public class ApiStatController implements ApiConstant {

	@Autowired
	private StatService statService;

	@GetMapping(API_SERVICES_URI + STAT_URI + COUNT_URI)
	@ResponseBody
	public Long getWwSearchCount() {
		return statService.getWwSearchCount();
	}

	@GetMapping(API_SERVICES_URI + STAT_URI + SEARCH_URI)
	@ResponseBody
	public StatSearchResult searchWwSearchStat(StatSearchFilter statSearchFilter) throws Exception {

		if (StatType.WW_SEARCH.equals(statSearchFilter.getStatType())) {
			return statService.searchWwSearchStat(statSearchFilter);
		} else {
			throw new Exception("Stat type not supported");
		}
	}

	@PostMapping(API_SERVICES_URI + STAT_URI + CREATE_URI + "/{statType}")
	@ResponseBody
	public String createStat(@RequestBody String statObjectJson, @PathVariable StatType statType) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		if (StatType.WW_SEARCH.equals(statType)) {
			SearchStat searchStat = objectMapper.readValue(statObjectJson, SearchStat.class);
			statService.createWwSearchStat(searchStat);
		} else if (StatType.WW_EXCEPTION.equals(statType)) {
			ExceptionStat exceptionStat = objectMapper.readValue(statObjectJson, ExceptionStat.class);
			statService.createWwExceptionStat(exceptionStat);
		} else {
			throw new Exception("Stat type not supported");
		}

		return "ok";
	}

}
