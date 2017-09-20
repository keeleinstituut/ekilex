package eki.common.service.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class BasicDbService extends AbstractDbService {

	public void executeScript(String sqlScript) {

		Map<String, ?> paramMap = new HashMap<>();
		jdbcTemplate.update(sqlScript, paramMap);
	}

	public Map<String, Object> queryForMap(String sqlQueryStr, Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(sqlQueryStr, paramMap);
		} catch (EmptyResultDataAccessException e) {
			result = null;
		}
		return result;
	}

	public Map<String, Object> select(String tableName, Map<String, Object> paramMap) throws Exception {

		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		List<String> paramNames = new ArrayList<>();
		for (String fieldName : fieldNames) {
			paramNames.add(":" + fieldName);
		}

		StringBuffer sqlScriptBuf = new StringBuffer();
		sqlScriptBuf.append("select * from ");
		sqlScriptBuf.append(tableName);
		sqlScriptBuf.append(" where ");
		for (int fieldIndex = 0; fieldIndex < fieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlScriptBuf.append(" and ");
			}
			sqlScriptBuf.append(fieldNames.get(fieldIndex));
			sqlScriptBuf.append(" = ");
			sqlScriptBuf.append(paramNames.get(fieldIndex));
		}

		String sqlQueryStr = sqlScriptBuf.toString();

		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(sqlQueryStr, paramMap);
		} catch (EmptyResultDataAccessException e) {
			result = null;
		}
		return result;
	}

	public Long create(String tableName, Map<String, Object> paramMap) throws Exception {

		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		List<String> paramNames = new ArrayList<>();
		for (String fieldName : fieldNames) {
			paramNames.add(":" + fieldName);
		}

		StringBuffer sqlQueryBuf = new StringBuffer();
		sqlQueryBuf.append("insert into ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" (");
		sqlQueryBuf.append(StringUtils.join(fieldNames, ", "));
		sqlQueryBuf.append(") values (");
		sqlQueryBuf.append(StringUtils.join(paramNames, ", "));
		sqlQueryBuf.append(") returning id");

		String sqlQueryStr = sqlQueryBuf.toString();

		Long id = jdbcTemplate.queryForObject(sqlQueryStr, paramMap, Long.class);

		return id;
	}

	public Long createIfNotExists(String tableName, Map<String, Object> paramMap) throws Exception {

		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		List<String> paramNames = new ArrayList<>();
		for (String fieldName : fieldNames) {
			paramNames.add(":" + fieldName);
		}

		StringBuffer sqlQueryBuf = new StringBuffer();
		sqlQueryBuf.append("insert into ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" (");
		sqlQueryBuf.append(StringUtils.join(fieldNames, ", "));
		sqlQueryBuf.append(") select ");
		sqlQueryBuf.append(StringUtils.join(paramNames, ", "));
		sqlQueryBuf.append(" where not exists (select id from ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" where ");
		for (int fieldIndex = 0; fieldIndex < fieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(" and ");
			}
			sqlQueryBuf.append(fieldNames.get(fieldIndex));
			sqlQueryBuf.append(" = ");
			sqlQueryBuf.append(paramNames.get(fieldIndex));
		}
		sqlQueryBuf.append(") returning id");
		
		String sqlScript = sqlQueryBuf.toString();

		Long id;
		try {
			id = jdbcTemplate.queryForObject(sqlScript, paramMap, Long.class);
		} catch (EmptyResultDataAccessException e) {
			id = null;
		}
		return id;
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results;
	}
}
