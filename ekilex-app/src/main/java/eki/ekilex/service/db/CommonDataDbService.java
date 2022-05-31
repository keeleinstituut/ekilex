package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ASPECT;
import static eki.ekilex.data.db.Tables.ASPECT_LABEL;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.DEFINITION_TYPE;
import static eki.ekilex.data.db.Tables.DEFINITION_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.DERIV;
import static eki.ekilex.data.db.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.Tables.DISPLAY_MORPH;
import static eki.ekilex.data.db.Tables.DISPLAY_MORPH_LABEL;
import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.Tables.ETYMOLOGY_TYPE;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.GENDER;
import static eki.ekilex.data.db.Tables.GENDER_LABEL;
import static eki.ekilex.data.db.Tables.GOVERNMENT_TYPE;
import static eki.ekilex.data.db.Tables.GOVERNMENT_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.LEX_REL_TYPE;
import static eki.ekilex.data.db.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_REL_TYPE;
import static eki.ekilex.data.db.Tables.MEANING_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.Tables.MEANING_TAG;
import static eki.ekilex.data.db.Tables.MORPH;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.POS;
import static eki.ekilex.data.db.Tables.POS_GROUP;
import static eki.ekilex.data.db.Tables.POS_GROUP_LABEL;
import static eki.ekilex.data.db.Tables.POS_LABEL;
import static eki.ekilex.data.db.Tables.PROFICIENCY_LEVEL;
import static eki.ekilex.data.db.Tables.PROFICIENCY_LEVEL_LABEL;
import static eki.ekilex.data.db.Tables.REGION;
import static eki.ekilex.data.db.Tables.REGISTER;
import static eki.ekilex.data.db.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.Tables.SEMANTIC_TYPE;
import static eki.ekilex.data.db.Tables.SEMANTIC_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.TAG;
import static eki.ekilex.data.db.Tables.USAGE_TYPE;
import static eki.ekilex.data.db.Tables.USAGE_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.VALUE_STATE;
import static eki.ekilex.data.db.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_TYPE;
import static eki.ekilex.data.db.Tables.WORD_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record11;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceOwner;
import eki.common.constant.SourceType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Media;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Origin;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.db.tables.Domain;
import eki.ekilex.data.db.tables.DomainLabel;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.LexRelTypeLabel;
import eki.ekilex.data.db.tables.LexRelation;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeRegister;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningRelTypeLabel;
import eki.ekilex.data.db.tables.MeaningRelation;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.UsageTypeLabel;
import eki.ekilex.data.db.tables.Word;

//only common use data reading!
@Component
public class CommonDataDbService extends AbstractDataDbService {

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public List<Dataset> getAllDatasets() {
		return create.selectFrom(DATASET).orderBy(DATASET.NAME).fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_DATASET)
	public List<Dataset> getVisibleDatasets() {
		return create.select(DATASET.CODE, DATASET.NAME).from(DATASET).where(DATASET.IS_VISIBLE.isTrue()).orderBy(DATASET.NAME).fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_TAG, key = "#root.methodName")
	public List<String> getTags() {
		return create.select(TAG.NAME).from(TAG).orderBy(TAG.ORDER_BY).fetchInto(String.class);
	}
	@Cacheable(value = CACHE_KEY_TAG, key = "{#root.methodName, #tagType}")
	public List<String> getTags(String tagType) {
		return create.select(TAG.NAME).from(TAG).where(TAG.TYPE.eq(tagType)).orderBy(TAG.ORDER_BY).fetchInto(String.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getLanguages(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.LANGUAGE),
						LANGUAGE_LABEL.CODE,
						LANGUAGE_LABEL.VALUE)
				.from(LANGUAGE.leftJoin(LANGUAGE_LABEL).on(LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)))
				.where(LANGUAGE_LABEL.LANG.eq(classifierLabelLang).and(LANGUAGE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.orderBy(LANGUAGE.ORDER_BY).fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getPoses(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						DSL.field(DSL.value(ClassifierName.POS.name())).as("name"),
						POS_LABEL.CODE,
						POS_LABEL.VALUE)
				.from(POS_LABEL, POS)
				.where(POS_LABEL.LANG.eq(classifierLabelLang).and(POS_LABEL.TYPE.eq(classifierLabelTypeCode)).and(POS_LABEL.CODE.eq(POS.CODE)))
				.orderBy(POS.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getRegisters(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.REGISTER),
						REGISTER_LABEL.CODE,
						REGISTER_LABEL.VALUE)
				.from(REGISTER_LABEL, REGISTER)
				.where(REGISTER_LABEL.LANG.eq(classifierLabelLang).and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode)).and(REGISTER_LABEL.CODE.eq(REGISTER.CODE)))
				.orderBy(REGISTER.ORDER_BY)
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
				.orderBy(REGION.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getDerivs(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.DERIV),
						DERIV_LABEL.CODE,
						DERIV_LABEL.VALUE)
				.from(DERIV_LABEL, DERIV)
				.where(DERIV_LABEL.LANG.eq(classifierLabelLang).and(DERIV_LABEL.TYPE.eq(classifierLabelTypeCode)).and(DERIV_LABEL.CODE.eq(DERIV.CODE)))
				.orderBy(DERIV.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getGenders(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.GENDER),
						GENDER_LABEL.CODE,
						GENDER_LABEL.VALUE)
				.from(GENDER_LABEL, GENDER)
				.where(GENDER_LABEL.LANG.eq(classifierLabelLang).and(GENDER_LABEL.TYPE.eq(classifierLabelTypeCode)).and(GENDER_LABEL.CODE.eq(GENDER.CODE)))
				.orderBy(GENDER.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getAspects(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.ASPECT),
						ASPECT_LABEL.CODE,
						ASPECT_LABEL.VALUE)
				.from(ASPECT_LABEL, ASPECT)
				.where(ASPECT_LABEL.LANG.eq(classifierLabelLang).and(ASPECT_LABEL.TYPE.eq(classifierLabelTypeCode)).and(ASPECT_LABEL.CODE.eq(ASPECT.CODE)))
				.orderBy(ASPECT.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getDomainsInUse(String classifierLabelLang, String classifierLabelTypeCode) {
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
								.and(MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE))
								.and(DOMAIN_LABEL.LANG.eq(classifierLabelLang))
								.and(DOMAIN_LABEL.TYPE.eq(classifierLabelTypeCode))))
				.orderBy(DOMAIN_LABEL.ORIGIN, DOMAIN_LABEL.CODE, DOMAIN_LABEL.LANG.desc())
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getWordTypes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.WORD_TYPE),
						WORD_TYPE_LABEL.CODE,
						WORD_TYPE_LABEL.VALUE)
				.from(WORD_TYPE_LABEL, WORD_TYPE)
				.where(WORD_TYPE_LABEL.LANG.eq(classifierLabelLang).and(WORD_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)).and(WORD_TYPE_LABEL.CODE.eq(WORD_TYPE.CODE)))
				.orderBy(WORD_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getWordRelationTypes(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.WORD_REL_TYPE),
						WORD_REL_TYPE_LABEL.CODE,
						WORD_REL_TYPE_LABEL.VALUE)
				.from(WORD_REL_TYPE_LABEL, WORD_REL_TYPE)
				.where(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)).and(WORD_REL_TYPE_LABEL.CODE.eq(WORD_REL_TYPE.CODE)))
				.orderBy(WORD_REL_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getLexemeRelationTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.LEX_REL_TYPE),
						LEX_REL_TYPE_LABEL.CODE,
						LEX_REL_TYPE_LABEL.VALUE)
				.from(LEX_REL_TYPE_LABEL, LEX_REL_TYPE)
				.where(LEX_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(LEX_REL_TYPE_LABEL.TYPE.eq(classifierLabelType)).and(LEX_REL_TYPE_LABEL.CODE.eq(LEX_REL_TYPE.CODE)))
				.orderBy(LEX_REL_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getMeaningRelationTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.MEANING_REL_TYPE),
						MEANING_REL_TYPE_LABEL.CODE,
						MEANING_REL_TYPE_LABEL.VALUE)
				.from(MEANING_REL_TYPE_LABEL, MEANING_REL_TYPE)
				.where(MEANING_REL_TYPE_LABEL.LANG.eq(classifierLabelLang).and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifierLabelType)).and(MEANING_REL_TYPE_LABEL.CODE.eq(MEANING_REL_TYPE.CODE)))
				.orderBy(MEANING_REL_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getDefinitionTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.DEFINITION_TYPE),
						DEFINITION_TYPE_LABEL.CODE,
						DEFINITION_TYPE_LABEL.VALUE)
				.from(DEFINITION_TYPE_LABEL, DEFINITION_TYPE)
				.where(DEFINITION_TYPE_LABEL.LANG.eq(classifierLabelLang).and(DEFINITION_TYPE_LABEL.TYPE.eq(classifierLabelType)).and(DEFINITION_TYPE_LABEL.CODE.eq(DEFINITION_TYPE.CODE)))
				.orderBy(DEFINITION_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getMorphs(String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						getClassifierNameField(ClassifierName.MORPH),
						MORPH_LABEL.CODE,
						MORPH_LABEL.VALUE)
				.from(MORPH_LABEL, MORPH)
				.where(MORPH_LABEL.LANG.eq(classifierLabelLang).and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode)).and(MORPH_LABEL.CODE.eq(MORPH.CODE)))
				.orderBy(MORPH.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getValueStates(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.VALUE_STATE),
						VALUE_STATE_LABEL.CODE,
						VALUE_STATE_LABEL.VALUE)
				.from(VALUE_STATE_LABEL, VALUE_STATE)
				.where(VALUE_STATE_LABEL.LANG.eq(classifierLabelLang).and(VALUE_STATE_LABEL.TYPE.eq(classifierLabelType)).and(VALUE_STATE_LABEL.CODE.eq(VALUE_STATE.CODE)))
				.orderBy(VALUE_STATE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getProficiencyLevels(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.PROFICIENCY_LEVEL),
						PROFICIENCY_LEVEL_LABEL.CODE,
						PROFICIENCY_LEVEL_LABEL.VALUE)
				.from(PROFICIENCY_LEVEL_LABEL, PROFICIENCY_LEVEL)
				.where(PROFICIENCY_LEVEL_LABEL.LANG.eq(classifierLabelLang).and(PROFICIENCY_LEVEL_LABEL.TYPE.eq(classifierLabelType)).and(PROFICIENCY_LEVEL_LABEL.CODE.eq(PROFICIENCY_LEVEL.CODE)))
				.orderBy(PROFICIENCY_LEVEL.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName}")
	public List<Origin> getDomainOrigins() {
		return create
				.selectDistinct(DOMAIN.ORIGIN.as("code"), DATASET.NAME.as("label"))
				.from(DOMAIN)
				.leftJoin(DATASET).on(DOMAIN.ORIGIN.eq(DATASET.CODE))
				.orderBy(DOMAIN.ORIGIN)
				.fetchInto(Origin.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #origin, #classifierLabelTypeCode}")
	public List<Classifier> getDomains(String origin, String classifierLabelTypeCode) {

		Domain d = DOMAIN.as("d");
		DomainLabel dll = DOMAIN_LABEL.as("dl");
		Language l = LANGUAGE.as("l");

		Table<Record3<String, String, String>> dl = DSL.select(
				dll.ORIGIN,
				dll.CODE,
				DSL.field("(array_agg(dl.value order by l.order_by)) [1]", String.class).as("value"))
				.from(dll, l)
				.where(
						dll.LANG.eq(l.CODE)
								.and(dll.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(dll.ORIGIN, dll.CODE)
				.asTable("dl");

		return create
				.select(getClassifierNameField(ClassifierName.DOMAIN),
						d.PARENT_ORIGIN,
						d.PARENT_CODE,
						d.ORIGIN,
						d.CODE,
						dl.field("value", String.class))
				.from(d, dl)
				.where(d.ORIGIN.eq(origin)
						.and(dl.field("origin", String.class).eq(d.ORIGIN))
						.and(dl.field("code", String.class).eq(d.CODE)))
				.orderBy(d.ORDER_BY)
				.fetchInto(Classifier.class);
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
				.orderBy(WORD_WORD_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getSemanticTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.SEMANTIC_TYPE),
						SEMANTIC_TYPE_LABEL.CODE,
						SEMANTIC_TYPE_LABEL.VALUE)
				.from(SEMANTIC_TYPE_LABEL, SEMANTIC_TYPE)
				.where(SEMANTIC_TYPE_LABEL.LANG.eq(classifierLabelLang).and(SEMANTIC_TYPE_LABEL.TYPE.eq(classifierLabelType)).and(SEMANTIC_TYPE_LABEL.CODE.eq(SEMANTIC_TYPE.CODE)))
				.orderBy(SEMANTIC_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getDisplayMorphs(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.DISPLAY_MORPH),
						DISPLAY_MORPH_LABEL.CODE,
						DISPLAY_MORPH_LABEL.VALUE)
				.from(DISPLAY_MORPH_LABEL, DISPLAY_MORPH)
				.where(DISPLAY_MORPH_LABEL.LANG.eq(classifierLabelLang).and(DISPLAY_MORPH_LABEL.TYPE.eq(classifierLabelType)).and(DISPLAY_MORPH_LABEL.CODE.eq(DISPLAY_MORPH.CODE)))
				.orderBy(DISPLAY_MORPH.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getGovernmentTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.GOVERNMENT_TYPE),
						GOVERNMENT_TYPE_LABEL.CODE,
						GOVERNMENT_TYPE_LABEL.VALUE)
				.from(GOVERNMENT_TYPE_LABEL, GOVERNMENT_TYPE)
				.where(GOVERNMENT_TYPE_LABEL.LANG.eq(classifierLabelLang).and(GOVERNMENT_TYPE_LABEL.TYPE.eq(classifierLabelType)).and(GOVERNMENT_TYPE_LABEL.CODE.eq(GOVERNMENT_TYPE.CODE)))
				.orderBy(GOVERNMENT_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Classifier> getEtymologyTypes() {
		return create
				.select(
						getClassifierNameField(ClassifierName.ETYMOLOGY_TYPE),
						ETYMOLOGY_TYPE.CODE,
						ETYMOLOGY_TYPE.CODE.as("value"))
				.from(ETYMOLOGY_TYPE)
				.orderBy(ETYMOLOGY_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getPosGroups(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.POS_GROUP),
						POS_GROUP_LABEL.CODE,
						POS_GROUP_LABEL.VALUE)
				.from(POS_GROUP_LABEL, POS_GROUP)
				.where(POS_GROUP_LABEL.LANG.eq(classifierLabelLang).and(POS_GROUP_LABEL.TYPE.eq(classifierLabelType)).and(POS_GROUP_LABEL.CODE.eq(POS_GROUP.CODE)))
				.orderBy(POS_GROUP.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getUsageTypes(String classifierLabelLang, String classifierLabelType) {
		return create
				.select(
						getClassifierNameField(ClassifierName.USAGE_TYPE),
						USAGE_TYPE_LABEL.CODE,
						USAGE_TYPE_LABEL.VALUE)
				.from(USAGE_TYPE_LABEL, USAGE_TYPE)
				.where(USAGE_TYPE_LABEL.LANG.eq(classifierLabelLang).and(USAGE_TYPE_LABEL.TYPE.eq(classifierLabelType)).and(USAGE_TYPE_LABEL.CODE.eq(USAGE_TYPE.CODE)))
				.orderBy(USAGE_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public eki.ekilex.data.Meaning getMeaning(Long meaningId) {
		return create
				.select(
						MEANING.ID.as("meaning_id"),
						MEANING.MANUAL_EVENT_ON,
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"))
				.from(MEANING, LEXEME)
				.where(
						MEANING.ID.eq(meaningId)
								.and(LEXEME.MEANING_ID.eq(MEANING.ID)))
				.groupBy(MEANING.ID)
				.fetchOptionalInto(eki.ekilex.data.Meaning.class)
				.orElse(null);
	}

	public List<FreeForm> getMeaningFreeforms(Long meaningId, String... excludeTypes) {
		return create
				.select(
						FREEFORM.ID,
						FREEFORM.TYPE,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.VALUE_DATE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY)
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
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY)
				.from(FREEFORM, MEANING_FREEFORM)
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
								.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.LEARNER_COMMENT.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<NoteSourceTuple> getMeaningNoteSourceTuples(Long meaningId) {

		return create
				.select(
						FREEFORM.ID.as("freeform_id"),
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.IS_PUBLIC,
						FREEFORM.ORDER_BY,
						FREEFORM.MODIFIED_BY,
						FREEFORM.MODIFIED_ON,
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"),
						FREEFORM_SOURCE_LINK.SOURCE_ID.as("source_id"))
				.from(MEANING_FREEFORM, FREEFORM.leftOuterJoin(FREEFORM_SOURCE_LINK)
						.on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(FREEFORM.ID)))
				.where(
						MEANING_FREEFORM.MEANING_ID.eq(meaningId)
								.and(FREEFORM.ID.eq(MEANING_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.NOTE.name())))
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
						iff.COMPLEXITY.as("image_freeform_complexity"),
						tff.VALUE_TEXT.as("title_freeform_value_text"),
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"),
						FREEFORM_SOURCE_LINK.SOURCE_ID.as("source_id"))
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

	public List<Media> getMeaningMedias(Long meaningId) {

		Freeform ff = FREEFORM.as("ff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");

		return create
				.select(
						ff.ID,
						ff.VALUE_TEXT.as("sourceUrl"),
						ff.COMPLEXITY)
				.from(ff, mff)
				.where(
						mff.MEANING_ID.eq(meaningId)
								.and(ff.ID.eq(mff.FREEFORM_ID))
								.and(ff.TYPE.eq(FreeformType.MEDIA_FILE.name())))
				.fetchInto(Media.class);
	}

	public List<OrderedClassifier> getMeaningDomains(Long meaningId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						getClassifierNameField(ClassifierName.DOMAIN),
						DOMAIN_LABEL.CODE,
						DOMAIN_LABEL.ORIGIN,
						DOMAIN_LABEL.VALUE,
						MEANING_DOMAIN.ID,
						MEANING_DOMAIN.ORDER_BY)
				.from(
						MEANING_DOMAIN.leftOuterJoin(DOMAIN_LABEL).on(
								MEANING_DOMAIN.DOMAIN_CODE.eq(DOMAIN_LABEL.CODE)
										.and(MEANING_DOMAIN.DOMAIN_ORIGIN.eq(DOMAIN_LABEL.ORIGIN))
										.and(DOMAIN_LABEL.LANG.eq(classifierLabelLang))
										.and(DOMAIN_LABEL.TYPE.eq(classifierLabelTypeCode))))
				.where(MEANING_DOMAIN.MEANING_ID.eq(meaningId))
				.orderBy(MEANING_DOMAIN.ORDER_BY, DOMAIN_LABEL.LANG.desc())
				.fetchInto(OrderedClassifier.class);
	}

	public List<Definition> getMeaningDefinitions(Long meaningId, String classifierLabelLang, String classifierLabelTypeCode) {
		return getMeaningDefinitions(meaningId, null, classifierLabelLang, classifierLabelTypeCode);
	}

	public List<Definition> getMeaningDefinitions(Long meaningId, String datasetCode, String classifierLabelLang, String classifierLabelTypeCode) {

		Condition where = DEFINITION.MEANING_ID.eq(meaningId);
		if (StringUtils.isNotBlank(datasetCode)) {
			where = where.and(DSL
					.exists(DSL
							.select(DEFINITION_DATASET.DEFINITION_ID)
							.from(DEFINITION_DATASET)
							.where(
									DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION.ID)
											.and(DEFINITION_DATASET.DATASET_CODE.eq(datasetCode)))));
		}
		return create
				.select(
						DEFINITION.ID,
						DEFINITION.VALUE_PRESE.as("value"),
						DEFINITION.LANG,
						DEFINITION.COMPLEXITY,
						DEFINITION.ORDER_BY,
						DEFINITION.DEFINITION_TYPE_CODE.as("type_code"),
						DEFINITION_TYPE_LABEL.VALUE.as("type_value"),
						DSL.field(DSL
								.select(DSL.arrayAgg(DEFINITION_DATASET.DATASET_CODE))
								.from(DEFINITION_DATASET)
								.where(DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION.ID)))
								.as("dataset_codes"),
						DEFINITION.IS_PUBLIC.as("is_public"))
				.from(
						DEFINITION
								.leftOuterJoin(DEFINITION_TYPE_LABEL).on(
										DEFINITION.DEFINITION_TYPE_CODE.eq(DEFINITION_TYPE_LABEL.CODE)
												.and(DEFINITION_TYPE_LABEL.LANG.eq(classifierLabelLang))
												.and(DEFINITION_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode))))
				.where(where)
				.orderBy(DEFINITION.ORDER_BY)
				.fetchInto(Definition.class);
	}

	public List<DefSourceAndNoteSourceTuple> getMeaningDefSourceAndNoteSourceTuples(Long meaningId) {

		return create
				.select(
						DEFINITION.ID.as("definition_id"),
						DEFINITION_SOURCE_LINK.ID.as("definition_source_link_id"),
						DEFINITION_SOURCE_LINK.TYPE.as("definition_source_link_type"),
						DEFINITION_SOURCE_LINK.NAME.as("definition_source_link_name"),
						DEFINITION_SOURCE_LINK.VALUE.as("definition_source_link_value"),
						DEFINITION_SOURCE_LINK.SOURCE_ID.as("definition_source_id"),
						FREEFORM.ID.as("note_id"),
						FREEFORM.VALUE_TEXT.as("note_value_text"),
						FREEFORM.VALUE_PRESE.as("note_value_prese"),
						FREEFORM.LANG.as("note_lang"),
						FREEFORM.COMPLEXITY.as("note_complexity"),
						FREEFORM.IS_PUBLIC.as("is_note_public"),
						FREEFORM.ORDER_BY.as("note_order_by"),
						FREEFORM_SOURCE_LINK.ID.as("note_source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("note_source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("note_source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("note_source_link_value"),
						FREEFORM_SOURCE_LINK.SOURCE_ID.as("note_source_id")
						)
				.from(
						DEFINITION
								.leftOuterJoin(DEFINITION_SOURCE_LINK).on(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(DEFINITION.ID))
								.leftOuterJoin(DEFINITION_FREEFORM).on(DEFINITION_FREEFORM.DEFINITION_ID.eq(DEFINITION.ID))
								.leftOuterJoin(FREEFORM).on(
										DEFINITION_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)
												.and(FREEFORM.TYPE.eq(FreeformType.NOTE.name())))
								.leftOuterJoin(FREEFORM_SOURCE_LINK).on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(FREEFORM.ID)))
				.where(DEFINITION.MEANING_ID.eq(meaningId))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(DefSourceAndNoteSourceTuple.class);
	}

	public List<MeaningWord> getMeaningWords(Long lexemeId) {

		SearchLangsRestriction meaningWordLangsRestriction = new SearchLangsRestriction();
		meaningWordLangsRestriction.setNoLangsFiltering(true);
		return getMeaningWords(lexemeId, meaningWordLangsRestriction);
	}

	public List<MeaningWord> getMeaningWords(Long lexemeId, SearchLangsRestriction meaningWordLangsRestriction) {

		boolean noLangsFiltering = meaningWordLangsRestriction.isNoLangsFiltering();
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		LexemeRegister lreg = LEXEME_REGISTER.as("lreg");
		LexRelation lrel = LEX_RELATION.as("lrel");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);
		Field<String[]> lrc = DSL.field(DSL.select(DSL.arrayAgg(lreg.REGISTER_CODE)).from(lreg).where(lreg.LEXEME_ID.eq(l2.ID)));

		Field<Boolean> whe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(wh)
				.where(
						wh.VALUE.eq(w2.VALUE)
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(
												lh.WORD_ID.eq(wh.ID)
														.and(lh.DATASET_CODE.eq(l2.DATASET_CODE)))))
				.groupBy(wh.VALUE)
				.asField();

		Field<Integer> dlrelc = DSL.field(DSL.select(DSL.count(lrel.ID))
				.from(lrel)
				.where(
						lrel.LEXEME1_ID.eq(l1.ID)
								.and(lrel.LEXEME2_ID.eq(l2.ID))
								.and(lrel.LEX_REL_TYPE_CODE.eq(LEX_REL_TYPE_CODE_DIRECT_MATCH))))
				.as("direct_match_lex_rel_count");

		Condition where =
				l1.ID.eq(lexemeId)
						.and(l2.MEANING_ID.eq(l1.MEANING_ID))
						.and(l2.ID.ne(l1.ID))
						.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
						.and(l2.WORD_ID.eq(w2.ID));

		if (!noLangsFiltering) {
			List<String> filteringLangs = meaningWordLangsRestriction.getFilteringLangs();
			where = where.and(w2.LANG.in(filteringLangs));
		}

		return create
				.select(
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.HOMONYM_NR,
						whe.as("homonyms_exist"),
						w2.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						l2.ID.as("lexeme_id"),
						l2.WEIGHT.as("lexeme_weight"),
						lrc.as("lex_register_codes"),
						dlrelc,
						l2.ORDER_BY)
				.from(l1, l2, w2)
				.where(where)
				.orderBy(w2.LANG, dlrelc.desc(), l2.ORDER_BY)
				.fetchInto(MeaningWord.class);
	}

	public List<eki.ekilex.data.MeaningRelation> getSynMeaningRelations(Long meaningId, String datasetCode) {

		MeaningRelation mr = MEANING_RELATION.as("mr");
		Meaning m2 = MEANING.as("m2");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		LexemeRegister lr = LEXEME_REGISTER.as("lr");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");

		Field<String[]> lexRegisterCodes = DSL
				.select(DSL.arrayAgg(lr.REGISTER_CODE).orderBy(lr.ORDER_BY))
				.from(lr)
				.where(lr.LEXEME_ID.eq(l2.ID))
				.groupBy(l2.ID)
				.asField("lexeme_register_codes");

		Table<Record11<Long, Long, Long, Long, String, String, String, Integer, String[], BigDecimal, Long>> mrel = DSL
				.select(
						mr.ID.as("id"),
						m2.ID.as("meaning_id"),
						l2.ID.as("lexeme_id"),
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						w2.HOMONYM_NR.as("word_homonym_nr"),
						lexRegisterCodes,
						mr.WEIGHT,
						mr.ORDER_BY
				)
				.from(
						mr
								.innerJoin(m2).on(m2.ID.eq(mr.MEANING2_ID))
								.innerJoin(l2).on(l2.MEANING_ID.eq(m2.ID).and(l2.DATASET_CODE.eq(datasetCode)))
								.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID)))
				.where(
						mr.MEANING1_ID.eq(meaningId)
								.and(mr.MEANING_REL_TYPE_CODE.eq(MEANING_REL_TYPE_CODE_SIMILAR)))
				.groupBy(m2.ID, mr.ID, l2.ID, w2.ID)
				.asTable("mrel");

		Field<Boolean> whe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(wh)
				.where(
						wh.VALUE.eq(mrel.field("word_value", String.class))
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(lh.WORD_ID.eq(wh.ID).and(lh.DATASET_CODE.eq(datasetCode)))))
				.groupBy(wh.VALUE)
				.asField();

		return create
				.select(
						mrel.field("id"),
						mrel.field("meaning_id"),
						mrel.field("lexeme_id"),
						mrel.field("word_id"),
						mrel.field("word_value"),
						mrel.field("word_value_prese"),
						mrel.field("word_lang"),
						mrel.field("word_homonym_nr"),
						mrel.field("lexeme_register_codes"),
						mrel.field("weight"),
						mrel.field("order_by"),
						whe.as("homonyms_exist"))
				.from(mrel)
				.orderBy(mrel.field("order_by"))
				.fetchInto(eki.ekilex.data.MeaningRelation.class);
	}

	public List<eki.ekilex.data.MeaningRelation> getMeaningRelations(
			Long meaningId, List<String> meaningWordPreferredOrderDatasetCodes, String classifierLabelLang, String classifierLabelTypeCode) {

		MeaningRelation mr = MEANING_RELATION.as("mr");
		MeaningRelTypeLabel mrtl = MEANING_REL_TYPE_LABEL.as("mrtl");
		Meaning m2 = MEANING.as("m2");
		Lexeme l2 = LEXEME.as("l2");
		LexemeRegister lr = LEXEME_REGISTER.as("lr");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Freeform ff = FREEFORM.as("ff");
		Word w2 = WORD.as("w2");

		Field<String> mrtf = DSL.field(DSL
				.select(mrtl.VALUE)
				.from(mrtl)
				.where(mr.MEANING_REL_TYPE_CODE.eq(mrtl.CODE))
				.and(mrtl.LANG.eq(classifierLabelLang))
				.and(mrtl.TYPE.eq(classifierLabelTypeCode)));

		Field<String[]> lvsf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(l2.VALUE_STATE_CODE))
				.from(l2)
				.where(l2.MEANING_ID.eq(m2.ID)
						.and(l2.WORD_ID.eq(w2.ID))
						.and(l2.VALUE_STATE_CODE.isNotNull()))
				.groupBy(l2.WORD_ID, l2.MEANING_ID));

		Field<String[]> lrf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(lr.REGISTER_CODE))
				.from(lr, l2)
				.where(l2.MEANING_ID.eq(m2.ID)
						.and(l2.WORD_ID.eq(w2.ID))
						.and(lr.LEXEME_ID.eq(l2.ID)))
				.groupBy(l2.WORD_ID, l2.MEANING_ID));

		Field<String[]> lgf = DSL.field(DSL
				.select(DSL.arrayAgg(ff.VALUE_PRESE))
				.from(ff, lff, l2)
				.where(l2.MEANING_ID.eq(m2.ID)
						.and(l2.WORD_ID.eq(w2.ID))
						.and(lff.LEXEME_ID.eq(l2.ID))
						.and(ff.ID.eq(lff.FREEFORM_ID))
						.and(ff.TYPE.eq(FreeformType.GOVERNMENT.name())))
				.groupBy(l2.WORD_ID, l2.MEANING_ID));

		Field<String[]> ldsf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(l2.DATASET_CODE))
				.from(l2)
				.where(l2.WORD_ID.eq(w2.ID))
				.groupBy(l2.WORD_ID));

		Condition orderByCond = l2.WORD_ID.eq(w2.ID)
				.and(l2.MEANING_ID.eq(m2.ID));

		if (CollectionUtils.isNotEmpty(meaningWordPreferredOrderDatasetCodes)) {
			if (meaningWordPreferredOrderDatasetCodes.size() == 1) {
				String meaningWordPreferredOrderDatasetCode = meaningWordPreferredOrderDatasetCodes.get(0);
				orderByCond = orderByCond.and(l2.DATASET_CODE.eq(meaningWordPreferredOrderDatasetCode));
			} else {
				orderByCond = orderByCond.and(l2.DATASET_CODE.in(meaningWordPreferredOrderDatasetCodes));				
			}
		}

		Field<Long> lobf = DSL.field(DSL
				.select(DSL.min(l2.ORDER_BY))
				.from(l2)
				.where(orderByCond));

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		return create
				.select(
						mr.ID.as("id"),
						m2.ID.as("meaning_id"),
						lvsf.as("lexeme_value_state_codes"),
						lrf.as("lexeme_register_codes"),
						lgf.as("lexeme_government_values"),
						lobf.as("lexeme_order_by"),
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						w2.ASPECT_CODE.as("word_aspect_code"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						ldsf.as("dataset_codes"),
						mr.MEANING_REL_TYPE_CODE.as("rel_type_code"),
						mrtf.as("rel_type_label"),
						mr.WEIGHT,
						mr.ORDER_BY
						)
				.from(
						mr
								.innerJoin(m2).on(m2.ID.eq(mr.MEANING2_ID))
								.innerJoin(l2).on(l2.MEANING_ID.eq(m2.ID))
								.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID)))
				.where(mr.MEANING1_ID.eq(meaningId).and(mr.MEANING_REL_TYPE_CODE.ne(MEANING_REL_TYPE_CODE_SIMILAR)))
				.groupBy(m2.ID, mr.ID, w2.ID)
				.orderBy(mr.ID, DSL.field("lexeme_order_by"))
				.fetchInto(eki.ekilex.data.MeaningRelation.class);
	}

	public List<Classifier> getMeaningSemanticTypes(Long meaningId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						getClassifierNameField(ClassifierName.SEMANTIC_TYPE),
						SEMANTIC_TYPE_LABEL.CODE,
						SEMANTIC_TYPE_LABEL.VALUE)
				.from(MEANING_SEMANTIC_TYPE, SEMANTIC_TYPE_LABEL)
				.where(
						MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId)
								.and(SEMANTIC_TYPE_LABEL.CODE.eq(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE))
								.and(SEMANTIC_TYPE_LABEL.LANG.eq(classifierLabelLang))
								.and(SEMANTIC_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.orderBy(MEANING_SEMANTIC_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public List<FreeForm> getLexemeFreeforms(Long lexemeId, String... excludedTypes) {
		return create
				.select(
						FREEFORM.ID,
						FREEFORM.TYPE,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.VALUE_DATE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.ORDER_BY)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
								.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.notIn(excludedTypes)))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<SourceLink> getLexemeSourceLinks(Long lexemeId) {
		return create
				.select(
						DSL.field(DSL.val(ReferenceOwner.LEXEME.name())).as("owner"),
						LEXEME_SOURCE_LINK.LEXEME_ID.as("owner_id"),
						LEXEME_SOURCE_LINK.ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.NAME,
						LEXEME_SOURCE_LINK.VALUE,
						LEXEME_SOURCE_LINK.SOURCE_ID)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_SOURCE_LINK.ORDER_BY)
				.fetchInto(SourceLink.class);
	}

	public List<FreeForm> getLexemeGrammars(Long lexemeId) {
		return create
				.select(
						FREEFORM.ID,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.ORDER_BY)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
						.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.eq(FreeformType.GRAMMAR.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(FreeForm.class);
	}

	public List<FreeForm> getOdLexemeRecommendations(Long lexemeId) {

		return create
				.select(
						FREEFORM.ID,
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.ORDER_BY,
						FREEFORM.MODIFIED_BY,
						FREEFORM.MODIFIED_ON)
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
								.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.OD_LEXEME_RECOMMENDATION.name())))
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
						gt.CLASSIF_CODE.as("type_code"),
						g.COMPLEXITY,
						g.ORDER_BY)
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
		Freeform odUd = FREEFORM.as("od_ud");
		Freeform odUa = FREEFORM.as("od_ua");
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
						u.COMPLEXITY.as("usage_complexity"),
						u.ORDER_BY.as("usage_order_by"),
						u.IS_PUBLIC.as("is_usage_public"),
						utype.CLASSIF_CODE.as("usage_type_code"),
						utypelbl.VALUE.as("usage_type_value"),
						ut.ID.as("usage_translation_id"),
						ut.VALUE_PRESE.as("usage_translation_value"),
						ut.LANG.as("usage_translation_lang"),
						ud.ID.as("usage_definition_id"),
						ud.VALUE_PRESE.as("usage_definition_value"),
						ud.LANG.as("usage_definition_lang"),
						odUd.ID.as("od_usage_definition_id"),
						odUd.VALUE_PRESE.as("od_usage_definition_value"),
						odUa.ID.as("od_usage_alternative_id"),
						odUa.VALUE_PRESE.as("od_usage_alternative_value"),
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
								.leftOuterJoin(odUd).on(odUd.PARENT_ID.eq(u.ID).and(odUd.TYPE.eq(FreeformType.OD_USAGE_DEFINITION.name())))
								.leftOuterJoin(odUa).on(odUa.PARENT_ID.eq(u.ID).and(odUa.TYPE.eq(FreeformType.OD_USAGE_ALTERNATIVE.name())))
								.leftOuterJoin(srcl).on(srcl.FREEFORM_ID.eq(u.ID))
								.leftOuterJoin(srcn).on(srcn.field("id").cast(Long.class).eq(srcl.SOURCE_ID))
								.leftOuterJoin(utype).on(utype.PARENT_ID.eq(u.ID).and(utype.TYPE.eq(FreeformType.USAGE_TYPE.name())))
								.leftOuterJoin(utypelbl).on(utypelbl.CODE.eq(utype.CLASSIF_CODE).and(utypelbl.LANG.eq(classifierLabelLang).and(utypelbl.TYPE.eq(classifierLabelTypeCode)))))
				.where(ulff.LEXEME_ID.eq(lexemeId))
				.orderBy(u.ORDER_BY, ut.ORDER_BY, ud.ORDER_BY, odUd.ORDER_BY, odUa.ORDER_BY, srcl.ORDER_BY)
				.fetchInto(UsageTranslationDefinitionTuple.class);
	}

	public List<NoteSourceTuple> getLexemeNoteSourceTuples(Long lexemeId) {

		return create
				.select(
						FREEFORM.ID.as("freeform_id"),
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.IS_PUBLIC,
						FREEFORM.ORDER_BY,
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"),
						FREEFORM_SOURCE_LINK.SOURCE_ID.as("source_id"))
				.from(LEXEME_FREEFORM, FREEFORM.leftOuterJoin(FREEFORM_SOURCE_LINK)
						.on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(FREEFORM.ID)))
				.where(
						LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)
								.and(FREEFORM.ID.eq(LEXEME_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.NOTE.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(NoteSourceTuple.class);
	}

	public List<LexemeRelation> getLexemeRelations(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		LexRelation r = LEX_RELATION.as("r");
		LexRelTypeLabel rtl = LEX_REL_TYPE_LABEL.as("rtl");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		return create
				.select(
						r.ID,
						l2.ID.as("lexeme_id"),
						l2.MEANING_ID,
						l2.WORD_ID,
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						rtl.VALUE.as("rel_type_label"),
						r.ORDER_BY)
				.from(
						r
						.innerJoin(l2).on(l2.ID.eq(r.LEXEME2_ID))
						.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID))
						.leftOuterJoin(rtl).on(
								r.LEX_REL_TYPE_CODE.eq(rtl.CODE)
										.and(rtl.LANG.eq(classifierLabelLang)
												.and(rtl.TYPE.eq(classifierLabelTypeCode)))))
				.where(r.LEXEME1_ID.eq(lexemeId))
				.groupBy(r.ID, l2.ID, w2.ID, rtl.VALUE)
				.orderBy(r.ORDER_BY)
				.fetchInto(LexemeRelation.class);
	}

	public List<String> getLexemeTags(Long lexemeId) {

		return create
				.select(LEXEME_TAG.TAG_NAME)
				.from(LEXEME_TAG)
				.where(LEXEME_TAG.LEXEME_ID.eq(lexemeId))
				.orderBy(LEXEME_TAG.CREATED_ON)
				.fetchInto(String.class);
	}

	public List<String> getMeaningTags(Long meaningId) {

		return create
				.select(MEANING_TAG.TAG_NAME)
				.from(MEANING_TAG)
				.where(MEANING_TAG.MEANING_ID.eq(meaningId))
				.orderBy(MEANING_TAG.CREATED_ON)
				.fetchInto(String.class);
	}

	public List<NoteSourceTuple> getWordNoteSourceTuples(Long wordId) {

		return create
				.select(
						FREEFORM.ID.as("freeform_id"),
						FREEFORM.VALUE_TEXT,
						FREEFORM.VALUE_PRESE,
						FREEFORM.LANG,
						FREEFORM.COMPLEXITY,
						FREEFORM.IS_PUBLIC,
						FREEFORM.ORDER_BY,
						FREEFORM.MODIFIED_BY,
						FREEFORM.MODIFIED_ON,
						FREEFORM_SOURCE_LINK.ID.as("source_link_id"),
						FREEFORM_SOURCE_LINK.TYPE.as("source_link_type"),
						FREEFORM_SOURCE_LINK.NAME.as("source_link_name"),
						FREEFORM_SOURCE_LINK.VALUE.as("source_link_value"),
						FREEFORM_SOURCE_LINK.SOURCE_ID.as("source_id"))
				.from(WORD_FREEFORM, FREEFORM.leftOuterJoin(FREEFORM_SOURCE_LINK)
						.on(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(FREEFORM.ID)))
				.where(
						WORD_FREEFORM.WORD_ID.eq(wordId)
								.and(FREEFORM.ID.eq(WORD_FREEFORM.FREEFORM_ID))
								.and(FREEFORM.TYPE.eq(FreeformType.NOTE.name())))
				.orderBy(FREEFORM.ORDER_BY)
				.fetchInto(NoteSourceTuple.class);
	}

	private Field<String> getClassifierNameField(ClassifierName classifierName) {
		return DSL.field(DSL.value(classifierName.name())).as("name");
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
		} else if (ClassifierName.DOMAIN.equals(classifierName)) {
			return create.select(
					getClassifierNameField(ClassifierName.DOMAIN),
					DOMAIN.ORIGIN,
					DOMAIN.CODE,
					DOMAIN_LABEL.VALUE)
					.from(DOMAIN.leftJoin(DOMAIN_LABEL).on(DOMAIN.CODE.eq(DOMAIN_LABEL.CODE).and(DOMAIN.ORIGIN.eq(DOMAIN_LABEL.ORIGIN))))
					.where(
							DOMAIN.CODE.eq(DOMAIN_LABEL.CODE)
									.and(DOMAIN.ORIGIN.eq(DOMAIN_LABEL.ORIGIN))
									.and(DOMAIN.DATASETS.contains(datasetCodes))
									.and(DOMAIN_LABEL.TYPE.eq(labelType))
									.and(DOMAIN_LABEL.LANG.eq(labelLanguage)))
					.orderBy(DOMAIN.CODE, DOMAIN_LABEL.LANG.desc())
					.fetchInto(Classifier.class);
		}

		throw new UnsupportedOperationException();
	}

	public List<Classifier> getDatasetDomains(String datasetCode) {
		String[] datasetCodes = {datasetCode};
		return create
				.select(getClassifierNameField(ClassifierName.DOMAIN),
						DOMAIN.PARENT_ORIGIN,
						DOMAIN.PARENT_CODE,
						DOMAIN.ORIGIN,
						DOMAIN.CODE,
						DOMAIN_LABEL.VALUE)
				.distinctOn(DOMAIN.CODE)
				.from(DOMAIN)
				.leftJoin(DOMAIN_LABEL)
				.on(DOMAIN.CODE.eq(DOMAIN_LABEL.CODE).and(DOMAIN.ORIGIN.eq(DOMAIN_LABEL.ORIGIN)))
				.where(DOMAIN.DATASETS.contains(datasetCodes))
				.fetchInto(Classifier.class);
	}

	public WordLexemeMeaningIdTuple getWordLexemeMeaningId(Long lexemeId) {

		return create
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(WordLexemeMeaningIdTuple.class);
	}

}