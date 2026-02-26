package eki.ekimedia.api.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.data.MediaFileContent;
import eki.common.data.MediaFileRef;
import eki.common.data.ValidationResult;
import eki.common.exception.ApiException;
import eki.ekimedia.constant.ApiConstant;
import eki.ekimedia.service.MediaService;

@ConditionalOnWebApplication
@RestController
public class ApiMediaController implements ApiConstant {

	private static final Logger logger = LoggerFactory.getLogger(ApiMediaController.class);

	@Autowired
	private MediaService mediaService;

	@PostMapping(value = API_SERVICES_URI + MEDIA_FILE_URI + CREATE_URI, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ResponseBody
	public MediaFileRef createMediaFile(
			@RequestPart("filename") String filename,
			@RequestPart("content") byte[] content,
			Principal principal) throws Exception {

		logger.debug("create media file. origin: \"{}\" filename: \"{}\" size: {}", principal.getName(), filename, content.length);

		MediaFileContent mediaFileContent = new MediaFileContent();
		mediaFileContent.setFilename(filename);
		mediaFileContent.setContent(content);

		ValidationResult validationResult = mediaService.validate(mediaFileContent);
		if (!validationResult.isValid()) {
			throw new ApiException(validationResult.getMessageKey());
		}
		String origin = principal.getName();
		return mediaService.createMediaFile(origin, mediaFileContent);
	}

	@PostMapping(value = API_SERVICES_URI + MEDIA_FILE_URI + DELETE_URI)
	@ResponseBody
	public String deleteMediaFile(
			@RequestParam("objectFilename") String objectFilename,
			Principal principal) throws Exception {

		logger.debug("delete media file. origin: \"{}\" filename: \"{}\"", principal.getName(), objectFilename);

		mediaService.deleteMediaFile(objectFilename);

		return RESPONSE_OK;
	}
}
