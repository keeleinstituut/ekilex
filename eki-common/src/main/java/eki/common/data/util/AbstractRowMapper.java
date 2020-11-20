package eki.common.data.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractRowMapper {

	protected Float getFloat(ResultSet rs, String fieldName) throws SQLException {
		BigDecimal valueObj = rs.getObject(fieldName, BigDecimal.class);
		Float value = null;
		if (valueObj != null) {
			value = valueObj.floatValue();
		}
		return value;
	}
}
