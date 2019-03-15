package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserDbService;

@Component
public class PermissionService {

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Transactional
	public List<EkiUserPermData> getEkiUserPermissions() {

		List<EkiUserPermData> users = permissionDbService.getUsers();
		for (EkiUserPermData user : users) {
			Long userId = user.getId();
			List<EkiUserApplication> userApplications = userDbService.getUserApplications(userId);
			List<DatasetPermission> datasetPermissions = permissionDbService.getUserDatasetPermissions(userId);
			user.setApplications(userApplications);
			user.setDatasetPermissions(datasetPermissions);
		}
		return users;
	}

	@PreAuthorize("principal.admin")
	@Transactional
	public void createDatasetPermission(Long userId, String datasetCode, AuthorityItem authItem, AuthorityOperation authOp, String authLang) {

		if (StringUtils.isBlank(datasetCode)) {
			return;
		}
		if (authItem == null) {
			return;
		}
		if (authOp == null) {
			return;
		}
		permissionDbService.createDatasetPermission(userId, datasetCode, authItem, authOp, authLang);
	}

	@PreAuthorize("principal.admin")
	@Transactional
	public void deleteDatasetPermission(Long datasetPermissionId) {

		permissionDbService.deleteDatasetPermission(datasetPermissionId);
	}
}
