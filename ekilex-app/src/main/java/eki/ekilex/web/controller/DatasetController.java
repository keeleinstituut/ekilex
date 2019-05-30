package eki.ekilex.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.DatasetService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
@PreAuthorize("authentication.principal.datasetPermissionsExist")
public class DatasetController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	@GetMapping(DICTIONARIES_URI)
	public String search(Model model) {

		logger.debug("Fetching all datasets");
		List<Dataset> datasets = datasetService.getDatasets();

		model.addAttribute("datasets", datasets);

		return DATASETS_PAGE;
	}

	@PostMapping(CREATE_DICTIONARY_URI)
	public String createDataset(@Valid @ModelAttribute("datasetData") Dataset datasetFormData) {
		logger.debug("Creating dataset, name : {}", datasetFormData.getName());
		datasetService.createDataset(datasetFormData);

		EkiUser currentUser = userService.getAuthenticatedUser();
		permissionService.createDatasetPermission(currentUser.getId(), datasetFormData.getCode(), AuthorityItem.DATASET, AuthorityOperation.OWN, null);

		return REDIRECT_PREF + DICTIONARIES_URI;
	}

	@PostMapping(UPDATE_DICTIONARY_URI)
	public String updateDataSet(@Valid @ModelAttribute("datasetData") Dataset datasetFormData) {
		logger.debug("Updating dataset, name : {}", datasetFormData.getName());
		datasetService.updateDataset(datasetFormData);

		return REDIRECT_PREF + DICTIONARIES_URI;
	}

	@GetMapping(DELETE_DICTIONARY_URI + "/{datasetCode}")
	@ResponseBody
	public String deleteDataset(@PathVariable("datasetCode") String datasetCode) throws JsonProcessingException {

		logger.debug("Deleting dataset, code: {}", datasetCode);

		Map<String, String> response = new HashMap<>();

		// if (sourceService.validateSourceDelete(sourceId)) {
		// 	response.put("status", "ok");
		// } else {
		// 	response.put("status", "invalid");
		// 	response.put("message", "Allikat ei saa kustutada, sest sellele on viidatud.");
		// }

		datasetService.deleteDataset(datasetCode);
		response.put("status", "ok");

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@GetMapping(REST_SERVICES_URI + VALIDATE_CREATE_DICTIONARY_URI + "/{datasetCode}")
	@ResponseBody
	public String validateCreateDataset(@PathVariable("datasetCode") String datasetCode) {
		if (datasetService.datasetCodeExists(datasetCode)) {
			logger.debug("Trying to create dataset with existing code '{}'.", datasetCode);
			return "code_exists";
		}
		return "ok";
	}
}
