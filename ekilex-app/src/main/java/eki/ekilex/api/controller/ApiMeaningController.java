package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.MeaningForum;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.CudService;

@ConditionalOnWebApplication
@RestController
public class ApiMeaningController extends AbstractApiController {

	@Autowired
	private CudService cudService;

	@Order(801)
	@PreAuthorize("principal.apiCrud")
	@PostMapping(API_SERVICES_URI + MEANING_FORUM_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningForum(@RequestBody MeaningForum meaningForum) {

		try {
			EkiUser user = userContext.getUser();
			Long meaningId = meaningForum.getMeaningId();
			String valuePrese = meaningForum.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.createMeaningForum(meaningId, valuePrese, user);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(802)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningForumCrudGranted(principal, #meaningForum)")
	@PostMapping(API_SERVICES_URI + MEANING_FORUM_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateMeaningForum(@RequestBody MeaningForum meaningForum) {

		try {
			EkiUser user = userContext.getUser();
			Long meaningForumId = meaningForum.getId();
			String valuePrese = meaningForum.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.updateMeaningForum(meaningForumId, valuePrese, user);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(803)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningForumCrudGranted(principal, #meaningForumId)")
	@PostMapping(API_SERVICES_URI + MEANING_FORUM_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteMeaningForum(@RequestParam("meaningForumId") Long meaningForumId) {

		try {
			cudService.deleteMeaningForum(meaningForumId);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

}
