package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.DatasetDbService;

@Component
public class DatasetService {

	@Autowired
	private DatasetDbService datasetDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return datasetDbService.getDatasets();
	}

	@Transactional
	public void createDataset(Dataset dataset) {
		datasetDbService.createDataset(dataset);

	}

	@Transactional
	public void updateDataset(Dataset dataset) {
		datasetDbService.updateDataset(dataset);
	}

	@Transactional
	public void deleteDataset(String code) {
		datasetDbService.deleteDataset(code);
	}

	@Transactional
	public boolean datasetCodeExists(String code) {
		return datasetDbService.datasetCodeExists(code);
	}
}
