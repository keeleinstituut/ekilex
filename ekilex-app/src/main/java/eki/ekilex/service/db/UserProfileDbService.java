package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.EKI_USER_PROFILE;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.db.tables.records.EkiUserProfileRecord;

@Component
public class UserProfileDbService {

	@Autowired
	private DSLContext create;

	public EkiUserProfile getUserProfile(Long userId) {
		return create.selectFrom(EKI_USER_PROFILE).where(EKI_USER_PROFILE.USER_ID.eq(userId)).fetchOptionalInto(EkiUserProfile.class).orElse(null);
	}

	public void createUserProfile(Long userId) {
		create.insertInto(EKI_USER_PROFILE, EKI_USER_PROFILE.USER_ID)
				.values(userId)
				.execute();
	}

	public void setRecentDatasetPermission(Long userId, Long permissionId) {

		create.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID, permissionId)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updatePreferredDatasets(List<String> selectedDatasets, Long userId) {

		String[] selectedDatasetsArray = selectedDatasets.toArray(new String[0]);
		create
				.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_DATASETS, selectedDatasetsArray)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updatePreferredBilingCandidateLangs(List<String> languages, Long userId) {

		String[] languagesArray = languages.toArray(new String[0]);
		create.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_BILING_CANDIDATE_LANGS, languagesArray)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updatePreferredMeaningWordLangs(List<String> languages, Long userId) {

		String[] languagesArray = languages.toArray(new String[0]);
		create.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_BILING_LEX_MEANING_WORD_LANGS, languagesArray)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updatePreferredMeaningRelationWordLangs(List<String> languages, Long userId) {

		String[] languagesArray = languages.toArray(new String[0]);
		create.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_MEANING_RELATION_WORD_LANGS, languagesArray)
				.where(EKI_USER_PROFILE.USER_ID.eq(userId))
				.execute();
	}

	public void updateUserProfile(EkiUserProfile userProfile) {

		Long userId = userProfile.getUserId();
		Long recentDatasetPermissionId = userProfile.getRecentDatasetPermissionId();
		String[] preferredDatasets = userProfile.getPreferredDatasets().toArray(new String[0]);
		String[] preferredBilingCandidateLangs = userProfile.getPreferredBilingCandidateLangs().toArray(new String[0]);
		String[] preferredBilingLexMeaningWordLangs = userProfile.getPreferredBilingLexMeaningWordLangs().toArray(new String[0]);
		String[] preferredMeaningRelationWordLangs = userProfile.getPreferredMeaningRelationWordLangs().toArray(new String[0]);
		boolean showLexMeaningRelationSourceLangWords = userProfile.isShowLexMeaningRelationSourceLangWords();
		boolean showMeaningRelationFirstWordOnly = userProfile.isShowMeaningRelationFirstWordOnly();
		boolean showMeaningRelationMeaningId = userProfile.isShowMeaningRelationMeaningId();
		boolean showMeaningRelationWordDatasets = userProfile.isShowMeaningRelationWordDatasets();

		EkiUserProfileRecord ekiUserProfile = create.selectFrom(EKI_USER_PROFILE).where(EKI_USER_PROFILE.USER_ID.eq(userId)).fetchOne();
		ekiUserProfile.setRecentDatasetPermissionId(recentDatasetPermissionId);
		ekiUserProfile.setPreferredDatasets(preferredDatasets);
		ekiUserProfile.setPreferredBilingCandidateLangs(preferredBilingCandidateLangs);
		ekiUserProfile.setPreferredBilingLexMeaningWordLangs(preferredBilingLexMeaningWordLangs);
		ekiUserProfile.setPreferredMeaningRelationWordLangs(preferredMeaningRelationWordLangs);
		ekiUserProfile.setShowLexMeaningRelationSourceLangWords(showLexMeaningRelationSourceLangWords);
		ekiUserProfile.setShowMeaningRelationFirstWordOnly(showMeaningRelationFirstWordOnly);
		ekiUserProfile.setShowMeaningRelationMeaningId(showMeaningRelationMeaningId);
		ekiUserProfile.setShowMeaningRelationWordDatasets(showMeaningRelationWordDatasets);

		ekiUserProfile.store();
	}

}
