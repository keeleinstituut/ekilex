package eki.ekilex.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Origin;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.DatasetService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.util.ClassifierEditor;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class DatasetController extends AbstractPrivatePageController {

	private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private CommonDataService commonDataService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Classifier.class, new ClassifierEditor());
	}

	@ModelAttribute("languages")
	public List<Classifier> getLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("origins")
	public List<Origin> getOrigins() {
		return commonDataService.getDomainOrigins();
	}

	@GetMapping(DATASETS_URI)
	public String list(Model model) {

		List<Dataset> datasets = datasetService.getDatasets();
		Long userId = userContext.getUserId();
		List<DatasetPermission> datasetPermissions = permissionService.getUserDatasetPermissions(userId);
		List<String> ownedDataSetCodes = datasetPermissions.stream()
				.filter(permission -> AuthorityOperation.OWN.equals(permission.getAuthOperation()))
				.map(DatasetPermission::getDatasetCode).collect(Collectors.toList());

		model.addAttribute("ownedDatasetCodes", ownedDataSetCodes);
		model.addAttribute("datasets", datasets);
		model.addAttribute("datasetData", new Dataset());

		return DATASETS_PAGE;
	}

	@PreAuthorize("authentication.authenticated")
	@PostMapping(CREATE_DATASET_URI)
	public String createDataset(@Valid @ModelAttribute("datasetData") Dataset datasetFormData) {

		logger.debug("Creating dataset, name : {}", datasetFormData.getName());
		datasetService.createDataset(datasetFormData);

		Long userId = userContext.getUserId();
		permissionService.createDatasetPermission(userId, datasetFormData.getCode(), AuthorityItem.DATASET, AuthorityOperation.OWN, null);
		userService.updateUserSecurityContext();

		return REDIRECT_PREF + DATASETS_URI;
	}

	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist or authentication.principal.admin")
	@PostMapping(UPDATE_DATASET_URI)
	public String updateDataset(@Valid @ModelAttribute("datasetData") Dataset datasetFormData) {

		logger.debug("Updating dataset, name : {}", datasetFormData.getName());

		datasetService.updateDataset(datasetFormData);

		return REDIRECT_PREF + DATASETS_URI;
	}

	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist or authentication.principal.admin")
	@GetMapping(DELETE_DATASET_URI + "/{datasetCode}")
	@ResponseBody
	public String deleteDataset(@PathVariable("datasetCode") String datasetCode) {

		logger.debug("Deleting dataset, code: {}", datasetCode);

		datasetService.deleteDataset(datasetCode);
		userService.updateUserSecurityContext();

		return RESPONSE_OK_VER1;
	}

	@GetMapping(VALIDATE_CREATE_DATASET_URI + "/{datasetCode}")
	@ResponseBody
	public String validateCreateDataset(@PathVariable("datasetCode") String datasetCode) {

		if (datasetService.datasetCodeExists(datasetCode)) {
			logger.debug("Trying to create dataset with existing code '{}'.", datasetCode);
			return "CODE_EXISTS";
		}
		return RESPONSE_OK_VER1;
	}

	@GetMapping(ORIGIN_DOMAINS_URI + "/{originCode}")
	@ResponseBody
	public String getOriginDomains(@PathVariable String originCode) throws Exception {

		List<Classifier> originDomains = datasetService.getDomains(originCode);

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(originDomains);
	}

	@GetMapping(DATASET_URI + "/{datasetCode}")
	@ResponseBody
	public Dataset fetchDataset(@PathVariable String datasetCode) {
		logger.debug("Fetching dataset code {}", datasetCode);
		Dataset dataset = datasetService.getDataset(datasetCode);
		return dataset;
	}
}