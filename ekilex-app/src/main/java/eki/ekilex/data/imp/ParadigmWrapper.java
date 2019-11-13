package eki.ekilex.data.imp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import eki.common.data.AbstractDataObject;

public class ParadigmWrapper extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@JsonProperty("paradigm")
	private List<Paradigm> paradigms;

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

}
