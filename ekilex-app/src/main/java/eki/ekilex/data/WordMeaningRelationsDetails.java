package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordMeaningRelationsDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordValue;

	private String language;

	private Long meaningId;

	private boolean importMeaningData;

	private boolean createRelation;

	private String relationType;

	private String oppositeRelationType;

	private Long relatedMeaningId;

	private String dataset;

	private String userName;

	private List<String> userPermDatasetCodes;

	private String backUri;

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public boolean isImportMeaningData() {
		return importMeaningData;
	}

	public void setImportMeaningData(boolean importMeaningData) {
		this.importMeaningData = importMeaningData;
	}

	public boolean isCreateRelation() {
		return createRelation;
	}

	public void setCreateRelation(boolean createRelation) {
		this.createRelation = createRelation;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getOppositeRelationType() {
		return oppositeRelationType;
	}

	public void setOppositeRelationType(String oppositeRelationType) {
		this.oppositeRelationType = oppositeRelationType;
	}

	public Long getRelatedMeaningId() {
		return relatedMeaningId;
	}

	public void setRelatedMeaningId(Long relatedMeaningId) {
		this.relatedMeaningId = relatedMeaningId;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getUserPermDatasetCodes() {
		return userPermDatasetCodes;
	}

	public void setUserPermDatasetCodes(List<String> userPermDatasetCodes) {
		this.userPermDatasetCodes = userPermDatasetCodes;
	}

	public String getBackUri() {
		return backUri;
	}

	public void setBackUri(String backUri) {
		this.backUri = backUri;
	}
}
