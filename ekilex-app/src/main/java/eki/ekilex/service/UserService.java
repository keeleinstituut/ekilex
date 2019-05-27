package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.util.CodeGenerator;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserDbService;

@Component
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final int MIN_PASSWORD_LENGTH = 8;

	private UserDbService userDbService;

	private PermissionDbService permissionDbService;

	private CommonDataDbService commonDataDbService;

	public UserService(
			UserDbService userDbService,
			PermissionDbService permissionDbService,
			CommonDataDbService commonDataDbService) {
		this.userDbService = userDbService;
		this.permissionDbService = permissionDbService;
		this.commonDataDbService = commonDataDbService;
	}

	public boolean isAuthenticatedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean isAuthenticated = principal instanceof EkiUser;
		return isAuthenticated;
	}

	public EkiUser getAuthenticatedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EkiUser user;
		if (principal instanceof EkiUser) {
			user = (EkiUser) principal;
		} else {
			user = new EkiUser();
			user.setName(principal.toString());
		}
		return user;
	}

	@Transactional
	public EkiUser getUserByEmail(String email) {
		EkiUser user = userDbService.getUserByEmail(email);
		if (user != null) {
			Long userId = user.getId();
			List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
			boolean datasetPermissionsExist = CollectionUtils.isNotEmpty(datasetPermissions) || user.isAdmin();
			boolean datasetOwnershipExist;
			if (CollectionUtils.isNotEmpty(datasetPermissions)) {
				datasetOwnershipExist = datasetPermissions.stream().anyMatch(
						perm -> AuthorityItem.DATASET.equals(perm.getAuthItem()) && AuthorityOperation.OWN.equals(perm.getAuthOperation()));
			} else {
				datasetOwnershipExist = false;
			}
			datasetOwnershipExist = datasetOwnershipExist || user.isAdmin();
			user.setDatasetPermissions(datasetPermissions);
			user.setDatasetPermissionsExist(datasetPermissionsExist);
			user.setDatasetOwnershipExist(datasetOwnershipExist);
		}
		return user;
	}

	@Transactional
	public boolean isValidUser(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		EkiUser user = userDbService.getUserByEmail(email);
		return user == null;
	}

	@Transactional
	public void createUser(String email, String name, String password, String activationKey) {
		userDbService.createUser(email, name, password, activationKey);
		EkiUser user = userDbService.getUserByEmail(email);
		logger.debug("Created new user : {}", user.getDescription());
	}

	@Transactional
	public EkiUser activateUser(String activationKey) {
		EkiUser user = userDbService.activateUser(activationKey);
		return user;
	}

	@PreAuthorize("principal.admin")
	@Transactional
	public void setAdmin(Long userId, boolean isAdmin) {
		userDbService.setAdmin(userId, isAdmin);
	}

	@PreAuthorize("principal.admin")
	@Transactional
	public void enableUser(Long userId, boolean enable) {
		userDbService.enableUser(userId, enable);
	}

	public String generateUniqueKey() {
		return CodeGenerator.generateUniqueId();
	}

	public boolean isActiveUser(EkiUser user) {
		return user != null && StringUtils.isBlank(user.getActivationKey());
	}

	public boolean isValidPassword(String password, String password2) {
		return StringUtils.length(password) >= MIN_PASSWORD_LENGTH && StringUtils.equals(password, password2);
	}

	@Transactional
	public void submitUserApplication(Long userId, List<String> datasets, String comment) {
		String[] datasetArr = null;
		if (CollectionUtils.isNotEmpty(datasets)) {
			datasetArr = datasets.toArray(new String[datasets.size()]);
		}
		userDbService.createUserApplication(userId, datasetArr, comment);
	}

	@Transactional
	public List<EkiUserApplication> getUserApplications(Long userId) {
		List<EkiUserApplication> userApplications = userDbService.getUserApplications(userId);
		List<Dataset> allDatasets = commonDataDbService.getDatasets();
		for (EkiUserApplication userApplication : userApplications) {
			List<String> userApplicationDatasetCodes = userApplication.getDatasetCodes();
			if (CollectionUtils.isNotEmpty(userApplicationDatasetCodes)) {
				List<Dataset> userApplicationDatasets = new ArrayList<>();
				userApplication.setDatasets(userApplicationDatasets);
				for (Dataset dataset : allDatasets) {
					if (userApplicationDatasetCodes.contains(dataset.getCode())) {
						userApplicationDatasets.add(dataset);
					}
				}
			}
		}
		return userApplications;
	}

	@Transactional
	public void updateUserRecoveryKey(Long userId, String recoveryKey) {
		userDbService.updateUserRecoveryKey(userId, recoveryKey);
	}

	@Transactional
	public EkiUser changePassword(String recoveryKey, String encodedPassword) {
		EkiUser user = userDbService.changeUserPassword(recoveryKey, encodedPassword);
		return user;
	}
}
