package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.EKI_USER_PROFILE;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_NR;
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

	public Dataset getDataset(String code) {

		return create
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

		List<Dataset> datasets = create
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

		create
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

		create
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

		create
				.update(DATASET)
				.set(DATASET.FED_TERM_COLLECTION_ID, fedTermCollectionId)
				.where(DATASET.CODE.eq(datasetCode))
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

		// delete word freeforms
		helper.deleteWordFreeforms(datasetCode, create);

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
			create.deleteFrom(MEANING).where(MEANING.ID.in(meaningIds)).execute();
		}

		// delete dataset
		create.delete(DATASET).where(DATASET.CODE.eq(datasetCode)).execute();
	}

	public boolean datasetCodeExists(String datasetCode) {
		return create.fetchExists(
				create.select()
						.from(DATASET)
						.where(DATASET.CODE.equalIgnoreCase(datasetCode)));
	}

	public void addDatasetToClassifier(ClassifierName classifierName, String datasetCode, List<Classifier> addedClassifiers) {

		if (CollectionUtils.isNotEmpty(addedClassifiers)) {
			if (ClassifierName.LANGUAGE.equals(classifierName)) {
				List<String> languageCodes = addedClassifiers.stream().map(Classifier::getCode).collect(Collectors.toList());
				create.update(LANGUAGE)
						.set(LANGUAGE.DATASETS, DSL.field(PostgresDSL.arrayAppend(LANGUAGE.DATASETS, datasetCode)))
						.where(LANGUAGE.CODE.in(languageCodes))
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

		if (ClassifierName.LANGUAGE.equals(classifierName)) {
			create.update(LANGUAGE)
					.set(LANGUAGE.DATASETS, DSL.field(PostgresDSL.arrayRemove(LANGUAGE.DATASETS, datasetCode)))
					.where(DSL.val(datasetCode).eq(DSL.any(LANGUAGE.DATASETS)))
					.execute();

		} else if (ClassifierName.DOMAIN.equals(classifierName)) {
			create.update(DOMAIN)
					.set(DOMAIN.DATASETS, DSL.field(PostgresDSL.arrayRemove(DOMAIN.DATASETS, datasetCode)))
					.where(DSL.val(datasetCode).eq(DSL.any(DOMAIN.DATASETS)))
					.execute();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void removeDatasetFromUserProfile(String datasetCode) {

		create.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.PREFERRED_DATASETS, DSL.field(PostgresDSL.arrayRemove(EKI_USER_PROFILE.PREFERRED_DATASETS, datasetCode)))
				.where(DSL.val(datasetCode).eq(DSL.any(EKI_USER_PROFILE.PREFERRED_DATASETS)))
				.execute();
	}
}
