package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@Tag(name = "Lexicography", description = "Operations with lex-words")
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
			@PathVariable("datasetCode") String datasetCode,
			Authentication authentication,
			HttpServletRequest request) {

		LexWord lexWord = lexWordService.getLexWord(wordId, datasetCode);
		addRequestStat(authentication, request);
		return lexWord;
	}

	@Order(861)
	@PreAuthorize("principal.apiCrud "
			+ "&& @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #lexWord.datasetCode) "
			+ "&& @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #lexWord.wordId)")
	@PostMapping(API_SERVICES_URI + LEX_WORD_URI + SAVE_URI)
	@ResponseBody
	public ApiResponse saveLexWord(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexWord lexWord,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long wordId = lexWordService.saveLexWord(lexWord, crudRoleDataset);
			return getOpSuccessResponse(authentication, request, "WORD", wordId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

}
