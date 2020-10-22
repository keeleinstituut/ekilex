package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;

public class DataFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private DatasetType datasetType;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private Complexity lexComplexity;

	private Integer maxDisplayLevel;

	private boolean fiCollationExists;

	public DataFilter(
			DatasetType datasetType,
			List<String> destinLangs,
			List<String> datasetCodes,
			Complexity lexComplexity,
			Integer maxDisplayLevel,
			boolean fiCollationExists) {
		this.datasetType = datasetType;
		this.destinLangs = destinLangs;
		this.datasetCodes = datasetCodes;
		this.lexComplexity = lexComplexity;
		this.maxDisplayLevel = maxDisplayLevel;
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

	public boolean isFiCollationExists() {
		return fiCollationExists;
	}

}
