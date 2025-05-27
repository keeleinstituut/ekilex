package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.util.LocalDateTimeDeserialiser;

public class Meaning extends AbstractGrantEntity {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String firstWordValue;

	private List<Long> lexemeIds;

	private List<Definition> definitions;

	private List<DefinitionLangGroup> definitionLangGroups;

	private List<Lexeme> lexemes;

	private List<LexemeLangGroup> lexemeLangGroups;

	private List<String> lexemeDatasetCodes;

	private List<OrderedClassifier> domains;

	private List<Classifier> semanticTypes;

	private List<Freeform> freeforms;

	private List<LearnerComment> learnerComments;

	private List<Media> images;

	private List<Media> medias;

	private List<MeaningForum> forums;

	private List<NoteLangGroup> noteLangGroups;

	private List<MeaningRelation> relations;

	private List<List<MeaningRelation>> viewRelations;

	private List<SynonymLangGroup> synonymLangGroups;

	private List<String> tags;

	private boolean activeTagComplete;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastActivityEventOn;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastApproveEventOn;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime manualEventOn;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getFirstWordValue() {
		return firstWordValue;
	}

	public void setFirstWordValue(String firstWordValue) {
		this.firstWordValue = firstWordValue;
	}

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<DefinitionLangGroup> getDefinitionLangGroups() {
		return definitionLangGroups;
	}

	public void setDefinitionLangGroups(List<DefinitionLangGroup> definitionLangGroups) {
		this.definitionLangGroups = definitionLangGroups;
	}

	public List<Lexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<Lexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<LexemeLangGroup> getLexemeLangGroups() {
		return lexemeLangGroups;
	}

	public void setLexemeLangGroups(List<LexemeLangGroup> lexemeLangGroups) {
		this.lexemeLangGroups = lexemeLangGroups;
	}

	public List<String> getLexemeDatasetCodes() {
		return lexemeDatasetCodes;
	}

	public void setLexemeDatasetCodes(List<String> lexemeDatasetCodes) {
		this.lexemeDatasetCodes = lexemeDatasetCodes;
	}

	public List<OrderedClassifier> getDomains() {
		return domains;
	}

	public void setDomains(List<OrderedClassifier> domains) {
		this.domains = domains;
	}

	public List<Classifier> getSemanticTypes() {
		return semanticTypes;
	}

	public void setSemanticTypes(List<Classifier> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}

	public List<Freeform> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<Freeform> freeforms) {
		this.freeforms = freeforms;
	}

	public List<LearnerComment> getLearnerComments() {
		return learnerComments;
	}

	public void setLearnerComments(List<LearnerComment> learnerComments) {
		this.learnerComments = learnerComments;
	}

	public List<Media> getImages() {
		return images;
	}

	public void setImages(List<Media> images) {
		this.images = images;
	}

	public List<Media> getMedias() {
		return medias;
	}

	public void setMedias(List<Media> medias) {
		this.medias = medias;
	}

	public List<MeaningForum> getForums() {
		return forums;
	}

	public void setForums(List<MeaningForum> forums) {
		this.forums = forums;
	}

	public List<NoteLangGroup> getNoteLangGroups() {
		return noteLangGroups;
	}

	public void setNoteLangGroups(List<NoteLangGroup> noteLangGroups) {
		this.noteLangGroups = noteLangGroups;
	}

	public List<MeaningRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<MeaningRelation> relations) {
		this.relations = relations;
	}

	public List<List<MeaningRelation>> getViewRelations() {
		return viewRelations;
	}

	public void setViewRelations(List<List<MeaningRelation>> viewRelations) {
		this.viewRelations = viewRelations;
	}

	public List<SynonymLangGroup> getSynonymLangGroups() {
		return synonymLangGroups;
	}

	public void setSynonymLangGroups(List<SynonymLangGroup> synonymLangGroups) {
		this.synonymLangGroups = synonymLangGroups;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean isActiveTagComplete() {
		return activeTagComplete;
	}

	public void setActiveTagComplete(boolean activeTagComplete) {
		this.activeTagComplete = activeTagComplete;
	}

	public LocalDateTime getLastActivityEventOn() {
		return lastActivityEventOn;
	}

	public void setLastActivityEventOn(LocalDateTime lastActivityEventOn) {
		this.lastActivityEventOn = lastActivityEventOn;
	}

	public LocalDateTime getLastApproveEventOn() {
		return lastApproveEventOn;
	}

	public void setLastApproveEventOn(LocalDateTime lastApproveEventOn) {
		this.lastApproveEventOn = lastApproveEventOn;
	}

	public LocalDateTime getManualEventOn() {
		return manualEventOn;
	}

	public void setManualEventOn(LocalDateTime manualEventOn) {
		this.manualEventOn = manualEventOn;
	}

}
