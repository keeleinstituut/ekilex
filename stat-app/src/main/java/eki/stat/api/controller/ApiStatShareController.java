package eki.stat.api.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnWebApplication
@RestController
public class ApiStatShareController {

	@GetMapping("/api/test")
	@ResponseBody
	public String test() {

		// http://localhost:5599/api/test
		return "ho ho hoo";
	}

}
