package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class TypeUsage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String usage;

	private String usageAuthor;

	private String usageTranslator;

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getUsageAuthor() {
		return usageAuthor;
	}

	public void setUsageAuthor(String usageAuthor) {
		this.usageAuthor = usageAuthor;
	}

	public String getUsageTranslator() {
		return usageTranslator;
	}

	public void setUsageTranslator(String usageTranslator) {
		this.usageTranslator = usageTranslator;
	}

}
