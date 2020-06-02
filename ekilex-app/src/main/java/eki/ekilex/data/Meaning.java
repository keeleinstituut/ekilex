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

	private List<MeaningNote> publicNotes;

	private List<Relation> relations;

	private List<List<Relation>> viewRelations;

	private Integer meaningProcessLogCount;

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

	public List<MeaningNote> getPublicNotes() {
		return publicNotes;
	}

	public void setPublicNotes(List<MeaningNote> publicNotes) {
		this.publicNotes = publicNotes;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public List<List<Relation>> getViewRelations() {
		return viewRelations;
	}

	public void setViewRelations(List<List<Relation>> viewRelations) {
		this.viewRelations = viewRelations;
	}

	public Integer getMeaningProcessLogCount() {
		return meaningProcessLogCount;
	}

	public void setMeaningProcessLogCount(Integer meaningProcessLogCount) {
		this.meaningProcessLogCount = meaningProcessLogCount;
	}

	public Timestamp getLastChangedOn() {
		return lastChangedOn;
	}

	public void setLastChangedOn(Timestamp lastChangedOn) {
		this.lastChangedOn = lastChangedOn;
	}
}
