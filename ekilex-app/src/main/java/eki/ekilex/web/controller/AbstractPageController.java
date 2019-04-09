package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.common.constant.TextDecoration;
import eki.common.data.CodeValue;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;

public abstract class AbstractPageController implements WebConstant {

	@Autowired
	protected UserService userService;

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected PermissionService permissionService;

	@ModelAttribute("allDatasets")
	public List<Dataset> getAllDatasets() {
		return commonDataService.getDatasets();
	}

	@ModelAttribute("userPermDatasets")
	public List<Dataset> getUserPermDatasets() {
		EkiUser user = userService.getLoggedInUser();
		Long userId = user.getId();
		return permissionService.getUserPermDatasets(userId);
	}

	@ModelAttribute("userOwnedDatasets")
	public List<Dataset> getUserOwnedDatasets() {
		EkiUser user = userService.getLoggedInUser();
		Long userId = user.getId();
		return permissionService.getUserOwnedDatasets(userId);
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("ekiMarkupElements")
	public CodeValue[] getEkiMarkupElements() {
		return TextDecoration.EKI_MARKUP_ELEMENTS;
	}
}
