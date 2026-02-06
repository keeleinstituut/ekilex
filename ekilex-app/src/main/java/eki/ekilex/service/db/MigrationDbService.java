package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Public.PUBLIC;
import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.main.Tables.GRAMMAR;
import static eki.ekilex.data.db.main.Tables.LEARNER_COMMENT;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.PUBLISHING;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_EKI_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_OS_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OS_USAGE;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.PublishingConstant;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Publishing;
import eki.ekilex.data.db.main.tables.Usage;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordOsMorph;
import eki.ekilex.data.db.main.tables.WordOsUsage;
import eki.ekilex.data.db.main.tables.WordRelation;
import eki.ekilex.data.migra.DefinitionDuplicate;
import eki.ekilex.data.migra.ValueId;
import eki.ekilex.service.db.util.QueryHelper;

// temporary content for data migration tools
@Component
public class MigrationDbService extends AbstractDataDbService implements PublishingConstant, GlobalConstant {

	@Autowired
	private QueryHelper queryHelper;

	public List<eki.ekilex.data.migra.Colloc> getCollocationsWithDuplicates() {

		Word cw = WORD.as("cw");
		Word cwd = WORD.as("cwd");
		Lexeme cl = LEXEME.as("cl");
		Lexeme cld = LEXEME.as("cld");
		CollocationMember cm = COLLOCATION_MEMBER.as("cm");
		CollocationMember cme = COLLOCATION_MEMBER.as("cme");
		Usage u = USAGE.as("u");

		Field<String[]> uvf = DSL
				.select(DSL
						.arrayAgg(u.VALUE)
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(cl.ID))
				.asField();

		Field<JSON> memf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(cm.ID),
										DSL.key("collocLexemeId").value(cm.COLLOC_LEXEME_ID),
										DSL.key("memberLexemeId").value(cm.MEMBER_LEXEME_ID),
										DSL.key("conjunctLexemeId").value(cm.CONJUNCT_LEXEME_ID),
										DSL.key("memberFormId").value(cm.MEMBER_FORM_ID),
										DSL.key("posGroupCode").value(cm.POS_GROUP_CODE),
										DSL.key("relGroupCode").value(cm.REL_GROUP_CODE),
										DSL.key("weight").value(cm.WEIGHT),
										DSL.key("memberOrder").value(cm.MEMBER_ORDER),
										DSL.key("groupOrder").value(cm.GROUP_ORDER)))
						.orderBy(cm.MEMBER_ORDER))
				.from(cm)
				.where(cm.COLLOC_LEXEME_ID.eq(cl.ID))
				.asField();

		Condition where = cl.WORD_ID.eq(cw.ID)
				.and(cl.IS_WORD.isFalse())
				.and(cl.IS_COLLOCATION.isTrue())
				.andExists(DSL
						.select(cme.ID)
						.from(cme)
						.where(cme.COLLOC_LEXEME_ID.eq(cl.ID)))
				.andExists(DSL
						.select(cld.ID)
						.from(cwd, cld)
						.where(
								cld.WORD_ID.eq(cwd.ID)
										.and(cld.IS_WORD.isFalse())
										.and(cld.IS_COLLOCATION.isTrue())
										.and(cld.ID.ne(cl.ID))
										.and(cwd.VALUE.eq(cw.VALUE))
										.andExists(DSL
												.select(cme.ID)
												.from(cme)
												.where(cme.COLLOC_LEXEME_ID.eq(cld.ID)))));

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME, cl.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_LEXEME, cl.ID);

		return mainDb
				.select(
						cl.ID.as("colloc_lexeme_id"),
						cw.ID.as("colloc_word_id"),
						cw.VALUE.as("colloc_word_value"),
						uvf.as("usage_values"),
						memf.as("colloc_members"),
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"))
				.from(cw, cl)
				.where(where)
				.orderBy(
						cw.VALUE,
						cl.ID)
				.fetchInto(eki.ekilex.data.migra.Colloc.class);

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

	public List<ValueId> getWordValues(String containingStr) {

		return getValues(containingStr, WORD, WORD.LANG);
	}

	public List<ValueId> getDefinitionValues(String containingStr) {

		return getValues(containingStr, DEFINITION, DEFINITION.LANG);
	}

	public List<ValueId> getUsageValues(String containingStr) {

		return getValues(containingStr, USAGE, USAGE.LANG);
	}

	public List<ValueId> getWordEkiRecommendationValues(String containingStr) {

		return getValues(containingStr, WORD_EKI_RECOMMENDATION, DSL.value(LANGUAGE_CODE_EST));
	}

	public List<ValueId> getWordOsUsageValues(String containingStr) {

		return getValues(containingStr, WORD_OS_USAGE, DSL.value(LANGUAGE_CODE_EST));
	}

	public List<ValueId> getMeaningNoteValues(String containingStr) {

		return getValues(containingStr, MEANING_NOTE, MEANING_NOTE.LANG);
	}

	public List<ValueId> getLexemeNoteValues(String containingStr) {

		return getValues(containingStr, LEXEME_NOTE, LEXEME_NOTE.LANG);
	}

	public List<ValueId> getMeaningForumValues(String containingStr) {

		return getValues(containingStr, MEANING_FORUM, DSL.value(LANGUAGE_CODE_EST));
	}

	public List<ValueId> getLearnerCommentValues(String containingStr) {

		return getValues(containingStr, LEARNER_COMMENT, DSL.value(LANGUAGE_CODE_EST));
	}

	public List<ValueId> getGrammarValues(String containingStr) {

		return getValues(containingStr, GRAMMAR, GRAMMAR.LANG);
	}

	public List<ValueId> getSourceValues(String containingStr) {

		return getValues(containingStr, SOURCE, DSL.value(LANGUAGE_CODE_EST));
	}

	private List<ValueId> getValues(String containingStr, Table<?> t, Field<String> langField) {

		String containingCrit = "%" + containingStr + "%";
		Condition where = t.field("value_prese", String.class).like(containingCrit);
		return mainDb
				.select(
						DSL.value(t.getName()).as("table_name"),
						t.field("id", Long.class),
						t.field("value", String.class),
						t.field("value_prese", String.class),
						langField.as("lang"))
				.from(t)
				.where(where)
				.fetchInto(ValueId.class);
	}

	public void updateValue(String tableName, Long id, String value, String valuePrese) {

		Table<?> t = PUBLIC.getTable(tableName).as("t");
		mainDb
				.update(t)
				.set(t.field("value", String.class), value)
				.set(t.field("value_prese", String.class), valuePrese)
				.where(t.field("id", Long.class).eq(id))
				.execute();
	}

	public void updateValuePrese(String tableName, Long id, String valuePrese) {

		Table<?> t = PUBLIC.getTable(tableName).as("t");
		mainDb
				.update(t)
				.set(t.field("value_prese", String.class), valuePrese)
				.where(t.field("id", Long.class).eq(id))
				.execute();
	}
}
