package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class Usage extends AbstractCreateUpdateEntity implements ValueAndPrese {

	private static final long serialVersionUID = 1L;

	private Long id;
	@Schema(example = "Pihlakamarjade kobarad.")
	private String value;
	@Schema(example = "Pihlakamarjade kobarad.")
	private String valuePrese;

	private String lang;

	private Long orderBy;
	@Schema(example = "null")
	private List<UsageTranslation> translations;
	@Schema(example = "null")
	private List<SourceLink> sourceLinks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public List<UsageTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<UsageTranslation> translations) {
		this.translations = translations;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
