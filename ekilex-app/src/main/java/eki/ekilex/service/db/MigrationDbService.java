package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_OD_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OD_USAGE;

import java.util.List;

import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordOdMorph;
import eki.ekilex.data.db.main.tables.WordOdUsage;

// temporary for data migration tools
@Component
public class MigrationDbService extends AbstractDataDbService {

	public List<Classifier> getDomains(String origin, String type) {

		return mainDb
				.select(
						DSL.val(ClassifierName.DOMAIN.name()).as("name"),
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.VALUE,
						DOMAIN_LABEL.LANG)
				.from(DOMAIN_LABEL)
				.where(
						DOMAIN_LABEL.ORIGIN.eq(origin)
								.and(DOMAIN_LABEL.TYPE.eq(type)))
				.orderBy(
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.LANG)
				.fetchInto(Classifier.class);
	}

	public void updateDomainLabelValue(String code, String origin, String value, String lang, String type) {

		mainDb
				.update(DOMAIN_LABEL)
				.set(DOMAIN_LABEL.VALUE, value)
				.where(
						DOMAIN_LABEL.CODE.eq(code)
								.and(DOMAIN_LABEL.ORIGIN.eq(origin))
								.and(DOMAIN_LABEL.LANG.eq(lang))
								.and(DOMAIN_LABEL.TYPE.eq(type)))
				.execute();
	}

	public boolean createDomainLabel(String code, String origin, String value, String lang, String type) {

		int resultCount = mainDb
				.insertInto(
						DOMAIN_LABEL,
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.VALUE,
						DOMAIN_LABEL.LANG,
						DOMAIN_LABEL.TYPE)
				.select(
						DSL.select(
								DSL.val(code),
								DSL.val(origin),
								DSL.val(value),
								DSL.val(lang),
								DSL.val(type))
								.whereNotExists(DSL
										.selectOne()
										.from(DOMAIN_LABEL)
										.where(
												DOMAIN_LABEL.CODE.eq(code)
														.and(DOMAIN_LABEL.ORIGIN.eq(origin))
														.and(DOMAIN_LABEL.LANG.eq(lang))
														.and(DOMAIN_LABEL.TYPE.eq(type)))))
				.execute();
		return resultCount > 0;
	}

	public boolean meaningExists(Long meaningId) {

		Meaning m = MEANING.as("m");

		return mainDb
				.fetchExists(DSL
						.select(m.ID)
						.from(m)
						.where(m.ID.eq(meaningId)));
	}

	public boolean wordExists(Long wordId) {

		Word w = WORD.as("w");

		return mainDb
				.fetchExists(DSL
						.select(w.ID)
						.from(w)
						.where(w.ID.eq(wordId)));
	}

	public Long getDefinitionId(Long meaningId, String definitionValue) {

		Definition d = DEFINITION.as("d");

		return mainDb
				.select(d.ID)
				.from(d)
				.where(
						d.MEANING_ID.eq(meaningId)
								.and(d.VALUE.eq(definitionValue)))
				.limit(1)
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getDefinitionId(Long meaningId, String definitionValue, String datasetCode) {

		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");

		return mainDb
				.select(d.ID)
				.from(d)
				.where(
						d.MEANING_ID.eq(meaningId)
								.and(d.VALUE.eq(definitionValue))
								.andExists(DSL
										.select(dd.DEFINITION_ID)
										.from(dd)
										.where(dd.DEFINITION_ID.eq(d.ID)
												.and(dd.DATASET_CODE.eq(datasetCode)))))
				.limit(1)
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public boolean definitionDatasetExists(Long definitionId, String datasetCode) {

		DefinitionDataset dd = DEFINITION_DATASET.as("dd");

		return mainDb
				.fetchExists(DSL
						.select(dd.DEFINITION_ID)
						.from(dd)
						.where(
								dd.DEFINITION_ID.eq(definitionId)
										.and(dd.DATASET_CODE.eq(datasetCode))));
	}

	public boolean wordOdUsageExists(Long wordId, String usageValue) {

		WordOdUsage wou = WORD_OD_USAGE.as("wou");

		return mainDb
				.fetchExists(DSL
						.select(wou.ID)
						.from(wou)
						.where(
								wou.WORD_ID.eq(wordId)
										.and(wou.VALUE.eq(usageValue))));
	}

	public boolean wordOdMorphExists(Long wordId) {

		WordOdMorph wom = WORD_OD_MORPH.as("wom");

		return mainDb
				.fetchExists(DSL
						.select(wom.ID)
						.from(wom)
						.where(
								wom.WORD_ID.eq(wordId)));
	}
}
