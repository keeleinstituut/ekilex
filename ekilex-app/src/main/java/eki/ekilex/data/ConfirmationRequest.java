package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class ConfirmationRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String opName;

	private String opCode;

	private Long id;

	private List<String> questions;

	private boolean unconfirmed;

	private String validationMessage;

	private boolean valid;

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}

	public boolean isUnconfirmed() {
		return unconfirmed;
	}

	public void setUnconfirmed(boolean unconfirmedDialogueExists) {
		this.unconfirmed = unconfirmedDialogueExists;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
