package eki.wordweb.data;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class DataFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sourceLang;

	private String destinLang;

	private Complexity lexComplexity;

	private Complexity dataComplexity;

	private Integer maxDisplayLevel;

	public DataFilter(String sourceLang, String destinLang, Complexity lexComplexity, Complexity dataComplexity, Integer maxDisplayLevel) {
		this.sourceLang = sourceLang;
		this.destinLang = destinLang;
		this.lexComplexity = lexComplexity;
		this.dataComplexity = dataComplexity;
		this.maxDisplayLevel = maxDisplayLevel;
	}

	public String getSourceLang() {
		return sourceLang;
	}

	public String getDestinLang() {
		return destinLang;
	}

	public Complexity getLexComplexity() {
		return lexComplexity;
	}

	public Complexity getDataComplexity() {
		return dataComplexity;
	}

	public Integer getMaxDisplayLevel() {
		return maxDisplayLevel;
	}

}
