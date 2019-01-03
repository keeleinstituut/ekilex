package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Meaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "meaning_process_state_code")
	private String processStateCode;

	@Column(name = "lexeme_ids")
	private List<Long> lexemeIds;

	private List<DefinitionLangGroup> definitionLangGroups;

	private List<LexemeLangGroup> lexemeLangGroups;

	private List<Classifier> domains;

	private List<FreeForm> freeforms;

	private List<Relation> relations;

	private List<List<Relation>> groupedRelations;

	private boolean contentExists;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
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

	public List<Classifier> getDomains() {
		return domains;
	}

	public void setDomains(List<Classifier> domains) {
		this.domains = domains;
	}

	public List<FreeForm> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<FreeForm> freeforms) {
		this.freeforms = freeforms;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public boolean isContentExists() {
		return contentExists;
	}

	public void setContentExists(boolean contentExists) {
		this.contentExists = contentExists;
	}

	public List<List<Relation>> getGroupedRelations() {
		return groupedRelations;
	}

	public void setGroupedRelations(List<List<Relation>> groupedRelations) {
		this.groupedRelations = groupedRelations;
	}

}
