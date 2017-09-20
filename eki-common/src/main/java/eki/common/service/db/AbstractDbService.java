package eki.common.service.db;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import eki.common.exception.NotSingleResultQueryException;

public abstract class AbstractDbService {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected NamedParameterJdbcTemplate jdbcTemplate;

	public <T> T getSingleResult(String sqlQueryStr, Map<String, Object> paramMap, RowMapper<T> rowMapper) throws Exception {

		List<T> results = jdbcTemplate.query(sqlQueryStr, paramMap, rowMapper);
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		if (results.size() > 1) {
			throw new NotSingleResultQueryException();
		}
		T singleResult = results.get(0);
		return singleResult;
	}

	public <T> List<T> getResults(String sqlQueryStr, Map<String, Object> paramMap, RowMapper<T> rowMapper) throws Exception {

		List<T> results = jdbcTemplate.query(sqlQueryStr, paramMap, rowMapper);
		return results;
	}
}
