package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.EKI_USER_PROFILE;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.db.main.tables.records.EkiUserProfileRecord;

@Component
public class UserProfileDbService {

	@Autowired
	private DSLContext mainDb;

	public EkiUserProfile getUserProfile(Long userId) {
		return mainDb.selectFrom(EKI_USER_PROFILE).where(EKI_USER_PROFILE.USER_ID.eq(userId)).fetchOptionalInto(EkiUserProfile.class).orElse(null);
	}

	public void createUserProfile(Long userId) {
		mainDb.insertInto(EKI_USER_PROFILE, EKI_USER_PROFILE.USER_ID)
				.values(userId)
				.execute();
	}

	public void setRecentDatasetPermission(Long userId, Long permissionId) {

		mainDb.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID, permissionId)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updatePreferredDatasets(List<String> selectedDatasets, Long userId) {

		String[] selectedDatasetsArray = selectedDatasets.toArray(new String[0]);
		mainDb
				.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_DATASETS, selectedDatasetsArray)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updateApproveMeaningEnabled(Long userId, boolean approveMeaningEnabled) {

		mainDb
				.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.IS_APPROVE_MEANING_ENABLED, approveMeaningEnabled)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updateUserProfile(EkiUserProfile userProfile) {

		Long userId = userProfile.getUserId();
		Long recentDatasetPermissionId = userProfile.getRecentDatasetPermissionId();
		List<String> preferredDatasets = userProfile.getPreferredDatasets();
		List<String> preferredPartSynCandidateLangs = userProfile.getPreferredPartSynCandidateLangs();
		List<String> preferredSynLexMeaningWordLangs = userProfile.getPreferredSynLexMeaningWordLangs();
		List<String> preferredMeaningRelationWordLangs = userProfile.getPreferredMeaningRelationWordLangs();
		boolean showLexMeaningRelationSourceLangWords = userProfile.isShowLexMeaningRelationSourceLangWords();
		boolean showMeaningRelationFirstWordOnly = userProfile.isShowMeaningRelationFirstWordOnly();
		boolean showMeaningRelationMeaningId = userProfile.isShowMeaningRelationMeaningId();
		boolean showMeaningRelationWordDatasets = userProfile.isShowMeaningRelationWordDatasets();
		String activeTagName = userProfile.getActiveTagName();
		String preferredFullSynCandidateLang = userProfile.getPreferredFullSynCandidateLang();
		String preferredFullSynCandidateDatasetCode = userProfile.getPreferredFullSynCandidateDatasetCode();
		List<String> preferredTagNames = userProfile.getPreferredTagNames();
		boolean isApproveMeaningEnabled = userProfile.isApproveMeaningEnabled();

		EkiUserProfileRecord ekiUserProfile = mainDb.selectFrom(EKI_USER_PROFILE).where(EKI_USER_PROFILE.USER_ID.eq(userId)).fetchOne();

		ekiUserProfile.setRecentDatasetPermissionId(recentDatasetPermissionId);
		if (CollectionUtils.isNotEmpty(preferredDatasets)) {
			ekiUserProfile.setPreferredDatasets(preferredDatasets.toArray(new String[0]));
		}
		if (CollectionUtils.isNotEmpty(preferredPartSynCandidateLangs)) {
			ekiUserProfile.setPreferredPartSynCandidateLangs(preferredPartSynCandidateLangs.toArray(new String[0]));
		}
		if (CollectionUtils.isNotEmpty(preferredSynLexMeaningWordLangs)) {
			ekiUserProfile.setPreferredSynLexMeaningWordLangs(preferredSynLexMeaningWordLangs.toArray(new String[0]));
		}
		if (CollectionUtils.isNotEmpty(preferredMeaningRelationWordLangs)) {
			ekiUserProfile.setPreferredMeaningRelationWordLangs(preferredMeaningRelationWordLangs.toArray(new String[0]));
		}
		ekiUserProfile.setShowLexMeaningRelationSourceLangWords(showLexMeaningRelationSourceLangWords);
		ekiUserProfile.setShowMeaningRelationFirstWordOnly(showMeaningRelationFirstWordOnly);
		ekiUserProfile.setShowMeaningRelationMeaningId(showMeaningRelationMeaningId);
		ekiUserProfile.setShowMeaningRelationWordDatasets(showMeaningRelationWordDatasets);
		ekiUserProfile.setIsApproveMeaningEnabled(isApproveMeaningEnabled);
		ekiUserProfile.setActiveTagName(activeTagName);
		if (StringUtils.isNotBlank(preferredFullSynCandidateLang)) {
			ekiUserProfile.setPreferredFullSynCandidateLang(preferredFullSynCandidateLang);
		}
		if (StringUtils.isNotBlank(preferredFullSynCandidateDatasetCode)) {
			ekiUserProfile.setPreferredFullSynCandidateDatasetCode(preferredFullSynCandidateDatasetCode);
		}
		if (CollectionUtils.isNotEmpty(preferredTagNames)) {
			ekiUserProfile.setPreferredTagNames(preferredTagNames.toArray(new String[0]));
		} else {
			ekiUserProfile.setPreferredTagNames(null);
		}

		ekiUserProfile.store();
	}

}
