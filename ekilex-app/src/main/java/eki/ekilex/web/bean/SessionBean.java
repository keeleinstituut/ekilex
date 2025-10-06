package eki.ekilex.web.bean;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.ClassifierSelect;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SearchResultMode termSearchResultMode;

	private String termSearchResultLang;

	private List<ClassifierSelect> languagesOrder;

	private String recentLanguage;

	private String recentNoteLanguage;

	private boolean isManualEventOnUpdateEnabled;

	private boolean isLexemeCollocExpanded;

	private boolean isMeaningFreeformExpanded;

	private boolean isMeaningImageExpanded;

	private boolean isMeaningMediaExpanded;

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

	public String getRecentLanguage() {
		return recentLanguage;
	}

	public void setRecentLanguage(String recentLanguage) {
		this.recentLanguage = recentLanguage;
	}

	public String getRecentNoteLanguage() {
		return recentNoteLanguage;
	}

	public void setRecentNoteLanguage(String recentNoteLanguage) {
		this.recentNoteLanguage = recentNoteLanguage;
	}

	public boolean isManualEventOnUpdateEnabled() {
		return isManualEventOnUpdateEnabled;
	}

	public void setManualEventOnUpdateEnabled(boolean manualEventOnUpdateEnabled) {
		isManualEventOnUpdateEnabled = manualEventOnUpdateEnabled;
	}

	public boolean isLexemeCollocExpanded() {
		return isLexemeCollocExpanded;
	}

	public void setLexemeCollocExpanded(boolean isLexemeCollocExpanded) {
		this.isLexemeCollocExpanded = isLexemeCollocExpanded;
	}

	public boolean isMeaningFreeformExpanded() {
		return isMeaningFreeformExpanded;
	}

	public void setMeaningFreeformExpanded(boolean isMeaningFreeformExpanded) {
		this.isMeaningFreeformExpanded = isMeaningFreeformExpanded;
	}

	public boolean isMeaningImageExpanded() {
		return isMeaningImageExpanded;
	}

	public void setMeaningImageExpanded(boolean isMeaningImageExpanded) {
		this.isMeaningImageExpanded = isMeaningImageExpanded;
	}

	public boolean isMeaningMediaExpanded() {
		return isMeaningMediaExpanded;
	}

	public void setMeaningMediaExpanded(boolean isMeaningMediaExpanded) {
		this.isMeaningMediaExpanded = isMeaningMediaExpanded;
	}

}
