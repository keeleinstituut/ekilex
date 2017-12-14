package eki.ekilex.runner;

import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Rection;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;
import eki.ekilex.service.WordMatcherService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class PsvLoaderRunner extends AbstractLoaderRunner {

	private final String dataLang = "est";
	private final String wordDisplayFormStripChars = ".+'`()¤:_|[]/";
	private final String formStrCleanupChars = ".()¤:_|[]/̄̆̇’\"'`´,;–+=";
	private final String defaultWordMorphCode = "SgN";
	private final String defaultRectionValue = "-";
	private final static String ARTICLES_REPORT_NAME = "keywords";
	private final static String SYNONYMS_REPORT_NAME = "synonyms";
	private final static String ANTONYMS_REPORT_NAME = "antonyms";
	private final static String BASIC_WORDS_REPORT_NAME = "basic_words";
	private final static String COMPOUND_WORDS_REPORT_NAME = "compound_words";
	private final static String REFERENCE_FORMS_REPORT_NAME = "reference_forms";
	private final static String MEANING_REFERENCES_REPORT_NAME = "meaning_references";
	private final static String JOINT_REFERENCES_REPORT_NAME = "joint_references";
	private final static String COMPOUND_REFERENCES_REPORT_NAME = "compound_references";
	private final static String VORMELS_REPORT_NAME = "vormels";
	private final static String SINGLE_FORMS_REPORT_NAME = "single_forms";
	private final static String COMPOUND_FORMS_REPORT_NAME = "compound_forms";
	private final static String WORD_COMPARATIVES_REPORT_NAME = "word_comparatives";
	private final static String WORD_SUPERLATIVES_REPORT_NAME = "word_superlatives";

	private final static String sqlFormsOfTheWord = "select f.* from " + FORM + " f, " + PARADIGM + " p where p.word_id = :word_id and f.paradigm_id = p.id";
	private final static String sqlUpdateSoundFiles = "update " + FORM + " set sound_file = :soundFile where id in "
			+ "(select f.id from " + FORM + " f join " + PARADIGM + " p on f.paradigm_id = p.id where f.value = :formValue and p.word_id = :wordId)";
	private final static String sqlWordLexemesByDataset = "select l.* from " + LEXEME + " l join " + LEXEME_DATASET + " ld on ld.lexeme_id = l.id "
			+ "where l.word_id = :wordId and ld.dataset_code = :dataset";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private Map<String, String> posCodes;
	private Map<String, String> derivCodes;
	private Map<String, String> lexemeTypes;
	private Map<String, String> processStateCodes;
	private ReportComposer reportComposer;
	private boolean reportingEnabled;

	@Autowired
	private WordMatcherService wordMatcherService;

	@Override
	void initialise() throws Exception {

		lexemeTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_LIIKTYYP);
		derivCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_DKTYYP);
		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
		processStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_ASTYYP);
	}

	@Transactional
	public void execute(
			String dataXmlFilePath,
			String dataset,
			Map<String, List<Paradigm>> wordParadigmsMap,
			boolean isAddReporting) throws Exception {

		final String articleExp = "/x:sr/x:A";
		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";
		final String reportingIdExp = "x:P/x:mg/x:m"; // use first word as id for reporting

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		reportingEnabled = isAddReporting;

		if (reportingEnabled) {
			reportComposer = new ReportComposer("PSV import",
					ARTICLES_REPORT_NAME, SYNONYMS_REPORT_NAME, ANTONYMS_REPORT_NAME, BASIC_WORDS_REPORT_NAME, REFERENCE_FORMS_REPORT_NAME,
					COMPOUND_WORDS_REPORT_NAME, REFERENCE_FORMS_REPORT_NAME, MEANING_REFERENCES_REPORT_NAME, JOINT_REFERENCES_REPORT_NAME,
					COMPOUND_REFERENCES_REPORT_NAME, VORMELS_REPORT_NAME, SINGLE_FORMS_REPORT_NAME, COMPOUND_FORMS_REPORT_NAME,
					WORD_COMPARATIVES_REPORT_NAME, WORD_SUPERLATIVES_REPORT_NAME);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);
		Context context = new Context();

		writeToLogFile("Artiklite töötlus", "", "");
		for (Element articleNode : articleNodes) {
			String guid = extractGuid(articleNode);
			List<WordData> newWords = new ArrayList<>();
			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			Element reportingIdNode = (Element) articleNode.selectSingleNode(reportingIdExp);
			String reportingId = reportingIdNode != null ? reportingIdNode.getTextTrim() : "";
			processArticleHeader(guid, reportingId, headerNode, newWords, context, wordParadigmsMap, wordDuplicateCount, dataset);

			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(reportingId, contentNode, newWords, dataset, lexemeDuplicateCount, context);
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
			context.importedWords.addAll(newWords);
		}

		processSynonyms(context, dataset);
		processAntonyms(context, dataset);
		processBasicWords(context, dataset);
		processReferenceForms(context);
		processCompoundWords(context, dataset);
		processMeaningReferences(context, dataset);
		processJointReferences(context, dataset);
		processCompoundReferences(context, dataset);
		processVormels(context, dataset);
		processSingleForms(context, dataset);
		processCompoundForms(context, dataset);
		processWordComparatives(context);
		processWordSuperlatives(context);

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);

		if (reportComposer != null) {
			reportComposer.end();
		}
		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private String extractGuid(Element node) {

		final String articleGuidExp = "x:G";

		Element guidNode = (Element) node.selectSingleNode(articleGuidExp);
		return guidNode != null ? StringUtils.lowerCase(guidNode.getTextTrim()) : null;
	}

	private void processWordSuperlatives(Context context) throws Exception {

		logger.debug("Starting word superlatives processing.");
		setActivateReport(WORD_SUPERLATIVES_REPORT_NAME);
		writeToLogFile("Ülivõrrete töötlus <x:kmp>", "", "");

		long count = 0;
		List<WordData> words = new ArrayList<>();
		words.addAll(context.importedWords.stream().filter(wd -> !wd.superlatives.isEmpty()).collect(Collectors.toList()));

		for (WordData wordData : words) {
			for (String superlative : wordData.superlatives) {
				count++;
				Long superlativeId;
				List<WordData> existingWords = context.importedWords.stream()
						.filter(w -> superlative.equals(w.value))
						.collect(Collectors.toList());
				if (existingWords.isEmpty()) {
					logger.debug("Creating word {}", superlative);
					WordData createdWord = createDefaultWordFrom(superlative);
					context.importedWords.add(createdWord);
					superlativeId = createdWord.id;
				} else {
					superlativeId = existingWords.get(0).id;
				}
				createWordRelation(wordData.id, superlativeId, "superl");
			}
		}
		logger.debug("Word superlatives processing done, {}.", count);
	}

	private void processWordComparatives(Context context) throws Exception {

		logger.debug("Starting word comparatives processing.");
		setActivateReport(WORD_COMPARATIVES_REPORT_NAME);
		writeToLogFile("Keskvõrrete töötlus <x:kmp>", "", "");

		long count = 0;
		List<WordData> words = new ArrayList<>();
		words.addAll(context.importedWords.stream().filter(wd -> !wd.comparatives.isEmpty()).collect(Collectors.toList()));

		for (WordData wordData : words) {
			for (String comparative : wordData.comparatives) {
				count++;
				Long comparativeId;
				List<WordData> existingWords = context.importedWords.stream()
						.filter(w -> comparative.equals(w.value))
						.collect(Collectors.toList());
				if (existingWords.isEmpty()) {
					logger.debug("Creating word {}", comparative);
					WordData createdWord = createDefaultWordFrom(comparative);
					context.importedWords.add(createdWord);
					comparativeId = createdWord.id;
				} else {
					comparativeId = existingWords.get(0).id;
				}
				createWordRelation(wordData.id, comparativeId, "komp");
			}
		}
		logger.debug("Word comparatives processing done, {}.", count);
	}

	private void processCompoundForms(Context context, String dataset) throws Exception {

		logger.debug("Found {} compound forms.", context.compoundForms.size());
		setActivateReport(COMPOUND_FORMS_REPORT_NAME);
		writeToLogFile("Ühendite töötlus <x:pyh>", "", "");

		for (LexemeToWordData compoundFormData : context.compoundForms) {
			List<WordData> existingWords = context.importedWords.stream()
					.filter(w -> compoundFormData.word.equals(w.value))
					.collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, compoundFormData, context, dataset);
			if (lexemeId != null) {
				createLexemeRelation(compoundFormData.lexemeId, lexemeId, "pyh", dataset);
			}
		}
		logger.debug("Compound form processing done.");
	}

	private void processSingleForms(Context context, String dataset) throws Exception {

		logger.debug("Found {} single forms.", context.singleForms.size());
		setActivateReport(SINGLE_FORMS_REPORT_NAME);
		writeToLogFile("Üksikvormide töötlus <x:yvr>", "", "");

		for (LexemeToWordData singleFormData : context.singleForms) {
			List<WordData> existingWords = context.importedWords.stream()
					.filter(w -> singleFormData.word.equals(w.value))
					.collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, singleFormData, context, dataset);
			if (lexemeId != null) {
				createLexemeRelation(singleFormData.lexemeId, lexemeId, "yvr", dataset);
			}
		}
		logger.debug("Single form processing done.");
	}

	private void processVormels(Context context, String dataset) throws Exception {

		logger.debug("Found {} vormels.", context.vormels.size());
		setActivateReport(VORMELS_REPORT_NAME);
		writeToLogFile("Vormelite töötlus <x:vor>", "", "");

		for (LexemeToWordData vormelData : context.vormels) {
			List<WordData> existingWords = context.importedWords.stream()
					.filter(w -> vormelData.word.equals(w.value))
					.collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, vormelData, context, dataset);
			if (lexemeId != null) {
				createLexemeRelation(vormelData.lexemeId, lexemeId, "vor", dataset);
			}
		}
		logger.debug("Vormel processing done.");
	}

	private void processCompoundReferences(Context context, String dataset) throws Exception {

		logger.debug("Found {} compound references.", context.compoundReferences.size());
		setActivateReport(COMPOUND_REFERENCES_REPORT_NAME);
		writeToLogFile("Ühendiviidete töötlus <x:yhvt>", "", "");

		for (LexemeToWordData compoundRefData : context.compoundReferences) {
			List<WordData> existingWords = context.importedWords.stream()
					.filter(w -> compoundRefData.word.equals(w.value))
					.collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, compoundRefData, context, dataset, compoundRefData.lexemeType);
			if (lexemeId != null) {
				createLexemeRelation(compoundRefData.lexemeId, lexemeId, "yhvt", dataset);
			}
		}
		logger.debug("Compound references processing done.");
	}

	private void processJointReferences(Context context, String dataset) throws Exception {

		logger.debug("Found {} joint references.", context.jointReferences.size());
		setActivateReport(JOINT_REFERENCES_REPORT_NAME);
		writeToLogFile("Ühisviidete töötlus <x:yvt>", "", "");

		for (LexemeToWordData jointRefData : context.jointReferences) {
			List<WordData> existingWords = context.importedWords.stream()
					.filter(w -> jointRefData.word.equals(w.value))
					.filter(w -> jointRefData.homonymNr == 0 || jointRefData.homonymNr == w.homonymNr)
					.collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, jointRefData, context, dataset);
			if (lexemeId != null) {
				String relationType = "yvt:" + jointRefData.relationType;
				createLexemeRelation(jointRefData.lexemeId, lexemeId, relationType, dataset);
			}
		}
		logger.debug("Joint references processing done.");
	}

	private void processMeaningReferences(Context context, String dataset) throws Exception {

		logger.debug("Found {} meaning references.", context.meaningReferences.size());
		setActivateReport(MEANING_REFERENCES_REPORT_NAME);
		writeToLogFile("Tähendusviidete töötlus <x:tvt>", "", "");

		for (LexemeToWordData meaningRefData : context.meaningReferences) {
			List<WordData> existingWords = context.importedWords.stream()
					.filter(w -> meaningRefData.word.equals(w.value))
					.filter(w -> meaningRefData.homonymNr == 0 || meaningRefData.homonymNr == w.homonymNr)
					.collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, meaningRefData, context, dataset);
			if (lexemeId != null) {
				String relationType = "tvt:" + meaningRefData.relationType;
				createLexemeRelation(meaningRefData.lexemeId, lexemeId, relationType, dataset);
			}
		}
		logger.debug("Meaning references processing done.");
	}

	private void processCompoundWords(Context context, String dataset) throws Exception {

		logger.debug("Found {} compound words.", context.compoundWords.size());
		setActivateReport(COMPOUND_WORDS_REPORT_NAME);
		writeToLogFile("Liitsõnade töötlus <x:ls>", "", "");

		for (LexemeToWordData compData : context.compoundWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> compData.word.equals(w.value)).collect(Collectors.toList());
			Long lexemeId = findOrCreateLexemeForWord(existingWords, compData, context, dataset);
			if (lexemeId != null) {
				createLexemeRelation(compData.lexemeId, lexemeId, "comp", dataset);
			}
		}
		logger.debug("Compound words processing done.");
	}

	private Long findOrCreateLexemeForWord(List<WordData> existingWords, LexemeToWordData data, Context context, String dataset) throws Exception {
		return findOrCreateLexemeForWord(existingWords, data, context, dataset, data.lexemeType);
	}

	private Long findOrCreateLexemeForWord(
			List<WordData> existingWords, LexemeToWordData data, Context context, String dataset, String lexemeType) throws Exception {

		if (existingWords.size() > 1) {
			logger.debug("Found more than one word : {}.", data.word);
			writeToLogFile(data.reportingId, "Leiti rohkem kui üks vaste sõnale", data.word);
		}
		Long lexemeId;
		if (existingWords.isEmpty()) {
			logger.debug("No word found, adding word with objects : {}.", data.word);
			lexemeId = createLexemeAndRelatedObjects(data, context, dataset, lexemeType);
			if (!data.usages.isEmpty()) {
				logger.debug("Usages found, adding them");
				String rectionValue = data.rection == null ? defaultRectionValue : data.rection.getValue();
				Long rectionId = createLexemeFreeform(lexemeId, FreeformType.RECTION, rectionValue, dataLang);
				for (Usage usage : data.usages) {
					createUsage(rectionId, usage);
				}
				if (data.rection != null && isNotEmpty(data.rection.getType())) {
					createFreeformClassifier(FreeformType.RECTION_TYPE, rectionId, data.rection.getType());
				}
			}
		} else {
			lexemeId = findLexemeIdForWord(existingWords.get(0).id, data, lexemeType, dataset);
			if (!data.usages.isEmpty()) {
				logger.debug("Usages found for word, skipping them : {}.", data.word);
				writeToLogFile(data.reportingId, "Leiti kasutusnäited olemasolevale ilmikule", data.word);
			}
		}
		return lexemeId;
	}

	private Long createLexemeAndRelatedObjects(LexemeToWordData wordData, Context context, String dataset, String lexemeType) throws Exception {

		WordData newWord = createDefaultWordFrom(wordData.word);
		context.importedWords.add(newWord);
		Long meaningId = createMeaning(dataset);
		Lexeme lexeme = new Lexeme();
		lexeme.setMeaningId(meaningId);
		lexeme.setWordId(newWord.id);
		lexeme.setLevel1(0);
		lexeme.setLevel2(0);
		lexeme.setLevel3(0);
		lexeme.setType(lexemeType);
		if (isNotBlank(wordData.definition)) {
			createDefinition(meaningId, wordData.definition, dataLang, dataset);
		}
		return createLexeme(lexeme, dataset);
	}

	private WordData createDefaultWordFrom(String wordValue) throws Exception {

		WordData createdWord = new WordData();
		createdWord.value = wordValue;
		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;
		Word word = new Word(wordValue, dataLang, null, null, null, null, homonymNr, defaultWordMorphCode, null);
		createdWord.id = saveWord(word, null, null, null);
		return createdWord;
	}

	private Long findLexemeIdForWord(Long wordId, LexemeToWordData data, String lexemeType, String dataset) throws Exception {

		Long lexemeId = null;
		Map<String, Object> params = new HashMap<>();
		params.put("wordId", wordId);
		params.put("dataset", dataset);
		if (data.lexemeLevel1 != 0) {
			params.put("level1", data.lexemeLevel1);
		}
		List<Map<String, Object>> lexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
		if (data.lexemeLevel1 != 0) {
			lexemes = lexemes.stream().filter(l -> (Integer)l.get("level1") == data.lexemeLevel1).collect(Collectors.toList());
		}
		if (lexemes.isEmpty()) {
			logger.debug("Lexeme not found for word : {}.", data.word);
			writeToLogFile(data.reportingId, "Ei leitud ilmikut sõnale", data.word);
		} else {
			if (lexemes.size() > 1) {
				logger.debug("Found more than one lexeme for : {}.", data.word);
				writeToLogFile(data.reportingId, "Leiti rohkem kui üks ilmik sõnale", data.word);
			}
			lexemeId = (Long) lexemes.get(0).get("id");
			if (isNotEmpty(lexemeType)) {
				String existingLexemeType = (String) lexemes.get(0).get("type_code");
				if (!Objects.equals(existingLexemeType, lexemeType)) {
					logger.debug("Lexeme types do not match : {}, {} != {}.", data.word, lexemeType, existingLexemeType);
					writeToLogFile(data.reportingId, "Ilmikute tüübid on erinevad", data.word + ", " + lexemeType + " != " + existingLexemeType);
				}
			}
		}
		return lexemeId;
	}

	private void processReferenceForms(Context context) throws Exception {

		logger.debug("Found {} reference forms.", context.referenceForms.size());
		setActivateReport(REFERENCE_FORMS_REPORT_NAME);
		writeToLogFile("Vormid mis viitavad põhisõnale töötlus <x:mvt>", "", "");

		for (ReferenceFormData referenceForm : context.referenceForms) {
			Optional<WordData> word = context.importedWords.stream()
					.filter(w -> referenceForm.wordValue.equals(w.value) && referenceForm.wordHomonymNr == w.homonymNr).findFirst();
			if (word.isPresent()) {
				Map<String, Object> params = new HashMap<>();
				params.put("word_id", word.get().id);
				List<Map<String, Object>> forms = basicDbService.queryList(sqlFormsOfTheWord, params);
				List<Map<String, Object>> wordForms = forms.stream().filter(f -> (boolean) f.get("is_word")).collect(Collectors.toList());
				if (wordForms.size() > 1) {
					logger.debug("More than one word form found for word : {}, id : {}", referenceForm.wordValue, word.get().id);
					continue;
				}
				Map<String, Object> wordForm = wordForms.get(0);
				Optional<Map<String, Object>> form = forms.stream().filter(f -> referenceForm.formValue.equals(f.get("value"))).findFirst();
				if (!form.isPresent()) {
					logger.debug("Form not found for {}, {} -> {}", referenceForm.reportingId, referenceForm.formValue, referenceForm.wordValue);
					writeToLogFile(referenceForm.reportingId, "Vormi ei leitud", referenceForm.formValue + " -> " + referenceForm.wordValue);
					continue;
				}
				params.clear();
				params.put("form1_id", form.get().get("id"));
				params.put("form2_id", wordForm.get("id"));
				params.put("form_rel_type_code", "mvt");
				basicDbService.create(FORM_RELATION, params);
			} else {
				logger.debug("Word not found {}, {}, {}", referenceForm.reportingId, referenceForm.wordValue, referenceForm.wordHomonymNr);
				writeToLogFile(referenceForm.reportingId, "Sihtsõna ei leitud", referenceForm.wordValue + ", " + referenceForm.wordHomonymNr);
			}
		}
		logger.debug("Reference forms processing done.");
	}

	private void processBasicWords(Context context, String dataset) throws Exception {

		logger.debug("Found {} basic words.", context.basicWords.size());
		setActivateReport(BASIC_WORDS_REPORT_NAME);
		writeToLogFile("Märksõna põhisõna seoste töötlus <x:ps>", "", "");

		for (WordData basicWord : context.basicWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> basicWord.value.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(basicWord.value, basicWord.homonymNr, existingWords, basicWord.reportingId);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("wordId", basicWord.id);
				params.put("dataset", dataset);
				List<Map<String, Object>> secondaryWordLexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
				for (Map<String, Object> secondaryWordLexeme : secondaryWordLexemes) {
					params.put("wordId", wordId);
					List<Map<String, Object>> lexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
					for (Map<String, Object> lexeme : lexemes) {
						createLexemeRelation((Long) secondaryWordLexeme.get("id"), (Long) lexeme.get("id"), "head", dataset);
					}
				}
			}
		}
		logger.debug("Basic words processing done.");
	}

	private void processAntonyms(Context context, String dataset) throws Exception {

		logger.debug("Found {} antonyms.", context.antonyms.size());
		setActivateReport(ANTONYMS_REPORT_NAME);
		writeToLogFile("Antonüümide töötlus <x:ant>", "", "");

		for (LexemeToWordData antonymData : context.antonyms) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> antonymData.word.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(antonymData.word, antonymData.homonymNr, existingWords, antonymData.reportingId);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("wordId", wordId);
				params.put("dataset", dataset);
				try {
					List<Map<String, Object>> lexemeObjects = basicDbService.queryList(sqlWordLexemesByDataset, params);
					Optional<Map<String, Object>> lexemeObject =
							lexemeObjects.stream().filter(l -> (Integer)l.get("level1") == antonymData.lexemeLevel1).findFirst();
					if (lexemeObject.isPresent()) {
						createLexemeRelation(antonymData.lexemeId, (Long) lexemeObject.get().get("id"), "ant", dataset);
					} else {
						logger.debug("Lexeme not found for antonym : {}, lexeme level1 : {}.", antonymData.word, antonymData.lexemeLevel1);
						writeToLogFile(antonymData.reportingId, "Ei leitud ilmikut antaonüümile", antonymData.word + ", level1 " + antonymData.lexemeLevel1);
					}
				} catch (Exception e) {
					logger.error("More than one lexeme {}, {}", antonymData.word, wordId);
				}
			}
		}
		logger.debug("Antonyms import done.");
	}

	private void processSynonyms(Context context, String dataset) throws Exception {

		logger.debug("Found {} synonyms", context.synonyms.size());
		setActivateReport(SYNONYMS_REPORT_NAME);
		writeToLogFile("Sünonüümide töötlus <x:syn>", "", "");

		Count newSynonymWordCount = new Count();
		for (SynonymData synonymData : context.synonyms) {
			Long wordId;
			List<WordData> existingWords = context.importedWords.stream().filter(w -> synonymData.word.equals(w.value)).collect(Collectors.toList());
			if (existingWords.isEmpty()) {
				WordData newWord = createDefaultWordFrom(synonymData.word);
				context.importedWords.add(newWord);
				newSynonymWordCount.increment();
				wordId = newWord.id;
			} else {
				wordId = getWordIdFor(synonymData.word, synonymData.homonymNr, existingWords, synonymData.reportingId);
				if (wordId == null)
					continue;
			}
			Lexeme lexeme = new Lexeme();
			lexeme.setWordId(wordId);
			lexeme.setMeaningId(synonymData.meaningId);
			lexeme.setLevel1(0);
			lexeme.setLevel2(0);
			lexeme.setLevel3(0);
			createLexeme(lexeme, dataset);
		}
		logger.debug("Synonym words created {}", newSynonymWordCount.getValue());
		logger.debug("Synonyms import done.");
	}

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words, String reportingId) throws Exception {

		Long wordId = null;
		if (words.size() > 1) {
			Optional<WordData> matchingWord = words.stream().filter(w -> w.homonymNr == homonymNr).findFirst();
			if (matchingWord.isPresent()) {
				wordId = matchingWord.get().id;
			} else {
				logger.debug("No matching word was found for {} word {}, {}", reportingId, wordValue, homonymNr);
				writeToLogFile(reportingId, "Ei leitud sihtsõna", wordValue + " : " + homonymNr);
			}
		} else {
			wordId = words.get(0).id;
		}
		return wordId;
	}

	private void processArticleContent(String reportingId, Element contentNode, List<WordData> newWords, String dataset, Count lexemeDuplicateCount,
			Context context) throws Exception {

		final String meaningNumberGroupExp = "x:tp";
		final String lexemeLevel1Attr = "tnr";
		final String meaningGroupExp = "x:tg";
		final String usageGroupExp = "x:ng";
		final String definitionValueExp = "x:dg/x:d";
		final String commonInfoNodeExp = "x:tyg2";
		final String lexemePosCodeExp = "x:grg/x:sl";
		final String meaningExternalIdExp = "x:tpid";
		final String learnerCommentExp = "x:qkom";
		final String imageNameExp = "x:plp/x:plg/x:plf";
		final String posAsTyypAttr = "as";

		List<Element> meaningNumberGroupNodes = contentNode.selectNodes(meaningNumberGroupExp);
		List<LexemeToWordData> jointReferences = extractJointReferences(contentNode, reportingId);
		List<LexemeToWordData> compoundReferences = extractCompoundReferences(contentNode, reportingId);
		Element commonInfoNode = (Element) contentNode.selectSingleNode(commonInfoNodeExp);
		List<LexemeToWordData> articleVormels = extractVormels(commonInfoNode);

		for (Element meaningNumberGroupNode : meaningNumberGroupNodes) {
			saveSymbol(meaningNumberGroupNode, context, reportingId);
			WordData abbreviation = processAbbreviation(meaningNumberGroupNode, context);
			String lexemeLevel1Str = meaningNumberGroupNode.attributeValue(lexemeLevel1Attr);
			Integer lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);
			List<Element> meaingGroupNodes = meaningNumberGroupNode.selectNodes(meaningGroupExp);
			List<String> compoundWords = extractCompoundWords(meaningNumberGroupNode);
			List<LexemeToWordData> meaningReferences = extractMeaningReferences(meaningNumberGroupNode, reportingId);
			List<LexemeToWordData> vormels = extractVormels(meaningNumberGroupNode);
			List<LexemeToWordData> singleForms = extractSingleForms(meaningNumberGroupNode);
			List<LexemeToWordData> compoundForms = extractCompoundForms(meaningNumberGroupNode, reportingId);
			List<Long> newLexemes = new ArrayList<>();
			List<Element> posCodeNodes = meaningNumberGroupNode.selectNodes(lexemePosCodeExp);
			List<PosData> meaningPosCodes = new ArrayList<>();
			for (Element posCodeNode : posCodeNodes) {
				PosData posData = new PosData();
				posData.code = posCodeNode.getTextTrim();
				posData.processStateCode = posCodeNode.attributeValue(posAsTyypAttr);
				meaningPosCodes.add(posData);
			}
			Element meaningExternalIdNode = (Element) meaningNumberGroupNode.selectSingleNode(meaningExternalIdExp);
			String meaningExternalId = meaningExternalIdNode == null ? null : meaningExternalIdNode.getTextTrim();
			Element learnerCommentNode = (Element) meaningNumberGroupNode.selectSingleNode(learnerCommentExp);
			String learnerComment = learnerCommentNode == null ? null : learnerCommentNode.getTextTrim();
			Element imageNameNode = (Element) meaningNumberGroupNode.selectSingleNode(imageNameExp);
			String imageName = imageNameNode == null ? null : imageNameNode.getTextTrim();

			for (Element meaningGroupNode : meaingGroupNodes) {
				List<Element> usageGroupNodes = meaningGroupNode.selectNodes(usageGroupExp);
				List<Usage> usages = extractUsages(usageGroupNodes);

				Long meaningId = createMeaning(dataset);
				if (isNotEmpty(meaningExternalId)) {
					createMeaningFreeform(meaningId, FreeformType.MEANING_EXTERNAL_ID, meaningExternalId);
				}
				if (isNotEmpty(learnerComment)) {
					createMeaningFreeform(meaningId, FreeformType.LEARNER_COMMENT, learnerComment);
				}
				if (isNotEmpty(imageName)) {
					createMeaningFreeform(meaningId, FreeformType.IMAGE_FILE, imageName);
				}
				if (abbreviation != null) {
					addAbbreviationLexeme(abbreviation, meaningId, dataset);
				}

				List<Element> definitionValueNodes = meaningGroupNode.selectNodes(definitionValueExp);
				saveDefinitions(definitionValueNodes, meaningId, dataLang, dataset);
				if (definitionValueNodes.size() > 1) {
					writeToLogFile(reportingId, "Leitud rohkem kui üks seletus <x:d>", newWords.get(0).value);
				}

				List<SynonymData> meaningSynonyms = extractSynonyms(reportingId, meaningGroupNode, meaningId);
				context.synonyms.addAll(meaningSynonyms);

				List<LexemeToWordData> meaningAntonyms = extractAntonyms(meaningGroupNode, reportingId);

				int lexemeLevel2 = 0;
				for (WordData newWordData : newWords) {
					lexemeLevel2++;
					Lexeme lexeme = new Lexeme();
					lexeme.setWordId(newWordData.id);
					lexeme.setType(newWordData.lexemeType);
					lexeme.setMeaningId(meaningId);
					lexeme.setLevel1(lexemeLevel1);
					lexeme.setLevel2(lexemeLevel2);
					lexeme.setLevel3(0);
					lexeme.setFrequencyGroup(newWordData.frequencyGroup);
					Long lexemeId = createLexeme(lexeme, dataset);
					if (lexemeId == null) {
						lexemeDuplicateCount.increment();
					} else {
						saveRectionsAndUsages(meaningNumberGroupNode, lexemeId, usages);
						savePosAndDeriv(lexemeId, newWordData, meaningPosCodes, reportingId);
						saveGrammars(meaningNumberGroupNode, lexemeId, newWordData);
						for (LexemeToWordData meaningAntonym : meaningAntonyms) {
							LexemeToWordData antonymData = meaningAntonym.copy();
							antonymData.lexemeId = lexemeId;
							antonymData.reportingId = reportingId;
							context.antonyms.add(antonymData);
						}
						for (String compoundWord : compoundWords) {
							LexemeToWordData compData = new LexemeToWordData();
							compData.word = compoundWord;
							compData.lexemeId = lexemeId;
							compData.reportingId = reportingId;
							context.compoundWords.add(compData);
						}
						for (LexemeToWordData meaningReference : meaningReferences) {
							LexemeToWordData referenceData = meaningReference.copy();
							referenceData.lexemeId = lexemeId;
							referenceData.reportingId = reportingId;
							context.meaningReferences.add(referenceData);
						}
						for (LexemeToWordData vormel : vormels) {
							LexemeToWordData vormelData = vormel.copy();
							vormelData.lexemeId = lexemeId;
							vormelData.reportingId = reportingId;
							context.vormels.add(vormelData);
						}
						for (LexemeToWordData singleForm : singleForms) {
							LexemeToWordData singleFormData = singleForm.copy();
							singleFormData.lexemeId = lexemeId;
							singleFormData.reportingId = reportingId;
							context.singleForms.add(singleFormData);
						}
						for (LexemeToWordData compoundForm : compoundForms) {
							LexemeToWordData compoundFormData = compoundForm.copy();
							compoundFormData.lexemeId = lexemeId;
							compoundFormData.reportingId = reportingId;
							context.compoundForms.add(compoundFormData);
						}
						newLexemes.add(lexemeId);
					}
				}
			}
			for (Long lexemeId : newLexemes) {
				for (LexemeToWordData jointReference : jointReferences) {
					LexemeToWordData referenceData = jointReference.copy();
					referenceData.lexemeId = lexemeId;
					referenceData.reportingId = reportingId;
					context.jointReferences.add(referenceData);
				}
				for (LexemeToWordData compoundReference : compoundReferences) {
					LexemeToWordData referenceData = compoundReference.copy();
					referenceData.lexemeId = lexemeId;
					referenceData.reportingId = reportingId;
					context.compoundReferences.add(referenceData);
				}
				for (LexemeToWordData vormel : articleVormels) {
					LexemeToWordData vormelData = vormel.copy();
					vormelData.lexemeId = lexemeId;
					vormelData.reportingId = reportingId;
					context.vormels.add(vormelData);
				}
			}
		}
	}

	private void addAbbreviationLexeme(WordData abbreviation, Long meaningId, String dataset) throws Exception {
		Lexeme lexeme = new Lexeme();
		lexeme.setMeaningId(meaningId);
		lexeme.setWordId(abbreviation.id);
		lexeme.setLevel1(0);
		lexeme.setLevel2(0);
		lexeme.setLevel3(0);
		lexeme.setType(lexemeTypes.get("l"));
		createLexeme(lexeme, dataset);
	}

	private WordData processAbbreviation(Element node, Context context) throws Exception {

		final String abbreviationExp = "x:lyh";
		Element abbreviationNode = (Element) node.selectSingleNode(abbreviationExp);
		if (abbreviationNode == null) {
			return null;
		}
		String abbreviationValue = abbreviationNode.getTextTrim();
		Optional<WordData> abbreviation = context.importedWords.stream().filter(w -> w.value.equals(abbreviationValue)).findFirst();
		if (abbreviation.isPresent()) {
			return abbreviation.get();
		} else {
			WordData abbrData =  createDefaultWordFrom(abbreviationValue);
			context.importedWords.add(abbrData);
			return abbrData;
		}
	}

	private List<LexemeToWordData> extractCompoundForms(Element node, String reportingId) throws Exception {

		final String compoundFormGroupNodeExp = "x:pyp/x:pyg";
		final String compoundFormNodeExp = "x:pyh";
		final String definitionGroupNodeExp = "x:pyt";
		final String definitionExp = "x:pyd";
		final String usageExp = "x:ng/x:n";
		final String rectionExp = "x:rek";
		final String usageDefinitionExp = "x:nd";
		final String lexemeTypeAttr = "liik";

		List<LexemeToWordData> compoundForms = new ArrayList<>();
		List<Element> compoundFormGroupNodes = node.selectNodes(compoundFormGroupNodeExp);
		for (Element compoundFormGroupNode : compoundFormGroupNodes) {
			List<LexemeToWordData> forms = new ArrayList<>();
			List<Element> compoundFormNodes = compoundFormGroupNode.selectNodes(compoundFormNodeExp);
			for (Element compoundFormNode : compoundFormNodes) {
				LexemeToWordData data = new LexemeToWordData();
				data.word = compoundFormNode.getTextTrim();
				if (compoundFormNode.hasMixedContent()) {
					data.rection = extractRection((Element) compoundFormNode.selectSingleNode(rectionExp));
				}
				if (compoundFormNode.attributeValue(lexemeTypeAttr) != null) {
					String lexemeType = compoundFormNode.attributeValue(lexemeTypeAttr);
					data.lexemeType = lexemeTypes.get(lexemeType);
					if (data.lexemeType == null) {
						writeToLogFile(reportingId, "Tundmatu märksõnaliik", lexemeType);
					}
				}
				forms.add(data);
			}
			for (LexemeToWordData data : forms) {
				Element definitionGroupNodeNode = (Element) compoundFormGroupNode.selectSingleNode(definitionGroupNodeExp);
				Element definitionNode = (Element) definitionGroupNodeNode.selectSingleNode(definitionExp);
				if (definitionNode != null) {
					data.definition = definitionNode.getTextTrim();
				}
				List<Element> usageNodes = definitionGroupNodeNode.selectNodes(usageExp);
				for (Element usageNode : usageNodes) {
					Usage usage = new Usage();
					usage.setValue(usageNode.getTextTrim());
					if (usageNode.hasMixedContent()) {
						usage.setDefinition(usageNode.selectSingleNode(usageDefinitionExp).getText());
					}
					data.usages.add(usage);
				}
			}
			compoundForms.addAll(forms);
		}
		return compoundForms;
	}

	private Rection extractRection(Element rectionNode) {

		final String rectionTypeAttr = "rliik";
		final String rectionVariantAttr = "var";
		final String rectionOptionalAttr = "fak";

		Rection rection = new Rection();
		rection.setValue(rectionNode.getTextTrim());
		rection.setType(rectionNode.attributeValue(rectionTypeAttr));
		rection.setVariant(rectionNode.attributeValue(rectionVariantAttr));
		rection.setOptional(rectionNode.attributeValue(rectionOptionalAttr));
		return rection;
	}

	private void saveSymbol(Element node, Context context, String reportingId) throws Exception {

		final String symbolExp = "x:symb";

		Element symbolNode = (Element) node.selectSingleNode(symbolExp);
		if (symbolNode != null) {
			String symbolValue = symbolNode.getTextTrim();
			WordData data = createDefaultWordFrom(symbolValue);
			data.reportingId = reportingId;
			context.importedWords.add(data);
		}
	}

	private List<LexemeToWordData> extractSingleForms(Element node) {

		final String singleFormGroupNodeExp = "x:yvp/x:yvg";
		final String singleFormNodeExp = "x:yvrg";
		final String formValueExp = "x:yvr";
		final String formDefinitionExp = "x:yvd";
		final String usageExp = "x:ng/x:n";
		final String rectionExp = "x:rek";
		final String usageDefinitionExp = "x:nd";

		List<LexemeToWordData> singleForms = new ArrayList<>();
		List<Element> singleFormGroupNodes = node.selectNodes(singleFormGroupNodeExp);
		for (Element singleFormGroupNode : singleFormGroupNodes) {
			List<Usage> usages = new ArrayList<>();
			List<Element> formUsageNodes = singleFormGroupNode.selectNodes(usageExp);
			for (Element usageNode : formUsageNodes) {
				Usage usage = new Usage();
				usage.setValue(usageNode.getTextTrim());
				if (usageNode.hasMixedContent()) {
					usage.setDefinition(usageNode.selectSingleNode(usageDefinitionExp).getText());
				}
				usages.add(usage);
			}
			List<Element> singleFormNodes = singleFormGroupNode.selectNodes(singleFormNodeExp);
			for (Element singleFormNode : singleFormNodes) {
				LexemeToWordData data = new LexemeToWordData();
				Element formValueNode = (Element) singleFormNode.selectSingleNode(formValueExp);
				Element formDefinitionNode = (Element) singleFormNode.selectSingleNode(formDefinitionExp);
				data.word = formValueNode.getTextTrim();
				if (formValueNode.hasMixedContent()) {
					data.rection = extractRection((Element) formValueNode.selectSingleNode(rectionExp));
				}
				if (formDefinitionNode != null) {
					data.definition = formDefinitionNode.getTextTrim();
				}
				data.usages.addAll(usages);
				singleForms.add(data);
			}
		}

		return singleForms;
	}

	private List<LexemeToWordData> extractVormels(Element node) {

		final String vormelNodeExp = "x:vop/x:vog";
		final String vormelExp = "x:vor";
		final String vormelDefinitionExp = "x:vod";
		final String vormelUsageExp = "x:ng/x:n";

		List<LexemeToWordData> vormels = new ArrayList<>();
		if (node == null) {
			return vormels;
		}
		List<Element> vormelNodes = node.selectNodes(vormelNodeExp);
		for (Element vormelNode : vormelNodes) {
			LexemeToWordData data = new LexemeToWordData();
			Element vormelValueNode = (Element) vormelNode.selectSingleNode(vormelExp);
			Element vormelDefinitionNode = (Element) vormelNode.selectSingleNode(vormelDefinitionExp);
			List<Element> vormelUsages = vormelNode.selectNodes(vormelUsageExp);
			data.word = vormelValueNode.getTextTrim();
			if (vormelDefinitionNode != null) {
				data.definition = vormelDefinitionNode.getTextTrim();
			}
			for (Element usageNode : vormelUsages) {
				Usage usage = new Usage();
				usage.setValue(usageNode.getTextTrim());
				data.usages.add(usage);
			}
			vormels.add(data);
		}
		return vormels;
	}

	private List<LexemeToWordData> extractCompoundReferences(Element node, String reportingId) throws Exception {

		final String compoundReferenceExp = "x:tyg2/x:yhvt";

		return extractLexemeMetadata(node, compoundReferenceExp, null, reportingId);
	}

	private List<LexemeToWordData> extractJointReferences(Element node, String reportingId) throws Exception {

		final String jointReferenceExp = "x:tyg2/x:yvt";
		final String relationTypeAttr = "yvtl";

		return extractLexemeMetadata(node, jointReferenceExp, relationTypeAttr, reportingId);
	}

	private List<String> extractCompoundWords(Element node) {

		final String compoundWordExp = "x:smp/x:lsg/x:ls";

		List<String> compoundWords = new ArrayList<>();
		List<Element> compoundWordNodes = node.selectNodes(compoundWordExp);
		for (Element compoundWordNode : compoundWordNodes) {
			compoundWords.add(compoundWordNode.getTextTrim());
		}
		return compoundWords;
	}

	private List<LexemeToWordData> extractMeaningReferences(Element node, String reportingId) throws Exception {

		final String meaningReferenceExp = "x:tvt";
		final String relationTypeAttr = "tvtl";

		return extractLexemeMetadata(node, meaningReferenceExp, relationTypeAttr, reportingId);
	}

	private List<LexemeToWordData> extractAntonyms(Element node, String reportingId) throws Exception {

		final String antonymExp = "x:ant";
		return extractLexemeMetadata(node, antonymExp, null, reportingId);
	}

	private List<LexemeToWordData> extractLexemeMetadata(Element node, String lexemeMetadataExp, String relationTypeAttr, String reportingId) throws Exception {

		final String lexemeLevel1Attr = "t";
		final String homonymNrAttr = "i";
		final String lexemeTypeAttr = "liik";
		final int defaultLexemeLevel1 = 1;

		List<LexemeToWordData> metadataList = new ArrayList<>();
		List<Element> metadataNodes = node.selectNodes(lexemeMetadataExp);
		for (Element metadataNode : metadataNodes) {
			LexemeToWordData lexemeMetadata = new LexemeToWordData();
			lexemeMetadata.word = metadataNode.getTextTrim();
			String lexemeLevel1AttrValue = metadataNode.attributeValue(lexemeLevel1Attr);
			if (StringUtils.isBlank(lexemeLevel1AttrValue)) {
				lexemeMetadata.lexemeLevel1 = defaultLexemeLevel1;
			} else {
				lexemeMetadata.lexemeLevel1 = Integer.parseInt(lexemeLevel1AttrValue);
			}
			String homonymNrAttrValue = metadataNode.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAttrValue)) {
				lexemeMetadata.homonymNr = Integer.parseInt(homonymNrAttrValue);
			}
			if (relationTypeAttr != null) {
				lexemeMetadata.relationType = metadataNode.attributeValue(relationTypeAttr);
			}
			String lexemeTypeAttrValue = metadataNode.attributeValue(lexemeTypeAttr);
			if (StringUtils.isNotBlank(lexemeTypeAttrValue)) {
				lexemeMetadata.lexemeType = lexemeTypes.get(lexemeTypeAttrValue);
				if (lexemeMetadata.lexemeType == null) {
					writeToLogFile(reportingId, "Tundmatu märksõnaliik", lexemeTypeAttrValue);
				}
			}
			metadataList.add(lexemeMetadata);
		}
		return metadataList;
	}

	private List<SynonymData> extractSynonyms(String reportingId, Element node, Long meaningId) {

		final String synonymExp = "x:syn";
		final String homonymNrAttr = "i";

		List<SynonymData> synonyms = new ArrayList<>();
		List<Element> synonymNodes = node.selectNodes(synonymExp);
		for (Element synonymNode : synonymNodes) {
			SynonymData data = new SynonymData();
			data.reportingId = reportingId;
			data.word = synonymNode.getTextTrim();
			data.meaningId = meaningId;
			String homonymNrAtrValue = synonymNode.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAtrValue)) {
				data.homonymNr = Integer.parseInt(homonymNrAtrValue);
			}
			synonyms.add(data);
		}
		return synonyms;
	}

	private void saveGrammars(Element node, Long lexemeId, WordData wordData) throws Exception {

		final String grammarValueExp = "x:grg/x:gki";

		List<Element> grammarNodes = node.selectNodes(grammarValueExp);
		for (Element grammarNode : grammarNodes) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammarNode.getTextTrim(), dataLang);
		}
		if (isNotEmpty(wordData.grammar)) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, wordData.grammar, dataLang);
		}
	}

	//POS - part of speech
	private void savePosAndDeriv(Long lexemeId, WordData newWordData, List<PosData> meaningPosCodes, String reportingId) throws Exception {

		Set<PosData> lexemePosCodes = new HashSet<>();
		if (meaningPosCodes.isEmpty()) {
			lexemePosCodes.addAll(newWordData.posCodes);
		} else {
			lexemePosCodes.addAll(meaningPosCodes);
			if (lexemePosCodes.size() > 1) {
				writeToLogFile(reportingId, "Tähenduse juures leiti rohkem kui üks sõnaliik <x:tp/x:grg/x:sl>", "");
			}
		}
		for (PosData posCode : lexemePosCodes) {
			if (posCodes.containsKey(posCode.code)) {
				Map<String, Object> params = new HashMap<>();
				params.put("lexeme_id", lexemeId);
				params.put("pos_code", posCodes.get(posCode.code));
				params.put("process_state_code", processStateCodes.get(posCode.processStateCode));
				basicDbService.create(LEXEME_POS, params);
			}
		}
		if (derivCodes.containsKey(newWordData.derivCode)) {
			Map<String, Object> params = new HashMap<>();
			params.put("lexeme_id", lexemeId);
			params.put("deriv_code", derivCodes.get(newWordData.derivCode));
			basicDbService.create(LEXEME_DERIV, params);
		}
	}

	private void saveRectionsAndUsages(Element node, Long lexemeId, List<Usage> usages) throws Exception {

		final String rectionGroupExp = "x:rep/x:reg";
		final String usageGroupExp = "x:ng";
		final String rectionExp = "x:rek";
		final String rectionPlacementAttr = "koht";

		if (!usages.isEmpty()) {
			Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, defaultRectionValue);
			for (Usage usage : usages) {
				createUsage(rectionId, usage);
			}
		}
		List<Element> rectionGroups = node.selectNodes(rectionGroupExp);
		for (Element rectionGroup : rectionGroups) {
			String rectionPlacement = rectionGroup.attributeValue(rectionPlacementAttr);
			List<Element> usageGroupNodes = rectionGroup.selectNodes(usageGroupExp);
			List<Usage> rectionUsages = extractUsages(usageGroupNodes);
			List<Element> rectionNodes = rectionGroup.selectNodes(rectionExp);
			for (Element rectionNode : rectionNodes) {
				Rection rection = extractRection(rectionNode);
				Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, rection.getValue());
				for (Usage usage : rectionUsages) {
					createUsage(rectionId, usage);
				}
				if (isNotEmpty(rection.getType())) {
					createFreeformClassifier(FreeformType.RECTION_TYPE, rectionId, rection.getType());
				}
				if (isNotEmpty(rectionPlacement)) {
					createFreeformTextOrDate(FreeformType.RECTION_PLACEMENT, rectionId, rectionPlacement, null);
				}
				if (isNotEmpty(rection.getVariant())) {
					createFreeformTextOrDate(FreeformType.RECTION_VARIANT, rectionId, rection.getVariant(), null);
				}
				if (isNotEmpty(rection.getOptional())) {
					createFreeformTextOrDate(FreeformType.RECTION_OPTIONAL, rectionId, rection.getOptional(), null);
				}
			}
		}
	}

	private void createUsage(Long rectionId, Usage usage) throws Exception {
		Long usageMeaningId = createFreeformTextOrDate(FreeformType.USAGE_MEANING, rectionId, "", null);
		createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, usage.getValue(), dataLang);
		if (isNotEmpty(usage.getDefinition())) {
			createFreeformTextOrDate(FreeformType.USAGE_DEFINITION, usageMeaningId, usage.getDefinition(), dataLang);
		}
	}

	private List<Usage> extractUsages(List<Element> usageGroupNodes) {

		final String usageExp = "x:n";

		List<Usage> usages = new ArrayList<>();
		for (Element usageGroupNode : usageGroupNodes) {
			List<Element> usageNodes = usageGroupNode.selectNodes(usageExp);
			for (Element usageNode : usageNodes) {
				Usage newUsage = new Usage();
				newUsage.setValue(usageNode.getTextTrim());
				if (usageNode.hasMixedContent()) {
					newUsage.setDefinition(usageNode.selectSingleNode("x:nd").getText());
				}
				usages.add(newUsage);
			}
		}
		return usages;
	}

	private void processArticleHeader(
			String guid,
			String reportingId,
			Element headerNode,
			List<WordData> newWords,
			Context context,
			Map<String, List<Paradigm>> wordParadigmsMap,
			Count wordDuplicateCount,
			String dataset) throws Exception {

		final String referenceFormExp = "x:mvt";

		List<Element> referenceFormNodes = headerNode.selectNodes(referenceFormExp);
		boolean isReferenceForm = !referenceFormNodes.isEmpty();

		if (isReferenceForm) {
			processAsForm(reportingId, headerNode, referenceFormNodes, context.referenceForms);
		} else {
			processAsWord(guid, reportingId, headerNode, newWords, context, wordParadigmsMap, wordDuplicateCount, dataset);
		}
	}

	private void processAsForm(String reportingId, Element headerNode, List<Element> referenceFormNodes, List<ReferenceFormData> referenceForms) {

		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String homonymNrAttr = "i";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			String formValue = wordNode.getTextTrim();
			formValue = StringUtils.replaceChars(formValue, wordDisplayFormStripChars, "");
			for (Element referenceFormNode : referenceFormNodes) {
				ReferenceFormData referenceFormData = new ReferenceFormData();
				referenceFormData.formValue = formValue;
				referenceFormData.reportingId = reportingId;
				referenceFormData.wordValue = referenceFormNode.getTextTrim();
				if (referenceFormNode.attributeValue(homonymNrAttr) != null) {
					referenceFormData.wordHomonymNr = Integer.parseInt(referenceFormNode.attributeValue(homonymNrAttr));
				}
				referenceForms.add(referenceFormData);
			}
		}
	}

	private void processAsWord(
			String guid,
			String reportingId,
			Element headerNode,
			List<WordData> newWords,
			Context context,
			Map<String, List<Paradigm>> wordParadigmsMap,
			Count wordDuplicateCount,
			String dataset) throws Exception {

		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordDerivCodeExp = "x:dk";
		final String wordGrammarExp = "x:mfp/x:gki";
		final String wordFrequencyGroupExp = "x:sag";
		final String wordComparativeExp = "x:mfp/x:kmpg/x:kmp";
		final String wordSuperlativeExp = "x:mfp/x:kmpg/x:suprl";
		final String posAsTyypAttr = "as";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid, context);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData, wordParadigmsMap);
				wordData.id = saveWord(word, paradigms, dataset, wordDuplicateCount);
			}

			addSoundFileNamesToForms(wordData.id, wordGroupNode);

			List<WordData> basicWordsOfTheWord = extractBasicWords(wordGroupNode, wordData.id, reportingId);
			context.basicWords.addAll(basicWordsOfTheWord);

			List<Element> posCodeNodes = wordGroupNode.selectNodes(wordPosCodeExp);
			for (Element posCodeNode : posCodeNodes) {
				PosData posData = new PosData();
				posData.code = posCodeNode.getTextTrim();
				posData.processStateCode = posCodeNode.attributeValue(posAsTyypAttr);
				wordData.posCodes.add(posData);
			}

			Element derivCodeNode = (Element) wordGroupNode.selectSingleNode(wordDerivCodeExp);
			wordData.derivCode = derivCodeNode == null ? null : derivCodeNode.getTextTrim();

			Element grammarNode = (Element) wordGroupNode.selectSingleNode(wordGrammarExp);
			wordData.grammar = grammarNode == null ? null : grammarNode.getTextTrim();

			Element frequencyNode = (Element) wordGroupNode.selectSingleNode(wordFrequencyGroupExp);
			wordData.frequencyGroup = frequencyNode == null ? null : frequencyNode.getTextTrim();

			List<Element> wordComparativeNodes = wordGroupNode.selectNodes(wordComparativeExp);
			wordData.comparatives = wordComparativeNodes.stream()
					.map(n -> StringUtils.replaceChars(n.getTextTrim(), formStrCleanupChars, ""))
					.collect(Collectors.toList());

			List<Element> wordSuperlativeNodes = wordGroupNode.selectNodes(wordSuperlativeExp);
			wordData.superlatives = wordSuperlativeNodes.stream()
					.map(n -> StringUtils.replaceChars(n.getTextTrim(), formStrCleanupChars, ""))
					.collect(Collectors.toList());

			newWords.add(wordData);
		}
	}

	private void addSoundFileNamesToForms(Long wordId, Element wordGroupNode) {

		final String morphValueGroupExp = "x:mfp/x:gkg/x:mvg/x:mvgp";
		final String soundFileExp = "x:hldf";
		final String formValueExp = "x:mvf";

		List<SoundFileData> soundFiles = new ArrayList<>();
		List<Element> morphValueGroupNodes = wordGroupNode.selectNodes(morphValueGroupExp);
		for (Element morphValueGroupNode : morphValueGroupNodes) {
			Element soundFileNode = (Element) morphValueGroupNode.selectSingleNode(soundFileExp);
			if (soundFileNode != null) {
				Element formValueNode = (Element) morphValueGroupNode.selectSingleNode(formValueExp);
				String formValue = StringUtils.replaceChars(formValueNode.getTextTrim(), formStrCleanupChars, "");
				SoundFileData data = new SoundFileData();
				data.soundFile = soundFileNode.getTextTrim();
				data.formValue = formValue;
				soundFiles.add(data);
			}
		}
		for (SoundFileData soundFileData : soundFiles) {
			Map<String, Object> params = new HashMap<>();
			params.put("formValue", soundFileData.formValue);
			params.put("wordId", wordId);
			params.put("soundFile", soundFileData.soundFile);
			basicDbService.executeScript(sqlUpdateSoundFiles, params);
		}
	}

	private List<WordData> extractBasicWords(Element node, Long wordId, String reportingId) {

		final String basicWordExp = "x:ps";
		final String homonymNrAttr = "i";

		List<WordData> basicWords = new ArrayList<>();
		List<Element> basicWordNodes = node.selectNodes(basicWordExp);
		for (Element basicWordNode : basicWordNodes) {
			WordData basicWord = new WordData();
			basicWord.id = wordId;
			basicWord.value = basicWordNode.getTextTrim();
			basicWord.reportingId = reportingId;
			if (basicWordNode.attributeValue(homonymNrAttr) != null) {
				basicWord.homonymNr = Integer.parseInt(basicWordNode.attributeValue(homonymNrAttr));
			}
			basicWords.add(basicWord);
		}
		return basicWords;
	}

	private Paradigm fetchParadigmFromMab(String wordValue, String inflectionTypeNr, Element node, Map<String, List<Paradigm>> wordParadigmsMap) {

		final String formsNodesExp = "x:mfp/x:gkg/x:mvg/x:mvgp/x:mvf";

		List<Paradigm> paradigms = wordParadigmsMap.get(wordValue);
		if (CollectionUtils.isEmpty(paradigms)) {
			return null;
		}

		if (isNotEmpty(inflectionTypeNr)) {
			long nrOfParadigmsMatchingInflectionType = paradigms.stream().filter(p -> Objects.equals(p.getInflectionTypeNr(), inflectionTypeNr)).count();
			if (nrOfParadigmsMatchingInflectionType == 1) {
				return paradigms.stream().filter(p -> Objects.equals(p.getInflectionTypeNr(), inflectionTypeNr)).findFirst().get();
			}
		}

		List<Element> formsNodes = node.selectNodes(formsNodesExp);
		if (formsNodes.isEmpty()) {
			return null;
		}
		List<String> formValues = formsNodes.stream().map(n -> StringUtils.replaceChars(n.getTextTrim(), formStrCleanupChars, "")).collect(Collectors.toList());
		List<String> mabFormValues;
		Collection<String> formValuesIntersection;
		int bestFormValuesMatchCount = 0;
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : paradigms) {
			mabFormValues = paradigm.getFormValues();
			formValuesIntersection = CollectionUtils.intersection(formValues, mabFormValues);
			if (formValuesIntersection.size() > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = formValuesIntersection.size();
				matchingParadigm = paradigm;
			}
		}
		return matchingParadigm;
	}

	private Word extractWordData(Element wordGroupNode, WordData wordData, String guid, Context context) throws Exception {

		final String wordExp = "x:m";
		final String wordDisplayMorphExp = "x:vk";
		final String homonymNrAttr = "i";
		final String lexemeTypeAttr = "liik";

		Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
		if (wordNode.attributeValue(homonymNrAttr) != null) {
			wordData.homonymNr = Integer.parseInt(wordNode.attributeValue(homonymNrAttr));
		}
		if (wordNode.attributeValue(lexemeTypeAttr) != null) {
			wordData.lexemeType = lexemeTypes.get(wordNode.attributeValue(lexemeTypeAttr));
		}
		String wordValue = wordNode.getTextTrim();
		String wordDisplayForm = wordValue;
		wordValue = StringUtils.replaceChars(wordValue, wordDisplayFormStripChars, "");
		wordData.value = wordValue;

		Word word = null;
		Long existingWordId = checkForAndGetExistingWordId(wordGroupNode, wordData, guid, context);
		if (existingWordId == null) {
			int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;
			String wordMorphCode = extractWordMorphCode(wordValue, wordGroupNode);

			word = new Word(wordValue, dataLang, null, null, wordDisplayForm, null, homonymNr, wordMorphCode, guid);

			Element wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(wordDisplayMorphExp);
			if (wordDisplayMorphNode != null) {
				word.setDisplayMorph(wordDisplayMorphNode.getTextTrim());
			}
		} else {
			wordData.id = existingWordId;
		}

		return word;
	}

	private Long checkForAndGetExistingWordId(Element wordGroupNode, WordData wordData, String guid, Context context) throws Exception {

		if (!wordMatcherService.isEnabled()) {
			return null;
		}
		List<String> forms = extractWordForms(wordGroupNode);
		Long existingWordId = wordMatcherService.getMatchingWordId(guid, wordData.value, forms);
		Optional<WordData> importedWord = context.importedWords.stream().filter(w -> w.id.equals(existingWordId)).findFirst();
		if (importedWord.isPresent() && importedWord.get().homonymNr != wordData.homonymNr) {
			return null;
		}
		return existingWordId;
	}

	private List<Paradigm> extractParadigms(Element wordGroupNode, WordData word, Map<String, List<Paradigm>> wordParadigmsMap) throws Exception {

		final String inflectionTypeNrExp = "x:mfp/x:mt";

		List<Paradigm> paradigms = new ArrayList<>();
		boolean isAddForms = !wordParadigmsMap.isEmpty();
		Element inflectionTypeNrNode = (Element) wordGroupNode.selectSingleNode(inflectionTypeNrExp);
		if (inflectionTypeNrNode != null) {
			String inflectionTypeNrStr = inflectionTypeNrNode.getTextTrim();
			String[] numberStrs = inflectionTypeNrStr.split("~");
			for (String numberStr : numberStrs) {
				Paradigm paradigm = new Paradigm();
				if (numberStr.endsWith("?")) {
					paradigm.setInflectionTypeNr(numberStr.replace("?", ""));
					paradigm.setSecondary(true);
				} else {
					paradigm.setInflectionTypeNr(numberStr);
				}
				if (isAddForms) {
					Paradigm paradigmFromMab = fetchParadigmFromMab(word.value, paradigm.getInflectionTypeNr(), wordGroupNode, wordParadigmsMap);
					if (paradigmFromMab != null) {
						paradigm.setForms(paradigmFromMab.getForms());
					}
				}
				paradigms.add(paradigm);
			}
		} else {
			if (isAddForms) {
				Paradigm paradigmFromMab = fetchParadigmFromMab(word.value, null, wordGroupNode, wordParadigmsMap);
				if (paradigmFromMab != null) {
					paradigms.add(paradigmFromMab);
				}
			}
		}
		return paradigms;
	}

	private String extractWordMorphCode(String word, Element wordGroupNode) {

		final String formGroupExp = "x:mfp/x:gkg/x:mvg";
		final String formExp = "x:mvgp/x:mvf";
		final String morphCodeAttributeExp = "vn";

		List<Element> formGroupNodes = wordGroupNode.selectNodes(formGroupExp);
		for (Element formGroup : formGroupNodes) {
			Element formElement = (Element) formGroup.selectSingleNode(formExp);
			String formValue = StringUtils.replaceChars(formElement.getTextTrim(), wordDisplayFormStripChars, "");
			if (word.equals(formValue)) {
				return formGroup.attributeValue(morphCodeAttributeExp);
			}
		}
		return defaultWordMorphCode;
	}

	private List<String> extractWordForms(Element wordGroupNode) {

		final String formValueExp = "x:mfp/x:gkg/x:mvg/x:mvgp/x:mvf";

		List<String> forms = new ArrayList<>();
		List<Element> formNodes = wordGroupNode.selectNodes(formValueExp);
		for (Element formNode : formNodes) {
			String formValue = StringUtils.replaceChars(formNode.getTextTrim(), wordDisplayFormStripChars, "");
			forms.add(formValue);
		}
		return forms;
	}

	private void saveDefinitions(List<Element> definitionValueNodes, Long meaningId, String wordMatchLang, String dataset) throws Exception {

		if (definitionValueNodes == null) {
			return;
		}
		for (Element definitionValueNode : definitionValueNodes) {
			String definition = definitionValueNode.getTextTrim();
			createDefinition(meaningId, definition, wordMatchLang, dataset);
		}
	}

	private void writeToLogFile(String reportingId, String message, String values) throws Exception {
		if (reportingEnabled) {
			String logMessage = String.join(String.valueOf(CSV_SEPARATOR), asList(reportingId, message, values));
			reportComposer.append(logMessage);
		}
	}

	private void setActivateReport(String reportName) {
		if (reportComposer != null) {
			reportComposer.setActiveStream(reportName);
		}
	}

	private class WordData {
		Long id;
		List<PosData> posCodes = new ArrayList<>();
		String derivCode;
		String grammar;
		String value;
		int homonymNr = 0;
		String reportingId;
		String frequencyGroup;
		String lexemeType;
		List<String> comparatives = new ArrayList<>();
		List<String> superlatives = new ArrayList<>();
	}

	private class PosData {
		String code;
		String processStateCode;
	}

	private class SynonymData {
		String word;
		Long meaningId;
		int homonymNr = 0;
		String reportingId;
	}

	private class LexemeToWordData {
		Long lexemeId;
		String word;
		int lexemeLevel1 = 1;
		int homonymNr = 0;
		String relationType;
		Rection rection;
		String definition;
		List<Usage> usages = new ArrayList<>();
		String reportingId;
		String lexemeType;

		LexemeToWordData copy() {
			LexemeToWordData newData = new LexemeToWordData();
			newData.lexemeId = this.lexemeId;
			newData.word = this.word;
			newData.lexemeLevel1 = this.lexemeLevel1;
			newData.homonymNr = this.homonymNr;
			newData.relationType = this.relationType;
			newData.rection = this.rection;
			newData.definition = this.definition;
			newData.reportingId = this.reportingId;
			newData.usages.addAll(this.usages);
			newData.lexemeType = this.lexemeType;
			return newData;
		}
	}

	private class ReferenceFormData {
		String formValue;
		String wordValue;
		int wordHomonymNr = 0;
		String reportingId;
	}

	private class SoundFileData {
		String soundFile;
		String formValue;
	}

	private class Context {
		List<SynonymData> synonyms = new ArrayList<>();
		List<LexemeToWordData> antonyms = new ArrayList<>();
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> basicWords = new ArrayList<>();
		List<ReferenceFormData> referenceForms = new ArrayList<>(); // viitemärksõna
		List<LexemeToWordData> compoundWords = new ArrayList<>(); // liitsõnad
		List<LexemeToWordData> meaningReferences = new ArrayList<>(); // tähendusviide
		List<LexemeToWordData> jointReferences = new ArrayList<>(); // ühisviide
		List<LexemeToWordData> compoundReferences = new ArrayList<>(); // ühendiviide
		List<LexemeToWordData> vormels = new ArrayList<>(); // vormel
		List<LexemeToWordData> singleForms = new ArrayList<>(); // üksikvorm
		List<LexemeToWordData> compoundForms = new ArrayList<>(); // ühend
	}

}
