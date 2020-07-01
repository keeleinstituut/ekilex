package eki.ekilex.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.PermConstant;
import eki.common.constant.ReferenceOwner;
import eki.common.data.AbstractDataObject;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.api.FreeformOwner;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.SourceDbService;
import eki.ekilex.service.db.SourceLinkDbService;

@Component("permEval")
public class EkilexPermissionEvaluator implements PermissionEvaluator, PermConstant {

	private static Logger logger = LoggerFactory.getLogger(EkilexPermissionEvaluator.class);

	private static final char PERM_KEY_VALUE_SEPARATOR = ':';

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private SourceDbService sourceDbService;

	@Autowired
	private SourceLinkDbService sourceLinkDbService;

	@Transactional
	public boolean isSourceCrudGranted(Authentication authentication, Long sourceId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		boolean isGranted = permissionDbService.isGrantedForSource(userId, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		return isGranted;
	}

	@Transactional
	public boolean isSourcePropertyCrudGranted(Authentication authentication, Long sourcePropertyId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		SourceProperty sourceProperty = sourceDbService.getSourceProperty(sourcePropertyId);
		if (sourceProperty == null) {
			return true;
		}
		Long sourceId = sourceProperty.getSourceId();
		boolean isGranted = permissionDbService.isGrantedForSource(userId, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		return isGranted;
	}

	@Transactional
	public boolean isSourceLinkCrudGranted(Authentication authentication, String crudRoleDataset, SourceLink sourceLink) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		boolean isValidCrudRole = isValidCrudRole(userId, crudRoleDataset);
		if (!isValidCrudRole) {
			return false;
		}
		ReferenceOwner referenceOwner = sourceLink.getOwner();
		Long ownerId = sourceLink.getOwnerId();
		if (ReferenceOwner.FREEFORM.equals(referenceOwner)) {
			FreeformOwner freeformOwner = sourceLinkDbService.getFreeformOwner(ownerId);
			LifecycleEntity entity = freeformOwner.getEntity();
			Long entityId = freeformOwner.getEntityId();
			if (LifecycleEntity.LEXEME.equals(entity)) {
				return permissionDbService.isGrantedForLexeme(entityId, crudRoleDataset);
			} else if (LifecycleEntity.MEANING.equals(entity)) {
				return permissionDbService.isGrantedForMeaning(userId, entityId, crudRoleDataset, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			} else if (LifecycleEntity.DEFINITION.equals(entity)) {
				return permissionDbService.isGrantedForDefinition(entityId, crudRoleDataset, null);
			}
		} else if (ReferenceOwner.DEFINITION.equals(referenceOwner)) {
			return permissionDbService.isGrantedForDefinition(ownerId, crudRoleDataset, null);
		} else if (ReferenceOwner.LEXEME.equals(referenceOwner)) {
			return permissionDbService.isGrantedForLexeme(ownerId, crudRoleDataset);
		}
		return false;
	}

	private boolean isValidCrudRole(Long userId, String crudRoleDataset) {

		final List<AuthorityOperation> crudAuthOps = Arrays.asList(AuthorityOperation.OWN, AuthorityOperation.CRUD);
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		boolean isValidCrudRole = datasetPermissions.stream()
				.anyMatch(datasetPermission -> AuthorityItem.DATASET.equals(datasetPermission.getAuthItem())
						&& crudAuthOps.contains(datasetPermission.getAuthOperation())
						&& StringUtils.equals(datasetPermission.getDatasetCode(), crudRoleDataset));
		return isValidCrudRole;
	}

	//not in use currently
	//hasPermission(#foo, 'write')
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();

		logger.debug("userId: \"{}\" targetObj: \"{}\" permission: \"{}\"", userId, targetDomainObject, permission);

		return false;
	}

	//not in use currently
	//hasPermission(#id, 'USAGE', 'DATASET:CRUD')
	@Transactional
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();

		// required authority
		Authority requiredAuthority = parse(permission);
		AuthorityItem requiredAuthItem = requiredAuthority.getAuthItem();
		List<String> requiredAuthOps = requiredAuthority.getAuthOps();

		if (requiredAuthOps.contains(AuthorityOperation.READ.name()) && user.isMaster()) {
			return true;
		}

		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}

		// provided user role
		String providedDatasetCode = userRole.getDatasetCode();
		AuthorityOperation providedAuthOperation = userRole.getAuthOperation();
		AuthorityItem providedAuthItem = userRole.getAuthItem();
		String providedAuthLang = userRole.getAuthLang();

		Long entityId = Long.valueOf(targetId.toString());
		boolean isPermGranted = false;

		if (requiredAuthItem.equals(providedAuthItem) && requiredAuthOps.contains(providedAuthOperation.name())) {
			if (StringUtils.equals(LifecycleEntity.WORD.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForWord(userId, entityId, providedDatasetCode, requiredAuthItem.name(), requiredAuthOps);
			} else if (StringUtils.equals(LifecycleEntity.MEANING.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForMeaning(userId, entityId, providedDatasetCode, requiredAuthItem.name(), requiredAuthOps);
			} else if (StringUtils.equals(LifecycleEntity.LEXEME.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForLexeme(entityId, providedDatasetCode);
			} else if (StringUtils.equals(LifecycleEntity.DEFINITION.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForDefinition(entityId, providedDatasetCode, providedAuthLang);
			} else if (StringUtils.equals(LifecycleEntity.USAGE.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForUsage(entityId, providedDatasetCode, providedAuthLang);
			} else if (StringUtils.equals(LifecycleEntity.SOURCE.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForSource(userId, entityId, requiredAuthItem.name(), requiredAuthOps);
			}
		}

		//logger.debug("userId: \"{}\" targetId: \"{}\" targetType: \"{}\" permission: \"{}\" granted: {}", userId, targetId, targetType, permission, isPermGranted);

		return isPermGranted;
	}

	private Authority parse(Object permission) {
		String permSentence = permission.toString();
		String[] perms = StringUtils.split(permSentence, PERM_KEY_VALUE_SEPARATOR);
		AuthorityItem authItem = AuthorityItem.valueOf(perms[0]);
		AuthorityOperation authOp = AuthorityOperation.valueOf(perms[1]);
		if (AuthorityItem.DATASET.equals(authItem)) {
			if (AuthorityOperation.CRUD.equals(authOp)) {
				// if crud is required, owner is also pass
				return new Authority(authItem, AUTH_OPS_CRUD);
			} else if (AuthorityOperation.READ.equals(authOp)) {
				// if read is required, crud and owner is also pass
				return new Authority(authItem, AUTH_OPS_READ);
			}
		}
		return new Authority(authItem, Collections.emptyList());
	}

	public class Authority extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private AuthorityItem authItem;

		private List<String> authOps;

		public Authority(AuthorityItem authItem, List<String> authOps) {
			this.authItem = authItem;
			this.authOps = authOps;
		}

		public AuthorityItem getAuthItem() {
			return authItem;
		}

		public List<String> getAuthOps() {
			return authOps;
		}
	}
}
