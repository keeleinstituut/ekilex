package eki.ekilex.runner;

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

public abstract class AbstractLoaderRunner implements InitializingBean, SystemConstant, TableName {

	private static final String SQL_SELECT_WORD_MAX_HOMONYM = "sql/select_word_max_homonym.sql";

	private static final String SQL_SELECT_WORD_BY_FORM_AND_HOMONYM = "sql/select_word_by_form_and_homonym.sql";

	private static final String SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE = "sql/select_lexeme_freeform_by_type_and_value.sql";

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

	protected Long saveWord(Word word, List<Paradigm> paradigms, Count wordDuplicateCount) throws Exception {

		String wordValue = word.getValue();
		String wordLang = word.getLang();
		String[] wordComponents = word.getComponents();
		String wordDisplayForm = word.getDisplayForm();
		String wordVocalForm = word.getVocalForm();
		int homonymNr = word.getHomonymNr();
		String wordMorphCode = word.getMorphCode();
		String wordDisplayMorph = word.getDisplayMorph();

		Map<String, Object> tableRowValueMap = getWord(wordValue, homonymNr, wordLang);
		Long wordId;

		if (tableRowValueMap == null) {
			wordId = createWord(wordMorphCode, homonymNr, wordLang, wordDisplayMorph);
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
				if (CollectionUtils.isNotEmpty(forms)) {
					for (Form form : forms) {
						if (form.isWord()) {
							createForm(form.getValue(), null, wordDisplayForm, wordVocalForm, form.getMorphCode(), paradigmId, form.isWord());
						} else {
							createForm(form.getValue(), null, null, null, form.getMorphCode(), paradigmId, form.isWord());
						}
					}
				} else {
					createForm(wordValue, wordComponents, wordDisplayForm, wordVocalForm, wordMorphCode, paradigmId, true);
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

	private Long createWord(final String morphCode, final int homonymNr, String lang, String displayMorph) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		tableRowParamMap.put("display_morph_code", displayMorph);
		Long wordId = basicDbService.create(WORD, tableRowParamMap);
		return wordId;
	}

	protected int getWordMaxHomonymNr(String word, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("lang", lang);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordMaxHomonym, tableRowParamMap);
		int homonymNr = (int) tableRowValueMap.get("max_homonym_nr");
		return homonymNr;
	}

	protected Long createMeaning(Meaning meaning, String dataset) throws Exception {

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
		String entryClassCode = meaning.getEntryClassCode();
		if (StringUtils.isNotBlank(entryClassCode)) {
			tableRowParamMap.put("entry_class_code", entryClassCode);
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
		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("dataset_code", dataset);
		basicDbService.createWithoutId(MEANING_DATASET, tableRowParamMap);
		return meaningId;
	}

	protected Long createMeaning(String dataset) throws Exception {

		Long meaningId = basicDbService.create(MEANING);
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("dataset_code", dataset);
		basicDbService.createWithoutId(MEANING_DATASET, tableRowParamMap);
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
			criteriaParamMap.clear();
			criteriaParamMap.put("lexeme_id", lexemeId);
			criteriaParamMap.put("dataset_code", dataset);
			basicDbService.createWithoutId(LEXEME_DATASET, criteriaParamMap);
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

	protected void createMeaningFreeform(Long meaningId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(MEANING_FREEFORM, tableRowParamMap);
	}

	protected void createDefinitionFreeform(Long definitionId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("definition_id", definitionId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(DEFINITION_FREEFORM, tableRowParamMap);
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

	protected void createLifecycleLog(Long ownerId, String ownerName, LifecycleLogType type, String eventBy, Timestamp eventOn) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("owner_id", ownerId);
		tableRowParamMap.put("owner_name", ownerName);
		tableRowParamMap.put("type", type.name());
		tableRowParamMap.put("event_by", eventBy);
		tableRowParamMap.put("event_on", eventOn);

		basicDbService.create(LIFECYCLE_LOG, tableRowParamMap);
	}

	protected void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType, String dataset) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("lexeme1_id", lexemeId1);
		relationParams.put("lexeme2_id", lexemeId2);
		relationParams.put("lex_rel_type_code", relationType);
		Long relationId = basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);
		if (relationId != null) {
			relationParams.clear();
			relationParams.put("lex_relation_id", relationId);
			relationParams.put("dataset_code", dataset);
			basicDbService.createWithoutId(LEX_RELATION_DATASET, relationParams);
		}
	}

	protected void createWordRelation(Long wordId1, Long wordId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("word1_id", wordId1);
		relationParams.put("word2_id", wordId2);
		relationParams.put("word_rel_type_code", relationType);
		basicDbService.createIfNotExists(WORD_RELATION, relationParams);
	}

}
