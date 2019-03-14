package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.util.CodeGenerator;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.UserDbService;
import eki.ekilex.web.util.UserContext;

@Component
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final int MIN_PASSWORD_LENGTH = 8;

	private UserContext userContext;

	private UserDbService userDbService;

	private CommonDataDbService commonDataDbService;

	public UserService(UserContext userContext, UserDbService userDbService, CommonDataDbService commonDataDbService) {
		this.userContext = userContext;
		this.userDbService = userDbService;
		this.commonDataDbService = commonDataDbService;
	}

	@Transactional
	public EkiUser getUserByEmail(String email) {
		EkiUser user = userDbService.getUserByEmail(email);
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

	public String generateActivationKey() {
		return CodeGenerator.generateUniqueId();
	}

	public boolean isActiveUser(EkiUser user) {
		return user != null && StringUtils.isBlank(user.getActivationKey());
	}

	public boolean isValidPassword(String password, String password2) {
		return StringUtils.length(password) >= MIN_PASSWORD_LENGTH && StringUtils.equals(password, password2);
	}

	@Transactional
	public void submitUserApplication(List<String> datasets, String comment) {
		Long userId = userContext.getUser().getId();
		String[] datasetArr = null;
		if (CollectionUtils.isNotEmpty(datasets)) {
			datasetArr = datasets.toArray(new String[datasets.size()]);
		}
		userDbService.createUserApplication(userId, datasetArr, comment);
	}

	@Transactional
	public List<EkiUserApplication> getUserApplications() {
		Long userId = userContext.getUser().getId();
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
}
