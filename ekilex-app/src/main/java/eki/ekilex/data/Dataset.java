package eki.ekilex.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;

@JsonInclude(Include.NON_NULL)
public class Dataset extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private DatasetType type;

	private String name;

	private String description;

	private String contact;

	private String imageUrl;

	private String fedTermCollectionId;

	private String fedTermDomainId;

	private boolean isVisible;

	private boolean isPublic;

	private boolean isSuperior;

	private List<String> origins;

	private List<Classifier> domains;

	private List<Classifier> languages;

	private List<Classifier> wordFreeformTypes;

	private List<Classifier> lexemeFreeformTypes;

	private List<Classifier> meaningFreeformTypes;

	private List<Classifier> definitionFreeformTypes;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DatasetType getType() {
		return type;
	}

	public void setType(DatasetType type) {
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

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getFedTermCollectionId() {
		return fedTermCollectionId;
	}

	public void setFedTermCollectionId(String fedTermCollectionId) {
		this.fedTermCollectionId = fedTermCollectionId;
	}

	public String getFedTermDomainId() {
		return fedTermDomainId;
	}

	public void setFedTermDomainId(String fedTermDomainId) {
		this.fedTermDomainId = fedTermDomainId;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public boolean isSuperior() {
		return isSuperior;
	}

	public void setSuperior(boolean superior) {
		isSuperior = superior;
	}

	public List<String> getOrigins() {
		return origins;
	}

	public void setOrigins(List<String> origins) {
		this.origins = origins;
	}

	public List<Classifier> getDomains() {
		return domains;
	}

	public void setDomains(List<Classifier> domains) {
		this.domains = domains;
	}

	public List<Classifier> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Classifier> languages) {
		this.languages = languages;
	}

	public List<Classifier> getWordFreeformTypes() {
		return wordFreeformTypes;
	}

	public void setWordFreeformTypes(List<Classifier> wordFreeformTypes) {
		this.wordFreeformTypes = wordFreeformTypes;
	}

	public List<Classifier> getLexemeFreeformTypes() {
		return lexemeFreeformTypes;
	}

	public void setLexemeFreeformTypes(List<Classifier> lexemeFreeformTypes) {
		this.lexemeFreeformTypes = lexemeFreeformTypes;
	}

	public List<Classifier> getMeaningFreeformTypes() {
		return meaningFreeformTypes;
	}

	public void setMeaningFreeformTypes(List<Classifier> meaningFreeformTypes) {
		this.meaningFreeformTypes = meaningFreeformTypes;
	}

	public List<Classifier> getDefinitionFreeformTypes() {
		return definitionFreeformTypes;
	}

	public void setDefinitionFreeformTypes(List<Classifier> definitionFreeformTypes) {
		this.definitionFreeformTypes = definitionFreeformTypes;
	}

}
