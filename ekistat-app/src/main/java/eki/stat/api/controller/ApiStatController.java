package eki.stat.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.StatType;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.stat.constant.StatConstant;
import eki.stat.service.StatService;

@ConditionalOnWebApplication
@RestController
public class ApiStatController implements StatConstant {

	@Autowired
	private StatService statService;

	@GetMapping(STAT_API_SERVICES_URI + WW_STAT_COUNT_URI)
	@ResponseBody
	public long getWwSearchStatCount() {

		return statService.getWwSearchStatCount();
	}

	@GetMapping(STAT_API_SERVICES_URI)
	@ResponseBody
	public Map<String, Integer> getSearchStat(
			@RequestParam(name = "statType") StatType statType,
			@RequestParam(name = "searchMode") String searchMode,
			@RequestParam(name = "datasetCode") String datasetCode,
			@RequestParam(name = "lang") String lang,
			@RequestParam(name = "resultsFrom") String resultsFrom,
			@RequestParam(name = "resultsUntil") String resultsUntil) throws Exception {

		if (StatType.WW_SEARCH.equals(statType)) {
			return statService.getWwSearchStat(searchMode, datasetCode, lang, resultsFrom, resultsUntil);
		} else {
			throw new Exception("Stat type not supported");
		}
	}

	@PostMapping(STAT_API_SERVICES_URI + "/{statType}")
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
