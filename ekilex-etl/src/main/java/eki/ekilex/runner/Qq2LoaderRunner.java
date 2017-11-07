package eki.ekilex.runner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eki.common.service.db.BasicDbService;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Rection;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.UsageMeaning;
import eki.ekilex.data.transform.UsageTranslation;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;

@Component
public class Qq2LoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Qq2LoaderRunner.class);

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "csv/transform-morph-deriv.csv";

	private static final String REPORT_MISSING_USAGE_MEANING_MATCH = "missing_usage_meaning_match";

	private static final String REPORT_AMBIGUOUS_USAGE_MEANING_MATCH = "ambiguous_usage_meaning_match";

	@Autowired
	private BasicDbService basicDbService;

	@Autowired
	private LuceneMorphology russianMorphology;

	private ReportComposer reportComposer;

	private Map<String, String> morphToMorphMap;

	private Map<String, String> morphToDerivMap;

	private final String articleExp = "/x:sr/x:A";
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
	private final String definitionValueExp = "x:xd";
	private final String wordMatchRectionExp = "x:xr";
	private final String synonymExp = "x:syn";
	private final String usageGroupExp = "x:np/x:ng";
	private final String usageExp = "x:n";
	private final String usageTranslationExp = "x:qnp/x:qng";
	private final String usageTranslationValueExp = "x:qn";
	private final String formsExp = "x:grg/x:vormid";

	private final String defaultWordMorphCode = "SgN";
	private final int defaultHomonymNr = 1;
	private final String defaultRectionValue = "-";
	private final String wordDisplayFormCleanupChars = "̄̆̇’'`´.:_–!°()¤";
	private final char wordComponentSeparator = '+';
	private final String formStrCleanupChars = "̄̆̇’\"'`´,;–+=";

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
			String dataXmlFilePath, String dataLang, String dataset,
			Map<String, List<Paradigm>> wordParadigmsMap, boolean doReports) throws Exception {

		logger.debug("Loading QQ2...");

		final String pseudoHomonymAttr = "i";
		final String lexemeLevel1Attr = "tnr";

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("qq2 load report", REPORT_MISSING_USAGE_MEANING_MATCH, REPORT_AMBIGUOUS_USAGE_MEANING_MATCH);
		}

		boolean isAddForms = wordParadigmsMap != null;
		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Map<Long, List<Rection>> wordIdRectionMap = new HashMap<>();
		Map<Long, List<Map<String, Object>>> wordIdGrammarMap = new HashMap<>();
		List<UsageMeaning> usageMeanings;

		Element headerNode, contentNode;
		List<Element> wordGroupNodes, rectionNodes, grammarNodes, meaningGroupNodes, meaningNodes;
		List<Element> definitionValueNodes, wordMatchNodes, synonymNodes, usageGroupNodes;
		Element wordNode, wordVocalFormNode, morphNode, wordMatchValueNode, formsNode;

		List<Word> newWords, wordMatches;
		List<Long> synonymLevel1WordIds, synonymLevel2WordIds;
		String word, wordMatch, pseudoHomonymNr, wordDisplayForm, wordVocalForm, lexemeLevel1Str, wordMatchLang, formsStr;
		String sourceMorphCode, destinMorphCode, destinDerivCode;
		int homonymNr, lexemeLevel1, lexemeLevel2, lexemeLevel3;
		Long wordId, newWordId, meaningId, lexemeId;
		String[] wordComponents;
		Word wordObj;
		Lexeme lexemeObj;
		Paradigm paradigmObj;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();
		Count missingUsageGroupCount = new Count();
		Count ambiguousUsageTranslationMatchCount = new Count();
		Count missingUsageTranslationMatchCount = new Count();
		Count successfulUsageTranslationMatchCount = new Count();
		Count singleUsageTranslationMatchCount = new Count();

		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

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
					paradigmObj = extractParadigm(word, wordComponents, formsNode, wordParadigmsMap);
				}

				// save word+paradigm+form
				wordObj = new Word(word, dataLang, wordComponents, wordDisplayForm, wordVocalForm, homonymNr, destinMorphCode);
				wordId = saveWord(wordObj, paradigmObj, wordDuplicateCount);
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

			synonymNodes = contentNode.selectNodes(synonymExp);
			synonymLevel1WordIds = saveWords(synonymNodes, dataLang, wordDuplicateCount);

			meaningGroupNodes = contentNode.selectNodes(meaningGroupExp);//x:tp

			for (Element meaningGroupNode : meaningGroupNodes) {

				lexemeLevel1Str = meaningGroupNode.attributeValue(lexemeLevel1Attr);
				lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);

				usageGroupNodes = meaningGroupNode.selectNodes(usageGroupExp);//x:np/x:ng
				//usages = extractUsagesAndTranslations(usageGroupNodes);
				usageMeanings = extractUsagesAndTranslations2(usageGroupNodes);
				if (CollectionUtils.isEmpty(usageGroupNodes)) {
					missingUsageGroupCount.increment();
				}
				wordMatches = new ArrayList<>();

				meaningNodes = meaningGroupNode.selectNodes(meaningExp);//x:tg
				boolean isSingleMeaning = meaningNodes.size() == 1;

				lexemeLevel2 = 0;

				for (Element meaningNode : meaningNodes) {

					lexemeLevel2++;
					lexemeLevel3 = 0;

					synonymNodes = meaningNode.selectNodes(synonymExp);
					synonymLevel2WordIds = saveWords(synonymNodes, dataLang, wordDuplicateCount);

					wordMatchNodes = meaningNode.selectNodes(wordMatchExpr);//x:xp/x:xg
					boolean isSingleWordMatch = wordMatchNodes.size() == 1;
					boolean isAbsoluteSingleMeaning = isSingleMeaning && isSingleWordMatch;

					for (Element wordMatchNode : wordMatchNodes) {

						lexemeLevel3++;

						wordMatchLang = wordMatchNode.attributeValue("lang");
						wordMatchLang = unifyLang(wordMatchLang);
						wordMatchValueNode = (Element) wordMatchNode.selectSingleNode(wordMatchValueExp);
						wordMatch = wordMatchValueNode.getTextTrim();
						wordMatch = StringUtils.replaceChars(wordMatch, wordDisplayFormCleanupChars, "");

						if (StringUtils.isBlank(wordMatch)) {
							continue;
						}

						wordObj = new Word(wordMatch, wordMatchLang, null, null, null, defaultHomonymNr, defaultWordMorphCode);
						wordId = saveWord(wordObj, null, wordDuplicateCount);
						wordMatches.add(wordObj);

						// meaning
						meaningId = createMeaning(dataset);

						// definitions
						definitionValueNodes = wordMatchNode.selectNodes(definitionValueExp);
						saveDefinitions(definitionValueNodes, meaningId, wordMatchLang, dataset);

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

						// new words lexemes+rections+grammar
						for (Word newWord : newWords) {

							newWordId = newWord.getId();
							lexemeObj = new Lexeme();
							lexemeObj.setWordId(newWordId);
							lexemeObj.setMeaningId(meaningId);
							lexemeObj.setLevel1(lexemeLevel1);
							lexemeObj.setLevel2(lexemeLevel2);
							lexemeObj.setLevel3(lexemeLevel3);
							lexemeId = createLexeme(lexemeObj, dataset);
							if (lexemeId == null) {
								lexemeDuplicateCount.increment();
							} else {

								// new word lexeme rections, usages, usage translations
								createRectionsAndUsagesAndTranslations(
										wordIdRectionMap, lexemeId, newWordId, wordMatch, usageMeanings, isAbsoluteSingleMeaning, singleUsageTranslationMatchCount);

								// new word lexeme grammars
								createGrammars(wordIdGrammarMap, lexemeId, newWordId, dataset);
							}
						}

						for (Long synonymWordId : synonymLevel1WordIds) {
							lexemeObj = new Lexeme();
							lexemeObj.setWordId(synonymWordId);
							lexemeObj.setMeaningId(meaningId);
							lexemeId = createLexeme(lexemeObj, dataset);
							if (lexemeId == null) {
								lexemeDuplicateCount.increment();
							}
						}

						for (Long synonymWordId : synonymLevel2WordIds) {
							lexemeObj = new Lexeme();
							lexemeObj.setWordId(synonymWordId);
							lexemeObj.setMeaningId(meaningId);
							lexemeId = createLexeme(lexemeObj, dataset);
							if (lexemeId == null) {
								lexemeDuplicateCount.increment();
							}
						}
					}
				}

				if (doReports) {
					detectAndReport(
							usageMeanings, newWords, wordMatches,
							ambiguousUsageTranslationMatchCount,
							missingUsageTranslationMatchCount,
							successfulUsageTranslationMatchCount);
				}
			}

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		if (reportComposer != null) {
			reportComposer.end();
		}

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);
		logger.debug("Found {} missing usage groups", missingUsageGroupCount);
		logger.debug("Found {} single usage translation matches", singleUsageTranslationMatchCount);
		if (doReports) {
			logger.debug("Found {} ambiguous usage translation matches", ambiguousUsageTranslationMatchCount);
			logger.debug("Found {} missing usage translation matches", missingUsageTranslationMatchCount);
			logger.debug("Found {} successful usage translation matches", successfulUsageTranslationMatchCount);
		}

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private Paradigm extractParadigm(String word, String[] wordComponents, Element formsNode, Map<String, List<Paradigm>> wordParadigmsMap) {

		int wordComponentCount = wordComponents.length;
		boolean isCompoundWord = wordComponentCount > 1;
		String wordLastComp = wordComponents[wordComponentCount - 1];
		List<Paradigm> paradigms = wordParadigmsMap.get(wordLastComp);
		if (CollectionUtils.isEmpty(paradigms)) {
			return null;
		}
		if (formsNode == null) {
			return null;
		}
		String formsStr = formsNode.getTextTrim();
		formsStr = StringUtils.replaceChars(formsStr, formStrCleanupChars, "");
		String[] formValuesArr = StringUtils.split(formsStr, ' ');
		List<String> qq2FormValues = Arrays.asList(formValuesArr);
		List<String> mabFormValues;
		Collection<String> formValuesIntersection;
		int bestFormValuesMatchCount = 0;
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : paradigms) {
			mabFormValues = paradigm.getFormValues();
			formValuesIntersection = CollectionUtils.intersection(qq2FormValues, mabFormValues);
			if (formValuesIntersection.size() > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = formValuesIntersection.size();
				matchingParadigm = paradigm;
			}
		}
		if (isCompoundWord && (matchingParadigm != null)) {
			List<String> compoundFormValues = new ArrayList<>();
			List<Form> mabForms = matchingParadigm.getForms();
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
			compoundWordParadigm.setWord(word);
			compoundWordParadigm.setFormValues(compoundFormValues);
			compoundWordParadigm.setForms(compoundForms);
			return compoundWordParadigm;
		}
		return matchingParadigm;
	}

	private void detectAndReport(
			List<UsageMeaning> usageMeanings, List<Word> newWords, List<Word> wordMatches,
			Count ambiguousUsageTranslationMatchCount,
			Count missingUsageTranslationMatchCount,
			Count successfulUsageTranslationMatchCount) throws Exception {

		List<UsageTranslation> usageTranslations;
		List<String> lemmatisedTokens;
		String wordMatch;
		StringBuffer logBuf;

		for (UsageMeaning usageMeaning : usageMeanings) {
			for (Usage usage : usageMeaning.getUsages()) {
				int usageWordMatchCount = 0;
				usageTranslations = usage.getUsageTranslations();
				for (UsageTranslation usageTranslation : usageTranslations) {
					lemmatisedTokens = usageTranslation.getLemmatisedTokens();
					for (Word wordMatchObj : wordMatches) {
						wordMatch = wordMatchObj.getValue();
						wordMatch = StringUtils.lowerCase(wordMatch);
						if (lemmatisedTokens.contains(wordMatch)) {
							usageWordMatchCount++;
						}
					}
				}
				if (usageWordMatchCount == 0) {
					missingUsageTranslationMatchCount.increment();
					logBuf = new StringBuffer();
					logBuf.append(newWords);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(usage);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(wordMatches);
					String logRow = logBuf.toString(); 
					reportComposer.append(REPORT_MISSING_USAGE_MEANING_MATCH, logRow);
				} else if (usageWordMatchCount == 1) {
					successfulUsageTranslationMatchCount.increment();
				} else if (usageWordMatchCount > 1) {
					ambiguousUsageTranslationMatchCount.increment();
					logBuf = new StringBuffer();
					logBuf.append(newWords);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(usage);
					logBuf.append(CSV_SEPARATOR);
					logBuf.append(wordMatches);
					String logRow = logBuf.toString(); 
					reportComposer.append(REPORT_AMBIGUOUS_USAGE_MEANING_MATCH, logRow);
				}
			}
		}
	}

	private List<Long> saveWords(List<Element> synonymNodes, String lang, Count wordDuplicateCount) throws Exception {

		List<Long> synonymWordIds = new ArrayList<>();
		Word wordObj;
		String synonym;
		Long wordId;

		for (Element synonymNode : synonymNodes) {
			synonym = synonymNode.getTextTrim();
			wordObj = new Word();
			wordObj.setValue(synonym);
			wordObj.setHomonymNr(defaultHomonymNr);
			wordObj.setMorphCode(defaultWordMorphCode);
			wordObj.setLang(lang);
			wordId = saveWord(wordObj, null, wordDuplicateCount);
			synonymWordIds.add(wordId);
		}
		return synonymWordIds;
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

	private void saveRections(List<Element> rectionNodes, Long lexemeId) throws Exception {

		if (rectionNodes == null) {
			return;
		}
		for (Element rectionNode : rectionNodes) {
			String rection = rectionNode.getTextTrim();
			createLexemeFreeform(lexemeId, FreeformType.RECTION, rection);
		}
	}

	private void extractGrammar(List<Element> grammarNodes, Long wordId, Map<Long, List<Map<String, Object>>> wordIdGrammarMap) {

		List<Map<String, Object>> grammarObjs;
		Map<String, Object> grammarObj;
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
			grammarObj = new HashMap<>();
			grammarObj.put("lang", grammarLang);
			grammarObj.put("value", grammar);
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

	private List<UsageMeaning> extractUsagesAndTranslations2(List<Element> usageGroupNodes) {

		//x:np/x:ng

		List<UsageMeaning> usageMeanings = new ArrayList<>();

		UsageMeaning usageMeaning;
		List<Usage> usages;
		Usage newUsage;
		UsageTranslation usageTranslation;
		List<UsageTranslation> usageTranslations;
		List<String> lemmatisedTokens;
		String usageValue;
		String usageTranslationLang;
		Element usageTranslationValueNode;
		String usageTranslationValue;
		String[] usageTranslationParts;

		for (Element usageGroupNode : usageGroupNodes) {

			List<Element> usageNodes = usageGroupNode.selectNodes(usageExp);
			List<Element> usageTranslationNodes = usageGroupNode.selectNodes(usageTranslationExp);

			usages = new ArrayList<>();
			for (Element usageNode : usageNodes) {
				usageValue = usageNode.getTextTrim();
				newUsage = new Usage();
				newUsage.setValue(usageValue);
				usages.add(newUsage);
			}

			usageTranslations = new ArrayList<>();
			for (Element usageTranslationNode : usageTranslationNodes) {
				usageTranslationLang = usageTranslationNode.attributeValue("lang");
				usageTranslationLang = unifyLang(usageTranslationLang);
				if (StringUtils.equalsIgnoreCase(usageTranslationLang, "rus")) {
					usageTranslationValueNode = (Element) usageTranslationNode.selectSingleNode(usageTranslationValueExp);
					usageTranslationValue = usageTranslationValueNode.getTextTrim();
					lemmatisedTokens = new ArrayList<>();
					usageTranslationParts = StringUtils.split(usageTranslationValue, ' ');
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

	private void createGrammars(Map<Long, List<Map<String, Object>>> wordIdGrammarMap, Long lexemeId, Long wordId, String dataset) throws Exception {

		List<Map<String, Object>> grammarObjs = wordIdGrammarMap.get(wordId);
		if (CollectionUtils.isNotEmpty(grammarObjs)) {
			for (Map<String, Object> grammarObj : grammarObjs) {
				grammarObj.put("lexeme_id", lexemeId);
				Long grammarId = basicDbService.createIfNotExists(GRAMMAR, grammarObj);
				if (grammarId != null) {
					Map<String, Object> params = new HashMap<>();
					params.put("grammar_id", grammarId);
					params.put("dataset_code", dataset);
					basicDbService.createWithoutId(GRAMMAR_DATASET, params);
				}
			}
		}
	}

	//TODO refactor
	private void createRectionsAndUsagesAndTranslations(
			Map<Long, List<Rection>> wordIdRectionMap,
			Long lexemeId, Long wordId, String wordMatch,
			List<UsageMeaning> usageMeanings, boolean isAbsoluteSingleMeaning,
			Count singleUsageTranslationMatchCount) throws Exception {

		if (CollectionUtils.isEmpty(usageMeanings)) {
			return;
		}

		wordMatch = StringUtils.lowerCase(wordMatch);
		List<Rection> rectionObjs = wordIdRectionMap.get(wordId);

		List<Long> rectionFreeformIds = new ArrayList<>();

		if (CollectionUtils.isEmpty(rectionObjs)) {

			Long rectionFreeformId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, defaultRectionValue);
			if (rectionFreeformId != null) {
				rectionFreeformIds.add(rectionFreeformId);
			}
		} else {
			for (Rection rectionObj : rectionObjs) {
				Long rectionFreeformId = createOrSelectLexemeFreeform(lexemeId, FreeformType.RECTION, rectionObj.getValue());
				if (rectionFreeformId != null) {
					rectionFreeformIds.add(rectionFreeformId);
				}
			}
		}

		List<UsageTranslation> usageTranslations;

		if (isAbsoluteSingleMeaning) {
			for (Long rectionFreeformId : rectionFreeformIds) {
				for (UsageMeaning usageMeaning : usageMeanings) {
					//TODO create meaning freeform
					for (Usage usage : usageMeaning.getUsages()) {
						usageTranslations = usage.getUsageTranslations();
						Long usageId = createUsage(rectionFreeformId, usage.getValue());
						for (UsageTranslation usageTranslation : usageTranslations) {
							createUsageTranslation(usageId, usageTranslation.getValue(), usageTranslation.getLang());
						}
					}
				}
			}
			singleUsageTranslationMatchCount.increment();
		} else {
			boolean isAnyUsageTranslationMatch = false;
			for (UsageMeaning usageMeaning : usageMeanings) {
				//TODO create meaning freeform
				for (Usage usage : usageMeaning.getUsages()) {
					usageTranslations = usage.getUsageTranslations();
					boolean isUsageTranslationMatch = false;
					for (UsageTranslation usageTranslation : usageTranslations) {
						if (usageTranslation.getLemmatisedTokens().contains(wordMatch)) {
							isAnyUsageTranslationMatch = isUsageTranslationMatch = true;
							break;
						}
					}
					if (isUsageTranslationMatch) {
						for (Long rectionId : rectionFreeformIds) {
							Long usageId = createUsage(rectionId, usage.getValue());
							for (UsageTranslation usageTranslation : usageTranslations) {
								createUsageTranslation(usageId, usageTranslation.getValue(), usageTranslation.getLang());
							}
						}
					}
				}
			}
			if (!isAnyUsageTranslationMatch) {
				basicDbService.delete(RECTION, rectionFreeformIds);
			}
		}
	}

	private List<String> getContentLines(InputStream resourceInputStream) throws Exception {
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}

}
