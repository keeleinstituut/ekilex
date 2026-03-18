package eki.ekimedia.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eki.ekimedia.constant.ApiConstant;

@RestController
public class HealthCheckController implements ApiConstant {

	@GetMapping(PING_URI)
	public Long getTimeStamp() {
		return System.currentTimeMillis();
	}
}
