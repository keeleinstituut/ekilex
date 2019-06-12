package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_NR;
import static eki.ekilex.data.db.Tables.PROCESS_LOG;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_GUID;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.util.DatasetDbServiceHelper;

@Component
public class DatasetDbService {

	@Autowired
	private DSLContext create;

	@Autowired
	private DatasetDbServiceHelper helper;

	public List<Dataset> getDatasets() {

		List<Dataset> datasets = create
				.select(DATASET.CODE,
						DATASET.NAME,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.from(DATASET)
				.orderBy(DATASET.ORDER_BY)
				.fetchInto(Dataset.class);


		return datasets;

	}

	public void createDataset(Dataset dataset) {

		create
				.insertInto(DATASET,
						DATASET.CODE,
						DATASET.NAME,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.values(dataset.getCode(),
						dataset.getName(),
						dataset.getDescription(),
						dataset.isVisible(),
						dataset.isPublic())
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

		// collect word ids
		List<Long> wordIds = helper.getWordIds(datasetCode, create);

		// collect meaning ids
		List<Long> meaningIds = helper.getMeaningIds(datasetCode, create);

		// delete definition freeforms
		helper.deleteDefinitionFreeforms(datasetCode, create);

		// delete meaning freeforms
		helper.deleteMeaningFreeforms(datasetCode, create);

		// delete lexeme freeforms
		helper.deleteLexemeFreeforms(datasetCode, create);

		// delete colloc freeforms
		helper.deleteCollocationFreeforms(datasetCode, create);

		// delete definitions
		helper.deleteDefinitions(datasetCode, create);

		// delete collocations
		helper.deleteCollocations(datasetCode, create);

		// delete lexemes, guids, mnrs
		create.deleteFrom(LEXEME).where(LEXEME.DATASET_CODE.eq(datasetCode)).execute();
		create.deleteFrom(WORD_GUID).where(WORD_GUID.DATASET_CODE.eq(datasetCode)).execute();
		create.deleteFrom(MEANING_NR).where(MEANING_NR.DATASET_CODE.eq(datasetCode)).execute();

		// delete words
		if (CollectionUtils.isNotEmpty(wordIds)) {
			create.deleteFrom(WORD).where(WORD.ID.in(wordIds)).execute();
		}

		// delete meanings
		if (CollectionUtils.isNotEmpty(meaningIds)) {
			create.deleteFrom(MEANING).where(MEANING.ID.in(wordIds)).execute();
		}

		// delete process log
		create.deleteFrom(PROCESS_LOG).where(PROCESS_LOG.DATASET_CODE.eq(datasetCode)).execute();


		// delete dataset
		create.delete(DATASET).where(DATASET.CODE.eq(datasetCode)).execute();
	}

	public boolean datasetCodeExists(String datasetCode) {
		return create.fetchExists(
				create.select()
						.from(DATASET)
						.where(DATASET.CODE.equalIgnoreCase(datasetCode)));
	}


}
