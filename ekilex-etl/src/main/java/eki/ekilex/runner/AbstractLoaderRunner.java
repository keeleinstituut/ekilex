package eki.ekilex.runner;

import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.constant.WordRelationGroupType;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Source;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

public abstract class AbstractLoaderRunner extends AbstractLoaderCommons implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(AbstractLoaderRunner.class);

	@Autowired
	private UnifiedLoaderQueries sqls;

	@Autowired
	private TextDecorationService textDecorationService;

	abstract String getDataset();
	abstract void deleteDatasetData() throws Exception;
	abstract void initialise() throws Exception;

	private ReportComposer reportComposer;
	protected boolean doReports;

	private static final String REPORT_GUID_MISMATCH = "guid_mismatch";
	private static final String REPORT_GUID_MAPPING_MISSING = "guid_mapping_missing";

	protected static final String GUID_OWNER_DATASET_CODE = "ss1";
	protected static final String COLLOC_OWNER_DATASET_CODE = "kol";
	protected static final String ETYMOLOGY_OWNER_DATASET_CODE = "ety";
	protected static final String DEFAULT_WORD_MORPH_CODE = "??";
	protected static final String FORM_COMPONENT_SEPARATOR = "+";
	protected static final String PROPRIETARY_AFIXOID_SYMBOL = "+";
	protected static final String UNIFIED_AFIXOID_SYMBOL = "-";
	protected static final String PREFIXOID_WORD_TYPE_CODE = "pf";
	protected static final String SUFFIXOID_WORD_TYPE_CODE = "sf";

	private static final String CLASSIFIERS_MAPPING_FILE_PATH = "./fileresources/csv/classifier-main-map.csv";

	private static final char[] RESERVED_DIACRITIC_CHARS = new char[] {'õ', 'ä', 'ö', 'ü', 'š', 'ž', 'Õ', 'Ä', 'Ö', 'Ü', 'Š', 'Ž'};
	private static final String[] DISCLOSED_DIACRITIC_LANGS = new String[] {"rus"};

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
		long secondMillis = 1000;
		long minuteMillis = 60000;
		String timeLog;
		if (timeMillis < secondMillis) {
			timeLog = timeMillis + " millis";
		} else if (timeMillis < minuteMillis) {
			float timeSeconds = (float) timeMillis / (float) secondMillis;
			BigDecimal timeSecondsRound = new BigDecimal(timeSeconds);
			timeSecondsRound = timeSecondsRound.setScale(2, BigDecimal.ROUND_HALF_UP);
			timeLog = timeSecondsRound.toString() + " seconds";
		} else {
			float timeMinutes = (float) timeMillis / (float) minuteMillis;
			BigDecimal timeMinutesRound = new BigDecimal(timeMinutes);
			timeMinutesRound = timeMinutesRound.setScale(2, BigDecimal.ROUND_HALF_UP);
			timeLog = timeMinutesRound.toString() + " minutes";
		}
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

	protected String cleanEkiEntityMarkup(String originalText) {
		return textDecorationService.cleanEkiEntityMarkup(originalText);
	}

	protected String convertEkiEntityMarkup(String originalText) {
		return textDecorationService.convertEkiEntityMarkup(originalText);
	}

	protected String removeAccents(String value, String lang) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (ArrayUtils.contains(DISCLOSED_DIACRITIC_LANGS, lang)) {
			return null;
		}
		boolean isAlreadyClean = Normalizer.isNormalized(value, Normalizer.Form.NFD);
		if (isAlreadyClean) {
			return null;
		}
		StringBuffer cleanValueBuf = new StringBuffer();
		char[] chars = value.toCharArray();
		String decomposedChars;
		String charAsStr;
		char primaryChar;
		for (char c : chars) {
			boolean isReservedChar = ArrayUtils.contains(RESERVED_DIACRITIC_CHARS, c);
			if (isReservedChar) {
				cleanValueBuf.append(c);
			} else {
				charAsStr = Character.toString(c);
				decomposedChars = Normalizer.normalize(charAsStr, Normalizer.Form.NFD);
				if (decomposedChars.length() > 1) {
					primaryChar = decomposedChars.charAt(0);
					cleanValueBuf.append(primaryChar);
				} else {
					cleanValueBuf.append(c);
				}
			}
		}
		String cleanValue = cleanValueBuf.toString();
		if (StringUtils.equals(value, cleanValue)) {
			return null;
		}
		return cleanValue;
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

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("dataset", dataset);

		List<Long> wordIdsLex = basicDbService.queryList(sqls.getSqlSelectWordIdsForDatasetByLexeme(), tableRowParamMap, Long.class);
		List<Long> wordIdsGuid = basicDbService.queryList(sqls.getSqlSelectWordIdsForDatasetByGuid(), tableRowParamMap, Long.class);
		List<Long> wordIds = new ArrayList<>();
		wordIds.addAll(wordIdsLex);
		wordIds.addAll(wordIdsGuid);
		wordIds = wordIds.stream().distinct().collect(Collectors.toList());
		logger.debug("There are {} words in \"{}\" to be deleted - {} by lexemes, {} by guids", wordIds.size(), dataset, wordIdsLex.size(), wordIdsGuid.size());

		List<Long> meaningIds = basicDbService.queryList(sqls.getSqlSelectMeaningIdsForDataset(), tableRowParamMap, Long.class);
		logger.debug("There are {} meanings in \"{}\" to be deleted", meaningIds.size(), dataset);

		String sql;

		// freeforms
		basicDbService.executeScript(sqls.getSqlDeleteDefinitionFreeformsForDataset(), tableRowParamMap);
		basicDbService.executeScript(sqls.getSqlDeleteMeaningFreeformsForDataset(), tableRowParamMap);
		basicDbService.executeScript(sqls.getSqlDeleteLexemeFreeformsForDataset(), tableRowParamMap);

		// delete definitions
		basicDbService.executeScript(sqls.getSqlDeleteDefinitionsForDataset(), tableRowParamMap);

		// delete collocations + freeforms
		if (StringUtils.equals(COLLOC_OWNER_DATASET_CODE, dataset)) {
			basicDbService.executeScript(sqls.getSqlDeleteCollocationFreeformsForDataset());
			sql = "delete from " + COLLOCATION;
			basicDbService.executeScript(sql);
		}

		// delete etymology
		if (StringUtils.equals(ETYMOLOGY_OWNER_DATASET_CODE, dataset)) {
			sql = "delete from " + WORD_ETYMOLOGY;
			basicDbService.executeScript(sql);
		}

		// delete lexemes
		sql = "delete from " + LEXEME + " l where l.dataset_code = :dataset";
		basicDbService.executeScript(sql, tableRowParamMap);

		// delete word guids
		if (!StringUtils.equals(GUID_OWNER_DATASET_CODE, dataset)) {
			sql = "delete from " + WORD_GUID + " wg where wg.dataset_code = :dataset";
			basicDbService.executeScript(sql, tableRowParamMap);
		}

		// delete words
		sql = "delete from " + WORD + " where id = :wordId";
		tableRowParamMap.clear();
		for (Long wordId : wordIds) {
			tableRowParamMap.put("wordId", wordId);
			basicDbService.executeScript(sql, tableRowParamMap);
		}

		// delete meanings
		sql = "delete from " + MEANING + " where id = :meaningId";
		tableRowParamMap.clear();
		for (Long meaningId : meaningIds) {
			tableRowParamMap.put("meaningId", meaningId);
			basicDbService.executeScript(sql, tableRowParamMap);
		}

		logger.debug("Data deletion complete for \"{}\"", dataset);
	}

	protected Long createOrSelectWord(Word word, List<Paradigm> paradigms, String dataset, Count reusedWordCount) throws Exception {

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
		String aspectCode = word.getAspectTypeCode();
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
				createWordGuid(wordId, dataset, guid);
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
			Word word, List<Paradigm> paradigms, String dataset, Map<String, List<Guid>> ssGuidMap,
			Count ssWordCount, Count reusedWordCount) throws Exception {

		if (MapUtils.isEmpty(ssGuidMap)) {
			return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
		}

		String wordOrigValue = word.getValue();
		String guid = word.getGuid();

		List<Guid> mappedGuids = ssGuidMap.get(guid);
		if (CollectionUtils.isEmpty(mappedGuids)) {
			appendToReport(REPORT_GUID_MAPPING_MISSING, dataset, wordOrigValue, guid);
			return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
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
					return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
				}
				ssWordCount.increment();
				Long wordId = (Long) tableRowValueMap.get("id");
				word.setId(wordId);
				if (StringUtils.isNotBlank(guid)) {
					PgArray guidDatasetCodesArr = (PgArray) tableRowValueMap.get("guid_dataset_codes");
					String[] guidDatasetCodes = (String[]) guidDatasetCodesArr.getArray();
					if (!ArrayUtils.contains(guidDatasetCodes, dataset)) {
						createWordGuid(wordId, dataset, guid);
					}
				}
				return wordId;
			}
		}
		List<String> mappedWordValues = mappedGuids.stream().map(Guid::getWord).collect(Collectors.toList());
		logger.debug("Word value doesn't match guid mapping(s): \"{}\" / \"{}\"", wordOrigValue, mappedWordValues);
		appendToReport(REPORT_GUID_MISMATCH, dataset, wordOrigValue, guid, mappedWordValues, "Sõnad ei kattu");

		return createOrSelectWord(word, paradigms, dataset, reusedWordCount);
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
		String soundFile = form.getSoundFile();
		Integer orderBy = form.getOrderBy();

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
		if (StringUtils.isNotBlank(soundFile)) {
			tableRowParamMap.put("sound_file", soundFile);
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
		createLifecycleLog(LifecycleLogOwner.WORD, wordId, LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, word);
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

	protected void createWordGuid(Long wordId, String dataset, String guid) throws Exception {

		guid = guid.toLowerCase();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("dataset_code", dataset);
		tableRowParamMap.put("guid", guid);
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
		Long meaningDomainId = basicDbService.create(MEANING_DOMAIN, tableRowParamMap);
		createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domainCode);
	}

	protected Long createDefinition(Long meaningId, String value, String lang, String dataset) throws Exception {

		String valueClean = cleanEkiEntityMarkup(value);
		String valuePrese = convertEkiEntityMarkup(value);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		Long definitionId = findDefinitionId(meaningId, valueClean);
		if (definitionId == null) {
			tableRowParamMap.put("meaning_id", meaningId);
			tableRowParamMap.put("value", valueClean);
			tableRowParamMap.put("value_prese", valuePrese);
			tableRowParamMap.put("lang", lang);
			definitionId = basicDbService.create(DEFINITION, tableRowParamMap);
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, value);
		}
		if (definitionId != null) {
			tableRowParamMap.clear();
			tableRowParamMap.put("definition_id", definitionId);
			tableRowParamMap.put("dataset_code", dataset);
			Map<String, Object> result = basicDbService.select(DEFINITION_DATASET, tableRowParamMap);
			if (MapUtils.isEmpty(result)) {
				basicDbService.createWithoutId(DEFINITION_DATASET, tableRowParamMap);
			}
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
		String valueStateCode = lexeme.getValueStateCode();
		String processStateCode = lexeme.getProcessStateCode();
		Float corpusFrequency = lexeme.getCorpusFrequency();

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
			if (StringUtils.isNotBlank(valueStateCode)) {
				valueParamMap.put("value_state_code", valueStateCode);
			}
			if (StringUtils.isNotBlank(processStateCode)) {
				valueParamMap.put("process_state_code", processStateCode);
			}
			if (corpusFrequency != null) {
				valueParamMap.put("corpus_frequency", corpusFrequency);
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
				extSourceId = EXT_SOURCE_ID_NA;
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
				Long freeformSourceLinkId = createFreeformSourceLink(usageId, referenceType, authorId, null, author);
				createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, freeformSourceLinkId, author);
			}
			if (CollectionUtils.isNotEmpty(usage.getDefinitions())) {
				for (String usageDefinition : usage.getDefinitions()) {
					Long usageDefinitionId = createFreeformTextOrDate(FreeformType.USAGE_DEFINITION, usageId, usageDefinition, dataLang);
					createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, usageDefinition);
				}
			}
			if (CollectionUtils.isNotEmpty(usage.getUsageTranslations())) {
				for (UsageTranslation usageTranslation : usage.getUsageTranslations()) {
					String usageTranslationValue = usageTranslation.getValue();
					String usageTranslationLang = usageTranslation.getLang();
					Long usageTranslationId = createFreeformTextOrDate(FreeformType.USAGE_TRANSLATION, usageId, usageTranslationValue, usageTranslationLang);
					createLifecycleLog(
							LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, usageTranslationValue);
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

		try {
			LifecycleEntity lifecycleEntity = LifecycleEntity.valueOf(freeformType.name());
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, lifecycleEntity, LifecycleProperty.VALUE, freeformId, value.toString());
		} catch (Exception e) {
		}

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

		Long freeformId = createFreeformTextOrDate(freeformType, null, value, null);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("freeform_id", freeformId);
		basicDbService.create(MEANING_FREEFORM, tableRowParamMap);

		try {
			LifecycleEntity lifecycleEntity = LifecycleEntity.valueOf(freeformType.name());
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId, LifecycleEventType.CREATE, lifecycleEntity, LifecycleProperty.VALUE, freeformId, value.toString());
		} catch (Exception e) {
		}

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
				String valueStr = (String) value;
				String valueClean = cleanEkiEntityMarkup(valueStr);
				String valuePrese = convertEkiEntityMarkup(valueStr);
				tableRowParamMap.put("value_text", valueClean);
				tableRowParamMap.put("value_prese", valuePrese);
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

		String valueClean = cleanEkiEntityMarkup(value);
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
		Long sourceLinkId = basicDbService.create(LEXEME_SOURCE_LINK, tableRowParamMap);

		createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, value);

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

	protected void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("lexeme1_id", lexemeId1);
		relationParams.put("lexeme2_id", lexemeId2);
		relationParams.put("lex_rel_type_code", relationType);
		Long lexemeRelationId = basicDbService.createIfNotExists(LEXEME_RELATION, relationParams);

		if (lexemeRelationId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId1, LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, lexemeRelationId, relationType);
		}
	}

	protected boolean hasNoWordRelationGroupWithMembers(WordRelationGroupType groupType, List<Long> memberIds) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_rel_type_code", groupType.name());
		params.put("memberIds", memberIds);
		params.put("nrOfMembers", memberIds.size());
		return MapUtils.isEmpty(basicDbService.queryForMap(sqls.getSqlSelectWordGroupWithMembers(), params));
	}

	protected Long createWordRelationGroup(WordRelationGroupType groupType) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_rel_type_code", groupType.name());
		return basicDbService.create(WORD_RELATION_GROUP, params);
	}

	protected Long createWordRelationGroupMember(Long groupId, Long lexemeId) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word_group_id", groupId);
		params.put("word_id", lexemeId);
		return basicDbService.create(WORD_RELATION_GROUP_MEMBER, params);
	}

	protected void createWordRelation(Long wordId1, Long wordId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("word1_id", wordId1);
		relationParams.put("word2_id", wordId2);
		relationParams.put("word_rel_type_code", relationType);
		Long wordRelationId = basicDbService.createIfNotExists(WORD_RELATION, relationParams);

		if (wordRelationId != null) {
			createLifecycleLog(LifecycleLogOwner.WORD, wordId1, LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, wordRelationId, relationType);
		}
	}

	protected void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType) throws Exception {

		Map<String, Object> relationParams = new HashMap<>();
		relationParams.put("meaning1_id", meaningId1);
		relationParams.put("meaning2_id", meaningId2);
		relationParams.put("meaning_rel_type_code", relationType);
		Long meaningRelationId = basicDbService.createIfNotExists(MEANING_RELATION, relationParams);

		if (meaningRelationId != null) {
			createLifecycleLog(LifecycleLogOwner.MEANING, meaningId1, LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, meaningRelationId, relationType);
		}
	}

	protected void createLexemeRegister(Long lexemeId, String registerCode) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("lexeme_id", lexemeId);
		params.put("register_code", registerCode);
		Long lexemeRegisterId = basicDbService.createIfNotExists(LEXEME_REGISTER, params);

		if (lexemeRegisterId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, lexemeId, LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
		}
	}

	protected Long createSource(Source source) throws Exception {

		String concept = source.getExtSourceId();
		SourceType type = source.getType();

		Map<String, Object> tableRowParamMap = new HashMap<>();
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
		List<Map<String, Object>> sources = basicDbService.queryList(sqls.getSqlSelectSourceByTypeAndName(), tableRowParamMap);

		if (CollectionUtils.isEmpty(sources)) {
			return null;
		}
		Map<String, Object> sourceRecord = sources.get(0);
		Long sourceId = (Long) sourceRecord.get("id");
		return sourceId;
	}

	protected void createLifecycleLog(
			LifecycleLogOwner logOwner, Long ownerId, LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String entry) throws Exception {

		String eventBy = "Ekileks " + getDataset() + "-laadur";

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("entity_id", entityId);
		tableRowParamMap.put("entity_name", entity.name());
		tableRowParamMap.put("entity_prop", property.name());
		tableRowParamMap.put("event_type", eventType.name());
		tableRowParamMap.put("event_by", eventBy);
		tableRowParamMap.put("entry", entry);
		Long lifecycleLogId = basicDbService.create(LIFECYCLE_LOG, tableRowParamMap);

		if (LifecycleLogOwner.LEXEME.equals(logOwner)) {
			createLexemeLifecycleLog(ownerId, lifecycleLogId);
		} else if (LifecycleLogOwner.MEANING.equals(logOwner)) {
			createMeaningLifecycleLog(ownerId, lifecycleLogId);
		} else if (LifecycleLogOwner.WORD.equals(logOwner)) {
			createWordLifecycleLog(ownerId, lifecycleLogId);
		}
	}

	private void createMeaningLifecycleLog(Long meaningId, Long lifecycleLogId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(MEANING_LIFECYCLE_LOG, tableRowParamMap);
	}

	private void createWordLifecycleLog(Long wordId, Long lifecycleLogId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(WORD_LIFECYCLE_LOG, tableRowParamMap);
	}

	private void createLexemeLifecycleLog(Long lexemeId, Long lifecycleLogId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(LEXEME_LIFECYCLE_LOG, tableRowParamMap);
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

	private void appendToReport(String reportName, Object ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	private Long findDefinitionId(Long meaningId, String definition) throws Exception {
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("value", definition);
		Map<String, Object> result = basicDbService.select(DEFINITION, tableRowParamMap);
		return MapUtils.isEmpty(result) ? null : (Long) result.get("id");
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
}
