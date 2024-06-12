package eki.ekilex.service.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.Dataset;

@Component
public class DatasetUtil implements GlobalConstant {

	public List<Dataset> resortPriorityDatasets(List<Dataset> datasets) {
		datasets = datasets.stream().collect(Collectors.toList());
		Dataset datasetEki = datasets.stream().filter(dataset -> StringUtils.equals(dataset.getCode(), DATASET_EKI)).findFirst().orElse(null);
		if (datasetEki != null) {
			datasets.remove(datasetEki);
			datasets.add(0, datasetEki);
		}
		return datasets;
	}
}
