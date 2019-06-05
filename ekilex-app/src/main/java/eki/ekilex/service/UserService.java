package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.util.CodeGenerator;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserDbService;

@Component
public class UserService implements WebConstant {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final int MIN_PASSWORD_LENGTH = 8;

	@Value("${ekilex.app.url:}")
	private String ekilexAppUrl;

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private EkilexPasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

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
	public String getUserEmailByRecoveryKey(String recoveryKey) {
		String email = userDbService.getUserEmailByRecoveryKey(recoveryKey);
		return email;
	}

	@Transactional
	public Long getUserIdByEmail(String email) {
		Long userId = userDbService.getUserIdByEmail(email);
		return userId;
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
	public String createUser(String email, String name, String password) {

		String activationKey = generateUniqueKey();
		String activationLink = ekilexAppUrl + REGISTER_PAGE_URI + ACTIVATE_PAGE_URI + "/" + activationKey;
		String encodedPassword = passwordEncoder.encode(password);
		userDbService.createUser(email, name, encodedPassword, activationKey);
		EkiUser user = userDbService.getUserByEmail(email);
		emailService.sendUserActivationEmail(email, activationLink);
		logger.debug("Created new user : {}", user.getDescription());
		return activationLink;
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

	public boolean isActiveUser(EkiUser user) {
		return user != null && StringUtils.isBlank(user.getActivationKey());
	}

	public boolean isValidPassword(String password, String password2) {
		return StringUtils.length(password) >= MIN_PASSWORD_LENGTH && StringUtils.equals(password, password2);
	}

	@Transactional
	public void submitUserApplication(EkiUser user, List<String> datasets, String comment) {
		String[] datasetArr = null;
		if (CollectionUtils.isNotEmpty(datasets)) {
			datasetArr = datasets.toArray(new String[datasets.size()]);
		}
		Long userId = user.getId();
		userDbService.createUserApplication(userId, datasetArr, comment);
		List<String> adminEmails = userDbService.getAdminEmails();
		emailService.sendApplicationSubmitEmail(adminEmails, user, datasets, comment);
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
	public String handleUserPasswordRecovery(Long userId, String email) {

		String recoveryKey = generateUniqueKey();
		String passwordRecoveryLink = ekilexAppUrl + PASSWORD_SET_PAGE_URI + "/" + recoveryKey;
		userDbService.setUserRecoveryKey(userId, recoveryKey);
		emailService.sendPasswordRecoveryEmail(email, passwordRecoveryLink);
		return passwordRecoveryLink;
	}

	@Transactional
	public void setUserPassword(String email, String password) {
		String encodedPassword = passwordEncoder.encode(password);
		userDbService.setUserPassword(email, encodedPassword);
	}

	private String generateUniqueKey() {
		return CodeGenerator.generateUniqueId();
	}

	public void updateUserSecurityContext() {

		String userEmail = getAuthenticatedUser().getEmail();

		if (StringUtils.isNotBlank(userEmail)) {
			EkiUser ekiUser = getUserByEmail(userEmail);

			Collection<? extends GrantedAuthority> authorities = CollectionUtils.emptyCollection();
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(ekiUser, null, authorities);

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(authenticationToken);
		}
	}
}
