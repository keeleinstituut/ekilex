package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_NR;
import static eki.ekilex.data.db.Tables.PROCESS_LOG;
import static eki.ekilex.data.db.Tables.PROCESS_STATE;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_GUID;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.jooq.Row2;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.Classifier;
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
						DATASET.TYPE,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.from(DATASET)
				.orderBy(DATASET.NAME)
				.fetchInto(Dataset.class);


		return datasets;

	}

	public void createDataset(Dataset dataset) {

		create
				.insertInto(DATASET,
						DATASET.CODE,
						DATASET.NAME,
						DATASET.TYPE,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.values(dataset.getCode(),
						dataset.getName(),
						(dataset.getType() != null ? dataset.getType().name() : null),
						dataset.getDescription(),
						dataset.isVisible(),
						dataset.isPublic())
				.execute();

	}

	public void updateDataset(Dataset dataset) {

		create
				.update(DATASET)
				.set(DATASET.NAME, dataset.getName())
				.set(DATASET.TYPE, (dataset.getType() != null ? dataset.getType().name() : null))
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


	public Dataset getDataset(String code) {

		return create
				.select(DATASET.CODE,
						DATASET.NAME,
						DATASET.TYPE,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.from(DATASET)
				.where(DATASET.CODE.eq(code))
				.fetchSingleInto(Dataset.class);

	}

	public void addDatasetToClassifier(ClassifierName classifierName, String datasetCode, List<Classifier> addedClassifiers) {

		if (CollectionUtils.isNotEmpty(addedClassifiers)) {
			if (ClassifierName.LANGUAGE.equals(classifierName)) {
				List<String> languageCodes = addedClassifiers.stream().map(Classifier::getCode).collect(Collectors.toList());
				create.update(LANGUAGE)
						.set(LANGUAGE.DATASETS, DSL.field(PostgresDSL.arrayAppend(LANGUAGE.DATASETS, datasetCode)))
						.where(LANGUAGE.CODE.in(languageCodes))
						.execute();

			} else if (ClassifierName.PROCESS_STATE.equals(classifierName)) {
				List<String> processStateCodes = addedClassifiers.stream().map(Classifier::getCode).collect(Collectors.toList());
				create.update(PROCESS_STATE)
						.set(PROCESS_STATE.DATASETS, DSL.field(PostgresDSL.arrayAppend(PROCESS_STATE.DATASETS, datasetCode)))
						.where(PROCESS_STATE.CODE.in(processStateCodes))
						.execute();

			} else if (ClassifierName.DOMAIN.equals(classifierName)) {
				List<Row2<String, String>> codeOriginTuples = addedClassifiers.stream().map(c -> DSL.row(DSL.val(c.getCode()), DSL.val(c.getOrigin()))).collect(Collectors.toList());
				create.update(DOMAIN)
						.set(DOMAIN.DATASETS, DSL.field(PostgresDSL.arrayAppend(DOMAIN.DATASETS, datasetCode)))
						.where(DSL.row(DOMAIN.CODE, DOMAIN.ORIGIN).in(codeOriginTuples))
						.execute();

			} else {
				throw new UnsupportedOperationException();
			}
		}
	}

	public void removeDatasetFromAllClassifiers(ClassifierName classifierName, String datasetCode) {

		String[] datasetCodes = {datasetCode};
		if (ClassifierName.LANGUAGE.equals(classifierName)) {
			create.update(LANGUAGE)
					.set(LANGUAGE.DATASETS, DSL.field(PostgresDSL.arrayRemove(LANGUAGE.DATASETS, datasetCode)))
					.where(LANGUAGE.DATASETS.contains(datasetCodes))
					.execute();

		} else if (ClassifierName.PROCESS_STATE.equals(classifierName)) {
			create.update(PROCESS_STATE)
					.set(PROCESS_STATE.DATASETS, DSL.field(PostgresDSL.arrayRemove(PROCESS_STATE.DATASETS, datasetCode)))
					.where(PROCESS_STATE.DATASETS.contains(datasetCodes))
					.execute();

		} else if (ClassifierName.DOMAIN.equals(classifierName)) {
			create.update(DOMAIN)
					.set(DOMAIN.DATASETS, DSL.field(PostgresDSL.arrayRemove(DOMAIN.DATASETS, datasetCode)))
					.where(DOMAIN.DATASETS.contains(datasetCodes))
					.execute();

		} else {
			throw new UnsupportedOperationException();
		}
	}
}
