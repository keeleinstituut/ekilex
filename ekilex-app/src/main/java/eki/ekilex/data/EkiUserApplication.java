package eki.ekilex.data;

import java.sql.Timestamp;

import eki.common.constant.AuthorityOperation;
import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.ApplicationStatus;

public class EkiUserApplication extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long userId;

	private String datasetCode;

	private String datasetName;

	private AuthorityOperation authOperation;

	private String lang;

	private String langValue;

	private String comment;

	private ApplicationStatus status;

	private Timestamp created;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public AuthorityOperation getAuthOperation() {
		return authOperation;
	}

	public void setAuthOperation(AuthorityOperation authOperation) {
		this.authOperation = authOperation;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLangValue() {
		return langValue;
	}

	public void setLangValue(String langValue) {
		this.langValue = langValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}
}
