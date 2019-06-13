package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ASPECT_LABEL;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREQUENCY_GROUP;
import static eki.ekilex.data.db.Tables.GENDER_LABEL;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGION;
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
import static eki.ekilex.data.db.Tables.REGION;
import static eki.ekilex.data.db.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.USAGE_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.DbConstant;
import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.UsageTypeLabel;

@Component
public class CommonDataDbService implements DbConstant, SystemConstant {

	@Autowired
	private DSLContext create;

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).where(DATASET.IS_VISIBLE.isTrue()).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	@Cacheable(value = CACHE_KEY_DATASET)
	public List<Dataset> getDatasets() {
		return create.select(DATASET.CODE, DATASET.NAME).from(DATASET).where(DATASET.IS_VISIBLE.isTrue()).orderBy(DATASET.ORDER_BY).fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getLanguages(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.LANGUAGE),
						LANGUAGE_LABEL.CODE,
						LANGUAGE_LABEL.VALUE)
				.from(LANGUAGE, LANGUAGE_LABEL)
				.where(
						LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)
								.and(LANGUAGE_LABEL.LANG.eq(classifierLabelLang))
								.and(LANGUAGE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.orderBy(LANGUAGE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Classifier> getFrequencyGroups() {
		return create
				.select(
						getClassifierNameField(ClassifierName.FREQUENCY_GROUP),
						FREQUENCY_GROUP.CODE,
						FREQUENCY_GROUP.CODE.as("value"))
				.from(FREQUENCY_GROUP)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getPoses(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						DSL.field(DSL.value(ClassifierName.POS.name())).as("name"),
						POS_LABEL.CODE,
						POS_LABEL.VALUE)
				.from(POS_LABEL)
				.where(POS_LABEL.LANG.eq(classifierLabelLang).and(POS_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getRegisters(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.REGISTER),
						REGISTER_LABEL.CODE,
						REGISTER_LABEL.VALUE)
				.from(REGISTER_LABEL)
				.where(REGISTER_LABEL.LANG.eq(classifierLabelLang).and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Classifier> getRegions() {
		return create
				.select(
						getClassifierNameField(ClassifierName.REGION),
						REGION.CODE,
						REGION.CODE.as("value"))
				.from(REGION)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getDerivs(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.DERIV),
						DERIV_LABEL.CODE,
						DERIV_LABEL.VALUE)
				.from(DERIV_LABEL)
				.where(DERIV_LABEL.LANG.eq(classifierLabelLang).and(DERIV_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getGenders(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.GENDER),
						GENDER_LABEL.CODE,
						GENDER_LABEL.VALUE)
				.from(GENDER_LABEL)
				.where(GENDER_LABEL.LANG.eq(classifierLabelLang).and(GENDER_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getAspects(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.ASPECT),
						ASPECT_LABEL.CODE,
						ASPECT_LABEL.VALUE)
				.from(ASPECT_LABEL)
				.where(ASPECT_LABEL.LANG.eq(classifierLabelLang).and(ASPECT_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Classifier> getDomains() {
		return create
				.select(
						getClassifierNameField(ClassifierName.DOMAIN),
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.orderBy(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.CODE)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Classifier> getDomainsInUse() {
		return create
				.select(
						getClassifierNameField(ClassifierName.DOMAIN),
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.whereExists(DSL
						.select(MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
						.from(MEANING_DOMAIN)
						.where(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN)
								.and(MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE))))
				.orderBy(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.CODE)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getWordTypes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.WORD_TYPE),
						WORD_TYPE_LABEL.CODE,
						WORD_TYPE_LABEL.VALUE)
				.from(WORD_TYPE_LABEL)
				.where(WORD_TYPE_LABEL.LANG.eq(classifierLabelLang).and(WORD_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getWordRelationTypes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.WORD_REL_TYPE),
						WORD_REL_TYPE_LABEL.CODE,
						WORD_REL_TYPE_LABEL.VALUE)
				.from(WORD_REL_TYPE_LABEL)
				.where(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getLexemeRelationTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.LEX_REL_TYPE),
						LEX_REL_TYPE_LABEL.CODE,
						LEX_REL_TYPE_LABEL.VALUE)
				.from(LEX_REL_TYPE_LABEL)
				.where(LEX_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(LEX_REL_TYPE_LABEL.TYPE.eq(classifierLabelType)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getMeaningRelationTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.MEANING_REL_TYPE),
						MEANING_REL_TYPE_LABEL.CODE,
						MEANING_REL_TYPE_LABEL.VALUE)
				.from(MEANING_REL_TYPE_LABEL)
				.where(MEANING_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifierLabelType)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getMorphs(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.MORPH),
						MORPH_LABEL.CODE,
						MORPH_LABEL.VALUE)
				.from(MORPH_LABEL)
				.where(MORPH_LABEL.LANG.eq(classifierLabelLang).and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getValueStates(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.VALUE_STATE),
						VALUE_STATE_LABEL.CODE,
						VALUE_STATE_LABEL.VALUE)
				.from(VALUE_STATE_LABEL)
				.where(VALUE_STATE_LABEL.LANG.eq(classifierLabelLang).and(VALUE_STATE_LABEL.TYPE.eq(classifierLabelType)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Classifier> getProcessStates() {
		return create
				.select(
						getClassifierNameField(ClassifierName.PROCESS_STATE),
						PROCESS_STATE.CODE,
						PROCESS_STATE.CODE.as("value"))
				.from(PROCESS_STATE)
				.orderBy(PROCESS_STATE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public Word getWord(Long wordId) {
		return create.select(
				WORD.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct form.value), ',', '*')").cast(String.class).as("word"),
				WORD.HOMONYM_NR,
				WORD.LANG,
				WORD.WORD_CLASS,
				WORD.GENDER_CODE,
				WORD.ASPECT_CODE)
				.from(WORD, PARADIGM, FORM)
				.where(WORD.ID.eq(wordId)
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name()))
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME)
								.where(
										LEXEME.WORD_ID.eq(WORD.ID))))
				.groupBy(WORD.ID)
				.fetchOneInto(Word.class);
	}

	public List<Classifier> getWordTypes(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						getClassifierNameField(ClassifierName.WORD_TYPE),
						WORD_TYPE_LABEL.CODE,
						WORD_TYPE_LABEL.VALUE)
				.from(WORD_WORD_TYPE, WORD_TYPE_LABEL)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordId)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_LABEL.CODE))
								.and(WORD_TYPE_LABEL.LANG.eq(classifierLabelLang))
								.and(WORD_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	public List<FreeForm> getMeaningFreeforms(Long meaningId, String... excludeTypes) {
		return create
				.select(
						FREEFORM.ID,
						FREEFORM.TYPE,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.VALUE_DATE)
				.from(FREEFORM, MEANING_FREEFORM)
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
								.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.notIn(excludeTypes)))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<FreeForm> getMeaningLearnerComments(Long meaningId) {
		return create
				.select(
						FREEFORM.ID,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE)
				.from(FREEFORM, MEANING_FREEFORM)
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
								.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.LEARNER_COMMENT.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<NoteSourceTuple> getMeaningNoteSourceTuples(FreeformType freeformType, Long meaningId) {

		return create
				.select(
						FREEFORM.ID.as("freeform_id"),
						FREEFORM.VALUE_TEXT.as("freeform_value_text"),
						FREEFORM.VALUE_PRESE.as("freeform_value_prese"),
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"))
				.from(MEANING_FREEFORM, FREEFORM.leftOuterJoin(FREEFORM_SOURCE_LINK)
						.on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(FREEFORM.ID)))
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
								.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(freeformType.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(NoteSourceTuple.class);
	}

	public List<ImageSourceTuple> getMeaningImageSourceTuples(Long meaningId) {

		Freeform iff = FREEFORM.as("iff");
		Freeform tff = FREEFORM.as("tff");

		return create
				.select(
						iff.ID.as("image_freeform_id"),
						iff.VALUE_TEXT.as("image_freeform_value_text"),
						tff.VALUE_TEXT.as("title_freeform_value_text"),
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"))
				.from(
						MEANING_FREEFORM,
						iff
								.leftOuterJoin(tff).on(tff.PARENT_ID.eq(iff.ID).and(tff.TYPE.eq(FreeformType.IMAGE_TITLE.name())))
								.leftOuterJoin(FREEFORM_SOURCE_LINK).on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(iff.ID)))
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
								.and(iff.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
								.and(iff.TYPE.eq(FreeformType.IMAGE_FILE.name())))
				.orderBy(iff.ORDER_BY)
				.fetchInto(ImageSourceTuple.class);
	}

	public List<Classifier> getMeaningDomains(Long meaningId) {

		return create
				.select(
						getClassifierNameField(ClassifierName.DOMAIN),
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.VALUE)
				.from(
						MEANING_DOMAIN.leftOuterJoin(DOMAIN_LABEL).on(
								MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE)
										.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN))))
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId))
				.fetchInto(Classifier.class);
	}

	public List<DefinitionRefTuple> getMeaningDefinitionRefTuples(Long meaningId) {

		return create
				.select(
						DEFINITION.ID.as("definition_id"),
						DEFINITION.VALUE_PRESE.as("definition_value"),
						DEFINITION.LANG.as("definition_lang"),
						DEFINITION.ORDER_BY.as("definition_order_by"),
						DEFINITION.DEFINITION_TYPE_CODE.as("definition_type_code"),
						DEFINITION_SOURCE_LINK.ID.as("source_link_id"),
						DEFINITION_SOURCE_LINK.TYPE.as("source_link_type"),
						DEFINITION_SOURCE_LINK.NAME.as("source_link_name"),
						DEFINITION_SOURCE_LINK.VALUE.as("source_link_value"))
				.from(DEFINITION.leftOuterJoin(DEFINITION_SOURCE_LINK)
						.on(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(DEFINITION.ID)))
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.orderBy(DEFINITION.ORDER_BY)
				.fetchInto(DefinitionRefTuple.class);
	}

	public List<Relation> getMeaningRelations(Long meaningId, String classifierLabelLang, String classifierLabelTypeCode) {

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
						MEANING_RELATION.ORDER_BY.as("order_by"))
				.from(
						MEANING_RELATION.leftOuterJoin(MEANING_REL_TYPE_LABEL).on(
								MEANING_RELATION.MEANING_REL_TYPE_CODE.eq(MEANING_REL_TYPE_LABEL.CODE)
										.and(MEANING_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						MEANING,
						LEXEME,
						WORD,
						PARADIGM,
						FORM)
				.where(
						MEANING_RELATION.MEANING1_ID.eq(meaningId)
								.and(MEANING_RELATION.MEANING2_ID.eq(MEANING.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.orderBy(MEANING_RELATION.ORDER_BY)
				.fetchInto(Relation.class);
	}

	public List<FreeForm> getLexemeFreeforms(Long lexemeId, String... excludedTypes) {
		return create
				.select(FREEFORM.ID, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE, FREEFORM.VALUE_DATE)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
								.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.notIn(excludedTypes)))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<FreeForm> getLexemePublicNotes(Long lexemeId) {
		return create
				.select(FREEFORM.ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
								.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.PUBLIC_NOTE.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<SourceLink> getLexemeSourceLinks(Long lexemeId) {
		return create
				.select(
						LEXEME_SOURCE_LINK.ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.NAME,
						LEXEME_SOURCE_LINK.VALUE)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_SOURCE_LINK.ORDER_BY)
				.fetchInto(SourceLink.class);
	}

	public List<FreeForm> getLexemeGrammars(Long lexemeId) {
		return create
				.select(FREEFORM.ID, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
						.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.eq(FreeformType.GRAMMAR.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<Government> getLexemeGovernments(Long lexemeId) {

		LexemeFreeform glff = LEXEME_FREEFORM.as("glff");
		Freeform g = FREEFORM.as("g");
		Freeform gt = FREEFORM.as("gt");

		return create
				.select(
						g.ID,
						g.VALUE_TEXT.as("value"),
						gt.CLASSIF_CODE.as("type_code"))
				.from(
						glff.innerJoin(g).on(glff.FREEFORM_ID.eq(g.ID).and(g.TYPE.eq(FreeformType.GOVERNMENT.name())))
								.leftOuterJoin(gt).on(gt.PARENT_ID.eq(g.ID).and(gt.TYPE.eq(FreeformType.GOVERNMENT_TYPE.name()))))
				.where(glff.LEXEME_ID.eq(lexemeId))
				.orderBy(g.ORDER_BY)
				.fetchInto(Government.class);
	}

	public List<UsageTranslationDefinitionTuple> getLexemeUsageTranslationDefinitionTuples(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

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

		Table<Record3<Long, String, String>> srcn = DSL
				.select(
						src.ID,
						src.TYPE,
						DSL.field("array_to_string(array_agg(distinct pname.value_text), ',', '*')").cast(String.class).as("src_name"))
				.from(src, srcff, pname)
				.where(
						src.TYPE.eq(SourceType.PERSON.name())
								.and(srcff.SOURCE_ID.eq(src.ID))
								.and(srcff.FREEFORM_ID.eq(pname.ID))
								.and(pname.TYPE.eq(FreeformType.SOURCE_NAME.name())))
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
						srcn.field("src_name").cast(String.class).as("usage_source_name"))
				.from(
						ulff.innerJoin(u).on(ulff.FREEFORM_ID.eq(u.ID).and(u.TYPE.eq(FreeformType.USAGE.name())))
								.leftOuterJoin(ut).on(ut.PARENT_ID.eq(u.ID).and(ut.TYPE.eq(FreeformType.USAGE_TRANSLATION.name())))
								.leftOuterJoin(ud).on(ud.PARENT_ID.eq(u.ID).and(ud.TYPE.eq(FreeformType.USAGE_DEFINITION.name())))
								.leftOuterJoin(srcl).on(srcl.FREEFORM_ID.eq(u.ID))
								.leftOuterJoin(srcn).on(srcn.field("id").cast(Long.class).eq(srcl.SOURCE_ID))
								.leftOuterJoin(utype).on(utype.PARENT_ID.eq(u.ID).and(utype.TYPE.eq(FreeformType.USAGE_TYPE.name())))
								.leftOuterJoin(utypelbl).on(utypelbl.CODE.eq(utype.CLASSIF_CODE).and(utypelbl.LANG.eq(classifierLabelLang).and(utypelbl.TYPE.eq(classifierLabelTypeCode)))))
				.where(ulff.LEXEME_ID.eq(lexemeId))
				.orderBy(u.ORDER_BY, ut.ORDER_BY, ud.ORDER_BY, srcl.ORDER_BY)
				.fetchInto(UsageTranslationDefinitionTuple.class);
	}

	public List<NoteSourceTuple> getLexemePublicNoteSourceTuples(Long lexemeId) {

		return create
				.select(
						FREEFORM.ID.as("freeform_id"),
						FREEFORM.VALUE_TEXT.as("freeform_value_text"),
						FREEFORM.VALUE_PRESE.as("freeform_value_prese"),
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"))
				.from(LEXEME_FREEFORM, FREEFORM.leftOuterJoin(FREEFORM_SOURCE_LINK)
						.on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(FREEFORM.ID)))
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
								.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.PUBLIC_NOTE.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(NoteSourceTuple.class);
	}

	public List<Classifier> getLexemePos(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						getClassifierNameField(ClassifierName.POS),
						POS_LABEL.CODE,
						POS_LABEL.VALUE)
				.from(LEXEME_POS, POS_LABEL)
				.where(
						LEXEME_POS.LEXEME_ID.eq(lexemeId)
								.and(POS_LABEL.CODE.eq(LEXEME_POS.POS_CODE))
								.and(POS_LABEL.LANG.eq(classifierLabelLang))
								.and(POS_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getLexemeDerivs(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						getClassifierNameField(ClassifierName.DERIV),
						DERIV_LABEL.CODE,
						DERIV_LABEL.VALUE)
				.from(LEXEME_DERIV, DERIV_LABEL)
				.where(
						LEXEME_DERIV.LEXEME_ID.eq(lexemeId)
								.and(DERIV_LABEL.CODE.eq(LEXEME_DERIV.DERIV_CODE))
								.and(DERIV_LABEL.LANG.eq(classifierLabelLang))
								.and(DERIV_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getLexemeRegisters(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						getClassifierNameField(ClassifierName.REGISTER),
						REGISTER_LABEL.CODE,
						REGISTER_LABEL.VALUE)
				.from(LEXEME_REGISTER, REGISTER_LABEL)
				.where(
						LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)
								.and(REGISTER_LABEL.CODE.eq(LEXEME_REGISTER.REGISTER_CODE))
								.and(REGISTER_LABEL.LANG.eq(classifierLabelLang))
								.and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getLexemeRegions(Long lexemeId) {

		return create
				.select(
						getClassifierNameField(ClassifierName.REGION),
						REGION.CODE,
						REGION.CODE.as("value"))
				.from(LEXEME_REGION, REGION)
				.where(
						LEXEME_REGION.LEXEME_ID.eq(lexemeId)
								.and(REGION.CODE.eq(LEXEME_REGION.REGION_CODE)))
				.orderBy(REGION.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public List<Relation> getLexemeRelations(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						LEX_RELATION.ID.as("id"),
						LEXEME.ID.as("lexeme_id"),
						WORD.ID.as("word_id"),
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						LEX_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						LEX_RELATION.ORDER_BY.as("order_by"),
						LEXEME.MEANING_ID.as("meaning_id"))
				.from(
						LEX_RELATION.leftOuterJoin(LEX_REL_TYPE_LABEL).on(
								LEX_RELATION.LEX_REL_TYPE_CODE.eq(LEX_REL_TYPE_LABEL.CODE)
										.and(LEX_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(LEX_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						LEXEME,
						WORD,
						PARADIGM,
						FORM)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId)
								.and(LEX_RELATION.LEXEME2_ID.eq(LEXEME.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.groupBy(LEX_RELATION.ID, LEXEME.ID, WORD.ID, FORM.VALUE, LEX_REL_TYPE_LABEL.VALUE)
				.orderBy(LEX_RELATION.ORDER_BY)
				.fetchInto(Relation.class);
	}

	private Field<String> getClassifierNameField(ClassifierName classifierName) {
		return DSL.field(DSL.value(classifierName.name())).as("name");
	}

	public void addDatasetCodeToClassifier(ClassifierName classifierName, String classifierCode, String datasetCode, String origin) {

		//TODO - study array_add possibility in jooq
		Classifier classifier = getClassifierWithDatasets(classifierName, classifierCode, origin);

		String[] classiferDatasets = classifier.getDatasets();
		classiferDatasets = ArrayUtils.add(classiferDatasets, datasetCode);

		updateClassiferDatasets(classifierName, classifierCode, classiferDatasets);
	}

	public void removeDatasetCodeFromClassifier(ClassifierName classifierName, String classifierCode, String datasetCode, String origin) {

		//TODO study array_remove option
		Classifier classifier = getClassifierWithDatasets(classifierName, classifierCode, origin);
		String[] classifierDatasetCodes = classifier.getDatasets();
		classifierDatasetCodes = ArrayUtils.removeElement(classifierDatasetCodes, datasetCode);

		updateClassiferDatasets(classifierName, classifierCode, classifierDatasetCodes);
	}

	private void updateClassiferDatasets(ClassifierName classifierName, String code, String[] datasets) {

		if (ClassifierName.LANGUAGE.equals(classifierName)) {

			create
					.update(LANGUAGE)
					.set(LANGUAGE.DATASETS, datasets)
					.where(LANGUAGE.CODE.eq(code))
					.execute();
		} else if (ClassifierName.DOMAIN.equals(classifierName)) {

			create
					.update(DOMAIN)
					.set(DOMAIN.DATASETS, datasets)
					.where(DOMAIN.CODE.eq(code))
					.execute();
		} else if (ClassifierName.PROCESS_STATE.equals(classifierName)) {

			create
					.update(PROCESS_STATE)
					.set(PROCESS_STATE.DATASETS, datasets)
					.where(PROCESS_STATE.CODE.eq(code))
					.execute();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private Classifier getClassifierWithDatasets(ClassifierName name, String classifierCode, String origin) {

		if (ClassifierName.LANGUAGE.equals(name)) {

			return create
					.select(getClassifierNameField(ClassifierName.LANGUAGE), LANGUAGE.CODE, LANGUAGE.DATASETS, LANGUAGE.ORDER_BY)
					.from(LANGUAGE)
					.where(LANGUAGE.CODE.eq(classifierCode))
					.fetchSingleInto(Classifier.class);
		} else if (ClassifierName.DOMAIN.equals(name)) {

			return create
					.select(getClassifierNameField(ClassifierName.DOMAIN), DOMAIN.CODE, DOMAIN.ORIGIN, DOMAIN.DATASETS, DOMAIN.ORDER_BY)
					.from(DOMAIN)
					.where(DOMAIN.CODE.eq(classifierCode))
					.and(DOMAIN.ORIGIN.eq(origin))
					.fetchSingleInto(Classifier.class);
		} else if (ClassifierName.PROCESS_STATE.equals(name)) {

			return create
					.select(getClassifierNameField(ClassifierName.PROCESS_STATE), PROCESS_STATE.CODE, PROCESS_STATE.DATASETS, PROCESS_STATE.ORDER_BY)
					.from(PROCESS_STATE)
					.where(PROCESS_STATE.CODE.eq(classifierCode))
					.fetchSingleInto(Classifier.class);
		}

		throw new UnsupportedOperationException();

	}

	public List<Classifier> getDatasetClassifiers(ClassifierName classifierName, String datasetCode, String labelLanguage, String labelType) {
		String[] datasetCodes = {datasetCode};
		if (ClassifierName.LANGUAGE.equals(classifierName)) {
			return create.select(getClassifierNameField(ClassifierName.LANGUAGE), LANGUAGE.CODE, LANGUAGE_LABEL.VALUE, LANGUAGE.ORDER_BY)
				.from(LANGUAGE, LANGUAGE_LABEL)
				.where(
					LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)
						.and(LANGUAGE_LABEL.TYPE.eq(labelType))
						.and(LANGUAGE_LABEL.LANG.eq(labelLanguage))
						.and(LANGUAGE.DATASETS.contains(datasetCodes)))
				.fetchInto(Classifier.class);
		} else if (ClassifierName.PROCESS_STATE.equals(classifierName)) {
			return create.select(getClassifierNameField(ClassifierName.PROCESS_STATE), PROCESS_STATE.CODE, PROCESS_STATE.CODE.as("value"),
				PROCESS_STATE.ORDER_BY.as("order_by"))
					.from(PROCESS_STATE)
					.where(PROCESS_STATE.DATASETS.contains(datasetCodes))
					.fetchInto(Classifier.class);
		} else if (ClassifierName.DOMAIN.equals(classifierName)) {
			return create.select(getClassifierNameField(ClassifierName.DOMAIN), DOMAIN.CODE,
				DOMAIN_LABEL.VALUE, DOMAIN.ORIGIN, DOMAIN.ORDER_BY)
					.from(DOMAIN, DOMAIN_LABEL)
					.where(
						DOMAIN.CODE.eq(DOMAIN_LABEL.CODE)
							.and(DOMAIN.ORIGIN.eq(DOMAIN_LABEL.ORIGIN))
							.and(DOMAIN_LABEL.LANG.eq(labelLanguage))
							.and(DOMAIN_LABEL.TYPE.eq(labelType)).and(
						DOMAIN.DATASETS.contains(datasetCodes)))
					.fetchInto(Classifier.class);
		}

		throw new UnsupportedOperationException();
	}

	public List<Classifier> findDomainsByValue(String searchValue) {
		return create
				.select(
						getClassifierNameField(ClassifierName.DOMAIN),
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.whereExists(DSL
						.select(MEANING_DOMAIN.DOMAIN_ORIGIN, MEANING_DOMAIN.DOMAIN_CODE)
						.from(MEANING_DOMAIN)
						.where(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN)
								.and(MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE))))
				.and(DOMAIN_LABEL.VALUE.containsIgnoreCase(searchValue))
				.orderBy(DOMAIN_LABEL.VALUE, DOMAIN_LABEL.ORIGIN)
				.fetchInto(Classifier.class);
	}

}
