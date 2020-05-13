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
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.ekilex.data.transform.Lexeme;
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
	private final String etymRelExp = "s:etgg[count(s:ex)>0]";
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

	@Override
	public Complexity getLexemeComplexity() {
		return Complexity.DEFAULT;
	}

	@Override
	public Complexity getDefinitionComplexity() {
		return Complexity.DEFAULT;
	}

	@Override
	public Complexity getFreeformComplexity() {
		return Complexity.DEFAULT;
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

		Count etymGroupCount = new Count();
		Count etymRecordCount = new Count();
		Count etymRelRecordCount = new Count();
		Count createdWordCount = new Count();
		Count etymSourceLinkCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Node articleNode : articleNodes) {

			Element etymNode = (Element) articleNode.selectSingleNode(etymExp);
			if (etymNode != null) {
				Element guidNode = (Element) articleNode.selectSingleNode(guidExp);
				String guid = guidNode.getTextTrim();
				List<Node> wordNodes = articleNode.selectNodes(headWordExp);
				List<String> headwords = wordNodes.stream()
						.map(wordNode -> {
							String rawWord = ((Element) wordNode).getTextTrim();
							String word = cleanUp(rawWord);
							word = StringUtils.removeEnd(word, UNIFIED_AFIXOID_SYMBOL);
							word = StringUtils.removeStart(word, UNIFIED_AFIXOID_SYMBOL);
							return word;
						})
						.distinct()
						.collect(Collectors.toList());

				List<Long> headwordIds = new ArrayList<>();
				Map<String, Long> headwordIdsMap = new HashMap<>();
				for (String headword : headwords) {
					List<Map<String, Object>> wordDatas = getWord(headword, guid, ssDataset);
					Map<String, Object> wordData = wordDatas.get(0);
					Long wordId = (Long) wordData.get("id");
					headwordIds.add(wordId);
					headwordIdsMap.put(headword, wordId);
				}

				Element etymCommentNode = (Element) etymNode.selectSingleNode(etymRootCommentExp);
				String etymComment = null;
				String etymCommentPrese = null;
				if (etymCommentNode != null) {
					String origText = etymCommentNode.getTextTrim();
					etymComment = removeEkiEntityMarkup(origText);
					etymCommentPrese = convertEkiEntityMarkup(origText);
				}

				List<SourceLink> documentSourceLinks = new ArrayList<>();
				List<SourceLink> personSourceLinks = new ArrayList<>();
				String etymYear = null;

				List<Node> headwordEtymGroupNodes = etymNode.selectNodes(headwordEtymGroupExp);
				if (CollectionUtils.isNotEmpty(headwordEtymGroupNodes)) {
					if (CollectionUtils.size(headwordEtymGroupNodes) > 1) {
						logger.debug("Too many etym groups per headword \"{}\"", headwords);
					}
					Element headwordEtymGroupElement = (Element) headwordEtymGroupNodes.get(0);
					Element headwordEtymSourceElement = (Element) headwordEtymGroupElement.selectSingleNode(etymSourceGroupExp);
					Element headwordEtymSourceDocumentElement = (Element) headwordEtymSourceElement.selectSingleNode(etymSourceDocumentExp);
					if (headwordEtymSourceDocumentElement != null) {
						String document = headwordEtymSourceDocumentElement.getTextTrim();
						SourceLink sourceLink = getOrCreateSource(document, SourceType.DOCUMENT);
						documentSourceLinks.add(sourceLink);
					}
					List<Node> headwordEtymSourceAuthorNodes = headwordEtymSourceElement.selectNodes(etymSourceAuthorExp);
					if (CollectionUtils.isNotEmpty(headwordEtymSourceAuthorNodes)) {
						for (Node headwordEtymSourceAuthorNode : headwordEtymSourceAuthorNodes) {
							Element headwordEtymSourceAuthorElement = (Element) headwordEtymSourceAuthorNode;
							String author = headwordEtymSourceAuthorElement.getTextTrim();
							SourceLink sourceLink = getOrCreateSource(author, SourceType.PERSON);
							personSourceLinks.add(sourceLink);
						}
					}
					Element headwordEtymYearElement = (Element) headwordEtymSourceElement.selectSingleNode(etymYearExp);
					if (headwordEtymYearElement != null) {
						etymYear = headwordEtymYearElement.getTextTrim();
					}
				}

				//TODO nobody needs this?
				List<Node> etymPrivateNoteNodes = etymNode.selectNodes(etymRootPrivateNoteExp);
				List<Node> etymGroupNodes = etymNode.selectNodes(etymGroupExp);

				WordEtym wordEtym;
				List<WordEtym> wordEtyms1 = null;
				List<WordEtym> wordEtyms2 = new ArrayList<>();

				WordEtymPeekAhead wordEtymPeekAhead = getWordEtymPeekAhead(etymGroupNodes, 0);
				for (Long headwordId : headwordIds) {

					wordEtym = new WordEtym();
					wordEtym.setWordId(headwordId);
					wordEtym.setEtymTypeCode(wordEtymPeekAhead.getEtymTypeCode());
					wordEtym.setQuestionable(wordEtymPeekAhead.isQuestionable());
					wordEtym.setEtymYear(etymYear);
					wordEtym.setComment(etymComment);
					wordEtym.setCommentPrese(etymCommentPrese);
					Long wordEtymId = createWordEtym(wordEtym);
					etymRecordCount.increment();
					wordEtyms2.add(wordEtym);

					if (CollectionUtils.isNotEmpty(documentSourceLinks)) {
						for (SourceLink sourceLink : documentSourceLinks) {
							createWordEtymSourceLink(wordEtymId, ReferenceType.ANY, sourceLink.getSourceId(), null, sourceLink.getValue());
							etymSourceLinkCount.increment();
						}
					}
					if (CollectionUtils.isNotEmpty(personSourceLinks)) {
						for (SourceLink sourceLink : personSourceLinks) {
							createWordEtymSourceLink(wordEtymId, ReferenceType.AUTHOR, sourceLink.getSourceId(), null, sourceLink.getValue());
							etymSourceLinkCount.increment();
						}
					}
				}

				String mappedRegisterCode = null;
				for (int etymGroupNodeIndex = 0; etymGroupNodeIndex < etymGroupNodes.size(); etymGroupNodeIndex++) {//etg

					etymGroupCount.increment();
					Element etymGroupElement = (Element) etymGroupNodes.get(etymGroupNodeIndex);
					String etymAlternative = etymGroupElement.attributeValue(etymAlternativeAttr);
					boolean isEtymAlternative = StringUtils.isNotBlank(etymAlternative);

					if (isEtymAlternative && (etymGroupNodeIndex > 0)) {
						List<Long> wordEtym1WordIds = wordEtyms1.stream().map(WordEtym::getWordId).collect(Collectors.toList());
						wordEtyms1 = new ArrayList<>();
						for (Long wordEtym1wordId : wordEtym1WordIds) {
							wordEtym = new WordEtym();
							wordEtym.setWordId(wordEtym1wordId);
							createWordEtym(wordEtym);
							etymRecordCount.increment();
							wordEtyms1.add(wordEtym);
						}
					} else {
						wordEtyms1 = new ArrayList<>(wordEtyms2);
					}
					wordEtyms2 = new ArrayList<>();

					// etym relations
					List<Node> etymWordsWrapupNodes = etymGroupElement.selectNodes(etymWordWrapupExp);
					List<String> etymWordsWrapup = etymWordsWrapupNodes.stream().map(node -> ((Element) node).getTextTrim()).collect(Collectors.toList());
					wordEtymPeekAhead = getWordEtymPeekAhead(etymGroupNodes, etymGroupNodeIndex + 1);

					List<Node> etymRelNodes = etymGroupElement.selectNodes(etymRelExp);
					for (Node etymRelNode : etymRelNodes) {//etgg

						Element etymRelElement = (Element) etymRelNode;
						String etymRelQuestionable = etymRelElement.attributeValue(etymQuestionableAttr);
						boolean isEtymRelQuestionable = StringUtils.isNotBlank(etymRelQuestionable);
						String etymRelWordCompound = etymRelElement.attributeValue(etymWordCompoundAttr);
						boolean isEtymRelWordCompound = StringUtils.isNotBlank(etymRelWordCompound);
						List<Node> etymWordNodes = etymRelNode.selectNodes(etymWordExp);
						List<Node> etymLangNodes = etymRelNode.selectNodes(etymLangExp);
						if (CollectionUtils.isEmpty(etymLangNodes)) {
							logger.debug("Missing lang for \"{}\" - \"{}\"", headwords, etymWordsWrapup);
							appendToReport(REPORT_MISSING_ETYM_LANG, "Keeleelement puudub", headwords, etymWordsWrapup, "-");
						}
						Element etymRelCommentNode = (Element) etymRelNode.selectSingleNode(etymCommentExp);
						String etymRelComment = null;
						String etymRelCommentPrese = null;
						if (etymRelCommentNode != null) {
							String origText = etymRelCommentNode.getTextTrim();
							etymRelComment = removeEkiEntityMarkup(origText);
							etymRelCommentPrese = convertEkiEntityMarkup(origText);
						}
						Node etymSourceGroupNode = etymRelNode.selectSingleNode(etymSourceGroupExp);
						etymYear = null;
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
									SourceLink sourceLink = getOrCreateSource(author, SourceType.PERSON);
									sourceLinks.add(sourceLink);
								}
							}
						}
						List<Node> etymRegisterNodes = etymRelNode.selectNodes(etymRegisterExp);
						List<String> etymRegisterCodes = new ArrayList<>();
						for (Node etymRegisterNode : etymRegisterNodes) {
							String etymRegister = ((Element) etymRegisterNode).getTextTrim();
							mappedRegisterCode = registerConversionMap.get(etymRegister);
							if (mappedRegisterCode == null) {
								logger.debug("Unknown register \"{}\"", etymRegister);
								appendToReport(REPORT_UNKNOWN_REGISTER, headwords, etymWordsWrapup, etymRegister);
								continue;
							}
							etymRegisterCodes.add(mappedRegisterCode);
						}

						Long meaningId = createMeaning();
						// foreign etym words
						for (Node etymWordNode : etymWordNodes) {

							Element etymWordElement = (Element) etymWordNode;
							String wordStr = etymWordElement.getTextTrim();
							List<String> words = new ArrayList<>();
							String wordVer;
							if (StringUtils.contains(wordStr, '(')) {
								wordVer = RegExUtils.removePattern(wordStr, "[(].*?[)]");
								wordVer = cleanUp(wordVer);
								words.add(wordVer);
							}
							wordVer = cleanUp(wordStr);
							words.add(wordVer); 

							for (String word : words) {

								for (Node etymLangNode : etymLangNodes) {
	
									Element etymLangElement = (Element) etymLangNode;
									String etymLang = etymLangElement.getTextTrim();
									String mappedEtymLangCode = languageConversionMap.get(etymLang);
									if (StringUtils.isBlank(mappedEtymLangCode)) {
										logger.debug("Unknown lang for \"{}\" - \"{}\" - \"{}\"", headwords, word, etymLang);
										appendToReport(REPORT_MISSING_ETYM_LANG, "Tundmatu keelekood", headwords, word, etymLang);
									}
									int homonymNr = getWordMaxHomonymNr(word, mappedEtymLangCode);
									homonymNr++;
									Long word2Id = createWordParadigmForm(word, DEFAULT_WORD_MORPH_CODE, homonymNr, mappedEtymLangCode);
									createdWordCount.increment();
									Long lexemeId = createLexeme(word2Id, meaningId);
									for (String etymRegisterCode : etymRegisterCodes) {
										createLexemeRegister(lexemeId, etymRegisterCode);
									}
									wordEtym = new WordEtym();
									wordEtym.setWordId(word2Id);
									wordEtym.setEtymTypeCode(wordEtymPeekAhead.getEtymTypeCode());
									wordEtym.setQuestionable(wordEtymPeekAhead.isQuestionable());
									wordEtym.setEtymYear(etymYear);
									Long wordEtymId = createWordEtym(wordEtym);
									etymRecordCount.increment();
									wordEtyms2.add(wordEtym);
	
									for (SourceLink sourceLink : sourceLinks) {
										createWordEtymSourceLink(wordEtymId, ReferenceType.AUTHOR, sourceLink.getSourceId(), null, sourceLink.getValue());
										etymSourceLinkCount.increment();
									}
	
									for (WordEtym wordEtym1 : wordEtyms1) {
										WordEtymRel wordEtymRel = new WordEtymRel();
										wordEtymRel.setWordEtymId(wordEtym1.getId());
										wordEtymRel.setRelatedWordId(word2Id);
										wordEtymRel.setComment(etymRelComment);
										wordEtymRel.setCommentPrese(etymRelCommentPrese);
										wordEtymRel.setQuestionable(isEtymRelQuestionable);
										wordEtymRel.setCompound(isEtymRelWordCompound);
										createWordEtymRel(wordEtymRel);
										etymRelRecordCount.increment();
									}
								}
							}
						}
						// est etym words
						List<Node> etymEstWordNodes = etymRelNode.selectNodes(etymEstWordExp);
						if (CollectionUtils.isNotEmpty(etymEstWordNodes)) {
							for (Node etymEstWordNode : etymEstWordNodes) {
								Element etymEstWordElement = (Element) etymEstWordNode;
								String word = etymEstWordElement.getTextTrim();
								word = cleanUp(word);
								Long wordId = headwordIdsMap.get(word);
								if (wordId == null) {
									int homonymNr = getWordMaxHomonymNr(word, langEst);
									homonymNr++;
									wordId = createWordParadigmForm(word, DEFAULT_WORD_MORPH_CODE, homonymNr, langEst);
									createdWordCount.increment();
								}
								createLexeme(wordId, meaningId);
								/* 
								 * the field contains comma-separated list which should all be individual words
								 * 
								String wordValuesStr = etymEstWordElement.getTextTrim();
								String[] words = StringUtils.split(wordValuesStr, ',');
								for (String word : words) {
									word = StringUtils.trim(word);
									word = cleanUp(word);
									Long wordId = headwordIdsMap.get(word);
									if (wordId == null) {
										int homonymNr = getWordMaxHomonymNr(word, langEst);
										homonymNr++;
										wordId = createWordParadigmForm(word, DEFAULT_WORD_MORPH_CODE, homonymNr, langEst);
										createdWordCount.increment();
									}
									createLexeme(wordId, meaningId);
								}
								*/
							}
						}
					} //etgg
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

		logger.debug("{} word etym groups", etymGroupCount.getValue());
		logger.debug("{} word etym records", etymRecordCount.getValue());
		logger.debug("{} word etym relation records", etymRelRecordCount.getValue());
		logger.debug("{} new words created", createdWordCount.getValue());
		logger.debug("{} word etym source links", etymSourceLinkCount.getValue());

		end();
	}

	private String cleanUp(String value) {
		String cleanedWord = replaceChars(value, formStrCleanupChars, "");
		cleanedWord = removePattern(cleanedWord, "[&]\\w+[;]");
		cleanedWord = StringUtils.trim(cleanedWord);
		return cleanedWord;
	}

	private WordEtymPeekAhead getWordEtymPeekAhead(List<Node> etymGroupNodes, int etymGroupNodePeekIndex) {
		WordEtymPeekAhead wordEtymPeekAhead = new WordEtymPeekAhead();
		if (CollectionUtils.isEmpty(etymGroupNodes)) {
			return wordEtymPeekAhead;
		}
		int etymGroupNodeCount = etymGroupNodes.size();
		if (etymGroupNodePeekIndex < etymGroupNodeCount) {
			Element nextEtymGroupElement = (Element) etymGroupNodes.get(etymGroupNodePeekIndex);
			Element etymTypeNode = (Element) nextEtymGroupElement.selectSingleNode(etymTypeExp);
			String etymTypeCode = null;
			if (etymTypeNode != null) {
				String sourceEtymTypeCode = etymTypeNode.getTextTrim();
				etymTypeCode = etymTypes.get(sourceEtymTypeCode);
				if (etymTypeCode == null) {
					logger.debug("Unknown etym type \"{}\"", sourceEtymTypeCode);
				}
			}
			String etymQuestionable = nextEtymGroupElement.attributeValue(etymQuestionableAttr);
			boolean isEtymQuestionable = StringUtils.isNotBlank(etymQuestionable);
			wordEtymPeekAhead.setEtymTypeCode(etymTypeCode);
			wordEtymPeekAhead.setQuestionable(isEtymQuestionable);
		}
		return wordEtymPeekAhead;
	}

	private SourceLink getOrCreateSource(String sourceName, SourceType sourceType) throws Exception {
		Long sourceId = getSource(sourceType, EXT_SOURCE_ID_NA, sourceName, getDataset());
		if (sourceId == null) {
			sourceId = createSource(sourceType, EXT_SOURCE_ID_NA, sourceName);
		}
		SourceLink sourceLink = new SourceLink(sourceId, sourceName);
		return sourceLink;
	}

	private Long createWordEtym(WordEtym wordEtym) throws Exception {

		Long wordId = wordEtym.getWordId();
		String etymTypeCode = wordEtym.getEtymTypeCode();
		String etymYear = wordEtym.getEtymYear();
		String comment = wordEtym.getComment();
		String commentPrese = wordEtym.getCommentPrese();
		boolean isQuestionable = wordEtym.isQuestionable();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		if (StringUtils.isNotBlank(etymTypeCode)) {
			tableRowParamMap.put("etymology_type_code", etymTypeCode);
		}
		if (StringUtils.isNotBlank(etymYear)) {
			tableRowParamMap.put("etymology_year", etymYear);
		}
		if (StringUtils.isNotBlank(comment)) {
			tableRowParamMap.put("comment", comment);
		}
		if (StringUtils.isNotBlank(commentPrese)) {
			tableRowParamMap.put("comment_prese", commentPrese);
		}
		tableRowParamMap.put("is_questionable", isQuestionable);
		Long wordEtymId = basicDbService.create(WORD_ETYMOLOGY, tableRowParamMap);
		wordEtym.setId(wordEtymId);
		return wordEtymId;
	}

	private void createWordEtymRel(WordEtymRel wordEtymRel) throws Exception {

		Long wordEtymId = wordEtymRel.getWordEtymId();
		Long relatedWordId = wordEtymRel.getRelatedWordId();
		String comment = wordEtymRel.getComment();
		String commentPrese = wordEtymRel.getCommentPrese();
		boolean isQuestionable = wordEtymRel.isQuestionable();
		boolean isCompound = wordEtymRel.isCompound();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_etym_id", wordEtymId);
		tableRowParamMap.put("related_word_id", relatedWordId);
		if (StringUtils.isNotBlank(comment)) {
			tableRowParamMap.put("comment", comment);
		}
		if (StringUtils.isNotBlank(commentPrese)) {
			tableRowParamMap.put("comment_prese", commentPrese);
		}
		tableRowParamMap.put("is_questionable", isQuestionable);
		tableRowParamMap.put("is_compound", isCompound);
		basicDbService.create(WORD_ETYMOLOGY_RELATION, tableRowParamMap);
	}

	private Long createWordEtymSourceLink(Long wordEtymId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_etym_id", wordEtymId);
		tableRowParamMap.put("type", refType.name());
		tableRowParamMap.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long sourceLinkId = basicDbService.create(WORD_ETYMOLOGY_SOURCE_LINK, tableRowParamMap);
		return sourceLinkId;
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
		tableRowParamMap.put("mode", FormMode.WORD.name());
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("morph_exists", Boolean.TRUE);
		tableRowParamMap.put("value", word);
		tableRowParamMap.put("value_prese", word);//TODO decoration conversion
		basicDbService.create(FORM, tableRowParamMap);

		createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.CREATE, word);
		return wordId;
	}

	private Long createLexeme(Long wordId, Long meaningId) throws Exception {
		Lexeme lexeme = new Lexeme();
		lexeme.setWordId(wordId);
		lexeme.setMeaningId(meaningId);
		Long lexemeId = createLexeme(lexeme);
		return lexemeId;
	}

	private void appendToReport(String reportName, Object... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	class WordEtym extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long id;

		private Long wordId;

		private String etymTypeCode;

		private String etymYear;

		private String comment;

		private String commentPrese;

		private boolean questionable;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
		}

		public String getEtymTypeCode() {
			return etymTypeCode;
		}

		public void setEtymTypeCode(String etymTypeCode) {
			this.etymTypeCode = etymTypeCode;
		}

		public String getEtymYear() {
			return etymYear;
		}

		public void setEtymYear(String etymYear) {
			this.etymYear = etymYear;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getCommentPrese() {
			return commentPrese;
		}

		public void setCommentPrese(String commentPrese) {
			this.commentPrese = commentPrese;
		}

		public boolean isQuestionable() {
			return questionable;
		}

		public void setQuestionable(boolean questionable) {
			this.questionable = questionable;
		}
	}

	class WordEtymPeekAhead extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String etymTypeCode;

		private boolean questionable;

		public String getEtymTypeCode() {
			return etymTypeCode;
		}

		public void setEtymTypeCode(String etymTypeCode) {
			this.etymTypeCode = etymTypeCode;
		}

		public boolean isQuestionable() {
			return questionable;
		}

		public void setQuestionable(boolean questionable) {
			this.questionable = questionable;
		}
	}

	class WordEtymRel extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long wordEtymId;

		private Long relatedWordId;

		private String comment;

		private String commentPrese;

		private boolean questionable;

		private boolean compound;

		public Long getWordEtymId() {
			return wordEtymId;
		}

		public void setWordEtymId(Long wordEtymId) {
			this.wordEtymId = wordEtymId;
		}

		public Long getRelatedWordId() {
			return relatedWordId;
		}

		public void setRelatedWordId(Long relatedWordId) {
			this.relatedWordId = relatedWordId;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getCommentPrese() {
			return commentPrese;
		}

		public void setCommentPrese(String commentPrese) {
			this.commentPrese = commentPrese;
		}

		public boolean isQuestionable() {
			return questionable;
		}

		public void setQuestionable(boolean questionable) {
			this.questionable = questionable;
		}

		public boolean isCompound() {
			return compound;
		}

		public void setCompound(boolean compound) {
			this.compound = compound;
		}
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
