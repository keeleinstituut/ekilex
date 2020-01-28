package eki.ekilex.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.web.bean.SessionBean;

@Component
public class EkilexPermissionEvaluator implements PermissionEvaluator {

	private static Logger logger = LoggerFactory.getLogger(EkilexPermissionEvaluator.class);

	private static final char PERM_KEY_VALUE_SEPARATOR = ':';

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired(required = false)
	private HttpServletRequest request;

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

		// required authority
		Authority requiredAuthority = parse(permission);
		AuthorityItem requiredAuthItem = requiredAuthority.getAuthItem();
		List<String> requiredAuthOps = requiredAuthority.getAuthOps();

		if (requiredAuthOps.contains(AuthorityOperation.READ.name())) {
			boolean isMaster = user.isMaster();
			if (isMaster) {
				return true;
			}
		}

		DatasetPermission userRole = getUserRole();
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
				isPermGranted = permissionDbService.isGrantedForLexeme(userId, entityId, providedDatasetCode);
			} else if (StringUtils.equals(LifecycleEntity.DEFINITION.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForDefinition(entityId, providedDatasetCode, providedAuthLang);
			} else if (StringUtils.equals(LifecycleEntity.USAGE.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForUsage(userId, entityId, providedDatasetCode, providedAuthLang);
			} else if (StringUtils.equals(LifecycleEntity.SOURCE.name(), targetType)) {
				isPermGranted = permissionDbService.isGrantedForSource(userId, entityId, requiredAuthItem.name(), requiredAuthOps);
			}
		}

		//logger.debug("userId: \"{}\" targetId: \"{}\" targetType: \"{}\" permission: \"{}\" granted: {}", userId, targetId, targetType, permission, isPermGranted);

		return isPermGranted;
	}

	private DatasetPermission getUserRole() {
		if (request == null) {
			return null;
		}
		HttpSession session = request.getSession();
		SessionBean sessionBean = (SessionBean) session.getAttribute(WebConstant.SESSION_BEAN);
		if (sessionBean == null) {
			return null;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		return userRole;
	}

	private Authority parse(Object permission) {
		String permSentence = permission.toString();
		String[] perms = StringUtils.split(permSentence, PERM_KEY_VALUE_SEPARATOR);
		AuthorityItem authItem = AuthorityItem.valueOf(perms[0]);
		AuthorityOperation authOp = AuthorityOperation.valueOf(perms[1]);
		List<String> authOps = new ArrayList<>();
		authOps.add(authOp.name());
		if (AuthorityItem.DATASET.equals(authItem)) {
			if (AuthorityOperation.CRUD.equals(authOp)) {
				// if crud is required, owner is also pass
				authOps.add(AuthorityOperation.OWN.name());
			} else if (AuthorityOperation.READ.equals(authOp)) {
				// if read is required, crud and owner is also pass
				authOps.add(AuthorityOperation.CRUD.name());
				authOps.add(AuthorityOperation.OWN.name());
			}
		}
		return new Authority(authItem, authOps);
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
