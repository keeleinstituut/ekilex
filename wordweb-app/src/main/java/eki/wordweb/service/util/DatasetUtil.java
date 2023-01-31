package eki.wordweb.service.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.wordweb.data.Dataset;

@Component
public class DatasetUtil implements GlobalConstant {

	public List<Dataset> resortPriorityDatasets(List<Dataset> datasets) {
		Dataset datasetEki = datasets.stream().filter(dataset -> StringUtils.equals(dataset.getCode(), DATASET_EKI)).findFirst().orElse(null);
		if (datasetEki != null) {
			datasets.remove(datasetEki);
			datasets.add(0, datasetEki);
		}
		return datasets;
	}
}
