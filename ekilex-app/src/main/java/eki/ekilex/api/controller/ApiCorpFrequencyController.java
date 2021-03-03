package eki.ekilex.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
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

@ConditionalOnWebApplication
@RestController
public class ApiCorpFrequencyController extends AbstractApiController {

	@Autowired
	private CorpFrequencyService corpFrequencyService;

	@Order(501)
	@GetMapping(API_SERVICES_URI + FREQ_CORP_URI + SEARCH_URI)
	@ResponseBody
	public List<FreqCorpId> getFreqCorps() {
		return corpFrequencyService.getFreqCorps();
	}

	@Order(502)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + FREQ_CORP_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createFreqCorp(@RequestBody FreqCorp freqCorp) {
		try {
			Long freqCorpId = corpFrequencyService.createFreqCorp(freqCorp);
			return getOpSuccessResponse(freqCorpId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(503)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + FREQ_CORP_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateFreqCorp(@RequestBody FreqCorpId freqCorp) {
		try {
			corpFrequencyService.updateFreqCorp(freqCorp);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(504)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + FORM_FREQ_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createFormFreq(@RequestBody List<FormFreq> formFreqs) {
		try {
			corpFrequencyService.createFormFreqs(formFreqs);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(505)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + MORPH_FREQ_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMorphFreq(@RequestBody List<MorphFreq> morphFreqs) {
		try {
			corpFrequencyService.createMorphFreqs(morphFreqs);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(506)
	@PreAuthorize("principal.admin")
	@PostMapping(API_SERVICES_URI + WORD_FREQ_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordFreq(@RequestBody List<WordFreq> wordFreqs) {
		try {
			corpFrequencyService.createWordFreqs(wordFreqs);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
