package eki.ekilex.runner;

import static java.util.stream.Collectors.toMap;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleLogType;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.constant.TableName;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Source;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.XmlReader;

public abstract class AbstractLoaderRunner implements InitializingBean, SystemConstant, TableName {

	private static Logger logger = LoggerFactory.getLogger(AbstractLoaderRunner.class);

	abstract String getDataset();

	private static final String SQL_SELECT_WORD_BY_FORM_AND_HOMONYM = "sql/select_word_by_form_and_homonym.sql";

	private static final String SQL_SELECT_WORD_BY_DATASET_AND_GUID = "sql/select_word_by_dataset_and_guid.sql";

	private static final String SQL_SELECT_WORD_MAX_HOMONYM = "sql/select_word_max_homonym.sql";

	private static final String SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE = "sql/select_lexeme_freeform_by_type_and_value.sql";

	private static final String SQL_SELECT_SOURCE_BY_TYPE_AND_NAME = "sql/select_source_by_type_and_name.sql";

	private static final String CLASSIFIERS_MAPPING_FILE_PATH = "./fileresources/csv/classifier-main-map.csv";

	protected static final String EKI_CLASSIFIER_STAATUS = "staatus";
	protected static final String EKI_CLASSIFIER_MÕISTETÜÜP = "mõistetüüp";
	protected static final String EKI_CLASSIFIER_KEELENDITÜÜP = "keelenditüüp";
	protected static final String EKI_CLASSIFIER_LIIKTYYP = "liik_tyyp";
	protected static final String EKI_CLASSIFIER_DKTYYP = "dk_tyyp";
	protected static final String EKI_CLASSIFIER_SLTYYP = "sl_tyyp";
	protected static final String EKI_CLASSIFIER_ASTYYP = "as_tyyp";
	protected static final String EKI_CLASSIFIER_VKTYYP = "vk_tyyp";
	protected static final String EKI_CLASSIFIER_MSAGTYYP = "msag_tyyp";
	protected static final String EKI_CLASSIFIER_STYYP = "s_tyyp";
	protected static final String EKI_CLASSIFIER_ENTRY_CLASS = "entry class";

	@Autowired
	protected XmlReader xmlReader;

	@Autowired
	protected BasicDbService basicDbService;

	private String sqlSelectWordByFormAndHomonym;

	private String sqlSelectWordByDatasetAndGuid;

	private String sqlSelectWordMaxHomonym;

	private String sqlSelectLexemeFreeform;

	private String sqlSourceByTypeAndName;

	abstract void initialise() throws Exception;

	@Override
	public void afterPropertiesSet() throws Exception {

		initialise();

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_FORM_AND_HOMONYM);
		sqlSelectWordByFormAndHomonym = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_DATASET_AND_GUID);
		sqlSelectWordByDatasetAndGuid = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_MAX_HOMONYM);
		sqlSelectWordMaxHomonym = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE);
		sqlSelectLexemeFreeform = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_SOURCE_BY_TYPE_AND_NAME);
		sqlSourceByTypeAndName = getContent(resourceFileInputStream);
	}

	protected String getContent(InputStream resourceInputStream) throws Exception {
		String content = IOUtils.toString(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return content;
	}

	protected boolean isLang(String lang) {
		Locale locale = new Locale(lang);
		String displayName = locale.getDisplayName();
		boolean isLang = !StringUtils.equalsIgnoreCase(lang, displayName);
		return isLang;
	}

	protected String unifyLang(String lang) {
		if (StringUtils.isBlank(lang)) {
			return null;
		}
		Locale locale = new Locale(lang);
		lang = locale.getISO3Language();
		return lang;
	}

	protected Long createOrSelectWord(Word word, List<Paradigm> paradigms, String dataset, Count reusedWordCount) throws Exception {

		String wordValue = word.getValue();
		String wordLang = word.getLang();
		String[] wordComponents = word.getComponents();
		String wordDisplayForm = word.getDisplayForm();
		String wordVocalForm = word.getVocalForm();
		int homonymNr = word.getHomonymNr();
		String wordMorphCode = word.getMorphCode();
		String wordDisplayMorph = word.getDisplayMorph();
		String guid = word.getGuid();
		String genderCode = word.getGenderCode();
		String typeCode = word.getWordTypeCode();

		Map<String, Object> tableRowValueMap = getWord(wordValue, homonymNr, wordLang);
		Long wordId;

		if (tableRowValueMap == null) {
			wordId = createWord(wordMorphCode, homonymNr, wordLang, wordDisplayMorph, genderCode, typeCode);
			if (StringUtils.isNotBlank(dataset) && StringUtils.isNotBlank(guid)) {
				createWordGuid(wordId, dataset, guid);
			}
			if (CollectionUtils.isEmpty(paradigms)) {
				Long paradigmId = createParadigm(wordId, null, false);
				createForm(wordValue, wordComponents, wordDisplayForm, wordVocalForm, wordMorphCode, paradigmId, true);
			}
		} else {
			wordId = (Long) tableRowValueMap.get("id");
			if (reusedWordCount != null) {
				reusedWordCount.increment();
			}
		}
		word.setId(wordId);
		if (CollectionUtils.isNotEmpty(paradigms)) {
			for (Paradigm paradigm : paradigms) {
				Long paradigmId = createParadigm(wordId, paradigm.getInflectionTypeNr(), paradigm.isSecondary());
				// mab forms
				List<Form> forms = paradigm.getForms();
				if (CollectionUtils.isEmpty(forms)) {
					createForm(wordValue, wordComponents, wordDisplayForm, wordVocalForm, wordMorphCode, paradigmId, true);
				} else {
					for (Form form : forms) {
						if (form.isWord()) {
							createForm(wordValue, null, wordDisplayForm, wordVocalForm, form.getMorphCode(), paradigmId, form.isWord());
						} else {
							createForm(form.getValue(), null, form.getDisplayForm(), null, form.getMorphCode(), paradigmId, form.isWord());
						}
					}					
				}
			}
		}
		return wordId;
	}

	protected Long createOrSelectWord(
			Word word, List<Paradigm> paradigms, String dataset, Map<String, List<Guid>> ssGuidMap,
			Count ssWordCount, Count reusedWordCount) throws Exception {

		if (MapUtils.isEmpty(ssGuidMap)) {
			return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
		}

		String wordValue = word.getValue();
		String guid = word.getGuid();

		List<Guid> mappedGuids = ssGuidMap.get(guid);
		if (CollectionUtils.isEmpty(mappedGuids)) {
			return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
		}

		for (Guid ssGuidObj : mappedGuids) {

			String ssWordValue = ssGuidObj.getWord();
			String ssGuid = ssGuidObj.getValue();
			String ssDataset = "ss1";
	
			if (StringUtils.equalsIgnoreCase(wordValue, ssWordValue)) {
				Map<String, Object> tableRowValueMap = getWord(wordValue, ssGuid, ssDataset);
				if (tableRowValueMap == null) {
					return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
				}
				ssWordCount.increment();
				Long wordId = (Long) tableRowValueMap.get("id");
				word.setId(wordId);
				return wordId;
			}
		}
		List<String> mappedWordValues = mappedGuids.stream().map(Guid::getWord).collect(Collectors.toList());
		logger.debug("Word value doesn't match guid mapping(s): \"{}\" / \"{}\"", wordValue, mappedWordValues);

		return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
	}

	private Map<String, Object> getWord(String word, int homonymNr, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("homonymNr", homonymNr);
		tableRowParamMap.put("lang", lang);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordByFormAndHomonym, tableRowParamMap);
		return tableRowValueMap;
	}

	private Map<String, Object> getWord(String word, String guid, String dataset) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("guid", guid);
		tableRowParamMap.put("dataset", dataset);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordByDatasetAndGuid, tableRowParamMap);
		return tableRowValueMap;
	}

	private void createForm(String form, String[] wordComponents, String wordDisplayForm, String wordVocalForm, String morphCode, Long paradigmId, boolean isWord) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("paradigm_id", paradigmId);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("value", form);
		if (wordComponents != null) {
			tableRowParamMap.put("components", new PgVarcharArray(wordComponents));
		}
		if (StringUtils.isNotBlank(wordDisplayForm)) {
			tableRowParamMap.put("display_form", wordDisplayForm);
		}
		if (StringUtils.isNotBlank(wordVocalForm)) {
			tableRowParamMap.put("vocal_form", wordVocalForm);
		}
		tableRowParamMap.put("is_word", isWord);
		basicDbService.create(FORM, tableRowParamMap);
	}

	private Long createParadigm(Long wordId, String inflectionTypeNr, boolean isSecondary) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("is_secondary", isSecondary);
		if (inflectionTypeNr != null) {
			tableRowParamMap.put("inflection_type_nr", inflectionTypeNr);
		}
		Long paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);
		return paradigmId;
	}

	private Long createWord(final String morphCode, final int homonymNr, String lang, String displayMorph, String genderCode, String typeCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		tableRowParamMap.put("display_morph_code", displayMorph);
		tableRowParamMap.put("gender_code", genderCode);
		tableRowParamMap.put("type_code", typeCode);
		Long wordId = basicDbService.create(WORD, tableRowParamMap);
		return wordId;
	}

	private void createWordGuid(Long wordId, String dataset, String guid) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("dataset_code", dataset);
		tableRowParamMap.put("guid", guid);
		basicDbService.create(WORD_GUID, tableRowParamMap);
	}

	protected int getWordMaxHomonymNr(String word, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("lang", lang);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordMaxHomonym, tableRowParamMap);
		int homonymNr = (int) tableRowValueMap.get("max_homonym_nr");
		return homonymNr;
	}

	protected Long createMeaning(Meaning meaning) throws Exception {

		Map<String, Object> tableRowParamMap;

		tableRowParamMap = new HashMap<>();
		Timestamp createdOn = meaning.getCreatedOn();
		if (createdOn != null) {
			tableRowParamMap.put("created_on", createdOn);
		}
		String createdBy = meaning.getCreatedBy();
		if (StringUtils.isNotBlank(createdBy)) {
			tableRowParamMap.put("created_by", createdBy);
		}
		Timestamp modifiedOn = meaning.getModifiedOn();
		if (modifiedOn != null) {
			tableRowParamMap.put("modified_on", modifiedOn);
		}
		String modifiedBy = meaning.getModifiedBy();
		if (StringUtils.isNotBlank(modifiedBy)) {
			tableRowParamMap.put("modified_by", modifiedBy);
		}
		String processStateCode = meaning.getProcessStateCode();
		if (StringUtils.isNotBlank(processStateCode)) {
			tableRowParamMap.put("process_state_code", processStateCode);
		}
		Long meaningId;
		if (MapUtils.isEmpty(tableRowParamMap)) {
			meaningId = basicDbService.create(MEANING);
		} else {
			meaningId = basicDbService.create(MEANING, tableRowParamMap);
		}
		meaning.setMeaningId(meaningId);
		return meaningId;
	}

	protected Long createMeaning() throws Exception {

		Long meaningId = basicDbService.create(MEANING);
		return meaningId;
	}

	protected void createMeaningDomain(Long meaningId, String domainCode, String domainOrigin) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("domain_code", domainCode);
		tableRowParamMap.put("domain_origin", domainOrigin);
		basicDbService.create(MEANING_DOMAIN, tableRowParamMap);
	}

	protected Long createDefinition(Long meaningId, String definition, String lang, String dataset) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("value", definition);
		tableRowParamMap.put("lang", lang);
		Long definitionId = basicDbService.create(DEFINITION, tableRowParamMap);
		if (definitionId != null) {
			tableRowParamMap.clear();
			tableRowParamMap.put("definition_id", definitionId);
			tableRowParamMap.put("dataset_code", dataset);
			basicDbService.createWithoutId(DEFINITION_DATASET, tableRowParamMap);
		}
		return definitionId;
	}

	protected void updateDefinitionValue(Long definitionId, String value) throws Exception {

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", definitionId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("value", value);
		basicDbService.update(DEFINITION, criteriaParamMap, valueParamMap);
	}

	protected Long createLexeme(Lexeme lexeme, String dataset) throws Exception {

		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();
		String createdBy = lexeme.getCreatedBy();
		Timestamp createdOn = lexeme.getCreatedOn();
		String modifiedBy = lexeme.getModifiedBy();
		Timestamp modifiedOn = lexeme.getModifiedOn();
		Integer lexemeLevel1 = lexeme.getLevel1();
		Integer lexemeLevel2 = lexeme.getLevel2();
		Integer lexemeLevel3 = lexeme.getLevel3();
		String frequencyGroup = lexeme.getFrequencyGroup();
		String valueState = lexeme.getValueState();

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", wordId);
		criteriaParamMap.put("meaning_id", meaningId);
		criteriaParamMap.put("dataset_code", dataset);
		Long lexemeId = basicDbService.createIfNotExists(LEXEME, criteriaParamMap);
		lexeme.setLexemeId(lexemeId);
		if (lexemeId != null) {
			criteriaParamMap.clear();
			criteriaParamMap.put("id", lexemeId);
			Map<String, Object> valueParamMap = new HashMap<>();
			if (StringUtils.isNotBlank(createdBy)) {
				valueParamMap.put("created_by", createdBy);
			}
			if (createdOn != null) {
				valueParamMap.put("created_on", createdOn);
			}
			if (StringUtils.isNotBlank(modifiedBy)) {
				valueParamMap.put("modified_by", modifiedBy);
			}
			if (modifiedOn != null) {
				valueParamMap.put("modified_on", modifiedOn);
			}
			if (lexemeLevel1 != null) {
				valueParamMap.put("level1", lexemeLevel1);
			}
			if (lexemeLevel2 != null) {
				valueParamMap.put("level2", lexemeLevel2);
			}
			if (lexemeLevel3 != null) {
				valueParamMap.put("level3", lexemeLevel3);
			}
			if (StringUtils.isNotBlank(frequencyGroup)) {
				valueParamMap.put("frequency_group", frequencyGroup);
			}
			if (StringUtils.isNotBlank(valueState)) {
				valueParamMap.put("value_state_code", valueState);
			}
			if (MapUtils.isNotEmpty(valueParamMap)) {
				basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
			}
		}
		return lexemeId;
	}

	protected void createUsages(Long lexemeId, List<Usage> usages, String dataLang) throws Exception {

		if (CollectionUtils.isEmpty(usages)) {
			return;
		}

		for (Usage usage : usages) {
			String usageValue = usage.getValue();
			String usageType = usage.getUsageType();
			String author = usage.getAuthor();
			String authorTypeStr = usage.getAuthorType();
			String extSourceId = usage.getExtSourceId();
			Long usageId = createLexemeFreeform(lexemeId, FreeformType.USAGE, usageValue, dataLang);
			if (StringUtils.isNotBlank(usageType)) {
				createFreeformClassifier(FreeformType.USAGE_TYPE, usageId, usageType);
			}
			if (StringUtils.isBlank(extSourceId)) {
				extSourceId = "n/a";
			}
			if (StringUtils.isNotBlank(author)) {
				Long authorId = getSource(SourceType.PERSON, extSourceId, author);
				if (authorId == null) {
					authorId = createSource(SourceType.PERSON, extSourceId, author);
				}
				ReferenceType referenceType;
				if (StringUtils.isEmpty(authorTypeStr)) {
					referenceType = ReferenceType.AUTHOR;
				} else {
					referenceType = ReferenceType.TRANSLATOR;
				}
				createFreeformSourceLink(usageId, referenceType, authorId, null, null);
			}
			if (CollectionUtils.isNotEmpty(usage.getDefinitions())) {
				for (String usageDefinition : usage.getDefinitions()) {
					createFreeformTextOrDate(FreeformType.USAGE_DEFINITION, usageId, usageDefinition, dataLang);
				}
			}
			if (CollectionUtils.isNotEmpty(usage.getUsageTranslations())) {
				for (UsageTranslation usageTranslation : usage.getUsageTranslations()) {
					String usageTranslationValue = usageTranslation.getValue();
					String usageTranslationLang = usageTranslation.getLang();
					createFreeformTextOrDate(FreeformType.USAGE_TRANSLATION, usageId, usageTranslationValue, usageTranslationLang);
				}
			}
		}
	}

	protected Long createLexemeFreeform(Long lexemeId, FreeformType freeformType, Object value, String lang) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, lang);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(LEXEME_FREEFORM, tableRowParamMap);

		return freeformId;
	}

	protected Long createOrSelectLexemeFreeform(Long lexemeId, FreeformType freeformType, String freeformValue) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("value", freeformValue);
		tableRowParamMap.put("type", freeformType.name());
		Map<String, Object> freeform = basicDbService.queryForMap(sqlSelectLexemeFreeform, tableRowParamMap);
		Long freeformId;
		if (freeform == null) {
			freeformId = createLexemeFreeform(lexemeId, freeformType, freeformValue, null);
		} else {
			freeformId = (Long) freeform.get("id");
		}
		return freeformId;
	}

	protected Long createMeaningFreeform(Long meaningId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(MEANING_FREEFORM, tableRowParamMap);

		return freeformId;
	}

	protected Long createDefinitionFreeform(Long definitionId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("definition_id", definitionId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(DEFINITION_FREEFORM, tableRowParamMap);

		return freeformId;
	}

	protected Long createFreeformTextOrDate(FreeformType freeformType, Long parentId, Object value, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", freeformType.name());
		if (parentId != null) {
			tableRowParamMap.put("parent_id", parentId);
		}
		if (value != null) {
			if (value instanceof String) {
				tableRowParamMap.put("value_text", value);
			} else if (value instanceof Timestamp) {
				tableRowParamMap.put("value_date", value);
			} else {
				throw new Exception("Not yet supported freeform data type " + value);
			}
		}
		if (StringUtils.isNotBlank(lang)) {
			tableRowParamMap.put("lang", lang);
		}
		Long freeformId = basicDbService.create(FREEFORM, tableRowParamMap);
		return freeformId;
	}

	protected Long createFreeformClassifier(FreeformType freeformType, Long parentId, String classifierCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", freeformType.name());
		if (parentId != null) {
			tableRowParamMap.put("parent_id", parentId);
		}
		tableRowParamMap.put("classif_code", classifierCode);
		return basicDbService.create(FREEFORM, tableRowParamMap);
	}

	protected void updateFreeformText(Long freeformId, String value) throws Exception {

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", freeformId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("value_text", value);
		basicDbService.update(FREEFORM, criteriaParamMap, valueParamMap);
	}

	protected Long createFreeformSourceLink(Long freeformId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("freeform_id", freeformId);
		tableRowParamMap.put("type", refType.name());
		tableRowParamMap.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long refLinkId = basicDbService.create(FREEFORM_SOURCE_LINK, tableRowParamMap);
		return refLinkId;
	}

	protected Long createLexemeSourceLink(Long lexemeId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("type", refType.name());
		tableRowParamMap.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long refLinkId = basicDbService.create(LEXEME_SOURCE_LINK, tableRowParamMap);
		return refLinkId;
	}

	protected Long createDefinitionSourceLink(Long definitionId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("definition_id", definitionId);
		tableRowParamMap.put("type", refType.name());
		tableRowParamMap.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long refLinkId = basicDbService.create(DEFINITION_SOURCE_LINK, tableRowParamMap);
		return refLinkId;
	}

	protected void createLifecycleLog(Long ownerId, String ownerName, LifecycleLogType type, String eventBy, Timestamp eventOn) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("owner_id", ownerId);
		tableRowParamMap.put("owner_name", ownerName);
		tableRowParamMap.put("type", type.name());
		tableRowParamMap.put("event_by", eventBy);
		tableRowParamMap.put("event_on", eventOn);
		basicDbService.create(LIFECYCLE_LOG, tableRowParamMap);
	}

	protected void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("lexeme1_id", lexemeId1);
		relationParams.put("lexeme2_id", lexemeId2);
		relationParams.put("lex_rel_type_code", relationType);
		basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);
	}

	protected void createWordRelation(Long wordId1, Long wordId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("word1_id", wordId1);
		relationParams.put("word2_id", wordId2);
		relationParams.put("word_rel_type_code", relationType);
		basicDbService.createIfNotExists(WORD_RELATION, relationParams);
	}

	protected void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("meaning1_id", meaningId1);
		relationParams.put("meaning2_id", meaningId2);
		relationParams.put("meaning_rel_type_code", relationType);
		basicDbService.createIfNotExists(MEANING_RELATION, relationParams);
	}

	protected void createLexemeRegister(Long lexemeId, String registerCode) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("lexeme_id", lexemeId);
		params.put("register_code", registerCode);
		basicDbService.createIfNotExists(LEXEME_REGISTER, params);
	}

	protected Long createSource(Source source) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		String concept = source.getExtSourceId();
		SourceType type = source.getType();
		tableRowParamMap.put("ext_source_id", concept);
		tableRowParamMap.put("type", type.name());
		Timestamp createdOn = source.getCreatedOn();
		if (createdOn != null) {
			tableRowParamMap.put("created_on", createdOn);
		}
		String createdBy = source.getCreatedBy();
		if (StringUtils.isNotBlank(createdBy)) {
			tableRowParamMap.put("created_by", createdBy);
		}
		Timestamp modifiedOn = source.getModifiedOn();
		if (modifiedOn != null) {
			tableRowParamMap.put("modified_on", modifiedOn);
		}
		String modifiedBy = source.getModifiedBy();
		if (StringUtils.isNotBlank(modifiedBy)) {
			tableRowParamMap.put("modified_by", modifiedBy);
		}
		String processStateCode = source.getProcessStateCode();
		if (StringUtils.isNotBlank(processStateCode)) {
			tableRowParamMap.put("process_state_code", processStateCode);
		}
		Long sourceId = basicDbService.create(SOURCE, tableRowParamMap);
		source.setSourceId(sourceId);
		return sourceId;
	}

	protected Long createSource(SourceType sourceType, String extSourceId, String sourceName) throws Exception {

		Source source = new Source();
		source.setType(sourceType);
		source.setExtSourceId(extSourceId);
		Long sourceId = createSource(source);
		createSourceFreeform(sourceId, FreeformType.SOURCE_NAME, sourceName);

		return sourceId;
	}

	protected Long createSourceFreeform(Long sourceId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("source_id", sourceId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(SOURCE_FREEFORM, tableRowParamMap);

		return freeformId;
	}

	protected Long getSource(SourceType sourceType, String extSourceId, String sourceName) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("sourceType", sourceType.name());
		tableRowParamMap.put("extSourceId", extSourceId);
		tableRowParamMap.put("sourcePropertyTypeName", FreeformType.SOURCE_NAME.name());
		tableRowParamMap.put("sourceName", sourceName);
		List<Map<String, Object>> sources = basicDbService.queryList(sqlSourceByTypeAndName, tableRowParamMap);

		if (CollectionUtils.isEmpty(sources)) {
			return null;
		}
		Map<String, Object> sourceRecord = sources.get(0);
		Long sourceId = (Long) sourceRecord.get("id");
		return sourceId;
	}

	protected Map<String, String> loadClassifierMappingsFor(String ekiClassifierName) throws Exception {
		return loadClassifierMappingsFor(ekiClassifierName, null);
	}

	protected Map<String, String> loadClassifierMappingsFor(String ekiClassifierName, String lexClassifierName) throws Exception {
		// in case of duplicate keys, last value is used
		return readFileLines(CLASSIFIERS_MAPPING_FILE_PATH).stream()
				.filter(line -> line.startsWith(ekiClassifierName))
				.map(line -> StringUtils.split(line, CSV_SEPARATOR))
				.filter(cells -> lexClassifierName == null || StringUtils.equalsIgnoreCase(lexClassifierName, cells[5]))
				.filter(cells -> "et".equals(cells[4]))
				.filter(cells -> !"-".equals(cells[5]))
				.collect(toMap(cells -> cells[2], cells -> cells[6], (c1, c2) -> c2));
	}

	protected List<String> readFileLines(String sourcePath) throws Exception {
		try (InputStream resourceInputStream = new FileInputStream(sourcePath)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

}
