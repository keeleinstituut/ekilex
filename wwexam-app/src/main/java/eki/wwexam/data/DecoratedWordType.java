package eki.wwexam.data;

import java.util.List;

public interface DecoratedWordType {

	String getValue();

	String getValuePrese();

	List<String> getWordTypeCodes();

	boolean isPrefixoid();

	boolean isSuffixoid();

	boolean isForeignWord();

	boolean isAbbreviationWord();
}
