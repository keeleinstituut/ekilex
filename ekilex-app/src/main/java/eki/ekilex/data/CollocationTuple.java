package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class CollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "colloc_pos_gr_id")
	private Long collocationPosGroupId;

	@Column(name = "colloc_pos_gr_name")
	private String collocationPosGroupName;

	@Column(name = "colloc_rel_gr_id")
	private Long collocationRelGroupId;

	@Column(name = "colloc_rel_gr_name")
	private String collocationRelGroupName;

	@Column(name = "colloc_rel_gr_freq")
	private Float collocationRelGroupFrequency;

	@Column(name = "colloc_rel_gr_score")
	private Float collocationRelGroupScore;

	@Column(name = "colloc_id")
	private Long collocationId;

	@Column(name = "colloc_word_id")
	private Long collocationWordId;

	@Column(name = "colloc")
	private String collocation;

	@Column(name = "colloc_freq")
	private Float collocationFrequency;

	@Column(name = "colloc_score")
	private Float collocationScore;

	@Column(name = "colloc_usages")
	private List<String> collocationUsages;

	public Long getCollocationPosGroupId() {
		return collocationPosGroupId;
	}

	public void setCollocationPosGroupId(Long collocationPosGroupId) {
		this.collocationPosGroupId = collocationPosGroupId;
	}

	public String getCollocationPosGroupName() {
		return collocationPosGroupName;
	}

	public void setCollocationPosGroupName(String collocationPosGroupName) {
		this.collocationPosGroupName = collocationPosGroupName;
	}

	public Long getCollocationRelGroupId() {
		return collocationRelGroupId;
	}

	public void setCollocationRelGroupId(Long collocationRelGroupId) {
		this.collocationRelGroupId = collocationRelGroupId;
	}

	public String getCollocationRelGroupName() {
		return collocationRelGroupName;
	}

	public void setCollocationRelGroupName(String collocationRelGroupName) {
		this.collocationRelGroupName = collocationRelGroupName;
	}

	public Float getCollocationRelGroupFrequency() {
		return collocationRelGroupFrequency;
	}

	public void setCollocationRelGroupFrequency(Float collocationRelGroupFrequency) {
		this.collocationRelGroupFrequency = collocationRelGroupFrequency;
	}

	public Float getCollocationRelGroupScore() {
		return collocationRelGroupScore;
	}

	public void setCollocationRelGroupScore(Float collocationRelGroupScore) {
		this.collocationRelGroupScore = collocationRelGroupScore;
	}

	public Long getCollocationId() {
		return collocationId;
	}

	public void setCollocationId(Long collocationId) {
		this.collocationId = collocationId;
	}

	public Long getCollocationWordId() {
		return collocationWordId;
	}

	public void setCollocationWordId(Long collocationWordId) {
		this.collocationWordId = collocationWordId;
	}

	public String getCollocation() {
		return collocation;
	}

	public void setCollocation(String collocation) {
		this.collocation = collocation;
	}

	public Float getCollocationFrequency() {
		return collocationFrequency;
	}

	public void setCollocationFrequency(Float collocationFrequency) {
		this.collocationFrequency = collocationFrequency;
	}

	public Float getCollocationScore() {
		return collocationScore;
	}

	public void setCollocationScore(Float collocationScore) {
		this.collocationScore = collocationScore;
	}

	public List<String> getCollocationUsages() {
		return collocationUsages;
	}

	public void setCollocationUsages(List<String> collocationUsages) {
		this.collocationUsages = collocationUsages;
	}

}
