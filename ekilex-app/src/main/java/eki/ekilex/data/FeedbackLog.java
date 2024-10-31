package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class FeedbackLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Timestamp createdOn;

	private String feedbackType;

	@Deprecated
	private String senderName;

	private String senderEmail;

	private String description;

	private String word;

	private String definition;

	@Deprecated
	private String definitionSource;

	@Deprecated
	private String domain;

	private String usage;

	@Deprecated
	private String usageSource;

	private String otherInfo;

	@Deprecated
	private String company;

	private String lastSearch;

	private List<FeedbackLogComment> feedbackLogComments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDefinitionSource() {
		return definitionSource;
	}

	public void setDefinitionSource(String definitionSource) {
		this.definitionSource = definitionSource;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getUsageSource() {
		return usageSource;
	}

	public void setUsageSource(String usageSource) {
		this.usageSource = usageSource;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(String lastSearch) {
		this.lastSearch = lastSearch;
	}

	public List<FeedbackLogComment> getFeedbackLogComments() {
		return feedbackLogComments;
	}

	public void setFeedbackLogComments(List<FeedbackLogComment> feedbackLogComments) {
		this.feedbackLogComments = feedbackLogComments;
	}

}
