package eki.common.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class PgVarcharArray implements java.sql.Array {

	private final String[] stringArray;
	private final String stringValue;

	public PgVarcharArray(String[] stringArray) {
		this.stringArray = stringArray;
		this.stringValue = convertToPgFormat(stringArray);
	}

	private static final String NULL = "NULL";

	private String convertToPgFormat(String[] stringArray) {
		if (stringArray == null) {
			return NULL;
		} else if (ArrayUtils.isEmpty(stringArray)) {
			return "{}";
		}
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		buf.append(StringUtils.join(stringArray, ", "));
		buf.append("}");
		return buf.toString();
	}

	@Override
	public Object getArray() throws SQLException {
		return stringArray == null ? null : Arrays.copyOf(stringArray, stringArray.length);
	}

	@Override
	public Object getArray(Map<String, Class<?>> map) throws SQLException {
		return getArray();
	}

	@Override
	public Object getArray(long index, int count) throws SQLException {
		return stringArray == null ? null : Arrays.copyOfRange(stringArray, (int) index, (int) index + count);
	}

	@Override
	public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
		return getArray(index, count);
	}

	@Override
	public int getBaseType() throws SQLException {
		return java.sql.Types.VARCHAR;
	}

	@Override
	public String getBaseTypeName() throws SQLException {
		return "varchar";
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet(long index, int count) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void free() throws SQLException {
	}

	@Override
	public String toString() {
		return stringValue;
	}
}
