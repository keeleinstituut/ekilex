package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.ApiCreateSourceRequest;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@RestController
public class ApiSourceController implements SystemConstant, ApiConstant {

	@Autowired
	private SourceService sourceService;

	@GetMapping(value = API_SERVICES_URI + "/source/create")
	@ResponseBody
	public ApiResponse createSource(@RequestBody ApiCreateSourceRequest apiCreateSourceRequest) {

		SourceType sourceType = apiCreateSourceRequest.getSourceType();
		String sourceName = apiCreateSourceRequest.getSourceName();
		List<SourceProperty> sourceProperties = apiCreateSourceRequest.getSourceProperties();

		List<SourceProperty> completeSourceProperties = new ArrayList<>();

		SourceProperty name = new SourceProperty();
		name.setType(FreeformType.SOURCE_NAME);
		name.setValueText(sourceName);
		completeSourceProperties.add(name);

		if (CollectionUtils.isNotEmpty(sourceProperties)) {
			completeSourceProperties.addAll(sourceProperties);
		}

		try {
			Long sourceId = sourceService.createSource(sourceType, completeSourceProperties);
			return new ApiResponse(true, sourceId);
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@GetMapping(value = API_SERVICES_URI + "/source/join")
	@ResponseBody
	public ApiResponse joinSources(
			@RequestParam("sourceId1") Long sourceId1,
			@RequestParam("sourceId2") Long sourceId2) {

		try {
			sourceService.joinSources(sourceId1, sourceId2);
			return new ApiResponse(true);
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}
}
