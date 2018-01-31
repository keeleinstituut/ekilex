package eki.eve.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Government extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private List<UsageMeaning> usageMeanings;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<UsageMeaning> getUsageMeanings() {
		return usageMeanings;
	}

	public void setUsageMeanings(List<UsageMeaning> usageMeanings) {
		this.usageMeanings = usageMeanings;
	}

	public boolean isMeaningful() {
		return (usageMeanings != null) && (usageMeanings.size() > 0);
	}
}
