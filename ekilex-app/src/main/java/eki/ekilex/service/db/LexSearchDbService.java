package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FORM_RELATION;
import static eki.ekilex.data.db.Tables.FORM_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.POS_LABEL;
import static eki.ekilex.data.db.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record12;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.MorphLabel;
import eki.ekilex.data.db.tables.Paradigm;

@Service
public class LexSearchDbService implements InitializingBean, SystemConstant {

	private static final int MAX_RESULTS_LIMIT = 50;

	private DSLContext create;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Autowired
	public LexSearchDbService(DSLContext context) {
		create = context;
	}

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public Result<Record7<Long, String, String[], String, String, String, String>> findConnectedForms(Long formId, String classifierLabelLang, String classifierLabelTypeCode) {

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
						.and(m.LANG.eq(classifierLabelLang))
						.and(m.TYPE.eq(classifierLabelTypeCode)))
				.orderBy(f2.ID)
				.fetch();
	}

	public Result<Record14<String,Long,Long,Long,String,Integer,Integer,Integer,String,String,String,String,String,String[]>> findFormMeanings(Long formId) {

		return create
				.select(
						FORM.VALUE.as("word"),
						WORD.ID.as("word_id"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.MEANING_ID,
						LEXEME.DATASET_CODE.as("dataset"),
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME.TYPE_CODE.as("lexeme_type_code"),
						LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"),
						MEANING.TYPE_CODE.as("meaning_type_code"),
						MEANING.PROCESS_STATE_CODE.as("meaning_process_state_code"),
						MEANING.STATE_CODE.as("meaning_state_code"),
						DSL.when(DSL.count(DEFINITION.VALUE).eq(0), new String[0]).otherwise(DSL.arrayAgg(DEFINITION.VALUE).orderBy(DEFINITION.ID)).as("definitions"))
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING.leftOuterJoin(DEFINITION).on(DEFINITION.MEANING_ID.eq(MEANING.ID)))
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

	public Result<Record2<String, String>> findLexemePos(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(POS_LABEL.CODE, POS_LABEL.VALUE)
				.from(LEXEME_POS, POS_LABEL)
				.where(
						LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(POS_LABEL.CODE.eq(LEXEME_POS.POS_CODE))
						.and(POS_LABEL.LANG.eq(classifierLabelLang))
						.and(POS_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.fetch();
	}

	public Result<Record2<String, String>> findLexemeDerivs(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(DERIV_LABEL.CODE, DERIV_LABEL.VALUE)
				.from(LEXEME_DERIV, DERIV_LABEL)
				.where(
						LEXEME_DERIV.LEXEME_ID.eq(lexemeId)
						.and(DERIV_LABEL.CODE.eq(LEXEME_DERIV.DERIV_CODE))
						.and(DERIV_LABEL.LANG.eq(classifierLabelLang))
						.and(DERIV_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.fetch();
	}

	public Result<Record2<String, String>> findLexemeRegisters(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(REGISTER_LABEL.CODE, REGISTER_LABEL.VALUE)
				.from(LEXEME_REGISTER, REGISTER_LABEL)
				.where(
						LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)
						.and(REGISTER_LABEL.CODE.eq(LEXEME_REGISTER.REGISTER_CODE))
						.and(REGISTER_LABEL.LANG.eq(classifierLabelLang))
						.and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.fetch();
	}

	public Result<Record3<String, String, String>> findMeaningDomains(Long meaningId) {

		return create
				.select(DOMAIN_LABEL.CODE, DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.VALUE)
				.from(
						MEANING_DOMAIN.leftOuterJoin(DOMAIN_LABEL).on(
								MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE)
								.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN))
								)
						)
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId))
				.fetch();
	}

	public Result<Record12<Long, String, Long, Long, String, String, Long, String, String, Long, String, String>> findRectionUsageTranslationDefinitionTuples(Long lexemeId) {

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

	public Result<Record4<Long, String, Integer, String>> findWordsInDatasets(String wordWithMetaCharacters, List<String> datasets) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");
		return create
				.select(
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.HOMONYM_NR,
						WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.likeIgnoreCase(theFilter)
						.and(FORM.IS_WORD.isTrue())
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.andExists(DSL.select(LEXEME.ID).from(LEXEME)
								.where((LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.DATASET_CODE.in(datasets)))
						)
				)
				.orderBy(FORM.VALUE, WORD.HOMONYM_NR)
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}

	public Result<Record15<String,String,Long,String,Long,Long,String,Integer,Integer,Integer,String,String,String,String,String>> findFormMeaningsInDatasets(
			Long formId, List<String> selectedDatasets) {

		return 
				create
				.select(
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						WORD.ID.as("word_id"),
						WORD.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.MEANING_ID,
						LEXEME.DATASET_CODE.as("dataset"),
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.LEVEL3,
						LEXEME.TYPE_CODE.as("lexeme_type_code"),
						LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"),
						MEANING.TYPE_CODE.as("meaning_type_code"),
						MEANING.PROCESS_STATE_CODE.as("meaning_process_state_code"),
						MEANING.STATE_CODE.as("meaning_state_code"))
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING)
				.where(
						FORM.ID.eq(formId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(MEANING.ID))
						.and(LEXEME.DATASET_CODE.in(selectedDatasets)))
				.groupBy(FORM.ID, WORD.ID, LEXEME.ID, MEANING.ID)
				.orderBy(WORD.ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}

	public Result<Record7<Long, String, String, String, String, String, String>> findConnectedWordsInDatasets(
			Long sourceFormId, Long meaningId, List<String> datasets, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.LANG,
						FORM.DISPLAY_FORM,
						FORM.VOCAL_FORM,
						FORM.MORPH_CODE,
						MORPH_LABEL.VALUE.as("morph_value")
						)
				.from(LEXEME, WORD, PARADIGM,
						FORM.leftOuterJoin(MORPH_LABEL).on(
								FORM.MORPH_CODE.eq(MORPH_LABEL.CODE)
								.and(MORPH_LABEL.LANG.eq(classifierLabelLang)
								.and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode))))
						)
				.where(
						FORM.PARADIGM_ID.eq(PARADIGM.ID)
						.and(FORM.ID.ne(sourceFormId))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(meaningId))
						.and(LEXEME.DATASET_CODE.in(datasets))
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

	public Result<Record2<Long, String>> findMeaningDefinitions(Long meaningId) {
		return create
				.select(DEFINITION.ID, DEFINITION.VALUE)
				.from(DEFINITION)
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.fetch();
	}

	public Result<Record6<Long,Long,Long,String,String,String>> findLexemeRelations(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						LEXEME.ID.as("lexeme_id"),
						WORD.ID.as("word_id"),
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						LEX_REL_TYPE_LABEL.VALUE.as("rel_type_label")
						)
				.from(
						LEX_RELATION.leftOuterJoin(LEX_REL_TYPE_LABEL).on(
								LEX_RELATION.LEX_REL_TYPE_CODE.eq(LEX_REL_TYPE_LABEL.CODE)
								.and(LEX_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
								.and(LEX_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						LEXEME,
						WORD,
						PARADIGM,
						FORM
						)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId)
						.and(LEX_RELATION.LEXEME2_ID.eq(LEXEME.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						)
				.orderBy(FORM.VALUE)
				.fetch();
				
	}

	public Result<Record3<String,String,String>> findWordRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						WORD_REL_TYPE_LABEL.VALUE.as("rel_type_label")
						)
				.from(
						WORD_RELATION.leftOuterJoin(WORD_REL_TYPE_LABEL).on(
								WORD_RELATION.WORD_REL_TYPE_CODE.eq(WORD_REL_TYPE_LABEL.CODE)
								.and(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
								.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						WORD,
						PARADIGM,
						FORM
						)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
						.and(WORD_RELATION.WORD2_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						)
				.orderBy(FORM.VALUE)
				.fetch();
	}

	public Result<Record7<Long,Long,Long,Long,String,String,String>> findMeaningRelations(Long meaningId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						MEANING.ID.as("meaning_id"),
						LEXEME.ID.as("lexeme_id"),
						WORD.ID.as("word_id"),
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						MEANING_REL_TYPE_LABEL.VALUE.as("rel_type_label")
				)
				.from(
						MEANING_RELATION.leftOuterJoin(MEANING_REL_TYPE_LABEL).on(
								MEANING_RELATION.MEANING_REL_TYPE_CODE.eq(MEANING_REL_TYPE_LABEL.CODE)
										.and(MEANING_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						MEANING,
						LEXEME,
						WORD,
						PARADIGM,
						FORM
				)
				.where(
						MEANING_RELATION.MEANING1_ID.eq(meaningId)
								.and(MEANING_RELATION.MEANING2_ID.eq(MEANING.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.IS_WORD.eq(Boolean.TRUE))
				)
				.orderBy(FORM.VALUE)
				.fetch();
	}

	public Result<Record3<String,String,String>> findFormRelations(Long formId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						FORM_REL_TYPE_LABEL.VALUE.as("rel_type_label")
						)
				.from(
						FORM_RELATION.leftOuterJoin(FORM_REL_TYPE_LABEL).on(
								FORM_RELATION.FORM_REL_TYPE_CODE.eq(FORM_REL_TYPE_LABEL.CODE)
								.and(FORM_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
								.and(FORM_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						WORD,
						PARADIGM,
						FORM
						)
				.where(
						FORM_RELATION.FORM2_ID.eq(formId)
						.and(FORM_RELATION.FORM1_ID.eq(FORM.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						)
				.orderBy(FORM.VALUE)
				.fetch();
	}

}
