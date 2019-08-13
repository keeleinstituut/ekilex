package eki.common.data;

import java.util.regex.Pattern;

public class TextDecorationDescriptor extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Pattern entityMatchPattern;

	private String preDecoration;

	private String postDecoration;

	private int applyMethod;

	public TextDecorationDescriptor(Pattern entityMatchPattern, String preDecoration, String postDecoration, int applyMethod) {
		this.entityMatchPattern = entityMatchPattern;
		this.preDecoration = preDecoration;
		this.postDecoration = postDecoration;
		this.applyMethod = applyMethod;
	}

	public Pattern getEntityMatchPattern() {
		return entityMatchPattern;
	}

	public String getPreDecoration() {
		return preDecoration;
	}

	public String getPostDecoration() {
		return postDecoration;
	}

	public int getApplyMethod() {
		return applyMethod;
	}

}
