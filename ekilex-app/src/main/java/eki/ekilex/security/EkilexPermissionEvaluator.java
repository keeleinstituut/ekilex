package eki.ekilex.security;

import java.io.Serializable;

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
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class EkilexPermissionEvaluator implements PermissionEvaluator {

	private static Logger logger = LoggerFactory.getLogger(EkilexPermissionEvaluator.class);

	private static final char PERM_KEY_VALUE_SEPARATOR = ':';

	private static final char PERM_SEPARATOR = ';';

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

	//hasPermission(#id, 'Foo', 'write')
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();

		if (user.isAdmin()) {
			return true;
		}

		logger.debug("userId: \"{}\" targetId: \"{}\" targetType: \"{}\" permission: \"{}\"", userId, targetId, targetType, permission);

		Long entityId = Long.valueOf(targetId.toString());
		LifecycleEntity entity = LifecycleEntity.valueOf(targetType);
		AuthorityOperation authOp = AuthorityOperation.CRUD;
		AuthorityItem authItem = null;
		String authLang = null;

		String permSentence = permission.toString();
		String[] perms = StringUtils.split(permSentence, PERM_SEPARATOR);
		for (String perm : perms) {
			String[] permKeyValue = StringUtils.split(perm, PERM_KEY_VALUE_SEPARATOR);
			String permKey = permKeyValue[0];
			String permValue = permKeyValue[1];
			if (StringUtils.equals(permKey, "auth")) {
				authItem = AuthorityItem.valueOf(permValue);
			} else if (StringUtils.equals(permKey, "lang")) {
				authLang = permValue;
			}
		}

		if (LifecycleEntity.MEANING.equals(entity)) {
			if (StringUtils.isEmpty(authLang)) {
				//TODO implement
			} else {
				//TODO implement
			}
		} else if (LifecycleEntity.LEXEME.equals(entity)) {
			if (StringUtils.isEmpty(authLang)) {
				return permissionDbService.isGrantedForLexeme(userId, entityId, authOp, authItem);
			} else {
				return permissionDbService.isGrantedForLexeme(userId, entityId, authOp, authItem, authLang);
			}
		} else if (LifecycleEntity.USAGE.equals(entity)) {
			return permissionDbService.isGrantedForUsage(userId, entityId, authOp, authItem);
		} else if (LifecycleEntity.USAGE_DEFINITION.equals(entity)) {
			//TODO implement
		} else if (LifecycleEntity.USAGE_TRANSLATION.equals(entity)) {
			//TODO implement
		} else if (LifecycleEntity.DEFINITION.equals(entity)) {
			return permissionDbService.isGrantedForDefinition(userId, entityId, authOp, authItem);
		}
		// other data types to be added

		return false;
	}

}
