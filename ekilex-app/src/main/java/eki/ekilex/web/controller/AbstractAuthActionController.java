package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.UserContext;
import eki.ekilex.service.UserProfileService;

public abstract class AbstractAuthActionController implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected UserProfileService userProfileService;

	@Autowired
	protected LookupService lookupService;

	protected String getDatasetCodeFromRole() {
		EkiUser user = userContext.getUser();
		DatasetPermission role = user.getRecentRole();
		if (role == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		return role.getDatasetCode();
	}

	protected UserContextData getUserContextData() {
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		String userName = user.getName();
		DatasetPermission userRole = user.getRecentRole();
		String userRoleDatasetCode = null;
		if (userRole != null) {
			userRoleDatasetCode = userRole.getDatasetCode();
		}
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		String activeTagName = userProfile.getActiveTagName();
		Tag activeTag = lookupService.getTag(activeTagName);
		List<String> preferredTagNames = userProfile.getPreferredTagNames();
		List<String> preferredDatasetCodes = userProfile.getPreferredDatasets();
		List<String> synCandidateLangCodes = userProfile.getPreferredSynCandidateLangs();
		List<String> synMeaningWordLangCodes = userProfile.getPreferredSynLexMeaningWordLangs();
		return new UserContextData(userId, userName, userRole, userRoleDatasetCode, activeTag, preferredTagNames, preferredDatasetCodes, synCandidateLangCodes, synMeaningWordLangCodes);
	}
}
