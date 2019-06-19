package eki.ekilex.runner.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.Complexity;
import eki.ekilex.data.transform.Collocation;

public class CollocationRowMapper extends AbstractRowMapper implements RowMapper<Collocation> {

	@Override
	public Collocation mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long collocationId = rs.getObject("id", Long.class);
		String value = rs.getString("value");
		String definition = rs.getString("definition");
		Float frequency = getFloat(rs, "frequency");
		Float score = getFloat(rs, "score");
		Array usagesArrayObj = rs.getArray("usages");
		List<String> usages = null;
		if (usagesArrayObj != null) {
			String[] usagesArr = (String[]) usagesArrayObj.getArray();
			usages = Arrays.asList(usagesArr);
		}
		String complexityStr = rs.getString("complexity");
		Complexity complexity = null;
		if (StringUtils.isNotBlank(complexityStr)) {
			complexity = Complexity.valueOf(complexityStr);
		}

		Collocation collocation = new Collocation();
		collocation.setCollocationId(collocationId);
		collocation.setValue(value);
		collocation.setDefinition(definition);
		collocation.setFrequency(frequency);
		collocation.setScore(score);
		collocation.setUsages(usages);
		collocation.setComplexity(complexity);
		return collocation;
	}

}
