package eki.ekilex.web.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.UserRole;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;

@Component
public class PermDataUtil {

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonDataService commonDataService;

	public List<Classifier> getUserPermLanguages(String datasetCode, SessionBean sessionBean) {
		EkiUser user = userService.getAuthenticatedUser();
		Long userId = user.getId();

		List<Classifier> userPermLanguages = permissionService.getUserDatasetLanguages(userId, datasetCode);
		List<Classifier> datasetLanguages = commonDataService.getDatasetLanguages(datasetCode);

		if (sessionBean != null && sessionBean.getUserRole() != null) {
			UserRole userRole = sessionBean.getUserRole();
			if (userRole.getDatasetPermission() != null && StringUtils.isNotBlank(userRole.getDatasetPermission().getAuthLang())) {
				datasetLanguages.removeIf(classifier -> !classifier.getCode().equals(userRole.getDatasetPermission().getAuthLang()));
			}
		}

		return userPermLanguages.stream().filter(datasetLanguages::contains).collect(Collectors.toList());
	}
}
