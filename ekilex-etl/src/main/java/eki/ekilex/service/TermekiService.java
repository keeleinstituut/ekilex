package eki.ekilex.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transform.DatasetId;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiService implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(TermekiService.class);

	private static final String SQL_SELECT_TERMEKI_TERMBASES = "select * from termeki_termbases where termbase_id = :baseId";

	private static final String SQL_SELECT_TERMS = "sql/select_termeki_terms.sql";

	private static final String SQL_SELECT_DEFINITIONS = "sql/select_termeki_definitions.sql";

	private static final String SQL_SELECT_DOMAINS = "sql/select_termeki_domains.sql";

	private static final String SQL_SELECT_SOURCES = "sql/select_termeki_sources.sql";

	private static final String SQL_SELECT_COMMENTS = "sql/select_termeki_comments.sql";

	private static final String SQL_SELECT_TERM_ATTRIBUTES = "sql/select_termeki_term_attributes.sql";

	private static final String SQL_SELECT_CONCEPT_ATTRIBUTES = "sql/select_termeki_concept_attributes.sql";

	private static final String SQL_SELECT_EXAMPLES = "sql/select_termeki_examples.sql";

	private static final String SQL_SELECT_IMAGES = "select * from termeki_concept_images where concept_id in (select concept_id from termeki_concepts where termbase_id = :baseId)";

	private static final String SQL_SELECT_IMAGE_IDS = "select distinct image_id from termeki_concept_images";

	@Autowired
	private LoaderConfService loaderConfService;

	private String sqlSelectTerms;

	private String sqlSelectDefinitions;

	private String sqlSelectDomains;

	private String sqlSelectSources;

	private String sqlSelectComments;

	private String sqlSelectTermAttributes;

	private String sqlSelectConceptAttributes;

	private Map<Integer, String> termbaseIdMap;

	private String sqlSelectExamples;

	@Autowired
	@Qualifier(value = "jdbcTemplateTermeki")
	protected NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {

		sqlSelectTerms = getContent(SQL_SELECT_TERMS);
		sqlSelectDefinitions = getContent(SQL_SELECT_DEFINITIONS);
		sqlSelectDomains = getContent(SQL_SELECT_DOMAINS);
		sqlSelectSources = getContent(SQL_SELECT_SOURCES);
		sqlSelectComments = getContent(SQL_SELECT_COMMENTS);
		sqlSelectTermAttributes = getContent(SQL_SELECT_TERM_ATTRIBUTES);
		sqlSelectConceptAttributes = getContent(SQL_SELECT_CONCEPT_ATTRIBUTES);
		sqlSelectExamples = getContent(SQL_SELECT_EXAMPLES);
		termbaseIdMap = null;
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results == null ? Collections.emptyList() : results;
	}

	public boolean hasTermDatabase(Integer baseId) {

		Map<String, Object> termbase = getTermbase(baseId);
		if (termbase != null) {
			logger.debug("Connection success, termeki base {} : \"{}\".", baseId, termbase.get("termbase_name"));
		} else {
			logger.info("No termeki base with id found", baseId);
		}
		return termbase != null;
	}

	public List<Map<String, Object>> getTerms(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectTerms, params);
	}

	public List<Map<String, Object>> getSources(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectSources, params);
	}

	public List<Map<String, Object>> getDefinitions(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectDefinitions, params);
	}

	public List<Map<String, Object>> getComments(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectComments, params);
	}

	public List<Map<String, Object>> getExamples(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectExamples, params);
	}

	public List<Map<String, Object>> getImages(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(SQL_SELECT_IMAGES, params);
	}

	public List<Map<String, Object>> getDomainsForLanguage(String language) {

		Map<Integer, String> termbaseIds = getTermbaseIdMap();
		Map<String, Object> params = new HashMap<>();
		params.put("lang", language);
		params.put("termbaseIds", termbaseIds.keySet());
		List<Map<String, Object>> domains = queryList(sqlSelectDomains, params);
		domains.forEach(domain -> {
			Integer termBaseId = (Integer) domain.get("termbase_id");
			String termBaseCode = termbaseIds.get(termBaseId);
			domain.put("termbase_code", termBaseCode);
		});
		return domains;
	}

	public List<Map<String, Object>> getTermAttributes(Integer attributeId) {

		Map<String, Object> params = new HashMap<>();
		params.put("attributeId", attributeId);
		return queryList(sqlSelectTermAttributes, params);
	}

	public List<Map<String, Object>> getConceptAttributes(Integer attributeId) {

		Map<String, Object> params = new HashMap<>();
		params.put("attributeId", attributeId);
		return queryList(sqlSelectConceptAttributes, params);
	}

	public Collection<String> getTermbaseCodes() {
		return getTermbaseIdMap().values();
	}

	public Map<String, Object> getTermbase(Integer baseId) {
		Map<String, Object> params = constructParameters(baseId);
		List<Map<String, Object>> result = queryList(SQL_SELECT_TERMEKI_TERMBASES, params);
		return result.isEmpty() ? null : result.get(0);
	}

	public List<Map<String, Object>> getImageIds() {
		return queryList(SQL_SELECT_IMAGE_IDS, Collections.emptyMap());
	}

	private Map<String, Object> constructParameters(Integer baseId) {
		Map<String, Object> params = new HashMap<>();
		params.put("baseId", baseId);
		return params;
	}

	private Map<Integer, String> getTermbaseIdMap() {
		if (termbaseIdMap == null) {
			List<DatasetId> termekiDatasetIds = loaderConfService.getTermekiDatasetIds();
			termbaseIdMap = termekiDatasetIds.stream().collect(Collectors.toMap(DatasetId::getId, DatasetId::getDataset));
		}
		return termbaseIdMap;
	}

	private String getContent(String resourcePath) throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		try (InputStream resourceInputStream = classLoader.getResourceAsStream(resourcePath)) {
			return IOUtils.toString(resourceInputStream, UTF_8);
		}
	}

}
