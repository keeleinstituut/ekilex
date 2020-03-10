package eki.wordweb.data;

public interface DecoratedWordType {

	String getWord();

	String getWordPrese();

	boolean isPrefixoid();

	boolean isSuffixoid();

	boolean isForeignWord();
}
