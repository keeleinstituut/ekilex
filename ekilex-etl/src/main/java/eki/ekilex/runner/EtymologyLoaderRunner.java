package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RegExUtils.removePattern;
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
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FormMode;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.ekilex.service.ReportComposer;

@Component
public class EtymologyLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EtymologyLoaderRunner.class);

	private static final String REPORT_MISSING_ETYM_TYPE = "missing_etym_type";

	private static final String REPORT_MISSING_ETYM_LANG = "missing_etym_lang";

	private static final String REPORT_UNKNOWN_ETYM_TYPE = "unknown_etym_type";

	private static final String REPORT_UNKNOWN_REGISTER = "unknown_register";

	private final String formStrCleanupChars = "()¤:_|[]̄̆̇\"`´–+=";
	private final String etymExp = "s:S/s:etp";
	private final String guidExp = "s:G";
	private final String headWordExp = "s:P/s:mg/s:m";
	private final String etymRootCommentExp = "s:ek";
	private final String etymRootPrivateNoteExp = "s:mrk";
	private final String headwordEtymGroupExp = "s:ettg";
	private final String etymGroupExp = "s:etg";
	private final String etymWordWrapupExp = "s:etgg/s:ex";
	private final String etymTypeExp = "s:epl";
	private final String etymSubGroupExp = "s:etgg[count(s:ex)>0]";
	private final String etymLangExp = "s:k";
	private final String etymWordExp = "s:ex";
	private final String etymCommentExp = "s:dtx";
	private final String etymRegisterExp = "s:s";
	private final String etymEstWordExp = "s:ed";
	private final String etymSourceGroupExp = "s:autg";
	private final String etymSourceAuthorExp = "s:aut";
	private final String etymSourceDocumentExp = "s:all";
	private final String etymYearExp = "s:a";
	private final String etymQuestionableAttr = "ky";
	private final String etymAlternativeAttr = "alt";
	private final String etymWordCompoundAttr = "etl";
	private final String langEst = "est";

	private ReportComposer reportComposer;

	private Map<String, String> etymTypes;

	private Map<String, String> registerConversionMap;

	private Map<String, String> languageConversionMap;

	@Override
	public String getDataset() {
		return "ety";
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	public void initialise() throws Exception {
		etymTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_ETYMPLTYYP);
		registerConversionMap = loadClassifierMappingsFor(EKI_CLASSIFIER_STYYP, ClassifierName.REGISTER.name());
		languageConversionMap = loadClassifierMappingsFor(EKI_CLASSIIFER_ETYMKEELTYYP, ClassifierName.LANGUAGE.name());
	}

	@Transactional
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", REPORT_MISSING_ETYM_TYPE, REPORT_MISSING_ETYM_LANG, REPORT_UNKNOWN_ETYM_TYPE);
		}
		start();

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
		long articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		final String ssDataset = "ss1";

		List<Map<String, Object>> wordDatas;

		Count etymCount = new Count();
		Count etymWordSourceAuthorCount = new Count();
		Count etymWordSourceDocumentCount = new Count();
		Count missingEtymTypeCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Node articleNode : articleNodes) {

			Element etymNode = (Element) articleNode.selectSingleNode(etymExp);
			if (etymNode != null) {
				Element guidNode = (Element) articleNode.selectSingleNode(guidExp);
				String guid = guidNode.getTextTrim();
				List<Node> wordNodes = articleNode.selectNodes(headWordExp);
				List<String> words = wordNodes.stream()
						.map(wordNode -> {
							String rawWord = ((Element)wordNode).getTextTrim();
							String word = cleanUp(rawWord);
							return word;
						})
						.distinct()
						.collect(Collectors.toList());

				List<Long> headWordIds = new ArrayList<>();
				for (String word : words) {
					wordDatas = getWord(word, guid, ssDataset);
					for (Map<String, Object> wordData : wordDatas) {
						Long wordId = (Long) wordData.get("id");
						headWordIds.add(wordId);
					}
				}

				List<Node> etymCommentNodes = etymNode.selectNodes(etymRootCommentExp);
				List<String> etymRootComments = null;
				if (CollectionUtils.isNotEmpty(etymCommentNodes)) {
					etymRootComments = etymCommentNodes
							.stream()
							.map(node -> {
								String etymComment = ((Element) node).getTextTrim();
								etymComment = cleanEkiEntityMarkup(etymComment);
								return etymComment;
							}).collect(Collectors.toList());
				}

				List<Node> headwordEtymGroupNodes = etymNode.selectNodes(headwordEtymGroupExp);
				if (CollectionUtils.isNotEmpty(headwordEtymGroupNodes)) {
					if (CollectionUtils.size(headwordEtymGroupNodes) > 1) {
						logger.debug("Too many etym groups per headword \"{}\"", words);
					}
					Element headwordEtymGroupElement = (Element) headwordEtymGroupNodes.get(0);
					Element headwordEtymSourceElement = (Element) headwordEtymGroupElement.selectSingleNode(etymSourceGroupExp);
					Element headwordEtymSourceDocumentElement = (Element) headwordEtymSourceElement.selectSingleNode(etymSourceDocumentExp);
					if (headwordEtymSourceDocumentElement != null) {
						String document = headwordEtymSourceDocumentElement.getTextTrim();
						SourceLink sourceLink = getOrCreateSource(document, SourceType.DOCUMENT, etymWordSourceDocumentCount);
						for (Long headwordId : headWordIds) {
							createWordSourceLink(headwordId, ReferenceType.ANY, sourceLink.getSourceId(), null, sourceLink.getValue());
						}
					}
					List<Node> headwordEtymSourceAuthorNodes = headwordEtymSourceElement.selectNodes(etymSourceAuthorExp);
					if (CollectionUtils.isNotEmpty(headwordEtymSourceAuthorNodes)) {
						for (Node headwordEtymSourceAuthorNode : headwordEtymSourceAuthorNodes) {
							Element headwordEtymSourceAuthorElement = (Element) headwordEtymSourceAuthorNode;
							String author = headwordEtymSourceAuthorElement.getTextTrim();
							SourceLink sourceLink = getOrCreateSource(author, SourceType.PERSON, etymWordSourceAuthorCount);
							for (Long headwordId : headWordIds) {
								createWordSourceLink(headwordId, ReferenceType.AUTHOR, sourceLink.getSourceId(), null, sourceLink.getValue());
							}
						}
					}
					Element headwordEtymYearElement = (Element) headwordEtymSourceElement.selectSingleNode(etymYearExp);
					if (headwordEtymYearElement != null) {
						String etymYear = headwordEtymYearElement.getTextTrim();
						for (Long headwordId : headWordIds) {
							updateWordEtymYear(headwordId, etymYear);
						}
					}
				}
				List<Node> headwordEtymTypeNodes = etymNode.selectNodes(etymGroupExp + "/" + etymTypeExp);
				if (CollectionUtils.isEmpty(headwordEtymTypeNodes)) {
					missingEtymTypeCount.increment();
				} else {
					if (CollectionUtils.size(headwordEtymTypeNodes) > 1) {
						logger.debug("Many etym type references per headword \"{}\"", words);
					}
					Element headwordEtymTypeElement = (Element) headwordEtymTypeNodes.get(0);
					String etymTypeCode = headwordEtymTypeElement.getTextTrim();
					String mappedEtymTypeCode = etymTypes.get(etymTypeCode);
					if (mappedEtymTypeCode == null) {
						logger.debug("Unknown etym type \"{}\"", etymTypeCode);
					} else {
						for (Long headwordId : headWordIds) {
							updateWordEtymType(headwordId, mappedEtymTypeCode);
						}
					}
				}

				//TODO impl
				List<Node> etymPrivateNoteNodes = etymNode.selectNodes(etymRootPrivateNoteExp);
				List<Node> etymGroupNodes = etymNode.selectNodes(etymGroupExp);
				List<Long> word1Ids = null;
				List<Long> word2Ids = null;
				List<String> etymComments = null;
				String mappedRegisterCode = null;
				for (Node etymGroupNode : etymGroupNodes) {
					Element etymGroupElement = (Element) etymGroupNode;
					List<Node> etymWordsWrapupNodes = etymGroupNode.selectNodes(etymWordWrapupExp);
					List<String> etymWordsWrapup = etymWordsWrapupNodes.stream().map(node -> ((Element) node).getTextTrim()).collect(Collectors.toList());
					String etymQuestionable = etymGroupElement.attributeValue(etymQuestionableAttr);
					boolean isEtymQuestionable = StringUtils.isNotBlank(etymQuestionable);
					String etymAlternative = etymGroupElement.attributeValue(etymAlternativeAttr);
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
					List<Node> etymSubGroupNodes = etymGroupNode.selectNodes(etymSubGroupExp);
					for (Node etymSubGroupNode : etymSubGroupNodes) {
						Element etymSubGroupElement = (Element) etymSubGroupNode;
						etymQuestionable = etymSubGroupElement.attributeValue(etymQuestionableAttr);
						isEtymQuestionable = (isFirstEtym && isEtymQuestionable) || StringUtils.isNotBlank(etymQuestionable);
						String etymWordCompound = etymSubGroupElement.attributeValue(etymWordCompoundAttr);
						boolean isEtymWordCompound = StringUtils.isNotBlank(etymWordCompound);
						List<Node> etymWordNodes = etymSubGroupNode.selectNodes(etymWordExp);
						List<Node> etymLangNodes = etymSubGroupNode.selectNodes(etymLangExp);
						if (CollectionUtils.isEmpty(etymLangNodes)) {
							logger.debug("Missing lang for \"{}\" - \"{}\"", words, etymWordsWrapup);
							appendToReport(REPORT_MISSING_ETYM_LANG, words, etymWordsWrapup);
						}
						Element etymCommentNode = (Element) etymSubGroupNode.selectSingleNode(etymCommentExp);
						if (isFirstEtym && CollectionUtils.isNotEmpty(etymRootComments)) {
							etymComments = new ArrayList<>(etymRootComments);
						} else {
							etymComments = new ArrayList<>();
						}
						if (etymCommentNode != null) {
							String etymComment = etymCommentNode.getTextTrim();
							etymComment = cleanEkiEntityMarkup(etymComment);
							etymComments.add(etymComment);
						}
						Node etymSourceGroupNode = etymSubGroupNode.selectSingleNode(etymSourceGroupExp);
						String etymYear = null;
						List<SourceLink> sourceLinks = new ArrayList<>();
						if (etymSourceGroupNode != null) {
							// etym year
							Element etymYearNode = (Element) etymSourceGroupNode.selectSingleNode(etymYearExp);
							if (etymYearNode != null) {
								etymYear = etymYearNode.getTextTrim();
							}
							// etym source links
							List<Node> etymSourceAuthorNodes = etymSourceGroupNode.selectNodes(etymSourceAuthorExp);
							if (CollectionUtils.isNotEmpty(etymSourceAuthorNodes)) {
								for (Node etymSourceAuthorNode : etymSourceAuthorNodes) {
									Element etymSourceAuthorElement = (Element) etymSourceAuthorNode;
									String author = etymSourceAuthorElement.getTextTrim();
									SourceLink sourceLink = getOrCreateSource(author, SourceType.PERSON, etymWordSourceAuthorCount);
									sourceLinks.add(sourceLink);
								}
							}
						}
						List<Node> etymRegisterNodes = etymSubGroupNode.selectNodes(etymRegisterExp);
						List<String> etymRegisterCodes = new ArrayList<>();
						for (Node etymRegisterNode : etymRegisterNodes) {
							String etymRegister = ((Element)etymRegisterNode).getTextTrim();
							mappedRegisterCode = registerConversionMap.get(etymRegister);
							if (mappedRegisterCode == null) {
								logger.debug("Unknown register \"{}\"", etymRegister);
								appendToReport(REPORT_UNKNOWN_REGISTER, words, etymWordsWrapup, etymRegister);
								continue;
							}
							etymRegisterCodes.add(mappedRegisterCode);
						}
						Long meaningId = createMeaning();
						// foreign etym words
						for (Node etymWordNode : etymWordNodes) {
							Element etymWordElement = (Element) etymWordNode;
							String word = etymWordElement.getTextTrim();
							word = cleanUp(word);
							for (Node etymLangNode : etymLangNodes) {
								Element etymLangElement = (Element) etymLangNode;
								String etymLang = etymLangElement.getTextTrim();
								String mappedEtymLangCode = languageConversionMap.get(etymLang);
								int homonymNr = getWordMaxHomonymNr(word, mappedEtymLangCode);
								homonymNr++;
								Long word2Id = createWordParadigmForm(word, DEFAULT_WORD_MORPH_CODE, homonymNr, mappedEtymLangCode, etymYear);
								for (SourceLink sourceLink : sourceLinks) {
									createWordSourceLink(word2Id, ReferenceType.AUTHOR, sourceLink.getSourceId(), null, sourceLink.getValue());
								}
								Long lexemeId = createLexeme(word2Id, meaningId);
								for (String etymRegisterCode : etymRegisterCodes) {
									createLexemeRegister(lexemeId, etymRegisterCode);
								}
								word2Ids.add(word2Id);
								for (Long word1Id : word1Ids) {
									createWordEtymology(word1Id, word2Id, etymComments, isEtymQuestionable, isEtymWordCompound);
									etymCount.increment();
								}
							}
						}
						// est etym words
						List<Node> etymEstWordNodes = etymSubGroupNode.selectNodes(etymEstWordExp);
						if (CollectionUtils.isNotEmpty(etymEstWordNodes)) {
							for (Node etymEstWordNode : etymEstWordNodes) {
								Element etymEstWordElement = (Element) etymEstWordNode;
								String word = etymEstWordElement.getTextTrim();
								word = cleanUp(word);
								int homonymNr = getWordMaxHomonymNr(word, langEst);
								homonymNr++;
								Long wordId = createWordParadigmForm(word, DEFAULT_WORD_MORPH_CODE, homonymNr, langEst, null);
								createLexeme(wordId, meaningId);
							}
						}
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
		logger.debug("Found {} word source authors", etymWordSourceAuthorCount.getValue());
		logger.debug("Found {} word source documents", etymWordSourceDocumentCount.getValue());
		logger.debug("Found {} etym groups with missing type", missingEtymTypeCount.getValue());

		end();
	}

	private String cleanUp(String value) {
		String cleanedWord = replaceChars(value, formStrCleanupChars, "");
		return removePattern(cleanedWord, "[&]\\w+[;]");
	}

	private SourceLink getOrCreateSource(String sourceName, SourceType sourceType, Count sourceCount) throws Exception {
		Long sourceId = getSource(sourceType, EXT_SOURCE_ID_NA, sourceName);
		if (sourceId == null) {
			sourceId = createSource(sourceType, EXT_SOURCE_ID_NA, sourceName);
			sourceCount.increment();
		}
		SourceLink sourceLink = new SourceLink(sourceId, sourceName);
		return sourceLink;
	}

	private void updateWordEtymYear(Long wordId, String etymYear) {

		String sql = "update " + WORD + " set etymology_year = :etymYear where id = :wordId";
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordId", wordId);
		tableRowParamMap.put("etymYear", etymYear);
		basicDbService.executeScript(sql, tableRowParamMap);
	}

	private void updateWordEtymType(Long wordId, String etymTypeCode) {

		String sql = "update " + WORD + " set etymology_type_code = :etymTypeCode where id = :wordId";
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordId", wordId);
		tableRowParamMap.put("etymTypeCode", etymTypeCode);
		basicDbService.executeScript(sql, tableRowParamMap);
	}

	private Long createWordEtymology(Long word1Id, Long word2Id, List<String> comments, boolean isQuestionable, boolean isCompound) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word1_id", word1Id);
		tableRowParamMap.put("word2_id", word2Id);
		if (CollectionUtils.isNotEmpty(comments)) {
			tableRowParamMap.put("comments", new PgVarcharArray(comments));
		}
		tableRowParamMap.put("is_questionable", isQuestionable);
		tableRowParamMap.put("is_compound", isCompound);
		Long wordEtymId = basicDbService.create(WORD_ETYMOLOGY, tableRowParamMap);
		return wordEtymId;
	}

	private Long createWordParadigmForm(String word, String morphCode, int homonymNr, String lang, String etymYear) throws Exception {

		Map<String, Object> tableRowParamMap;

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		if (StringUtils.isNotBlank(etymYear)) {
			tableRowParamMap.put("etymology_year", etymYear);	
		}
		Long wordId = basicDbService.create(WORD, tableRowParamMap);

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		Long paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);

		tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("paradigm_id", paradigmId);
		tableRowParamMap.put("mode", FormMode.WORD.name());
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("morph_exists", Boolean.TRUE);
		tableRowParamMap.put("value", word);
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

	private Long createWordSourceLink(Long wordId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("type", refType.name());
		tableRowParamMap.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long sourceLinkId = basicDbService.create(WORD_SOURCE_LINK, tableRowParamMap);
		return sourceLinkId;
	}

	private void appendToReport(String reportName, Object ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	class SourceLink extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long sourceId;

		private String value;

		public SourceLink(Long sourceId, String value) {
			this.sourceId = sourceId;
			this.value = value;
		}

		public Long getSourceId() {
			return sourceId;
		}

		public String getValue() {
			return value;
		}

	}
}
