package eki.ekilex.data;

public class WordOdMorph extends AbstractCreateUpdateEntity implements ValueAndPrese {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private String value;

	private String valuePrese;

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

}
