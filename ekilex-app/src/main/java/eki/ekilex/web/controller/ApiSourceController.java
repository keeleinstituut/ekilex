package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ApiResponse;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@RestController
public class ApiSourceController implements SystemConstant, ApiConstant {

	@Autowired
	private SourceService sourceService;

	@GetMapping(value = API_SERVICES_URI + "/create/source", params = {"sourceType", "sourceName", "sourceProperties"})
	@ResponseBody
	public ApiResponse createSource(
			@RequestParam("sourceType") SourceType sourceType,
			@RequestParam("sourceName") String sourceName,
			@RequestParam(name = "sourceProperties", required = false) List<SourceProperty> sourceProperties) {

		List<SourceProperty> completeSourceProperties = new ArrayList<>();

		SourceProperty name = new SourceProperty();
		name.setType(FreeformType.SOURCE_NAME);
		name.setValueText(sourceName);
		completeSourceProperties.add(name);

		if (CollectionUtils.isNotEmpty(sourceProperties)) {
			completeSourceProperties.addAll(sourceProperties);
		}

		Long sourceId = sourceService.createSource(sourceType, completeSourceProperties);
		return new ApiResponse(true, sourceId);
	}
}
