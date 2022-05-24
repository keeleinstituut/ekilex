package eki.wordweb.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeDomain;
import eki.wordweb.data.type.TypeFreeform;
import eki.wordweb.data.type.TypeMeaningRelation;
import eki.wordweb.data.type.TypeMediaFile;
import eki.wordweb.data.type.TypeSourceLink;

public class Meaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Timestamp meaningManualEventOn;

	private Timestamp meaningLastActivityEventOn;

	private List<TypeDomain> domainCodes;

	private List<TypeMediaFile> imageFiles;

	private List<TypeMediaFile> mediaFiles;

	private List<String> systematicPolysemyPatterns;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<TypeFreeform> notes;

	private List<TypeDefinition> definitions;

	private List<TypeMeaningRelation> relatedMeanings;

	private List<TypeSourceLink> freeformSourceLinks;

	private List<TypeSourceLink> definitionSourceLinks;

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

	public Timestamp getMeaningManualEventOn() {
		return meaningManualEventOn;
	}

	public void setMeaningManualEventOn(Timestamp meaningManualEventOn) {
		this.meaningManualEventOn = meaningManualEventOn;
	}

	public Timestamp getMeaningLastActivityEventOn() {
		return meaningLastActivityEventOn;
	}

	public void setMeaningLastActivityEventOn(Timestamp meaningLastActivityEventOn) {
		this.meaningLastActivityEventOn = meaningLastActivityEventOn;
	}

	public List<TypeDomain> getDomainCodes() {
		return domainCodes;
	}

	public void setDomainCodes(List<TypeDomain> domainCodes) {
		this.domainCodes = domainCodes;
	}

	public List<TypeMediaFile> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<TypeMediaFile> imageFiles) {
		this.imageFiles = imageFiles;
	}

	public List<TypeMediaFile> getMediaFiles() {
		return mediaFiles;
	}

	public void setMediaFiles(List<TypeMediaFile> mediaFiles) {
		this.mediaFiles = mediaFiles;
	}

	public List<String> getSystematicPolysemyPatterns() {
		return systematicPolysemyPatterns;
	}

	public void setSystematicPolysemyPatterns(List<String> systematicPolysemyPatterns) {
		this.systematicPolysemyPatterns = systematicPolysemyPatterns;
	}

	public List<String> getSemanticTypes() {
		return semanticTypes;
	}

	public void setSemanticTypes(List<String> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}

	public List<String> getLearnerComments() {
		return learnerComments;
	}

	public void setLearnerComments(List<String> learnerComments) {
		this.learnerComments = learnerComments;
	}

	public List<TypeFreeform> getNotes() {
		return notes;
	}

	public void setNotes(List<TypeFreeform> notes) {
		this.notes = notes;
	}

	public List<TypeDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TypeDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<TypeMeaningRelation> getRelatedMeanings() {
		return relatedMeanings;
	}

	public void setRelatedMeanings(List<TypeMeaningRelation> relatedMeanings) {
		this.relatedMeanings = relatedMeanings;
	}

	public List<TypeSourceLink> getFreeformSourceLinks() {
		return freeformSourceLinks;
	}

	public void setFreeformSourceLinks(List<TypeSourceLink> freeformSourceLinks) {
		this.freeformSourceLinks = freeformSourceLinks;
	}

	public List<TypeSourceLink> getDefinitionSourceLinks() {
		return definitionSourceLinks;
	}

	public void setDefinitionSourceLinks(List<TypeSourceLink> definitionSourceLinks) {
		this.definitionSourceLinks = definitionSourceLinks;
	}

}
