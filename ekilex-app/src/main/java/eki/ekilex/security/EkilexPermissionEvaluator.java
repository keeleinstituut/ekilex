package eki.ekilex.security;

import java.io.Serializable;
import java.security.Principal;
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

	// source

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

	// source link

	@Transactional
	public boolean isSourceLinkCrudGranted(Principal principal, String crudRoleDataset, SourceLink sourceLink) {

		EkiUser user = (EkiUser) principal;
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		ReferenceOwner sourceLinkOwner = sourceLink.getOwner();
		Long ownerId = sourceLink.getOwnerId();
		if (ReferenceOwner.FREEFORM.equals(sourceLinkOwner)) {
			return isFreeformSourceLinkCrudGranted(userId, crudRole, ownerId);
		} else if (ReferenceOwner.DEFINITION.equals(sourceLinkOwner)) {
			return permissionDbService.isGrantedForDefinition(userId, crudRole, ownerId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (ReferenceOwner.LEXEME.equals(sourceLinkOwner)) {
			return permissionDbService.isGrantedForLexeme(userId, crudRole, ownerId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		}
		return false;
	}

	@Transactional
	public boolean isSourceLinkCrudGranted(
			Principal principal, String crudRoleDataset, ReferenceOwner sourceLinkOwner, Long sourceLinkId) {

		EkiUser user = (EkiUser) principal;
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		if (ReferenceOwner.FREEFORM.equals(sourceLinkOwner)) {
			SourceLink sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
			Long ownerId = sourceLink.getOwnerId();
			return isFreeformSourceLinkCrudGranted(userId, crudRole, ownerId);
		} else if (ReferenceOwner.DEFINITION.equals(sourceLinkOwner)) {
			SourceLink sourceLink = sourceLinkDbService.getDefinitionSourceLink(sourceLinkId);
			Long ownerId = sourceLink.getOwnerId();
			return permissionDbService.isGrantedForDefinition(userId, crudRole, ownerId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (ReferenceOwner.LEXEME.equals(sourceLinkOwner)) {
			SourceLink sourceLink = sourceLinkDbService.getLexemeSourceLink(sourceLinkId);
			Long ownerId = sourceLink.getOwnerId();
			return permissionDbService.isGrantedForLexeme(userId, crudRole, ownerId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		}
		return false;
	}

	private boolean isFreeformSourceLinkCrudGranted(Long userId, DatasetPermission crudRole, Long ownerId) {
		FreeformOwner freeformOwner = sourceLinkDbService.getFreeformOwner(ownerId);
		LifecycleEntity entity = freeformOwner.getEntity();
		Long entityId = freeformOwner.getEntityId();
		if (LifecycleEntity.LEXEME.equals(entity)) {
			return permissionDbService.isGrantedForLexeme(userId, crudRole, entityId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (LifecycleEntity.MEANING.equals(entity)) {
			return permissionDbService.isGrantedForMeaning(userId, crudRole, entityId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (LifecycleEntity.DEFINITION.equals(entity)) {
			return permissionDbService.isGrantedForDefinition(userId, crudRole, entityId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		}
		return false;
	}

	private DatasetPermission getCrudRole(Long userId, String crudRoleDataset) {

		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		DatasetPermission crudRole = datasetPermissions.stream()
				.filter(datasetPermission -> AuthorityItem.DATASET.equals(datasetPermission.getAuthItem())
						&& AUTH_OPS_CRUD.contains(datasetPermission.getAuthOperation().name())
						&& StringUtils.equals(datasetPermission.getDatasetCode(), crudRoleDataset))
				.findAny()
				.orElse(null);
		return crudRole;
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
		AuthorityOperation providedAuthOperation = userRole.getAuthOperation();
		AuthorityItem providedAuthItem = userRole.getAuthItem();

		Long entityId = Long.valueOf(targetId.toString());
		boolean isPermGranted = false;

		if (requiredAuthItem.equals(providedAuthItem) && requiredAuthOps.contains(providedAuthOperation.name())) {
			if (StringUtils.equals(LifecycleEntity.WORD.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForWord(userId, userRole, entityId, requiredAuthItem.name(), requiredAuthOps);
			} else if (StringUtils.equals(LifecycleEntity.MEANING.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForMeaning(userId, userRole, entityId, requiredAuthItem.name(), requiredAuthOps);
			} else if (StringUtils.equals(LifecycleEntity.LEXEME.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForLexeme(userId, userRole, entityId, requiredAuthItem.name(), requiredAuthOps);
			} else if (StringUtils.equals(LifecycleEntity.DEFINITION.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForDefinition(userId, userRole, entityId, requiredAuthItem.name(), requiredAuthOps);
			} else if (StringUtils.equals(LifecycleEntity.USAGE.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForUsage(userId, userRole, entityId, requiredAuthItem.name(), requiredAuthOps);
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
				return new Authority(authItem, AUTH_OPS_CRUD);
			} else if (AuthorityOperation.READ.equals(authOp)) {
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
