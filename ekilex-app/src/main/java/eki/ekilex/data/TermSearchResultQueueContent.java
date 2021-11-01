package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermSearchResultQueueContent extends AbstractDataObject implements QueueContent {

	private static final long serialVersionUID = 1L;

	private EkiUserProfile userProfile;

	private List<ClassifierSelect> languagesOrder;

	private String searchUri;

	private SearchUriData searchUriData;

	public EkiUserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(EkiUserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public List<ClassifierSelect> getLanguagesOrder() {
		return languagesOrder;
	}

	public void setLanguagesOrder(List<ClassifierSelect> languagesOrder) {
		this.languagesOrder = languagesOrder;
	}

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	public SearchUriData getSearchUriData() {
		return searchUriData;
	}

	public void setSearchUriData(SearchUriData searchUriData) {
		this.searchUriData = searchUriData;
	}

}
