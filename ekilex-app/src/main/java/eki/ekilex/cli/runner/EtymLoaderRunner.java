package eki.ekilex.cli.runner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.SourceType;
import eki.common.data.Count;
import eki.common.data.KeyValuePair;
import eki.ekilex.constant.WordEtymGroupType;
import eki.ekilex.data.LanguageGroup;
import eki.ekilex.data.Note;
import eki.ekilex.data.Source;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.etym2.WordEtym;
import eki.ekilex.data.etym2.WordEtymGroup;
import eki.ekilex.data.migra.ValueMarkup;
import eki.ekilex.service.db.EtymDbService;
import eki.ekilex.service.db.SourceDbService;
import eki.ekilex.service.db.VariantDbService;

@Component
public class EtymLoaderRunner extends AbstractLanguageGroupLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EtymLoaderRunner.class);

	@Autowired
	private EtymDbService etymDbService;

	@Autowired
	private SourceDbService sourceDbService;

	@Autowired
	private VariantDbService variantDbService;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final String reportFileName = "report.txt";

	private boolean makeReport = false;

	@Override
	List<String> getRequiredFilenames() {
		List<String> requiredFilenames = Arrays.asList(
				CLASSIF_LANG_GROUP_SS1_FILENAME,
				CLASSIF_LANG_GROUP_VSL_FILENAME,
				CLASSIF_LANG_SS1_FILENAME,
				CLASSIF_LANG_VSL_FILENAME,
				SS1_FILENAME,
				VSL_FILENAME);
		return requiredFilenames;
	}

	@Transactional(rollbackFor = Exception.class)
	public void execute(String folderPath) throws Exception {

		logger.info("Starting loading...");

		validateFilesExist(folderPath);
		folderPath = cleanup(folderPath);

		FileOutputStream reportStream = null;
		OutputStreamWriter reportWriter = null;
		if (makeReport) {
			reportStream = new FileOutputStream(reportFileName);
			reportWriter = new OutputStreamWriter(reportStream, StandardCharsets.UTF_8);
		}

		List<LanguageGroup> languageGroups = getLanguageGroups();

		handleEtymSs1(folderPath, languageGroups, reportWriter);
		// TODO implement
		//handleEtymVsl(folderPath, reportWriter);

		if (makeReport) {
			reportWriter.flush();
			reportStream.flush();
			reportWriter.close();
			reportStream.close();
		}

		logger.info("End");
	}

	private void handleEtymSs1(String folderPath, List<LanguageGroup> languageGroups, OutputStreamWriter reportWriter) throws Exception {

		Count ignoredArticleCount = new Count();
		Count headwordEtymCount = new Count();
		Count etymWordEtymCount = new Count();
		Count wordEtymGroupCount = new Count();
		Count wordEtymGroupMemberCount = new Count();
		Count etymWordVariantCount = new Count();

		String dataXmlFilePath = folderPath + SS1_FILENAME;
		Document dataDoc = readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		Map<String, String> langCodeMap = collectLangCodeMapSs1(folderPath);
		Map<String, String> langGroupNameMap = collectLangGroupNameMapSs1(folderPath);

		List<Element> articleElements = rootElement.elements();

		int articleCounter = 0;
		int articleCount = articleElements.size();
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleElement : articleElements) {

			//totalArticleCount.increment();
			// headword
			Node mNode = articleElement.selectSingleNode("s:m");
			boolean ekilexIdExists = hasAttribute(mNode, "ekilex_id");

			if (!ekilexIdExists) {
				ignoredArticleCount.increment();
				// TODO report
				continue;
			}

			// etym root node
			Node etpNode = articleElement.selectSingleNode("s:etp");

			String ekilexIdStr = getAttributeValue(mNode, "ekilex_id");
			List<Long> headwordIds = toLongs(ekilexIdStr);

			for (Long headwordId : headwordIds) {
				handleArticleSs1(
						headwordId, mNode, etpNode, langCodeMap, langGroupNameMap,
						headwordEtymCount, etymWordEtymCount, wordEtymGroupCount, wordEtymGroupMemberCount, etymWordVariantCount,
						reportWriter);
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.info("{}% - {} articles processed", progressPercent, articleCounter);
			}
		}

		logger.info("Article count with unspecified headword id: {}", ignoredArticleCount.getValue());
		logger.info("Headword etym count: {}", headwordEtymCount.getValue());
		logger.info("Etym word etym count: {}", etymWordEtymCount.getValue());
		logger.info("Etym group count: {}", wordEtymGroupCount.getValue());
		logger.info("Etym group member count: {}", wordEtymGroupMemberCount.getValue());
		logger.info("Etym word variant count: {}", etymWordVariantCount.getValue());
		logger.info("Done loading \"{}\"", SS1_FILENAME);
	}

	public void handleArticleSs1(
			Long headwordId,
			Node mNode,
			Node etpNode,
			Map<String, String> langCodeMap,
			Map<String, String> langGroupNameMap,
			Count headwordEtymCount,
			Count etymWordEtymCount,
			Count wordEtymGroupCount,
			Count wordEtymGroupMemberCount,
			Count etymWordVariantCount,
			OutputStreamWriter reportWriter) throws Exception {

		WordEtym headwordEtym = new WordEtym();

		// headword value
		ValueMarkup headwordValueTuple = getNodeText(mNode);
		removeSymbol(headwordValueTuple, '+');

		// comment
		ValueMarkup commentValueTuple = getSingleNodeText(etpNode, "s:ek");

		// notes
		List<Node> mrkNodes = etpNode.selectNodes("s:mrk");

		// etp ettg - first level ettg root
		Node etpEttgNode = etpNode.selectSingleNode("s:ettg");

		ValueMarkup sourceNameTuple = null;
		List<ValueMarkup> authorNameTuples = null;

		if (etpEttgNode != null) {

			// sources root
			Node autgNode = etpEttgNode.selectSingleNode("s:autg");
			if (autgNode != null) {

				sourceNameTuple = getSingleNodeText(autgNode, "s:all");
				authorNameTuples = getNodesTexts(autgNode, "s:aut");

				String headwordEtymYearStr = getTextValue(getSingleNodeText(autgNode, "s:a"));
				headwordEtym.setEtymologyYear(headwordEtymYearStr);
			}
		}

		// -- headword etym --

		Long headwordEtymId = etymDbService.createWordEtym(headwordId, headwordEtym);
		headwordEtymCount.increment();
		WordEtymGroup headwordEtymGroup = new WordEtymGroup();
		headwordEtymGroup.setGroupType(WordEtymGroupType.ROOT);
		headwordEtymGroup.setQuestionable(false);
		Long headwordEtymGroupId = etymDbService.createWordEtymGroup(headwordEtymGroup);
		etymDbService.createWordEtymGroupMember(headwordEtymId, headwordEtymGroupId, false);

		if (commentValueTuple != null) {

			commentValueTuple.setAttrName("s:ek");
			createWordEtymComment(headwordEtymId, commentValueTuple);
		}

		if (CollectionUtils.isNotEmpty(mrkNodes)) {

			for (Node mrkNode : mrkNodes) {

				ValueMarkup noteValueTuple = getNodeText(mrkNode);
				String noteCreatedBy = getAttributeValue(mrkNode, "aT");
				String noteCreatedOnStr = getAttributeValue(mrkNode, "maeg");
				LocalDateTime noteCreatedOn = null;
				if (StringUtils.isNotBlank(noteCreatedOnStr)) {
					noteCreatedOn = LocalDateTime.parse(noteCreatedOnStr, dateTimeFormatter);
				}

				if (StringUtils.isBlank(noteValueTuple.getValue())) {
					continue;
				}

				Note headwordEtymNote = new Note();
				headwordEtymNote.setValue(noteValueTuple.getValue());
				headwordEtymNote.setValuePrese(noteValueTuple.getValuePrese());
				headwordEtymNote.setLang(LANGUAGE_CODE_EST);
				headwordEtymNote.setPublic(PUBLICITY_PUBLIC);
				headwordEtymNote.setCreatedBy(noteCreatedBy);
				headwordEtymNote.setCreatedOn(noteCreatedOn);
				headwordEtymNote.setModifiedBy(noteCreatedBy);
				headwordEtymNote.setModifiedOn(noteCreatedOn);

				etymDbService.createWordEtymNote(headwordEtymId, headwordEtymNote);
			}
		}

		if (sourceNameTuple != null) {

			SourceType sourceType = SourceType.DOCUMENT;
			handleSourceAndSourceLink(headwordEtymId, sourceNameTuple, sourceType);
		}

		if (CollectionUtils.isNotEmpty(authorNameTuples)) {

			for (ValueMarkup authorNameTuple : authorNameTuples) {

				SourceType sourceType = SourceType.PERSON;
				handleSourceAndSourceLink(headwordEtymId, authorNameTuple, sourceType);
			}
		}

		// -- word etym groups --

		List<Node> etgNodes = etpNode.selectNodes("s:etg");

		if (CollectionUtils.isNotEmpty(etgNodes)) {

			Long parentWordEtymGroupId = headwordEtymGroupId;
			Long wordEtymGroupId = null;

			for (int etgNodeIndex = 0; etgNodeIndex < etgNodes.size(); etgNodeIndex++) {

				Node etgNode = etgNodes.get(etgNodeIndex);
				boolean isEtymGroupQuestionable = hasAttributeValue(etgNode, "ky", "ky");
				boolean isAlternative = hasAttributeValue(etgNode, "alt", "v") && (etgNodeIndex > 0);
				ValueMarkup etymTypeCodeTuple = getSingleNodeText(etgNode, "s:epl");
				String etymTypeCode = getTextValue(etymTypeCodeTuple);

				List<String> origLanguageGroupValues = getTextValues(getNodesTexts(etgNode, "s:kr"));
				List<String> etymWordLangGroupNames = convertValues(origLanguageGroupValues, langGroupNameMap);

				if (isAlternative) {
					// the parent remains the same
				} else if (etgNodeIndex > 0) {
					parentWordEtymGroupId = wordEtymGroupId;
				}

				// -- word etym group --

				// TODO connect with lang group
				WordEtymGroup wordEtymGroup = new WordEtymGroup();
				wordEtymGroup.setGroupType(WordEtymGroupType.ABSTRACT);
				wordEtymGroup.setEtymologyTypeCode(etymTypeCode);
				wordEtymGroup.setQuestionable(isEtymGroupQuestionable);
				wordEtymGroupId = etymDbService.createWordEtymGroup(wordEtymGroup);
				wordEtymGroupCount.increment();
				etymDbService.createWordEtymGroupTree(parentWordEtymGroupId, wordEtymGroupId);

				// -- word etym group members --

				List<Node> etggNodes = etgNode.selectNodes("s:etgg");
				int etymGroupMemberCount = 0;
				boolean isEtymGroupCompound = false;

				if (CollectionUtils.isEmpty(etggNodes)) {

					// no group members

				} else {

					for (int etggNodeIndex = 0; etggNodeIndex < etggNodes.size(); etggNodeIndex++) {

						Node etggNode = etggNodes.get(etggNodeIndex);
						boolean isEtymGroupMemberQuestionable = hasAttributeValue(etggNode, "ky", "ky");
						boolean isEtymGroupMemberCompound = hasAttributeValue(etggNode, "etl", "lo");
						List<String> origLanguages = getTextValues(getNodesTexts(etggNode, "s:k"));
						List<String> etymWordLangCodes = convertValues(origLanguages, langCodeMap);
						List<ValueMarkup> etymWordAndVariantValueTuples = getNodesTexts(etggNode, "s:ex");
						List<String> etymWordRegisterCodes = getTextValues(getNodesTexts(etggNode, "s:s"));// TODO this thing here?
						List<ValueMarkup> etymWordEtymCommentTuples = getNodesTexts(etggNode, "s:ed");
						ValueMarkup etymWordAnotherEtymCommentValueTuple = null;
						List<ValueMarkup> etymWordEtymAuthorNameTuples = null;

						if (isEtymGroupMemberCompound) {
							isEtymGroupCompound = true;
						}

						Node dtxNode = etggNode.selectSingleNode("s:dtx");
						if (dtxNode != null) {
							String origName = "s:dtx";
							boolean isSlg = hasAttribute(dtxNode, "slg");
							if (isSlg) {
								origName = origName + "@s:slg";
							}
							etymWordAnotherEtymCommentValueTuple = getNodeText(dtxNode);
							etymWordAnotherEtymCommentValueTuple.setAttrName(origName);
						}

						String etymWordEtymYearStr = null;
						Node autgNode = etggNode.selectSingleNode("s:autg");
						if (autgNode != null) {
							etymWordEtymAuthorNameTuples = getNodesTexts(autgNode, "s:aut");
							etymWordEtymYearStr = getTextValue(getSingleNodeText(autgNode, "s:a"));
						}

						ValueMarkup etymWordValueTuple = etymWordAndVariantValueTuples.get(0);

						List<ValueMarkup> etymWordVariantValueTuples = null;
						if (etymWordAndVariantValueTuples.size() > 1) {
							etymWordVariantValueTuples = etymWordAndVariantValueTuples.subList(1, etymWordAndVariantValueTuples.size());
						}

						// -- word etym group word etyms --

						for (String etymWordLangCode : etymWordLangCodes) {

							WordLexemeMeaningIdTuple etymWordLexemeMeaningId = getOrCreateWordLexemeMeaning(etymWordValueTuple, etymWordLangCode);
							Long etymWordId = etymWordLexemeMeaningId.getWordId();
							Long etymWordLexemeId = etymWordLexemeMeaningId.getLexemeId();
							Long etymWordMeaningId = etymWordLexemeMeaningId.getMeaningId();

							WordEtym etymWordEtym = new WordEtym();
							etymWordEtym.setWordId(etymWordId);
							//etymWordEtym.setEtymologyTypeCode(??);
							etymWordEtym.setEtymologyYear(etymWordEtymYearStr);

							// -- etym word etym --

							Long etymWordEtymId = etymDbService.createWordEtym(etymWordId, etymWordEtym);
							etymWordEtymCount.increment();
							etymDbService.createWordEtymGroupMember(etymWordEtymId, wordEtymGroupId, isEtymGroupMemberQuestionable);
							wordEtymGroupMemberCount.increment();
							etymGroupMemberCount++;

							if (CollectionUtils.isNotEmpty(etymWordEtymCommentTuples)) {
								for (ValueMarkup etymWordEtymCommentTuple : etymWordEtymCommentTuples) {
									etymWordEtymCommentTuple.setAttrName("s:ed");
									createWordEtymComment(etymWordEtymId, etymWordEtymCommentTuple);
								}
							}

							if (etymWordAnotherEtymCommentValueTuple != null) {
								createWordEtymComment(etymWordEtymId, etymWordAnotherEtymCommentValueTuple);
							}

							if (CollectionUtils.isNotEmpty(etymWordEtymAuthorNameTuples)) {
								SourceType sourceType = SourceType.PERSON;
								for (ValueMarkup etymWordEtymAuthorNameTuple : etymWordEtymAuthorNameTuples) {
									handleSourceAndSourceLink(etymWordEtymId, etymWordEtymAuthorNameTuple, sourceType);
								}
							}

							if (CollectionUtils.isNotEmpty(etymWordVariantValueTuples)) {

								for (ValueMarkup etymWordVariantValueTuple : etymWordVariantValueTuples) {

									// -- etym word variant --

									WordLexemeMeaningIdTuple etymWordVariantLexemeMeaningId = getOrCreateWordLexeme(etymWordVariantValueTuple, etymWordLangCode, etymWordMeaningId);
									Long etymWordVariantLexemeId = etymWordVariantLexemeMeaningId.getLexemeId();
									variantDbService.createLexemeVariant(etymWordLexemeId, etymWordVariantLexemeId, null);
									etymWordVariantCount.increment();
								}
							}
						}
					}
				}

				WordEtymGroupType deductedWordEtymGroupType = null;
				if (etymGroupMemberCount == 1) {
					deductedWordEtymGroupType = WordEtymGroupType.SINGLE_MEMBER;
				} else if (isEtymGroupCompound) {
					deductedWordEtymGroupType = WordEtymGroupType.COMPOUND;
				} else if (CollectionUtils.isEmpty(etymWordLangGroupNames) && (etymGroupMemberCount > 1)) {
					deductedWordEtymGroupType = WordEtymGroupType.ALTERNATIVE;
				} else if (CollectionUtils.isNotEmpty(etymWordLangGroupNames)) {
					// TODO incorrect, needs to be correlated per group
					deductedWordEtymGroupType = WordEtymGroupType.LANGUAGE_GROUP;
				}
				if (deductedWordEtymGroupType != null) {
					etymDbService.updateWordEtymGroup(wordEtymGroupId, deductedWordEtymGroupType);
				}
			}
		}
	}

	private void createWordEtymComment(Long wordEtymId, ValueMarkup valueTuple) {

		String commentValue = valueTuple.getValue();
		String commentValuePrese = valueTuple.getValuePrese();
		String commentOrigName = valueTuple.getAttrName();

		etymDbService.createWordEtymComment(wordEtymId, commentValue, commentValuePrese, commentOrigName);
	}

	private void handleSourceAndSourceLink(Long wordEtymId, ValueMarkup sourceNameTuple, SourceType sourceType) {

		String sourceName = sourceNameTuple.getValue();
		String sourceValue = sourceNameTuple.getValue();
		String sourceValuePrese = sourceNameTuple.getValuePrese();

		Long sourceId = migrationDbService.getSourceId(sourceName, DATASET_ETY);

		if (sourceId == null) {

			Source source = new Source();
			source.setDatasetCode(DATASET_ETY);
			source.setType(sourceType);
			source.setName(sourceName);
			source.setValue(sourceValue);
			source.setValuePrese(sourceValuePrese);
			source.setComment("Fail: ss1");
			source.setPublic(PUBLICITY_PUBLIC);

			sourceId = sourceDbService.createSource(source);
		}

		etymDbService.createWordEtymSourceLink(wordEtymId, sourceId, sourceName);
	}

	private WordLexemeMeaningIdTuple getOrCreateWordLexemeMeaning(ValueMarkup wordValueTuple, String langCode) throws Exception {

		String wordValue = wordValueTuple.getValue();
		String wordValuePrese = wordValueTuple.getValuePrese();
		String asWordValue = textDecorationService.removeAccents(wordValue);

		WordLexemeMeaningIdTuple wordLexemeMeaningId;
		Long wordId = migrationDbService.getWordId(wordValue, asWordValue, langCode, DATASET_ETY);
		if (wordId == null) {
			wordLexemeMeaningId = migrationDbService.createWordAndLexemeAndMeaning(
					wordValue, wordValuePrese, asWordValue, langCode, DATASET_ETY, PUBLICITY_PUBLIC);
		} else {
			wordLexemeMeaningId = migrationDbService.createLexemeAndMeaning(wordId, DATASET_ETY, PUBLICITY_PUBLIC);
		}
		return wordLexemeMeaningId;
	}

	private WordLexemeMeaningIdTuple getOrCreateWordLexeme(ValueMarkup wordValueTuple, String langCode, Long meaningId) throws Exception {

		String wordValue = wordValueTuple.getValue();
		String wordValuePrese = wordValueTuple.getValuePrese();
		String asWordValue = textDecorationService.removeAccents(wordValue);

		WordLexemeMeaningIdTuple wordLexemeMeaningId;
		Long wordId = migrationDbService.getWordId(wordValue, asWordValue, langCode, DATASET_ETY);
		if (wordId == null) {
			wordLexemeMeaningId = migrationDbService.createWordAndLexeme(
					wordValue, wordValuePrese, asWordValue, langCode, DATASET_ETY, PUBLICITY_PUBLIC, meaningId);
		} else {
			wordLexemeMeaningId = migrationDbService.getLexemeId(wordId, meaningId, DATASET_ETY);
			if (wordLexemeMeaningId == null) {
				wordLexemeMeaningId = migrationDbService.createLexeme(wordId, meaningId, DATASET_ETY, PUBLICITY_PUBLIC);
			}
		}
		return wordLexemeMeaningId;
	}

	private List<String> convertValues(List<String> origValues, Map<String, String> valueMap) {

		if (CollectionUtils.isEmpty(origValues)) {
			return null;
		}

		List<String> mappedValues = new ArrayList<>();
		for (String origValue : origValues) {
			String mappedValue = valueMap.get(origValue);
			if (StringUtils.isBlank(mappedValue)) {
				String placeholderValue = "(" + origValue + ")?";
				mappedValues.add(placeholderValue);
			} else {
				mappedValues.add(mappedValue);
			}
		}
		return mappedValues;
	}

	private Map<String, String> collectLangCodeMapSs1(String folderPath) throws Exception {

		String langGroupSs1FilePath = folderPath + CLASSIF_LANG_SS1_FILENAME;
		List<String> langGroupSs1FileLines = readFileLines(langGroupSs1FilePath);
		langGroupSs1FileLines.remove(0);

		List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<>();

		for (String fileLine : langGroupSs1FileLines) {

			String[] fileLineCells = StringUtils.splitPreserveAllTokens(fileLine, CSV_SEPARATOR);
			String ekilexLanguageCode = StringUtils.trim(fileLineCells[1]);
			String ss1LanguageCode = StringUtils.trim(fileLineCells[0]);
			KeyValuePair<String, String> valueMapping = new KeyValuePair<String, String>(ss1LanguageCode, ekilexLanguageCode);
			keyValuePairs.add(valueMapping);
		}

		return toMap(keyValuePairs);
	}

	private Map<String, String> collectLangGroupNameMapSs1(String folderPath) throws Exception {

		String langGroupSs1FilePath = folderPath + CLASSIF_LANG_GROUP_SS1_FILENAME;
		List<String> langGroupSs1FileLines = readFileLines(langGroupSs1FilePath);
		langGroupSs1FileLines.remove(0);

		List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<>();

		for (String fileLine : langGroupSs1FileLines) {

			String[] fileLineCells = StringUtils.splitPreserveAllTokens(fileLine, CSV_SEPARATOR);
			String ekilexLanguageGroupName = StringUtils.trim(fileLineCells[2]);
			String ss1LanguageGroupName = StringUtils.trim(fileLineCells[3]);
			KeyValuePair<String, String> valueMapping = new KeyValuePair<String, String>(ss1LanguageGroupName, ekilexLanguageGroupName);
			keyValuePairs.add(valueMapping);
		}

		return toMap(keyValuePairs);
	}

	private Map<String, String> toMap(List<KeyValuePair<String, String>> keyValuePairs) {

		Map<String, String> valueMap = keyValuePairs.stream()
				.distinct()
				.collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue));

		return valueMap;
	}

	private List<Long> toLongs(String valuesStr) {

		if (StringUtils.isBlank(valuesStr)) {
			return null;
		}
		String[] valuesArr = StringUtils.split(valuesStr, ',');
		List<Long> values = Arrays.stream(valuesArr)
				.map(value -> StringUtils.trim(value))
				.filter(value -> StringUtils.isNotBlank(value))
				.map(value -> Long.valueOf(value))
				.collect(Collectors.toList());
		return values;
	}

	private String getAttributeValue(Node node, String attrName) {

		Element element = (Element) node;
		String origAttrValue = element.attributeValue(attrName);
		if (StringUtils.isBlank(origAttrValue)) {
			return null;
		}
		String cleanAttrValue = valueUtil.trimAndClean(origAttrValue);
		return cleanAttrValue;
	}

	private boolean hasAttribute(Node node, String attrName) {

		String attrValue = getAttributeValue(node, attrName);
		return StringUtils.isNotBlank(attrValue);
	}

	private boolean hasAttributeValue(Node node, String attrName, String attrValue) {

		String existingAttrValue = getAttributeValue(node, attrName);
		return StringUtils.equalsIgnoreCase(existingAttrValue, attrValue);
	}

	private ValueMarkup getNodeText(Node node) {

		if (node == null) {
			return null;
		}
		String origValue = valueUtil.trimAndClean(node.getText());
		String value = textDecorationService.removeEkiEntityMarkup(origValue);
		String valuePrese = textDecorationService.convertEkiEntityMarkup(origValue);
		return new ValueMarkup(value, valuePrese);
	}

	private ValueMarkup getSingleNodeText(Node parent, String nodeName) {

		Node node = parent.selectSingleNode(nodeName);
		if (node == null) {
			return null;
		}
		return getNodeText(node);
	}

	private List<ValueMarkup> getNodesTexts(Node parent, String nodeName) {

		List<Node> nodes = parent.selectNodes(nodeName);
		if (CollectionUtils.isEmpty(nodes)) {
			return null;
		}
		List<ValueMarkup> values = new ArrayList<>();
		for (Node node : nodes) {
			ValueMarkup nodeText = getNodeText(node);
			if (nodeText != null) {
				values.add(nodeText);
			}
		}
		return values;
	}

	private void removeSymbol(ValueMarkup valueMarkup, char sym) {

		String value = valueMarkup.getValue();
		String valuePrese = valueMarkup.getValuePrese();
		value = StringUtils.remove(value, sym);
		valuePrese = StringUtils.remove(valuePrese, sym);
		valueMarkup.setValue(value);
		valueMarkup.setValuePrese(valuePrese);
	}

	private String getTextValue(ValueMarkup valueMarkup) {

		if (valueMarkup == null) {
			return null;
		}
		return valueMarkup.getValue();
	}

	private List<String> getTextValues(List<ValueMarkup> valueMarkups) {

		if (CollectionUtils.isEmpty(valueMarkups)) {
			return null;
		}
		List<String> textValues = valueMarkups.stream()
				.map(ValueMarkup::getValue)
				.collect(Collectors.toList());
		return textValues;
	}
}
