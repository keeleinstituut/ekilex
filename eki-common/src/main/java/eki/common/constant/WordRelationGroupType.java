package eki.common.constant;

import java.util.Optional;

public enum WordRelationGroupType {
	SERIES,
	VARIANTS,
	ASPECTS;

	public static Optional<WordRelationGroupType> toRelationGroupType(String memberType) {
		try {
			return Optional.of(WordRelationGroupType.valueOf(memberType));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}
