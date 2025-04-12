package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.api.ApiStatService;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.service.util.MessageUtil;
import eki.ekilex.service.util.ValueUtil;

public abstract class AbstractApiController implements SystemConstant, ApiConstant, GlobalConstant {

	@Autowired
	private ApiStatService apiStatService;

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected ValueUtil valueUtil;

	@Autowired
	private MessageUtil messageUtil;

	protected ApiResponse getOpSuccessResponse(Authentication authentication, HttpServletRequest request) {
		addRequestStat(authentication, request);
		String positiveQuote = messageUtil.getPositiveQuote();
		return new ApiResponse(true, positiveQuote);
	}

	protected ApiResponse getOpSuccessResponse(Authentication authentication, HttpServletRequest request, String message) {
		addRequestStat(authentication, request);
		return new ApiResponse(true, message);
	}

	protected ApiResponse getOpSuccessResponse(Authentication authentication, HttpServletRequest request, String tableName, Long id) {
		addRequestStat(authentication, request);
		String positiveQuote = messageUtil.getPositiveQuote();
		return new ApiResponse(true, positiveQuote, tableName, id);
	}

	protected void addRequestStat(Authentication authentication, HttpServletRequest request) {
		String authName = authentication.getName();
		String servletPath = request.getServletPath();
		apiStatService.addRequest(authName, servletPath);
	}

	protected ApiResponse getOpFailResponse(Authentication authentication, HttpServletRequest request, Exception exception) {
		return getOpFailResponse(authentication, request, exception.getMessage());
	}

	protected ApiResponse getOpFailResponse(Authentication authentication, HttpServletRequest request, String message) {
		String authName = authentication.getName();
		String servletPath = request.getServletPath();
		apiStatService.addError(authName, servletPath, message);
		return new ApiResponse(false, message);
	}
}
