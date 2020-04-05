package eki.wordweb.data;

import java.util.List;

public interface DecoratedWordType {

	String getWord();

	String getWordPrese();

	List<String> getWordTypeCodes();

	boolean isPrefixoid();

	boolean isSuffixoid();

	boolean isForeignWord();
}
