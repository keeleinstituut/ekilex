package eki.eve.data;

import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Rection extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "rection")
	private String value;

	@Column(name = "usages")
	private String[] usages;

	public Rection() {
	}

	public Rection(Consumer<Rection> builder) {
		builder.accept(this);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[] getUsages() {
		return usages;
	}

	public void setUsages(String[] usages) {
		this.usages = usages;
	}

}
