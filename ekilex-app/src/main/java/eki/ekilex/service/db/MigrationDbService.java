package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.PUBLISHING;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_OS_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OS_USAGE;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;

import java.util.List;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.PublishingConstant;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Publishing;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordOsMorph;
import eki.ekilex.data.db.main.tables.WordOsUsage;
import eki.ekilex.data.db.main.tables.WordRelation;
import eki.ekilex.data.migra.DefinitionDuplicate;

// temporary content for data migration tools
@Component
public class MigrationDbService extends AbstractDataDbService implements PublishingConstant, GlobalConstant {

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

	public boolean wordOsUsageExists(Long wordId, String usageValue) {

		WordOsUsage wou = WORD_OS_USAGE.as("wou");

		return mainDb
				.fetchExists(DSL
						.select(wou.ID)
						.from(wou)
						.where(
								wou.WORD_ID.eq(wordId)
										.and(wou.VALUE.eq(usageValue))));
	}

	public boolean wordOsMorphExists(Long wordId) {

		WordOsMorph wom = WORD_OS_MORPH.as("wom");

		return mainDb
				.fetchExists(DSL
						.select(wom.ID)
						.from(wom)
						.where(
								wom.WORD_ID.eq(wordId)));
	}

	public List<DefinitionDuplicate> getDefinitionDuplicates(String datasetCode) {

		Definition d1 = DEFINITION.as("d1");
		Definition d2 = DEFINITION.as("d2");
		Publishing p = PUBLISHING.as("p");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");

		return mainDb
				.select(
						d1.ID.as("definition_id"),
						d1.MEANING_ID,
						p.ID.as("publishing_id"),
						p.TARGET_NAME)
				.from(d1
						.leftOuterJoin(p).on(
								p.ENTITY_NAME.eq(ENTITY_NAME_DEFINITION)
										.and(p.ENTITY_ID.eq(d1.ID))))
				.whereExists(DSL
						.select(dd.DEFINITION_ID)
						.from(dd)
						.where(
								dd.DEFINITION_ID.eq(d1.ID)
										.and(dd.DATASET_CODE.eq(datasetCode))))
				.andExists(DSL
						.select(d2.ID)
						.from(d2)
						.where(
								d2.MEANING_ID.eq(d1.MEANING_ID)
										.and(d2.VALUE.eq(d1.VALUE))
										.and(d2.ID.ne(d1.ID))
										.andExists(DSL
												.select(dd.DEFINITION_ID)
												.from(dd)
												.where(
														dd.DEFINITION_ID.eq(d2.ID)
																.and(dd.DATASET_CODE.eq(datasetCode))))))
				.orderBy(d1.MEANING_ID, d1.VALUE, d1.ID)
				.fetchInto(DefinitionDuplicate.class);
	}

	public void updatePublishingEntityId(Long publishingId, Long entityId) {

		mainDb
				.update(PUBLISHING)
				.set(PUBLISHING.ENTITY_ID, entityId)
				.where(PUBLISHING.ID.eq(publishingId))
				.execute();
	}

	public List<eki.ekilex.data.migra.WordRelation> getWordRelationsByContaingPrefix(String wordRelTypeCode, String relatedWordValuePrefix) {

		Word w1 = WORD.as("w1");
		Word w2 = WORD.as("w2");
		Word wt = WORD.as("wt");
		WordRelation wr = WORD_RELATION.as("wr");
		WordRelation wrt = WORD_RELATION.as("wre");
		Field<String> w2ValueFilter = DSL.lower(relatedWordValuePrefix + " %");

		return mainDb
				.select(
						wr.ID,
						wr.WORD1_ID,
						w1.VALUE.as("word1_value"),
						wr.WORD2_ID,
						w2.VALUE.as("word2_value"),
						wr.WORD_REL_TYPE_CODE,
						wr.ORDER_BY)
				.from(wr, w1, w2)
				.where(
						wr.WORD1_ID.eq(w1.ID)
								.and(wr.WORD2_ID.eq(w2.ID))
								.and(wr.WORD_REL_TYPE_CODE.eq(wordRelTypeCode))
								.andExists(DSL
										.select(wrt.ID)
										.from(wrt, wt)
										.where(
												wrt.WORD1_ID.eq(wr.WORD1_ID)
														.and(wrt.WORD2_ID.eq(wt.ID))
														.and(wrt.WORD_REL_TYPE_CODE.eq(wordRelTypeCode))
														.and(DSL.lower(wt.VALUE).like(w2ValueFilter))))

				)
				.orderBy(w1.ID, wr.ORDER_BY)
				.fetchInto(eki.ekilex.data.migra.WordRelation.class);
	}
}
