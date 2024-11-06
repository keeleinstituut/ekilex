package eki.ekilex.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.FormWord;
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
	public List<Paradigm> getParadigms(
			@PathVariable("wordId") Long wordId,
			Authentication authentication,
			HttpServletRequest request) {

		List<Paradigm> paradigms = morphologyService.getParadigms(wordId);
		addRequestStat(authentication, request);
		return paradigms;
	}

	@Order(402)
	@GetMapping(API_SERVICES_URI + FORM_URI + SEARCH_URI + "/{form}")
	@ResponseBody
	public List<FormWord> formSearch(
			@PathVariable("form") String formValue,
			Authentication authentication,
			HttpServletRequest request) throws Exception {

		List<FormWord> formWords = morphologyService.getFormWords(formValue);
		addRequestStat(authentication, request);
		return formWords;
	}

	@Order(403)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + PARADIGM_URI + SAVE_URI)
	@ResponseBody
	public ApiResponse saveMorphology(
			@RequestBody ParadigmWrapper paradigmWrapper,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			morphologyService.save(paradigmWrapper);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

}
