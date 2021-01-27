package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private String comment;

	private String inflectionType;

	private Map<String, List<Form>> formMorphCodeMap;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public void setInflectionType(String inflectionType) {
		this.inflectionType = inflectionType;
	}

	public Map<String, List<Form>> getFormMorphCodeMap() {
		return formMorphCodeMap;
	}

	public void setFormMorphCodeMap(Map<String, List<Form>> formMorphCodeMap) {
		this.formMorphCodeMap = formMorphCodeMap;
	}

}
