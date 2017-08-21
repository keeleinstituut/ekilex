package eki.common.service.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BasicDbService extends AbstractDbService {

	public void executeScript(String sqlScript) {

		Map<String, ?> paramMap = new HashMap<>();
		jdbcTemplate.update(sqlScript, paramMap);
	}
}
