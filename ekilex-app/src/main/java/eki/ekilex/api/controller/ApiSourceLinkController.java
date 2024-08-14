package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.ContentKey;
import eki.ekilex.data.DefinitionNoteSourceLink;
import eki.ekilex.data.DefinitionSourceLink;
import eki.ekilex.data.LexemeNoteSourceLink;
import eki.ekilex.data.LexemeSourceLink;
import eki.ekilex.data.MeaningImageSourceLink;
import eki.ekilex.data.MeaningNoteSourceLink;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.UsageSourceLink;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.SourceLinkService;

@ConditionalOnWebApplication
@RestController
public class ApiSourceLinkController extends AbstractApiController implements ContentKey {

	@Autowired
	private SourceLinkService sourceLinkService;

	@Order(301)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + DEFINITION_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + DEFINITION_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createDefinitionSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody DefinitionSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long definitionId = sourceLink.getDefinitionId();
			Long sourceLinkId = sourceLinkService.createDefinitionSourceLink(definitionId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(302)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + DEFINITION_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + DEFINITION_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteDefinitionSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteDefinitionSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(303)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + DEFINITION_NOTE_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + DEFINITION_NOTE_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createDefinitionNoteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody DefinitionNoteSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long definitionNoteId = sourceLink.getDefinitionNoteId();
			Long sourceLinkId = sourceLinkService.createDefinitionNoteSourceLink(definitionNoteId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(304)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + DEFINITION_NOTE_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + DEFINITION_NOTE_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteDefinitionNoteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteDefinitionNoteSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(305)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + LEXEME_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + LEXEME_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createLexemeSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexemeSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long lexemeId = sourceLink.getLexemeId();
			Long sourceLinkId = sourceLinkService.createLexemeSourceLink(lexemeId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(306)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + LEXEME_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + LEXEME_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteLexemeSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteLexemeSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(307)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + LEXEME_NOTE_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + LEXEME_NOTE_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createLexemeNoteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexemeNoteSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long lexemeNoteId = sourceLink.getLexemeNoteId();
			Long sourceLinkId = sourceLinkService.createLexemeNoteSourceLink(lexemeNoteId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(308)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + LEXEME_NOTE_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + LEXEME_NOTE_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteLexemeNoteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteLexemeNoteSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(309)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + USAGE_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + USAGE_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createUsageSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody UsageSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long usageId = sourceLink.getUsageId();
			Long sourceLinkId = sourceLinkService.createUsageSourceLink(usageId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(310)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + USAGE_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + USAGE_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteUsageSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteUsageSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(311)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + MEANING_IMAGE_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + MEANING_IMAGE_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningImageSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody MeaningImageSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long meaningImageId = sourceLink.getMeaningImageId();
			Long sourceLinkId = sourceLinkService.createMeaningImageSourceLink(meaningImageId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(312)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + MEANING_IMAGE_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + MEANING_IMAGE_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteMeaningImageSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteMeaningImageSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(313)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + MEANING_NOTE_SOURCE_LINK + "', #sourceLink)")
	@PostMapping(API_SERVICES_URI + MEANING_NOTE_SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningNoteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody MeaningNoteSourceLink sourceLink) {

		try {
			cleanSourceLinkName(sourceLink);
			Long meaningNoteId = sourceLink.getMeaningNoteId();
			Long sourceLinkId = sourceLinkService.createMeaningNoteSourceLink(meaningNoteId, sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(314)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, '" + MEANING_NOTE_SOURCE_LINK + "', #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + MEANING_NOTE_SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteMeaningNoteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteMeaningNoteSourceLink(sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	private void cleanSourceLinkName(SourceLink sourceLink) {
		String sourceLinkName = sourceLink.getName();
		sourceLinkName = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(sourceLinkName);
		sourceLink.setName(sourceLinkName);
	}
}
