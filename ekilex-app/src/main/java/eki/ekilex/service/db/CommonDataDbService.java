package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ASPECT_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.GENDER_LABEL;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_FREQUENCY;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
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
import static eki.ekilex.data.db.Tables.PROCESS_STATE;
import static eki.ekilex.data.db.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.USAGE_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_TYPE_LABEL;

import java.sql.Timestamp;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record18;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.constant.DbConstant;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.UsageTypeLabel;

@Component
public class CommonDataDbService implements DbConstant {

	@Autowired
	private DSLContext create;

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).where(DATASET.IS_PUBLIC.isTrue()).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public Result<Record2<String, String>> getDatasets() {
		return create.select(DATASET.CODE, DATASET.NAME).from(DATASET).where(DATASET.IS_PUBLIC.isTrue()).orderBy(DATASET.ORDER_BY).fetch();
	}

	public Result<Record2<String, String>> getLanguages(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(LANGUAGE_LABEL.CODE, LANGUAGE_LABEL.VALUE)
				.from(LANGUAGE, LANGUAGE_LABEL)
				.where(
						LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)
						.and(LANGUAGE_LABEL.LANG.eq(classifierLabelLang))
						.and(LANGUAGE_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.orderBy(LANGUAGE.ORDER_BY)
				.fetch();
	}

	public Result<Record2<String, String>> getLexemeFrequencyGroups() {
		return create.select(LEXEME_FREQUENCY.CODE, LEXEME_FREQUENCY.CODE.as("value")).from(LEXEME_FREQUENCY).fetch();
	}

	public Result<Record2<String, String>> getAllLexemePos(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(POS_LABEL.CODE, POS_LABEL.VALUE)
				.from(POS_LABEL)
				.where(POS_LABEL.LANG.eq(classifierLabelLang).and(POS_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record2<String, String>> getLexemeRegisters(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(REGISTER_LABEL.CODE, REGISTER_LABEL.VALUE)
				.from(REGISTER_LABEL)
				.where(REGISTER_LABEL.LANG.eq(classifierLabelLang).and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record2<String, String>> getLexemeDerivs(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(DERIV_LABEL.CODE, DERIV_LABEL.VALUE)
				.from(DERIV_LABEL)
				.where(DERIV_LABEL.LANG.eq(classifierLabelLang).and(DERIV_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record2<String, String>> getWordGenders(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(GENDER_LABEL.CODE, GENDER_LABEL.VALUE)
				.from(GENDER_LABEL)
				.where(GENDER_LABEL.LANG.eq(classifierLabelLang).and(GENDER_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record2<String, String>> getWordTypes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(WORD_TYPE_LABEL.CODE, WORD_TYPE_LABEL.VALUE)
				.from(WORD_TYPE_LABEL)
				.where(WORD_TYPE_LABEL.LANG.eq(classifierLabelLang).and(WORD_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record2<String, String>> getWordAspects(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(ASPECT_TYPE_LABEL.CODE, ASPECT_TYPE_LABEL.VALUE)
				.from(ASPECT_TYPE_LABEL)
				.where(ASPECT_TYPE_LABEL.LANG.eq(classifierLabelLang).and(ASPECT_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record2<String, String>> getWordRelationTypes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(WORD_REL_TYPE_LABEL.CODE, WORD_REL_TYPE_LABEL.VALUE)
				.from(WORD_REL_TYPE_LABEL)
				.where(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Result<Record3<String, String, String>> getDomains() {
		return create
				.select(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.CODE, DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.orderBy(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.VALUE)
				.fetch();
	}

	public Result<Record3<String, String, String>> getDomainsInUse() {
		return create
				.select(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.CODE, DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.whereExists(DSL
						.select(MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
						.from(MEANING_DOMAIN)
						.where(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN)
								.and(MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE))))
				.orderBy(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.VALUE)
				.fetch();
	}

	public Result<Record4<Long, String, String, Timestamp>> findLexemeFreeforms(Long lexemeId) {
		return create
				.select(FREEFORM.ID, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_DATE)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId).and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.notIn(FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetch();
	}

	public Result<Record2<Long, String>> findLexemeGrammars(Long lexemeId) {
		return create
				.select(FREEFORM.ID, FREEFORM.VALUE_TEXT)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
						.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.eq(FreeformType.GRAMMAR.name()))
						.and(FREEFORM.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.orderBy(FREEFORM.ORDER_BY)
				.fetch();
	}

	public Result<Record4<Long,String,String,String>> findLexemeSourceLinks(Long lexemeId) {
		return create
				.select(
						LEXEME_SOURCE_LINK.ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.NAME,
						LEXEME_SOURCE_LINK.VALUE
				)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(lexemeId).and(LEXEME_SOURCE_LINK.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.orderBy(LEXEME_SOURCE_LINK.ORDER_BY)
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
				.orderBy(FREEFORM.ORDER_BY)
				.fetch();
	}

	public Result<Record8<Long,String,String,Long,Long,String,String,String>> findMeaningDefinitionRefTuples(Long meaningId) {

		return create
				.select(
						DEFINITION.ID.as("definition_id"),
						DEFINITION.VALUE_PRESE.as("definition_value"),
						DEFINITION.LANG.as("definition_lang"),
						DEFINITION.ORDER_BY.as("definition_order_by"),
						DEFINITION_SOURCE_LINK.ID.as("source_link_id"),
						DEFINITION_SOURCE_LINK.TYPE.as("source_link_type"),
						DEFINITION_SOURCE_LINK.NAME.as("source_link_name"),
						DEFINITION_SOURCE_LINK.VALUE.as("source_link_value")
						)
				.from(DEFINITION.leftOuterJoin(DEFINITION_SOURCE_LINK)
						.on(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(DEFINITION.ID))
						.and(DEFINITION_SOURCE_LINK.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.where(DEFINITION.MEANING_ID.eq(meaningId).and(DEFINITION.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.orderBy(DEFINITION.ORDER_BY)
				.fetch();
	}

	public Result<Record3<Long,String,String>> findGovernments(Long lexemeId) {

		LexemeFreeform glff = LEXEME_FREEFORM.as("glff");
		Freeform g = FREEFORM.as("g");
		Freeform gt = FREEFORM.as("gt");

		return create
				.select(
						g.ID,
						g.VALUE_TEXT.as("value"),
						gt.CLASSIF_CODE.as("type_code")
						)
				.from(
						glff.innerJoin(g).on(glff.FREEFORM_ID.eq(g.ID).and(g.TYPE.eq(FreeformType.GOVERNMENT.name())).and(g.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
						.leftOuterJoin(gt).on(gt.PARENT_ID.eq(g.ID).and(gt.TYPE.eq(FreeformType.GOVERNMENT_TYPE.name())))
						)
				.where(glff.LEXEME_ID.eq(lexemeId))
				.orderBy(g.ORDER_BY)
				.fetch();
	}

	public Result<Record18<Long,String,String,String,String,Long,String,String,Long,String,String,Long,String,String,String,Long,String,String>>
				findUsageTranslationDefinitionTuples(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		LexemeFreeform ulff = LEXEME_FREEFORM.as("ulff");
		Freeform u = FREEFORM.as("u");
		Freeform ut = FREEFORM.as("ut");
		Freeform ud = FREEFORM.as("ud");
		Freeform utype = FREEFORM.as("utype");
		Freeform pname = FREEFORM.as("pname");
		Source src = SOURCE.as("src");
		SourceFreeform srcff = SOURCE_FREEFORM.as("srcff");
		UsageTypeLabel utypelbl = USAGE_TYPE_LABEL.as("utypelbl");
		FreeformSourceLink srcl = FREEFORM_SOURCE_LINK.as("uauthl");
	
		Table<Record3<Long,String,String>> srcn = DSL
			.select(
					src.ID,
					src.TYPE,
					DSL.field("array_to_string(array_agg(distinct pname.value_text), ',', '*')").cast(String.class).as("src_name")
					)
			.from(src, srcff, pname)
			.where(
					src.TYPE.eq(SourceType.PERSON.name())
					.and(src.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
					.and(srcff.SOURCE_ID.eq(src.ID))
					.and(srcff.FREEFORM_ID.eq(pname.ID))
					.and(pname.TYPE.eq(FreeformType.SOURCE_NAME.name()))
					.and(pname.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
					)
			.groupBy(src.ID)
			.asTable("srcn");

	return create
			.select(
					u.ID.as("usage_id"),
					u.VALUE_PRESE.as("usage_value"),
					u.LANG.as("usage_lang"),
					utype.CLASSIF_CODE.as("usage_type_code"),
					utypelbl.VALUE.as("usage_type_value"),
					ut.ID.as("usage_translation_id"),
					ut.VALUE_PRESE.as("usage_translation_value"),
					ut.LANG.as("usage_translation_lang"),
					ud.ID.as("usage_definition_id"),
					ud.VALUE_PRESE.as("usage_definition_value"),
					ud.LANG.as("usage_definition_lang"),
					srcl.ID.as("usage_source_link_id"),
					srcl.TYPE.as("usage_source_link_type"),
					srcl.NAME.as("usage_source_link_name"),
					srcl.VALUE.as("usage_source_link_value"),
					srcn.field("id").cast(Long.class).as("usage_source_id"),
					srcn.field("type").cast(String.class).as("usage_source_type"),
					srcn.field("src_name").cast(String.class).as("usage_source_name")
					)
			.from(
					ulff.innerJoin(u).on(ulff.FREEFORM_ID.eq(u.ID).and(u.TYPE.eq(FreeformType.USAGE.name())).and(u.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
					.leftOuterJoin(ut).on(ut.PARENT_ID.eq(u.ID).and(ut.TYPE.eq(FreeformType.USAGE_TRANSLATION.name())).and(ut.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
					.leftOuterJoin(ud).on(ud.PARENT_ID.eq(u.ID).and(ud.TYPE.eq(FreeformType.USAGE_DEFINITION.name())).and(ud.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
					.leftOuterJoin(srcl).on(srcl.FREEFORM_ID.eq(u.ID).and(srcl.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
					.leftOuterJoin(srcn).on(srcn.field("id").cast(Long.class).eq(srcl.SOURCE_ID))
					.leftOuterJoin(utype).on(utype.PARENT_ID.eq(u.ID).and(utype.TYPE.eq(FreeformType.USAGE_TYPE.name())).and(utype.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
					.leftOuterJoin(utypelbl).on(utypelbl.CODE.eq(utype.CLASSIF_CODE).and(utypelbl.LANG.eq(classifierLabelLang).and(utypelbl.TYPE.eq(classifierLabelTypeCode))))
					)
			.where(ulff.LEXEME_ID.eq(lexemeId))
			.orderBy(u.ORDER_BY, ut.ORDER_BY, ud.ORDER_BY, srcl.ORDER_BY)
			.fetch();
	}

	public Result<Record9<Long,Long,Long,Long,Long,String,String,String,Long>> findMeaningRelations(Long meaningId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						MEANING_RELATION.ID.as("id"),
						MEANING.ID.as("meaning_id"),
						LEXEME.ID.as("lexeme_id"),
						WORD.ID.as("word_id"),
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						MEANING_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						MEANING_RELATION.ORDER_BY.as("order_by")
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
								.and(MEANING_RELATION.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
								.and(MEANING_RELATION.MEANING2_ID.eq(MEANING.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
				)
				.orderBy(MEANING_RELATION.ORDER_BY)
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
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId).and(MEANING_DOMAIN.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED)))
				.fetch();
	}

	public Result<Record2<String, String>> findLexemePos(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(POS_LABEL.CODE, POS_LABEL.VALUE)
				.from(LEXEME_POS, POS_LABEL)
				.where(
						LEXEME_POS.LEXEME_ID.eq(lexemeId)
						.and(LEXEME_POS.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
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
						.and(LEXEME_DERIV.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
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
						.and(LEXEME_REGISTER.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
						.and(REGISTER_LABEL.CODE.eq(LEXEME_REGISTER.REGISTER_CODE))
						.and(REGISTER_LABEL.LANG.eq(classifierLabelLang))
						.and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.fetch();
	}

	public Result<Record2<String, String>> getWordMorphCodes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(MORPH_LABEL.CODE, MORPH_LABEL.VALUE)
				.from(MORPH_LABEL)
				.where(MORPH_LABEL.LANG.eq(classifierLabelLang).and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetch();
	}

	public Record4<Long, String, Integer, String> getWord(Long wordId) {
		return create.select(PARADIGM.WORD_ID, FORM.VALUE.as("word"), WORD.HOMONYM_NR, WORD.LANG).from(PARADIGM, FORM, WORD)
				.where(PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name()))
						.and(WORD.ID.eq(wordId)))
				.fetchOne();
	}

	public Result<Record2<String, String>> getLexemeRelationTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(LEX_REL_TYPE_LABEL.CODE, LEX_REL_TYPE_LABEL.VALUE)
				.from(LEX_REL_TYPE_LABEL)
				.where(LEX_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(LEX_REL_TYPE_LABEL.TYPE.eq(classifierLabelType)))
				.fetch();
	}

	public Result<Record2<String, String>> getMeaningRelationTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(MEANING_REL_TYPE_LABEL.CODE, MEANING_REL_TYPE_LABEL.VALUE)
				.from(MEANING_REL_TYPE_LABEL)
				.where(MEANING_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifierLabelType)))
				.fetch();
	}

	public Result<Record2<String, String>> getLexemeValueStates(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(VALUE_STATE_LABEL.CODE, VALUE_STATE_LABEL.VALUE)
				.from(VALUE_STATE_LABEL)
				.where(VALUE_STATE_LABEL.LANG.eq(classifierLabelLang).and(VALUE_STATE_LABEL.TYPE.eq(classifierLabelType)))
				.fetch();
	}

	public Result<Record2<String, String>> getProcessStates() {
		return create
				.select(PROCESS_STATE.CODE, PROCESS_STATE.CODE.as("value"))
				.from(PROCESS_STATE)
				.orderBy(PROCESS_STATE.ORDER_BY)
				.fetch();
	}

	public Result<Record9<Long,Long,Long,Long,String,String,String,Long,Long>> findLexemeRelations(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						LEX_RELATION.ID.as("id"),
						LEXEME.ID.as("lexeme_id"),
						WORD.ID.as("word_id"),
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						LEX_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						LEX_RELATION.ORDER_BY.as("order_by"),
						LEXEME.MEANING_ID.as("meaning_id")
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
								.and(LEX_RELATION.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
								.and(LEX_RELATION.LEXEME2_ID.eq(LEXEME.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
				)
				.orderBy(LEX_RELATION.ORDER_BY)
				.fetch();
	}

}
