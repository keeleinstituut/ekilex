package eki.wordweb.data;

import java.util.List;

public class Usage extends AbstractCreateUpdateEntity implements SourceLinkType, LangType {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String valuePrese;

	private String lang;

	private boolean langEst;

	private List<String> usageTranslationValues;

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

	public boolean isLangEst() {
		return langEst;
	}

	public void setLangEst(boolean langEst) {
		this.langEst = langEst;
	}

	public List<String> getUsageTranslationValues() {
		return usageTranslationValues;
	}

	public void setUsageTranslationValues(List<String> usageTranslationValues) {
		this.usageTranslationValues = usageTranslationValues;
	}

	@Override
	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
