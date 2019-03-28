package eki.ekilex.web.controller;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.UserContext;

public abstract class AbstractPageController implements WebConstant {

	@Autowired
	protected UserContext userContext;

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
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		return permissionService.getUserPermDatasets(userId);
	}

	@ModelAttribute("userOwnedDatasets")
	public List<Dataset> getUserOwnedDatasets() {
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		return permissionService.getUserOwnedDatasets(userId);
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

	//TODO handle with or wo dataset
	@ModelAttribute("userPermLanguages")
	public List<Classifier> getUserPermLanguages(@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {
		String newWordSelectedDataset = sessionBean.getNewWordSelectedDataset();
		if (StringUtils.isNotBlank(newWordSelectedDataset)) {
			EkiUser user = userContext.getUser();
			Long userId = user.getId();
			List<Classifier> userPermLanguages = permissionService.getUserDatasetLanguages(userId, newWordSelectedDataset);
			return userPermLanguages;
		}
		return Collections.emptyList();
	}
}
