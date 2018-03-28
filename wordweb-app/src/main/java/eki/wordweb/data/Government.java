package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Government extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long governmentId;

	private String government;

	private List<UsageMeaning> usageMeanings;

	public Long getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(Long governmentId) {
		this.governmentId = governmentId;
	}

	public String getGovernment() {
		return government;
	}

	public void setGovernment(String government) {
		this.government = government;
	}

	public List<UsageMeaning> getUsageMeanings() {
		return usageMeanings;
	}

	public void setUsageMeanings(List<UsageMeaning> usageMeanings) {
		this.usageMeanings = usageMeanings;
	}

}
