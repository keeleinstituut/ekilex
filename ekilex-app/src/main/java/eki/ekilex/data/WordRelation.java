package eki.ekilex.data;

public class WordRelation extends AbstractRelation {

	private static final long serialVersionUID = 1L;

	private Long groupId;

	private String groupWordRelTypeCode;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupWordRelTypeCode() {
		return groupWordRelTypeCode;
	}

	public void setGroupWordRelTypeCode(String groupWordRelTypeCode) {
		this.groupWordRelTypeCode = groupWordRelTypeCode;
	}
}
