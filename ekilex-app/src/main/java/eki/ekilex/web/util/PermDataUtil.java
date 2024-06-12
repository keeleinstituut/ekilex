package eki.ekilex.web.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Meaning;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.PermissionGrantService;
import eki.ekilex.service.core.UserContext;

@Component
public class PermDataUtil {

	@Autowired
	private UserContext userContext;

	@Autowired
	private PermissionGrantService permissionGrantService;

	@Autowired
	private LookupService lookupService;

	public boolean isRoleSelected(DatasetPermission datasetPermission) {

		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		boolean isRoleSelected = userRole.getId().equals(datasetPermission.getId());
		return isRoleSelected;
	}

	public boolean isOwnPermission(Long userId) {

		Long authenticatedUserId = userContext.getUserId();
		boolean isOwnPermission = authenticatedUserId.equals(userId);
		return isOwnPermission;
	}

	public boolean isSourceMeaningCrudGranted(Long sourceMeaningId, Long targetMeaningId) {

		EkiUser user = userContext.getUser();
		boolean isMeaningPairCrudGranted = permissionGrantService.isMeaningPairCrudGranted(user, sourceMeaningId, targetMeaningId);
		return isMeaningPairCrudGranted;
	}

	public boolean isSourceWordCrudGranted(Long sourceWordId, Long targetWordId) {

		EkiUser user = userContext.getUser();
		boolean isWordPairCrudGranted = permissionGrantService.isWordPairCrudGranted(user, sourceWordId, targetWordId);
		return isWordPairCrudGranted;
	}

	public boolean isWordValidForJoin(Long sourceWordId, Long targetWordId) {
		if (sourceWordId.equals(targetWordId)) {
			return false;
		}
		return lookupService.isValidWordStressAndMarkup(sourceWordId, targetWordId);
	}

	public boolean isMeaningValidForJoin(Meaning sourceMeaning, Meaning targetMeaning) {

		Long sourceMeaningId = sourceMeaning.getMeaningId();
		Long targetMeaningId = targetMeaning.getMeaningId();
		List<String> sourceMeaningDatasetCodes = sourceMeaning.getLexemeDatasetCodes();
		List<String> targetMeaningDatasetCodes = targetMeaning.getLexemeDatasetCodes();

		if (sourceMeaningId.equals(targetMeaningId)) {
			return false;
		}
		if (sourceMeaningDatasetCodes.size() > 1 || targetMeaningDatasetCodes.size() > 1) {
			return false;
		}

		String sourceMeaningDatasetCode = sourceMeaningDatasetCodes.get(0);
		String targetMeaningDatasetCode = targetMeaningDatasetCodes.get(0);

		boolean isSameDatasetMeanings = StringUtils.equals(sourceMeaningDatasetCode, targetMeaningDatasetCode);
		return isSameDatasetMeanings;
	}
}
