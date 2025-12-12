package eki.ekilex.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.FormFreq;
import eki.ekilex.data.api.FreqCorp;
import eki.ekilex.data.api.FreqCorpId;
import eki.ekilex.data.api.MorphFreq;
import eki.ekilex.data.api.WordFreq;
import eki.ekilex.service.api.CorpFrequencyService;
@Tag(name = "Corp Frequency")
@ConditionalOnWebApplication
@RestController
public class ApiCorpFrequencyController extends AbstractApiController {

	@Autowired
	private CorpFrequencyService corpFrequencyService;

	@Order(501)
	@GetMapping(API_SERVICES_URI + FREQ_CORP_URI + SEARCH_URI)
	@ResponseBody
	public List<FreqCorpId> getFreqCorps(Authentication authentication, HttpServletRequest request) {
		List<FreqCorpId> freqCorps = corpFrequencyService.getFreqCorps();
		addRequestStat(authentication, request);
		return freqCorps;
	}

	@Order(502)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + FREQ_CORP_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createFreqCorp(
			@RequestBody FreqCorp freqCorp,
			Authentication authentication,
			HttpServletRequest request) {
		try {
			Long freqCorpId = corpFrequencyService.createFreqCorp(freqCorp);
			return getOpSuccessResponse(authentication, request, "FREQ_CORP", freqCorpId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(503)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + FREQ_CORP_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateFreqCorp(
			@RequestBody FreqCorpId freqCorp,
			Authentication authentication,
			HttpServletRequest request) {
		try {
			corpFrequencyService.updateFreqCorp(freqCorp);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(504)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + FORM_FREQ_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createFormFreq(
			@RequestBody List<FormFreq> formFreqs,
			Authentication authentication,
			HttpServletRequest request) {
		try {
			corpFrequencyService.createFormFreqs(formFreqs);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(505)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + MORPH_FREQ_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMorphFreq(
			@RequestBody List<MorphFreq> morphFreqs,
			Authentication authentication,
			HttpServletRequest request) {
		try {
			corpFrequencyService.createMorphFreqs(morphFreqs);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(506)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + WORD_FREQ_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordFreq(
			@RequestBody List<WordFreq> wordFreqs,
			Authentication authentication,
			HttpServletRequest request) {
		try {
			corpFrequencyService.createWordFreqs(wordFreqs);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}
}
