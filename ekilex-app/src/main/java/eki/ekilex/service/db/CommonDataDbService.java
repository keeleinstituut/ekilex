package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Public.PUBLIC;
import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.DATASET_FREEFORM_TYPE;
import static eki.ekilex.data.db.main.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DEFINITION_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.DOMAIN;
import static eki.ekilex.data.db.main.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.FREEFORM_TYPE;
import static eki.ekilex.data.db.main.Tables.FREEFORM_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.GOVERNMENT;
import static eki.ekilex.data.db.main.Tables.GRAMMAR;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.main.Tables.LEARNER_COMMENT;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.main.Tables.LEX_RELATION;
import static eki.ekilex.data.db.main.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_MEDIA;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING_REL_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.main.Tables.MEANING_TAG;
import static eki.ekilex.data.db.main.Tables.SEMANTIC_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.TAG;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.USAGE_DEFINITION;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_FORUM;
import static eki.ekilex.data.db.main.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.main.Tables.WORD_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record11;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformOwner;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.MeaningForum;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Origin;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.DatasetFreeformType;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.DefinitionNote;
import eki.ekilex.data.db.main.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.main.tables.DefinitionSourceLink;
import eki.ekilex.data.db.main.tables.DefinitionTypeLabel;
import eki.ekilex.data.db.main.tables.Domain;
import eki.ekilex.data.db.main.tables.DomainLabel;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.Freeform;
import eki.ekilex.data.db.main.tables.FreeformSourceLink;
import eki.ekilex.data.db.main.tables.FreeformType;
import eki.ekilex.data.db.main.tables.FreeformTypeLabel;
import eki.ekilex.data.db.main.tables.Government;
import eki.ekilex.data.db.main.tables.Grammar;
import eki.ekilex.data.db.main.tables.Language;
import eki.ekilex.data.db.main.tables.LanguageLabel;
import eki.ekilex.data.db.main.tables.LexRelTypeLabel;
import eki.ekilex.data.db.main.tables.LexRelation;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeFreeform;
import eki.ekilex.data.db.main.tables.LexemeRegister;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningDomain;
import eki.ekilex.data.db.main.tables.MeaningFreeform;
import eki.ekilex.data.db.main.tables.MeaningImage;
import eki.ekilex.data.db.main.tables.MeaningImageSourceLink;
import eki.ekilex.data.db.main.tables.MeaningMedia;
import eki.ekilex.data.db.main.tables.MeaningNote;
import eki.ekilex.data.db.main.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.main.tables.MeaningRelTypeLabel;
import eki.ekilex.data.db.main.tables.MeaningRelation;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.Usage;
import eki.ekilex.data.db.main.tables.UsageDefinition;
import eki.ekilex.data.db.main.tables.UsageSourceLink;
import eki.ekilex.data.db.main.tables.UsageTranslation;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordFreeform;

//only common use data reading!
@Component
public class CommonDataDbService extends AbstractDataDbService {

	public Map<String, String> getDatasetNameMap() {
		return mainDb.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public List<Dataset> getAllDatasets() {
		return mainDb.selectFrom(DATASET).orderBy(DATASET.NAME).fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_DATASET, key = "#root.methodName")
	public List<Dataset> getVisibleDatasets() {
		return mainDb.select(DATASET.CODE, DATASET.NAME).from(DATASET).where(DATASET.IS_VISIBLE.isTrue()).orderBy(DATASET.NAME).fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_DATASET, key = "#root.methodName")
	public List<Dataset> getVisibleDatasetsWithOwner() {
		return mainDb
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DATASET.IS_VISIBLE.isTrue()
						.andExists(DSL
								.select(DATASET_PERMISSION.ID)
								.from(DATASET_PERMISSION)
								.where(DATASET_PERMISSION.DATASET_CODE.eq(DATASET.CODE)
										.and(DATASET_PERMISSION.AUTH_ITEM.eq(AuthorityItem.DATASET.name()))
										.and(DATASET_PERMISSION.AUTH_OPERATION.eq(AuthorityOperation.OWN.name())))))
				.orderBy(DATASET.NAME)
				.fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_TAG, key = "#root.methodName")
	public List<String> getTags() {
		return mainDb.select(TAG.NAME).from(TAG).orderBy(TAG.ORDER_BY).fetchInto(String.class);
	}

	@Cacheable(value = CACHE_KEY_TAG, key = "{#root.methodName, #tagType}")
	public List<String> getTags(String tagType) {
		return mainDb.select(TAG.NAME).from(TAG).where(TAG.TYPE.eq(tagType)).orderBy(TAG.ORDER_BY).fetchInto(String.class);
	}

	public List<Classifier> getFreeformTypes(FreeformOwner freeformOwner, String classifierLabelLang) {
		return getFreeformTypes(null, freeformOwner, classifierLabelLang);
	}

	public List<Classifier> getFreeformTypes(String datasetCode, FreeformOwner freeformOwner, String classifierLabelLang) {

		FreeformType cl = FREEFORM_TYPE.as("cl");
		FreeformTypeLabel cll = FREEFORM_TYPE_LABEL.as("cll");
		DatasetFreeformType dscl = DATASET_FREEFORM_TYPE.as("dscl");

		Field<String> clvf = DSL
				.select(cll.VALUE)
				.from(cll)
				.where(
						cll.CODE.eq(cl.CODE)
								.and(cll.LANG.eq(classifierLabelLang))
								.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.limit(1)
				.asField();

		Condition where1 = dscl.FREEFORM_OWNER.eq(freeformOwner.name())
				.and(dscl.FREEFORM_TYPE_CODE.eq(cl.CODE));
		if (StringUtils.isNotBlank(datasetCode)) {
			where1 = where1.and(dscl.DATASET_CODE.eq(datasetCode));
		}

		return mainDb
				.select(
						getClassifierNameField(ClassifierName.FREEFORM_TYPE).as("name"),
						cl.CODE,
						DSL.coalesce(clvf, cl.CODE).as("value"))
				.from(cl)
				.whereExists(DSL
						.select(dscl.ID)
						.from(dscl)
						.where(where1)

				)
				.orderBy(cl.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName}")
	public List<Origin> getDomainOrigins() {
		return mainDb
				.selectDistinct(DOMAIN.ORIGIN.as("code"), DATASET.NAME.as("label"))
				.from(DOMAIN)
				.leftJoin(DATASET).on(DOMAIN.ORIGIN.eq(DATASET.CODE))
				.orderBy(DOMAIN.ORIGIN)
				.fetchInto(Origin.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierLabelLang}")
	public List<Classifier> getDomainsInUse(String classifierLabelLang) {

		Domain cl = DOMAIN.as("cl");
		MeaningDomain mcl = MEANING_DOMAIN.as("mcl");
		Condition where = DSL.exists(DSL
				.select(mcl.ID)
				.from(mcl)
				.where(
						mcl.DOMAIN_ORIGIN.eq(cl.ORIGIN)
								.and(mcl.DOMAIN_CODE.eq(cl.CODE))));

		return getDomains(classifierLabelLang, cl, where);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #origin, #classifierLabelLang}")
	public List<Classifier> getDomains(String origin, String classifierLabelLang) {

		Domain cl = DOMAIN.as("cl");
		Condition where = cl.ORIGIN.eq(origin);

		return getDomains(classifierLabelLang, cl, where);
	}

	private List<Classifier> getDomains(String classifierLabelLang, Domain cl, Condition where) {

		DomainLabel cll = DOMAIN_LABEL.as("cll");

		Field<String> clvf = DSL
				.select(cll.VALUE)
				.from(cll)
				.where(
						cll.CODE.eq(cl.CODE)
								.and(cll.ORIGIN.eq(cl.ORIGIN))
								.and(cll.LANG.eq(classifierLabelLang))
								.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.limit(1)
				.asField();

		Field<String> clcf = DSL
				.select(cll.VALUE)
				.from(cll)
				.where(
						cll.CODE.eq(cl.CODE)
								.and(cll.ORIGIN.eq(cl.ORIGIN))
								.and(cll.LANG.eq(classifierLabelLang))
								.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_COMMENT)))
				.limit(1)
				.asField();

		return mainDb
				.select(
						getClassifierNameField(ClassifierName.DOMAIN).as("name"),
						cl.PARENT_ORIGIN,
						cl.PARENT_CODE,
						cl.ORIGIN,
						cl.CODE,
						clvf.as("value"),
						clcf.as("comment"))
				.from(cl)
				.where(where)
				.orderBy(cl.ORIGIN, cl.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierName, #classifierLabelLang}")
	public List<Classifier> getDefaultClassifiers(ClassifierName classifierName, String classifierLabelLang) {
		return getClassifiers(classifierName, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #classifierName, #classifierLabelLang, #classifierLabelTypeCode}")
	public List<Classifier> getClassifiers(ClassifierName classifierName, String classifierLabelLang, String classifierLabelTypeCode) {

		String classifierTableName = getTableName(classifierName);
		Table<?> cl = PUBLIC.getTable(classifierTableName).as("cl");
		Field<String> clNameField = DSL.field(DSL.value(classifierName.name()));
		Field<String> clCodeField = cl.field("code", String.class);

		if (classifierName.hasLabel()) {

			String classifierLabelTableName = getLabelTableName(classifierName);
			Table<?> cll = PUBLIC.getTable(classifierLabelTableName).as("cll");
			Field<String> cllCodeField = cll.field("code", String.class);
			Field<String> cllValueField = cll.field("value", String.class);
			Field<String> cllLangField = cll.field("lang", String.class);
			Field<String> cllTypeField = cll.field("type", String.class);

			Field<String> clvf = DSL
					.select(cllValueField)
					.from(cll)
					.where(
							cllCodeField.eq(clCodeField)
									.and(cllLangField.eq(classifierLabelLang))
									.and(cllTypeField.eq(classifierLabelTypeCode)))
					.limit(1)
					.asField();

			Field<String> clcf = DSL
					.select(cllValueField)
					.from(cll)
					.where(
							cllCodeField.eq(clCodeField)
									.and(cllLangField.eq(classifierLabelLang))
									.and(cllTypeField.eq(CLASSIF_LABEL_TYPE_COMMENT)))
					.limit(1)
					.asField();

			return mainDb
					.select(
							clNameField.as("name"),
							clCodeField.as("code"),
							DSL.coalesce(clvf, clCodeField).as("value"),
							clcf.as("comment"))
					.from(cl)
					.orderBy(cl.field("order_by"))
					.fetchInto(Classifier.class);

		} else {

			return mainDb
					.select(
							clNameField.as("name"),
							clCodeField.as("code"),
							clCodeField.as("value"))
					.from(cl)
					.orderBy(cl.field("order_by"))
					.fetchInto(Classifier.class);
		}
	}

	public List<Classifier> getWordTypes(Long wordId, String classifierLabelLang) {

		return mainDb
				.select(
						getClassifierNameField(ClassifierName.WORD_TYPE).as("name"),
						WORD_TYPE_LABEL.CODE,
						WORD_TYPE_LABEL.VALUE)
				.from(WORD_WORD_TYPE, WORD_TYPE_LABEL)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordId)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_LABEL.CODE))
								.and(WORD_TYPE_LABEL.LANG.eq(classifierLabelLang))
								.and(WORD_TYPE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.orderBy(WORD_WORD_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public List<WordForum> getWordForums(Long wordId) {

		return mainDb
				.selectFrom(WORD_FORUM)
				.where(WORD_FORUM.WORD_ID.eq(wordId))
				.orderBy(WORD_FORUM.ORDER_BY.desc())
				.fetchInto(WordForum.class);
	}

	public List<eki.ekilex.data.Freeform> getWordFreeforms(Long wordId, String classifierLabelLang) {

		Freeform f = FREEFORM.as("f");
		WordFreeform wf = WORD_FREEFORM.as("wf");
		FreeformTypeLabel ftl = FREEFORM_TYPE_LABEL.as("ftl");
		FreeformSourceLink fsl = FREEFORM_SOURCE_LINK.as("fsl");
		Source s = SOURCE.as("s");

		Field<JSON> fslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(fsl.ID),
										DSL.key("name").value(fsl.NAME),
										DSL.key("sourceId").value(fsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(fsl.ORDER_BY)))
						.orderBy(fsl.ORDER_BY))
				.from(fsl, s)
				.where(
						fsl.FREEFORM_ID.eq(f.ID)
								.and(fsl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						f.ID,
						f.FREEFORM_TYPE_CODE,
						DSL.coalesce(ftl.VALUE, f.FREEFORM_TYPE_CODE).as("freeform_type_value"),
						f.VALUE,
						f.VALUE_PRESE,
						f.LANG,
						f.ORDER_BY,
						f.CREATED_BY,
						f.CREATED_ON,
						f.MODIFIED_BY,
						f.MODIFIED_ON,
						fslf.as("source_links"))
				.from(
						wf
								.innerJoin(f).on(f.ID.eq(wf.FREEFORM_ID))
								.leftOuterJoin(ftl).on(
										ftl.CODE.eq(f.FREEFORM_TYPE_CODE)
												.and(ftl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
												.and(ftl.LANG.eq(classifierLabelLang))))
				.where(wf.WORD_ID.eq(wordId))
				.orderBy(f.ORDER_BY)
				.fetchInto(eki.ekilex.data.Freeform.class);
	}

	public eki.ekilex.data.Meaning getMeaning(Long meaningId) {
		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");

		return mainDb
				.select(
						m.ID.as("meaning_id"),
						m.MANUAL_EVENT_ON,
						DSL.arrayAggDistinct(l.ID).orderBy(l.ID).as("lexeme_ids"))
				.from(m, l)
				.where(
						m.ID.eq(meaningId)
								.and(l.MEANING_ID.eq(m.ID)))
				.groupBy(m.ID)
				.fetchOptionalInto(eki.ekilex.data.Meaning.class)
				.orElse(null);
	}

	public eki.ekilex.data.Freeform getFreeform(Long id, String classifierLabelLang) {

		Freeform f = FREEFORM.as("f");
		FreeformTypeLabel ftl = FREEFORM_TYPE_LABEL.as("ftl");
		FreeformSourceLink fsl = FREEFORM_SOURCE_LINK.as("fsl");
		Source s = SOURCE.as("s");

		Field<JSON> fslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(fsl.ID),
										DSL.key("name").value(fsl.NAME),
										DSL.key("sourceId").value(fsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(fsl.ORDER_BY)))
						.orderBy(fsl.ORDER_BY))
				.from(fsl, s)
				.where(
						fsl.FREEFORM_ID.eq(f.ID)
								.and(fsl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						f.ID,
						f.FREEFORM_TYPE_CODE,
						DSL.coalesce(ftl.VALUE, f.FREEFORM_TYPE_CODE).as("freeform_type_value"),
						f.VALUE,
						f.VALUE_PRESE,
						f.LANG,
						f.ORDER_BY,
						f.CREATED_BY,
						f.CREATED_ON,
						f.MODIFIED_BY,
						f.MODIFIED_ON,
						fslf.as("source_links"))
				.from(
						f.leftOuterJoin(ftl).on(
								ftl.CODE.eq(f.FREEFORM_TYPE_CODE)
										.and(ftl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
										.and(ftl.LANG.eq(classifierLabelLang))))
				.where(f.ID.eq(id))
				.fetchOptionalInto(eki.ekilex.data.Freeform.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.Freeform> getMeaningFreeforms(Long meaningId, String classifierLabelLang) {

		Freeform f = FREEFORM.as("f");
		MeaningFreeform mf = MEANING_FREEFORM.as("mf");
		FreeformTypeLabel ftl = FREEFORM_TYPE_LABEL.as("ftl");
		FreeformSourceLink fsl = FREEFORM_SOURCE_LINK.as("fsl");
		Source s = SOURCE.as("s");

		Field<JSON> fslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(fsl.ID),
										DSL.key("name").value(fsl.NAME),
										DSL.key("sourceId").value(fsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(fsl.ORDER_BY)))
						.orderBy(fsl.ORDER_BY))
				.from(fsl, s)
				.where(
						fsl.FREEFORM_ID.eq(f.ID)
								.and(fsl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						f.ID,
						f.FREEFORM_TYPE_CODE,
						DSL.coalesce(ftl.VALUE, f.FREEFORM_TYPE_CODE).as("freeform_type_value"),
						f.VALUE,
						f.VALUE_PRESE,
						f.LANG,
						f.ORDER_BY,
						f.CREATED_BY,
						f.CREATED_ON,
						f.MODIFIED_BY,
						f.MODIFIED_ON,
						fslf.as("source_links"))
				.from(
						mf
								.innerJoin(f).on(f.ID.eq(mf.FREEFORM_ID))
								.leftOuterJoin(ftl).on(
										ftl.CODE.eq(f.FREEFORM_TYPE_CODE)
												.and(ftl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
												.and(ftl.LANG.eq(classifierLabelLang))))
				.where(mf.MEANING_ID.eq(meaningId))
				.orderBy(f.ORDER_BY)
				.fetchInto(eki.ekilex.data.Freeform.class);
	}

	public List<eki.ekilex.data.LearnerComment> getMeaningLearnerComments(Long meaningId) {

		return mainDb
				.selectFrom(LEARNER_COMMENT)
				.where(LEARNER_COMMENT.MEANING_ID.eq(meaningId))
				.orderBy(LEARNER_COMMENT.ORDER_BY)
				.fetchInto(eki.ekilex.data.LearnerComment.class);
	}

	public List<eki.ekilex.data.MeaningNote> getMeaningNotes(Long meaningId) {

		MeaningNote mn = MEANING_NOTE.as("mn");
		MeaningNoteSourceLink mnsl = MEANING_NOTE_SOURCE_LINK.as("mnsl");
		Source s = SOURCE.as("s");

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_MEANING_NOTE, mn.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_NOTE, mn.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_MEANING_NOTE, mn.ID);

		Field<JSON> mnslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(mnsl.ID),
										DSL.key("name").value(mnsl.NAME),
										DSL.key("sourceId").value(mnsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(mnsl.ORDER_BY)))
						.orderBy(mnsl.ORDER_BY))
				.from(mnsl, s)
				.where(
						mnsl.MEANING_NOTE_ID.eq(mn.ID)
								.and(mnsl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						mn.ID,
						mn.MEANING_ID,
						mn.VALUE,
						mn.VALUE_PRESE,
						mn.LANG,
						mn.IS_PUBLIC,
						mn.CREATED_ON,
						mn.CREATED_BY,
						mn.MODIFIED_ON,
						mn.MODIFIED_BY,
						mn.ORDER_BY,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"),
						mnslf.as("source_links"))
				.from(mn)
				.where(mn.MEANING_ID.eq(meaningId))
				.orderBy(mn.ORDER_BY)
				.fetchInto(eki.ekilex.data.MeaningNote.class);
	}

	public List<eki.ekilex.data.MeaningImage> getMeaningImages(Long meaningId) {

		MeaningImage mi = MEANING_IMAGE.as("mi");
		MeaningImageSourceLink misl = MEANING_IMAGE_SOURCE_LINK.as("misl");
		Source s = SOURCE.as("s");

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_MEANING_IMAGE, mi.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_IMAGE, mi.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_MEANING_IMAGE, mi.ID);

		Field<JSON> mislf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(misl.ID),
										DSL.key("name").value(misl.NAME),
										DSL.key("sourceId").value(misl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(misl.ORDER_BY)))
						.orderBy(misl.ORDER_BY))
				.from(misl, s)
				.where(
						misl.MEANING_IMAGE_ID.eq(mi.ID)
								.and(misl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						mi.ID,
						mi.URL,
						mi.TITLE,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"),
						mislf.as("source_links"))
				.from(mi)
				.where(mi.MEANING_ID.eq(meaningId))
				.orderBy(mi.ORDER_BY)
				.fetchInto(eki.ekilex.data.MeaningImage.class);
	}

	public List<eki.ekilex.data.MeaningMedia> getMeaningMedias(Long meaningId) {

		MeaningMedia mm = MEANING_MEDIA.as("mm");

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_MEANING_MEDIA, mm.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_MEDIA, mm.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_MEANING_MEDIA, mm.ID);

		return mainDb
				.select(
						mm.ID,
						mm.URL,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"))
				.from(mm)
				.where(mm.MEANING_ID.eq(meaningId))
				.fetchInto(eki.ekilex.data.MeaningMedia.class);
	}

	public List<OrderedClassifier> getMeaningDomains(Long meaningId, String classifierLabelLang) {

		Domain cl = DOMAIN.as("cl");
		DomainLabel cll = DOMAIN_LABEL.as("cll");
		MeaningDomain md = MEANING_DOMAIN.as("md");

		Field<String> dlvf = DSL
				.select(cll.VALUE)
				.from(cll)
				.where(
						cll.CODE.eq(cl.CODE)
								.and(cll.ORIGIN.eq(cl.ORIGIN))
								.and(cll.LANG.eq(classifierLabelLang))
								.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.limit(1)
				.asField();

		Field<String> dcvf = DSL
				.select(cll.VALUE)
				.from(cll)
				.where(
						cll.CODE.eq(cl.CODE)
								.and(cll.ORIGIN.eq(cl.ORIGIN))
								.and(cll.LANG.eq(classifierLabelLang))
								.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_COMMENT)))
				.limit(1)
				.asField();

		return mainDb
				.select(
						getClassifierNameField(ClassifierName.DOMAIN).as("name"),
						cl.PARENT_ORIGIN,
						cl.PARENT_CODE,
						cl.ORIGIN,
						cl.CODE,
						dlvf.as("value"),
						dcvf.as("comment"),
						md.ID,
						md.ORDER_BY)
				.from(cl, md)
				.where(
						md.DOMAIN_ORIGIN.eq(cl.ORIGIN)
								.and(md.DOMAIN_CODE.eq(cl.CODE))
								.and(md.MEANING_ID.eq(meaningId)))
				.orderBy(md.ORDER_BY, cl.ORIGIN, cl.ORDER_BY)
				.fetchInto(OrderedClassifier.class);
	}

	public List<eki.ekilex.data.Definition> getMeaningDefinitions(Long meaningId, String classifierLabelLang) {
		return getMeaningDefinitions(meaningId, null, classifierLabelLang);
	}

	public List<eki.ekilex.data.Definition> getMeaningDefinitions(Long meaningId, String datasetCode, String classifierLabelLang) {

		Definition d = DEFINITION.as("d");
		DefinitionDataset dds = DEFINITION_DATASET.as("dds");
		DefinitionTypeLabel dtl = DEFINITION_TYPE_LABEL.as("dtl");
		DefinitionNote dn = DEFINITION_NOTE.as("dn");
		DefinitionNoteSourceLink dnsl = DEFINITION_NOTE_SOURCE_LINK.as("dnsl");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = SOURCE.as("s");

		Condition where = d.MEANING_ID.eq(meaningId);
		if (StringUtils.isNotBlank(datasetCode)) {
			where = where
					.andExists(DSL
							.select(dds.DEFINITION_ID)
							.from(dds)
							.where(
									dds.DEFINITION_ID.eq(d.ID)
											.and(dds.DATASET_CODE.eq(datasetCode))));
		}

		Field<String[]> ddsf = DSL.field(DSL
				.select(DSL.arrayAgg(dds.DATASET_CODE))
				.from(dds)
				.where(dds.DEFINITION_ID.eq(d.ID)));

		Field<JSON> dnslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(dnsl.ID),
										DSL.key("name").value(dnsl.NAME),
										DSL.key("sourceId").value(dnsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(dnsl.ORDER_BY)))
						.orderBy(dnsl.ORDER_BY))
				.from(dnsl, s)
				.where(
						dnsl.DEFINITION_NOTE_ID.eq(dn.ID)
								.and(dnsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> dnf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(dn.ID),
										DSL.key("definitionId").value(dn.DEFINITION_ID),
										DSL.key("value").value(dn.VALUE),
										DSL.key("valuePrese").value(dn.VALUE_PRESE),
										DSL.key("lang").value(dn.LANG),
										DSL.key("public").value(dn.IS_PUBLIC),
										DSL.key("createdOn").value(dn.CREATED_ON),
										DSL.key("createdBy").value(dn.CREATED_BY),
										DSL.key("modifiedOn").value(dn.MODIFIED_ON),
										DSL.key("modifiedBy").value(dn.MODIFIED_BY),
										DSL.key("sourceLinks").value(dnslf)))
						.orderBy(dn.ORDER_BY))
				.from(dn)
				.where(dn.DEFINITION_ID.eq(d.ID))
				.asField();

		Field<JSON> dslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(dsl.ID),
										DSL.key("name").value(dsl.NAME),
										DSL.key("sourceId").value(dsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(dsl.ORDER_BY)))
						.orderBy(dsl.ORDER_BY))
				.from(dsl, s)
				.where(
						dsl.DEFINITION_ID.eq(d.ID)
								.and(dsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_DEFINITION, d.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_DEFINITION, d.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_DEFINITION, d.ID);

		return mainDb
				.select(
						d.ID,
						d.VALUE,
						d.VALUE_PRESE,
						d.LANG,
						d.ORDER_BY,
						d.DEFINITION_TYPE_CODE.as("type_code"),
						dtl.VALUE.as("type_value"),
						d.IS_PUBLIC,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"),
						ddsf.as("dataset_codes"),
						dnf.as("notes"),
						dslf.as("source_links"))
				.from(
						d
								.leftOuterJoin(dtl).on(
										d.DEFINITION_TYPE_CODE.eq(dtl.CODE)
												.and(dtl.LANG.eq(classifierLabelLang))
												.and(dtl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(where)
				.orderBy(d.ORDER_BY)
				.fetchInto(eki.ekilex.data.Definition.class);
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
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(w2.ID);
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(w2.ID);
		Field<String[]> lrc = DSL.field(DSL.select(DSL.arrayAgg(lreg.REGISTER_CODE)).from(lreg).where(lreg.LEXEME_ID.eq(l2.ID)));

		Field<Boolean> whe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(wh)
				.where(
						wh.VALUE.eq(w2.VALUE)
								.and(wh.IS_PUBLIC.isTrue())
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(
												lh.WORD_ID.eq(wh.ID)
														.and(lh.DATASET_CODE.eq(l2.DATASET_CODE)))))
				.groupBy(wh.VALUE)
				.asField();

		Condition where = l1.ID.eq(lexemeId)
				.and(l2.MEANING_ID.eq(l1.MEANING_ID))
				.and(l2.ID.ne(l1.ID))
				.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
				.and(l2.WORD_ID.eq(w2.ID))
				.and(w2.IS_PUBLIC.isTrue());

		if (!noLangsFiltering) {
			List<String> filteringLangs = meaningWordLangsRestriction.getFilteringLangs();
			where = where.and(w2.LANG.in(filteringLangs));
		}

		return mainDb
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
						l2.IS_PUBLIC.as("is_lexeme_public"),
						lrc.as("lex_register_codes"),
						l2.ORDER_BY)
				.from(l1, l2, w2)
				.where(where)
				.orderBy(w2.LANG, l2.ORDER_BY)
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
						mr.ORDER_BY)
				.from(
						mr
								.innerJoin(m2).on(m2.ID.eq(mr.MEANING2_ID))
								.innerJoin(l2).on(l2.MEANING_ID.eq(m2.ID).and(l2.DATASET_CODE.eq(datasetCode)))
								.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID).and(w2.IS_PUBLIC.isTrue())))
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
								.and(wh.IS_PUBLIC.isTrue())
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(lh.WORD_ID.eq(wh.ID).and(lh.DATASET_CODE.eq(datasetCode)))))
				.groupBy(wh.VALUE)
				.asField();

		return mainDb
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
			Long meaningId, List<String> meaningWordPreferredOrderDatasetCodes, String classifierLabelLang) {

		MeaningRelation mr = MEANING_RELATION.as("mr");
		MeaningRelTypeLabel mrtl = MEANING_REL_TYPE_LABEL.as("mrtl");
		Meaning m2 = MEANING.as("m2");
		Lexeme l2 = LEXEME.as("l2");
		LexemeRegister lr = LEXEME_REGISTER.as("lr");
		Government lg = GOVERNMENT.as("lg");
		Word w2 = WORD.as("w2");

		Field<String> mrtf = DSL.field(DSL
				.select(mrtl.VALUE)
				.from(mrtl)
				.where(mr.MEANING_REL_TYPE_CODE.eq(mrtl.CODE))
				.and(mrtl.LANG.eq(classifierLabelLang))
				.and(mrtl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)));

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
				.select(DSL.arrayAgg(lg.VALUE))
				.from(lg, l2)
				.where(l2.MEANING_ID.eq(m2.ID)
						.and(l2.WORD_ID.eq(w2.ID))
						.and(lg.LEXEME_ID.eq(l2.ID)))
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

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(w2.ID);
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(w2.ID);

		return mainDb
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
						mr.ORDER_BY)
				.from(
						mr
								.innerJoin(m2).on(m2.ID.eq(mr.MEANING2_ID))
								.innerJoin(l2).on(l2.MEANING_ID.eq(m2.ID))
								.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID).and(w2.IS_PUBLIC.isTrue())))
				.where(mr.MEANING1_ID.eq(meaningId).and(mr.MEANING_REL_TYPE_CODE.ne(MEANING_REL_TYPE_CODE_SIMILAR)))
				.groupBy(m2.ID, mr.ID, w2.ID)
				.orderBy(mr.ID, DSL.field("lexeme_order_by"))
				.fetchInto(eki.ekilex.data.MeaningRelation.class);
	}

	public List<Classifier> getMeaningSemanticTypes(Long meaningId, String classifierLabelLang) {

		return mainDb
				.select(
						getClassifierNameField(ClassifierName.SEMANTIC_TYPE).as("name"),
						SEMANTIC_TYPE_LABEL.CODE,
						SEMANTIC_TYPE_LABEL.VALUE)
				.from(MEANING_SEMANTIC_TYPE, SEMANTIC_TYPE_LABEL)
				.where(
						MEANING_SEMANTIC_TYPE.MEANING_ID.eq(meaningId)
								.and(SEMANTIC_TYPE_LABEL.CODE.eq(MEANING_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE))
								.and(SEMANTIC_TYPE_LABEL.LANG.eq(classifierLabelLang))
								.and(SEMANTIC_TYPE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.orderBy(MEANING_SEMANTIC_TYPE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public List<MeaningForum> getMeaningForums(Long meaningId) {

		return mainDb
				.selectFrom(MEANING_FORUM)
				.where(MEANING_FORUM.MEANING_ID.eq(meaningId))
				.orderBy(MEANING_FORUM.ORDER_BY.desc())
				.fetchInto(MeaningForum.class);
	}

	public List<eki.ekilex.data.Freeform> getLexemeFreeforms(Long lexemeId, String classifierLabelLang) {

		Freeform f = FREEFORM.as("f");
		LexemeFreeform lf = LEXEME_FREEFORM.as("lf");
		FreeformTypeLabel ftl = FREEFORM_TYPE_LABEL.as("ftl");
		FreeformSourceLink fsl = FREEFORM_SOURCE_LINK.as("fsl");
		Source s = SOURCE.as("s");

		Field<JSON> fslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(fsl.ID),
										DSL.key("name").value(fsl.NAME),
										DSL.key("sourceId").value(fsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(fsl.ORDER_BY)))
						.orderBy(fsl.ORDER_BY))
				.from(fsl, s)
				.where(
						fsl.FREEFORM_ID.eq(f.ID)
								.and(fsl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						f.ID,
						f.FREEFORM_TYPE_CODE,
						DSL.coalesce(ftl.VALUE, f.FREEFORM_TYPE_CODE).as("freeform_type_value"),
						f.VALUE,
						f.VALUE_PRESE,
						f.LANG,
						f.ORDER_BY,
						f.CREATED_BY,
						f.CREATED_ON,
						f.MODIFIED_BY,
						f.MODIFIED_ON,
						fslf.as("source_links"))
				.from(
						lf
								.innerJoin(f).on(f.ID.eq(lf.FREEFORM_ID))
								.leftOuterJoin(ftl).on(
										ftl.CODE.eq(f.FREEFORM_TYPE_CODE)
												.and(ftl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
												.and(ftl.LANG.eq(classifierLabelLang))))
				.where(lf.LEXEME_ID.eq(lexemeId))
				.orderBy(f.ORDER_BY)
				.fetchInto(eki.ekilex.data.Freeform.class);
	}

	public List<eki.ekilex.data.Grammar> getLexemeGrammars(Long lexemeId) {

		Grammar g = GRAMMAR.as("g");
		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_GRAMMAR, g.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_GRAMMAR, g.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_GRAMMAR, g.ID);

		return mainDb
				.select(
						g.ID,
						g.VALUE,
						g.VALUE_PRESE,
						g.LANG,
						g.CREATED_BY,
						g.CREATED_ON,
						g.MODIFIED_BY,
						g.MODIFIED_ON,
						g.ORDER_BY,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"))
				.from(g)
				.where(g.LEXEME_ID.eq(lexemeId))
				.orderBy(g.ORDER_BY)
				.fetchInto(eki.ekilex.data.Grammar.class);
	}

	public List<eki.ekilex.data.Government> getLexemeGovernments(Long lexemeId) {

		Government g = GOVERNMENT.as("g");
		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_GOVERNMENT, g.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_GOVERNMENT, g.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_GOVERNMENT, g.ID);

		return mainDb
				.select(
						g.ID,
						g.VALUE,
						g.CREATED_BY,
						g.CREATED_ON,
						g.MODIFIED_BY,
						g.MODIFIED_ON,
						g.ORDER_BY,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"))
				.from(g)
				.where(g.LEXEME_ID.eq(lexemeId))
				.orderBy(g.ORDER_BY)
				.fetchInto(eki.ekilex.data.Government.class);
	}

	public List<eki.ekilex.data.Usage> getUsages(Long lexemeId) {

		Usage u = USAGE.as("u");
		UsageTranslation ut = USAGE_TRANSLATION.as("ut");
		UsageDefinition ud = USAGE_DEFINITION.as("ud");
		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		Source s = SOURCE.as("s");

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_USAGE, u.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_USAGE, u.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_USAGE, u.ID);

		Field<JSON> utf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ut.ID),
										DSL.key("usageId").value(ut.USAGE_ID),
										DSL.key("value").value(ut.VALUE),
										DSL.key("valuePrese").value(ut.VALUE_PRESE),
										DSL.key("lang").value(ut.LANG),
										DSL.key("createdBy").value(ut.CREATED_BY),
										DSL.key("createdOn").value(ut.CREATED_ON),
										DSL.key("modifiedBy").value(ut.MODIFIED_BY),
										DSL.key("modifiedOn").value(ut.MODIFIED_ON),
										DSL.key("orderBy").value(ut.ORDER_BY)))
						.orderBy(ut.ORDER_BY))
				.from(ut)
				.where(ut.USAGE_ID.eq(u.ID))
				.asField();

		Field<JSON> udf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ud.ID),
										DSL.key("usageId").value(ud.USAGE_ID),
										DSL.key("value").value(ud.VALUE),
										DSL.key("valuePrese").value(ud.VALUE_PRESE),
										DSL.key("lang").value(ud.LANG),
										DSL.key("createdBy").value(ud.CREATED_BY),
										DSL.key("createdOn").value(ud.CREATED_ON),
										DSL.key("modifiedBy").value(ud.MODIFIED_BY),
										DSL.key("modifiedOn").value(ud.MODIFIED_ON),
										DSL.key("orderBy").value(ud.ORDER_BY)))
						.orderBy(ud.ORDER_BY))
				.from(ud)
				.where(ud.USAGE_ID.eq(u.ID))
				.asField();

		Field<JSON> uslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(usl.ID),
										DSL.key("name").value(usl.NAME),
										DSL.key("sourceId").value(usl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(usl.ORDER_BY)))
						.orderBy(usl.ORDER_BY))
				.from(usl, s)
				.where(
						usl.USAGE_ID.eq(u.ID)
								.and(usl.SOURCE_ID.eq(s.ID)))
				.asField();

		return mainDb
				.select(
						u.ID,
						u.VALUE,
						u.VALUE_PRESE,
						u.LANG,
						u.IS_PUBLIC,
						u.CREATED_BY,
						u.CREATED_ON,
						u.MODIFIED_BY,
						u.MODIFIED_BY,
						u.ORDER_BY,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"),
						utf.as("translations"),
						udf.as("definitions"),
						uslf.as("source_links"))
				.from(u)
				.where(u.LEXEME_ID.eq(lexemeId))
				.orderBy(u.ORDER_BY)
				.fetchInto(eki.ekilex.data.Usage.class);
	}

	public List<LexemeRelation> getLexemeRelations(Long lexemeId, String classifierLabelLang) {

		LexRelation r = LEX_RELATION.as("r");
		LexRelTypeLabel rtl = LEX_REL_TYPE_LABEL.as("rtl");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(w2.ID);
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(w2.ID);

		return mainDb
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
								.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID).and(w2.IS_PUBLIC.isTrue()))
								.leftOuterJoin(rtl).on(
										r.LEX_REL_TYPE_CODE.eq(rtl.CODE)
												.and(rtl.LANG.eq(classifierLabelLang)
														.and(rtl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))))
				.where(r.LEXEME1_ID.eq(lexemeId))
				.groupBy(r.ID, l2.ID, w2.ID, rtl.VALUE)
				.orderBy(r.ORDER_BY)
				.fetchInto(LexemeRelation.class);
	}

	public List<CollocMember> getCollocationMembers(Long lexemeId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");
		Word mw = WORD.as("mw");
		Lexeme ml = LEXEME.as("ml");
		Form mf = FORM.as("mf");

		return mainDb
				.select(
						cm.ID,
						cm.CONJUNCT,
						ml.ID.as("lexeme_id"),
						mw.ID.as("word_id"),
						mw.VALUE.as("word_value"),
						mf.ID.as("form_id"),
						mf.VALUE.as("form_value"),
						mf.MORPH_CODE,
						cm.WEIGHT,
						cm.MEMBER_ORDER)
				.from(mf, mw, ml, cm)
				.where(
						cm.COLLOC_LEXEME_ID.eq(lexemeId)
								.and(cm.MEMBER_LEXEME_ID.eq(ml.ID))
								.and(cm.MEMBER_FORM_ID.eq(mf.ID))
								.and(ml.WORD_ID.eq(mw.ID)))
				.orderBy(cm.MEMBER_ORDER)
				.fetchInto(CollocMember.class);
	}

	public List<String> getMeaningTags(Long meaningId) {

		return mainDb
				.select(MEANING_TAG.TAG_NAME)
				.from(MEANING_TAG)
				.where(MEANING_TAG.MEANING_ID.eq(meaningId))
				.orderBy(MEANING_TAG.CREATED_ON)
				.fetchInto(String.class);
	}

	public List<Classifier> getDatasetClassifiers(
			ClassifierName classifierName,
			String datasetCode,
			String classifierLabelLang) {

		String[] datasetCodes = {datasetCode};
		if (ClassifierName.LANGUAGE.equals(classifierName)) {

			Language cl = LANGUAGE.as("cl");
			LanguageLabel cll = LANGUAGE_LABEL.as("cll");

			Field<String> clvf = DSL
					.select(cll.VALUE)
					.from(cll)
					.where(
							cll.CODE.eq(cl.CODE)
									.and(cll.LANG.eq(classifierLabelLang))
									.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
					.limit(1)
					.asField();

			return mainDb
					.select(
							getClassifierNameField(ClassifierName.LANGUAGE).as("name"),
							cl.CODE,
							clvf.as("value"),
							cl.ORDER_BY)
					.from(cl)
					.where(cl.DATASETS.contains(datasetCodes))
					.fetchInto(Classifier.class);

		} else if (ClassifierName.DOMAIN.equals(classifierName)) {

			Domain cl = DOMAIN.as("d");
			DomainLabel cll = DOMAIN_LABEL.as("cll");

			Field<String> clvf = DSL
					.select(cll.VALUE)
					.from(cll)
					.where(
							cll.CODE.eq(cl.CODE)
									.and(cll.ORIGIN.eq(cl.ORIGIN))
									.and(cll.LANG.eq(classifierLabelLang))
									.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
					.limit(1)
					.asField();

			Field<String> clcf = DSL
					.select(cll.VALUE)
					.from(cll)
					.where(
							cll.CODE.eq(cl.CODE)
									.and(cll.ORIGIN.eq(cl.ORIGIN))
									.and(cll.LANG.eq(classifierLabelLang))
									.and(cll.TYPE.eq(CLASSIF_LABEL_TYPE_COMMENT)))
					.limit(1)
					.asField();

			return mainDb
					.select(
							getClassifierNameField(ClassifierName.DOMAIN).as("name"),
							cl.PARENT_ORIGIN,
							cl.PARENT_CODE,
							cl.ORIGIN,
							cl.CODE,
							clvf.as("value"),
							clcf.as("comment"))
					.from(cl)
					.where(cl.DATASETS.contains(datasetCodes))
					.orderBy(cl.ORIGIN, cl.ORDER_BY)
					.fetchInto(Classifier.class);
		}

		throw new UnsupportedOperationException();
	}

	private String getTableName(ClassifierName classifierName) {
		return classifierName.name().toLowerCase();
	}

	private String getLabelTableName(ClassifierName classifierName) {
		return getTableName(classifierName) + "_label";
	}

	private Field<String> getClassifierNameField(ClassifierName classifierName) {
		return DSL.field(DSL.value(classifierName.name()));
	}
}