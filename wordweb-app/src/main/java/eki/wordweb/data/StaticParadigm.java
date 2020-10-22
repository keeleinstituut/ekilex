package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.data.AbstractDataObject;

public class StaticParadigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private Map<String, List<Form>> formMorphCodeMap;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public Map<String, List<Form>> getFormMorphCodeMap() {
		return formMorphCodeMap;
	}

	public void setFormMorphCodeMap(Map<String, List<Form>> formMorphCodeMap) {
		this.formMorphCodeMap = formMorphCodeMap;
	}

}
