package eki.ekilex.security;

import java.io.Serializable;
import java.util.ArrayList;
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
import eki.common.data.AbstractDataObject;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class EkilexPermissionEvaluator implements PermissionEvaluator {

	private static Logger logger = LoggerFactory.getLogger(EkilexPermissionEvaluator.class);

	private static final char PERM_KEY_VALUE_SEPARATOR = ':';

	@Autowired
	private PermissionDbService permissionDbService;

	//hasPermission(#foo, 'write')
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();

		if (user.isAdmin()) {
			return true;
		}

		logger.debug("userId: \"{}\" targetObj: \"{}\" permission: \"{}\"", userId, targetDomainObject, permission);

		return false;
	}

	//hasPermission(#id, 'USAGE', 'DATASET:CRUD')
	@Transactional
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();

		if (user.isAdmin()) {
			return true;
		}

		Long entityId = Long.valueOf(targetId.toString());
		RequiredAuthority requiredAuthority = parse(permission);
		String authItem = requiredAuthority.getItem();
		List<String> authOps = requiredAuthority.getOps();
		boolean isPermGranted = false;

		if (StringUtils.equals(LifecycleEntity.WORD.name(), targetType)) {
			isPermGranted = permissionDbService.isGrantedForWord(userId, entityId, authItem, authOps);
		} else if (StringUtils.equals(LifecycleEntity.MEANING.name(), targetType)) {
			isPermGranted = permissionDbService.isGrantedForMeaning(userId, entityId, authItem, authOps);
		} else if (StringUtils.equals(LifecycleEntity.LEXEME.name(), targetType)) {
			isPermGranted = permissionDbService.isGrantedForLexeme(userId, entityId, authItem, authOps);
		} else if (StringUtils.equals(LifecycleEntity.DEFINITION.name(), targetType)) {
			isPermGranted = permissionDbService.isGrantedForDefinition(userId, entityId, authItem, authOps);
		} else if (StringUtils.equals(LifecycleEntity.USAGE.name(), targetType)) {
			isPermGranted = permissionDbService.isGrantedForUsage(userId, entityId, authItem, authOps);
		}

		//logger.debug("userId: \"{}\" targetId: \"{}\" targetType: \"{}\" permission: \"{}\" granted: {}", userId, targetId, targetType, permission, isPermGranted);

		return isPermGranted;
	}

	private RequiredAuthority parse(Object permission) {
		String permSentence = permission.toString();
		String[] perms = StringUtils.split(permSentence, PERM_KEY_VALUE_SEPARATOR);
		AuthorityItem permAuthItem = AuthorityItem.valueOf(perms[0]);
		AuthorityOperation permAuthOp = AuthorityOperation.valueOf(perms[1]);
		String item = permAuthItem.name();
		List<String> ops = new ArrayList<>();
		ops.add(permAuthOp.name());
		// if crud is required, owner is also pass
		if (AuthorityItem.DATASET.equals(permAuthItem) && AuthorityOperation.CRUD.equals(permAuthOp)) {
			ops.add(AuthorityOperation.OWN.name());
		}
		return new RequiredAuthority(item, ops);
	}

	public class RequiredAuthority extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String item;

		private List<String> ops;

		public RequiredAuthority(String item, List<String> ops) {
			this.item = item;
			this.ops = ops;
		}

		public String getItem() {
			return item;
		}

		public List<String> getOps() {
			return ops;
		}
	}
}
