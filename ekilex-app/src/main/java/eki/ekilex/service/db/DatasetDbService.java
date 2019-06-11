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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.CodeOriginTuple;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.util.DatasetDbServiceHelper;

@Component
public class DatasetDbService {

	@Autowired
	private DSLContext create;

	@Autowired
	private DatasetDbServiceHelper helper;

	@Autowired
	private CommonDataDbService commonDataDbService;

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

		for (Dataset dataset : datasets) {
			String[] datasetCodeArr = {dataset.getCode()};
			List<String> languageCodes = create.select(LANGUAGE.CODE).from(LANGUAGE)
					.where(LANGUAGE.DATASETS.contains(datasetCodeArr)).fetchInto(String.class);

			List<String> processStateCodes = create.select(PROCESS_STATE.CODE).from(PROCESS_STATE)
					.where(PROCESS_STATE.DATASETS.contains(datasetCodeArr)).fetchInto(String.class);

			List<CodeOriginTuple> domainCodeOrigins = create.select(DOMAIN.CODE, DOMAIN.ORIGIN)
					.from(DOMAIN)
					.where(DOMAIN.DATASETS.contains(datasetCodeArr))
					.fetchInto(CodeOriginTuple.class);

			// fill domain labels for selected domains
			//TODO can be done by some jooq trick or just to take the first label from domain_label?
			domainCodeOrigins.forEach(
					codeOriginTuple -> codeOriginTuple.setValue(
							String.join(", ", commonDataDbService.getDomainLabels(codeOriginTuple.getCode(), codeOriginTuple.getOrigin()))));

			dataset.setSelectedLanguageCodes(languageCodes);
			dataset.setSelectedProcessStateCodes(processStateCodes);
			dataset.setSelectedDomainCodeOriginPairs(domainCodeOrigins);

		}
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

		addSelectedClassifiers(dataset.getCode(), ClassifierName.LANGUAGE, dataset.getSelectedLanguageCodes());
		addSelectedClassifiers(dataset.getCode(), ClassifierName.PROCESS_STATE, dataset.getSelectedProcessStateCodes());

		addSelectedDomains(dataset);
	}

	private void addSelectedClassifiers(String datasetCode, ClassifierName classifierName, List<String> selectedClassifierCodes) {
		if (selectedClassifierCodes != null) {
			for (String code : selectedClassifierCodes) {
				commonDataDbService.addDatasetCodeToClassifier(classifierName, code, datasetCode, null);
			}
		}
	}

	private void addSelectedDomains(Dataset dataset) {
		List<CodeOriginTuple> selectedClassifierCodeOrigins = dataset.getSelectedDomainCodeOriginPairs();
		if (selectedClassifierCodeOrigins != null) {
			for (CodeOriginTuple codeOrigin : selectedClassifierCodeOrigins) {
				commonDataDbService.addDatasetCodeToClassifier(ClassifierName.DOMAIN, codeOrigin.getCode(), dataset.getCode(), codeOrigin.getOrigin());
			}
		}
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

		updateDatasetClassifiers(ClassifierName.LANGUAGE, dataset.getCode(), dataset.getSelectedLanguageCodes());
		updateDatasetClassifiers(ClassifierName.PROCESS_STATE, dataset.getCode(), dataset.getSelectedProcessStateCodes());
		updateDatasetDomains(dataset);

	}

	private void updateDatasetClassifiers(ClassifierName classifierName, String datasetCode, List<String> selectedClassifierCodes) {

		List<String> existingClassifierCodes = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode)
				.stream()
				.map(Classifier::getCode)
				.collect(Collectors.toList());

		// remove dataset code from Classifier if was unselected
		existingClassifierCodes
				.stream()
				.filter(code -> selectedClassifierCodes == null || !selectedClassifierCodes.contains(code))
				.forEach(code -> commonDataDbService.removeDatasetCodeFromClassifier(classifierName, code, datasetCode, null));

		if (selectedClassifierCodes != null) {
			for (String classifCode : selectedClassifierCodes) {
				if (!existingClassifierCodes.contains(classifCode)) {
					commonDataDbService.addDatasetCodeToClassifier(classifierName, classifCode, datasetCode, null);
				}
			}
		}
	}

	private void removeDatasetClassifiers(ClassifierName classifierName, String datasetCode) {

		List<Classifier> existingClassifiers = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode)
				.stream()
				.collect(Collectors.toList());

		existingClassifiers.forEach(classifier -> commonDataDbService.removeDatasetCodeFromClassifier(classifierName, classifier.getCode(), datasetCode, classifier.getOrigin()));
	}

	private void updateDatasetDomains(Dataset dataset) {

		List<CodeOriginTuple> existingClassifierCodeOrigins = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, dataset.getCode())
				.stream()
				.map(classifier -> {
					CodeOriginTuple tuple = new CodeOriginTuple();
					tuple.setCode(classifier.getCode());
					tuple.setOrigin(classifier.getOrigin());
					return tuple;
				})
				.collect(Collectors.toList());

		// remove dataset code from Classifier if was unselected
		existingClassifierCodeOrigins
				.stream()
				.filter(codeOriginTuple -> dataset.getSelectedDomainCodeOriginPairs() == null || !dataset.getSelectedDomainCodeOriginPairs().contains(codeOriginTuple))
				.forEach(codeOriginTuple -> commonDataDbService.removeDatasetCodeFromClassifier(ClassifierName.DOMAIN, codeOriginTuple.getCode(), dataset.getCode(), codeOriginTuple.getOrigin()));

		if (dataset.getSelectedDomainCodeOriginPairs() != null) {
			for (CodeOriginTuple codeOriginTuple : dataset.getSelectedDomainCodeOriginPairs()) {
				if (!existingClassifierCodeOrigins.contains(codeOriginTuple)) {
					commonDataDbService.addDatasetCodeToClassifier(ClassifierName.DOMAIN, codeOriginTuple.getCode(), dataset.getCode(), codeOriginTuple.getOrigin());
				}
			}
		}
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

		// delete classifiers
		removeDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode);
		removeDatasetClassifiers(ClassifierName.PROCESS_STATE, datasetCode);
		removeDatasetClassifiers(ClassifierName.DOMAIN, datasetCode);

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
