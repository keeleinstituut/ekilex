package eki.ekilex.web.bean;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SearchResultMode termSearchResultMode;

	private String termSearchResultLang;

	private List<ClassifierSelect> languagesOrder;

	private String newWordSelectedLanguage;

	private String newWordSelectedMorphCode;

	private DatasetPermission userRole;

	public SearchResultMode getTermSearchResultMode() {
		return termSearchResultMode;
	}

	public void setTermSearchResultMode(SearchResultMode termSearchResultMode) {
		this.termSearchResultMode = termSearchResultMode;
	}

	public String getTermSearchResultLang() {
		return termSearchResultLang;
	}

	public void setTermSearchResultLang(String resultLang) {
		this.termSearchResultLang = resultLang;
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
