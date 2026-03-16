package eki.ekilex.service;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import eki.common.data.MediaFileContent;
import eki.common.data.MediaFileRef;
import eki.ekilex.client.EkimediaClient;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.data.MediaUploadResponse;
import eki.ekilex.data.Response;

@Component
public class MediaUploadService {

	private static final Logger logger = LoggerFactory.getLogger(MediaUploadService.class);

	private static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "svg"};
	private static final String[] ALLOWED_VIDEO_EXTENSIONS = {"mp4"};
	private static final String[] ALLOWED_MEDIA_EXTENSIONS = ArrayUtils.addAll(ALLOWED_IMAGE_EXTENSIONS, ALLOWED_VIDEO_EXTENSIONS);

	private static final int MAX_MEDIA_FILE_SIZE_MB = 500;

	@Autowired
	private EkimediaClient ekimediaClient;

	@Autowired
	private MessageSource messageSource;

	public MediaUploadResponse uploadMediaFile(MultipartFile file) {

		Locale locale = LocaleContextHolder.getLocale();
		MediaUploadResponse response = validateMediaFile(file, locale);
		if (ResponseStatus.ERROR == response.getStatus()) {
			return response;
		}

		try {
			MediaFileContent mediaFileContent = new MediaFileContent();
			mediaFileContent.setFilename(file.getOriginalFilename());
			mediaFileContent.setContent(file.getBytes());

			MediaFileRef mediaFileRef = ekimediaClient.createMediaFile(mediaFileContent);
			logger.debug("Uploaded media file: {}", mediaFileRef.getUrl());

			response.setStatus(ResponseStatus.OK);
			response.setUrl(mediaFileRef.getUrl());
			response.setObjectFilename(mediaFileRef.getFilename());
			return response;
		} catch (Exception e) {
			logger.error("Failed to upload media file", e);
			response.setStatus(ResponseStatus.ERROR);
			String userMessage = messageSource.getMessage("media.upload.error.common.upload", new Object[0], locale);
			response.setMessage(userMessage);
			response.setDetailMessage(e.getMessage());
			return response;
		}
	}

	public Response deleteMediaFile(String objectFilename) {

		Locale locale = LocaleContextHolder.getLocale();
		Response response = new Response();

		if (StringUtils.isBlank(objectFilename)) {
			logger.error("Missing object filename when deleting media file");
			response.setStatus(ResponseStatus.ERROR);
			String message = messageSource.getMessage("media.upload.error.common.delete", new Object[0], locale);
			response.setMessage(message);
			return response;
		}

		try {
			ekimediaClient.deleteMediaFile(objectFilename);
			logger.debug("Deleted media file: {}", objectFilename);
			response.setStatus(ResponseStatus.OK);
			return response;
		} catch (Exception e) {
			logger.error("Failed to delete media file", e);
			response.setStatus(ResponseStatus.ERROR);
			String message = messageSource.getMessage("media.upload.error.common.delete", new Object[0], locale);
			response.setMessage(message);
			return response;
		}
	}

	private MediaUploadResponse validateMediaFile(MultipartFile file, Locale locale) {

		MediaUploadResponse response = new MediaUploadResponse();

		if (file == null || file.isEmpty()) {
			response.setStatus(ResponseStatus.ERROR);
			String userMessage = messageSource.getMessage("media.upload.error.missing.file", new Object[0], locale);
			response.setMessage(userMessage);
			return response;
		}

		String originalFilename = file.getOriginalFilename();
		if (StringUtils.isBlank(originalFilename)) {
			response.setStatus(ResponseStatus.ERROR);
			String userMessage = messageSource.getMessage("media.upload.error.missing.filename", new Object[0], locale);
			response.setMessage(userMessage);
			return response;
		}

		String filenameExt = StringUtils.substringAfterLast(originalFilename, ".");
		if (StringUtils.isBlank(filenameExt)) {
			response.setStatus(ResponseStatus.ERROR);
			String userMessage = messageSource.getMessage("media.upload.error.missing.file.extension", new Object[0], locale);
			response.setMessage(userMessage);
			return response;
		}

		filenameExt = filenameExt.toLowerCase();
		if (!ArrayUtils.contains(ALLOWED_MEDIA_EXTENSIONS, filenameExt)) {
			response.setStatus(ResponseStatus.ERROR);
			String[] messageArgs = new String[] {StringUtils.join(ALLOWED_MEDIA_EXTENSIONS, ", ")};
			String userMessage = messageSource.getMessage("media.upload.error.unsupported.file.extension", messageArgs, locale);
			response.setMessage(userMessage);
			return response;
		}

		if (file.getSize() > MAX_MEDIA_FILE_SIZE_MB * 1024 * 1024) {
			response.setStatus(ResponseStatus.ERROR);
			String[] messageArgs = new String[] {String.valueOf(MAX_MEDIA_FILE_SIZE_MB)};
			String userMessage = messageSource.getMessage("media.upload.error.file.size.exceeded", messageArgs, locale);
			response.setMessage(userMessage);
			return response;
		}

		response.setStatus(ResponseStatus.OK);
		return response;
	}
}
