package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

public class Meaning extends AbstractCrudEntity {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<Long> lexemeIds;

	private List<Definition> definitions;

	private List<DefinitionLangGroup> definitionLangGroups;

	private List<LexemeLangGroup> lexemeLangGroups;

	private List<OrderedClassifier> domains;

	private List<Classifier> semanticTypes;

	private List<FreeForm> freeforms;

	private List<FreeForm> learnerComments;

	private List<Image> images;

	private List<NoteLangGroup> noteLangGroups;

	private List<Relation> relations;

	private List<List<Relation>> viewSynRelations;

	private List<List<Relation>> viewOtherRelations;

	private boolean activeTagComplete;

	private Timestamp lastChangedOn;

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

	public List<LexemeLangGroup> getLexemeLangGroups() {
		return lexemeLangGroups;
	}

	public void setLexemeLangGroups(List<LexemeLangGroup> lexemeLangGroups) {
		this.lexemeLangGroups = lexemeLangGroups;
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

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<NoteLangGroup> getNoteLangGroups() {
		return noteLangGroups;
	}

	public void setNoteLangGroups(List<NoteLangGroup> noteLangGroups) {
		this.noteLangGroups = noteLangGroups;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public List<List<Relation>> getViewSynRelations() {
		return viewSynRelations;
	}

	public void setViewSynRelations(List<List<Relation>> viewSynRelations) {
		this.viewSynRelations = viewSynRelations;
	}

	public List<List<Relation>> getViewOtherRelations() {
		return viewOtherRelations;
	}

	public void setViewOtherRelations(List<List<Relation>> viewRelations) {
		this.viewOtherRelations = viewRelations;
	}

	public boolean isActiveTagComplete() {
		return activeTagComplete;
	}

	public void setActiveTagComplete(boolean activeTagComplete) {
		this.activeTagComplete = activeTagComplete;
	}

	public Timestamp getLastChangedOn() {
		return lastChangedOn;
	}

	public void setLastChangedOn(Timestamp lastChangedOn) {
		this.lastChangedOn = lastChangedOn;
	}
}
