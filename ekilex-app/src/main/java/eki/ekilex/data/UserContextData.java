package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class UserContextData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long userId;

	private String userName;

	private EkiUser user;

	private DatasetPermission userRole;

	private String userRoleDatasetCode;

	private Tag activeTag;

	private List<String> tagNames;

	private List<String> preferredDatasetCodes;

	private List<String> partSynCandidateLangCodes;

	private List<String> synMeaningWordLangCodes;

	private String fullSynCandidateLangCode;

	private String fullSynCandidateDatasetCode;

	public UserContextData(
			Long userId, String userName, EkiUser user, DatasetPermission userRole, String userRoleDatasetCode, Tag activeTag, List<String> tagNames,
			List<String> preferredDatasetCodes, List<String> partSynCandidateLangCodes, List<String> synMeaningWordLangCodes,
			String fullSynCandidateLangCode, String fullSynCandidateDatasetCode) {
		this.userId = userId;
		this.userName = userName;
		this.user = user;
		this.userRole = userRole;
		this.userRoleDatasetCode = userRoleDatasetCode;
		this.activeTag = activeTag;
		this.tagNames = tagNames;
		this.preferredDatasetCodes = preferredDatasetCodes;
		this.partSynCandidateLangCodes = partSynCandidateLangCodes;
		this.synMeaningWordLangCodes = synMeaningWordLangCodes;
		this.fullSynCandidateLangCode = fullSynCandidateLangCode;
		this.fullSynCandidateDatasetCode = fullSynCandidateDatasetCode;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public EkiUser getUser() {
		return user;
	}

	public DatasetPermission getUserRole() {
		return userRole;
	}

	public String getUserRoleDatasetCode() {
		return userRoleDatasetCode;
	}

	public Tag getActiveTag() {
		return activeTag;
	}

	public List<String> getTagNames() {
		return tagNames;
	}

	public List<String> getPreferredDatasetCodes() {
		return preferredDatasetCodes;
	}

	public List<String> getPartSynCandidateLangCodes() {
		return partSynCandidateLangCodes;
	}

	public List<String> getSynMeaningWordLangCodes() {
		return synMeaningWordLangCodes;
	}

	public String getFullSynCandidateLangCode() {
		return fullSynCandidateLangCode;
	}

	public String getFullSynCandidateDatasetCode() {
		return fullSynCandidateDatasetCode;
	}
}
