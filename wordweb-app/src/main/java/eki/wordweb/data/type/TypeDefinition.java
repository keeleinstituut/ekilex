package eki.wordweb.data.type;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.LangType;
import eki.wordweb.data.SourceLinkType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeDefinition extends AbstractDataObject implements ComplexityType, LangType, SourceLinkType {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Long definitionId;

	private String value;

	private String valuePrese;

	private String valuePreseCut;

	private String lang;

	private Complexity complexity;

	private List<TypeFreeform> notes;

	private List<TypeSourceLink> sourceLinks;

	private boolean subDataExists;

	@Override
	public Long getOwnerId() {
		return definitionId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
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

	public String getValuePreseCut() {
		return valuePreseCut;
	}

	public void setValuePreseCut(String valuePreseCut) {
		this.valuePreseCut = valuePreseCut;
	}

	@Override
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public List<TypeFreeform> getNotes() {
		return notes;
	}

	public void setNotes(List<TypeFreeform> notes) {
		this.notes = notes;
	}

	@Override
	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public boolean isSubDataExists() {
		return subDataExists;
	}

	public void setSubDataExists(boolean subDataExists) {
		this.subDataExists = subDataExists;
	}

}
