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
import eki.ekilex.service.MediaService;

@ConditionalOnWebApplication
@Controller
public class MediaController extends AbstractMutableDataPageController {

	@Autowired
	private MediaService mediaService;

	@PostMapping(UPLOAD_MEDIA_FILE_URI)
	@ResponseBody
	public MediaUploadResponse uploadMediaFile(@RequestParam("file") MultipartFile file) {

		return mediaService.uploadMediaFile(file);
	}

	@PostMapping(DELETE_MEDIA_FILE_URI)
	@ResponseBody
	public Response deleteMediaFile(@RequestParam("objectFilename") String objectFilename) {

		return mediaService.deleteMediaFile(objectFilename);
	}
}
