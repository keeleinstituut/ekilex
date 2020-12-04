package eki.stat.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.data.SearchStat;
import eki.stat.service.StatService;

@ConditionalOnWebApplication
@RestController
public class ApiStatCollectController {

	@Autowired
	private StatService statService;

	@PostMapping("/api/stat")
	@ResponseBody
	public String createSearchStat(@RequestBody SearchStat searchStat) {

		statService.createSearchStat(searchStat);
		return "ok";
	}

}
