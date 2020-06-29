package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.PermissionGrantService;
import eki.ekilex.service.UserContext;
import eki.ekilex.service.util.MessageUtil;
import eki.ekilex.web.util.ValueUtil;

public abstract class AbstractApiController implements SystemConstant, ApiConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected PermissionGrantService permissionGrantService;

	@Autowired
	protected ValueUtil valueUtil;

	@Autowired
	private MessageUtil messageUtil;

	protected ApiResponse getOpPositiveResponse() {
		String positiveQuote = messageUtil.getPositiveQuote();
		return new ApiResponse(true, positiveQuote);
	}

	protected ApiResponse getOpPositiveResponse(Long id) {
		String positiveQuote = messageUtil.getPositiveQuote();
		return new ApiResponse(true, positiveQuote, id);
	}

	protected ApiResponse getOpFailResponse(Exception exception) {
		String message = exception.toString();
		return new ApiResponse(false, message);
	}

	protected ApiResponse getOpFailResponse(String message) {
		return new ApiResponse(false, message);
	}

	protected ApiResponse getPermFailResponse() {
		return new ApiResponse(false, "Permission denied");
	}
}
