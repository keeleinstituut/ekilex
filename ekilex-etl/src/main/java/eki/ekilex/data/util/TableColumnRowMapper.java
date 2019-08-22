package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.transport.TableColumn;

public class TableColumnRowMapper extends AbstractRowMapper implements RowMapper<TableColumn> {

	@Override
	public TableColumn mapRow(ResultSet rs, int rowNum) throws SQLException {

		String tableName = rs.getString("table_name");
		String columnName = rs.getString("column_name");
		String dataType = rs.getString("data_type");
		boolean isPrimaryKey = rs.getBoolean("is_primary_key");
		String fkTableName = rs.getString("fk_table_name");
		String fkColumnName = rs.getString("fk_column_name");

		TableColumn tableColumn = new TableColumn();
		tableColumn.setTableName(tableName);
		tableColumn.setColumnName(columnName);
		tableColumn.setDataType(dataType);
		tableColumn.setPrimaryKey(isPrimaryKey);
		tableColumn.setFkTableName(fkTableName);
		tableColumn.setFkColumnName(fkColumnName);
		return tableColumn;
	}

}