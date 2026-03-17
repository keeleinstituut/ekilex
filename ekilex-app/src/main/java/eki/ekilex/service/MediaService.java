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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.MediaFileContent;
import eki.common.data.MediaFileRef;
import eki.ekilex.client.EkimediaClient;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.MeaningImage;
import eki.ekilex.data.MeaningMedia;
import eki.ekilex.data.MeaningMediaRequest;
import eki.ekilex.data.MediaUploadResponse;
import eki.ekilex.data.Response;

@Component
public class MediaService extends AbstractCudService {

	private static final Logger logger = LoggerFactory.getLogger(MediaService.class);

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

		if (StringUtils.isBlank(objectFilename)) {
			logger.error("Missing object filename when deleting media file");
			String message = messageSource.getMessage("media.upload.error.common.delete", new Object[0], locale);
			return composeResponse(ResponseStatus.ERROR, message);
		}

		try {
			ekimediaClient.deleteMediaFile(objectFilename);
			logger.debug("Deleted media file: {}", objectFilename);
			return composeResponse(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Failed to delete media file", e);
			String message = messageSource.getMessage("media.upload.error.common.delete", new Object[0], locale);
			return composeResponse(ResponseStatus.ERROR, message);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Response createMeaningImage(MeaningMediaRequest meaningMediaRequest, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		Long meaningId = meaningMediaRequest.getMeaningId();
		String url = meaningMediaRequest.getUrl();
		String title = meaningMediaRequest.getTitle();
		String objectFilename = meaningMediaRequest.getObjectFilename();

		if (StringUtils.isBlank(url)) {
			return composeResponse(ResponseStatus.ERROR);
		}

		MeaningImage meaningImage = new MeaningImage();
		meaningImage.setTitle(title);
		meaningImage.setUrl(url);
		meaningImage.setObjectFilename(objectFilename);
		applyCreateUpdate(meaningImage);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningImageId = cudDbService.createMeaningImage(meaningId, meaningImage);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_IMAGE, meaningImageId);
		activityLogService.createActivityLog(activityLog, meaningImageId, ActivityEntity.MEANING_IMAGE);

		String message = messageSource.getMessage("common.create.success", new Object[0], locale);
		return composeResponse(ResponseStatus.OK, message);
	}

	@Transactional(rollbackFor = Exception.class)
	public Response createMeaningMedia(MeaningMediaRequest meaningMediaRequest, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		Long meaningId = meaningMediaRequest.getMeaningId();
		String url = meaningMediaRequest.getUrl();
		String title = meaningMediaRequest.getTitle();
		String objectFilename = meaningMediaRequest.getObjectFilename();

		if (StringUtils.isBlank(url)) {
			return composeResponse(ResponseStatus.ERROR);
		}

		MeaningMedia meaningMedia = new MeaningMedia();
		meaningMedia.setTitle(title);
		meaningMedia.setUrl(url);
		meaningMedia.setObjectFilename(objectFilename);
		applyCreateUpdate(meaningMedia);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningMediaId = cudDbService.createMeaningMedia(meaningId, meaningMedia);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_MEDIA, meaningMediaId);
		activityLogService.createActivityLog(activityLog, meaningMediaId, ActivityEntity.MEANING_MEDIA);

		String message = messageSource.getMessage("common.create.success", new Object[0], locale);
		return composeResponse(ResponseStatus.OK, message);
	}

	@Transactional(rollbackFor = Exception.class)
	public Response updateMeaningImage(MeaningMediaRequest meaningMediaRequest, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		Long meaningImageId = meaningMediaRequest.getMeaningImageId();
		String url = meaningMediaRequest.getUrl();
		String title = meaningMediaRequest.getTitle();

		if (StringUtils.isBlank(url)) {
			return composeResponse(ResponseStatus.ERROR);
		}

		MeaningImage meaningImage = new MeaningImage();
		meaningImage.setTitle(title);
		meaningImage.setUrl(url);
		applyUpdate(meaningImage);

		Long meaningId = activityLogService.getActivityOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningImage(meaningImageId, meaningImage);
		activityLogService.createActivityLog(activityLog, meaningImageId, ActivityEntity.MEANING_IMAGE);

		String message = messageSource.getMessage("common.update.success", new Object[0], locale);
		return composeResponse(ResponseStatus.OK, message);
	}

	@Transactional(rollbackFor = Exception.class)
	public Response updateMeaningMedia(MeaningMediaRequest meaningMediaRequest, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		Long meaningMediaId = meaningMediaRequest.getMeaningMediaId();
		String url = meaningMediaRequest.getUrl();
		String title = meaningMediaRequest.getTitle();

		if (StringUtils.isBlank(url)) {
			return composeResponse(ResponseStatus.ERROR);
		}

		MeaningMedia meaningMedia = new MeaningMedia();
		meaningMedia.setTitle(title);
		meaningMedia.setUrl(url);
		applyUpdate(meaningMedia);

		Long meaningId = activityLogService.getActivityOwnerId(meaningMediaId, ActivityEntity.MEANING_MEDIA);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningMedia(meaningMediaId, meaningMedia);
		activityLogService.createActivityLog(activityLog, meaningMediaId, ActivityEntity.MEANING_MEDIA);

		String message = messageSource.getMessage("common.update.success", new Object[0], locale);
		return composeResponse(ResponseStatus.OK, message);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMeaningImage(Long meaningImageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String objectFilename = lookupDbService.getMeaningImageObjectFilename(meaningImageId);
		if (StringUtils.isNotBlank(objectFilename)) {
			ekimediaClient.deleteMediaFile(objectFilename);
		}

		Long meaningId = activityLogService.getActivityOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningImage(meaningImageId);
		activityLogService.createActivityLog(activityLog, meaningImageId, ActivityEntity.MEANING_IMAGE);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMeaningMedia(Long meaningMediaId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String objectFilename = lookupDbService.getMeaningMediaObjectFilename(meaningMediaId);
		if (StringUtils.isNotBlank(objectFilename)) {
			ekimediaClient.deleteMediaFile(objectFilename);
		}

		Long meaningId = activityLogService.getActivityOwnerId(meaningMediaId, ActivityEntity.MEANING_MEDIA);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningMedia(meaningMediaId);
		activityLogService.createActivityLog(activityLog, meaningMediaId, ActivityEntity.MEANING_MEDIA);
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

	private Response composeResponse(ResponseStatus responseStatus) {
		return composeResponse(responseStatus, null);
	}

	private Response composeResponse(ResponseStatus responseStatus, String message) {

		Response response = new Response();
		response.setStatus(responseStatus);
		response.setMessage(message);
		return response;
	}
}
