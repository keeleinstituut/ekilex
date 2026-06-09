package eki.ekilex.data.etym2;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymLevel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Integer level;

	private List<WordEtymGroup> groups;

	private int groupCount;

	private boolean singleGroup;

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<WordEtymGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<WordEtymGroup> groups) {
		this.groups = groups;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public boolean isSingleGroup() {
		return singleGroup;
	}

	public void setSingleGroup(boolean singleGroup) {
		this.singleGroup = singleGroup;
	}

}
