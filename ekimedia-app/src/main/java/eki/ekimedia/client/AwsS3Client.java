package eki.ekimedia.client;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eki.common.util.CodeGenerator;
import eki.ekimedia.data.MediaFileUpload;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Component
public class AwsS3Client implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AwsS3Client.class);

	@Value("${aws.access.key}")
	private String accessKey;

	@Value("${aws.secret.key}")
	private String secretKey;

	@Value("${aws.region.name}")
	private String regionName;

	@Value("${aws.bucket.name}")
	private String bucketName;

	private S3Client s3Client;

	@Override
	public void afterPropertiesSet() throws Exception {

		AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		s3Client = S3Client
				.builder()
				.region(Region.of(regionName))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.build();
	}

	public String upload(MediaFileUpload mediaFileUpload) {

		String origin = mediaFileUpload.getOrigin();
		String originalFilename = mediaFileUpload.getOriginalFilename();
		String filenameExt = mediaFileUpload.getFilenameExt();
		byte[] content = mediaFileUpload.getContent();

		originalFilename = removeAccentsAndRestrictedSymbols(originalFilename);
		String objectKey = CodeGenerator.generateUniqueId().toLowerCase();
		String objectFilename = objectKey + "." + filenameExt;
		RequestBody objectContentBody = RequestBody.fromBytes(content);
		String contentType = getContentType(filenameExt);

		Map<String, String> metadata = new HashMap<>();
		metadata.put("origin", origin);
		metadata.put("original_filename", originalFilename);
		metadata.put("filename_ext", filenameExt);

		PutObjectResponse putObjectResponse = s3Client.putObject(request -> request
				.bucket(bucketName)
				.key(objectFilename)
				.contentType(contentType)
				.metadata(metadata)
				.ifNoneMatch("*"),
				objectContentBody);

		String eTag = putObjectResponse.eTag();

		logger.info("Uploaded \"{}\" -> \"{}\", size: {} bytes, tag: {}",
				originalFilename, objectFilename, content.length, eTag);

		return objectFilename;
	}

	private String removeAccentsAndRestrictedSymbols(String originalFilename) {
		String cleanValue = Normalizer.normalize(originalFilename, Normalizer.Form.NFKD);
		cleanValue = RegExUtils.replaceAll(cleanValue, "\\p{M}", "");
		cleanValue = StringUtils.replaceChars(cleanValue, "?!#=&£$€@+,'\"", "x");
		return cleanValue;
	}

	public void delete(String objectFilename) {

		DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(request -> request
				.bucket(bucketName)
				.key(objectFilename));

		logger.info("Deleted \"{}\" with {}", objectFilename, deleteObjectResponse);
	}

	private String getContentType(String filenameExt) {

		if (StringUtils.equals(filenameExt, "svg")) {
			return "image/svg+xml";
		} else if (StringUtils.equals(filenameExt, "jpg")) {
			return MediaType.IMAGE_JPEG_VALUE;
		} else if (StringUtils.equals(filenameExt, "jpeg")) {
			return MediaType.IMAGE_JPEG_VALUE;
		} else if (StringUtils.equals(filenameExt, "png")) {
			return MediaType.IMAGE_PNG_VALUE;
		} else if (StringUtils.equals(filenameExt, "wav")) {
			return "audio/wav";
		} else if (StringUtils.equals(filenameExt, "mp3")) {
			return "audio/mpeg";
		} else if (StringUtils.equals(filenameExt, "mp4")) {
			return "video/mp4";
		}
		return MediaType.ALL_VALUE;
	}
}
