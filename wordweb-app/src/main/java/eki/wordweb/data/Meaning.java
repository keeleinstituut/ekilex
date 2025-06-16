package eki.wordweb.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.data.AbstractDataObject;
import eki.common.util.LocalDateTimeDeserialiser;
import eki.wordweb.data.type.TypeDomain;

public class Meaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime meaningManualEventOn;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime meaningLastActivityEventOn;

	private List<TypeDomain> domainCodes;

	private List<Definition> definitions;

	private List<MeaningImage> meaningImages;

	private List<MeaningMedia> meaningMedias;

	private List<String> semanticTypes;

	private List<String> learnerComments;

	private List<Note> meaningNotes;

	private List<MeaningRelation> relatedMeanings;

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

	public LocalDateTime getMeaningManualEventOn() {
		return meaningManualEventOn;
	}

	public void setMeaningManualEventOn(LocalDateTime meaningManualEventOn) {
		this.meaningManualEventOn = meaningManualEventOn;
	}

	public LocalDateTime getMeaningLastActivityEventOn() {
		return meaningLastActivityEventOn;
	}

	public void setMeaningLastActivityEventOn(LocalDateTime meaningLastActivityEventOn) {
		this.meaningLastActivityEventOn = meaningLastActivityEventOn;
	}

	public List<TypeDomain> getDomainCodes() {
		return domainCodes;
	}

	public void setDomainCodes(List<TypeDomain> domainCodes) {
		this.domainCodes = domainCodes;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<MeaningImage> getMeaningImages() {
		return meaningImages;
	}

	public void setMeaningImages(List<MeaningImage> meaningImages) {
		this.meaningImages = meaningImages;
	}

	public List<MeaningMedia> getMeaningMedias() {
		return meaningMedias;
	}

	public void setMeaningMedias(List<MeaningMedia> meaningMedias) {
		this.meaningMedias = meaningMedias;
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

	public List<Note> getMeaningNotes() {
		return meaningNotes;
	}

	public void setMeaningNotes(List<Note> meaningNotes) {
		this.meaningNotes = meaningNotes;
	}

	public List<MeaningRelation> getRelatedMeanings() {
		return relatedMeanings;
	}

	public void setRelatedMeanings(List<MeaningRelation> relatedMeanings) {
		this.relatedMeanings = relatedMeanings;
	}

}
