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

import eki.common.data.Count;
import eki.common.data.KeyValuePair;
import eki.ekilex.data.LanguageGroup;
import eki.ekilex.data.etym2.WordEtym;
import eki.ekilex.data.migra.ValueMarkup;
import eki.ekilex.service.db.EtymDbService;

@Component
public class EtymLoaderRunner extends AbstractLanguageGroupLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EtymLoaderRunner.class);

	@Autowired
	private EtymDbService etymDbService;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final String reportFileName = "report.txt";

	private boolean makeReport = true;

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

		logger.info("Done");
	}

	private void handleEtymSs1(String folderPath, List<LanguageGroup> languageGroups, OutputStreamWriter reportWriter) throws Exception {

		// keel
		//s:sr/s:A/s:S/s:etp/s:etg/s:etgg/s:k
		// keelerühm
		//s:sr/s:A/s:S/s:etp/s:etg/s:kr

		// vaste
		//s:sr/s:A/s:S/s:etp/s:etg/s:etgg/s:ex
		// tõlge?
		//s:sr/s:A/s:S/s:etp/s:etg/s:etgg/s:ed

		Count ignoredArticleCount = new Count();

		String dataXmlFilePath = folderPath + SS1_FILENAME;
		Document dataDoc = readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		Map<String, String> langCodeMap = collectLangCodeMapSs1(folderPath);
		Map<String, String> langGroupNameMap = collectLangGroupNameMapSs1(folderPath);

		List<Element> articleElements = rootElement.elements();

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
			List<Long> wordIds = toLongs(ekilexIdStr);

			//for (Long wordId : wordIds) {
			Long wordId = wordIds.get(0);
			handleArticle(wordId, mNode, etpNode, langCodeMap, langGroupNameMap, reportWriter);
			//}
		}
	}

	public void handleArticle(
			Long wordId,
			Node mNode,
			Node etpNode,
			Map<String, String> langCodeMap,
			Map<String, String> langGroupNameMap,
			OutputStreamWriter reportWriter) throws Exception {

		WordEtym headwordEtym = new WordEtym();

		// headword value
		ValueMarkup wordValue = getNodeText(mNode);
		removeSymbol(wordValue, '+');

		// comment
		ValueMarkup commentValue = getSingleNodeText(etpNode, "s:ek");

		// notes
		List<Node> mrkNodes = etpNode.selectNodes("s:mrk");
		if (CollectionUtils.isNotEmpty(mrkNodes)) {

			for (Node mrkNode : mrkNodes) {

				String createdBy = getAttributeValue(mrkNode, "aT");
				String createdOnStr = getAttributeValue(mrkNode, "maeg");
				LocalDateTime createdOn = null;
				if (StringUtils.isNotBlank(createdOnStr)) {
					dateTimeFormatter.parse(createdOnStr);
					createdOn = LocalDateTime.parse(createdOnStr, dateTimeFormatter);
				}
				// TODO create and set note
			}
		}

		// etp ettg - first level ettg root
		Node etpEttgNode = etpNode.selectSingleNode("s:ettg");

		if (etpEttgNode != null) {

			// sources root
			Node autgNode = etpEttgNode.selectSingleNode("s:autg");
			if (autgNode != null) {

				ValueMarkup sourceName = getSingleNodeText(autgNode, "s:all");
				if (sourceName != null) {
					// TODO create and set source
				}

				List<ValueMarkup> authorNames = getNodesTexts(autgNode, "s:aut");
				// TODO create and set source

				String yearStr = getTextValue(getSingleNodeText(autgNode, "s:a"));
				headwordEtym.setEtymologyYear(yearStr);
			}
		}

		List<Node> etgNodes = etpNode.selectNodes("s:etg");
		if (CollectionUtils.isNotEmpty(etgNodes)) {

			int level = 0;

			for (int etgNodeIndex = 0; etgNodeIndex < etgNodes.size(); etgNodeIndex++) {

				//s:etg - etümoloogilise pärinevuse info plokk ehk siit tekib märksõnale pärinevuse seosed.
				//Kui neid on ühe x:etp all mitu tükki, siis nendest kujuneb xml'is oleva järjestuse järgi pärinevuse hierarhia

				Node etgNode = etgNodes.get(etgNodeIndex);
				boolean isEtgQuestionable = hasAttributeValue(etgNode, "ky", "ky");
				boolean isAlternative = hasAttributeValue(etgNode, "alt", "v") && (etgNodeIndex > 0);

				if (!isAlternative) {
					level++;
				}

				ValueMarkup etymTypeCode = getSingleNodeText(etgNode, "s:epl");
				headwordEtym.setEtymologyTypeCode(getTextValue(etymTypeCode));

				List<String> origLanguageGroupValues = getTextValues(getNodesTexts(etgNode, "s:kr"));
				String matchWordLangGroupNamesStr;
				if (CollectionUtils.isEmpty(origLanguageGroupValues)) {
					matchWordLangGroupNamesStr = "-";
				} else {
					List<String> matchWordLangGroupNames = convertValues(origLanguageGroupValues, langGroupNameMap);
					matchWordLangGroupNamesStr = StringUtils.join(matchWordLangGroupNames, ", ");
				}

				List<Node> etggNodes = etgNode.selectNodes("s:etgg");

				for (int etggNodeIndex = 0; etggNodeIndex < etggNodes.size(); etggNodeIndex++) {

					List<String> logRow = new ArrayList<>();
					logRow.add(getTextValue(wordValue));
					logRow.add(wordId.toString());

					//s:ettg - päritolukeelendi etümoloogia infoplokk (seos).
					//Kui on mitu s:ettg ühe s:etg all, siis need on paralleelsed pärinevused ehk pärineb ühest VÕI teisest. Igaühe kohta oma grupp.

					Node etggNode = etggNodes.get(etggNodeIndex);
					boolean isEtggQuestionable = hasAttributeValue(etggNode, "ky", "ky");

					List<String> origLanguages = getTextValues(getNodesTexts(etggNode, "s:k"));
					List<String> matchWordLangCodes = convertValues(origLanguages, langCodeMap);
					List<ValueMarkup> matchWordAndVariantValues = getNodesTexts(etggNode, "s:ex");
					List<String> matchRegisterCodes = getTextValues(getNodesTexts(etggNode, "s:s"));
					List<ValueMarkup> matchComments = getNodesTexts(etggNode, "s:ed");
					if (CollectionUtils.isNotEmpty(matchComments)) {
						for (ValueMarkup matchComment : matchComments) {
							String origName = "s:ed";
							// TODO create and set comment
						}
					}
					Node dtxNode = etggNode.selectSingleNode("s:dtx");
					if (dtxNode != null) {
						String origName = "s:dtx";
						boolean isSlg = hasAttribute(dtxNode, "slg");
						if (isSlg) {
							origName = origName + "@s:slg";
						}
						ValueMarkup matchAnotherCommentValue = getNodeText(dtxNode);
						// TODO create and set another comment
					}

					// sources root
					Node autgNode = etggNode.selectSingleNode("s:autg");
					if (autgNode != null) {

						List<ValueMarkup> matchAuthorNames = getNodesTexts(autgNode, "s:aut");
						// TODO create and set source

						String matchYearStr = getTextValue(getSingleNodeText(autgNode, "s:a"));
						// TODO set
					}

					// -- conversions --

					logRow.add(String.valueOf(level));

					ValueMarkup matchWordValue = null;
					Long matchWordId = null;

					if (CollectionUtils.isNotEmpty(matchWordAndVariantValues)) {
						matchWordValue = matchWordAndVariantValues.get(0);
						String matchWordValueText = getTextValue(matchWordValue);
						String noAccentMatchWordValue = textDecorationService.removeAccents(matchWordValueText);
						matchWordId = migrationDbService.getWordId(matchWordValueText, noAccentMatchWordValue, matchWordLangCodes, DATASET_ETY);
					}

					if (matchWordValue == null) {
						logRow.add("-");
					} else {
						logRow.add(getTextValue(matchWordValue));
					}

					if (matchWordId == null) {
						logRow.add("-");
					} else {
						logRow.add(matchWordId.toString());
					}

					if (CollectionUtils.isEmpty(matchWordLangCodes)) {
						// never happens
						logRow.add("-");
					} else {
						String matchWordLangCodesStr = StringUtils.join(matchWordLangCodes, ", ");
						logRow.add(matchWordLangCodesStr);
					}

					if (StringUtils.isBlank(matchWordLangGroupNamesStr)) {
						logRow.add("-");
					} else {
						logRow.add(matchWordLangGroupNamesStr);
					}

					if (makeReport) {
						writeLogRow(reportWriter, logRow);
					}
				}
			}
		}
	}

	public List<String> convertValues(List<String> origValues, Map<String, String> valueMap) {

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

	public Map<String, String> toMap(List<KeyValuePair<String, String>> keyValuePairs) {

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

	public void removeSymbol(ValueMarkup valueMarkup, char sym) {

		String value = valueMarkup.getValue();
		String valuePrese = valueMarkup.getValuePrese();
		value = StringUtils.remove(value, sym);
		valuePrese = StringUtils.remove(valuePrese, sym);
		valueMarkup.setValue(value);
		valueMarkup.setValuePrese(valuePrese);
	}

	public String getTextValue(ValueMarkup valueMarkup) {

		if (valueMarkup == null) {
			return null;
		}
		return valueMarkup.getValue();
	}

	public List<String> getTextValues(List<ValueMarkup> valueMarkups) {

		if (CollectionUtils.isEmpty(valueMarkups)) {
			return null;
		}
		List<String> textValues = valueMarkups.stream()
				.map(ValueMarkup::getValue)
				.collect(Collectors.toList());
		return textValues;
	}
}
