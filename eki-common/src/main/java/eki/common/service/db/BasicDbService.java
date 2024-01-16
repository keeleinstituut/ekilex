package eki.common.service.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class BasicDbService extends AbstractDbService {

	public int executeScript(String sqlScript) {

		Map<String, ?> paramMap = new HashMap<>();
		return jdbcTemplate.update(sqlScript, paramMap);
	}

	public int executeScript(String sqlScript, Map<String, Object> paramMap) {

		return jdbcTemplate.update(sqlScript, paramMap);
	}

	public <T> List<T> queryList(String sqlScript, Map<String, ?> paramMap, Class<T> fieldType) {
		List<T> results = jdbcTemplate.queryForList(sqlScript, paramMap, fieldType);
		return results;
	}

	public List<Map<String, Object>> queryList(String sqlScript, Map<String, ?> paramMap) {

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlScript, paramMap);
		return results;
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

	// query string must project rows with names "key" and "value"
	public Map<String, String> queryListAsMap(String sqlQueryStr, Map<String, Object> paramMap) throws Exception {

		Map<String, String> resultMap = new HashMap<>();
		List<Map<String, Object>> results;
		try {
			results = jdbcTemplate.queryForList(sqlQueryStr, paramMap);
			String key, value;
			for (Map<String, Object> result : results) {
				key = result.get("key").toString();
				value = result.get("value").toString();
				resultMap.put(key, value);
			}
			return resultMap;
		} catch (EmptyResultDataAccessException e) {
			return resultMap;
		}
	}

	public Map<String, Object> select(String tableName, Map<String, Object> paramMap) throws Exception {

		String sqlQueryStr = parseSelectSql(tableName, paramMap);
		return queryForMap(sqlQueryStr, paramMap);
	}

	public List<Map<String, Object>> selectAll(String tableName, Map<String, Object> paramMap) throws Exception {

		String sqlQueryStr = parseSelectSql(tableName, paramMap);

		List<Map<String, Object>> result;
		try {
			result = jdbcTemplate.queryForList(sqlQueryStr, paramMap);
		} catch (EmptyResultDataAccessException e) {
			result = Collections.emptyList();
		}
		return result;
	}

	public List<Map<String, Object>> selectAll(String tableName, Map<String, Object> paramMap, int limit) throws Exception {

		String sqlQueryStr = parseSelectSql(tableName, paramMap);
		sqlQueryStr = sqlQueryStr + " limit " + limit;

		List<Map<String, Object>> result;
		try {
			result = jdbcTemplate.queryForList(sqlQueryStr, paramMap);
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

		boolean exists = jdbcTemplate.queryForObject(sqlQueryStr, paramMap, boolean.class);
		return exists;
	}

	public Long create(String tableName) throws Exception {

		String sql = "insert into " + tableName + " (id) values (default) returning id";
		Long id = jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
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
			String fieldName = fieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(fieldName);
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

	public Long createOrSelect(String tableName, Map<String, Object> paramMap) throws Exception {

		List<String> fieldNames = new ArrayList<>(paramMap.keySet());
		List<String> paramNames = new ArrayList<>();
		for (String fieldName : fieldNames) {
			paramNames.add(":" + fieldName);
		}
		StringBuffer sqlQueryBuf = new StringBuffer();
		sqlQueryBuf.append("with sel as (select id from ");
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
		sqlQueryBuf.append("), ins as (insert into ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append("(");
		sqlQueryBuf.append(StringUtils.join(fieldNames, ", "));
		sqlQueryBuf.append(") select ");
		sqlQueryBuf.append(StringUtils.join(paramNames, ", "));
		sqlQueryBuf.append(" where not exists (select id from sel) returning id) ");
		sqlQueryBuf.append("select id from ins union all select id from sel");
		String sqlScript = sqlQueryBuf.toString();
		Long id = jdbcTemplate.queryForObject(sqlScript, paramMap, Long.class);
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

		jdbcTemplate.update(sqlQueryStr, paramMap);
	}

	public int update(String tableName, Map<String, Object> criteriaParamMap, Map<String, Object> valueParamMap) throws Exception {

		final String critParamPrefix = "crit_";
		final String valParamPrefix = "val_";
		Map<String, Object> renamedCritParamMap = criteriaParamMap.entrySet().stream()
				.collect(Collectors.toMap(entry -> critParamPrefix + entry.getKey(), entry -> entry.getValue()));
		Map<String, Object> renamedValParamMap = valueParamMap.entrySet().stream()
				.collect(Collectors.toMap(entry -> valParamPrefix + entry.getKey(), entry -> entry.getValue()));
		List<String> criteriaFieldNames = new ArrayList<>(criteriaParamMap.keySet());
		List<String> valueFieldNames = new ArrayList<>(valueParamMap.keySet());
		StringBuffer sqlQueryBuf = new StringBuffer();
		sqlQueryBuf.append("update ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" set ");
		for (int fieldIndex = 0; fieldIndex < valueFieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(", ");
			}
			String fieldName = valueFieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(valParamPrefix);
			sqlQueryBuf.append(fieldName);
		}
		sqlQueryBuf.append(" where ");
		for (int fieldIndex = 0; fieldIndex < criteriaFieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(" and ");
			}
			String fieldName = criteriaFieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(critParamPrefix);
			sqlQueryBuf.append(fieldName);
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.putAll(renamedCritParamMap);
		paramMap.putAll(renamedValParamMap);

		String sqlQueryStr = sqlQueryBuf.toString();

		return jdbcTemplate.update(sqlQueryStr, paramMap);
	}

	public int updateIfNotExists(String tableName, Map<String, Object> criteriaParamMap, Map<String, Object> valueParamMap, List<String> notExistsFields) throws Exception {

		final String critParamPrefix = "crit_";
		final String valParamPrefix = "val_";
		final String tableAlias1 = tableName + "1";
		final String tableAlias2 = tableName + "2";
		Map<String, Object> renamedCritParamMap = criteriaParamMap.entrySet().stream()
				.collect(Collectors.toMap(entry -> critParamPrefix + entry.getKey(), entry -> entry.getValue()));
		Map<String, Object> renamedValParamMap = valueParamMap.entrySet().stream()
				.collect(Collectors.toMap(entry -> valParamPrefix + entry.getKey(), entry -> entry.getValue()));
		List<String> criteriaFieldNames = new ArrayList<>(criteriaParamMap.keySet());
		List<String> valueFieldNames = new ArrayList<>(valueParamMap.keySet());
		StringBuffer sqlQueryBuf = new StringBuffer();
		sqlQueryBuf.append("update ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" ");
		sqlQueryBuf.append(tableAlias1);
		sqlQueryBuf.append(" set ");
		for (int fieldIndex = 0; fieldIndex < valueFieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(", ");
			}
			String fieldName = valueFieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(valParamPrefix);
			sqlQueryBuf.append(fieldName);
		}
		sqlQueryBuf.append(" where ");
		for (int fieldIndex = 0; fieldIndex < criteriaFieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(" and ");
			}
			String fieldName = criteriaFieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(critParamPrefix);
			sqlQueryBuf.append(fieldName);
		}
		sqlQueryBuf.append(" and not exists (select ");
		sqlQueryBuf.append(tableAlias2);
		sqlQueryBuf.append(".id from ");
		sqlQueryBuf.append(tableName);
		sqlQueryBuf.append(" ");
		sqlQueryBuf.append(tableAlias2);
		sqlQueryBuf.append(" where ");
		sqlQueryBuf.append(tableAlias2);
		sqlQueryBuf.append(".");
		for (int fieldIndex = 0; fieldIndex < valueFieldNames.size(); fieldIndex++) {
			if (fieldIndex > 0) {
				sqlQueryBuf.append(", ");
			}
			String fieldName = valueFieldNames.get(fieldIndex);
			sqlQueryBuf.append(fieldName);
			sqlQueryBuf.append(" = :");
			sqlQueryBuf.append(valParamPrefix);
			sqlQueryBuf.append(fieldName);
		}
		for (String field : notExistsFields) {
			sqlQueryBuf.append(" and ");
			sqlQueryBuf.append(tableAlias2);
			sqlQueryBuf.append(".");
			sqlQueryBuf.append(field);
			sqlQueryBuf.append(" = ");
			sqlQueryBuf.append(tableAlias1);
			sqlQueryBuf.append(".");
			sqlQueryBuf.append(field);
		}
		sqlQueryBuf.append(")");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.putAll(renamedCritParamMap);
		paramMap.putAll(renamedValParamMap);

		String sqlQueryStr = sqlQueryBuf.toString();

		return jdbcTemplate.update(sqlQueryStr, paramMap);
	}

	public int delete(String tableName, Long id) {

		String sqlQueryStr = "delete from " + tableName + " where id = :id";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);
		return jdbcTemplate.update(sqlQueryStr, paramMap);
	}

	public int delete(String tableName, List<Long> ids) {

		String sqlQueryStr = "delete from " + tableName + " where id in (:ids)";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("ids", ids);
		return jdbcTemplate.update(sqlQueryStr, paramMap);
	}
}
