package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class FormPair extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Form form1;

	private Form form2;

	public Form getForm1() {
		return form1;
	}

	public void setForm1(Form form1) {
		this.form1 = form1;
	}

	public Form getForm2() {
		return form2;
	}

	public void setForm2(Form form2) {
		this.form2 = form2;
	}

}
