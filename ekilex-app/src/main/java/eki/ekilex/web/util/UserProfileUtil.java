package eki.ekilex.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.UserService;

@Component
public class UserProfileUtil {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserService userService;

	public boolean showMeaningRelationMeaningId() {

		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		boolean showMeaningRelationMeaningId = userProfile.isShowMeaningRelationMeaningId();
		return showMeaningRelationMeaningId;
	}

}
