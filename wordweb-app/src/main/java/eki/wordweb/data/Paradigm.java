package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private String vocalForm;

	private String morphComment;

	private String paradigmComment;

	private String inflectionType;

	private String inflectionTypeNr;

	private String wordClass;

	private Map<String, List<Form>> formMorphCodeMap;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getMorphComment() {
		return morphComment;
	}

	public void setMorphComment(String morphComment) {
		this.morphComment = morphComment;
	}

	public String getParadigmComment() {
		return paradigmComment;
	}

	public void setParadigmComment(String paradigmComment) {
		this.paradigmComment = paradigmComment;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public void setInflectionType(String inflectionType) {
		this.inflectionType = inflectionType;
	}

	public String getInflectionTypeNr() {
		return inflectionTypeNr;
	}

	public void setInflectionTypeNr(String inflectionTypeNr) {
		this.inflectionTypeNr = inflectionTypeNr;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public Map<String, List<Form>> getFormMorphCodeMap() {
		return formMorphCodeMap;
	}

	public void setFormMorphCodeMap(Map<String, List<Form>> formMorphCodeMap) {
		this.formMorphCodeMap = formMorphCodeMap;
	}

}
