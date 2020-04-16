package eki.ekilex.runner;

import static java.util.stream.Collectors.toMap;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.LexemeType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.constant.WordRelationGroupType;
import eki.common.data.CodeValue;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Mnr;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.RelationPart;
import eki.ekilex.data.transform.Source;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

public abstract class AbstractLoaderRunner extends AbstractLifecycleLogger implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(AbstractLoaderRunner.class);

	@Autowired
	protected UnifiedLoaderQueries sqls;

	@Autowired
	private TextDecorationService textDecorationService;

	abstract String getDataset();
	abstract Complexity getLexemeComplexity();
	abstract Complexity getDefinitionComplexity();
	abstract Complexity getFreeformComplexity();
	abstract void deleteDatasetData() throws Exception;
	abstract void initialise() throws Exception;

	private ReportComposer reportComposer;
	protected boolean doReports;

	private static final String REPORT_GUID_MISMATCH = "guid_mismatch";
	private static final String REPORT_GUID_MAPPING_MISSING = "guid_mapping_missing";

	protected static final String DOMAIN_ORIGIN_EKI_GENERIC = "eki üld";
	protected static final String GUID_OWNER_DATASET_CODE = "ss1";
	protected static final String COLLOC_OWNER_DATASET_CODE = "kol";
	protected static final String ETYMOLOGY_OWNER_DATASET_CODE = "ety";
	protected static final String DEFAULT_WORD_MORPH_CODE = "??";
	protected static final String FORM_COMPONENT_SEPARATOR = "+";
	protected static final String PROPRIETARY_AFIXOID_SYMBOL = "+";
	protected static final String UNIFIED_AFIXOID_SYMBOL = "-";
	protected static final String PREFIXOID_WORD_TYPE_CODE = "pf";
	protected static final String SUFFIXOID_WORD_TYPE_CODE = "sf";
	protected static final String DEFAULT_DEFINITION_TYPE_CODE = "määramata";
	protected static final String DEFAULT_PROCESS_STATE_CODE = "avalik";

	private static final String CLASSIFIERS_MAPPING_FILE_PATH = "./fileresources/csv/classifier-main-map.csv";

	protected static final String EXT_SOURCE_ID_NA = "n/a";
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
	protected static final String EKI_CLASSIFIER_ETYMPLTYYP = "etympl_tyyp";
	protected static final String EKI_CLASSIIFER_ETYMKEELTYYP = "etymkeel_tyyp";
	protected static final String EKI_CLASSIFIER_ENTRY_CLASS = "entry class";
	protected static final String EKI_CLASSIFIER_VALMIDUS = "valmidus";

	private List<String> afixoidWordTypeCodes;

	private long t1;
	private long t2;

	@Override
	public void afterPropertiesSet() throws Exception {

		initialise();

		afixoidWordTypeCodes = new ArrayList<>();
		afixoidWordTypeCodes.add(PREFIXOID_WORD_TYPE_CODE);
		afixoidWordTypeCodes.add(SUFFIXOID_WORD_TYPE_CODE);
	}

	@Override
	protected String getLogEventBy() {
		return "Ekilex " + getDataset() + "-laadur";
	}

	protected void start() throws Exception {
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " unified loader", REPORT_GUID_MISMATCH, REPORT_GUID_MAPPING_MISSING);
		}
		logger.debug("Loading \"{}\" ...", getDataset());
		t1 = System.currentTimeMillis();
	}

	protected void end() throws Exception {
		deleteFloatingData();
		t2 = System.currentTimeMillis();
		long timeMillis = t2 - t1;
		String timeLog = toReadableFormat(timeMillis);
		logger.debug("Done loading \"{}\" in {}", getDataset(), timeLog);
		if (reportComposer != null) {
			reportComposer.end();
		}
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

	protected String unifyAfixoids(String word) {
		boolean isPrefixoid = StringUtils.endsWith(word, PROPRIETARY_AFIXOID_SYMBOL);
		boolean isSuffixoid = StringUtils.startsWith(word, PROPRIETARY_AFIXOID_SYMBOL);
		if (isPrefixoid) {
			word = StringUtils.removeEnd(word, PROPRIETARY_AFIXOID_SYMBOL);
			word = word + UNIFIED_AFIXOID_SYMBOL;
		} else if (isSuffixoid) {
			word = StringUtils.removeStart(word, PROPRIETARY_AFIXOID_SYMBOL);
			word = UNIFIED_AFIXOID_SYMBOL + word;
		}
		return word;
	}

	protected String cleanEkiElementMarkup(String originalText) {
		return textDecorationService.cleanEkiElementMarkup(originalText);
	}

	protected String cleanEkiEntityMarkup(String originalText) {
		return textDecorationService.cleanEkiEntityMarkup(originalText);
	}

	protected String cleanEkiEntityMarkupSkipStress(String originalText) {
		return textDecorationService.cleanEkiEntityMarkupSkipStress(originalText);
	}

	protected String convertEkiEntityMarkup(String originalText) {
		return textDecorationService.convertEkiEntityMarkup(originalText);
	}

	protected String composeLinkMarkup(String linkType, String linkId, String linkValue) {
		return textDecorationService.composeLinkMarkup(linkType, linkId, linkValue);
	}

	protected String applyPattern(Pattern pattern, String text, CodeValue codeValue) {
		return textDecorationService.applyPattern(pattern, text, codeValue);
	}

	protected String removeAccents(String value, String lang) {
		return textDecorationService.removeAccents(value, lang);
	}

	private void deleteFloatingData() throws Exception {

		String dataset = getDataset();
		List<Long> wordIds = basicDbService.queryList(sqls.getSqlSelectFloatingWordIds(), new HashMap<>(), Long.class);
		if (CollectionUtils.isNotEmpty(wordIds)) {
			logger.debug("There are {} floating words created by \"{}\" which are now deleted", wordIds.size(), dataset);
			Map<String, Object> tableRowParamMap = new HashMap<>();
			String sql = "delete from " + WORD + " where id = :wordId";
			for (Long wordId : wordIds) {
				tableRowParamMap.put("wordId", wordId);
				basicDbService.executeScript(sql, tableRowParamMap);
			}
		}
	}

	protected void deleteDatasetData(String dataset) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("dataset", dataset);

		List<Long> wordIdsLex = basicDbService.queryList(sqls.getSqlSelectWordIdsForDatasetByLexeme(), paramMap, Long.class);
		List<Long> wordIdsGuid = basicDbService.queryList(sqls.getSqlSelectWordIdsForDatasetByGuid(), paramMap, Long.class);
		List<Long> wordIds = new ArrayList<>();
		wordIds.addAll(wordIdsLex);
		wordIds.addAll(wordIdsGuid);
		wordIds = wordIds.stream().distinct().collect(Collectors.toList());
		logger.debug("There are {} words in \"{}\" to be deleted - {} by lexemes, {} by guids", wordIds.size(), dataset, wordIdsLex.size(), wordIdsGuid.size());

		List<Long> meaningIds = basicDbService.queryList(sqls.getSqlSelectMeaningIdsForDataset(), paramMap, Long.class);
		logger.debug("There are {} meanings in \"{}\" to be deleted", meaningIds.size(), dataset);

		String sql;
		int deletedRecordCount;

		// delete freeforms
		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteDefinitionFreeformsForDataset(), paramMap);
		logger.debug("Deleted {} definition freeforms in \"{}\"", deletedRecordCount, dataset);

		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteMeaningFreeformsForDataset(), paramMap);
		logger.debug("Deleted {} meaning freeforms in \"{}\"", deletedRecordCount, dataset);

		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteLexemeFreeformsForDataset(), paramMap);
		logger.debug("Deleted {} lexeme freeforms in \"{}\"", deletedRecordCount, dataset);

		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteCollocationFreeformsForDataset(), paramMap);
		logger.debug("Deleted {} collocation freeforms in \"{}\"", deletedRecordCount, dataset);

		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteWordFreeformsForDataset(), paramMap);
		logger.debug("Deleted {} word freeforms in \"{}\"", deletedRecordCount, dataset);

		// delete lifecycle logs
		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteWordLifecycleLogsForDataset(), paramMap);
		logger.debug("Deleted {} word lifecycle logs in \"{}\"", deletedRecordCount, dataset);

		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteLexemeLifecycleLogsForDataset(), paramMap);
		logger.debug("Deleted {} lexeme lifecycle logs in \"{}\"", deletedRecordCount, dataset);

		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteMeaningLifecycleLogsForDataset(), paramMap);
		logger.debug("Deleted {} meaning lifecycle logs in \"{}\"", deletedRecordCount, dataset);

		// delete process logs
		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteProcessLogsForDataset(), paramMap);
		logger.debug("Deleted {} process logs in \"{}\"", deletedRecordCount, dataset);

		// delete definitions
		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteDefinitionsForDataset(), paramMap);
		logger.debug("Deleted {} definitions in \"{}\"", deletedRecordCount, dataset);

		// delete collocations
		deletedRecordCount = basicDbService.executeScript(sqls.getSqlDeleteCollocationsForDataset(), paramMap);
		logger.debug("Deleted {} collocations in \"{}\"", deletedRecordCount, dataset);

		// delete etymology
		if (StringUtils.equals(ETYMOLOGY_OWNER_DATASET_CODE, dataset)) {
			sql = "delete from " + WORD_ETYMOLOGY;
			deletedRecordCount = basicDbService.executeScript(sql);
			logger.debug("Deleted {} word etyms in \"{}\"", deletedRecordCount, dataset);
		}

		// delete lexemes
		sql = "delete from " + LEXEME + " l where l.dataset_code = :dataset";
		deletedRecordCount = basicDbService.executeScript(sql, paramMap);
		logger.debug("Deleted {} lexemes in \"{}\"", deletedRecordCount, dataset);

		// delete word guids and mnrs
		if (!StringUtils.equals(GUID_OWNER_DATASET_CODE, dataset)) {
			sql = "delete from " + WORD_GUID + " wg where wg.dataset_code = :dataset";
			deletedRecordCount = basicDbService.executeScript(sql, paramMap);
			sql = "delete from " + MEANING_NR + " mn where mn.dataset_code = :dataset";
			deletedRecordCount= basicDbService.executeScript(sql, paramMap);
		}

		// delete words
		sql = "delete from " + WORD + " where id = :wordId";
		paramMap.clear();
		for (Long wordId : wordIds) {
			paramMap.put("wordId", wordId);
			basicDbService.executeScript(sql, paramMap);
		}

		// delete meanings
		sql = "delete from " + MEANING + " where id = :meaningId";
		paramMap.clear();
		for (Long meaningId : meaningIds) {
			paramMap.put("meaningId", meaningId);
			basicDbService.executeScript(sql, paramMap);
		}

		logger.debug("Data deletion complete for \"{}\"", dataset);
	}

	protected Long createOrSelectWord(Word word, List<Paradigm> paradigms, Count reusedWordCount) throws Exception {

		String dataset = getDataset();
		String wordOrigValue = word.getValue();
		AffixoidData affixoidData = getAffixoidData(wordOrigValue);

		handleAffixoidClassifiers(word, affixoidData);

		String affixoidWordTypeCode = affixoidData.getAffixoidWordTypeCode();
		String wordCleanValue = affixoidData.getWordCleanValue();
		String wordLang = word.getLang();
		String[] wordComponents = word.getComponents();
		String wordDisplayForm = word.getDisplayForm();
		String wordVocalForm = word.getVocalForm();
		int homonymNr = word.getHomonymNr();
		String wordMorphCode = word.getMorphCode();
		String wordDisplayMorph = word.getDisplayMorph();
		String guid = word.getGuid();
		String genderCode = word.getGenderCode();
		List<String> wordTypeCodes = word.getWordTypeCodes();
		String aspectCode = word.getAspectCode();
		String wordClass = null;

		Form wordForm = new Form();
		wordForm.setMode(FormMode.WORD);
		wordForm.setMorphCode(wordMorphCode);
		wordForm.setMorphExists(new Boolean(true));
		wordForm.setValue(wordCleanValue);
		wordForm.setComponents(wordComponents);
		wordForm.setDisplayForm(wordDisplayForm);
		wordForm.setVocalForm(wordVocalForm);

		Map<String, Object> tableRowValueMap = getWord(wordCleanValue, homonymNr, wordLang, affixoidWordTypeCode);
		Long wordId;

		//TODO temp solution until MAB loading as separate dataset is implemented
		if (CollectionUtils.isNotEmpty(paradigms)) {
			wordClass = paradigms.get(0).getWordClass();
		}
		//...

		if (tableRowValueMap == null) {
			wordId = createWord(wordCleanValue, wordMorphCode, homonymNr, wordClass, wordLang, wordDisplayMorph, genderCode, aspectCode);
			if (CollectionUtils.isNotEmpty(wordTypeCodes)) {
				createWordTypes(wordId, wordTypeCodes);
			}
			if (StringUtils.isNotBlank(dataset) && StringUtils.isNotBlank(guid)) {
				createWordGuid(wordId, guid, dataset);
			}
			if (CollectionUtils.isEmpty(paradigms)) {
				Long paradigmId = createParadigm(wordId, null, null, false);
				createWordFormWithAsWord(paradigmId, wordForm, wordLang);
			}
		} else {
			wordId = (Long) tableRowValueMap.get("id");
			//TODO temp solution until MAB loading as separate dataset is implemented
			if (StringUtils.isNotEmpty(wordClass)) {
				updateWordClass(wordId, wordClass);
			}
			//...
			if (reusedWordCount != null) {
				reusedWordCount.increment();
			}
		}
		word.setId(wordId);

		if (CollectionUtils.isNotEmpty(paradigms)) {
			for (Paradigm paradigm : paradigms) {
				Long paradigmId = createParadigm(wordId, paradigm.getInflectionTypeNr(), paradigm.getInflectionType(), paradigm.isSecondary());
				// mab forms
				List<Form> forms = paradigm.getForms();
				if (CollectionUtils.isEmpty(forms)) {
					createWordFormWithAsWord(paradigmId, wordForm, wordLang);
				} else {
					for (Form form : forms) {
						if (form.getMode().equals(FormMode.WORD)) {
							form.setVocalForm(wordVocalForm);
						}
						createForm(paradigmId, form);
					}
				}
			}
		}
		return wordId;
	}

	protected Long createOrSelectWord(
			Word word,
			List<Paradigm> paradigms,
			Map<String, List<Guid>> ssGuidMap,
			Count ssWordCount,
			Count reusedWordCount) throws Exception {

		if (MapUtils.isEmpty(ssGuidMap)) {
			return createOrSelectWord(word, paradigms, reusedWordCount);
		}

		String dataset = getDataset();
		String wordOrigValue = word.getValue();
		String guid = word.getGuid();

		List<Guid> mappedGuids = ssGuidMap.get(guid);
		if (CollectionUtils.isEmpty(mappedGuids)) {
			appendToReport(REPORT_GUID_MAPPING_MISSING, dataset, wordOrigValue, guid);
			return createOrSelectWord(word, paradigms, reusedWordCount);
		}

		AffixoidData affixoidData = getAffixoidData(wordOrigValue);
		String wordCleanValue = affixoidData.getWordCleanValue();

		for (Guid ssGuidObj : mappedGuids) {

			String ssWordValue = ssGuidObj.getWord();
			String ssGuid = ssGuidObj.getValue();

			if (StringUtils.equalsIgnoreCase(wordCleanValue, ssWordValue)) {
				List<Map<String, Object>> tableRowValueMaps = getWord(wordCleanValue, ssGuid, GUID_OWNER_DATASET_CODE);
				Map<String, Object> tableRowValueMap = null;
				if (CollectionUtils.size(tableRowValueMaps) == 1) {
					tableRowValueMap = tableRowValueMaps.get(0);
				} else if (CollectionUtils.size(tableRowValueMaps) > 1) {
					tableRowValueMap = tableRowValueMaps.get(0);
					logger.warn("There are multiple words with same value and guid in {}: \"{}\" - \"{}\"", GUID_OWNER_DATASET_CODE, wordOrigValue, ssGuid);
				}
				if (tableRowValueMap == null) {
					return createOrSelectWord(word, paradigms, reusedWordCount);
				}
				ssWordCount.increment();
				Long wordId = (Long) tableRowValueMap.get("id");
				word.setId(wordId);
				if (StringUtils.isNotBlank(guid)) {
					PgArray guidDatasetCodesArr = (PgArray) tableRowValueMap.get("guid_dataset_codes");
					String[] guidDatasetCodes = (String[]) guidDatasetCodesArr.getArray();
					if (!ArrayUtils.contains(guidDatasetCodes, dataset)) {
						createWordGuid(wordId, guid, dataset);
					}
				}
				return wordId;
			}
		}
		List<String> mappedWordValues = mappedGuids.stream().map(Guid::getWord).collect(Collectors.toList());
		logger.debug("Word value doesn't match guid mapping(s): \"{}\" / \"{}\"", wordOrigValue, mappedWordValues);
		appendToReport(REPORT_GUID_MISMATCH, dataset, wordOrigValue, guid, mappedWordValues, "Sõnad ei kattu");

		return createOrSelectWord(word, paradigms, reusedWordCount);
	}

	private void handleAffixoidClassifiers(Word word, AffixoidData affixoidData) {

		if (!affixoidData.isPrefixoid() && !affixoidData.isSuffixoid()) {
			return;
		}
		List<String> wordTypeCodes = word.getWordTypeCodes();
		if (affixoidData.isPrefixoid()) {
			if (wordTypeCodes == null) {
				wordTypeCodes = new ArrayList<>();
				word.setWordTypeCodes(wordTypeCodes);
			}
			if (!wordTypeCodes.contains(PREFIXOID_WORD_TYPE_CODE)) {
				wordTypeCodes.add(PREFIXOID_WORD_TYPE_CODE);
			}
		}
		if (affixoidData.isSuffixoid()) {
			if (wordTypeCodes == null) {
				wordTypeCodes = new ArrayList<>();
				word.setWordTypeCodes(wordTypeCodes);
			}
			if (!wordTypeCodes.contains(SUFFIXOID_WORD_TYPE_CODE)) {
				wordTypeCodes.add(SUFFIXOID_WORD_TYPE_CODE);
			}
		}
	}

	private AffixoidData getAffixoidData(String wordValue) {

		if (StringUtils.endsWith(wordValue, UNIFIED_AFIXOID_SYMBOL)) {
			String cleanWordValue = StringUtils.removeEnd(wordValue, UNIFIED_AFIXOID_SYMBOL);
			return new AffixoidData(wordValue, cleanWordValue, PREFIXOID_WORD_TYPE_CODE, true, false);
		}
		if (StringUtils.startsWith(wordValue, UNIFIED_AFIXOID_SYMBOL)) {
			String cleanWordValue = StringUtils.removeStart(wordValue, UNIFIED_AFIXOID_SYMBOL);
			return new AffixoidData(wordValue, cleanWordValue, SUFFIXOID_WORD_TYPE_CODE, false, true);
		}
		return new AffixoidData(wordValue, wordValue, null, false, false);
	}

	private Map<String, Object> getWord(String word, int homonymNr, String lang, String wordTypeCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("homonymNr", homonymNr);
		tableRowParamMap.put("lang", lang);
		String sql;
		if (StringUtils.isBlank(wordTypeCode)) {
			tableRowParamMap.put("wordTypeCodes", afixoidWordTypeCodes);
			sql = sqls.getSqlSelectWordByFormLangHomon();
		} else {
			tableRowParamMap.put("wordTypeCode", wordTypeCode);
			sql = sqls.getSqlSelectWordByFormLangHomonType();
		}
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sql, tableRowParamMap);
		return tableRowValueMap;
	}

	protected List<Map<String, Object>> getWord(String word, String guid, String dataset) throws Exception {

		guid = guid.toLowerCase();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("guid", guid);
		tableRowParamMap.put("dataset", dataset);
		List<Map<String, Object>> tableRowValueMaps = basicDbService.queryList(sqls.getSqlSelectWordByDatasetAndGuid(), tableRowParamMap);
		return tableRowValueMaps;
	}

	//TODO temp solution until MAB loading as separate dataset is implemented
	private void updateWordClass(Long wordId, String wordClass) {

		String wordClassUpdateSql = "update " + WORD + " set word_class = :wordClass where id = :wordId";
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordId", wordId);
		tableRowParamMap.put("wordClass", wordClass);
		basicDbService.executeScript(wordClassUpdateSql, tableRowParamMap);
	}

	private void createWordFormWithAsWord(Long paradigmId, Form wordForm, String lang) throws Exception {

		createForm(paradigmId, wordForm);
		String wordValue = wordForm.getValue();
		wordValue = removeAccents(wordValue, lang);
		String morphCode = wordForm.getMorphCode();
		if (StringUtils.isNotBlank(wordValue)) {
			Form asWordForm = new Form();
			asWordForm.setMode(FormMode.AS_WORD);
			asWordForm.setValue(wordValue);
			asWordForm.setMorphCode(morphCode);
			asWordForm.setMorphExists(new Boolean(true));
			createForm(paradigmId, asWordForm);
		}
	}

	protected void createForm(Long paradigmId, Form form) throws Exception {

		FormMode mode = form.getMode();
		String morphGroup1 = form.getMorphGroup1();
		String morphGroup2 = form.getMorphGroup2();
		String morphGroup3 = form.getMorphGroup3();
		Integer displayLevel = form.getDisplayLevel();
		String morphCode = form.getMorphCode();
		Boolean morphExists = form.getMorphExists();
		String value = form.getValue();
		String[] components = form.getComponents();
		String displayForm = form.getDisplayForm();
		String vocalForm = form.getVocalForm();
		String audioFile = form.getAudioFile();
		Long orderBy = form.getOrderBy();

		String valueClean = cleanEkiEntityMarkup(value);
		String valuePrese = convertEkiEntityMarkup(value);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("paradigm_id", paradigmId);
		tableRowParamMap.put("mode", mode.name());
		if (StringUtils.isNotBlank(morphGroup1)) {
			tableRowParamMap.put("morph_group1", morphGroup1);
		}
		if (StringUtils.isNotBlank(morphGroup2)) {
			tableRowParamMap.put("morph_group2", morphGroup2);
		}
		if (StringUtils.isNotBlank(morphGroup3)) {
			tableRowParamMap.put("morph_group3", morphGroup3);
		}
		if (displayLevel != null) {
			tableRowParamMap.put("display_level", displayLevel);
		}
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("morph_exists", morphExists);
		tableRowParamMap.put("value", valueClean);
		tableRowParamMap.put("value_prese", valuePrese);
		if (components != null) {
			tableRowParamMap.put("components", new PgVarcharArray(components));
		}
		if (StringUtils.isNotBlank(displayForm)) {
			tableRowParamMap.put("display_form", displayForm);
		}
		if (StringUtils.isNotBlank(vocalForm)) {
			tableRowParamMap.put("vocal_form", vocalForm);
		}
		if (StringUtils.isNotBlank(audioFile)) {
			tableRowParamMap.put("audio_file", audioFile);
		}
		if (orderBy != null) {
			tableRowParamMap.put("order_by", orderBy);
		}
		basicDbService.create(FORM, tableRowParamMap);
	}

	protected Long createParadigm(Long wordId, String inflectionTypeNr, String inflectionType, boolean isSecondary) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("is_secondary", isSecondary);
		if (StringUtils.isNotBlank(inflectionTypeNr)) {
			tableRowParamMap.put("inflection_type_nr", inflectionTypeNr);
		}
		if (StringUtils.isNotBlank(inflectionType)) {
			tableRowParamMap.put("inflection_type", inflectionType);
		}
		Long paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);
		return paradigmId;
	}

	protected Long createWord(
			String word, final String morphCode, final int homonymNr, String wordClass, String lang, String displayMorph, String genderCode, String aspectCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		tableRowParamMap.put("word_class", wordClass);
		tableRowParamMap.put("display_morph_code", displayMorph);
		tableRowParamMap.put("gender_code", genderCode);
		tableRowParamMap.put("aspect_code", aspectCode);
		Long wordId = basicDbService.create(WORD, tableRowParamMap);
		createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.CREATE, word);
		return wordId;
	}

	private void createWordTypes(Long wordId, List<String> wordTypeCodes) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		for (String wordTypeCode : wordTypeCodes) {
			tableRowParamMap.put("word_type_code", wordTypeCode);
			basicDbService.create(WORD_WORD_TYPE, tableRowParamMap);
		}
	}

	protected void createWordGuid(Long wordId, String guid, String dataset) throws Exception {

		guid = guid.toLowerCase();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("guid", guid);
		tableRowParamMap.put("dataset_code", dataset);
		basicDbService.create(WORD_GUID, tableRowParamMap);
	}

	protected int getWordMaxHomonymNr(String word, String lang) throws Exception {

		AffixoidData affixoidData = getAffixoidData(word);
		String wordCleanValue = affixoidData.getWordCleanValue();
		wordCleanValue = StringUtils.lowerCase(wordCleanValue);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", wordCleanValue);
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("mode", FormMode.WORD.name());
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqls.getSqlSelectWordMaxHomonByWordLang(), tableRowParamMap);
		if (MapUtils.isEmpty(tableRowValueMap)) {
			return 0;
		}
		Object result = tableRowValueMap.get("max_homonym_nr");
		if (result == null) {
			return 0;
		}
		int homonymNr = (int) result;
		return homonymNr;
	}

	protected List<Map<String, Object>> getWords(String word, String dataset) {
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("dataset", dataset);
		return basicDbService.queryList(sqls.getSqlSelectWordByDataset(), tableRowParamMap);
	}

	protected Long createOrSelectMeaning(String mnr, Map<String, List<Mnr>> ssMnrMap, Count ssMeaningCount) throws Exception {

		if (MapUtils.isEmpty(ssMnrMap)) {
			return createMeaning();
		}

		if (StringUtils.isBlank(mnr)) {
			return createMeaning();
		}

		List<Mnr> mappedMnrs = ssMnrMap.get(mnr);
		if (CollectionUtils.isEmpty(mappedMnrs)) {
			return createMeaning();
		}

		String dataset = getDataset();

		for (Mnr ssMnrObj : mappedMnrs) {

			String ssMnr = ssMnrObj.getValue();
			List<Map<String, Object>> tableRowValueMaps = getMeaning(ssMnr, GUID_OWNER_DATASET_CODE);
			Map<String, Object> tableRowValueMap = null;
			if (CollectionUtils.size(tableRowValueMaps) == 1) {
				tableRowValueMap = tableRowValueMaps.get(0);
				ssMeaningCount.increment();
				Long meaningId = (Long) tableRowValueMap.get("id");
				PgArray mnrDatasetCodesArr = (PgArray) tableRowValueMap.get("mnr_dataset_codes");
				String[] mnrDatasetCodes = (String[]) mnrDatasetCodesArr.getArray();
				if (!ArrayUtils.contains(mnrDatasetCodes, dataset)) {
					createMeaningNr(meaningId, mnr, dataset);
				}
				return meaningId;
			}
		}
		return createMeaning();
	}

	protected Long createMeaning() throws Exception {

		Long meaningId = basicDbService.create(MEANING);
		return meaningId;
	}

	protected List<Map<String, Object>> getMeaning(String mnr, String dataset) {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("mnr", mnr);
		tableRowParamMap.put("dataset", dataset);
		List<Map<String, Object>> tableRowValueMaps = basicDbService.queryList(sqls.getSqlSelectMeaningByDatasetAndMnr(), tableRowParamMap);
		return tableRowValueMaps;
	}

	protected void createMeaningNr(Long meaningId, String mnr, String dataset) throws Exception {

		mnr = mnr.toLowerCase();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("mnr", mnr);
		tableRowParamMap.put("dataset_code", dataset);
		basicDbService.createIfNotExists(MEANING_NR, tableRowParamMap);
	}

	protected void createMeaningDomain(Long meaningId, String domainOrigin, String domainCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("domain_origin", domainOrigin);
		tableRowParamMap.put("domain_code", domainCode);
		Long meaningDomainId = basicDbService.createIfNotExists(MEANING_DOMAIN, tableRowParamMap);

		if (meaningDomainId != null) {
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, meaningId, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, LifecycleEventType.CREATE, domainCode);
		}
	}

	protected Long createOrSelectDefinition(Long meaningId, String value, String lang) throws Exception {

		Long definitionId = createOrSelectDefinition(meaningId, value, DEFAULT_DEFINITION_TYPE_CODE, lang);
		return definitionId;
	}

	protected Long createOrSelectDefinition(Long meaningId, String value, String definitionTypeCode, String lang) throws Exception {

		String dataset = getDataset();
		String valueClean = cleanEkiEntityMarkup(value);
		valueClean = cleanEkiElementMarkup(valueClean);
		Complexity complexity = getDefinitionComplexity();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaningId", meaningId);
		tableRowParamMap.put("value", valueClean);
		tableRowParamMap.put("definitionTypeCode", definitionTypeCode);
		tableRowParamMap.put("datasetCode", dataset);
		List<Map<String, Object>> definitionCandidates = basicDbService.queryList(sqls.getSqlSelectDefinitionByMeaningValueType(), tableRowParamMap);
		Long definitionId = null;
		if (CollectionUtils.isEmpty(definitionCandidates)) {
			String valuePrese = convertEkiEntityMarkup(value);
			tableRowParamMap.clear();
			tableRowParamMap.put("meaning_id", meaningId);
			tableRowParamMap.put("value", valueClean);
			tableRowParamMap.put("value_prese", valuePrese);
			tableRowParamMap.put("lang", lang);
			tableRowParamMap.put("definition_type_code", definitionTypeCode);
			tableRowParamMap.put("complexity", complexity.name());
			definitionId = basicDbService.create(DEFINITION, tableRowParamMap);
			createDefinitionDataset(definitionId, dataset);
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, definitionId, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, LifecycleEventType.CREATE, value);
		} else {
			Map<String, Object> definition = definitionCandidates.get(0);
			definitionId = (Long) definition.get("id");
			boolean datasetExists = (boolean) definition.get("dataset_exists");
			if (!datasetExists) {
				tableRowParamMap.clear();
				createDefinitionDataset(definitionId, dataset);
			}
		}
		return definitionId;
	}

	private void createDefinitionDataset(Long definitionId, String dataset) throws Exception {
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("definition_id", definitionId);
		tableRowParamMap.put("dataset_code", dataset);
		basicDbService.createWithoutId(DEFINITION_DATASET, tableRowParamMap);
	}

	protected Long createOrSelectLexemeId(Lexeme lexeme) throws Exception {

		String dataset = getDataset();
		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", wordId);
		criteriaParamMap.put("meaning_id", meaningId);
		criteriaParamMap.put("dataset_code", dataset);
		Map<String, Object> lexemeResult = basicDbService.select(LEXEME, criteriaParamMap);
		Long lexemeId;
		if (MapUtils.isEmpty(lexemeResult)) {
			lexemeId = createLexeme(lexeme);
			lexeme.setLexemeId(lexemeId);
		} else {
			lexemeId = (Long) lexemeResult.get("id");
		}
		return lexemeId;
	}

	protected Long createLexeme(Lexeme lexeme) throws Exception {

		String dataset = getDataset();
		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();
		Complexity complexity = lexeme.getComplexity();
		if (complexity == null) {
			complexity = getLexemeComplexity();
		}

		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", wordId);
		valueParamMap.put("meaning_id", meaningId);
		valueParamMap.put("dataset_code", dataset);
		valueParamMap.put("type", LexemeType.PRIMARY.name());
		valueParamMap.put("complexity", complexity.name());
		populateLexemeValueParamMap(lexeme, valueParamMap);
		Long lexemeId = basicDbService.create(LEXEME, valueParamMap);
		String processStateCode = (String) valueParamMap.get("process_state_code");
		createLexemeProcessStateProcessLog(lexemeId, processStateCode);
		return lexemeId;
	}

	protected Long createLexemeIfNotExists(Lexeme lexeme) throws Exception {

		String dataset = getDataset();
		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();
		Complexity complexity = lexeme.getComplexity();
		if (complexity == null) {
			complexity = getLexemeComplexity();
		}

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", wordId);
		criteriaParamMap.put("meaning_id", meaningId);
		criteriaParamMap.put("dataset_code", dataset);
		criteriaParamMap.put("type", LexemeType.PRIMARY.name());
		criteriaParamMap.put("complexity", complexity.name());
		Long lexemeId = basicDbService.createIfNotExists(LEXEME, criteriaParamMap);
		lexeme.setLexemeId(lexemeId);
		if (lexemeId != null) {
			criteriaParamMap.clear();
			criteriaParamMap.put("id", lexemeId);
			Map<String, Object> valueParamMap = new HashMap<>();
			populateLexemeValueParamMap(lexeme, valueParamMap);
			if (MapUtils.isNotEmpty(valueParamMap)) {
				basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
				String processStateCode = (String) valueParamMap.get("process_state_code");
				createLexemeProcessStateProcessLog(lexemeId, processStateCode);
			}
		}
		return lexemeId;
	}

	private void populateLexemeValueParamMap(Lexeme lexeme, Map<String, Object> valueParamMap) {

		Integer lexemeLevel1 = lexeme.getLevel1();
		Integer lexemeLevel2 = lexeme.getLevel2();
		String frequencyGroupCode = lexeme.getFrequencyGroupCode();
		String valueStateCode = lexeme.getValueStateCode();
		String processStateCode = lexeme.getProcessStateCode();
		Float corpusFrequency = lexeme.getCorpusFrequency();

		if (lexemeLevel1 != null) {
			valueParamMap.put("level1", lexemeLevel1);
		}
		if (lexemeLevel2 != null) {
			valueParamMap.put("level2", lexemeLevel2);
		}
		if (StringUtils.isNotBlank(frequencyGroupCode)) {
			valueParamMap.put("frequency_group_code", frequencyGroupCode);
		}
		if (StringUtils.isNotBlank(valueStateCode)) {
			valueParamMap.put("value_state_code", valueStateCode);
		}
		if (corpusFrequency != null) {
			valueParamMap.put("corpus_frequency", corpusFrequency);
		}
		if (StringUtils.isBlank(processStateCode)) {
			valueParamMap.put("process_state_code", DEFAULT_PROCESS_STATE_CODE);
		} else {
			valueParamMap.put("process_state_code", processStateCode);			
		}
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
				createFreeformClassifier(usageId, FreeformType.USAGE_TYPE, usageType);
			}
			if (StringUtils.isBlank(extSourceId)) {
				extSourceId = EXT_SOURCE_ID_NA;
			}
			if (StringUtils.isNotBlank(author)) {
				Long authorId = getSource(SourceType.PERSON, extSourceId, author, getDataset());
				if (authorId == null) {
					authorId = createSource(SourceType.PERSON, extSourceId, author);
				}
				ReferenceType referenceType;
				if (StringUtils.isEmpty(authorTypeStr)) {
					referenceType = ReferenceType.AUTHOR;
				} else {
					referenceType = ReferenceType.TRANSLATOR;
				}
				createFreeformSourceLink(usageId, referenceType, authorId, null, author);
				createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, usageId, LifecycleEntity.USAGE, LifecycleProperty.SOURCE_LINK, LifecycleEventType.CREATE, author);
			}
			if (CollectionUtils.isNotEmpty(usage.getDefinitions())) {
				for (String usageDefinition : usage.getDefinitions()) {
					Long usageDefinitionId = createFreeformTextEkiMarkup(usageId, FreeformType.USAGE_DEFINITION, usageDefinition, dataLang, null);
					createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, usageDefinitionId, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, LifecycleEventType.CREATE, usageDefinition);
				}
			}
			if (CollectionUtils.isNotEmpty(usage.getUsageTranslations())) {
				for (UsageTranslation usageTranslation : usage.getUsageTranslations()) {
					String usageTranslationValue = usageTranslation.getValue();
					String usageTranslationLang = usageTranslation.getLang();
					Long usageTranslationId = createFreeformTextEkiMarkup(usageId, FreeformType.USAGE_TRANSLATION, usageTranslationValue, usageTranslationLang, null);
					createLifecycleLog(
							LifecycleLogOwner.LEXEME, lexemeId, usageTranslationId, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, LifecycleEventType.CREATE, usageTranslationValue);
				}
			}
		}
	}

	protected Long createProcessLog(String eventBy, Timestamp eventOn, String comment, String processStateCode, String datasetCode) throws Exception {

		if (StringUtils.isBlank(eventBy)) {
			eventBy = "Ekilex " + datasetCode + "-laadur";
		}

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("event_by", eventBy);
		if (eventOn != null) {
			tableRowParamMap.put("event_on", eventOn);
		}
		if (StringUtils.isNotBlank(comment)) {
			String commentClean = cleanEkiEntityMarkup(comment);
			String commentPrese = convertEkiEntityMarkup(comment);
			tableRowParamMap.put("comment", commentClean);
			tableRowParamMap.put("comment_prese", commentPrese);
		}
		if (StringUtils.isNotBlank(processStateCode)) {
			tableRowParamMap.put("process_state_code", processStateCode);
		}
		tableRowParamMap.put("dataset_code", datasetCode);
		Long processLogId = basicDbService.create(PROCESS_LOG, tableRowParamMap);
		return processLogId;
	}

	protected void updateProcessLogText(Long processLogId, String comment) throws Exception {

		String commentClean = cleanEkiEntityMarkup(comment);
		commentClean = cleanEkiElementMarkup(commentClean);
		String commentPrese = convertEkiEntityMarkup(comment);

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", processLogId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("comment", commentClean);
		valueParamMap.put("comment_prese", commentPrese);
		basicDbService.update(PROCESS_LOG, criteriaParamMap, valueParamMap);
	}

	protected void updateLexemeProcessState(Long lexemeId, String processStateCode) throws Exception {

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", lexemeId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("process_state_code", processStateCode);
		basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);

		createLexemeProcessStateProcessLog(lexemeId, processStateCode);
	}

	protected Long createWordProcessLog(Long wordId, String eventBy, Timestamp eventOn, String comment) throws Exception {

		String datasetCode = getDataset();

		Long processLogId = createProcessLog(eventBy, eventOn, comment, null, datasetCode);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("process_log_id", processLogId);
		basicDbService.create(WORD_PROCESS_LOG, tableRowParamMap);

		return processLogId;
	}

	protected Long createLexemeProcessStateProcessLog(Long lexemeId, String processStateCode) throws Exception {
		String datasetCode = getDataset();
		return createLexemeProcessLog(lexemeId, null, null, null, processStateCode, datasetCode);
	}

	protected Long createLexemeProcessLog(Long lexemeId, String comment) throws Exception {
		String datasetCode = getDataset();
		return createLexemeProcessLog(lexemeId, null, null, comment, null, datasetCode);
	}

	protected Long createLexemeProcessLog(Long lexemeId, String eventBy, Timestamp eventOn, String comment, String processStateCode, String datasetCode) throws Exception {
		
		Long processLogId = createProcessLog(eventBy, eventOn, comment, processStateCode, datasetCode);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("process_log_id", processLogId);
		basicDbService.create(LEXEME_PROCESS_LOG, tableRowParamMap);

		return processLogId;
	}

	protected Long createMeaningProcessLog(Long meaningId, String comment) throws Exception {

		String datasetCode = getDataset();

		Long processLogId = createProcessLog(null, null, comment, null, datasetCode);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("process_log_id", processLogId);
		basicDbService.create(MEANING_PROCESS_LOG, tableRowParamMap);

		return processLogId;
	}

	protected Long createLexemeFreeform(Long lexemeId, FreeformType freeformType, Object value, String lang) throws Exception {

		Complexity complexity = getFreeformComplexity();

		Long freeformId = createFreeformTextOrTimestamp(freeformType, value, lang, complexity);
		if (freeformId == null) {
			return null;
		}

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(LEXEME_FREEFORM, tableRowParamMap);

		LifecycleEntity lifecycleEntity = translate(freeformType);
		createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, freeformId, lifecycleEntity, LifecycleProperty.VALUE, LifecycleEventType.CREATE, value.toString());

		return freeformId;
	}

	protected Long createLexemeFreeform(Long lexemeId, FreeformType freeformType, String valueText, String valuePrese, String lang, Complexity complexity) throws Exception {

		Long freeformId = createFreeformText(null, freeformType, valueText, valuePrese, lang, complexity);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(LEXEME_FREEFORM, tableRowParamMap);

		LifecycleEntity lifecycleEntity = translate(freeformType);
		createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, freeformId, lifecycleEntity, LifecycleProperty.VALUE, LifecycleEventType.CREATE, valueText);

		return freeformId;
	}

	protected Long createOrSelectLexemeFreeform(Long lexemeId, FreeformType freeformType, String freeformValue) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("value", freeformValue);
		tableRowParamMap.put("type", freeformType.name());
		Map<String, Object> freeform = basicDbService.queryForMap(sqls.getSqlSelectLexemeFreeform(), tableRowParamMap);
		Long freeformId;
		if (freeform == null) {
			freeformId = createLexemeFreeform(lexemeId, freeformType, freeformValue, null);
		} else {
			freeformId = (Long) freeform.get("id");
		}
		return freeformId;
	}

	protected Long createMeaningFreeform(Long meaningId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrTimestamp(freeformType, value, null, null);
		if (freeformId == null) {
			return null;
		}
		
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(MEANING_FREEFORM, tableRowParamMap);

		LifecycleEntity lifecycleEntity = translate(freeformType);
		createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, freeformId, lifecycleEntity, LifecycleProperty.VALUE, LifecycleEventType.CREATE, value.toString());

		return freeformId;
	}

	protected Long createDefinitionFreeform(Long definitionId, FreeformType freeformType, Object value) throws Exception {

		Long freeformId = createFreeformTextOrTimestamp(freeformType, value, null, null);
		if (freeformId == null) {
			return null;
		}

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("definition_id", definitionId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(DEFINITION_FREEFORM, tableRowParamMap);

		return freeformId;
	}

	protected Long createFreeformTextEkiMarkup(Long parentId, FreeformType freeformType, String value, String lang, Complexity complexity) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", freeformType.name());
		if (parentId != null) {
			tableRowParamMap.put("parent_id", parentId);
		}
		if (value != null) {
			String valueStr = (String) value;
			String valueClean = cleanEkiEntityMarkup(valueStr);
			String valuePrese = convertEkiEntityMarkup(valueStr);
			tableRowParamMap.put("value_text", valueClean);
			tableRowParamMap.put("value_prese", valuePrese);
		}
		if (StringUtils.isNotBlank(lang)) {
			tableRowParamMap.put("lang", lang);
		}
		if (complexity != null) {
			tableRowParamMap.put("complexity", complexity.name());
		}
		Long freeformId = basicDbService.create(FREEFORM, tableRowParamMap);
		return freeformId;
	}

	private Long createFreeformTextOrTimestamp(FreeformType freeformType, Object value, String lang, Complexity complexity) throws Exception {
		if (value == null) {
			return null;
		}
		Long freeformId = null;
		if (value instanceof String) {
			String valueStr = (String) value;
			freeformId = createFreeformTextEkiMarkup(null, freeformType, valueStr, lang, complexity);
		} else if (value instanceof Timestamp) {
			Timestamp valueTs = (Timestamp) value;
			freeformId = createFreeformDate(null, freeformType, valueTs, null);
		} else {
			throw new Exception("Not yet supported freeform data type " + value);
		}
		return freeformId;
	}

	protected Long createFreeformText(Long parentId, FreeformType freeformType, String valueText, String valuePrese, String lang, Complexity complexity) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", freeformType.name());
		if (parentId != null) {
			tableRowParamMap.put("parent_id", parentId);
		}
		tableRowParamMap.put("value_text", valueText);
		tableRowParamMap.put("value_prese", valuePrese);
		if (StringUtils.isNotBlank(lang)) {
			tableRowParamMap.put("lang", lang);
		}
		if (complexity != null) {
			tableRowParamMap.put("complexity", complexity.name());
		}
		Long freeformId = basicDbService.create(FREEFORM, tableRowParamMap);
		return freeformId;
	}

	protected Long createFreeformDate(Long parentId, FreeformType freeformType, Timestamp value, Complexity complexity) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", freeformType.name());
		if (parentId != null) {
			tableRowParamMap.put("parent_id", parentId);
		}
		tableRowParamMap.put("value_date", value);
		if (complexity != null) {
			tableRowParamMap.put("complexity", complexity.name());
		}
		Long freeformId = basicDbService.create(FREEFORM, tableRowParamMap);
		return freeformId;
	}

	protected Long createFreeformClassifier(Long parentId, FreeformType freeformType, String classifierCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", freeformType.name());
		if (parentId != null) {
			tableRowParamMap.put("parent_id", parentId);
		}
		tableRowParamMap.put("classif_code", classifierCode);
		return basicDbService.create(FREEFORM, tableRowParamMap);
	}

	protected void updateFreeformText(Long freeformId, String value) throws Exception {

		String valueClean = cleanEkiEntityMarkup(value);
		valueClean = cleanEkiElementMarkup(valueClean);
		String valuePrese = convertEkiEntityMarkup(value);

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", freeformId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("value_text", valueClean);
		valueParamMap.put("value_prese", valuePrese);
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
		Long sourceLinkId = basicDbService.create(FREEFORM_SOURCE_LINK, tableRowParamMap);
		return sourceLinkId;
	}

	protected Long createProcessLogSourceLink(Long processLogId, ReferenceType refType, Long sourceId, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("process_log_id", processLogId);
		tableRowParamMap.put("type", refType.name());
		tableRowParamMap.put("source_id", sourceId);
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long sourceLinkId = basicDbService.create(PROCESS_LOG_SOURCE_LINK, tableRowParamMap);
		return sourceLinkId;
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
		Long sourceLinkId = basicDbService.createIfNotExists(LEXEME_SOURCE_LINK, tableRowParamMap);

		if (sourceLinkId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.SOURCE_LINK, LifecycleEventType.CREATE, value);
		}
		return sourceLinkId;
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

	protected Long createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("lexeme1_id", lexemeId1);
		relationParams.put("lexeme2_id", lexemeId2);
		relationParams.put("lex_rel_type_code", relationType);
		Long lexemeRelationId = basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);

		if (lexemeRelationId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId1, lexemeRelationId, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, LifecycleEventType.CREATE, relationType);
		}
		return lexemeRelationId;
	}

	protected boolean hasNoWordRelationGroupWithMembers(WordRelationGroupType groupType, List<Long> memberIds) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_rel_type_code", groupType.name());
		params.put("memberIds", memberIds);
		params.put("nrOfMembers", memberIds.size());
		return MapUtils.isEmpty(basicDbService.queryForMap(sqls.getSqlSelectWordGroupWithMembers(), params));
	}

	protected Long createWordGroup(WordRelationGroupType groupType) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_rel_type_code", groupType.name());
		return basicDbService.create(WORD_GROUP, params);
	}

	protected Long createWordGroupMember(Long groupId, Long wordId) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_group_id", groupId);
		params.put("word_id", wordId);
		return basicDbService.create(WORD_GROUP_MEMBER, params);
	}

	protected void createWordRelation(Long wordId1, Long wordId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("word1_id", wordId1);
		relationParams.put("word2_id", wordId2);
		relationParams.put("word_rel_type_code", relationType);
		Long wordRelationId = basicDbService.createIfNotExists(WORD_RELATION, relationParams);

		if (wordRelationId != null) {
			createLifecycleLog(LifecycleLogOwner.WORD, wordId1, wordRelationId, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, LifecycleEventType.CREATE, relationType);
		}
	}

	protected void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("meaning1_id", meaningId1);
		relationParams.put("meaning2_id", meaningId2);
		relationParams.put("meaning_rel_type_code", relationType);
		Long meaningRelationId = basicDbService.createIfNotExists(MEANING_RELATION, relationParams);

		if (meaningRelationId != null) {
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId1, meaningRelationId, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, LifecycleEventType.CREATE, relationType);
		}
	}

	protected void createLexemeRegister(Long lexemeId, String registerCode) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("lexeme_id", lexemeId);
		params.put("register_code", registerCode);
		Long lexemeRegisterId = basicDbService.createIfNotExists(LEXEME_REGISTER, params);

		if (lexemeRegisterId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, LifecycleEventType.CREATE, registerCode);
		}
	}

	protected void createLexemePos(Long lexemeId, String posCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("pos_code", posCode);
		Long lexemePosId = basicDbService.createIfNotExists(LEXEME_POS, tableRowParamMap);

		if (lexemePosId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.POS, LifecycleEventType.CREATE, posCode);
		}
	}

	protected void createLexemeDeriv(Long lexemeId, String derivCode) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("deriv_code", derivCode);
		Long lexemeDerivId = basicDbService.createIfNotExists(LEXEME_DERIV, tableRowParamMap);

		if (lexemeDerivId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, LifecycleEventType.CREATE, derivCode);
		}
	}

	protected void createLexemeRegion(Long lexemeId, String regionCode) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("lexeme_id", lexemeId);
		params.put("region_code", regionCode);
		Long lexemeRegionId = basicDbService.createIfNotExists(LEXEME_REGION, params);

		if (lexemeRegionId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, lexemeId, LifecycleEntity.LEXEME, LifecycleProperty.REGION, LifecycleEventType.CREATE, regionCode);
		}
	}

	protected Long createCollocation(String value, String definition, Float frequency, Float score, List<String> collocUsages, Complexity complexity) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("value", value);
		if (StringUtils.isNotBlank(definition)) {
			tableRowParamMap.put("definition", definition);
		}
		if (frequency != null) {
			tableRowParamMap.put("frequency", frequency);
		}
		if (score != null) {
			tableRowParamMap.put("score", score);
		}
		if (CollectionUtils.isNotEmpty(collocUsages)) {
			String[] collocUsagesArr = collocUsages.toArray(new String[0]);
			tableRowParamMap.put("usages", collocUsagesArr);
		}
		tableRowParamMap.put("complexity", complexity.name());
		Long collocationId = basicDbService.create(COLLOCATION, tableRowParamMap);
		return collocationId;
	}

	protected Long createSource(Source source) throws Exception {

		SourceType type = source.getType();
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("type", type.name());
		Long sourceId = basicDbService.create(SOURCE, tableRowParamMap);
		source.setSourceId(sourceId);
		return sourceId;
	}

	protected Long createSource(SourceType sourceType, String extSourceId, String sourceName) throws Exception {

		Source source = new Source();
		source.setType(sourceType);
		Long sourceId = createSource(source);
		createSourceFreeform(sourceId, FreeformType.SOURCE_NAME, sourceName);
		createSourceFreeform(sourceId, FreeformType.SOURCE_FILE, getDataset());
		createSourceFreeform(sourceId, FreeformType.EXTERNAL_SOURCE_ID, extSourceId);

		return sourceId;
	}

	protected Long createSourceFreeform(Long sourceId, FreeformType freeformType, Object value) throws Exception {

		if (value == null) {
			return null;
		}
		Long freeformId = null;
		if (value instanceof String) {
			String valueStr = (String) value;
			freeformId = createFreeformTextEkiMarkup(null, freeformType, valueStr, null, null);
		} else if (value instanceof Timestamp) {
			Timestamp valueTs = (Timestamp) value;
			freeformId = createFreeformDate(null, freeformType, valueTs, null);
		} else {
			throw new Exception("Not yet supported freeform data type " + value);
		}

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("source_id", sourceId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(SOURCE_FREEFORM, tableRowParamMap);

		return freeformId;
	}

	protected Long getSource(SourceType sourceType, String extSourceId, String sourceName, String fileName) {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("sourceType", sourceType.name());
		tableRowParamMap.put("sourcePropertyTypeName", FreeformType.SOURCE_NAME.name());
		tableRowParamMap.put("sourcePropertyTypeFileName", FreeformType.SOURCE_FILE.name());
		tableRowParamMap.put("sourceName", sourceName);
		tableRowParamMap.put("sourceFileName", fileName);
		tableRowParamMap.put("sourcePropertyTypeExtSourceId", FreeformType.EXTERNAL_SOURCE_ID.name());
		tableRowParamMap.put("extSourceId", extSourceId);
		List<Map<String, Object>> sources = basicDbService.queryList(sqls.getSqlSelectSourceByTypeAndNameAndFileName(), tableRowParamMap);

		if (CollectionUtils.isEmpty(sources)) {
			return null;
		}
		Map<String, Object> sourceRecord = sources.get(0);
		Long sourceId = (Long) sourceRecord.get("id");
		return sourceId;
	}

	protected List<RelationPart> getMeaningRelationParts(String word) {

		List<RelationPart> relationParts = new ArrayList<>();
		String dataset = getDataset();
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("dataset", dataset);
		tableRowParamMap.put("word", word);
		List<Map<String, Object>> queryResults = basicDbService.queryList(sqls.getSqlSelectMeaningIdsAndWordLangs(), tableRowParamMap);

		if (CollectionUtils.isNotEmpty(queryResults)) {
			for (Map<String, Object> queryResult : queryResults) {
				Long meaningId = (Long) queryResult.get("id");
				String lang = (String) queryResult.get("lang");

				RelationPart relationPart = new RelationPart();
				relationPart.setMeaningId(meaningId);
				relationPart.setLang(lang);
				relationParts.add(relationPart);
			}
		}
		return relationParts;
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

	protected String getNodeStringValue(Node node, String exp) {

		String result = null;
		Element stringNode = (Element) node.selectSingleNode(exp);
		if (stringNode != null) {
			result = stringNode.getTextTrim();
		}
		return result;
	}

	protected Timestamp getNodeTimestampValue(Node node, String exp, DateFormat dateFormat) throws ParseException {

		Timestamp timestamp = null;
		Element timestampNode = (Element) node.selectSingleNode(exp);
		if (timestampNode != null) {
			String timestampStr = timestampNode.getTextTrim();
			long timestampLong = dateFormat.parse(timestampStr).getTime();
			timestamp = new Timestamp(timestampLong);
		}
		return timestamp;
	}

	private void appendToReport(String reportName, Object ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	class AffixoidData {

		private String wordOrigValue;

		private String wordCleanValue;

		private String affixoidWordTypeCode;

		private boolean prefixoid;

		private boolean suffixoid;

		public AffixoidData(String wordOrigValue, String wordCleanValue, String affixoidWordTypeCode, boolean prefixoid, boolean suffixoid) {
			this.wordOrigValue = wordOrigValue;
			this.wordCleanValue = wordCleanValue;
			this.affixoidWordTypeCode = affixoidWordTypeCode;
			this.prefixoid = prefixoid;
			this.suffixoid = suffixoid;
		}

		public String getWordOrigValue() {
			return wordOrigValue;
		}

		public String getWordCleanValue() {
			return wordCleanValue;
		}

		public String getAffixoidWordTypeCode() {
			return affixoidWordTypeCode;
		}

		public boolean isPrefixoid() {
			return prefixoid;
		}

		public boolean isSuffixoid() {
			return suffixoid;
		}

	}

	class ArticleLogData {

		private String createdBy;

		private Timestamp createdOn;

		private Timestamp creationEnd;

		private String modifiedBy;

		private Timestamp modifiedOn;

		private Timestamp modificationEnd;

		private String chiefEditedBy;

		private Timestamp chiefEditedOn;

		public String getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}

		public Timestamp getCreatedOn() {
			return createdOn;
		}

		public void setCreatedOn(Timestamp createdOn) {
			this.createdOn = createdOn;
		}

		public Timestamp getCreationEnd() {
			return creationEnd;
		}

		public void setCreationEnd(Timestamp creationEnd) {
			this.creationEnd = creationEnd;
		}

		public String getModifiedBy() {
			return modifiedBy;
		}

		public void setModifiedBy(String modifiedBy) {
			this.modifiedBy = modifiedBy;
		}

		public Timestamp getModifiedOn() {
			return modifiedOn;
		}

		public void setModifiedOn(Timestamp modifiedOn) {
			this.modifiedOn = modifiedOn;
		}

		public Timestamp getModificationEnd() {
			return modificationEnd;
		}

		public void setModificationEnd(Timestamp modificationEnd) {
			this.modificationEnd = modificationEnd;
		}

		public String getChiefEditedBy() {
			return chiefEditedBy;
		}

		public void setChiefEditedBy(String chiefEditedBy) {
			this.chiefEditedBy = chiefEditedBy;
		}

		public Timestamp getChiefEditedOn() {
			return chiefEditedOn;
		}

		public void setChiefEditedOn(Timestamp chiefEditedOn) {
			this.chiefEditedOn = chiefEditedOn;
		}
	}
}
