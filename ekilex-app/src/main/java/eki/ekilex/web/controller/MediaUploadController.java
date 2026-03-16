package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eki.ekilex.data.MediaUploadResponse;
import eki.ekilex.data.Response;
import eki.ekilex.service.MediaUploadService;

@ConditionalOnWebApplication
@Controller
public class MediaUploadController extends AbstractMutableDataPageController {

	@Autowired
	private MediaUploadService mediaUploadService;

	@PostMapping(UPLOAD_MEDIA_FILE_URI)
	@ResponseBody
	public MediaUploadResponse uploadMediaFile(@RequestParam("file") MultipartFile file) {

		return mediaUploadService.uploadMediaFile(file);
	}

	@PostMapping(DELETE_MEDIA_FILE_URI)
	@ResponseBody
	public Response deleteMediaFile(@RequestParam("objectFilename") String objectFilename) {

		return mediaUploadService.deleteMediaFile(objectFilename);
	}
}
