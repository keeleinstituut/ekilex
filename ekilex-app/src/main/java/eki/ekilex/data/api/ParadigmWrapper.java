package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "List of paradigms")
public class ParadigmWrapper extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Paradigm> paradigms;

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

}
