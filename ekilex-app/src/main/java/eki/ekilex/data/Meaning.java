package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

public class Meaning extends AbstractCrudEntity {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<Long> lexemeIds;

	private List<Definition> definitions;

	private List<DefinitionLangGroup> definitionLangGroups;

	private List<Lexeme> lexemes;

	private List<LexemeLangGroup> lexemeLangGroups;

	private List<String> lexemeDatasetCodes;

	private String firstWordValue;

	private List<OrderedClassifier> domains;

	private List<Classifier> semanticTypes;

	private List<FreeForm> freeforms;

	private List<FreeForm> learnerComments;

	private List<Media> images;

	private List<Media> medias;

	private List<MeaningForum> forums;

	private List<NoteLangGroup> noteLangGroups;

	private List<MeaningRelation> relations;

	private List<List<MeaningRelation>> viewRelations;

	private List<SynonymLangGroup> synonymLangGroups;

	private List<String> tags;

	private boolean activeTagComplete;

	private Timestamp lastActivityEventOn;

	private Timestamp lastApproveEventOn;

	private Timestamp manualEventOn;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
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

	public String getFirstWordValue() {
		return firstWordValue;
	}

	public void setFirstWordValue(String firstWordValue) {
		this.firstWordValue = firstWordValue;
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

	public List<FreeForm> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<FreeForm> freeforms) {
		this.freeforms = freeforms;
	}

	public List<FreeForm> getLearnerComments() {
		return learnerComments;
	}

	public void setLearnerComments(List<FreeForm> learnerComments) {
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

	public Timestamp getLastActivityEventOn() {
		return lastActivityEventOn;
	}

	public void setLastActivityEventOn(Timestamp lastActivityEventOn) {
		this.lastActivityEventOn = lastActivityEventOn;
	}

	public Timestamp getLastApproveEventOn() {
		return lastApproveEventOn;
	}

	public void setLastApproveEventOn(Timestamp lastApproveEventOn) {
		this.lastApproveEventOn = lastApproveEventOn;
	}

	public Timestamp getManualEventOn() {
		return manualEventOn;
	}

	public void setManualEventOn(Timestamp manualEventOn) {
		this.manualEventOn = manualEventOn;
	}
}
