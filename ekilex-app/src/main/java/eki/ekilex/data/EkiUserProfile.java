package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserProfile extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long recentDatasetPermissionId;

	private List<String> preferredDatasets;

	private List<String> preferredBilingCandidateLangs;

	private List<String> preferredBilingLexMeaningWordLangs;

	public Long getRecentDatasetPermissionId() {
		return recentDatasetPermissionId;
	}

	public void setRecentDatasetPermissionId(Long recentDatasetPermissionId) {
		this.recentDatasetPermissionId = recentDatasetPermissionId;
	}

	public List<String> getPreferredDatasets() {
		return preferredDatasets;
	}

	public void setPreferredDatasets(List<String> preferredDatasets) {
		this.preferredDatasets = preferredDatasets;
	}

	public List<String> getPreferredBilingCandidateLangs() {
		return preferredBilingCandidateLangs;
	}

	public void setPreferredBilingCandidateLangs(List<String> preferredBilingCandidateLangs) {
		this.preferredBilingCandidateLangs = preferredBilingCandidateLangs;
	}

	public List<String> getPreferredBilingLexMeaningWordLangs() {
		return preferredBilingLexMeaningWordLangs;
	}

	public void setPreferredBilingLexMeaningWordLangs(List<String> preferredBilingLexMeaningWordLangs) {
		this.preferredBilingLexMeaningWordLangs = preferredBilingLexMeaningWordLangs;
	}
}
