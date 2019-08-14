package eki.ekilex.web.bean;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String resultLang;

	private List<ClassifierSelect> languagesOrder;

	private String newWordSelectedLanguage;

	private String newWordSelectedMorphCode;

	private DatasetPermission userRole;

	public String getResultLang() {
		return resultLang;
	}

	public void setResultLang(String resultLang) {
		this.resultLang = resultLang;
	}

	public List<ClassifierSelect> getLanguagesOrder() {
		return languagesOrder;
	}

	public void setLanguagesOrder(List<ClassifierSelect> languagesOrder) {
		this.languagesOrder = languagesOrder;
	}

	public String getNewWordSelectedLanguage() {
		return newWordSelectedLanguage;
	}

	public void setNewWordSelectedLanguage(String newWordSelectedLanguage) {
		this.newWordSelectedLanguage = newWordSelectedLanguage;
	}

	public String getNewWordSelectedMorphCode() {
		return newWordSelectedMorphCode;
	}

	public void setNewWordSelectedMorphCode(String newWordSelectedMorphCode) {
		this.newWordSelectedMorphCode = newWordSelectedMorphCode;
	}

	public DatasetPermission getUserRole() {
		return userRole;
	}

	public void setUserRole(DatasetPermission userRole) {
		this.userRole = userRole;
	}

}
