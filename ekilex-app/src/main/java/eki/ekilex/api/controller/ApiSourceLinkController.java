package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.SourceLink;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.SourceLinkService;
import eki.ekilex.web.util.ValueUtil;

@ConditionalOnWebApplication
@RestController
public class ApiSourceLinkController extends AbstractApiController {

	@Autowired
	private SourceLinkService sourceLinkService;

	@Autowired
	private ValueUtil valueUtil;

	//TODO perm grants
	@Order(301)
	@GetMapping(value = API_SERVICES_URI + SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSourceLink(@RequestBody SourceLink sourceLink) {

		try {
			String name = sourceLink.getName();
			name = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(name);
			sourceLink.setName(name);
			String value = sourceLink.getValue();
			value = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(value);
			sourceLink.setValue(value);
			Long sourceLinkId = sourceLinkService.createSourceLink(sourceLink);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpPositiveResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
