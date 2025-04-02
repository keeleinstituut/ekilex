package eki.ekilex.data;

public class WordOdRecommendation extends AbstractCreateUpdateEntity implements  ValueAndPrese {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private String value;

	private String valuePrese;

	private String optValue;

	private String optValuePrese;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValuePrese() {
		return valuePrese;
	}

	@Override
	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public String getOptValue() {
		return optValue;
	}

	public void setOptValue(String optValue) {
		this.optValue = optValue;
	}

	public String getOptValuePrese() {
		return optValuePrese;
	}

	public void setOptValuePrese(String optValuePrese) {
		this.optValuePrese = optValuePrese;
	}

}
