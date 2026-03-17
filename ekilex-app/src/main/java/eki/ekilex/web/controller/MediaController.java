package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.MeaningMediaRequest;
import eki.ekilex.data.MediaUploadResponse;
import eki.ekilex.data.Response;
import eki.ekilex.service.MediaService;
import eki.ekilex.web.bean.SessionBean;

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

	@PostMapping(CREATE_MEANING_IMAGE_URI)
	@ResponseBody
	public Response createMeaningImage(
			@RequestBody MeaningMediaRequest meaningMediaRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		EkiUser user = userContext.getUser();
		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = mediaService.createMeaningImage(meaningMediaRequest, user, roleDatasetCode, isManualEventOnUpdateEnabled);
		sessionBean.setMeaningImageExpanded(true);
		return response;
	}

	@PostMapping(CREATE_MEANING_MEDIA_URI)
	@ResponseBody
	public Response createMeaningMedia(
			@RequestBody MeaningMediaRequest meaningMediaRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		EkiUser user = userContext.getUser();
		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = mediaService.createMeaningMedia(meaningMediaRequest, user, roleDatasetCode, isManualEventOnUpdateEnabled);
		sessionBean.setMeaningMediaExpanded(true);
		return response;
	}

	@PostMapping(UPDATE_MEANING_IMAGE_URI)
	@ResponseBody
	public Response updateMeaningImage(
			@RequestBody MeaningMediaRequest meaningMediaRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = mediaService.updateMeaningImage(meaningMediaRequest, roleDatasetCode, isManualEventOnUpdateEnabled);
		return response;
	}

	@PostMapping(UPDATE_MEANING_MEDIA_URI)
	@ResponseBody
	public Response updateMeaningMedia(
			@RequestBody MeaningMediaRequest meaningMediaRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = mediaService.updateMeaningMedia(meaningMediaRequest, roleDatasetCode, isManualEventOnUpdateEnabled);
		return response;
	}
}
