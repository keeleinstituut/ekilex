package eki.ekilex.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.GlobalConstant;
import eki.common.constant.OrderingField;
import eki.ekilex.constant.ApplicationStatus;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserDbService;
import eki.ekilex.service.util.DatasetUtil;

@Component
public class PermissionService implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private DatasetUtil datasetUtil;

	@Transactional
	public List<EkiUserPermData> getEkiUserPermissions(
			String userNameFilter, String userPermDatasetCodeFilter, Boolean userEnablePendingFilter, OrderingField orderBy) {

		List<EkiUserPermData> users = permissionDbService.getUsers(userNameFilter, userPermDatasetCodeFilter, userEnablePendingFilter, orderBy);
		for (EkiUserPermData user : users) {
			Long userId = user.getId();
			List<EkiUserApplication> userApplications = userDbService.getUserApplications(userId);
			userApplications.sort(Comparator.comparing(userApplication -> userApplication.getStatus().getOrder()));
			List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
			user.setApplications(userApplications);
			user.setDatasetPermissions(datasetPermissions);
		}
		return users;
	}

	@Transactional
	public List<Dataset> getUserVisibleDatasets(Long userId) {
		if (userId == null) {
			return Collections.emptyList();
		}
		List<Dataset> datasets = permissionDbService.getUserVisibleDatasets(userId);
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public List<String> getUserVisibleDatasetCodes(Long userId) {
		if (userId == null) {
			return Collections.emptyList();
		}
		List<Dataset> datasets = permissionDbService.getUserVisibleDatasets(userId);
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		List<String> datasetCodes = datasets.stream().map(Dataset::getCode).collect(Collectors.toList());
		return datasetCodes;
	}

	@Transactional
	public List<Dataset> getUserOwnedDatasets(Long userId) {
		if (userId == null) {
			return Collections.emptyList();
		}
		List<Dataset> datasets = permissionDbService.getUserOwnedDatasets(userId);
		return datasets;
	}

	@Transactional
	public List<Dataset> userVisibleNonPublicDatasets(Long userId) {
		if (userId == null) {
			return Collections.emptyList();
		}
		List<Dataset> datasets = permissionDbService.userVisibleNonPublicDatasets(userId);
		return datasets;
	}

	@Transactional
	public List<Classifier> getUserDatasetLanguages(Long userId, String datasetCode) {
		return permissionDbService.getUserDatasetLanguages(userId, datasetCode, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createDatasetPermission(
			EkiUser permittedUser, EkiUser permittingUser, String datasetCode, AuthorityItem authItem, AuthorityOperation authOp, String authLang) {

		if (StringUtils.isBlank(datasetCode)) {
			return;
		}
		if (authItem == null) {
			return;
		}
		if (authOp == null) {
			return;
		}
		if (StringUtils.isBlank(authLang)) {
			authLang = null;
		}
		Long permittedUserId = permittedUser.getId();
		String permittedUserName = permittedUser.getName();
		String permittingUserName = permittingUser.getName();
		Long permissionId = permissionDbService.createDatasetPermission(permittedUserId, datasetCode, authItem, authOp, authLang);
		logger.info("User \"{}\" created dataset \"{}\" \"{}\" permission with id {} for user \"{}\"", permittingUserName, datasetCode, authOp.name(), permissionId, permittedUserName);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteDatasetPermission(Long permissionId, EkiUser user) {

		String userName = user.getName();
		DatasetPermission datasetPermission = permissionDbService.getDatasetPermission(permissionId);
		String datasetCode = datasetPermission.getDatasetCode();
		AuthorityOperation authOp = datasetPermission.getAuthOperation();
		Long forbiddenUserId = datasetPermission.getUserId();
		EkiUser forbiddenUser = userDbService.getUserById(forbiddenUserId);
		String forbiddenUserName = forbiddenUser.getName();

		permissionDbService.deleteDatasetPermission(permissionId);
		logger.info("User \"{}\" deleted dataset \"{}\" \"{}\" permission with id {} for user \"{}\"", userName, datasetCode, authOp.name(), permissionId, forbiddenUserName);

	}

	@Transactional(rollbackOn = Exception.class)
	public void sendPermissionsEmail(String userEmail, EkiUser sender) {

		EkiUser receiver = userDbService.getUserByEmail(userEmail);
		Long receiverId = receiver.getId();
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(receiverId);
		receiver.setDatasetPermissions(datasetPermissions);

		emailService.sendPermissionsEmail(receiver, sender);
	}

	@Transactional
	public List<DatasetPermission> getUserDatasetPermissions(Long userId) {
		return permissionDbService.getDatasetPermissions(userId);
	}

	@Transactional(rollbackOn = Exception.class)
	public void approveApplication(Long userApplicationId, EkiUser approver) {

		boolean isApprove = true;
		approveOrRejectApplication(userApplicationId, approver, isApprove);
	}

	@Transactional(rollbackOn = Exception.class)
	public void rejectApplication(Long userApplicationId, EkiUser rejecter) {

		boolean isApprove = false;
		approveOrRejectApplication(userApplicationId, rejecter, isApprove);
	}

	private void approveOrRejectApplication(Long userApplicationId, EkiUser reviewer, boolean isApprove) {

		String reviewerName = reviewer.getName();
		EkiUserApplication userApplication = userDbService.getUserApplication(userApplicationId);
		Long applicantUserId = userApplication.getUserId();
		String datasetCode = userApplication.getDatasetCode();
		String datasetName = userApplication.getDatasetName();
		AuthorityOperation authOp = userApplication.getAuthOperation();
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(applicantUserId);
		EkiUser applicant = userDbService.getUserById(applicantUserId);
		String applicantName = applicant.getName();
		applicant.setDatasetPermissions(datasetPermissions);

		if (isApprove) {
			userDbService.updateApplicationStatus(userApplicationId, ApplicationStatus.APPROVED);
			logger.info("User \"{}\" approved dataset \"{}\" \"{}\" application with id {} for user \"{}\"", reviewerName, datasetCode, authOp.name(), userApplicationId, applicantName);
			if (!Boolean.TRUE.equals(applicant.getEnabled())) {
				Long applicantId = applicant.getId();
				userDbService.enableUser(applicantId, true);
			}
		} else {
			userDbService.updateApplicationStatus(userApplicationId, ApplicationStatus.REJECTED);
			logger.info("User \"{}\" rejected dataset \"{}\" \"{}\" application with id {} for user \"{}\"", reviewerName, datasetCode, authOp.name(), userApplicationId, applicantName);
		}

		emailService.sendApplicationApprovalEmail(applicant, reviewer, datasetName, isApprove);
	}
}
