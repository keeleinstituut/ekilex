package eki.ekilex.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.Paradigm;
import eki.ekilex.data.api.ParadigmWrapper;
import eki.ekilex.service.api.MorphologyService;

@ConditionalOnWebApplication
@RestController
public class ApiMorphController extends AbstractApiController {

	@Autowired
	private MorphologyService morphologyService;

	@Order(401)
	@GetMapping(API_SERVICES_URI + PARADIGM_URI + DETAILS_URI + "/{wordId}")
	public List<Paradigm> getParadigms(@PathVariable("wordId") Long wordId) {

		return morphologyService.getParadigms(wordId);
	}

	@Order(402)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + PARADIGM_URI + REPLACE_URI)
	@ResponseBody
	public ApiResponse replaceMorphology(@RequestBody ParadigmWrapper paradigmWrapper) {

		try {
			morphologyService.replace(paradigmWrapper);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

}
