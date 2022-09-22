package eki.ekilex.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.data.OrderedMap;
import eki.ekilex.client.FedTermClient;
import eki.ekilex.constant.QueueAction;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.FedTermUploadQueueContent;
import eki.ekilex.data.MeaningLexemeWordTuple;
import eki.ekilex.data.QueueItem;
import eki.ekilex.data.TypeValueNameLang;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.FedTermDataDbService;

@Component
public class FedTermUploadService implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(FedTermUploadService.class);

	private static final String VALUE_MAP_ANY_OTHER_KEY = "*";

	private static final int FED_TERM_MESSAGE_MAX_MEANING_COUNT = 100;

	@Value("${wordweb.dataset.url}")
	private String datasetLandingPageUrl;

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private FedTermDataDbService fedTermDataDbService;

	@Autowired
	private FedTermClient fedTermClient;

	private Map<Boolean, String> lexemeIsPublicMap;

	private Map<String, String> wordGenderMap;

	private Map<String, String> wordDisplayMorphMap;

	private Map<String, String> lexemePosMap;

	private Map<String, String> wordTypeMap;

	private Map<String, String> lexemeValueStateMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		lexemeIsPublicMap = new HashMap<>();
		lexemeIsPublicMap.put(Boolean.TRUE, "approved");
		lexemeIsPublicMap.put(Boolean.FALSE, "draft");

		wordGenderMap = new HashMap<>();
		wordGenderMap.put("m", "masculine");
		wordGenderMap.put("f", "feminine");
		wordGenderMap.put("n", "neutrum");
		wordGenderMap.put(VALUE_MAP_ANY_OTHER_KEY, "other");

		wordDisplayMorphMap = new HashMap<>();
		wordDisplayMorphMap.put("sg", "singular");
		wordDisplayMorphMap.put("pl", "slural");
		wordDisplayMorphMap.put(VALUE_MAP_ANY_OTHER_KEY, null);

		lexemePosMap = new HashMap<>();
		lexemePosMap.put("s", "noun");
		lexemePosMap.put("v", "verb");
		lexemePosMap.put("adj", "adjective");
		lexemePosMap.put("adv", "adverb");
		lexemePosMap.put("prop", "proper noun");
		lexemePosMap.put(VALUE_MAP_ANY_OTHER_KEY, "other");

		wordTypeMap = new HashMap<>();
		wordTypeMap.put("l", "acronym");
		wordTypeMap.put("th", "symbol");
		wordTypeMap.put(VALUE_MAP_ANY_OTHER_KEY, null);

		lexemeValueStateMap = new HashMap<>();
		lexemeValueStateMap.put("mööndav", "admitted term");
		lexemeValueStateMap.put("väldi", "deprecated term");
		lexemeValueStateMap.put("eelistatud", "preferred term");
		lexemeValueStateMap.put("endine", "superseded term");
		lexemeValueStateMap.put(VALUE_MAP_ANY_OTHER_KEY, null);
	}

	public boolean isFedTermAccessEnabled() {
		return fedTermClient.isFedTermAccessEnabled();
	}

	@Transactional
	public List<QueueItem> composeFedTermUploadQueueSteps(EkiUser user, String datasetCode) {

		if (!isFedTermAccessEnabled()) {
			return Collections.emptyList();
		}

		Dataset dataset = datasetDbService.getDataset(datasetCode);
		String datasetName = dataset.getName();
		int datasetMeaningCount = fedTermDataDbService.getMeaningCount(datasetCode);

		List<QueueItem> fedTermUploadQueueSteps = new ArrayList<>();

		for (int meaningOffset = 0; meaningOffset < datasetMeaningCount; meaningOffset += FED_TERM_MESSAGE_MAX_MEANING_COUNT) {

			QueueAction queueAction = QueueAction.FEDTERM_UPLOAD;
			String groupId = queueAction.name() + " " + datasetName;

			FedTermUploadQueueContent content = new FedTermUploadQueueContent();
			content.setDatasetCode(datasetCode);
			content.setDatasetName(datasetName);
			content.setMeaningOffset(meaningOffset);

			QueueItem queueItem = new QueueItem();
			queueItem.setGroupId(groupId);
			queueItem.setAction(queueAction);
			queueItem.setUser(user);
			queueItem.setContent(content);

			fedTermUploadQueueSteps.add(queueItem);
		}
		return fedTermUploadQueueSteps;
	}

	@Transactional
	public String getOrCreateFedTermCollectionId(String datasetCode) throws Exception {

		Dataset dataset = datasetDbService.getDataset(datasetCode);
		String fedTermCollectionId = dataset.getFedTermCollectionId();
		Map<String, Object> collectionMessageMap = composeFedTermCollectionMessageMap(dataset);
		String collectionMessageJson = convertToJson(collectionMessageMap);
		if (StringUtils.isBlank(fedTermCollectionId)) {
			fedTermCollectionId = fedTermClient.createFedTermCollection(datasetCode, collectionMessageJson);
			datasetDbService.setFedTermCollectionId(datasetCode, fedTermCollectionId);
		} else {
			fedTermClient.updateFedTermCollection(datasetCode, fedTermCollectionId, collectionMessageJson);
		}
		return fedTermCollectionId;
	}

	@Transactional
	public void deleteFedTermCollection(String datasetCode) throws Exception {

		Dataset dataset = datasetDbService.getDataset(datasetCode);
		String fedTermCollectionId = dataset.getFedTermCollectionId();
		datasetDbService.setFedTermCollectionId(datasetCode, null);
		fedTermClient.deleteFedTermCollection(datasetCode, fedTermCollectionId);
	}

	@Transactional
	public void uploadFedTermConceptEntries(String fedTermCollectionId, FedTermUploadQueueContent content) throws Exception {

		if (StringUtils.isBlank(fedTermCollectionId)) {
			logger.warn("FedTerm collection id not specified. Skipping {}", content);
			return;
		}
		String datasetCode = content.getDatasetCode();
		String datasetName = content.getDatasetName();
		int meaningOffset = content.getMeaningOffset();
		int meaningLimit = FED_TERM_MESSAGE_MAX_MEANING_COUNT - 1;
		Map<String, String> languageIso2Map = commonDataService.getLanguageIso2Map();
		List<Long> meaningIds = fedTermDataDbService.getMeaningIds(datasetCode, meaningOffset, meaningLimit);
		List<MeaningLexemeWordTuple> datasetMeaningLexemeWordTuples = fedTermDataDbService.getMeaningLexemeWordTuples(datasetCode, meaningIds);
		Document conceptEntriesTbxMessageDocument = composeFedTermConceptEntriesTbxDocument(datasetName, datasetMeaningLexemeWordTuples, languageIso2Map);
		String conceptEntriesTbxMessageXml = convertToXml(conceptEntriesTbxMessageDocument);
		fedTermClient.createOrUpdateFedTermConceptEntries(datasetCode, fedTermCollectionId, conceptEntriesTbxMessageXml);
	}

	private Document composeFedTermConceptEntriesTbxDocument(
			String datasetName, List<MeaningLexemeWordTuple> datasetMeaningLexemeWordTuples, Map<String, String> languageIso2Map) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element martif = appendChild(document, document, "martif");
		martif.setAttribute("type", "TBX");
		martif.setAttribute("xml:lang", "en");
		Element martifHeader = appendChild(document, martif, "martifHeader");
		Element fileDesc = appendChild(document, martifHeader, "fileDesc");
		Element sourceDesc = appendChild(document, fileDesc, "sourceDesc");
		appendChild(document, sourceDesc, "p", datasetName);
		Element text = appendChild(document, martif, "text");
		Element body = appendChild(document, text, "body");

		List<Long> meaningIds = datasetMeaningLexemeWordTuples.stream().map(MeaningLexemeWordTuple::getMeaningId).distinct().collect(Collectors.toList());
		Map<Long, List<MeaningLexemeWordTuple>> meaningMap = datasetMeaningLexemeWordTuples.stream().collect(Collectors.groupingBy(MeaningLexemeWordTuple::getMeaningId));

		for (Long meaningId : meaningIds) {

			String externalIdAttrValue = "external-id-" + meaningId;
			Element termEntry = appendChild(document, body, "termEntry", "id", externalIdAttrValue);

			List<MeaningLexemeWordTuple> meaningLexemeWordTuples = meaningMap.get(meaningId);

			for (MeaningLexemeWordTuple meaningLexemeWordTuple : meaningLexemeWordTuples) {

				String wordLanguageCode = meaningLexemeWordTuple.getWordLanguageCode();
				String wordLanguageCodeIso2 = languageIso2Map.get(wordLanguageCode);
				Element langSet = appendChild(document, termEntry, "langSet", "xml:lang", wordLanguageCodeIso2);
				Element ntig = appendChild(document, langSet, "ntig");
				Element termGrp = appendChild(document, ntig, "termGrp");
				appendChild(document, termGrp, "term", meaningLexemeWordTuple.getWordValue());

				String lexemeIsPublicMappedValue = lexemeIsPublicMap.get(meaningLexemeWordTuple.isLexemeIsPublic());
				appendChild(document, ntig, "admin", lexemeIsPublicMappedValue, "type", "status");

				String wordGenderCode = meaningLexemeWordTuple.getWordGenderCode();
				if (StringUtils.isNotBlank(wordGenderCode)) {
					String wordGenderMappedValue = getMappedValue(wordGenderCode, wordGenderMap);
					appendChild(document, ntig, "termNote", wordGenderMappedValue, "type", "grammaticalGender");
				}

				String wordDisplayMorphCode = meaningLexemeWordTuple.getWordDisplayMorphCode();
				if (StringUtils.isNotBlank(wordDisplayMorphCode)) {
					String wordDisplayMorphMappedValue = getMappedValue(wordDisplayMorphCode, wordDisplayMorphMap);
					if (StringUtils.isNotBlank(wordDisplayMorphMappedValue)) {
						appendChild(document, ntig, "termNote", wordDisplayMorphMappedValue, "type", "grammaticalNumber");
					}
				}

				String lexemePosCode = meaningLexemeWordTuple.getLexemePosCode();
				if (StringUtils.isNotBlank(lexemePosCode)) {
					String lexemePosMappedValue = getMappedValue(lexemePosCode, lexemePosMap);
					appendChild(document, ntig, "termNote", lexemePosMappedValue, "type", "partOfSpeech");
				}

				String wordTypeCode = meaningLexemeWordTuple.getWordTypeCode();
				if (StringUtils.isNotBlank(wordTypeCode)) {
					String wordTypeMappedValue = getMappedValue(wordTypeCode, wordTypeMap);
					if (StringUtils.isNotBlank(wordTypeMappedValue)) {
						appendChild(document, ntig, "termNote", wordTypeMappedValue, "type", "termType");
					}
				}

				String lexemeValueStateCode = meaningLexemeWordTuple.getLexemeValueStateCode();
				if (StringUtils.isNotBlank(lexemeValueStateCode)) {
					String lexemeValueStateMappedValue = getMappedValue(lexemeValueStateCode, lexemeValueStateMap);
					if (StringUtils.isNotBlank(lexemeValueStateMappedValue)) {
						appendChild(document, ntig, "termNote", lexemeValueStateMappedValue, "type", "administrativeStatus");
					}
				}

				List<TypeValueNameLang> lexemeNoteValuesAndSourceNames = meaningLexemeWordTuple.getLexemeNoteValuesAndSourceNames();
				if (CollectionUtils.isNotEmpty(lexemeNoteValuesAndSourceNames)) {
					TypeValueNameLang lexemeNoteValueAndSourceName = lexemeNoteValuesAndSourceNames.get(0);
					String lexemeNoteValue = lexemeNoteValueAndSourceName.getValue();
					String lexemeNoteSourceName = lexemeNoteValueAndSourceName.getName();
					appendChild(document, ntig, "termNote", lexemeNoteValue, "type", "usageNote");
					if (StringUtils.isNotBlank(lexemeNoteSourceName)) {
						Element xref = appendChild(document, ntig, "xref", "type", "xSource");
						xref.setAttribute("target", lexemeNoteSourceName);
					}
				}

				List<TypeValueNameLang> lexemeUsageValuesAndSourceNames = meaningLexemeWordTuple.getLexemeUsageValuesAndSourceNames();
				if (CollectionUtils.isNotEmpty(lexemeUsageValuesAndSourceNames)) {
					TypeValueNameLang lexemeUsageValueAndSourceName = lexemeUsageValuesAndSourceNames.get(0);
					String lexemeUsageValue = lexemeUsageValueAndSourceName.getValue();
					String lexemeUsageSourceName = lexemeUsageValueAndSourceName.getName();
					Element descripGrp = appendChild(document, ntig, "descripGrp");
					appendChild(document, descripGrp, "descrip", lexemeUsageValue, "type", "context");
					if (StringUtils.isNotBlank(lexemeUsageSourceName)) {
						appendChild(document, descripGrp, "admin", lexemeUsageSourceName, "type", "source");
					}
				}

				List<TypeValueNameLang> definitionValuesAndSourceNames = meaningLexemeWordTuple.getDefinitionValuesAndSourceNames();
				if (CollectionUtils.isNotEmpty(definitionValuesAndSourceNames)) {
					definitionValuesAndSourceNames = definitionValuesAndSourceNames.stream()
							.filter(row -> StringUtils.equals(wordLanguageCode, row.getLang()))
							.collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(definitionValuesAndSourceNames)) {
						List<String> definitionValues = new ArrayList<>();
						for (TypeValueNameLang definitionValuesAndSourceName : definitionValuesAndSourceNames) {
							String definitionValue = definitionValuesAndSourceName.getValue();
							String definitionSourceName = definitionValuesAndSourceName.getName();
							if (definitionValues.contains(definitionValue)) {
								continue;
							}
							definitionValues.add(definitionValue);
							Element descripGrp = appendChild(document, langSet, "descripGrp");
							appendChild(document, descripGrp, "descrip", definitionValue, "type", "definition");
							if (StringUtils.isNotBlank(definitionSourceName)) {
								appendChild(document, descripGrp, "admin", definitionSourceName, "type", "source");
							}
						}
					}
				}
			}
		}

		return document;
	}

	private Element appendChild(Document document, Node parent, String childName) {
		Element martifHeader = document.createElement(childName);
		parent.appendChild(martifHeader);
		return martifHeader;
	}

	private Element appendChild(Document document, Node parent, String childName, String childValue) {
		Element child = document.createElement(childName);
		child.setTextContent(childValue);
		parent.appendChild(child);
		return child;
	}

	private Element appendChild(Document document, Node parent, String childName, String attrName, String attrValue) {
		Element child = document.createElement(childName);
		child.setAttribute(attrName, attrValue);
		parent.appendChild(child);
		return child;
	}

	private Element appendChild(Document document, Node parent, String childName, String childValue, String attrName, String attrValue) {
		Element child = document.createElement(childName);
		child.setAttribute(attrName, attrValue);
		child.setTextContent(childValue);
		parent.appendChild(child);
		return child;
	}

	private String getMappedValue(String originalValue, Map<String, String> valueMap) {
		String mappedValue;
		if (valueMap.containsKey(originalValue)) {
			mappedValue = valueMap.get(originalValue);
		} else {
			mappedValue = valueMap.get(VALUE_MAP_ANY_OTHER_KEY);
		}
		return mappedValue;
	}

	private Map<String, Object> composeFedTermCollectionMessageMap(Dataset dataset) throws Exception {

		String datasetCode = dataset.getCode();
		String datasetName = dataset.getName();
		String datasetDescription = dataset.getDescription();
		datasetDescription = StringUtils.replaceChars(datasetDescription, '\r', ' ');
		datasetDescription = StringUtils.replaceChars(datasetDescription, '\n', ' ');
		String fedTermDomainId = dataset.getFedTermDomainId();
		String datasetLandingPageUrlWithDatasetCode = StringUtils.replace(datasetLandingPageUrl, "{datasetCode}", datasetCode);

		Map<String, Object> collectionMessageMap = new OrderedMap<>();
		collectionMessageMap.put("name", datasetName);
		collectionMessageMap.put("description", datasetDescription);
		collectionMessageMap.put("domainid", fedTermDomainId);
		collectionMessageMap.put("allowsUsesBesidesDGT", Boolean.TRUE);
		collectionMessageMap.put("appropriatnessForDSI", Boolean.TRUE);
		collectionMessageMap.put("attributionText", datasetDescription);
		/* 
		 * temporarily removed
		 *  
		collectionMessageMap.put("cpName", "n/a");
		collectionMessageMap.put("cpSurname", "n/a");
		collectionMessageMap.put("cpOrganization", "Institute of the Estonian Language");
		collectionMessageMap.put("cpEmail", "eki@eki.ee");
		collectionMessageMap.put("iprName", "n/a");
		collectionMessageMap.put("iprSurname", "n/a");
		collectionMessageMap.put("iprOrganization", "Institute of the Estonian Language");
		collectionMessageMap.put("iprEmail", "eki@eki.ee");
		*/
		collectionMessageMap.put("isPSI", Boolean.FALSE);
		collectionMessageMap.put("licence", "CC-BY-4.0");
		collectionMessageMap.put("originalName", datasetName);
		//collectionMessageMap.put("originalNameLang", "et");
		collectionMessageMap.put("restrictionsOfUse", "No restrictions, you are welcome to use");
		collectionMessageMap.put("sourceURL", datasetLandingPageUrlWithDatasetCode);

		return collectionMessageMap;
	}

	private String convertToJson(Map<String, Object> map) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

		return json;
	}

	private String convertToXml(Document document) throws Exception {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 2);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "https://eurotermbank.com/TBXcoreStructV02%20%281%29.dtd");
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(byteArrayOutputStream);
		transformer.transform(source, result);
		String xml = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
		byteArrayOutputStream.close();

		return xml;
	}
}
