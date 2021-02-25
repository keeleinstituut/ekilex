package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;

public class SearchContext extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private DatasetType datasetType;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private Complexity lexComplexity;

	private Integer maxDisplayLevel;

	private boolean excludeQuestionable;

	private boolean fiCollationExists;

	public SearchContext(
			DatasetType datasetType,
			List<String> destinLangs,
			List<String> datasetCodes,
			Complexity lexComplexity,
			Integer maxDisplayLevel,
			boolean excludeQuestionable,
			boolean fiCollationExists) {
		this.datasetType = datasetType;
		this.destinLangs = destinLangs;
		this.datasetCodes = datasetCodes;
		this.lexComplexity = lexComplexity;
		this.maxDisplayLevel = maxDisplayLevel;
		this.excludeQuestionable = excludeQuestionable;
		this.fiCollationExists = fiCollationExists;
	}

	public DatasetType getDatasetType() {
		return datasetType;
	}

	public List<String> getDestinLangs() {
		return destinLangs;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public Complexity getLexComplexity() {
		return lexComplexity;
	}

	public Integer getMaxDisplayLevel() {
		return maxDisplayLevel;
	}

	public boolean isExcludeQuestionable() {
		return excludeQuestionable;
	}

	public boolean isFiCollationExists() {
		return fiCollationExists;
	}

}
