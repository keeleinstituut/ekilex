package eki.eve.service.db;

import eki.common.constant.FreeformType;
import eki.eve.constant.SystemConstant;
import eki.eve.data.db.tables.Form;
import eki.eve.data.db.tables.Freeform;
import eki.eve.data.db.tables.LexemeFreeform;
import eki.eve.data.db.tables.MorphLabel;
import eki.eve.data.db.tables.Paradigm;
import org.jooq.DSLContext;
import org.jooq.Record12;
import org.jooq.Record14;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static eki.eve.data.db.Tables.DATASET;
import static eki.eve.data.db.Tables.DEFINITION;
import static eki.eve.data.db.Tables.DOMAIN_LABEL;
import static eki.eve.data.db.Tables.FORM;
import static eki.eve.data.db.Tables.LEXEME;
import static eki.eve.data.db.Tables.MEANING;
import static eki.eve.data.db.Tables.MEANING_DATASET;
import static eki.eve.data.db.Tables.MEANING_DOMAIN;
import static eki.eve.data.db.Tables.MORPH_LABEL;
import static eki.eve.data.db.Tables.PARADIGM;
import static eki.eve.data.db.Tables.WORD;
import static eki.eve.data.db.Tables.FREEFORM;
import static eki.eve.data.db.Tables.LEXEME_DATASET;
import static eki.eve.data.db.Tables.LEXEME_FREEFORM;
import static eki.eve.data.db.Tables.MEANING_FREEFORM;

@Service
public class SearchDbService implements InitializingBean, SystemConstant {

	private static final int MAX_RESULTS_LIMIT = 50;

	private DSLContext create;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Autowired
	public SearchDbService(DSLContext context) {
		create = context;
	}

	public Result<Record4<Long, String, Integer, String>> findWords(String wordWithMetaCharacters) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");
		return create
				.select(FORM.ID.as("form_id"), FORM.VALUE.as("word"), WORD.HOMONYM_NR, WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.likeIgnoreCase(theFilter)
								.and(FORM.IS_WORD.isTrue())
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.orderBy(FORM.VALUE, WORD.HOMONYM_NR)
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}

	public Result<Record7<Long, String, String[], String, String, String, String>> findConnectedForms(Long formId) {

		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");
		Paradigm p = PARADIGM.as("p");
		MorphLabel m = MORPH_LABEL.as("m");
		return create
				.select(
						f2.ID.as("form_id"),
						f2.VALUE.as("word"),
						f2.COMPONENTS,
						f2.DISPLAY_FORM,
						f2.VOCAL_FORM,
						f2.MORPH_CODE,
						m.VALUE.as("morph_value")
				)
				.from(f1, f2, p, m)
				.where(
						f1.ID.eq(formId)
								.and(f1.PARADIGM_ID.eq(p.ID))
								.and(f2.PARADIGM_ID.eq(p.ID))
								.and(m.CODE.eq(f2.MORPH_CODE))
								.and(m.LANG.eq("est"))
								.and(m.TYPE.eq("descrip")))
				.orderBy(f2.ID)
				.fetch();
	}

	public Result<Record7<Long, String, String, String, String, String, String>> findConnectedWords(Long meaningId) {

		return create
				.select(FORM.ID.as("form_id"), FORM.VALUE.as("word"), WORD.LANG, FORM.DISPLAY_FORM, FORM.VOCAL_FORM, FORM.MORPH_CODE, MORPH_LABEL.VALUE.as("morph_value"))
				.from(LEXEME, WORD, PARADIGM,
						FORM.leftOuterJoin(MORPH_LABEL).on(FORM.MORPH_CODE.eq(MORPH_LABEL.CODE).and(MORPH_LABEL.LANG.eq("est").and(MORPH_LABEL.TYPE.eq("descrip")))))
				.where(
						FORM.PARADIGM_ID.eq(PARADIGM.ID)
								.and(FORM.IS_WORD.eq(Boolean.TRUE))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.MEANING_ID.eq(meaningId)))
				.fetch();
	}

	public Result<Record14<String, Long, Long, Long, Integer, Integer, Integer, String, String, String, String, String, String[], String[]>> findFormMeanings(Long formId) {

		return create
				.select(
						FORM.VALUE.as("word"), WORD.ID.as("word_id"), LEXEME.ID.as("lexeme_id"), LEXEME.MEANING_ID,
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME.TYPE_CODE.as("lexeme_type_code"),
						LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"),
						MEANING.TYPE_CODE.as("meaning_type_code"),
						MEANING.ENTRY_CLASS_CODE.as("meaning_entry_class_code"),
						MEANING.STATE_CODE.as("meaning_state_code"),
						DSL.arrayAggDistinct(MEANING_DATASET.DATASET_CODE).as("datasets"),
						DSL.when(DSL.count(DEFINITION.VALUE).eq(0), new String[0]).otherwise(DSL.arrayAgg(DEFINITION.VALUE).orderBy(DEFINITION.ID)).as("definitions"))
				.from(FORM, PARADIGM, WORD, LEXEME,
						MEANING.leftOuterJoin(DEFINITION).on(DEFINITION.MEANING_ID.eq(MEANING.ID)).leftOuterJoin(MEANING_DATASET).on(MEANING_DATASET.MEANING_ID.eq(MEANING.ID))
				)
				.where(
						FORM.ID.eq(formId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID)))
				.groupBy(FORM.ID, WORD.ID, LEXEME.ID, MEANING.ID)
				.orderBy(WORD.ID, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}

	public Result<Record3<String, String, String>> findMeaningDomains(Long meaningId) {

		return create
				.select(DOMAIN_LABEL.CODE, DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.VALUE)
				.from(
						MEANING_DOMAIN.leftOuterJoin(DOMAIN_LABEL).on(
								MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE)
										.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN))
										.and(DOMAIN_LABEL.LANG.eq("eng"))
										.and(DOMAIN_LABEL.TYPE.eq("descrip"))
						)
				)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId))
				.fetch();
	}

	public Result<Record12<Long,String,Long,Long,String,String,Long,String,String,Long,String,String>> findRectionUsageTranslationDefinitionTuples(Long lexemeId) {

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform r = FREEFORM.as("r");
		Freeform um = FREEFORM.as("um");
		Freeform u = FREEFORM.as("u");
		Freeform ut = FREEFORM.as("ut");
		Freeform ud = FREEFORM.as("ud");

		return create
				.select(
						r.ID.as("rection_id"),
						r.VALUE_TEXT.as("rection_value"),
						um.ID.as("usage_meaning_id"),
						u.ID.as("usage_id"),
						u.VALUE_TEXT.as("usage_value"),
						u.LANG.as("usage_lang"),
						ut.ID.as("usage_translation_id"),
						ut.VALUE_TEXT.as("usage_translation_value"),
						ut.LANG.as("usage_translation_lang"),
						ud.ID.as("usage_definition_id"),
						ud.VALUE_TEXT.as("usage_definition_value"),
						ud.LANG.as("usage_definition_lang")
				)
				.from(
						lff.innerJoin(r
								.leftOuterJoin(um).on(um.PARENT_ID.eq(r.ID).and(um.TYPE.eq(FreeformType.USAGE_MEANING.name())))
								.leftOuterJoin(u).on(u.PARENT_ID.eq(um.ID).and(u.TYPE.eq(FreeformType.USAGE.name())))
								.leftOuterJoin(ut).on(ut.PARENT_ID.eq(um.ID).and(ut.TYPE.eq(FreeformType.USAGE_TRANSLATION.name())))
								.leftOuterJoin(ud).on(ud.PARENT_ID.eq(um.ID).and(ud.TYPE.eq(FreeformType.USAGE_DEFINITION.name())))
						).on(lff.FREEFORM_ID.eq(r.ID).and(r.TYPE.eq(FreeformType.RECTION.name())))
				)
				.where(lff.LEXEME_ID.eq(lexemeId))
				.orderBy(r.ID, um.ID, u.ID, ut.ID, ud.ID)
				.fetch();
	}

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public Result<Record4<Long, String, Integer, String>> findWordsInDatasets(String wordWithMetaCharacters, List<String> datasets) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");
		return create
				.select(FORM.ID.as("form_id"), FORM.VALUE.as("word"), WORD.HOMONYM_NR, WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.likeIgnoreCase(theFilter)
								.and(FORM.IS_WORD.isTrue())
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.andExists(DSL.select(LEXEME.WORD_ID).from(LEXEME, LEXEME_DATASET)
										.where((LEXEME.WORD_ID.eq(WORD.ID))
												.and(LEXEME_DATASET.LEXEME_ID.eq(LEXEME.ID))
												.and(LEXEME_DATASET.DATASET_CODE.in(datasets)))
								)
				)
				.orderBy(FORM.VALUE, WORD.HOMONYM_NR)
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}

	public Result<Record14<String, Long, Long, Long, Integer, Integer, Integer, String, String, String, String, String, String[], String[]>> findFormMeaningsInDatasets(Long formId, List<String> selectedDatasets) {

		return create
				.select(
						FORM.VALUE.as("word"), WORD.ID.as("word_id"), LEXEME.ID.as("lexeme_id"), LEXEME.MEANING_ID,
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME.TYPE_CODE.as("lexeme_type_code"),
						LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"),
						MEANING.TYPE_CODE.as("meaning_type_code"),
						MEANING.ENTRY_CLASS_CODE.as("meaning_entry_class_code"),
						MEANING.STATE_CODE.as("meaning_state_code"),
						DSL.arrayAggDistinct(MEANING_DATASET.DATASET_CODE).as("datasets"),
						DSL.when(DSL.count(DEFINITION.VALUE).eq(0), new String[0]).otherwise(DSL.arrayAgg(DEFINITION.VALUE).orderBy(DEFINITION.ID)).as("definitions"))
				.from(FORM, PARADIGM, WORD, LEXEME,
						MEANING.leftOuterJoin(DEFINITION).on(DEFINITION.MEANING_ID.eq(MEANING.ID)).leftOuterJoin(MEANING_DATASET).on(MEANING_DATASET.MEANING_ID.eq(MEANING.ID))
				)
				.where(
						FORM.ID.eq(formId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(MEANING_DATASET.DATASET_CODE.in(selectedDatasets)))
				.groupBy(FORM.ID, WORD.ID, LEXEME.ID, MEANING.ID)
				.orderBy(WORD.ID, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}

	public Result<Record7<Long, String, String, String, String, String, String>> findConnectedWordsInDatasets(Long meaningId, List<String> datasets) {

		return create
				.select(FORM.ID.as("form_id"), FORM.VALUE.as("word"), WORD.LANG, FORM.DISPLAY_FORM, FORM.VOCAL_FORM, FORM.MORPH_CODE, MORPH_LABEL.VALUE.as("morph_value"))
				.from(LEXEME, WORD, PARADIGM,
						FORM.leftOuterJoin(MORPH_LABEL).on(FORM.MORPH_CODE.eq(MORPH_LABEL.CODE).and(MORPH_LABEL.LANG.eq("est").and(MORPH_LABEL.TYPE.eq("descrip")))))
				.where(
						FORM.PARADIGM_ID.eq(PARADIGM.ID)
								.and(FORM.IS_WORD.eq(Boolean.TRUE))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.MEANING_ID.eq(meaningId))
								.andExists(DSL.select(LEXEME_DATASET.LEXEME_ID).from(LEXEME_DATASET)
										.where((LEXEME_DATASET.LEXEME_ID.eq(LEXEME.ID))
												.and(LEXEME_DATASET.DATASET_CODE.in(datasets))))
				)
				.fetch();
	}

	public Result<Record4<Long, String, String, Timestamp>> findLexemeFreeforms(Long lexemeId) {
		return create
				.select(FREEFORM.ID, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_DATE)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId).and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.notIn(FreeformType.RECTION.name(), FreeformType.GRAMMAR.name())))
				.fetch();
	}

	public Result<Record4<Long, String, String, Timestamp>> findMeaningFreeforms(Long meaningId) {
		return create
				.select(FREEFORM.ID, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_DATE)
				.from(FREEFORM, MEANING_FREEFORM)
				.where(MEANING_FREEFORM.MEANING_ID.eq(meaningId).and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID)))
				.fetch();
	}

	public Result<Record2<String, String>> findMeaningDefinitions(Long meaningId) {
		return create
				.select(DEFINITION.LANG, DEFINITION.VALUE)
				.from(DEFINITION)
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.orderBy(DEFINITION.ID)
				.fetch();
	}

}
