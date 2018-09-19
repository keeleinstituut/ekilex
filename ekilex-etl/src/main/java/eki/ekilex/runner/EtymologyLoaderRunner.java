package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.ekilex.service.ReportComposer;

@Component
public class EtymologyLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EtymologyLoaderRunner.class);

	private static final String REPORT_MISSING_ETYM_TYPE = "missing_etym_type";

	private static final String REPORT_UNKNOWN_ETYM_TYPE = "unknown_etym_type";

	private static final String REPORT_UNKNOWN_REGISTER = "unknown_register";

	private final String formStrCleanupChars = ".()¤:_|[]̄̆̇\"`´–+=";
	private final String etymExp = "s:S/s:etp";
	private final String guidExp = "s:G";
	private final String headWordExp = "s:P/s:mg/s:m";
	private final String etymRootCommentExp = "s:ek";
	private final String etymRootPrivateNoteExp = "s:mrk";
	private final String etymGroupExp = "s:etg";
	private final String etymWordWrapupExp = "s:etgg/s:ex";
	private final String etymTypeExp = "s:epl";
	private final String etymSubGroupExp = "s:etgg[count(s:ex)>0]";
	private final String etymLangExp = "s:k";
	private final String etymWordExp = "s:ex";
	private final String etymCommentExp = "s:dtx";
	private final String etymRegisterExp = "s:s";
	private final String etymEstWordExp = "s:ed";
	private final String etymAuthorExp = "s:autg/s:aut";
	private final String etymYearExp = "s:autg/s:a";
	private final String etymQuestionableAttr = "ky";
	private final String etymAlternativeAttr = "alt";
	private final String etymWordCompoundAttr = "etl";
	private final String langEst = "est";

	private ReportComposer reportComposer;

	private Map<String, String> etymTypes;

	private Map<String, String> registerConversionMap;

	@Override
	String getDataset() {
		return "ety";
	}

	@Override
	public void initialise() throws Exception {
		etymTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_ETYMPLTYYP);
		registerConversionMap = loadClassifierMappingsFor(EKI_CLASSIFIER_STYYP, ClassifierName.REGISTER.name());
	}

	@Transactional
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Loading Etymology...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("etymology loader report", REPORT_MISSING_ETYM_TYPE, REPORT_UNKNOWN_ETYM_TYPE);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
		long articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		final String ssDataset = "ss1";
		//TODO remove later
		final String missingLangCode = "---";
		final String defaultWordMorphCode = "??";

		List<Map<String, Object>> wordDatas;

		Count etymCount = new Count();
		Count missingEtymTypeCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			Element etymNode = (Element) articleNode.selectSingleNode(etymExp);
			if (etymNode != null) {
				Element guidNode = (Element) articleNode.selectSingleNode(guidExp);
				String guid = guidNode.getTextTrim();
				List<Element> wordNodes = articleNode.selectNodes(headWordExp);
				List<String> words = wordNodes.stream()
						.map(wordNode -> {
							String rawWord = wordNode.getTextTrim();
							String word = cleanUp(rawWord);
							return word;
						})
						.distinct()
						.collect(Collectors.toList());

				List<Long> headWordIds = new ArrayList<>();
				for (String word : words) {
					wordDatas = getWords(word, guid, ssDataset);
					for (Map<String, Object> wordData : wordDatas) {
						Long wordId = (Long) wordData.get("id");
						headWordIds.add(wordId);
					}
				}

				List<Element> etymCommentNodes = etymNode.selectNodes(etymRootCommentExp);
				List<String> etymRootComments = null;
				if (CollectionUtils.isNotEmpty(etymCommentNodes)) {
					etymRootComments = etymCommentNodes.stream().map(Element::getTextTrim).collect(Collectors.toList());
				}
				//TODO impl
				List<Element> etymPrivateNoteNodes = etymNode.selectNodes(etymRootPrivateNoteExp);
				List<Element> etymGroupNodes = etymNode.selectNodes(etymGroupExp);
				List<Long> word1Ids = null;
				List<Long> word2Ids = null;
				List<String> etymComments = null;
				String etymTypeCode = null;
				String mappedEtymTypeCode = null;
				String mappedRegisterCode = null;
				for (Element etymGroupNode : etymGroupNodes) {
					List<Element> etymWordsWrapupNodes = etymGroupNode.selectNodes(etymWordWrapupExp);
					List<String> etymWordsWrapup = etymWordsWrapupNodes.stream().map(Element::getTextTrim).collect(Collectors.toList());
					Element etymTypeNode = (Element) etymGroupNode.selectSingleNode(etymTypeExp);
					if (etymTypeNode == null) {
						missingEtymTypeCount.increment();
						mappedEtymTypeCode = null;
						appendToReport(doReports, REPORT_MISSING_ETYM_TYPE, words, etymWordsWrapup);
					} else {
						etymTypeCode = etymTypeNode.getTextTrim();
						mappedEtymTypeCode = etymTypes.get(etymTypeCode);
						if (mappedEtymTypeCode == null) {
							logger.debug("Unknown etym type \"{}\"", etymTypeCode);
							appendToReport(doReports, REPORT_UNKNOWN_ETYM_TYPE, words, etymWordsWrapup, etymTypeCode);
						}
					}
					String etymQuestionable = etymGroupNode.attributeValue(etymQuestionableAttr);
					boolean isEtymQuestionable = StringUtils.isNotBlank(etymQuestionable);
					String etymAlternative = etymGroupNode.attributeValue(etymAlternativeAttr);
					boolean isEtymAlternative = StringUtils.isNotBlank(etymAlternative);
					boolean isFirstEtym;
					if (word1Ids == null) {
						word1Ids = new ArrayList<>(headWordIds);
						isFirstEtym = true;
					} else {
						if (!isEtymAlternative) {
							word1Ids = new ArrayList<>(word2Ids);
						}
						isFirstEtym = false;
					}
					word2Ids = new ArrayList<>();
					// etym subgroup
					List<Element> etymSubGroupNodes = etymGroupNode.selectNodes(etymSubGroupExp);
					for (Element etymSubGroupNode : etymSubGroupNodes) {
						etymQuestionable = etymSubGroupNode.attributeValue(etymQuestionableAttr);
						isEtymQuestionable = (isFirstEtym && isEtymQuestionable) || StringUtils.isNotBlank(etymQuestionable);
						String etymWordCompound = etymSubGroupNode.attributeValue(etymWordCompoundAttr);
						boolean isEtymWordCompound = StringUtils.isNotBlank(etymWordCompound);
						//TODO impl
						List<Element> etymLangNodes = etymSubGroupNode.selectNodes(etymLangExp);
						List<Element> etymWordNodes = etymSubGroupNode.selectNodes(etymWordExp);
						Element etymCommentNode = (Element) etymSubGroupNode.selectSingleNode(etymCommentExp);
						if (isFirstEtym && CollectionUtils.isNotEmpty(etymRootComments)) {
							etymComments = new ArrayList<>(etymRootComments);
						} else {
							etymComments = new ArrayList<>();
						}
						if (etymCommentNode != null) {
							String etymComment = etymCommentNode.getTextTrim();
							etymComments.add(etymComment);
						}
						List<Element> etymRegisterNodes = etymSubGroupNode.selectNodes(etymRegisterExp);
						List<String> etymRegisterCodes = new ArrayList<>();
						for (Element etymRegisterNode : etymRegisterNodes) {
							String etymRegister = etymRegisterNode.getTextTrim();
							mappedRegisterCode = registerConversionMap.get(etymRegister);
							if (mappedRegisterCode == null) {
								logger.debug("Unknown register \"{}\"", etymRegister);
								appendToReport(doReports, REPORT_UNKNOWN_REGISTER, words, etymWordsWrapup, etymRegister);
								continue;
							}
							etymRegisterCodes.add(mappedRegisterCode);
						}
						Long meaningId = createMeaning();
						// foreign etym words
						for (Element etymWordNode : etymWordNodes) {
							String word = etymWordNode.getTextTrim();
							int homonymNr = getWordMaxHomonymNr(word, missingLangCode);//TODO collect lang by mapped iso 
							homonymNr++;
							Long wordId = createWordParadigmForm(word, defaultWordMorphCode, homonymNr, missingLangCode);//TODO collect lang by mapped iso
							Long lexemeId = createLexeme(wordId, meaningId);
							for (String etymRegisterCode : etymRegisterCodes) {
								createLexemeRegister(lexemeId, etymRegisterCode);
							}
							word2Ids.add(wordId);
							for (Long word1Id : word1Ids) {
								createWordEtymology(word1Id, wordId, mappedEtymTypeCode, etymComments, isEtymQuestionable, isEtymWordCompound);
								etymCount.increment();
							}
						}
						// est etym words
						List<Element> etymEstWordNodes = etymSubGroupNode.selectNodes(etymEstWordExp);
						if (CollectionUtils.isNotEmpty(etymEstWordNodes)) {
							for (Element etymEstWordNode : etymEstWordNodes) {
								String word = etymEstWordNode.getTextTrim();
								int homonymNr = getWordMaxHomonymNr(word, langEst);
								homonymNr++;
								Long wordId = createWordParadigmForm(word, defaultWordMorphCode, homonymNr, langEst);
								createLexeme(wordId, meaningId);
							}
						}
						//TODO impl
						List<Element> etymAuthorNodes = etymSubGroupNode.selectNodes(etymAuthorExp);
						//TODO impl
						Element etymYearNode = (Element) etymSubGroupNode.selectSingleNode(etymYearExp);

					}
				}
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

		logger.debug("Found {} word etym relations", etymCount.getValue());
		logger.debug("Found {} etym groups with missing type", missingEtymTypeCount.getValue());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private String cleanUp(String value) {
		String cleanedWord = replaceChars(value, formStrCleanupChars, "");
		return removePattern(cleanedWord, "[&]\\w+[;]");
	}

	private List<Map<String, Object>> getWords(String word, String guid, String dataset) throws Exception {

		guid = guid.toLowerCase();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("guid", guid);
		tableRowParamMap.put("dataset", dataset);
		List<Map<String, Object>> results = basicDbService.queryList(sqlSelectWordByDatasetAndGuid, tableRowParamMap);
		return results;
	}

	private Long createWordEtymology(Long word1Id, Long word2Id, String etymTypeCode, List<String> comments, boolean isQuestionable, boolean isCompound) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word1_id", word1Id);
		tableRowParamMap.put("word2_id", word2Id);
		if (StringUtils.isNotBlank(etymTypeCode)) {
			tableRowParamMap.put("etymology_type_code", etymTypeCode);
		}
		if (CollectionUtils.isNotEmpty(comments)) {
			List<String> maskedComments = new ArrayList<>();
			for (String comment : comments) {
				String maskedComment = StringUtils.replace(comment, "\"", "\\\"");
				maskedComment = StringUtils.replace(maskedComment, "'", "\\'");
				maskedComments.add(maskedComment);
			}
			String[] maskedCommentsArr = maskedComments.toArray(new String[0]);
			tableRowParamMap.put("comments", new PgVarcharArray(maskedCommentsArr));
		}
		tableRowParamMap.put("is_questionable", isQuestionable);
		tableRowParamMap.put("is_compound", isCompound);
		Long wordEtymId = basicDbService.create(WORD_ETYMOLOGY, tableRowParamMap);
		return wordEtymId;
	}

	private Long createWordParadigmForm(String word, String morphCode, int homonymNr, String lang) throws Exception {

		Map<String, Object> tableRowParamMap;

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		Long wordId = basicDbService.create(WORD, tableRowParamMap);

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		Long paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("paradigm_id", paradigmId);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("value", word);
		tableRowParamMap.put("is_word", Boolean.TRUE);
		basicDbService.create(FORM, tableRowParamMap);

		createLifecycleLog(LifecycleLogOwner.WORD, wordId, LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, word);
		return wordId;
	}

	private Long createLexeme(Long wordId, Long meaningId) throws Exception {
		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", wordId);
		criteriaParamMap.put("meaning_id", meaningId);
		criteriaParamMap.put("dataset_code", getDataset());
		Long lexemeId = basicDbService.create(LEXEME, criteriaParamMap);
		return lexemeId;
	}

	private void appendToReport(boolean doReports, String reportName, Object ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}
}
