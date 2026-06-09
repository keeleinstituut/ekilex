package eki.ekilex.data.etym2;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.WordEtymGroupType;

public class WordEtymGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private WordEtymGroupType groupType;

	private String etymologyTypeCode;

	private Long languageGroupId;

	private String languageGroupName;

	private boolean questionable;

	private List<WordEtymGroupMember> groupMembers;

	private Long orderBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WordEtymGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(WordEtymGroupType groupType) {
		this.groupType = groupType;
	}

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public Long getLanguageGroupId() {
		return languageGroupId;
	}

	public void setLanguageGroupId(Long languageGroupId) {
		this.languageGroupId = languageGroupId;
	}

	public String getLanguageGroupName() {
		return languageGroupName;
	}

	public void setLanguageGroupName(String languageGroupName) {
		this.languageGroupName = languageGroupName;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

	public List<WordEtymGroupMember> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(List<WordEtymGroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
