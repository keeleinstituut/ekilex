package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.LexWord;
import eki.ekilex.service.api.LexWordService;

@ConditionalOnWebApplication
@RestController
public class ApiLexWordController extends AbstractApiController {

	@Autowired
	private LexWordService lexWordService;

	@Order(860)
	@PreAuthorize("@permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #datasetCode)")
	@GetMapping(API_SERVICES_URI + LEX_WORD_URI + DETAILS_URI + "/{wordId}/{datasetCode}")
	@ResponseBody
	public LexWord getLexWord(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@PathVariable("wordId") Long wordId,
			@PathVariable("datasetCode") String datasetCode) {

		return lexWordService.getLexWord(wordId, datasetCode);
	}

	@Order(861)
	@PreAuthorize("principal.apiCrud "
			+ "&& @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #lexWord.datasetCode) "
			+ "&& @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #lexWord.wordId)")
	@PostMapping(API_SERVICES_URI + LEX_WORD_URI + SAVE_URI)
	@ResponseBody
	public ApiResponse saveLexWord(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexWord lexWord) {

		try {
			Long wordId = lexWordService.saveLexWord(lexWord, crudRoleDataset);
			return getOpSuccessResponse(wordId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

}
