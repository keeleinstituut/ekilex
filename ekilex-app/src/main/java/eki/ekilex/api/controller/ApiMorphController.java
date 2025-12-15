package eki.ekilex.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.api.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.service.api.MorphologyService;
@Tag(name = "Morphology")
@ConditionalOnWebApplication
@RestController
public class ApiMorphController extends AbstractApiController {

	@Autowired
	private MorphologyService morphologyService;

	@Operation(
			summary = "Get all available morphological paradigms for the queried word",
			description = "Retrieves... ",
			parameters = {
					@io.swagger.v3.oas.annotations.Parameter(
							name = "wordId",
							description = "The unique identifier of the word to retrieve paradigms for",
							required = true,
							example = "182736",
							schema = @Schema(type = "integer", format = "int64")
					)
			},
			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							description = "Word paradigms successfully retrieved.",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = eki.ekilex.data.Paradigm.class))
							)
					)
			})
	@Order(401)
	@GetMapping(API_SERVICES_URI + PARADIGM_URI + DETAILS_URI + "/{wordId}")
	@ResponseBody
	public List<Paradigm> getParadigms(
			@PathVariable("wordId") Long wordId,
			Authentication authentication,
			HttpServletRequest request) {

		List<Paradigm> paradigms = morphologyService.getParadigms(wordId);
		addRequestStat(authentication, request);
		return paradigms;
	}
	@Operation(
			summary = "Search for base word forms from inflected word forms",
			description = "Searches for the base word form (lemma) of a given inflected word form. " +
					"Returns all possible base forms if multiple matches exist  \n" +
					"**Example:**\n" +
					"The form 'läks' (Estonian past tense form) returns the base form 'minema' (to go).\n\n",
			parameters = {
					@io.swagger.v3.oas.annotations.Parameter(
							name = "form",
							description = "The word form to analyze. Can be any inflected form in Estonian (conjugated verb, declined noun, etc.) " +
									"The endpoint will perform morphological analysis to find the base form(s).",
							required = true,
							example = "läks",
							schema = @Schema(type = "string")
					)
			},
			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							description = "Base form(s) successfully retrieved for the given word form.",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(
											implementation = FormWord.class,
											description = "List of base word forms matching the input form"
									))
							)
					)
			}
	)
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
