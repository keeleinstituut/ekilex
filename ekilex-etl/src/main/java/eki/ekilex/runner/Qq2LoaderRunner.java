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
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Government;
import eki.ekilex.data.transform.Grammar;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
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
	private final String wordGovernmentExp = "x:r";
	private final String wordGrammarExp = "x:grg/x:gki";
	private final String articleBodyExp = "x:S";
	private final String meaningGroupExp = "x:tp";
	private final String meaningExp = "x:tg";
	private final String wordMatchExpr = "x:xp/x:xg";
	private final String wordMatchValueExp = "x:x";
	private final String definitionExp = "x:xd";
	private final String wordMatchGovernmentExp = "x:xr";
	private final String synonymExp = "x:syn";
	private final String usageGroupExp = "x:np/x:ng";
	private final String usageExp = "x:n";
	private final String usageTranslationExp = "x:qnp/x:qng";
	private final String usageTranslationValueExp = "x:qn";
	private final String formsExp = "x:grg/x:vormid";

	private final String langAttr = "lang";

	private final String defaultWordMorphCode = "??";
	private final int defaultHomonymNr = 1;
	private final String wordDisplayFormCleanupChars = "̄̆̇’'`´.:_–!°()¤";
	private final String wordComponentSeparator = "+";
	private final String formStrCleanupChars = "̄̆̇’\"'`´,;–+=()";
	private final String usageTranslationLangRus = "rus";
	private final String dataLang = "est";

	@Override
	public String getDataset() {
		return "qq2";
	}

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
	public void execute(String dataXmlFilePath, Map<String, List<Guid>> ssGuidMap, boolean doReports) throws Exception {

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
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
		long articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Map<Long, List<Government>> wordIdGovernmentMap = new HashMap<>();
		Map<Long, List<Grammar>> wordIdGrammarMap = new HashMap<>();

		Element headerNode, contentNode;
		List<Node> wordGroupNodes, governmentNodes, grammarNodes, meaningGroupNodes, meaningNodes;
		List<Node> definitionNodes, wordMatchNodes, synonymLevel1Nodes, synonymLevel2Nodes, usageGroupNodes;
		Element guidNode, wordNode, wordVocalFormNode, morphNode, wordMatchValueNode, formsNode;

		List<Usage> allUsages;
		List<Word> newWords, wordMatches, allWordMatches;
		List<Paradigm> paradigms;
		List<Government> governments;
		String guid, word, wordFormsStr, wordMatch, pseudoHomonymNr, wordDisplayForm, wordVocalForm, lexemeLevel1Str, wordMatchLang;
		String sourceMorphCode, destinMorphCode, destinDerivCode;
		int homonymNr, lexemeLevel1, lexemeLevel2;
		Long wordId, newWordId, meaningId, lexemeId;
		String[] wordComponents;
		Word wordObj;
		Lexeme lexemeObj;
		List<Long> wordIds;

		Count reusedWordCount = new Count();
		Count ssWordCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count missingUsageGroupCount = new Count();
		Count missingMabIntegrationCaseCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Node articleNode : articleNodes) {

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
			wordIds = new ArrayList<>();

			for (Node wordGroupNode : wordGroupNodes) {

				// word, form...
				wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				word = wordDisplayForm = wordNode.getTextTrim();
				word = cleanEkiEntityMarkup(word);
				word = StringUtils.replaceChars(word, wordDisplayFormCleanupChars, "");
				if (StringUtils.endsWith(word, wordComponentSeparator)) {
					word = StringUtils.removeEnd(word, wordComponentSeparator);
					word = word + "-";
				}
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
				wordObj = new Word(word, dataLang, wordFormsStr, wordComponents, wordDisplayForm, wordVocalForm, homonymNr, destinMorphCode, guid, null);
				wordId = createOrSelectWord(wordObj, paradigms, getDataset(), ssGuidMap, ssWordCount, reusedWordCount);
				if (!wordIds.contains(wordId)) {
					wordIds.add(wordId);
					newWords.add(wordObj);
				}

				// governments...
				governmentNodes = wordGroupNode.selectNodes(wordGovernmentExp);
				extractGovernments(governmentNodes, wordId, wordIdGovernmentMap);

				// grammar...
				grammarNodes = wordGroupNode.selectNodes(wordGrammarExp);
				extractGrammar(grammarNodes, wordId, wordIdGrammarMap);
			}

			// body...

			synonymLevel1Nodes = contentNode.selectNodes(synonymExp);
			meaningGroupNodes = contentNode.selectNodes(meaningGroupExp);//x:tp

			for (Node meaningGroupNode : meaningGroupNodes) {

				lexemeLevel1Str = ((Element)meaningGroupNode).attributeValue(lexemeLevel1Attr);
				lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);

				usageGroupNodes = meaningGroupNode.selectNodes(usageGroupExp);//x:np/x:ng
				allUsages = extractUsages(usageGroupNodes);
				if (CollectionUtils.isEmpty(usageGroupNodes)) {
					missingUsageGroupCount.increment();
				}

				meaningNodes = meaningGroupNode.selectNodes(meaningExp);//x:tg
				boolean isSingleMeaning = meaningNodes.size() == 1;
				lexemeLevel2 = 0;
				allWordMatches = new ArrayList<>();

				for (Node meaningNode : meaningNodes) {

					lexemeLevel2++;

					// meaning
					meaningId = createMeaning();

					// definitions #1
					synonymLevel2Nodes = meaningNode.selectNodes(synonymExp);
					createDefinitions(synonymLevel1Nodes, meaningId, dataLang, getDataset());
					createDefinitions(synonymLevel2Nodes, meaningId, dataLang, getDataset());

					wordMatchNodes = meaningNode.selectNodes(wordMatchExpr);//x:xp/x:xg
					wordMatches = new ArrayList<>();

					for (Node wordMatchNode : wordMatchNodes) {

						wordMatchLang = ((Element)wordMatchNode).attributeValue(langAttr);
						wordMatchLang = unifyLang(wordMatchLang);
						wordMatchValueNode = (Element) wordMatchNode.selectSingleNode(wordMatchValueExp);
						wordMatch = wordMatchValueNode.getTextTrim();
						wordMatch = cleanEkiEntityMarkup(wordMatch);
						wordMatch = StringUtils.replaceChars(wordMatch, wordDisplayFormCleanupChars, "");

						if (StringUtils.isBlank(wordMatch)) {
							continue;
						}

						wordObj = new Word(wordMatch, wordMatchLang, null, null, null, null, defaultHomonymNr, defaultWordMorphCode, null, null);
						wordId = createOrSelectWord(wordObj, null, getDataset(), reusedWordCount);
						wordMatches.add(wordObj);
						allWordMatches.add(wordObj);

						// definitions #2
						definitionNodes = wordMatchNode.selectNodes(definitionExp);
						createDefinitions(definitionNodes, meaningId, wordMatchLang, getDataset());

						// word match lexeme
						lexemeObj = new Lexeme();
						lexemeObj.setWordId(wordId);
						lexemeObj.setMeaningId(meaningId);
						lexemeId = createLexeme(lexemeObj, getDataset());

						if (lexemeId == null) {
							lexemeDuplicateCount.increment();
						} else {
							// word match lexeme government
							governmentNodes = wordMatchValueNode.selectNodes(wordMatchGovernmentExp);
							createGovernments(governmentNodes, lexemeId, wordMatchLang);
						}
					}

					// new words lexemes+governments+usages+grammar
					for (Word newWord : newWords) {

						newWordId = newWord.getId();
						lexemeObj = new Lexeme();
						lexemeObj.setWordId(newWordId);
						lexemeObj.setMeaningId(meaningId);
						lexemeObj.setLevel1(lexemeLevel1);
						lexemeObj.setLevel2(lexemeLevel2);
						lexemeId = createLexeme(lexemeObj, getDataset());

						governments = wordIdGovernmentMap.get(newWordId);

						createGovernments(lexemeId, governments, dataLang);

						if (isSingleMeaning) {
							createUsages(lexemeId, allUsages, dataLang);
						} else {
							List<Usage> matchingUsages = collectMatchingUsages(wordMatches, allUsages);
							createUsages(lexemeId, matchingUsages, dataLang);
						}

						createGrammars(wordIdGrammarMap, lexemeId, newWordId, getDataset());
					}
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

		logger.debug("Found {} reused words", reusedWordCount.getValue());
		logger.debug("Found {} ss words", ssWordCount.getValue());
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount.getValue());
		logger.debug("Found {} missing usage groups", missingUsageGroupCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
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

	private List<Paradigm> extractParadigms(String word, String wordFormsStr, String[] wordComponents) throws Exception {

		int wordComponentCount = wordComponents.length;
		boolean isCompoundWord = wordComponentCount > 1;
		String wordLastComp = wordComponents[wordComponentCount - 1];
		List<Paradigm> matchingParadigms;

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
			matchingParadigms = mabService.getWordParadigms(wordLastComp);
			if (isCompoundWord) {
				return composeCompoundWordParadigms(wordComponents, wordComponentCount, matchingParadigms);
			}
			return matchingParadigms;
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
		matchingParadigms = mabService.getWordParadigmsForHomonym(wordLastComp, homonymNr);
		if (isCompoundWord) {
			return composeCompoundWordParadigms(wordComponents, wordComponentCount, matchingParadigms);
		}
		return matchingParadigms;
	}

	private List<Paradigm> composeCompoundWordParadigms(String[] wordComponents, int wordComponentCount, List<Paradigm> lastCompParadigms) {

		List<Paradigm> compoundWordParadigms = new ArrayList<>();
		for (Paradigm lastCompParadigm : lastCompParadigms) {
			List<String> compoundFormValues = new ArrayList<>();
			List<Form> mabForms = lastCompParadigm.getForms();
			List<Form> compoundForms = new ArrayList<>();
			for (Form mabForm : mabForms) {
				String compoundFormValue = StringUtils.join(wordComponents, "", 0, wordComponentCount - 1) + mabForm.getValue();
				String compoundDisplayForm = StringUtils.join(wordComponents, '+', 0, wordComponentCount - 1) + '+' + mabForm.getDisplayForm(); 
				compoundFormValues.add(compoundFormValue);
				Form compoundForm = copy(mabForm);
				compoundForm.setValue(compoundFormValue);
				compoundForm.setDisplayForm(compoundDisplayForm);
				compoundForms.add(compoundForm);
			}
			Paradigm compoundWordParadigm = new Paradigm();
			compoundWordParadigm.setFormValues(compoundFormValues);
			compoundWordParadigm.setForms(compoundForms);
			compoundWordParadigms.add(compoundWordParadigm);
		}
		return compoundWordParadigms;
	}

	private Form copy(Form mabForm) {
		Form copy = new Form();
		copy.setMode(mabForm.getMode());
		copy.setMorphGroup1(mabForm.getMorphGroup1());
		copy.setMorphGroup2(mabForm.getMorphGroup2());
		copy.setMorphGroup3(mabForm.getMorphGroup3());
		copy.setDisplayLevel(mabForm.getDisplayLevel());
		copy.setMorphCode(mabForm.getMorphCode());
		copy.setMorphExists(mabForm.getMorphExists());
		copy.setValue(mabForm.getValue());
		copy.setDisplayForm(mabForm.getDisplayForm());
		copy.setOrderBy(mabForm.getOrderBy());
		return copy;
	}

	private void createDefinitions(List<Node> definitionValueNodes, Long meaningId, String dataLang, String dataset) throws Exception {

		if (definitionValueNodes == null) {
			return;
		}
		for (Node definitionValueNode : definitionValueNodes) {
			String definition = ((Element)definitionValueNode).getTextTrim();
			definition = cleanEkiEntityMarkup(definition);
			createDefinition(meaningId, definition, dataLang, dataset);
		}
	}

	private void createGovernments(List<Node> governmentNodes, Long lexemeId, String lang) throws Exception {

		if (governmentNodes == null) {
			return;
		}
		for (Node governmentNode : governmentNodes) {
			String government = ((Element)governmentNode).getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government, lang);
		}
	}

	private void extractGrammar(List<Node> grammarNodes, Long wordId, Map<Long, List<Grammar>> wordIdGrammarMap) {

		List<Grammar> grammarObjs;
		Grammar grammarObj;
		String grammarLang;
		String grammar;

		for (Node grammarNode : grammarNodes) {

			grammarLang = ((Element)grammarNode).attributeValue("lang");
			grammarLang = unifyLang(grammarLang);
			grammar = ((Element)grammarNode).getTextTrim();
			grammar = cleanEkiEntityMarkup(grammar);

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

	private void extractGovernments(List<Node> governmentNodes, Long wordId, Map<Long, List<Government>> wordIdGovernmentMap) {

		if (governmentNodes == null) {
			return;
		}
		List<Government> governmentObjs = wordIdGovernmentMap.get(wordId);
		if (governmentObjs == null) {
			governmentObjs = new ArrayList<>();
			wordIdGovernmentMap.put(wordId, governmentObjs);
		}
		for (Node governmentNode : governmentNodes) {
			String government = ((Element)governmentNode).getTextTrim();
			Government governmentObj = new Government();
			governmentObj.setValue(government);
			governmentObjs.add(governmentObj);
		}
	}

	private List<Usage> extractUsages(List<Node> usageGroupNodes) {

		//x:np/x:ng

		List<Usage> usages = new ArrayList<>();

		Usage newUsage;
		UsageTranslation usageTranslation;
		List<UsageTranslation> usageTranslations;
		List<String> originalTokens, lemmatisedTokens;
		String usageValue;
		String usageTranslationLang;
		Element usageTranslationValueNode;
		String usageTranslationValue;
		String[] usageTranslationParts;

		for (Node usageGroupNode : usageGroupNodes) {

			List<Node> usageNodes = usageGroupNode.selectNodes(usageExp);//x:n
			List<Node> usageTranslationNodes = usageGroupNode.selectNodes(usageTranslationExp);//x:qnp/x:qng

			usages = new ArrayList<>();
			for (Node usageNode : usageNodes) {
				usageValue = ((Element)usageNode).getTextTrim();
				usageValue = cleanEkiEntityMarkup(usageValue);
				newUsage = new Usage();
				newUsage.setValue(usageValue);
				usages.add(newUsage);
			}

			usageTranslations = new ArrayList<>();
			for (Node usageTranslationNode : usageTranslationNodes) {
				usageTranslationLang = ((Element)usageTranslationNode).attributeValue(langAttr);
				usageTranslationLang = unifyLang(usageTranslationLang);
				if (StringUtils.equalsIgnoreCase(usageTranslationLang, usageTranslationLangRus)) {
					usageTranslationValueNode = (Element) usageTranslationNode.selectSingleNode(usageTranslationValueExp);
					usageTranslationValue = usageTranslationValueNode.getTextTrim();
					usageTranslationValue = cleanEkiEntityMarkup(usageTranslationValue);
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
		}
		return usages;
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

	private void createGovernments(Long lexemeId, List<Government> governments, String dataLang) throws Exception {

		if (CollectionUtils.isEmpty(governments)) {
			return;
		}

		for (Government government : governments) {
			createLexemeFreeform(lexemeId, FreeformType.GOVERNMENT, government.getValue(), dataLang);
		}
	}

	private List<Usage> collectMatchingUsages(List<Word> wordMatches, List<Usage> allUsages) {

		List<String> wordMatchValues = wordMatches.stream().map(wordMatchObj -> wordMatchObj.getValue().toLowerCase()).collect(Collectors.toList());
		boolean containsMultiWords = containsMultiWords(wordMatchValues);

		List<Usage> matchingUsages = new ArrayList<>();
		List<String> originalTokens;
		List<String> lemmatisedTokens;

		for (Usage usage : allUsages) {
			boolean isUsageTranslationMatch = false;
			for (UsageTranslation usageTranslation : usage.getUsageTranslations()) {
				originalTokens = usageTranslation.getOriginalTokens();
				lemmatisedTokens = usageTranslation.getLemmatisedTokens();
				if (CollectionUtils.containsAny(originalTokens, wordMatchValues)) {
					isUsageTranslationMatch = true;
					break;
				} else if (CollectionUtils.containsAny(lemmatisedTokens, wordMatchValues)) {
					isUsageTranslationMatch = true;
					break;
				} else if (containsMultiWords) {
					boolean containsAnyMatchByWordSplit = containsAnyMatchByWordSplit(wordMatchValues, originalTokens, lemmatisedTokens);
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

		return matchingUsages;
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
