package eki.common.constant;

public enum WordRelationGroupType {

	SERIES, VARIANTS, ASPECTS;

	public static WordRelationGroupType toRelationGroupType(String memberType) {
		try {
			return WordRelationGroupType.valueOf(memberType);
		} catch (Exception e) {
			return null;
		}
	}
}
