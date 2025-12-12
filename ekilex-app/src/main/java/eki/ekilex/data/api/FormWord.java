package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

public class FormWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;
	@Schema(example = "202944")
	private Long wordId;
	@Schema(example = "minema")
	private String wordValue;
	@Schema(example = "est")
	private String lang;
	@Schema(example = "1")
	private Integer homonymNr;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

}
