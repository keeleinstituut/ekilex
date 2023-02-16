package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.service.core.UserContext;

public abstract class AbstractPublicPageController implements WebConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected UserService userService;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected MessageSource messageSource;

	@ModelAttribute("datasetsWithOwner")
	public List<Dataset> getDatasetsWithOwner() {
		return commonDataService.getVisibleDatasetsWithOwner();
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("userDatasetPermissions")
	public List<DatasetPermission> getUserDatasetPermissions() {
		Long userId = userContext.getUserId();
		return permissionService.getUserDatasetPermissions(userId);
	}
}
