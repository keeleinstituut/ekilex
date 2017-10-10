package eki.ekilex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TermekiService {

	private static Logger logger = LoggerFactory.getLogger(TermekiService.class);

	@Autowired @Qualifier(value = "jdbcTemplateTermeki")
	protected NamedParameterJdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results == null ? Collections.emptyList() : results;
	}

	public boolean hasTermDatabase(Integer baseId) {

		Map<String, Object> params = new HashMap<>();
		params.put("baseId", baseId);
		List<Map<String, Object>> result = queryList("select * from termeki_termbases where termbase_id=:baseId", params);
		if (!result.isEmpty()) {
			logger.debug("Connection success, termeki base \"{}\".", result.get(0).get("termbase_name"));
		} else {
			logger.info("No termeki base with id found", baseId);
		}
		return !result.isEmpty();
	}

}
