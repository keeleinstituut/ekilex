package eki.ekilex.data;

public interface DecoratedWordType {

	void setWordValue(String wordValue);

	void setWordValuePrese(String wordValuePrese);

	void setWordTypeCodes(String[] wordTypeCodes);

	void setPrefixoid(boolean isPrefixoid);

	void setSuffixoid(boolean isSuffixoid);

	void setForeign(boolean isForeign);

	String getWordValue();

	String getWordValuePrese();

	String[] getWordTypeCodes();

	boolean isPrefixoid();

	boolean isSuffixoid();

	boolean isForeign();
}
