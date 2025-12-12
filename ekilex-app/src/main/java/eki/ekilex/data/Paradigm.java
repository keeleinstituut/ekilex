package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Morphological paradigm with forms and metadata")
public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;
	@Schema(example = "1293387")
	private Long paradigmId;
	@Schema(example = "null")
	private String comment;
	@Schema(example = "2")
	private String inflectionType;
	@Schema(example = "2")
	private String inflectionTypeNr;
	@Schema(example = "noomen")
	private String wordClass;

	private List<Form> forms;

	private boolean formsExist;

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

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public boolean isFormsExist() {
		return formsExist;
	}

	public void setFormsExist(boolean formsExist) {
		this.formsExist = formsExist;
	}

}
