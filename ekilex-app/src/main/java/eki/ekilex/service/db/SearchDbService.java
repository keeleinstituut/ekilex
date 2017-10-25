package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DATASET;
import static eki.ekilex.data.db.Tables.LEXEME_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DATASET;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.RECTION;
import static eki.ekilex.data.db.Tables.USAGE;
import static eki.ekilex.data.db.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.MorphLabel;
import eki.ekilex.data.db.tables.Paradigm;

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

	public Result<Record2<String, String[][]>> findConnectedRections(Long lexemeId) {

		return create
				.select(RECTION.VALUE.as("rection"), DSL.arrayAgg(DSL.array(USAGE.VALUE, USAGE_TRANSLATION.VALUE)).orderBy(USAGE.ID).as("usages"))
				.from(RECTION.leftOuterJoin(USAGE).on(USAGE.RECTION_ID.eq(RECTION.ID)).leftOuterJoin(USAGE_TRANSLATION).on(USAGE_TRANSLATION.USAGE_ID.eq(USAGE.ID)))
				.where(
						RECTION.LEXEME_ID.eq(lexemeId))
				.groupBy(RECTION.ID)
				.fetch();
	}

	public Result<Record11<String, Long, Long, Integer, Integer, Integer, String, String, Long, String[], String[]>> findFormMeanings(Long formId) {

		return create
				.select(
						FORM.VALUE.as("word"), WORD.ID.as("word_id"), LEXEME.ID.as("lexeme_id"), LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME_TYPE_LABEL.CODE.as("lexeme_type_code"), LEXEME_TYPE_LABEL.VALUE.as("lexeme_type_value"),
						LEXEME.MEANING_ID, DSL.arrayAggDistinct(MEANING_DATASET.DATASET_CODE).as("datasets"),
						DSL.when(DSL.count(DEFINITION.VALUE).eq(0), new String[0]).otherwise(DSL.arrayAgg(DEFINITION.VALUE).orderBy(DEFINITION.ID)).as("definitions"))
				.from(FORM, PARADIGM, WORD,
						LEXEME.leftOuterJoin(LEXEME_TYPE_LABEL).on(LEXEME_TYPE_LABEL.CODE.eq(LEXEME.TYPE).and(LEXEME_TYPE_LABEL.LANG.eq("est")).and(LEXEME_TYPE_LABEL.TYPE.eq("descrip"))),
						MEANING.leftOuterJoin(DEFINITION).on(DEFINITION.MEANING_ID.eq(MEANING.ID)).leftOuterJoin(MEANING_DATASET).on(MEANING_DATASET.MEANING_ID.eq(MEANING.ID))
						)
				.where(
						FORM.ID.eq(formId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(MEANING.ID)))
				.groupBy(FORM.ID, WORD.ID, LEXEME.ID, MEANING.ID, LEXEME_TYPE_LABEL.CODE, LEXEME_TYPE_LABEL.VALUE)
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

}
