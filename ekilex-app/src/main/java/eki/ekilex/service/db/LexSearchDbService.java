package eki.ekilex.service.db;

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
import static eki.ekilex.data.db.Tables.USAGE_AUTHOR_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.sql.Timestamp;
import java.util.List;

import eki.ekilex.data.db.tables.UsageAuthorTypeLabel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

@Service
public class LexSearchDbService implements SystemConstant {

	private static final int MAX_RESULTS_LIMIT = 50;

	private DSLContext create;

	@Autowired
	public LexSearchDbService(DSLContext context) {
		create = context;
	}

	public Result<Record> findWords(SearchFilter searchFilter, List<String> datasets) throws Exception {

		List<SearchCriterion> searchCriteria = searchFilter.getSearchCriteria();

		Word w1 = WORD.as("w1");
		Paradigm p1 = PARADIGM.as("p1");
		Form f1 = FORM.as("f1");

		Table<Record> from1 = w1.join(p1.join(f1).on(f1.PARADIGM_ID.eq(p1.ID).and(f1.IS_WORD.isTrue()))).on(p1.WORD_ID.eq(w1.ID));

		Condition where1 = DSL.trueCondition();

		for (SearchCriterion searchCriterion : searchCriteria) {

			SearchKey searchKey = searchCriterion.getSearchKey();
			SearchOperand searchOperand = searchCriterion.getSearchOperand();
			Object searchValue = searchCriterion.getSearchValue();
			String searchValueStr = searchValue.toString();
			searchValueStr = StringUtils.lowerCase(searchValueStr);

			if (SearchKey.WORD_VALUE.equals(searchKey)) {

				where1 = applySearchValueFilter(searchValueStr, searchOperand, f1.VALUE, where1);

			} else if (SearchKey.FORM_VALUE.equals(searchKey)) {

				Paradigm p2 = PARADIGM.as("p2");
				Form f2 = FORM.as("f2");
				Condition where2 = f2.PARADIGM_ID.eq(p2.ID).and(p2.WORD_ID.eq(w1.ID));
				where2 = applySearchValueFilter(searchValueStr, searchOperand, f2.VALUE, where2);
				where1 = where1.and(DSL.exists(DSL.select(f2.ID).from(f2, p2).where(where2)));

			} else if (SearchKey.DEFINITION_VALUE.equals(searchKey)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				Definition d2 = DEFINITION.as("d2");
				Condition where2 = l2.WORD_ID.eq(w1.ID).and(l2.MEANING_ID.eq(m2.ID)).and(d2.MEANING_ID.eq(m2.ID));
				where2 = applySearchValueFilter(searchValueStr, searchOperand, d2.VALUE, where2);
				where1 = where1.and(DSL.exists(DSL.select(d2.ID).from(l2, m2, d2).where(where2)));

			} else if (SearchKey.USAGE_VALUE.equals(searchKey)) {

				Lexeme l2 = LEXEME.as("l2");
				LexemeFreeform l2ff = LEXEME_FREEFORM.as("l2ff");
				Freeform rect2 = FREEFORM.as("rect2");
				Freeform um2 = FREEFORM.as("um2");
				Freeform u2 = FREEFORM.as("u2");

				Condition where2 =
						l2.WORD_ID.eq(w1.ID)
						.and(l2ff.LEXEME_ID.eq(l2.ID))
						.and(l2ff.FREEFORM_ID.eq(rect2.ID))
						.and(rect2.TYPE.eq(FreeformType.RECTION.name()))
						.and(um2.PARENT_ID.eq(rect2.ID))
						.and(um2.TYPE.eq(FreeformType.USAGE_MEANING.name()))
						.and(u2.PARENT_ID.eq(um2.ID))
						.and(u2.TYPE.eq(FreeformType.USAGE.name()));

				where2 = applySearchValueFilter(searchValueStr, searchOperand, u2.VALUE_TEXT, where2);
				where1 = where1.and(DSL.exists(DSL.select(u2.ID).from(l2, l2ff, rect2, um2, u2).where(where2)));

			} else if (SearchKey.CONCEPT_ID.equals(searchKey)) {

				Lexeme l2 = LEXEME.as("l2");
				LexemeFreeform l2ff = LEXEME_FREEFORM.as("l2ff");
				Freeform concept = FREEFORM.as("concept");

				Condition where2 =
						l2.WORD_ID.eq(w1.ID)
						.and(l2ff.LEXEME_ID.eq(l2.ID))
						.and(l2ff.FREEFORM_ID.eq(concept.ID))
						.and(concept.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				where2 = applySearchValueFilter(searchValueStr, searchOperand, concept.VALUE_TEXT, where2);
				where1 = where1.and(DSL.exists(DSL.select(concept.ID).from(l2, l2ff, concept).where(where2)));
			}
		}
		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme l1 = LEXEME.as("l1");
			where1 = where1.andExists(
						DSL.select(l1.ID).from(l1)
						.where((l1.WORD_ID.eq(w1.ID))
						.and(l1.DATASET_CODE.in(datasets))));
		}

		Table<Record4<Long,String,Integer,String>> wordsQuery = create
				.select(
						w1.ID.as("word_id"),
						DSL.field("(array_agg(distinct f1.value))[1]").cast(String.class).as("word"),
						w1.HOMONYM_NR,
						w1.LANG)
				.from(from1)
				.where(where1)
				.groupBy(w1.ID)
				.asTable("wq");

		return create
				.select(wordsQuery.fields())
				.from(wordsQuery)
				.orderBy(wordsQuery.field("word"), wordsQuery.field("homonym_nr"))
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}

	private Condition applySearchValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition) throws Exception {

		if (SearchOperand.EQUALS.equals(searchOperand)) {
			condition = condition.and(searchField.equalIgnoreCase(searchValueStr));
		} else if (SearchOperand.STARTS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().startsWith(searchValueStr));
		} else if (SearchOperand.ENDS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().endsWith(searchValueStr));
		} else if (SearchOperand.CONTAINS.equals(searchOperand)) {
			condition = condition.and(searchField.lower().contains(searchValueStr));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	public Result<Record> findWords(String wordWithMetaCharacters, List<String> datasets) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");
		
		Table<Record4<Long, String, Integer, String>> wordsQuery = create
				.select(
						WORD.ID.as("word_id"),
						DSL.field("(array_agg(distinct form.value))[1]").cast(String.class).as("word"),
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
				.groupBy(WORD.ID)
				.asTable("wq");

		return create
				.select(wordsQuery.fields())
				.from(wordsQuery)
				.orderBy(wordsQuery.field("word"), wordsQuery.field("homonym_nr"))
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}

	public Result<Record8<Long,Long,String,String[],String,String,String,String>> findParadigmFormTuples(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						PARADIGM.ID.as("paradigm_id"),
						FORM.ID.as("form_id"),
						FORM.VALUE.as("form"),
						FORM.COMPONENTS,
						FORM.DISPLAY_FORM,
						FORM.VOCAL_FORM,
						FORM.MORPH_CODE,
						MORPH_LABEL.VALUE.as("morph_value")
						)
				.from(PARADIGM, FORM, MORPH_LABEL)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(MORPH_LABEL.CODE.eq(FORM.MORPH_CODE))
						.and(MORPH_LABEL.LANG.eq(classifierLabelLang))
						.and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.orderBy(PARADIGM.ID, FORM.ID)
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

	public Result<Record14<Long, String, Long, Long, String, String, Long, String, String, Long, String, String, String, String>>
		findRectionUsageTranslationDefinitionTuples(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform r = FREEFORM.as("r");
		Freeform um = FREEFORM.as("um");
		Freeform u = FREEFORM.as("u");
		Freeform ut = FREEFORM.as("ut");
		Freeform ud = FREEFORM.as("ud");
		Freeform ua = FREEFORM.as("ua");
		Freeform uat = FREEFORM.as("uat");
		UsageAuthorTypeLabel uatl = USAGE_AUTHOR_TYPE_LABEL.as("uatl");

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
						ud.LANG.as("usage_definition_lang"),
						ua.VALUE_TEXT.as("usage_author"),
						uatl.VALUE.as("usage_author_type")
						)
				.from(
						lff.innerJoin(r
								.leftOuterJoin(um).on(um.PARENT_ID.eq(r.ID).and(um.TYPE.eq(FreeformType.USAGE_MEANING.name())))
								.leftOuterJoin(u).on(u.PARENT_ID.eq(um.ID).and(u.TYPE.eq(FreeformType.USAGE.name())))
								.leftOuterJoin(ut).on(ut.PARENT_ID.eq(um.ID).and(ut.TYPE.eq(FreeformType.USAGE_TRANSLATION.name())))
								.leftOuterJoin(ud).on(ud.PARENT_ID.eq(um.ID).and(ud.TYPE.eq(FreeformType.USAGE_DEFINITION.name())))
								.leftOuterJoin(ua).on(ua.PARENT_ID.eq(u.ID).and(ua.TYPE.eq(FreeformType.USAGE_AUTHOR.name())))
								.leftOuterJoin(uat).on(uat.PARENT_ID.eq(u.ID).and(uat.TYPE.eq(FreeformType.USAGE_AUTHOR_TYPE.name())))
								).on(lff.FREEFORM_ID.eq(r.ID).and(r.TYPE.eq(FreeformType.RECTION.name())))
								.leftOuterJoin(uatl)
								.on(uatl.CODE.eq(uat.CLASSIF_CODE).and(uatl.LANG.eq(classifierLabelLang).and(uatl.TYPE.eq(classifierLabelTypeCode))))
						)
				.where(lff.LEXEME_ID.eq(lexemeId))
				.orderBy(r.ID, um.ID, u.ID, ut.ID, ud.ID)
				.fetch();
	}

	public Result<Record15<String[],String,Long,String,Long,Long,String,Integer,Integer,Integer,String,String,String,String,String>> findFormMeanings(
			Long wordId, List<String> selectedDatasets) {

		return 
				create
				.select(
						DSL.arrayAggDistinct(FORM.VALUE).as("words"),
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
						WORD.ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.isTrue())
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(MEANING.ID))
						.and(LEXEME.DATASET_CODE.in(selectedDatasets)))
				.groupBy(WORD.ID, LEXEME.ID, MEANING.ID)
				.orderBy(WORD.ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}

	public Result<Record4<Long,String,Integer,String>> findMeaningWords(Long sourceWordId, Long meaningId, List<String> datasets) {

		return create
				.select(
						WORD.ID.as("word_id"),
						FORM.VALUE.as("word"),
						WORD.HOMONYM_NR,
						WORD.LANG
						)
				.from(LEXEME, WORD, PARADIGM, FORM)
				.where(
						FORM.PARADIGM_ID.eq(PARADIGM.ID)
						.and(FORM.IS_WORD.isTrue())
						.and(WORD.ID.ne(sourceWordId))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(meaningId))
						.and(LEXEME.DATASET_CODE.in(datasets))
				)
				.groupBy(WORD.ID, FORM.VALUE)
				.orderBy(FORM.VALUE)
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
				.select(
						FREEFORM.ID,
						FREEFORM.TYPE,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_DATE)
				.from(FREEFORM, MEANING_FREEFORM)
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
						.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID)))
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

	public Result<Record7<Long,Long,String,Long,String,String,String>> findWordFormRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");

		return create
				.select(
						PARADIGM.ID.as("paradigm_id"),
						f1.ID.as("form1_id"),
						f1.VALUE.as("form1_value"),
						f2.ID.as("form2_id"),
						f2.VALUE.as("form2_value"),
						FORM_REL_TYPE_LABEL.CODE.as("rel_type_code"),
						FORM_REL_TYPE_LABEL.VALUE.as("rel_type_label")
						)
				.from(
						PARADIGM,
						f1,
						f2,
						FORM_RELATION.leftOuterJoin(FORM_REL_TYPE_LABEL).on(
							FORM_RELATION.FORM_REL_TYPE_CODE.eq(FORM_REL_TYPE_LABEL.CODE)
							.and(FORM_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
							.and(FORM_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode))))
						)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
						.and(f1.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM_RELATION.FORM1_ID.eq(f1.ID))
						.and(FORM_RELATION.FORM2_ID.eq(f2.ID))
						)
				.orderBy(PARADIGM.ID, f1.ID)
				.fetch();
	}
}
