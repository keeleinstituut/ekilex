package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.Schema;

public class WordOsUsage extends AbstractCreateUpdateEntity implements ValueAndPrese {

	private static final long serialVersionUID = 1L;
	@Schema(example = "20441")
	private Long id;
	@Schema(example = "182736")
	private Long wordId;
	@Schema(example = "Kobar viinamarju = viinamarjakobar.")
	private String value;

	private String valuePrese;

	private Long orderBy;

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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
