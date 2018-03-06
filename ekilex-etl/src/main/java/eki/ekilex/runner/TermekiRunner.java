package eki.ekilex.runner;

import eki.common.constant.ContentKey;
import eki.common.constant.FreeformType;
import eki.common.constant.ReferenceType;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.TermekiService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	private static final String SQL_UPDATE_DOMAIN_DATSETS = "update " + DOMAIN + " set datasets = :datasets where code = :code and origin = :origin";

	private static final String defaultWordMorphCode = "SgN";
	private final static String LEXEME_RELATION_ABBREVIATION = "lyh";
	private static final String TERMEKI_CLASSIFIER_PRONUNCIATION = "termeki_pronunciation";
	private static final String TERMEKI_CLASSIFIER_WORD_CLASS = "termeki_word_class";

	private Map<String, String> posCodes;

	@Autowired
	private TermekiService termekiService;

	@Override
	void initialise() throws Exception {
		posCodes = loadClassifierMappingsFor(TERMEKI_CLASSIFIER_PRONUNCIATION);
		posCodes.putAll(loadClassifierMappingsFor(TERMEKI_CLASSIFIER_WORD_CLASS));
	}

	@Transactional
	public void execute(Integer baseId, String dataset) throws Exception {

		if (!hasTermDatabaseAndIsKnownDataset(baseId, dataset)) {
			return;
		}
		logger.debug("Start import from Termeki...");
		long t1, t2;
		t1 = System.currentTimeMillis();

		Context context = new Context();
		context.terms = termekiService.getTerms(baseId);
		logger.info("Found {} terms.", context.terms.size());
		context.definitions = termekiService.getDefinitions(baseId);
		logger.info("Found {} definitions.", context.definitions.size());
		context.sourceMapping = loadSources(baseId);
		logger.info("Found {} sources.", context.sourceMapping.size());
		context.comments = termekiService.getComments(baseId);
		logger.info("Found {} comments.", context.comments.size());
		context.abbreviations = loadAbbreviations(dataset);
		context.genuses = loadGenuses(dataset);
		context.families = loadFamilies(dataset);
		context.describers = loadDescribers(dataset);
		context.describingYears = loadDescribingYears(dataset);
		context.geolDomains = loadGeolDomains(dataset);
		doImport(context, dataset);
		updateDataset(baseId, dataset);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	@Transactional
	public void batchLoad(String termbasesCsvFilePath) throws Exception {

		List<String> lines = readFileLines(termbasesCsvFilePath);
		for (String line : lines) {
			String[] cells = StringUtils.split(line, CSV_SEPARATOR);
			if (cells.length > 1) {
				Integer termbaseId = Integer.parseInt(cells[0]);
				String dataset =  cells[1];
				execute(termbaseId, dataset);
			}
		}
	}

	private List<String> readFileLines(String resourcePath) throws Exception {
		try (InputStream resourceInputStream = new FileInputStream(resourcePath)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

	// abbreviations are present only in termbase 1283851 - Eesti E-tervise SA terminibaas (ett)
	private List<Map<String, Object>> loadAbbreviations(String dataset) {
		if ("ett".equals(dataset)) {
			List<Map<String, Object>> abbreviations = termekiService.getTermAttributes(38708);
			logger.info("Found {} abbreviations.", abbreviations.size());
			return abbreviations;
		} else {
			return emptyList();
		}
	}

	// genuses are present only in termbase 7351963 - Ihtüoloogia (iht)
	private List<Map<String, Object>> loadGenuses(String dataset) {
		if ("iht".equals(dataset)) {
			List<Map<String, Object>> attributes = termekiService.getConceptAttributes(41152);
			logger.info("Found {} genuses.", attributes.size());
			return attributes;
		} else {
			return emptyList();
		}
	}

	// families are present only in termbase 7351963 - Ihtüoloogia (iht)
	private List<Map<String, Object>> loadFamilies(String dataset) {
		if ("iht".equals(dataset)) {
			List<Map<String, Object>> attributes = termekiService.getConceptAttributes(41153);
			logger.info("Found {} families.", attributes.size());
			return attributes;
		} else {
			return emptyList();
		}
	}

	// describers are present only in termbase 7351963 - Ihtüoloogia (iht)
	private List<Map<String, Object>> loadDescribers(String dataset) {
		if ("iht".equals(dataset)) {
			List<Map<String, Object>> attributes = termekiService.getConceptAttributes(44274);
			logger.info("Found {} describers.", attributes.size());
			return attributes;
		} else {
			return emptyList();
		}
	}

	// describing years are present only in termbase 7351963 - Ihtüoloogia (iht)
	private List<Map<String, Object>> loadDescribingYears(String dataset) {
		if ("iht".equals(dataset)) {
			List<Map<String, Object>> attributes = termekiService.getConceptAttributes(44275);
			logger.info("Found {} describing years.", attributes.size());
			return attributes;
		} else {
			return emptyList();
		}
	}

	// in case of Geol, domains are stored as concept extended attributes, termbase 4441577 - Geol (get)
	private List<Map<String, Object>> loadGeolDomains(String dataset) {
		if ("get".equals(dataset)) {
			List<Map<String, Object>> attributes = termekiService.getConceptAttributes(44388);
			logger.info("Found {} domains.", attributes.size());
			return attributes;
		} else {
			return emptyList();
		}
	}

	private Map<Integer, SourceData> loadSources(Integer baseId) throws Exception {
		List<Map<String, Object>> sources = termekiService.getSources(baseId);
		Map<Integer, SourceData> termekiSourceToEkilexSourceMap = new HashMap<>();
		for (Map<String, Object> source : sources) {
			String name = (String)source.get("source_name");
			if (StringUtils.isNotBlank(name)) {
				Long sourceId = createSource(source.get("source_id").toString());
				Long sourceFreeformId = createSourceFreeform(sourceId, FreeformType.SOURCE_NAME, name);
				String author = (String) source.get("author");
				if (StringUtils.isNotBlank(author)) {
					createFreeformTextOrDate(FreeformType.SOURCE_AUTHOR, sourceFreeformId, author, null);
				}
				String isbn = (String) source.get("isbn");
				if (StringUtils.isNotBlank(isbn)) {
					createFreeformTextOrDate(FreeformType.SOURCE_ISBN, sourceFreeformId, isbn, null);
				}
				String www = (String) source.get("source_link");
				if (StringUtils.isNotBlank(www)) {
					createFreeformTextOrDate(FreeformType.SOURCE_WWW, sourceFreeformId, www, null);
				}
				String publisher = (String) source.get("publisher");
				if (StringUtils.isNotBlank(publisher)) {
					createFreeformTextOrDate(FreeformType.SOURCE_PUBLISHER, sourceFreeformId, publisher, null);
				}
				Date publishDate = (Date) source.get("publish_date");
				if (publishDate != null) {
					Timestamp publishedTs = new Timestamp(publishDate.getTime());
					createFreeformTextOrDate(FreeformType.SOURCE_PUBLICATION_YEAR, sourceFreeformId, publishedTs, null);
				}
				termekiSourceToEkilexSourceMap.put((Integer)source.get("source_id"), new SourceData(sourceId, name));
			}
		}
		return termekiSourceToEkilexSourceMap;
	}

	private Long createSource(String termekiId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("concept", termekiId);
		return basicDbService.create(SOURCE, params);
	}

	private void updateDataset(Integer baseId, String dataset) throws Exception {

		Map<String, Object> termbase = termekiService.getTermbase(baseId);
		Map<String, Object> params = new HashMap<>();
		params.put("code", dataset);
		Map<String, Object> values = new HashMap<>();
		values.put("description", termbase.get("description"));
		values.put("is_public", termbase.get("is_public"));
		basicDbService.update(DATASET, params, values);
	}

	private void doImport(
			Context context,
			String dataset) throws Exception {

		Count wordDuplicateCount = new Count();
		Map<Integer, Long> conceptMeanings = new HashMap<>();
		long count = 0;

		for (Map<String, Object> term : context.terms) {
			String language = unifyLang((String)term.get("lang"));
			String wordValue = (String)term.get("term");
			int homonymNr = getWordMaxHomonymNr(wordValue, language) + 1;
			Word word = new Word(wordValue,language, null, null, null, null, homonymNr, defaultWordMorphCode, null);
			String genderCode = (String)term.get("gender");
			if (StringUtils.isNotBlank(genderCode)) {
				word.setGenderCode(genderCode);
			}
			Long wordId = saveWord(word, null, null, wordDuplicateCount);

			Integer conceptId = (Integer) term.get("concept_id");
			if (!conceptMeanings.containsKey(conceptId)) {
				Long meaningId = createMeaning();
				createMeaningFreeform(meaningId, FreeformType.MEANING_EXTERNAL_ID, conceptId.toString());
				conceptMeanings.put(conceptId, meaningId);
				List<String> domainCodes = domainCodes(term, context, dataset);
				for (String domainCode : domainCodes) {
					Map<String, Object> domain = getDomain(domainCode, dataset);
					if (domain == null) {
						logger.info("Invalid domain code : {}", domainCode);
					} else {
						createMeaningDomain(meaningId, domainCode, dataset);
//						updateDomainDatsetsIfNeeded(domain, dataset);
					}
				}
				addMeaningFreeforms(context, conceptId, meaningId);
			}

			Long meaningId = conceptMeanings.get(conceptId);
			Lexeme lexeme = new Lexeme();
			lexeme.setWordId(wordId);
			lexeme.setMeaningId(meaningId);
			Long lexemeId = createLexeme(lexeme, dataset);
			String posCode = StringUtils.isNotBlank((String)term.get("pronunciation")) ? (String)term.get("pronunciation") : term.get("word_class").toString();
			savePosCode(lexemeId, posCode);
			Integer sourceId = (Integer) term.get("source_id");
			connectSourceToLexeme(sourceId, lexemeId, context.sourceMapping);
			Integer termId = (Integer) term.get("term_id");
			createAbbreviationIfNeeded(context, termId, meaningId, lexemeId, language, dataset, wordDuplicateCount);
			if (++count % 100 == 0) {
				System.out.print(".");
			}
		}
		System.out.println();
		logger.info("{} words imported", context.terms.size());
		logger.info("{} duplicate words found", wordDuplicateCount.getValue());
		logger.info("{} meanings created", conceptMeanings.size());

		int definitionsCount = 0;
		for (Map<String, Object> definition : context.definitions) {
			String language = unifyLang((String)definition.get("lang"));
			Integer conceptId = (Integer) definition.get("concept_id");
			if (conceptMeanings.containsKey(conceptId)) {
				Long meaningId = conceptMeanings.get(conceptId);
				String definitionValue = (String) definition.get("definition");
				Long definitionId = createDefinition(meaningId, definitionValue, language, dataset);
				definitionsCount++;
				String publicNote = (String)definition.get("description");
				if (isNotBlank(publicNote)) {
					createMeaningFreeform(meaningId, FreeformType.PUBLIC_NOTE, publicNote);
				}
				Integer sourceId = (Integer) definition.get("source_id");
				connectSourceToDefinition(sourceId, definitionId, context.sourceMapping, definitionValue);
			}
		}
		logger.info("{} definitions created", definitionsCount);

		for (Map<String, Object> comment : context.comments) {
			Integer conceptId = (Integer) comment.get("concept_id");
			if (conceptMeanings.containsKey(conceptId)) {
				Long meaningId = conceptMeanings.get(conceptId);
				String privateNote = (String) comment.get("content");
				if (isNotBlank(privateNote)) {
					createMeaningFreeform(meaningId, FreeformType.PRIVATE_NOTE, privateNote);
				}
			}
		}
	}

	private List<String> domainCodes(Map<String, Object> term, Context context, String dataset) {

		List<String> codes = new ArrayList<>();
		if ("get".equals(dataset)) {
			Integer conceptId = (Integer) term.get("concept_id");
			Optional<Map<String, Object>> domainCode = context.geolDomains.stream().filter(d -> d.get("concept_id").equals(conceptId)).findFirst();
			if (domainCode.isPresent()) {
				String codeValue = cleanUpGeoDomainCodes((String)domainCode.get().get("attribute_value"));
				String[] codeValues = codeValue.split(",");
				for (String code: codeValues) {
					if (isNotBlank(code)) {
						codes.add(code);
					}
				}
			}
		} else {
			String code = (String)term.get("domain_code");
			if (isNotBlank(code)) {
				codes.add(code);
			}
		}
		return codes;
	}

	private String cleanUpGeoDomainCodes(String domainCodes) {
		String cleanedUp = domainCodes.toLowerCase()
				.replace("(paleo)", "pecol,")
				.replace("ecology", "ecol")
				.replace("cim", "clim")
				.replace("paleoecol", "pecol")
				.replace("glscio", "glacio")
				.replace("goechem", "geochem")
				.replace("hydrogeology", "hydro")
				.replace(" ", "")
				.replace("gen.gl", "gen. gl")
				.replace("gengl", "gen. gl")
				.replace("q", "Q")
				.replace(";", ",");
		if (cleanedUp.equals("eco")) {
			cleanedUp = "ecol";
		}
		return cleanedUp;
	}

	private void addMeaningFreeforms(Context context, Integer conceptId, Long meaningId) throws Exception {
		createMeaningFreeformOfType(FreeformType.GENUS, context.genuses, conceptId, meaningId);
		createMeaningFreeformOfType(FreeformType.FAMILY, context.families, conceptId, meaningId);
		createMeaningFreeformOfType(FreeformType.DESCRIBER, context.describers, conceptId, meaningId);
		createMeaningFreeformOfType(FreeformType.DESCRIBING_YEAR, context.describingYears, conceptId, meaningId);
	}

	private void createMeaningFreeformOfType(FreeformType freeformType, List<Map<String, Object>> items, Integer conceptId, Long meaningId) throws Exception {
		if (!items.isEmpty()) {
			Optional<Map<String, Object>> item = items.stream().filter(i -> i.get("concept_id").equals(conceptId)).findFirst();
			if (item.isPresent()) {
				createMeaningFreeform(meaningId, freeformType, item.get().get("attribute_value"));
			}
		}
	}

	private void createAbbreviationIfNeeded(
			Context context,
			Integer termId,
			Long meaningId,
			Long termLexemeId,
			String language,
			String dataset,
			Count wordDuplicateCount) throws Exception {

		if (context.abbreviations.isEmpty()) return;

		Optional<Map<String, Object>> abbreviation = context.abbreviations.stream().filter(a -> a.get("term_id").equals(termId)).findFirst();
		if (abbreviation.isPresent()) {
			String abbreviationValue = (String) abbreviation.get().get("attribute_value");
			int homonymNr = getWordMaxHomonymNr(abbreviationValue, language) + 1;
			Word word = new Word(abbreviationValue,language, null, null, null, null, homonymNr, defaultWordMorphCode, null);
			Long wordId = saveWord(word, null, null, wordDuplicateCount);
			Lexeme lexeme = new Lexeme();
			lexeme.setWordId(wordId);
			lexeme.setMeaningId(meaningId);
			Long abbreviationLexemeId = createLexeme(lexeme, dataset);
			createLexemeRelation(abbreviationLexemeId, termLexemeId, LEXEME_RELATION_ABBREVIATION);
		}
	}

	private void connectSourceToDefinition(Integer sourceId, Long definitionId, Map<Integer, SourceData> sourceMapping, String definition) throws Exception {

		if (sourceMapping.containsKey(sourceId)) {
			SourceData ekilexSource = sourceMapping.get(sourceId);
			Long refLinkId = createDefinitionRefLink(definitionId, ReferenceType.SOURCE, ekilexSource.id, null);
			String markdownLink = String.format("%s [%s](%s:%d)", definition, ekilexSource.name, ContentKey.DEFINITION_REF_LINK, refLinkId);
			updateDefinitionValue(definitionId, markdownLink);
		}
	}

	private void connectSourceToLexeme(Integer sourceId, Long lexemeId, Map<Integer, SourceData> sourceMapping) throws Exception {

		if (sourceMapping.containsKey(sourceId)) {
			SourceData ekilexSource = sourceMapping.get(sourceId);
			Long freeformId = createLexemeFreeform(lexemeId, FreeformType.SOURCE, null, null);
			Long refLinkId = createFreeformRefLink(freeformId, ReferenceType.SOURCE, ekilexSource.id, null);
			String markdownLink = String.format("[%s](%s:%d)", ekilexSource.name, ContentKey.FREEFORM_REF_LINK, refLinkId);
			updateFreeformText(freeformId, markdownLink);
		}
	}

	private void savePosCode(Long lexemeId, String posCode) throws Exception {

		if (posCodes.containsKey(posCode)) {
			Map<String, Object> params = new HashMap<>();
			params.put("lexeme_id", lexemeId);
			params.put("pos_code", posCodes.get(posCode));
			basicDbService.create(LEXEME_POS, params);
		}
	}

	private void updateDomainDatsetsIfNeeded(Map<String, Object> domain, String dataset) throws Exception {

		List<String> datasets = Arrays.asList((String[])((PgArray)domain.get("datasets")).getArray());
		if (!datasets.contains(dataset)) {
			List<String> updatedDataset = new ArrayList<>(datasets);
			updatedDataset.add(dataset);
			Map<String, Object> tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("code", domain.get("code"));
			tableRowParamMap.put("origin", domain.get("origin"));
			tableRowParamMap.put("datasets", new PgVarcharArray(updatedDataset.toArray(new String[updatedDataset.size()])));
			basicDbService.executeScript(SQL_UPDATE_DOMAIN_DATSETS, tableRowParamMap);
		}
	}

	private boolean hasTermDatabaseAndIsKnownDataset(Integer baseId, String dataset) throws Exception {
		return termekiService.hasTermDatabase(baseId) && isKnownDataset(dataset);
	}

	private Map<String, Object> getDomain(String code, String origin) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", code);
		tableRowParamMap.put("origin", origin);
		return basicDbService.select(DOMAIN, tableRowParamMap);
	}

	private boolean isKnownDataset(String dataset) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("code", dataset);
		Map<String, Object> selectedDataset = basicDbService.select(DATASET, params);
		if (selectedDataset == null) {
			logger.info("No dataset with id {} defined in EKILEX", dataset);
		} else {
			logger.info("Dataset {} : {}", dataset, selectedDataset.get("name"));
		}
		return selectedDataset != null;
	}

	private class SourceData {
		Long id;
		String name;

		SourceData(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private class Context {
		List<Map<String, Object>> terms;
		List<Map<String, Object>> definitions;
		Map<Integer, SourceData> sourceMapping;
		List<Map<String, Object>> comments;
		List<Map<String, Object>> abbreviations;
		List<Map<String, Object>> genuses;
		List<Map<String, Object>> families;
		List<Map<String, Object>> describers;
		List<Map<String, Object>> describingYears;
		List<Map<String, Object>> geolDomains;
	}
}
