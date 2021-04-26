package eki.ekilex.cli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.ClassifierName;
import eki.common.constant.Complexity;
import eki.common.constant.ContentKey;
import eki.common.constant.DatasetType;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.data.Count;
import eki.common.data.OrderedMap;
import eki.common.exception.DataLoadingException;
import eki.common.service.TextDecorationService;
import eki.common.service.XmlReader;
import eki.common.util.ConsolePromptUtil;
import eki.ekilex.cli.util.TermLoaderHelper;
import eki.ekilex.constant.TermLoaderConstant;
import eki.ekilex.data.AffixoidData;
import eki.ekilex.data.Content;
import eki.ekilex.data.Ref;

public class EstermXml2Json implements LoaderConstant, TermLoaderConstant, GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(EstermXml2Json.class);

	private static final String ARG_KEY_SOURCE_FILE = "srcfile";

	private static final String ARG_KEY_TARGET_FOLDER = "trgfolder";

	private static final int SEQ_INIT_VAL = 100000;

	private XmlReader xmlReader;

	private TextDecorationService textDecorationService;

	private TermLoaderHelper loaderHelper;

	private Count sourceIdSeq;

	private Count wordIdSeq;

	private Count lexemeIdSeq;

	private Count meaningIdSeq;

	private Count definitionIdSeq;

	private Count freeformIdSeq;

	private Count sourceFreeformIdSeq;

	private Count lexemeFreeformIdSeq;

	private Count meaningFreeformIdSeq;

	private Count definitionFreeformIdSeq;

	private Count definitionSourceLinkIdSeq;

	private Count lexemeSourceLinkIdSeq;

	private Count freeformSourceLinkIdSeq;

	private Map<String, Count> homonymNrMap;

	private Map<String, String> lexemeValueStateCodes;

	private Map<String, String> wordTypeCodes;

	//mvn exec:java -D exec.args="srcfile="/path/to/esterm.xml" trgfolder="/path/to/target/""
	public static void main(String[] args) throws Exception {
		if (ArrayUtils.isEmpty(args)) {
			logger.warn("Please provide arguments \"{}\", \"{}\"", ARG_KEY_SOURCE_FILE, ARG_KEY_TARGET_FOLDER);
			return;
		}
		String dataXmlFilePath = ConsolePromptUtil.getKeyValue(ARG_KEY_SOURCE_FILE, args);
		String targetFolder = ConsolePromptUtil.getKeyValue(ARG_KEY_TARGET_FOLDER, args);

		if (StringUtils.isBlank(dataXmlFilePath)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_SOURCE_FILE);
			return;
		}
		if (StringUtils.isBlank(targetFolder)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_TARGET_FOLDER);
			return;
		}
		if (!validateDataFileExists(dataXmlFilePath)) {
			logger.warn("Incorrect source file path: \"{}\"", dataXmlFilePath);
			return;
		}
		if (!validateDataFolderExists(targetFolder)) {
			logger.warn("Incorrect target folder: \"{}\"", targetFolder);
			return;
		}
		EstermXml2Json estermXml2Json = new EstermXml2Json();
		estermXml2Json.initialise();
		estermXml2Json.execute(dataXmlFilePath, targetFolder);
	}

	private String getComplexityValue() {
		return Complexity.DETAIL.name();
	}

	private String getDatasetCode() {
		return "est";
	}

	private void initialise() throws Exception {

		xmlReader = new XmlReader();
		textDecorationService = new TextDecorationService();
		textDecorationService.afterPropertiesSet();
		loaderHelper = new TermLoaderHelper();

		// id sequences
		sourceIdSeq = new Count(SEQ_INIT_VAL);
		wordIdSeq = new Count(SEQ_INIT_VAL);
		lexemeIdSeq = new Count(SEQ_INIT_VAL);
		meaningIdSeq = new Count(SEQ_INIT_VAL);
		definitionIdSeq = new Count(SEQ_INIT_VAL);
		freeformIdSeq = new Count(SEQ_INIT_VAL);
		sourceFreeformIdSeq = new Count(SEQ_INIT_VAL);
		lexemeFreeformIdSeq = new Count(SEQ_INIT_VAL);
		meaningFreeformIdSeq = new Count(SEQ_INIT_VAL);
		definitionFreeformIdSeq = new Count(SEQ_INIT_VAL);
		definitionSourceLinkIdSeq = new Count(SEQ_INIT_VAL);
		lexemeSourceLinkIdSeq = new Count(SEQ_INIT_VAL);
		freeformSourceLinkIdSeq = new Count(SEQ_INIT_VAL);

		homonymNrMap = new HashMap<>();

		// classif mapings
		Map<String, String> tempCodes;

		// word type
		wordTypeCodes = new HashMap<>();
		tempCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_MÕISTETÜÜP, ClassifierName.WORD_TYPE.name());
		wordTypeCodes.putAll(tempCodes);
		tempCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_KEELENDITÜÜP, ClassifierName.WORD_TYPE.name());
		wordTypeCodes.putAll(tempCodes);

		// lexeme value state
		lexemeValueStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_KEELENDITÜÜP, ClassifierName.VALUE_STATE.name());
	}

	private void execute(String dataXmlFilePath, String targetFolder) throws Exception {

		logger.debug("Starting loading file: \"{}\"", dataXmlFilePath);

		Map<String, Object> serialisableData = new OrderedMap<>();
		Map<String, Long> sorceIdMap = new HashMap<>();
		extractSources(dataXmlFilePath, serialisableData, sorceIdMap);
		extractDataset(dataXmlFilePath, serialisableData, sorceIdMap);
		saveToFile(serialisableData, targetFolder);

		logger.info("All done!");
	}

	private void extractSources(String dataXmlFilePath, Map<String, Object> serialisableData, Map<String, Long> sorceIdMap) throws Exception {

		logger.debug("Extracting sources...");

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		String fileName = FilenameUtils.getName(dataXmlFilePath);
		List<Node> conceptGroupNodes = dataDoc.selectNodes(sourceConceptGroupExp);

		List<Map<String, Object>> sources = new ArrayList<>();
		serialisableData.put("source", sources);

		int conceptGroupCount = conceptGroupNodes.size();
		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Node conceptGroupNode : conceptGroupNodes) {

			Node extSourceIdNode = conceptGroupNode.selectSingleNode(conceptExp);
			Element extSourceIdElement = (Element) extSourceIdNode;
			String extSourceId = extSourceIdElement.getTextTrim();

			Node sourceNameNode = conceptGroupNode.selectSingleNode(langGroupExp + "/" + termGroupExp + "/" + termValueExp);
			Element sourceNameElement = (Element) sourceNameNode;
			String sourceName = sourceNameElement.getTextTrim();

			Map<String, Object> source = extractAndCreateSource(conceptGroupNode, extSourceId, fileName);
			sources.add(source);

			Long sourceId = (Long) source.get("id");
			sorceIdMap.put(sourceName, sourceId);

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		logger.debug("Extracted {} sources, {} source name mappings", sources.size(), sorceIdMap.size());
	}

	private Map<String, Object> extractAndCreateSource(Node conceptGroupNode, String extSourceId, String fileName) throws Exception {

		Map<String, Object> sourceFreeform;
		List<Node> termGroupNodes;
		Long sourceId;
		Map<String, Object> source;
		List<Map<String, Object>> sourceFreeforms = new ArrayList<>();
		sourceId = sourceIdSeq.increment();

		source = new OrderedMap<>();
		source.put("id", sourceId);
		source.put("type", SourceType.DOCUMENT.name());
		source.put("source_freeform", sourceFreeforms);

		sourceFreeform = createSourceFreeform(sourceId, FreeformType.EXTERNAL_SOURCE_ID, extSourceId);
		sourceFreeforms.add(sourceFreeform);
		sourceFreeform = createSourceFreeform(sourceId, FreeformType.SOURCE_FILE, fileName);
		sourceFreeforms.add(sourceFreeform);

		termGroupNodes = conceptGroupNode.selectNodes(langGroupExp + "/" + termGroupExp);
		for (Node termGroupNode : termGroupNodes) {

			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_NAME, termValueExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.LTB_SOURCE, sourceLtbSourceExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_RT, sourceRtExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_CELEX, sourceCelexExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_WWW, sourceWwwExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_AUTHOR, sourceAuthorExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_ISBN, sourceIsbnExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_ISSN, sourceIssnExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_PUBLISHER, sourcePublisherExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_PUBLICATION_YEAR, sourcePublicationYearExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_PUBLICATION_PLACE, sourcePublicationPlaceExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.SOURCE_PUBLICATION_NAME, sourcePublicationNameExp);
			extractAndSaveFreeforms(sourceId, sourceFreeforms, termGroupNode, FreeformType.NOTE, sourceNoteExp);
		}
		return source;
	}

	private void extractAndSaveFreeforms(Long sourceId, List<Map<String, Object>> sourceFreeforms, Node termGroupNode, FreeformType freeformType, String sourceTermPropertyExp) throws Exception {

		List<Node> sourceTermPropertyNodes = termGroupNode.selectNodes(sourceTermPropertyExp);
		String valueStr;

		for (Node sourceTermPropertyNode : sourceTermPropertyNodes) {
			valueStr = ((Element) sourceTermPropertyNode).getTextTrim();
			if (StringUtils.isBlank(valueStr)) {
				continue;
			}
			Map<String, Object> sourceFreeform = createSourceFreeform(sourceId, freeformType, valueStr);
			if (sourceFreeform != null) {
				sourceFreeforms.add(sourceFreeform);
			}
		}
	}

	private Map<String, Object> createSourceFreeform(Long sourceId, FreeformType freeformType, String value) throws Exception {

		Map<String, Object> freeform = createFreeformTextOrTimestamp(freeformType, value, null, true);
		if (freeform == null) {
			return null;
		}

		Long id = sourceFreeformIdSeq.increment();
		Long freeformId = (Long) freeform.get("id");
		Map<String, Object> lexemeFreeform = new OrderedMap<>();
		lexemeFreeform.put("id", id);
		lexemeFreeform.put("source_id", sourceId);
		lexemeFreeform.put("freeform_id", freeformId);
		lexemeFreeform.put("freeform", freeform);

		return lexemeFreeform;
	}

	private void extractDataset(String dataXmlFilePath, Map<String, Object> serialisableData, Map<String, Long> sourceIdMap) throws Exception {

		logger.debug("Extracting dataset...");

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		List<Node> conceptGroupNodes = dataDoc.selectNodes(conceptGroupExp);
		conceptGroupNodes = conceptGroupNodes.stream().filter(node -> isLanguageTypeConcept(node)).collect(Collectors.toList());

		Element valueNode;
		List<Node> langGroupNodes, termGroupNodes, domainNodes;
		Long wordId, meaningId, lexemeId;
		List<String> termWordTypeCodes;
		String valueStr, concept, term, conceptWordTypeCode, valueStateCode;
		String lang;
		Map<String, Object> dataset, word, meaning, lexeme;

		dataset = new OrderedMap<>();
		dataset.put("code", getDatasetCode());
		dataset.put("name", "EKI terminibaas Esterm");
		dataset.put("description",
				"Esterm on Eesti Keele Instituudi mitmekeelne terminibaas, mis sisaldab peamiselt Euroopa Liidu ja Eesti Vabariigi õigusaktide termineid. "
				+ "Terminibaas koondab üle 50 valdkonna terminoloogiat, näiteks põllumajandus, rahandus, õigus, transport, meditsiin, keemia, bioloogia, avalik haldus. "
				+ "Peale terminite on baasis ka organisatsioonide ja asutuste nimetusi, ametinimetusi, õigusaktide ja dokumentide pealkirju ning tõlkeprobleeme ehk fraase, mis võivad tõlkimisel raskusi valmistada.\n"
				+ "Eesti Keele Instituudi terminoloogid täiendavad ja ajakohastavad Estermi pidevalt, sest mõisted ja terminid muutuvad. Terminitöö eesmärk on koondada, korrastada, talletada ja levitada terminoloogiat. "
				+ "Konsulteerime sageli eri valdkondade asjatundjatega ja oleme avatud terminoloogiakoostööle. \n"
				+ "Esterm loodi 1996. aastal Eesti Õigustõlke Keskuses õigusaktide tõlkimise abivahendina, et talletada uuritud termineid ja tagada tõlgitavates õigusaktides järjepidava terminoloogia kasutamine. "
				+ "2006. aastast haldab Estermi Eesti Keele Instituut. Tõlkepõhiselt terminitöölt oleme üle läinud süsteemsele, valdkondade ja mõistesüsteemide kaupa tehtavale terminitööle.\n"
				+ "Esterm on mõeldud tõlkijatele, tõlkidele, spetsialistidele, ajakirjanikele, toimetajatele, ametnikele, teadustöötajatele, aga ka teistele erialatekstide tõlkimise või koostamisega tegelevatele inimestele.\n"
				+ "Kontakt: terminoloogia@eki.ee");
		dataset.put("type", DatasetType.TERM.name());
		dataset.put("is_public", Boolean.FALSE);
		dataset.put("is_visible", Boolean.TRUE);

		List<Map<String, Object>> words = new ArrayList<>();
		List<Map<String, Object>> meanings = new ArrayList<>();
		List<Map<String, Object>> lexemes = new ArrayList<>();

		serialisableData.put("dataset", dataset);
		serialisableData.put("word", words);
		serialisableData.put("meaning", meanings);
		serialisableData.put("lexeme", lexemes);

		int conceptGroupCount = conceptGroupNodes.size();
		int conceptGroupCounter = 0;
		int progressIndicator = conceptGroupCount / Math.min(conceptGroupCount, 100);

		for (Node conceptGroupNode : conceptGroupNodes) {

			valueNode = (Element) conceptGroupNode.selectSingleNode(conceptExp);
			concept = valueNode.getTextTrim();

			// meaning
			List<Map<String, Object>> meaningDomains = new ArrayList<>();
			List<Map<String, Object>> meaningFreeforms = new ArrayList<>();
			List<Map<String, Object>> definitions = new ArrayList<>();

			meaningId = meaningIdSeq.increment();
			meaning = new OrderedMap<>();
			meaning.put("id", meaningId);
			meaning.put("meaning_domain", meaningDomains);
			meaning.put("meaning_freeform", meaningFreeforms);
			meaning.put("definition", definitions);
			meanings.add(meaning);

			extractAndCreateMeaningFreeforms(meaningId, meaningFreeforms, conceptGroupNode, sourceIdMap);

			domainNodes = conceptGroupNode.selectNodes(domainExp);
			extractAndCreateDomains(meaningId, meaningDomains, domainNodes, originLenoch);
			domainNodes = conceptGroupNode.selectNodes(subdomainExp);
			extractAndCreateDomains(meaningId, meaningDomains, domainNodes, originLtb);

			boolean isPublic = extractPublicity(conceptGroupNode);
			conceptWordTypeCode = extractConceptWordType(conceptGroupNode);

			langGroupNodes = conceptGroupNode.selectNodes(langGroupExp);

			for (Node langGroupNode : langGroupNodes) {

				valueNode = (Element) langGroupNode.selectSingleNode(langExp);
				valueStr = valueNode.attributeValue(langTypeAttr);
				boolean isLang = isLang(valueStr);

				if (!isLang) {
					continue;
				}

				lang = unifyLang(valueStr);

				//upper level definitions and notes
				extractAndCreateDefinitionsAndNotes(meaningId, definitions, langGroupNode, lang, concept, sourceIdMap);

				termGroupNodes = langGroupNode.selectNodes(termGroupExp);

				for (Node termGroupNode : termGroupNodes) {

					valueNode = (Element) termGroupNode.selectSingleNode(termExp);
					term = valueNode.getTextTrim();
					if (StringUtils.isBlank(term)) {
						continue;
					}

					termWordTypeCodes = extractTermGroupWordTypeCode(termGroupNode, conceptWordTypeCode);

					AffixoidData affixoidData = getAffixoidData(term);
					handleAffixoidClassifiers(termWordTypeCodes, affixoidData);
					term = affixoidData.getWordCleanValue();
					String termCleanValue = textDecorationService.unifyToApostrophe(term);
					String termValueAsWord = textDecorationService.removeAccents(termCleanValue);
					if (StringUtils.isBlank(termValueAsWord) && !StringUtils.equals(term, termCleanValue)) {
						termValueAsWord = termCleanValue;
					}

					// word
					wordId = wordIdSeq.increment();
					Integer homonymNr = getHomonymNr(termCleanValue, lang);
					word = new OrderedMap<>();
					word.put("id", wordId);
					word.put("lang", lang);
					word.put("homonym_nr", homonymNr);
					word.put("value", termCleanValue);
					word.put("value_prese", termCleanValue);
					word.put("value_as_word", termValueAsWord);
					words.add(word);

					createWordWordTypes(word, termWordTypeCodes);

					// lexeme
					valueStateCode = extractValueStateCode(termGroupNode);
					List<Map<String, Object>> lexemeFreeforms = new ArrayList<>();
					List<Map<String, Object>> lexemeSourceLinks = new ArrayList<>();

					lexemeId = lexemeIdSeq.increment();
					lexeme = new OrderedMap<>();
					lexeme.put("id", lexemeId);
					lexeme.put("word_id", wordId);
					lexeme.put("meaning_id", meaningId);
					lexeme.put("dataset_code", getDatasetCode());
					lexeme.put("complexity", getComplexityValue());
					lexeme.put("value_state_code", valueStateCode);
					lexeme.put("is_public", isPublic);
					lexeme.put("lexeme_freeform", lexemeFreeforms);
					lexeme.put("lexeme_source_link", lexemeSourceLinks);
					lexemes.add(lexeme);

					extractAndCreateDefinitionsAndSourceLinks(meaningId, definitions, termGroupNode, term, lang, sourceIdMap);
					extractAndCreateLexemeSourceLinks(lexemeId, lexemeSourceLinks, termGroupNode, term, lang, sourceIdMap);
					extractAndCreateLexemeNotesAndSourceLinks(lexemeId, lexemeFreeforms, termGroupNode, term, lang, sourceIdMap);
					extractAndCreateUsagesAndSourceLinks(lexemeId, lexemeFreeforms, termGroupNode, term, lang, sourceIdMap);
				}
			}

			conceptGroupCounter++;
			if (conceptGroupCounter % progressIndicator == 0) {
				int progressPercent = conceptGroupCounter / progressIndicator;
				logger.debug("{}% - {} concept groups iterated", progressPercent, conceptGroupCounter);
			}
		}

		logger.info("Extracted {} meanings", meanings.size());
		logger.info("Extracted {} words", words.size());
		logger.info("Extracted {} lexemes", lexemes.size());
	}

	private void saveToFile(Map<String, Object> serialisableData, String targetFolder) throws Exception {

		logger.info("Saving to file...");

		String dataFileName = getDatasetCode() + ".zip";
		String dataFilePath = targetFolder + dataFileName;
		File exportFile = new File(dataFilePath);
		FileOutputStream exportFileOutputStream = new FileOutputStream(exportFile);
		BufferedOutputStream exportBufferedOutputStream = new BufferedOutputStream(exportFileOutputStream);
		ZipOutputStream jsonZipOutputStream = new ZipOutputStream(exportBufferedOutputStream, Charset.forName(UTF_8));

		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(jsonZipOutputStream);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonGenerator.setCodec(objectMapper);

		String zipEntryName;
		ZipEntry zipEntry;

		for (Entry<String, Object> dataEntry : serialisableData.entrySet()) {
			String dataName = dataEntry.getKey();
			Object dataObject = dataEntry.getValue();
			zipEntryName = dataName + ".json";
			zipEntry = new ZipEntry(zipEntryName);
			jsonZipOutputStream.putNextEntry(zipEntry);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeFieldName(dataName);
			jsonGenerator.writeObject(dataObject);
			jsonGenerator.writeEndObject();
			jsonGenerator.flush();
			jsonZipOutputStream.closeEntry();
		}

		jsonGenerator.close();
		jsonZipOutputStream.flush();
		jsonZipOutputStream.close();
		exportBufferedOutputStream.flush();
		exportBufferedOutputStream.close();
		exportFileOutputStream.flush();
		exportFileOutputStream.close();
	}

	private static boolean validateDataFileExists(String dataFilePath) {
		File dataFile = new File(dataFilePath);
		if (!dataFile.exists()) {
			return false;
		}
		if (!dataFile.isFile()) {
			return false;
		}
		return true;
	}

	private static boolean validateDataFolderExists(String dataFolderPath) {
		File dataFolder = new File(dataFolderPath);
		if (!dataFolder.exists()) {
			return false;
		}
		if (dataFolder.isFile()) {
			return false;
		}
		return true;
	}

	private Map<String, String> loadClassifierMappingsFor(String ekiClassifierName, String lexClassifierName) throws Exception {
		// in case of duplicate keys, last value is used
		List<String> classifMappingLines = readFileLines(CLASSIFIERS_MAPPING_FILE_PATH);
		return classifMappingLines.stream()
				.filter(line -> StringUtils.startsWith(line, ekiClassifierName))
				.map(line -> StringUtils.split(line, CSV_SEPARATOR))
				.filter(cells -> (lexClassifierName == null) || StringUtils.equalsIgnoreCase(lexClassifierName, cells[5]))
				.filter(cells -> "et".equals(cells[4]))
				.filter(cells -> !"-".equals(cells[5]))
				.collect(Collectors.toMap(cells -> cells[2], cells -> cells[6], (c1, c2) -> c2));
	}

	private List<String> readFileLines(String sourcePath) throws Exception {
		try (InputStream resourceInputStream = new FileInputStream(sourcePath)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

	private boolean isLanguageTypeConcept(Node conceptGroupNode) {

		String valueStr;
		List<Node> valueNodes = conceptGroupNode.selectNodes(langGroupExp + "/" + langExp);
		for (Node langNode : valueNodes) {
			valueStr = ((Element) langNode).attributeValue(langTypeAttr);
			boolean isLang = isLang(valueStr);
			if (isLang) {
				continue;
			}
			return false;
		}
		return true;
	}

	private boolean isLang(String lang) {
		Locale locale = new Locale(lang);
		String displayName = locale.getDisplayName();
		boolean isLang = !StringUtils.equalsIgnoreCase(lang, displayName);
		return isLang;
	}

	private void extractAndCreateMeaningFreeforms(Long meaningId, List<Map<String, Object>> meaningFreeforms, Node conceptGroupNode, Map<String, Long> sourceIdMap) throws Exception {

		List<Node> valueNodes;
		Element valueNode;
		String valueStr;
		Map<String, Object> meaningFreeform;

		valueNode = (Element) conceptGroupNode.selectSingleNode(conceptExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.CONCEPT_ID, valueStr, true);
			meaningFreeforms.add(meaningFreeform);
		}

		valueNodes = conceptGroupNode.selectNodes(ltbIdExp);
		for (Node ltbIdNode : valueNodes) {
			valueStr = ((Element) ltbIdNode).getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.LTB_ID, valueStr, true);
			meaningFreeforms.add(meaningFreeform);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(ltbSourceExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.LTB_SOURCE, valueStr, true);
			meaningFreeforms.add(meaningFreeform);
		}

		valueNodes = conceptGroupNode.selectNodes(noteExp);
		for (Node noteValueNode : valueNodes) {
			valueStr = ((Element) noteValueNode).getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.NOTE, valueStr, true);
			meaningFreeforms.add(meaningFreeform);
			if (((Element) noteValueNode).hasMixedContent()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> freeform = (Map<String, Object>) meaningFreeform.get("freeform");
				valueStr = handleFreeformTextSourceLinks(SourceOwner.NOTE, noteValueNode, freeform, sourceIdMap);
				updateFreeformText(freeform, valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(privateNoteExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.NOTE, valueStr, false);
			meaningFreeforms.add(meaningFreeform);
			if (valueNode.hasMixedContent()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> freeform = (Map<String, Object>) meaningFreeform.get("freeform");
				valueStr = handleFreeformTextSourceLinks(SourceOwner.NOTE, valueNode, freeform, sourceIdMap);
				updateFreeformText(freeform, valueStr);
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(unclassifiedExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.UNCLASSIFIED, valueStr, true);
			meaningFreeforms.add(meaningFreeform);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(worksheetExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningFreeform = createMeaningFreeform(meaningId, FreeformType.NOTE, valueStr, false);
			meaningFreeforms.add(meaningFreeform);
		}
	}

	private String handleFreeformTextSourceLinks(SourceOwner sourceOwner, Node mixedContentNode, Map<String, Object> freeform, Map<String, Long> sourceIdMap) throws Exception {

		Long freeformId = (Long) freeform.get("id");

		List<Map<String, Object>> freeformSourceLinks = new ArrayList<>();
		freeform.put("freeform_source_link", freeformSourceLinks);

		Iterator<Node> contentNodeIter = ((Element) mixedContentNode).nodeIterator();
		StringBuffer contentBuf = new StringBuffer();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				contentBuf.append(valueStr);
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					String sourceName;
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						sourceName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
					} else {
						sourceName = elemContentNode.getTextTrim();
					}
					Long sourceId = sourceIdMap.get(sourceName);
					if (sourceId == null) {
						contentBuf.append(valueStr);
					} else {
						if (SourceOwner.NOTE.equals(sourceOwner)) {
							Map<String, Object> freeformSourceLink = createFreeformSourceLink(freeformId, ReferenceType.ANY, sourceId, null, valueStr);
							freeformSourceLinks.add(freeformSourceLink);
							Long sourceLinkId = (Long) freeformSourceLink.get("id");
							String sourceLinkMarkup = textDecorationService.composeLinkMarkup(ContentKey.FREEFORM_SOURCE_LINK, sourceLinkId.toString(), valueStr);
							contentBuf.append(sourceLinkMarkup);
						}
					}
				} else {
					throw new DataLoadingException("Unsupported mixed content node name: " + contentNode.getName());
				}
			} else {
				throw new DataLoadingException("Unsupported mixed content node type: " + contentNode.getClass());
			}
		}
		valueStr = contentBuf.toString();
		return valueStr;
	}

	private void extractAndCreateDomains(Long meaningId, List<Map<String, Object>> meaningDomains, List<Node> domainNodes, String domainOrigin) throws Exception {

		if (domainNodes == null) {
			return;
		}
		List<String> domainCodes = new ArrayList<>();
		String domainCode;

		for (Node domainNode : domainNodes) {
			domainCode = ((Element) domainNode).getTextTrim();
			int listingDelimCount = StringUtils.countMatches(domainCode, listingsDelimiter);
			if (listingDelimCount == 0) {
				Map<String, Object> meaningDomain = createDomain(meaningId, domainCode, domainOrigin, domainCodes);
				if (meaningDomain != null) {
					meaningDomains.add(meaningDomain);
				}
			} else {
				List<Map<String, Object>> inlineMeaningDomains = handleDomainListing(meaningId, domainCode, domainOrigin, domainCodes);
				if (CollectionUtils.isNotEmpty(inlineMeaningDomains)) {
					meaningDomains.addAll(inlineMeaningDomains);
				}
			}
		}
	}

	private List<Map<String, Object>> handleDomainListing(Long meaningId, String domainCodeListing, String domainOrigin, List<String> domainCodes) throws Exception {

		List<Map<String, Object>> meaningDomains = new ArrayList<>();
		String[] separateDomainCodes = StringUtils.split(domainCodeListing, listingsDelimiter);
		for (String separateDomainCode : separateDomainCodes) {
			Map<String, Object> meaningDomain = createDomain(meaningId, separateDomainCode, domainOrigin, domainCodes);
			if (meaningDomain != null) {
				meaningDomains.add(meaningDomain);
			}
		}
		return meaningDomains;
	}

	private Map<String, Object> createDomain(Long meaningId, String domainCode, String domainOrigin, List<String> domainCodes) throws Exception {

		if (domainCodes.contains(domainCode)) {
			return null;
		}
		Map<String, Object> meaningDomain = new OrderedMap<>();
		meaningDomain.put("meaning_id", meaningId);
		meaningDomain.put("domain_origin", domainOrigin);
		meaningDomain.put("domain_code", domainCode);
		domainCodes.add(domainCode);
		return meaningDomain;
	}

	private boolean extractPublicity(Node conceptGroupNode) {

		Element valueNode;
		String valueStr;

		valueNode = (Element) conceptGroupNode.selectSingleNode(entryClassExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (StringUtils.equals(valueStr, processStateCodePublic)) {
				return PUBLICITY_PUBLIC;
			}
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(processStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (StringUtils.equals(valueStr, processStateCodePublic)) {
				return PUBLICITY_PUBLIC;
			}
		}

		return PUBLICITY_PRIVATE;
	}

	private String extractConceptWordType(Node conceptGroupNode) {

		Element valueNode = (Element) conceptGroupNode.selectSingleNode(meaningTypeExp);
		if (valueNode != null) {
			String valueStr = valueNode.getTextTrim();
			if (wordTypeCodes.containsKey(valueStr)) {
				String wordType = wordTypeCodes.get(valueStr);
				return wordType;
			} else {
				logger.warn("Incorrect word type reference: \"{}\"", valueStr);
			}
		}
		return null;
	}

	private String unifyLang(String lang) {
		if (StringUtils.isBlank(lang)) {
			return null;
		}
		Locale locale = new Locale(lang);
		lang = locale.getISO3Language();
		return lang;
	}

	private void extractAndCreateDefinitionsAndNotes(
			Long meaningId,
			List<Map<String, Object>> definitions,
			Node langGroupNode,
			String lang,
			String concept,
			Map<String, Long> sourceIdMap) throws Exception {

		List<Node> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Node> definitionNoteNodes = langGroupNode.selectNodes(noteExp);

		for (Node definitionNode : definitionNodes) {
			List<Content> wrappedDefinitions = extractContentAndRefs(definitionNode, concept, lang, true);
			List<Map<String, Object>> nodeDefinitions = createDefinitionsAndSourceLinks(meaningId, wrappedDefinitions, "*", sourceIdMap);
			definitions.addAll(nodeDefinitions);
			for (Map<String, Object> nodeDefinition : nodeDefinitions) {
				Long definitionId = (Long) nodeDefinition.get("id");
				List<Map<String, Object>> definitionFreeforms = new ArrayList<>();
				nodeDefinition.put("definition_freeform", definitionFreeforms);
				for (Node definitionNoteNode : definitionNoteNodes) {
					String definitionNote = ((Element) definitionNoteNode).getTextTrim();
					Map<String, Object> definitionFreeform = createDefinitionFreeform(definitionId, FreeformType.NOTE, definitionNote, true);
					definitionFreeforms.add(definitionFreeform);
					if (((Element) definitionNoteNode).hasMixedContent()) {
						@SuppressWarnings("unchecked")
						Map<String, Object> freeform = (Map<String, Object>) definitionFreeform.get("freeform");
						definitionNote = handleFreeformTextSourceLinks(SourceOwner.NOTE, definitionNoteNode, freeform, sourceIdMap);
						updateFreeformText(freeform, definitionNote);
					}
				}
			}
		}
	}

	private List<Content> extractContentAndRefs(Node rootContentNode, String term, String lang, boolean logWarrnings) throws Exception {

		List<Content> contentList = new ArrayList<>();
		Iterator<Node> contentNodeIter = ((Element) rootContentNode).nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;
		Content contentObj = null;
		Ref refObj = null;
		boolean isRefOn = false;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				valueStr = StringUtils.replaceChars(valueStr, '\n', ' ');
				valueStr = StringUtils.trim(valueStr);
				boolean isListing = loaderHelper.isListing(valueStr);
				boolean isRefEnd = loaderHelper.isRefEnd(valueStr);
				boolean isValued = StringUtils.isNotEmpty(valueStr);
				String content = loaderHelper.getContent(valueStr);
				boolean contentExists = StringUtils.isNotBlank(content);
				if (isListing) {
					continue;
				}
				if (!isRefOn && isRefEnd && logWarrnings) {
					logger.warn("Illegal ref end notation @ \"{}\" : {}", term, rootContentNode.asXML());
				}
				if (isRefOn && isValued) {
					String minorRef;
					if (isRefEnd) {
						minorRef = loaderHelper.collectMinorRef(valueStr);
					} else {
						minorRef = loaderHelper.cleanupResidue(valueStr);
					}
					if (StringUtils.isNotBlank(minorRef)) {
						refObj.setMinorRef(minorRef);
					}
				}
				if (contentExists) {
					if (contentObj == null) {
						contentObj = newContent(lang, content);
						contentList.add(contentObj);
					} else if (!isRefOn) {
						content = contentObj.getValue() + '\n' + content;
						contentObj.setValue(content);
					} else {
						contentObj = newContent(lang, content);
						contentList.add(contentObj);
					}
				}
				isRefOn = false;
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					String sourceName;
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						sourceName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
					} else {
						sourceName = elemContentNode.getTextTrim();
					}

					if (contentObj == null) {
						contentObj = newContent(lang, EMPTY_CONTENT);
						contentList.add(contentObj);
						if (logWarrnings) {
							logger.warn("Source reference for empty content @ \"{}\"-\"{}\"", term, sourceName);
						}
					}
					isRefOn = true;
					ReferenceType refType;
					if (StringUtils.equalsIgnoreCase(refTypeExpert, sourceName)) {
						refType = ReferenceType.EXPERT;
					} else if (StringUtils.equalsIgnoreCase(refTypeQuery, sourceName)) {
						refType = ReferenceType.QUERY;
					} else {
						refType = ReferenceType.ANY;
					}
					refObj = new Ref();
					refObj.setMajorRef(sourceName);
					refObj.setType(refType);
					contentObj.getRefs().add(refObj);
				}
			}
		}
		return contentList;
	}

	private Content newContent(String lang, String content) {

		Content contentObj;
		contentObj = new Content();
		contentObj.setValue(content);
		contentObj.setLang(lang);
		contentObj.setRefs(new ArrayList<>());
		return contentObj;
	}

	private void extractAndCreateDefinitionsAndSourceLinks(
			Long meaningId,
			List<Map<String, Object>> definitions,
			Node termGroupNode,
			String term,
			String lang,
			Map<String, Long> sourceIdMap) throws Exception {

		List<Node> definitionNodes = termGroupNode.selectNodes(definitionExp);
		for (Node definitionNode : definitionNodes) {
			List<Content> wrappedDefinitions = extractContentAndRefs(definitionNode, term, lang, true);
			List<Map<String, Object>> nodeDefinitions = createDefinitionsAndSourceLinks(meaningId, wrappedDefinitions, "*", sourceIdMap);
			definitions.addAll(nodeDefinitions);
		}
	}

	private List<Map<String, Object>> createDefinitionsAndSourceLinks(Long meaningId, List<Content> wrappedDefinitions, String term, Map<String, Long> sourceIdMap) throws Exception {

		List<Map<String, Object>> definitions = new ArrayList<>();

		for (Content definitionObj : wrappedDefinitions) {

			Long definitionId = definitionIdSeq.increment();
			definitionObj.setId(definitionId);
			String value = definitionObj.getValue();
			String lang = definitionObj.getLang();
			String valueClean = textDecorationService.removeEkiEntityMarkup(value);
			valueClean = textDecorationService.removeEkiElementMarkup(valueClean);
			String valuePrese = textDecorationService.convertEkiEntityMarkup(value);

			Map<String, Object> definitionDataset = new OrderedMap<>();
			definitionDataset.put("definition_id", definitionId);
			definitionDataset.put("dataset_code", getDatasetCode());
			List<Map<String, Object>> definitionDatasets = new ArrayList<>();
			definitionDatasets.add(definitionDataset);

			List<Map<String, Object>> definitionSourceLinks = new ArrayList<>();
			List<Ref> refs = definitionObj.getRefs();
			for (Ref ref : refs) {
				Map<String, Object> definitionSourceLink = createSourceLink(SourceOwner.DEFINITION, definitionId, ref, term, sourceIdMap);
				if (definitionSourceLink != null) {
					definitionSourceLinks.add(definitionSourceLink);
				}
			}

			Map<String, Object> definition = new OrderedMap<>();
			definition.put("id", definitionId);
			definition.put("meaning_id", meaningId);
			definition.put("definition_type_code", DEFAULT_DEFINITION_TYPE_CODE);
			definition.put("value", valueClean);
			definition.put("value_prese", valuePrese);
			definition.put("lang", lang);
			definition.put("complexity", getComplexityValue());
			definition.put("definition_dataset", definitionDatasets);
			definition.put("definition_source_link", definitionSourceLinks);
			definitions.add(definition);
		}

		return definitions;
	}

	private Map<String, Object> createMeaningFreeform(Long meaningId, FreeformType freeformType, Object value, boolean isPublic) throws Exception {

		Map<String, Object> freeform = createFreeformTextOrTimestamp(freeformType, value, null, isPublic);
		if (freeform == null) {
			return null;
		}
		Long id = meaningFreeformIdSeq.increment();
		Long freeformId = (Long) freeform.get("id");
		Map<String, Object> meaningFreeform = new OrderedMap<>();
		meaningFreeform.put("id", id);
		meaningFreeform.put("meaning_id", meaningId);
		meaningFreeform.put("freeform_id", freeformId);
		meaningFreeform.put("freeform", freeform);

		return meaningFreeform;
	}

	private Map<String, Object> createDefinitionFreeform(Long definitionId, FreeformType freeformType, Object value, boolean isPublic) throws Exception {

		Map<String, Object> freeform = createFreeformTextOrTimestamp(freeformType, value, null, isPublic);
		if (freeform == null) {
			return null;
		}

		Long id = definitionFreeformIdSeq.increment();
		Long freeformId = (Long) freeform.get("id");
		Map<String, Object> definitionFreeform = new OrderedMap<>();
		definitionFreeform.put("id", id);
		definitionFreeform.put("definition_id", definitionId);
		definitionFreeform.put("freeform_id", freeformId);
		definitionFreeform.put("freeform", freeform);

		return definitionFreeform;
	}

	private Map<String, Object> createLexemeFreeform(Long lexemeId, FreeformType freeformType, Object value, String lang, boolean isPublic) throws Exception {

		Map<String, Object> freeform = createFreeformTextOrTimestamp(freeformType, value, lang, isPublic);
		if (freeform == null) {
			return null;
		}

		Long id = lexemeFreeformIdSeq.increment();
		Long freeformId = (Long) freeform.get("id");
		Map<String, Object> lexemeFreeform = new OrderedMap<>();
		lexemeFreeform.put("id", id);
		lexemeFreeform.put("lexeme_id", lexemeId);
		lexemeFreeform.put("freeform_id", freeformId);
		lexemeFreeform.put("freeform", freeform);

		return lexemeFreeform;
	}

	private Map<String, Object> createFreeformTextOrTimestamp(FreeformType freeformType, Object value, String lang, boolean isPublic) throws Exception {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			String valueStr = (String) value;
			return createFreeformTextEkiMarkup(null, freeformType, valueStr, lang, isPublic);
		} else if (value instanceof Timestamp) {
			Timestamp valueTs = (Timestamp) value;
			return createFreeformDate(null, freeformType, valueTs, isPublic);
		}
		return null;
	}

	private Map<String, Object> createFreeformTextEkiMarkup(Long parentId, FreeformType freeformType, String value, String lang, boolean isPublic) throws Exception {

		Long id = freeformIdSeq.increment();
		Map<String, Object> freeform = new OrderedMap<>();
		freeform.put("id", id);
		freeform.put("type", freeformType.name());
		if (parentId != null) {
			freeform.put("parent_id", parentId);
		}
		String valueStr = (String) value;
		String valueClean = textDecorationService.removeEkiEntityMarkup(valueStr);
		String valuePrese = textDecorationService.convertEkiEntityMarkup(valueStr);
		freeform.put("value_text", valueClean);
		freeform.put("value_prese", valuePrese);
		if (StringUtils.isNotBlank(lang)) {
			freeform.put("lang", lang);
		}
		freeform.put("complexity", getComplexityValue());
		freeform.put("is_public", isPublic);
		return freeform;
	}

	private Map<String, Object> createFreeformDate(Long parentId, FreeformType freeformType, Timestamp value, boolean isPublic) throws Exception {

		Long id = freeformIdSeq.increment();
		Map<String, Object> freeform = new OrderedMap<>();
		freeform.put("id", id);
		freeform.put("type", freeformType.name());
		if (parentId != null) {
			freeform.put("parent_id", parentId);
		}
		freeform.put("value_date", value);
		freeform.put("complexity", getComplexityValue());
		freeform.put("is_public", isPublic);
		return freeform;
	}

	private void updateFreeformText(Map<String, Object> freeform, String value) throws Exception {

		String valueClean = textDecorationService.removeEkiEntityMarkup(value);
		valueClean = textDecorationService.removeEkiElementMarkup(valueClean);
		String valuePrese = textDecorationService.convertEkiEntityMarkup(value);

		freeform.put("value_text", valueClean);
		freeform.put("value_prese", valuePrese);
	}

	private Map<String, Object> createSourceLink(SourceOwner sourceOwner, Long ownerId, Ref ref, String term, Map<String, Long> sourceIdMap) throws Exception {

		String minorRef = ref.getMinorRef();
		String majorRef = ref.getMajorRef();
		ReferenceType refType = ref.getType();
		Long sourceId = sourceIdMap.get(majorRef);
		if (sourceId == null) {
			return null;
		}
		if (StringUtils.equalsIgnoreCase(refTypeExpert, majorRef)) {
			majorRef = minorRef;
			minorRef = null;
		}
		if (StringUtils.equalsIgnoreCase(refTypeQuery, majorRef)) {
			majorRef = minorRef;
			minorRef = null;
		}
		if (StringUtils.isBlank(majorRef)) {
			majorRef = "?";
		}
		if (SourceOwner.LEXEME.equals(sourceOwner)) {
			return createLexemeSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		} else if (SourceOwner.DEFINITION.equals(sourceOwner)) {
			return createDefinitionSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		} else if (SourceOwner.USAGE.equals(sourceOwner)) {
			return createFreeformSourceLink(ownerId, refType, sourceId, minorRef, majorRef);
		}
		return null;
	}

	private Map<String, Object> createLexemeSourceLink(Long lexemeId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Long id = lexemeSourceLinkIdSeq.increment();
		Map<String, Object> lexemeSourceLink = new OrderedMap<>();
		lexemeSourceLink.put("id", id);
		lexemeSourceLink.put("lexeme_id", lexemeId);
		lexemeSourceLink.put("type", refType.name());
		lexemeSourceLink.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			lexemeSourceLink.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			lexemeSourceLink.put("value", value);
		}
		return lexemeSourceLink;
	}

	private Map<String, Object> createDefinitionSourceLink(Long definitionId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Long id = definitionSourceLinkIdSeq.increment();
		Map<String, Object> definitionSourceLink = new OrderedMap<>();
		definitionSourceLink.put("id", id);
		definitionSourceLink.put("definition_id", definitionId);
		definitionSourceLink.put("type", refType.name());
		definitionSourceLink.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			definitionSourceLink.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			definitionSourceLink.put("value", value);
		}
		return definitionSourceLink;
	}

	private Map<String, Object> createFreeformSourceLink(Long freeformId, ReferenceType refType, Long sourceId, String name, String value) throws Exception {

		Long id = freeformSourceLinkIdSeq.increment();
		Map<String, Object> freeformSourceLink = new OrderedMap<>();
		freeformSourceLink.put("id", id);
		freeformSourceLink.put("freeform_id", freeformId);
		freeformSourceLink.put("type", refType.name());
		freeformSourceLink.put("source_id", sourceId);
		if (StringUtils.isNotBlank(name)) {
			freeformSourceLink.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			freeformSourceLink.put("value", value);
		}
		return freeformSourceLink;
	}

	private Integer getHomonymNr(String term, String lang) {
		String termLangKey = term + "|" + lang;
		Count homonymNrCount = homonymNrMap.get(termLangKey);
		Integer homonymNr;
		if (homonymNrCount == null) {
			homonymNr = 1;
			homonymNrCount = new Count(homonymNr);
			homonymNrMap.put(termLangKey, homonymNrCount);
		} else {
			homonymNr = Math.toIntExact(homonymNrCount.increment());
		}
		return homonymNr;
	}

	private AffixoidData getAffixoidData(String wordValue) {

		if (StringUtils.endsWith(wordValue, UNIFIED_AFIXOID_SYMBOL)) {
			String cleanWordValue = StringUtils.removeEnd(wordValue, UNIFIED_AFIXOID_SYMBOL);
			return new AffixoidData(wordValue, cleanWordValue, PREFIXOID_WORD_TYPE_CODE, true, false);
		}
		if (StringUtils.startsWith(wordValue, UNIFIED_AFIXOID_SYMBOL)) {
			String cleanWordValue = StringUtils.removeStart(wordValue, UNIFIED_AFIXOID_SYMBOL);
			return new AffixoidData(wordValue, cleanWordValue, SUFFIXOID_WORD_TYPE_CODE, false, true);
		}
		return new AffixoidData(wordValue, wordValue, null, false, false);
	}

	private void handleAffixoidClassifiers(List<String> wordTypeCodes, AffixoidData affixoidData) {

		if (!affixoidData.isPrefixoid() && !affixoidData.isSuffixoid()) {
			return;
		}
		if (affixoidData.isPrefixoid()) {
			if (wordTypeCodes == null) {
				wordTypeCodes = new ArrayList<>();
			}
			if (!wordTypeCodes.contains(PREFIXOID_WORD_TYPE_CODE)) {
				wordTypeCodes.add(PREFIXOID_WORD_TYPE_CODE);
			}
		}
		if (affixoidData.isSuffixoid()) {
			if (wordTypeCodes == null) {
				wordTypeCodes = new ArrayList<>();
			}
			if (!wordTypeCodes.contains(SUFFIXOID_WORD_TYPE_CODE)) {
				wordTypeCodes.add(SUFFIXOID_WORD_TYPE_CODE);
			}
		}
	}

	private List<String> extractTermGroupWordTypeCode(Node termGroupNode, String conceptWordTypeCode) {

		List<String> termWordTypeCodes = new ArrayList<>();
		if (StringUtils.isNotBlank(conceptWordTypeCode)) {
			termWordTypeCodes.add(conceptWordTypeCode);
		}

		Element valueNode = (Element) termGroupNode.selectSingleNode(valueStateExp);
		if (valueNode != null) {
			String valueStr = valueNode.getTextTrim();
			if (wordTypeCodes.containsKey(valueStr)) {
				String termWordTypeCode = wordTypeCodes.get(valueStr);
				termWordTypeCodes.add(termWordTypeCode);
			} else if (lexemeValueStateCodes.containsKey(valueStr)) {
				// ok then, handled elsewhere
			}
		}
		return termWordTypeCodes;
	}

	private void createWordWordTypes(Map<String, Object> word, List<String> termWordTypeCodes) {

		Long wordId = (Long) word.get("id");
		if (CollectionUtils.isNotEmpty(termWordTypeCodes)) {
			List<Map<String, Object>> wordWordTypes = new ArrayList<>();
			for (String wordTypeCode : termWordTypeCodes) {
				Map<String, Object> wordType = new OrderedMap<>();
				wordType.put("word_id", wordId);
				wordType.put("word_type_code", wordTypeCode);
				wordWordTypes.add(wordType);
			}
			word.put("word_word_type", wordWordTypes);
		}
	}

	private String extractValueStateCode(Node termGroupNode) throws Exception {

		Element valueNode = (Element) termGroupNode.selectSingleNode(valueStateExp);
		if (valueNode != null) {
			String valueStr = valueNode.getTextTrim();
			if (lexemeValueStateCodes.containsKey(valueStr)) {
				String mappedValueStr = lexemeValueStateCodes.get(valueStr);
				return mappedValueStr;
			} else if (wordTypeCodes.containsKey(valueStr)) {
				// ok then, handled elsewhere
			} else {
				logger.warn("Incorrect lexeme value state or word type reference: \"{}\"", valueStr);
			}
		}
		return null;
	}

	private void extractAndCreateLexemeNotesAndSourceLinks(
			Long lexemeId,
			List<Map<String, Object>> lexemeFreeforms,
			Node termGroupNode,
			String term,
			String lang,
			Map<String, Long> sourceIdMap) throws Exception {

		List<Node> valueNodes = termGroupNode.selectNodes(noteExp);
		for (Node valueNode : valueNodes) {
			String valueStr = ((Element) valueNode).getTextTrim();
			Map<String, Object> lexemeFreeform = createLexemeFreeform(lexemeId, FreeformType.NOTE, valueStr, lang, true);
			lexemeFreeforms.add(lexemeFreeform);
			if (((Element) valueNode).hasMixedContent()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> freeform = (Map<String, Object>) lexemeFreeform.get("freeform");
				valueStr = handleFreeformTextSourceLinks(SourceOwner.NOTE, valueNode, freeform, sourceIdMap);
				updateFreeformText(freeform, valueStr);
			}
		}
	}

	private void extractAndCreateUsagesAndSourceLinks(
			Long lexemeId,
			List<Map<String, Object>> lexemeFreeforms,
			Node termGroupNode,
			String term,
			String lang,
			Map<String, Long> sourceIdMap) throws Exception {

		List<Node> valueNodes = termGroupNode.selectNodes(usageExp);
		if (CollectionUtils.isNotEmpty(valueNodes)) {
			for (Node usageNode : valueNodes) {
				List<Content> wrappedUsages = extractContentAndRefs(usageNode, term, lang, true);
				for (Content usageObj : wrappedUsages) {
					String valueStr = usageObj.getValue();
					List<Ref> refs = usageObj.getRefs();
					Map<String, Object> lexemeFreeform = createLexemeFreeform(lexemeId, FreeformType.USAGE, valueStr, lang, true);
					if (lexemeFreeform != null) {
						lexemeFreeforms.add(lexemeFreeform);
						@SuppressWarnings("unchecked")
						Map<String, Object> freeform = (Map<String, Object>) lexemeFreeform.get("freeform");
						List<Map<String, Object>> freeformSourceLinks = new ArrayList<>();
						freeform.put("freeform_source_link", freeformSourceLinks);
						Long usageId = (Long) freeform.get("id");
						for (Ref ref : refs) {
							Map<String, Object> usageSourceLink = createSourceLink(SourceOwner.USAGE, usageId, ref, term, sourceIdMap);
							if (usageSourceLink != null) {
								freeformSourceLinks.add(usageSourceLink);
							}
						}
					}
				}
			}
		}
	}

	private void extractAndCreateLexemeSourceLinks(
			Long lexemeId,
			List<Map<String, Object>> lexemeSourceLinks,
			Node termGroupNode,
			String term,
			String lang,
			Map<String, Long> sourceIdMap) throws Exception {

		List<Node> valueNodes = termGroupNode.selectNodes(sourceExp);
		for (Node sourceNode : valueNodes) {
			List<Content> wrappedSources = extractContentAndRefs(sourceNode, term, lang, false);
			for (Content sourceObj : wrappedSources) {
				List<Ref> refs = sourceObj.getRefs();
				for (Ref ref : refs) {
					Map<String, Object> lexemeSourceLink = createSourceLink(SourceOwner.LEXEME, lexemeId, ref, term, sourceIdMap);
					if (lexemeSourceLink != null) {
						lexemeSourceLinks.add(lexemeSourceLink);
					}
				}
			}
		}
	}

	enum SourceOwner {
		LEXEME, DEFINITION, USAGE, NOTE
	}
}
