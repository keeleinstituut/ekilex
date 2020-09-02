package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordRelationDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Relation> wordRelations;

	private List<WordGroup> primaryWordRelationGroups;

	private List<WordGroup> secondaryWordRelationGroups;

	private List<WordGroup> wordGroups;

	private boolean anyRelationExists;

	private boolean groupRelationExists;

	public List<Relation> getWordRelations() {
		return wordRelations;
	}

	public void setWordRelations(List<Relation> wordRelations) {
		this.wordRelations = wordRelations;
	}

	public List<WordGroup> getPrimaryWordRelationGroups() {
		return primaryWordRelationGroups;
	}

	public void setPrimaryWordRelationGroups(List<WordGroup> primaryWordRelationGroups) {
		this.primaryWordRelationGroups = primaryWordRelationGroups;
	}

	public List<WordGroup> getSecondaryWordRelationGroups() {
		return secondaryWordRelationGroups;
	}

	public void setSecondaryWordRelationGroups(List<WordGroup> secondaryWordRelationGroups) {
		this.secondaryWordRelationGroups = secondaryWordRelationGroups;
	}

	public List<WordGroup> getWordGroups() {
		return wordGroups;
	}

	public void setWordGroups(List<WordGroup> wordGroups) {
		this.wordGroups = wordGroups;
	}

	public boolean isAnyRelationExists() {
		return anyRelationExists;
	}

	public void setAnyRelationExists(boolean anyRelationExists) {
		this.anyRelationExists = anyRelationExists;
	}

	public boolean isGroupRelationExists() {
		return groupRelationExists;
	}

	public void setGroupRelationExists(boolean groupRelationExists) {
		this.groupRelationExists = groupRelationExists;
	}
}
