package eki.ekilex.runner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleLogType;
import eki.common.constant.ReferenceType;
import eki.common.constant.TableName;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.XmlReader;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractLoaderRunner implements InitializingBean, SystemConstant, TableName {

	private static final String SQL_SELECT_WORD_MAX_HOMONYM = "sql/select_word_max_homonym.sql";

	private static final String SQL_SELECT_WORD_BY_FORM_AND_HOMONYM = "sql/select_word_by_form_and_homonym.sql";

	private static final String SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE = "sql/select_lexeme_freeform_by_type_and_value.sql";

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

	@Autowired
	protected XmlReader xmlReader;

	@Autowired
	protected BasicDbService basicDbService;

	private String sqlSelectWordByFormAndHomonym;

	private String sqlSelectWordMaxHomonym;

	private String sqlSelectLexemeFreeform;

	abstract void initialise() throws Exception;

	@Override
	public void afterPropertiesSet() throws Exception {

		initialise();

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_MAX_HOMONYM);
		sqlSelectWordMaxHomonym = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_FORM_AND_HOMONYM);
		sqlSelectWordByFormAndHomonym = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE);
		sqlSelectLexemeFreeform = getContent(resourceFileInputStream);
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
		Locale locale = new Locale(lang);
		lang = locale.getISO3Language();
		return lang;
	}

	protected Long saveWord(Word word, List<Paradigm> paradigms, String dataset, Count wordDuplicateCount) throws Exception {

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

		Map<String, Object> tableRowValueMap = getWord(wordValue, homonymNr, wordLang);
		Long wordId;

		if (tableRowValueMap == null) {
			wordId = createWord(wordMorphCode, homonymNr, wordLang, wordDisplayMorph, genderCode);
			if (StringUtils.isNotBlank(dataset) && StringUtils.isNotBlank(guid)) {
				createWordGuid(wordId, dataset, guid);
			}
			if (CollectionUtils.isEmpty(paradigms)) {
				Long paradigmId = createParadigm(wordId, null, false);
				createForm(wordValue, wordComponents, wordDisplayForm, wordVocalForm, wordMorphCode, paradigmId, true);
			}
		} else {
			wordId = (Long) tableRowValueMap.get("id");
			if (wordDuplicateCount != null) {
				wordDuplicateCount.increment();
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

	protected Map<String, Object> getWord(String word, int homonymNr, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("homonymNr", homonymNr);
		tableRowParamMap.put("lang", lang);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordByFormAndHomonym, tableRowParamMap);
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

	private Long createWord(final String morphCode, final int homonymNr, String lang, String displayMorph, String genderCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		tableRowParamMap.put("display_morph_code", displayMorph);
		tableRowParamMap.put("gender_code", genderCode);
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
		String meaningStateCode = meaning.getMeaningStateCode();
		if (StringUtils.isNotBlank(meaningStateCode)) {
			tableRowParamMap.put("state_code", meaningStateCode);
		}
		String meaningTypeCode = meaning.getMeaningTypeCode();
		if (StringUtils.isNotBlank(meaningTypeCode)) {
			tableRowParamMap.put("type_code", meaningTypeCode);
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
		String lexemeType = lexeme.getType();

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
			if (StringUtils.isNotBlank(lexemeType)) {
				valueParamMap.put("type_code", lexemeType);
			}
			if (MapUtils.isNotEmpty(valueParamMap)) {
				basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
			}
		}
		return lexemeId;
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

	protected Long createDefinitionRefLink(Long definitionId, ReferenceType refType, Long refId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("definition_id", definitionId);
		tableRowParamMap.put("ref_type", refType.name());
		tableRowParamMap.put("ref_id", refId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long refLinkId = basicDbService.create(DEFINITION_REF_LINK, tableRowParamMap);
		return refLinkId;
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

	protected Long createFreeformRefLink(Long freeformId, ReferenceType refType, Long refId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("freeform_id", freeformId);
		tableRowParamMap.put("ref_type", refType.name());
		tableRowParamMap.put("ref_id", refId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long refLinkId = basicDbService.create(FREEFORM_REF_LINK, tableRowParamMap);
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

	protected Long createSourceFreeform(Long sourceId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("source_id", sourceId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(SOURCE_FREEFORM, tableRowParamMap);

		return freeformId;
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

	protected Long createOrSelectPerson(String name) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		return basicDbService.createOrSelect(PERSON, params);
	}

	private List<String> readFileLines(String sourcePath) throws Exception {
		try (InputStream resourceInputStream = new FileInputStream(sourcePath)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

}
