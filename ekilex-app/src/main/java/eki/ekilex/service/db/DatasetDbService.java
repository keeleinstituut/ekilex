package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.DATASET_FREEFORM_TYPE;
import static eki.ekilex.data.db.main.Tables.DOMAIN;
import static eki.ekilex.data.db.main.Tables.EKI_USER_PROFILE;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_NR;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_GUID;

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
import eki.common.constant.FreeformOwner;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.util.DatasetDbServiceHelper;

@Component
public class DatasetDbService {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private DatasetDbServiceHelper helper;

	public boolean datasetExists(String code) {

		return mainDb
				.fetchExists(DSL
						.select(DATASET.CODE)
						.from(DATASET)
						.where(DATASET.CODE.eq(code)));
	}

	public Dataset getDataset(String code) {

		return mainDb
				.select(DATASET.CODE,
						DATASET.NAME,
						DATASET.TYPE,
						DATASET.DESCRIPTION,
						DATASET.CONTACT,
						DATASET.IMAGE_URL,
						DATASET.FED_TERM_COLLECTION_ID,
						DATASET.FED_TERM_DOMAIN_ID,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC,
						DATASET.IS_VISIBLE)
				.from(DATASET)
				.where(DATASET.CODE.eq(code))
				.fetchSingleInto(Dataset.class);
	}

	public List<Dataset> getDatasets() {

		List<Dataset> datasets = mainDb
				.select(DATASET.CODE,
						DATASET.NAME,
						DATASET.TYPE,
						DATASET.DESCRIPTION,
						DATASET.CONTACT,
						DATASET.IMAGE_URL,
						DATASET.FED_TERM_COLLECTION_ID,
						DATASET.FED_TERM_DOMAIN_ID,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC,
						DATASET.IS_SUPERIOR)
				.from(DATASET)
				.orderBy(DATASET.NAME)
				.fetchInto(Dataset.class);
		return datasets;

	}

	public void createDataset(Dataset dataset) {

		mainDb
				.insertInto(DATASET,
						DATASET.CODE,
						DATASET.NAME,
						DATASET.TYPE,
						DATASET.DESCRIPTION,
						DATASET.CONTACT,
						DATASET.IMAGE_URL,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.values(dataset.getCode(),
						dataset.getName(),
						(dataset.getType() != null ? dataset.getType().name() : null),
						dataset.getDescription(),
						dataset.getContact(),
						dataset.getImageUrl(),
						dataset.isVisible(),
						dataset.isPublic())
				.execute();

	}

	public void updateDataset(Dataset dataset) {

		mainDb
				.update(DATASET)
				.set(DATASET.NAME, dataset.getName())
				.set(DATASET.TYPE, (dataset.getType() != null ? dataset.getType().name() : null))
				.set(DATASET.DESCRIPTION, dataset.getDescription())
				.set(DATASET.CONTACT, dataset.getContact())
				.set(DATASET.IMAGE_URL, dataset.getImageUrl())
				.set(DATASET.IS_VISIBLE, dataset.isVisible())
				.set(DATASET.IS_PUBLIC, dataset.isPublic())
				.where(DATASET.CODE.eq(dataset.getCode()))
				.execute();

	}

	public void setFedTermCollectionId(String datasetCode, String fedTermCollectionId) {

		mainDb
				.update(DATASET)
				.set(DATASET.FED_TERM_COLLECTION_ID, fedTermCollectionId)
				.where(DATASET.CODE.eq(datasetCode))
				.execute();

	}

	public void deleteDataset(String datasetCode) {

		// collect word ids
		List<Long> wordIds = helper.getWordIds(datasetCode, mainDb);

		// collect meaning ids
		List<Long> meaningIds = helper.getMeaningIds(datasetCode, mainDb);

		// delete definition freeforms
		helper.deleteDefinitionFreeforms(datasetCode, mainDb);

		// delete meaning freeforms
		helper.deleteMeaningFreeforms(datasetCode, mainDb);

		// delete lexeme freeforms
		helper.deleteLexemeFreeforms(datasetCode, mainDb);

		// delete word freeforms
		helper.deleteWordFreeforms(datasetCode, mainDb);

		// delete definitions
		helper.deleteDefinitions(datasetCode, mainDb);

		// delete lexemes, guids, mnrs
		mainDb.deleteFrom(LEXEME).where(LEXEME.DATASET_CODE.eq(datasetCode)).execute();
		mainDb.deleteFrom(WORD_GUID).where(WORD_GUID.DATASET_CODE.eq(datasetCode)).execute();
		mainDb.deleteFrom(MEANING_NR).where(MEANING_NR.DATASET_CODE.eq(datasetCode)).execute();

		// delete words
		if (CollectionUtils.isNotEmpty(wordIds)) {
			mainDb.deleteFrom(WORD).where(WORD.ID.in(wordIds)).execute();
		}

		// delete meanings
		if (CollectionUtils.isNotEmpty(meaningIds)) {
			mainDb.deleteFrom(MEANING).where(MEANING.ID.in(meaningIds)).execute();
		}

		// delete dataset
		mainDb.delete(DATASET).where(DATASET.CODE.eq(datasetCode)).execute();
	}

	public boolean datasetCodeExists(String datasetCode) {
		return mainDb.fetchExists(
				mainDb
						.select()
						.from(DATASET)
						.where(DATASET.CODE.equalIgnoreCase(datasetCode)));
	}

	public void addDatasetToClassifiers(ClassifierName classifierName, String datasetCode, List<Classifier> classifiers) {

		if (CollectionUtils.isEmpty(classifiers)) {
			return;
		}
		if (ClassifierName.LANGUAGE.equals(classifierName)) {

			addDatasetToClassifiers(classifierName, datasetCode, classifiers, null);

		} else if (ClassifierName.DOMAIN.equals(classifierName)) {

			addDatasetToClassifiers(classifierName, datasetCode, classifiers, null);

		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void addDatasetToClassifiers(ClassifierName classifierName, String datasetCode, List<Classifier> classifiers, FreeformOwner freeformOwner) {

		if (CollectionUtils.isEmpty(classifiers)) {
			return;
		}

		List<String> classifierCodes = classifiers.stream().map(Classifier::getCode).collect(Collectors.toList());

		if (ClassifierName.LANGUAGE.equals(classifierName)) {

			mainDb
					.update(LANGUAGE)
					.set(LANGUAGE.DATASETS, DSL.field(PostgresDSL.arrayAppend(LANGUAGE.DATASETS, datasetCode)))
					.where(
							LANGUAGE.CODE.in(classifierCodes)
									.and(DSL.val(datasetCode).ne(DSL.any(LANGUAGE.DATASETS))))
					.execute();

		} else if (ClassifierName.FREEFORM_TYPE.equals(classifierName)) {

			for (String classifierCode : classifierCodes) {

				mainDb
						.insertInto(
								DATASET_FREEFORM_TYPE,
								DATASET_FREEFORM_TYPE.DATASET_CODE,
								DATASET_FREEFORM_TYPE.FREEFORM_OWNER,
								DATASET_FREEFORM_TYPE.FREEFORM_TYPE_CODE)
						.values(
								datasetCode,
								freeformOwner.name(),
								classifierCode)
						.execute();
			}

		} else if (ClassifierName.DOMAIN.equals(classifierName)) {

			List<Row2<String, String>> codeOriginTuples = classifiers.stream()
					.map(classif -> DSL.row(DSL.val(classif.getCode()), DSL.val(classif.getOrigin())))
					.collect(Collectors.toList());
			mainDb
					.update(DOMAIN)
					.set(DOMAIN.DATASETS, DSL.field(PostgresDSL.arrayAppend(DOMAIN.DATASETS, datasetCode)))
					.where(DSL.row(DOMAIN.CODE, DOMAIN.ORIGIN).in(codeOriginTuples))
					.execute();

		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void removeDatasetFromClassifiers(ClassifierName classifierName, String datasetCode) {

		if (ClassifierName.LANGUAGE.equals(classifierName)) {

			mainDb
					.update(LANGUAGE)
					.set(LANGUAGE.DATASETS, DSL.field(PostgresDSL.arrayRemove(LANGUAGE.DATASETS, datasetCode)))
					.where(DSL.val(datasetCode).eq(DSL.any(LANGUAGE.DATASETS)))
					.execute();

		} else if (ClassifierName.FREEFORM_TYPE.equals(classifierName)) {

			mainDb
					.deleteFrom(DATASET_FREEFORM_TYPE)
					.where(DATASET_FREEFORM_TYPE.DATASET_CODE.eq(datasetCode))
					.execute();

		} else if (ClassifierName.DOMAIN.equals(classifierName)) {

			mainDb
					.update(DOMAIN)
					.set(DOMAIN.DATASETS, DSL.field(PostgresDSL.arrayRemove(DOMAIN.DATASETS, datasetCode)))
					.where(DSL.val(datasetCode).eq(DSL.any(DOMAIN.DATASETS)))
					.execute();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void removeDatasetFromUserProfile(String datasetCode) {

		mainDb
				.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_DATASETS, DSL.field(PostgresDSL.arrayRemove(EKI_USER_PROFILE.PREFERRED_DATASETS, datasetCode)))
				.where(DSL.val(datasetCode).eq(DSL.any(EKI_USER_PROFILE.PREFERRED_DATASETS)))
				.execute();
	}
}
