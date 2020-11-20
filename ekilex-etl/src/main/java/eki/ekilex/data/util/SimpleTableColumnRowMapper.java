package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.common.data.transport.SimpleTableColumn;
import eki.common.data.util.AbstractRowMapper;

public class SimpleTableColumnRowMapper extends AbstractRowMapper implements RowMapper<SimpleTableColumn> {

	@Override
	public SimpleTableColumn mapRow(ResultSet rs, int rowNum) throws SQLException {

		String tableName = rs.getString("table_name");
		String columnName = rs.getString("column_name");

		SimpleTableColumn tableColumn = new SimpleTableColumn();
		tableColumn.setTableName(tableName);
		tableColumn.setColumnName(columnName);
		return tableColumn;
	}

}