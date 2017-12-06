package eki.ekilex.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eki.ekilex.constant.SystemConstant.CSV_SEPARATOR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

@Service
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiService implements InitializingBean {

	private static final String SQL_SELECT_TERMS = "sql/select_termeki_terms.sql";

	private static final String SQL_SELECT_DEFINITIONS = "sql/select_termeki_definitions.sql";

	private static final String SQL_SELECT_DOMAINS = "sql/select_termeki_domains.sql";

	private static final String TERMBASE_IDS = "csv/termeki-databases.csv";

	private static Logger logger = LoggerFactory.getLogger(TermekiService.class);

	private String sqlSelectTerms;

	private String sqlSelectDefinitions;

	private String sqlSelectDomains;

	private List<Integer> termbaseIds;

	@Autowired @Qualifier(value = "jdbcTemplateTermeki")
	protected NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {

		sqlSelectTerms = getContent(SQL_SELECT_TERMS);
		sqlSelectDefinitions = getContent(SQL_SELECT_DEFINITIONS);
		sqlSelectDomains = getContent(SQL_SELECT_DOMAINS);
		termbaseIds = readFileLines(TERMBASE_IDS).stream()
				.map(l -> Integer.parseInt(StringUtils.split(l, CSV_SEPARATOR)[0]))
				.collect(toList());
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results == null ? Collections.emptyList() : results;
	}

	public boolean hasTermDatabase(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		List<Map<String, Object>> result = queryList("select * from termeki_termbases where termbase_id=:baseId", params);
		if (!result.isEmpty()) {
			logger.debug("Connection success, termeki base {} : \"{}\".", baseId, result.get(0).get("termbase_name"));
		} else {
			logger.info("No termeki base with id found", baseId);
		}
		return !result.isEmpty();
	}

	public List<Map<String, Object>> getTerms(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectTerms, params);
	}

	public List<Map<String, Object>> getDefinitions(Integer baseId) {

		Map<String, Object> params = constructParameters(baseId);
		return queryList(sqlSelectDefinitions, params);
	}

	public List<Map<String, Object>> getDomainsForLanguage(String language) {

		Map<String, Object> params = new HashMap<>();
		params.put("lang", language);
		params.put("termbaseIds", termbaseIds);
		return queryList(sqlSelectDomains, params);
	}

	private Map<String, Object> constructParameters(Integer baseId) {
		Map<String, Object> params = new HashMap<>();
		params.put("baseId", baseId);
		return params;
	}

	private String getContent(String resourcePath) throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		try (InputStream resourceInputStream = classLoader.getResourceAsStream(resourcePath)) {
			return IOUtils.toString(resourceInputStream, UTF_8);
		}
	}
	private List<String> readFileLines(String resourcePath) throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		try (InputStream resourceInputStream = classLoader.getResourceAsStream(resourcePath)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

}
