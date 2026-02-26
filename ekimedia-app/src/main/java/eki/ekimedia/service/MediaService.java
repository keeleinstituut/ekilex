package eki.ekimedia.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.data.MediaFileContent;
import eki.common.data.MediaFileRef;
import eki.common.data.ValidationResult;
import eki.common.exception.ApiException;
import eki.ekimedia.client.AwsS3Client;
import eki.ekimedia.constant.SystemConstant;
import eki.ekimedia.data.MediaFile;
import eki.ekimedia.data.MediaFileUpload;
import eki.ekimedia.service.db.MediaDbService;

@Component
public class MediaService implements InitializingBean, SystemConstant {

	private static final String[] SUPPORTED_MEDIA_FILE_EXT = {"jpg", "jpeg", "png", "svg", "wav", "mp3", "mp4"};

	@Value("${aws.public.url}")
	private String awsPublicUrl;

	@Autowired
	private AwsS3Client awsS3Client;

	@Autowired
	private MediaDbService mediaDbService;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.endsWith(awsPublicUrl, "/")) {
			awsPublicUrl = awsPublicUrl + "/";
		}
	}

	public ValidationResult validate(MediaFileContent mediaFileContent) {

		if (mediaFileContent == null) {
			return new ValidationResult(false, "Missing media file");
		}
		String filename = mediaFileContent.getFilename();
		byte[] content = mediaFileContent.getContent();

		if (StringUtils.isBlank(filename)) {
			return new ValidationResult(false, "Missing filename");
		}
		String filenameExt = StringUtils.substringAfterLast(filename, ".");
		if (StringUtils.isBlank(filenameExt)) {
			return new ValidationResult(false, "Missing file extension");
		}
		filenameExt = filenameExt.toLowerCase();
		if (!ArrayUtils.contains(SUPPORTED_MEDIA_FILE_EXT, filenameExt)) {
			return new ValidationResult(false, "Unsupported file extension");
		}
		if (content == null) {
			return new ValidationResult(false, "Missing file content");
		}
		if (content.length == 0) {
			return new ValidationResult(false, "Missing file content");
		}
		return new ValidationResult(true);
	}

	@Transactional(rollbackFor = Exception.class)
	public MediaFileRef createMediaFile(String origin, MediaFileContent mediaFileContent) {

		String originalFilename = mediaFileContent.getFilename();
		originalFilename = StringUtils.trim(originalFilename);
		originalFilename = StringUtils.lowerCase(originalFilename);
		String filenameExt = StringUtils.substringAfterLast(originalFilename, ".");
		byte[] content = mediaFileContent.getContent();

		MediaFileUpload mediaFileUpload = new MediaFileUpload();
		mediaFileUpload.setOrigin(origin);
		mediaFileUpload.setOriginalFilename(originalFilename);
		mediaFileUpload.setFilenameExt(filenameExt);
		mediaFileUpload.setContent(content);

		String objectFilename = awsS3Client.upload(mediaFileUpload);
		String url = awsPublicUrl + objectFilename;

		MediaFile mediaFile = new MediaFile();
		mediaFile.setOrigin(origin);
		mediaFile.setOriginalFilename(originalFilename);
		mediaFile.setObjectFilename(objectFilename);
		mediaFile.setFilenameExt(filenameExt);

		Long mediaFileId = mediaDbService.createMediaFile(mediaFile);

		MediaFileRef mediaFileRef = new MediaFileRef();
		mediaFileRef.setId(mediaFileId);
		mediaFileRef.setFilename(objectFilename);
		mediaFileRef.setUrl(url);

		return mediaFileRef;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMediaFile(String objectFilename) throws Exception {

		boolean mediaFileExists = mediaDbService.mediaFileExistsByObjectFilename(objectFilename);
		if (!mediaFileExists) {
			throw new ApiException("No such media file exists: " + objectFilename);
		}

		awsS3Client.delete(objectFilename);
		mediaDbService.deleteMediaFileByObjectFilename(objectFilename);
	}
}
