package eki.common.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BasicDbService extends AbstractDbService {

	public void executeScript(String sqlScript) {

		Map<String, ?> paramMap = new HashMap<>();
		jdbcTemplate.update(sqlScript, paramMap);
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results;
	}
}
