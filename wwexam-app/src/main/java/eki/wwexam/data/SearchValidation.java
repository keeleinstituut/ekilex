package eki.wwexam.data;

public class SearchValidation extends SearchFilter {

	private static final long serialVersionUID = 1L;

	private String searchUri;

	private boolean valid;

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
