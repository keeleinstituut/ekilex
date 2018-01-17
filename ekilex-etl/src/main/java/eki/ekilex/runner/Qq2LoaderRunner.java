package eki.ekilex.runner;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianWordProcessing;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Grammar;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Rection;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageMeaning;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.MabService;
import eki.ekilex.service.ReportComposer;

@Component
public class Qq2LoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Qq2LoaderRunner.class);

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "csv/transform-morph-deriv.csv";

	private static final String REPORT_MISSING_USAGE_MEANING_MATCH = "missing_usage_meaning_match";

	private static final String REPORT_AMBIGUOUS_USAGE_MEANING_MATCH = "ambiguous_usage_meaning_match";

	private static final String REPORT_USAGE_MEANING_MATCH_BY_CREATIVE_ANALYSIS = "usage_meaning_match_by_creative_analysis";

	private static final String REPORT_MISSING_MAB_INTEGRATION_CASE = "missing_mab_integration_case";

	@Autowired
	private MabService mabService;

	@Autowired
	private LuceneMorphology russianMorphology;

	private ReportComposer reportComposer;

	private Map<String, String> morphToMorphMap;

	private Map<String, String> morphToDerivMap;

	private final String guidExp = "x:G";
	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String wordVocalFormExp = "x:hld";
	private final String wordMorphExp = "x:vk";
	private final String wordRectionExp = "x:r";
	private final String wordGrammarExp = "x:grg/x:gki";
	private final String articleBodyExp = "x:S";
	private final String meaningGroupExp = "x:tp";
	private final String meaningExp = "x:tg";
	private final String wordMatchExpr = "x:xp/x:xg";
	private final String wordMatchValueExp = "x:x";
	private final String definitionExp = "x:xd";
	private final String wordMatchRectionExp = "x:xr";
	private final String synonymExp = "x:syn";
	private final String usageGroupExp = "x:np/x:ng";
	private final String usageExp = "x:n";
	private final String usageTranslationExp = "x:qnp/x:qng";
	private final String usageTranslationValueExp = "x:qn";
	private final String formsExp = "x:grg/x:vormid";

	private final String langAttr = "lang";

	private final String defaultWordMorphCode = "SgN";
	private final int defaultHomonymNr = 1;
	private final String defaultRectionValue = "-";
	private final String wordDisplayFormCleanupChars = "̄̆̇’'`´.:_–!°()¤";
	private final char wordComponentSeparator = '+';
	private final String formStrCleanupChars = "̄̆̇’\"'`´,;–+=()";
	private final String[] textCleanupEnitites = new String[] {"&ema;v&eml;", "&v;"};
	private final String[] textCleanupEnityReplacements = new String[] {"", ""};
	private final String usageTranslationLangRus = "rus";

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(TRANSFORM_MORPH_DERIV_FILE_PATH);
		List<String> morphDerivMapLines = getContentLines(resourceFileInputStream);
		morphToMorphMap = new HashMap<>();
		morphToDerivMap = new HashMap<>();
		for (String morphDerivMapLine : morphDerivMapLines) {
			if (StringUtils.isBlank(morphDerivMapLine)) {
				continue;
			}
			String[] morphDerivMapLineParts = StringUtils.split(morphDerivMapLine, CSV_SEPARATOR);
			String sourceMorphCode = morphDerivMapLineParts[0];
			String destinMorphCode = morphDerivMapLineParts[1];
			String destinDerivCode = morphDerivMapLineParts[2];
			morphToMorphMap.put(sourceMorphCode, destinMorphCode);
			if (!StringUtils.equals(destinDerivCode, String.valueOf(CSV_EMPTY_CELL))) {
				morphToDerivMap.put(sourceMorphCode, destinDerivCode);
			}
		}
	}

	@Transactional
	public void execute(
			String dataXmlFilePath, String dataLang, String dataset, boolean doReports) throws Exception {

		logger.debug("Loading QQ2...");

		final String pseudoHomonymAttr = "i";
		final String lexemeLevel1Attr = "tnr";

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("qq2 load report",
					REPORT_MISSING_USAGE_MEANING_MATCH, REPORT_AMBIGUOUS_USAGE_MEANING_MATCH, REPORT_MISSING_MAB_INTEGRATION_CASE,
					REPORT_USAGE_MEANING_MATCH_BY_CREATIVE_ANALYSIS);
		}

		boolean isAddForms = mabService.isMabLoaded();
		dataLang = unifyLang(dataLang);
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		logger.debug("Extracted {} articles", articleCount);

		Map<Long, List<Rection>> wordIdRectionMap = new HashMap<>();
		Map<Long, List<Grammar>> wordIdGrammarMap = new HashMap<>();

		Element headerNode, contentNode;
		List<Element> wordGroupNodes, rectionNodes, grammarNodes, meaningGroupNodes, meaningNodes;
		List<Element> definitionNodes, wordMatchNodes, synonymLevel1Nodes, synonymLevel2Nodes, usageGroupNodes;
		Element guidNode, wordNode, wordVocalFormNode, morphNode, wordMatchValueNode, formsNode;

		List<UsageMeaning> usageMeanings;
		List<Word> newWords, wordMatches, allWordMatches;
		List<Paradigm> paradigms;
		List<Rection> rections;
		String guid, word, wordFormsStr, wordMatch, pseudoHomonymNr, wordDisplayForm, wordVocalForm, lexemeLevel1Str, wordMatchLang;
		String sourceMorphCode, destinMorphCode, destinDerivCode;
		int homonymNr, lexemeLevel1, lexemeLevel2;
		Long wordId, newWordId, meaningId, lexemeId;
		String[] wordComponents;
		Word wordObj;
		Lexeme lexemeObj;
		Paradigm paradigmObj;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count missingUsageGroupCount = new Count();
		Count missingMabIntegrationCaseCount = new Count();
		Count ambiguousUsageTranslationMatchCount = new Count();
		Count missingUsageTranslationMatchCount = new Count();
		Count successfulUsageTranslationMatchCount = new Count();
		Count singleUsageTranslationMatchCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			guidNode = (Element) articleNode.selectSingleNode(guidExp);
			guid = guidNode.getTextTrim();
			guid = StringUtils.lowerCase(guid);

			// header...
			newWords = new ArrayList<>();
			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNodes = headerNode.selectNodes(wordGroupExp);
			word = null;
			paradigmObj = null;

			for (Element wordGroupNode : wordGroupNodes) {

				// word, form...
				wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				word = wordDisplayForm = wordNode.getTextTrim();
				word = StringUtils.replaceChars(word, wordDisplayFormCleanupChars, "");
				wordComponents = StringUtils.split(word, wordComponentSeparator);
				word = StringUtils.remove(word, wordComponentSeparator);
				pseudoHomonymNr = wordNode.attributeValue(pseudoHomonymAttr);
				wordFormsStr = null;
				paradigms = null;
				if (StringUtils.isNotBlank(pseudoHomonymNr)) {
					word = StringUtils.substringBefore(word, pseudoHomonymNr);
				}
				homonymNr = getWordMaxHomonymNr(word, dataLang);
				homonymNr++;
				wordVocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
				if (wordVocalFormNode == null) {
					wordVocalForm = null;
				} else {
					wordVocalForm = wordVocalFormNode.getTextTrim();
				}
				morphNode = (Element) wordGroupNode.selectSingleNode(wordMorphExp);
				if (morphNode == null) {
					destinMorphCode = defaultWordMorphCode;
					destinDerivCode = null;
				} else {
					sourceMorphCode = morphNode.getTextTrim();
					destinMorphCode = morphToMorphMap.get(sourceMorphCode);
					destinDerivCode = morphToDerivMap.get(sourceMorphCode);//currently not used
				}
				if (isAddForms) {
					formsNode = (Element) wordGroupNode.selectSingleNode(formsExp);
					if (formsNode != null) {
						wordFormsStr = formsNode.getTextTrim();
						paradigms = extractParadigms(word, wordFormsStr, wordComponents);
					}
				}

				// save word+paradigm+form
				wordObj = new Word(word, dataLang, wordFormsStr, wordComponents, wordDisplayForm, wordVocalForm, homonymNr, destinMorphCode, guid);
				wordId = saveWord(wordObj, paradigms, dataset, wordDuplicateCount);
				newWords.add(wordObj);

				// further references...

				// rections...
				rectionNodes = wordGroupNode.selectNodes(wordRectionExp);
				extractRections(rectionNodes, wordId, wordIdRectionMap);

				// grammar...
				grammarNodes = wordGroupNode.selectNodes(wordGrammarExp);
				extractGrammar(grammarNodes, wordId, wordIdGrammarMap);
			}

			// body...

			synonymLevel1Nodes = contentNode.selectNodes(synonymExp);
			meaningGroupNodes = contentNode.selectNodes(meaningGroupExp);//x:tp

			for (Element meaningGroupNode : meaningGroupNodes) {

				lexemeLevel1Str = meaningGroupNode.attributeValue(lexemeLevel1Attr);
				lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);

				usageGroupNodes = meaningGroupNode.selectNodes(usageGroupExp);//x:np/x:ng
				usageMeanings = extractUsagesAndTranslations(usageGroupNodes);
				if (CollectionUtils.isEmpty(usageGroupNodes)) {
					missingUsageGroupCount.increment();
				}

				meaningNodes = meaningGroupNode.selectNodes(meaningExp);//x:tg
				boolean isSingleMeaning = meaningNodes.size() == 1;
				lexemeLevel2 = 0;
				allWordMatches = new ArrayList<>();

				for (Element meaningNode : meaningNodes) {

					lexemeLevel2++;

					// meaning
					meaningId = createMeaning();

					// definitions #1
					synonymLevel2Nodes = meaningNode.selectNodes(synonymExp);
					saveDefinitions(synonymLevel1Nodes, meaningId, dataLang, dataset);
					saveDefinitions(synonymLevel2Nodes, meaningId, dataLang, dataset);

					wordMatchNodes = meaningNode.selectNodes(wordMatchExpr);//x:xp/x:xg
					wordMatches = new ArrayList<>();

					for (Element wordMatchNode : wordMatchNodes) {

						wordMatchLang = wordMatchNode.attributeValue(langAttr);
						wordMatchLang = unifyLang(wordMatchLang);
						wordMatchValueNode = (Element) wordMatchNode.selectSingleNode(wordMatchValueExp);
						wordMatch = wordMatchValueNode.getTextTrim();
						wordMatch = StringUtils.replaceEach(wordMatch, textCleanupEnitites, textCleanupEnityReplacements);
						wordMatch = StringUtils.replaceChars(wordMatch, wordDisplayFormCleanupChars, "");

						if (StringUtils.isBlank(wordMatch)) {
							continue;
						}

						wordObj = new Word(wordMatch, wordMatchLang, null, null, null, null, defaultHomonymNr, defaultWordMorphCode, null);
						wordId = saveWord(wordObj, null, null, wordDuplicateCount);
						wordMatches.add(wordObj);
						allWordMatches.add(wordObj);

						// definitions #2
						definitionNodes = wordMatchNode.selectNodes(definitionExp);
						saveDefinitions(definitionNodes, meaningId, wordMatchLang, dataset);

						// word match lexeme
						lexemeObj = new Lexeme();
						lexemeObj.setWordId(wordId);
						lexemeObj.setMeaningId(meaningId);
						lexemeId = createLexeme(lexemeObj, dataset);

						if (lexemeId == null) {
							lexemeDuplicateCount.increment();
						} else {
							// word match lexeme rection
							rectionNodes = wordMatchValueNode.selectNodes(wordMatchRectionExp);
							saveRections(rectionNodes, lexemeId);
						}
					}

					// new words lexemes+rections+grammar
					for (Word newWord : newWords) {

						newWordId = newWord.getId();
						lexemeObj = new Lexeme();
						lexemeObj.setWordId(newWordId);
						lexemeObj.setMeaningId(meaningId);
						lexemeObj.setLevel1(lexemeLevel1);
						lexemeObj.setLevel2(lexemeLevel2);
						lexemeId = createLexeme(lexemeObj, dataset);

						rections = wordIdRectionMap.get(newWordId);

						// new word lexeme rections, usages, usage translations
						createRectionsAndUsagesAndTranslations(
								dataLang, lexemeId, wordMatches, rections, usageMeanings, isSingleMeaning, singleUsageTranslationMatchCount);

						// new word lexeme grammars
						createGrammars(wordIdGrammarMap, lexemeId, newWordId, dataset);
					}
				}

				if (doReports) {
					detectAndReportAtMeaning(
							usageMeanings, newWords, allWordMatches, isSingleMeaning,
							ambiguousUsageTranslationMatchCount,
							missingUsageTranslationMatchCount,
							successfulUsageTranslationMatchCount);
				}
			}

			if (doReports) {
				detectAndReportAtArticle(newWords, missingMabIntegrationCaseCount);
			}

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		if (reportComposer != null) {
			reportComposer.end();
		}

		logger.debug("Found {} word duplicates", wordDuplicateCount.getValue());
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount.getValue());
		logger.debug("Found {} missing usage groups", missingUsageGroupCount.getValue());
		logger.debug("Found {} single usage translation matches", singleUsageTranslationMatchCount.getValue());
		if (doReports) {
			logger.debug("Found {} ambiguous usage translation matches", ambiguousUsageTranslationMatchCount.getValue());
			logger.debug("Found {} missing usage translation matches", missingUsageTranslationMatchCount.getValue());
			logger.debug("Found {} successful usage translation matches", successfulUsageTranslationMatchCount.getValue());
		}

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private void detectAndReportAtMeaning(
			List<UsageMeaning> usageMeanings,
			List<Word> newWords,
			List<Word> wordMatches,
			boolean isSingleMeaning,
			Count ambiguousUsageTranslationMatchCount,
			Count missingUsageTranslationMatchCount,
			Count successfulUsageTranslationMatchCount) throws Exception {

		if (isSingleMeaning) {
			return;
		}

		List<UsageTranslation> usageTranslations;
		List<String> originalTokens, lemmatisedTokens;
		String usageValue;
		StringBuffer logBuf;

		List<String> newWordValues = newWords.stream().map(word -> word.getValue().toLowerCase()).collect(Collectors.toList());
		List<String> wordMatchValues = wordMatches.stream().map(word -> word.getValue().toLowerCase()).collect(Collectors.toList());
		List<String> usageTranslationValues;

		boolean containsMultiWordMatches = containsMultiWords(wordMatchValues);

		for (UsageMeaning usageMeaning : usageMeanings) {
			for (Usage usageObj : usageMeaning.getUsages()) {
				int usageWordMatchByOriginalTokenCount = 0;
				int usageWordMatchByLemmatisationCount = 0;
				int usageWordMatchByTokenSplitCount = 0;
				usageValue = usageObj.getValue();
				usageTranslations = usageObj.getUsageTranslations();
				usageTranslationValues = usageTranslations.stream().map(usageTranslation -> usageTranslation.getValue()).collect(Collectors.toList());
				for (UsageTranslation usageTranslation : usageTranslations) {
					originalTokens = usageTranslation.getOriginalTokens();
					lemmatisedTokens = usageTranslation.getLemmatisedTokens();
					if (CollectionUtils.containsAny(originalTokens, wordMatchValues)) {
						usageWordMatchByOriginalTokenCount++;
					}
					if (CollectionUtils.containsAny(lemmatisedTokens, wordMatchValues)) {
						usageWordMatchByLemmatisationCount++;
					}
					if (containsMultiWordMatches) {
						boolean containsAnyMatchByWordSplit = containsAnyMatchByWordSplit(wordMatchValues, originalTokens, lemmatisedTokens);
						if (containsAnyMatchByWordSplit) {
							usageWordMatchByTokenSplitCount++;
						}
					}
				}
				if (usageWordMatchByLemmatisationCount == 0) {
					if (usageWordMatchByOriginalTokenCount > 0) {
						successfulUsageTranslationMatchCount.increment();
						logBuf = new StringBuffer();
						logBuf.append(newWordValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(usageValue);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordMatchValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(usageTranslationValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("lemmatiseerimata sõnade võrdlus");
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_USAGE_MEANING_MATCH_BY_CREATIVE_ANALYSIS, logRow);
					} else if (usageWordMatchByTokenSplitCount > 0) {
						successfulUsageTranslationMatchCount.increment();
						logBuf = new StringBuffer();
						logBuf.append(newWordValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(usageValue);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordMatchValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(usageTranslationValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append("mitmesõnalise vaste võrdlus");
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_USAGE_MEANING_MATCH_BY_CREATIVE_ANALYSIS, logRow);
					} else {
						missingUsageTranslationMatchCount.increment();
						logBuf = new StringBuffer();
						logBuf.append(newWordValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(usageValue);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(wordMatchValues);
						logBuf.append(CSV_SEPARATOR);
						logBuf.append(usageTranslationValues);
						String logRow = logBuf.toString();
						reportComposer.append(REPORT_MISSING_USAGE_MEANING_MATCH, logRow);
					}
				} else if (usageWordMatchByLemmatisationCount == 1) {
					successfulUsageTranslationMatchCount.increment();
				} else if (usageWordMatchByLemmatisationCount > 1) {
					ambiguousUsageTranslationMatchCount.increment();
					logBuf = new StringBuffer();
					logBuf.append(newWordValues);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(usageValue);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(wordMatchValues);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(usageTranslationValues);
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_AMBIGUOUS_USAGE_MEANING_MATCH, logRow);
				}
			}
		}
	}

	private void detectAndReportAtArticle(
			List<Word> newWords,
			Count missingMabIntegrationCaseCount) throws Exception {

		if (!mabService.isMabLoaded()) {
			return;
		}

		StringBuffer logBuf;

		for (Word wordObj : newWords) {

			String word = wordObj.getValue();
			String[] wordComponents = wordObj.getComponents();
			String wordFormsString = wordObj.getFormsString();
			int wordComponentCount = wordComponents.length;
			String wordLastComp = wordComponents[wordComponentCount - 1];

			if (!mabService.paradigmsExist(wordLastComp)) {
				missingMabIntegrationCaseCount.increment();
				if (StringUtils.isBlank(wordFormsString)) {
					wordFormsString = "-";
				}
				logBuf = new StringBuffer();
				logBuf.append(word);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append(wordFormsString);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("MAB-s sõna puudub");
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_MISSING_MAB_INTEGRATION_CASE, logRow);
				continue;
			}
			if (StringUtils.isBlank(wordFormsString)) {
				if (!mabService.isSingleParadigm(wordLastComp)) {
					missingMabIntegrationCaseCount.increment();
					logBuf = new StringBuffer();
					logBuf.append(word);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("-");
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("QQ vormid puuduvad");
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_MISSING_MAB_INTEGRATION_CASE, logRow);
					continue;
				}
			} else {
				if (StringUtils.countMatches(wordFormsString, '+') > 1) {
					missingMabIntegrationCaseCount.increment();
					logBuf = new StringBuffer();
					logBuf.append(word);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(wordFormsString);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("Mitmekordselt käänduv liitsõna?");
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_MISSING_MAB_INTEGRATION_CASE, logRow);
					continue;
				}
				String strippedWordFormsStr = StringUtils.replaceChars(wordFormsString, formStrCleanupChars, "");
				String[] formValuesArr = StringUtils.split(strippedWordFormsStr, ' ');
				List<String> qq2FormValues = asList(formValuesArr);
				List<String> mabFormValues;
				Collection<String> formValuesIntersection;
				int bestFormValuesMatchCount = 0;
				List<Paradigm> paradigms = mabService.getWordParadigms(wordLastComp);
				Paradigm matchingParadigm = null;
				for (Paradigm paradigm : paradigms) {
					mabFormValues = paradigm.getFormValues();
					formValuesIntersection = CollectionUtils.intersection(qq2FormValues, mabFormValues);
					if (formValuesIntersection.size() > bestFormValuesMatchCount) {
						bestFormValuesMatchCount = formValuesIntersection.size();
						matchingParadigm = paradigm;
					}
				}
				if (matchingParadigm == null) {
					missingMabIntegrationCaseCount.increment();
					logBuf = new StringBuffer();
					logBuf.append(word);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(wordFormsString);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append("Vormid ei kattu MAB-ga");
					String logRow = logBuf.toString();
					reportComposer.append(REPORT_MISSING_MAB_INTEGRATION_CASE, logRow);
					continue;
				}
			}
		}
	}

	// TODO paradigms.... 
	private List<Paradigm> extractParadigms(String word, String wordFormsStr, String[] wordComponents) throws Exception {

		int wordComponentCount = wordComponents.length;
		boolean isCompoundWord = wordComponentCount > 1;
		String wordLastComp = wordComponents[wordComponentCount - 1];

		if (!mabService.isMabLoaded()) {
			return null;
		}
		if (!mabService.paradigmsExist(wordLastComp)) {
			return null;
		}
		if (StringUtils.isNotBlank(wordFormsStr) && StringUtils.countMatches(wordFormsStr, '+') > 1) {
			return null;
		}
		if (mabService.isSingleParadigm(wordLastComp)) {
			return mabService.getWordParadigms(wordLastComp);
		}
		if (StringUtils.isBlank(wordFormsStr)) {
			logger.warn("\"{}({})\" has no forms to compare with MAB paradigms", wordLastComp, word);
			return null;
		}
		String strippedWordFormsStr = StringUtils.replaceChars(wordFormsStr, formStrCleanupChars, "");
		String[] formValuesArr = StringUtils.split(strippedWordFormsStr, ' ');
		List<String> qq2FormValues = asList(formValuesArr);
		List<String> mabFormValues;
		Collection<String> formValuesIntersection;
		int bestFormValuesMatchCount = 0;
		List<Paradigm> allParadigms = mabService.getWordParadigms(wordLastComp);
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : allParadigms) {
			mabFormValues = paradigm.getFormValues();
			formValuesIntersection = CollectionUtils.intersection(qq2FormValues, mabFormValues);
			if (formValuesIntersection.size() > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = formValuesIntersection.size();
				matchingParadigm = paradigm;
			}
		}
		if (matchingParadigm == null) {
			logger.warn("\"{}({})\" has no paradigms in MAB", wordLastComp, word);
			return null;
		}
		Integer homonymNr = matchingParadigm.getHomonymNr();
		List<Paradigm> matchingParadigms = mabService.getWordParadigmsForHomonym(wordLastComp, homonymNr);
		if (isCompoundWord) {
			List<Paradigm> compoundWordParadigms = new ArrayList<>();
			for (Paradigm paradigm : matchingParadigms) {
				List<String> compoundFormValues = new ArrayList<>();
				List<Form> mabForms = paradigm.getForms();
				List<Form> compoundForms = new ArrayList<>();
				for (Form mabForm : mabForms) {
					String mabFormValue = mabForm.getValue();
					String compoundFormValue = StringUtils.join(wordComponents, "", 0, wordComponentCount - 1) + mabFormValue;
					compoundFormValues.add(compoundFormValue);
					Form compoundForm = new Form();
					compoundForm.setWord(mabForm.isWord());
					compoundForm.setMorphCode(mabForm.getMorphCode());
					compoundForm.setValue(compoundFormValue);
					compoundForms.add(compoundForm);
				}
				Paradigm compoundWordParadigm = new Paradigm();
				compoundWordParadigm.setFormValues(compoundFormValues);
				compoundWordParadigm.setForms(compoundForms);
				compoundWordParadigms.add(compoundWordParadigm);
			}
			return compoundWordParadigms;
		}
		return matchingParadigms;
	}

	private void saveDefinitions(List<Element> definitionValueNodes, Long meaningId, String dataLang, String dataset) throws Exception {

		if (definitionValueNodes == null) {
			return;
		}
		for (Element definitionValueNode : definitionValueNodes) {
			String definition = definitionValueNode.getTextTrim();
			createDefinition(meaningId, definition, dataLang, dataset);
		}
	}

	private void saveRections(List<Element> rectionNodes, Long lexemeId) throws Exception {

		if (rectionNodes == null) {
			return;
		}
		for (Element rectionNode : rectionNodes) {
			String rection = rectionNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.RECTION, rection, null);
		}
	}

	private void extractGrammar(List<Element> grammarNodes, Long wordId, Map<Long, List<Grammar>> wordIdGrammarMap) {

		List<Grammar> grammarObjs;
		Grammar grammarObj;
		String grammarLang;
		String grammar;

		for (Element grammarNode : grammarNodes) {

			grammarLang = grammarNode.attributeValue("lang");
			grammarLang = unifyLang(grammarLang);
			grammar = grammarNode.getTextTrim();

			grammarObjs = wordIdGrammarMap.get(wordId);
			if (grammarObjs == null) {
				grammarObjs = new ArrayList<>();
				wordIdGrammarMap.put(wordId, grammarObjs);
			}
			grammarObj = new Grammar();
			grammarObj.setLang(grammarLang);
			grammarObj.setValue(grammar);
			grammarObjs.add(grammarObj);
		}
	}

	private void extractRections(List<Element> rectionNodes, Long wordId, Map<Long, List<Rection>> wordIdRectionMap) {

		if (rectionNodes == null) {
			return;
		}
		List<Rection> rectionObjs = wordIdRectionMap.get(wordId);
		if (rectionObjs == null) {
			rectionObjs = new ArrayList<>();
			wordIdRectionMap.put(wordId, rectionObjs);
		}
		for (Element rectionNode : rectionNodes) {
			String rection = rectionNode.getTextTrim();
			Rection rectionObj = new Rection();
			rectionObj.setValue(rection);
			rectionObjs.add(rectionObj);
		}
	}

	private List<UsageMeaning> extractUsagesAndTranslations(List<Element> usageGroupNodes) {

		//x:np/x:ng

		List<UsageMeaning> usageMeanings = new ArrayList<>();

		UsageMeaning usageMeaning;
		List<Usage> usages;
		Usage newUsage;
		UsageTranslation usageTranslation;
		List<UsageTranslation> usageTranslations;
		List<String> originalTokens, lemmatisedTokens;
		String usageValue;
		String usageTranslationLang;
		Element usageTranslationValueNode;
		String usageTranslationValue;
		String[] usageTranslationParts;

		for (Element usageGroupNode : usageGroupNodes) {

			List<Element> usageNodes = usageGroupNode.selectNodes(usageExp);//x:n
			List<Element> usageTranslationNodes = usageGroupNode.selectNodes(usageTranslationExp);//x:qnp/x:qng

			usages = new ArrayList<>();
			for (Element usageNode : usageNodes) {
				usageValue = usageNode.getTextTrim();
				newUsage = new Usage();
				newUsage.setValue(usageValue);
				usages.add(newUsage);
			}

			usageTranslations = new ArrayList<>();
			for (Element usageTranslationNode : usageTranslationNodes) {
				usageTranslationLang = usageTranslationNode.attributeValue(langAttr);
				usageTranslationLang = unifyLang(usageTranslationLang);
				if (StringUtils.equalsIgnoreCase(usageTranslationLang, usageTranslationLangRus)) {
					usageTranslationValueNode = (Element) usageTranslationNode.selectSingleNode(usageTranslationValueExp);
					usageTranslationValue = usageTranslationValueNode.getTextTrim();
					usageTranslationValue = StringUtils.replaceEach(usageTranslationValue, textCleanupEnitites, textCleanupEnityReplacements);
					lemmatisedTokens = new ArrayList<>();
					usageTranslationParts = StringUtils.split(usageTranslationValue, ' ');
					originalTokens = new ArrayList<>(Arrays.asList(usageTranslationParts));
					for (String usageTranslationPart : usageTranslationParts) {
						usageTranslationPart = RussianWordProcessing.stripIllegalLetters(usageTranslationPart);
						usageTranslationPart = StringUtils.lowerCase(usageTranslationPart);
						if (StringUtils.isBlank(usageTranslationPart)) {
							continue;
						}
						List<String> usageTranslationPartWords = russianMorphology.getLemmas(usageTranslationPart);
						lemmatisedTokens.addAll(usageTranslationPartWords);
					}
					usageTranslation = new UsageTranslation();
					usageTranslation.setLang(usageTranslationLang);
					usageTranslation.setValue(usageTranslationValue);
					usageTranslation.setOriginalTokens(originalTokens);
					usageTranslation.setLemmatisedTokens(lemmatisedTokens);
					usageTranslations.add(usageTranslation);
				} else {
					//now what?!
					logger.warn("Unsupported usage translation language \"{}\"", usageTranslationLang);
				}
			}

			for (Usage usage : usages) {
				usage.setUsageTranslations(usageTranslations);
			}

			usageMeaning = new UsageMeaning();
			usageMeaning.setUsages(usages);
			usageMeanings.add(usageMeaning);
		}
		return usageMeanings;
	}

	private void createGrammars(Map<Long, List<Grammar>> wordIdGrammarMap, Long lexemeId, Long wordId, String dataset) throws Exception {

		List<Grammar> grammarObjs = wordIdGrammarMap.get(wordId);
		if (CollectionUtils.isEmpty(grammarObjs)) {
			return;
		}
		for (Grammar grammarObj : grammarObjs) {
			createLexemeFreeform(lexemeId, FreeformType.GRAMMAR, grammarObj.getValue(), grammarObj.getLang());
		}
	}

	private void createRectionsAndUsagesAndTranslations(
			String dataLang, Long lexemeId,
			List<Word> wordMatchObjs,
			List<Rection> rectionObjs,
			List<UsageMeaning> allUsageMeanings,
			boolean isSingleMeaning,
			Count singleUsageTranslationMatchCount) throws Exception {

		if (CollectionUtils.isEmpty(allUsageMeanings)) {
			return;
		}

		List<String> wordMatches = wordMatchObjs.stream().map(wordMatchObj -> wordMatchObj.getValue().toLowerCase()).collect(Collectors.toList());
		boolean containsMultiWords = containsMultiWords(wordMatches);

		if (CollectionUtils.isEmpty(rectionObjs)) {
			Rection rectionObj = new Rection();
			rectionObj.setValue(defaultRectionValue);
			rectionObjs = new ArrayList<>();
			rectionObjs.add(rectionObj);
		}

		List<UsageTranslation> usageTranslations;

		// collect usage meanings, usages, translations
		if (isSingleMeaning) {
			for (Rection rectionObj : rectionObjs) {
				rectionObj.setUsageMeanings(allUsageMeanings);
			}
			singleUsageTranslationMatchCount.increment();
		} else {
			List<UsageMeaning> matchingUsageMeanings = new ArrayList<>();
			List<Usage> matchingUsages;
			List<String> originalTokens;
			List<String> lemmatisedTokens;
			UsageMeaning matchingUsageMeaning;
			for (UsageMeaning usageMeaning : allUsageMeanings) {
				matchingUsages = new ArrayList<>();
				for (Usage usage : usageMeaning.getUsages()) {
					usageTranslations = usage.getUsageTranslations();
					boolean isUsageTranslationMatch = false;
					for (UsageTranslation usageTranslation : usageTranslations) {
						originalTokens = usageTranslation.getOriginalTokens();
						lemmatisedTokens = usageTranslation.getLemmatisedTokens();
						if (CollectionUtils.containsAny(originalTokens, wordMatches)) {
							isUsageTranslationMatch = true;
							break;
						} else if (CollectionUtils.containsAny(lemmatisedTokens, wordMatches)) {
							isUsageTranslationMatch = true;
							break;
						} else if (containsMultiWords) {
							boolean containsAnyMatchByWordSplit = containsAnyMatchByWordSplit(wordMatches, originalTokens, lemmatisedTokens);
							if (containsAnyMatchByWordSplit) {
								isUsageTranslationMatch = true;
								break;
							}
						}
					}
					if (isUsageTranslationMatch) {
						matchingUsages.add(usage);
					}
				}
				if (CollectionUtils.isNotEmpty(matchingUsages)) {
					matchingUsageMeaning = new UsageMeaning();
					matchingUsageMeaning.setUsages(matchingUsages);
					matchingUsageMeanings.add(matchingUsageMeaning);
				}
			}
			if (CollectionUtils.isNotEmpty(matchingUsageMeanings)) {
				for (Rection rectionObj : rectionObjs) {
					rectionObj.setUsageMeanings(matchingUsageMeanings);
				}
			}
		}

		// save rections, usage meanings, usages, usage translations
		for (Rection rectionObj : rectionObjs) {
			List<UsageMeaning> usageMeanings = rectionObj.getUsageMeanings();
			if (CollectionUtils.isEmpty(usageMeanings)) {
				if (!StringUtils.equals(rectionObj.getValue(), defaultRectionValue)) {
					createLexemeFreeform(lexemeId, FreeformType.RECTION, rectionObj.getValue(), null);
				}
			} else {
				Long rectionId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, rectionObj.getValue());
				for (UsageMeaning usageMeaning : usageMeanings) {
					Long usageMeaningId = createFreeformTextOrDate(FreeformType.USAGE_MEANING, rectionId, null, null);
					for (Usage usage : usageMeaning.getUsages()) {
						createFreeformTextOrDate(FreeformType.USAGE, usageMeaningId, usage.getValue(), dataLang);
						for (UsageTranslation usageTranslation : usage.getUsageTranslations()) {
							createFreeformTextOrDate(FreeformType.USAGE_TRANSLATION, usageMeaningId, usageTranslation.getValue(), usageTranslation.getLang());
						}
					}
				}
			}
		}
	}

	private boolean containsMultiWords(List<String> words) {

		for (String word : words) {
			word = StringUtils.trim(word);
			if (StringUtils.contains(word, ' ')) {
				return true;
			}
		}
		return false;
	}

	//TODO is this sufficient? should go with ordered words instead?
	private boolean containsAnyMatchByWordSplit(List<String> words, List<String> originalTokens, List<String> lemmatisedTokens) {

		List<String> wordTokensOfAllKind;
		List<String> wordTokenLemmas;
		for (String word : words) {
			String[] wordTokenArr = StringUtils.split(word, ' ');
			wordTokensOfAllKind = new ArrayList<>(Arrays.asList(wordTokenArr));
			for (String wordToken : wordTokenArr) {
				wordToken = RussianWordProcessing.stripIllegalLetters(wordToken);
				wordToken = StringUtils.lowerCase(wordToken);
				if (StringUtils.isBlank(wordToken)) {
					continue;
				}
				wordTokenLemmas = russianMorphology.getLemmas(wordToken);
				wordTokensOfAllKind.addAll(wordTokenLemmas);
			}
			if (CollectionUtils.containsAny(originalTokens, wordTokensOfAllKind)) {
				return true;
			} else if (CollectionUtils.containsAny(lemmatisedTokens, wordTokensOfAllKind)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getContentLines(InputStream resourceInputStream) throws Exception {
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}

}
