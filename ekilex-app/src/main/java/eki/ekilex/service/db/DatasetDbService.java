package eki.ekilex.service.db;

import static eki.ekilex.data.db.tables.Dataset.DATASET;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;

@Component
public class DatasetDbService {

	private DSLContext create;

	public DatasetDbService(DSLContext context) {
		create = context;
	}

	public List<Dataset> getDatasets() {

		return
				create
					.select(DATASET.CODE,
							DATASET.NAME,
							DATASET.DESCRIPTION,
							DATASET.IS_VISIBLE,
							DATASET.IS_PUBLIC)
					.from(DATASET)
					.orderBy(DATASET.ORDER_BY)
					.fetchInto(Dataset.class);

	}

	public void createDataset(Dataset dataset) {

		create
			.insertInto(DATASET,
					DATASET.CODE,
					DATASET.NAME,
					DATASET.DESCRIPTION,
					DATASET.IS_VISIBLE,
					DATASET.IS_PUBLIC)
			.values(dataset.getCode(), dataset.getName(), dataset.getDescription(), dataset.isVisible(), dataset.isPublic())
			.execute();
	}

	public void updateDataset(Dataset dataset) {

		create
			.update(DATASET)
			.set(DATASET.NAME, dataset.getName())
			.set(DATASET.DESCRIPTION, dataset.getDescription())
			.set(DATASET.IS_VISIBLE, dataset.isVisible())
			.set(DATASET.IS_PUBLIC, dataset.isPublic())
			.where(DATASET.CODE.eq(dataset.getCode()))
			.execute();
	}

	public void deleteDataset(String datasetCode) {

		create
				.delete(DATASET)
				.where(DATASET.CODE.eq(datasetCode))
				.execute();
	}

	public boolean datasetCodeExists(String datasetCode) {
		return
			create.fetchExists(
				create.select()
						.from(DATASET)
						.where(DATASET.CODE.equalIgnoreCase(datasetCode))
			);
	}

}
