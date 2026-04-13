package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LanguageGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private List<String> languageCodes;

	private List<Classifier> languageGroupMembers;

	private int memberCount;

	private boolean memberExists;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getLanguageCodes() {
		return languageCodes;
	}

	public void setLanguageCodes(List<String> languageCodes) {
		this.languageCodes = languageCodes;
	}

	public List<Classifier> getLanguageGroupMembers() {
		return languageGroupMembers;
	}

	public void setLanguageGroupMembers(List<Classifier> languageGroupMembers) {
		this.languageGroupMembers = languageGroupMembers;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public boolean isMemberExists() {
		return memberExists;
	}

	public void setMemberExists(boolean memberExists) {
		this.memberExists = memberExists;
	}

}
