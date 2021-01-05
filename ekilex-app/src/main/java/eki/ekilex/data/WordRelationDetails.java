package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordRelationDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<SynRelation> wordSynRelations;

	private List<WordGroup> primaryWordRelationGroups;

	private List<WordGroup> secondaryWordRelationGroups;

	private List<WordGroup> wordGroups;

	private boolean groupRelationExists;

	public List<SynRelation> getWordSynRelations() {
		return wordSynRelations;
	}

	public void setWordSynRelations(List<SynRelation> wordSynRelations) {
		this.wordSynRelations = wordSynRelations;
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

	public boolean isGroupRelationExists() {
		return groupRelationExists;
	}

	public void setGroupRelationExists(boolean groupRelationExists) {
		this.groupRelationExists = groupRelationExists;
	}
}
