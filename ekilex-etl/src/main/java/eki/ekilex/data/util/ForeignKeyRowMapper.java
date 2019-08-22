package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transport.ForeignKey;

public class ForeignKeyRowMapper extends AbstractRowMapper implements RowMapper<ForeignKey> {

	@Override
	public ForeignKey mapRow(ResultSet rs, int rowNum) throws SQLException {

		String pkTableName = rs.getString("pk_table_name");
		String pkColumnName = rs.getString("pk_column_name");
		String fkTableName = rs.getString("fk_table_name");
		String fkColumnName = rs.getString("fk_column_name");

		ForeignKey foreignKey = new ForeignKey();
		foreignKey.setPkTableName(pkTableName);
		foreignKey.setPkColumnName(pkColumnName);
		foreignKey.setFkTableName(fkTableName);
		foreignKey.setFkColumnName(fkColumnName);
		return foreignKey;
	}

}