package eki.ekilex.service.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

// TODO should be removed and replaced by specific ORM services
@Component
public class BasicDbService {

	@Autowired
	private NamedParameterJdbcTemplate mainNamedParameterJdbcTemplate;

	public <T> List<T> getResults(String sqlQueryStr, Map<String, Object> paramMap, RowMapper<T> rowMapper) throws Exception {

		List<T> results = mainNamedParameterJdbcTemplate.query(sqlQueryStr, paramMap, rowMapper);
		return results;
	}

	public int executeScript(String sqlScript) {

		Map<String, ?> paramMap = new HashMap<>();
		return mainNamedParameterJdbcTemplate.update(sqlScript, paramMap);
	}

	public int executeScript(String sqlScript, Map<String, Object> paramMap) {

		return mainNamedParameterJdbcTemplate.update(sqlScript, paramMap);
	}

	public <T> List<T> queryList(String sqlScript, Map<String, ?> paramMap, Class<T> fieldType) {
		List<T> results = mainNamedParameterJdbcTemplate.queryForList(sqlScript, paramMap, fieldType);
		return results;
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = mainNamedParameterJdbcTemplate.queryForList(sqlScript, paramMap);
		return results;
	}

	public Map<String, Object> queryForMap(String sqlQueryStr, Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result;
		try {
			result = mainNamedParameterJdbcTemplate.queryForMap(sqlQueryStr, paramMap);
		} catch (EmptyResultDataAccessException e) {
			result = null;
		}
		return result;
	}

	public Map<String, Object> select(String tableName, Map<String, Object> paramMap) throws Exception {

		String sqlQueryStr = parseSelectSql(tableName, paramMap);
		return queryForMap(sqlQueryStr, paramMap);
	}

	public List<Map<String, Object>> selectAll(String tableName, Map<String, Object> paramMap) throws Exception {

		String sqlQueryStr = parseSelectSql(tableName, paramMap);

		List<Map<String, Object>> result;
		try {
			result = mainNamedParameterJdbcTemplate.queryForList(sqlQueryStr, paramMap);
		} catch (EmptyResultDataAccessException e) {
			result = Collections.emptyList();
		}
		return result;
	}

	private String parseSelectSql(String tableName, Map<String, Object> paramMap) {
		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		StringBuffer sqlScriptBuf = new StringBuffer();
		sqlScriptBuf.append("select * from ");
		sqlScriptBuf.append(tableName);
		if (!paramMap.isEmpty()) {
			sqlScriptBuf.append(" where ");
			for (int fieldIndex = 0; fieldIndex < fieldNames.size(); fieldIndex++) {
				if (fieldIndex > 0) {
					sqlScriptBuf.append(" and ");
				}
				String fieldName = fieldNames.get(fieldIndex);
				sqlScriptBuf.append(fieldName);
				sqlScriptBuf.append(" = :");
				sqlScriptBuf.append(fieldName);
			}
		}

		return sqlScriptBuf.toString();
	}

	public boolean exists(String tableName, Map<String, Object> paramMap) {

		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		List<String> paramNames = new ArrayList<>();
		for (String fieldName : fieldNames) {
			paramNames.add(":" + fieldName);
		}
		StringBuffer sqlQueryBuf = new StringBuffer();
		sqlQueryBuf.append("select count(*) > 0 from ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" where ");
		for (int fieldIndex = 0; fieldIndex < fieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(" and ");
			}
			String fieldName = fieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(fieldName);
		}
		String sqlQueryStr = sqlQueryBuf.toString();

		boolean exists = mainNamedParameterJdbcTemplate.queryForObject(sqlQueryStr, paramMap, boolean.class);
		return exists;
	}

	public Long create(String tableName) throws Exception {

		String sql = "insert into " + tableName + " (id) values (default) returning id";
		Long id = mainNamedParameterJdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
		return id;
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

		Long id = mainNamedParameterJdbcTemplate.queryForObject(sqlQueryStr, paramMap, Long.class);

		return id;
	}

	public void createWithoutId(String tableName, Map<String, Object> paramMap) throws Exception {

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
		sqlQueryBuf.append(")");

		String sqlQueryStr = sqlQueryBuf.toString();

		mainNamedParameterJdbcTemplate.update(sqlQueryStr, paramMap);
	}

	public int delete(String tableName, Long id) {

		String sqlQueryStr = "delete from " + tableName + " where id = :id";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);
		return mainNamedParameterJdbcTemplate.update(sqlQueryStr, paramMap);
	}
}
