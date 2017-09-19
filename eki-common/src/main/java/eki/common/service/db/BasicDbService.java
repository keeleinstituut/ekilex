package eki.common.service.db;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.data.PgTextArray;

@Component
public class BasicDbService extends AbstractDbService {

	public void executeScript(String sqlScript) {

		Map<String, ?> paramMap = new HashMap<>();
		jdbcTemplate.update(sqlScript, paramMap);
	}

	public Long create(String tableName, Map<String, Object> paramMap) throws Exception {

		//System.out.println(tableName + ": " + paramMap);

		for (Entry<String, Object> paramMapEntry : paramMap.entrySet()) {
			Object paramValue = paramMapEntry.getValue();
			if (paramValue instanceof String[]) {
				String[] paramValueArr = (String[]) paramValue;
				Array paramDbValue = new PgTextArray(paramValueArr);
				paramMapEntry.setValue(paramDbValue);
			} else {
				//other type of arrays here...
			}
		}

		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		List<String> paramNames = new ArrayList<>();
		for (String fieldName : fieldNames) {
			paramNames.add(":" + fieldName);
		}

		StringBuffer sqlScriptBuf = new StringBuffer();
		sqlScriptBuf.append("insert into ");
		sqlScriptBuf.append(tableName);
		sqlScriptBuf.append(" (");
		sqlScriptBuf.append(StringUtils.join(fieldNames, ", "));
		sqlScriptBuf.append(") values (");
		sqlScriptBuf.append(StringUtils.join(paramNames, ", "));
		sqlScriptBuf.append(") returning id");

		String sqlScript = sqlScriptBuf.toString();

		Long id = jdbcTemplate.queryForObject(sqlScript, paramMap, Long.class);

		return id;
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results;
	}
}
