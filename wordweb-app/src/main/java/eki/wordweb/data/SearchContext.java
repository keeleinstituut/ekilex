package eki.wordweb.data;

import java.util.List;

import eki.common.constant.DatasetType;

public class SearchContext extends AbstractPublishingEntity {

	private static final long serialVersionUID = 1L;

	private DatasetType datasetType;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private Integer maxDisplayLevel;

	private boolean excludeQuestionable;

	private boolean fiCollationExists;

	public SearchContext(
			DatasetType datasetType,
			List<String> destinLangs,
			List<String> datasetCodes,
			Integer maxDisplayLevel,
			boolean excludeQuestionable,
			boolean fiCollationExists) {
		this.datasetType = datasetType;
		this.destinLangs = destinLangs;
		this.datasetCodes = datasetCodes;
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
