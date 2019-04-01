package eki.ekilex.web.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.PermissionService;

@Component
public class PermDataUtil {

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	private UserContext userContext;

	public List<Classifier> getUserPermLanguages(String datasetCode) {
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		List<Classifier> userPermLanguages = permissionService.getUserDatasetLanguages(userId, datasetCode);
		return userPermLanguages;
	}
}
