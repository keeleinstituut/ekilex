package eki.ekilex.service;

import static java.util.Collections.emptyList;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.constant.FormMode;
import eki.common.data.AbstractDataObject;
import eki.common.exception.DataLoadingException;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.ParadigmFormTuple;
import eki.ekilex.data.transform.WordParadigms;

@Service
public class MabService implements SystemConstant, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(MabService.class);

	private static final String DATASET_CODE_MAB = "mab";

	private static final String SQL_SELECT_WORDS_FOR_DATASET_BY_GUID = "sql/select_words_for_dataset_by_guid.sql";

	private static final String SQL_SELECT_PARADIGMS_FORMS_BY_WORD_IDS = "sql/select_paradigms_forms_by_word_ids.sql";

	@Autowired
	protected BasicDbService basicDbService;

	private Map<String, List<MabWordStat>> mabWordStats;

	private String sqlSelectWordsForDatasetByGuid;

	private String sqlSelectParadigmsFormsByWordIds;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORDS_FOR_DATASET_BY_GUID);
		sqlSelectWordsForDatasetByGuid = IOUtils.toString(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_PARADIGMS_FORMS_BY_WORD_IDS);
		sqlSelectParadigmsFormsByWordIds = IOUtils.toString(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();
	}

	@Transactional
	public void initialise() {
		logger.debug("Composing MAB lookup map");
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("dataset", DATASET_CODE_MAB);
		List<Map<String, Object>> mabWordsRaw = basicDbService.queryList(sqlSelectWordsForDatasetByGuid, tableRowParamMap);
		mabWordStats = mabWordsRaw.stream().map(row -> {
			Long wordId = (Long) row.get("word_id");
			String word = (String) row.get("word");
			Long paradigmCount = (Long) row.get("paradigm_count");
			MabWordStat mabWordStat = new MabWordStat(wordId, word, paradigmCount.intValue());
			return mabWordStat;
		}).collect(Collectors.groupingBy(MabWordStat::getWord));
		logger.debug("MAB service ready!");
	}

	public boolean isMabLoaded() {
		return MapUtils.isNotEmpty(mabWordStats);
	}

	public boolean homonymsExist(String word) throws Exception {
		if (MapUtils.isEmpty(mabWordStats)) {
			throw new DataLoadingException("MAB not loaded!");
		}
		return mabWordStats.containsKey(word);
	}

	public boolean isSingleHomonym(String word) throws Exception {
		if (!homonymsExist(word)) {
			return true;
		}
		List<MabWordStat> mabWordHomonyms = mabWordStats.get(word);
		boolean isSingleHomonym = mabWordHomonyms.size() == 1;
		return isSingleHomonym;
	}

	public List<Paradigm> getMatchingWordParadigms(String word, List<String> suggestedFormValues) throws Exception {

		List<WordParadigms> wordParadigmsList = getWordParadigms(word);
		if (isSingleHomonym(word)) {
			WordParadigms singleHomonymParadigms = wordParadigmsList.get(0);
			return singleHomonymParadigms.getParadigms();
		}
		if (CollectionUtils.isEmpty(suggestedFormValues)) {
			return Collections.emptyList();
		}
		WordParadigms matchingWordParadigms = null;
		List<Paradigm> paradigms;
		List<String> paradigmFormValues;
		int bestFormValuesMatchCount = -1;
		Collection<String> formValuesIntersection;
		for (WordParadigms wordParadigms : wordParadigmsList) {
			paradigms = wordParadigms.getParadigms();
			for (Paradigm paradigm : paradigms) {
				paradigmFormValues = paradigm.getFormValues();
				formValuesIntersection = CollectionUtils.intersection(suggestedFormValues, paradigmFormValues);
				if (formValuesIntersection.size() > bestFormValuesMatchCount) {
					bestFormValuesMatchCount = formValuesIntersection.size();
					matchingWordParadigms = wordParadigms;
				}
			}
		}
		if (matchingWordParadigms == null) {
			return Collections.emptyList();
		}
		return matchingWordParadigms.getParadigms();
	}

	private List<WordParadigms> getWordParadigms(String word) throws Exception {
		if (!homonymsExist(word)) {
			return emptyList();
		}
		List<MabWordStat> mabWordHomonyms = mabWordStats.get(word);
		List<Long> wordIds = mabWordHomonyms.stream().map(MabWordStat::getWordId).collect(Collectors.toList());
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordIds", wordIds);
		List<Map<String, Object>> paradigmFormsRaw = basicDbService.queryList(sqlSelectParadigmsFormsByWordIds, tableRowParamMap);
		Map<Long, List<ParadigmFormTuple>> wordParadigmsForms = paradigmFormsRaw.stream().map(row -> convert(row)).collect(Collectors.groupingBy(ParadigmFormTuple::getWordId));
		List<WordParadigms> wordParadigmsList = new ArrayList<>();
		for (Long wordId : wordIds) {
			List<ParadigmFormTuple> paradigmFormTuples = wordParadigmsForms.get(wordId);
			WordParadigms wordParadigms = new WordParadigms();
			wordParadigms.setWordValue(word);
			wordParadigms.setParadigms(new ArrayList<>());
			wordParadigmsList.add(wordParadigms);
			Map<Long, Paradigm> paradigmMap = new HashMap<>();
			for (ParadigmFormTuple tuple : paradigmFormTuples) {
				Long paradigmId = tuple.getParadigmId();
				Paradigm paradigm = paradigmMap.get(paradigmId);
				if (paradigm == null) {
					paradigm = new Paradigm();
					paradigm.setInflectionTypeNr(tuple.getInflectionTypeNr());
					paradigm.setInflectionType(tuple.getInflectionType());
					paradigm.setForms(new ArrayList<>());
					paradigm.setFormValues(new ArrayList<>());
					wordParadigms.getParadigms().add(paradigm);
					paradigmMap.put(paradigmId, paradigm);
				}
				Form form = new Form();
				form.setMode(tuple.getMode());
				form.setMorphGroup1(tuple.getMorphGroup1());
				form.setMorphGroup2(tuple.getMorphGroup2());
				form.setMorphGroup3(tuple.getMorphGroup3());
				form.setDisplayLevel(tuple.getDisplayLevel());
				form.setMorphCode(tuple.getMorphCode());
				form.setMorphExists(tuple.getMorphExists());
				form.setValue(tuple.getValue());
				form.setComponents(tuple.getComponents());
				form.setDisplayForm(tuple.getDisplayForm());
				form.setVocalForm(tuple.getVocalForm());
				form.setSoundFile(tuple.getSoundFile());
				form.setOrderBy(tuple.getOrderBy());
				paradigm.getForms().add(form);
			}
			for (Paradigm paradigm : wordParadigms.getParadigms()) {
				List<String> formValues = paradigm.getForms().stream().map(Form::getValue).collect(Collectors.toList());
				paradigm.setFormValues(formValues);
			}
		}
		return wordParadigmsList;
	}

	private ParadigmFormTuple convert(Map<String, Object> row) {
		Long wordId = (Long) row.get("word_id");
		Long paradigmId = (Long) row.get("paradigm_id");
		String inflectionTypeNr = (String) row.get("inflection_type_nr");
		String inflectionType = (String) row.get("inflection_type");
		String modeStr = (String) row.get("mode");
		FormMode mode = FormMode.valueOf(modeStr);
		String morphGroup1 = (String) row.get("morph_group1");
		String morphGroup2 = (String) row.get("morph_group2");
		String morphGroup3 = (String) row.get("morph_group3");
		Integer displayLevel = (Integer) row.get("display_level");
		String morphCode = (String) row.get("morph_code");
		Boolean morphExists = (Boolean) row.get("morph_exists");
		String value = (String) row.get("value");
		PgArray componentsArrayField = (PgArray) row.get("components");
		String[] components = null;
		if (componentsArrayField != null) {
			try {
				components = (String[]) componentsArrayField.getArray();
			} catch (SQLException e) {
				logger.error("Failed to read form components", e);
			}
		}
		String displayForm = (String) row.get("display_form");
		String vocalForm = (String) row.get("vocal_form");
		String soundFile = (String) row.get("sound_file");
		Integer orderBy = (Integer) row.get("order_by");
		ParadigmFormTuple tuple = new ParadigmFormTuple();
		tuple.setWordId(wordId);
		tuple.setParadigmId(paradigmId);
		tuple.setInflectionTypeNr(inflectionTypeNr);
		tuple.setInflectionType(inflectionType);
		tuple.setMode(mode);
		tuple.setMorphGroup1(morphGroup1);
		tuple.setMorphGroup2(morphGroup2);
		tuple.setMorphGroup3(morphGroup3);
		tuple.setDisplayLevel(displayLevel);
		tuple.setMorphCode(morphCode);
		tuple.setMorphExists(morphExists);
		tuple.setValue(value);
		tuple.setComponents(components);
		tuple.setDisplayForm(displayForm);
		tuple.setVocalForm(vocalForm);
		tuple.setSoundFile(soundFile);
		tuple.setOrderBy(orderBy);
		return tuple;
	}

	class MabWordStat extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long wordId;

		private String word;

		private int paradigmCount;

		public MabWordStat(Long wordId, String word, int paradigmCount) {
			this.wordId = wordId;
			this.word = word;
			this.paradigmCount = paradigmCount;
		}

		public Long getWordId() {
			return wordId;
		}

		public String getWord() {
			return word;
		}

		public int getParadigmCount() {
			return paradigmCount;
		}
	}
}
