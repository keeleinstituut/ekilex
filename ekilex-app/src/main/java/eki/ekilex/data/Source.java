package eki.ekilex.data;

import java.util.List;

import eki.common.constant.SourceType;

public class Source extends AbstractCrudEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private SourceType type;

	private String name;

	private String description;

	private String comment;

	private boolean isPublic;

	private List<String> nameTypeSourceProperties;

	private List<SourceProperty> sourceProperties;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<String> getNameTypeSourceProperties() {
		return nameTypeSourceProperties;
	}

	public void setNameTypeSourceProperties(List<String> nameTypeSourceProperties) {
		this.nameTypeSourceProperties = nameTypeSourceProperties;
	}

	public List<SourceProperty> getSourceProperties() {
		return sourceProperties;
	}

	public void setSourceProperties(List<SourceProperty> sourceProperties) {
		this.sourceProperties = sourceProperties;
	}
}
