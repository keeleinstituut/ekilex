package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.util.LocalDateTimeDeserialiser;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "An entity that holds definitions that explain the same meaning")
public class Meaning extends AbstractGrantEntity {

	private static final long serialVersionUID = 1L;
	@Schema(example = "658908")
	private Long meaningId;
	@Schema(example = "null")
	private String firstWordValue;
	@Schema(example = "null")
	private List<Long> lexemeIds;

	private List<Definition> definitions;

	private List<DefinitionLangGroup> definitionLangGroups;

	private List<Lexeme> lexemes;
	@Schema(example = "null")
	private List<LexemeLangGroup> lexemeLangGroups;
	@Schema(example = "null")
	private List<String> lexemeDatasetCodes;
	@Schema(example = "[]")
	private List<OrderedClassifier> domains;
	@Schema(example = "[\n" +
			"                    {\n" +
			"                        \"name\": \"SEMANTIC_TYPE\",\n" +
			"                        \"code\": \"taim\",\n" +
			"                        \"value\": \"taim, taimestik\"\n" +
			"                    }\n" +
			"                ]")
	private List<Classifier> semanticTypes;
	@Schema(example = "[]")
	private List<Freeform> freeforms;
	@Schema(example = "[]")
	private List<LearnerComment> learnerComments;
	@Schema(example = "[]")
	private List<MeaningImage> images;
	@Schema(example = "[]")
	private List<MeaningMedia> medias;
	@Schema(example = "[]")
	private List<MeaningForum> forums;
	@Schema(example = "[]")
	private List<NoteLangGroup> noteLangGroups;
	@Schema(example = "[]")
	private List<MeaningRelation> relations;
	@Schema(example = "[]")
	private List<List<MeaningRelation>> viewRelations;
	@Schema(example = "[]")
	private List<SynonymLangGroup> synonymLangGroups;
	@Schema(example = "null")
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

	public List<MeaningImage> getImages() {
		return images;
	}

	public void setImages(List<MeaningImage> images) {
		this.images = images;
	}

	public List<MeaningMedia> getMedias() {
		return medias;
	}

	public void setMedias(List<MeaningMedia> medias) {
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
