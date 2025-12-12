package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Word morphology in the dictionary Õigekeelsussõnaraamat")
public class WordOsMorph extends AbstractCreateUpdateEntity implements ValueAndPrese {

	private static final long serialVersionUID = 1L;
	@Schema(example = "135846")
	private Long id;
	@Schema(example = "182736")
	private Long wordId;
	@Schema(example = "kobar, -a, -at 2")
	private String value;
	@Schema(example = "kobar, -a, -at <ext-link href=\"https://eki.ee/teatmik/kaandsona-tuup-2-opik-minut-akvaarium/#2.-opik\">2</ext-link>")
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
