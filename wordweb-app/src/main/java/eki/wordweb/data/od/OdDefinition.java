package eki.wordweb.data.od;

import eki.common.data.AbstractDataObject;

public class OdDefinition extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private Long meaningId;

	private String value;

	private String valuePrese;

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

}
