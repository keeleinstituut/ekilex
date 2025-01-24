package eki.ekilex.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.GlobalConstant;
import eki.common.util.CodeGenerator;
import eki.ekilex.constant.ApplicationStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserDbService;
import eki.ekilex.service.db.UserProfileDbService;

@Component
public class UserService implements WebConstant, GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final int ACCESS_TYPE_USER = 1;

	private static final int ACCESS_TYPE_API = 2;

	@Value("${ekilex.app.url:}")
	private String ekilexAppUrl;

	@Autowired
	private UserContext userContext;

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private UserProfileDbService userProfileDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private EkilexPasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	public void updateUserSecurityContext() {

		String userEmail = userContext.getUser().getEmail();

		if (StringUtils.isNotBlank(userEmail)) {
			EkiUser ekiUser = getUserByEmail(userEmail);
			userContext.updateUserSecurityContext(ekiUser);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUserEmail(String email) {

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		email = StringUtils.lowerCase(email);
		userDbService.updateUserEmail(userId, email);
		user = userDbService.getUserByEmail(email);
		applyPermissions(user, ACCESS_TYPE_USER);
		userContext.updateUserSecurityContext(user);
	}

	@Transactional
	public EkiUser getUserByEmail(String email) {

		if (StringUtils.isEmpty(email)) {
			return null;
		}
		email = StringUtils.lowerCase(email);
		EkiUser user = userDbService.getUserByEmail(email);
		applyPermissions(user, ACCESS_TYPE_USER);
		return user;
	}

	@Transactional
	public EkiUser getUserByApiKey(String apiKey) {

		if (StringUtils.isEmpty(apiKey)) {
			return null;
		}
		EkiUser user = userDbService.getUserByApiKey(apiKey);
		applyPermissions(user, ACCESS_TYPE_API);
		return user;
	}

	@Transactional
	public EkiUser getUserById(Long id) {

		EkiUser user = userDbService.getUserById(id);
		applyPermissions(user, ACCESS_TYPE_USER);
		return user;
	}

	private void applyPermissions(EkiUser user, int accessType) {

		if (user == null) {
			return;
		}
		if (user.getEnabled() == null) {
			return;
		}
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			return;
		}
		if (accessType == ACCESS_TYPE_API) {
			user.setName(user.getName() + " (API)");
		}
		Long userId = user.getId();
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		boolean datasetPermissionsExist;
		boolean datasetCrudPermissionsExist;
		boolean datasetOwnershipExist;
		boolean hasSingleDatasetPermission;
		if (CollectionUtils.isEmpty(datasetPermissions)) {
			datasetPermissionsExist = false;
			datasetCrudPermissionsExist = false;
			datasetOwnershipExist = false;
			hasSingleDatasetPermission = false;
		} else {
			datasetPermissionsExist = true;
			datasetCrudPermissionsExist = datasetPermissions.stream()
					.filter(perm -> AuthorityItem.DATASET.equals(perm.getAuthItem()))
					.anyMatch(perm -> AuthorityOperation.CRUD.equals(perm.getAuthOperation()) || AuthorityOperation.OWN.equals(perm.getAuthOperation()));
			datasetOwnershipExist = datasetPermissions.stream().anyMatch(
					perm -> AuthorityItem.DATASET.equals(perm.getAuthItem()) && AuthorityOperation.OWN.equals(perm.getAuthOperation()));
			hasSingleDatasetPermission = datasetPermissions.size() == 1;
		}
		DatasetPermission recentRole = resolveRecentRole(userId, datasetPermissions);

		user.setDatasetPermissions(datasetPermissions);
		user.setDatasetPermissionsExist(datasetPermissionsExist);
		user.setDatasetCrudPermissionsExist(datasetCrudPermissionsExist);
		user.setDatasetOwnershipExist(datasetOwnershipExist);
		user.setHasSingleDatasetPermission(hasSingleDatasetPermission);
		user.setRecentRole(recentRole);
	}

	private DatasetPermission resolveRecentRole(Long userId, List<DatasetPermission> userPermissions) {

		EkiUserProfile userProfile = userProfileDbService.getUserProfile(userId);
		if (userProfile == null) {
			return null;
		}
		if (CollectionUtils.isEmpty(userPermissions)) {
			return null;
		}
		Long recentDatasetPermissionId = userProfile.getRecentDatasetPermissionId();
		DatasetPermission recentRole = null;
		if (recentDatasetPermissionId == null) {
			if (userPermissions.size() == 1) {
				recentRole = userPermissions.get(0);
				userProfileDbService.setRecentDatasetPermission(userId, recentRole.getId());
			}
		} else {
			recentRole = userPermissions.stream()
					.filter(perm -> perm.getId().equals(recentDatasetPermissionId))
					.findFirst().orElse(null);
			if (recentRole == null) {
				userProfileDbService.setRecentDatasetPermission(userId, null);
			}
		}
		return recentRole;
	}

	@Transactional
	public String getUserEmailByRecoveryKey(String recoveryKey) {
		String email = userDbService.getUserEmailByRecoveryKey(recoveryKey);
		return email;
	}

	@Transactional
	public Long getUserIdByEmail(String email) {

		email = StringUtils.lowerCase(email);
		Long userId = userDbService.getUserIdByEmail(email);
		return userId;
	}

	@Transactional
	public boolean isValidUser(String email) {

		if (StringUtils.isBlank(email)) {
			return false;
		}
		email = StringUtils.lowerCase(email);
		EkiUser user = userDbService.getUserByEmail(email);
		return user == null;
	}

	@Transactional
	public boolean userExists(String email) {

		if (StringUtils.isBlank(email)) {
			return false;
		}
		email = StringUtils.lowerCase(email);
		EkiUser user = userDbService.getUserByEmail(email);
		return user != null;
	}

	@Transactional(rollbackOn = Exception.class)
	public String createUser(String email, String name, String password) {

		email = StringUtils.lowerCase(email);
		String activationKey = generateUniqueKey();
		String activationLink = ekilexAppUrl + REGISTER_PAGE_URI + ACTIVATE_PAGE_URI + "/" + activationKey;
		String encodedPassword = passwordEncoder.encode(password);
		String activeTermsVersion = userDbService.getActiveTermsVersion();
		Long userId = userDbService.createUser(email, name, encodedPassword, activationKey, activeTermsVersion);
		userProfileDbService.createUserProfile(userId);
		EkiUser user = userDbService.getUserByEmail(email);
		emailService.sendUserActivationEmail(email, activationLink);
		logger.debug("Created new user : {}", user.getDescription());
		return activationLink;
	}

	@Transactional(rollbackOn = Exception.class)
	public EkiUser activateUser(String activationKey) {
		EkiUser user = userDbService.activateUser(activationKey);
		return user;
	}

	@PreAuthorize("principal.admin")
	@Transactional(rollbackOn = Exception.class)
	public void setApiCrud(Long userId, boolean isApiCrud) {
		userDbService.setApiCrud(userId, isApiCrud);
	}

	@PreAuthorize("principal.admin")
	@Transactional(rollbackOn = Exception.class)
	public void setAdmin(Long userId, boolean isAdmin) {
		userDbService.setAdmin(userId, isAdmin);
	}

	@PreAuthorize("principal.admin")
	@Transactional(rollbackOn = Exception.class)
	public void setMaster(Long userId, boolean isMaster) {
		userDbService.setMaster(userId, isMaster);
	}

	@Transactional(rollbackOn = Exception.class)
	public void enableUser(Long userId, boolean enable) {
		userDbService.enableUser(userId, enable);
	}

	@Transactional(rollbackOn = Exception.class)
	public void enableUserWithTestAndLimitedDatasetPerm(Long userId) {

		userDbService.enableUser(userId, true);
		Long permissionId = permissionDbService.createDatasetPermission(userId, DATASET_TEST, AuthorityItem.DATASET, AuthorityOperation.CRUD, null);
		userProfileDbService.setRecentDatasetPermission(userId, permissionId);
		permissionDbService.createDatasetPermission(userId, DATASET_LIMITED, AuthorityItem.DATASET, AuthorityOperation.CRUD, null);
		updateUserSecurityContext();
	}

	@Transactional(rollbackOn = Exception.class)
	public void enableLimitedUser(Long userId) {

		userDbService.enableUser(userId, true);
		permissionDbService.createDatasetPermission(userId, DATASET_LIMITED, AuthorityItem.DATASET, AuthorityOperation.CRUD, null);
		EkiUserProfile userProfile = userProfileDbService.getUserProfile(userId);
		userProfile.setPreferredDatasets(Arrays.asList(DATASET_LIMITED));
		userProfileDbService.updateUserProfile(userProfile);
		updateUserSecurityContext();
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateReviewComment(Long userId, String reviewComment) {
		userDbService.updateReviewComment(userId, reviewComment);
	}

	@Transactional
	public String getActiveTermsValue() {
		return userDbService.getActiveTermsValue();
	}

	@Transactional(rollbackOn = Exception.class)
	public void agreeActiveTerms(Long userId) {
		userDbService.agreeActiveTerms(userId);
		updateUserSecurityContext();
	}

	@Transactional(rollbackOn = Exception.class)
	public void refuseTerms(EkiUser user) {
		List<String> adminEmails = userDbService.getAdminEmails();
		emailService.sendTermsRefuseEmail(user, adminEmails);
	}

	@Transactional(rollbackOn = Exception.class)
	public void submitUserApplication(EkiUser user, String datasetCode, AuthorityOperation authOp, String lang, String comment) {

		createUserApplication(user, datasetCode, authOp, lang, comment);

		String datasetPermissionsUrl = ekilexAppUrl + PERMISSIONS_URI + APPLICATIONS_URI + "/" + datasetCode;
		List<String> datasetOwnerEmails = userDbService.getDatasetOwnerEmails(datasetCode);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		String datasetName = datasetNameMap.get(datasetCode);
		emailService.sendApplicationSubmitEmail(user, datasetOwnerEmails, datasetCode, datasetName, comment, authOp, datasetPermissionsUrl);
	}

	private void createUserApplication(EkiUser user, String datasetCode, AuthorityOperation authOp, String lang, String comment) {

		Long userId = user.getId();
		if (StringUtils.isBlank(lang)) {
			lang = null;
		}
		if (StringUtils.isBlank(comment)) {
			comment = null;
		}
		userDbService.createUserApplication(userId, datasetCode, authOp, lang, comment, ApplicationStatus.NEW);
	}

	@Transactional
	public List<EkiUserApplication> getUserApplications(Long userId) {
		List<EkiUserApplication> userApplications = userDbService.getUserApplications(userId);
		return userApplications;
	}

	@Transactional(rollbackOn = Exception.class)
	public String handleUserPasswordRecovery(Long userId, String email) {

		String recoveryKey = generateUniqueKey();
		String passwordRecoveryLink = ekilexAppUrl + PASSWORD_SET_PAGE_URI + "/" + recoveryKey;
		userDbService.setUserRecoveryKey(userId, recoveryKey);
		emailService.sendPasswordRecoveryEmail(email, passwordRecoveryLink);
		return passwordRecoveryLink;
	}

	@Transactional(rollbackOn = Exception.class)
	public void setUserPassword(String email, String password) {
		String encodedPassword = passwordEncoder.encode(password);
		userDbService.setUserPassword(email, encodedPassword);
	}

	@Transactional(rollbackOn = Exception.class)
	public String generateApiKey(EkiUser user) {

		Long userId = user.getId();
		boolean isApiCrud = user.isAdmin();
		String apiKey = generateUniqueKey();
		userDbService.updateApiKey(userId, apiKey, isApiCrud);
		updateUserSecurityContext();
		return apiKey;
	}

	private String generateUniqueKey() {
		return CodeGenerator.generateUniqueId();
	}

}
