package eki.ekilex.data.etym2;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.WordEtymGroupType;

public class WordEtymGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private WordEtymGroupType groupType;

	private String etymologyTypeCode;

	private Long languageGroupMemberId;

	private boolean questionable;

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

	public Long getLanguageGroupMemberId() {
		return languageGroupMemberId;
	}

	public void setLanguageGroupMemberId(Long languageGroupMemberId) {
		this.languageGroupMemberId = languageGroupMemberId;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

}
