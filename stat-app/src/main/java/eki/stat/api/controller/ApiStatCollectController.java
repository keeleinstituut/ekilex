package eki.stat.api.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.data.SearchStat;

@ConditionalOnWebApplication
@RestController
public class ApiStatCollectController {

	@PostMapping("/api/stat")
	@ResponseBody
	public String test(@RequestBody SearchStat searchStat) {

		return "ok";
	}

}
