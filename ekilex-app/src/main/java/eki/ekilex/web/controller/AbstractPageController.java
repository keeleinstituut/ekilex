package eki.ekilex.web.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.common.constant.TextDecoration;
import eki.common.data.CodeValue;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.PermDataUtil;

public abstract class AbstractPageController implements WebConstant {

	@Autowired
	protected UserService userService;

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected PermDataUtil permDataUtil;

	protected SessionBean getSessionBean(Model model) {
		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
			model.addAttribute(SESSION_BEAN, sessionBean);
		}
		return sessionBean;
	}

	@ModelAttribute("allDatasets")
	public List<Dataset> getAllDatasets() {
		return commonDataService.getDatasets();
	}

	@ModelAttribute("userRoleLanguages")
	public List<Classifier> getUserRoleLanguages(@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		if (sessionBean == null) {
			return Collections.emptyList();
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return Collections.emptyList();
		}
		String datasetCode = userRole.getDatasetCode();
		String authLang = userRole.getAuthLang();

		EkiUser user = userService.getAuthenticatedUser();
		Long userId = user.getId();

		List<Classifier> userPermLanguages = permissionService.getUserDatasetLanguages(userId, datasetCode);
		List<Classifier> datasetLanguages = commonDataService.getDatasetLanguages(datasetCode);

		if (StringUtils.isNotBlank(authLang)) {
			datasetLanguages.removeIf(classifier -> !StringUtils.equals(classifier.getCode(), authLang));
		}

		return userPermLanguages.stream().filter(datasetLanguages::contains).collect(Collectors.toList());
	}

	@ModelAttribute("userOwnedDatasets")
	public List<Dataset> getUserOwnedDatasets() {
		EkiUser user = userService.getAuthenticatedUser();
		Long userId = user.getId();
		return permissionService.getUserOwnedDatasets(userId);
	}

	@ModelAttribute("userDatasetPermissions")
	public List<DatasetPermission> getUserDatasetPermissions() {
		EkiUser user = userService.getAuthenticatedUser();
		Long userId = user.getId();
		return permissionService.getUserDatasetPermissions(userId);
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("ekiMarkupElements")
	public CodeValue[] getEkiMarkupElements() {
		return TextDecoration.EKI_MARKUP_ELEMENTS;
	}

	@ModelAttribute("sourceTypes")
	public List<SourceType> getSourceTypes() {
		return Arrays.asList(SourceType.class.getEnumConstants());
	}

	@ModelAttribute("sourcePropertyTypes")
	public List<FreeformType> getSourcePropertyTypes() {

		return Arrays.asList(FreeformType.SOURCE_AUTHOR, FreeformType.SOURCE_NAME, FreeformType.EXTERNAL_SOURCE_ID, FreeformType.PUBLIC_NOTE,
				FreeformType.SOURCE_ISBN, FreeformType.SOURCE_ISSN, FreeformType.SOURCE_WWW, FreeformType.SOURCE_FILE, FreeformType.SOURCE_PUBLISHER,
				FreeformType.SOURCE_PUBLICATION_NAME, FreeformType.SOURCE_PUBLICATION_PLACE, FreeformType.SOURCE_PUBLICATION_YEAR, FreeformType.SOURCE_CELEX,
				FreeformType.SOURCE_RT);
	}

	@ModelAttribute("complexities")
	public List<Complexity> getComplexities() {
		return Arrays.asList(Complexity.values());
	}
}
